package io.zino.restquery.routing;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import io.zino.restquery.config.ConfigDO;

import java.util.HashMap;
import java.util.Map;

public class ConnectionTable {
    private Map<String, QueryConf> routings;

    public ConnectionTable(ConfigDO configDO) {
        routings = new HashMap<>();
        for (ConfigDO.DataSource ds : configDO.getSources()) {
            HikariDataSource connection = createConnection(ds.getDriver(), ds.getServer(), ds.getUser(), ds.getPass());
            for (ConfigDO.DataSource.Query q : ds.getQueries()) {
                QueryConf dbConfig = new QueryConf(ds.getName(), q.getName(), q.getDescription(), q.getQuery(), connection);
                routings.put(dbConfig.getName(), dbConfig);
            }
        }
    }

    public Map<String, QueryConf> getRoutings() {
        return routings;
    }

    public QueryConf getByRoute(String route) {
        return routings.get(route);
    }


    private HikariDataSource createConnection(String driver, String url, String user, String password) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(url);
        config.setUsername(user);
        config.setPassword(password);
        config.setDriverClassName(driver);
        return new HikariDataSource(config);
    }

    public class QueryConf {
        private String connectionName;
        private String queryName;
        private String description;
        private String query;
        private HikariDataSource dataSource;

        public QueryConf(String connectionName, String queryName, String description, String query, HikariDataSource dataSource) {
            this.connectionName = connectionName;
            this.queryName = queryName;
            this.description = description;
            this.query = query;
            this.dataSource = dataSource;
        }

        public String getConnectionName() {
            return connectionName;
        }

        public String getQueryName() {
            return queryName;
        }

        public String getDescription() {
            return description;
        }

        public String getQuery() {
            return query;
        }

        public HikariDataSource getDataSource() {
            return dataSource;
        }

        public String getName() {
            return connectionName + "/" + queryName;
        }

    }
}
