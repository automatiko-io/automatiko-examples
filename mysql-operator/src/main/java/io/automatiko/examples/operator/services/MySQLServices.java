package io.automatiko.examples.operator.services;

import static java.lang.String.format;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import jakarta.enterprise.context.ApplicationScoped;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.automatiko.engine.api.workflow.ServiceExecutionError;
import io.automatiko.examples.operator.MySQLSchema;

@ApplicationScoped
public class MySQLServices {

    private final Logger log = LoggerFactory.getLogger(getClass());

    public String createSchema(MySQLSchema resource, String username, String password) {
        try (Connection connection = getConnection()) {
            if (!schemaExists(connection, resource.getMetadata().getName())) {
                connection.createStatement().execute(format("CREATE SCHEMA `%1$s` DEFAULT CHARACTER SET %2$s",
                        resource.getMetadata().getName(),
                        resource.getSpec().getEncoding()));

                connection.createStatement().execute(format(
                        "CREATE USER '%1$s' IDENTIFIED BY '%2$s'",
                        username, password));
                connection.createStatement().execute(format(
                        "GRANT ALL ON `%1$s`.* TO '%2$s'",
                        resource.getMetadata().getName(), username));
                log.info("Schema '{}' created", resource.getMetadata().getName());
            }
            return format("jdbc:mysql://%1$s/%2$s",
                    System.getenv("MYSQL_HOST"),
                    resource.getMetadata().getName());
        } catch (Exception e) {
            throw new ServiceExecutionError("0000001", e);
        }
    }

    public void deleteSchema(MySQLSchema resource) {
        try (Connection connection = getConnection()) {
            if (schemaExists(connection, resource.getMetadata().getName())) {
                connection.createStatement().execute("DROP DATABASE `" + resource.getMetadata().getName() + "`");

                log.info("Schema '{}' deleted", resource.getMetadata().getName());
            }
        } catch (SQLException e) {
            throw new ServiceExecutionError("000002", e);
        }
    }

    public void deleteUser(MySQLSchema resource) {
        try (Connection connection = getConnection()) {
            if (userExists(connection, resource.getStatus().getUserName())) {
                connection.createStatement().execute("DROP USER '" + resource.getStatus().getUserName() + "'");
                log.info("User '{}' deleted", resource.getStatus().getUserName());
            }
        } catch (SQLException e) {
            throw new ServiceExecutionError("0000002", e);
        }
    }

    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(format("jdbc:mysql://%1$s:%2$s?user=%3$s&password=%4$s",
                System.getenv("MYSQL_HOST"),
                System.getenv("MYSQL_PORT") != null ? System.getenv("MYSQL_PORT") : "3306",
                System.getenv("MYSQL_USER"),
                System.getenv("MYSQL_PASSWORD")));
    }

    private boolean schemaExists(Connection connection, String schemaName) throws SQLException {
        ResultSet resultSet = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
                .executeQuery(
                        format("SELECT schema_name FROM information_schema.schemata WHERE schema_name = \"%1$s\"",
                                schemaName));
        return resultSet.first();
    }

    private boolean userExists(Connection connection, String userName) throws SQLException {
        ResultSet resultSet = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_READ_ONLY)
                .executeQuery(
                        format("SELECT User FROM mysql.user WHERE User='%1$s'", userName));
        return resultSet.first();
    }
}
