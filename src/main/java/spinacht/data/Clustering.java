package spinacht.data;

import java.util.*;
import java.util.function.*;

import spinacht.data.Subset;
import spinacht.data.Subspace;


public interface Clustering {

    void forEachCluster(Function<Subspace, Consumer<Subset>> f);

    default Map<Subspace, Set<Subset>> collect() {
        Map<Subspace, Set<Subset>> clustering = new HashMap<>();
        forEachCluster(subspace -> subset ->
            clustering.compute(subspace, (IDONTCARE, curr) -> {
                if (curr == null) {
                    curr = new HashSet<>();
                    curr.add(subset);
                } else {
                    curr.add(subset);
                }
                return curr;
            })
        );
        return clustering;
    }

}
