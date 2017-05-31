package spinacht.index;

import java.util.function.Consumer;
import java.util.function.ToDoubleFunction;

/**
 * Range Tree implemented as a Red-Black Tree.
 * Some snippets of rb-tree from http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
 * @param <T>
 */
class RangeTree<T> {

    private static final boolean RED = true;
    private static final boolean BLACK = false;

    /**
     * Simple Red-Black Tree node
     */
    private class Node {

        private T val;
        private Node left, right;
        private boolean color;

        public Node(T val, boolean color) {
            this.val = val;
            this.color = color;
        }

    }

    private Node root = null;
    private final ToDoubleFunction<T> key;

    /**
     * Construct a Range Tree that compares elements on the given function.
     * That is, it compares them using: f(x) < f(y).
     * This allows Range Trees that only select certain dimensions to be easily constructed.
     * @param key Function on which to compare values.
     */
    RangeTree(ToDoubleFunction<T> key) {
        this.key = key;
    }

    /**
     * Initialize a Range Tree with the given values.
     * @param values Values to put in tree.
     * @param key See above.
     */
    RangeTree(Iterable<T> values, ToDoubleFunction<T> key) {
        this(key);
        for (T value : values) {
            this.insert(value);
        }
    }

    /**
     * Visit and consume each value in the given inclusive range in order.
     * @param lo Bottom of range
     * @param hi Top of range
     * @param consumer Function which which to touch each node traversed.
     */
    void forEachInRange(double lo, double hi, Consumer<T> consumer) {
    traverse(root, lo, hi, consumer);
  }

    /**
     * Traverse each node in the given range below the given node.
     */
    private void traverse(Node node, double lo, double hi, Consumer<T> consumer) {
        if (node != null) {
            if (key.applyAsDouble(node.val) < lo) {
                traverse(node.right, lo, hi, consumer);
            } else if (key.applyAsDouble(node.val) > hi) {
                traverse(node.left, lo, hi, consumer);
            } else {
                traverseRight(node.left, lo, consumer);
                consumer.accept(node.val);
                traverseLeft(node.right, hi, consumer);
            }
        }
    }

    /**
     * Traverse each node in the given range, where the high end of the range is not a child of this node,
     * but rather far off to the right.
     */
    private void traverseRight(Node node, double lo, Consumer<T> consumer) {
        if (node != null) {
            if (key.applyAsDouble(node.val) >= lo) {
                traverseRight(node.left, lo, consumer);
                consumer.accept(node.val);
            }
            traverseRight(node.right, lo, consumer);
        }
    }

    /**
     * Traverse each node in the given range, where the low end of the range is not a child of this node,
     * but rather far off to the left.
     */
    private void traverseLeft(Node node, double hi, Consumer<T> consumer) {
        if (node != null) {
            traverseLeft(node.left, hi, consumer);
            if (key.applyAsDouble(node.val) <= hi) {
                consumer.accept(node.val);
                traverseLeft(node.right, hi, consumer);
            }
        }
    }

    /**
     * Insert a value into the tree.
     */
    void insert(T val) {
        root = insert(root, val);
    }

    /**
     * Insert a value into this node or one of its children.
     */
    private Node insert(Node node, T val) {
        if (node == null) {
            return new Node(val, RED);
        } else {
            if (key.applyAsDouble(val) < key.applyAsDouble(node.val)) {
                node.left = insert(node.left, val);
            } else {
                node.right = insert(node.right, val);
            }
            if (isRed(node.right) && !isRed(node.left)) {
                node = rotateLeft(node);
            }
            if (isRed(node.left) && isRed(node.left.left)) {
                node = rotateRight(node);
            }
            if (isRed(node.left) && isRed(node.right)) {
                flipColors(node);
            }
        return node;
        }
    }

    // Familiar Red-Black tree operations:

    private boolean isRed(Node node) {
        return node != null && node.color == RED;
    }

    private Node rotateRight(Node node) {
        Node left = node.left;
        node.left = left.right;
        left.right = node;
        left.color = left.right.color;
        left.right.color = RED;
        return left;
    }

    private Node rotateLeft(Node node) {
        Node right = node.right;
        node.right = right.left;
        right.left = node;
        right.color = right.left.color;
        right.left.color = RED;
        return right;
    }

    private void flipColors(Node node) {
        node.color = !node.color;
        node.left.color = !node.left.color;
        node.right.color = !node.right.color;
    }

}
