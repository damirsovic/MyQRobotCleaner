package cz.test.damirsovic.myqrobotcleaner;

import org.json.JSONObject;

public interface RobotFinishedListener {
    void onRobotFinished(JSONObject info);
}
