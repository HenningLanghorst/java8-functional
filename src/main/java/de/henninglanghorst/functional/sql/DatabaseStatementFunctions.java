package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Provides Functions for creating {@link PreparedStatement}s.
 *
 * @author Henning Langhorst
 */
public class DatabaseStatementFunctions {

    private DatabaseStatementFunctions() {
        // prevent instantiation
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
