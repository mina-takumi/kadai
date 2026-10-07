import java.io.PrintStream;
import java.util.Random;
import java.util.Scanner;

public class OmikujiApp {
    enum Command { DRAW, EXIT, INVALID }

    static Command parse(String line) {
        return switch (line.trim()) {
            case "1" -> Command.DRAW;
            case "2" -> Command.EXIT;
            default -> Command.INVALID;
        };
    }

    static void run(Scanner in, PrintStream out, Omikuji omikuji) {
        out.println("=== おみくじ ===");
        while (true) {
            out.println("1: おみくじを引く / 2: 終了");
            out.print("> ");
            // 入力が尽きたら(パイプで流し込んだ場合など)そこで終わる
            if (!in.hasNextLine()) {
                out.println();
                return;
            }
            switch (parse(in.nextLine())) {
                case DRAW -> out.println("あなたの運勢は【" + omikuji.draw() + "】です。");
                case EXIT -> {
                    out.println("終了します。");
                    return;
                }
                case INVALID -> out.println("1か2を入力してください。");
            }
        }
    }

    public static void main(String[] args) {
        run(new Scanner(System.in), System.out, new Omikuji(new Random()));
    }
}
