package org.gephi.neo4j.impl;


import java.io.File;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;
import org.gephi.data.attributes.api.AttributeController;
import org.gephi.data.attributes.api.AttributeOrigin;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.graph.api.Attributes;
import org.gephi.graph.api.Edge;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.neo4j.api.Neo4jImporter;
import org.gephi.project.api.ProjectController;
import org.gephi.project.api.Workspace;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.ProgressTicket;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipExpander;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.PruneEvaluator;
import org.neo4j.graphdb.traversal.ReturnFilter;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.kernel.TraversalFactory;
import org.neo4j.remote.RemoteGraphDatabase;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;


@ServiceProvider(service=Neo4jImporter.class)
public final class Neo4jImporterImpl implements Neo4jImporter, LongTask {
    private GraphDatabaseService graphDB;
    private Map<Long, Integer> idMapper;
    private GraphModel graphModel;
    private ProgressTicket progressTicket;
    private boolean cancelImport;//TODO finish implementing canceling task


    @Override
    public boolean cancel() {
        cancelImport = true;
        return true;
    }

    @Override
    public void setProgressTicket(ProgressTicket progressTicket) {
        cancelImport = false;
        this.progressTicket = progressTicket;
    }

    @Override
    public void importLocal(File neo4jDirectory) {
        progressTicket.setDisplayName("Importing data from local Neo4j database");
        progressTicket.start();

        graphDB = new EmbeddedGraphDatabase(neo4jDirectory.getAbsolutePath());
        doImport();
    }

    @Override
    public void importRemote(String resourceURI) {
        progressTicket.setDisplayName("Importing data from remote Neo4j database");
        progressTicket.start();

        try {
            graphDB = new RemoteGraphDatabase(resourceURI);
            doImport();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void importRemote(String resourceURI, String login, String password) {
        progressTicket.setDisplayName("Importing data from remote Neo4j database");
        progressTicket.start();

        try {
            graphDB = new RemoteGraphDatabase(resourceURI, login, password);
            doImport();
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void doImport() {
        idMapper = new HashMap<Long, Integer>();

        Transaction transaction = graphDB.beginTx();
        try {
            importGraph();
            transaction.success();
        }
        finally {
            transaction.finish();
        }

        progressTicket.finish();
        graphDB.shutdown();
    }

    private void importGraph() {
        TraversalDescription traversalDescription = TraversalFactory.createTraversalDescription();
        RelationshipExpander relationshipExpander = TraversalFactory.expanderForAllTypes();

        org.neo4j.graphdb.Node referenceNode = graphDB.getReferenceNode();

        Traverser traverser = 
            traversalDescription.depthFirst()
                                .expand(relationshipExpander)
                                .filter(ReturnFilter.ALL)
                                .prune(PruneEvaluator.NONE)
                                .traverse(referenceNode);

        createNewProject();
        initializeGraphModel();
        //TODO solve problem with projects and workspace, how to treat canceling task?

        // the algorithm traverses through all relationships
        for (Relationship neoRelationship : traverser.relationships()) {
            if (cancelImport)
                break;

            processRelationship(neoRelationship);
        }

        if (!cancelImport)
            showCurrentWorkspace();
    }

    private void createNewProject() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);
        pc.newProject();
    }

    private void initializeGraphModel() {
        GraphController graphController = Lookup.getDefault().lookup(GraphController.class);
        graphModel = graphController.getModel();
    }

    private void showCurrentWorkspace() {
        ProjectController pc = Lookup.getDefault().lookup(ProjectController.class);

        Workspace workspace = pc.getCurrentWorkspace();
        if (workspace == null)
            workspace = pc.newWorkspace(pc.getCurrentProject());
        pc.openWorkspace(workspace);
    }

    private void processRelationship(Relationship neoRelationship) {
        GephiHelper gephiHelper = new GephiHelper();

        org.gephi.graph.api.Node gephiStartNode =
                gephiHelper.createGephiNodeFromNeoNode(neoRelationship.getStartNode());
        org.gephi.graph.api.Node gephiEndNode =
                gephiHelper.createGephiNodeFromNeoNode(neoRelationship.getEndNode());

        gephiHelper.createGephiEdge(gephiStartNode, gephiEndNode, neoRelationship);
    }

    private class GephiHelper {
        public GephiHelper() {}

        /**
         * Creates Gephi node representation from Neo4j node and all its property data. If Gephi node
         * doesn't already exist, it will be created with all attached data from Neo4j node, otherwise
         * it will not be created again.
         *
         * @param neoNode Neo4j node
         * @return Gephi node
         */
        public org.gephi.graph.api.Node createGephiNodeFromNeoNode(org.neo4j.graphdb.Node neoNode) {
            Integer gephiNodeId = idMapper.get(neoNode.getId());

            org.gephi.graph.api.Node gephiNode;
            if (gephiNodeId != null)
                gephiNode = graphModel.getGraph().getNode(gephiNodeId);
            else {
                gephiNode = graphModel.factory().newNode();
                graphModel.getGraph().addNode(gephiNode);

                AttributeController attributeController =
                            Lookup.getDefault().lookup(AttributeController.class);

                for (String neoPropertyKey : neoNode.getPropertyKeys()) {
                    Object neoPropertyValue = neoNode.getProperty(neoPropertyKey);

                    AttributeTable nodeTable = attributeController.getModel().getNodeTable();
                    nodeTable.addColumn(neoPropertyKey, AttributeType.parse(neoPropertyValue), AttributeOrigin.PROPERTY);

                    Attributes attributes = gephiNode.getNodeData().getAttributes();
                    attributes.setValue(neoPropertyKey, neoPropertyValue);
                }

                idMapper.put(neoNode.getId(), gephiNode.getId());
            }

            return gephiNode;
        }

        /**
         * Creates Gephi edge betweeen two Gephi nodes. Graph is traversing through all relationships
         * (edges), so for every Neo4j relationship a Gephi edge will be created.
         *
         * @param startGephiNode  start Gephi node
         * @param endGephiNode    end Gephi node
         * @param neoRelationship Neo4j relationship
         */
        public void createGephiEdge(org.gephi.graph.api.Node startGephiNode,
                                           org.gephi.graph.api.Node endGephiNode,
                                           Relationship neoRelationship) {
            Edge gephiEdge = graphModel.factory().newEdge(startGephiNode, endGephiNode);
            graphModel.getGraph().addEdge(gephiEdge);

            AttributeController attributeController =
                        Lookup.getDefault().lookup(AttributeController.class);

            for (String neoPropertyKey : neoRelationship.getPropertyKeys()) {
                Object neoPropertyValue = neoRelationship.getProperty(neoPropertyKey);

                AttributeTable edgeTable = attributeController.getModel().getEdgeTable();
                edgeTable.addColumn(neoPropertyKey, AttributeType.parse(neoPropertyValue), AttributeOrigin.PROPERTY);

                Attributes attributes = gephiEdge.getEdgeData().getAttributes();
                attributes.setValue(neoPropertyKey, neoPropertyValue);
            }
        }
    }
}
