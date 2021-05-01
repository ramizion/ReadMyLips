import java.io.File;
import java.util.ArrayList;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

public class EyesDetector {

	private File file;
	private Mat imageMat;
	private Rect[] rectFace;
	private Point[] circleEyes;
	private Point circleNose;
	
	EyesDetector(File file) {
		this.file=file;
		setFaceDetrctor();
		setEyesDetrctor();
		setMouthDetrctor();
	}
	
	public Rect[] getRectFace() {
		return rectFace;
	}

	public void setRectFace(Rect[] rectFace) {
		this.rectFace = rectFace;
	}
	
	
	public Point[] geteEyes(){
		
		return circleEyes;
	}
	
	public Point getNose(){
		
		return circleNose;
	}
	
	public Triangle getTriangle(){
		Triangle tr;
		java.awt.Point nose = new java.awt.Point();
		java.awt.Point eye1 = new java.awt.Point();
		java.awt.Point eye2 = new java.awt.Point();
		
		eye1 = convertOpenCVPoint(circleEyes[0]);
		if( circleEyes.length==2)
			eye2 = convertOpenCVPoint(circleEyes[1]);
		else
			eye2=null;

		nose = convertOpenCVPoint(circleNose);
		
		if(eye1==null || eye2==null
				|| nose==null){
			tr=new Triangle(eye1,eye2, nose);	
		}
		else if(eye1.x<eye2.x){
			tr=new Triangle(eye1,eye2,nose);	
		}
		else{
			tr=new Triangle(eye2,eye1,nose);	
		}		
		return tr;
		
	}
	
	public void getBigDiff(){
		
	}
	
	public java.awt.Point convertOpenCVPoint(Point pOpen){
		if(pOpen==null)
			return null;
		java.awt.Point p =new java.awt.Point((int)pOpen.x,(int)pOpen.y);
		return p;
	}
	
	public int[] getIndexOfMinDiff(int[] points){
		int[] indexes = new int[2];
		ArrayList<Integer>[] diff;
		int min;
		return indexes;
	}
	
	public void setFaceDetrctor(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier faceDetector
		= new CascadeClassifier("C:\\Users\\Rami\\workspace_eclipse\\ReadMyLips\\src\\haarcascade_frontalface_alt.xml");
		
		imageMat = Highgui
				.imread(file.getPath());

		MatOfRect faceDetections = new MatOfRect();
		faceDetector.detectMultiScale(imageMat, faceDetections, 1.1, 2, 0, new Size(imageMat.width()*0.20,imageMat.height()*0.20), new Size());

		rectFace=faceDetections.toArray();
	}
	
	
	public void setEyesDetrctor(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier eye_cascade
		= new CascadeClassifier("C:\\Users\\Rami\\workspace_eclipse\\ReadMyLips\\src\\haarcascade_eye_tree_eyeglasses.xml");
		
		MatOfRect eyedetections=new MatOfRect();
		
		for (int i = 0; i < rectFace.length; i++) {
			
			Rect rectFaceTemp = new Rect(rectFace[i].x,rectFace[i].y,rectFace[i].width,rectFace[i].height/2);

//			Core.rectangle(image, new Point(facesArray[i].x,facesArray[i].y), 
//					new Point (facesArray[i].x+facesArray[i].width,facesArray[i].y+facesArray[i].height),new Scalar(60, 40, 12));
//			
			Core.rectangle(imageMat, new Point(rectFaceTemp.x,rectFaceTemp.y), 
					new Point (rectFaceTemp.x+rectFaceTemp.width,rectFaceTemp.y+rectFaceTemp.height),new Scalar(255, 0, 0));
			
			Mat faceROI = imageMat.submat(rectFaceTemp);

			eye_cascade.detectMultiScale(faceROI, eyedetections, 1.02, 2, 1,
					new Size(30, 30), new Size());
			
			Rect[] eyesArray = eyedetections.toArray();
			System.out.println("Eyes Detected:" + eyesArray.length);

			if(eyesArray.length>=2){
				circleEyes = new Point[eyesArray.length];
				for (int j = 0; j < eyesArray.length ; j++) {

					circleEyes[j] = new Point(rectFace[i].x + eyesArray[j].x+ eyesArray[j].width * 0.5,
							rectFace[i].y+ eyesArray[j].y + eyesArray[j].height * 0.5);
					
//					center2 = new Point(eyesArray[j].x+eyesArray[j].width-(facesArray[i].x + eyesArray[j].x+ eyesArray[j].width * 0.5-eyesArray[j].x),
//							facesArray[i].y+ eyesArray[j].y + eyesArray[j].height * 0.5);
					
					int radius = (int) Math.round(5);
					Core.circle(imageMat, circleEyes[j], radius, new Scalar(255, 0, 0), 4,
							8, 0);
//					Core.circle(image, center2, radius, new Scalar(255, 0, 0), 4,
//							8, 0);

				}
			}

		}
		
//		for (int i = 0; i < rectFaceTemp.length; i++){     
//			Mat faceROI = imageMat.submat(rectFace[i]);
//			
//			eye_cascade.detectMultiScale(faceROI, eyedetections, 1.1, 1, 1, new Size(30,30), new Size());
//			Rect[] eyesArray = eyedetections.toArray();
//			System.out.println("Eyes Detected:" + eyesArray.length);
//			
//			for (int j = 0; j < 2; j++){
//				circleEyes[j] = new Point(rectFace[i].x + eyesArray[j].x + eyesArray[j].width * 0.5, 
//	            		rectFace[i].y + eyesArray[j].y + eyesArray[j].height * 0.5);
//			}
//		}
	}
	
