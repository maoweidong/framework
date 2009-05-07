package nds.util;

import javax.imageio.ImageIO;
import javax.imageio.IIOException;
import java.awt.image.BufferedImage;
import java.awt.Image;
import java.io.File;
import java.awt.image.AffineTransformOp;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.awt.*;
import java.io.InputStream;
import java.io.File;
import java.io.FileOutputStream;
/**
 * Common method handling image
 * @author yfzhu
 *
 */
public class ImageUtils {
	
	
		 
		 
	public static void main (String argv[]) throws Exception {
	 //����1(from),����2(to),����3(��),����4(��)
		 createThumbnail("c:/image2.jpg","c:/imgTest3.jpg",49,64);
		 createThumbnails("c:/image2.jpg","d:/tmp","134.jpg",new int[]{140,49,34});
	}
	/**
	 * ��ָ��Ŀ¼�´������ɸ��ߴ������ͼ, �ļ�����Ϊ ԭͼƬ����"_1.jpg",  ԭͼƬ����"_2.jpg", ��������
	 * @param fromFileStr ԭͼ·������֧��jpg �� png ���ָ�ʽ
	 * @param destFolder ����ͼ����Ŀ¼����󲻺��ָ��������� c:\pdt
	 * @param fileName ����ͼ��ͳһ�ļ���ģ�壬 ���ɵ�����ͼ������ <�ļ���>+"_?."+ <�ļ�����׺> ����
	 * @param widths�� ��ͬ�Ŀ��, �߶Ƚ���ԭͼ�߿���Զ�����
	 */
	public static void createThumbnails(String fromFileStr,String destFolder, String fileName, int[] widths) throws Exception{
		 BufferedImage srcImage;
		 String imgType = "JPEG";
		 if (fromFileStr.toLowerCase().endsWith(".png")) {
			 imgType = "PNG";
		 }
		 File fromFile=new File(fromFileStr);
		 srcImage = ImageIO.read(fromFile);
		 int origWidth= srcImage.getWidth();
		 int origHeight= srcImage.getHeight();
		 String origFileName= fileName;
		 int idx= origFileName.lastIndexOf('.');
		 
		 String ps= origFileName.substring(0,idx);
		 String pse=origFileName.substring(idx);
		 for(int i=0;i< widths.length;i++){
			 double sx = (double) widths[i] / origWidth;
			 if(sx>1)throw new java.lang.IllegalArgumentException("Found thumbnail width ("+widths[i]+") greater than original image width "+origWidth);
			 int height= (int)(origHeight * sx);
			 String saveToFileStr=destFolder+ File.separator+ ps+"_"+ (i+1)+ pse;
			 File saveFile=new File(saveToFileStr);
			 BufferedImage desImage=getScaledInstance(srcImage, widths[i], height,
					 RenderingHints.VALUE_INTERPOLATION_BILINEAR,false,true);//VALUE_INTERPOLATION_BICUBIC
			 ImageIO.write(desImage, imgType, saveFile);
		 }
		 
		
	}
	/**
	 * ��������ͼ��Ŀǰԭͼ������ͼ��֧�� jpg �� png ���ָ�ʽ������������ʽ����һ��
	 * @param fromFileStr ԭͼƬ·���������� jgp �� png ��ʽ
	 * @param saveToFileStr ����ͼȫ·��
	 * @param width ����ͼ����ȣ����ճߴ����С�ڴ˴�С�������߿�ȣ�
	 * @param hight ����ͼ���߶ȣ����ճߴ����С�ڴ˴�С�������߿�ȣ�
	 * @throws Exception
	 */ 
	public static void createThumbnail (String fromFileStr,String saveToFileStr,int width,int hight)
		 						throws Exception {
		 BufferedImage srcImage;
		 String imgType = "JPEG";
		 if (fromFileStr.toLowerCase().endsWith(".png")) {
			 imgType = "PNG";
		 }
		 File saveFile=new File(saveToFileStr);
		 File fromFile=new File(fromFileStr);
		 srcImage = ImageIO.read(fromFile);
		 srcImage = getScaledInstance(srcImage, width, hight,
					 RenderingHints.VALUE_INTERPOLATION_BILINEAR,true,true); // VALUE_INTERPOLATION_BICUBIC
		 ImageIO.write(srcImage, imgType, saveFile);
	}
	 /**
	     * Convenience method that returns a scaled instance of the
	     * provided {@code BufferedImage}.
	     *
	     * @param img the original image to be scaled
	     * @param targetWidth the desired width of the scaled instance,
	     *    in pixels
	     * @param targetHeight the desired height of the scaled instance,
	     *    in pixels
	     * @param hint one of the rendering hints that corresponds to
	     *    {@code RenderingHints.KEY_INTERPOLATION} (e.g.
	     *    {@code RenderingHints.VALUE_INTERPOLATION_NEAREST_NEIGHBOR},
	     *    {@code RenderingHints.VALUE_INTERPOLATION_BILINEAR},
	     *    {@code RenderingHints.VALUE_INTERPOLATION_BICUBIC})
	     * @param fixedWHScale if true, will fixed scale rate on width and height, 
	     * @param higherQuality if true, this method will use a multi-step
	     *    scaling technique that provides higher quality than the usual
	     *    one-step technique (only useful in downscaling cases, where
	     *    {@code targetWidth} or {@code targetHeight} is
	     *    smaller than the original dimensions, and generally only when
	     *    the {@code BILINEAR} hint is specified)
	     * @return a scaled version of the original {@code BufferedImage}
	     */
	    public static BufferedImage getScaledInstance(BufferedImage img,
	                                           int targetWidth,
	                                           int targetHeight,
	                                           Object hint, boolean fixedWHScale,
	                                           boolean higherQuality)
	    {
	        int type = (img.getTransparency() == Transparency.OPAQUE) ?
	            BufferedImage.TYPE_INT_RGB : BufferedImage.TYPE_INT_ARGB;
	        BufferedImage ret = (BufferedImage)img;
			if(fixedWHScale){ 
				double sx = (double) targetWidth / img.getWidth();
				double sy = (double) targetHeight / img.getHeight();
				 //������ʵ����targetW��targetH��Χ��ʵ�ֵȱ�����, ȡ���ű���С���Ǹ��������ø�С��
				 if(sx>sy){
					 sx = sy;
					 targetWidth = (int)(sx * img.getWidth());
				 }else{
					 sy = sx;
					 targetHeight = (int)(sy * img.getHeight());
				 }
			}
	        int w, h;
	        if (higherQuality) {
	            // Use multi-step technique: start with original size, then
	            // scale down in multiple passes with drawImage()
	            // until the target size is reached
	            w = img.getWidth();
	            h = img.getHeight();
	        } else {
	            // Use one-step technique: scale directly from original
	            // size to target size with a single drawImage() call
	            w = targetWidth;
	            h = targetHeight;
	        }
	        
	        do {
	            if (higherQuality && w > targetWidth) {
	                w /= 2;
	                if (w < targetWidth) {
	                    w = targetWidth;
	                }
	            }

	            if (higherQuality && h > targetHeight) {
	                h /= 2;
	                if (h < targetHeight) {
	                    h = targetHeight;
	                }
	            }

	            BufferedImage tmp = new BufferedImage(w, h, type);
	            Graphics2D g2 = tmp.createGraphics();
	            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, hint);
	            g2.drawImage(ret, 0, 0, w, h, null);
	            g2.dispose();

	            ret = tmp;
	        } while (w != targetWidth || h != targetHeight);

	        return ret;
	    }

		 

}
