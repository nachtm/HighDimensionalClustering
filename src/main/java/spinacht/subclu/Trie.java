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


/** This class is complicated. It implements SUBCLU using a trie. It can be
 * opaquely traversed after clustering is done to retrieve clusters.
 *
 * Edges in this trie correspond to dimensions and nodes correspond to subspaces
 * (equivalent to the path from the root to that node).  The trie is strictly
 * ordered: edges closer to the root correspond to smaller items (by index) than
 * children. This ensures that nodes correspond to subspaces and vice
 * versa. Nodes contain the clusters in their corresponding subspace.  The general
 * idea is to start with a trie of height 1, and then extend it one level at a
 * time, only extending to and clustering in subspaces that the A-prior
 * principle allows, given the monotonicity of density-connectivity.
 * I used this same algorithm for the Association Rules assignment.
 */
class Trie implements Clustering {

    private final DBSCANNER dbscanner;
    private final Params params;
    private final Node root;

    Trie(Params params) {
        this.dbscanner = new PaperDBScanner(params.getEps(), params.getMinPts(), new Index(params.getDatabase()));
        this.params = params;
        this.root = new Node();
    }

    /**
     * Extend the trie by one level, returning whether there were any subspaces
     * with clusters in the new level. If not then no more clusters will be
     * found.
     */
    boolean extend(){
        Node[] marks = new Node[this.params.getDatabase().getDimensionality()];
        return root.extend(0, marks, new LinkedList<>());
    }

    /**
     * Visit each cluster of each subspace.
     */
    public void forEachCluster(Function<Subspace, Consumer<Subset>> f) {
        this.root.forEachCluster(f);
    }

    /**
     * Internal node class of trie.
     */
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

        // other nodes
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

        /**
         * Recursively visit each cluster of each subspace.
         */
        public void forEachCluster(Function<Subspace, Consumer<Subset>> f) {
            this.clusters.forEach(f.apply(this));
            if (this.children != null) {
                for (Node child : children.values()) {
                    child.forEachCluster(f);
                }
            }
        }

        /**
         * Iterate through edge values (corresponding to dimensions) from here
         * to the root. This iteration corresponds to a subspace.
         */
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


        /** Subspace extension algorithm. Suppose you are to creating the k+1'th
         * level of the tree (so you are finding candidate k+1-subspaces and
         * their clusters). The idea is to traverse the tree, at each level in
         * your current traversal keeping track of what will end up being all of
         * the k-1 subspaces with clusters that are subspaces of the k subspace
         * at each leaf. Once a leaf is reached, the current leaf might be
         * extended to each dimensions that is both greater than the last
         * dimension in the path to the leaf and a child of each k-1 subspace
         * that has been kept track of.  In such a way, the resulting subspace
         * has every single subspace contained in the trie already.  This union
         * of all of the clusters in all of these subspaces of the subspace in
         * question is then clustered in the k+1 subspace being investigates. If
         * there are any clusters, these clusters are added to the trie at a
         * newly extended leaf corresponding to this k+1 subspace.
         *
         * This is accomplished in a single traversal, without any nested
         * traversals, which may seem surprising. The trade-off is keeping track
         * of a list of other nodes (in marks) that was described above. The
         * number of nodes being kept track of at any step in the traversal is
         * equal to k+1, which is small compared to the size of the tree.
         *
         * This is a pretty involved algorithm, as it involves fusing a bunch of
         * things into just one traversal. As mentioned in our paper, it doesn't
         * effect the asymptotic runtime of SUBCLU, but it is conjecturally both
         * more efficient and niftier (though much more involved) then what is
         * proposed in the SUBCLU paper.
         */
        boolean extend(int k, Node[] marks, LinkedList<Integer> path) {

            if (this.children == null) {
                // We are at a leaf. Try to extend. First collect a set of
                // possible extensions based on A-priori.
                Set<Integer> candidates = new HashSet<>(marks[0].children.keySet());
                for (int i = 1; i < k; i++) {
                    candidates.retainAll(marks[i].children.keySet());
                }
                this.children = new HashMap<>();
                for (Integer extension : candidates) {
                    // Determine whether each candidate extension actually has
                    // any clusters, as is done in the SUBCLU paper.
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
                // We haven't yet reached a leaf. Recurse to each child, passing
                // modifying the list of marks for each one.
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
