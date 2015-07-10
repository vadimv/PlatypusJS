/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.eas.client.queries;

import com.eas.client.DatabasesClientWithResource;
import com.eas.client.SqlQuery;
import com.eas.client.cache.ApplicationSourceIndexer;
import com.eas.client.metadata.Field;
import com.eas.client.metadata.Fields;
import com.eas.client.settings.DbConnectionSettings;
import com.eas.script.JsDoc;
import org.junit.*;
import static org.junit.Assert.*;

/**
 *
 * @author pk, mg
 */
public class ScriptedQueryFactoryTest {

    protected static ApplicationSourceIndexer indexer;
    protected static DatabasesClientWithResource resource;

    public ScriptedQueryFactoryTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
        indexer = new ApplicationSourceIndexer("c:/projects/platypus-tests");
        resource = new DatabasesClientWithResource(new DbConnectionSettings("jdbc:oracle:thin:@asvr/adb", "eas", "eas", "eas", 1));
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testFirstAnnotationsComma1() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n"
                + " * %s %s   , %s,%s\n"
                + " * \n"
                + " * \n"
                + " */\n"
                + "select from dual", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testFirstAnnotationsComma2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s   , %s,%s\r\n"
                + " * \n\r"
                + " * \r\n"
                + " */\n"
                + "select from dual", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testFirstAnnotationsSpace() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * \n\r"
                + " * \r\n"
                + " */\n"
                + "select from dual", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testLastAnnotationsSpace() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format("select from dual"
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * \n\r"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_TAG.toUpperCase(), role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleAnnotationsSpace1() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * select from dual\r\n"
                + " * %s %s    %s %s\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * select from dual\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(q.getReadRoles(), q.getWriteRoles());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleReadAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * select from dual\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_READ_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(0, q.getWriteRoles().size());
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
    }

