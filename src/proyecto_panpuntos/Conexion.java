package proyecto_panpuntos;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class Conexion {

    private static final String ORACLE_URL = "jdbc:oracle:thin:@localhost:1521:XE";
    private static final String ORACLE_USER = "system";
    private static final String ORACLE_PASS = "1234";

    private static final String SUPABASE_URL = "jdbc:postgresql://aws-0-us-east-1.pooler.supabase.com:6543/postgres?sslmode=require";
    private static final String SUPABASE_USER = "postgres.tbiimjbtvczvpbivsgiu";
    private static final String SUPABASE_PASS = "Morita003$*!";

    public static Connection getConexion() throws SQLException {
        String envUseSupabase = System.getenv("USE_SUPABASE");
        if ("true".equalsIgnoreCase(envUseSupabase) 
                || System.getenv("RENDER") != null 
                || System.getenv("RAILWAY_ENVIRONMENT") != null 
                || System.getenv("PORT") != null) {
            return getConexionSupabase();
        }

        try {
            Class.forName("oracle.jdbc.OracleDriver");
            return DriverManager.getConnection(ORACLE_URL, ORACLE_USER, ORACLE_PASS);
        } catch (Exception e) {
            return getConexionSupabase();
        }
    }

    public static Connection getConexionSupabase() throws SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("Driver de PostgreSQL no encontrado", e);
        }
        return DriverManager.getConnection(SUPABASE_URL, SUPABASE_USER, SUPABASE_PASS);
    }
}