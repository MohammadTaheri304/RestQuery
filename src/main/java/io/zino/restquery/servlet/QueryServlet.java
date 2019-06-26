package io.zino.restquery.servlet;

import com.google.gson.Gson;
import io.zino.restquery.routing.ConnectionTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;

public class QueryServlet extends HttpServlet {
    private final Logger logger = LoggerFactory.getLogger(QueryServlet.class);
    private final Gson gson = new Gson();

    public static final String PREFIX = "/query/";

    private ConnectionTable connectionTable;

    public QueryServlet(ConnectionTable connectionTable) {
        this.connectionTable = connectionTable;
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        TableDO tableDO = getQueryTable();


        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(gson.toJson(tableDO));
    }

    protected void doPost(
            HttpServletRequest request,
            HttpServletResponse response)
            throws ServletException, IOException {
        logger.debug("query request reciveid. {}", request);

        String queryName = request.getRequestURI().split(PREFIX)[1];
        String body = getBody(request);
        QueryParamDO queryParamDO = gson.fromJson(body, QueryParamDO.class);

        ConnectionTable.QueryConf queryConf = connectionTable.getByRoute(queryName);
        TableDO tableDO = doQuery(queryConf, queryParamDO);

        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_OK);
        response.getWriter().println(gson.toJson(tableDO));
    }

    private String getBody(HttpServletRequest request) throws IOException {

        String body = null;
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader bufferedReader = null;

        try {
            InputStream inputStream = request.getInputStream();
            if (inputStream != null) {
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                char[] charBuffer = new char[128];
                int bytesRead = -1;
                while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
                    stringBuilder.append(charBuffer, 0, bytesRead);
                }
            } else {
                stringBuilder.append("");
            }
        } catch (IOException ex) {
            throw ex;
        } finally {
            if (bufferedReader != null) {
                try {
                    bufferedReader.close();
                } catch (IOException ex) {
                    throw ex;
                }
            }
        }

        body = stringBuilder.toString();
        return body;
    }

    private TableDO doQuery(ConnectionTable.QueryConf queryConf, QueryParamDO queryParamDO) {
        Statement statement = null;
        String query = prepareQuery(queryConf.getQuery(), queryParamDO);
        try {
            statement = queryConf.getDataSource().getConnection().createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            TableDO res = new TableDO();
            while (resultSet.next()) {
                Map<String, Object> rawRow = new HashMap<>();
                int columnCount = resultSet.getMetaData().getColumnCount();
                for (int i = 1; i <= columnCount; i++) {
                    String columnName = resultSet.getMetaData().getColumnName(i);
                    int columnType = resultSet.getMetaData().getColumnType(i);
                    Object object = resultSet.getObject(i);
                    rawRow.put(columnName, object);
                }
                res.addRow(rawRow);
            }
            return res;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    private String prepareQuery(String rawQuery, QueryParamDO queryParamDO) {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT ");
        sb = isEmpty(queryParamDO.getSelect()) ? sb.append(" * ") : sb.append(queryParamDO.getSelect());
        sb.append(" FROM (").append(rawQuery).append(") as t1 ");
        sb = isEmpty(queryParamDO.getWhere()) ? sb : sb.append(" WHERE ").append(queryParamDO.getWhere());
        sb = isEmpty(queryParamDO.getOrderby()) ? sb : sb.append(" ORDER BY ").append(queryParamDO.getOrderby());
        if (queryParamDO.getLimit() != null) {
            sb.append(" LIMIT ").append(queryParamDO.getLimit());
            sb = queryParamDO.getOffset() == null ? sb : sb.append(" OFFSET ").append(queryParamDO.getOffset());
        }
        return sb.toString();
    }

    private boolean isEmpty(String string) {
        if (string == null) return true;
        if ("".equals(string)) return true;
        return false;
    }

    private TableDO getQueryTable() {
        Map<String, ConnectionTable.QueryConf> routings = connectionTable.getRoutings();
        TableDO tableDO = new TableDO();
        routings.values().stream().forEach(item -> {
            Map<String, Object> map = new HashMap<>();
            map.put("route", item.getName());
            map.put("description", item.getDescription());
            tableDO.addRow(map);
        });
        return tableDO;
    }
}