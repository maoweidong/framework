package nds.weather;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Properties;
import java.util.StringTokenizer;

import nds.net.ThreadProcess;
import nds.query.QueryEngine;
import nds.util.Configurations;
import nds.util.NDSException;

import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;
import org.mozilla.javascript.JavaScriptException;
import org.mozilla.javascript.Scriptable;

import com.meterware.httpunit.WebConversation;
import com.meterware.httpunit.WebResponse;

/*
* Fetch city's weather. Incoming properties should be like:
*
*
* city.nanjing=CX103
* city.shanghai=TDB023
*/
public class Fetcher extends ThreadProcess {
    private final static SimpleDateFormat dateTimeSecondsFormatter=new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
    private final static String INSERT="insert into weather (id, city,ccontext,ctemp,cicon,winds,windd,humid,real,uv,visb,lastup,"+
                                        "mintemp1,maxtemp1,icon1,mintemp2,maxtemp2,icon2,mintemp3,maxtemp3,icon3) values ("+
                                        "get_sequences('weather'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
    private Properties props;
    private WebConversation wc ;
    private String weburl;
    public Fetcher() {
    }
    /**
     * Run as deamon thread, so write to db
     * cities can be retrieved from property named "cities"
     */
    public void execute(){
        Connection conn=null;
        PreparedStatement stmt=null;
        try{

            conn= QueryEngine.getInstance().getConnection();
            stmt= conn.prepareStatement(INSERT);
/*            "insert into weather (id, city,context,ctemp,cicon,winds,windd,humid,real,uv,vis,lastup,"+
                                                    "mintemp1,maxtemp1,icon1,mintemp2,maxtemp2,icon2,mintemp3,maxtemp3,icon3) values ("+
                                        "get_sequences('weather'),?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
*/
            StringTokenizer st= new StringTokenizer( props.getProperty("cities"),",");
            while(st.hasMoreTokens() ){
                String city= st.nextToken();
                try{
                WeatherObject wo= fetch(city );
                int d;
                logger.debug("city:"+city +","+ wo);
                stmt.setString(1, CityManager.getInstance().getCityNameInChinese(city));
                stmt.setString(2, wo.getProperty("context"));
                setIntValue(stmt, 3, wo.getPropertyInt("ctemp", -999), -999);
                stmt.setString(4, wo.getProperty("cicon"));
                stmt.setString(5, wo.getProperty("winds"));
                stmt.setString(6, wo.getProperty("windd"));
                stmt.setString(7, wo.getProperty("humid"));
                stmt.setString(8, wo.getProperty("real"));

                setIntValue(stmt, 9, wo.getPropertyInt("uv", -999), -999);
                setIntValue(stmt, 10, wo.getPropertyInt("visb", -999), -999);

                stmt.setTimestamp(11, new Timestamp(wo.getProperyDate("lastup",new java.sql.Date(System.currentTimeMillis())).getTime()));
//                stmt.setDate(11, wo.getProperyDate("lastup"));

                setIntValue(stmt, 12, wo.getPropertyInt("mintemp1", -999), -999);
                setIntValue(stmt, 13, wo.getPropertyInt("maxtemp1", -999), -999);

                stmt.setString(14, wo.getProperty("icon1"));
                setIntValue(stmt, 15, wo.getPropertyInt("mintemp2", -999), -999);
                setIntValue(stmt, 16, wo.getPropertyInt("maxtemp2", -999), -999);
                stmt.setString(17, wo.getProperty("icon2"));
                setIntValue(stmt, 18, wo.getPropertyInt("mintemp3", -999), -999);
                setIntValue(stmt, 19, wo.getPropertyInt("maxtemp3", -999), -999);
                stmt.setString(20, wo.getProperty("icon3"));
                stmt.executeUpdate();
                }catch(Exception e){
                    logger.info("Error fetching data for city: "+ city+" ::"+e);
                }
            }


        }catch(Exception e){
            logger.debug("Error fetching data.", e);
        }finally{
            try{if( stmt !=null) stmt.close();}catch(Exception e2){}
            try{if( conn!=null)conn.close();}catch(Exception  ee){}
        }
    }
    private void setIntValue(PreparedStatement stmt, int pos, int value, int invalidValue) throws Exception{
        if (value== invalidValue)
            stmt.setNull(pos, java.sql.Types.NUMERIC);
        else stmt.setInt(pos, value);
    }
    /*
    * Fetch and return, does not write to db
    * @return null if not found
    */
    public WeatherObject fetch(String city){
        String cityCode= CityManager.getInstance().getCityCode(city);
        if (cityCode ==null) return null;

        try {
            logger.debug("City:"+ city+", URL="+ weburl+cityCode);
            WebResponse res= wc.getResponse(weburl+cityCode);
            if(res !=null){
                String content=  res.getText();
                return createWeatherObj(content);
            }else{
                logger.error("fail to get response for city:"+ city);
            }

        }catch (Exception ex) {
            logger.error("Fail to get weather for city:"+city, ex);
        }
        return null;

    }
    private WeatherObject createWeatherObj( String script)throws JavaScriptException, NDSException{
        Scriptable sc= createObject(script);
        if (sc.get("swCity", sc)==Scriptable.NOT_FOUND) {
            logger.debug("Could not parse script:"+ script);
            return null;
        }
        WeatherObject wo= new WeatherObject();
        wo.setProperty("city", CityManager.getInstance().getCityNameInChinese(""+sc.get("swCity", sc)));
//        wo.setProperty("subdiv",sc.get("swSubDiv", sc));
//        wo.setProperty("country",sc.get("swCountry", sc));
//        wo.setProperty("region", sc.get("swRegion", sc));
        wo.setProperty("ctemp",transUnit("temp",sc.get("swTemp", sc)));
        wo.setProperty("cicon",sc.get("swCIcon", sc));
        wo.setProperty("winds", changewind(sc.get("swWindS", sc)));
        wo.setProperty("windd",translateonly(sc.get("swWindD", sc)));
        wo.setProperty("humid",sc.get("swHumid", sc)+"%");
        wo.setProperty("real", sc.get("swReal", sc));
        wo.setProperty("uv", sc.get("swUV", sc));

        Object o=sc.get("swVis", sc);
        if ( o==null) wo.setProperty("visb","");
        else wo.setProperty("visb",""+transUnit("visb",o));

        wo.setProperty("lastup",getLastUp(sc.get("swLastUp", sc)));
        wo.setProperty("context",translateonly(sc.get("swConText", sc)));

        String fore=(String)sc.get("swFore", sc);
        ArrayList al=new ArrayList();
        if (fore!=null && fore.length() >0) {
            StringTokenizer st= new StringTokenizer(fore,"|");
            while(st.hasMoreTokens() ){
                al.add(st.nextToken() );
            }
        }
        wo.setProperty("maxtemp1", transUnit("temp",al.get(20)));
        wo.setProperty("maxtemp2", transUnit("temp",al.get(21)));
        wo.setProperty("maxtemp3", transUnit("temp",al.get(22)));
        wo.setProperty("mintemp1", transUnit("temp",al.get(40)));
        wo.setProperty("mintemp2", transUnit("temp",al.get(41)));
        wo.setProperty("mintemp3", transUnit("temp",al.get(42)));
        wo.setProperty("icon1", al.get(10));
        wo.setProperty("icon2", al.get(11));
        wo.setProperty("icon3", al.get(12));
        return wo;
    }
    private String getLastUp(Object o){
        Calendar c=Calendar.getInstance();
        if (o==null || "".equals(o)){
            java.util.Date d= new java.util.Date();
            c.setTime(d);
            c.set(c.HOUR,0);
            c.set(c.MINUTE,0);
            c.set(c.SECOND, 0);
            c.set(c.MILLISECOND,0);
            return dateTimeSecondsFormatter.format(c.getTime());
        }
        try {

            c.setTime(dateTimeSecondsFormatter.parse(""+o));
            c.add(c.HOUR, 13); // add 13 hours
            return dateTimeSecondsFormatter.format(c.getTime());
        }
        catch (ParseException ex) {
            logger.error("Error parsing "+ o + " to datetime format", ex);
            return "";
        }

    }
    private Scriptable createObject(String script)throws JavaScriptException, NDSException{
        Context cx = Context.enter();
        try {
            // Initialize the standard objects (Object, Function, etc.)
            // This must be done before scripts can be executed. Returns
            // a scope object that we use in later calls.
            Scriptable scope = cx.initStandardObjects(null);
            // Now evaluate the string we've colected.
            cx.evaluateString(scope, script, "<cmd>", 1, null);
            Object fObj = scope.get("makeWeatherObj", scope);
            if (!(fObj instanceof Function)) {
                logger.error("Could not found weather information");
                throw new NDSException("Fail to retrieve weather information from web");
            } else {
                Object functionArgs[] = { };
                Function f = (Function)fObj;
                Scriptable result = f.construct(cx, scope,  functionArgs);
                return result;
            }
        } finally {
            // Exit from the context.
            Context.exit();
        }
    }
    public void init(Properties props){
        this.props=props;
        Configurations conf= new Configurations(props);
        weburl= props.getProperty("weburl", "http://www.msnbc.com/m/chnk/d/weather_d_src.asp?acid=");
        wc= new WebConversation();
    }

