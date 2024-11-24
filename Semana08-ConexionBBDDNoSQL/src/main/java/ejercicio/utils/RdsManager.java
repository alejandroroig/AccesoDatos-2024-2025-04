package ejercicio.utils;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Properties;

public class RdsManager {
    private static Connection connection;

    public static Connection getConnection() {
        if (connection == null) {
            initializeConnection();
        }
        return connection;
    }

    private static Properties loadProperties() {
        Properties properties = new Properties();
        try (InputStream input = RdsManager.class.getClassLoader().getResourceAsStream("rds.properties")) {
            properties.load(input);
        } catch (Exception ex) {
            System.err.println("Error al cargar el archivo de propiedades: " + ex.getMessage());
        }
        return properties;
    }

    private static void initializeConnection() {
        Properties properties = loadProperties();
        String url = properties.getProperty("db.url");
        String user = properties.getProperty("db.user");
        String password = properties.getProperty("db.password");

        try {
            connection = DriverManager.getConnection(url, user, password);
            System.out.println("Conexión exitosa a la base de datos.");
        } catch (Exception ex) {
            System.err.println("Error al conectar a la base de datos: " + ex.getMessage());
        }
    }
}
