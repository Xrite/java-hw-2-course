package ru.hse.aabukov.db;

import org.jetbrains.annotations.NotNull;
import xyz.morphia.annotations.Entity;
import xyz.morphia.annotations.Id;
import xyz.morphia.annotations.Property;

import java.util.Objects;

/** This class stores the pair contains person name and number */
@Entity
class PersonData {
    private static @NotNull Long nextId = 0L;
    @Id
    private @NotNull Long id;
    @Property("name")
    private @NotNull String name;
    @Property("number")
    private @NotNull String number;

    /** Creates empty entity (required by morphia, do not use) */
    public PersonData() {
        id = nextId++;
    }

    /** Creates entity with given name and number */
    PersonData(@NotNull String name, @NotNull String number) {
        this.name = name;
        this.number = number;
        id = nextId++;
    }

    /** Compares two PersonData. Two objects are equal iff their names and numbers are equal. */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonData that = (PersonData) o;
        return name.equals(that.name) &&
                number.equals(that.number);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, number);
    }

    /** Returns the name of the entity */
    @NotNull String getName() {
        return name;
    }

    /** Returns the number of the entity */
    @NotNull String getNumber() {
        return number;
    }
}
