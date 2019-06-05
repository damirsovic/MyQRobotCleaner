package cz.test.damirsovic.myqrobotcleaner.robot;

public class Command {
    String sign;
    int cost = 0;

    public Command(String sign, int cost) {
        this.sign = sign;
        this.cost = cost;
    }

    public String getSign() {
        return sign;
    }

    public int getCost() {
        return cost;
    }

    public Command get(String sign) {
        return (this.sign.equals(sign)) ? this : null;
    }
}
