package io.zino.restquery.config;

public class ConfigDO {
    private DataSource[] sources;

    public static class DataSource {
        private String name;
        private String driver;
        private String server;
        private String user;
        private String pass;
        private Query[] queries;

        public static class Query {
            private String name;
            private String description;
            private String query;

            public String getName() {
                return name;
            }

            public void setName(String name) {
                this.name = name;
            }

            public String getQuery() {
                return query;
            }

            public void setQuery(String query) {
                this.query = query;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getDriver() {
            return driver;
        }

        public void setDriver(String driver) {
            this.driver = driver;
        }

        public String getServer() {
            return server;
        }

        public void setServer(String server) {
            this.server = server;
        }

        public String getUser() {
            return user;
        }

        public void setUser(String user) {
            this.user = user;
        }

        public String getPass() {
            return pass;
        }

        public void setPass(String pass) {
            this.pass = pass;
        }

        public Query[] getQueries() {
            return queries;
        }

        public void setQueries(Query[] queries) {
            this.queries = queries;
        }
    }

    public DataSource[] getSources() {
        return sources;
    }

    public void setSources(DataSource[] sources) {
        this.sources = sources;
    }
}