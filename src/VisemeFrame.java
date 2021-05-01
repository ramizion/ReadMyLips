

import java.io.File;
import java.io.IOException;
import java.util.Map;

import javax.imageio.ImageIO;

import com.sun.corba.se.impl.encoding.CodeSetConversion.BTCConverter;
import com.sun.glass.ui.Size;

import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;

public class VisemeFrame extends BorderPane implements Comparable<VisemeFrame>{

	private String 	    url;
	private TriangleMap map;
	private Image 	    image;
	private Boolean     change;
	private Button 	    btnAdd;
	private boolean     status;
	private Size 	    imageSize;
	private int 		visemeNumber;

	private char	  phoneme;
	private Button 	  btnDelete;
	private Triangle  triangle;
	private ImageView imageView;
	private String 	  emptyImageUrl;
	private String 	  sampleImageUrl;


	/**
	 * Constructor 
	 * @param visemeNumber
	 */
	VisemeFrame(int visemeNumber){

		this(100,100,visemeNumber);
	}

	/**
	 * Constructor
	 * @param height
	 * @param width
	 * @param visemeNumber
	 */
	VisemeFrame(int height, int width, int visemeNumber){

		change=false;
		status=false;
		this.visemeNumber=visemeNumber;

		for(Map.Entry entry: Data.getVisemestype2phoneme().entrySet()){
			if((Integer)visemeNumber==entry.getValue()){
				phoneme = (char) entry.getKey();
				break; 
			}
		}

		sampleImageUrl="\\image\\visemeExample\\"+this.visemeNumber+".png";
		emptyImageUrl = Data.getEmptyimageurl();
		url= emptyImageUrl;

		setImage(url);
		imageView.setFitHeight(height);
		imageView.setFitWidth(width);

		btnAdd= new Button("Add", new ImageView(new Image("addViseme.jpg")));
		btnAdd.setMinSize(width/2, height/6);
		btnAdd.setStyle("-fx-background-color: #e8e9f2;");

		btnDelete= new Button("Delete", new ImageView(new Image("delViseme.jpg")));
		btnDelete.setMinSize(width/2, height/6);

		HBox btnPane = new HBox();
		btnPane.getChildren().addAll(btnAdd);
		btnPane.getChildren().addAll(btnDelete);

		// Set tool-tip
		final Tooltip toolTip = new Tooltip();
		toolTip.setFont(new Font("Arial", 160));
		toolTip.setText("Upload here viseme for phoneme: /" + phoneme+"/");
		toolTip.setStyle("-fx-font-size: 1.85em;");

		ImageView image = new ImageView(new Image(sampleImageUrl));
		image.setFitHeight(height);
		image.setFitWidth(width);
		//		toolTip.setGraphic(image);

		// Add Label for Image+ToolTip
		Label lbImageView = new Label();
		lbImageView.setGraphic(imageView);  
		lbImageView.setTooltip(toolTip);

		// Add Image and buttons to frame
		setCenter(lbImageView);
		setBottom(btnPane);
		setMaxWidth(width);//
		updateStatusLabelViseme();

	}
	
	/**
	 * Eyes and lips detrctor
	 */
	public void eyesDetector(){
		if(!(url.equals("file:"+sampleImageUrl) || url.equals("file:"+emptyImageUrl))){
			File file = new File(url.substring(url.indexOf("file:")+("file:").length(), url.length()));
			System.out.println(file.toString());

			EyesDetector eyesDet = new EyesDetector(file);
			triangle=eyesDet.getTriangle();
		}
	}
	
	/**
	 * Update status of viseme: (boolean) status
	 */
	public void updateStatus(){
		if(!(url.toLowerCase().contains(sampleImageUrl.toLowerCase()) 
				|| url.toLowerCase().contains(emptyImageUrl.toLowerCase()))){
			if(triangle.getP1()==null || triangle.getP2()==null|| triangle.getP3()==null){
				//				System.out.println("bad viseme");	
			}
			else{
				updateMap();
				System.out.println(map.toString());
				status=true;
			}
		}
		else
			status=false;
	}

	/**
	 * Update the status on the label in the pane
	 */
	public void updateStatusLabelViseme(){
		updateStatus();
		HBox boxStatus = new HBox();
		boxStatus.setStyle("-fx-background-color: #d0e0ea");
		Image imageV = new Image("image\\greenV.png");
		Image imageX = new Image("image\\redX.png");
		ImageView statusView;
		if (status)
			statusView=new ImageView (imageV);
		else
			statusView=new ImageView (imageX);
		String str="Viseme for phoneme: " + phoneme;
		Label lableStatus = new Label(str);
		lableStatus.setFont(new Font("Miriam",15));

		boxStatus.getChildren().add(statusView);
		boxStatus.getChildren().add(lableStatus);

		setTop(boxStatus);
	}

	/**
	 * @return the phoneme
	 */
	public char getPhoneme() {
		return phoneme;
	}

	/**
	 * Save the Image on the folder
	 * @param urlFolder
	 * @throws IOException
	 */
	public void saveImage(String urlFolder) throws IOException{

		change=true;
		if(image!=null && !url.toLowerCase().contains(sampleImageUrl.toLowerCase()) 
				&& !url.toLowerCase().contains(emptyImageUrl.toLowerCase()))			
			ImageIO.write(SwingFXUtils.fromFXImage(image, null), "jpg", new File(urlFolder));
	}

