/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.partition;

import com.google.common.collect.ArrayListMultimap;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.Node;
import org.gephi.partition.api.EdgePartition;
import org.gephi.partition.api.NodePartition;
import org.gephi.partition.api.Part;
import org.gephi.partition.api.Partition;

/**
 *
 * @author Mathieu Bastian
 */
public class PartitionFactory {

    public static boolean isNodePartitionColumn(AttributeColumn column, Graph graph) {
        if (column.getAttributeType().equals(AttributeType.STRING) ||
                column.getAttributeType().equals(AttributeType.BOOLEAN) ||
                column.getAttributeType().equals(AttributeType.INT)) {
            Set values = new HashSet();
            int nonNullvalues = 0;
            for (Node n : graph.getNodes()) {
                Object value = n.getNodeData().getAttributes().getValue(column.getIndex());
                if (value != null) {
                    nonNullvalues++;
                }
                values.add(value);
            }
            if (values.size() < 2f / 3f * nonNullvalues) {      //If #different values is < 2:3 of total non-null values
                return true;
            }
        }
        return false;
    }

    public static boolean isEdgePartitionColumn(AttributeColumn column, Graph graph) {
        if (column.getAttributeType().equals(AttributeType.STRING) ||
                column.getAttributeType().equals(AttributeType.BOOLEAN) ||
                column.getAttributeType().equals(AttributeType.INT)) {
            Set values = new HashSet();
            int nonNullvalues = 0;
            for (Edge n : graph.getEdges()) {
                Object value = n.getEdgeData().getAttributes().getValue(column.getIndex());
                if (value != null) {
                    nonNullvalues++;
                }
                values.add(value);
            }
            if (values.size() < 2f / 3f * nonNullvalues) {      //If #different values is < 2:3 of total non-null values
                return true;
            }
        }
        return false;
    }

    public static NodePartition createNodePartition(AttributeColumn column) {
        return new NodePartitionImpl(column);
    }

    public static EdgePartition createEdgePartition(AttributeColumn column) {
        return new EdgePartitionImpl(column);
    }

    public static void buildNodePartition(NodePartition partition, Graph graph) {

        NodePartitionImpl partitionImpl = (NodePartitionImpl) partition;
        ArrayListMultimap<Object, Node> multimap = ArrayListMultimap.create();
        for (Node n : graph.getNodes()) {
            Object value = n.getNodeData().getAttributes().getValue(partitionImpl.column.getIndex());
            multimap.put(value, n);
        }

        PartImpl<Node>[] parts = new PartImpl[multimap.keySet().size()];
        Map<Object, Collection<Node>> map = multimap.asMap();
        int i = 0;
        for (Entry<Object, Collection<Node>> entry : map.entrySet()) {
            PartImpl<Node> part = new PartImpl<Node>(partition, entry.getKey(), entry.getValue().toArray(new Node[0]));
            parts[i] = part;
        }
        partitionImpl.setParts(parts);
    }

    public static void buildEdgePartition(EdgePartition partition, Graph graph) {
        EdgePartitionImpl partitionImpl = (EdgePartitionImpl) partition;

        ArrayListMultimap<Object, Edge> multimap = ArrayListMultimap.create();
        for (Edge n : graph.getEdges()) {
            Object value = n.getEdgeData().getAttributes().getValue(partitionImpl.column.getIndex());
            multimap.put(value, n);
        }

        PartImpl<Edge>[] parts = new PartImpl[multimap.keySet().size()];
        Map<Object, Collection<Edge>> map = multimap.asMap();
        int i = 0;
        for (Entry<Object, Collection<Edge>> entry : map.entrySet()) {
            PartImpl<Edge> part = new PartImpl<Edge>(partition, entry.getKey(), entry.getValue().toArray(new Edge[0]));
            parts[i] = part;
        }
        partitionImpl.setParts(parts);
    }

    private static class NodePartitionImpl implements NodePartition {

        private HashMap<Node, Part<Node>> nodeMap;
        private PartImpl<Node>[] parts;
        private AttributeColumn column;

        public NodePartitionImpl(AttributeColumn column) {
            this.column = column;
            nodeMap = new HashMap<Node, Part<Node>>();
            parts = new PartImpl[0];
        }

        public int getPartsCount() {
            return parts.length;
        }

        public Part<Node>[] getParts() {
            return parts;
        }

        public Map<Node, Part<Node>> getMap() {
            return nodeMap;
        }

        public Part<Node> getPart(Node element) {
            return nodeMap.get(element);
        }

        public void setParts(PartImpl<Node>[] parts) {
            this.parts = parts;
            for (PartImpl<Node> p : parts) {
                for (Node n : p.objects) {
                    nodeMap.put(n, p);
                }
            }
        }

        @Override
        public String toString() {
            return column.getTitle();
        }
    }

    private static class EdgePartitionImpl implements EdgePartition {

        private HashMap<Edge, Part<Edge>> edgeMap;
        private PartImpl<Edge>[] parts;
        private AttributeColumn column;

        public EdgePartitionImpl(AttributeColumn column) {
            this.column = column;
            edgeMap = new HashMap<Edge, Part<Edge>>();
            parts = new PartImpl[0];
        }

        public int getPartsCount() {
            return parts.length;
        }

        public Part<Edge>[] getParts() {
            return parts;
        }

        public Map<Edge, Part<Edge>> getMap() {
            return edgeMap;
        }

        public Part<Edge> getPart(Edge element) {
            return edgeMap.get(element);
        }

        public void setParts(PartImpl<Edge>[] parts) {
            this.parts = parts;
            for (PartImpl<Edge> p : parts) {
                for (Edge n : p.objects) {
                    edgeMap.put(n, p);
                }
            }
        }

        @Override
        public String toString() {
            return column.getTitle();
        }
    }

    private static class PartImpl<Element> implements Part<Element> {

        private Partition<Element> partition;
        private Element[] objects;
        private Object value;

        public PartImpl(Partition<Element> partition, Object value, Element[] objects) {
            this.partition = partition;
            this.value = value;
            this.objects = objects;
        }

        public Element[] getObjects() {
            return objects;
        }

        public Object getValue() {
            return value;
        }

        public String getDisplayName() {
            return value.toString();
        }

        public boolean isInPart(Element element) {
            return partition.getPart(element) == this;
        }
    }
}
