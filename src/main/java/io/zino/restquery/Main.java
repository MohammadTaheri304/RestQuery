package io.zino.restquery;


import io.zino.restquery.config.ConfigDO;
import io.zino.restquery.config.ConfigUtil;
import io.zino.restquery.routing.ConnectionTable;
import io.zino.restquery.servlet.QueryServlet;
import org.eclipse.jetty.server.Connector;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.ServerConnector;
import org.eclipse.jetty.servlet.ServletHandler;
import org.eclipse.jetty.servlet.ServletHolder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    public static void main(String[] args) {
        try {
            new Main().start(args);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Logger logger = LoggerFactory.getLogger(Main.class);

    public void start(String args[]) throws Exception {

        String configPath = "config.json";
        Integer port = 8080;

        for (String arg:args){
            if(arg.startsWith("-p")){
                String portSt = arg.split("-p")[1];
                port = Integer.parseInt(portSt);
            }

            if (arg.startsWith("-cf")){
                String cPath = arg.split("-cf")[1];
                configPath = cPath;
            }
        }


        long start = System.currentTimeMillis();
        logger.info("Starting ...");
        ConfigDO configDO = getconfig(configPath);
        ConnectionTable connectionTable = createConnectionTable(configDO);
        runJetty(connectionTable, port);
        long end = System.currentTimeMillis();
        logger.info("Started in "+(end-start)+"ms.");
    }

    private ConfigDO getconfig(String configPath){
        logger.info("try to load configs ...");
        ConfigDO configDO = ConfigUtil.getConfig(configPath);
        logger.info("Load config successfully");
        return configDO;
    }

    private ConnectionTable createConnectionTable(ConfigDO configDO){
        logger.info("try to create connection table ...");
        ConnectionTable table = new ConnectionTable(configDO);
        logger.info("Create connection table successfully");
        return table;
    }

    private void runJetty(ConnectionTable connectionTable, int port) throws Exception {
        logger.info("Starting web server ...");
        Server server = new Server();
        ServerConnector connector = new ServerConnector(server);
        connector.setPort(port);
        server.setConnectors(new Connector[]{connector});
        ServletHandler servletHandler = new ServletHandler();
        ServletHolder qsHolder = new ServletHolder(new QueryServlet(connectionTable));
        servletHandler.addServletWithMapping(qsHolder, QueryServlet.PREFIX+"*");
        server.setHandler(servletHandler);
        server.start();
        logger.info("Start web server successfully ...");
    }
}
