/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.menu;

import com.eas.client.forms.api.Container;
import com.eas.script.ScriptFunction;
import javax.swing.JMenu;
import javax.swing.JPopupMenu;

/**
 *
 * @author mg
 */
public class PopupMenu extends Container<JPopupMenu> {

    private static final String CONSTRUCTOR_JSDOC = "/**\n"
            + "* An implementation of a popup menu -- a small window that pops up\n" 
            + "* and displays a series of choices.\n"
            + "*/";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {})
    public PopupMenu() {
        super();
        setDelegate(new JPopupMenu());
    }

    protected PopupMenu(JPopupMenu aDelegate) {
        super();
        setDelegate(aDelegate);
    }
    
    private static final String ADD_JSDOC = "/**\n"
            + "* Adds the item to the menu.\n"
            + "* @param menu the menu component to add\n"
            + "*/";
    
    @ScriptFunction(jsDoc = ADD_JSDOC, params = {"menu"})
    public void add(Menu aMenu) {
        delegate.add((JMenu) unwrap(aMenu));
    }
}
