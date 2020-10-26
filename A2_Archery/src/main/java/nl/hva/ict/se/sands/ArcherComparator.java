package nl.hva.ict.se.sands;

import java.util.Comparator;

public class ArcherComparator implements Comparator<Archer> {
    @Override
    public int compare(Archer o1, Archer o2) {
        int result = Integer.compare(o2.getTotalScore(), o1.getTotalScore());
        if (result == 0) result = Integer.compare(o2.getTens(), o1.getTens());
        if (result == 0) result = Integer.compare(o2.getNines(), o1.getNines());
        if (result == 0) result = Integer.compare(o1.getId(), o2.getId());
        return result;
    }
}
