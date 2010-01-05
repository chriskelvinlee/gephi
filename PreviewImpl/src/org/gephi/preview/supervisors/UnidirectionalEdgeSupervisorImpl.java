package org.gephi.preview.supervisors;

import java.awt.Font;
import org.gephi.preview.updaters.EdgeB1ColorMode;
import org.gephi.preview.updaters.ParentColorMode;

/**
 * Unidirectional edge supervisor implementation.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public class UnidirectionalEdgeSupervisorImpl extends DirectedEdgeSupervisorImpl {

    /**
     * Constructor.
     *
     * Initializes default values.
     */
    public UnidirectionalEdgeSupervisorImpl() {
        curvedFlag = false;
        colorizer = new EdgeB1ColorMode();
        showLabelsFlag = true;
        shortenLabelsFlag = false;
        labelMaxChar = 10;
        baseLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 10);
        labelColorizer = new ParentColorMode();
        showMiniLabelsFlag = true;
        shortenMiniLabelsFlag = false;
        miniLabelMaxChar = 10;
        miniLabelFont = new Font(Font.SANS_SERIF, Font.PLAIN, 8);
        miniLabelAddedRadius = 15f;
        miniLabelColorizer = new ParentColorMode();
        showArrowsFlag = true;
        arrowAddedRadius = 65f;
        arrowSize = 20f;
        arrowColorizer = new ParentColorMode();
    }
}
