package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import de.henninglanghorst.functional.sql.function.Supplier;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class providing functions for transaction handling, usable with {@link DatabaseOperations#doInDatabase(Supplier, Function)}.
 *
 * @author Henning Langhorst
 */
public final class DatabaseTransactionFunctions {

    private DatabaseTransactionFunctions() {
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
