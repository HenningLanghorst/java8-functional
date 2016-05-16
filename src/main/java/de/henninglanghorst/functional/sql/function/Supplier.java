package de.henninglanghorst.functional.sql.function;

import java.sql.SQLException;

/**
 * Provides an Object of type {@link T} and is able to throw {@link SQLException}s.
 *
 * @author Henning Langhorst
 */
@FunctionalInterface
public interface Supplier<T> {
    T get() throws SQLException;
}
