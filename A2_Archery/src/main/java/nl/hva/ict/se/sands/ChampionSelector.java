package nl.hva.ict.se.sands;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * Given a list of Archer's this class can be used to sort the list using one of three sorting algorithms.
 * Note that you are NOT allowed to change the signature of these methods! Adding method is perfectly fine.
 */
public class ChampionSelector {

    public static boolean less(Archer v, Archer w) {
        int result = Integer.compare(w.getTotalScore(), v.getTotalScore());
        if (result == 0) result = Integer.compare(w.getTens(), v.getTens());
        if (result == 0) result = Integer.compare(w.getNines(), v.getNines());
        if (result == 0) result = Integer.compare(w.getId(), v.getId());
        return result < 0;
    }

    private static void exch(List<Archer> a, int i, int j) {
        Archer t = a.get(i);
        a.set(i, a.get(j));
        a.set(j, t);
    }

    /**
     * This method uses either selection sort or insertion sort for sorting the archers.
     */
    public static List<Archer> selInsSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        final int N = archers.size();
        for (int i = 0; i < N; i++) {
            int min = i;
            for (int j = i+1; j < N; j++) if (less(archers.get(j), archers.get(min))) min = j;
            exch(archers, i, min);
        }
        return archers;
    }

    /**
     * This method uses quick sort for sorting the archers.
     */
    public static List<Archer> quickSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        return archers;
    }

    /**
     * This method uses the Java collections sort algorithm for sorting the archers.
     */
    public static List<Archer> collectionSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        return archers;
    }
}
