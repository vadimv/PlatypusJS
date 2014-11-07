/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.result;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.client.model.ModelElementRef;
import com.eas.client.model.query.QueryEntity;
import com.eas.dbcontrols.DbControlDesignInfo;
import com.eas.dbcontrols.DbControlPanel;
import com.eas.dbcontrols.DbControlsUtils;
import com.eas.dbcontrols.ScalarDbControl;
import com.eas.dbcontrols.check.DbCheck;
import com.eas.dbcontrols.date.DbDate;
import com.eas.dbcontrols.date.DbDateDesignInfo;
import com.eas.dbcontrols.visitors.DbSwingFactory;
import java.awt.Component;
import java.util.ArrayList;
import java.util.EventObject;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.event.CellEditorListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.TableCellEditor;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;

/**
 *
 * @author mg
 */
public class EntityFieldsGrid extends JTable {

    protected static final int HEADER_COLUMN_INDEX = 0;
    protected static final int LABEL_COLUMN_INDEX = 1;
    protected static final int VALUE_COLUMN_INDEX = 2;
    protected QueryEntity entity;
    protected String labelTitle = "Characteristic";
    protected String valueTitle = "Value";
    protected List<ScalarDbControl> controls = new ArrayList<>();
    protected Set<String> hidingFields = new HashSet<>();
    protected boolean filterPrimaryKeys;
    protected boolean filterForeignKeys;
    protected boolean editable = true;
    protected String booleanFieldsMask;

    public boolean isFilterForeignKeys() {
        return filterForeignKeys;
    }

    public void setFilterForeignKeys(boolean aValue) {
        filterForeignKeys = aValue;
    }

    public boolean isFilterPrimaryKeys() {
        return filterPrimaryKeys;
    }

    public void setFilterPrimaryKeys(boolean aValue) {
        filterPrimaryKeys = aValue;
    }

    public String getBooleanFieldsMask() {
        return booleanFieldsMask;
    }

    public void setBooleanFieldsMask(String aValue) {
        booleanFieldsMask = aValue;
    }

    private Fields fieldsByEntity(QueryEntity entity) {
        Fields fields = filterFields(entity.getFields());
        assert fields != null;
        return fields;
    }

    private Fields filterFields(Fields aFields) {
        Fields resFields = new Fields();
        for (int i = 1; i <= aFields.getFieldsCount(); i++) {
            Field field = aFields.get(i);
            if ((!filterForeignKeys || !field.isFk())
                    && (!filterPrimaryKeys || !field.isPk())
                    && !hidingFields.contains(field.getName().toLowerCase())) {
                resFields.add(field);
            }
        }
        return resFields;
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
        for (ScalarDbControl control : controls) {
            if (control instanceof DbControlPanel) {
                ((DbControlPanel) control).setEditable(aValue);
            }
        }
    }

    protected class EntityFieldsModel implements TableModel {

        protected Set<TableModelListener> listeners = new HashSet<>();

        @Override
        public int getRowCount() {
            if (entity != null) {
                Fields rsmd = fieldsByEntity(entity);
                return rsmd.getFieldsCount();
            }
            return 0;
        }

        @Override
        public int getColumnCount() {
            return 3;
        }

        @Override
        public String getColumnName(int columnIndex) {
            switch (columnIndex) {
                case 0:
                    return "";
                case 1:
                    return labelTitle;
                case 2:
                    return valueTitle;
            }
            return "";
        }

        @Override
        public Class<?> getColumnClass(int columnIndex) {
            if (columnIndex == LABEL_COLUMN_INDEX) {
                return String.class;
            }
            return Object.class;
        }

        @Override
        public boolean isCellEditable(int rowIndex, int columnIndex) {
            return columnIndex == VALUE_COLUMN_INDEX;
        }

