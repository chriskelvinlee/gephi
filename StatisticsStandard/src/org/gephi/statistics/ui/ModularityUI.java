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
package org.gephi.statistics.ui;

import java.text.DecimalFormat;
import javax.swing.JPanel;
import org.gephi.statistics.Modularity;
import org.gephi.statistics.api.Statistics;
import org.gephi.statistics.ui.api.StatisticsUI;
import org.openide.util.lookup.ServiceProvider;

@ServiceProvider(service = StatisticsUI.class)
public class ModularityUI implements StatisticsUI {

    private ModularityPanel panel;
    private Modularity mod;

    public JPanel getSettingsPanel() {
        panel = new ModularityPanel();
        return panel;
    }

    public void setup(Statistics statistics) {
        this.mod = (Modularity) statistics;
        if (panel != null) {
            panel.setRandomize(mod.getRandom());
        }
    }

    public void unsetup() {
        mod.setRandom(panel.isRandomize());
        panel = null;
    }

    public Class<? extends Statistics> getStatisticsClass() {
        return Modularity.class;
    }

    public String getValue() {
        DecimalFormat df = new DecimalFormat("###.###");
        return "" + df.format(mod.getModularity());
    }

    public String getDisplayName() {
        return "Modularity";
    }

    public String getCategory() {
        return StatisticsUI.CATEGORY_NETWORK_OVERVIEW;
    }

    public int getPosition() {
        return 600;
    }
}
