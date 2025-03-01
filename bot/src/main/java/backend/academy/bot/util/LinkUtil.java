package backend.academy.bot.util;

import lombok.experimental.UtilityClass;

@UtilityClass
public class LinkUtil {

    public static String getLink(String[] text) {
        for (String message : text) {
            if (isLink(message)) {
                return message;
            }
        }
        return null;
    }

    public static boolean isLink(String s) {
        return s.startsWith("http://") || s.startsWith("https://");
    }
}
