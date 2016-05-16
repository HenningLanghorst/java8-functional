package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import de.henninglanghorst.functional.sql.function.Supplier;
import de.henninglanghorst.functional.util.Either;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
     * Returns a function creating a {@link PreparedStatement} with the given SQL statement from a {@link Connection}.
     *
     * @param sql SQL statement to be used in {@link Connection#prepareStatement(String)} when preparing the statement.
     * @return Function preparing a statement from a connection.
     */
    public static Function<Connection, PreparedStatement> statement(final String sql) {
        return connection -> connection.prepareStatement(sql);
    }


    /**
     * Returns a function creating a {@link PreparedStatement} with given the SQL statement and parameters from a
     * {@link Connection}.
     *
     * @param sql        SQL statement to be used in {@link Connection#prepareStatement(String)} when preparing the
     *                   statement.
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

}