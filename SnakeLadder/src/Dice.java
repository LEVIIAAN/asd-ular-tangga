import java.util.Random;

public class Dice {
    private final Random random = new Random();
    public int rollMain() { return random.nextInt(6) + 1; }
}