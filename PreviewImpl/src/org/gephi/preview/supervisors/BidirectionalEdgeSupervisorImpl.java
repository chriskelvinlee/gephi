package org.gephi.preview.supervisors;

import java.awt.Font;
import org.gephi.preview.updaters.EdgeBothBColorMode;
import org.gephi.preview.updaters.ParentColorMode;

/**
 * Bidirectional edge supervisor implementation.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public class BidirectionalEdgeSupervisorImpl extends DirectedEdgeSupervisorImpl {

    /**
     * Constructor.
     *
     * Initializes default values.
     */
    public BidirectionalEdgeSupervisorImpl() {
        defaultValues();
    }

    public void defaultValues() {
        curvedFlag = false;
        colorizer = new EdgeBothBColorMode();
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
