package spinacht.data;

import java.util.Collection;

/**
 * An interface that defines a dataset that we may want to cluster later.
 * @param <T>
 */
public interface Database<T extends Point> extends Collection<T> {

    /**
     * Gets the dimensionality of each point in the dataset.
     * @return the number of dimensions of each point.
     */
    int getDimensionality();

}
