package de.henninglanghorst.functional.util;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.util.function.Consumer;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;

/**
 * Either Tester.
 *
 * @author <Authors name>
 * @version 1.0
 * @since <pre>Mai 15, 2016</pre>
 */
public class EitherTest {

    @Rule
    public MockitoRule mockitoRule = MockitoJUnit.rule();

    @Mock
    private Consumer<Integer> integerConsumer;

    @Mock
    private Consumer<String> stringConsumer;


    @Test
    public void isLeftShouldReturnTrueOnLeftValue() throws Exception {
        final Either<Integer, ?> left = Either.left(2);
        assertThat(left.isLeft(), is(true));
        assertThat(left.isRight(), is(false));
    }

    @Test
    public void leftConsumerShouldBeInvokedOnLeftValue() throws Exception {
        final Either<Integer, String> left = Either.left(2);
        left.handleResult(integerConsumer, stringConsumer);
        verify(integerConsumer).accept(2);
        verifyZeroInteractions(stringConsumer);
    }

    @Test
    public void isRightShouldReturnTrueOnRightValue() throws Exception {
        final Either<?, String> right = Either.right("Hallo");
        assertThat(right.isLeft(), is(false));
        assertThat(right.isRight(), is(true));
    }

    @Test
    public void rightConsumerShouldBeInvokedOnRightValue() throws Exception {
        final Either<Integer, String> right = Either.right("Hallo");
        right.handleResult(integerConsumer, stringConsumer);
        verifyZeroInteractions(integerConsumer);
        verify(stringConsumer).accept("Hallo");
    }

} 
