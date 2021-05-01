

import java.awt.Point;

public class Triangle {
	
	private Point p1;
	private Point p2;
	private Point p3;
	
	/**
	 * @param Point: p1 - Point number 1 
	 * @param Point: p2 - Point number 2 
	 * @param Point: p3	- Point number 3
	 */
	public Triangle(Point p1, Point p2, Point p3) {
		this.p1=p1;
		this.p2=p2;
		this.p3=p3;
	}
	
	/**
	 * @param Char 'x','y'	 
	 * @return Point: get x or y of the point P3
	 */
	public double getxOryOfPoint(int point, char xOry) {
		
		Point p;
		xOry=Character.toLowerCase(xOry); 
		if(point==1)
			p=p1;
		else if(point==2)
			p=p2;
		else
			p=p3;
			
		return (int) (xOry=='x'? p.getX():p.getY());
	}
	
	
	/**
	 * @return Point: get P1
	 */
	public Point getP1() {
		return p1;
	}
	/**
	 * @param Point: p1 
	 */
	public void setP1(Point p1) {
		this.p1 = p1;
	}
	/**
	 * @param Char 'x','y'	 
	 * @return Point: get x or y of the point P1
	 */
	public double getP1xOry(char xOry) {
		xOry=Character.toLowerCase(xOry); 
		return xOry=='x'? p1.getX():p1.getY();
	}
	

	/**
	 * @return Point: get P2
	 */
	public Point getP2() {
		return p2;
	}
	/**
	 * @param Point: p2
	 */
	public void setP2(Point p2) {
		this.p2 = p2;
	}
	/**
	 * @param Char 'x','y'	 
	 * @return Point: get x or y of the point P2
	 */
	public double getP2xOry(char xOry) {
		xOry=Character.toLowerCase(xOry); 
		return xOry=='x'? p2.getX():p2.getY();
	}
	
	/**
	 * @return Point: get P3
	 */
	public Point getP3() {
		return p3;
	}
	/**
	 * @param Point: p3
	 */
	public void setP3(Point p3) {
		this.p3 = p3;
	}
	/**
	 * @param Char 'x','y'	 
	 * @return Point: get x or y of the point P3
	 */
	public double getP3xOry(char xOry) {
		xOry=Character.toLowerCase(xOry); 
		return xOry=='x'? p3.getX():p3.getY();
	}
	

}
