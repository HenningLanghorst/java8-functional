package de.henninglanghorst.functional;

import de.henninglanghorst.functional.model.Person;
import de.henninglanghorst.functional.sql.function.Function;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import static de.henninglanghorst.functional.sql.DatabaseOperations.statement;
import static de.henninglanghorst.functional.sql.DatabaseQueryFunctions.*;
import static de.henninglanghorst.functional.sql.DatabaseTransactionFunctions.withinTransaction;
import static de.henninglanghorst.functional.sql.DatabaseUpdateFunctions.databaseUpdate;
import static de.henninglanghorst.functional.sql.DatabaseUpdateFunctions.multipleDatabaseUpdates;
import static java.util.Arrays.asList;

/**
 * Contains database function relating to database table {@code Person}.
 *
 * @author Henning Langhorst
 */
public final class PersonDbFunctions {

    private PersonDbFunctions() {
    }

    public static Function<Connection, Integer> dropTablePerson() {
        return databaseUpdate(statement("drop table Person"));
    }

    public static Function<Connection, Integer> createTablePerson() {
        return databaseUpdate(
                statement("create table Person (" +
                        "id integer primary key, firstName varchar2, " +
                        "lastName varchar2, birthday date)"));
    }

    public static Function<Connection, int[]> insertPersons() {
        return withinTransaction(
                multipleDatabaseUpdates(
                        asList(
                                statement(
                                        "insert into Person values (?, ?, ?, ?);",
                                        1, "Carl", "Carlsson", Date.valueOf("1972-04-02")),
                                statement("insert into Person values (?, ?, ?, ?);",
                                        2, "Lenny", "Leonard", Date.valueOf("1981-04-02")))
                ));
    }

    public static Function<Connection, List<Person>> selectAllPersons() {
        return databaseQuery(
                statement("select * from Person"),
                multipleRowExtraction(PersonDbFunctions::mapResultSetToPerson));
    }

    public static Function<Connection, Person> selectPersonWithId(int id) {
        return databaseQuery(
                statement("select * from Person where id = ?", id),
                singleRowExtraction(PersonDbFunctions::mapResultSetToPerson));
    }

    private static Person mapResultSetToPerson(final ResultSet resultSet) throws SQLException {
        return new Person(
                resultSet.getInt("id"),
                resultSet.getString("firstName"),
                resultSet.getString("lastName"),
                resultSet.getDate("birthday").toLocalDate());
    }


}
