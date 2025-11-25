package com.webharmony.core.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexUtils {

    private RegexUtils() {

    }

    public static List<String> getStringsSurroundedBy(String str, String prefix, String suffix) {
        List<String> resultList = new ArrayList<>();

        boolean onlySingleChar = false;
        if(prefix.length() == 1) {
            prefix = "\\" + prefix;
            onlySingleChar = true;
        }

        if (suffix.length() == 1) {
            suffix = "\\" + suffix;
            onlySingleChar = true;
        }

        final String regex = String.format(onlySingleChar ? "%s(.*?)%s" : "%s.*?%s", prefix, suffix);
        Matcher m = Pattern.compile(regex).matcher(str);
        while (m.find()) {
            resultList.add(m.group(onlySingleChar ? 1 : 0));
        }
        return resultList;
    }

}
