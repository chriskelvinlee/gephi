package org.gephi.neo4j.api;


import java.util.Collection;
import org.neo4j.graphdb.GraphDatabaseService;


/**
 * Imports data from local or remote Neo4j database.
 *
 * @author Martin Škurla
 */
public interface Neo4jImporter {
    void importDatabase(GraphDatabaseService graphDB);

    void importDatabase(GraphDatabaseService graphDB, long startNodeId, TraversalOrder order, int maxDepth);

    void importDatabase(GraphDatabaseService graphDB, long startNodeId, TraversalOrder order, int maxDepth,
            Collection<RelationshipDescription> relationshipDescriptions);

    void importDatabase(GraphDatabaseService graphDB, long startNodeId, TraversalOrder order, int maxDepth,
            Collection<RelationshipDescription> relationshipDescriptions, Collection<FilterDescription> filterDescriptions,
            boolean restrictMode, boolean matchCase);
}
