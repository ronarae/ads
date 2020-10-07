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

    /**
     * This test is designed to check if the results of the sorted lists of the
     * selection sort algorithm and collection sort algorithm aligns.
     */
    @Test
    public void selInsSortAndCollectionSortResultInSameOrder() {
        List<Archer> unsortedArchersForSelIns = Archer.generateArchers(23);
        List<Archer> unsortedArchersForCollection = new ArrayList<>(unsortedArchersForSelIns);

        List<Archer> sortedArchersSelIns = ChampionSelector.selInsSort(unsortedArchersForSelIns, comparator);
        List<Archer> sortedArchersCollection = ChampionSelector.collectionSort(unsortedArchersForCollection, comparator);

        assertEquals(sortedArchersCollection, sortedArchersSelIns);
    }

    /**
     * This test is designed to check if the results of the sorted lists of the
     * quick sort algorithm and collection sort algorithm aligns.
     */
    @Test
    public void quickSortAndCollectionSortResultInSameOrder() {
        List<Archer> unsortedArchersQS = Archer.generateArchers(25);
        List<Archer> sameUnsortedArchersCS = new ArrayList<>(unsortedArchersQS);

        List<Archer> sortedArchersQS = ChampionSelector.selInsSort(unsortedArchersQS, comparator);
        List<Archer> sortedArchersCollection = ChampionSelector.collectionSort(sameUnsortedArchersCS, comparator);

        assertEquals(sortedArchersQS, sortedArchersCollection);
    }

    /**
     * This test is designed to check if the results of the sorted lists of every algorithm aligns.
     */
    @Test
    public void allAlgorithmsResultInSameOrder(){
        List<Archer> unsortedArchersSS = Archer.generateArchers(25);
        List<Archer> sameUnsortedArchersQS = new ArrayList<>(unsortedArchersSS);
        List<Archer> sameUnsortedArchersCS = new ArrayList<>(unsortedArchersSS);

        List<Archer> sortedArchersSS = ChampionSelector.selInsSort(unsortedArchersSS, comparator);
        List<Archer> sortedArchersQS = ChampionSelector.selInsSort(sameUnsortedArchersQS, comparator);
        List<Archer> sortedArchersCollection = ChampionSelector.collectionSort(sameUnsortedArchersCS, comparator);

        assertEquals(sortedArchersSS, sortedArchersQS);
        assertEquals(sortedArchersQS, sortedArchersCollection);
        assertEquals(sortedArchersSS, sortedArchersCollection);
    }
}
