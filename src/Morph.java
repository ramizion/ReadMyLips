import java.awt.Point;
import java.io.IOException;
import java.nio.IntBuffer;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.image.WritablePixelFormat;

public class Morph{

	final static private int triangleAmount=Data.getTriangleamount();
	final static private int framecnt=Data.getFramesForMorph();
	private int[] pixelSrcImage, pixelDstImage;
	private int imageHeight, imageWidth;
	private TriangleMap srcMap;
	private TriangleMap dstMap;
	private Image frames[];
	private Image imageSrc;
	private Image imageDst;
	private int time;

	/**
	 * @param String: imagesrcfile 	- Link to image of the viseme source 
	 * @param String: imagedstfile 	- Link to image of the viseme destination 
	 * @param int: time				- Time between to viseme (milliseconds)
	 * @param TriangleMap: srcMap	- Map of viseme source
	 * @param TriangleMap: dstMap	- Map of viseme destination
	 */
	Morph (String imagesrcfile, String imagedstfile, int time
			,TriangleMap srcMap, TriangleMap dstMap){
		this(new Image(imagesrcfile), new Image(imagedstfile), time, srcMap,  dstMap);
	}

	/**
	 * @param Image: imageSrc 		- Image of the viseme source 
	 * @param Image: imageDst 		- Image of the viseme destination 
	 * @param int: time				- Time between to viseme (milliseconds)
	 * @param TriangleMap: srcMap	- Map of viseme source
	 * @param TriangleMap: dstMap	- Map of viseme destination
	 */
	Morph (Image imageSrc, Image imageDst, int time
			,TriangleMap srcMap, TriangleMap dstMap){

		this.imageSrc=imageSrc;
		this.imageDst=imageDst;
		this.srcMap=srcMap;
		this.dstMap=dstMap;
		this.time=time;

		getPixelData();
		createFrameArray();
	}

	/**
	 * getPixelData - Convert images (src+Dst) to Vector of pixels
	 */	
	private void getPixelData(){

		// SrcImage and DstImage must be same size!
		imageHeight=(int) imageSrc.getHeight();
		imageWidth=(int) imageSrc.getWidth();

		// vector of pixels
		pixelSrcImage=new int[imageHeight*imageWidth];
		pixelDstImage=new int[imageHeight*imageWidth];

		// Format of data of the pixels: Alpha, Red, Green, Blue
		WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();

		PixelReader prSrc=imageSrc.getPixelReader();
		prSrc.getPixels(0, 0, imageWidth, imageHeight, format, pixelSrcImage, 0, imageWidth);

		PixelReader prDst=imageDst.getPixelReader();
		prDst.getPixels(0, 0, imageWidth, imageHeight, format, pixelDstImage, 0, imageWidth);
	}

	/**
	 * 	createFrameArray: Set first frame and last frame
	 */	
	private void createFrameArray(){
		double timeInSeconds = (double)time/1000; // 1[millisecond] to [seconds] = 1/1000[s]
		int frameAmount = (int) Math.round(((double)framecnt)*timeInSeconds);
		frames = new Image[frameAmount];

		frames[0]=imageSrc;					// First frame - Image of viseme source
		frames[frameAmount-1]=imageDst;		// Last frame - Image of viseme destination
	}

	/**
	 * 	setAllFrames: Set all Frame...
	 * 	from the second frame to the one before the last.
	 */	
	public void setAllFrames(){

		int frameNum;
		double ratioOfFrame;
		TriangleMap middleMap;

		WritablePixelFormat<IntBuffer> format = WritablePixelFormat.getIntArgbInstance();

		for (frameNum=1; frameNum < (frames.length-1) ; frameNum++){

			ratioOfFrame = (double)frameNum / (frames.length-1);
			int[] step  = new int[imageWidth*imageHeight];

			middleMap=getMiddleMap(ratioOfFrame);
			middleMap.setMap();

			// Create the middle frame
			// Builds the image triangle-triangle
			for (int i=0; i<triangleAmount; i++)
				morph_triangles(srcMap.getMap()[i], dstMap.getMap()[i], middleMap.getMap()[i],
						pixelSrcImage, pixelDstImage, step, ratioOfFrame);

			WritableImage wi =
					new WritableImage(imageWidth, imageHeight);
			PixelWriter pw = wi.getPixelWriter();
			pw.setPixels(0, 0, imageWidth, imageHeight, format, step, 0, imageWidth);

			//			ImageView destImageView = new ImageView();
			//			destImageView.setImage(wi);
			frames[frameNum] = wi;
		}

	}
	
