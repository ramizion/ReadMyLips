

import java.awt.Point;

public class TriangleMap {
	
	final static private  int triangleAmount = 14;
	private Triangle mainTriangle;
	private int heightImage;
	private int widthImage;
	private Triangle[] map;
	
	TriangleMap(){
		
	}
	
	/**
	 * Constructor
	 * @param p1
	 * @param p2
	 * @param p3
	 * @param heightImage
	 * @param widthImage
	 */
	TriangleMap(Point p1, Point p2, Point p3, int heightImage, int widthImage){
		this(new Triangle(p1,p2,p3), heightImage, widthImage);
	}
	
	TriangleMap(Triangle mainTriangle, int heightImage, int widthImage){
		
		this.map = new Triangle[triangleAmount];
		this.mainTriangle=mainTriangle;
		this.heightImage=heightImage;
		this.widthImage=widthImage;
	}
	
	public Triangle getMainTriangle() {
		return mainTriangle;
	}
	
	public Point getPointOfMainTriangle(int pointNumber){
		
		if(pointNumber==1)
			return mainTriangle.getP1();
		else if(pointNumber==2)
			return mainTriangle.getP2();
		else
			return mainTriangle.getP3();
	}

	public void setMainTriangle(Triangle mainTriangle) {
		this.mainTriangle = mainTriangle;
	}
	
	/**
	 * Set the Map of the viseme
	 */
	public void setMap(){
		
		Triangle[] tempTrian;
		
		tempTrian=splitTriangle(mainTriangle);
		map[0]=tempTrian[0];
		map[1]=tempTrian[1];

		tempTrian=splitTriangle(new Triangle(mainTriangle.getP1(), mainTriangle.getP3(), new Point(0,heightImage-1)));
		map[2]=tempTrian[0];
		map[3]=tempTrian[1];
		
		tempTrian=splitTriangle(new Triangle(mainTriangle.getP2(), mainTriangle.getP3(), new Point(widthImage-1,heightImage-1)));
		map[4]=tempTrian[0];
		map[5]=tempTrian[1];

		tempTrian=splitTriangle(new Triangle(new Point(0,0), mainTriangle.getP1(), new Point(0,heightImage-1)));
		map[6]=tempTrian[0];
		map[7]=tempTrian[1];
		
		tempTrian=splitTriangle(new Triangle(new Point(widthImage-1,0), mainTriangle.getP2(), new Point(widthImage-1,heightImage-1)));
		map[8]=tempTrian[0];
		map[9]=tempTrian[1];
		
		tempTrian=splitTriangle(new Triangle(new Point(widthImage-1,0), mainTriangle.getP1(), mainTriangle.getP2()));
		map[10]=tempTrian[0];
		map[11]=tempTrian[1];
			
		map[12]=new Triangle(new Point(0,0), new Point(widthImage-1,0),mainTriangle.getP1());
		map[13]=new Triangle(new Point(0,heightImage-1),  new Point(widthImage-1,heightImage-1), mainTriangle.getP3());

	}
	
	/**
	 * @return Triangle[]
	 */
	public Triangle[] getMap(){
		
		return map;
	}
	
	/**
	 * split triangle to 2 triangle
	 * @param tr
	 * @return
	 */
	public Triangle[] splitTriangle(Triangle tr){
		
		Triangle[] trArray= new Triangle[2];
		
		double a= (tr.getP3().getX()-tr.getP1().getX())/(tr.getP3().getY()-tr.getP1().getY());
		int x=(int) (tr.getP1().getX()+Math.round(a*(tr.getP2().getY()-tr.getP1().getY())));
		
		Point middlePoint = new Point(x,tr.getP2().y);
		
		if(tr.getP2().getX()>x){
			trArray[0]= new Triangle(middlePoint,tr.getP2(), tr.getP3());
			trArray[1]= new Triangle(middlePoint,tr.getP2(), tr.getP1());
		}
		else{
			trArray[0]= new Triangle(tr.getP2(),middlePoint, tr.getP3());
			trArray[1]= new Triangle(tr.getP2(),middlePoint, tr.getP1());			
		}
		return trArray;
	}
	
	public String toString(){
		
		String str="";
		for(int i=0; i<map.length;i++){
			str+=i+"-("+map[i].getP1().getX()+","+map[i].getP1().getY()+") ";
			str+="("+map[i].getP2().getX()+","+map[i].getP2().getY()+") ";
			str+="("+map[i].getP3().getX()+","+map[i].getP3().getY()+")\n";
		}
		return str;
	}
}