package spinacht.data;

import java.util.Collection;


public interface Database<T extends Point> extends Collection<T> {

    int getDimensionality();

}
