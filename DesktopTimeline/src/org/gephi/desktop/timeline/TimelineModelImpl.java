/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.desktop.timeline;

import java.util.ArrayList;
import java.util.List;

import javax.swing.event.ChangeEvent;
import org.gephi.filters.api.FilterController;
import org.gephi.filters.api.Range;
import org.gephi.filters.spi.FilterProperty;
import org.gephi.graph.api.Graph;
import org.gephi.graph.api.GraphController;
import org.gephi.graph.api.GraphModel;
import org.gephi.project.api.ProjectController;

import org.gephi.timeline.api.TimelineModel;
import org.gephi.timeline.api.TimelineModelListener;
import org.openide.util.Lookup;

import org.openide.util.lookup.ServiceProvider;

/**
 *
 * @author jbilcke
 */
@ServiceProvider(service = TimelineModel.class)
public class TimelineModelImpl
        implements
        TimelineModel {

    private List<TimelineModelListener> listeners;
    private FilterProperty filter;
    private double fromFloat = 0.0f;
    private double toFloat = 1.0f;
    private double fromValue = 0.0f;
    private double toValue = 0.0f;
    private double maxValue = 1.0f;
    private double minValue = 0.0f;

    public TimelineModelImpl() {
        listeners = new ArrayList<TimelineModelListener>();
    }

    public void fireChangeEvent() {
        ChangeEvent evt = new ChangeEvent(this);
        for (TimelineModelListener listener : listeners) {
            listener.timelineModelChanged(evt);
        }
    }

    public void addListener(TimelineModelListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeListener(TimelineModelListener listener) {
        listeners.remove(listener);
    }

    public synchronized void setFilterProperty(FilterProperty filter) {
        this.filter = filter;
    }

    public synchronized FilterProperty getFilterProperty() {
        return filter;
    }

    // Not used for the moment (will be used to generate charts)
    public String getFirstAttributeLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Not used for the moment (will be used to generate charts)
    public String getLastAttributeLabel() {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Not used for the moment (will be used to generate charts)
    public String getAttributeLabel(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Not used for the moment (will be used to generate charts)
    public String getAttributeLabel(int from, int to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Not used for the moment (will be used to generate charts)
    public double getAttributeValue(int i) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    // Not used for the moment (will be used to generate charts)
    public double getAttributeValue(int from, int to) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    public synchronized double getTotalSize() {
        return getMaxValue() - getMinValue();
    }
    public synchronized double getRangeSizeValue() {
        return getToValue() - getFromValue();
    }
    public synchronized double getRangeSizeFloat() {
        return getToFloat() - getFromFloat();
    }
    // set the range using real values
    public synchronized double getMinValue() {
        return minValue;
    }
    // set the range using real values

    public synchronized double getMaxValue() {
        return maxValue;
    }

    // set the range using real values
    public synchronized void setMinValue(double min) {
        //if (min >= maxValue) return;
        this.minValue = min;
        setFromValue(getMinValue() + getFromFloat() * getTotalSize());
    }
    // set the range using real values

    public synchronized void setMaxValue(double max) {
        //if (max <= minValue) return;
        this.maxValue = max;
        setFromValue(getMaxValue() + getToFloat() * getTotalSize());
    }

    public synchronized void setMinMax(double min, double max) {
        if (min >= max) return;
        this.minValue = min;
        setFromValue(getMinValue() + getFromFloat() * getTotalSize());
        this.maxValue = max;
        setFromValue(getMaxValue() + getToFloat() * getTotalSize());
    }

    // set the range using real values
    public synchronized void setRangeFromRealValues(double from, double to) {
        if (from >= to) return;
        fromValue = from;
        toValue = to;
        if (filter != null) filter.setValue(new Range(from, to));
    }

    public synchronized void setRangeFromFloat(double from, double to) {
        if (from >= to) return;
        fromFloat = from;
        fromValue = getMinValue() + from * getTotalSize();
        toFloat = to;
        toValue = getMinValue() + to * getTotalSize();
        if (filter != null) filter.setValue(new Range(getFromValue(), getToValue()));
    }

    public synchronized void setFromFloat(double from) {
        //if (from >= toFloat) return;
        fromFloat = from;
        setFromValue(getMinValue() + getFromFloat() * getTotalSize());
        if (filter != null) filter.setValue(new Range(getFromValue(), getToValue()));
    }

    public synchronized void setToFloat(double to) {
        //if (to <= toFloat) return;
        toFloat = to;
        setToValue(getMinValue() + getFromFloat() * getTotalSize());
        if (filter != null) filter.setValue(new Range(getFromValue(), getToValue()));
    }

    public synchronized double getFromFloat() {
        return fromFloat;
    }

    public synchronized double getToFloat() {
        return toFloat;
    }

    public synchronized void setFromValue(double from) {
        //if (from >= toValue) return;
        fromValue = from;
    }

    public synchronized void setToValue(double to) {
        //if (to <= fromValue) return;
        toValue = to;
    }

    public synchronized double getFromValue() {
        return fromValue;
    }

    public synchronized double getToValue() {
        return toValue;
    }
}
