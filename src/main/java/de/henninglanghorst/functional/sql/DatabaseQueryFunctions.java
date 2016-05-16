package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import de.henninglanghorst.functional.sql.function.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class providing functions for SQL queries, usable with {@link DatabaseOperations#doInDatabase(Supplier, Function)}.
 *
 * @author Henning Langhorst
 */
public final class DatabaseQueryFunctions {

    private DatabaseQueryFunctions() {
    }

    /**
     * Returns a function which performs a SQL query on the database for use in
     * {@link DatabaseOperations#doInDatabase(Supplier, Function)}.
     *
     * @param prepStmtFactory Creates the select statement using the given {@link Connection}.
     * @param resultSetExtraction      Function extracting data from {@link ResultSet}.
     * @param <R>                      Type to which the {@link ResultSet} is mapped.
     * @return A Function returning a List of Elements of type {@link R}.
     */
    public static <R> Function<Connection, R> databaseQuery(final Function<Connection, PreparedStatement> prepStmtFactory,
                                                            final Function<ResultSet, R> resultSetExtraction) {
        return connection -> performQueryOnConnection(connection, prepStmtFactory, resultSetExtraction);
    }

    private static <R> R performQueryOnConnection(final Connection connection,
                                                  final Function<Connection, PreparedStatement> preparedStatementFactory,
                                                  final Function<ResultSet, R> resultSetMapper) throws SQLException {
        try (PreparedStatement preparedStatement = preparedStatementFactory.apply(connection)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                return resultSetMapper.apply(resultSet);
            }
        }
    }

    /**
     * Creates a function which extracts data from a {@link ResultSet} using a given mapper function.
     *
     * @param resultSetMapper Function used for extracting single data records from {@link ResultSet}.
     * @param <R>             Type to which every {@link ResultSet} entry is mapped.
     * @return A Function returning multiple elements of type {@link R} from the {@link ResultSet}.
     */
    public static <R> Function<ResultSet, List<R>> multipleRowExtraction(final Function<ResultSet, R> resultSetMapper) {
        return resultSet -> extractRowsFromResultSet(resultSet, resultSetMapper);
    }

    private static <R> List<R> extractRowsFromResultSet(final ResultSet resultSet,
                                                        final Function<ResultSet, R> resultSetMapper) throws SQLException {
        List<R> result = new ArrayList<>();
        while (resultSet.next()) {
            final R entry = resultSetMapper.apply(resultSet);
            result.add(entry);
        }
        return result;
    }

    /**
     * Creates a function which extracts one data record from a {@link ResultSet} using a given mapper function.
     *
     * @param resultSetMapper Function used for extracting a single data record from {@link ResultSet}.
     * @param <R>             Type to which the {@link ResultSet} row is mapped.
     * @return A Function returning a single element of type {@link R} from the {@link ResultSet}.
     */
    public static <R> Function<ResultSet, R> singleRowExtraction(final Function<ResultSet, R> resultSetMapper) {
        return resultSet -> extractSingleRowFromResultSet(resultSet, resultSetMapper);
    }

    private static <R> R extractSingleRowFromResultSet(final ResultSet resultSet,
                                                       final Function<ResultSet, R> resultSetMapper) throws SQLException {
        if (!resultSet.next()) {
            throw new SQLException("No data found");
        }
        final R entry = resultSetMapper.apply(resultSet);
        if (resultSet.next()) {
            throw new SQLException("More than one record in result");
        }
        return entry;
    }


}
