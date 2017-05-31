package spinacht.data;

import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

/**
 * An iterable that defines the subspace that we want to look at. For example, we may want to only consider the 1st and 3rd
 * dimensions.
 */
public interface Subspace extends Iterable<Integer> {

    /**
     * A string representing a subspace in a nice format
     * @param subspace The subspace to stringify
     * @return The string representing the subspace
     */
    static String pprint(Subspace subspace) {
        return "{" + StreamSupport.stream(subspace.spliterator(), false).map(i -> i.toString()).collect(Collectors.joining(", ")) + "}";
    }

}