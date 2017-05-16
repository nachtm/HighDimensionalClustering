package spinacht.dbscan;

import java.util.Collection;
import java.util.Comparator;
import java.util.function.Consumer;

// snippets of rb-tree from http://algs4.cs.princeton.edu/33balanced/RedBlackBST.java.html
class RangeTree<T> {

  private static final boolean RED   = true;
  private static final boolean BLACK = false;

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
  private final Comparator<T> comp;

  RangeTree(Comparator<T> comp) {
    this.comp = comp;
  }

  void forEachInRange(T lo, T hi, Consumer<T> consumer) {
    traverse(root, lo, hi, consumer);
  }

  private void traverse(Node node, T lo, T hi, Consumer<T> consumer) {
    if (node != null) {
      if (comp.compare(node.val, lo) < 0) {
        traverse(node.right, lo, hi, consumer);
      } else if (comp.compare(node.val, hi) > 0) {
        traverse(node.left, lo, hi, consumer);
      } else {
        traverseRight(node.left, lo, consumer);
        consumer.accept(node.val);
        traverseLeft(node.right, hi, consumer);
      }
    }
  }

  private void traverseRight(Node node, T lo, Consumer<T> consumer) {
    if (node != null) {
      if (comp.compare(node.val, lo) >= 0) {
        traverseRight(node.left, lo, consumer);
        consumer.accept(node.val);
      }
      traverseRight(node.right, lo, consumer);
    }
  }

  private void traverseLeft(Node node, T hi, Consumer<T> consumer) {
    if (node != null) {
      traverseLeft(node.left, hi, consumer);
      if (comp.compare(node.val, hi) <= 0) {
        consumer.accept(node.val);
        traverseLeft(node.right, hi, consumer);
      }
    }
  }

  void insert(T val) {
    root = insert(root, val);
  }

  private Node insert(Node node, T val) {

    if (node == null) {

      return new Node(val, RED);

    } else {

      if (comp.compare(val, node.val) < 0) {
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
