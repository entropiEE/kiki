package com.finalhome.kiki.excelgenie.io;

import java.util.List;

public interface Reader<T> {
    List<T> read(Class clazz);
}
