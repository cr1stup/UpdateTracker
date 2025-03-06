package backend.academy.scrapper.util;

import java.net.URI;
import java.util.regex.Pattern;
import lombok.experimental.UtilityClass;

@UtilityClass
public class LinkParser {

    public static boolean isSupport(URI url, Pattern pattern) {
        return pattern.matcher(url.toString()).matches();
    }

    public static String getQuestionId(URI url, Pattern pattern) {
        var matcher = pattern.matcher(url.toString());
        if (!matcher.matches()) {
            return null;
        }
        return matcher.group(1);
    }
}
