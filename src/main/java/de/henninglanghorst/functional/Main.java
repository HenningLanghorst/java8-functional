package de.henninglanghorst.functional;

import de.henninglanghorst.functional.model.Person;
import de.henninglanghorst.functional.sql.function.Supplier;
import de.henninglanghorst.functional.util.Either;
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
import static de.henninglanghorst.functional.sql.DatabaseOperationFunctions.doInDatabase;
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

        JdbcConnectionPool connectionPool = JdbcConnectionPool.create("jdbc:h2:~/testdb", "sa", "");

        Supplier<Connection> connectionFactory = connectionPool::getConnection;

        Either<Integer, SQLException> dropTableResult = doInDatabase(connectionFactory, dropTablePerson());
        dropTableResult.handleResult(objects -> LOGGER.info("Success " + objects), Main::logError);

        Either<Integer, SQLException> createTableResult = doInDatabase(connectionFactory, createTablePerson());
        createTableResult.handleResult(objects -> LOGGER.info("Success " + objects), Main::logError);

        Either<int[], SQLException> insertPersonsResult = doInDatabase(
                connectionFactory,
                withinTransaction(
                        insertPersons(
                                new Person(1, "Carl", "Carlsson", LocalDate.of(1972, Month.APRIL, 2)),
                                new Person(2, "Lenny", "Leonard", LocalDate.of(1981, Month.APRIL, 2))
                        )));
        insertPersonsResult.handleResult(
                        objects -> LOGGER.info("Inserted rows: " + Arrays.toString(objects)),
                        Main::logError);

        Either<List<Person>, SQLException> selectAllPersonsResult =
                doInDatabase(
                        connectionFactory,
                        selectAllPersons());
        selectAllPersonsResult.handleResult(
                persons -> LOGGER.info("All Persons selected:" + listToString(persons)),
                Main::logError);


        Either<Person, SQLException> selectPersonWithId1Result = doInDatabase(connectionFactory, selectPersonWithId(1));
        selectPersonWithId1Result.handleResult(
                person -> LOGGER.info("Person with Id 1 selected: " + person),
                Main::logError);


        connectionPool.dispose();
    }


    private static void logError(final SQLException e) {
        LOGGER.error("Error during database operation", e);
    }

    private static String listToString(final List<Person> persons) {
        return persons.stream().map(Object::toString).collect(joining("\n        ", "\nPersons ", ""));
    }


}
