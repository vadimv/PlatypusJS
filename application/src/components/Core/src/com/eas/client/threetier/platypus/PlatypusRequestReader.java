/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.threetier.platypus;

import com.eas.client.changes.BinaryChanges;
import com.eas.client.metadata.Parameter;
import com.eas.client.metadata.Parameters;
import com.eas.client.threetier.Request;
import com.eas.client.threetier.requests.AppQueryRequest;
import com.eas.client.threetier.requests.CommitRequest;
import com.eas.client.threetier.requests.CreateServerModuleRequest;
import com.eas.client.threetier.requests.DisposeServerModuleRequest;
import com.eas.client.threetier.requests.ExecuteQueryRequest;
import com.eas.client.threetier.requests.RPCRequest;
import com.eas.client.threetier.requests.LogoutRequest;
import com.eas.client.threetier.requests.ModuleStructureRequest;
import com.eas.client.threetier.requests.PlatypusRequestVisitor;
import com.eas.client.threetier.requests.PlatypusRequestsFactory;
import com.eas.client.threetier.requests.ResourceRequest;
import com.eas.client.threetier.requests.CredentialRequest;
import com.eas.proto.CoreTags;
import com.eas.proto.ProtoReader;
import com.eas.proto.ProtoReaderException;
import com.eas.proto.dom.ProtoDOMBuilder;
import com.eas.proto.dom.ProtoNode;
import com.eas.script.Scripts;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 *
 * @author mg
 */
public class PlatypusRequestReader implements PlatypusRequestVisitor {

    protected byte[] bytes;
    protected Scripts.Space space;

    public PlatypusRequestReader(byte[] aBytes, Scripts.Space aSpace) {
        super();
        bytes = aBytes;
        space = aSpace;
    }

