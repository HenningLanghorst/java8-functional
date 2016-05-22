package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import de.henninglanghorst.functional.sql.function.Supplier;
import de.henninglanghorst.functional.util.Either;

import java.sql.Connection;
import java.sql.SQLException;

/**
 * Class for database operations.
 *
 * @author Henning Langhorst
 */
public final class DatabaseOperationFunctions {

    private DatabaseOperationFunctions() {
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


}