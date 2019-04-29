package ru.hse.aabukov.db;

import com.mongodb.MongoClient;
import org.jetbrains.annotations.NotNull;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;
import xyz.morphia.query.Query;

import java.util.List;

/** This class interacts with database to store pairs of names and numbers and to perform some operations with them */
class PhoneDataBase {
    private final @NotNull Datastore datastore;
    private static final @NotNull String NAME_PROPERTY = "name";
    private static final @NotNull String NUMBER_PROPERTY = "number";

    /**
     * Creates a database with given name to interact with it
     *
     * @param dataBaseName Name of database file
     */
    PhoneDataBase(@NotNull String dataBaseName) {
        Morphia morphia = new Morphia();
        morphia.mapPackage("ru.hse.aabukov.db");
        datastore = morphia.createDatastore(new MongoClient(), dataBaseName);
    }

    @NotNull
    private Query<PersonData> byNameAndNumberQuery(@NotNull String name, @NotNull String number) {
        return datastore.createQuery(PersonData.class)
                .field(NAME_PROPERTY).equal(name)
                .field(NUMBER_PROPERTY).equal(number);
    }

    /** Adds a record with given name and number into the database */
    void addRecord(@NotNull String name, @NotNull String number) {
        datastore.save(new PersonData(name, number));
    }

    /** Finds all entries with given name */
    @NotNull List<PersonData> findByName(@NotNull String name) {
        final var query = datastore.createQuery(PersonData.class)
                .field(NAME_PROPERTY).equal(name);
        return query.asList();
    }

    /** Finds all entries with given number */
    @NotNull List<PersonData> findByNumber(@NotNull String number) {
        final var query = datastore.createQuery(PersonData.class)
                .field(NUMBER_PROPERTY).equal(number);
        return query.asList();
    }

    /** Deletes all records with given name and number */
    void deleteRecord(@NotNull String name, @NotNull String number) {
        datastore.delete(byNameAndNumberQuery(name, number));
    }

    /** Changes name in all entries with given name and number */
    void changeName(@NotNull String name, @NotNull String number, @NotNull String newName) {
        var operations = datastore.createUpdateOperations(PersonData.class)
                .set(NAME_PROPERTY, newName);
        datastore.update(byNameAndNumberQuery(name, number), operations);
    }

    /** Changes number in all entries with given name and number */
    void changePhone(@NotNull String name, @NotNull String number, @NotNull String newNumber) {
        var operations = datastore.createUpdateOperations(PersonData.class)
                .set(NUMBER_PROPERTY, newNumber);
        datastore.update(byNameAndNumberQuery(name, number), operations);
    }

    /** Returns all entries in database */
    @NotNull List<PersonData> allRecords() {
        return datastore.createQuery(PersonData.class).asList();
    }

    /** Deletes the database held by the object of this class */
    void drop() {
        datastore.getDB().dropDatabase();
    }
}
