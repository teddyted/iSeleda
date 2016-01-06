package pixelitor;

import java.util.ArrayList;

public class WordDict {
	
	private String Word_AR;
	private String Word_EN;
	private String File; 
	private ArrayList<String> Polysemy = new ArrayList<String>();
	private String Character;
	
	public WordDict(){
		
	}

	public String getWord_AR() {
		return Word_AR;
	}

	public void setWord_AR(String word_AR) {
		Word_AR = word_AR;
	}

	public String getWord_EN() {
		return Word_EN;
	}

	public void setWord_EN(String word_EN) {
		Word_EN = word_EN;
	}

	public String getFile() {
		return File;
	}

	public void setFile(String file) {
		File = file;
	}

	public ArrayList<String> getPolysemy() {
		return Polysemy;
	}

	public void setPolysemy(ArrayList<String> polysemy) {
		Polysemy = polysemy;
	}

	public String getCharacter() {
		return Character;
	}

	public void setCharacter(String character) {
		Character = character;
	}
	
	public void addPolysemy(String poly){
		Polysemy.add(poly);
	}

}
