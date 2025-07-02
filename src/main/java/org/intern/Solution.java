// package org.intern;

import java.util.*;

// Capacity = k

public class Solution {

    class Pair {
        String value;
        DoublyLinkedList.Node node;

        public Pair(String value, DoublyLinkedList.Node node) {
            this.value = value;
            this.node = node;
        }
    }

    private HashMap<Integer, Pair> map;
    private DoublyLinkedList capacityQueue;
    private int capacity;

    public static void main(String[] args) {
        Solution lruCache = new Solution(3);

        lruCache.put(1, "Hello");

        assert lruCache.get(1).equals("Hello");
        assert lruCache.get(2) == null;

        // Check capacity
        lruCache.put(2, "hi");
        lruCache.put(3, "good bye");

        assert lruCache.get(2).equals("hi");
        assert lruCache.get(3).equals("good bye");
        assert lruCache.get(1).equals("Hello");

        // Check key override
        lruCache.put(2, "no hi");
        assert lruCache.get(2).equals("no hi");

        // Check for staleness refresh.
        lruCache.get(3);
        lruCache.put(1, "2 is removed");
        lruCache.put(4, "askdjslakj");

        assert lruCache.get(2) == null;

        System.out.println("Tests run");
    }

    public Solution(int capacity) {
        this.capacity = capacity;
        map = new HashMap<>();
        capacityQueue = new DoublyLinkedList();
    }

    // Runtime
    // Case 1 override another key:
    // O(1)
    // Case 2 add a new key
    // O(1)
    public void put(int key, String value) {
        // Handle overriding keys

        if (map.containsKey(key)) {
            Pair oldMap = map.get(key);
            // Value, Node
            map.put(key, new Pair(value, oldMap.node));
            capacityQueue.refreshStaleness(oldMap.node);
            return;
        }

        while (capacityQueue.size() >= capacity) {
            int oldestKey = capacityQueue.poll();
            map.remove(oldestKey);
        }

        DoublyLinkedList.Node newNode = capacityQueue.add(key);
        map.put(key, new Pair(value, newNode));
    }

    // O(1)
    public String get(int key) {
        if (!map.containsKey(key)) {
            return null;
        }

        Pair mappedVal = map.get(key);

        capacityQueue.refreshStaleness(mappedVal.node);
        return mappedVal.value;
    }

}

class DoublyLinkedList {
    private Node head;
    private Node tail;
    private int length;

    class Node {
        public int value;
        Node next;
        Node prev;

        public Node(int val) {
            value = val;
            next = null;
            prev = null;
        }
    }

    public DoublyLinkedList() {
        length = 0;
        head = null;
        tail = null;
    }

    public Node add(int val) {
        Node newNode = new Node(val);

        // The linked list is empty

        if (length == 0) {
            head = newNode;
            tail = newNode;
            length = 1;
            return newNode;
        }
        // It has at least 1 element
        newNode.next = head;
        head.prev = newNode;
        head = newNode;
        length++;

        return newNode;
    }

    public int poll() {
        if (length == 0) {
            throw new RuntimeException("Empty list");
        }

        // Theres 1 element

        if (length == 1) {
            Node returnValue = head;

            head = null;
            tail = null;
            length = 0;

            return returnValue.value;
        }

        Node returnValue = tail;
        tail = tail.prev;
        tail.next = null;
        length--;

        return returnValue.value;
    }

    public int size() {
        return length;
    }

    public void refreshStaleness(Node toBeRefreshed) {

        if (length == 1 || head == toBeRefreshed)
            return;

        Node prev = toBeRefreshed.prev;
        Node next = toBeRefreshed.next;

        if (prev != null) {
            prev.next = next;
        }

        if (next != null) {
            next.prev = prev;
        }

        toBeRefreshed.prev = null;
        toBeRefreshed.next = head;
        head.prev = toBeRefreshed;
        head = toBeRefreshed;

        if (tail == toBeRefreshed) {
            tail = prev;
        }
    }
}
