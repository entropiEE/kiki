package com.finalhome.kiki.excelgenie.annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Inherited
public @interface Column {
    String aliasName();
    String realName();
}
