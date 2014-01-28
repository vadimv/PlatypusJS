/*
 * LoginFrame.java
 *
 * Created on Jun 25, 2009, 11:19:43 AM
 */
package com.eas.client.login;

import com.eas.client.Client;
import com.eas.client.ClientFactory;
import com.eas.client.settings.ConnectionSettings;
import com.eas.client.settings.PlatypusConnectionSettings;
import com.eas.util.exceptions.ExceptionListenerSupport;
import com.eas.util.exceptions.ExceptionThrower;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.beans.ExceptionListener;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.security.auth.login.FailedLoginException;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

/**
 *
 * @author pk
 */
public class LoginFrame extends javax.swing.JDialog implements ExceptionThrower {

    /**
     * A return status code - returned if Cancel button has been pressed
     */
    public static final int RET_CANCEL = 0;
    /**
     * A return status code - returned if OK button has been pressed
     */
    public static final ResourceBundle bundle = ResourceBundle.getBundle("com/eas/client/login/Bundle");
    public static final int RET_OK = 1;
    private static final String CANCEL_ACTION_ID = "cancel";
    private static final String OK_ACTION_ID = "ok";
    private int returnStatus = RET_CANCEL;
    private final ExceptionListenerSupport exSupport = new ExceptionListenerSupport();
    private final LoginAction loginAction = new LoginAction();
    private final NewConnectionAction newConnectionAction = new NewConnectionAction();
    private final ModifyConnectionAction modifyConnectionAction = new ModifyConnectionAction();
    private final ToggleConnectionsVisibility toggleConnectionsVisibilityAction = new ToggleConnectionsVisibility();
    private final DeleteConnectionAction deleteConnectionAction = new DeleteConnectionAction();
    private final ConnectionsSelectionListener connectionsSelectionListener = new ConnectionsSelectionListener();
    private final ConnectionsListModel connectionsListModel;
    private int connectionsPanelHeight;
    private final LoginCallback loginCallback;
    private Client client;
    protected String defaultUrl;
    protected String user;
    protected char[] password;

