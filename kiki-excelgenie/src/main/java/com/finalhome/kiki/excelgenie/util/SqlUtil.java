package com.finalhome.kiki.excelgenie.util;

import org.apache.commons.lang3.StringUtils;

public class SqlUtil {
    public static String convertToSqlStringValue(String input) {
        if (StringUtils.isBlank(input))
            return "NULL";
        else
            return "\"" + input + "\"";
    }
}
