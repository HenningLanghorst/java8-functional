package de.henninglanghorst.functional;

import de.henninglanghorst.functional.model.Person;
import de.henninglanghorst.functional.sql.function.Function;

import java.sql.*;
import java.util.List;
import java.util.stream.Stream;

import static de.henninglanghorst.functional.sql.DatabaseOperations.statement;
import static de.henninglanghorst.functional.sql.DatabaseQueryFunctions.*;
import static de.henninglanghorst.functional.sql.DatabaseUpdateFunctions.databaseUpdate;
import static de.henninglanghorst.functional.sql.DatabaseUpdateFunctions.multipleDatabaseUpdates;
import static java.util.stream.Collectors.toList;

/**
 * Contains database functions relating to database table {@code Person}.
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

    public static Function<Connection, int[]> insertPersons(Person... persons) {
        return
                multipleDatabaseUpdates(
                        Stream.of(persons)
                                .map(PersonDbFunctions::insertPerson)
                                .collect(toList()));
    }

    private static Function<Connection, PreparedStatement> insertPerson(final Person person) {
        return statement("insert into Person values (?, ?, ?, ?);",
                person.getId(),
                person.getFirstName(),
                person.getLastName(),
                Date.valueOf(person.getBirthday()));
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
