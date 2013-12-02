/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.bearsoft.org.netbeans.modules.form.completion;

import com.bearsoft.org.netbeans.modules.form.FormModel;
import com.bearsoft.org.netbeans.modules.form.FormUtils;
import com.bearsoft.org.netbeans.modules.form.PersistenceException;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormDataObject;
import com.bearsoft.org.netbeans.modules.form.PlatypusFormSupport;
import com.bearsoft.org.netbeans.modules.form.RADComponent;
import com.bearsoft.org.netbeans.modules.form.RADVisualFormContainer;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelGridColumn;
import com.bearsoft.org.netbeans.modules.form.bound.RADModelMapLayer;
import com.eas.client.forms.FormRunner;
import com.eas.client.forms.api.components.model.ModelGrid;
import com.eas.dbcontrols.grid.DbGrid;
import com.eas.designer.application.module.completion.BeanCompletionItem;
import com.eas.designer.application.module.completion.CompletionContext;
import com.eas.designer.application.module.completion.JsCompletionProvider;
import com.eas.designer.application.module.completion.ModuleCompletionContext;
import com.eas.designer.application.module.completion.ModuleThisCompletionContext;
import java.awt.Container;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/**
 *
 * @author vv
 */
public class FormThisCompletionContext extends ModuleThisCompletionContext {
    
    public FormThisCompletionContext(ModuleCompletionContext aParentContext, boolean anEnableJsElementsCompletion) {
        super(aParentContext, anEnableJsElementsCompletion);
    }
    
    @Override
    public void applyCompletionItems(JsCompletionProvider.CompletionPoint point, int offset, CompletionResultSet resultSet) throws Exception {
        super.applyCompletionItems(point, offset, resultSet);
        ModuleCompletionContext.JsCodeCompletionScopeInfo completionScopeInfo = ModuleCompletionContext.getCompletionScopeInfo(getParentContext().getDataObject(), offset, point.filter);
        if (completionScopeInfo.mode == ModuleCompletionContext.CompletionMode.VARIABLES_AND_FUNCTIONS) {
            addItem(resultSet, point.filter, new BeanCompletionItem(Container.class, FormRunner.VIEW_SCRIPT_NAME, null, point.caretBeginWordOffset, point.caretEndWordOffset)); //NOI18N
            fillComponents(point, resultSet);
        }
    }
    
    @Override
    public CompletionContext getChildContext(String fieldName, int offset) throws Exception {
        CompletionContext completionContext = super.getChildContext(fieldName, offset);
        if (completionContext != null) {
            return completionContext;
        }
        if (FormRunner.VIEW_SCRIPT_NAME.equals(fieldName)) {
            return new CompletionContext(Container.class);
        }
        RADComponent<?> comp = getComponentByName(fieldName);
        if (comp != null) {
            Class<?> platypusControlClass = FormUtils.getPlatypusControlClass(comp.getBeanClass());
            if (!ModelGrid.class.isAssignableFrom(platypusControlClass)) {
                return new CompletionContext(platypusControlClass);
            } else {
                return new DbGridCompletionContext((DbGrid) comp.getBeanInstance());

            }
        }
        return null;
    }

    protected void fillComponents(JsCompletionProvider.CompletionPoint point, CompletionResultSet resultSet) {
        FormModel fm = getFormModel();
        for (RADComponent<?> comp : fm.getOrderedComponentList()) {
            if (!(comp instanceof RADModelGridColumn) && !(comp instanceof RADModelMapLayer) && comp.getName() != null && !comp.getName().isEmpty()) {
                // <comp>
                if (point.filter == null || point.filter.isEmpty() || comp.getName().toLowerCase().startsWith(point.filter.toLowerCase())) {
                    String compName = comp.getName();
                    if (RADVisualFormContainer.FORM_NAME.equals(compName)) {
                        continue;
                    }
                    addItem(resultSet, point.filter, new BeanCompletionItem(FormUtils.getPlatypusControlClass(comp.getBeanClass()), compName, null, point.caretBeginWordOffset, point.caretEndWordOffset));
                }
            }
        }
    }

    private FormModel getFormModel() {
        try {
            PlatypusFormDataObject formDataObject = (PlatypusFormDataObject) getParentContext().getDataObject();
            PlatypusFormSupport support = formDataObject.getLookup().lookup(PlatypusFormSupport.class);
            support.loadForm();
            return support.getFormModel();
        } catch (PersistenceException ex) {
            throw new IllegalStateException("Form model can't be read");
        }
    }

    protected RADComponent<?> getComponentByName(String aName) {
        RADComponent<?> comp = getFormModel().getRADComponent(aName);
        if (!(comp instanceof RADModelGridColumn) && !(comp instanceof RADModelMapLayer)) {
            return comp;
        } else {
            return null;
        }
    }


}