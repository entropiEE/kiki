package com.finalhome.kiki.excelgenie.io;

import com.finalhome.kiki.excelgenie.annotation.Column;
import com.finalhome.kiki.excelgenie.annotation.Table;
import com.finalhome.kiki.excelgenie.util.ValidationUtil;
import org.apache.commons.collections4.MapUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellReference;
import org.springframework.stereotype.Component;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component("excelReader")
public class ExcelReader<GenieTable> implements Reader {
    @Override
    public List<GenieTable> read(Class tableClass) {
        TableConfig config = getTableConfig(tableClass);
        ValidationUtil.validate(config);
        List<GenieTable> result = new ArrayList<>();
        Map<String, Integer> headerPos = new HashMap<>();
        //  get absolute path
        Workbook wb = getWorkBook(config.getInputPath());
        Sheet sheet = wb.getSheet(config.getSheet());
        int startRow = Integer.parseInt(config.getStartRow()) - 1;
        if (startRow > 0) {
            DataFormatter df = new DataFormatter();
            //  read headers
            Row headerRow = sheet.getRow(startRow);
            int startColNum = CellReference.convertColStringToIndex(config.getStartColumn());
            int endColNum = CellReference.convertColStringToIndex(config.getEndColumn());
            for (int i = startColNum; i <= endColNum; i++) {
                Cell cell = headerRow.getCell(i);
                String excelAliasName = df.formatCellValue(cell);
                headerPos.put(excelAliasName,i);
            }
            //  read data
            int dataStart = Integer.parseInt(config.getStartRow());
            int dataEnd = Integer.parseInt(config.getEndRow()) - 1;
            try {
                for (int r = dataStart; r <= dataEnd ; r++) {
                    Row dataRow = sheet.getRow(r);
                    GenieTable data = (GenieTable) tableClass.newInstance();
                    headerPos.forEach((ean,pos) -> {
                        String fieldName = config.getHeaders().get(ean);
                        Cell cell = dataRow.getCell(pos);
                        String value = df.formatCellValue(cell);
                        setAttribute(tableClass,data,fieldName,value);
                    });
                    result.add(data);
                }
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InstantiationException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    private void setAttribute(Class<? extends GenieTable> clazz, GenieTable data, String fieldName, String value) {
        try {
            PropertyDescriptor pd = new PropertyDescriptor(fieldName,clazz);
            Method setter = pd.getWriteMethod();
            Class<?>[] types = setter.getParameterTypes();
            String type = types.length == 1 ? types[0].getSimpleName() : "";
            switch (type) {
                case "int":
                case "Integer":
                    setter.invoke(data,Integer.parseInt(value));
                    break;
                case "long":
                case "Long":
                    setter.invoke(data,Long.parseLong(value));
                    break;
                case "float":
                case "Float":
                    setter.invoke(data,Float.parseFloat(value));
                    break;
                case "double":
                case "Double":
                    setter.invoke(data,Double.parseDouble(value));
                    break;
                case "":
                    throw new RuntimeException("Setter of " + fieldName + " in Class " + clazz + " has no parameter, please check");
                default:
                    setter.invoke(data,value);
                    break;
            }
        } catch (IntrospectionException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private Workbook getWorkBook(String path) {
        Workbook wb = null;
        try {
            wb = WorkbookFactory.create(new File(path));
            return wb;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    private TableConfig getTableConfig(Class<GenieTable> tableClass) {
        TableConfig config = new TableConfig();
        Table tableAnnotation = tableClass.getAnnotation(Table.class);
        config.setAliasName(tableAnnotation.aliasName());
        config.setRealName(tableAnnotation.realName());
        config.setInputPath(tableAnnotation.inputPath());
        config.setSheet(tableAnnotation.sheet());
        config.setHeaders(getHeaders(tableClass));
        setStartEnd(config,tableAnnotation);
        return config;
    }

    private void setStartEnd(TableConfig config, Table tableAnnotation) {
        String regex = "([A-Z]+)(\\d+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher ms = pattern.matcher(tableAnnotation.start());
        Matcher me = pattern.matcher(tableAnnotation.end());
        if (ms.find()) {
            config.setStartColumn(ms.group(1));
            config.setStartRow(ms.group(2));
        }
        if (me.find()) {
            config.setEndColumn(me.group(1));
            config.setEndRow(me.group(2));
        }
    }

    private Map<String, String> getHeaders(Class<GenieTable> tableClass) {
        Map<String, String> headers = new HashMap<>();
        Field[] fields = tableClass.getDeclaredFields();
        for (Field f : fields) {
            if (f.isAnnotationPresent(Column.class)) {
                Column column = f.getAnnotation(Column.class);
                String aliasName = column.aliasName();
                String filedName = f.getName();
                headers.putIfAbsent(aliasName,filedName);
            }
        }
        return MapUtils.isEmpty(headers) ? null : headers;
    }
}
