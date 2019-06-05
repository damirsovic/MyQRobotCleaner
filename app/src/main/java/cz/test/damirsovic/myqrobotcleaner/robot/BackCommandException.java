package cz.test.damirsovic.myqrobotcleaner.robot;

public class BackCommandException extends Exception {
    public BackCommandException() {
        super("BACK (B) command not allowed");
    }
}
