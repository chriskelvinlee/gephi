/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * UIExporterPDFPanel.java
 *
 * Created on 13 avr. 2010, 18:21:09
 */
package org.gephi.ui.exporter.preview;

import com.itextpdf.text.PageSize;
import com.itextpdf.text.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.text.NumberFormat;
import javax.swing.DefaultComboBoxModel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import org.gephi.io.exporter.preview.PDFExporter;
import org.openide.util.NbBundle;

/**
 *
 * @author Mathieu Bastian
 */
public class UIExporterPDFPanel extends javax.swing.JPanel {

    private static final double INCH = 72.0;
    private static final double MM = 2.8346456692895527;
    private final String customSizeString;
    private boolean milimeter = true;
    private NumberFormat sizeFormatter;

    public UIExporterPDFPanel() {
        initComponents();

        sizeFormatter = NumberFormat.getNumberInstance();
        sizeFormatter.setMaximumFractionDigits(3);

        //Page size model - http://en.wikipedia.org/wiki/Paper_size
        DefaultComboBoxModel comboBoxModel = new DefaultComboBoxModel();
        comboBoxModel.addElement(new PageSizeItem(PageSize.A0, "A0", 841, 1189, 33.1, 46.8));
        comboBoxModel.addElement(new PageSizeItem(PageSize.A1, "A1", 594, 841, 23.4, 33.1));
        comboBoxModel.addElement(new PageSizeItem(PageSize.A2, "A2", 420, 594, 16.5, 23.4));
        comboBoxModel.addElement(new PageSizeItem(PageSize.A3, "A3", 297, 420, 11.7, 16.5));
        comboBoxModel.addElement(new PageSizeItem(PageSize.A4, "A4", 210, 297, 8.3, 11.7));
        comboBoxModel.addElement(new PageSizeItem(PageSize.A5, "A5", 148, 210, 5.8, 8.3));
        comboBoxModel.addElement(new PageSizeItem(PageSize.ARCH_A, "ARCH A", 229, 305, 9, 12));
        comboBoxModel.addElement(new PageSizeItem(PageSize.ARCH_B, "ARCH B", 305, 457, 12, 18));
        comboBoxModel.addElement(new PageSizeItem(PageSize.ARCH_C, "ARCH C", 457, 610, 18, 24));
        comboBoxModel.addElement(new PageSizeItem(PageSize.ARCH_D, "ARCH D", 610, 914, 24, 36));
        comboBoxModel.addElement(new PageSizeItem(PageSize.ARCH_E, "ARCH E", 914, 1219, 36, 48));
        comboBoxModel.addElement(new PageSizeItem(PageSize.B0, "B0", 1000, 1414, 39.4, 55.7));
        comboBoxModel.addElement(new PageSizeItem(PageSize.B1, "B1", 707, 1000, 27.8, 39.4));
        comboBoxModel.addElement(new PageSizeItem(PageSize.B2, "B2", 500, 707, 19.7, 27.8));
        comboBoxModel.addElement(new PageSizeItem(PageSize.B3, "B3", 353, 500, 13.9, 19.7));
        comboBoxModel.addElement(new PageSizeItem(PageSize.B4, "B4", 250, 353, 9.8, 13.9));
        comboBoxModel.addElement(new PageSizeItem(PageSize.B5, "B5", 176, 250, 6.9, 9.8));
        comboBoxModel.addElement(new PageSizeItem(PageSize.LEDGER, "Ledger", 432, 279, 17, 11));
        comboBoxModel.addElement(new PageSizeItem(PageSize.LEGAL, "Legal", 216, 356, 8.5, 14));
        comboBoxModel.addElement(new PageSizeItem(PageSize.LETTER, "Letter", 216, 279, 8.5, 11));
        comboBoxModel.addElement(new PageSizeItem(PageSize.TABLOID, "Tabloid", 279, 432, 11, 17));

        customSizeString = NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.pageSize.custom");
        comboBoxModel.addElement(customSizeString);
        pageSizeCombo.setModel(comboBoxModel);

        initEvents();
    }

