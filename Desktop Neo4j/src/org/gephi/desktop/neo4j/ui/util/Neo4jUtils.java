package org.gephi.desktop.neo4j.ui.util;

import java.io.File;
import java.net.URISyntaxException;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.kernel.EmbeddedGraphDatabase;
import org.neo4j.remote.RemoteGraphDatabase;

/**
 *
 * @author Martin Škurla
 */
public class Neo4jUtils {
    private Neo4jUtils() {}

    public static GraphDatabaseService localDatabase(File neo4jDirectory) {
        return new EmbeddedGraphDatabase(neo4jDirectory.getAbsolutePath());
    }

    public static GraphDatabaseService remoteDatabase(String resourceURI, String login, String password) {
        try {
            if (login == null && password == null)
                return new RemoteGraphDatabase(resourceURI);
            else
                return new RemoteGraphDatabase(resourceURI, login, password);
        }
        catch (URISyntaxException e) {
            e.printStackTrace();
            return null;
        }
    }
}
