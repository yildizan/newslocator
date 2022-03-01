package com.yildizan.newsfrom.locator.utility;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import com.yildizan.newsfrom.locator.entity.Language;

public final class StringUtils {

	private static final List<Character> punctuations = Arrays.asList('.', ',', ';', ':', '!', '?', '\"');
	
	private StringUtils() {}
	
	public static String cleanCode(String string) {
		String cleanString = string.trim();
		String codeRegex = "<[^>]*>";
		
		// extract from cdata
		cleanString = cleanString.startsWith("<![CDATA[") ? cleanString.substring("<![CDATA[".length(), cleanString.indexOf("]]>")) : cleanString;
				
		// clean html
		cleanString = Pattern.compile(".*" + codeRegex + ".*").matcher(cleanString).find() ? cleanString.replaceAll(codeRegex, "") : cleanString;
		
		// clean sputnik prefix
		cleanString = cleanString.contains("(Sputnik) - ") ? cleanString.substring(cleanString.indexOf("(Sputnik) - ") + "(Sputnik) - ".length()) : cleanString;
		return cleanString;
	}

	public static String cleanSuffix(String string, int language) {
		String cleanString = string
				.replace('`', '\'')
				.replace('’', '\'')
				.replace('“', '\"');
		while(endsWithPunctuation(cleanString)) {
			cleanString = cleanString.substring(0, cleanString.length() - 1);
		}
		cleanString = endsWithPossessive(cleanString, language) ? cleanString.substring(0, cleanString.lastIndexOf('\'')) : cleanString;
		return cleanString;
	}
	
	public static boolean startsWithUppercase(String string) {
		return Character.isUpperCase(string.charAt(0));
	}
	
	public static boolean endsWithPunctuation(String string) {
		return punctuations.contains(string.charAt(string.length() - 1)) && string.length() > 1 && !Character.isUpperCase(string.charAt(string.length() - 2));
	}
	
	public static boolean endsWithPossessive(String string, int language) {
		if(language == Language.ENGLISH) {
			return string.endsWith("'s") || string.endsWith("s'");
		}
		else if(language == Language.TURKISH) {
			return endsWithPunctuation(string);
		}
		else {
			throw new UnsupportedOperationException();
		}
	}

	public static String wrapWith(String string, String wrapper) {
		return wrapper + string + wrapper;
	}

}
