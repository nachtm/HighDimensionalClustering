package spinacht.data;

import java.util.Collection;


public interface Database extends Collection<Point> {

  public int getDimensionality();

  public Point get(int i);

  // @Override public boolean add(Object element) { throw new UnsupportedOperationException(); }
  // @Override public boolean addAll(Collection c) { throw new UnsupportedOperationException(); }
  // @Override public boolean remove(Object element) { throw new UnsupportedOperationException(); }
  // @Override public boolean removeAll(Collection c) { throw new UnsupportedOperationException(); }
  // @Override public boolean retainAll(Collection c) { throw new UnsupportedOperationException(); }
  // @Override public int size() { throw new UnsupportedOperationException(); }
  // @Override public void clear() { throw new UnsupportedOperationException(); }
  // @Override public boolean contains(Object element) { throw new UnsupportedOperationException(); }
  // @Override public boolean containsAll(Collection c) { throw new UnsupportedOperationException(); }
  // @Override public Iterator iterator() { throw new UnsupportedOperationException(); }
  // @Override public Object[] toArray() { throw new UnsupportedOperationException(); }
  // @Override public boolean isEmpty() { throw new UnsupportedOperationException(); }
  // @Override public boolean equals(Object element) { throw new UnsupportedOperationException(); }
  // @Override public int hashCode() { throw new UnsupportedOperationException(); }

}