    /**
     * User's login and connection selection dialog.
     *
     * @param aUrl preset URL
     * @param aUser preset application user name
     * @param aPassword preset password
     * @param aLoginCallback callback for login action
     * @throws Exception if login failed
     */
    public LoginFrame(String aUrl, String aUser, char[] aPassword, LoginCallback aLoginCallback) throws Exception {
        super((java.awt.Frame) null, true);
        defaultUrl = aUrl;
        user = aUser;
        password = aPassword;
        loginCallback = aLoginCallback;
        connectionsListModel = new ConnectionsListModel();
        initComponents();
        if (!isFullModeLogin()) {
            hideConnectionUI();
        }
        getRootPane().setDefaultButton(btnOk);
        Action cancelAction = new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) {
                btnCancelActionPerformed(e);
            }
        };
        tfUserName.getActionMap().put(OK_ACTION_ID, loginAction);
        tfUserName.getActionMap().put(CANCEL_ACTION_ID, cancelAction);
        tfPassword.getActionMap().put(OK_ACTION_ID, loginAction);
        tfPassword.getActionMap().put(CANCEL_ACTION_ID, cancelAction);
        tfUserName.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), OK_ACTION_ID);
        tfUserName.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_ID);
        tfPassword.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), OK_ACTION_ID);
        tfPassword.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), CANCEL_ACTION_ID);
        if (isFullModeLogin()) {
            lstConnections.setCellRenderer(connectionsListModel.getCellRenderer());
            lstConnections.clearSelection();
            lstConnections.addListSelectionListener(connectionsSelectionListener);
            toggleConnectionsVisibilityAction.actionPerformed(null);
        }
    }

    private void hideConnectionUI() {
        btnToggleConnections.setVisible(false);
        pnlConnectionInfo.setVisible(false);
    }

    private void doClose(int retStatus) {
        defaultUrl = null;
        user = null;
        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = 0;
            }
        }
        password = null;
        returnStatus = retStatus;
        setVisible(false);
        dispose();
    }

    private boolean isFullModeLogin() {
        return defaultUrl == null || defaultUrl.isEmpty();
    }

    public Client getClient() {
        return client;
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlLogin = new javax.swing.JPanel();
        pnlAppLogin = new javax.swing.JPanel();
        tfUserName = new javax.swing.JTextField();
        lblUserName = new javax.swing.JLabel();
        tfPassword = new javax.swing.JPasswordField();
        lblPassword = new javax.swing.JLabel();
        checkRememberPassword = new javax.swing.JCheckBox();
        pnlConnectionInfo = new javax.swing.JPanel();
        lblConnections = new javax.swing.JLabel();
        scrollConnections = new javax.swing.JScrollPane();
        lstConnections = new javax.swing.JList();
        btnNewConnection = new javax.swing.JButton();
        btnDeleteConnection = new javax.swing.JButton();
        btnModifyConnection = new javax.swing.JButton();
        pnlBottom = new javax.swing.JPanel();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnToggleConnections = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(bundle.getString("LoginDialog.title")); // NOI18N
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        setIconImage(new javax.swing.ImageIcon(getClass().getResource("/com/eas/client/login/key.png")).getImage());
        setLocationByPlatform(true);
        setName("loginFrame"); // NOI18N
        addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                formKeyTyped(evt);
            }
        });

        lblUserName.setLabelFor(tfUserName);
        lblUserName.setText(bundle.getString("LoginDialog.lblUserName.text")); // NOI18N

        lblPassword.setLabelFor(tfPassword);
        lblPassword.setText(bundle.getString("LoginDialog.lblPassword.text")); // NOI18N

        checkRememberPassword.setText(bundle.getString("checkRememberPassword")); // NOI18N

        javax.swing.GroupLayout pnlAppLoginLayout = new javax.swing.GroupLayout(pnlAppLogin);
        pnlAppLogin.setLayout(pnlAppLoginLayout);
        pnlAppLoginLayout.setHorizontalGroup(
            pnlAppLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppLoginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAppLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlAppLoginLayout.createSequentialGroup()
                        .addGroup(pnlAppLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblPassword, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(lblUserName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(pnlAppLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(tfUserName)
                            .addComponent(tfPassword))
                        .addContainerGap())
                    .addGroup(pnlAppLoginLayout.createSequentialGroup()
                        .addComponent(checkRememberPassword)
                        .addGap(17, 345, Short.MAX_VALUE))))
        );
        pnlAppLoginLayout.setVerticalGroup(
            pnlAppLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppLoginLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlAppLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblUserName)
                    .addComponent(tfUserName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlAppLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblPassword)
                    .addComponent(tfPassword, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(18, 18, 18)
                .addComponent(checkRememberPassword, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(10, 10, 10))
        );

        javax.swing.GroupLayout pnlLoginLayout = new javax.swing.GroupLayout(pnlLogin);
        pnlLogin.setLayout(pnlLoginLayout);
        pnlLoginLayout.setHorizontalGroup(
            pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLoginLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(pnlAppLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        pnlLoginLayout.setVerticalGroup(
            pnlLoginLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlAppLogin, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        getContentPane().add(pnlLogin, java.awt.BorderLayout.NORTH);

        lblConnections.setText(bundle.getString("LoginDialog.lblConnections.text")); // NOI18N

        lstConnections.setModel(connectionsListModel);
        scrollConnections.setViewportView(lstConnections);

        btnNewConnection.setAction(newConnectionAction);
        btnNewConnection.setText(bundle.getString("LoginDialog.btnNewConnection.text")); // NOI18N
        btnNewConnection.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btnDeleteConnection.setAction(deleteConnectionAction);
        btnDeleteConnection.setText(bundle.getString("LoginDialog.btnDeleteConnection.text")); // NOI18N
        btnDeleteConnection.setMargin(new java.awt.Insets(2, 2, 2, 2));

        btnModifyConnection.setAction(modifyConnectionAction);
        btnModifyConnection.setText(bundle.getString("LoginDialog.btnModifyConnection.text")); // NOI18N
        btnModifyConnection.setMargin(new java.awt.Insets(2, 2, 2, 2));

        javax.swing.GroupLayout pnlConnectionInfoLayout = new javax.swing.GroupLayout(pnlConnectionInfo);
        pnlConnectionInfo.setLayout(pnlConnectionInfoLayout);
        pnlConnectionInfoLayout.setHorizontalGroup(
            pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlConnectionInfoLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(lblConnections)
                    .addComponent(scrollConnections, javax.swing.GroupLayout.DEFAULT_SIZE, 388, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(btnNewConnection, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnDeleteConnection, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(btnModifyConnection, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 86, Short.MAX_VALUE))
                .addContainerGap())
        );
        pnlConnectionInfoLayout.setVerticalGroup(
            pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlConnectionInfoLayout.createSequentialGroup()
                .addComponent(lblConnections)
                .addGroup(pnlConnectionInfoLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlConnectionInfoLayout.createSequentialGroup()
                        .addGap(2, 2, 2)
                        .addComponent(btnNewConnection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnModifyConnection)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnDeleteConnection))
                    .addGroup(pnlConnectionInfoLayout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scrollConnections, javax.swing.GroupLayout.DEFAULT_SIZE, 150, Short.MAX_VALUE)))
                .addContainerGap())
        );

        getContentPane().add(pnlConnectionInfo, java.awt.BorderLayout.CENTER);

        btnOk.setAction(loginAction);
        btnOk.setText(bundle.getString("Dialog.OKButton.text")); // NOI18N

        btnCancel.setText(bundle.getString("Dialog.CancelButton.text")); // NOI18N
        btnCancel.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnCancelActionPerformed(evt);
            }
        });

        btnToggleConnections.setAction(toggleConnectionsVisibilityAction);
        btnToggleConnections.setText(">>>");

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnToggleConnections, javax.swing.GroupLayout.PREFERRED_SIZE, 68, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 243, Short.MAX_VALUE)
                .addComponent(btnOk, javax.swing.GroupLayout.PREFERRED_SIZE, 74, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCancel, javax.swing.GroupLayout.PREFERRED_SIZE, 89, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        pnlBottomLayout.setVerticalGroup(
            pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlBottomLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCancel)
                    .addComponent(btnToggleConnections)
                    .addComponent(btnOk))
                .addContainerGap())
        );

        getContentPane().add(pnlBottom, java.awt.BorderLayout.SOUTH);

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnCancelActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_btnCancelActionPerformed
    {//GEN-HEADEREND:event_btnCancelActionPerformed
        doClose(RET_CANCEL);
    }//GEN-LAST:event_btnCancelActionPerformed

    private void formKeyTyped(java.awt.event.KeyEvent evt)//GEN-FIRST:event_formKeyTyped
    {//GEN-HEADEREND:event_formKeyTyped
        if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
            loginAction.actionPerformed(new ActionEvent(this, 0, null));
        }
        if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
            btnCancelActionPerformed(new ActionEvent(this, 0, null));
        }
    }//GEN-LAST:event_formKeyTyped

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDeleteConnection;
    private javax.swing.JButton btnModifyConnection;
    private javax.swing.JButton btnNewConnection;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnToggleConnections;
    private javax.swing.JCheckBox checkRememberPassword;
    private javax.swing.JLabel lblConnections;
    private javax.swing.JLabel lblPassword;
    private javax.swing.JLabel lblUserName;
    private javax.swing.JList lstConnections;
    private javax.swing.JPanel pnlAppLogin;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlConnectionInfo;
    private javax.swing.JPanel pnlLogin;
    private javax.swing.JScrollPane scrollConnections;
    private javax.swing.JPasswordField tfPassword;
    private javax.swing.JTextField tfUserName;
    // End of variables declaration//GEN-END:variables

    /**
     * @return the returnStatus
     */
    public int getReturnStatus() {
        return returnStatus;
    }

    @Override
    public void addExceptionListener(ExceptionListener l) {
        exSupport.addExceptionListener(l);
    }

    @Override
    public void removeExceptionListener(ExceptionListener l) {
        exSupport.removeExceptionListener(l);
    }

    private boolean login() throws Exception {
        if (!isFullModeLogin()) {
            return loginCallback.tryToLogin(defaultUrl, tfUserName.getText(), tfPassword.getPassword());
        } else if (!lstConnections.isSelectionEmpty()) {
            ConnectionSettings settings = (ConnectionSettings)lstConnections.getSelectedValue();
            return loginCallback.tryToLogin(settings.getUrl(), tfUserName.getText(), tfPassword.getPassword());
        } else {
            return false;
        }
    }

    public void updatePreferences() {
        try {
            Preferences connectionsPref = Preferences.userRoot().node(ClientFactory.CONNECTIONS_SETTINGS_NODE);
            connectionsPref.removeNode();
            connectionsPref = Preferences.userRoot().node(ClientFactory.CONNECTIONS_SETTINGS_NODE);
            for (int i = 0; i < connectionsListModel.getSize(); i++) {
                ConnectionSettings settings = (ConnectionSettings) connectionsListModel.getElementAt(i);
                if (settings.isEditable()) {
                    String strIndex = String.valueOf(i);
                    connectionsPref.node(strIndex).put(ClientFactory.CONNECTION_TITLE_SETTING, settings.getName() != null ? settings.getName() : "");
                    connectionsPref.node(strIndex).put(ClientFactory.CONNECTION_URL_SETTING, settings.getUrl() != null ? settings.getUrl() : "");
                    connectionsPref.node(strIndex).put(ClientFactory.CONNECTION_USER_SETTING, settings.getUser() != null ? settings.getUser() : "");
                    connectionsPref.node(strIndex).put(ClientFactory.CONNECTION_PASSWORD_SETTING, settings.getPassword() != null ? settings.getPassword() : "");
                }
            }
        } catch (BackingStoreException ex) {
            exSupport.exceptionThrown(ex);
        }
    }

    public void setSelectedConnectionIndex(int aIndex) {
        lstConnections.setSelectedIndex(aIndex);
    }

    public int getSelectedConnectionIndex() {
        return lstConnections.getSelectedIndex();
    }

    public void setUserPassword(String aPassword) {
        tfPassword.setText(aPassword);
        checkRememberPassword.setSelected(aPassword != null && !aPassword.isEmpty());
    }

    public String getUserPassword() {
        if (checkRememberPassword.isSelected()) {
            return new String(tfPassword.getPassword());
        } else {
            return null;
        }
    }

    public void selectDefaultSettings() {
        if (ClientFactory.getDefaultSettings() != null) {
            lstConnections.setSelectedValue(ClientFactory.getDefaultSettings(), true);
        }
    }

    private class LoginAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                if (login()) {
                    if (isFullModeLogin()) {
                        if (checkRememberPassword.isSelected()) {
                            ConnectionSettings settings = (ConnectionSettings) lstConnections.getSelectedValue();
                            if (settings != null) {
                                settings.setUser(tfUserName.getText());
                                settings.setPassword(String.valueOf(tfPassword.getPassword()));
                            }
                        }
                        updatePreferences();
                    }
                    doClose(RET_OK);
                }
            } catch (Exception ex) {
                if (ex instanceof FailedLoginException) {
                    Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, bundle.getString("LoginDialog.LoginFailedMessage"));
                    JOptionPane.showMessageDialog(LoginFrame.this, bundle.getString("LoginDialog.LoginFailedMessage"), bundle.getString("LoginDialog.title"), JOptionPane.ERROR_MESSAGE);
                } else {
                    Logger.getLogger(LoginFrame.class.getName()).log(Level.SEVERE, "{0} ({1})", new Object[]{bundle.getString("LoginDialog.CannotLoginMessage"), ex.getLocalizedMessage()});
                    JOptionPane.showMessageDialog(LoginFrame.this, bundle.getString("LoginDialog.CannotLoginMessage") + String.format(" (%s)", ex.getLocalizedMessage()), bundle.getString("LoginDialog.title"), JOptionPane.ERROR_MESSAGE);
                }
            }
        }
    }

    private class NewConnectionAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                ConnectionSettingsDialog dlg = new ConnectionSettingsDialog(null, true);
                dlg.setUrl("platypus://<host>:<port>");
                dlg.setVisible(true);
                if (dlg.getReturnStatus() == ConnectionSettingsDialog.RET_OK) {
                    int saveIndex = lstConnections.getSelectedIndex();
                    if (saveIndex < 0) {
                        saveIndex = connectionsListModel.getSize();
                    }
                    String url = dlg.getUrl();
                    String name = dlg.getConnectionName();
                    ConnectionSettings settings = new PlatypusConnectionSettings();
                    settings.setUrl(url);
                    if (name != null && !name.isEmpty()) {
                        settings.setName(name);
                    }
                    connectionsListModel.putElementAt(saveIndex, settings);
                    lstConnections.setSelectedIndex(saveIndex);
                    updatePreferences();
                    lstConnections.requestFocus();
                }
            } catch (Throwable t) {
                assert exSupport != null;
                exSupport.exceptionThrown(new Exception(t));
            }
        }
    }

    private class ModifyConnectionAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int selectedIndex = lstConnections.getSelectedIndex();
                assert selectedIndex >= 0; //we won't be enabled otherwise
                ConnectionSettings settings = (ConnectionSettings) connectionsListModel.getElementAt(selectedIndex);
                ConnectionSettingsDialog dlg = new ConnectionSettingsDialog(null, true);
                dlg.setUrl(settings.getUrl());
                dlg.setConnectionName(settings.getName());
                dlg.setVisible(true);
                int retVal = dlg.getReturnStatus();
                if (retVal == ConnectionSettingsDialog.RET_OK) {
                    settings.setUrl(dlg.getUrl());
                    settings.setName(dlg.getConnectionName());
                    connectionsListModel.fireContentsChanged(selectedIndex);
                    updatePreferences();
                    lstConnections.requestFocus();
                }
            } catch (Throwable t) {
                exSupport.exceptionThrown(new Exception(t));
            }
        }
    }

    private class DeleteConnectionAction extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                int selectedIndex = lstConnections.getSelectedIndex();
                assert selectedIndex >= 0; //we won't be enabled otherwise
                ConnectionSettings settings = (ConnectionSettings) connectionsListModel.getElementAt(selectedIndex);
                int choice = JOptionPane.showConfirmDialog(LoginFrame.this, bundle.getString("LoginDialog.ConnectionDeletionConfirmationMessage") + settings.getUrl(), bundle.getString("LoginDialog.ConnectionDeletionConfirmationTitle"), JOptionPane.YES_NO_OPTION);
                if (choice == JOptionPane.YES_OPTION) {
                    connectionsListModel.removeElementAt(selectedIndex);
                    updatePreferences();
                    lstConnections.setSelectedIndex(selectedIndex);
                    lstConnections.requestFocus();
                }
            } catch (Throwable t) {
                exSupport.exceptionThrown(new Exception(t));
            }
        }
    }

    private void updateCredentialsControls() {
        ConnectionSettings selectedSettings = (ConnectionSettings) lstConnections.getSelectedValue();
        if (selectedSettings != null) {
            tfUserName.setText(selectedSettings.getUser());
            tfPassword.setText(selectedSettings.getPassword());
        }
        if (tfUserName.getText() != null && !tfUserName.getText().isEmpty()) {
            tfPassword.requestFocus();
        }
    }

    private class ConnectionsSelectionListener implements ListSelectionListener {

        @Override
        public void valueChanged(ListSelectionEvent e) {
            if (!e.getValueIsAdjusting() && isFullModeLogin()) {
                ConnectionSettings selectedSettings = (ConnectionSettings) lstConnections.getSelectedValue();
                boolean modificationsEnabled = selectedSettings != null && selectedSettings.isEditable();
                modifyConnectionAction.setEnabled(modificationsEnabled);
                deleteConnectionAction.setEnabled(modificationsEnabled);
                updateCredentialsControls();
                lstConnections.requestFocus();
            }
        }
    }

    private class ToggleConnectionsVisibility extends AbstractAction {

        @Override
        public void actionPerformed(ActionEvent e) {
            if (pnlConnectionInfo.isVisible()) {
                // make it invisible and shrink frame bounds.
                connectionsPanelHeight = pnlConnectionInfo.getSize().height;
                pnlConnectionInfo.setVisible(false);
                setSize(getSize().width, getSize().height - connectionsPanelHeight);
                btnToggleConnections.setText(">>>");
            } else {
                // make it visible and enlarge frame bounds.
                pnlConnectionInfo.setVisible(true);
                setSize(getSize().width, getSize().height + connectionsPanelHeight);
                btnToggleConnections.setText("<<<");
            }
        }
    }
}
