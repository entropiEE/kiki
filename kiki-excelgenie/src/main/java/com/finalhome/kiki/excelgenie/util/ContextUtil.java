package com.finalhome.kiki.excelgenie.util;

import org.springframework.context.ConfigurableApplicationContext;

public class ContextUtil {
    private static ConfigurableApplicationContext context;

    public static void setContext(ConfigurableApplicationContext appContext) {
        context = appContext;
    }

    public static Object getBean(String name) {
        return context.getBean(name);
    }
}
