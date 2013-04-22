/*
 * Copyright (c) 2004 JETA Software, Inc.  All rights reserved.
 * 
 * Redistribution and use in source and binary forms, with or without modification, 
 * are permitted provided that the following conditions are met:
 *
 *  o Redistributions of source code must retain the above copyright notice, 
 *    this list of conditions and the following disclaimer.
 *
 *  o Redistributions in binary form must reproduce the above copyright notice, 
 *    this list of conditions and the following disclaimer in the documentation 
 *    and/or other materials provided with the distribution.
 *
 *  o Neither the name of JETA Software nor the names of its contributors may 
 *    be used to endorse or promote products derived from this software without 
 *    specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE 
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE 
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES 
 * INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT 
 * INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS 
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.jeta.forms.gui.beans.factories;

import java.awt.Component;
import javax.swing.AbstractButton;
import javax.swing.JCheckBox;
import com.jeta.forms.gui.beans.JETABean;
import com.jeta.forms.gui.common.FormException;

/**
 * Factory for instantiating a JETABean that contains an JCheckBox and its
 * associated properties.  
 *
 * @author Jeff Tassin
 */
public class CheckBoxBeanFactory extends AbstractButtonFactory {

    /**
     * Creates a <code>CheckBoxBeanFactory</code> instance.
     */
    public CheckBoxBeanFactory() {
        super(JCheckBox.class);
    }

    /**
     * BeanFactory implementation.  Override from JComponentBeanFactory because we want to
     * change the default border for JCheckBoxes.  The main reason is because in some look and feels
     * the checkbox has a border that offsets the component to the left.  This makes it impossible to
     * align a checkbox with other components on a form and results in an unpleasant looking GUI.
     * @param compName the name to assign to this component by calling Component.setName
     * @param instantiateBean set to true if the underlying Java Bean should be instantiated as well. During deserialization
     *  we don't want to do this because the BeanDeserializer will create the JavaBean for us.
     * @param setDefaults sets default properties for the bean.  If false, no properties will be set (e.g. the text for a JButton)
     * @return the newly instantiated JETABean
     */
    @Override
    public JETABean createBean(String compName, boolean instantiateBean, boolean setDefaults) throws FormException {
        JETABean jbean = super.createBean(compName, instantiateBean, setDefaults);
        Component comp = jbean.getDelegate();
        if (comp instanceof AbstractButton) {
            AbstractButton jcomp = (AbstractButton) comp;
            jcomp.setBorder(new javax.swing.border.EmptyBorder(1, 0, 1, 2));
        }
        return jbean;
    }
}
