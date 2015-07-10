/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client;

import com.eas.client.cache.PlatypusFiles;
import com.eas.client.cache.PlatypusFilesSupport;
import com.eas.util.FileUtils;
import com.eas.util.IDGenerator;
import java.io.File;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

/**
 *
 * @author vv
 */
public class AppElementFiles {

    private final SortedSet<File> files = new TreeSet<>();
    private final Set<String> filesExtensions = new HashSet<>();

    public AppElementFiles() {
        super();
    }

    public void addFile(File file) {
        files.add(file);
        if (!file.isDirectory()) {
            filesExtensions.add(FileUtils.getFileExtension(file));
        }
    }

    public Set<File> getFiles() {
        return Collections.unmodifiableSet(files);
    }

    public String getAppElementId(int appElementType) {
        if (appElementType == ClientConstants.ET_COMPONENT || appElementType == ClientConstants.ET_FORM || appElementType == ClientConstants.ET_REPORT) {
            return PlatypusFilesSupport.getAppElementIdByAnnotation(findFileByExtension(PlatypusFiles.JAVASCRIPT_EXTENSION));
        } else if (appElementType == ClientConstants.ET_QUERY) {
            return PlatypusFilesSupport.getAppElementIdByAnnotation(findFileByExtension(PlatypusFiles.SQL_EXTENSION));
        } else if (appElementType == ClientConstants.ET_DB_SCHEME) {
            return IDGenerator.genID() + "";
        }
        return null;
    }

    public Integer getAppElementType() {
        if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION) && hasExtension(PlatypusFiles.FORM_EXTENSION)) {
            return ClientConstants.ET_FORM;
        } else if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION) && (hasExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION) || hasExtension(PlatypusFiles.REPORT_LAYOUT_EXTENSION_X))) {
            return ClientConstants.ET_REPORT;
        } else if (hasExtension(PlatypusFiles.JAVASCRIPT_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION)) {
            return ClientConstants.ET_COMPONENT;
        } else if (hasExtension(PlatypusFiles.SQL_EXTENSION) && hasExtension(PlatypusFiles.MODEL_EXTENSION)) {
            return ClientConstants.ET_QUERY;
        } else if (hasExtension(PlatypusFiles.DB_SCHEME_EXTENSION)) {
            return ClientConstants.ET_DB_SCHEME;
        }
        return null;
    }

    public boolean isModule() {
        Integer type = getAppElementType();
        if (type != null) {
            return type == ClientConstants.ET_COMPONENT || type == ClientConstants.ET_FORM || type == ClientConstants.ET_REPORT;
        } else {
            return false;
        }
    }

    public boolean hasExtension(String ext) {
        return filesExtensions.contains(ext);
    }

    public File findFileByExtension(String aExt) {
        for (File file : files) {
            String ext = FileUtils.getFileExtension(file);
            if (ext != null && ext.equalsIgnoreCase(aExt)) {
                return file;
            }
        }
        return null;
    }

    private File removeFileByExtension(String ext) {
        if (ext != null) {
            for (File file : files) {
                if (ext.equals(FileUtils.getFileExtension(file))) {
                    files.remove(file);
                    filesExtensions.remove(ext);
                    return file;
                }
            }
        }
        return null;
    }

    public File removeFile(File aFile) {
        return removeFileByExtension(FileUtils.getFileExtension(aFile));
    }

    public boolean isEmpty() {
        return files.isEmpty();
    }

    public Date getLastModified() {
        long lastModified = 0;
        for (File file : files) {
            if (file.lastModified() > lastModified) {
                lastModified = file.lastModified();
            }
        }
        return lastModified != 0 ? new Date(lastModified) : null;
    }

    public AppElementFiles copy() {
        AppElementFiles aFiles = new AppElementFiles();
        files.stream().forEach((f) -> {
            aFiles.addFile(f);
        });
        return aFiles;
    }
}
