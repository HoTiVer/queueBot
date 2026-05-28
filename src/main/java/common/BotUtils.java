package common;

import org.telegram.telegrambots.meta.api.objects.User;

public class BotUtils {

    public String getUserDisplayName(User user) {
        if (user.getUserName() != null && !user.getUserName().isEmpty()) {
            return "@" + user.getUserName();
        } else if (user.getFirstName() != null) {
            return user.getFirstName();
        } else {
            return "user";
        }
    }

    public String getFirstWord(String text) {
        if (text == null || text.isBlank()) return "";
        String[] words = text.trim().split("\\s+");
        return words.length >= 1 ? words[0] : "";
    }

    public boolean isNumber(String text) {
        try {
            short num = Short.parseShort(text);
            return num > 0;
        }
        catch (Exception e){
            return false;
        }
    }
}
