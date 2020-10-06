package nl.hva.ict.se;

import nl.hva.ict.se.sands.Archer;
import nl.hva.ict.se.sands.ChampionSelector;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Archer> archers = Archer.generateArchers(3);
        for (Archer a : archers) {
            System.out.println(a);
        }

        ChampionSelector.quickSort(archers, (o1, o2) -> {
            int result = Integer.compare(o2.getTotalScore(), o1.getTotalScore());
            if (result == 0) result = Integer.compare(o2.getTens(), o1.getTens());
            if (result == 0) result = Integer.compare(o2.getNines(), o1.getNines());
            if (result == 0) result = Integer.compare(o2.getId(), o1.getId());
            return result;
        });

        System.out.println("\n\n\n");
        for (Archer a : archers) {
            System.out.println(a);
        }
    }
}
