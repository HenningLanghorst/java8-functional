package de.henninglanghorst.functional;

import de.henninglanghorst.functional.model.Person;
import de.henninglanghorst.functional.sql.function.Supplier;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static de.henninglanghorst.functional.PersonDbFunctions.*;
import static de.henninglanghorst.functional.sql.DatabaseOperations.doInDatabase;
import static java.util.stream.Collectors.joining;

/**
 * Example program.
 *
 * @author Henning Langhorst
 */
public class Main {

    private static final Logger LOGGER = LoggerFactory.getLogger(Main.class);


    public static void main(String[] args) {

        final JdbcConnectionPool connectionPool = JdbcConnectionPool.create("jdbc:h2:~/testdb", "sa", "");

        final Supplier<Connection> connectionFactory = connectionPool::getConnection;

        doInDatabase(connectionFactory, dropTablePerson())
                .handleResult(objects -> LOGGER.info("Success " + objects), Main::logError);

        doInDatabase(connectionFactory, createTablePerson())
                .handleResult(objects -> LOGGER.info("Success " + objects), Main::logError);

        doInDatabase(connectionFactory, insertPersons())
                .handleResult(
                objects -> LOGGER.info("Inserted rows: " + Arrays.toString(objects)),
                Main::logError);

        doInDatabase(connectionFactory, selectAllPersons())
                .handleResult(persons -> LOGGER.info("All Persons selected:" + listToString(persons)), Main::logError);


        doInDatabase(connectionFactory, selectPersonWithId(1))
                .handleResult(persons -> LOGGER.info("Person with Id 1 selected:" + listToString(persons)), Main::logError);


        connectionPool.dispose();
    }


    private static void logError(final SQLException e) {
        LOGGER.error("Error during database operation", e);
    }

    private static String listToString(final List<Person> persons) {
        return persons.stream().map(Object::toString).collect(joining("\n        ", "\nPersons ", ""));
    }


}
