package spinacht.data;

import com.google.common.collect.Iterables;

import java.util.Arrays;
import java.util.Iterator;


public class SubspaceWrapper implements TransparentSubspace {

    private final Iterable<Integer> wrapped;

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
