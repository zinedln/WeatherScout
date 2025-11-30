package com.example.weatherscout;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LauncherTest {

    @Test
    public void simpleTest() {
        int result = 1 + 1;
        assertEquals(2, result, "Kurzer Test");
    }

    @Test
    public void testWeatherFormat() {
        String city = "Vienna";
        String labelText = "City: " + city;

        assertEquals("City: Vienna", labelText);
    }
}
