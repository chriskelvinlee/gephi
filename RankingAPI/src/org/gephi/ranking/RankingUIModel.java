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
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.ranking;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 *
 * @author Mathieu Bastian
 */
public class RankingUIModel {

    //Const
    public static final int NODE_RANKING = 1;
    public static final int EDGE_RANKING = 2;

    //Model
    protected int ranking;
    protected boolean barChartVisible;
    protected boolean listVisible;

    //Listener
    protected List<PropertyChangeListener> listeners = new ArrayList<PropertyChangeListener>();

    public RankingUIModel() {
        //DefaultValues
        ranking = NODE_RANKING;
        barChartVisible = false;
        listVisible = false;
    }

    public int getRanking() {
        return ranking;
    }

    public boolean isBarChartVisible() {
        return barChartVisible;
    }

    public boolean isListVisible() {
        return listVisible;
    }

    public void setListVisible(boolean listVisible) {
        if (this.listVisible == listVisible) {
            return;
        }
        boolean oldValue = this.listVisible;
        this.listVisible = listVisible;
        firePropertyChangeEvent("listVisible", oldValue, listVisible);
    }

    public void setBarChartVisible(boolean barChartVisible) {
        if (this.barChartVisible == barChartVisible) {
            return;
        }
        boolean oldValue = this.barChartVisible;
        this.barChartVisible = barChartVisible;
        firePropertyChangeEvent("barChartVisible", oldValue, barChartVisible);
    }

    public void setRanking(int ranking) {
        if (ranking != NODE_RANKING && ranking != EDGE_RANKING) {
            throw new IllegalArgumentException("Ranking must be NODE_RANKING or EDGE_RANKING");
        }
        if (this.ranking == ranking) {
            return;
        }
        int oldValue = this.ranking;
        this.ranking = ranking;
        firePropertyChangeEvent("ranking", oldValue, ranking);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removePropertyChangeListener(PropertyChangeListener listener) {
        listeners.remove(listener);
    }

    private void firePropertyChangeEvent(String propertyName, Object beforeValue, Object afterValue) {
        PropertyChangeEvent event = new PropertyChangeEvent(this, propertyName, beforeValue, afterValue);
        for (PropertyChangeListener listener : listeners) {
            listener.propertyChange(event);
        }
    }

    //XML
    public void readXML(Element modelElement) {
        Element rankingE = (Element) modelElement.getElementsByTagName("ranking").item(0);
        ranking = Integer.parseInt(rankingE.getTextContent());

        Element barchartvisibleE = (Element) modelElement.getElementsByTagName("barchartvisible").item(0);
        barChartVisible = Boolean.parseBoolean(barchartvisibleE.getTextContent());

        Element listvisibleE = (Element) modelElement.getElementsByTagName("listvisible").item(0);
        listVisible = Boolean.parseBoolean(listvisibleE.getTextContent());
    }

    public Element writeXML(Document document) {
        Element rankingModelE = document.createElement("rankinguimodel");

        Element rankingE = document.createElement("ranking");
        rankingE.setTextContent(String.valueOf(ranking));
        rankingModelE.appendChild(rankingE);

        Element barChartE = document.createElement("barchartvisible");
        barChartE.setTextContent(String.valueOf(barChartVisible));
        rankingModelE.appendChild(barChartE);

        Element listE = document.createElement("listvisible");
        listE.setTextContent(String.valueOf(listVisible));
        rankingModelE.appendChild(barChartE);

        return rankingModelE;
    }
}
