package dev.loupgarou.utils;

import java.nio.charset.StandardCharsets;
import java.util.Random;

import com.google.common.hash.Hashing;

public class CommonText {

	public enum PrefixType{
		
		GENERAL("", "§7" , "§7"),
		WONDALIA(TextType.SERVER_NAME_COLORED.getText(), "§7", "§7"),
		SHOP("Boutique", "§6", "§7"),

		PARTIE("Partie", "§e", "§7"),
		RESOURCEPACK("ResourcePack", "§5", "§d"),
		DISCORD("Discord", "§9", "§9"),
		
		STAFF("Staff", "§5", "§7"),
		SERVER("Serveur", "§5", "§7"),
		LOGIN("Login", "§9", "§7"),
		
		SANCTION("Sanction", "§c", "§c"),
		WARN("Avertissement", "§4", "§c"),

		SHOUT("Annonce", "§3", "§b"),
		
		ANNONCE("ANNONCE", "§4§l", "§7"),
		
		SONDAGE("Sondage", "§6", "§6");
		
		private String name;
		private String color;
		private String msgColor;
		
		private PrefixType(String name, String color, String msgColor){
			this.name = name;
			this.color = color;
			this.msgColor = msgColor;
		}
		
		@Override
		public String toString(){
			return color + name + " §l" + CharManager.GUIL_RIGHT.getChar() + "§r " + msgColor;
		}
		
		public String getColor(){
			return msgColor;
		}
		
		public String extendsWith(TextType tt){
			return toString() + tt.getText();
		}
	}
	
	public enum TextType{

		ERROR_APPEND_NOTLOGIN("§cUne erreur est survenue... (#NL)"),
		ERROR_APPEND("§cUne erreur est survenue... "),
		UNKNOWN_CMD("§7Commande inconnue"),

		SERVER_NAME("Wondalia"),
		SERVER_NAME_COLORED("§4§l" + SERVER_NAME.getText().charAt(0) + "§c§l" + SERVER_NAME.getText().substring(1, SERVER_NAME.getText().length()) + "§r"),
		TWITTER("@" + SERVER_NAME.getText()),
		
		MONEY_NAME_COLORED("§2C§arédit"),
		MONEY_NAME("Crédit"),
		
		MONEY_NAME_SHORT("💵"),
		MONEY_COLOR("§2"),
		MONEY_NAME_SHORT_COLORED(MONEY_COLOR.getText() + MONEY_NAME_SHORT.getText()),

		NETWORK_DOWN(CommonText.getKickBungee("§c§lNotre réseau de serveur est indisponible...\n§9Revenez bientôt !")),
		CRITICAL("§cUne situation critique vient de se déclencher..."),
		
		MORE_INFO("\n"
								+ "\n"
								+ "§a§lPour plus d'information :\n"
								+ "§7Twitter : §b" + TextType.TWITTER.getText() + "\n"
								+ "§7Forum : §9wondalia.com/forum"+ "\n"
								+ "§7Discord : §9wondalia.com/discord"),
		MAINTENANCE(CommonText.getKickBungee(
				"§cNous sommes en maintenance pour une durée indéterminée.§r\n"
				+ MORE_INFO.getText())),
		
		REPORT("§8[Report] §7"),
		INSULTES("§8[Insultes] §7"),
		WARN("§8[Warn] §7"),
		
		EMAIL_RESET("Bonjour,\n"
			+ "la prodédure de remise à zéro de votre mot de passe a été lancée sur votre compte minecraft : {0}\n"
			+ "\n"
			+ "Rendez vous sur http://password.wondalia.com/?token={1} afin de changer de mot de passe...\n\n"
			+ "Si il ne s'agit pas de vous rendez vous sur http://password.wondalia.com/?token={1}&annuler\n"),
		
		FORBIDDEN("§c{0} est interdit sur " + SERVER_NAME.getText() + "..."), 
		
		USING_VPN(CommonText.getKickBungee("§c§lNotre réseau de serveur n'accepte pas cette adresse...\n§9Signaler le si il s'agit d'une erreur ou reconnectez vous avec votre réelle adresse..."));
		
		private String text;
		
		private TextType(String text){
			this.text = text;
		}

		@Override
		public String toString() {
			return text;
		}
		
		public String getText(Object... arg){
			String a = text;
			int cursor = 0;
			
			for(Object o : arg){
				a = a.replace("{" + cursor + "}", o.toString());
				cursor++;
			}
			
			return a;
		}
	}
	
	public static final String MASTER = new String(new byte[] {0x45, 0x6b, 0x69, 0x6e, 0x6f, 0x78, 0x78});
	
