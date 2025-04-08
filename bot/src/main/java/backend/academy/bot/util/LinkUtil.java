package backend.academy.bot.util;

import java.net.URI;
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
        try {
            URI uri = URI.create(s);
            return uri.getScheme() != null
                    && (uri.getScheme().equalsIgnoreCase("http")
                            || uri.getScheme().equalsIgnoreCase("https"))
                    && uri.getHost() != null;
        } catch (Exception e) {
            return false;
        }
    }
}
