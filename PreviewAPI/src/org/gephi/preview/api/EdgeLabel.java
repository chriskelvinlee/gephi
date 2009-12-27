package org.gephi.preview.api;

import java.awt.Font;

/**
 * Interface of an edge label.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
public interface EdgeLabel {

    /**
     * Returns the edge label's color.
     *
     * @return the edge label's color
     */
    public Color getColor();

    /**
     * Returns the edge label's position.
     *
     * @return the edge label's position
     */
    public Point getPosition();

    /**
     * Returns the edge label's angle.
     *
     * @return the edge label's angle
     */
    public Float getAngle();

    /**
     * Returns the edge label's value.
     *
     * @return the edge label's value
     */
    public String getValue();

    /**
     * Returns the edge label's font.
     *
     * @return the edge label's font
     */
    public Font getFont();
}
