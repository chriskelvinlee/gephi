package org.gephi.io.exporter.preview;

import com.itextpdf.text.BaseColor;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfWriter;
import java.awt.Font;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import org.gephi.io.exporter.api.FileType;
import org.gephi.io.exporter.preview.util.LengthUnit;
import org.gephi.io.exporter.preview.util.SupportSize;
import org.gephi.io.exporter.spi.VectorialFileExporter;
import org.gephi.preview.api.BidirectionalEdge;
import org.gephi.preview.api.Color;
import org.gephi.preview.api.DirectedEdge;
import org.gephi.preview.api.Edge;
import org.gephi.preview.api.EdgeArrow;
import org.gephi.preview.api.EdgeLabel;
import org.gephi.preview.api.EdgeMiniLabel;
import org.gephi.preview.api.Graph;
import org.gephi.preview.api.GraphRenderer;
import org.gephi.preview.api.GraphSheet;
import org.gephi.preview.api.Node;
import org.gephi.preview.api.NodeLabel;
import org.gephi.preview.api.NodeLabelBorder;
import org.gephi.preview.api.Point;
import org.gephi.preview.api.PreviewController;
import org.gephi.preview.api.SelfLoop;
import org.gephi.preview.api.UndirectedEdge;
import org.gephi.preview.api.UnidirectionalEdge;
import org.gephi.utils.longtask.spi.LongTask;
import org.gephi.utils.progress.Progress;
import org.gephi.utils.progress.ProgressTicket;
import org.gephi.project.api.Workspace;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.lookup.ServiceProvider;

/**
 * Class exporting the preview graph as an SVG image.
 *
 * @author Jérémy Subtil <jeremy.subtil@gephi.org>
 */
@ServiceProvider(service = VectorialFileExporter.class)
public class PDFExporter implements GraphRenderer, VectorialFileExporter, LongTask {

    private ProgressTicket progress;
    private boolean cancel = false;
    private PdfContentByte pdfSketch;
    private Document document;

    public boolean exportData(File file, Workspace workspace) throws Exception {
        try {
            SupportSize supportSize = new SupportSize(210, 297, LengthUnit.MILLIMETER);
            exportData(file, supportSize);
        } catch (Exception e) {
            clean();
            throw e;
        }
        boolean c = cancel;
        clean();
        return !c;
    }

    public FileType[] getFileTypes() {
        return new FileType[]{new FileType(".pdf", "PDF files")};
    }

    public String getName() {
        return "PDF Exporter";
    }

    public boolean cancel() {
        cancel = true;
        return true;
    }

    public void setProgressTicket(ProgressTicket progressTicket) {
        this.progress = progressTicket;
    }

    public void renderGraph(Graph graph) {
        if (graph.showEdges()) {
            renderGraphEdges(graph);
        }

        if (graph.showNodes()) {
            renderGraphNodes(graph);
        }

        renderGraphLabels(graph);

        renderGraphLabelBorders(graph);
    }

    public void renderGraphEdges(Graph graph) {
    }

    public void renderGraphSelfLoops(Graph graph) {
    }

    public void renderGraphUnidirectionalEdges(Graph graph) {
    }

    public void renderGraphBidirectionalEdges(Graph graph) {
    }

    public void renderGraphUndirectedEdges(Graph graph) {
    }

    public void renderGraphNodes(Graph graph) {
        for (Node n : graph.getNodes()) {
            renderNode(n);
        }
    }

    public void renderGraphLabels(Graph graph) {
        for (UnidirectionalEdge e : graph.getUnidirectionalEdges()) {
            if (!e.isCurved()) {
                if (e.showLabel() && e.hasLabel()) {
                    renderEdgeLabel(e.getLabel());
                }

                if (e.showMiniLabels()) {
                    renderEdgeMiniLabels(e);
                }
            }
        }

        for (BidirectionalEdge e : graph.getBidirectionalEdges()) {
            if (!e.isCurved()) {
                if (e.showLabel() && e.hasLabel()) {
                    renderEdgeLabel(e.getLabel());
                }

                if (e.showMiniLabels()) {
                    renderEdgeMiniLabels(e);
                }
            }
        }

        for (UndirectedEdge e : graph.getUndirectedEdges()) {
            if (e.showLabel() && !e.isCurved() && e.hasLabel()) {
                renderEdgeLabel(e.getLabel());
            }
        }

        for (Node n : graph.getNodes()) {
            if (n.showLabel() && n.hasLabel()) {
                renderNodeLabel(n.getLabel());
            }
        }
    }

