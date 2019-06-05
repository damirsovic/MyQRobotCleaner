package cz.test.damirsovic.myqrobotcleaner.robot;

import java.util.List;

public class JsonInputInfo {
    private  List<List<Character>> map;
    private JsonStartInfo startInfo;
    List<Movement> commands;
    int battery;

    public JsonInputInfo(List<List<Character>> map, JsonStartInfo startInfo, List<Movement> commands, int battery){
        this.map = map;
        this.startInfo = startInfo;
        this.commands = commands;
        this.battery = battery;
    }

    public List<List<Character>> getMap() {
        return map;
    }

    public JsonStartInfo getStartInfo() {
        return startInfo;
    }

    public List<Movement> getCommands() {
        return commands;
    }

    public int getBattery() {
        return battery;
    }
}
