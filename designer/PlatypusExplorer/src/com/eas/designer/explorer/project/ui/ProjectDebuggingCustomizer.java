/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ProjectRunningCustomizer.java
 *
 * Created on 17.03.2011, 18:07:31
 */
package com.eas.designer.explorer.project.ui;

import com.eas.deploy.project.PlatypusSettings;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.designer.explorer.project.PlatypusProjectSettings;

/**
 *
 * @author mg
 */
public class ProjectDebuggingCustomizer extends javax.swing.JPanel {

    protected PlatypusProjectSettings projectSettings;

    /**
     * Creates new form ProjectRunningCustomizer
     */
    public ProjectDebuggingCustomizer(PlatypusProject aProject) throws Exception {
        initComponents();
        projectSettings = aProject.getSettings();
        spClientDebugPort.setValue(projectSettings.getDebugClientPort());
        spServerDebugPort.setValue(projectSettings.getDebugServerPort());    
    }
    
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        lblClientDebugPort = new javax.swing.JLabel();
        lblServerDebugPort = new javax.swing.JLabel();
        spClientDebugPort = new javax.swing.JSpinner();
        spServerDebugPort = new javax.swing.JSpinner();

        lblClientDebugPort.setText(org.openide.util.NbBundle.getMessage(ProjectDebuggingCustomizer.class, "ProjectDebuggingCustomizer.lblClientDebugPort.text")); // NOI18N

        lblServerDebugPort.setText(org.openide.util.NbBundle.getMessage(ProjectDebuggingCustomizer.class, "ProjectDebuggingCustomizer.lblServerDebugPort.text")); // NOI18N

        spClientDebugPort.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spClientDebugPortStateChanged(evt);
            }
        });
        spClientDebugPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spClientDebugPortFocusLost(evt);
            }
        });

        spServerDebugPort.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spServerDebugPortStateChanged(evt);
            }
        });
        spServerDebugPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spServerDebugPortFocusLost(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblClientDebugPort)
                    .addComponent(lblServerDebugPort))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(spClientDebugPort, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)
                    .addComponent(spServerDebugPort))
                .addContainerGap(353, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblClientDebugPort)
                    .addComponent(spClientDebugPort, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerDebugPort)
                    .addComponent(spServerDebugPort, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(231, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents

    private void spClientDebugPortStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spClientDebugPortStateChanged
        projectSettings.setDebugClientPort((int)spClientDebugPort.getValue());
    }//GEN-LAST:event_spClientDebugPortStateChanged

    private void spServerDebugPortStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spServerDebugPortStateChanged
        projectSettings.setDebugServerPort((int)spServerDebugPort.getValue());
    }//GEN-LAST:event_spServerDebugPortStateChanged

    private void spServerDebugPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spServerDebugPortFocusLost
        projectSettings.setDebugServerPort((int)spServerDebugPort.getValue());
    }//GEN-LAST:event_spServerDebugPortFocusLost

    private void spClientDebugPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spClientDebugPortFocusLost
        projectSettings.setDebugClientPort((int)spClientDebugPort.getValue());
    }//GEN-LAST:event_spClientDebugPortFocusLost

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel lblClientDebugPort;
    private javax.swing.JLabel lblServerDebugPort;
    private javax.swing.JSpinner spClientDebugPort;
    private javax.swing.JSpinner spServerDebugPort;
    // End of variables declaration//GEN-END:variables

}
