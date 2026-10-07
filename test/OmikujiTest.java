import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;

public class OmikujiTest {
    static final List<String> EXPECTED = List.of("大吉", "中吉", "小吉", "吉", "末吉", "凶", "大凶");

    // nextInt(bound) が決めた順に値を返す Random。渡された bound も記録する
    static class FixedRandom extends Random {
        private final int[] values;
        private int index = 0;
        int lastBound = -1;

        FixedRandom(int... values) {
            this.values = values;
        }

        @Override
        public int nextInt(int bound) {
            lastBound = bound;
            return values[index++];
        }
    }

    public static void main(String[] args) {
        testParse();
        testFortunesAreSevenDistinct();
        testDrawMapsIndexToFortune();
        testDrawCoversAllFortunes();
        testRunDrawsUntilExit();
        testRunEndsAtEndOfInput();
        System.out.println("すべてのテストに合格しました。");
    }

    static void testParse() {
        check(OmikujiApp.parse("1") == OmikujiApp.Command.DRAW, "「1」は引く");
        check(OmikujiApp.parse("2") == OmikujiApp.Command.EXIT, "「2」は終了");
        check(OmikujiApp.parse(" 1 ") == OmikujiApp.Command.DRAW, "前後の空白は無視する");
        check(OmikujiApp.parse("") == OmikujiApp.Command.INVALID, "空の入力は無効");
        check(OmikujiApp.parse("3") == OmikujiApp.Command.INVALID, "「3」は無効");
        check(OmikujiApp.parse("12") == OmikujiApp.Command.INVALID, "「12」は無効");
        check(OmikujiApp.parse("a") == OmikujiApp.Command.INVALID, "文字は無効");
    }

    static void testFortunesAreSevenDistinct() {
        check(List.of(Omikuji.FORTUNES).equals(EXPECTED), "運勢は大吉〜大凶の7種類をこの順で持つ");
    }

    // 乱数が i を返したとき i 番目の運勢になり、乱数の範囲は 0〜6 の7通りであること
    static void testDrawMapsIndexToFortune() {
        for (int i = 0; i < EXPECTED.size(); i++) {
            FixedRandom random = new FixedRandom(i);
            String fortune = new Omikuji(random).draw();
            check(fortune.equals(EXPECTED.get(i)), i + " 番目は " + EXPECTED.get(i));
            check(random.lastBound == EXPECTED.size(), "乱数の範囲は7通り");
        }
    }

    static void testDrawCoversAllFortunes() {
        Omikuji omikuji = new Omikuji(new Random(42));
        Set<String> seen = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
            seen.add(omikuji.draw());
        }
        check(EXPECTED.containsAll(seen), "1000回引いても大吉〜大凶以外は出ない");
        check(seen.equals(Set.copyOf(EXPECTED)), "7種類すべてが出る");
    }

    // 「2」の後ろの「1」を読むと FixedRandom の値が足りず例外になるので、「2」で止まることも確かめられる
    static void testRunDrawsUntilExit() {
        String output = runWith("1\n3\n\n 1 \n2\n1\n", new FixedRandom(0, 6));
        check(output.contains("【大吉】"), "1回目は大吉");
        check(output.contains("【大凶】"), "2回目は大凶");
        check(count(output, "あなたの運勢は") == 2, "引いたのは2回");
        check(count(output, "1か2を入力してください。") == 2, "無効な入力は2回");
        check(output.contains("終了します。"), "「2」で終了する");
    }

    static void testRunEndsAtEndOfInput() {
        String output = runWith("1\n", new FixedRandom(3));
        check(output.contains("【吉】"), "引いた結果が出る");
        check(!output.contains("終了します。"), "入力が尽きたら終了メッセージなしで終わる");
    }

    static String runWith(String input, Random random) {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(buffer, true, StandardCharsets.UTF_8);
        OmikujiApp.run(new Scanner(input), out, new Omikuji(random));
        return buffer.toString(StandardCharsets.UTF_8);
    }

    static int count(String text, String word) {
        return text.split(java.util.regex.Pattern.quote(word), -1).length - 1;
    }

    static void check(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError("失敗: " + message);
        }
        System.out.println("OK: " + message);
    }
}
