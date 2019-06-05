package cz.test.damirsovic.myqrobotcleaner.robot;

import android.util.Log;
import cz.test.damirsovic.myqrobotcleaner.RobotFinishedListener;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class MyQRobotCleaner implements IMotion, Runnable {
    int x;
    int y;
    Character orientation;
    int battery;
    List<Movement> commands = new ArrayList<>();
    List<List<Character>> map;
    int commandCounter = 0;
    int strategyCounter = 0;
    JsonOutputInfo outputInfo = new JsonOutputInfo();
    RobotFinishedListener listener;

    List<List<Movement>> backOffStrategy = Arrays.asList(
            Arrays.asList(
                    new Movement("TR"),
                    new Movement("A")
            ),
            Arrays.asList(
                    new Movement("TL"),
                    new Movement("B"),
                    new Movement("TR"),
                    new Movement("A")
            ),
            Arrays.asList(
                    new Movement("TL"),
                    new Movement("TL"),
                    new Movement("A")
            ),
            Arrays.asList(
                    new Movement("TL"),
                    new Movement("B"),
                    new Movement("TR"),
                    new Movement("A")
            ),
            Arrays.asList(
                    new Movement("TL"),
                    new Movement("TL"),
                    new Movement("A")
            )
    );

    public MyQRobotCleaner(RobotFinishedListener listener, JsonInputInfo inputInfo){
        this.listener = listener;
        this.x = inputInfo.getStartInfo().getPosition().getX();
        this.y = inputInfo.getStartInfo().getPosition().getY();
        this.orientation = inputInfo.getStartInfo().getOrientation();
        this.commands = inputInfo.getCommands();
        this.battery = inputInfo.getBattery();
        this.map = inputInfo.getMap();
        outputInfo.getVisited().add(inputInfo.getStartInfo().getPosition());
    }

    public void run (){
        Log.d("Run", "commands.size: " + commands.size());
        try{
            while(commandCounter < commands.size()) {
                Log.d("Run", " counter: " + commandCounter);
                Command command = commands.get(commandCounter++).get();
                if(command.sign.equals("B"))
                    throw new BackCommandException();
                move(command);
            }
        }catch(OutOfBatteryException oobEx){
            Log.d("RobotCleaner", oobEx.getMessage());
        }catch(BackCommandException bcEx){
            Log.d("RobotCleaner", bcEx.getMessage());
        }finally{
            outputInfo.setFinalPosition(new Position(x, y));
            outputInfo.setFinalOrientation(orientation);
            outputInfo.setBattery(battery);
            Log.d("RobotCleaner", outputInfo.toJsonObject().toString());
            listener.onRobotFinished(outputInfo.toJsonObject());
            return;
        }
    }

    @Override
    public void move(Command command) throws OutOfBatteryException {
        Log.d("Move", command.sign);
        if(battery < command.getCost()) throw new OutOfBatteryException();
        Log.d("Move", "x: " + x + " y: " + y + " battery: " + battery + " orientation: " + orientation);
        switch(command.getSign()){
            case "TL":
                turnLeft();
                break;
            case "TR":
                turnRight();
                break;
            case "A":
                advance();
                break;
            case "B":
                back();
                break;
            case "C":
                clean();
                break;
        }
        battery -= command.getCost();
        Log.d("Move", "x: " + x + " y: " + y + " battery: " + battery + " orientation: " + orientation);
    }

    public void convertCommands(List<String> commands){
        this.commands.clear();
        for(String command: commands){
            this.commands.add(new Movement(command));
        }
    }

    public void turnRight(){
        switch(orientation){
            case 'N': orientation = 'E'; break;
            case 'E': orientation = 'S'; break;
            case 'S': orientation = 'W'; break;
            case 'W': orientation = 'N'; break;
        }
    }

    public void turnLeft(){
        switch(orientation){
            case 'N': orientation = 'W'; break;
            case 'E': orientation = 'N'; break;
            case 'S': orientation = 'E'; break;
            case 'W': orientation = 'S'; break;
        }
    }

    public void advance(){
        int tmpX = x;
        int tmpY = y;
        switch(orientation){
            case 'N': tmpX--; break;
            case 'E': tmpY++; break;
            case 'S': tmpX++; break;
            case 'W': tmpY--; break;
        }
        if( (tmpX < 0) || (tmpY < 0)){
            backOff();
        }
        if( (tmpX >= map.size()) || (tmpY >= map.get(0).size())){
            backOff();
        }
        if(map.get(tmpX).get(tmpY) == null){
            backOff();
        }
        x = tmpX;
        y = tmpY;
        if(outputInfo.getVisited().indexOf(new Position(x, y))< 0)
            outputInfo.getVisited().add(new Position(x, y));
    }

    public void back(){
        int tmpX = x;
        int tmpY = y;
        switch(orientation){
            case 'N': tmpX++; break;
            case 'E': tmpY--; break;
            case 'S': tmpX--; break;
            case 'W': tmpY++; break;
        }
        if( (tmpX < 0) || (tmpY < 0)){
            backOff();
        }
        if( (tmpX >= map.size()) || (tmpY >= map.get(0).size())){
            backOff();
        }
        if(map.get(tmpX).get(tmpY) == null){
            backOff();
        }
        x = tmpX;
        y = tmpY;
        if(outputInfo.getVisited().indexOf(new Position(x, y))< 0)
            outputInfo.getVisited().add(new Position(x, y));
    }

    public void clean(){
        map.get(x).set(y, 'C');
        if(outputInfo.getCleaned().indexOf(new Position(x, y))< 0)
            outputInfo.getCleaned().add(new Position(x, y));
    }

    private void backOff(){
        // hitting back_off
        Log.e("RobotCleaner", "Back off Strategy");
        List<Movement> backOffCommands = backOffStrategy.get(strategyCounter++);
        Iterator<Movement> itrCommands = backOffCommands.iterator();
        try{
            while(itrCommands.hasNext()){
                move(itrCommands.next().get());
            }
        }catch(OutOfBatteryException oobEx){
            Log.d("RobotCleaner", "Out of battery");
        }finally {
            if (commandCounter < commands.size())
                run();
        }
    }
}