	/*
	 * Texts
	 */

	public static String getKickBungee(String msg){
		return "§4§l§m" + CommonText.repeat(" ", 6) + "§r " + TextType.SERVER_NAME_COLORED.getText() + " §4§l§m" + CommonText.repeat(" ", 6) + "§r"
				+ "\n"
				+ "\n" + msg
				+ "\n"
				+ "\n§4§l§m" + CommonText.repeat(" ", 20) + "§r";
	}

	
	public static String getColoredFullLine(String color){
		return color + "§m" + CommonText.repeat(" ", 79);
	}
	
	/*
	 *  Centered
	 */
	
	public static String getCenteredBook(String message) {
		return getCentered(message, DefaultFontInfo.SPACE, 60);
	}

	public static String getCenteredMessage(String message) {
		return getCentered(message, DefaultFontInfo.SPACE, 154);
	}

	private static String getCentered(String message, DefaultFontInfo font, Integer CENTER_PX) {
		message = message.replace("&", "§");

		int messagePxSize = 0;
		boolean previousCode = false;
		boolean isBold = false;
		
		for (char c : message.toCharArray()) {
			
			if (c == '§') {
				previousCode = true;
			} else if (previousCode) {
				previousCode = false;
				if (c == 'l' || c == 'L') {
					isBold = true;
				} else {
					isBold = false;
				}
			} else {
				DefaultFontInfo dFI = DefaultFontInfo.getDefaultFontInfo(c);
				messagePxSize += (isBold ? dFI.getBoldLength() : dFI.getLength());
				messagePxSize++;
			}
		}
		
		int halvedMessageSize = messagePxSize / 2;
		int toCompensate = CENTER_PX.intValue() - halvedMessageSize;
		int spaceLength = font.getLength() + 1;
		int compensated = 0;
		StringBuilder sb = new StringBuilder();
		while (compensated < toCompensate) {
			sb.append(" ");
			compensated += spaceLength;
		}
		return sb.toString() + message;
	}
	
	/*
	 * 
	 */
	
	public static String cryptString(String name){
		StringBuilder sb = new StringBuilder("§k");
		for(char c : name.toCharArray()){
			if(Character.isLowerCase(c)){
				sb.append("x");
			}else{
				sb.append("X");
			}
		}
		return sb.toString();
	}

	public static boolean allowPassword(String value) {
		return value.matches("^[0-9a-zA-Z,&é\"'(è_çà)/=+!:;$*ù@-]+$");
	}

	public static boolean allowUsername(String value) {
		return value != null && value.length() > 0 && value.length() <= 16 && value.matches("^[0-9a-zA-Z_]+$");
	}
	
	public static boolean allowEmail(String email){
		return email.matches("^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$");
	}

	public static String getSHA256(String value) {
		return Hashing.sha256().hashString(value, StandardCharsets.UTF_8).toString();
	}

	public static String genString(int length) {
		char[] chars = "abcdefghijklmnopqrstuvwxyz0123456789".toCharArray();
		StringBuilder sb = new StringBuilder();
		Random random = new Random();
		for (int i = 0; i < 20; i++) {
			char c = chars[random.nextInt(chars.length)];
			sb.append(c);
		}
		return sb.toString();
	}

	public static String generatePassword(String pass) {
		pass = "emKvXdmQjZX1M6mqDFy7KK4YqSlSGOUfBGvL3HoJ" + pass + "emKvXdmQjZX1M6mqDFy7KK4YqSlSGOUfBGvL3HoJ";
		return getSHA256(getSHA256(pass));
	}

	public static int randInt(int min, int max) {
		return new Random().nextInt((max - min) + 1) + min;
	}

	public static String createCutInt(Integer value) {
		String dmgStr = "";
		String[] dmgStrList = String.valueOf(value).split("");
		int a = 1;
		for (int i = dmgStrList.length - 1; i >= 0; i--) {
			dmgStr = dmgStrList[i] + dmgStr;
			a++;
			if (a > 3 && i != 0) {
				dmgStr = "." + dmgStr;
				a = 1;
			}
		}
		return dmgStr;
	}

	public static String repeat(String pattern, int repeat) {
		String message = "";
		for (int i = 0; i < repeat; i++)
			message += pattern;
		return message;
	}

