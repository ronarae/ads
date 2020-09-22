package models;

public abstract class Wagon {
    protected int id;                 // some unique ID of a Wagon
    private Wagon nextWagon;        // another wagon that is appended at the tail of this wagon
                                    // a.k.a. the successor of this wagon in a sequence
                                    // set to null if no successor is connected
    private Wagon previousWagon;    // another wagon that is prepended at the front of this wagon
                                    // a.k.a. the predecessor of this wagon in a sequence
                                    // set to null if no predecessor is connected


    // representation invariant propositions:
    // tail-connection-invariant:   wagon.nextWagon == null or wagon == wagon.nextWagon.previousWagon
    // front-connection-invariant:  wagon.previousWagon == null or wagon = wagon.previousWagon.nextWagon

    public Wagon (int wagonId) {
        this.id = wagonId;
    }

    public Wagon() {

    }

    public int getId() {
        return id;
    }

    public Wagon getNextWagon() {
        return nextWagon;
    }

    public Wagon getPreviousWagon() {
        return previousWagon;
    }

    /**
     * @return  whether this wagon has a wagon appended at the tail
     */
    public boolean hasNextWagon() {
        return nextWagon != null;
    }

    /**
     * @return  whether this wagon has a wagon prepended at the front
     */
    public boolean hasPreviousWagon() {
        return previousWagon != null;
    }

    /**
     * finds the last wagon of the sequence of wagons attached to this wagon
     * if no wagons are attached return this wagon itselves
     * @return  the wagon found
     */
    public Wagon getLastWagonAttached() {
        //create a local variable to store the current element
        Wagon cur = this;
        //while the current wagon has a next wagon go to the next wagon
        while (cur.hasNextWagon()) {
            cur = cur.getNextWagon();
        }
        //return the last wagon
        return cur;
    }

    /**
     * @return  the number of wagons appended to this wagon
     *          return 0 if no wagons have been appended.
     */
    public int getSequenceLength() {
        //check if the current wagon has a next wagon
        if (hasNextWagon()) {
            //call the same function on the next wagon (recursion) and add 1 to the return value
            return getNextWagon().getSequenceLength() + 1;
        } else {
            //return one when there isn't another Wagon attached (stop criterium)
            return 1;
        }
    }

    /**
     * attaches this wagon at the tail of a given prevWagon.
     * @param newPreviousWagon
     * @throws RuntimeException if this wagon already has been appended to a wagon.
     * @throws RuntimeException if prevWagon already has got a wagon appended.
     */
    public void attachTo(Wagon newPreviousWagon) {
        //if there is a previous wagon the wagon is already attached throw a runtime exception
        if (hasPreviousWagon()) throw new RuntimeException();
        //else if there is already an wagon attached to wagon the wagon wants to be attached to throw a runtime exception
        else if (newPreviousWagon.hasNextWagon()) throw new RuntimeException();
        //attach the wagon the the new previous wagon
        else {
            newPreviousWagon.nextWagon = this;
            previousWagon = newPreviousWagon;
        }
    }

    /**
     * detaches this wagon from its previous wagons.
     * no action if this wagon has no previous wagon attached.
     */
    public void detachFromPrevious() {
        //check if the wagon has a wagon before itself
        if (hasPreviousWagon()) {
            //check if the currently appointed previous wagon points to this wagon
            if (this == getPreviousWagon().getNextWagon()) {
                //if the previous wagon point to this wagon remove pointerr
                getPreviousWagon().nextWagon = null;
            }
            //set the pointer to the previous wagon to null
            previousWagon = null;
        }
    }

    /**
     * detaches this wagon from its tail wagons.
     * no action if this wagon has no succeeding next wagon attached.
     */
    public void detachTail() {
        //check if the current wagon has a wagon attached to it
        if (hasNextWagon()) {
            //check if the wagon attached to the current wagon points back to the current wagon
            if (this == getNextWagon().getPreviousWagon()) {
                //set the pointer to the current wagon to null
                getNextWagon().previousWagon = null;
            }
            //set the pointer to the next wagon to null
            nextWagon = null;
        }

    }

    /**
     * attaches this wagon at the tail of a given newPreviousWagon.
     * if required, first detaches this wagon from its current predecessor
     * and/or detaches the newPreviousWagon from its current successor
     * @param newPreviousWagon
     */
    public void reAttachTo(Wagon newPreviousWagon) {
        //if the wagon is already attached to another wagon, detach the wagon
        if (hasPreviousWagon()) detachFromPrevious();
        //if the wagon this wagon wants to attach to, detach the tail of that wagon
        if (newPreviousWagon.hasNextWagon()) newPreviousWagon.detachTail();
        //attach the new wagon to the given tail
        attachTo(newPreviousWagon);
    }

    /**
     * Removes this wagon from the sequence that it is part of, if any.
     * Reconnect the subsequence of its predecessors with the subsequence of its successors, if any.
     */
    public void removeFromSequence() {
        //if the wagon has previous wagon and a next wagon, attach the next wagon to the previous wagon
        if (getPreviousWagon() != null && getNextWagon() != null) getNextWagon().reAttachTo(getPreviousWagon());
        //if the wagon has a previous wagon, detach from the previous wagon
        if (hasPreviousWagon()) detachFromPrevious();
        //if the wagon has a next wagon, detach the tail of the wagon
        if (hasNextWagon()) detachTail();
    }


    /**
     * reverses the order in the sequence of wagons from this Wagon until its final successor.
     * The reversed sequence is attached again to the predecessor of this Wagon, if any.
     * no action if this Wagon has no succeeding next wagon attached.
     * @return the new start Wagon of the reversed sequence (with is the former last Wagon of the original sequence)
     */
    public Wagon reverseSequence() {
        //create a pointer to the current wagon
        Wagon cur = this;
        //create a empty temporary pointer
        Wagon temp;

        //create a null pointer to the previous wagon
        Wagon previous = null;
        //check if the current wagon has a previous wagon
        if (cur.hasPreviousWagon()) {
            //set the previous pointer equals to the previous wagon
            previous = cur.getPreviousWagon();
            //detach the current wagon from the previous wagon
            cur.detachFromPrevious();
        }

        //loop through until infinity or until the break criterium
        while (true) {
            //when the current wagon has a following wagon set the temp equals to the next wagon
            if (cur.hasNextWagon()) temp = cur.getNextWagon();
            //set the temp wagon to null (starts the stop criterium)
            else temp = null;
            //set the next wagon equals to the previouswagon
            cur.nextWagon = cur.getPreviousWagon();
            //set the previous wagon equals to the temp wagon
            cur.previousWagon = temp;
            //when the temp is unequal to null set the current wagon equals to the temp wagon
            if (temp != null) cur = temp;
            //when temp is equals null stop the loop
            else break;
        }

        //when the previous wagon is null, attach the new start of the sequence to the last wagon
        if (previous != null) {
            cur.reAttachTo(previous);
        }
        //return the now first wagon of the reversed sequence
        return cur;
    }

    @Override
    public String toString() {
        //return a string representation of this class
        return String.format("[Wagon-%d]", getId());
    }
}