    @Test
    public void testMiddleWriteAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " * select from dual\r\n"
                + " * \r\n"
                + " */\n"
                + "", JsDoc.Tag.ROLES_ALLOWED_WRITE_TAG, role1, role2, role3);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(0, q.getReadRoles().size());
        assertEquals(3, q.getWriteRoles().size());
        assertTrue(q.getWriteRoles().contains(role1));
        assertTrue(q.getWriteRoles().contains(role2));
        assertTrue(q.getWriteRoles().contains(role3));
    }

    @Test
    public void testMiddleReadWriteAnnotationsSpace2() throws Exception {
        String role1 = "admin";
        String role2 = "mechaniker";
        String role3 = "dispatcher";
        String sqlText = String.format(""
                + "/**\n\r"
                + " * %s %s    %s %s\r\n"
                + " */\n"
                + "select \r\n"
                + "/**\n"
                + " * %s %s    %s \r\n"
                + " * \r\n"
                + " */\n"
                + "from dual",
                JsDoc.Tag.ROLES_ALLOWED_READ_TAG, role1, role2, role3,
                JsDoc.Tag.ROLES_ALLOWED_WRITE_TAG, role1, role2);
        SqlQuery q = new SqlQuery(null, sqlText);
        ScriptedQueryFactory.putRolesMutatables(q);
        assertEquals(3, q.getReadRoles().size());
        assertTrue(q.getReadRoles().contains(role1));
        assertTrue(q.getReadRoles().contains(role2));
        assertTrue(q.getReadRoles().contains(role3));
        assertEquals(2, q.getWriteRoles().size());
        assertTrue(q.getWriteRoles().contains(role1));
        assertTrue(q.getWriteRoles().contains(role2));
        assertFalse(q.getWriteRoles().contains(role3));
    }

    @Test
    public void testCompilingWithSubqueries() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("sub_query_compile", null, null, null);
        assertEquals("/**\n"
                + " * \n"
                + " * @author mg\n"
                + " * @name sub_query_compile\n"
                + " */\n"
                + "SELECT T0.ORDER_NO, 'Some text' AS VALUE_FIELD_1, TABLE1.ID, TABLE1.F1, TABLE1.F3, T0.AMOUNT FROM TABLE1, TABLE2,  (/**\n"
                + " * @name namedQuery4Tests\n"
                + "*/\n"
                + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \n"
                + "From GOODORDER goodOrder\n"
                + " Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n"
                + " and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)\n",
                testQuery.getSqlText());
        assertEquals(6, testQuery.getFields().getFieldsCount());
        for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
            Field fieldMtd = testQuery.getFields().get(i + 1);
            assertNotNull(fieldMtd);
            if (i == 0 || i == 5) {
                assertNotNull(fieldMtd.getDescription());
            } else {
                assertNull(fieldMtd.getDescription());
            }
        }
        assertEquals(4, testQuery.getParameters().getParametersCount());
    }

    @Test
    public void testCompilingWithSubqueriesBad() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("bad_schema", null, null, null);
        assertEquals("/**\n"
                + " * \n"
                + " * @author mg\n"
                + " * @name bad_schema\n"
                + " */\n"
                + "SELECT T0.ORDER_NO, 'Some text', TABLE1.ID, TABLE1.F1, TABLE1.F3, T0.AMOUNT FROM TABLE1, TABLE2,  (/**\n"
                + " * @name 128082898425059\n"
                + "*/\n"
                + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \n"
                + "From GOODORDER goodOrder\n"
                + " Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n"
                + " and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n"
                + " Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)\n",
                testQuery.getSqlText());
        assertEquals(6, testQuery.getFields().getFieldsCount());
        for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
            Field fieldMtd = testQuery.getFields().get(i + 1);
            assertNotNull(fieldMtd);
            if (i == 0 || i == 5) {
                assertNotNull(fieldMtd.getDescription());
            } else {
                assertNull(fieldMtd.getDescription());
            }
        }
        assertEquals(4, testQuery.getParameters().getParametersCount());
    }

    @Test
    public void testAsteriskMetadata() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("asterisk_schema", null, null, null);
        assertEquals(""
                + "/**\n"
                + " * \n"
                + " * @author mg\n"
                + " * @name asterisk_schema\n"
                + " */\n"
                + "SELECT * FROM TABLE1, TABLE2,  (/**\n"
                + " * @name 128082898425059\n"
                + "*/\n"
                + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \nFrom GOODORDER goodOrder\n Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)",
                testQuery.getSqlText());
        assertEquals(11, testQuery.getFields().getFieldsCount());
        for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
            Field fieldMtd = testQuery.getFields().get(i + 1);
            assertNotNull(fieldMtd);
        }
        assertEquals(4, testQuery.getParameters().getParametersCount());
    }

    @Test
    public void testBadSubquery() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("bad_subquery", null, null, null);
        assertEquals("/**\n"
                + " * \n"
                + " * @author mg\n"
                + " * @name bad_subquery\n"
                + " */\n"
                + "SELECT * FROM TABLE1, TABLE2, #_1_2_8082898425059 T0 WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)\n"
                + "", testQuery.getSqlText());
    }

    @Test
    public void testPartialTablesAsteriskMetadata() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("partial_asterisk_schema", null, null, null);
        assertEquals("/**\n"
                + " * \n"
                + " * @author mg\n"
                + " * @name partial_asterisk_schema\n"
                + " */\n"
                + "SELECT TABLE1.*, TABLE2.FiELdB FROM TABLE1, TABLE2,  (/**\n"
                + " * @name namedQuery4Tests\n"
                + "*/\n"
                + "Select goodOrder.ORDER_ID as ORDER_NO, goodOrder.AMOUNT, customers.CUSTOMER_NAME as CUSTOMER \n"
                + "From GOODORDER goodOrder\n"
                + " Inner Join CUSTOMER customers on (goodOrder.CUSTOMER = customers.CUSTOMER_ID)\n"
                + " and (goodOrder.AMOUNT > customers.CUSTOMER_NAME)\n"
                + " Where :P4 = goodOrder.GOOD)  T0  WHERE ((TABLE2.FIELDA<TABLE1.F1) AND (:P2=TABLE1.F3)) AND (:P3=T0.AMOUNT)\n",
                testQuery.getSqlText());
        assertEquals(5, testQuery.getFields().getFieldsCount());
        for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
            Field fieldMtd = testQuery.getFields().get(i + 1);
            assertNotNull(fieldMtd);
        }
        assertEquals(4, testQuery.getParameters().getParametersCount());
    }

    @Test
    public void testWithoutAliases_Schema_NonSchema_Schema_Columns() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("without_aliases_with_schema_without_schema_columns_from_single_table", null, null, null);
        assertEquals("/**\n"
                + " * \n"
                + " * @author mg\n"
                + " * @name without_aliases_with_schema_without_schema_columns_from_single_table\n"
                + " */\n"
                + "SELECT EAS.MTD_EntitiES.MDENt_ID, MTD_EntitiES.MDENT_NAME, EAS.MTD_EntitiES.MDENT_TYPe, MDENT_ORDER FROM EaS.MTD_EntitiES\n",
                testQuery.getSqlText());
        assertEquals(4, testQuery.getFields().getFieldsCount());
        for (int i = 0; i < testQuery.getFields().getFieldsCount(); i++) {
            Field fieldMtd = testQuery.getFields().get(i + 1);
            assertNotNull(fieldMtd);
        }
        assertEquals(0, testQuery.getParameters().getParametersCount());
    }

    @Test
    public void testPrimaryKey() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("primary_key", null, null, null);
        Fields schema = testQuery.getFields();
        assertNotNull(schema);
        assertTrue(schema.getFieldsCount() > 0);
        assertTrue(schema.get(1).isPk());
    }

    @Test
    public void testMultiplePrimaryKeys() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("multiple_primary_keys", null, null, null);
        Fields fields = testQuery.getFields();
        assertNotNull(fields);
        assertTrue(fields.getFieldsCount() == 2);
        assertTrue(fields.get(1).isPk());
        assertTrue(fields.get(2).isPk());
    }

    @Test
    public void testMultiplePrimaryKeysWithAsterisk() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("multiple_primary_keys_asterisk", null, null, null);
        Fields fields = testQuery.getFields();
        assertNotNull(fields);
        assertTrue(fields.getFieldsCount() == 23);
        assertNotNull(fields.get("MdENT_ID"));
        assertTrue(fields.get("MDENT_iD").isPk());
        assertNotNull(fields.get("MDlOG_ID"));
        assertTrue(fields.get("MDLOG_ID").isPk());
        assertFalse(fields.getPrimaryKeys().isEmpty());
        assertEquals(2, fields.getPrimaryKeys().size());
        assertEquals("mdent_id", fields.getPrimaryKeys().get(0).getName());
        assertEquals("mdlog_id", fields.getPrimaryKeys().get(1).getName());
    }

    @Test
    public void testGetQuery() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        SqlQuery testQuery = queriesProxy.getQuery("get_query", null, null, null);
        Fields metadata = testQuery.getFields();
        assertEquals(3, metadata.getFieldsCount());
    }

    @Test
    public void testGetEmptyQuery() throws Exception {
        LocalQueriesProxy queriesProxy = new LocalQueriesProxy(resource.getClient(), indexer);
        try {
            SqlQuery testQuery = queriesProxy.getQuery("empty_query", null, null, null);
            fail("Empty query must lead to an exception, but it doesn't. Why?");
        } catch (Exception ex) {
            //fine. there muist be an exception
        }
    }
}