        @Override
        public Object getValueAt(int rowIndex, int columnIndex) {
            if (rowIndex >= 0 && rowIndex < getRowCount()) {
                if (entity != null) {
                    Fields rsmd = fieldsByEntity(entity);
                    Field f = rsmd.get(rowIndex + 1);
                    if (columnIndex == LABEL_COLUMN_INDEX) {
                        String rowTitle = f.getName();
                        if (f.getDescription() != null && !f.getDescription().isEmpty()) {
                            rowTitle = f.getDescription();
                        }
                        return rowTitle;
                    } else if (columnIndex == HEADER_COLUMN_INDEX) {
                        return f.isNullable() ? "" : " * ";
                    }
                }
            }
            return null;
        }

        @Override
        public void setValueAt(Object aValue, int rowIndex, int columnIndex) {
        }

        @Override
        public void addTableModelListener(TableModelListener l) {
            listeners.add(l);
        }

        @Override
        public void removeTableModelListener(TableModelListener l) {
            listeners.remove(l);
        }

        private void fireDataChanged() {
            TableModelEvent tme = new TableModelEvent(EntityFieldsModel.this);
            for (TableModelListener tml : listeners) {
                tml.tableChanged(tme);
            }
        }
    }

    protected class EntityFieldsCellEditor implements TableCellEditor, TableCellRenderer {

