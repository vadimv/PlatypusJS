/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.forms.api.components.model;

import com.eas.client.forms.api.HasComponentEvents;
import com.eas.client.forms.api.HasJsName;
import com.eas.client.forms.api.Widget;
import com.eas.client.forms.components.VCheckBox;
import com.eas.script.HasPublished;
import com.eas.script.NoPublisherException;
import com.eas.script.ScriptFunction;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ModelCheckBox extends ModelComponentDecorator<VCheckBox, Boolean> implements HasPublished, HasComponentEvents, HasJsName, Widget{

    public ModelCheckBox() {
        super();
        setDecorated(new VCheckBox());
    }

    private static final String CONSTRUCTOR_JSDOC = ""
            + "/**\n"
            + " * An implementation of a model check box -- an item that can be selected or deselected, and which displays its state to the user.\n"
            + " * @param text the text of the component (optional).\n"
            + " */";

    @ScriptFunction(jsDoc = CONSTRUCTOR_JSDOC, params = {"text"})
    public ModelCheckBox(String aText) throws Exception {
        this();
        decorated.setText(aText);
    }

    private static final String TEXT_JSDOC = ""
            + "/**\n"
            + "* Text on the check box."
            + "*/";

    @ScriptFunction(jsDoc = TEXT_JSDOC)
    public String getText() {
        return decorated.getText();
    }

    @ScriptFunction
    public void setText(String aValue) {
        decorated.setText(aValue);
    }

    @Override
    public JSObject getPublished() {
        if (published == null) {
            if (publisher == null || !publisher.isFunction()) {
                throw new NoPublisherException();
            }
            published = (JSObject)publisher.call(null, new Object[]{this});
        }
        return published;
    }

    private static JSObject publisher;

    public static void setPublisher(JSObject aPublisher) {
        publisher = aPublisher;
    }
}
