package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ChampionSelectorTest {
    protected Comparator<Archer> comparator;

    @BeforeEach
    public void createComparator() {
        comparator = new Comparator<Archer>() {
            @Override
            public int compare(Archer o1, Archer o2) {
                int result = Integer.compare(o2.getTotalScore(), o1.getTotalScore());
                if (result == 0) result = Integer.compare(o2.getTens(), o1.getTens());
                if (result == 0) result = Integer.compare(o2.getNines(), o1.getNines());
                if (result == 0) result = Integer.compare(o2.getId(), o1.getId());
                return result;
            }
        };
    }

    @Test
    public void selInsSortAndCollectionSortResultInSameOrder() {
        List<Archer> unsortedArchersForSelIns = Archer.generateArchers(23);
        List<Archer> unsortedArchersForCollection = new ArrayList<>(unsortedArchersForSelIns);

        List<Archer> sortedArchersSelIns = ChampionSelector.selInsSort(unsortedArchersForSelIns, comparator);
        List<Archer> sortedArchersCollection = ChampionSelector.collectionSort(unsortedArchersForCollection, comparator);

        assertEquals(sortedArchersCollection, sortedArchersSelIns);
    }

}
