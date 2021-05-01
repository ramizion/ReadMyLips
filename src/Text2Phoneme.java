

import java.awt.Point;
import java.util.HashMap;
import java.util.LinkedList;

public interface  Text2Phoneme {
	
	/**
	 * Set the phonemes of the language
	 * @param phonemesNikud
	 */
	public abstract  void setPhonemesNikud(HashMap<Character,Character> phonemesNikud);

	/**
	 * Find the phonemes of the text and saves in:
	 * (LinkedList<Character>) phonemes
	 * and the range in: (LinkedList<Point>) rangePhonemesInText 	
	 */
	public abstract  void        findPhonemes();
	
	/**
	 * Checks for right text in  language
	 * @return boolean
	 */
	public abstract  boolean     isRightText();
	
	/**
	 * return the phonemes
	 * @return Character
	 */
	public abstract  Character[] getPhonemes();
	
	/**
	 * return the range of all phonemes in the text for the subtitle
	 * @return Point[]
	 */
	public abstract  Point[] 	 getRangePhonemesInText();
	
	/**
	 * 	return the sentence
	 * @return String
	 */
	public abstract  String getSentence();
}
