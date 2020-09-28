package nl.hva.ict.se.sands;

import org.junit.jupiter.api.Test;

import java.lang.reflect.Constructor;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class ArcherTest {

    @Test
    void archerIdsIncreaseCorrectly() {
        List<Archer> archers = Archer.generateArchers(3);
        assertTrue(archers.get(1).getId() == archers.get(0).getId()+ 1);
        assertTrue(archers.get(2).getId() == archers.get(1).getId()+ 1);
    }

    @Test
    void visibilityOfConstructorsShouldBeUnchanged() {
        for (Constructor constructor : Archer.class.getDeclaredConstructors()) {
            assertTrue((constructor.getModifiers() & 0x00000004) != 0);
        }
    }

    @Test
    void idFieldShouldBeUnchangeable() throws NoSuchFieldException {
        assertTrue((Archer.class.getDeclaredField("id").getModifiers() & 0x00000010) != 0);
    }

}
