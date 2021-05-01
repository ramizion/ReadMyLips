
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.font.TextAttribute;
import java.awt.image.BufferedImage;
import java.text.AttributedString;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.TimeUnit;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

import java.awt.FontMetrics;

import com.sun.glass.ui.Size;
import com.xuggle.mediatool.IMediaWriter;
import com.xuggle.mediatool.ToolFactory;
import com.xuggle.xuggler.ICodec;

public class Image2Video {

	private static final double FRAME_RATE = 35;
	private String outputFilePath;
	private ArrayList<Image[]> frames;
	private final static String videoName = "video.mp4";
	private String text;
	private HashMap<String, Image[]> hashMapPhonFrames;
	private Character[] phonems;
	private Size imageSize;
	private Point[] phonemsRange;
	
	Image2Video(ArrayList<Image[]> frames, String text, String outputFilePath){

		this.outputFilePath=outputFilePath;
		this.frames =frames;
		this.text = text;
		makeVideo();
	}

	/**
	 * Constructor
	 * @param hashMapPhonFrames
	 * @param phonems2
	 * @param text
	 * @param outputFilePath
	 */
	Image2Video(HashMap<String, Image[]> hashMapPhonFrames,
			Character[] phonems2, String text, String outputFilePath) {
		this.outputFilePath=outputFilePath;
		this.hashMapPhonFrames =hashMapPhonFrames;
		this.text = text;
		this.phonems=phonems2;
		
		Image randFrame= hashMapPhonFrames.get("s|s")[0];
		imageSize.height=(int) randFrame.getHeight();
		imageSize.width=(int) randFrame.getWidth();
		makeVideo2();
	}

	/**
	 * Constructor
	 * @param hashMapPhonFrames
	 * @param phonemsRange
	 * @param phonems2
	 * @param text
	 * @param outputFilePath
	 */
	Image2Video(HashMap<String, Image[]> hashMapPhonFrames,Point[] phonemsRange,
			Character[] phonems2, String text, String outputFilePath) {
		
		this.outputFilePath=outputFilePath;
		this.hashMapPhonFrames =hashMapPhonFrames;
		this.text = text;
		this.phonems=phonems2;
		this.phonemsRange = phonemsRange;
		
		Image randFrame= hashMapPhonFrames.get("s|s")[0];
		imageSize= new Size();
		imageSize.height=(int) randFrame.getHeight();
		imageSize.width=(int) randFrame.getWidth();
		makeVideo2();
	}

	/**
	 * Make a video to the frames
	 * Include subtitle
	 */
	public void makeVideo2(){

		long startTime;

		int imageWidth = (int) (imageSize.width%2==0? 
				(imageSize.width):(imageSize.width-1));
		int imageHeight = (int) (imageSize.height%2==0? 
				(imageSize.height):(imageSize.height-1)); //

		// Create the writer
		final IMediaWriter writer = ToolFactory.makeWriter(outputFilePath+"\\"+videoName);

		// Add video stream
		ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
		writer.addVideoStream(0, 0, videoCodec, imageWidth, imageHeight);
		
		int s=0;
		startTime = System.nanoTime();
		for (int i = 0; i < phonems.length-1; i++) {
			Image[] frames = hashMapPhonFrames.get(phonems[i]+"|"+phonems[i+1]);

			for (int k=0; k<frames.length;k++) {
				s++;
				BufferedImage frameBuff;
				frameBuff = new BufferedImage(imageSize.width,imageSize.height,
						BufferedImage.TYPE_3BYTE_BGR);
				frameBuff.getGraphics().drawImage(SwingFXUtils.fromFXImage(frames[k], null), 0, 0, null);

				Graphics g = frameBuff.getGraphics();
				getSubTitle(g, phonemsRange[i]); // להעביר לפני הלולאה הזו
				long time = System.nanoTime() - startTime;
				writer.encodeVideo(0, frameBuff, time, 
						TimeUnit.NANOSECONDS);
				 
				// Sleep frame rate (milliseconds)
				try {
					Thread.sleep((long) (1000/FRAME_RATE));
				} 
				catch (InterruptedException e) {
				}
			}				
		}
		writer.close();
		System.out.println(s);
	}

