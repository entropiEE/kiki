package com.finalhome.kiki.excelgenie.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
public @interface Table {
    String aliasName();
    String realName();
    String inputPath();
    String outputPath();
    String sheet();
    String start();
    String end();
}
