/*
Copyright 2008-2010 Gephi
Authors : Eduardo Ramos <eduramiba@gmail.com>
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
package org.gephi.datalaboratory.impl.manipulators.attributecolumns;

import java.awt.Image;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.SwingUtilities;
import org.gephi.data.attributes.api.AttributeColumn;
import org.gephi.data.attributes.api.AttributeTable;
import org.gephi.data.attributes.api.AttributeUtils;
import org.gephi.datalaboratory.api.AttributeColumnsController;
import org.gephi.datalaboratory.api.utils.HTMLEscape;
import org.gephi.datalaboratory.spi.attributecolumns.AttributeColumnsManipulator;
import org.gephi.datalaboratory.spi.attributecolumns.AttributeColumnsManipulatorUI;
import org.gephi.ui.components.JFreeChartDialog;
import org.gephi.ui.components.SimpleHTMLReport;
import org.gephi.utils.TempDirUtils;
import org.gephi.utils.TempDirUtils.TempDir;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartUtilities;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.labels.BoxAndWhiskerToolTipGenerator;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.category.BoxAndWhiskerRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.statistics.DefaultBoxAndWhiskerCategoryDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;
import org.openide.util.Exceptions;
import org.openide.util.ImageUtilities;
import org.openide.util.Lookup;
import org.openide.util.NbBundle;
import org.openide.util.lookup.ServiceProvider;
import org.openide.windows.WindowManager;

/**
 * AttributeColumnsManipulator that shows a report with statistics values of a number/number list column.
 * @author Eduardo Ramos <eduramiba@gmail.com>
 */
@ServiceProvider(service = AttributeColumnsManipulator.class)
public class NumberColumnStatisticsReport implements AttributeColumnsManipulator {

    public void execute(AttributeTable table, final AttributeColumn column) {
        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        final BigDecimal[] statistics = ac.getNumberOrNumberListColumnStatistics(table, column);
        final StringBuilder sb = new StringBuilder();
        sb.append("<html>");
        sb.append(NbBundle.getMessage(NumberColumnStatisticsReport.class, "NumberColumnStatisticsReport.report.header", HTMLEscape.stringToHTMLString(column.getTitle())));
        sb.append("<hr>");
        if (statistics != null) {//There are numbers in the column and statistics can be shown:
            sb.append("<ul>");
            writeStatistic(sb, "NumberColumnStatisticsReport.report.average", statistics[0]);
            writeStatistic(sb, "NumberColumnStatisticsReport.report.Q1", statistics[1]);
            writeStatistic(sb, "NumberColumnStatisticsReport.report.median", statistics[2]);
            writeStatistic(sb, "NumberColumnStatisticsReport.report.Q3", statistics[3]);
            writeStatistic(sb, "NumberColumnStatisticsReport.report.IQR", statistics[4]);
            writeStatistic(sb, "NumberColumnStatisticsReport.report.sum", statistics[5]);
            writeStatistic(sb, "NumberColumnStatisticsReport.report.min", statistics[6]);
            writeStatistic(sb, "NumberColumnStatisticsReport.report.max", statistics[7]);
            sb.append("</ul>");
            sb.append("<hr>");
            try {
                Number[] columnNumbers = ac.getColumnNumbers(table, column);
                final JFreeChart boxPlot = buildBoxPlot(columnNumbers, column.getTitle());
                writeBoxPlot(sb, boxPlot);
                sb.append("<hr>");
                final JFreeChart scatterPlot = buildScatterPlot(columnNumbers, column.getTitle());
                writeScatterPlot(sb, scatterPlot);
            } catch (IOException ex) {
                Exceptions.printStackTrace(ex);
            }
        } else {
            sb.append(getMessage("NumberColumnStatisticsReport.report.empty"));
        }
        sb.append("</html>");

        SwingUtilities.invokeLater(new Runnable() {

            public void run() {
                SimpleHTMLReport dialog = new SimpleHTMLReport(WindowManager.getDefault().getMainWindow(), sb.toString());
                JFreeChartDialog chartDialog = new JFreeChartDialog(WindowManager.getDefault().getMainWindow(), "test", buildBoxPlot(statistics, column.getTitle()), 300, 500);
            }
        });
    }

    public String getName() {
        return getMessage("NumberColumnStatisticsReport.name");
    }

    public String getDescription() {
        return getMessage("NumberColumnStatisticsReport.description");
    }

    public boolean canManipulateColumn(AttributeTable table, AttributeColumn column) {
        AttributeColumnsController ac = Lookup.getDefault().lookup(AttributeColumnsController.class);
        return AttributeUtils.getDefault().isNumberOrNumberListColumn(column) && ac.getTableRowsCount(table) > 0;//Make sure it is a number/number list column and there is at least 1 row
    }

