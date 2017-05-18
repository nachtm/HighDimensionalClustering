package spinacht.data;

import java.lang.Iterable;
import java.util.*;


public interface Subspace extends Iterable<Integer> {

  static Subspace of(Integer... dims) {
    return new Subspace.Simple(dims);
  }

  static class Simple implements Subspace {
    private final Iterable<Integer> it;
    Simple(Integer[] dims) {
      this.it = Arrays.asList(dims);
    }
    public Iterator<Integer> iterator() {
      return this.it.iterator();
    }
  }

}
