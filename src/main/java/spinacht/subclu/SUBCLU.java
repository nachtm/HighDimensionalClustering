package spinacht.subclu;

import java.lang.Iterable;

import spinacht.subclu.Trie;
import spinacht.data.Clustering;
import spinacht.common.Params;


public class SUBCLU {

  static Clustering go(Params params) {
    Trie trie = new Trie(params);
    while (trie.extend());
    return trie;
  }

}
