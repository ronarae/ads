package models;

import java.util.Iterator;
import java.util.NoSuchElementException;

public class Train implements Iterable<Wagon> {
    private String origin;
    private String destination;
    private Locomotive engine;
    private Wagon firstWagon;

    /* Representation invariants:
        firstWagon == null || firstWagon.previousWagon == null
        engine != null
     */

    public Train(Locomotive engine, String origin, String destination) {
        this.engine = engine;
        this.destination = destination;
        this.origin = origin;
    }

    /* three helper methods that are usefull in other methods */
    public boolean hasWagons() {
        return firstWagon != null;
    }

    public boolean isPassengerTrain() {
        System.out.println(firstWagon instanceof PassengerWagon);
        return firstWagon instanceof PassengerWagon;
    }

    public boolean isFreightTrain() {
        return firstWagon instanceof FreightWagon;
    }

    public Locomotive getEngine() {
        return engine;
    }

    public Wagon getFirstWagon() {
        return firstWagon;
    }

    /**
     * Replaces the current sequence of wagons (if any) in the train
     * by the given new sequence of wagons (if any)
     * (sustaining all representation invariants)
     * @param newSequence   the new sequence of wagons (can be null)
     */
    public void setFirstWagon(Wagon newSequence) {
        firstWagon = newSequence;
    }

    /**
     * @return  the number of Wagons connected to the train
     */
    public int getNumberOfWagons() {
        int counter = 0;
        Wagon curWagon = firstWagon;
        while (curWagon.hasNextWagon()) {
            counter++;
            curWagon = curWagon.getNextWagon();
        }
        return counter;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        Wagon curWagon = firstWagon;
        while(curWagon.hasNextWagon()) {
            curWagon = curWagon.getNextWagon();
        }
        return curWagon;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        if (isFreightTrain()) {
            return 0;
        } else if (isPassengerTrain()){
            int seatCounter = 0;
            Wagon curWagon = firstWagon;
            while(curWagon.hasNextWagon()) {
                seatCounter += ((PassengerWagon)curWagon).getNumberOfSeats();
                curWagon = curWagon.getNextWagon();
            }
            return seatCounter;
        } else {
            return 0;
        }
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        if (isPassengerTrain()) {
            return 0;
        } else if (isFreightTrain()) {
            int maxWeightCounter = 0;
            Wagon curWagon = firstWagon;
            while(curWagon.hasNextWagon()) {
                maxWeightCounter += ((FreightWagon)curWagon).getMaxWeight();
                curWagon = curWagon.getNextWagon();
            }
            return maxWeightCounter;
        } else {
            return 0;
        }
    }

     /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        Wagon curWagon = firstWagon;
        while(position > 0) {
            if (!curWagon.hasNextWagon()) return null;
            curWagon = curWagon.getNextWagon();
            position--;
        }
        return curWagon;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        Wagon curWagon = firstWagon;
        while(curWagon.hasNextWagon()) {
            if (curWagon.getId() == wagonId) return curWagon;
        }
        return null;
    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verfies of the type of wagons match the type of train (Passenger or Freight)
     * Verfies that the capacity of the engine is sufficient to pull the additional wagons
     * @param sequence
     * @return
     */
    public boolean canAttach(Wagon sequence) {
        //TODO
//        int maxWagons = engine.getMaxWagons();
//        int totalWagons = getNumberOfWagons();
//        if (totalWagons > maxWagons) return false;
//        boolean allOfTheSameType = true;
//
//        while(sequence.hasNextWagon()) {
//
//        }
//        if (!allOfTheSameType) return false;
        return true;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param sequence
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon sequence) {
        try {
            if (firstWagon != null) {
                sequence.attachTo(firstWagon.getLastWagonAttached());
            } else {
                firstWagon = sequence;
            }
            return true;
        } catch(Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param sequence
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon sequence) {
        try {
            Wagon oldFirstWagon = firstWagon;
            firstWagon = sequence;
            firstWagon.setNextWagon(oldFirstWagon);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * Tries to insert the given sequence of wagons at the given wagon position in the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible of the engine has insufficient capacity
     * or the given position is not valid in this train)
     * @param sequence
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtPosition(int position, Wagon sequence) {
        try {
            Wagon wagonAtPosition = findWagonAtPosition(position);
            Wagon prevWagon = wagonAtPosition.getPreviousWagon();
            Wagon nextWagon = wagonAtPosition.getNextWagon();
            prevWagon.setNextWagon(sequence);
            Wagon lastWagonOfSequence = sequence.getLastWagonAttached();
            nextWagon.setPreviousWagon(wagonAtPosition);
            wagonAtPosition.setPreviousWagon(lastWagonOfSequence);
            lastWagonOfSequence.setNextWagon(wagonAtPosition);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Tries to remove one Wagon with the given wagonId from this train
     * and attach it at the rear of the given toTrain
     * No change is made if the removal or attachment cannot be made
     * (when the wagon cannot be found, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param wagonId
     * @param toTrain
     * @return  whether the move could be completed successfully
     */
    public boolean moveOneWagon(int wagonId, Train toTrain) {
        // TODO

        return false;
     }

    /**
     * Tries to split this train and move the complete sequence of wagons from the given position
     * to the rear of toTrain
     * No change is made if the split or re-attachment cannot be made
     * (when the position is not valid for this train, or the trains are not compatible
     * or the engine of toTrain has insufficient capacity)
     * @param position
     * @param toTrain
     * @return  whether the move could be completed successfully
     */
    public boolean splitAtPosition(int position, Train toTrain) {
        // TODO

        return false;
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        // TODO

    }

    @Override
    public Iterator<Wagon> iterator() {
        return new TrainIterator();
    }

    class TrainIterator implements Iterator<Wagon> {
        Wagon index;

        public TrainIterator() {
            index = firstWagon;
        }

        @Override
        public boolean hasNext() {
            return index != null;
        }

        @Override
        public Wagon next() {
            if (!hasNext()) throw new NoSuchElementException();
            return index.getNextWagon();
        }
    }

    // TODO
}