    public void initEvents() {
        pageSizeCombo.addItemListener(new ItemListener() {

            public void itemStateChanged(ItemEvent e) {
                Object selectedItem = pageSizeCombo.getSelectedItem();
                if (selectedItem != customSizeString) {
                    PageSizeItem pageSize = (PageSizeItem) selectedItem;
                    setPageSize(pageSize);
                }
            }
        });

        widthTextField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updatePageSize();
            }
        });

        heightTextField.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                updatePageSize();
            }
        });
    }

    public void setup(PDFExporter pdfExporter) {
        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) pageSizeCombo.getModel();
        PageSizeItem pageSize = new PageSizeItem(pdfExporter.getPageSize());
        int index = 0;
        if ((index = comboBoxModel.getIndexOf(pageSize)) == -1) {
            comboBoxModel.setSelectedItem(customSizeString);
        } else {
            pageSize = (PageSizeItem) comboBoxModel.getElementAt(index);
            comboBoxModel.setSelectedItem(pageSize);
        }

        setPageSize(pageSize);
        setMargins(pdfExporter.getMarginTop(), pdfExporter.getMarginBottom(), pdfExporter.getMarginLeft(), pdfExporter.getMarginRight());
        setOrientation(pdfExporter.isLandscape());
    }

    public void unsetup(PDFExporter pdfExporter) {
        if (pageSizeCombo.getSelectedItem() == customSizeString) {
            double width = Double.parseDouble(widthTextField.getText());
            double height = Double.parseDouble(heightTextField.getText());
            if (milimeter) {
                width *= MM;
                height *= MM;
            } else {
                width *= INCH;
                height *= INCH;
            }
            float w = (float) width;
            float h = (float) height;
            Rectangle rect = new Rectangle(w, h);
            pdfExporter.setPageSize(rect);
        } else {
            pdfExporter.setPageSize(((PageSizeItem) pageSizeCombo.getSelectedItem()).getPageSize());
        }

        pdfExporter.setLandscape(landscapeRadio.isSelected());

        double top = Double.parseDouble(topMarginTextField.getText());
        double bottom = Double.parseDouble(bottomMarginTextField.getText());
        double left = Double.parseDouble(leftMarginTextField.getText());
        double right = Double.parseDouble(rightMargintextField.getText());
        if (milimeter) {
            top *= MM;
            bottom *= MM;
            left *= MM;
            right *= MM;
        }
        pdfExporter.setMarginTop((float) top);
        pdfExporter.setMarginBottom((float) bottom);
        pdfExporter.setMarginLeft((float) left);
        pdfExporter.setMarginRight((float) right);
    }

    private void updatePageSize() {
        if (pageSizeCombo.getSelectedItem() != customSizeString && !widthTextField.getText().isEmpty() && !heightTextField.getText().isEmpty()) {

            DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) pageSizeCombo.getModel();
            PageSizeItem item = getItem(widthTextField.getText(), heightTextField.getText());
            if (item == null) {
                comboBoxModel.setSelectedItem(customSizeString);
            } else {
                comboBoxModel.setSelectedItem(item);
            }
        }
    }

    private void setPageSize(PageSizeItem pageSize) {
        double pageWidth = 0;
        double pageHeight = 0;
        if (milimeter) {
            pageWidth = pageSize.mmWidth;
            pageHeight = pageSize.mmHeight;
        } else {
            pageWidth = pageSize.inWidth;
            pageHeight = pageSize.inHeight;
        }
        widthTextField.setText(sizeFormatter.format(pageWidth));
        heightTextField.setText(sizeFormatter.format(pageHeight));
    }

    private void setOrientation(boolean landscape) {
        portraitRadio.setSelected(!landscape);
        landscapeRadio.setSelected(landscape);
    }

    private void setMargins(float top, float bottom, float left, float right) {
        if (milimeter) {
            top /= MM;
            bottom /= MM;
            left /= MM;
            right /= MM;
        }
        topMarginTextField.setText(Float.toString(top));
        bottomMarginTextField.setText(Float.toString(bottom));
        leftMarginTextField.setText(Float.toString(left));
        rightMargintextField.setText(Float.toString(right));
    }

    private PageSizeItem getItem(String width, String height) {
        DefaultComboBoxModel comboBoxModel = (DefaultComboBoxModel) pageSizeCombo.getModel();
        for (int i = 0; i < comboBoxModel.getSize(); i++) {
            Object o = comboBoxModel.getElementAt(i);
            if (o instanceof PageSizeItem) {
                PageSizeItem pageSize = (PageSizeItem) o;
                double pageWidth = 0;
                double pageHeight = 0;
                if (milimeter) {
                    pageWidth = pageSize.mmWidth;
                    pageHeight = pageSize.mmHeight;
                } else {
                    pageWidth = pageSize.inWidth;
                    pageHeight = pageSize.inHeight;
                }
                String wStr = sizeFormatter.format(pageWidth);
                String hStr = sizeFormatter.format(pageHeight);
                if (wStr.equals(width) && hStr.equals(height)) {
                    return ((PageSizeItem) o);
                }
            }
        }
        return null;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        orientationButtonGroup = new javax.swing.ButtonGroup();
        labelPageSize = new javax.swing.JLabel();
        pageSizeCombo = new javax.swing.JComboBox();
        labelWidth = new javax.swing.JLabel();
        widthTextField = new javax.swing.JTextField();
        labelHeight = new javax.swing.JLabel();
        heightTextField = new javax.swing.JTextField();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        labelOrientation = new javax.swing.JLabel();
        portraitRadio = new javax.swing.JRadioButton();
        landscapeRadio = new javax.swing.JRadioButton();
        labelMargins = new javax.swing.JLabel();
        labelTop = new javax.swing.JLabel();
        topMarginTextField = new javax.swing.JTextField();
        labelBottom = new javax.swing.JLabel();
        bottomMarginTextField = new javax.swing.JTextField();
        labelLeft = new javax.swing.JLabel();
        labelRight = new javax.swing.JLabel();
        leftMarginTextField = new javax.swing.JTextField();
        rightMargintextField = new javax.swing.JTextField();

        labelPageSize.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelPageSize.text")); // NOI18N

        pageSizeCombo.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        labelWidth.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelWidth.text")); // NOI18N

        widthTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        widthTextField.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.widthTextField.text")); // NOI18N

        labelHeight.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelHeight.text")); // NOI18N

        heightTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        heightTextField.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.heightTextField.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.jLabel4.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.jLabel5.text")); // NOI18N

        labelOrientation.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelOrientation.text")); // NOI18N

        orientationButtonGroup.add(portraitRadio);
        portraitRadio.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.portraitRadio.text")); // NOI18N

        orientationButtonGroup.add(landscapeRadio);
        landscapeRadio.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.landscapeRadio.text")); // NOI18N

        labelMargins.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelMargins.text")); // NOI18N

        labelTop.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelTop.text")); // NOI18N

        topMarginTextField.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.topMarginTextField.text")); // NOI18N

        labelBottom.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelBottom.text")); // NOI18N

        bottomMarginTextField.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.bottomMarginTextField.text")); // NOI18N

        labelLeft.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelLeft.text")); // NOI18N

        labelRight.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.labelRight.text")); // NOI18N

        leftMarginTextField.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.leftMarginTextField.text")); // NOI18N

        rightMargintextField.setText(org.openide.util.NbBundle.getMessage(UIExporterPDFPanel.class, "UIExporterPDFPanel.rightMargintextField.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(labelPageSize)
                    .addComponent(labelOrientation)
                    .addComponent(labelMargins))
                .addGap(27, 27, 27)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(landscapeRadio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(portraitRadio, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(labelHeight)
                                    .addComponent(labelWidth))
                                .addGap(18, 18, 18)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(heightTextField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 95, javax.swing.GroupLayout.PREFERRED_SIZE))))
                        .addGap(10, 10, 10)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel4)
                            .addComponent(jLabel5)))
                    .addComponent(pageSizeCombo, 0, 224, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelTop)
                                .addGap(26, 26, 26)
                                .addComponent(topMarginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(labelLeft))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(labelBottom)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                                .addComponent(bottomMarginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(18, 18, 18)
                                .addComponent(labelRight)))
                        .addGap(21, 21, 21)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(leftMarginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rightMargintextField, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelPageSize)
                    .addComponent(pageSizeCombo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelWidth))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(labelHeight))
                .addGap(28, 28, 28)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelOrientation)
                    .addComponent(portraitRadio))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(landscapeRadio)
                .addGap(30, 30, 30)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(labelMargins)
                    .addComponent(labelTop)
                    .addComponent(topMarginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelLeft)
                    .addComponent(leftMarginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(bottomMarginTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(labelBottom)
                    .addComponent(labelRight)
                    .addComponent(rightMargintextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(59, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private static class PageSizeItem {

        private final Rectangle pageSize;
        private String name = "";
        private final double inWidth;
        private final double inHeight;
        private final double mmWidth;
        private final double mmHeight;

        public PageSizeItem(Rectangle pageSize) {
            this.pageSize = pageSize;
            this.inHeight = pageSize.getHeight() / INCH;
            this.inWidth = pageSize.getWidth() / INCH;
            this.mmHeight = pageSize.getHeight() / MM;
            this.mmWidth = pageSize.getWidth() / MM;
        }

        public PageSizeItem(Rectangle pageSize, String name, double mmWidth, double mmHeight, double inWidth, double inHeight) {
            this.pageSize = pageSize;
            this.name = name;
            this.inHeight = inHeight;
            this.inWidth = inWidth;
            this.mmHeight = mmHeight;
            this.mmWidth = mmWidth;
        }

        public Rectangle getPageSize() {
            return pageSize;
        }

        public double getInHeight() {
            return inHeight;
        }

        public double getInWidth() {
            return inWidth;
        }

        public double getMmHeight() {
            return mmHeight;
        }

        public double getMmWidth() {
            return mmWidth;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final PageSizeItem other = (PageSizeItem) obj;
            if (this.pageSize != other.pageSize && (this.pageSize == null || !this.pageSize.equals(other.pageSize))) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int hash = 3;
            hash = 47 * hash + (this.pageSize != null ? this.pageSize.hashCode() : 0);
            return hash;
        }

        @Override
        public String toString() {
            return name;
        }
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTextField bottomMarginTextField;
    private javax.swing.JTextField heightTextField;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel labelBottom;
    private javax.swing.JLabel labelHeight;
    private javax.swing.JLabel labelLeft;
    private javax.swing.JLabel labelMargins;
    private javax.swing.JLabel labelOrientation;
    private javax.swing.JLabel labelPageSize;
    private javax.swing.JLabel labelRight;
    private javax.swing.JLabel labelTop;
    private javax.swing.JLabel labelWidth;
    private javax.swing.JRadioButton landscapeRadio;
    private javax.swing.JTextField leftMarginTextField;
    private javax.swing.ButtonGroup orientationButtonGroup;
    private javax.swing.JComboBox pageSizeCombo;
    private javax.swing.JRadioButton portraitRadio;
    private javax.swing.JTextField rightMargintextField;
    private javax.swing.JTextField topMarginTextField;
    private javax.swing.JTextField widthTextField;
    // End of variables declaration//GEN-END:variables
}
