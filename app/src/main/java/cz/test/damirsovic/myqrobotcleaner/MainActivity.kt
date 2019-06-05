package cz.test.damirsovic.myqrobotcleaner

import android.content.res.Resources
import android.os.Bundle
import android.os.Parcel
import android.os.Parcelable
import android.support.annotation.UiThread
import android.support.v7.app.AppCompatActivity
import cz.test.damirsovic.myqrobotcleaner.robot.*
import org.json.JSONObject
import android.support.v4.view.MenuItemCompat.setShortcut
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.SubMenu
import android.widget.TextView
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.io.File
import java.io.IOException
import java.net.URL
import java.util.*
import java.util.concurrent.Executors
import kotlin.concurrent.thread


class MainActivity() : AppCompatActivity(), RobotFinishedListener {

    val url = "https://https://myq-test.000webhostapp.com"
    val testList = ArrayList<String>()
    val client = OkHttpClient();

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        collectJson()
    }

    override fun onResume() {
        super.onResume()

    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        super.onCreateOptionsMenu(menu)

        var i = 0
        for (name in testList) {
            menu.add(0, i++, 0, name)
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        Executors.newSingleThreadExecutor().execute({
//
//
//        }
        when (item.getItemId()) {
            0 -> {
                val text = resources
                    .openRawResource(R.raw.test1)
                    .bufferedReader()
                    .use { it.readText() }
                runRobot(text)
            }
            1 -> {
                val text = resources
                    .openRawResource(R.raw.test2)
                    .bufferedReader()
                    .use { it.readText() }
                runRobot(text)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun runRobot(text: String){
        val robot = MyQRobotCleaner(this, readFromJson(text))
        val robotThread = Thread(robot)
        robotThread.run()
    }

    private fun readFromJson(text:String): JsonInputInfo {
        val jsonObject = JSONObject(text)
        val jsonArray = jsonObject.getJSONArray("map")
        val map = jsonArray.let {array1 -> 0.until(array1.length())
                    .map { i -> array1.getJSONArray(i)
                        .let{ array2 -> 0.until(array2.length())
                                .map{ j -> if(array2.getString(j).equals("null")) null else array2.getString(j).first()}
                        }
                    }
            }

        val startInfo = jsonObject.getJSONObject("start")
        val x: Int = startInfo.getInt("X")
        val y: Int = startInfo.getInt("Y")
        val orientation: Char = startInfo.getString("facing").first() as Char
        val commands: List<Movement> = jsonObject.getJSONArray("commands")
            .let { 0.until(it.length())
                .map { i -> Movement(it.optString(i)) } }
        val battery: Int = jsonObject.getInt("battery")
        return JsonInputInfo(map, JsonStartInfo(Position(x, y), orientation), commands, battery)
    }

    private fun collectJson() {
        Log.d("Collect", "Collecting")
        doAsync {
            val content = readUrl(url)
            uiThread {
                Log.d("Collect", content)
                testList.add("test1.json")
                testList.add("test2.json")
            }
        }
    }

    fun readUrl(url: String): String? {
        val request = Request.Builder()
            .url(url)
            .build();

        val response = client.newCall(request).execute()
        return response.body()?.string()
    }

    override fun onRobotFinished(info: JSONObject?) {
        val result = findViewById<TextView>(R.id.result)
        result.text = info.toString()
        //File(fileName).writeText(fileContent)
    }
}
