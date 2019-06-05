package cz.test.damirsovic.myqrobotcleaner.robot;

public class OutOfBatteryException extends Exception {
    public OutOfBatteryException() {
        super("Battery drained");
    }
}
