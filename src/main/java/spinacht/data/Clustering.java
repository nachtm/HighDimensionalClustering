package spinacht.data;

import java.util.*;
import java.util.function.*;

import spinacht.data.Subset;
import spinacht.data.Subspace;


public interface Clustering {

    void forEachCluster(Function<Subspace, Consumer<Subset>> f);

    default void pprint() {
        this.forEachCluster(subspace -> {
            System.out.print("SUBSPACE: " + Subspace.pprint(subspace));
            System.out.println();
            return subset -> {
                System.out.println("  SUBSET");
                for (Point p : subset) {
                    System.out.println("    " + p);
                }
            };
        });
    }

}
