import java.awt.*;
import java.io.ObjectStreamException;
import java.sql.Array;
import java.util.NoSuchElementException;

/**
 * Your implementation of an ArrayList.
 *
 * @author Quang Nguyen
 * @version 1.0
 * @userid qnguyen305 (i.e. gburdell3)
 * @GTID 903770019 (i.e. 900000000)
 *
 * Collaborators: LIST ALL COLLABORATORS YOU WORKED WITH HERE
 *
 * Resources: LIST ALL NON-COURSE RESOURCES YOU CONSULTED HERE
 */
public class ArrayList<T> {

    /**
     * The initial capacity of the ArrayList.
     *
     * DO NOT MODIFY THIS VARIABLE!
     */
    public static final int INITIAL_CAPACITY = 9;

    // Do not add new instance variables or modify existing ones.
    private T[] backingArray;
    private int size;

    /**
     * Constructs a new ArrayList.
     *
     * Java does not allow for regular generic array creation, so you will have
     * to cast an Object[] to a T[] to get the generic typing.
     */
    public ArrayList() {
        this.backingArray = (T[]) new Object[INITIAL_CAPACITY];   // create a new Object[] and cast to T[]
        this.size = 0;                                            // set size to 0
    }

    /**
     * Adds the element to the specified index.
     *
     * Remember that this add may require elements to be shifted.
     *
     * Must be amortized O(1) for index size and O(n) for all other cases.
     *
     * @param index the index at which to add the new element
     * @param data  the data to add at the specified index
     * @throws java.lang.IndexOutOfBoundsException if index < 0 or index > size
     * @throws java.lang.IllegalArgumentException  if data is null
     */
    public void addAtIndex(int index, T data) {
        if (index < 0) {
            // throw exception if index < 0
            throw(new IndexOutOfBoundsException("index smaller than 0!"));
        } else if (index > this.size) {
            // throw exception if index > array size, created a null value between array values
            throw(new IndexOutOfBoundsException("index bigger than array size!"));
        }
        if (data == null) {
            // throw exception if data is null
            throw(new IllegalArgumentException("data is null!"));
        }

        // increase array size by 1
        this.size++;

        int capacity;

        /* set the capacity of the array
         * if size <= initial capacity (9)
         * then set the capacity = initial capacity
         * else, set the capacity = array length
         */
        if (this.size <= INITIAL_CAPACITY) {
            capacity = INITIAL_CAPACITY;
        } else {
            capacity = this.backingArray.length;
        }

        /* if the value at index is null and array size does not exceed or array size = array capacity
        *  then the value at index = data
        *  since the case where non-consecutive has throw exception
        *  this is a quick way to add data to the back of the array
        *  this should resolve in O(1)
        */
        if (this.size <= capacity && this.backingArray[index] == null) {
            this.backingArray[index] = data;
        } else if (this.size > capacity) {
            /* if array size exceed to array capacity
             * then create a new array with double the capacity
             * this should resolve in O(n)
             * n: the # of index in the array
             */
            capacity *= 2;
            // create a temp array with double capacity
            T[] temp = (T[]) new Object[capacity];

            // index to control temp array
            int tempIndex = 0;
            // index to control backingArray array
            int backingArrayIndex = 0;

            /* Iterate through backingArray array
             * if it is not the index where the user want to add
             * copy the value from backingArray array to temp array
             * increase tempIndex and backingArrayIndex
             * else if it is the index where the user want to add
             * add the data value into the index
             * increase tempIndex by 1
             * after exit the loop
             * change the pointer of backingArray toward temp
             */
            while (backingArrayIndex < this.backingArray.length) {
                if (tempIndex != index) {
                    temp[tempIndex] = this.backingArray[backingArrayIndex];
                    tempIndex++;
                    backingArrayIndex++;
                } else {
                    temp[tempIndex] = data;
                    tempIndex++;
                }
            }
            this.backingArray = temp;
        } else {
            /* if the index is not null and the size does not exceed the capacity
             * then move every values from index to size - 1 down 1 index
             * this can be done by moving the value from the back first
             * since it will not exceed the capacity and the value at size - 1 is null
             * then set the data value into the index
             * this resolved in O(n)
             * n: the # of (size - index) in array
             * n is not the number of index because it only moving from index to size - 1
             * this will also take less memories
             * since the computer do not need to create a new array
             */
            for (int i = this.size - 1; i > index; i--) {
                this.backingArray[i] = this.backingArray[i - 1];
            }

            this.backingArray[index] = data;
        }
    }

