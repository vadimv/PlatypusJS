/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.eas.client.model.gui.edits.NotSavableUndoableEditSupport;
import javax.swing.undo.CompoundEdit;
import javax.swing.undo.UndoableEdit;

/**
 *
 * @author mg
 */
public class DbStructureUndoableEditSupport extends NotSavableUndoableEditSupport {
    
    @Override
    protected CompoundEdit createCompoundEdit() {
        if (notSavable){
            return new NotSavableDbStructureCompoundEdit();
        } else {
            return new DbStructureCompoundEdit();
        }   
    }

    @Override
    public synchronized void postEdit(UndoableEdit e) {
        if (compoundEdit != null && e instanceof DbStructureCompoundEdit) {
            DbStructureCompoundEdit subCompound = (DbStructureCompoundEdit) e;
            UndoableEdit[] subEdits = subCompound.getEdits();
            for (int i = 0; i < subEdits.length; i++) {
                super.postEdit(subEdits[i]);
            }
        } else {
            super.postEdit(e);
        }
    }
}
