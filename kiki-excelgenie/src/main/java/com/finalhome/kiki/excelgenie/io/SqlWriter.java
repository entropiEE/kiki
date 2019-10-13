package com.finalhome.kiki.excelgenie.io;

import com.finalhome.kiki.excelgenie.annotation.Column;
import com.finalhome.kiki.excelgenie.annotation.Table;
import com.finalhome.kiki.excelgenie.table.GenieTable;
import com.finalhome.kiki.excelgenie.util.SqlUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component("sqlWriter")
public class SqlWriter implements Writer {
    private static final Logger LOG = LoggerFactory.getLogger(SqlWriter.class);
    public static final String INSERT = "INSERT INTO %s (%s) VALUES (%s);";
    public static final String COMMIT = "COMMIT;";

    @Override
    public void write(List<GenieTable> data) {
        if (CollectionUtils.isEmpty(data))
            return;
        Class<? extends GenieTable> clazz = data.get(0).getClass();
        String tableName = getTableName(clazz);
        String outputPath = getOutputPath(clazz);
        Map<String, String> columns = getColumns(data.get(0));
        List<String> columnNames = new ArrayList<>();
        List<String> columnValues = new ArrayList<>();
        List<String> sqlResult = new ArrayList<>();
        if (MapUtils.isEmpty(columns))
            return;
        data.forEach(e -> {
            columns.forEach((fn,rn) -> {
                String value = getAttribute(clazz,e,fn);
                if (StringUtils.isBlank(value))
                    LOG.warn(fn + " on Table " + tableName + " has no valid attributes. Please check");
                columnNames.add(rn);
                columnValues.add(value);
            });
            if (CollectionUtils.isEmpty(columnNames) || CollectionUtils.isEmpty(columnValues) || columnNames.size() != columnValues.size())
                return;
            String columnSql = StringUtils.join(columnNames,',');
            String valueSql = StringUtils.join(columnValues,',');
            String sqlRow = String.format(INSERT, tableName, columnSql, valueSql);
            sqlResult.add(sqlRow);
            columnNames.clear();
            columnValues.clear();
        });
        generateOutputSql(outputPath,sqlResult);
    }

    private void generateOutputSql(String outputPath, List<String> sqlResult) {
        File file = new File(outputPath);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file,false), StandardCharsets.UTF_8));) {
            sqlResult.forEach(sql -> {
                try {
                    bf.write(sql);
                    bf.newLine();
                    bf.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            bf.write(COMMIT);
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOutputPath(Class<? extends GenieTable> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = clazz.getAnnotation(Table.class);
            return tableAnnotation.outputPath();
        } else {
            throw new RuntimeException(clazz.getName() + " has no valid @output annotation, please check");
        }
    }

    private String getAttribute(Class<? extends GenieTable> clazz, GenieTable e, String fieldName) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName, clazz);
            Method getter = pd.getReadMethod();
            if (getter == null)
                throw new RuntimeException(fieldName + " in Class " + clazz + " has no getter, please check");
            Class<?> returnType = getter.getReturnType();
            String type = returnType.getSimpleName();
            switch (type) {
                case "int":
                case "Integer":
                    return String.valueOf((int) getter.invoke(e));
                case "long":
                case "Long":
                    return String.valueOf((long) getter.invoke(e));
                case "float":
                case "Float":
                    return String.valueOf((float) getter.invoke(e));
                case "double":
                case "Double":
                    return String.valueOf((double) getter.invoke(e));
                case "String":
                    return SqlUtil.convertToSqlStringValue((String) getter.invoke(e));
                default:
                    throw new RuntimeException("Return type of " + fieldName + "getter in Class " + clazz + " not supported, please check");
            }
        } catch (IntrospectionException ex) {
            ex.printStackTrace();
            return null;
        } catch (IllegalAccessException ex) {
            ex.printStackTrace();
            return null;
        } catch (InvocationTargetException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private Map<String, String> getColumns(GenieTable table) {
        Field[] fields = table.getClass().getDeclaredFields();
        Map<String, String> columns = new HashMap<>();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                Column column = f.getAnnotation(Column.class);
                String fieldName = f.getName();
                String realName = column.realName();
                if (StringUtils.isNotBlank(realName))
                    columns.putIfAbsent(fieldName, realName);
                else {
                    // TODO: 2019/10/8 add log error
                    throw new RuntimeException("Field " + fieldName + " in " + table.getClass().getName() + "has no valid @realName annotation, please check");
                }
            }
        }
        return MapUtils.isEmpty(columns) ? null : columns;
    }

    private String getTableName(Class<? extends GenieTable> clazz) {
        if (clazz.isAnnotationPresent(Table.class)) {
            Table tableAnnotation = clazz.getAnnotation(Table.class);
            return tableAnnotation.realName();
        } else
            return null;
    }
}