	/**
	 * morph_triangles: 
	 * Decoded to format of pixel ARGB
	 * @param green (Triangle)
	 * @param alpha (Triangle) 
	 * @param red 	(Triangle) 
	 * @param blue 	(int[])
	 * @return ARGB format of pixel (int[])
	 * @return ARGB format of pixel (int[])
	 * @return ARGB format of pixel (double)
	 */
	private void morph_triangles(Triangle trSrc, Triangle trDst, 
			Triangle trMid, int[] from, int[] to,
			int[] step, double ratio){

		int    x, y;
		boolean flag=true;
		double srcP1x, srcP2x, srcP3y;
		double dstP1x, dstP2x, dstP3y;
		double midP1x, midP2x, midP3y;
		double slopeSrc1To3, slopeSrc2To3;
		double slopeDst1To3, slopeDst2To3;
		double slopeMid1To3, slopeMid2To3;
		double srcP1And2y,dstP1And2y,midP1And2y;
		double directionSrc2Mid,directionDst2Mid;

		// Points of triangle in source viseme
		srcP1x = (double)(trSrc.getxOryOfPoint(1,'x'));
		srcP2x = (double)(trSrc.getxOryOfPoint(2,'x'));
		srcP3y = (double)(trSrc.getxOryOfPoint(3,'y'));
		srcP1And2y  = (double)(trSrc.getxOryOfPoint(1,'y'));

		// Points of triangle in destination viseme
		dstP1x = (double)(trDst.getxOryOfPoint(1,'x'));
		dstP2x = (double)(trDst.getxOryOfPoint(2,'x'));
		dstP3y = (double)(trDst.getxOryOfPoint(3,'y')); 
		dstP1And2y = (double)(trDst.getxOryOfPoint(1,'y'));

		// Points of triangle in middle viseme
		midP1x = (double)(trMid.getxOryOfPoint(1,'x'));
		midP2x = (double)(trMid.getxOryOfPoint(2,'x'));
		midP3y = (double)(trMid.getxOryOfPoint(3,'y'));
		midP1And2y = (double)(trMid.getxOryOfPoint(1,'y'));

		directionSrc2Mid = calculateSlope(srcP1And2y, srcP3y ,midP1And2y, midP3y);
		directionDst2Mid = calculateSlope(dstP1And2y, dstP3y, midP1And2y, midP3y);

		// Slope line 1-3 source viseme 
		slopeSrc1To3 =calculateSlope(trSrc.getP1(),trSrc.getP3());
		// Slope line 2-3 source viseme 
		slopeSrc2To3 =calculateSlope(trSrc.getP2(),trSrc.getP3());
		// Slope line 1-3 destination viseme 
		slopeDst1To3 =calculateSlope(trDst.getP1(),trDst.getP3());
		// Slope line 2-3 destination viseme 
		slopeDst2To3 =calculateSlope(trDst.getP2(),trDst.getP3());
		// Slope line 1-3 destination viseme 			
		slopeMid1To3 =calculateSlope(trMid.getP1(),trMid.getP3());
		// Slope line 2-3 destination viseme 
		slopeMid2To3 =calculateSlope(trMid.getP2(),trMid.getP3());
		
		if(trMid.getP3().getY()<trMid.getP1().getY()){	
			slopeSrc1To3=-slopeSrc1To3;
			slopeSrc2To3=-slopeSrc2To3;

			slopeDst1To3=-slopeDst1To3;
			slopeDst2To3=-slopeDst2To3;

			slopeMid1To3=-slopeMid1To3;
			slopeMid2To3=-slopeMid2To3;
		}

		y = (int)midP1And2y;

		while(flag){

			for (x=(int)midP1x; x<=(int)midP2x; x++){

				double ratioOfX=((double)x-midP1x)/(midP2x-midP1x);

				int pixleSrc = from[((int)Math.round(srcP1And2y) * imageWidth) + 
				          (int)Math.round(srcP1x + (ratioOfX * (srcP2x - srcP1x)))];
				int pixleDst = to  [((int)Math.round(dstP1And2y) * imageWidth) +
				          (int)Math.round(dstP1x + (ratioOfX * (dstP2x - dstP1x)))];

				int[] pixelFromDataSeparated = getPixelData(pixleSrc);
				int[] pixelToDataSeparated = getPixelData(pixleDst);
				int alpha,red,green,blue;
				
				// Linear interpolation for color of the pixel
				alpha = (int) (pixelFromDataSeparated[0] +
						((pixelToDataSeparated[0] - pixelFromDataSeparated[0]) * ratio));
				red  = (int) (pixelFromDataSeparated[1] +
						((pixelToDataSeparated[1] - pixelFromDataSeparated[1]) * ratio));
				green= (int) (pixelFromDataSeparated[2] +
						((pixelToDataSeparated[2] - pixelFromDataSeparated[2]) * ratio));
				blue = (int) (pixelFromDataSeparated[3] + 
						((pixelToDataSeparated[3] - pixelFromDataSeparated[3]) * ratio));
				
				step[(y*imageWidth)+x]=setPixelData(alpha, red, green, blue);
//				step[(y*imageWidth)+x]=(alpa<<24) | (red<<16) | (green<<8) | blue;
			}

			srcP1x += slopeSrc1To3;
			srcP2x += slopeSrc2To3;
			dstP1x += slopeDst1To3;
			dstP2x += slopeDst2To3;
			midP1x += slopeMid1To3;
			midP2x += slopeMid2To3;

			if(trMid.getP3().getY()>trMid.getP1().getY()){
				srcP1And2y+= directionSrc2Mid;
				dstP1And2y+= directionDst2Mid;
				y++;
			}
			else{
				srcP1And2y-= directionSrc2Mid;
				dstP1And2y-= directionDst2Mid;
				y--;
			}

			if(trMid.getP3().getY()>trMid.getP1().getY() && y>=trMid.getP3().getY())
				flag=false;
			else if(trMid.getP3().getY()<=trMid.getP1().getY() && y<=trMid.getP3().getY())
				flag=false;		
		}
	}

