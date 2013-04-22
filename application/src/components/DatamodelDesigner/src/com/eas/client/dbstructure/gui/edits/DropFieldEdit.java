/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.dbstructure.gui.edits;

import com.bearsoft.rowset.metadata.Field;
import com.bearsoft.rowset.metadata.ForeignKeySpec;
import com.eas.client.DbClient;
import com.eas.client.dbstructure.SqlActionsController;
import com.eas.client.dbstructure.SqlActionsController.AddFieldAction;
import com.eas.client.dbstructure.SqlActionsController.CreateConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DescribeFieldAction;
import com.eas.client.dbstructure.SqlActionsController.DropConstraintAction;
import com.eas.client.dbstructure.SqlActionsController.DropFieldAction;
import com.eas.client.dbstructure.exceptions.DbActionException;
import com.eas.client.model.Relation;
import com.eas.client.model.dbscheme.FieldsEntity;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author mg
 */
public class DropFieldEdit extends DbStructureEdit {

    protected String tableName = null;
    protected Field field = null;
    protected List<ForeignKeySpec> inFks = new ArrayList<>();
    protected List<ForeignKeySpec> outFks = new ArrayList<>();

    public DropFieldEdit(SqlActionsController aSqlController, Field aField, FieldsEntity tableEntity) {
        super(aSqlController);
        tableName = tableEntity.getTableName();
        field = new Field(aField);
        extractInFks(tableEntity);
        extractOutFks(tableEntity);
    }

    @Override
    protected void doUndoWork() throws Exception {
        createField();
        createConstraints(inFks);
        createConstraints(outFks);
    }

    @Override
    protected void doRedoWork() throws Exception {
        dropConstraints(inFks);
        dropConstraints(outFks);
        dropField();
    }

    protected void dropField() throws Exception {
        DropFieldAction laction = sqlController.createDropFieldAction(tableName, field);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            ex.setParam2(field.getName());
            throw ex;
        }
    }

    protected void dropConstraints(List<ForeignKeySpec> fks) throws Exception {
        for (ForeignKeySpec fk : fks) {
            DropConstraintAction caction = sqlController.createDropConstraintAction(fk);
            if (!caction.execute()) {
                DbActionException ex = new DbActionException(caction.getErrorString());
                ex.setParam1(fk.getCName());
                throw ex;
            }
        }
    }

    private void createConstraints(List<ForeignKeySpec> fks) throws DbActionException {
        for (ForeignKeySpec fk : fks) {
            CreateConstraintAction laction = sqlController.createCreateConstraintAction(fk);
            if (!laction.execute()) {
                DbActionException ex = new DbActionException(laction.getErrorString());
                ex.setParam1(fk.getCName());
                throw ex;
            }
        }
    }

    private void createField() throws DbActionException {
        AddFieldAction laction = sqlController.createAddFieldAction(tableName, field);
        if (!laction.execute()) {
            DbActionException ex = new DbActionException(laction.getErrorString());
            ex.setParam1(tableName);
            ex.setParam2(field.getName());
            throw ex;
        }
        if (field.getDescription() != null && !field.getDescription().isEmpty()) {
            DescribeFieldAction daction = sqlController.createDescribeFieldAction(tableName, field.getName(), field.getDescription());
            if (!daction.execute()) {
                DbActionException ex = new DbActionException(daction.getErrorString());
                ex.setParam1(tableName);
                ex.setParam2(field.getName());
                throw ex;
            }
        }
    }

    private void extractInFks(FieldsEntity aEntity) {
        if (aEntity != null) {
            Set<Relation<FieldsEntity>> rels = aEntity.getInRelations();
            for (Relation<FieldsEntity> r : rels) {
                if (r != null) {
                    FieldsEntity lEntity = r.getLeftEntity();
                    FieldsEntity rEntity = r.getRightEntity();
                    assert rEntity == aEntity;

                    if (rEntity != lEntity && r.getRightField().toLowerCase().equals(field.getName().toLowerCase())) {
                        ForeignKeySpec fkSpec = new ForeignKeySpec(lEntity.getTableSchemaName(), lEntity.getTableName(), r.getLeftField(), r.getFkName(), r.getFkUpdateRule(), r.getFkDeleteRule(), r.isFkDeferrable(), rEntity.getTableSchemaName(), rEntity.getTableName(), r.getRightField(), null);
                        inFks.add(fkSpec);
                    }
                }
            }
        }
    }

    private void extractOutFks(FieldsEntity aEntity) {
        if (aEntity != null) {
            Set<Relation<FieldsEntity>> rels = aEntity.getOutRelations();
            for (Relation<FieldsEntity> r : rels) {
                if (r != null) {
                    FieldsEntity lEntity = r.getLeftEntity();
                    FieldsEntity rEntity = r.getRightEntity();
                    assert lEntity == aEntity;
                    if (r.getLeftField().toLowerCase().equals(field.getName().toLowerCase())) {
                        ForeignKeySpec fkSpec = new ForeignKeySpec(lEntity.getTableSchemaName(), lEntity.getTableName(), r.getLeftField(), r.getFkName(), r.getFkUpdateRule(), r.getFkDeleteRule(), r.isFkDeferrable(), rEntity.getTableSchemaName(), rEntity.getTableName(), r.getRightField(), null);
                        outFks.add(fkSpec);
                    }
                }
            }
        }
    }

    @Override
    protected void clearTablesCache() {
        try {
            DbClient client = sqlController.getClient();
            client.dbTableChanged(sqlController.getDbId(), sqlController.getSchema(), tableName);
            for (ForeignKeySpec fk : inFks) {
                client.dbTableChanged(sqlController.getDbId(), sqlController.getSchema(), fk.getTable());
            }
        } catch (Exception ex) {
            Logger.getLogger(DropFieldEdit.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
