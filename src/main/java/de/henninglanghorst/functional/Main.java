package de.henninglanghorst.functional;

import de.henninglanghorst.functional.model.Person;
import de.henninglanghorst.functional.sql.function.Supplier;
import org.h2.jdbcx.JdbcConnectionPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Month;
import java.util.Arrays;
import java.util.List;

import static de.henninglanghorst.functional.PersonDbFunctions.*;
import static de.henninglanghorst.functional.sql.DatabaseOperations.doInDatabase;
import static de.henninglanghorst.functional.sql.DatabaseTransactionFunctions.withinTransaction;
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

        doInDatabase(connectionFactory,
                withinTransaction(
                        insertPersons(
                                new Person(1, "Carl", "Carlsson", LocalDate.of(1972, Month.APRIL, 2)),
                                new Person(2, "Lenny", "Leonard", LocalDate.of(1981, Month.APRIL, 2))
                        )))
                .handleResult(
                        objects -> LOGGER.info("Inserted rows: " + Arrays.toString(objects)),
                        Main::logError);

        doInDatabase(connectionFactory, selectAllPersons())
                .handleResult(persons -> LOGGER.info("All Persons selected:" + listToString(persons)), Main::logError);


        doInDatabase(connectionFactory, selectPersonWithId(1))
                .handleResult(person -> LOGGER.info("Person with Id 1 selected: " + person), Main::logError);


        connectionPool.dispose();
    }


    private static void logError(final SQLException e) {
        LOGGER.error("Error during database operation", e);
    }

    private static String listToString(final List<Person> persons) {
        return persons.stream().map(Object::toString).collect(joining("\n        ", "\nPersons ", ""));
    }


}
