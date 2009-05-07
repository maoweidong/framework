package nds.control.web.test.distribution;

import java.io.IOException;

import junit.framework.Test;
import junit.framework.TestSuite;
import nds.control.web.MainServlet;
import nds.log.Logger;
import nds.log.LoggerManager;

import org.apache.cactus.ServletTestCase;
import org.apache.cactus.WebRequest;



public class DisRequestShtItemTest extends ServletTestCase
{
    private static Logger logger= LoggerManager.getInstance().getLogger(DisRequestShtItemTest.class.getName());
    private String id;
    private MainServlet servlet=null;

    public DisRequestShtItemTest(String name) {
        super(name);
     //   id= "110001";// the id of user to be tested on
    }
    public static Test suite() {
        TestSuite suite= new TestSuite("DisRequestShtItemTest");
        suite.addTest(new DisRequestShtItemTest("testCreate"));
        suite.addTest(new DisRequestShtItemTest("testModify"));
        suite.addTest(new DisRequestShtItemTest("testDelete"));
        System.out.println("suite in DisRequestShtItemTest called");
        return suite;
    }

    public void beginModify(WebRequest req)
    {
        // Set up HTTP related parameters
        req.setURL("172.16.0.5:7001", "/nds", "/control","/command", null);
        req.addParameter("command", "DisRequestShtItemModify");
        //req.addParameter("id", id);
        req.addParameter("id", "10100");
        req.addParameter("productNo" , "1");
        req.addParameter("requestDisQty" , "21");
        req.addParameter("realUp" ,"13");
        req.addParameter("note" , "sjjjjjjjjs1");

        req.addParameter("id", "10101");
        req.addParameter("productNo" , "2");
        req.addParameter("requestDisQty" , "12");
        req.addParameter("realUp" ,"1");
        req.addParameter("note" , "33ddddddfff2231");

        req.addParameter("id", "10102");
        req.addParameter("productNo" , "3");
        req.addParameter("requestDisQty" , "15");
        req.addParameter("realUp" ,"11");
        req.addParameter("note" , "vvvvvvvvvvv");



    }
    public void beginCreate(WebRequest req)
    {
        // Set up HTTP related parameters
        req.setURL("172.16.0.5:7001", "/nds", "/control",
            "/command", null);
        req.addParameter("command", "DisRequestShtItemCreate");
        // req.addParameter("id", id);
        req.addParameter("objectid" , "1");
        req.addParameter("id", "10100");
        req.addParameter("productNo" , "1");
        req.addParameter("requestDisQty" , "21");
        req.addParameter("realUp" ,"13");
        req.addParameter("note" , "sjjjjjjjjs1");

        req.addParameter("id", "10101");
        req.addParameter("productNo" , "2");
        req.addParameter("requestDisQty" , "12");
        req.addParameter("realUp" ,"1");
        req.addParameter("note" , "33ddddddfff2231");

        req.addParameter("id", "10102");
        req.addParameter("productNo" , "3");
        req.addParameter("requestDisQty" , "15");
        req.addParameter("realUp" ,"11");
        req.addParameter("note" , "1hhhssssas");




    }
    public void beginDelete(WebRequest req)
    {
        // Set up HTTP related parameters
        req.setURL("172.16.0.5:7001", "/nds", "/control",
            "/command", null);
        req.addParameter("command", "DisRequestShtItemDelete");
        //req.addParameter("id", id);

        req.addParameter("id", "10100");
        req.addParameter("productNo" , "1");
        req.addParameter("requestDisQty" , "21");
        req.addParameter("realUp" ,"13");
        req.addParameter("note" , "sjjjjjjjjs1");

        req.addParameter("id", "10101");
        req.addParameter("productNo" , "2");
        req.addParameter("requestDisQty" , "12");
        req.addParameter("realUp" ,"1");
        req.addParameter("note" , "33ddddddfff2231");

        req.addParameter("id", "10102");
        req.addParameter("productNo" , "3");
        req.addParameter("requestDisQty" , "15");
        req.addParameter("realUp" ,"11");
        req.addParameter("note" , "1hhhssssas");

    }

    public void testCreate()   throws Exception{
        System.out.println("testCreate in DisRequestShtItemTest called");
        askServlet();
    }
    public void endCreate(com.meterware.httpunit.WebResponse res) throws IOException, org.xml.sax.SAXException
    {
        checkSuccess(res);
    }
    public void testModify() throws Exception{
        System.out.println("testModify in DisRequestShtItemTest called");
        askServlet();

    }
    public void endModify(com.meterware.httpunit.WebResponse res) throws IOException, org.xml.sax.SAXException
    {
        checkSuccess(res);
    }
    public void testDelete() throws Exception{
        System.out.println("testDelete in DisRequestShtItemTest called");
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
