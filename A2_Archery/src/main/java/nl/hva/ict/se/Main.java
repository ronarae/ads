package nl.hva.ict.se;

import nl.hva.ict.se.sands.Archer;
import nl.hva.ict.se.sands.ArcherComparator;
import nl.hva.ict.se.sands.ChampionSelector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Archer> archers = Archer.generateArchers(100);
        List<Archer> copyListSelection = new ArrayList<>();
        List<Archer> copyListQuicksort = new ArrayList<>();
        List<Archer> copyListCollections = new ArrayList<>();
        boolean SSBool = true;
        boolean QSBool = true;
        boolean CSBool = true;


        //keeps looping until amount of archers is over 5 million.
        while (archers.size() < 5000000) {
            //generate two times the current amount of archers
            archers = Archer.generateArchers(archers.size() * 2);

            //clear copied archers list firstly to remove old list and add newly generated archers afterwards
            copyListSelection.clear();
            copyListSelection.addAll(archers);
            copyListQuicksort.clear();
            copyListQuicksort.addAll(archers);
            copyListCollections.clear();
            copyListCollections.addAll(archers);

            //sorting algorithm selection sort keeps looping until the sorting time takes longer than 20 seconds
            // it does this by setting a boolean on false which stops the loop from occurring
            if (SSBool) {
                long startTimeSS = System.currentTimeMillis();
                ChampionSelector.selInsSort(copyListSelection, new ArcherComparator());
                long endTimeSS = System.currentTimeMillis();
                long totalTimeSS = endTimeSS - startTimeSS;
                System.out.println("\nSelection sort:");
                System.out.println("Time it took:");
                System.out.println(totalTimeSS);
                System.out.println("Amount of archers:");
                System.out.println(archers.size());
                //sets boolean on false when the total time it took exceeds 20 seconds.
                if (totalTimeSS >= 20000) {
                    System.out.println("Selection break");
                    SSBool = false;
                }
            }

            //sorting algorithm quicksort keeps looping until the sorting time takes longer than 20 seconds
            // it does this by setting a boolean on false which stops the loop from occurring
            if (QSBool) {
                long startTimeQS = System.currentTimeMillis();
                ChampionSelector.quickSort(copyListQuicksort, new ArcherComparator());
                long endTimeQS = System.currentTimeMillis();
                long totalTimeQS = endTimeQS - startTimeQS;

                System.out.println("\nQuicksort:");
                System.out.println("Time it took:");
                System.out.println(totalTimeQS);
                System.out.println("Amount of archers:");
                System.out.println(copyListQuicksort.size());
                //sets boolean on false when the total time it took exceeds 20 seconds.
                if (totalTimeQS >= 20000) {
                    System.out.println("quicksort break");
                    QSBool = false;
                }
            }

            //sorting algorithm collections keeps looping until the sorting time takes longer than 20 seconds
            // it does this by setting a boolean on false which stops the loop from occurring
            if (CSBool) {
                long startTimeCS = System.currentTimeMillis();
                ChampionSelector.collectionSort(copyListCollections, new ArcherComparator());
                long endTimeCS = System.currentTimeMillis();
                long totalTimeCS = endTimeCS - startTimeCS;

                System.out.println("\nCollections sort:");
                System.out.println("Time it took:");
                System.out.println(totalTimeCS);
                System.out.println("Amount of archers:");
                System.out.println(copyListCollections.size());
                //sets boolean on false when the total time it took exceeds 20 seconds.
                if (totalTimeCS >= 20000) {
                    System.out.println("collections break");
                    CSBool = false;
                }
            }
        }
        System.out.println("loop finished");
    }

}
