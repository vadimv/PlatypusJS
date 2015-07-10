/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.model;

import com.eas.client.AppElementFiles;
import com.eas.client.DatabasesClient;
import com.eas.client.SqlQuery;
import com.eas.client.cache.PlatypusFiles;
import com.eas.client.metadata.DataTypeInfo;
import com.eas.client.model.query.QueryModel;
import com.eas.client.model.store.XmlDom2QueryModel;
import com.eas.client.queries.QueriesProxy;
import com.eas.client.settings.SettingsConstants;
import com.eas.util.FileUtils;
import com.eas.xml.dom.Source2XmlDom;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 *
 * @author mg
 */
public class QueryDocument {

    public final static String OUTPUT_FIELDS_TAG_NAME = "outputFields";
    public final static String OUTPUT_FIELD_TAG_NAME = "field";
    public final static String FIELD_NAME_ATTRIBUTE_NAME = "bindedColumn";
    public final static String FIELD_DESCRIPTION_ATTRIBUTE_NAME = "description";
    public final static String FIELD_TYPE_ATTRIBUTE_NAME = "sqlType";
    
    public static class StoredFieldMetadata {

        public String bindedColumn;
        public String description;
        public DataTypeInfo typeInfo;

        public StoredFieldMetadata() {
            super();
        }

        public StoredFieldMetadata(String aBindedColumn) {
            super();
            bindedColumn = aBindedColumn;
        }

        public DataTypeInfo getTypeInfo() {
            return typeInfo;
        }

        public void setTypeInfo(DataTypeInfo aValue) {
            typeInfo = aValue;
        }

        public String getBindedColumn() {
            return bindedColumn;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String aValue) {
            description = aValue;
        }
    }

    protected SqlQuery query;
    protected QueryModel model;
    protected List<StoredFieldMetadata> additionalFieldsMetadata;

    public QueryDocument(SqlQuery aQuery, QueryModel aModel, List<StoredFieldMetadata> aAdditionalFieldsMetadata) {
        super();
        query = aQuery;
        model = aModel;
        additionalFieldsMetadata = aAdditionalFieldsMetadata;
        query.setDatasourceName(model.getDatasourceName());
        assert query.getEntityName() != null : "SqlQuery should be constructured with non-null entity id!";
    }

    public List<StoredFieldMetadata> getAdditionalFieldsMetadata() {
        return additionalFieldsMetadata;
    }

    public SqlQuery getQuery() {
        return query;
    }

    public QueryModel getModel() {
        return model;
    }

    public static QueryDocument parse(String aName, AppElementFiles aFiles, DatabasesClient aBasesProxy, QueriesProxy<SqlQuery> aQueriesPrioxy) throws Exception {
        // sql source
        File sqlFile = aFiles.findFileByExtension(PlatypusFiles.SQL_EXTENSION);
        String sqlContent = FileUtils.readString(sqlFile, SettingsConstants.COMMON_ENCODING);
        // sql dialect source
        File dialectFile = aFiles.findFileByExtension(PlatypusFiles.DIALECT_EXTENSION);
        String dialectContent = FileUtils.readString(dialectFile, SettingsConstants.COMMON_ENCODING);
        // query model
        File modelFile = aFiles.findFileByExtension(PlatypusFiles.MODEL_EXTENSION);
        String modelContent = FileUtils.readString(modelFile, SettingsConstants.COMMON_ENCODING);
        Document modelDoc = Source2XmlDom.transform(modelContent);        
        QueryModel model = XmlDom2QueryModel.transform(aBasesProxy, aQueriesPrioxy, modelDoc);
        // output fields hints
        File outFile = aFiles.findFileByExtension(PlatypusFiles.OUT_EXTENSION);
        String outContent = FileUtils.readString(outFile, SettingsConstants.COMMON_ENCODING);
        Document outDoc = Source2XmlDom.transform(outContent);
        List<QueryDocument.StoredFieldMetadata> additionalFields = parseFieldsHintsTag(outDoc.getDocumentElement());
        //
        SqlQuery query = new SqlQuery(aBasesProxy);
        query.setEntityName(aName);
        query.setSqlText(sqlContent);
        query.setFullSqlText(dialectContent);
        return new QueryDocument(query, model, additionalFields);
    }

    public static List<QueryDocument.StoredFieldMetadata> parseFieldsHintsTag(Element aTag) {
        List<QueryDocument.StoredFieldMetadata> additionalFields = new ArrayList<>();
        NodeList fieldsNodes = aTag.getChildNodes();
        for (int k = 0; k < fieldsNodes.getLength(); k++) {
            Node fn = fieldsNodes.item(k);
            if (OUTPUT_FIELD_TAG_NAME.equals(fn.getNodeName())) {
                NamedNodeMap attrs = fn.getAttributes();
                Node bindedColAttr = attrs.getNamedItem(FIELD_NAME_ATTRIBUTE_NAME),
                        descAttr = attrs.getNamedItem(FIELD_DESCRIPTION_ATTRIBUTE_NAME),
                        typeAttr = attrs.getNamedItem(FIELD_TYPE_ATTRIBUTE_NAME);
                if (bindedColAttr != null && (descAttr != null || typeAttr != null)) {
                    QueryDocument.StoredFieldMetadata additionalField = new QueryDocument.StoredFieldMetadata();
                    additionalField.bindedColumn = bindedColAttr.getNodeValue();
                    if (descAttr != null) {
                        additionalField.description = descAttr.getNodeValue();
                    }
                    if (typeAttr != null) {
                        additionalField.typeInfo = DataTypeInfo.valueOf(Integer.valueOf(typeAttr.getNodeValue()));
                    }
                    additionalFields.add(additionalField);
                }
            }
        }
        return additionalFields;
    }
}
