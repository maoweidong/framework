package nds.control.web.test.distribution;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import nds.control.web.MainServlet;
import nds.log.Logger;
import nds.log.LoggerManager;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;



public class DisShipShtItemTest extends ServletTestCase
{
    private static Logger logger= LoggerManager.getInstance().getLogger(DisShipShtItemTest.class.getName());
    private String id;
    private MainServlet servlet=null;
    public DisShipShtItemTest(String name) {
        super(name);
      //  id= "100004";// the id of user to be tested on
    }
    public static Test suite() {
        TestSuite suite= new TestSuite("DisShipShtItemTest");
        suite.addTest(new DisShipShtItemTest("testCreate"));
        suite.addTest(new DisShipShtItemTest("testModify"));
        suite.addTest(new DisShipShtItemTest("testDelete"));
        System.out.println("suite in DisShipShtItemTest called");
        return suite;
    }

    public void beginModify(WebRequest req)
    {
        // Set up HTTP related parameters
        req.setURL("172.16.0.5:7001", "/nds", "/control","/command", null);
        req.addParameter("command", "DisShipShtItemModify");
        //req.addParameter("id", id);

        req.addParameter("id" , "109911");
        req.addParameter("disShipShtId" , "10175");
        req.addParameter("markNo" , "1");
        req.addParameter("productNo" , "3");
        req.addParameter("disQty" , "133");
        req.addParameter("shipQty" , "155");
        req.addParameter("ShipStockId" , "1");
        req.addParameter("realUp" , "177");
        req.addParameter("note" , "1kkk");

    }

    public void beginCreate(WebRequest req)
    {
        // Set up HTTP related parameters
        req.setURL("172.16.0.5:7001", "/nds", "/control",
            "/command", null);
        req.addParameter("command", "DisShipShtItemCreate");
        //req.addParameter("id", id);
        req.addParameter("id" , "109911");

        req.addParameter("disShipShtId" , "10175");
        req.addParameter("markNo" , "1");
        req.addParameter("productNo" , "1");
        req.addParameter("disQty" , "88");
        req.addParameter("shipQty" , "77");
        req.addParameter("ShipStockId" , "1");
        req.addParameter("realUp" , "22");
        req.addParameter("note" , "1");





    }

    public void beginDelete(WebRequest req)
    {
        // Set up HTTP related parameters
        req.setURL("172.16.0.5:7001", "/nds", "/control",
            "/command", null);
        req.addParameter("command", "DisShipShtItemDelete");
        //req.addParameter("id", id);
        req.addParameter("id" , "109911");

        req.addParameter("disShipShtId" , "10175");
        req.addParameter("markNo" , "1");
        req.addParameter("productNo" , "1");
        req.addParameter("disQty" , "1");
        req.addParameter("shipQty" , "1");
        req.addParameter("ShipStockId" , "1");
        req.addParameter("realUp" , "1");
        req.addParameter("note" , "1");


    }


    public void testCreate()   throws Exception{
        System.out.println("testCreate in DisShipShtItemTest called");
        askServlet();
    }
    public void endCreate(com.meterware.httpunit.WebResponse res) throws IOException, org.xml.sax.SAXException
    {
        checkSuccess(res);
    }
    public void testModify() throws Exception{
        System.out.println("testModify in DisShipShtItemTest called");
        askServlet();

    }


    public void endModify(com.meterware.httpunit.WebResponse res) throws IOException, org.xml.sax.SAXException
    {
        checkSuccess(res);
    }

    public void testDelete() throws Exception{
        System.out.println("testDelete in DisShipShtItemTest called");
        askServlet();

    }

    public void endDelete(com.meterware.httpunit.WebResponse res) throws IOException, org.xml.sax.SAXException
    {
        checkSuccess(res);
    }


    public void askServlet() throws Exception{
    // Initialize class to test
        if(servlet ==null){
        servlet = new MainServlet();
        // VERY IMPORTANT : Call the init() method in order to initialize the
        //                  Servlet ServletConfig object.
        servlet.init(config);
        servlet.init();
        }
        servlet.doGet(this.request, this.response);

    }

    public void checkSuccess(com.meterware.httpunit.WebResponse res) throws IOException, org.xml.sax.SAXException{
        String content= res.getText();
        assertTrue(content, res.isHTML());
            String name=res.getTitle();
            boolean b=  "��Ϣ".equals(name);
            if( !b) logger.error(content);
            assertEquals("��Ϣ", name);

    }

}