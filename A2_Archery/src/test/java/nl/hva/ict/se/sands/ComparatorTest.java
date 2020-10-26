package nl.hva.ict.se.sands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class ComparatorTest {
    protected ArcherComparator comparator;

    @BeforeEach
    public void createComparator() {
        comparator = new ArcherComparator();
    }

    @Test
    public void higherScoreBeforeLowerScore() {
        List<Archer> a = Archer.generateArchers(23);
        List<Integer> b = new ArrayList<>();
        List<Integer> c = new ArrayList<>();

        for (Archer ar : a) {
            b.add(ar.getTotalScore());
        }
        Collections.sort(b);
        Collections.reverse(b);

        ChampionSelector.collectionSort(a, comparator);

        for (Archer ar : a) {
            c.add(ar.getTotalScore());
        }

        assertEquals(b, c);
    }

    @Test
    public void sameScoreMoreTensWin() {
        List<Archer> a = Archer.generateArchers(3);
        List<Archer> b = new ArrayList<>(a);

        int[] all10Round = new int[10];
        Arrays.fill(all10Round, 10);

        int[] firstLastRound = new int[10];
        int[] secondLastRound = new int[10];
        int[] thirdLastRound = new int[10];

        Arrays.fill(firstLastRound, 10);
        Arrays.fill(secondLastRound, 10);
        Arrays.fill(thirdLastRound, 10);

        firstLastRound[7] = 9;
        firstLastRound[8] = 9;
        firstLastRound[9] = 9;

        secondLastRound[9] = 7;

        thirdLastRound[8] = 9;
        thirdLastRound[9] = 8;

        a.get(0).registerScoreForRound(0, all10Round);
        a.get(1).registerScoreForRound(0, all10Round);
        a.get(2).registerScoreForRound(0, all10Round);

        a.get(0).registerScoreForRound(1, all10Round);
        a.get(1).registerScoreForRound(1, all10Round);
        a.get(2).registerScoreForRound(1, all10Round);

        a.get(0).registerScoreForRound(2, firstLastRound);
        a.get(1).registerScoreForRound(2, secondLastRound);
        a.get(2).registerScoreForRound(2, thirdLastRound);

        ChampionSelector.collectionSort(a, comparator);

        assertEquals(a.get(0), b.get(1));
        assertEquals(a.get(1), b.get(2));
        assertEquals(a.get(2), b.get(0));
    }

    @Test
    public void sameScoreMoreNinesWin() {
        List<Archer> a = Archer.generateArchers(3);
        List<Archer> b = new ArrayList<>(a);

        int[] all10Round = new int[10];
        Arrays.fill(all10Round, 9);

        int[] firstLastRound = new int[10];
        int[] secondLastRound = new int[10];
        int[] thirdLastRound = new int[10];

        Arrays.fill(firstLastRound, 9);
        Arrays.fill(secondLastRound, 9);
        Arrays.fill(thirdLastRound, 9);

        firstLastRound[7] = 8;
        firstLastRound[8] = 8;
        firstLastRound[9] = 8;

        secondLastRound[9] = 6;

        thirdLastRound[8] = 8;
        thirdLastRound[9] = 7;

        a.get(0).registerScoreForRound(0, all10Round);
        a.get(1).registerScoreForRound(0, all10Round);
        a.get(2).registerScoreForRound(0, all10Round);

        a.get(0).registerScoreForRound(1, all10Round);
        a.get(1).registerScoreForRound(1, all10Round);
        a.get(2).registerScoreForRound(1, all10Round);

        a.get(0).registerScoreForRound(2, firstLastRound);
        a.get(1).registerScoreForRound(2, secondLastRound);
        a.get(2).registerScoreForRound(2, thirdLastRound);

        ChampionSelector.collectionSort(a, comparator);

        assertEquals(a.get(0), b.get(1));
        assertEquals(a.get(1), b.get(2));
        assertEquals(a.get(2), b.get(0));
    }

    @Test
    public void mostExperiencedArcherWillWin() {
        List<Archer> a = Archer.generateArchers(20);
        List<Archer> b = new ArrayList<>(a);

        int[] allTheSameScores = new int[10];
        Arrays.fill(allTheSameScores, 10);

        for (Archer ar : a) {
            ar.registerScoreForRound(0, allTheSameScores);
            ar.registerScoreForRound(1, allTheSameScores);
            ar.registerScoreForRound(2, allTheSameScores);
        }

        ChampionSelector.collectionSort(a, comparator);

        assertEquals(a, b);
    }
}
