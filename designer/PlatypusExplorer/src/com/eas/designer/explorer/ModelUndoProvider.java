/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.explorer;

import org.openide.awt.UndoRedo;

/**
 *
 * @author vv
 */
public interface ModelUndoProvider {
    UndoRedo.Manager getModelUndo();
}
