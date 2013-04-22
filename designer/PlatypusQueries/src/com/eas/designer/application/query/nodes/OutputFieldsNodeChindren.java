/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.query.nodes;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.Fields;
import com.eas.designer.application.query.PlatypusQueryDataObject;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Collections;
import org.openide.ErrorManager;
import org.openide.nodes.Children;
import org.openide.nodes.Node;

/**
 *
 * @author vv
 */
public class OutputFieldsNodeChindren extends Children.Keys<Field> {

    PlatypusQueryDataObject dataObject;
    DataObjectPropertyChangeListener dataObjectPropertyChangeListener = new DataObjectPropertyChangeListener();

    public OutputFieldsNodeChindren(PlatypusQueryDataObject aDataObject) {
        dataObject = aDataObject;
        dataObject.addPropertyChangeListener(dataObjectPropertyChangeListener);
    }

    @Override
    protected void addNotify() {
        setKeysImpl();
    }

    private void setKeysImpl() {
        Fields fields = getFields();
        if (fields != null) {
            setKeys(fields.toCollection());
        } else {
            setKeys(Collections.EMPTY_LIST);
        }
    }

    private Fields getFields() {
        Fields fields = null;
        try {
            fields = dataObject.getOutputFields();
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
        }
        return fields;
    }

    @Override
    protected Node[] createNodes(Field key) {
        try {
            return new Node[]{new OutputFieldNode(dataObject, key)};
        } catch (Exception ex) {
            ErrorManager.getDefault().notify(ex);
            return null;
        }
    }

    protected class DataObjectPropertyChangeListener implements PropertyChangeListener {

        @Override
        public void propertyChange(PropertyChangeEvent evt) {
            if (PlatypusQueryDataObject.OUTPUT_FIELDS.equals(evt.getPropertyName())) {
                setKeysImpl();
            }
        }
    }
}
