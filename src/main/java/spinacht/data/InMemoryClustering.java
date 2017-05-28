package spinacht.data;

import java.util.HashMap;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * Created by nachtm on 5/25/17.
 */
public class InMemoryClustering extends HashMap<TransparentSubspace, Set<Subset>> implements Clustering {

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

    @Override
    public InMemoryClustering collect() {
        return this;
    }

}
