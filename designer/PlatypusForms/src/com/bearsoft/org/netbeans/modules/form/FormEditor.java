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

import com.bearsoft.org.netbeans.modules.form.actions.EditContainerAction;
import com.bearsoft.org.netbeans.modules.form.actions.EditFormAction;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.Action;
import javax.swing.JButton;
import javax.swing.JEditorPane;
import org.openide.DialogDisplayer;
import org.openide.ErrorManager;
import org.openide.NotifyDescriptor;
import org.openide.awt.UndoRedo;
import org.openide.loaders.DataObject;
import org.openide.nodes.Node;
import org.openide.util.Mutex;
import org.openide.util.actions.SystemAction;

/**
 * Form editor.
 *
 * @author Jan Stola
 */
public class FormEditor {

    static final int LOADING = 1;
    static final int SAVING = 2;
    /**
     * The FormModel instance holding the form itself
     */
    private FormModel formModel;
    /**
     * The root node of form hierarchy presented in Component Inspector
     */
    private FormRootNode formRootNode;
    /**
     * The designer component - the last active designer of the form (there can
     * be more clones). May happen to be null if the active designer was closed
     * and no other designer of given form was activated since then.
     */
    private PlatypusFormLayoutView formDesigner;
    /**
     * The code generator for the form
     */
    private CodeGenerator codeGenerator;
    /**
     * List of exceptions occurred during the last persistence operation
     */
    private List<Throwable> persistenceErrors;
    /**
     * Persistence manager responsible for saving the form
     */
    private PersistenceManager persistenceManager;
    /**
     * An indicator whether the form has been loaded (from the .form file)
     */
    private boolean formLoaded = false;
    /**
     * Table of opened FormModel instances (FormModel to FormEditor map)
     */
    private static Map<FormModel, FormEditor> openForms = new HashMap<>();
    /**
     * List of floating windows - must be closed when the form is closed.
     */
//    private List<java.awt.Window> floatingWindows;
    /**
     * The DataObject of the form
     */
    private PlatypusFormDataObject formDataObject;
    private PropertyChangeListener dataObjectListener;
    // listeners
    private FormModelListener formListener;
    /**
     * List of actions that are tried when a component is double-clicked.
     */
    private List<Action> defaultActions;
    /**
     * Indicates that a task has been posted to ask the user about format
     * upgrade - not to show the confirmation dialog multiple times.
     */
    private boolean upgradeCheckPosted;

    // -----
    FormEditor(PlatypusFormDataObject aDataObject) {
        formDataObject = aDataObject;
    }

    /**
     * @return root node representing the form (in pair with the class node)
     */
    public final FormNode getFormRootNode() {
        return formRootNode;
    }

    public final FormNode getOthersContainerNode() {
        FormNode othersNode = formRootNode.getOthersNode();
        return othersNode != null ? othersNode : formRootNode;
    }

    /**
     * @return the FormModel of this form, null if the form is not loaded
     */
    public final FormModel getFormModel() {
        return formModel;
    }

    public final PlatypusFormDataObject getFormDataObject() {
        return formDataObject;
    }

    CodeGenerator getCodeGenerator() {
        if (!formLoaded) {
            return null;
        }
        if (codeGenerator == null) {
            codeGenerator = new FormsJsCodeGenerator();
        }
        return codeGenerator;
    }

    /**
     * To be used just before loading a form to set a persistence manager that
     * already has the form recognized and superclass determined (i.e.
     * potentially long java parsing already done).
     */
    void setPersistenceManager(PersistenceManager pm) {
        persistenceManager = pm;
    }

    boolean isFormLoaded() {
        return formLoaded;
    }

