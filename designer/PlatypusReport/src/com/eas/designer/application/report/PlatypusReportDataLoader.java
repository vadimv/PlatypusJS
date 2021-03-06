/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.designer.application.report;

import com.eas.client.cache.PlatypusFiles;
import org.openide.ErrorManager;
import org.openide.filesystems.FileObject;
import org.openide.filesystems.FileUtil;
import org.openide.loaders.DataObject;
import org.openide.loaders.DataObjectExistsException;
import org.openide.loaders.FileEntry;
import org.openide.loaders.MultiDataObject;
import org.openide.loaders.MultiFileLoader;

/**
 *
 * @author mg
 */
@DataObject.Registrations(value = {
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusReport_loader_name", mimeType = "application/vnd.ms-excel"),
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusReport_loader_name", mimeType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"),
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusReport_loader_name", mimeType = "text/javascript"),
    @DataObject.Registration(position = 200, displayName = "#LBL_PlatypusReport_loader_name", mimeType = "text/model+xml")
})
public class PlatypusReportDataLoader extends MultiFileLoader {

    static final long serialVersionUID = 4579145057402524013L;

    /**
     * Constructs a new PlatypusModuleDataLoader
     */
    public PlatypusReportDataLoader() {
        super(PlatypusReportDataObject.class.getName()); // NOI18N
    }

    @Override
    protected String actionsContext() {
        return "Loaders/text/javascript/Actions/"; // NOI18N
    }

    /**
     * For a given file finds a primary file.
     *
     * @param fo the file to find primary file for
     *
     * @return the primary file for the file or null if the file is not
     * recognized by this loader
     */
    @Override
    protected FileObject findPrimaryFile(FileObject fo) {
        return findPrimaryFileImpl(fo);
    }

    public static FileObject findPrimaryFileImpl(FileObject fo) {
        // never recognize folders.
        if (!fo.isFolder()) {
            String ext = fo.getExt();
            if (ext.equals(PlatypusFiles.MODEL_EXTENSION) && (FileUtil.findBrother(fo, PlatypusFiles.REPORT_LAYOUT_EXTENSION_X) != null || FileUtil.findBrother(fo, PlatypusFiles.REPORT_LAYOUT_EXTENSION) != null)) {
                return FileUtil.findBrother(fo, PlatypusFiles.JAVASCRIPT_EXTENSION);
            } else if ((ext.equals(PlatypusFiles.REPORT_LAYOUT_EXTENSION_X) || ext.equals(PlatypusFiles.REPORT_LAYOUT_EXTENSION)) && FileUtil.findBrother(fo, PlatypusFiles.MODEL_EXTENSION) != null) {
                return FileUtil.findBrother(fo, PlatypusFiles.JAVASCRIPT_EXTENSION);
            } else if (ext.equals(PlatypusFiles.JAVASCRIPT_EXTENSION)
                    && FileUtil.findBrother(fo, PlatypusFiles.MODEL_EXTENSION) != null
                    && (FileUtil.findBrother(fo, PlatypusFiles.REPORT_LAYOUT_EXTENSION_X) != null
                        || FileUtil.findBrother(fo, PlatypusFiles.REPORT_LAYOUT_EXTENSION) != null)) {
                return fo;
            }
        }
        return null;
    }

    /**
     * Creates the right data object for given primary file. It is guaranteed
     * that the provided file is realy primary file returned from the method
     * findPrimaryFile.
     *
     * @param primaryFile the primary file
     * @return the data object for this file
     * @exception DataObjectExistsException if the primary file already has data
     * object
     */
    @Override
    protected MultiDataObject createMultiObject(FileObject primaryFile) throws DataObjectExistsException {
        try {
            return new PlatypusReportDataObject(primaryFile, this);
        } catch (Exception ex) {
            if (ex instanceof DataObjectExistsException) {
                throw (DataObjectExistsException) ex;
            } else {
                ErrorManager.getDefault().notify(ex);
                return null;
            }
        }
    }

    // [?] Probably needed in case FormDataObject is deserialized, then the
    // secondary entry is created additionally.
    @Override
    protected MultiDataObject.Entry createSecondaryEntry(MultiDataObject obj,
            FileObject secondaryFile) {
        assert PlatypusFiles.MODEL_EXTENSION.equals(secondaryFile.getExt())
                || PlatypusFiles.REPORT_LAYOUT_EXTENSION_X.equals(secondaryFile.getExt()) 
                || PlatypusFiles.REPORT_LAYOUT_EXTENSION.equals(secondaryFile.getExt());
        return new FileEntry(obj, secondaryFile);
    }

    @Override
    protected MultiDataObject.Entry createPrimaryEntry(MultiDataObject obj, FileObject primaryFile) {
        return new FileEntry(obj, primaryFile);
    }
}
