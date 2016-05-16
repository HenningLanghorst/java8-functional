package de.henninglanghorst.functional.sql.function;

import java.sql.SQLException;

/**
 * Provides an Object of type {@link T}.
 *
 * @author Henning Langhorst
 */
public interface Supplier<T> {
    T get() throws SQLException;
}
