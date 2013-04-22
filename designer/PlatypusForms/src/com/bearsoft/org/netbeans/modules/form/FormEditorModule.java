/*
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS HEADER.
 *
 * Copyright 1997-2010 Oracle and/or its affiliates. All rights reserved.
 *
 * Oracle and Java are registered trademarks of Oracle and/or its affiliates.
 * Other names may be trademarks of their respective owners.
 *
 * The contents of this file are subject to the terms of either the GNU
 * General Public License Version 2 only ("GPL") or the Common
 * Development and Distribution License("CDDL") (collectively, the
 * "License"). You may not use this file except in compliance with the
 * License. You can obtain a copy of the License at
 * http://www.netbeans.org/cddl-gplv2.html
 * or nbbuild/licenses/CDDL-GPL-2-CP. See the License for the
 * specific language governing permissions and limitations under the
 * License.  When distributing the software, include this License Header
 * Notice in each file and include the License file at
 * nbbuild/licenses/CDDL-GPL-2-CP.  Oracle designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Oracle in the GPL Version 2 section of the License file that
 * accompanied this code. If applicable, add the following below the
 * License Header, with the fields enclosed by brackets [] replaced by
 * your own identifying information:
 * "Portions Copyrighted [year] [name of copyright owner]"
 *
 * Contributor(s):
 *
 * The Original Software is NetBeans. The Initial Developer of the Original
 * Software is Sun Microsystems, Inc. Portions Copyright 1997-2006 Sun
 * Microsystems, Inc. All Rights Reserved.
 *
 * If you wish your version of this file to be governed by only the CDDL
 * or only the GPL Version 2, indicate your decision by adding
 * "[Contributor] elects to include this software in this distribution
 * under the [CDDL or GPL Version 2] license." If you do not indicate a
 * single choice of license, a recipient has the option to distribute
 * your version of this file under either the CDDL, the GPL Version 2 or
 * to extend the choice of license to its licensees as provided above.
 * However, if you add GPL Version 2 code and therefore, elected the GPL
 * Version 2 license, then the option applies only if the new code is
 * made subject to such option by the copyright holder.
 */
package com.bearsoft.org.netbeans.modules.form;

import java.awt.Cursor;
import java.beans.Beans;
import java.beans.Introspector;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import org.openide.modules.ModuleInstall;

/**
 * Module installation class for Form Editor
 */
public class FormEditorModule extends ModuleInstall {

    private static final String BEANINFO_PATH_SWING = "com.bearsoft.org.netbeans.modules.form.beaninfo.swing"; // NOI18N

    @Override
    public void restored() {
        Beans.setDesignTime(true);

        FormPropertyEditorManager.registerEditor(
                javax.swing.KeyStroke.class,
                com.bearsoft.org.netbeans.modules.form.editors.KeyStrokeEditor.class);
        FormPropertyEditorManager.registerEditor(
                Cursor.class,
                com.bearsoft.org.netbeans.modules.form.editors.CursorEditor.class);
        FormPropertyEditorManager.registerEditor(
                javax.swing.Icon.class,
                com.bearsoft.org.netbeans.modules.form.editors.IconEditor.class);
        FormPropertyEditorManager.registerEditor(
                com.bearsoft.org.netbeans.modules.form.editors.IconEditor.NbImageIcon.class,
                com.bearsoft.org.netbeans.modules.form.editors.IconEditor.class);
        FormPropertyEditorManager.registerEditor(
                javax.swing.border.Border.class,
                com.bearsoft.org.netbeans.modules.form.editors.BorderEditor.class);
        FormPropertyEditorManager.registerEditor(
                javax.swing.JFormattedTextField.AbstractFormatterFactory.class,
                com.bearsoft.org.netbeans.modules.form.editors.AbstractFormatterFactoryEditor.class);
        FormPropertyEditorManager.registerEditor(
                com.eas.client.model.ModelElementRef.class,
                com.bearsoft.org.netbeans.modules.form.editors.ModelElementRefEditor.class);
        FormPropertyEditorManager.registerEditor(
                com.eas.client.model.ModelEntityRef.class,
                com.bearsoft.org.netbeans.modules.form.editors.ModelEntityRefEditor.class);
        FormPropertyEditorManager.registerEditor(
                com.eas.client.model.ModelEntityParameterRef.class,
                com.bearsoft.org.netbeans.modules.form.editors.ModelEntityParameterRefEditor.class);
        
        // Add beaninfo search path.
        String[] sp = Introspector.getBeanInfoSearchPath();
        List<String> paths = new ArrayList<>(Arrays.asList(sp));
        if (!paths.contains(BEANINFO_PATH_SWING)) {
            paths.add(BEANINFO_PATH_SWING);
        }
        Introspector.setBeanInfoSearchPath(paths.toArray(new String[paths.size()]));
    }

    @Override
    public void uninstalled() {
        // Remove beaninfo search path.
        String[] sp = Introspector.getBeanInfoSearchPath();
        List<String> paths = new ArrayList<>(Arrays.asList(sp));
        paths.remove(BEANINFO_PATH_SWING);
        Introspector.setBeanInfoSearchPath(paths.toArray(new String[paths.size()]));
    }
}
