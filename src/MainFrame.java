import java.awt.Point;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Optional;
import javax.swing.JFileChooser;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.NodeOrientation;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class MainFrame extends Application {

	private VisemeFrame[] visemePane; // All visemes
	private int visemesAmount;
	private TextArea txtInput;
	private MyToolBar toolBar; // why is it a global variable?!???!
	private String currentDirectory; // Full path to the folder project
	private String projectName; // project name = folder name
	private Stage primaryStage;
	private Button btnSayIt;
	private BorderPane videoPane;
	private VBox inputPane;
	private int videoWidth;
	private int videoHeight;
	private boolean statusVisems;
	private int projectStatus;	// (0)->new project , (1)->project saved , (-1)->project edited and didn't save


	/**
	 * Building  the stage  and add all the panes
	 */
	@Override
	public void start(Stage primaryStage) {

		this.primaryStage = primaryStage;

		setStageSize();
		intialData();

		// Create Tool Bar
		toolBar = new MyToolBar();
		setActionForToolBar();

		// Create pane for input
		inputPane = createInputPane();

		// Create pane for output
		videoPane = createVideoPane();

		// Create new project
		newProject();		
		BorderPane mainPane = new BorderPane();
		mainPane.setTop(toolBar);
		mainPane.setCenter(videoPane);
		mainPane.setLeft(inputPane);

		Scene scene = new Scene(mainPane);
		this.primaryStage.setScene(scene);
		this.primaryStage.setMaximized(true);
		this.primaryStage.show();
		setKeyBoardShortcuts();

		this.primaryStage.setOnCloseRequest(e ->{

			if(projectStatus==-1){
				boolean check = Data.alertMessageWarning(Data.getMessagenotsaved(),'y');
				if(check)
					save();
			}			
			if(Data.alertMessageWarning(Data.getMessageexit(),'n')){
				Platform.exit();
			}
			else
				e.consume();
		});
	}

	/**
	 * Initial the visemes amount (from Data class) and project status
	 */
	private void intialData(){

		visemesAmount = Data.getVisemesamount();
		projectStatus=0; //	#0 for new project
	}

	/**
	 * Update the stage size by receiving information from primary stage.
	 */
	private void setStageSize(){

		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
		primaryStage.setHeight(primaryScreenBounds.getWidth());
		primaryStage.setWidth(primaryScreenBounds.getHeight());
		primaryStage.widthProperty().addListener(listenerStageResize);
		primaryStage.heightProperty().addListener(listenerStageResize);
	}

	/**
	 * Change the panes size (Listening to resize primary stage)
	 */
	private final ChangeListener<Number> listenerStageResize = new ChangeListener<Number>(){

		@Override
		public void changed(ObservableValue<? extends Number> arg0,
				Number arg1, Number arg2) {
			videoPane.setMaxSize(primaryStage.getWidth()*0.32, primaryStage.getHeight());
			videoPane.setMinSize(primaryStage.getWidth()*0.32, primaryStage.getHeight());
			inputPane.setMaxSize(primaryStage.getWidth()*0.65, primaryStage.getHeight());
			inputPane.setMinSize(primaryStage.getWidth()*0.65, primaryStage.getHeight());
		}
	};

	/**
	 * Set the project name in the title of the primary stage
	 * and in the ToolBar of the system
	 */
	public void setProjectName(String projectName) {

		this.projectName = projectName;
		this.primaryStage.setTitle("Read My Lips - " + projectName
				+ (projectStatus>0 ? "" : "*"));
		toolBar.setTitle(projectName + (projectStatus>0 ? "" : "*"));
	}

	/**
	 * Update primary stage
	 */
	public void updateFrame(){

		updateStatusVisems();
		btnSayIt.setDisable(!statusVisems);
	}

	/**
	 * Update status of the visemes
	 */
	public void updateStatusVisems(){

		for(VisemeFrame viseme : visemePane){
			if(!viseme.isStatus()){
				statusVisems=viseme.isStatus();
				return;
			}
			viseme.updateStatusLabelViseme();
		}
		Text2Phoneme textPhoneme = new Text2PhonemeHebrew(txtInput.getText());
		if(!txtInput.getText().isEmpty() && textPhoneme.isRightText())
			statusVisems=true;
		else
			statusVisems=false;
	}

	/**
	 * Creates the pane
	 *  for input
	 *  @return {@link VBox}
	 */
	private VBox createInputPane() {

		VBox pane = new VBox(0);
		pane.getStylesheets().add("CSS/segmented.css");

		// Label for text input
		Text lblInputText = new Text("Enter text:");
		lblInputText.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		lblInputText.setFill(Color.ORANGERED);
		pane.getChildren().add(lblInputText);
		// Text Area for input Text
		txtInput = new TextArea();
		txtInput.setNodeOrientation(NodeOrientation.LEFT_TO_RIGHT);
		txtInput.setPadding(new Insets(5, 15, 5, 15));
		txtInput.setPrefRowCount(2);
		txtInput.setPrefColumnCount(200);
		txtInput.setWrapText(true);
		txtInput.setPrefWidth(700);
		txtInput.setStyle("-fx-font-size: 30;");
		txtInput.setOnKeyPressed((e) -> {
			//			isSavedForChange = false;
			projectStatus=-1;
			setProjectName(projectName);
			updateFrame();
		});
		pane.getChildren().add(txtInput);

		// Label for text visemes
		Text lblInputVisemes = new Text("Upload visemes:");
		lblInputVisemes.setFont(Font.font("Verdana", FontWeight.BOLD, 20));
		lblInputVisemes.setFill(Color.ORANGERED);
		pane.getChildren().add(lblInputVisemes);

		ToolBar visemeBar = createGridVisemes();
		//		visemeBar.getItems().addAll(createGridVisemes());
		// Grid of visemes

		pane.getChildren().add(visemeBar);

		pane.getStyleClass().addAll("pane", "vbox");

		return pane;
	}

	/**
	 * Creates the grid pane for Visemes
	 * (This pane is a sub-pane of InputPane)
	 *  @return {@link ToolBar}
	 */
	private ToolBar createGridVisemes() {

		ToolBar grid = new ToolBar();
		visemePane = new VisemeFrame[visemesAmount];

		final EventHandler<ActionEvent> eventHandlerSetNotSave = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				projectStatus=-1;
				setProjectName(projectName);
				updateFrame();
			}
		};

		final EventHandler<WindowEvent> eventUpdaeFrame = new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent arg0) {
//				updateFrame();	
			}
		};
		
		int visemesInLine=4;


		
		for (int i=0; i<visemesAmount/visemesInLine; i++) {
			for (int j=0; j<visemesInLine; j++) {
				visemePane[i*visemesInLine+j] = new VisemeFrame(170, 170,(i*visemesInLine+j));
				visemePane[i*visemesInLine+j].setPadding(new Insets(5, 5, 5, 5));
				visemePane[i*visemesInLine+j].setStyle("-fx-background-color: #171e24;");
				visemePane[i*visemesInLine+j].addActionListener(eventHandlerSetNotSave);
				visemePane[i*visemesInLine+j].setOnMouseClicked(t ->  {
					if(t.getClickCount()==2 && 
							!(((VisemeFrame)t.getSource()).getUrl().equals(Data.getEmptyimageurl()))){
						ZoomToViseme zoom = new ZoomToViseme((VisemeFrame)t.getSource(),eventUpdaeFrame);
						zoom.getStage().show();
					}
				});
				grid.getItems().add(visemePane[i*visemesInLine+j]);
				visemePane[i*visemesInLine+j].getBtnAdd().setOnAction(e->{
					checkVisemeSize();
					if(isChangeAfterSaved())
						projectStatus=-1;
				});
			}
		}
		return grid;
	}

	/**
	 * check viseme size
	 * @return
	 */
	public boolean checkVisemeSize(){

		for(VisemeFrame viseme:visemePane){
			if(viseme.getImageSize()!=null){
				for(VisemeFrame viseme2:visemePane){
					if(viseme.compareTo(viseme2)==1){
						System.out.println("ddf---=1=-1");
					}
				}
			}

		}

		return true;
	}

	/**
	 * Creates pane for display
	 *  the film-strip
	 *  @return {@link BorderPane}
	 */
	private BorderPane createVideoPane() {

		BorderPane bPane = new BorderPane();
		bPane.setStyle("-fx-background-color: 	#f8f8ff   ;");
		bPane.getStylesheets().add("CSS/segmented.css");
		bPane.setPadding(new Insets(20, 0, 0, 0));

		// Button "Say it"
		btnSayIt = new Button("Say It!");
		bPane.getStylesheets().add("CSS/button.css");

		btnSayIt.setOnAction(t -> {
			sayIt();
		});

		updateFrame();

		bPane.setTop(btnSayIt);
		bPane.setPadding(new Insets(10,0,0,0));
		BorderPane.setAlignment(btnSayIt, Pos.TOP_CENTER);

		return bPane;
	}

	/**
	 * Create media player 
	 * for output film-strip
	 *  @return {@link MediaControl}
	 */
	private MediaControl setVideoPlayer(File file) {

		Media media = new Media (file.toURI().toString());
		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setAutoPlay(true);
		MediaControl mediaControl = new MediaControl(mediaPlayer, videoWidth*0.4, videoHeight);

		return mediaControl;
	}

	/**
	 * Starts the process 
	 * of the Morph algorithm
	 * Create the frames 
	 * and send them to  class Image2Video
	 */
	public void sayIt() {
		
		new Thread(() ->{
			long time = System.currentTimeMillis();
			System.out.println(time);
			Point[] phonemsRange;
			Character[] phonems;

			Text2Phoneme textPhoneme = new Text2PhonemeHebrew(txtInput.getText());
			if(!textPhoneme.isRightText()){
				Data.alertMessageWarningOnlyOK(Data.getMessagewrongtext());
				return;
			}
			textPhoneme.findPhonemes();
			phonems = textPhoneme.getPhonemes();
			phonemsRange = textPhoneme.getRangePhonemesInText();
			HashMap<String, Image[]> hashMapPhonFrames = new HashMap<String, Image[]>();

			for(int i=0; i<phonems.length-1; i++){
				if(!hashMapPhonFrames.containsKey(phonems[i]+"|"+phonems[i+1])){
					Image[] frames=doMorph(phonems[i],phonems[i+1]);
					hashMapPhonFrames.put(phonems[i]+"|"+phonems[i+1], frames);
				}
			}
			Platform.runLater(new Runnable() {
				@Override
				public void run() {
					System.out.println(">>>> "+(System.currentTimeMillis()-time));
					// Set the video of the frames in ArrayList.
					new Image2Video(hashMapPhonFrames, phonemsRange, phonems, txtInput.getText(), currentDirectory + "\\" + projectName);
					// Add the video to the player. 
					MediaControl madiaPlayer = setVideoPlayer(new File(currentDirectory + "\\" + projectName+"\\Video.mp4"));
					videoPane.setCenter(madiaPlayer);
				}
			});
		}).start();
	}

	/**
	 * Do Morph
	 * @param fromPhonem
	 * @param toPhonem
	 * @return
	 */
	public Image[] doMorph(char fromPhonem, char toPhonem){

		int time;
		Morph morph;
		int fromVisem;
		int toVisem;

		fromVisem = Data.getVisemestype2phoneme().get(fromPhonem);		// get the number of viseme "from" by phoneme
		toVisem = Data.getVisemestype2phoneme().get(toPhonem);		// get the number of viseme "to" by phoneme

		// Get Time between 2 Phonemes from Data class
		time = Data.getDefaulttimeforphonemes();	// Default time 
		if(Data.getPhonemestime().get(Arrays.asList(fromPhonem,toPhonem)) != null)	
			time = Data.getPhonemestime().get(Arrays.asList(fromPhonem,toPhonem));
		else if(Data.getPhonemestime().get(Arrays.asList(toPhonem,fromPhonem)) != null)
			time = Data.getPhonemestime().get(Arrays.asList(toPhonem,fromPhonem));
		else if(Data.getPhonemestime().get(Arrays.asList(fromPhonem,'*')) != null)
			time = Data.getPhonemestime().get(Arrays.asList(fromPhonem,'*'));
		else if(Data.getPhonemestime().get(Arrays.asList(toPhonem,'*')) != null)
			time = Data.getPhonemestime().get(Arrays.asList(toPhonem,'*'));
		System.out.println(time+": "+fromVisem + " -> " + toVisem);

		// Set Morph to the phonemes and get the frames 
		morph = new Morph(visemePane[fromVisem].getImage(),visemePane[toVisem].getImage(),time,
				visemePane[fromVisem].getMap(), visemePane[toVisem].getMap());
		morph.setAllFrames();

		return morph.getFrames(); 
	}
	/**
	 * Save project
	 * @throws IOException
	 */
	public void saveProject() throws IOException {

		projectStatus=1;

		for (int i = 0; i < visemesAmount; i++) {
			if(!visemePane[i].getUrl().equals(Data.getEmptyimageurl()))
				visemePane[i].saveImage(currentDirectory + "\\" + projectName
						+ "\\Visem_" + i + ".jpg");
		}
		FileWriter fw = null;
		try {
			fw = new FileWriter(currentDirectory + "\\" + projectName
					+ "\\input.txt");
			BufferedWriter bw = new BufferedWriter(fw);
			bw.write(txtInput.getText());
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Save as dialog
	 */
	public void saveAsDialog() {

		// get directory
		String defaultDirectory = new JFileChooser().getFileSystemView()
				.getDefaultDirectory().toString();
		File defaultDir = new File(defaultDirectory + "\\ReadMyLips");
		if (!defaultDir.exists())
			defaultDir.mkdirs();
		currentDirectory = defaultDir.toString();

		// Create dialog.
		Alert dialog = new Alert(AlertType.CONFIRMATION);
		dialog.setTitle("Save Project");
		dialog.setHeaderText("Choose your saving preferences");

		// Create fields.
		GridPane grid = new GridPane();
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(20, 150, 10, 10));

		TextField dirTxf = new TextField();
		dirTxf.setDisable(true);
		dirTxf.setPrefWidth(400);
		dirTxf.setText(currentDirectory);

		Button changeDirBtn = new Button("Change directory");
		changeDirBtn.setOnAction(v -> {
			File file = changeDirectory(dirTxf.getText());
			if (file != null)
				dirTxf.setText(file.toPath().toString());
		});

		TextField proNameTxf = new TextField();
		proNameTxf.setText(projectName);

		grid.add(new Label("The project will be saved in"), 0, 0);
		grid.add(dirTxf, 1, 0);
		grid.add(new Label("The project will be saved in"), 0, 0);
		grid.add(changeDirBtn, 2, 0);
		grid.add(new Label("Choose project Name: "), 0, 1);
		grid.add(proNameTxf, 1, 1);

		dialog.getDialogPane().setContent(grid);

		// Set the button types.
		ButtonType saveBtn = new ButtonType("Save", ButtonData.OK_DONE);
		dialog.getButtonTypes().setAll(saveBtn, ButtonType.CANCEL);
		Optional<ButtonType> result = dialog.showAndWait();
		if (result.get() == saveBtn) {
			//			isSavedForChange = true;
			projectStatus=1;
			setProjectName(proNameTxf.getText());
			if (setFolder(this.currentDirectory + "//" + projectName)) {
				try {
					//					projectIsSaved = true;
					saveProject();
					//					isSavedForChange = true;
					projectStatus=1;
					setProjectName(proNameTxf.getText());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * open Dialog
	 * @return
	 */
	public File openDialog() {

		DirectoryChooser fileChooser = new DirectoryChooser();
		fileChooser.setTitle("Open project");
		if (!((currentDirectory).isEmpty()))
			fileChooser.setInitialDirectory(new File(currentDirectory));
		return fileChooser.showDialog(null);
	}

	/**
	 * change Directory
	 * @param url
	 * @return
	 */
	public File changeDirectory(String url) {

		DirectoryChooser chooser = new DirectoryChooser();
		chooser.setTitle("Change Directory");
		if (!url.isEmpty()) {
			File defaultDirectory = new File(url);
			chooser.setInitialDirectory(defaultDirectory);
		}
		return chooser.showDialog(null);
	}

	/**
	 * set Folder
	 * @param file
	 * @return
	 */
	public static boolean setFolder(String file) {

		File f = new File(file);
		try {
			if (!f.exists())
				f.mkdirs();
			f.getCanonicalPath();
			return true;
		} catch (IOException e) {
			return false;
		}
	}

	/**
	 * Open existing project
	 */
	public void openProject() {

		// Open dialog
		File file = openDialog();

		// Project is not selected
		if (file == null)
			return;

		// Save directory
		currentDirectory = file.toString().substring(0,
				file.toString().lastIndexOf("\\"));

		// save project name
		String pName = file.toString().substring(
				file.toString().lastIndexOf("\\") + 1);

		// Change status to (1)->project saved
		projectStatus=1;

		// Set project name in the title
		setProjectName(pName);

		// Reads the text file and add it to the text box
		file = new File(currentDirectory +"\\"+pName + "\\input.txt");
		if (file.exists()) {
			String text;
			StringBuilder stringBuffer = new StringBuilder();
			BufferedReader bufferedReader = null;
			try {
				bufferedReader = new BufferedReader(new FileReader(file));
				while ((text = bufferedReader.readLine()) != null) {
					stringBuffer.append(text);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			txtInput.setText(stringBuffer.toString());
		}

		// Read the visemes and set them in the viseme pane
		for (int i = 0; i < visemesAmount; i++) {
			file = new File(currentDirectory + "\\" + projectName + "\\Visem_"
					+ i + ".jpg");
			if (file.exists()) {
				System.out.println(currentDirectory + "\\" + projectName
						+ "\\Visem_" + i + ".jpg");
				visemePane[i].setImage(currentDirectory + "\\" + projectName
						+ "\\Visem_" + i + ".jpg");
			}
		}
		
		// Reads the video file 
		file = new File(currentDirectory +"\\"+pName+ "\\video.mp4");
		if (file.exists()) {
			// Add the video to the player. 
			MediaControl madiaPlayer = setVideoPlayer(new File(currentDirectory + "\\" + projectName+"\\Video.mp4"));
			videoPane.setCenter(madiaPlayer);
		}

		// Update frame
		updateFrame();
	}

	/**
	 * set Action For ToolBar
	 */
	public void setActionForToolBar() {

		final EventHandler<ActionEvent> eventHandlerNew = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				newProject();
			}
		};

		final EventHandler<ActionEvent> eventHandlerOpen = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				openProject();
			}
		};

		final EventHandler<ActionEvent> eventHandlerSave = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				save();
			}
		};

		final EventHandler<ActionEvent> eventHandlerSaveAs = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				saveAsDialog();
			}
		};

		final EventHandler<ActionEvent> eventHandlerHelp = new EventHandler<ActionEvent>() {

			@Override
			public void handle(ActionEvent event) {
				save();
			}
		};

		toolBar.addActionListener(eventHandlerNew, eventHandlerSave,
				eventHandlerOpen, eventHandlerSaveAs, eventHandlerHelp);
	}

	/**
	 * Save project
	 */
	public void save(){
		try {
			if (projectStatus<1 && !(new File(currentDirectory + "\\" + projectName)).exists())
				saveAsDialog();
			else {
				saveProject();
				setProjectName(projectName);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * set key board shortcuts
	 */
	public void setKeyBoardShortcuts(){

		final KeyCombination keyCombNew=new KeyCodeCombination(KeyCode.N,KeyCombination.CONTROL_DOWN);
		final KeyCombination keyCombSave=new KeyCodeCombination(KeyCode.S,KeyCombination.CONTROL_DOWN);
		final KeyCombination keyCombOpen=new KeyCodeCombination(KeyCode.O,KeyCombination.CONTROL_DOWN);

		// New project keyboard shortcut
		this.primaryStage.getScene().getAccelerators().put(keyCombNew,new Runnable() {

			@Override
			public void run() {
				newProject();
			}
		});

		// Save project keyboard shortcut
		this.primaryStage.getScene().getAccelerators().put(keyCombSave,new Runnable() {

			@Override
			public void run() {
				save();
			}
		});

		// Open project keyboard shortcut
		this.primaryStage.getScene().getAccelerators().put(keyCombOpen,new Runnable() {

			@Override
			public void run() {
				openProject();
			}
		});
	}

	/**
	 * New Project
	 */
	public void newProject() {

		if(projectStatus<0){
			if(Data.alertMessageWarning(Data.getMessagenotsaved(),'y')){
				save();
				return;
			}
		}

		for (int i = 0; i < visemesAmount; i++) {
			visemePane[i].setEmptyImage();
			visemePane[i].updateStatusLabelViseme();
		}

		txtInput.setText("");
		projectName = "Project" + (int) (Math.random() * 100); // Random number 1-1000 for project name
		currentDirectory = new JFileChooser().getFileSystemView()
				.getDefaultDirectory().toString();
		projectStatus=0;
		setProjectName(projectName);
		videoPane.setCenter(null);
		updateFrame();
	}

	/**
	 * Checks if saved after change
	 */
	public boolean isChangeAfterSaved() {
		for (int i = 0; i < visemesAmount; i++) {
			if (visemePane[i].isChangeAfterSaved())
				return true;
		}
		return false;
	}

	/**
	 * @return the primaryStage
	 */
	public Stage getPrimaryStage() {
		return primaryStage;
	}

	/**
	 * @param primaryStage the primaryStage to set
	 */
	public void setPrimaryStage(Stage primaryStage) {
		this.primaryStage = primaryStage;
	}

	/**
	 * main
	 * @param args
	 */
	public static void main(String[] args) {
		launch(args);
	}

}
