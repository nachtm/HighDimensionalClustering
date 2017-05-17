package spinacht.data;

import java.util.*;
import java.util.function.*;

import spinacht.data.Subset;
import spinacht.data.Subspace;


public interface Clustering {

  public void forEachCluster(Function<Subspace, Consumer<Subset>> f);

}
