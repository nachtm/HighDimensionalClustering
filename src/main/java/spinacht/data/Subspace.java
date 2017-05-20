package spinacht.data;

import java.lang.Iterable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;


public interface Subspace extends Iterable<Integer> {

  static Subspace of(Integer... dims) {
    return new Subspace.Simple(dims);
  }

  class Simple implements Subspace {
    private final Iterable<Integer> it;
    Simple(Integer[] dims) {
      this.it = Arrays.asList(dims);
    }
    public Iterator<Integer> iterator() {
      return this.it.iterator();
    }
  }

  static String pprint(Subspace subspace) {
    return "{" + StreamSupport.stream(subspace.spliterator(), false).map(i -> i.toString()).collect(Collectors.joining(", ")) + "}";
  }

}