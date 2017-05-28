package spinacht.subclu;

import spinacht.Params;
import spinacht.data.Clustering;


public class SUBCLU {

    public static Clustering go(Params params) {
        return go(params, false);
    }

    public static Clustering go(Params params, boolean verbose){
        Trie trie = new Trie(params);
        int k = 1;
        while (trie.extend(verbose)){
            if(verbose){
                System.out.println(k);
                k++;
            }
        }
        return trie;
    }


}