    /**
     * This methods loads the form, reports errors, creates the
     * PlatypusFormLayoutView
     */
    void loadFormDesigner() {
        getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).showOpeningStatus("FMT_OpeningForm"); // NOI18N
        // load form data and report errors
        try {
            loadFormData();
        } catch (PersistenceException ex) { // a fatal loading error happened
            logPersistenceError(ex, 0);
            if (!formLoaded) { // loading failed - don't keep empty designer opened
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).selectJsEditor();
                    }
                });
            }
        }
        getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).hideOpeningStatus();
        // report errors during loading
        reportErrors(LOADING);
    }

    boolean loadForm() {
        if (!formLoaded) {
            if (java.awt.EventQueue.isDispatchThread()) {
                try {
                    loadFormData();
                } catch (PersistenceException ex) {
                    logPersistenceError(ex, 0);
                }
            } else { // loading must be done in AWT event dispatch thread
                try {
                    java.awt.EventQueue.invokeAndWait(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                loadFormData();
                            } catch (PersistenceException ex) {
                                logPersistenceError(ex, 0);
                            }
                        }
                    });
                } catch (Exception ex) {
                    ErrorManager.getDefault().notify(ex);
                }
            }
        }
        return formLoaded;
    }

    /**
     * This method performs the form data loading. All open/load methods go
     * through this one.
     */
    private void loadFormData() throws PersistenceException {
        if (!formLoaded) {
            resetPersistenceErrorLog(); // clear log of errors
            // first find PersistenceManager for loading the form
            if (persistenceManager == null) {
                persistenceManager = recognizeForm(formDataObject);
            }
            // create and register new FormModel instance
            formModel = new FormModel(formDataObject);

            openForms.put(formModel, this);
            Logger.getLogger("TIMER").log(Level.FINE, "FormModel", new Object[]{formDataObject.getPrimaryFile(), formModel}); // NOI18N
            // load the form data (FormModel) and report errors
            try {
                FormLAF.<Object>executeWithLookAndFeel(formModel, new Mutex.ExceptionAction<Object>() {
                    @Override
                    public Object run() throws Exception {
                        persistenceManager.loadForm(formDataObject,
                                formModel,
                                persistenceErrors);
                        return null;
                    }
                });
            } catch (PersistenceException ex) { // some fatal error occurred
                persistenceManager = null;
                openForms.remove(formModel);
                formModel = null;
                throw ex;
            } catch (Exception ex) { // should not happen, but for sure...
                ErrorManager.getDefault().notify(ex);
                persistenceManager = null;
                openForms.remove(formModel);
                formModel = null;
                return;
            }

            // form is successfully loaded...
            formLoaded = true;

            getCodeGenerator().initialize(formModel);
            formModel.fireFormLoaded();
            // create form nodes hierarchy and add it to SourceChildren
            formRootNode = new FormRootNode(formModel);
            formRootNode.getChildren().getNodes();
            formDataObject.getNodeDelegate().getChildren().add(new Node[]{formRootNode});

            attachFormListener();
            attachDataObjectListener();
        }
    }

    /**
     * Public method for saving form data to file. Does not save the source code
     * (document), does not report errors and does not throw any exceptions.
     *
     * @return whether there was not any fatal error during saving (true means
     * everything was ok); returns true even if nothing was saved because form
     * was not loaded or read-only, etc.
     */
    public boolean saveForm() {
        try {
            saveFormData();
            return true;
        } catch (PersistenceException ex) {
            logPersistenceError(ex, 0);
            return false;
        }
    }

    void saveFormData() throws PersistenceException {
        if (formLoaded && !formDataObject.formFileReadOnly() && !formModel.isReadOnly()) {
            formModel.fireFormToBeSaved();

            resetPersistenceErrorLog();

            persistenceManager.saveForm(formDataObject, formModel, persistenceErrors);
        }
    }

    private void resetPersistenceErrorLog() {
        if (persistenceErrors != null) {
            persistenceErrors.clear();
        } else {
            persistenceErrors = new ArrayList<>();
        }
    }

    private void logPersistenceError(Throwable t, int index) {
        if (persistenceErrors == null) {
            persistenceErrors = new ArrayList<>();
        }

        if (index < 0) {
            persistenceErrors.add(t);
        } else {
            persistenceErrors.add(index, t);
        }
    }

    /**
     * Finds PersistenceManager that can load and save the form.
     */
    private PersistenceManager recognizeForm(PlatypusFormDataObject formDO)
            throws PersistenceException {
        List<PersistenceManager> perisitenceManagers = PersistenceManager.getManagers();
        if (perisitenceManagers.isEmpty()) { // there's no PersistenceManager available
            PersistenceException ex = new PersistenceException(
                    "No persistence manager registered"); // NOI18N
            ErrorManager.getDefault().annotate(
                    ex,
                    ErrorManager.ERROR,
                    null,
                    FormUtils.getBundleString("MSG_ERR_NoPersistenceManager"), // NOI18N
                    null,
                    null);
            throw ex;
        }

        for (PersistenceManager pm : perisitenceManagers) {
            synchronized (pm) {
                try {
                    if (pm.canLoadForm(formDO)) {
                        resetPersistenceErrorLog();
                        return pm;
                    }
                } catch (PersistenceException ex) {
                    logPersistenceError(ex);
                    // [continue on exception?]
                }
            }
        }

        // no PersistenceManager is able to load the form
        PersistenceException ex;
        if (!anyPersistenceError()) {
            // no error occurred, the format is just unknown
            ex = new PersistenceException("Form file format not recognized"); // NOI18N
            ErrorManager.getDefault().annotate(
                    ex,
                    ErrorManager.ERROR,
                    null,
                    FormUtils.getBundleString("MSG_ERR_NotRecognizedForm"), // NOI18N
                    null,
                    null);
        } else { // some errors occurred when recognizing the form file format
            Throwable annotateT = null;
            int n = persistenceErrors.size();
            if (n == 1) { // just one exception occurred
                ex = (PersistenceException) persistenceErrors.get(0);
                Throwable t = ex.getOriginalException();
                annotateT = t != null ? t : ex;
                n = 0;
            } else { // there were more exceptions
                ex = new PersistenceException("Form file cannot be loaded"); // NOI18N
                annotateT = ex;
            }
            ErrorManager.getDefault().annotate(
                    annotateT,
                    FormUtils.getBundleString("MSG_ERR_LoadingErrors") // NOI18N
                    );
            for (int i = 0; i < n; i++) {
                PersistenceException pe = (PersistenceException) persistenceErrors.get(i);
                Throwable t = pe.getOriginalException();
                ErrorManager.getDefault().annotate(ex, (t != null ? t : pe));
            }
            // all the exceptions were attached to the main exception to
            // be thrown, so the log can be cleared
            resetPersistenceErrorLog();
        }
        throw ex;
    }

    private void logPersistenceError(Throwable t) {
        logPersistenceError(t, -1);
    }

    boolean anyPersistenceError() {
        return persistenceErrors != null && !persistenceErrors.isEmpty();
    }

    /**
     * Reports errors occurred during loading or saving the form.
     *
     * @param operation operation being performed.
     */
    public void reportErrors(int operation) {
        if (!anyPersistenceError()) {
            return; // no errors or warnings logged
        }
        final ErrorManager errorManager = ErrorManager.getDefault();
        final PlatypusPersistenceManager persistManager =
                (PlatypusPersistenceManager) persistenceManager;

        boolean checkLoadingErrors = operation == LOADING && formLoaded;
        boolean anyNonFatalLoadingError = false; // was there a real error?

        StringBuilder userErrorMsgs = new StringBuilder();

        for (Throwable t : persistenceErrors) {
            if (t instanceof PersistenceException) {
                Throwable th = ((PersistenceException) t).getOriginalException();
                if (th != null) {
                    t = th;
                }
            }

            if (checkLoadingErrors && !anyNonFatalLoadingError) {
                // was there a real loading error (not just warnings) causing
                // some data not loaded?
                ErrorManager.Annotation[] annotations =
                        errorManager.findAnnotations(t);
                int severity = 0;
                if ((annotations != null) && (annotations.length != 0)) {
                    for (int i = 0; i < annotations.length; i++) {
                        int s = annotations[i].getSeverity();
                        if (s == ErrorManager.UNKNOWN) {
                            s = ErrorManager.EXCEPTION;
                        }
                        if (s > severity) {
                            severity = s;
                        }
                    }
                } else {
                    severity = ErrorManager.EXCEPTION;
                }

                if (severity > ErrorManager.WARNING) {
                    anyNonFatalLoadingError = true;
                }
            }
            errorManager.notify(ErrorManager.INFORMATIONAL, t);
        }

        if (checkLoadingErrors && anyNonFatalLoadingError) {
            // the form was loaded with some non-fatal errors - some data
            // was not loaded - show a warning about possible data loss
            final String wholeMsg = userErrorMsgs.append(
                    FormUtils.getBundleString("MSG_FormLoadedWithErrors")).toString();  // NOI18N

            java.awt.EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    // for some reason this would be displayed before the
                    // ErrorManager if not invoked later
                    if (isFormLoaded()) {// issue #164444
                        JButton viewOnly = new JButton(FormUtils.getBundleString("CTL_ViewOnly"));		// NOI18N
                        JButton allowEditing = new JButton(FormUtils.getBundleString("CTL_AllowEditing"));	// NOI18N                                        

                        Object ret = DialogDisplayer.getDefault().notify(new NotifyDescriptor(
                                wholeMsg,
                                FormUtils.getBundleString("CTL_FormLoadedWithErrors"), // NOI18N
                                NotifyDescriptor.DEFAULT_OPTION,
                                NotifyDescriptor.WARNING_MESSAGE,
                                new Object[]{viewOnly, allowEditing, NotifyDescriptor.CANCEL_OPTION},
                                viewOnly));

                        if (ret == viewOnly) {
                            setFormReadOnly();
                        } else if (ret == allowEditing) {
                            destroyInvalidComponents();
                        } else { // close form, switch to source editor
                            getFormDesigner().reset(FormEditor.this); // might be reused
                            closeForm();
                            getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).selectJsEditor();
                        }
                    }
                }
            });
        }

        resetPersistenceErrorLog();
    }

    /**
     * Destroys all components from {@link #formModel} taged as invalid
     */
    private void destroyInvalidComponents() {
        Collection<RADComponent<?>> allComps = formModel.getAllComponents();
        List<RADComponent<?>> invalidComponents = new ArrayList<>(allComps.size());
        // collect all invalid components
        for (RADComponent<?> comp : allComps) {
            if (!comp.isValid()) {
                invalidComponents.add(comp);
            }
        }
        // destroy all invalid components
        for (RADComponent<?> comp : invalidComponents) {
            try {
                RADComponentNode node = comp.getNodeReference();
                if (node != null) {
                    node.destroy();
                }
            } catch (java.io.IOException ex) { // should not happen
                ErrorManager.getDefault().notify(ex);
            }
        }
    }

    /**
     * Sets the FormEditor in Read-Only mode
     */
    private void setFormReadOnly() {
        formModel.setReadOnly(true);
        getFormDesigner().getHandleLayer().setViewOnly(true);
        detachFormListener();
        getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).updateTitles();
    }

    /**
     * @return the last activated PlatypusFormLayoutView for this form
     */
    PlatypusFormLayoutView getFormDesigner() {
        if (formLoaded) {
            return formDesigner;
        } else {
            return null;
        }
    }

    /**
     * Called by PlatypusFormLayoutView when activated.
     */
    void setFormDesigner(PlatypusFormLayoutView designer) {
        formDesigner = designer;
    }

    /**
     * Closes the form. Used when closing the form editor or reloading the form.
     */
    void closeForm() {
        if (formLoaded) {
            formModel.fireFormToBeClosed();

            openForms.remove(formModel);
            formLoaded = false;

            // remove nodes hierarchy
            if (formDataObject.isValid()) {
                // Avoiding deadlock (issue 51796)
                java.awt.EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        if (formDataObject.isValid()) {
                            formDataObject.getNodeDelegate().getChildren().remove(new Node[]{formRootNode});
                        }
                        formRootNode = null;
                    }
                });
            }

            // remove listeners
            detachFormListener();
            detachDataObjectListener();

            // reset references
            formDesigner = null;
            persistenceManager = null;
            persistenceErrors = null;
            formModel = null;
            codeGenerator = null;
        }
    }

    private void attachFormListener() {
        if (formListener == null && !formDataObject.isReadOnly() && !formModel.isReadOnly()) {
            // this listener ensures necessary updates of nodes according to
            // changes in containers in form
            formListener = new FormModelListener() {
                @Override
                public void formChanged(FormModelEvent[] events) {
                    if (events == null) {
                        return;
                    }

                    boolean modifying = false;
                    Set<ComponentContainer> changedContainers = events.length > 0
                            ? new HashSet<ComponentContainer>() : null;
                    Set<RADComponent<?>> compsToSelect = null;
                    FormNode nodeToSelect = null;
                    for (int i = 0; i < events.length; i++) {
                        FormModelEvent ev = events[i];

                        if (ev.isModifying()) {
                            modifying = true;
                        }

                        int type = ev.getChangeType();
                        if (type == FormModelEvent.CONTAINER_LAYOUT_EXCHANGED
                                || type == FormModelEvent.CONTAINER_LAYOUT_CHANGED
                                || type == FormModelEvent.COMPONENT_ADDED
                                || type == FormModelEvent.COMPONENT_REMOVED
                                || type == FormModelEvent.COMPONENTS_REORDERED) {
                            ComponentContainer cont = ev.getContainer();
                            if (changedContainers == null
                                    || !changedContainers.contains(cont)) {
                                updateNodeChildren(cont);
                                if (changedContainers != null) {
                                    changedContainers.add(cont);
                                }
                            }

                            if (type == FormModelEvent.COMPONENT_REMOVED) {
                                FormNode select;
                                if (cont instanceof RADComponent) {
                                    select = ((RADComponent) cont).getNodeReference();
                                } else {
                                    select = getOthersContainerNode();
                                }

                                if (!(nodeToSelect instanceof RADComponentNode)) {
                                    if (nodeToSelect != formRootNode) {
                                        nodeToSelect = select;
                                    }
                                } else if (nodeToSelect != select) {
                                    nodeToSelect = formRootNode;
                                }
                            } else if (type == FormModelEvent.CONTAINER_LAYOUT_EXCHANGED) {
                                assert cont instanceof RADVisualContainer<?>;
                                nodeToSelect = ((RADVisualContainer<?>) cont).getLayoutNodeReference();
                            } else if (type == FormModelEvent.COMPONENT_ADDED
                                    && ev.getComponent().isInModel()) {
                                if (compsToSelect == null) {
                                    compsToSelect = new HashSet<>();
                                }
                                compsToSelect.add(ev.getComponent());
                                if (ev.getContainer() instanceof RADVisualContainer<?>) {
                                    compsToSelect.remove((RADVisualContainer<?>) ev.getContainer());
                                }
                            }
                        } else if (type == FormModelEvent.COLUMN_VIEW_EXCHANGED){
                            updateNodeChildren(ev.getColumn());
                            nodeToSelect = ev.getColumn().getViewControl().getNodeReference();
                        }
                    }
                    PlatypusFormLayoutView designer = getFormDesigner();
                    if (designer != null) {
                        designer.updateVisualSettings();
                        if (compsToSelect != null) {
                            designer.clearSelectionImpl();
                            for (RADComponent<?> comp : compsToSelect) {
                                designer.addComponentToSelectionImpl(comp);
                            }
                            designer.updateNodesSelection();
                        } else if (nodeToSelect != null) {
                            designer.setSelectedNode(nodeToSelect);
                        }
                    }

                    if (modifying) { // mark the form document modified explicitly
                        getFormDataObject().getLookup().lookup(PlatypusFormSupport.class).markFormModified();
                    }
                }
            };
            formModel.addFormModelListener(formListener);
        }
    }

    private void detachFormListener() {
        if (formListener != null) {
            formModel.removeFormModelListener(formListener);
            formListener = null;
        }
    }

    /**
     * Updates (sub)nodes of a container (in Component Inspector) after a change
     * has been made (like component added or removed).
     */
    void updateNodeChildren(ComponentContainer radCont) {
        FormNode node = null;

        if (radCont == null || radCont == formModel.getModelContainer()) {
            node = (formRootNode != null ? getOthersContainerNode() : null);
        } else if (radCont instanceof RADComponent) {
            node = ((RADComponent) radCont).getNodeReference();
        }

        if (node != null) {
            node.updateChildren();
        }
    }

    private void attachDataObjectListener() {
        if (dataObjectListener == null) {
            dataObjectListener = new PropertyChangeListener() {
                @Override
                public void propertyChange(PropertyChangeEvent ev) {
                    switch (ev.getPropertyName()) {
                        case DataObject.PROP_NAME:
                            // PlatypusFormDataObject's name has changed
                            String name = formDataObject.getName();
                            formModel.setName(name);
                            formRootNode.updateName(name);
                            // multiview updated by FormEditorSupport
                            // code regenerated by FormRefactoringUpdate
                            break;
                        case DataObject.PROP_COOKIE:
                            break;
                    }
                }
            };
            formDataObject.addPropertyChangeListener(dataObjectListener);
        }
    }

    private void detachDataObjectListener() {
        if (dataObjectListener != null) {
            formDataObject.removePropertyChangeListener(dataObjectListener);
            dataObjectListener = null;
        }
    }

    void reinstallListener() {
        if (formListener != null) {
            formModel.removeFormModelListener(formListener);
            formModel.addFormModelListener(formListener);
        }
    }

    /**
     * Returns code editor pane for the specified form.
     *
     * @param formModel form model.
     * @return JEditorPane set up with the actuall forms java source
     */
    public static JEditorPane createCodeEditorPane(FormModel formModel) {
        PlatypusFormDataObject dobj = formModel.getDataObject();
        JEditorPane codePane = new JEditorPane();
        FormUtils.setupEditorPane(codePane, dobj.getPrimaryFile(), 0);
        return codePane;
    }

    /**
     * @param formModel form model.
     * @return PlatypusFormLayoutView for given form
     */
    public static PlatypusFormLayoutView getFormDesigner(FormModel formModel) {
        FormEditor formEditor = openForms.get(formModel);
        return formEditor != null ? formEditor.getFormDesigner() : null;
    }

    /**
     * Returns code generator for the specified form.
     *
     * @param formModel form model.
     * @return CodeGenerator for given form
     */
    public static CodeGenerator getCodeGenerator(FormModel formModel) {
        FormEditor formEditor = openForms.get(formModel);
        return formEditor != null ? formEditor.getCodeGenerator() : null;
    }

    /**
     * Returns form editor for the specified form.
     *
     * @param formModel form model.
     * @return FormEditor instance for given form
     */
    public static FormEditor getFormEditor(FormModel formModel) {
        return openForms.get(formModel);
    }

    UndoRedo.Manager getFormUndoRedoManager() {
        return formModel != null ? formModel.getUndoRedoManager() : null;
    }

    public void registerDefaultComponentAction(Action action) {
        if (defaultActions == null) {
            createDefaultComponentActionsList();
        } else {
            defaultActions.remove(action);
        }
        defaultActions.add(0, action);
    }

    public void unregisterDefaultComponentAction(Action action) {
        if (defaultActions != null) {
            defaultActions.remove(action);
        }
    }

    private void createDefaultComponentActionsList() {
        defaultActions = new ArrayList<>();
        defaultActions.add(SystemAction.get(EditContainerAction.class));
        defaultActions.add(SystemAction.get(EditFormAction.class));
        defaultActions.add(SystemAction.get(DefaultRADAction.class));
    }

    Collection<Action> getDefaultComponentActions() {
        if (defaultActions == null) {
            createDefaultComponentActionsList();
        }
        return Collections.unmodifiableList(defaultActions);
    }

    public static boolean isNonVisualTrayEnabled() {
        return Boolean.getBoolean("netbeans.form.non_visual_tray"); // NOI18N
    }
}
