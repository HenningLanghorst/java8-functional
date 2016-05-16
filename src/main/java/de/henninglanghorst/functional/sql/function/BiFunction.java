package de.henninglanghorst.functional.sql.function;

import java.sql.SQLException;

/**
 * Created by henning on 16.05.16.
 */
public interface BiFunction<T, U, R> {
    R apply(T t, U u) throws SQLException;
}

