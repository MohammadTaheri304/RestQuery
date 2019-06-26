package io.zino.restquery.servlet;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class TableDO {
    List<Map<String, Object>> data = new ArrayList<>();

    public void addRow(Map<String, Object> row) {
        data.add(row);
    }

    public List<Map<String, Object>> getData() {
        return data;
    }
}
