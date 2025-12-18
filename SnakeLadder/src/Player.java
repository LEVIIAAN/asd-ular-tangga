import java.awt.Color;

public class Player {
    private final String name;
    private final Color color;
    private int position;
    private int score;

    // MENYIMPAN TIPE: 0=Dolphin, 1=Turtle, 2=Submarine
    private final int characterType;

    public Player(String name, Color color, int characterType) {
        this.name = name;
        this.color = color;
        this.characterType = characterType;
        this.position = 1;
        this.score = 0;
    }

    public String getName() { return name; }
    public Color getColor() { return color; }
    public int getPosition() { return position; }
    public int getScore() { return score; }
    public int getCharacterType() { return characterType; } // Method Wajib

    public void setPosition(int position) {
        this.position = Math.max(1, position);
    }

    public void addScore(int points) {
        this.score += points;
    }

    public boolean hasWon(int targetPosition) {
        return position >= targetPosition;
    }

    public void setScore(int score) {
        this.score = score;
    }
}