    public void renderGraphLabelBorders(Graph graph) {
    }

    public void renderNode(Node node) {
        Point center = node.getPosition();
        Color c = node.getColor();
        Color bc = node.getBorderColor();

        // node border rendering
        pdfSketch.setRGBColorFill(bc.getRed(), bc.getGreen(), bc.getBlue());
        pdfSketch.circle(center.getX(), center.getY(), node.getRadius() + node.getBorderWidth());
        pdfSketch.fill();

        // node content rendering
        pdfSketch.setRGBColorFill(c.getRed(), c.getGreen(), c.getBlue());
        pdfSketch.circle(center.getX(), center.getY(), node.getRadius());
        pdfSketch.fillStroke();
    }

    public void renderNodeLabel(NodeLabel label) {
        Color c = label.getColor();
        Point p = label.getPosition();
        Font font = label.getFont();

        pdfSketch.setRGBColorFill(c.getRed(), c.getGreen(), c.getBlue());

        try {
            BaseFont bf = genBaseFont(font);
            pdfSketch.beginText();
            pdfSketch.setFontAndSize(bf, font.getSize());
            pdfSketch.showTextAligned(PdfContentByte.ALIGN_CENTER, label.getValue(), p.getX(), p.getY(), -90);
            pdfSketch.endText();
        } catch (DocumentException ex) {
            Exceptions.printStackTrace(ex);
        } catch (IOException ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    public void renderNodeLabelBorder(NodeLabelBorder border) {
    }

    public void renderSelfLoop(SelfLoop selfLoop) {
    }

    public void renderDirectedEdge(DirectedEdge edge) {
    }

    public void renderEdge(Edge edge) {
    }

    public void renderStraightEdge(Edge edge) {
    }

    public void renderCurvedEdge(Edge edge) {
    }

    public void renderEdgeArrows(DirectedEdge edge) {
    }

    public void renderEdgeMiniLabels(DirectedEdge edge) {
    }

    public void renderEdgeArrow(EdgeArrow arrow) {
    }

    public void renderEdgeLabel(EdgeLabel label) {
    }

    public void renderEdgeMiniLabel(EdgeMiniLabel miniLabel) {
    }

    /**
     * Cleans all fields.
     */
    public void clean() {
        progress = null;
        cancel = false;
        pdfSketch = null;
        document = null;
    }

    /**
     * Does export the preview graph as an SVG image.
     *
     * @param file         the output SVG file
     * @param supportSize  the support size of the exported image
     * @throws Exception
     */
    private void exportData(File file, SupportSize supportSize) throws Exception {
        // fetches the preview graph sheet
        PreviewController controller = Lookup.getDefault().lookup(PreviewController.class);
        GraphSheet graphSheet = controller.getGraphSheet();
        Graph graph = graphSheet.getGraph();

        Progress.start(progress);

        // calculates progress units count
        int max = 0;
        if (graph.showNodes()) {
            max += graph.countNodes();
        }
        if (graph.showEdges()) {
            max += graph.countUnidirectionalEdges() + graph.countBidirectionalEdges();
            if (graph.showSelfLoops()) {
                max += graph.countSelfLoops();
            }
        }
        Progress.switchToDeterminate(progress, max);

        // export task
        document = new Document(PageSize.A4);
        PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
        document.open();
        pdfSketch = writer.getDirectContent();
        pdfSketch.saveState();
        pdfSketch.concatCTM(0f, 1f, -1f, 0f, 0f, 0f);
        renderGraph(graphSheet.getGraph());
        pdfSketch.restoreState();
        document.close();

        Progress.finish(progress);
    }

    private BaseFont genBaseFont(java.awt.Font font) throws DocumentException, IOException {
        return BaseFont.createFont();
    }
}
