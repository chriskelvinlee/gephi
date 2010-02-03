/*
Copyright 2010 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke, Patrick J. McSweeney
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.ui.timeline.plugin.drawers.minimal;

import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.Date;
import java.util.Locale;
import javax.swing.JPanel;
import javax.swing.event.ChangeEvent;
import org.gephi.timeline.api.TimelineAnimator;
import org.gephi.timeline.api.TimelineAnimatorListener;
import org.gephi.timeline.api.TimelineModel;
import org.gephi.timeline.api.TimelineModelListener;
import org.gephi.timeline.spi.TimelineDrawer;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.joda.time.Hours;
import org.joda.time.Interval;
import org.joda.time.Months;
import org.joda.time.Period;
import org.joda.time.PeriodType;
import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jbilcke
 */
@ServiceProvider(service = TimelineDrawer.class)
public class MinimalDrawer extends JPanel
        implements TimelineDrawer,
        MouseListener,
        MouseMotionListener,
        TimelineAnimatorListener,
        TimelineModelListener {

    private static final long serialVersionUID = 1L;
    private MinimalDrawerSettings settings = new MinimalDrawerSettings();

    /** Creates new form MinimalDrawer */
    public MinimalDrawer() {

        // initComponents();
        //setSize(new Dimension(800, 38));
        //setPreferredSize(new Dimension(800, 38));

        System.out.println("width: " + getWidth());
        System.out.println("height: " + getHeight());
        setVisible(true);
        addMouseMotionListener(this);
        addMouseListener(this);
        // setEnabled(true);

    }
    private TimelineModel model = null;
    private TimelineAnimator animator = null;

    public void setModel(TimelineModel model) {
        if (model == null) {
            return;
        }
        if (model != this.model) {
            if (this.model != null) {
            }
            this.model = model;
        }
    }

    public TimelineModel getModel() {
        return model;
    }

    public void setAnimator(TimelineAnimator animator) {
        this.animator = animator;
    }

    public TimelineAnimator getAnimator() {
        return animator;
    }
    private Integer mousex = null;
    private static Cursor CURSOR_DEFAULT = new Cursor(Cursor.DEFAULT_CURSOR);
    private static Cursor CURSOR_LEFT_HOOK = new Cursor(Cursor.E_RESIZE_CURSOR);
    private static Cursor CURSOR_CENTRAL_HOOK = new Cursor(Cursor.MOVE_CURSOR);
    private static Cursor CURSOR_RIGHT_HOOK = new Cursor(Cursor.W_RESIZE_CURSOR);

    private static Locale LOCALE = Locale.FRENCH;
    
    public void mouseClicked(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
    }

    public void mousePressed(MouseEvent e) {
        if (model == null) {
            return;
        }

        int x = e.getX();
        float w = getWidth();
        int r = 16;//skin.getSelectionHookSideLength();

        // SELECTED ZONE BEGIN POSITION, IN PIXELS
        int sf = (int) (model.getFromFloat() * (double) w);

        // SELECTED ZONE END POSITION, IN PIXELS
        int st = (int) (model.getToFloat() * (double) w);

        if (currentState == TimelineState.IDLE) {
            if (inRange(x, sf - 1, sf + r + 1)) {
                highlightedComponent = HighlightedComponent.LEFT_HOOK;
                currentState = TimelineState.RESIZE_FROM;
            } else if (inRange(x, sf + r, st - r)) {
                highlightedComponent = HighlightedComponent.CENTER_HOOK;
                currentState = TimelineState.MOVING;
            } else if (inRange(x, st - r - 1, st + 1)) {
                highlightedComponent = HighlightedComponent.RIGHT_HOOK;
                currentState = TimelineState.RESIZE_TO;
            }
        }
    }

    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        mousex = e.getX();
    }

    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet.");
        if (currentState == TimelineState.IDLE) {
            highlightedComponent = HighlightedComponent.NONE;
            repaint();
        }
    }

    public void timelineAnimatorChanged(ChangeEvent event) {
        repaint();
    }

    public void timelineModelChanged(ChangeEvent event) {

        if (event != null && event.getSource() != null) {
            repaint();
        }
    }

    public enum TimelineLevel {

        MILLISECOND,
        SECOND,
        MINUTE,
        HOUR,
        DAY,
        WEEK,
        MONTH,
        YEAR,
        YEAR_10,
        YEAR_20,
        YEAR_50,
        YEAR_100
    }

    public enum TimelineState {

        IDLE,
        MOVING,
        RESIZE_FROM,
        RESIZE_TO
    }
    TimelineState currentState = TimelineState.IDLE;

    public enum HighlightedComponent {

        NONE,
        LEFT_HOOK,
        RIGHT_HOOK,
        CENTER_HOOK
    }
    HighlightedComponent highlightedComponent = HighlightedComponent.NONE;

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        setMinimumSize(new java.awt.Dimension(300, 28));
        setLayout(new java.awt.BorderLayout());
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        int tmMarginTop = 2;
        int tmMarginBottom = 4;

        int width = getWidth();
        int height = getHeight();
        settings.update(width, height);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setBackground(settings.background.top);
        g2d.setPaint(settings.background.paint);
        g2d.fillRect(0, tmMarginTop + 1, width, height - tmMarginBottom - 2);

        if (!this.isEnabled() || model == null) {
            return;
        }

        g2d.setRenderingHints(settings.renderingHints);

        // SELECTED ZONE BEGIN POSITION, IN PIXELS
        int sf = (int) (model.getFromFloat() * (double) width);

        // SELECTED ZONE END POSITION, IN PIXELS
        int st = (int) (model.getToFloat() * (double) width);

        // VISIBLE HOOK (THE LITTLE GREEN RECTANGLE ON EACH SIDE) WIDTH
        int vhw = settings.selection.visibleHookWidth;

        // SELECTED ZONE WIDTH, IN PIXELS
        int sw = st - sf;

        if (highlightedComponent != HighlightedComponent.NONE) {
            g2d.setPaint(settings.selection.mouseOverPaint);
            switch (highlightedComponent) {
                case LEFT_HOOK:
                    g2d.fillRect(
                            sf,
                            tmMarginTop,
                            vhw,
                            height - tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            sf + vhw,
                            tmMarginTop,
                            sw - vhw,
                            height - tmMarginBottom - 1);
                    break;
                case CENTER_HOOK:
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            sf,
                            tmMarginTop,
                            vhw,
                            height - tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.mouseOverPaint);
                    g2d.fillRect(
                            sf + vhw,
                            tmMarginTop,
                            sw - vhw * 2,
                            height - tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            st - vhw,
                            tmMarginTop,
                            vhw,
                            height - tmMarginBottom - 1);
                    break;
                case RIGHT_HOOK:
                    g2d.setPaint(settings.selection.paint);
                    g2d.fillRect(
                            sf,
                            tmMarginTop,
                            sw - vhw,
                            height - tmMarginBottom - 1);
                    g2d.setPaint(settings.selection.mouseOverPaint);
                    g2d.fillRect(
                            st - vhw,
                            tmMarginTop,
                            vhw,
                            height - tmMarginBottom - 1);
                    break;
            }
        } else {
            g2d.setPaint(settings.selection.paint);
            g2d.fillRect(sf, tmMarginTop, sw, height - tmMarginBottom - 1);
        }

        paintTimelineForDateTime(g2d);

        g2d.setColor(settings.defaultStrokeColor);
        g2d.drawRect(sf, tmMarginTop, sw, height - tmMarginBottom - 1);

    }

    private void paintTimelineForDateTime(Graphics2D g2d) {
        long min = (long) model.getMinValue();
        long max = (long) model.getMaxValue();
        if (max <= min && min < 0 && max <= 0) {
            return;
        }

        //DateTime dtFrom = new DateTime(1455, 1, 1, 1, 1, 1, 1);
        //DateTime dtTo = new DateTime(1960, 2, 10, 1, 1, 1, 1);
        paintUpperRulerForInterval(g2d,
                new DateTime(new Date(min)),
                new DateTime(new Date(max)));
        // paintHoverTip(g2d);
    }

    private void paintHoverTip(Graphics2D g2d) {
        g2d.setFont(settings.informations.font);
        g2d.setColor(settings.informations.fontColor);
        int leftMargin = settings.informations.leftMargin;
        int topMargin = settings.informations.topMargin + settings.informations.fontSize;
        int width = getWidth();
        int height = getHeight();
        // TODO take these from the model
        DateTime dtFrom = new DateTime(1987, 1, 1, 1, 1, 1, 1);
        DateTime dtTo = new DateTime(1987, 2, 10, 1, 1, 1, 1);
        Interval interval = new Interval(dtFrom, dtTo);
    }

    private void paintUpperRulerForInterval(Graphics2D g2d, DateTime dtFrom, DateTime dtTo) {

        g2d.setFont(settings.informations.font);
        g2d.setColor(settings.informations.fontColor);
        int leftMargin = settings.informations.leftMargin;
        int textTopPosition = settings.informations.textTopPosition;
        int width = getWidth();
        int height = getHeight();
        // TODO take these from the model

        Interval interval = new Interval(dtFrom, dtTo);

        Period p = interval.toPeriod(PeriodType.days());
        // try to determine length if we had to show milliseconds

        int n = p.getDays();
        int unitSize = (int) (settings.informations.fontMetrics.getStringBounds("wednesday  ", null)).getWidth();
        if (n < (width / unitSize)) {
            //System.out.println("jour");
            for (int i = 0; i < n; i++) {
                g2d.drawString(dtFrom.plusDays(i).dayOfMonth().getAsText(LOCALE),
                        leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Hours.hoursBetween(dtFrom.plusDays(i), dtFrom.plusDays(i + 1)).getHours());
            }

            return;
        }


        unitSize = (int) (settings.informations.fontMetrics.getStringBounds("wed ", null)).getWidth();
        if (n < (width / unitSize)) {
            //System.out.println("jou");
            for (int i = 0; i < n; i++) {
                g2d.drawString(dtFrom.plusDays(i).dayOfMonth().getAsShortText(LOCALE),
                        leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);

                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Hours.hoursBetween(dtFrom.plusDays(i), dtFrom.plusDays(i + 1)).getHours());
            }
            return;
        }


        p = interval.toPeriod(PeriodType.days());
        n = p.getDays();
        unitSize = (int) (settings.informations.fontMetrics.getStringBounds("30", null)).getWidth();
        if (n < (width / unitSize)) {
            //System.out.println("j");
            for (int i = 0; i < n; i++) {
                g2d.drawString("" + (dtFrom.getDayOfMonth() + i),
                        leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);

                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Hours.hoursBetween(dtFrom.plusDays(i), dtFrom.plusDays(i + 1)).getHours());
            }
            return;
        }

        p = interval.toPeriod(PeriodType.months());
        n = p.getMonths();
        unitSize = (int) (settings.informations.fontMetrics.getStringBounds("September  ", null)).getWidth();
        if (n < (width / unitSize)) {
            //System.out.println("mois");
            for (int i = 0; i < n; i++) {
                g2d.drawString(dtFrom.plusMonths(i).monthOfYear().getAsText(LOCALE),
                        leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);

                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Days.daysBetween(dtFrom.plusMonths(i), dtFrom.plusMonths(i + 1)).getDays());
            }
            return;
        }


        unitSize = (int) (settings.informations.fontMetrics.getStringBounds("dec ", null)).getWidth();
        if (n < (width / unitSize)) {
            //System.out.println("mo");
            for (int i = 0; i < n; i++) {
                g2d.drawString(dtFrom.plusMonths(i).monthOfYear().getAsShortText(LOCALE),
                        leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Days.daysBetween(dtFrom.plusMonths(i), dtFrom.plusMonths(i + 1)).getDays());
            }
            return;
        }

        unitSize = (int) (settings.informations.fontMetrics.getStringBounds("29 ", null)).getWidth();
        if (n < (width / unitSize)) {
            //System.out.println("m");
            for (int i = 0; i < n; i++) {
                g2d.drawString("" + (dtFrom.getMonthOfYear() + i), leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Days.daysBetween(dtFrom.plusMonths(i), dtFrom.plusMonths(i + 1)).getDays());
            }
            return;
        }

        p = interval.toPeriod(PeriodType.years());
        n = p.getYears();
        unitSize = (int) (settings.informations.fontMetrics.getStringBounds("1980 ", null)).getWidth();
        if (n < (width / unitSize)) {
            //System.out.println("year");
            for (int i = 0; i < n; i++) {
                g2d.drawString("" + (dtFrom.getYear() + i), leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Months.monthsBetween(dtFrom.plusYears(i), dtFrom.plusYears(i + 1)).getMonths());
            }
            return;
        }

        int group = 10;
        n = p.getYears() / group;
        if (n < (width / unitSize)) {
            //System.out.println("10 years");
            for (int i = 0; i < n; i++) {
                g2d.drawString("" + (dtFrom.getYear() + i * group), leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Months.monthsBetween(dtFrom.plusYears(i * group), dtFrom.plusYears((i + 1) * group)).getMonths());
            }
            return;
        }
        group = 20;
        n = p.getYears() / group;
        if (n < (width / unitSize)) {
            //System.out.println("20 years");
            for (int i = 0; i < n; i++) {
                g2d.drawString("" + (dtFrom.getYear() + i * group), leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Months.monthsBetween(dtFrom.plusYears(i * group), dtFrom.plusYears((i + 1) * group)).getMonths());
            }
            return;
        }
        group = 50;
        n = p.getYears() / group;
        if (n < (width / unitSize)) {
            //System.out.println("50 years");
            for (int i = 0; i < n; i++) {
                g2d.drawString("" + (dtFrom.getYear() + i * group), leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Months.monthsBetween(dtFrom.plusYears(i * group), dtFrom.plusYears((i + 1) * group)).getMonths());
            }
            return;
        }
        group = 100;
        n = p.getYears() / group;
        if (n / 100 < (width / unitSize)) {
            //System.out.println("100 years");
            for (int i = 0; i < n; i++) {
                g2d.drawString("" + (dtFrom.getYear() + i * group), leftMargin + 2 + i * (width / n),
                        textTopPosition);
                g2d.drawLine(leftMargin + i * (width / n), 2, leftMargin + i * (width / n), height - settings.informations.textBottomMargin);
                paintSmallGraduations(g2d,
                        leftMargin + i * (width / n),
                        leftMargin + (i + 1) * (width / n),
                        Months.monthsBetween(dtFrom.plusYears(i * group), dtFrom.plusYears((i + 1) * group)).getMonths());
            }
        }
        return;
    }

    private void paintSmallGraduations(Graphics2D g2d, int x, int y, int numOfGrads) {
        int width = y - x;
        int height = getHeight();
        int leftMargin = x;
        int topMargin = height - settings.informations.fontSize - 2;
        int unitSize = 3;

        if (numOfGrads > (width / unitSize)) {
            return;
        }
        for (int i = 1; i < numOfGrads; i++) {
            int xi = leftMargin + i * (width / numOfGrads);
            g2d.drawLine(xi, topMargin, xi, height - settings.informations.textBottomMargin);
        }
    }

    private boolean inRange(int x, int a, int b) {
        return (a < x && x < b);
    }

    public void mouseReleased(MouseEvent evt) {

        mousex = null;
        //highlightedComponent = HighlightedComponent.NONE;
        currentState = TimelineState.IDLE;
        this.getParent().repaint(); // so it will repaint upper and bottom panes

    }

    public void mouseMoved(MouseEvent evt) {
    if (model==null) return;

        //System.out.println("mouse moved");
        int x = evt.getX();
        float w = getWidth();
        int r = settings.selection.visibleHookWidth;

        // SELECTED ZONE BEGIN POSITION, IN PIXELS
        int sf = (int) (model.getFromFloat() * (double) w);

        // SELECTED ZONE END POSITION, IN PIXELS
        int st = (int) (model.getToFloat() * (double) w);

        HighlightedComponent old = highlightedComponent;
        Cursor newCursor = null;

        int a = 0;//settings.selection.invisibleHookMargin;

        if (inRange(x, sf - 1, sf + r + 1)) {
            newCursor = CURSOR_LEFT_HOOK;
            highlightedComponent = HighlightedComponent.LEFT_HOOK;
        } else if (inRange(x, sf + r, st - r)) {
            highlightedComponent = HighlightedComponent.CENTER_HOOK;
            newCursor = CURSOR_CENTRAL_HOOK;
        } else if (inRange(x, st - r - 1, st + 1)) {
            highlightedComponent = HighlightedComponent.RIGHT_HOOK;
            newCursor = CURSOR_RIGHT_HOOK;
        } else {
            highlightedComponent = HighlightedComponent.NONE;
            newCursor = CURSOR_DEFAULT;
        }
        if (newCursor != getCursor()) {
            setCursor(newCursor);
        }
        // only repaint if highlight has changed (save a lot of fps)
        if (highlightedComponent != old) {
            repaint();
        }

    }

    public void mouseDragged(MouseEvent evt) {

    if (model==null) return;

        int x = evt.getX();
        double w = getWidth();
        int r = settings.selection.visibleHookWidth;//skin.getSelectionHookSideLength();

        // SELECTED ZONE BEGIN POSITION, IN PIXELS
        double sf = (model.getFromFloat() * w);

        // SELECTED ZONE END POSITION, IN PIXELS
        double st = (model.getToFloat() * w);

        if (currentState == TimelineState.IDLE) {
            if (inRange(x, (int) sf - 1, (int) sf + r + 1)) {
                highlightedComponent = HighlightedComponent.LEFT_HOOK;
                currentState = TimelineState.RESIZE_FROM;
            } else if (inRange(x, (int) sf + r, (int) st - r)) {
                highlightedComponent = HighlightedComponent.CENTER_HOOK;
                currentState = TimelineState.MOVING;
            } else if (inRange(x, (int) st - r - 1, (int) st + 1)) {
                highlightedComponent = HighlightedComponent.RIGHT_HOOK;
                currentState = TimelineState.RESIZE_TO;
            }
        }
        double delta = 0;
        if (mousex != null) {
            delta = x - mousex;
        }
        mousex = x;

        switch (currentState) {
            case RESIZE_FROM:
                // TODO
                if ((sf + delta <= 0)) {
                    sf = 0;
                } else if (Math.abs(st - sf + delta) > settings.selection.minimalWidth) {
                    sf += delta;
                } else {
                }
                break;
            case RESIZE_TO:
                // TODO
                if ((st + delta >= w)) {
                    st = w;
                } else if (Math.abs((st + delta) - sf) > settings.selection.minimalWidth) {
                    st += delta;
                }
                break;
            case MOVING:
                if ((sf + delta <= 0)) {
                    sf = 0;
                } else if (st + delta >= w) {
                    st = w;
                } else {
                    sf += delta;
                    st += delta;
                }
                break;

        }

        if (model != null && w != 0) {
            model.setRangeFromFloat(sf * (1.0 / w), st * (1.0 / w));
        }
        this.repaint(); // so it will repaint all panels

    }
}