	public void setMouthDetrctor(){
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		CascadeClassifier mouth_cascade
		= new CascadeClassifier("C:\\Users\\Rami\\workspace_eclipse\\ReadMyLips\\src\\Mouth.xml");

		MatOfRect mouthDetections = new MatOfRect();
		
		for (int i = 0; i < rectFace.length; i++) {
		
			Rect rectFaceTemp = new Rect(rectFace[i].x,
					(int) (rectFace[i].y+rectFace[i].height/2),rectFace[i].width,rectFace[i].height/2);
			Core.rectangle(imageMat, new Point(rectFaceTemp.x,rectFaceTemp.y), 
					new Point (rectFaceTemp.x+rectFaceTemp.width,rectFaceTemp.y+rectFaceTemp.height),
					new Scalar(255, 0, 225));
			
			//ROI -- Region of Interest
			Mat faceROI = imageMat.submat(rectFaceTemp);
			
			mouth_cascade.detectMultiScale(faceROI, mouthDetections, 1.05,4,1, 
					new Size(imageMat.width()*0.11, imageMat.height()*0.06), new Size());
			Rect[] mouthArray = mouthDetections.toArray();
			System.out.println("mouth Detected:" + mouthArray.length);
			for (int j = 0; j < mouthArray.length ; j++) {
//				if(mouthCenterEnvironment().x<mouthArray[j].x && 
//						mouthCenterEnvironment().y>mouthArray[j].x){
				circleNose = new Point(rectFace[i].x + mouthArray[j].x+ mouthArray[j].width * 0.5,
							rectFace[i].y+ mouthArray[j].y + mouthArray[j].height * 0.5+rectFace[i].height/2);
					

//					Core.circle(imageMat, circleNose, 3, new Scalar(0, 255, 0), 4, 8, 0);
//					
//					Core.rectangle(imageMat, 
//							new Point(rectFace[i].x + mouthArray[j].x,
//									rectFace[i].y+ mouthArray[j].y +rectFace[i].height/2),
//							new Point (rectFace[i].x + mouthArray[j].x+ mouthArray[j].width
//									, rectFace[i].y+ mouthArray[j].y + mouthArray[j].height+rectFace[i].height/2),
//									new Scalar(255, 0, 0));
//					break;
//				}
			}

		}
//		
//		MatOfRect eyedetections=new MatOfRect();
//		
//		Rect[] rectFaceTemp = new Rect[Math.abs(rectFace.length/2)];
//		System.arraycopy(rectFace, Math.abs(rectFace.length/2),rectFaceTemp, 0, Math.abs(rectFace.length/2));
//		
//		for (int i = 0; i < 1; i++){     
//			Mat faceROI = imageMat.submat(rectFace[i]);
//			
//			mouth_cascade.detectMultiScale(faceROI, eyedetections, 1.1, 1, 1, new Size(30,30), new Size());
//			Rect[] mouthArray = eyedetections.toArray();
//			
//			for (int j = 0; j < mouthArray.length; j++){
//				circleMouth[j] = new Point(rectFace[i].x + mouthArray[j].x + mouthArray[j].width * 0.5, 
//	            		rectFace[i].y + mouthArray[j].y + mouthArray[j].height * 0.5);
//			}
//		}
	}
}
