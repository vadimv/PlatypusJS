/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.result;

import com.bearsoft.rowset.metadata.DataTypeInfo;
import com.bearsoft.rowset.metadata.Parameter;
import com.bearsoft.rowset.metadata.Parameters;
import com.eas.client.model.application.ApplicationDbModel;
import com.eas.dbcontrols.grid.EntityFieldsGrid;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import com.eas.designer.application.query.editing.SqlTextEditsComplementor;
import com.eas.designer.application.query.lexer.SqlLanguageHierarchy;
import com.eas.designer.application.query.result.QueryResultsView.PageSizeItem;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dialog;
import java.awt.event.ItemEvent;
import java.io.StringReader;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComponent;
import javax.swing.JEditorPane;
import javax.swing.JScrollPane;
import javax.swing.event.UndoableEditEvent;
import javax.swing.event.UndoableEditListener;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.NamedParameter;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.statement.Statement;
import org.netbeans.modules.editor.NbEditorDocument;
import org.openide.DialogDescriptor;
import org.openide.text.CloneableEditorSupport;
import org.openide.text.NbDocument;
import org.openide.util.NbBundle;
import org.openide.util.NbPreferences;

/**
 *
 * @author vv
 */
public class QuerySetupView extends javax.swing.JPanel {

    protected String SAVE_PARAMS_VAULES_ENABLED_KEY = "saveParamsValues";//NOI18N
    protected QueryResultsView parentView;
    protected DialogDescriptor dialogDescriptor;
    protected Dialog dialog;
    protected ApplicationDbModel paramsModel;
    protected EntityFieldsGrid parametersGrid;
    protected Document sqlTextDocument;
    protected EditorKit editorKit = CloneableEditorSupport.getEditorKit(SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);
    protected CCJSqlParserManager parserManager = new CCJSqlParserManager();
    protected static final Logger logger = Logger.getLogger(QuerySetupView.class.getName());

    public QuerySetupView(QueryResultsView aParentView) throws Exception {
        parentView = aParentView;
        initComponents();
        initParametersView();
        initDocument();
        initSqlEditor();
        saveParamsCheckBox.setSelected(isSaveParamsValuesEnabled());
    }

    public final boolean isSaveParamsValuesEnabled() {
        return NbPreferences.forModule(QuerySetupView.class).getBoolean(SAVE_PARAMS_VAULES_ENABLED_KEY, true);
    }