        protected Set<CellEditorListener> listeners = new HashSet<>();

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            return getTableCellEditorComponent(table, value, isSelected, row, column);
        }

        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
            assert table == EntityFieldsGrid.this;
            if (row >= 0 && row < controls.size()) {
                ScalarDbControl control = controls.get(row);
                assert control instanceof Component;
                return (Component) control;
            }
            return null;
        }

        @Override
        public Object getCellEditorValue() {
            return null;
        }

        @Override
        public boolean isCellEditable(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean shouldSelectCell(EventObject anEvent) {
            return true;
        }

        @Override
        public boolean stopCellEditing() {
            ChangeEvent ce = new ChangeEvent(EntityFieldsCellEditor.this);
            for (CellEditorListener l : listeners) {
                l.editingStopped(ce);
            }
            return true;
        }

        @Override
        public void cancelCellEditing() {
            ChangeEvent ce = new ChangeEvent(EntityFieldsCellEditor.this);
            for (CellEditorListener l : listeners) {
                l.editingCanceled(ce);
            }
        }

        @Override
        public void addCellEditorListener(CellEditorListener l) {
            listeners.add(l);
        }

        @Override
        public void removeCellEditorListener(CellEditorListener l) {
            listeners.remove(l);
        }
    }

    public EntityFieldsGrid() {
        super();
        labelTitle = DbControlsUtils.getLocalizedString(labelTitle);
        valueTitle = DbControlsUtils.getLocalizedString(valueTitle);
        setModel(new EntityFieldsModel());
        setAutoResizeMode(AUTO_RESIZE_LAST_COLUMN);
        TableColumn tc = getColumnModel().getColumn(VALUE_COLUMN_INDEX);
        if (tc != null) {
            tc.setCellEditor(new EntityFieldsCellEditor());
            tc.setCellRenderer(new EntityFieldsCellEditor());
        }
        tc = getColumnModel().getColumn(HEADER_COLUMN_INDEX);
        if (tc != null) {
            tc.setMaxWidth(15);
            tc.setWidth(15);
        }
        setRowHeight(20);
    }

    public EntityFieldsGrid(String... aHidingFields) {
        this();
        if (aHidingFields != null) {
            for (int i = 0; i < aHidingFields.length; i++) {
                if (aHidingFields[i] != null) {
                    hidingFields.add(aHidingFields[i].toLowerCase());
                }
            }
        }
    }

    public void setEntity(QueryEntity aEntity) throws Exception {
        entity = aEntity;
        if (entity != null) {
            fillDbControls();
        }
    }

    public String getLabelTitle() {
        return labelTitle;
    }

    public String getValueTitle() {
        return valueTitle;
    }

    public void setLabelTitle(String labelTitle) {
        this.labelTitle = labelTitle;
        TableColumn tc = getColumnModel().getColumn(LABEL_COLUMN_INDEX);
        if (tc != null) {
            tc.setHeaderValue(labelTitle);
        }
    }

    public void setValueTitle(String aValue) {
        valueTitle = aValue;
        TableColumn tc = getColumnModel().getColumn(VALUE_COLUMN_INDEX);
        if (tc != null) {
            tc.setHeaderValue(aValue);
        }
    }

    public void stopEditing() {
        if (isEditing()) {
            TableCellEditor editor = getCellEditor();
            if (editor != null) {
                editor.stopCellEditing();
            }
        }
    }

    public List<ScalarDbControl> getControls() {
        return controls;
    }

    private void fillDbControls() throws Exception {
        assert entity != null;
        // cleanup
        for (ScalarDbControl control : controls) {
            control.setModel(null);
            control.cleanup();
        }
        controls.clear();
        // fill in the controls
        if (entity != null) {
            Fields fields = fieldsByEntity(entity);
            int cCount = fields.getFieldsCount();
            for (int i = 0; i < cCount; i++) {
                Field field = fields.get(i + 1);
                ModelElementRef ref = new ModelElementRef();
                ref.setEntityId(entity.getEntityId());
                ref.setFieldName(field.getName());
                Class<?>[] compatibleControlsClasses = DbControlsUtils.getCompatibleControls(field.getTypeInfo().getSqlType());
                if (compatibleControlsClasses != null && compatibleControlsClasses.length > 0) {
                    Class<?> lControlClass = compatibleControlsClasses[0];
                    if (booleanFieldsMask != null && Pattern.matches(booleanFieldsMask, field.getName())) {
                        lControlClass = DbCheck.class;
                    }
                    if (lControlClass != null) {
                        Class<?> infoClass = DbControlsUtils.getDesignInfoClass(lControlClass);
                        if (infoClass != null) {
                            Logger.getLogger(EntityFieldsGrid.class.getName()).log(Level.FINEST, "Creating control for parameter {0} of type {1} with control class {2}", new Object[]{field.getName(), field.getTypeInfo().getSqlTypeName(), lControlClass.getName()});
                            DbControlDesignInfo cdi = (DbControlDesignInfo) infoClass.newInstance();
                            cdi.setDatamodelElement(ref);
                            if (cdi instanceof DbDateDesignInfo) {
                                DbDateDesignInfo dateDesignInfo = (DbDateDesignInfo) cdi;
                                if (field.getTypeInfo().getSqlType() == java.sql.Types.TIMESTAMP) {
                                    dateDesignInfo.setDateFormat(DbDate.DD_MM_YYYY_HH_MM_SS);
                                } else if (field.getTypeInfo().getSqlType() == java.sql.Types.TIME) {
                                    dateDesignInfo.setDateFormat(DbDate.HH_MM_SS);
                                }
                            }
                            DbSwingFactory factory = new DbSwingFactory(null);
                            cdi.accept(factory);
                            assert factory.getComp() instanceof ScalarDbControl;
                            ScalarDbControl control = (ScalarDbControl) factory.getComp();
                            control.configure();
                            control.setBorderless(true);

                            if (control instanceof DbControlPanel) {
                                ((DbControlPanel) control).setBorder(null);
                                ((DbControlPanel) control).setEditable(editable);
                                ((DbControlPanel) control).setName(field.getName());
                                if (control instanceof DbCheck) {
                                    ((DbControlPanel) control).setAlign(SwingConstants.CENTER);
                                }
                            }
                            controls.add(control);

                            control.beginUpdate();
                            try {
                                control.setEditingValue(((ScalarDbControl) control).getValueFromRowset());
                            } finally {
                                control.endUpdate();
                            }
                        }
                    }
                }
            }
        }
        // notify all of change
        ((EntityFieldsModel) getModel()).fireDataChanged();
    }
}