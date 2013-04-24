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
import com.eas.designer.application.indexer.IndexerQuery;
import com.eas.designer.explorer.FileChooser;
import com.eas.designer.explorer.project.PlatypusProject;
import com.eas.designer.explorer.project.PlatypusProjectSettings;
import java.util.HashSet;
import java.util.Set;
import org.openide.filesystems.FileObject;
import org.openide.util.Exceptions;

/**
 *
 * @author mg
 */
public class ProjectRunningCustomizer extends javax.swing.JPanel {

    protected final PlatypusProject project;
    protected final FileObject appRoot;
    protected final PlatypusProjectSettings projectSettings;
    protected final PlatypusSettings appSettings;
    /**
     * Creates new form ProjectRunningCustomizer
     */
    public ProjectRunningCustomizer(PlatypusProject aProject) throws Exception {
        initComponents();
        project = aProject;
        appRoot = aProject.getApplicationRoot();
        projectSettings = aProject.getSettings();
        appSettings = projectSettings.getAppSettings();
        if (appSettings.getRunElement() != null) {
            txtRunPath.setText(projectSettings.getAppSettings().getRunElement());
        }
        chDbAppSources.setSelected(projectSettings.isDbAppSources());
        chAppServerMode.setSelected(projectSettings.isUseAppServer());
        
        if (projectSettings.getRunUser() != null) {
            txtUserName.setText(projectSettings.getRunUser());
        }
        if (projectSettings.getRunPassword() != null) {
            txtPassword.setText(projectSettings.getRunPassword());
        }
        if (projectSettings.getRunClientOptions() != null) {
            txtClientOptions.setText(projectSettings.getRunClientOptions());
        }
        if (projectSettings.getServerProtocol() != null) {
            cbProtocol.setSelectedItem(projectSettings.getServerProtocol());
        }
        if (projectSettings.getServerHost() != null) {
            txtServerHost.setText(projectSettings.getServerHost());
        }
        spServerPort.setValue(projectSettings.getServerPort()); 
        if (projectSettings.getRunServerOptions() != null) {
            txtServerOptions.setText(projectSettings.getRunServerOptions());
        }
        chStartServer.setSelected(projectSettings.isStartServer());
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        txtRunPath = new javax.swing.JTextField();
        lblRunPath = new javax.swing.JLabel();
        btnBrowse = new javax.swing.JButton();
        tabbedPane1 = new javax.swing.JTabbedPane();
        clientPanel = new javax.swing.JPanel();
        txtUserName = new javax.swing.JTextField();
        txtClientOptions = new javax.swing.JTextField();
        lblPassword = new javax.swing.JLabel();
        lblUserName = new javax.swing.JLabel();
        lblClientOptions = new javax.swing.JLabel();
        txtPassword = new javax.swing.JPasswordField();
        serverPanel = new javax.swing.JPanel();
        lblHost = new javax.swing.JLabel();
        txtServerHost = new javax.swing.JTextField();
        lblServerOptions = new javax.swing.JLabel();
        txtServerOptions = new javax.swing.JTextField();
        lblProtocol = new javax.swing.JLabel();
        cbProtocol = new javax.swing.JComboBox();
        lblServerPort = new javax.swing.JLabel();
        spServerPort = new javax.swing.JSpinner();
        chStartServer = new javax.swing.JCheckBox();
        chDbAppSources = new javax.swing.JCheckBox();
        chAppServerMode = new javax.swing.JCheckBox();

        txtRunPath.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtRunPathActionPerformed(evt);
            }
        });
        txtRunPath.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtRunPathFocusLost(evt);
            }
        });

        lblRunPath.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblRunPath.text")); // NOI18N

        btnBrowse.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.btnBrowse.text")); // NOI18N
        btnBrowse.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnBrowseActionPerformed(evt);
            }
        });

        txtUserName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtUserNameActionPerformed(evt);
            }
        });
        txtUserName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtUserNameFocusLost(evt);
            }
        });

        txtClientOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtClientOptionsActionPerformed(evt);
            }
        });
        txtClientOptions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtClientOptionsFocusLost(evt);
            }
        });

        lblPassword.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblPassword.text")); // NOI18N

        lblUserName.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblUserName.text")); // NOI18N

        lblClientOptions.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblClientOptions.text")); // NOI18N

        txtPassword.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtPasswordActionPerformed(evt);
            }
        });
        txtPassword.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtPasswordFocusLost(evt);
            }
        });

        javax.swing.GroupLayout clientPanelLayout = new javax.swing.GroupLayout(clientPanel);
        clientPanel.setLayout(clientPanelLayout);
        clientPanelLayout.setHorizontalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblPassword)
                    .addComponent(lblClientOptions)
                    .addComponent(lblUserName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtUserName)
                    .addComponent(txtClientOptions)
                    .addComponent(txtPassword, javax.swing.GroupLayout.DEFAULT_SIZE, 475, Short.MAX_VALUE))
                .addContainerGap())
        );
        clientPanelLayout.setVerticalGroup(
            clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(clientPanelLayout.createSequentialGroup()
                .addGap(11, 11, 11)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblUserName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblPassword))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(clientPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtClientOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblClientOptions))
                .addContainerGap(77, Short.MAX_VALUE))
        );

        tabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.clientPanel.TabConstraints.tabTitle"), clientPanel); // NOI18N

        lblHost.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblHost.text")); // NOI18N

        txtServerHost.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServerHostActionPerformed(evt);
            }
        });
        txtServerHost.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServerHostFocusLost(evt);
            }
        });

        lblServerOptions.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServerOptions.text")); // NOI18N

        txtServerOptions.setToolTipText(""); // NOI18N
        txtServerOptions.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                txtServerOptionsActionPerformed(evt);
            }
        });
        txtServerOptions.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                txtServerOptionsFocusLost(evt);
            }
        });

        lblProtocol.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblProtocol.text")); // NOI18N

        cbProtocol.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "platypus", "http", "https" }));
        cbProtocol.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cbProtocolActionPerformed(evt);
            }
        });
        cbProtocol.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                cbProtocolFocusLost(evt);
            }
        });

        lblServerPort.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.lblServerPort.text")); // NOI18N

        spServerPort.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spServerPortStateChanged(evt);
            }
        });
        spServerPort.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusLost(java.awt.event.FocusEvent evt) {
                spServerPortFocusLost(evt);
            }
        });

        chStartServer.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.chStartServer.text")); // NOI18N
        chStartServer.setActionCommand("");
        chStartServer.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chStartServerActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout serverPanelLayout = new javax.swing.GroupLayout(serverPanel);
        serverPanel.setLayout(serverPanelLayout);
        serverPanelLayout.setHorizontalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblHost)
                    .addComponent(chStartServer)
                    .addGroup(serverPanelLayout.createSequentialGroup()
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lblProtocol)
                            .addComponent(lblServerPort)
                            .addComponent(lblServerOptions))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(txtServerOptions, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 483, Short.MAX_VALUE)
                            .addGroup(serverPanelLayout.createSequentialGroup()
                                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 80, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(cbProtocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(0, 0, Short.MAX_VALUE))
                            .addComponent(txtServerHost))))
                .addContainerGap())
        );
        serverPanelLayout.setVerticalGroup(
            serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, serverPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblProtocol)
                    .addComponent(cbProtocol, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblHost)
                    .addComponent(txtServerHost, javax.swing.GroupLayout.PREFERRED_SIZE, 27, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(5, 5, 5)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblServerPort)
                    .addComponent(spServerPort, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(serverPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtServerOptions, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblServerOptions))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(chStartServer)
                .addContainerGap(27, Short.MAX_VALUE))
        );

        tabbedPane1.addTab(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.serverPanel.TabConstraints.tabTitle"), serverPanel); // NOI18N

        chDbAppSources.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.chDbAppSources.text")); // NOI18N
        chDbAppSources.setActionCommand("");
        chDbAppSources.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chDbAppSourcesActionPerformed(evt);
            }
        });

        chAppServerMode.setText(org.openide.util.NbBundle.getMessage(ProjectRunningCustomizer.class, "ProjectRunningCustomizer.chAppServerMode.text")); // NOI18N
        chAppServerMode.setActionCommand("");
        chAppServerMode.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                chAppServerModeActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tabbedPane1)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(lblRunPath)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(txtRunPath, javax.swing.GroupLayout.PREFERRED_SIZE, 296, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnBrowse, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(chDbAppSources)
                            .addComponent(chAppServerMode))
                        .addContainerGap())))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtRunPath, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblRunPath)
                    .addComponent(btnBrowse))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chDbAppSources)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chAppServerMode)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(tabbedPane1)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void txtRunPathActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtRunPathActionPerformed
        projectSettings.getAppSettings().setRunElement(txtRunPath.getText());
    }//GEN-LAST:event_txtRunPathActionPerformed

    private void btnBrowseActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnBrowseActionPerformed
        try {
            FileObject selectedFile = null;// TODO Rework app element selector
            Set<String> allowedTypes = new HashSet<>();
            allowedTypes.add("text/javascript");
            FileObject newSelectedFile = FileChooser.selectFile(appRoot, selectedFile, allowedTypes);
            if (newSelectedFile != null && newSelectedFile != selectedFile) {
                String appElementId = IndexerQuery.file2AppElementId(newSelectedFile);
                projectSettings.getAppSettings().setRunElement(appElementId);
                txtRunPath.setText(appElementId);
            }
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }//GEN-LAST:event_btnBrowseActionPerformed

    private void txtRunPathFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtRunPathFocusLost
        appSettings.setRunElement(txtRunPath.getText());
    }//GEN-LAST:event_txtRunPathFocusLost

    private void txtClientOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtClientOptionsFocusLost
        projectSettings.setClientOptions(txtClientOptions.getText());
    }//GEN-LAST:event_txtClientOptionsFocusLost

    private void txtClientOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtClientOptionsActionPerformed
        projectSettings.setClientOptions(txtClientOptions.getText());
    }//GEN-LAST:event_txtClientOptionsActionPerformed

    private void txtUserNameFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtUserNameFocusLost
        projectSettings.setRunUser(txtUserName.getText());
    }//GEN-LAST:event_txtUserNameFocusLost

    private void chDbAppSourcesActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chDbAppSourcesActionPerformed
        projectSettings.setDbAppSources(chDbAppSources.isSelected());
    }//GEN-LAST:event_chDbAppSourcesActionPerformed

    private void chAppServerModeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chAppServerModeActionPerformed
        projectSettings.setUseAppServer(chAppServerMode.isSelected());
    }//GEN-LAST:event_chAppServerModeActionPerformed

    private void txtUserNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtUserNameActionPerformed
        projectSettings.setRunUser(txtUserName.getText());
    }//GEN-LAST:event_txtUserNameActionPerformed

    private void txtPasswordActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtPasswordActionPerformed
        projectSettings.setRunPassword(new String(txtPassword.getPassword()));
    }//GEN-LAST:event_txtPasswordActionPerformed

    private void txtPasswordFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtPasswordFocusLost
        projectSettings.setRunPassword(new String(txtPassword.getPassword()));
    }//GEN-LAST:event_txtPasswordFocusLost

    private void txtServerHostActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServerHostActionPerformed
        projectSettings.setServerHost(txtServerHost.getText());
    }//GEN-LAST:event_txtServerHostActionPerformed

    private void txtServerHostFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServerHostFocusLost
        projectSettings.setServerHost(txtServerHost.getText());
    }//GEN-LAST:event_txtServerHostFocusLost

    private void txtServerOptionsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_txtServerOptionsActionPerformed
        projectSettings.setServerOptions(txtServerOptions.getText());
    }//GEN-LAST:event_txtServerOptionsActionPerformed

    private void txtServerOptionsFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_txtServerOptionsFocusLost
        projectSettings.setServerOptions(txtServerOptions.getText());
    }//GEN-LAST:event_txtServerOptionsFocusLost

    private void chStartServerActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_chStartServerActionPerformed
        projectSettings.setStartServer(chStartServer.isSelected());
    }//GEN-LAST:event_chStartServerActionPerformed

    private void cbProtocolActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cbProtocolActionPerformed
        projectSettings.setServerProtocol(String.valueOf(cbProtocol.getSelectedItem()));
    }//GEN-LAST:event_cbProtocolActionPerformed

    private void cbProtocolFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_cbProtocolFocusLost
        projectSettings.setServerProtocol(String.valueOf(cbProtocol.getSelectedItem()));
    }//GEN-LAST:event_cbProtocolFocusLost

    private void spServerPortFocusLost(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_spServerPortFocusLost
        projectSettings.setServerPort((Integer)spServerPort.getValue());
    }//GEN-LAST:event_spServerPortFocusLost

    private void spServerPortStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spServerPortStateChanged
        projectSettings.setServerPort((Integer)spServerPort.getValue());
    }//GEN-LAST:event_spServerPortStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnBrowse;
    private javax.swing.JComboBox cbProtocol;
    private javax.swing.JCheckBox chAppServerMode;
    private javax.swing.JCheckBox chDbAppSources;
    private javax.swing.JCheckBox chStartServer;
    private javax.swing.JPanel clientPanel;
    private javax.swing.JLabel lblClientOptions;
    private javax.swing.JLabel lblHost;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblProtocol;
    private javax.swing.JLabel lblRunPath;
    private javax.swing.JLabel lblServerOptions;
    private javax.swing.JLabel lblServerPort;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JPanel serverPanel;
    private javax.swing.JSpinner spServerPort;
    private javax.swing.JTabbedPane tabbedPane1;
    private javax.swing.JTextField txtClientOptions;
    private javax.swing.JPasswordField txtPassword;
    private javax.swing.JTextField txtRunPath;
    private javax.swing.JTextField txtServerHost;
    private javax.swing.JTextField txtServerOptions;
    private javax.swing.JTextField txtUserName;
    // End of variables declaration//GEN-END:variables

}
