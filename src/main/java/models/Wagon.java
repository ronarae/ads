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
        //a boolean where it is saved which element is that one that the method called for the first time
        boolean wagonToStartTheReverseSequenceOn = false;
        //if the first wagon was connected to another wagon save that wagon to this variable
        Wagon wagonBeforeTheReverseSequence = null;
        //make the variable result available in the whole function
        Wagon result;
        //make the variable temp available in the whole method
        Wagon temp;

        //check if the current active wagon has a wagon before itself
        if (hasPreviousWagon()) {
            //check if this wagon has a following wagon
            if (!hasNextWagon()) {
                //if this is not the case set the next wagon equals to the previous wagon
                nextWagon = getPreviousWagon();
                //remove the value of the previous wagon
                previousWagon = null;
                //return this instance, there are no more wagons, so this is the last wagon of the sequence
                return this;
            }
            //check for a representation invariant propositions if the proposition is correct it is the first call to this method
            if (getPreviousWagon().getNextWagon() == this) {
                //set the previous wagon equals to the previous wagon
                wagonBeforeTheReverseSequence = getPreviousWagon();
                //make sure the method knows this is the first caller of the method
                wagonToStartTheReverseSequenceOn = true;
            }
            //create a temp variable to store the next wagon
            temp = getNextWagon();
            //the next wagon is set equals to the previous wagon
            nextWagon = getPreviousWagon();
            //the previous wagon is set to the temp variable which has the next wagon stored
            previousWagon = temp;
            //call the method again on the next wagon (recursion)
            result = temp.reverseSequence();
        }
        //the current wagon doesn't have a wagon before itself, this means that the wagon is the first wagon
        else {
            //if this wagon doesn't have a following wagon, return this wagon, you cannot reverse something with the length of one
            if (!hasNextWagon()) return this;
            //make sure the method knows this is a first call of the method
            wagonToStartTheReverseSequenceOn = true;
            //set the temp and the previous wagon variable equals to the next wagon
            temp = previousWagon = getNextWagon();
            //the first wagon will be the last wagon so the next wagon must be set to null
            nextWagon = null;
            //call the function again on the wagon that was the following wagon
            result = temp.reverseSequence();
        }

        //check if this wagon is the first wagon that called the method
        if (wagonToStartTheReverseSequenceOn) {
            //set the next wagon to null, the first will be the last (Matthew 20:16) so there is no following wagon
            nextWagon = null;
            //if this reverse call is a partial reverse run the code in the if block
            if (wagonBeforeTheReverseSequence != null) {
                //reattach the now first wagon of the sequence to the sequence it was attached to.
                result.reAttachTo(wagonBeforeTheReverseSequence);
            }
        }
        //return the new first wagon in the sequence
        return result;
    }

    @Override
    public String toString() {
        //return a string representation of this class
        return String.format("[Wagon-%d]", getId());
    }
}
