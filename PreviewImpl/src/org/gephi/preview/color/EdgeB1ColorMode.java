package org.gephi.preview.color;

import org.gephi.preview.api.color.ColorizerType;
import org.gephi.preview.api.color.EdgeChildColorizer;
import org.gephi.preview.api.color.EdgeColorizer;

/**
 *
 * @author jeremy
 */
public class EdgeB1ColorMode
        implements EdgeColorizer, EdgeChildColorizer {

    public ColorizerType getColorizerType() {
        return ColorizerType.EDGE_B1;
    }

//    ColorizedEdge m_client;
//
//    @Override
//    public void setEdge(ColorizedEdge client) {
//        m_client = client;
//    }
//
//    @Override
//    public void setEdgeChild(AbstractEdgeChild client) {
//        setEdge(client);
//    }
//
//    @Override
//    public void colorClient() {
//        m_client.setColor(new InheritedColor(
//                m_client.getNode1().getColorHolder()));
//    }
}
