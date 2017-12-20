package userAction;

public class CheckInput {
    final static String rus = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя";

    private boolean checkSpace(String str) {
        if (str.contains(" ")) {
            return false;
        } else
            return true;
    }

    public static boolean checkForInputName(String name) {
        if (name == null) {
            return false;
        }
        if (name.length() < 2 || name.length() > 40)
            return false;
        for (int i = 0; i < name.length(); i++) {
            Character ch = name.charAt(i);
            if (!Character.isLetter(ch))
                return false;
        }
        return true;
    }

    public static boolean checkForLogin(String login) {

        if (login == null) {
            return false;
        }
        if (login.length() < 3 || login.length() > 20) {
            return false;
        }
        for (int i = 0; i < login.length(); i++) {
            Character ch = login.charAt(i);
            if (rus.contains(ch.toString()))
                return false;
            if (!Character.isLetter(ch) || !Character.isDigit(ch))
                return false;
        }
        return true;
    }

    public static boolean checkPassword(String password) {
        if (password == null) {
            return false;
        }
        if (password.length() < 5 || password.length() > 25) {
            return false;
        }
        for (int i = 0; i < password.length(); i++) {
            Character ch = password.charAt(i);
            if (rus.contains(ch.toString())) {
                return false;
            }
            if (!Character.isLetter(ch) || !Character.isDigit(ch))
                return false;
        }
        return true;
    }

    public static String firstUpperCase(String word) {
        if (word == null || word.isEmpty()) return "";
        return word.substring(0, 1).toUpperCase() + word.substring(1);
    }

    public static String deleteSpace(String str) {
        return str.replaceAll(" ", "");
    }
}
