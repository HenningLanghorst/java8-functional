package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import de.henninglanghorst.functional.sql.function.Supplier;
import de.henninglanghorst.functional.util.Either;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Class for database operations.
 *
 * @author Henning Langhorst
 */
public final class DatabaseOperations {

    private DatabaseOperations() {
    }

    /**
     * Creates a {@link Connection}, applies a function (e.g. for selecting data) on an this database connection
     * and closes it.
     *
     * @param connectionFactory {@link Supplier} providing the collection to be used.
     * @param dbFunction        Function applied to the connection.
     * @param <R>               Type of the return value after successful execution.
     * @return Either the return value of Type {@link R} or the {@link SQLException} in case of an error.
     */
    public static <R> Either<R, SQLException> doInDatabase(final Supplier<Connection> connectionFactory,
                                                           final Function<Connection, R> dbFunction) {
        try (Connection connection = connectionFactory.get()) {
            R result = dbFunction.apply(connection);
            return Either.left(result);
        } catch (SQLException e) {
            return Either.right(e);
        }
    }

    /**
     * Returns a function which performs a SQL query on the database for use in {@link #doInDatabase(Supplier, Function)}.
     *
     * @param preparedStatementFactory Creates the select statement using the given {@link Connection}.
     * @param resultSetMapper          Maps the the {@link ResultSet} entries to a specific type.
     * @param <R>                      Type to which every {@link ResultSet} entry is mapped.
     * @return A Function returning a List with Elements of typ {@link R}.
     */
    public static <R> Function<Connection, List<R>> databaseQuery(final Function<Connection, PreparedStatement> preparedStatementFactory,
                                                                  final Function<ResultSet, R> resultSetMapper) {
        return connection -> performQueryOnConnection(connection, preparedStatementFactory, resultSetMapper);
    }

    private static <R> List<R> performQueryOnConnection(final Connection connection,
                                                        final Function<Connection, PreparedStatement> preparedStatementFactory,
                                                        final Function<ResultSet, R> resultSetMapper) throws SQLException {
        try (PreparedStatement preparedStatement = preparedStatementFactory.apply(connection)) {
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                List<R> result = new ArrayList<>();
                while (resultSet.next()) {
                    final R entry = resultSetMapper.apply(resultSet);
                    result.add(entry);
                }
                return result;
            }
        }
    }

    /**
     * Returns a function which performs a SQL update on the database.
     *
     * @param preparedStatementFactory Creates the update statement using the given {@link Connection}.
     * @return A Function returning an {@link Integer} value.
     */
    public static Function<Connection, Integer> databaseUpdate(final Function<Connection, PreparedStatement> preparedStatementFactory) {
        return connection -> performUpdateOnConnection(connection, preparedStatementFactory);
    }


    private static int performUpdateOnConnection(final Connection connection,
                                                 final Function<Connection, PreparedStatement> preparedStatementFactory) throws SQLException {
        try (PreparedStatement preparedStatement = preparedStatementFactory.apply(connection)) {
            return preparedStatement.executeUpdate();
        }
    }

    public static Function<Connection, int[]> multipleDatabaseUpdates(final Collection< Function<Connection, PreparedStatement>> preparedStatementFactories) {
        return connection -> performUpdatesOnConnection(connection, preparedStatementFactories);
    }

    private static int[] performUpdatesOnConnection(final Connection connection, final Collection<Function<Connection, PreparedStatement>> preparedStatementFactories) throws SQLException {
        int[] result = new int[preparedStatementFactories.size()];
        int currentIndex = 0;
        for (Function<Connection, PreparedStatement> f : preparedStatementFactories){
            result[currentIndex++] = performUpdateOnConnection(connection, f);
        }
        return result;
    }



    /**
     * Returns a function creating a {@link PreparedStatement} with the given SQL statement from a {@link Connection}.
     *
     * @param sql SQL statement to be used in {@link Connection#prepareStatement(String)} when preparing the statement.
     * @return Function preparing a statement from a connection.
     */
    public static Function<Connection, PreparedStatement> statement(final String sql) {
        return connection -> connection.prepareStatement(sql);
    }

    /**
     * * Returns a function creating a {@link PreparedStatement} with given the SQL statement and parameters from a {@link Connection}.
     *
     * @param sql        SQL statement to be used in {@link Connection#prepareStatement(String)} when preparing the statement.
     * @param parameters Parameters to be set on {@link PreparedStatement}.
     * @return Function preparing a statement from a connection.
     */
    public static Function<Connection, PreparedStatement> statement(final String sql, Object... parameters) {
        return connection -> prepareStatementWithParameters(connection, sql, parameters);
    }

    private static PreparedStatement prepareStatementWithParameters(final Connection connection,
                                                                    final String sql,
                                                                    final Object... parameters) throws SQLException {
        final PreparedStatement preparedStatement = connection.prepareStatement(sql);
        for (int i = 0; i < parameters.length; i++) {
            preparedStatement.setObject(i + 1, parameters[i]);
        }
        return preparedStatement;
    }


    /**
     * Performs the given database operation within a transaction.
     *
     * @param databaseOperation Operation to be performed within a transaction.
     * @param <T>               Type of the result of the database operation.
     * @return A function performing transaction handling around the given function.
     */
    public static <T> Function<Connection, T> withinTransaction(Function<Connection, T> databaseOperation) {
        final Function<Connection, T> performWithinTransaction = connection1 -> performWithinTransaction(databaseOperation, connection1);
        return connection -> preserveAutoCommit(connection, performWithinTransaction);
    }

    private static <T> T performWithinTransaction(final Function<Connection, T> databaseOperation, final Connection connection) throws SQLException {
        try {
            final T result = databaseOperation.apply(connection);
            connection.commit();
            return result;
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        }
    }

    private static <T> T preserveAutoCommit(Connection connection, Function<Connection, T> actualOperation) throws SQLException {
        final boolean autoCommitInitiallyEnabled = connection.getAutoCommit();
        if (autoCommitInitiallyEnabled) {
            connection.setAutoCommit(false);
        }
        final T result = actualOperation.apply(connection);

        if (autoCommitInitiallyEnabled) {
            connection.setAutoCommit(true);
        }

        return result;
    }

}