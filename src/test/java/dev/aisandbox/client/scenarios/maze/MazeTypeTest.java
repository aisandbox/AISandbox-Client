package dev.aisandbox.client.scenarios.maze;

import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class MazeTypeTest {

    @Test
    public void testUniqueName() {
        Set<String> names = new HashSet<>();
        for (MazeType t : MazeType.values()) {
            assertNotNull("null toString for " + t.name(), t.toString());
            assertFalse("Maze type " + t.name() + " has duplicate toString", names.contains(t.toString()));
            names.add(t.toString());
        }
    }

}