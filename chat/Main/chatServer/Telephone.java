package chat.Main.chatServer;

import chat.Main.InvalidTelephoneException;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Telephone {
    private final static String regex = "^(\\+7|7|8)?[\\s\\-]?\\(?[489][0-9]{2}\\)?[\\s\\-]?[0-9]{3}[\\s\\-]?[0-9]{2}[\\s\\-]?[0-9]{2}$";
    private final static Pattern pattern = Pattern.compile(regex);

    public static String processTelephone(String telephone) throws InvalidTelephoneException {
        Matcher match = pattern.matcher(telephone);
        if (match.find()) {
            String onlyNumbers = "";

            for (int i = 0; i < telephone.length(); i++) {
                if (Character.isDigit(telephone.charAt(i))) {
                    onlyNumbers += Character.toString(telephone.charAt(i));
                }
            }
            return String.format("+7 (%s) %s-%s-%s", onlyNumbers.substring(1, 4), onlyNumbers.substring(4, 7), onlyNumbers.substring(7, 9), onlyNumbers.substring(9, 11));
        } else {
            throw new InvalidTelephoneException("Incorrect telephone");
        }
    }
}
