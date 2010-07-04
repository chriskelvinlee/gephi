/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gephi.io.importer.plugin.file;

import java.io.File;
import java.io.FileReader;
import java.net.URL;
import java.util.List;
import org.gephi.data.attributes.api.AttributeType;
import org.gephi.data.attributes.api.AttributeValue;
import org.gephi.data.attributes.type.StringList;
import org.gephi.io.importer.api.Container;
import org.gephi.io.importer.api.ContainerFactory;
import org.gephi.io.importer.api.ContainerUnloader;
import org.gephi.io.importer.api.NodeDraftGetter;
import org.gephi.io.importer.api.Report;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;

/**
 *
 * @author Mathieu Bastian
 */
public class ImporterGEXF2Test {

    private ImporterGEXF2 importer;

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
        importer = new ImporterGEXF2();
        try {
            URL url = getClass().getResource("/org/gephi/io/importer/plugin/file/testparser.gexf");
            File file = new File(url.toURI());
            importer.setReader(new FileReader(file));
        } catch (Exception ex) {
            Exceptions.printStackTrace(ex);
        }
    }

    @After
    public void tearDown() {
        importer = null;
    }

    @Test
    public void testAttributeModel() {
        final Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
        container.setReport(new Report());
        importer.execute(container.getLoader());

        ContainerUnloader unloader = container.getUnloader();
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("0", AttributeType.STRING));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("1", AttributeType.FLOAT));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("2", AttributeType.LIST_STRING));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("3", AttributeType.FLOAT));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("4", AttributeType.FLOAT));

        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("type", AttributeType.STRING));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("Attribute 1", AttributeType.FLOAT));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("Attribute 2", AttributeType.LIST_STRING));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("Attribute 3", AttributeType.FLOAT));
        assertNotNull(unloader.getAttributeModel().getNodeTable().getColumn("Attribute 4", AttributeType.FLOAT));

        assertNotNull(unloader.getAttributeModel().getEdgeTable().getColumn("weight", AttributeType.FLOAT));
    }

    @Test
    public void testNode() {
        final Container container = Lookup.getDefault().lookup(ContainerFactory.class).newContainer();
        container.setReport(new Report());
        importer.execute(container.getLoader());

        NodeDraftGetter n1 = (NodeDraftGetter) container.getLoader().getNode("4c175b4b2c9016b90d1b4bc2");
        NodeDraftGetter n2 = (NodeDraftGetter) container.getLoader().getNode("4c175b542c9016b9ce1b4bc2");
        NodeDraftGetter n3 = (NodeDraftGetter) container.getLoader().getNode("4c175b4b2c9016b9071b4bc2");
        NodeDraftGetter n4 = (NodeDraftGetter) container.getLoader().getNode("4c175b4b2c9016b9101b4bc2");
        NodeDraftGetter n5 = (NodeDraftGetter) container.getLoader().getNode("4c175b4b2c9016b93a1b4bc2");
        NodeDraftGetter n6 = (NodeDraftGetter) container.getLoader().getNode("4c175b4a2c9016b9a51a4bc2");
        NodeDraftGetter n7 = (NodeDraftGetter) container.getLoader().getNode("4c175b4b2c9016b9ba1a4bc2");
        NodeDraftGetter n8 = (NodeDraftGetter) container.getLoader().getNode("4c175b4b2c9016b9ed1a4bc2");
        NodeDraftGetter n9 = (NodeDraftGetter) container.getLoader().getNode("4c175b4b2c9016b9f61a4bc2");

        assertNotNull(n1);
        assertNotNull(n2);
        assertNotNull(n3);
        assertNotNull(n4);
        assertNotNull(n5);
        assertNotNull(n6);
        assertNotNull(n7);
        assertNotNull(n8);
        assertNotNull(n9);

        assertEquals("Node 1", n1.getLabel());
        assertEquals("2000-01-01", n1.getSlices().get(0)[0]);
        assertEquals("2000-12-31", n1.getSlices().get(0)[1]);

        List<AttributeValue> values1 = n1.getAttributeValues();
        assertEquals("0", values1.get(0).getColumn().getId());
        assertEquals("3", values1.get(1).getColumn().getId());
        assertEquals("Author", values1.get(0).getValue());
        assertEquals(new Float(1), values1.get(1).getValue());

        List<AttributeValue> values2 = n2.getAttributeValues();
        assertEquals("0", values2.get(0).getColumn().getId());
        assertEquals("2", values2.get(1).getColumn().getId());
        assertEquals("Author", values2.get(0).getValue());
        assertEquals(new StringList("String1, String2, String 3"), values2.get(1).getValue());

        assertEquals("2000-01-01", n3.getSlices().get(0)[0]);
        assertEquals("2000-01-15", n3.getSlices().get(0)[1]);
        assertEquals("2001-01-30", n3.getSlices().get(1)[0]);
        assertEquals("2001-02-01", n3.getSlices().get(1)[1]);
        assertEquals(2, n3.getSlices().size());

        container.closeLoader();
        System.out.println(container.getReport().getText());
    }
}
