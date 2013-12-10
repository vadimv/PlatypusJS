package com.eas.designer.application.module.completion;

import com.eas.designer.application.module.PlatypusModuleDataObject;
import javax.swing.text.Document;
import org.netbeans.spi.editor.completion.CompletionResultSet;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author vv
 */
public class ModuleCompletionProvider extends JsCompletionProvider {

    @Override
    protected void fillCompletionPoint(PlatypusModuleDataObject dataObject, CompletionPoint point, CompletionResultSet resultSet, Document doc, int caretOffset) throws Exception {
        if (dataObject != null && dataObject.getProject().isDbConnected()) {
            CompletionContext completionContext = dataObject.getCompletionContext();
            if (point.getContext() != null && point.getContext().length > 0) {
                for (int i = 0; i < point.getContext().length; i++) {
                    completionContext = completionContext.getChildContext(point.getContext()[i], caretOffset);
                    if (completionContext == null) {
                        return;
                    }
                    caretOffset = 0;
                }
            }
            completionContext.applyCompletionItems(point, caretOffset, resultSet);
        }
    }
}
