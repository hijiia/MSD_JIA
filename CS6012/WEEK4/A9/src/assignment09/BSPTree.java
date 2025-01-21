package assignment09;

import java.util.ArrayList;
import java.util.List;

public class BSPTree {
    private Node root;

    private static class Node {
        Segment splittingSegment;
        Node rightChild;
        Node leftChild;
        List<Segment> segments;

        Node(Segment segment) {
            this.splittingSegment = segment;
            this.rightChild = null;
            this.leftChild = null;
            this.segments = new ArrayList<>();
            this.segments.add(segment);
        }
    }

    /**
     * Default constructor creating an empty BSP tree.
     */
    public BSPTree() {
        root = null;
    }

    /**
     * constructor that builds the BSP tree from a list of segments.
     * @param segments -- List of segments to construct the tree
     */
    public BSPTree(ArrayList<Segment> segments) {
        if (segments == null || segments.isEmpty()) {
            return;
        }
        root = buildTree(segments);
    }

    /**
     * Recursive method to build the BSP tree using the bulk construction algorithm.
     */
    private Node buildTree(ArrayList<Segment> segments) {
        if (segments.isEmpty()) {
            return null;
        }

        // Choose the first segment as the splitting segment
        Segment splitter = segments.get(0);
        ArrayList<Segment> rightSegments = new ArrayList<>();
        ArrayList<Segment> leftSegments = new ArrayList<>();
        ArrayList<Segment> coplanarSegments = new ArrayList<>();
        coplanarSegments.add(splitter);
        // Partition the remaining segments
        for (int i = 1; i < segments.size(); i++) {
            Segment current = segments.get(i);
            int side = splitter.whichSide(current);
            if (side > 0) {
                rightSegments.add(current);
            } else if (side < 0) {
                leftSegments.add(current);
            } else if (side == 0) {
                // Segment spans the splitting plane - split it
                Segment[] splitParts = splitter.split(current);
                leftSegments.add(splitParts[0]);   // First part is on left side
                rightSegments.add(splitParts[1]);  // Second part is on right side
            }
        }

        // Create the node and recursively build subtrees
        Node node = new Node(splitter);
        node.segments = coplanarSegments;
        node.leftChild = buildTree(leftSegments);
        node.rightChild = buildTree(rightSegments);

        return node;
    }

    /**
     * Insert a new segment into the BSP tree.
     * @param segment Segment to insert
     */
    public void insert(Segment segment) {
        if (root == null) {
            root = new Node(segment);
            return;
        }
        insertRecursive(root, segment);
    }

    /**
     * Recursive helper method for inserting a segment.
     */
    private void insertRecursive(Node node, Segment segment) {
        int side = node.splittingSegment.whichSide(segment);

        if (side > 0) {  // Right side
            if (node.rightChild == null) {
                node.rightChild = new Node(segment);
            } else {
                insertRecursive(node.rightChild, segment);
            }
        } else if (side < 0) {  // Left side
            if (node.leftChild == null) {
                node.leftChild = new Node(segment);
            } else {
                insertRecursive(node.leftChild, segment);
            }
        } else {  // Spanning the splitting plane
            if (!node.splittingSegment.equals(segment)) {
                Segment[] splitParts = node.splittingSegment.split(segment);
                if (node.leftChild == null) {
                    node.leftChild = new Node(splitParts[0]);
                } else {
                    insertRecursive(node.leftChild, splitParts[0]);
                }
                if (node.rightChild == null) {
                    node.rightChild = new Node(splitParts[1]);
                } else {
                    insertRecursive(node.rightChild, splitParts[1]);
                }
            }
        }
    }

    /**
     * Traverse the BSP tree from far to near relative to a point.
     * @param x x-coordinate of the reference point
     * @param y y-coordinate of the reference point
     * @param callback Callback to execute on each segment
     */
    public void traverseFarToNear(double x, double y, SegmentCallback callback) {
        traverseFarToNearRecursive(root, x, y, callback);
    }

    /**
     * Recursive helper method for far-to-near traversal.
     */
    private void traverseFarToNearRecursive(Node node, double x, double y, SegmentCallback callback) {
        if (node == null) {
            return;
        }

        int side = node.splittingSegment.whichSidePoint(x, y);
        if (side > 0) {  // Point is on right side
            traverseFarToNearRecursive(node.leftChild, x, y, callback);
            for (Segment segment : node.segments) {
                callback.callback(segment);
            }
            traverseFarToNearRecursive(node.rightChild, x, y, callback);
        } else {  // Point is on left side or on the splitting plane
            traverseFarToNearRecursive(node.rightChild, x, y, callback);
            for (Segment segment : node.segments) {
                callback.callback(segment);
            }
            traverseFarToNearRecursive(node.leftChild, x, y, callback);
        }
    }

    /**
     * Find any segment in the tree that collides with the query segment.
     * @param query Segment to check for collisions
     * @return A segment that intersects with the query, or null if no collision exists
     */
    public Segment collision(Segment query) {
        return collisionRecursive(root, query);
    }

    /**
     * Recursive helper method for collision detection.
     */
    private Segment collisionRecursive(Node node, Segment query) {
        if (node == null) {
            return null;
        }
        for (Segment segment : node.segments) {
            if (query.intersects(segment)) {
                return segment;
            }
        }

        // Determine which side(s) of the splitting plane to check
        int side = node.splittingSegment.whichSide(query);
        if (side > 0) {  // Query is entirely on right side
            return collisionRecursive(node.rightChild, query);
        } else if (side < 0) {  // Query is entirely on left side
            return collisionRecursive(node.leftChild, query);
        } else {  // Query spans the splitting plane - need to check both sides
            Segment[] splitParts = node.splittingSegment.split(query);
            // Check left side first
            Segment collision = collisionRecursive(node.leftChild, splitParts[0]);
            if (collision != null) {
                return collision;
            }

            // If no collision on left side, check right side
            return collisionRecursive(node.rightChild, splitParts[1]);
        }
    }
}