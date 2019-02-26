package ru.hse.aabukov.db;

import org.junit.jupiter.api.*;
import xyz.morphia.Datastore;
import xyz.morphia.Morphia;

import java.awt.*;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.TreeSet;

import static org.junit.jupiter.api.Assertions.*;

class PhoneDataBaseTest {
    final String[] sampleNames   = {"a", "a", "b", "b", "e", "f", "g", "a"};
    final String[] sampleNumbers = {"a", "b", "c", "a", "f", "f", "g", "c"};
    final PersonData[] sampleData;

    PhoneDataBase dataBase;

    PhoneDataBaseTest() {
        sampleData = new PersonData[sampleNames.length];
        Arrays.setAll(sampleData, i -> new PersonData(sampleNames[i], sampleNumbers[i]));
    }

    @BeforeEach
    void initTestDatabase() {
        assertEquals(sampleNames.length, sampleNumbers.length);
        dataBase = new PhoneDataBase("test");
    }

    @AfterEach
    void dropTestDatabase() {
        dataBase.drop();
    }

    private void fillDataBase() {
        for(int i = 0; i < sampleNames.length; i++) {
            dataBase.addRecord(sampleNames[i], sampleNumbers[i]);
        }
    }

    @Test
    void addRecord() {
        for(int i = 0; i < sampleNames.length; i++) {
            dataBase.addRecord(sampleNames[i], sampleNumbers[i]);
            assertEquals(Set.copyOf(Arrays.asList(sampleData).subList(0, i + 1)), Set.copyOf(dataBase.allRecords()));
        }
    }

    @Test
    void findByName() {
        fillDataBase();
        assertEquals(Set.of(sampleData[0], sampleData[1], sampleData[7]), Set.copyOf(dataBase.findByName("a")));
        assertEquals(Set.of(sampleData[2], sampleData[3]), Set.copyOf(dataBase.findByName("b")));
        assertEquals(Set.of(sampleData[4]), Set.copyOf(dataBase.findByName("e")));
        assertEquals(Set.of(sampleData[5]), Set.copyOf(dataBase.findByName("f")));
        assertEquals(Set.of(sampleData[6]), Set.copyOf(dataBase.findByName("g")));
        assertEquals(Set.of(), Set.copyOf(dataBase.findByName("c")));
    }

    @Test
    void findByNumber() {
        fillDataBase();
        assertEquals(Set.of(sampleData[0], sampleData[3]), Set.copyOf(dataBase.findByNumber("a")));
        assertEquals(Set.of(sampleData[1]), Set.copyOf(dataBase.findByNumber("b")));
        assertEquals(Set.of(sampleData[2], sampleData[7]), Set.copyOf(dataBase.findByNumber("c")));
        assertEquals(Set.of(sampleData[4], sampleData[5]), Set.copyOf(dataBase.findByNumber("f")));
        assertEquals(Set.of(sampleData[6]), Set.copyOf(dataBase.findByNumber("g")));
        assertEquals(Set.of(), Set.copyOf(dataBase.findByNumber("d")));
    }

    @Test
    void deleteRecord() {
        fillDataBase();
        for(int i = 0; i < sampleNames.length; i++) {
            dataBase.deleteRecord(sampleNames[i], sampleNumbers[i]);
            assertEquals(Set.copyOf(Arrays.asList(sampleData).subList(i + 1, sampleData.length)), Set.copyOf(dataBase.allRecords()));
        }
    }

    @Test
    void changeName() {
        dataBase.addRecord("a", "a");
        dataBase.addRecord("b", "a");
        dataBase.changeName("a", "a", "a");
        assertEquals(Set.of(new PersonData("a", "a"), new PersonData("b", "a")), Set.copyOf(dataBase.allRecords()));
        dataBase.changeName("a", "a", "b");
        assertArrayEquals(new PersonData[] {new PersonData("b", "a"), new PersonData("b", "a")}, dataBase.allRecords().toArray());
        dataBase.changeName("a", "a", "a");
        assertArrayEquals(new PersonData[] {new PersonData("b", "a"), new PersonData("b", "a")}, dataBase.allRecords().toArray());
    }

    @Test
    void changePhone() {
        dataBase.addRecord("a", "a");
        dataBase.addRecord("a", "b");
        dataBase.changePhone("a", "a", "a");
        assertEquals(Set.of(new PersonData("a", "a"), new PersonData("a", "b")), Set.copyOf(dataBase.allRecords()));
        dataBase.changePhone("a", "b", "a");
        assertArrayEquals(new PersonData[] {new PersonData("a", "a"), new PersonData("a", "a")}, dataBase.allRecords().toArray());
        dataBase.changeName("a", "b", "a");
        assertArrayEquals(new PersonData[] {new PersonData("a", "a"), new PersonData("a", "a")}, dataBase.allRecords().toArray());
    }

    @Test
    void allRecords() {
        assertArrayEquals(new PersonData[] {}, dataBase.allRecords().toArray());
        fillDataBase();
        assertEquals(Set.copyOf(Arrays.asList(sampleData)), Set.copyOf(dataBase.allRecords()));
    }

    @Test
    void drop() {
        fillDataBase();
        dataBase.drop();
        assertEquals(Set.of(), Set.copyOf(dataBase.allRecords()));
    }

}