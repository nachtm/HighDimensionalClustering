package spinacht.subclu;

import java.lang.*;
import java.util.*;
import java.util.function.*;

import spinacht.dbscan.DBSCAN;
import spinacht.data.Cluster;
import spinacht.data.Clustering;
import spinacht.data.Subset;
import spinacht.data.Subspace;
import spinacht.common.Params;


class Trie implements Clustering {

  private final DBSCAN dbscan;
  private final Params params;

  Trie(Params params) {
    this.dbscan = new DBSCAN(params);
    this.params = params;
  }

  boolean extend() {
    return root.extend(0, new Node[this.params.getDatabase().getDimensionality()], new LinkedList<>());
  }

  public void forEachCluster(Function<Subspace, Consumer<Subset>> f) {
    this.root.forEachCluster(f);
  }

  // public Iterator<Cluster> iterator() {
  //   return new Iterator<Cluster>() {
  //     public boolean hasNext() {
  //       return false;
  //     }
  //     public Cluster next() {
  //       return null;
  //     }
  //   };
  // }

  private Node root;

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
        this.children.put(i, new Node(this, i, dbscan.go(everything, Arrays.asList(i))));
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
          return this.curr != null;
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
        for (int i = 1; i < marks.length; i++) {
          candidates.retainAll(marks[i].children.keySet());
        }
        this.children = new HashMap<>();
        for (Integer extension : candidates) {
          path.addLast(extension);
          if (extension > this.upEdge) {
            Node bestSubspace = this;
            for (int i = 0; i < k; i++) {
              if (marks[i].npoints < bestSubspace.npoints) {
                bestSubspace = marks[i];
              }
            }
            List<Subset> clusters = new LinkedList<>();
            for (Subset cluster : bestSubspace.clusters) {
              for (Subset c : dbscan.go(cluster, path)) {
                clusters.add(c);
              }
            }
            if (!clusters.isEmpty()) {
              this.children.put(extension, new Node(this, extension, clusters));
            }
          }
          path.removeLast();
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
