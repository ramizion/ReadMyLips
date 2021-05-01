

import java.awt.event.ActionListener;
import java.util.Collection;

import javax.swing.text.html.HTMLDocument.RunElement;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.effect.InnerShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCodeCombination;
import javafx.scene.input.KeyCombination;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontSmoothingType;
import javafx.scene.text.Text;


public class MyToolBar extends ToolBar {

	private Button btnNew;
	private Button btnOpen;
	private Button btnSave;
	private Button btnSaveAs;
	private Button btnHelp;
	private Text title;

	MyToolBar(){

		btnNew = addButton("New","newProject");
		btnNew.setTooltip(new Tooltip("Creating a new project."));

		
		btnOpen = addButton("Open","openProject");
		btnOpen.setTooltip(new Tooltip("Open existing project."));
		
		btnSave = addButton("Save","saveProject");
		btnSave.setTooltip(new Tooltip("Save your project."));
		
		btnSaveAs = addButton("Save As","saveAsProject");
		btnSaveAs.setTooltip(new Tooltip("Save your project and set the name of the project."));
		
		btnHelp = addButton("Help","help");
		btnHelp.setTooltip(new Tooltip("Open help window."));
		
		title = new Text();
		title.setFont(Font.font ("Times New Roman", 30));
		title.setFill(Color.WHITE);
		title.setFontSmoothingType(FontSmoothingType.LCD);

		getItems().addAll(btnNew, btnOpen, btnSave, btnSaveAs,btnHelp ,title);
		setPrefWidth(100);
		getStylesheets().add("CSS/segmented.css");
	}

	public void setTitle(String title){
		this.title.setText(title);
	}

	private Button addButton(String buttomName, String image) {

		Button btn = new Button(buttomName, new ImageView(new Image(image+".jpg")));
		setStyle("-fx-font: 20 Narkisim; -fx-base: #b6e7c9;");
		return btn;
	}

	private Button addButton(String buttomName) {

		Button btn = new Button(buttomName);
		setStyle("-fx-font: 20 Narkisim; -fx-base: #b6e7c9;");
		return btn;
	}
	
	public void addActionListener(EventHandler<ActionEvent> eventHandlerNew,
			EventHandler<ActionEvent> eventHandlerSave, EventHandler<ActionEvent> eventHandlerOpen,
			EventHandler<ActionEvent> eventHandlerSaveAs, EventHandler<ActionEvent> eventHandlerHelp){
	
		btnNew.setOnAction(eventHandlerNew);
		btnSave.setOnAction(eventHandlerSave);
		btnSaveAs.setOnAction(eventHandlerSaveAs);
		btnOpen.setOnAction(eventHandlerOpen);
		btnHelp.setOnAction(eventHandlerHelp);
	}

}
