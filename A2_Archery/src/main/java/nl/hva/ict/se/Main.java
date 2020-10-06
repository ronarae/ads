package nl.hva.ict.se;

import nl.hva.ict.se.sands.Archer;
import nl.hva.ict.se.sands.ChampionSelector;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Archer> archers = Archer.generateArchers(100);
        List<Archer> copyListQuicksort = new ArrayList<>();
        List<Archer> copyListCollections = new ArrayList<>();
        boolean SSBool = true;
        boolean QSBool = true;
        boolean CSBool = true;


        //keeps looping until amount of archers is over 5 million.
        while (archers.size() < 5000000) {
            //generate two times the current amount of archers
            archers = Archer.generateArchers(archers.size() * 2);
            copyListQuicksort.clear();
            copyListQuicksort.addAll(archers);
            copyListCollections.clear();
            copyListCollections.addAll(archers);

            //sorting algorithm selection sort
            if (SSBool) {
                long startTimeSS = System.currentTimeMillis();
                ChampionSelector.selInsSort(archers, (o1, o2) -> {
                    int result = Integer.compare(o2.getTotalScore(), o1.getTotalScore());
                    if (result == 0) result = Integer.compare(o2.getTens(), o1.getTens());
                    if (result == 0) result = Integer.compare(o2.getNines(), o1.getNines());
                    if (result == 0) result = Integer.compare(o2.getId(), o1.getId());
                    return result;
                });
                long endTimeSS = System.currentTimeMillis();
                long totalTimeSS = endTimeSS - startTimeSS;
                System.out.println("\nSelection sort:");
                System.out.println("Time it took:");
                System.out.println(totalTimeSS);
                System.out.println("Amount of archers:");
                System.out.println(archers.size());
                if (totalTimeSS >= 20000) {
                    System.out.println("Selection break");
                    SSBool = false;
                }
            }

            //sorting algorithm quicksort
            if (QSBool) {
                long startTimeQS = System.currentTimeMillis();
                ChampionSelector.quickSort(copyListQuicksort, (o1, o2) -> {
                    int result = Integer.compare(o2.getTotalScore(), o1.getTotalScore());
                    if (result == 0) result = Integer.compare(o2.getTens(), o1.getTens());
                    if (result == 0) result = Integer.compare(o2.getNines(), o1.getNines());
                    if (result == 0) result = Integer.compare(o2.getId(), o1.getId());
                    return result;
                });
                long endTimeQS = System.currentTimeMillis();
                long totalTimeQS = endTimeQS - startTimeQS;

                System.out.println("\nQuicksort:");
                System.out.println("Time it took:");
                System.out.println(totalTimeQS);
                System.out.println("Amount of archers:");
                System.out.println(copyListQuicksort.size());
                if (totalTimeQS >= 20000) {
                    System.out.println("quicksort break");
                    QSBool = false;
                }
            }

            //sorting algorithm collections
            if (CSBool) {
                long startTimeCS = System.currentTimeMillis();
                ChampionSelector.collectionSort(copyListCollections, (o1, o2) -> {
                    int result = Integer.compare(o2.getTotalScore(), o1.getTotalScore());
                    if (result == 0) result = Integer.compare(o2.getTens(), o1.getTens());
                    if (result == 0) result = Integer.compare(o2.getNines(), o1.getNines());
                    if (result == 0) result = Integer.compare(o2.getId(), o1.getId());
                    return result;
                });
                long endTimeCS = System.currentTimeMillis();
                long totalTimeCS = endTimeCS - startTimeCS;

                System.out.println("\nCollections sort:");
                System.out.println("Time it took:");
                System.out.println(totalTimeCS);
                System.out.println("Amount of archers:");
                System.out.println(copyListCollections.size());
                if (totalTimeCS >= 20000) {
                    System.out.println("collections break");
                    CSBool = false;
                }
            }
        }
        System.out.println("loop finished");
    }

}
