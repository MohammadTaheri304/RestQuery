## RestQuery
Make rest api from database query.

####How to build?
```$xslt
mvn clean package
```


####How to run?
```$xslt
java -jar target/RestQuery-1.0-SNAPSHOT-jar-with-dependencies.jar -p8090 -cfconfig.json
```

####Apis
######How to get query list?
```$xslt
curl -X GET -i http://localhost:8090/query/
```

######How to query?
```$xslt
curl -X POST -i http://localhost:8090/query/ds1/q1 --data '{
  "select": "amount, creationdate",
  "where" : "amount=1.00",
  "orderby" : "amount desc",
  "limit" : 5,
  "offset" : 10
}'
```

####How to config?
Save your config in json format into a file and use ```-cf<your-config-file-path>```
```$xslt
{
  "sources": [
    {
      "name": "ds1",
      "driver": "org.postgresql.ds.PGSimpleDataSource",
      "server": "postgres://localhost:5432/core",
      "user": "core",
      "pass": "core",
      "queries": [
        {
          "name": "q1",
          "description" : "this is a test query",
          "query": "select * from fastrac.transactions limit 100"
        }
      ]
    }
  ]
}
```

####Supported Databases
- Postgres driver="org.postgresql.ds.PGSimpleDataSource"
- Mysql driver="com.mysql.cj.jdbc.Driver"