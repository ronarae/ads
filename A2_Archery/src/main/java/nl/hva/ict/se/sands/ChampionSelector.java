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
        sortOfQuickSort(archers, 0, archers.size() - 1);
        return archers;
    }

    /**
     * This method was made to partition the given list.
     * @param partComp - partitioning item
     * @param lo - lowest point
     * @param hi - highest point
     * @return j
     */
    private static int partition(List<Archer> partComp, int lo, int hi)
    { // Partition into a[lo..i-1], a[i], a[i+1..hi].
        int i = lo, j = hi+1; // left and right scan indices
        Archer partItem = partComp.get(lo); // partitioning item
        while (true)
        { // Scan right, scan left, check for scan complete, and exchange.
            while (less(partComp.get(++i), partItem)) if (i == hi) break;
            while (less(partItem,  partComp.get(--j))) if (j == lo) break;
            if (i >= j) break;
            exch(partComp, i, j);
        }
        exch(partComp, lo, j); // Put v = a[j] into position
        return j; // with a[lo..j-1] <= a[j] <= a[j+1..hi].
    }

    /**
     *  recursive sort of the list using the partition method
     * @param a - List of archers
     * @param lo - lowest point
     * @param hi - highest point
     */
    private static void sortOfQuickSort(List<Archer> a, int lo, int hi)
    {
        if (hi <= lo) return;
        int j = partition(a, lo, hi); // Partition.
        sortOfQuickSort(a, lo, j-1); // Sort left part a[lo .. j-1].
        sortOfQuickSort(a, j+1, hi); // Sort right part a[j+1 .. hi].
    }

    /**
     * This method uses the Java collections sort algorithm for sorting the archers.
     */
    public static List<Archer> collectionSort(List<Archer> archers, Comparator<Archer> scoringScheme) {
        archers.sort(scoringScheme);
        return archers;
    }

}