    public final void setSaveParamsValuesEnabled(boolean val) {
        NbPreferences.forModule(QuerySetupView.class).putBoolean(SAVE_PARAMS_VAULES_ENABLED_KEY, val);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolBar = new javax.swing.JToolBar();
        toolbarRunButton = new javax.swing.JButton();
        pageSizeComboBox = new javax.swing.JComboBox<PageSizeItem>();
        saveParamsCheckBox = new javax.swing.JCheckBox();
        mainPane = new javax.swing.JSplitPane();
        topPanel = new javax.swing.JPanel();
        bottomPanel = new javax.swing.JPanel();
        sqlSourcePanel = new javax.swing.JPanel();
        scrollSqlPane = new javax.swing.JScrollPane();
        txtSqlPane = new javax.swing.JEditorPane();

        setPreferredSize(new java.awt.Dimension(460, 320));
        setLayout(new java.awt.BorderLayout());

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        toolbarRunButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/com/eas/designer/application/query/result/runsql.png"))); // NOI18N
        toolbarRunButton.setText(org.openide.util.NbBundle.getMessage(QuerySetupView.class, "QuerySetupView.toolbarRunButton.text")); // NOI18N
        toolbarRunButton.setToolTipText(org.openide.util.NbBundle.getMessage(QuerySetupView.class, "toolbarRunButton.Tooltip")); // NOI18N
        toolbarRunButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        toolbarRunButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolbarRunButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                toolbarRunButtonActionPerformed(evt);
            }
        });
        toolBar.add(toolbarRunButton);

        pageSizeComboBox.setModel(new DefaultComboBoxModel<>(parentView.getPageSizeItems()));
        pageSizeComboBox.setPreferredSize(new java.awt.Dimension(120, 27));
        pageSizeComboBox.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                pageSizeComboBoxItemStateChanged(evt);
            }
        });
        toolBar.add(pageSizeComboBox);

        saveParamsCheckBox.setText(org.openide.util.NbBundle.getMessage(QuerySetupView.class, "QuerySetupView.saveParamsCheckBox.text")); // NOI18N
        saveParamsCheckBox.setFocusable(false);
        saveParamsCheckBox.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        saveParamsCheckBox.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        saveParamsCheckBox.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        saveParamsCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveParamsCheckBoxActionPerformed(evt);
            }
        });
        toolBar.add(saveParamsCheckBox);

        add(toolBar, java.awt.BorderLayout.NORTH);

        mainPane.setDividerLocation(100);
        mainPane.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        topPanel.setLayout(new java.awt.BorderLayout());
        mainPane.setTopComponent(topPanel);

        bottomPanel.setLayout(new java.awt.BorderLayout());

        sqlSourcePanel.setLayout(new java.awt.BorderLayout());

        scrollSqlPane.setViewportView(txtSqlPane);

        sqlSourcePanel.add(scrollSqlPane, java.awt.BorderLayout.CENTER);

        bottomPanel.add(sqlSourcePanel, java.awt.BorderLayout.CENTER);

        mainPane.setRightComponent(bottomPanel);

        add(mainPane, java.awt.BorderLayout.CENTER);
    }// </editor-fold>//GEN-END:initComponents

    private void pageSizeComboBoxItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_pageSizeComboBoxItemStateChanged
        if (evt.getStateChange() == ItemEvent.SELECTED) {
            parentView.setPageSize(((PageSizeItem) evt.getItem()).getValue());
        }
    }//GEN-LAST:event_pageSizeComboBoxItemStateChanged

    private void toolbarRunButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_toolbarRunButtonActionPerformed
        ((JComponent) evt.getSource()).requestFocus();//changes focus to validate entered values
        dialogDescriptor.setValue(DialogDescriptor.OK_OPTION);
        dialog.setVisible(false);
    }//GEN-LAST:event_toolbarRunButtonActionPerformed

    private void saveParamsCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveParamsCheckBoxActionPerformed
        setSaveParamsValuesEnabled(saveParamsCheckBox.isSelected());
    }//GEN-LAST:event_saveParamsCheckBoxActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel bottomPanel;
    private javax.swing.JSplitPane mainPane;
    private javax.swing.JComboBox<PageSizeItem> pageSizeComboBox;
    private javax.swing.JCheckBox saveParamsCheckBox;
    private javax.swing.JScrollPane scrollSqlPane;
    private javax.swing.JPanel sqlSourcePanel;
    private javax.swing.JToolBar toolBar;
    private javax.swing.JButton toolbarRunButton;
    private javax.swing.JPanel topPanel;
    private javax.swing.JEditorPane txtSqlPane;
    // End of variables declaration//GEN-END:variables

    private void initParametersView() throws Exception {
        paramsModel = new ApplicationDbModel();
        paramsModel.setParameters(parentView.getParameters().copy());
        paramsModel.requery();
        parametersGrid = new EntityFieldsGrid();
        parametersGrid.setLabelTitle(NbBundle.getMessage(QuerySetupView.class, "Parameter")); //NOI18N
        parametersGrid.setValueTitle(NbBundle.getMessage(QuerySetupView.class, "Value")); //NOI18N
        parametersGrid.setEntity(paramsModel.getParametersEntity());
        topPanel.add(new JScrollPane(parametersGrid), BorderLayout.CENTER);
    }

    private void initSqlEditor() throws BadLocationException {
        txtSqlPane.setEditorKit(editorKit);
        txtSqlPane.setDocument(sqlTextDocument);
        Component refinedComponent = initCustomEditor(txtSqlPane);
        sqlSourcePanel.add(refinedComponent, BorderLayout.CENTER);
    }

    private void initDocument() throws BadLocationException {
        sqlTextDocument = (NbEditorDocument) editorKit.createDefaultDocument();
        sqlTextDocument.putProperty(NbEditorDocument.MIME_TYPE_PROP, SqlLanguageHierarchy.PLATYPUS_SQL_MIME_TYPE_NAME);
        sqlTextDocument.putProperty(PlatypusQueryDataObject.DATAOBJECT_DOC_PROPERTY, parentView.getQueryDataObject());// to enable code completion
        sqlTextDocument.insertString(0, parentView.getQueryText(), null);
    }

    public String getSqlText() throws BadLocationException {
        assert sqlTextDocument != null;
        return sqlTextDocument.getText(0, sqlTextDocument.getLength());
    }

    public Parameters getParameters() {
        return paramsModel.getParameters();
    }

    public void setDialog(Dialog aDialog, DialogDescriptor aDialogDescriptor) {
        if (aDialog == null || aDialogDescriptor == null) {
            throw new IllegalArgumentException("Parent dialog or dialog descriptor is null."); // NOI18N
        }
        dialog = aDialog;
        dialogDescriptor = aDialogDescriptor;
    }

    protected Component initCustomEditor(JEditorPane aPane) {
        if (aPane.getDocument() instanceof NbDocument.CustomEditor) {
            NbDocument.CustomEditor ce = (NbDocument.CustomEditor) aPane.getDocument();
            ce.addUndoableEditListener(new UndoableEditListener() {
                @Override
                public void undoableEditHappened(UndoableEditEvent e) {
                    try {
                        String sqlText = getSqlText();
                        updateParameters(sqlText);
                        parametersGrid.setEntity(paramsModel.getParametersEntity());
                    } catch (Exception ex) {
                        logger.log(Level.SEVERE, "Error updating parameters", ex); // NOI18N
                    }
                }
            });
            Component customComponent = ce.createEditor(aPane);
            if (customComponent == null) {
                throw new IllegalStateException(
                        "Document:" + aPane.getDocument() // NOI18N
                        + " implementing NbDocument.CustomEditor may not" // NOI18N
                        + " return null component" // NOI18N
                );
            }
            return customComponent;
        }
        return null;
    }

    private void updateParameters(String sqlText) throws JSQLParserException, BadLocationException {
        try {
            Statement statement = parserManager.parse(new StringReader(sqlText));
            mergeParameters(SqlTextEditsComplementor.extractParameters(statement));
        } catch (JSQLParserException ex) {
            //NO OP
        }
    }

    private void mergeParameters(Set<NamedParameter> parsedParameters) {
        Parameters parameters = paramsModel.getParameters();
        //Remove absent parameters from model
        Set<String> parametersNames = new HashSet<>();
        for (NamedParameter parameter : parsedParameters) {
            parametersNames.add(parameter.getName());
        }
        Set<Parameter> parametersToRemove = new HashSet<>();
        for (int i = 1; i <= parameters.getParametersCount(); i++) {
            Parameter parameter = parameters.get(i);
            if (!parametersNames.contains(parameter.getName())) {
                parametersToRemove.add(parameter);
            }
        }
        for (Parameter parameter : parametersToRemove) {
            parameters.remove(parameter);
        }
        //Add new parameters
        for (NamedParameter parsedParameter : parsedParameters) {
            if (parameters.get(parsedParameter.getName()) == null) {
                Parameter newParameter = new Parameter(parsedParameter.getName());
                newParameter.setMode(1);
                newParameter.setTypeInfo(DataTypeInfo.VARCHAR);
                newParameter.setValue("");
                parameters.add(newParameter);
            }
        }
    }
}
