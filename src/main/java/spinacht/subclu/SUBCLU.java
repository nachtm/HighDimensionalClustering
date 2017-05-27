package spinacht.subclu;

import spinacht.data.Clustering;
import spinacht.common.Params;


public class SUBCLU {

  public static Clustering go(Params params) {
    Trie trie = new Trie(params);
    while (trie.extend());
    return trie;
  }

}
