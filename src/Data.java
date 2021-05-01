

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.HashMap;
import java.util.Optional;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceDialog;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.layout.GridPane;
import javafx.util.Pair;

public class Data {

	// Number of viseme
	private final static int visemesAmount = 4;
	// FPS - Frames per second
	private final static int framesForMorph = 37;
	// Number of triangles in one map
	private final static int triangleAmount = 14;
	// Default time between 2 visemes (phoneme)
	private final static int defaultTimeForPhonemes = 500; // Milliseconds
	// Message for exit from the system
	private final static String messageExit = "Do you want to log off?";
	// Message - Not an image format
	private final static String messageNotSaved = "The current project is not saved."
			+ " \n Do you want to save your project?\n";
	// Message - ask to save before exit
	private final static String messageNotImage = "Image cannot be found."
			+ " \n Try again.\n";
	// Message - worng text
	private final static String messageWrongText = "The text you entered isn't enabled.\n"
			+ "Please, try again.";
	// URL to empty viseme - black image
	private final static String emptyImageUrl = "\\image\\visemeExample\\empty.png";
	// All viseme type
	private final static HashMap <Character,Integer> visemesType2Phoneme  = new HashMap <Character,Integer>(){{
		put('s',0);		// Silence
		put('a',1);		// "Fatah"
		put('i',2);		// "Hirik"
		put('o',3);		// "Holam"
	}};
	// Phoneme v.s Nikud
	// Can be more than one Nikud to one phoneme
	private final static HashMap <Character,Character> phonemesNikud  = new HashMap <Character,Character>(){{
		put('Ç','a');
		put('Ä','i');
		put('É','o');
		put(' ','s');
	}};
	// Time between 2 visemes (phoneme) 
	// Character "*" for all phonemes. Times in milliseconds
	private final static HashMap <List<Character>,Integer> phonemesTime = new HashMap <List<Character>,Integer>(){{ 
		put(Arrays.asList('s','*'),Integer.valueOf(250));
		put(Arrays.asList('a','a'),Integer.valueOf(500));
		put(Arrays.asList('a','i'),Integer.valueOf(500));
		put(Arrays.asList('a','o'),Integer.valueOf(500));
		put(Arrays.asList('i','o'),Integer.valueOf(500));
	}};
	// Extension Filter of Image
	private final static String[] extensionImage={"*.TIF","*.JPG","*.PNG","*.GIF","*.JEPG"};
	
	
	/**
	 * alert Message - Warning with 2 options: Yes. No 
	 * @return boolean: TRUE for 'YES', and FALSE for 'No'
	 * @param alertContent - String of the message
	 * @param defaultButton - 'y' for 'YES' button as a default, 'n' for 'No'
	 */
	static public void alertMessageWarningOnlyOK(String alertContent){

		Alert alert = new Alert(AlertType.ERROR);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Error");
		alert.setContentText(alertContent);
		
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.OK);
		alert.showAndWait();
	}
	
	/**
	 * alert Message - Warning with 2 options: Yes. No 
	 * @return boolean: TRUE for 'YES', and FALSE for 'No'
	 * @param alertContent - String of the message
	 * @param defaultButton - 'y' for 'YES' button as a default, 'n' for 'No'
	 */
	static public boolean alertMessageWarning(String alertContent, char defaultButton){

		Alert alert = new Alert(AlertType.CONFIRMATION);
		alert.setTitle("Confirmation Dialog");
		alert.setHeaderText("Are you sure?");
		alert.setContentText(alertContent);
		
		alert.getButtonTypes().clear();
		alert.getButtonTypes().addAll(ButtonType.YES, ButtonType.NO);
		
		defaultButton=Character.toLowerCase(defaultButton);
		
		Button yesButton = (Button) alert.getDialogPane().lookupButton(ButtonType.YES);
	    yesButton.setDefaultButton(defaultButton=='y');
	    
	    Button noButton = (Button) alert.getDialogPane().lookupButton(ButtonType.NO);
	    noButton.setDefaultButton(defaultButton=='n');
	    
	    final  Optional<ButtonType> result = alert.showAndWait();
		
	    return (result.get() == ButtonType.YES);
	}
	
	/**
	 * @return the messagenotimage
	 */
	public static String getMessagenotimage() {
		return messageNotImage;
	}
	
	/**
	 * @return the messagewrongtext
	 */
	public static String getMessagewrongtext() {
		return messageWrongText;
	}

	/**
	 * @return the extensionimage
	 */
	public static String[] getExtensionimage() {
		return extensionImage;
	}
	
	/**
	 * @return String: URL to empty viseme - black image
	 */
	public static String getEmptyimageurl() {
		return emptyImageUrl;
	}
	
	/**
	 * @return the int: FPS - Frames per second
	 */
	public static int getFramesformorph() {
		return framesForMorph;
	}

	/**
	 * @return int: Default time between 2 visemes (phoneme)
	 */
	public static int getDefaulttimeforphonemes() {
		return defaultTimeForPhonemes;
	}

	/**
	 * @return int: FPS - Frames per second
	 */
	public static int getFramesForMorph() {
		return framesForMorph;
	}

	/**
	 * @return the phonemestime
	 */
	public static HashMap<List<Character>, Integer> getPhonemestime() {
		return phonemesTime;
	}

	/**
	 * @return the triangleamount
	 */
	public static int getTriangleamount() {
		return triangleAmount;
	}

	/**
	 * @return int: Number of viseme
	 */
	public static int getVisemesamount() {
		return visemesAmount;
	}


	/**
	 * @return HashMap<Character,Integer>: all visemes type and their phoneme  
	 */
	public static HashMap<Character,Integer> getVisemestype2phoneme() {
		return visemesType2Phoneme;
	}


	/**
	 * @return HashMap<Character, Character>: Nikud and their phoneme  
	 */
	public static HashMap<Character, Character> getPhonemesnikud() {
		return phonemesNikud;
	}

	/**
	 * @return String: Message for exit from the system
	 */
	public static String getMessageexit() {
		return messageExit;
	}

	/**
	 * @return String: Message - ask to save before exit
	 */
	public static String getMessagenotsaved() {
		return messageNotSaved;
	}
}
