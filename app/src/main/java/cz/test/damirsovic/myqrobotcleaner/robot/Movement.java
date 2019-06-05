package cz.test.damirsovic.myqrobotcleaner.robot;

public class Movement {

    public final Command TURN_LEFT = new Command("TL", 1);
    public final Command TURN_RIGHT = new Command("TR", 1);
    public final Command ADVANCE = new Command("A", 2);
    public final Command BACK = new Command("B", 3);
    public final Command CLEAN = new Command("C", 5);

    private Command command;

    public Movement(String command) {
        switch (command) {
            case "TL":
                this.command = TURN_LEFT;
                break;
            case "TR":
                this.command = TURN_RIGHT;
                break;
            case "A":
                this.command = ADVANCE;
                break;
            case "B":
                this.command = BACK;
                break;
            case "C":
                this.command = CLEAN;
                break;
        }
    }

    public Command get() {
        return this.command;
    }
}
