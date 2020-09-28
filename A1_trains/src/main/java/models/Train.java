package models;

import java.util.Iterator;

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
        //if the firstwagon is not equals to null return the sequence length of the firstwagon else return 0
        return (firstWagon != null) ? firstWagon.getSequenceLength() : 0;
    }

    /**
     * @return  the last wagon attached to the train
     */
    public Wagon getLastWagonAttached() {
        //if the firstwagon is not equals to null return the last wagon that is attached to the firstwagon else return null
        return (firstWagon != null) ? firstWagon.getLastWagonAttached() : null;
    }

    /**
     * @return  the total number of seats on a passenger train
     *          (return 0 for a freight train)
     */
    public int getTotalNumberOfSeats() {
        //when instance of freighttrain return 0;
        if (isFreightTrain()) return 0;
        //instantiate counter and set value to 0
        int counter = 0;
        //loop through all the wagons in this instance with an enhanced for loop (for-each loop)
        for (Wagon w : this) {
            //add the number of seats of each wagon to the counter
            counter += ((PassengerWagon)w).getNumberOfSeats();
        }
        //return the end value of the counter
        return counter;
    }

    /**
     * calculates the total maximum weight of a freight train
     * @return  the total maximum weight of a freight train
     *          (return 0 for a passenger train)
     *
     */
    public int getTotalMaxWeight() {
        //when instanceof passengertrain return 0
        if (isPassengerTrain()) return 0;
        //instantiate counter and set value to 0
        int counter = 0;
        //loop through all the wagons in this instance with an enhanced for loop (for-each loop)
        for (Wagon w : this) {
            //add the number of maxweight of each wagon to the counter
            counter += ((FreightWagon)w).getMaxWeight();
        }
        //return the end value of the counter
        return counter;
    }

    /**
     * Finds the wagon at the given position (starting at 1 for the first wagon of the train)
     * @param position
     * @return  the wagon found at the given position
     *          (return null if the position is not valid for this train)
     */
    public Wagon findWagonAtPosition(int position) {
        //check if position 0, when the case return null because of an invalid position
        if (position == 0) return null;
        //set a pointer to the current wagon equals to the firstwagon
        Wagon cur = firstWagon;
        //loop while position is greater than 1
        while (position > 1) {
            //return null when the current wagon doesn't have any following wagons
            if (!cur.hasNextWagon()) return null;
            //set the current wagon equals to the next wagon
            cur = cur.getNextWagon();
            //decrement the position by 1
            position--;
        }
        //return the last selected wagon
        return cur;
    }

    /**
     * Finds the wagon with a given wagonId
     * @param wagonId
     * @return  the wagon found
     *          (return null if no wagon was found with the given wagonId)
     */
    public Wagon findWagonById(int wagonId) {
        //return null when there isn't a wagon on this train
        if (firstWagon == null) return null;
        //set a pointer for the current wagon to the firstwagon
        Wagon cur = firstWagon;
        //keep looping while the id the of current wagon is not equals to the given wagonid
        while (cur.getId() != wagonId) {
            //return null when there isn't another wagon to the list (this means that no wagon was found)
            if (!cur.hasNextWagon()) return null;
            //set the pointer of the current wagon to the next wagon
            cur = cur.getNextWagon();
        }
        //return the last selected wagon
        return cur;
    }

    /**
     * Determines if the given sequence of wagons can be attached to the train
     * Verfies of the type of wagons match the type of train (Passenger or Freight)
     * Verfies that the capacity of the engine is sufficient to pull the additional wagons
     * @param sequence
     * @return
     */
    public boolean canAttach(Wagon sequence) {
        //give the boolean canattach a default value of true;
        boolean canAttach = true;
        //check if there is a first wagon, if there is a firstwagon immediately return true;
        if (firstWagon != null) {
            //check if the sequence lenght of the current sequence together with the length of the given sequence is not to much for the engine to handle
            if ((firstWagon.getSequenceLength() + sequence.getSequenceLength()) > engine.getMaxWagons())
                canAttach = false;
            //check the id of the firstwagon is not the same of the id of the given sequence
            else if (firstWagon.getId() == sequence.getId()) canAttach = false;
            //check if the current train is a passengertrain and the sequence is of type freighttrain
            else if (isPassengerTrain() && sequence instanceof FreightWagon) canAttach = false;
            //check if the current train is a freighttrain and the sequence is of type passengertrain
            else if (isFreightTrain() && sequence instanceof PassengerWagon) canAttach = false;
        }
        //return if the sequence can attach to the current sequence
        return canAttach;
    }

    /**
     * Tries to attach the given sequence of wagons to the rear of the train
     * No change is made if the attachment cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param sequence
     * @return  whether the attachment could be completed successfully
     */
    public boolean attachToRear(Wagon sequence) {
        //check if the given sequence can attach to the current sequence
        if (!canAttach(sequence)) return false;
        try {
            //when there isn't a sequence yet set the sequence to the given sequence else attach the sequence to the current sequence
            if (firstWagon != null) {
                sequence.attachTo(firstWagon.getLastWagonAttached());
            } else {
                setFirstWagon(sequence);
            }
            return true;
            //catch a runtime exception when necessary
        } catch (RuntimeException e) {
            return false;
        }
    }

    /**
     * Tries to insert the given sequence of wagons at the front of the train
     * No change is made if the insertion cannot be made.
     * (when the sequence is not compatible or the engine has insufficient capacity)
     * @param sequence
     * @return  whether the insertion could be completed successfully
     */
    public boolean insertAtFront(Wagon sequence) {
        //check if the given sequence can attach to the current sequence
        if (!canAttach(sequence)) return false;
        try {
            //when the firstwagon is not null attach the current sequence to the given sequence
            if (firstWagon != null) firstWagon.attachTo(sequence.getLastWagonAttached());
            //set the current sequence equal to the given sequence
            setFirstWagon(sequence);
            return true;
            //catch a runtime exception
        } catch (RuntimeException e) {
            return false;
        }
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
        //check if the given sequence can attach to the current sequence
        if (!canAttach(sequence)) return false;
        //when to position to attach to is the first position, chang function call to insertatfront
        if (position == 1) return insertAtFront(sequence);
        try {
            //get the wagon at the gvien position
            Wagon atPosition = findWagonAtPosition(position);
            //detach the wagon at the given position from the previous wagons
            atPosition.detachFromPrevious();
            //attach the sequence to the last wagon attached to the sequence
            sequence.attachTo(getLastWagonAttached());
            //attach the wagon that was at the given position to the last wagon attached
            atPosition.attachTo(getLastWagonAttached());
            return true;
            //catch a runtime exception when necessary
        } catch (RuntimeException e) {
            return false;
        }
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
        //find the wagon with the given id
        Wagon w = findWagonById(wagonId);
        //check if the wagon was found and the found wagon can attach to the given train else return false
        if (w == null || !toTrain.canAttach(w)) return false;
        //remove the found wagon from the current train
        w.removeFromSequence();
        //return if the wagon was succesfully attached to the given train
        return toTrain.attachToRear(w);
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
        //find the wagon at the given position
        Wagon w = findWagonAtPosition(position);
        //check if a wagon was found and the wagon that was found can attach to the given train else return false
        if (w == null || !toTrain.canAttach(w)) return false;
        //detach the found wagon from the current train
        w.detachFromPrevious();
        //return if the sequnce was succesfully addded to the given train
        return toTrain.attachToRear(w);
    }

    /**
     * Reverses the sequence of wagons in this train (if any)
     * i.e. the last wagon becomes the first wagon
     *      the previous wagon of the last wagon becomes the second wagon
     *      etc.
     * (No change if the train has no wagons or only one wagon)
     */
    public void reverse() {
        //check if there is a firstwagon and that the firstwagon has a nextwagon else stop the function
        if (firstWagon == null || !firstWagon.hasNextWagon()) return;
        //set the firstwagon equals to the reverse of the sequence
        setFirstWagon(firstWagon.reverseSequence());
    }

    @Override
    public Iterator<Wagon> iterator() {
        //create a new instance of a iterator
        return new Iterator<>() {
            //create a pointer to current wagon and set it equals to the firstwagon
            Wagon cur = firstWagon;

            @Override
            public boolean hasNext() {
                //check if the current wagon exists
                return cur != null;
            }

            @Override
            public Wagon next() {
                //set the local var prev equals to the current wagon
                Wagon prev = cur;
                //set the curent wagon equals to the next wagon
                cur = cur.getNextWagon();
                //return the prev wagon
                return prev;
            }
        };
    }

    @Override
    public String toString() {
        //instantiate a new stringbuilder
        StringBuilder sb = new StringBuilder();
        //append the toString method of the locomotive (engine) of the current instance
        sb.append(getEngine());
        //loop through every wagon (another type of foreach loop) and append every toString method of each wagon to the output result
        forEach((wagon) -> {
            sb.append(wagon);
        });
        //append the word with with spaces to the final string
        sb.append(" with ");
        //append the total number of wagons to the string
        sb.append(getNumberOfWagons());
        //add another little bit of text to the string
        sb.append(" wagons from ");
        //add the location tha the train is traveling from to the string
        sb.append(origin);
        //add another word to the string
        sb.append(" to ");
        //add the final destination to the string
        sb.append(destination);
        //return the final string
        return sb.toString();
    }
}