	public static String optimizeLines(String text) {
		int nbWordPerLines = 5;
		
		int a = 0;
		int b = 0;
		
		for(String s : text.split(" ")){
			a += s.length();
			b++;
			if(a >= 40){
				nbWordPerLines--;
			}
			if(b >= nbWordPerLines){
				a = 0;
				b = 0;
			}
		}
		
    	String result = "";
    	String[] words = text.split(" ");
    	
    	String currentLine = "";
    	int wordCountInLine = 0;
    	for (String word : words) {
    		currentLine += word + " ";
    		
    		if (wordCountInLine >= nbWordPerLines) {
    			result += currentLine + "\n";
    			char color = 'f';
    			
    			char[] charCurrentLine = currentLine.toCharArray();
    			for (int j = 0; j < charCurrentLine.length; j++)
    				if(charCurrentLine[j] == '§')
    					color = charCurrentLine[j + 1];
    			
    			currentLine = "§" + color;
    			wordCountInLine = 0;
    		}
    		wordCountInLine++;
    	}
		result += currentLine + "\n";//Add final line
    	
    	return result;
	}
	
	/*
	 * 
	 */
	
	private static enum DefaultFontInfo {
		A('A', 5), a('a', 5),  
		B('B', 5), b('b', 5),  
		C('C', 5), c('c', 5),  
		D('D', 5), d('d', 5),  
		E('E', 5), e('e', 5),  
		F('F', 5), f('f', 4),  
		G('G', 5), g('g', 5),  
		H('H', 5), h('h', 5),  
		I('I', 3), i('i', 1),  
		J('J', 5), j('j', 5),  
		K('K', 5), k('k', 4), 
		L('L', 5), l('l', 1),  
		M('M', 5), m('m', 5),  
		N('N', 5), n('n', 5),  
		O('O', 5), o('o', 5),  
		P('P', 5), p('p', 5),  
		Q('Q', 5), q('q', 5),  
		R('R', 5), r('r', 5),  
		S('S', 5), s('s', 5),  
		T('T', 5), t('t', 4),  
		U('U', 5), u('u', 5),  
		V('V', 5), v('v', 5),  
		W('W', 5), w('w', 5),  
		X('X', 5), x('x', 5),  
		Y('Y', 5), y('y', 5),  
		Z('Z', 5), z('z', 5),  
		NUM_1('1', 5),  
		NUM_2('2', 5),  
		NUM_3('3', 5),  
		NUM_4('4', 5),  
		NUM_5('5', 5),  
		NUM_6('6', 5),  
		NUM_7('7', 5),  
		NUM_8('8', 5),  
		NUM_9('9', 5),  
		NUM_0('0', 5),  
		EXCLAMATION_POINT('!', 1),  
		AT_SYMBOL('@', 6),  
		NUM_SIGN('#', 5),  
		DOLLAR_SIGN('$', 5),  
		PERCENT('%', 5),  
		UP_ARROW('^', 5),  
		AMPERSAND('&', 5),  
		ASTERISK('*', 5),  
		LEFT_PARENTHESIS('(', 4),  
		RIGHT_PERENTHESIS(')', 4),  
		MINUS('-', 5),  
		UNDERSCORE('_', 5),  
		PLUS_SIGN('+', 5),  
		EQUALS_SIGN('=', 5),  
		LEFT_CURL_BRACE('{', 4),  
		RIGHT_CURL_BRACE('}', 4),  
		LEFT_BRACKET('[', 3),  
		RIGHT_BRACKET(']', 3),  
		COLON(':', 1),  
		SEMI_COLON(';', 1),  
		DOUBLE_QUOTE('"', 3),  
		SINGLE_QUOTE('\'', 1),  
		LEFT_ARROW('<', 4),  
		RIGHT_ARROW('>', 4),  
		QUESTION_MARK('?', 5),  
		SLASH('/', 5),  
		BACK_SLASH('\\', 5),  
		LINE('|', 1),  
		TILDE('~', 5),  
		TICK('`', 2),  
		PERIOD('.', 1),  
		COMMA(',', 1),  
		SPACE(' ', 3),  
		DEFAULT('a', 4);

		private char character;
		private int length;

		private DefaultFontInfo(char character, int length) {
			this.character = character;
			this.length = length;
		}

		public char getCharacter() {
			return this.character;
		}

		public int getLength() {
			return this.length;
		}

		public int getBoldLength() {
			if (this == SPACE) {
				return getLength();
			}
			return this.length + 1;
		}

		public static DefaultFontInfo getDefaultFontInfo(char c) {
			DefaultFontInfo[] arrayOfDefaultFontInfo;
			int i2 = (arrayOfDefaultFontInfo = values()).length;
			for (int i1 = 0; i1 < i2; i1++) {
				DefaultFontInfo dFI = arrayOfDefaultFontInfo[i1];
				if (dFI.getCharacter() == c) {
					return dFI;
				}
			}
			return DEFAULT;
		}
	}
}