    /**
     * Adds the element to the front of the list.
     *
     * Remember that this add may require elements to be shifted.
     *
     * Must be O(n).
     *
     * @param data the data to add to the front of the list
     * @throws java.lang.IllegalArgumentException if data is null
     */
    public void addToFront(T data) {
        // called addAtIndex func with index 0
        addAtIndex(0, data);
    }

    /**
     * Adds the element to the back of the list.
     *
     * Must be amortized O(1).
     *
     * @param data the data to add to the back of the list
     * @throws java.lang.IllegalArgumentException if data is null
     */
    public void addToBack(T data) {
        /* called addAtIndex func with array size
         * it is not size - 1
         * because size will increase by 1
         */
        addAtIndex(this.size, data);
    }

    /**
     * Removes and returns the element at the specified index.
     *
     * Remember that this remove may require elements to be shifted.
     *
     * Must be O(1) for index size - 1 and O(n) for all other cases.
     *
     * @param index the index of the element to remove
     * @return the data formerly located at the specified index
     * @throws java.lang.IndexOutOfBoundsException if index < 0 or index >= size
     */
    public T removeAtIndex(int index) {
        if (index < 0) {
            // throw exception if index < 0
            throw(new IndexOutOfBoundsException("index smaller than 0!"));
        } else if (index >= this.size) {
            // throw exception if index > array size
            throw(new IndexOutOfBoundsException("index bigger than array size!"));
        }
        // decrease array size by 1
        this.size--;

        // store the value at index in a data var
        T data = this.backingArray[index];

        // set the value at index to null
        this.backingArray[index] = null;

        /* if index = array size
         * indicate remove from back of the array
         * return the data, since there are nothing more to do
         * this resolved in O(1)
         */
        if (index == this.size) {
            return data;
        }

        /* otherwise
         * move every values from index + 1 to size up by 1 index
         * same logic as addAtIndex
         * since the value at index is null
         * this resolved in O(n)
         * n: the # of (size - index) in array
         */

        for (int i = index; i <= this.size; i++) {
            this.backingArray[i] = this.backingArray[i + 1];
        }
        return data;
    }

    /**
     * Removes and returns the first element of the list.
     *
     * Remember that this remove may require elements to be shifted.
     *
     * Must be O(n).
     *
     * @return the data formerly located at the front of the list
     * @throws java.util.NoSuchElementException if the list is empty
     */
    public T removeFromFront() {
        if (isEmpty()) {
            // throw exception if list is empty
            // check list empty by called isEmpty
            throw(new NoSuchElementException("list is empty!"));
        }
        // called removeAtIndex with index 0
        return removeAtIndex(0);
    }

    /**
     * Removes and returns the last element of the list.
     *
     * Must be O(1).
     *
     * @return the data formerly located at the back of the list
     * @throws java.util.NoSuchElementException if the list is empty
     */
    public T removeFromBack() {
        if (isEmpty()) {
            // throw exception if list is empty
            // check list empty by called isEmpty
            throw(new NoSuchElementException("list is empty!"));
        }
        // called removeAtIndex with size - 1
        return removeAtIndex(this.size - 1);
    }

    /**
     * Returns the element at the specified index.
     *
     * Must be O(1).
     *
     * @param index the index of the element to get
     * @return the data stored at the index in the list
     * @throws java.lang.IndexOutOfBoundsException if index < 0 or index >= size
     */
    public T get(int index) {
        if (index < 0) {
            // throw exception if index < 0
            throw(new IndexOutOfBoundsException("index smaller than 0!"));
        } else if (index >= this.size) {
            // throw exception if index >= size
            throw(new IndexOutOfBoundsException("index bigger than array size!"));
        }
        // this resolve in O(1)
        return this.backingArray[index];
    }

    /**
     * Returns whether or not the list is empty.
     *
     * Must be O(1).
     *
     * @return true if empty, false otherwise
     */
    public boolean isEmpty() {
        // this resolved in O(1)
        return size <= 0;
    }

    /**
     * Clears the list.
     *
     * Resets the backing array to a new array of the initial capacity and
     * resets the size.
     *
     * Must be O(1).
     */
    public void clear() {
        // this resolved in O(1)
        this.backingArray = (T[]) new Object[INITIAL_CAPACITY];
        this.size = 0;
    }

    /**
     * Returns the backing array of the list.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the backing array of the list
     */
    public T[] getBackingArray() {
        // DO NOT MODIFY THIS METHOD!
        return backingArray;
    }

    /**
     * Returns the size of the list.
     *
     * For grading purposes only. You shouldn't need to use this method since
     * you have direct access to the variable.
     *
     * @return the size of the list
     */
    public int size() {
        // DO NOT MODIFY THIS METHOD!
        return size;
    }
}
