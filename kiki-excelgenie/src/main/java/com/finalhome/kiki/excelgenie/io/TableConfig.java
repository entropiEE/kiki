package com.finalhome.kiki.excelgenie.io;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.HashMap;
import java.util.Map;

public class TableConfig {

    @NotBlank
    private String realName;

    @NotBlank
    private String aliasName;

    @NotBlank
    private String inputPath;

    @NotBlank
    private String sheet;

    @NotBlank
    private String startColumn;

    @NotBlank
    private String endColumn;

    @NotBlank
    private String startRow;

    @NotBlank
    private String endRow;

    @NotEmpty
    private Map<String, String> headers;

    public TableConfig() {
        headers = new HashMap<String, String>();
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public String getAliasName() {
        return aliasName;
    }

    public void setAliasName(String aliasName) {
        this.aliasName = aliasName;
    }

    public String getInputPath() {
        return inputPath;
    }

    public void setInputPath(String inputPath) {
        this.inputPath = inputPath;
    }

    public String getSheet() {
        return sheet;
    }

    public void setSheet(String sheet) {
        this.sheet = sheet;
    }

    public String getStartColumn() {
        return startColumn;
    }

    public void setStartColumn(String startColumn) {
        this.startColumn = startColumn;
    }

    public String getEndColumn() {
        return endColumn;
    }

    public void setEndColumn(String endColumn) {
        this.endColumn = endColumn;
    }

    public String getStartRow() {
        return startRow;
    }

    public void setStartRow(String startRow) {
        this.startRow = startRow;
    }

    public String getEndRow() {
        return endRow;
    }

    public void setEndRow(String endRow) {
        this.endRow = endRow;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TableConfig{");
        sb.append("realName='").append(realName).append('\'');
        sb.append(", aliasName='").append(aliasName).append('\'');
        sb.append(", path='").append(inputPath).append('\'');
        sb.append(", sheet='").append(sheet).append('\'');
        sb.append(", startColumn='").append(startColumn).append('\'');
        sb.append(", endColumn='").append(endColumn).append('\'');
        sb.append(", startRow='").append(startRow).append('\'');
        sb.append(", endRow='").append(endRow).append('\'');
        sb.append(", headers=").append(headers);
        sb.append('}');
        return sb.toString();
    }
}
