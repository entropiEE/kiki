package com.finalhome.kiki.excelgenie.table;

import com.finalhome.kiki.excelgenie.annotation.Column;
import com.finalhome.kiki.excelgenie.annotation.Table;

@Table(aliasName = "Liverpool team", realName = "player", inputPath = "excel_genie/input/test_table.xlsx",
        outputPath = "excel_genie/output/test_table.sql", sheet = "Liverpool", start = "B4", end = "D7")
public class TestTable extends GenieTable{

    @Column(aliasName = "Player Name", realName = "name")
    private String name;

    @Column(aliasName = "Kit Number", realName = "number")
    private String number;

    @Column(aliasName = "Age", realName = "age")
    private int age;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("TestTable{");
        sb.append("name='").append(name).append('\'');
        sb.append(", number='").append(number).append('\'');
        sb.append(", age=").append(age);
        sb.append('}');
        return sb.toString();
    }
}
