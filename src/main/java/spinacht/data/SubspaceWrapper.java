package spinacht.data;

import com.google.common.collect.Iterables;
import com.google.common.collect.Iterators;

import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;


public class SubspaceWrapper implements TransparentSubspace {

    private final Iterable<Integer> wrapped;

    public SubspaceWrapper(Iterator<Integer> contents) {
        LinkedList<Integer> list = new LinkedList<>();
        Iterators.addAll(list, contents);
        this.wrapped = list;
    }

    public SubspaceWrapper(Iterable<Integer> wrapped) {
    this.wrapped = wrapped;
  }

    public SubspaceWrapper(Integer... contents) {
        this.wrapped = Arrays.asList(contents);
    }

    @Override
    public Iterator<Integer> iterator() {
    return this.wrapped.iterator();
  }

    @Override
    public int hashCode() {
        int x = 0;
        for (Integer i : this) {
            x ^= i;
        }
        return x;
    }

    @Override
    public boolean equals(Object other) {
        return this == other || (other instanceof SubspaceWrapper) && Iterables.elementsEqual(this, (SubspaceWrapper) other);
    }

}
