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

package com.jeta.forms.store.properties;

import java.awt.Component;

import java.io.IOException;

import javax.swing.border.Border;
import javax.swing.border.TitledBorder;

import com.jeta.forms.store.JETAObjectInput;
import com.jeta.forms.store.JETAObjectOutput;

/**
 * A property for a titled border
 * 
 * @author Jeff Tassin
 */
public class TitledBorderProperty extends BorderProperty {
	static final long serialVersionUID = -8705211071875597758L;

	/**
	 * The version of this class
	 */
	public static final int VERSION = 1;

	/**
	 * Creates an uninitialized <code>TitledBorderProperty</code> instance.
	 */
	public TitledBorderProperty() {

	}

	/**
	 * Creates a titled border instance that can be applied to any Swing
	 * component.
	 */
    @Override
	public Border createBorder(Component comp) {
		TitledBorder b = new TitledBorder(getTitle());
		b.setTitlePosition(getPosition());
		b.setTitleJustification(getJustification());
		b.setTitleColor(new ColorProxy(getTextColorProperty()));
		return b;
	}

	/**
	 * JETAPersistable Implementation
	 */
    @Override
	public void read(JETAObjectInput in) throws ClassNotFoundException, IOException {
		super.read( in.getSuperClassInput() );
		int version = in.readVersion();
	}

	/**
	 * JETAPersistable Implementation
	 */
    @Override
	public void write(JETAObjectOutput out) throws IOException {
		super.write( out.getSuperClassOutput( BorderProperty.class ) );
		out.writeVersion(VERSION);
	}

    @Override
	public String toString() {
		return "TITLED";
	}
}