	/**
	 * Checks if viseme change after it saved
	 * @return
	 */
	public boolean isChangeAfterSaved(){

		return change;
	}

	/**
	 * Checks if the url is image
	 * @param url
	 * @return
	 */
	public boolean isImage(String url){

		String[] extensionImage= Data.getExtensionimage();

		for(String extension: extensionImage)
			if(url.toLowerCase().contains(extension.substring(2).toLowerCase()))
				return true;
		return false;
	}

	/**
	 * Set the image in the pane
	 * @param url
	 */
	public void setImage(String url){
		status=false;

		if(!url.toLowerCase().toString().startsWith("file"))
			url = "file:"+url;
		this.url=url;
		System.out.println(this.url);
		image=new Image(this.url);
		if(imageView==null)
			imageView = new ImageView(image);
		else
			imageView.setImage(image);

		imageSize = new Size((int)image.getHeight(),(int)image.getWidth());

		eyesDetector();
		updateStatusLabelViseme();
	}

	/**
	 * @return the triangle
	 */
	public Triangle getTriangle() {
		return triangle;
	}

	/**
	 * @param triangle {@link Triangle} the triangle to set
	 */
	public void setTriangle(Triangle triangle) {
		this.triangle = triangle;
	}

	/**
	 * Return image
	 * @return {@link image}
	 */
	public Image getImage(){
		return image;
	}

	/**
	 * Return map of the viseme
	 * @return {@link TriangleMap}
	 */
	public TriangleMap getMap(){

		return map;
	}

	/**
	 * Update the map of the viseme
	 */
	public void updateMap(){

		map=new TriangleMap(triangle,(int) image.getHeight(),(int) image.getWidth());
		map.setMap();
	}

	/**
	 * Returns if there is viseme
	 * @return {@link boolean}
	 */
	public boolean isViseme(){

		if(!url.contains(sampleImageUrl) && !url.contains(emptyImageUrl))
			return true;
		return false;
	}

	/**
	 * Set sample Viseme
	 */
	public void setSampleImage(){

		url=sampleImageUrl;
		image=new Image(sampleImageUrl);
		imageView.setImage(image);
	}

	/**
	 * Set empty image in the viseme pane
	 */
	public void setEmptyImage(){

		url=emptyImageUrl;
		image=new Image(emptyImageUrl);
		imageView.setImage(image);
	}

	/**
	 * Action for btnAdd and btnDelete
	 * @param eventHandlerSetNotSave
	 */
	public void addActionListener(EventHandler<ActionEvent> eventHandlerSetNotSave){

		btnAdd.addEventHandler(ActionEvent.ACTION, (e)-> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open Resource File");
			FileChooser.ExtensionFilter extFilter = new FileChooser.ExtensionFilter("pictures file format",
					"*.TIF","*.JPG","*.PNG","*.GIF","*.JEPG", ".BMP");
			fileChooser.getExtensionFilters().addAll(extFilter);
			File file = fileChooser.showOpenDialog(null);

			if (file != null){

				if(!isImage(file.toString())){
					Data.alertMessageWarningOnlyOK(Data.getMessagenotimage());
					return;
				}

				if(isViseme()){
					if(!Data.alertMessageWarning("The viseme will change, and previous viseme will be delete.",'n'))
						return;
				}


				setImage("file:"+file.toString());
				change=true;
			}
		});

		btnAdd.addEventHandler(ActionEvent.ACTION, eventHandlerSetNotSave);

		btnDelete.addEventHandler(ActionEvent.ACTION, (e)-> {

			if(!url.contains(emptyImageUrl)){
				if(!Data.alertMessageWarning("Are you sure you want delete the viseme?.",'n'))
					return;
				change=true;
				setImage(emptyImageUrl);
			}
		});

		btnDelete.addEventHandler(ActionEvent.ACTION, eventHandlerSetNotSave);
	}

	/**
	 * @return the visemeNumber
	 */
	public int getVisemeNumber() {
		return visemeNumber;
	}

	/**
	 * @return the url
	 */
	public String getUrl() {
		return url;
	}

	/**
	 * @return the status
	 */
	public boolean isStatus() {
		return status;
	}

	/**
	 * @return the imageSize
	 * 	(Height, Width)
	 */
	public Size getImageSize() {
		return imageSize;
	}

	/**
	 * @return the btnDelete
	 */
	public Button getBtnDelete() {
		return btnDelete;
	}

	/**
	 * @return the btnAdd
	 */
	public Button getBtnAdd() {
		return btnAdd;
	}

	@Override
	public String toString() {
		return "VisemeFrame [url=" + url + ", sampleImageUrl=" + sampleImageUrl
				+ ", changeAfterSaved=" + change + ", imageView="
				+ imageView + ", image=" + image + ", triangle=" + triangle
				+ ", btnAdd=" + btnAdd + ", btnDelete=" + btnDelete + ", map="
				+ map + ", status=" + status + ", imageSize=" + imageSize + "]";
	}

	@Override
	public int compareTo(VisemeFrame viFrame) {
		if(viFrame.getImageSize()==null)
			return 0;
		return (this.getImageSize().height!=viFrame.getImageSize().height? 
				1:this.getImageSize().width!=viFrame.getImageSize().width? 0:1);
	}

}
