package com.yildizan.newsfrom.locator.utility;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StringUtils {

	private static final List<Character> punctuations = Arrays.asList('.', ',', ';', ':', '!', '?', '\"');
	
	public static String cleanCode(String string) {
		String cleanString = string.trim();
		String codeRegex = "<[^>]*>";
		
		// extract from cdata
		cleanString = cleanString.startsWith("<![CDATA[") ? cleanString.substring("<![CDATA[".length(), cleanString.indexOf("]]>")) : cleanString;
				
		// clean html
		cleanString = Pattern.compile(".*" + codeRegex + ".*").matcher(cleanString).find() ? cleanString.replaceAll(codeRegex, "") : cleanString;
		
		// clean sputnik prefix
		cleanString = cleanString.contains("(Sputnik) - ") ? cleanString.substring(cleanString.indexOf("(Sputnik) - ") + "(Sputnik) - ".length()) : cleanString;

		// clean buzzfeed suffix
		cleanString = cleanString.contains("View Entire Post") ? cleanString.substring(0, cleanString.indexOf("View Entire Post")) : cleanString;

		return cleanString;
	}

	public static String cleanSuffix(String string) {
		String cleanString = string
				.replace('`', '\'')
				.replace('’', '\'')
				.replace('“', '\"');
		while(endsWithPunctuation(cleanString)) {
			cleanString = cleanString.substring(0, cleanString.length() - 1);
		}
		cleanString = endsWithPossessive(cleanString) ? cleanString.substring(0, cleanString.lastIndexOf('\'')) : cleanString;
		return cleanString;
	}
	
	public static boolean startsWithUppercase(String string) {
		return Character.isUpperCase(string.charAt(0));
	}
	
	public static boolean endsWithPunctuation(String string) {
		return punctuations.contains(string.charAt(string.length() - 1)) && string.length() > 1 && !Character.isUpperCase(string.charAt(string.length() - 2));
	}
	
	public static boolean endsWithPossessive(String string) {
		return string.endsWith("'s") || string.endsWith("s'");
	}

	public static String wrap(String string, String wrapper) {
		return wrapper + string + wrapper;
	}

	public static String emptyString() {
		return "";
	}

	public static String[] splitBySpace(String string) {
		return string.split("\\s+");
	}

	public static boolean isEmpty(String string) {
		return Objects.isNull(string) || string.isEmpty();
	}

	public static boolean isNotEmpty(String string) {
		return !isEmpty(string);
	}

}
