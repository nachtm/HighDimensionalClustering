package spinacht.subclu;

import spinacht.Params;
import spinacht.data.Clustering;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.data.SubspaceWrapper;
import spinacht.dbscan.DBSCANNER;
import spinacht.dbscan.PaperDBScanner;
import spinacht.index.Index;

import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;


class Trie implements Clustering {

  private final DBSCANNER dbscanner;
  private final Params params;
  private final Node root;

  Trie(Params params) {
    this.dbscanner = new PaperDBScanner(params.getEps(), params.getMinPts(), new Index(params.getDatabase()));
    this.params = params;
    this.root = new Node();
  }

  boolean extend() {
    Node[] marks = new Node[this.params.getDatabase().getDimensionality()];
    return root.extend(0, marks, new LinkedList<>());
  }

  public void forEachCluster(Function<Subspace, Consumer<Subset>> f) {
    this.root.forEachCluster(f);
  }

  private class Node implements Subspace, Clustering {

    final Node parent;
    final int upEdge;
    final Collection<Subset> clusters;
    final int npoints;
    Map<Integer, Node> children;

    // root
    Node() {
      this.parent = null;
      this.upEdge = -1;
      this.children = new HashMap<>();
      Subset everything = new Subset(Trie.this.params.getDatabase());
      for (int i = 0; i < Trie.this.params.getDatabase().getDimensionality(); i++) {
        Collection<Subset> clusters = dbscanner.dbscan(new SubspaceWrapper(Arrays.asList(i)), everything);
        this.children.put(i, new Node(this, i, clusters));
      }
      this.clusters = Collections.EMPTY_SET;
      this.npoints = 0;
    }

    // rest
    Node(Node parent, int upEdge, Collection<Subset> clusters) {
      this.parent = parent;
      this.upEdge = upEdge;
      this.children = null;
      this.clusters = clusters;
      int sum = 0;
      for (Subset cluster : clusters) {
        sum += cluster.size();
      }
      this.npoints = sum;
    }

    public void forEachCluster(Function<Subspace, Consumer<Subset>> f) {
      this.clusters.forEach(f.apply(this));
      if (this.children != null) {
        for (Node child : children.values()) {
          child.forEachCluster(f);
        }
      }
    }

    public Iterator<Integer> iterator() {
      return new Iterator<Integer>() {
        private Node curr = Node.this;
        public boolean hasNext() {
          return this.curr.parent != null;
        }
        public Integer next() {
          int last = this.curr.upEdge;
          this.curr = curr.parent;
          return last;
        }
      };
    }

    boolean extend(int k, Node[] marks, LinkedList<Integer> path) {
      if (this.children == null) {
        Set<Integer> candidates = new HashSet<>(marks[0].children.keySet());
        for (int i = 1; i < k; i++) {
          candidates.retainAll(marks[i].children.keySet());
        }
        this.children = new HashMap<>();
        for (Integer extension : candidates) {
          if (extension > this.upEdge) {
            path.addLast(extension);
            Node bestSubspace = this;
            for (int i = 0; i < k; i++) {
              if (marks[i].children.get(extension).npoints < bestSubspace.npoints) {
                bestSubspace = marks[i].children.get(extension);
              }
            }
            List<Subset> clusters = new LinkedList<>();
            for (Subset cluster : bestSubspace.clusters) {
              clusters.addAll(dbscanner.dbscan(new SubspaceWrapper(path), cluster));
            }
            if (!clusters.isEmpty()) {
              this.children.put(extension, new Node(this, extension, clusters));
            }
            path.removeLast();
          }
        }
        return !this.children.isEmpty();
      } else {
        boolean extended = false;
        marks[k] = this;
        int stopIx = k;
        for (Map.Entry<Integer, Node> entry : this.children.entrySet()) {
          for (int i = 0; i < k; i++) {
            Node next = marks[i].children.get(entry.getKey());
            if (next == null) {
              stopIx = i;
            } else {
              marks[i] = next;
            }
          }
          if (stopIx == k) {
            path.addLast(entry.getKey());
            extended = entry.getValue().extend(k + 1, marks, path) || extended;
            path.removeLast();
          }
          for (int i = 0; i < stopIx; i++) {
            marks[i] = marks[i].parent;
          }
        }
        return extended;
      }
    }

  }

}
