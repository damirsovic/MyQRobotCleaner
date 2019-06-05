package cz.test.damirsovic.myqrobotcleaner

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import cz.test.damirsovic.myqrobotcleaner.robot.*
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import org.json.JSONObject
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity(), RobotFinishedListener {

    val url = "https://myq-test.000webhostapp.com"
    val testList = ArrayList<String>()
    val client = OkHttpClient()
    var jsonString: String? = null
    var selectedName: String? = null
    val PERMISSION_REQUEST_CODE = 10001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupPermissions()
        collectJson()
    }


    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        menu.clear()
        var i = 0
        for (name in testList) {
            menu.add(0, i++, 0, name)
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        selectedName = item.title.toString()
        readJson(url + "/" + selectedName)

        return super.onOptionsItemSelected(item)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("MainActivity", "Permission has been denied by user")
                    return
                } else {
                    Log.i("MainActivity", "Permission has been granted by user")
                }
            }
        }
    }

    private fun runRobot(text: String) {
        val robot = MyQRobotCleaner(this, readFromJson(text))
        val robotThread = Thread(robot)
        robotThread.run()
    }

    private fun readFromJson(text: String): JsonInputInfo {
        val jsonObject = JSONObject(text)
        val jsonArray = jsonObject.getJSONArray("map")
        val map = jsonArray.let { array1 ->
            0.until(array1.length())
                .map { i ->
                    array1.getJSONArray(i)
                        .let { array2 ->
                            0.until(array2.length())
                                .map { j -> if (array2.getString(j).equals("null")) null else array2.getString(j).first() }
                        }
                }
        }

        val startInfo = jsonObject.getJSONObject("start")
        val x: Int = startInfo.getInt("X")
        val y: Int = startInfo.getInt("Y")
        val orientation: Char = startInfo.getString("facing").first()
        val commands: List<Movement> = jsonObject.getJSONArray("commands")
            .let {
                0.until(it.length())
                    .map { i -> Movement(it.optString(i)) }
            }
        val battery: Int = jsonObject.getInt("battery")
        return JsonInputInfo(map, JsonStartInfo(Position(x, y), orientation), commands, battery)
    }

    private fun collectJson() {
        testList.clear()
        Log.d("Collect", "Collecting")
        testList.add("test1.json")
        testList.add("test2.json")
        invalidateOptionsMenu()
    }


    fun readJson(url: String) {
        doAsync {
            val request = Request.Builder()
                .url(url)
                .build()

            val response = client.newCall(request).execute()
            uiThread {
                jsonString = response.body?.string()
                runRobot(jsonString!!)
            }

        }
    }

    override fun onRobotFinished(info: JSONObject?) {
        val result = findViewById<TextView>(R.id.result)
        result.text = info.toString()
        // Save to file
        val file = File(getExternalFilesDir(null).absolutePath, selectedName?.replace(".", "_result."))
            .writeText(info.toString())
    }

    private fun setupPermissions() {
        val permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            Log.i("MainActivity", "Permission to record denied")
            makeRequest()
        }
    }

    private fun makeRequest() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_REQUEST_CODE
        )
    }
}