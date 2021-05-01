

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;

import javafx.application.Application;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.MouseEvent;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class ZoomToViseme extends Application{

	private VisemeFrame visemeFrame;	
	private Point p=new Point(0,0);
	private Font font = new Font("Courier New",16);
	private ImageView imageView;
	private EventHandler<WindowEvent> eventupdaeFrame; 

	public ZoomToViseme() {
		super();
	}
	
	/**
	 * Constructor
	 * @param visemeFrame
	 * @param eventupdaeFrame
	 */
	public ZoomToViseme(VisemeFrame visemeFrame,EventHandler<WindowEvent> eventupdaeFrame) {
		this.imageView= new ImageView();
		this.visemeFrame = visemeFrame;
		this.eventupdaeFrame =eventupdaeFrame;
	}

	/**
	 * Return the stage
	 * @return {@link Stage}
	 */
	public Stage getStage(){

		Stage stage = new Stage();
		BorderPane pane = new BorderPane();
		
		imageView.setImage(getTrianglesImage());
		
		pane.getStylesheets().add("CSS/zoomCss.css");
		pane.setCenter(imageView);
		pane.setLeft(getLeftPane());

		Scene scene = new Scene(pane);
		stage.setScene(scene);
		
		stage.setOnCloseRequest((e)->visemeFrame.updateStatusLabelViseme());
//		stage.addEventHandler(WindowEvent.ANY, eventupdaeFrame);
		return stage;
	}

	/**
	 * pane for detection status
	 * @return
	 */
	public GridPane detectionPane(){

		GridPane gPaneDetection = new GridPane();
		gPaneDetection.setPadding(new Insets(20,20,20,20));
		gPaneDetection.setVgap(10);
		gPaneDetection.setHgap(10);
		Font font = new Font("Courier New",16);
		Image imageV = new Image("image\\greenV.png");
		Image imageX = new Image("image\\redX.png");

		String strLeftEyes="Left eye detection";
		Label labelLeftEyes = new Label (strLeftEyes);
		labelLeftEyes.setFont(font);
		ImageView imageLeftEye;

		String strRightEye="Right eye detection";
		Label labelRightEye = new Label (strRightEye);
		labelRightEye.setFont(font);
		ImageView imageRightEye;

		String strLips="Lips detection";
		Label labelLips = new Label (strLips);
		labelLips.setFont(font);
		ImageView imageLips;

		// label Eyes
		if(visemeFrame.getTriangle()!=null &&
				visemeFrame.getTriangle().getP1()!=null)
			imageRightEye=new ImageView(imageV);
		else
			imageRightEye=new ImageView(imageX);

		// label Eyes
		if(visemeFrame.getTriangle()!=null &&
				visemeFrame.getTriangle().getP2()!=null)
			imageLeftEye=new ImageView(imageV);
		else
			imageLeftEye=new ImageView(imageX);

		// label lips
		if(visemeFrame.getTriangle()!=null &&
				visemeFrame.getTriangle().getP3()!=null)
			imageLips=new ImageView(imageV);
		else
			imageLips=new ImageView(imageX);

		gPaneDetection.add(imageRightEye,0,0);
		gPaneDetection.add(labelLeftEyes,1,0);

		gPaneDetection.add(imageLeftEye,0,1);
		gPaneDetection.add(labelRightEye,1,1);

		gPaneDetection.add(imageLips,0,2);
		gPaneDetection.add(labelLips,1,2);

		CheckBox chkManual = new CheckBox("Manual");
		gPaneDetection.add(chkManual,0,4);

		chkManual.setOnAction(new EventHandler<ActionEvent>() {


			@Override
			public void handle(ActionEvent arg0) {
				if ( chkManual.isSelected()){
					EventHandler<MouseEvent> mouseEvent = new EventHandler<MouseEvent>() {
						public void handle(MouseEvent event) {
							
								ClipboardContent content = new ClipboardContent();
								content.putString(((Label)event.getSource()).getText());
								System.out.println(((Label)event.getSource()).getText());
								Dragboard db = labelLeftEyes.startDragAndDrop(TransferMode.MOVE);
								db.setContent(content); 
								event.consume();
						}
					};	
					
					labelLeftEyes.setOnDragDetected(mouseEvent);
					labelRightEye.setOnDragDetected(mouseEvent);
					labelLips.setOnDragDetected(mouseEvent);
					
					imageView.setOnDragOver(new EventHandler <DragEvent>() {
						public void handle(DragEvent event) {
							System.out.println("onDragOver");
							event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
						}
					});
					
					imageView.setOnDragDropped(dragEvent());
				}
				else{
					labelLeftEyes.setOnDragDetected(null);
					labelRightEye.setOnDragDetected(null);
					labelLips.setOnDragDetected(null);
				}


			}
		});
		return gPaneDetection;
	}

	/**
	 * Drag event
	 * @return
	 */
	private EventHandler<DragEvent> dragEvent(){

		EventHandler <DragEvent> dragEvent = new EventHandler <DragEvent>() {
			public void handle(DragEvent event) {

				Dragboard db = event.getDragboard();
				boolean success = false;

				if (db.getString()=="Left eye detection"){
					visemeFrame.setTriangle(new Triangle(
							new Point((int)event.getX(),(int)event.getY()),
							visemeFrame.getTriangle().getP2(),
							visemeFrame.getTriangle().getP3())
					);
				}
					
				else  if (db.getString()=="Right eye detection"){
					visemeFrame.setTriangle(new Triangle(
							visemeFrame.getTriangle().getP1(),
							new Point((int)event.getX(),(int)event.getY()),
							visemeFrame.getTriangle().getP3())
					);					
				}
				else  if (db.getString()=="Lips detection"){
					visemeFrame.setTriangle(new Triangle(
							visemeFrame.getTriangle().getP1(),
							visemeFrame.getTriangle().getP2(),
							new Point((int)event.getX(),(int)event.getY()))
					);	
				}
				visemeFrame.updateMap();
				imageView.setImage(getTrianglesImage());

				event.setDropCompleted(success);
				event.consume();
			}
		};
		return dragEvent;
	}

	/**
	 * create pane for left 
	 * @return
	 */
	public VBox getLeftPane(){

		// Image properties
		GridPane gPaneSize = new GridPane();

		String strImageWidth = "Width  - " + visemeFrame.getImage().getWidth() + " Pixels";
		Label labelImageWidth = new Label (strImageWidth);
		labelImageWidth.setFont(font);

		String strImageHeight = "Height - " + visemeFrame.getImage().getHeight() + " Pixels";
		Label labelImageHeight = new Label (strImageHeight);
		labelImageHeight.setFont(font);

		gPaneSize.add(labelImageWidth,1,3);
		gPaneSize.add(labelImageHeight,1,4);

		// Viseme properties
		GridPane gPaneViseme = new GridPane();

		String strVisemePro = "Phoneme: /" +visemeFrame.getPhoneme() +"/";
		Label labelVisemePro = new Label (strVisemePro);
		labelVisemePro.setFont(font);

		gPaneViseme.add(labelVisemePro,1,3);

		//		Manual viseme detection
		VBox gPaneManualDetec = new VBox();

		Text textEyesRight = new Text("Left Eye");
		textEyesRight.setFont(font);
		Text textEyesLeft = new Text("Right Eye");
		textEyesLeft.setFont(font);
		Text textLips = new Text("Lips");
		textLips.setFont(font);

		gPaneManualDetec.getChildren().add(textEyesRight);
		gPaneManualDetec.getChildren().add(textEyesLeft);
		gPaneManualDetec.getChildren().add(textLips);


		VBox vbox = new VBox(20);
		vbox.setPadding(new Insets(20,20,20,20));

		vbox.getChildren().add(new BorderedTitledPane("Image properties",gPaneSize));
		vbox.getChildren().add(new BorderedTitledPane("Viseme properties",gPaneViseme));
		vbox.getChildren().add(new BorderedTitledPane("Viseme detection",detectionPane()));

		return vbox;
	}
	
	/**
	 * Get the image with the triangle
	 * @return {@link image}
	 */
	public Image getTrianglesImage(){

		Image image = visemeFrame.getImage();
		System.out.println(visemeFrame.getTriangle().getP1());
		BufferedImage imageGraphics = SwingFXUtils.fromFXImage(image, null);
		Graphics g = imageGraphics.getGraphics(); 
		g.setColor(Color.BLACK);

		int diameter =20;

		if(visemeFrame.getTriangle()==null);
		else if(visemeFrame.getTriangle().getP1()==null ||
				visemeFrame.getTriangle().getP2()==null ||
				visemeFrame.getTriangle().getP3()==null){
			if(visemeFrame.getTriangle().getP1()!=null)
				g.drawOval(visemeFrame.getTriangle().getP1().x-diameter/2,
						visemeFrame.getTriangle().getP1().y-diameter/2, diameter, diameter);
			if(visemeFrame.getTriangle().getP2()!=null)
				g.drawOval(visemeFrame.getTriangle().getP2().x-diameter/2,
						visemeFrame.getTriangle().getP2().y-diameter/2, diameter, diameter);
			if(visemeFrame.getTriangle().getP3()!=null)
				g.drawOval(visemeFrame.getTriangle().getP3().x-diameter/2,
						visemeFrame.getTriangle().getP3().y-diameter/2, diameter, diameter);
		}
		else{
	
			Triangle[] triangles = visemeFrame.getMap().getMap();

			for(Triangle tri: triangles){
				int[] x={tri.getP1().x, tri.getP2().x, tri.getP3().x};
				int[] y={tri.getP1().y, tri.getP2().y, tri.getP3().y};
				g.drawPolygon(x,y,3);
				g.setColor(Color.BLACK);
			}
			g.dispose();
		}
		image=SwingFXUtils.toFXImage(imageGraphics, null);
		return image;
	}

	private void getPoint() {

		visemeFrame.setOnMouseMoved(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				p = new Point((int)event.getX(),(int)event.getY());
			}
		});
		visemeFrame.setOnMouseExited(new EventHandler<MouseEvent>() {
			@Override public void handle(MouseEvent event) {
				p = new Point(0,0);
			}
		});
	}

	@Override
	public void start(Stage stage) {
//		VisemeFrame visemePane = new VisemeFrame(170, 170,1);
//		visemePane.setImage("C:\\Users\\Rami\\Documents\\ReadMyLips\\visemes\\bibi_1 - Copy.jpg");
//		ZoomToViseme z = new ZoomToViseme(visemePane);
//
//		stage=z.getStage();
//		stage.show();
	}
}
