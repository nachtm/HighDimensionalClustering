package spinacht;

import java.util.Optional;
import java.util.Comparator;
import java.util.function.Consumer;

// snippets of rb-tree from http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
class RangeTree<T> {

  private static final boolean RED = true;
  private static final boolean BLACK = false;

  private class Node {

      private final T value;
      private final Optional<Node> left, right;
      private final boolean color;

      public Node(T value, boolean color, Optional<Node> left, Optional<Node> right) {
        this.value = value;
        this.color = color;
        this.left = left;
        this.right = right;
      }

  }

  private Optional<Node> root = Optional.empty();
  private final Comparator<T> cmp;

  RangeTree(Comparator<T> cmp) {
    this.cmp = cmp;
  }

  void insert(T value) {
    root = insert(root, value);
  }

  private Optional<Node> insert(Optional<Node> node, T value) {
    return Optional.of(
        node.map(n -> {

          final Node m = cmp.compare(value, n.value) < 0
                       ? new Node(n.value, n.color, insert(n.left, value), n.right)
                       : new Node(n.value, n.color, n.left, insert(n.right, value))
                       ;

          // if (isRed(n.right) && !isRed(n.left)) {
          //   n = rotateLeft(n);
          // }
          // if (isRed(n.left) && n.left.map(left -> isRed(left.left)).orElse(false)) {
          //   n = rotateRight(n);
          // }
          // if (isRed(n.left) && isRed(n.right)) {
          //   flipColors(n);
          // }

          return m;

      }).orElse(new Node(value, RED, Optional.empty(), Optional.empty()))
    );
  }

  void forEachInRange(T lo, T hi, Consumer<T> consumer) {
    traverse(root, lo, hi, consumer);
  }

  private void traverse(Optional<Node> node, T lo, T hi, Consumer<T> consumer) {
    node.ifPresent(n -> {
      if (cmp.compare(n.value, lo) < 0) {
        traverse(n.right, lo, hi, consumer);
      } else if (cmp.compare(n.value, hi) > 0) {
        traverse(n.left, lo, hi, consumer);
      } else {
        traverseRight(n.left, lo, consumer);
        consumer.accept(n.value);
        traverseLeft(n.right, lo, consumer);
      }
    });
  }

  private void traverseRight(Optional<Node> node, T lo, Consumer<T> consumer) {
    node.ifPresent(n -> {
      if (cmp.compare(n.value, lo) >= 0) {
        traverseRight(n.left, lo, consumer);
        consumer.accept(n.value);
      }
      traverseRight(n.right, lo, consumer);
    });
  }

  private void traverseLeft(Optional<Node> node, T hi, Consumer<T> consumer) {
    node.ifPresent(n -> {
      traverseLeft(n.left, hi, consumer);
      if (cmp.compare(n.value, hi) <= 0) {
        consumer.accept(n.value);
        traverseLeft(n.right, hi, consumer);
      }
    });
  }

}
