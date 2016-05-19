package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import de.henninglanghorst.functional.sql.function.Supplier;
import de.henninglanghorst.functional.util.Either;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import static de.henninglanghorst.functional.sql.DatabaseOperations.doInDatabase;
import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests class {@link DatabaseOperations}.
 *
 * @author Henning Langhorst
 */
public class DatabaseOperationsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Test
    public void shouldInvokeSupplierAndConnectionFunctionAndReturnResultInEitherObject() throws Exception {
        // given
        final Supplier<Connection> connectionSupplier = () -> connection;
        final String dbResult = "DB result";
        final Function<Connection, String> connectionFunction = c -> dbResult;

        // when
        final Either<String, SQLException> result = doInDatabase(connectionSupplier, connectionFunction);

        // then
        assertThat(result).isEqualTo(Either.left(dbResult));
    }


    @Test
    public void shouldInvokeSupplierAndConnectionFunctionAndReturnThrownSqlExceptionInEitherObject() throws Exception {
        // given
        final Supplier<Connection> connectionSupplier = () -> connection;
        final SQLException exceptionToBeThrown = new SQLException("Test");
        final Function<Connection, String> connectionFunction = c -> {
            throw exceptionToBeThrown;
        };

        // when
        final Either<String, SQLException> result = doInDatabase(connectionSupplier, connectionFunction);

        // then
        assertThat(result).isEqualTo(Either.right(exceptionToBeThrown));
    }


}