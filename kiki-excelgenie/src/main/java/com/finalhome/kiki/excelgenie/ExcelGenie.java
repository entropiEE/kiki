package com.finalhome.kiki.excelgenie;

import com.finalhome.kiki.excelgenie.io.ExcelReader;
import com.finalhome.kiki.excelgenie.io.SqlWriter;
import com.finalhome.kiki.excelgenie.table.GenieTable;
import com.finalhome.kiki.excelgenie.table.TestTable;
import com.finalhome.kiki.excelgenie.util.ContextUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class ExcelGenie {
    private static final Logger LOG = LoggerFactory.getLogger(ExcelGenie.class);

    public static void execute() {
        LOG.info("Excel Genie Application started");
        ExcelReader reader = (ExcelReader) ContextUtil.getBean("excelReader");
        SqlWriter writer = (SqlWriter) ContextUtil.getBean("sqlWriter");
        List<GenieTable> data = reader.read(TestTable.class);
        writer.write(data);
        LOG.info("Excel Genie Application ended");
        System.exit(0);
    }
}
