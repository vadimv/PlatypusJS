/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model.gui.selectors;

import com.bearsoft.rowset.metadata.Field;
import com.eas.client.model.Entity;

/**
 *
 * @author mg
 */
public class SelectedField<E extends Entity<?, ?, E>> {
    public E entity;
    public Field field;
    
    public SelectedField(E aEntity, Field aField)
    {
        super();
        entity = aEntity;
        field = aField;
    }
}
