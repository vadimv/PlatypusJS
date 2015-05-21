/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.http;

import com.eas.client.changes.ChangeValue;
import com.eas.client.changes.ChangeVisitor;
import com.eas.client.changes.Command;
import com.eas.client.changes.Delete;
import com.eas.client.changes.Insert;
import com.eas.client.changes.Update;
import com.eas.script.Scripts;
import com.eas.util.JSONUtils;
import com.eas.util.StringUtils;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import jdk.nashorn.api.scripting.JSObject;

/**
 *
 * @author mg
 */
public class ChangeJSONWriter implements ChangeVisitor {

    private static final String CHANGE_DATA_NAME = "data";
    private static final String CHANGE_KEYS_NAME = "keys";
    private static final String CHANGE_PARAMETERS_NAME = "parameters";
    private static final String CHANGE_KIND_NAME = "kind";
    private static final String CHANGE_ENTITY_NAME = "entity";

    protected String written;
    protected Scripts.Space space;

    public ChangeJSONWriter(Scripts.Space aSpace) {
        super();
        space = aSpace;
    }

    public String getWritten() {
        return written;
    }

    @Override
    public void visit(Insert aChange) throws Exception {
        List<String> data = new ArrayList<>();
        for (ChangeValue data1 : aChange.getData()) {
            data.add(data1.name);
            data.add(valueToString(data1.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("insert").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_DATA_NAME, JSONUtils.o(data.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Update aChange) throws Exception {
        List<String> data = new ArrayList<>();
        for (ChangeValue datum : aChange.getData()) {
            data.add(datum.name);
            data.add(valueToString(datum.value));
        }
        List<String> keys = new ArrayList<>();
        for (ChangeValue key : aChange.getKeys()) {
            keys.add(key.name);
            keys.add(valueToString(key.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("update").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_DATA_NAME, JSONUtils.o(data.toArray(new String[]{})).toString(),
                CHANGE_KEYS_NAME, JSONUtils.o(keys.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Delete aChange) throws Exception {
        List<String> keys = new ArrayList<>();
        for (ChangeValue key : aChange.getKeys()) {
            keys.add(key.name);
            keys.add(valueToString(key.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("delete").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_KEYS_NAME, JSONUtils.o(keys.toArray(new String[]{})).toString()
        ).toString();
    }

    @Override
    public void visit(Command aChange) throws Exception {
        List<String> params = new ArrayList<>();
        for (ChangeValue parameter : aChange.getParameters()) {
            params.add(parameter.name);
            params.add(valueToString(parameter.value));
        }
        written = JSONUtils.o(
                CHANGE_KIND_NAME, JSONUtils.s("command").toString(),
                CHANGE_ENTITY_NAME, JSONUtils.s(aChange.entityName).toString(),
                CHANGE_PARAMETERS_NAME, JSONUtils.as(params.toArray(new String[]{})).toString()
        ).toString();
    }

    private String valueToString(Object aValue) throws Exception {
        if (aValue != null) {
            if (aValue instanceof JSObject) {
                aValue = space.toJava(aValue);
            }
            if (aValue instanceof Boolean) {
                return aValue.toString();
            } else if (aValue instanceof Number) {
                return StringUtils.formatDouble(((Number) aValue).doubleValue());
            } else if (aValue instanceof String) {
                return JSONUtils.s((String) aValue).toString();
            } else if (aValue instanceof Date) {
                Long millis = ((Date) aValue).getTime();
                return millis.toString();
            } else {
                throw new Exception("Value of unknown or unsupported type found! It's class is: " + aValue.getClass().getName());
            }
        } else {
            return "null";
        }
    }
}
