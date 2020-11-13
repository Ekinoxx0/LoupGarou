package dev.loupgarou.utils;

/**
 * @author Ekinoxx
 */
public enum CharManager {

	GUIL_RIGHT('»'),
	GUIL_LEFT('«'),
	CHECKED('✅'),
	CROSS('✖'),
	ARROW_RIGHT('→'),
	ARROW_LEFT('←'),
	ARROW_UP('↑'),
	ARROW_DOWN('↓'),
    STAR_FILLED('★'),
    STAR_UNFILL('☆'),
	STAR_ROUND('✪'),
	RELOAD('↻'), 
	LETTER('✉'),
	HEART('❤'),
	RECTANGLE('█'),
    OLD_PHONE('☎'),
    NOTE_QUARTER('♩'),
    NOTE_EIGHT('♪'),
    NOTE_BEAM_EIGHT('♫'),
    NOTE_BEAM_SIXTEEN('♬'),
    WARNING('⚠');

	private char c;

	CharManager(char c){
		this.c = c;
	}
	
	/**
	 * Get char of enum
	 *
	 * @return Char
	 */
	public char getChar(){
		return c;
	}
	
	@Override
	public String toString() {
		return c + "";
	}
	
}