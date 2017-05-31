package spinacht.data;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A class that stores the entire clustering in memory as a HashMap from TransparentSubspace -> a set of subsets.
 *
 * Created by nachtm on 5/25/17.
 */
public class InMemoryClustering extends HashMap<TransparentSubspace, Set<Subset>> implements Clustering {

    /**
     * Default initializer for an InMemoryClustering.
     */
    public InMemoryClustering(){
        super();
    }

    @Override
    public void forEachCluster(Function<Subspace, Consumer<Subset>> f) {
        for(Entry<TransparentSubspace, Set<Subset>> e : this.entrySet()){
            for(Subset s : e.getValue()){
                f.apply(e.getKey()).accept(s);
            }
        }
    }

    /**
     * Returns this object (since we don't need to re-copy this every single time).
     * @return this InMemoryClustering.
     */
    @Override
    public InMemoryClustering collect() {
        return this;
    }

}
