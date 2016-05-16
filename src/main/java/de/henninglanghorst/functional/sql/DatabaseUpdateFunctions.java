package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import de.henninglanghorst.functional.sql.function.Supplier;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;

/**
 * Class providing functions for updating database tables, usable with {@link DatabaseOperations#doInDatabase(Supplier, Function)}.
 *
 * @author Henning Langhorst
 */
public final class DatabaseUpdateFunctions {

    private DatabaseUpdateFunctions() {
    }

    /**
     * Returns a function which performs a SQL update on the database.
     *
     * @param preparedStatementFactory Creates the update statement using a given {@link Connection}.
     * @return A Function performing the update and returning an {@link Integer} value.
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

    /**
     * Returns a function which performs multiple SQL update on the database.
     *
     * @param preparedStatementFactories List of {@link PreparedStatement} factories creating update statements using a given {@link Connection}.
     * @return A Function performing the updates and returning an {@code int[]} array value with the number of the updates rows per update.
     */
    public static Function<Connection, int[]> multipleDatabaseUpdates(final Collection<Function<Connection, PreparedStatement>> preparedStatementFactories) {
        return connection -> performUpdatesOnConnection(connection, preparedStatementFactories);
    }
    private static int[] performUpdatesOnConnection(final Connection connection, final Collection<Function<Connection, PreparedStatement>> preparedStatementFactories) throws SQLException {
        int[] result = new int[preparedStatementFactories.size()];
        int currentIndex = 0;
        for (Function<Connection, PreparedStatement> f : preparedStatementFactories) {
            result[currentIndex++] = performUpdateOnConnection(connection, f);
        }
        return result;
    }


}
