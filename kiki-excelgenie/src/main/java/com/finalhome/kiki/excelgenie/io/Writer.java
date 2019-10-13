package com.finalhome.kiki.excelgenie.io;

import com.finalhome.kiki.excelgenie.table.GenieTable;

import java.util.List;

public interface Writer {
    void write(List<GenieTable> data);
}
