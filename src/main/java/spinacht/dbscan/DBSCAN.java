package spinacht.dbscan;

import java.lang.Iterable;
import java.util.Collection;

import spinacht.common.Params;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.index.Index;


public class DBSCAN {

  private final PaperDBScanner dbscan;

  public DBSCAN(Params params, Index index) {
    dbscan = new PaperDBScanner(params.getEps(), params.getMinPts(), index);
  }

  public Collection<Subset> go(Subset subset, Subspace subspace) {
    return dbscan.dbscan(subspace, subset);
  }

}
