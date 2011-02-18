/*
Copyright 2008 WebAtlas
Authors : Mathieu Bastian, Mathieu Jacomy, Julian Bilcke
Website : http://www.gephi.org

This file is part of Gephi.

Gephi is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

Gephi is distributed in the hope that it will be useful,
but WITHOUType ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.ranking.impl;

import org.gephi.ranking.api.XYZTransformer;

/**
 *
 * @author Mathieu Bastian
 */
public abstract class AbstractXYZTransformer<Target> extends AbstractTransformer<Target> implements XYZTransformer<Target> {

    protected Double min = 0d;
    protected Double max = 500d;
    protected String axe = "X";

    public AbstractXYZTransformer() {
    }

    public AbstractXYZTransformer(float lowerBound, float upperBound) {
        super(lowerBound, upperBound);
    }

    public AbstractXYZTransformer(float lowerBound, float upperBound, Double min, Double max) {
        super(lowerBound, upperBound);
        this.min = min;
        this.max = max;
    }

    public Double getMin() {
        return min;
    }

    public Double getMax() {
        return max;
    }

    public String getAxe() {
        return axe;
    }

    public void setMinValue(Double min) {
        this.min = min;
    }

    public void setMaxValue(Double max) {
        this.max = max;
    }

    public void setAxe(String axe) {
        this.axe = axe;
    }

    public float getValue(float normalizedValue){
        if (interpolator != null) {
            normalizedValue = interpolator.interpolate(normalizedValue);
        }
        return (float)(normalizedValue * (getMax() - getMin()) + getMin());
    }
}
