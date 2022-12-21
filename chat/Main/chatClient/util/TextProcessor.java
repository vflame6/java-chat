package chat.Main.chatClient.util;

public class TextProcessor {
    public static String processMessage(String content) {
        String result = "   ";
        String[] words = content.split(" ");
        int symbols = 3;

        for (String word : words) {
            if (symbols + word.length() < 44) {
                result += " " + word;
                symbols += 1 + word.length();
            } else {
                if (word.length() >= 44) {
                    String bigWord = processBigWord(word, symbols);
                    result += bigWord;
                    String[] lines = bigWord.split("\n");
                    String lastLine = lines[lines.length - 1];
                    symbols = lastLine.length();
                } else {
                    result += "\n" + "    " + word;
                    symbols = 4 + word.length();
                }
            }
        }
        return result;
    }

    public static String[] separateMessages(String content) {
        int MAX_SYMBOLS = 100;
        String result = "";

        int symbols = 0;
        String[] words = content.split(" ");
        for (String word : words) {
            if (symbols + 1 + word.length() < MAX_SYMBOLS) {
                result += " " + word;
                symbols += 1 + word.length();
            } else {
                result += "\n" + word;
                symbols = word.length();
            }
        }
        return result.trim()
                .split("\n");
    }

    private static String processBigWord(String word, int offset) {
        String result = "";
        result += " " + word.substring(0, 44 - offset - 1) + "\n";

        word = word.substring(44 - offset);

        for (int i = 0; i < word.length() / 44 + 1; i++) {
            if (44 * i + 44 > word.length()) {
                result += "    " + word.substring(44 * i);
            } else {
                result += "    " + word.substring(44 * i, 44 * i + 44) + "\n";
            }
        }
        return result;
    }
}