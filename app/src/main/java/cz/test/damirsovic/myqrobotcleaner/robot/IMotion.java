package cz.test.damirsovic.myqrobotcleaner.robot;

public interface IMotion {
    public void move(Command move) throws OutOfBatteryException;
}
