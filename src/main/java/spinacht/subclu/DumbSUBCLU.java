package spinacht.subclu;

import spinacht.common.Params;
import spinacht.data.Clustering;
import spinacht.data.InMemoryClustering;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.dbscan.DBSCANNER;
import spinacht.dbscan.PaperDBScanner;
import spinacht.index.Index;

import java.util.HashSet;
import java.util.Set;
import java.util.function.BiFunction;

/**
 * Created by nachtm on 5/25/17.
 */
public class DumbSUBCLU {

    public static InMemoryClustering go(Params params){

        Index index = new Index(params.getDatabase());

        DBSCANNER ds = new PaperDBScanner(params.getEps(), params.getMinPts(), index);

        Subset everything = new Subset(params.getDatabase());

        //assume 2d database
        InMemoryClustering result = new InMemoryClustering();

        BiFunction<Subspace, Set<Subset>, Set<Subset>> addToMap = (subspace, subset) -> new HashSet<>(ds.dbscan(subspace, everything));
        result.compute(Subspace.of(0),   addToMap);
        result.compute(Subspace.of(1),   addToMap);
        result.compute(Subspace.of(0,1), addToMap);

        return result;
    }
}