    private String transUnit(String name,Object o) {
        if (o==null|| o==Scriptable.NOT_FOUND|| o.equals("")) return "";
        try{
        float value= Float.parseFloat(""+o);
        double returnvalue=value;
        if (name.equals("temp")) {
                returnvalue = Math.round((5.0/9.0)*(value-32));
        }else if( name.equals("wind")){
                returnvalue = value*1.609334;
        }else if( name.equals("pres")){
                returnvalue = value*3386.389;
        }else if( name.equals("visb")){
                returnvalue = value*1.609334;
        }
        return ""+ Math.round(returnvalue);
        }catch(Exception e){
            logger.debug("error transUnit:"+ name +", "+ o+":"+ e);
            return "";
        }
    }

    private String translateonly(Object WTwords){
        if (WTwords==null || WTwords==Scriptable.NOT_FOUND) return "";
        String w= nds.util.StringUtils.replace(""+WTwords," ","");
        return dictionary.getProperty(w,""+WTwords);
    }

    private String changewind(Object swWind)
    {
       if ( swWind ==null || swWind== Scriptable.NOT_FOUND|| "".equals(swWind)) return "";
       double swind=Long.parseLong(transUnit("wind",swWind))*1000.0/3600.0;
       if(swind<=0.3){
           return "�޷�";
       }
       if(swind>0.3&&swind<=1.6){
           return "һ��";
       }
       if(swind>1.6&&swind<=3.4){
           return "����";
       }
       if(swind>3.4&&swind<=5.5){
           return "����";
       }
       if(swind>5.5&&swind<=8.0){
           return "�ļ�";
       }
       if(swind>8.0&&swind<=10.8){
           return "�弶";
       }
       if(swind>10.8&&swind<=13.9){
           return "����";
       }
       if(swind>13.9&&swind<=17.2){
           return "�߼�";
       }
       if(swind>17.2&&swind<=20.8){
           return "�˼�";
       }
       if(swind>20.8&&swind<=24.5){
          return "�ż�";
       }
       if(swind>24.5&&swind<=28.5){
          return "ʮ��";
       }
       if(swind>28.5&&swind<=32.7){
           return "ʮһ��";
       }
       if(swind>32.7){
           return "ʮ����";
        }
        return ""+swind;
    }
    private static Properties dictionary;
    static{
        dictionary=new Properties();

        dictionary.setProperty("E", "����");
        dictionary.setProperty("S", "�Ϸ�");
        dictionary.setProperty("N", "����");
        dictionary.setProperty("W", "����");
        dictionary.setProperty("NE", "����");
        dictionary.setProperty("SW", "����");
        dictionary.setProperty("SE", "����");
        dictionary.setProperty("NW", "����");
        dictionary.setProperty("N", "����");
        dictionary.setProperty("W", "����");
        dictionary.setProperty("NE", "����");
        dictionary.setProperty("SW", "����");
        dictionary.setProperty("SE", "����");
        dictionary.setProperty("NW", "����");
        dictionary.setProperty("WNW", "��ת����");
        dictionary.setProperty("NNE", "��ת����");

        dictionary.setProperty("ESE", "��ת����");
        dictionary.setProperty("NNW", "��ת����");
        dictionary.setProperty("SSW", "��ת����");
        dictionary.setProperty("WSW", "��ת����");
        dictionary.setProperty("ENE", "��ת����");
        dictionary.setProperty("SSE", "��ת����");
        dictionary.setProperty("VAR", "�仯");
        dictionary.setProperty("Fog", "����");

        dictionary.setProperty("LightSnow", "Сѩ");
        dictionary.setProperty("LightRain", "С��");
        dictionary.setProperty("PartlyCloudy", "�������");
        dictionary.setProperty("Cloudy", "����");
        dictionary.setProperty("Haze", "����");
        dictionary.setProperty("MostlyCloudy", "����");
        dictionary.setProperty("Sunny", "��");
        dictionary.setProperty("MostlySunny", "�󲿷�����");
        dictionary.setProperty("Fair", "һ��");
        dictionary.setProperty("CALM", "ƽ��");
        dictionary.setProperty("Clear", "��");
        dictionary.setProperty("Mist", "����");
        dictionary.setProperty("RainShower", "����");
        dictionary.setProperty("WT_Tempc", "����");
        dictionary.setProperty("WT_Tempf", "����");
        dictionary.setProperty("WT_Locimg", "����");
        dictionary.setProperty("WT_Remoteimg", "Զ��(http)");
        dictionary.setProperty("WT_Visbkm", "����");
        dictionary.setProperty("WT_Visbm", "Ӣ��");
        dictionary.setProperty("WT_Prespa", "��˹��");
        dictionary.setProperty("WT_Wday", "����");
        dictionary.setProperty("WT_Wnight", "ҹ��");
        dictionary.setProperty("WT_Precip", "��ˮ����");
        dictionary.setProperty("WT_Monday", "��һ");
        dictionary.setProperty("WT_Presinches", "Ӣ��ˮ����");
        dictionary.setProperty("WT_Windkmph", "����/Сʱ");
        dictionary.setProperty("WT_Windmph", "Ӣ��/Сʱ");
        dictionary.setProperty("WT_Tuesday", "�ܶ�");

        dictionary.setProperty("WT_Wednesday", "����");
        dictionary.setProperty("WT_Thursday", "����");
        dictionary.setProperty("WT_Friday", "����");
        dictionary.setProperty("WT_Saturday", "����");


        dictionary.setProperty("WT_Precipabbre", "��ˮ");
        dictionary.setProperty("WT_Sunday", "����");
        dictionary.setProperty("WT_Thighabbre", "���");
        dictionary.setProperty("WT_Tlowabbre", "���");



    }


}
