package cz.test.damirsovic.myqrobotcleaner.robot;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonOutputInfo {
    List<Position> visited = new ArrayList();
    List<Position> cleaned = new ArrayList<>();
    Position finalPosition;
    Character finalOrientation;
    int battery;

    public List<Position> getVisited() {
        return visited;
    }

    public void setVisited(List<Position> visited) {
        this.visited = visited;
    }

    public List<Position> getCleaned() {
        return cleaned;
    }

    public void setCleaned(List<Position> cleaned) {
        this.cleaned = cleaned;
    }

    public Position getFinalPosition() {
        return finalPosition;
    }

    public void setFinalPosition(Position finalPosition) {
        this.finalPosition = finalPosition;
    }

    public Character getFinalOrientation() {
        return finalOrientation;
    }

    public void setFinalOrientation(Character finalOrientation) {
        this.finalOrientation = finalOrientation;
    }

    public int getBattery() {
        return battery;
    }

    public void setBattery(int battery) {
        this.battery = battery;
    }

    public JSONObject toJsonObject(){
        JSONObject object = new JSONObject();
        try {
            JSONArray jsonVisited = new JSONArray();
            for(Position p : visited){
                JSONObject o = new JSONObject();
                o.put("X", p.getX());
                o.put("Y", p.getY());
                jsonVisited.put(o);
            }
            object.put("visited", jsonVisited);
            JSONArray jsonCleaned = new JSONArray();
            for(Position p : cleaned){
                JSONObject o = new JSONObject();
                o.put("X", p.getX());
                o.put("Y", p.getY());
                jsonCleaned.put(o);
            }
            object.put("cleaned", jsonCleaned);
            JSONObject finalState = new JSONObject();
            finalState.put("X", finalPosition.getX());
            finalState.put("Y", finalPosition.getY());
            finalState.put("facing", getFinalOrientation());
            object.put("final", finalState);
            object.put("battery", battery);
        } catch(JSONException jsonEx){
        }
        return object;
    }
}
