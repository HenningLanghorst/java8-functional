package de.henninglanghorst.functional.sql.function;

import java.sql.SQLException;

/**
 * Represents a function which can throw an {@link SQLException}.
 *
 * @author Henning Langhorst
 */
public interface Function<T, R> {
    R apply(T t) throws SQLException;

    default <S> Function<T, S> andThen(Function<R, S> after) {
        return t -> after.apply(apply(t));
    }

    default Supplier<R> curry(T t) {
        return () -> apply(t);
    }
}
