package io.automatiko.examples.operator;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import io.automatiko.engine.api.Functions;

public class MySQLFunctions implements Functions {

    public static void setStatus(MySQLSchema resource, String url, String username, String secretName) {
        SchemaStatus status = new SchemaStatus();

        status.setUrl(url);
        status.setUserName(username);
        status.setSecretName(secretName);
        status.setStatus("CREATED");
        resource.setStatus(status);
    }

    public static void setErrorStatus(MySQLSchema resource, String error) {
        SchemaStatus status = new SchemaStatus();

        status.setStatus(error);
        resource.setStatus(status);
    }

    public static boolean noSchemaFound(MySQLSchema resource) {

        try (Connection connection = getConnection()) {

            ResultSet resultSet = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
                    .executeQuery(
                            format("SELECT schema_name FROM information_schema.schemata WHERE schema_name = \"%1$s\"",
                                    resource.getMetadata().getName()));
            return !resultSet.first();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static boolean hasUser(String username) {
        try (Connection connection = getConnection()) {

            ResultSet resultSet = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
                    .executeQuery(
                            format("SELECT User FROM mysql.user WHERE User='%1$s'", username));
            return resultSet.first();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(format("jdbc:mysql://%1$s:%2$s?user=%3$s&password=%4$s",
                System.getenv("MYSQL_HOST"),
                System.getenv("MYSQL_PORT") != null ? System.getenv("MYSQL_PORT") : "3306",
                System.getenv("MYSQL_USER"),
                System.getenv("MYSQL_PASSWORD")));
    }
}
