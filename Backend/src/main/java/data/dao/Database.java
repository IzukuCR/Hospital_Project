package data.dao;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Properties;

public class Database {
    private static Database theInstance;

    public static Database instance(){
        if (theInstance == null){
            theInstance = new Database();
        }
        return theInstance;
    }

    private static final String PROPERTIES_FILE_NAME = "/database.properties";
    private Connection cnx;

    private Database(){
        cnx = this.getConnection();
    }

    private Connection getConnection(){
        try (InputStream input = getClass().getResourceAsStream(PROPERTIES_FILE_NAME)) {
            Properties prop = new Properties();
            prop.load(input);

            String driver = prop.getProperty("database_driver");
            String server = prop.getProperty("database_server");
            String port = prop.getProperty("database_port");
            String user = prop.getProperty("database_user");
            String password = prop.getProperty("database_password");
            String database = prop.getProperty("database_name");

            String URL_conexion = "jdbc:mysql://" + server + ":" + port + "/" +
                    database + "?user=" + user + "&password=" + password +
                    "&serverTimezone=UTC&useSSL=false";

            Class.forName(driver).getDeclaredConstructor().newInstance();
            return DriverManager.getConnection(URL_conexion);
        } catch (Exception e) {
            System.err.println("Error conectando a la base de datos: " + e.getMessage());
            e.printStackTrace();
            System.exit(-1);
        }
        return null;
    }

    public PreparedStatement prepareStatement(String statement) throws SQLException {
        return cnx.prepareStatement(statement);
    }

    public int executeUpdate(PreparedStatement statement) {
        try {
            statement.executeUpdate();
            return statement.getUpdateCount();
        } catch (SQLException ex) {
            System.err.println("Error ejecutando update: " + ex.getMessage());
            return 0;
        }
    }

    public ResultSet executeQuery(PreparedStatement statement){
        try {
            return statement.executeQuery();
        } catch (SQLException ex) {
            System.err.println("Error ejecutando query: " + ex.getMessage());
        }
        return null;
    }

    public void close() throws Exception {
        if (cnx != null && !cnx.isClosed()){
            cnx.close();
        }
    }
}
