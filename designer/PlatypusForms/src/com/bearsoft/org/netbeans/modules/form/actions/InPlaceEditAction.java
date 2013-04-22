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
package com.bearsoft.org.netbeans.modules.form.actions;

import com.bearsoft.org.netbeans.modules.form.*;
import org.openide.awt.ActionID;
import org.openide.awt.ActionRegistration;
import org.openide.nodes.Node;
import org.openide.util.HelpCtx;
import org.openide.util.NbBundle;
import org.openide.util.actions.*;

/**
 * Action that starts in-place editing of selected component in
 * PlatypusFormLayoutView.
 */
@ActionID(id = "com.bearsoft.org.netbeans.modules.form.actions.InPlaceEditAction", category = "Form")
@ActionRegistration(displayName = "#ACT_InPlaceEdit", lazy=true)
public class InPlaceEditAction extends NodeAction {

    private static String name;

    /**
     * Perform the action based on the currently activated nodes. Note that if
     * the source of the event triggering this action was itself a node, that
     * node will be the sole argument to this method, rather than the activated
     * nodes.
     *
     * @param activatedNodes current activated nodes, may be empty but
     * not <code>null</code>
     */
    @Override
    protected void performAction(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1) {
            RADComponentCookie radCookie = activatedNodes[0].getLookup().lookup(RADComponentCookie.class);
            RADComponent<?> radComp = radCookie == null ? null
                    : radCookie.getRADComponent();
            if (radComp != null) {
                PlatypusFormLayoutView designer = FormEditor.getFormDesigner(radComp.getFormModel());
                if (designer != null) {
                    designer.startInPlaceEditing(radComp);
                }
            }
        }
    }

    @Override
    protected boolean asynchronous() {
        return false;
    }

    /**
     * Test whether the action should be enabled based on the currently
     * activated nodes.
     *
     * @param activatedNodes current activated nodes, may be empty but
     * not <code>null</code>
     * @return <code>true</code> to be enabled, <code>false</code> to be
     * disabled
     */
    @Override
    protected boolean enable(Node[] activatedNodes) {
        if (activatedNodes != null && activatedNodes.length == 1) {
            RADComponentCookie radCookie = activatedNodes[0].getLookup().lookup(RADComponentCookie.class);
            RADComponent<?> radComp = radCookie == null ? null : radCookie.getRADComponent();
            if (radComp != null) {
                PlatypusFormLayoutView designer = FormEditor.getFormDesigner(radComp.getFormModel());
                if (designer != null) {
                    return designer.isEditableInPlace(radComp); 
                }
            }
        }
        return false;
    }

    /**
     * human presentable name of the action. This should be presented as an item
     * in a menu.
     *
     * @return the name of the action
     */
    @Override
    public String getName() {
        if (name == null) {
            name = NbBundle.getMessage(InPlaceEditAction.class, "ACT_InPlaceEdit"); // NOI18N
        }
        return name;
    }

    /**
     * Help context where to find more about the action.
     *
     * @return the help context for this action
     */
    @Override
    public HelpCtx getHelpCtx() {
        return new HelpCtx("gui.quickref"); // NOI18N
    }
}
