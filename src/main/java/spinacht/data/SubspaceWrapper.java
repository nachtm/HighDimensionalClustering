package spinacht.data;

import java.util.Iterator;


public class SubspaceWrapper implements Subspace {

  final Iterable<Integer> wrapped;

  public SubspaceWrapper(Iterable<Integer> wrapped) {
    this.wrapped = wrapped;
  }

  public Iterator<Integer> iterator() {
    return this.wrapped.iterator();
  }

}
