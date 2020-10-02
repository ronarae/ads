package nl.hva.ict.se;

import nl.hva.ict.se.sands.Archer;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Archer> archers = Archer.generateArchers(10);
        for (Archer a : archers) {
            System.out.println(a);
        }
    }
}
