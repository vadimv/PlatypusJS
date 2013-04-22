/* Datamodel license
 * Exclusive rights on this code in any form
 * are belong to it's athor.
 * This code was developed for commercial purposes only 
 * For any questions and any actions with this code in any form
 * you have to contact it's athor.
 * All rights reserved
 */
package com.eas.client.settings;

import com.eas.client.ConnectionSettingsVisitor;
import java.util.Properties;

/**
 *
 * @author mg
 */
public abstract class EasSettings {

    public static transient final String EAS_SETTINGS_FILE_NAME = "EasSettings.xml";
    protected Properties info = new Properties();
    protected String url = "";
    protected String name;
    protected boolean editable = true;

    protected EasSettings() {
        super();
    }

    public Properties getInfo() {
        return info;
    }

    public void setInfo(Properties aValue) {
        info = aValue;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String aUrl) {
        if (aUrl != null) {
            aUrl = aUrl.replace('\\', '/');
            aUrl = aUrl.replace("\\s\\r\\n\\t", "");
        }
        url = aUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String aValue) {
        name = aValue;
    }

    public static EasSettings createInstance(String connectionString) throws Exception {
        if (connectionString.startsWith("jdbc:")) {
            return new DbConnectionSettings();
        } else if (connectionString.startsWith("platypus") || connectionString.startsWith("http")) {
            return new PlatypusConnectionSettings();
        } else {
            return null;
        }
    }

    public boolean isEditable() {
        return editable;
    }

    public void setEditable(boolean aValue) {
        editable = aValue;
    }

    public abstract void accept(ConnectionSettingsVisitor v);
}
