package com.techreier.edrops;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// A test of the new Record type in Java
// TODO Move code to other project
public class TestRecord {
    static final String NAME = "Hans Reier Sigmond";
    static final String ADDRESS = "Hans Reier Sigmond";
    private static final Logger logger = LoggerFactory.getLogger(TestRecord.class);
    private record Person (String name, String address) {}
    private record Point(int x, int y) {}

    private Person getPerson(String name, String address) {
       return new Person(name, address);
    }

   private int sumWithPatternMatching(Object obj)  {
        if (obj instanceof Point(int x, int y)) {
            int sum = x + y;
            logger.info ("sum: {}", x+y);
            return sum;
        } else {
            throw new RuntimeException("Is no point record");
        }
    }

    @Test
    public void testRecord() {
        Person person = new Person(NAME, ADDRESS);
        assertEquals(NAME, person.name);
        assertEquals(ADDRESS, person.address);
        Person person2 = getPerson(NAME, ADDRESS);
        Person person3 = getPerson("Petter Pettersen","Grutles veg 3");
        assertEquals(person, person2);
        assertEquals(person.hashCode(), person2.hashCode());
        assertThat(person2).isNotEqualTo(person3);
        logger.info("Person {}", person);
    }

    @Test
    public void testSumWithPatternMatching() {
        Object point = new Point(3,5);
        int sum = sumWithPatternMatching(point);
        assertEquals(8, sum);
        logger.info ("sum: {}",sum);
    }

    @Test
    public void testSumWithPatternMatchingFailure() {
        Object point = new Object();
        assertThrows(Exception.class, () ->
            sumWithPatternMatching(point) );
    }
}
