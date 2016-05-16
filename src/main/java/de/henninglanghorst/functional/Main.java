package de.henninglanghorst.functional;

import de.henninglanghorst.functional.model.Person;
import de.henninglanghorst.functional.util.Either;
import org.h2.jdbcx.JdbcConnectionPool;

import java.sql.Date;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import static de.henninglanghorst.functional.sql.DatabaseOperations.*;
import static java.util.Arrays.asList;

/**
 * Example program.
 *
 * @author Henning Langhorst
 */
public class Main {

    public static void main(String[] args) {

        final JdbcConnectionPool cp = JdbcConnectionPool.create("jdbc:h2:~/testdb", "sa", "");

        doInDatabase(cp::getConnection, databaseUpdate(statement("drop table Person")))
                .handleResult(objects -> System.out.println("Success " + objects), Throwable::printStackTrace);

        doInDatabase(
                cp::getConnection,
                withinTransaction(
                        databaseUpdate(
                                statement("create table Person (id integer primary key, firstName varchar2, lastName varchar2, birthday date)"))))
                .handleResult(objects -> System.out.println("Success " + objects), Throwable::printStackTrace);

        doInDatabase(cp::getConnection, multipleDatabaseUpdates(
                asList(
                        statement(
                                "insert into Person values (?, ?, ?, ?);",
                                1, "Carl", "Carlsson", Date.valueOf("1972-04-02")),
                        statement("insert into Person values (?, ?, ?, ?);",
                                2, "Lenny", "Leonard", Date.valueOf("1981-04-02")))
                )
        ).handleResult(
                objects -> System.out.println("Inserted rows: " + Arrays.toString(objects)),
                Throwable::printStackTrace);



        final Either<List<Person>, SQLException> result = doInDatabase(
                cp::getConnection,
                databaseQuery(
                        statement("select * from Person where id = ?", 1),
                        resultSet -> new Person(
                                resultSet.getInt("id"),
                                resultSet.getString("firstName"),
                                resultSet.getString("lastName"),
                                resultSet.getDate("birthday").toLocalDate())));


        System.out.println(result.left().get());
    }


}
