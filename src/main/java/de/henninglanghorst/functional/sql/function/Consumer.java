package de.henninglanghorst.functional.sql.function;

import java.sql.SQLException;

/**
 * Interface for method accepting one Object of type {@link T} which can throw an {@link SQLException}.
 *
 * Created by henning on 15.05.16.
 */
public interface Consumer<T> {
    void accept(T t) throws SQLException;
}
