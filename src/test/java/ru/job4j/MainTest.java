package ru.job4j;

import org.junit.Test;
import ru.job4j.grabber.Main;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class MainTest {

    @Test
    public void whenNothingThenResult() {
        assertThat(1, is(Main.one()));
    }
}
