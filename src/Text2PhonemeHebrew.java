

import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;

import javax.swing.text.StyledEditorKit.BoldAction;

import com.sun.javafx.collections.MappingChange.Map;

public class Text2PhonemeHebrew implements  Text2Phoneme{

	String sentence;
	HashMap <Character,Character> phonemesNikud;
	LinkedList<Character> phonemes;		// Example:  /a/,/i/
	LinkedList<Point> rangePhonemesInText;	// Example: text "Rami" -> (0,1),(2,3)



	Text2PhonemeHebrew(String sentence){
		this.phonemesNikud = Data.getPhonemesnikud();
		this.rangePhonemesInText = new LinkedList<Point>();
		this.phonemes = new LinkedList<Character>();
		this.sentence = sentence;
//		//		setPhonemesNikud();
//		findPhonemes();
	}

	@ Override
	public void setPhonemesNikud(HashMap<Character,Character> phonemesNikud){
		this.phonemesNikud=phonemesNikud;
	}
	@ Override
	public String getSentence(){
		return sentence;
	}
	
	/**
	 * set the senctence
	 * @param sentence
	 */
	public void setSenctence(String sentence){
		this.sentence = sentence;
		findPhonemes();
	}

	/**
	 * @return the phonemes
	 */
	@Override
	public Character[] getPhonemes(){

		return phonemes.toArray(new Character[phonemes.size()]);
	}

	/**
	 * @return the rangePhonemesInText
	 */
	@Override
	public Point[] getRangePhonemesInText() {
		return rangePhonemesInText.toArray(new Point[rangePhonemesInText.size()]);
	}

	@Override
	public void findPhonemes(){

		final char[] sentenceChars = sentence.toCharArray();
		LinkedList<Character> sentenceCharsVowel = new LinkedList<Character>();
		rangePhonemesInText.clear();
		
		for (int i=0; i<sentenceChars.length;i++){
			
			if(i==0){// Start with 2 'silent'
				rangePhonemesInText.add(new Point(0,0));
				rangePhonemesInText.add(new Point(0,0));
			}
			if(sentenceChars[i]==' '){
				rangePhonemesInText.add(new Point(i,i));
				sentenceCharsVowel.add(sentenceChars[i]);
			}
			else if(i==sentenceChars.length-1 && isAVowelLetters(sentenceChars[i]))
				rangePhonemesInText.getLast().y=i;				
			else if(i!=sentenceChars.length-1 && isAVowelLetters(sentenceChars[i],sentenceChars[i+1]))
				rangePhonemesInText.getLast().y=i;
			else if(i!=sentenceChars.length-1 && isVowelIncludeNikud(sentenceChars[i],sentenceChars[i+1])){
				rangePhonemesInText.add(new Point(i,i+1));
				sentenceCharsVowel.add(sentenceChars[i+1]);
				i++;
			}
		}

		phonemes.clear();
		addToPhonemesList(' ',' '); // Start with 2 'silent'
		
		for (final char letter: sentenceCharsVowel){
			if(letter == ' ' || ((int)letter > 1456 && (int)letter < 1469)) {// Ascii of Nikud Hebrew
				if(letter!=' ' && phonemes.get(phonemes.size()-1)==letter){
					addToPhonemesList(' ');
				}
				addToPhonemesList(letter);
			}
		}
		addToPhonemesList(' '); // End with 'silence' 
	}

	/**
	 * Vowel letters (аде"й without Nikud)
	 * @param c1
	 * @param c2
	 * @return boolean
	 */
	private boolean isAVowelLetters(char c1,char c2){
		if(c1== 'а' || c1 == 'д' || c1 == 'е' || c1 == 'й')
			if((c2>'а' && c2<'ъ')||c2==' ')
				return true;
		return false;
	}

	/**
	 * Vowel letters (аде"й without Nikud)
	 * @param c1
	 * @return boolean
	 */
	private boolean isAVowelLetters(char c1){
		if(c1== 'а' || c1 == 'д' || c1 == 'е' || c1 == 'й')
			return true;
		return false;
	}

	/**
	 * Vowel and nikud after him
	 * @param c1
	 * @param c2
	 * @return
	 */
	private boolean isVowelIncludeNikud(char c1, char c2){
		if(c1>'а' && c1<'ъ')
			if((int)c2 > 1456 && (int)c2 < 1469)
			return true;
		return false;
	}

	/**
	 * Add phoneme to the lists: phonemes, stringPhonemes
	 * @param chars
	 */
	private void addToPhonemesList(char... chars) {

		for(char c:chars)
			phonemes.add(phonemesNikud.get(c));
	}

	/**
	 * Checks if the text is  right
	 * @return {@link Boolean}
	 */
	@Override
	public boolean isRightText(){

		final char[] sentenceChars = sentence.toCharArray();

		for (final char letter: sentenceChars){
			if(!(letter<='ъ' && letter>='а'))		// Not a Hebrew letter
				if(!charIsNikud(letter))			// Not a Hebrew Nikud
					return false;
		}
		return true;
	}

	/**
	 * Checks if the letter is a Nikud
	 * @return {@link Boolean}
	 */
	public boolean charIsNikud(char letter){

		Iterator<Entry<Character, Character>> it;
		it = phonemesNikud.entrySet().iterator();

		while(it.hasNext()){
			if(letter==it.next().getKey()){
				return true;
			}
		}
		return false;
	}
}
