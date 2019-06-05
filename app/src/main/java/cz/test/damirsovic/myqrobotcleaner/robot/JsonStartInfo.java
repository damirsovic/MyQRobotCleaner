package cz.test.damirsovic.myqrobotcleaner.robot;

public class JsonStartInfo {
    Position position;
    Character orientation;

    public JsonStartInfo(Position position, Character orientation){
        this.position = position;
        this.orientation = orientation;
    }

    public Position getPosition(){
        return this.position;
    }

    public Character getOrientation() {
        return orientation;
    }
}
