import java.util.Random;

public class Omikuji {
    static final String[] FORTUNES = {"大吉", "中吉", "小吉", "吉", "末吉", "凶", "大凶"};

    private final Random random;

    public Omikuji(Random random) {
        this.random = random;
    }

    public String draw() {
        return FORTUNES[random.nextInt(FORTUNES.length)];
    }
}
