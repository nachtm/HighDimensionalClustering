package spinacht.data;

import java.util.Collection;
import java.util.HashSet;

/**
 * A cluster, or subset, of points. This is mostly semantic at this point, though it is conceivable that later
 * we may want to do more things here.
 */
public class Subset extends HashSet<Point> {

    /**
     * Construct a subset.
     */
    public Subset() {
    super();
  }

    /**
     * Construct a subset using another collection of points.
     * @param c
     */
    public Subset(Collection<Point> c) {
        super(c);
    }

}
