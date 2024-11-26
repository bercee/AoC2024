package com.chemaxon;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


public class MyClassTest {
    @Test
    public void test() {
        var myClass = new MyClass(5);
        assertEquals(5, myClass.getId());
    }
}