	public void getSubTitle(Graphics grap, Point range){
		Font font;
		if(text.length()>22)
			font = new Font("Arial", Font.BOLD, 10);			    	
		else
			font = new Font("Arial", Font.BOLD, 48);
		grap.setFont(font);

		java.awt.FontMetrics fm = grap.getFontMetrics();
		int x = ((imageSize.width - fm.stringWidth(text)) / 2);
		int y = (imageSize.height-50-fm.getHeight()+fm.getAscent());
		
		grap.setColor(new Color(0,0, 0, 50));
		grap.fillRect(0, y-49, imageSize.width , 5+56);

		grap.setColor(Color.BLACK);
		AttributedString attribute = new AttributedString(text);

		attribute.addAttribute(TextAttribute.FONT, font, 0, text.length());
		if((int)range.getY()-(int)range.getX()>0){
			attribute.addAttribute(TextAttribute.FOREGROUND, Color.RED,(int)range.getX(),(int)range.getY()+1);
		}
		grap.drawString(attribute.getIterator(), x, y);
		grap.dispose();
	}

	public void makeVideo(){

		long startTime = System.nanoTime();

		// Parameters:
		Image firstImage;
		firstImage= frames.get(0)[0];
		// FRAME_RATE
		int imageWidth = (int) (firstImage.getWidth()%2==0? (firstImage.getWidth()):(firstImage.getWidth()-1));
		int imageHeight = (int) (firstImage.getHeight()%2==0? (firstImage.getHeight()):(firstImage.getHeight()-1)); //

		// Create the writer
		final IMediaWriter writer = ToolFactory.makeWriter(outputFilePath+"\\"+videoName);

		// Add video stream
		ICodec videoCodec = ICodec.findEncodingCodec(ICodec.ID.CODEC_ID_H264);
		writer.addVideoStream(0, 0, videoCodec, imageWidth, imageHeight);

		for (int i = 0; i < frames.size(); i++) {
			System.out.println(i);
			System.out.println(frames.get(i).length);
			int k=0;
			for (Image imageFrame : frames.get(i)) {
				BufferedImage frameBuff;
				System.out.println(k++);
				frameBuff = new BufferedImage((int)firstImage.getWidth(),
						(int)firstImage.getHeight(), BufferedImage.TYPE_3BYTE_BGR);
				frameBuff.getGraphics().drawImage(SwingFXUtils.fromFXImage(imageFrame, null), 0, 0, null);

				Graphics g = frameBuff.getGraphics();

				Font font;
				if(text.length()>22)
					font = new Font("Arial", Font.BOLD, 24);			    	
				else
					font = new Font("Arial", Font.BOLD, 48);
				//			    g.drawLine(imageWidth / 2, 0, imageWidth / 2, imageHeight);
				//			    g.drawLine(0, imageHeight / 2, imageWidth, imageHeight / 2);
				//			    g.setFont(font);


				//			    FontMetrics fm = g.getFontMetrics();
				//	            int x = ((imageWidth - fm.stringWidth(text)) / 2);
				//	            int y = (imageHeight-50-fm.getHeight()+fm.getAscent());
				//	            
				//	            g.fillRect(0, y-49, imageWidth, 5+56);
				//	            g.setColor(Color.BLACK);
				//	            
				//	            AttributedString a = new AttributedString(text);
				//	            a.addAttribute(TextAttribute.FOREGROUND, Color.RED, (i*2), (i*2)+1);
				//	            a.addAttribute(TextAttribute.FONT, font, 0, text.length());
				//	            g.setFont(font);
				//	            g.drawString(a.getIterator(), x, y);
				//	            g.setColor(new Color(245, 245, 245));
				//			    g.dispose();

				long time = System.nanoTime() - startTime;
				writer.encodeVideo(0, frameBuff, time, 
						TimeUnit.NANOSECONDS);

				try {
					Thread.sleep((long) (1000 / FRAME_RATE));
				} 
				catch (InterruptedException e) {
				}
			}				
		}
		writer.close();
	}

}