package nds.weather;

import java.util.Iterator;
import java.util.Properties;
/**
 * City infomation including code, name, pingyin
 */
public final class CityManager {
    private static CityManager instance=null;
    private Properties pc=new Properties();//key:pingyin, value:code
    private Properties pn=new Properties();//key:pingyin, value:name in chinese
    private CityManager() {
        for(int i=0;i<cityInfo.length;i+=3){
            pn.setProperty(cityInfo[i+1].toLowerCase() , cityInfo[i]);
            pc.setProperty(cityInfo[i+1].toLowerCase() , cityInfo[i+2]);
        }
    }
    public String getCityCode(String pingyin){
        return pc.getProperty(pingyin.toLowerCase() , pingyin);
    }
    public String getCityNameInChinese(String pingyin){
        return pn.getProperty(pingyin.toLowerCase() , pingyin);

    }
    public static CityManager getInstance(){
        if(instance==null) instance= new CityManager();
        return instance;
    }
    public void dumpAll(){
        for(Iterator it=pc.keySet().iterator();it.hasNext();){
            String key=(String) it.next();
            System.out.println(key+","+ pc.getProperty(key)+","+ pn.getProperty(key));
        }
    }
    public static void  main(String[] args){
        CityManager.getInstance().dumpAll();
    }
    private static String[] cityInfo={
            "����","BeiJing","CHXX0008",
            "�Ϻ�","ShangHai","CHXX0116",
            "����","GuangZhou","CHXX0037",
            "��˳","AnShun","CHXX0005",
            "����","BaoDing","CHXX0308",
            "��ɽ","BaoShan","CHXX0370",
            "��ɳ","ChangSha","CHXX0013",
            "����","ChangChun","CHXX0010",
            "����","ChangZhou","CHXX0015",
            "����","ZhongQing","CHXX0017",
            "�ɶ�","ChengDu","CHXX0016",
            "���","ChiFeng","CHXX0286",
            "����","DaLian","CHXX0019",
            "����","DaLi","CHXX0371",
            "��ͬ","DaTong","CHXX0251",
            "��ɽ","FoShan","CHXX0028",
            "��˳","FuShun","CHXX0029",
            "����","FuZhou","CHXX0031",
            "����","GaoXiong","TWXX0013",
            "����","GuiLin","CHXX0434",
            "����","GuiYang","CHXX0039",
            "������","HaErbin","CHXX0046",
            "����","HaiKou","CHXX0502",
            "����","HangZhou","CHXX0044",
            "�Ϸ�","HeFei","CHXX0448",
            "����","HuiZhou","CHXX0053",
            "����","JiLin","CHXX0063",
            "����","JiNan","CHXX0064",
            "�Ž�","JiuJiang","CHXX0068",
            "����","KaiFeng","CHXX0072",
            "����","KunMing","CHXX0076",
            "����","LaSa","CHXX0080",
            "����","LanZhou","CHXX0079",
            "����","LuoYang","CHXX0086",
            "����","LiuZhou","CHXX0479",
            "�ϲ�","NanChang","CHXX0097",
            "�Ͼ�","NanJing","CHXX0099",
            "����","NanNing","CHXX0100",
            "��ͨ","NanTong","CHXX0101",
            "����","MianYang","CHXX0351",
            "ĵ����","MuDanjiang","CHXX0278",
            "�ൺ","QingDao","CHXX0110",
            "Ȫ��","QuanZhou","CHXX0114",
            "����","ShaoXing","CHXX0117",
            "��ͷ","ShanTou","CHXX0493",
            "����","ShenYang","CHXX0119",
            "����","ShenZhen","CHXX0120",
            "ʯ��ׯ","ShiJiazhuang","CHXX0122",
            "̫ԭ","TaiYuan","CHXX0129",
            "̨��","TaiBei","TWXX0021",
            "̨��","TaiZhong","TWXX0019",
            "���","TianJin","CHXX0133",
            "����","WenZhou","CHXX0462",
            "��³ľ��","WuLumuqi","CHXX0135",
            "����","XiAn","CHXX0141",
            "����","XiNing","CHXX0236",
            "����","XiaMen","CHXX0140",
            "���","XiangGang","CHXX0049",
            "����","XianYang","CHXX0143",
            "����","XinXiang","CHXX0148",
            "����","XinZhu","TWXX0009",
            "����","XuZhou","CHXX0437",
            "�人","WuHan","CHXX0138",
            "����ɽ","WuYishan","CHXX0467",
            "�Ӽ�","YanJi","CHXX0291",
            "�˲�","YiChang","CHXX0407",
            "�˱�","YiBin","CHXX0362",
            "����","YiNing","CHXX0203",
            "����","YinChuan","CHXX0259",
            "����","YueYang","CHXX0411",
            "�żҿ�","ZhangJiakou","CHXX0300",
            "֣��","ZhengZhou","CHXX0165"
    };
}