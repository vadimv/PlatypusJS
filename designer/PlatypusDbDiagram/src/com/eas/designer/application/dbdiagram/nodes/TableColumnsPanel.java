/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.dbdiagram.nodes;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.dbscheme.FieldsEntity;
import com.eas.client.model.gui.view.FieldsListModel;
import com.eas.client.model.gui.view.FieldsParametersListCellRenderer;
import java.util.List;

/**
 *
 * @author vv
 */
public class TableColumnsPanel extends javax.swing.JPanel {

    protected FieldsListModel<Field> availableFieldsModel = new FieldsListModel.FieldsModel();
    protected FieldsParametersListCellRenderer<FieldsEntity> filedsRenderer = new FieldsParametersListCellRenderer<>();
    FieldsEntity entity;
    
    /**
     * Creates new form TableColumnsPanel
     */
    public TableColumnsPanel(FieldsEntity anEntity) {
        initComponents();
        entity = anEntity;
        availableFieldsModel.setFields(entity.getFields());
        filedsRenderer.setEntity(anEntity);
    }
    
    public List<Field> getSelected() {
        return columnsList.getSelectedValuesList();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        scrollPanel = new javax.swing.JScrollPane();
        columnsList = new javax.swing.JList<Field>();

        columnsList.setModel(availableFieldsModel);
        columnsList.setCellRenderer(filedsRenderer);
        scrollPanel.setViewportView(columnsList);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 249, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(scrollPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 190, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList<Field> columnsList;
    private javax.swing.JScrollPane scrollPanel;
    // End of variables declaration//GEN-END:variables
}
