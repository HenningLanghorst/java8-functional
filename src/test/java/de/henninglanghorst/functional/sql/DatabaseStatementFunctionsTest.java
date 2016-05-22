package de.henninglanghorst.functional.sql;

import de.henninglanghorst.functional.sql.function.Function;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.sql.Connection;
import java.sql.PreparedStatement;

import static de.henninglanghorst.functional.sql.DatabaseStatementFunctions.statement;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

/**
 * Tests for functions in {@link DatabaseStatementFunctions}.
 *
 * @author Henning Langhorst
 */
public class DatabaseStatementFunctionsTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement preparedStatement;

    @Test
    public void statementShouldReturnFunctionCreatingStatementFromConnection() throws Exception {
        // given
        final String sql = "select 1 from dual";
        given(connection.prepareStatement(sql)).willReturn(preparedStatement);
        // when
        final Function<Connection, PreparedStatement> statementFunction = statement(sql);
        // then
        assertThat(statementFunction.apply(connection)).isSameAs(preparedStatement);
    }

    @Test
    public void statementShouldReturnFunctionCreatingStatementFromConnectionAndParameters() throws Exception {
        // given
        final String sql = "select 1 from dual where name = ?";
        String stringParam = "SomeName";
        given(connection.prepareStatement(sql)).willReturn(preparedStatement);
        // when
        final Function<Connection, PreparedStatement> statementFunction = statement(sql, stringParam);
        // then
        final PreparedStatement stmt = statementFunction.apply(connection);
        assertThat(stmt).isSameAs(preparedStatement);
        verify(stmt).setObject(1, stringParam);
    }
}