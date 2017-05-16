package spinacht.subclu;

import java.lang.Iterable;

import spinacht.subclu.Trie;
import spinacht.common.Cluster;
import spinacht.common.Params;


public class SUBCLU {

  static Iterable<Cluster> go(Params params) {
    Trie trie = new Trie(params);
    while (trie.extend());
    return trie;
  }

}