    public AttributeColumnsManipulatorUI getUI() {
        return null;
    }

    public int getType() {
        return 100;
    }

    public int getPosition() {
        return 100;
    }

    public Image getIcon() {
        return ImageUtilities.loadImage("org/gephi/datalaboratory/impl/manipulators/resources/statistics.png");
    }

    private void writeStatistic(StringBuilder sb, String resName, BigDecimal number) {
        sb.append("<li>");
        sb.append(getMessage(resName));
        sb.append(": ");
        sb.append(number);
        sb.append("</li>");
    }

    private JFreeChart buildBoxPlot(final Number[] numbers, final String columnTitle) {
        DefaultBoxAndWhiskerCategoryDataset dataset = new DefaultBoxAndWhiskerCategoryDataset();
        final ArrayList<Number> list = new ArrayList<Number>();
        list.addAll(Arrays.asList(numbers));

        final String valuesString = getMessage("NumberColumnStatisticsReport.report.box-plot.values");
        dataset.add(list, valuesString, "");

        final BoxAndWhiskerRenderer renderer = new BoxAndWhiskerRenderer();
        renderer.setMeanVisible(false);
        renderer.setFillBox(false);
        renderer.setMaximumBarWidth(0.5);

        final CategoryAxis xAxis = new CategoryAxis(NbBundle.getMessage(NumberColumnStatisticsReport.class, "NumberColumnStatisticsReport.report.box-plot.column", columnTitle));
        final NumberAxis yAxis = new NumberAxis(getMessage("NumberColumnStatisticsReport.report.box-plot.values-range"));
        yAxis.setAutoRangeIncludesZero(false);
        renderer.setBaseToolTipGenerator(new BoxAndWhiskerToolTipGenerator());
        final CategoryPlot plot = new CategoryPlot(dataset, xAxis, yAxis, renderer);
        plot.setRenderer(renderer);

        JFreeChart boxPlot = new JFreeChart(getMessage("NumberColumnStatisticsReport.report.box-plot.title"), plot);
        return boxPlot;
    }

    private void writeBoxPlot(final StringBuilder sb, JFreeChart boxPlot) throws IOException {
        TempDir tempDir = TempDirUtils.createTempDir();
        String imageFile = "";
        String fileName = "box-plot-chart.png";
        File file = tempDir.createFile(fileName);
        imageFile = "<center><img src=\"file:" + file.getAbsolutePath() + "\"</img></center>";
        ChartUtilities.saveChartAsPNG(file, boxPlot, 300, 500);

        sb.append(imageFile);
    }

    private JFreeChart buildScatterPlot(final Number[] numbers, final String columnTitle) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        XYSeries series1 = new XYSeries(columnTitle);
        for (int i = 0; i < numbers.length; i++) {
            series1.add(i, numbers[i]);
        }
        dataset.addSeries(series1);
        JFreeChart scatterPlot = ChartFactory.createXYLineChart(
                getMessage("NumberColumnStatisticsReport.report.scatter-plot.title"),
                getMessage("NumberColumnStatisticsReport.report.scatter-plot.xLabel"),
                columnTitle,
                dataset,
                PlotOrientation.VERTICAL,
                true,
                true,
                false);

        XYPlot plot = (XYPlot) scatterPlot.getPlot();
        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, false);
        renderer.setSeriesShapesVisible(0, true);
        renderer.setSeriesShape(0, new java.awt.geom.Ellipse2D.Double(0, 0, 1, 1));
        plot.setBackgroundPaint(java.awt.Color.WHITE);
        plot.setDomainGridlinePaint(java.awt.Color.GRAY);
        plot.setRangeGridlinePaint(java.awt.Color.GRAY);
        plot.setRenderer(renderer);

        return scatterPlot;
    }

    private void writeScatterPlot(final StringBuilder sb, JFreeChart scatterPlot) throws IOException {
        TempDir tempDir = TempDirUtils.createTempDir();
        String imageFile = "";
        String fileName = "scatter-plot-chart.png";
        File file = tempDir.createFile(fileName);
        imageFile = "<center><img src=\"file:" + file.getAbsolutePath() + "\"</img></center>";
        ChartUtilities.saveChartAsPNG(file, scatterPlot, 600, 400);

        sb.append(imageFile);
    }

    private String getMessage(String resName) {
        return NbBundle.getMessage(NumberColumnStatisticsReport.class, resName);
    }
}
