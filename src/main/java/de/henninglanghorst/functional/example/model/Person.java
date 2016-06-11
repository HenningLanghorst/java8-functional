package de.henninglanghorst.functional.example.model;

import lombok.Data;

import java.time.LocalDate;

/**
 * Test entity representing a person.
 */
@Data
public final class Person {

    private final int id;
    private final String firstName;
    private final String lastName;
    private final LocalDate birthday;

    public Person(final int id, final String firstName, final String lastName, final LocalDate birthday) {
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
        this.birthday = birthday;
    }

}
