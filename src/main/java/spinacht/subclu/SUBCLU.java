package spinacht.subclu;

import spinacht.Params;
import spinacht.data.Clustering;

/**
 * Provides the function that implements the SUBCLU algorithm.
 */
public class SUBCLU {

    /**
     * Cluster in each subspace with SUBCLU with the given parameters.
     * @param params Parameters to SUBCLU
     * @return A Clustering object that represents the result of the clustering
     */
    public static Clustering go(Params params) {
        return go(params, false);
    }

    /**
     * Cluster in each subspace with SUBCLU with the given parameters, optionally reporting progress (for debugging).
     * @param params Parameters to SUBCLU
     * @param verbose Whether to write debugging output to stdout
     * @return A Clustering object that represents the result of the clustering
     */
    public static Clustering go(Params params, boolean verbose){
        Trie trie = new Trie(params);
        while (trie.extend());
        return trie;
    }


}
