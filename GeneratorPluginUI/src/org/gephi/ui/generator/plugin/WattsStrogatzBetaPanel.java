/*
 * Copyright 2008-2010 Gephi
 * Authors : Cezary Bartosiak
 * Website : http://www.gephi.org
 *
 * This file is part of Gephi.
 *
 * Gephi is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Gephi is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Gephi.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.gephi.ui.generator.plugin;

import org.gephi.lib.validation.BetweenZeroAndOneValidator;
import org.netbeans.validation.api.Problems;
import org.netbeans.validation.api.Validator;
import org.netbeans.validation.api.builtin.Validators;
import org.netbeans.validation.api.ui.ValidationGroup;
import org.netbeans.validation.api.ui.ValidationPanel;

/**
 *
 *
 * @author Cezary Bartosiak
 */
public class WattsStrogatzBetaPanel extends javax.swing.JPanel {

    /** Creates new form BarabasiAlbertPanel */
    public WattsStrogatzBetaPanel() {
        initComponents();
    }

	public static ValidationPanel createValidationPanel(WattsStrogatzBetaPanel innerPanel) {
		ValidationPanel validationPanel = new ValidationPanel();
		if (innerPanel == null)
			innerPanel = new WattsStrogatzBetaPanel();
		validationPanel.setInnerComponent(innerPanel);

		ValidationGroup group = validationPanel.getValidationGroup();

		group.add(innerPanel.NField, Validators.REQUIRE_NON_EMPTY_STRING,
				new NValidator(innerPanel));
		group.add(innerPanel.KField, Validators.REQUIRE_NON_EMPTY_STRING,
				new KValidator(innerPanel));
		group.add(innerPanel.betaField, Validators.REQUIRE_NON_EMPTY_STRING,
				new BetweenZeroAndOneValidator());

		return validationPanel;
	}

	private static class NValidator implements Validator<String> {
		private WattsStrogatzBetaPanel innerPanel;

		public NValidator(WattsStrogatzBetaPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Integer N = Integer.parseInt(innerPanel.NField.getText());
				Integer K = Integer.parseInt(innerPanel.KField.getText());
				result = N > K;
			}
			catch (Exception e) { }
			if (!result) {
				String message = "<html>N &gt; K</html>";
				problems.add(message);
			}

			return result;
		}
    }

	private static class KValidator implements Validator<String> {
		private WattsStrogatzBetaPanel innerPanel;

		public KValidator(WattsStrogatzBetaPanel innerPanel) {
			this.innerPanel = innerPanel;
		}

		@Override
		public boolean validate(Problems problems, String compName, String model) {
			boolean result = false;

			try {
				Integer N   = Integer.parseInt(innerPanel.NField.getText());
				Integer K   = Integer.parseInt(innerPanel.KField.getText());
				Double  lnN = Math.log(N);
				result = K >= lnN && lnN >= 1 && K % 2 == 0;
			}
			catch (Exception e) { }
			if (!result) {
				String message = "<html>K &gt;= ln(N) &gt;= 1 and K is even</html>";
				problems.add(message);
			}

			return result;
		}
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        betaLabel = new javax.swing.JLabel();
        betaField = new javax.swing.JTextField();
        NField = new javax.swing.JTextField();
        KField = new javax.swing.JTextField();
        NLabel = new javax.swing.JLabel();
        KLabel = new javax.swing.JLabel();
        constraintsLabel = new javax.swing.JLabel();

        setPreferredSize(new java.awt.Dimension(451, 142));

        betaLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzBetaPanel.class, "WattsStrogatzBetaPanel.betaLabel.text")); // NOI18N

        betaField.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzBetaPanel.class, "WattsStrogatzBetaPanel.betaField.text")); // NOI18N

        NField.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzBetaPanel.class, "WattsStrogatzBetaPanel.NField.text")); // NOI18N

        KField.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzBetaPanel.class, "WattsStrogatzBetaPanel.KField.text")); // NOI18N

        NLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzBetaPanel.class, "WattsStrogatzBetaPanel.NLabel.text")); // NOI18N

        KLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzBetaPanel.class, "WattsStrogatzBetaPanel.KLabel.text")); // NOI18N

        constraintsLabel.setText(org.openide.util.NbBundle.getMessage(WattsStrogatzBetaPanel.class, "WattsStrogatzBetaPanel.constraintsLabel.text")); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(constraintsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(NLabel)
                        .addComponent(KLabel)
                        .addComponent(betaLabel)))
                .addGap(25, 25, 25)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(KField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addComponent(betaField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE)
                    .addComponent(NField, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 131, Short.MAX_VALUE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(NField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(NLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(KField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(KLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(betaField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(betaLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(constraintsLabel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    protected javax.swing.JTextField KField;
    private javax.swing.JLabel KLabel;
    protected javax.swing.JTextField NField;
    private javax.swing.JLabel NLabel;
    protected javax.swing.JTextField betaField;
    private javax.swing.JLabel betaLabel;
    private javax.swing.JLabel constraintsLabel;
    // End of variables declaration//GEN-END:variables

}