	/**
	 * getMiddleMap: 
	 * 
	 * @param ratio (double)
	 * @return (TriangleMap)
	 */
	private  TriangleMap getMiddleMap(double ratio){

		TriangleMap middleMap; 

		Point p1 = new Point(getPointOnLine(ratio,1));
		Point p2 = new Point(getPointOnLine(ratio,2));
		Point p3 = new Point(getPointOnLine(ratio,3));

		middleMap = new TriangleMap(p1, p2, p3, imageHeight, imageWidth);

		return middleMap;
	}

	/**
	 * getPointOnLine: 
	 * 
	 * @param ratio (double)
	 * @param pointNumber (int)
	 * @return (Point) 
	 */
	private Point getPointOnLine(double ratio, int pointNumber){

		Point p= new Point();
		p.x = (int)Math.round( (double)srcMap.getPointOfMainTriangle(pointNumber).getX() +
				((double)(dstMap.getPointOfMainTriangle(pointNumber).getX() - srcMap.getPointOfMainTriangle(pointNumber).getX()) * ratio));
		p.y = (int)Math.round( srcMap.getPointOfMainTriangle(pointNumber).getY() +
				((double)(dstMap.getPointOfMainTriangle(pointNumber).getY() - srcMap.getPointOfMainTriangle(pointNumber).getY()) * ratio));

		return p;
	}

	/**
	 * calculateSlope: 
	 * Slope calculation of two points: (x2-x1)/(y2-y1)
	 * @param x1 (double)
	 * @param x2 (double)
	 * @param y1 (double)
	 * @param y2  (double)
	 * 	
	 */
	private double calculateSlope(double x1, double x2,double y1, double y2){

		return (double)(x2 - x1) / (double)(y2 - y1);
	}

	/**
	 * calculateSlope: 
	 * Slope calculation of two points: p1->x1,y1 p2->x2,y2 (x2-x1)/(y2-y1)
	 * @param p1 (Point)
	 * @param p2 (Point) 	
	 */
	private double calculateSlope(Point p1, Point p2){

		return calculateSlope(p1.getX(), p2.getX(),p1.getY() ,p2.getY());
	}

	/**
	 * getPixelData: 
	 * Decoded to format of pixels ARGB
	 * @param pixel (int)
	 * @return array of int by order: Alpha, Red, Green, Blue 	
	 */
	private int[] getPixelData(int pixel){

		int rgb[] = new int[] {
				(pixel >> 24) & 0xFF, 	// Alpha
				(pixel >> 16) & 0xFF, 	// Red
				(pixel >>  8) & 0xFF,	// Green
				(pixel)       & 0xFF	// Blue
		};
		return rgb;
	}
	
	/**
	 * setPixelData: 
	 * Decoded to format of pixel ARGB
	 * @param alpha (int) 
	 * @param red (int) 
	 * @param green (int)
	 * @param blue (int)
	 * @return ARGB format of pixel (int)
	 */
	private int setPixelData(int alpha, int red, int green, int blue){

		return (alpha << 24) | (red << 16) | (green << 8) | blue;
	}

	/**
	 * @return array of Image. All the frames.
	 */
	public Image[] getFrames() {
		return frames;
	}
}