    /**
     *
     * @param reader
     * @return
     * @throws IOException
     * @throws ProtoReaderException
     */
    public static Request read(ProtoReader reader, Scripts.Space aSpace) throws Exception {
        Request rq = null;
        Integer type = null;
        byte[] data = null;
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_REQUEST_TYPE:
                    type = reader.getInt();
                    break;
                case RequestsTags.TAG_REQUEST_DATA:
                    data = reader.getSubStreamData();
                    break;
                case RequestsTags.TAG_REQUEST_END:
                    if (type != null) {
                        rq = PlatypusRequestsFactory.create(type);
                        PlatypusRequestReader requestReader = new PlatypusRequestReader(data, aSpace);
                        rq.accept(requestReader);
                        break;
                    } else {
                        throw new NullPointerException("Request type must present");
                    }
            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF && reader.getCurrentTag() != RequestsTags.TAG_REQUEST_END);
        return rq;
    }

    @Override
    public void visit(ModuleStructureRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new NullPointerException("No module name specified");
        }
        rq.setModuleOrResourceName(dom.getChild(RequestsTags.TAG_MODULE_NAME).getString());
    }

    @Override
    public void visit(AppQueryRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            throw new NullPointerException("No query specified");
        }
        rq.setQueryName(dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
        if (dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
            rq.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
        }
    }

    @Override
    public void visit(ResourceRequest rq) throws Exception {
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_RESOURCE_NAME)) {
            throw new NullPointerException("No resource name specified");
        }
        rq.setResourceName(dom.getChild(RequestsTags.TAG_RESOURCE_NAME).getString());
        if (dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
            rq.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
        }
    }

    @Override
    public void visit(CreateServerModuleRequest rq) throws Exception {
        final ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new ProtoReaderException("Module name not specified.");
        }
        rq.setModuleName(dom.getChild(RequestsTags.TAG_MODULE_NAME).getString());
        if (dom.containsChild(RequestsTags.TAG_TIMESTAMP)) {
            rq.setTimeStamp(dom.getChild(RequestsTags.TAG_TIMESTAMP).getDate());
        }
    }

    @Override
    public void visit(LogoutRequest rq) throws Exception {
    }

    @Override
    public void visit(CommitRequest rq) throws Exception {
        ProtoReader reader = new ProtoReader(new ByteArrayInputStream(bytes));
        rq.setChanges(null);
        do {
            switch (reader.getNextTag()) {
                case RequestsTags.TAG_CHANGES:
                    rq.setChanges(BinaryChanges.read(reader.getSubStreamData()));
                    break;
            }
        } while (reader.getCurrentTag() != CoreTags.TAG_EOF);
    }

    @Override
    public void visit(DisposeServerModuleRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        if (!input.containsChild(RequestsTags.TAG_MODULE_NAME)) {
            throw new ProtoReaderException("Module name is not specified.");
        }
        rq.setModuleName(input.getChild(RequestsTags.TAG_MODULE_NAME).getString());
    }

    @Override
    public void visit(RPCRequest rq) throws Exception {
        final ProtoNode input = ProtoDOMBuilder.buildDOM(bytes);
        final Iterator<ProtoNode> it = input.iterator();
        final List<Object> args = new ArrayList<>();
        while (it.hasNext()) {
            final ProtoNode node = it.next();
            switch (node.getNodeTag()) {
                case RequestsTags.TAG_MODULE_NAME:
                    rq.setModuleName(node.getString());
                    break;
                case RequestsTags.TAG_METHOD_NAME:
                    rq.setMethodName(node.getString());
                    break;
                case RequestsTags.TAG_ARGUMENT_VALUE: {
                    args.add(space.parseJsonWithDates(node.getString()));
                    break;
                }
            }
        }
        rq.setArguments(args.toArray());
    }

    public static Parameter readParameter(ProtoNode node) throws ProtoReaderException {
        Parameter param = new Parameter();
        Object value;
        int paramType;
        int paramMode;
        String paramTypeName = null;
        String paramTypeClassName = null;
        String paramName = null;
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE)) {
            throw new ProtoReaderException("No parameter type");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME)) {
            throw new ProtoReaderException("No parameter type name");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME)) {
            throw new ProtoReaderException("No parameter type java class name");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_MODE)) {
            throw new ProtoReaderException("No parameter mode");
        }
        if (!node.containsChild(RequestsTags.TAG_SQL_PARAMETER_NAME)) {
            throw new ProtoReaderException("No parameter name");
        }
        paramType = node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE).getInt();
        paramTypeName = node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_NAME).getString();
        paramTypeClassName = node.getChild(RequestsTags.TAG_SQL_PARAMETER_TYPE_CLASS_NAME).getString();
        paramMode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_MODE).getInt();
        paramName = node.getChild(RequestsTags.TAG_SQL_PARAMETER_NAME).getString();
        param.getTypeInfo().setSqlType(paramType);
        param.getTypeInfo().setSqlTypeName(paramTypeName);
        param.getTypeInfo().setJavaClassName(paramTypeClassName);
        if (node.containsChild(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION)) {
            param.setDescription(node.getChild(RequestsTags.TAG_SQL_PARAMETER_DESCRIPTION).getString());
        }
        value = null;
        if (node.containsChild(RequestsTags.TAG_SQL_PARAMETER_VALUE)) {
            ProtoNode valueNode = node.getChild(RequestsTags.TAG_SQL_PARAMETER_VALUE);
            value = valueNode.getJDBCCompatible(paramType);
        }
        param.setName(paramName);
        param.setValue(value);
        param.setMode(paramMode);
        return param;
    }

    @Override
    public void visit(ExecuteQueryRequest rq) throws Exception {
        rq.setParams(new Parameters());
        ProtoNode dom = ProtoDOMBuilder.buildDOM(bytes);
        if (!dom.containsChild(RequestsTags.TAG_QUERY_ID)) {
            throw new NullPointerException("No query specified");
        }
        rq.setQueryName(dom.getChild(RequestsTags.TAG_QUERY_ID).getString());
        Iterator<ProtoNode> it = dom.iterator();
        while (it.hasNext()) {
            ProtoNode node = it.next();
            if (node.getNodeTag() == RequestsTags.TAG_SQL_PARAMETER) {
                Parameter param = readParameter(node);
                rq.getParams().add(param);
            }
        }
    }

    @Override
    public void visit(CredentialRequest rq) throws Exception {
    }
}
