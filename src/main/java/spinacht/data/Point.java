package spinacht.data;

/**
 * A point in some-dimensional space.
 */
public interface Point {

    /**
     * Get the ith dimension of this point
     * @param i the dimension to index with
     * @return the value stored by the Point
     */
    double get(int i);

}
