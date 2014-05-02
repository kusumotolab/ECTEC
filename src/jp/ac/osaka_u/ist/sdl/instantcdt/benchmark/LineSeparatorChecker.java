package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;

public class LineSeparatorChecker {
	
	private static final String SEPARATOR = System.getProperty("line.separator");
	
	public static String getLineAt(String str, int lineNum) {
		int index = 0;
		
		String[] splitedLines = str.split(SEPARATOR);
		
		if (lineNum == 1) {
			String result = splitedLines[0];
			if (result.length() < SEPARATOR.length()) {
				return "";
			}
			return result.substring(0, result.length() - SEPARATOR.length() + 1);
		}
		
		if (lineNum > splitedLines.length) {
			return "";
		}
		
		int lineCount = 1;
		while ((index = str.indexOf(SEPARATOR, index)) != -1) {
			if (lineCount == lineNum) {
				break;
			}
			lineCount++;
			index++;
		}
		
		String result = splitedLines[lineCount - 1];
		if (result.length() < SEPARATOR.length()) {
			return "";
		}
		return result.substring(0, result.length() - SEPARATOR.length() + 1);
	}
	
	public static int getIndexOfLineStart(String str, int lineNum) {
		StringBuilder builder = new StringBuilder(str);
		return getIndexOfLineStart(builder, lineNum);
	}

	public static int getIndexOfLineStart(StringBuilder builder, int lineNum) {
		int index = 0;
		
		if (lineNum == 1) {
			return index;
		}
		
		int lineCount = 1;
		while ((index = builder.indexOf(SEPARATOR, index)) != -1) {
			if (lineCount == lineNum - 1) break;
			lineCount++;
			index++;
		}
		if (index == -1) {
			return builder.length();
		}
		return index + SEPARATOR.length();
	}
	
	public static int getIndexOfLineEnd(String str, int lineNum) {
		StringBuilder builder = new StringBuilder(str);
		return getIndexOfLineEnd(builder, lineNum);
	}
	
	public static int getIndexOfLineEnd(StringBuilder builder, int lineNum) {
		int index = 0;
		
		int lineCount = 1;
		while ((index = builder.indexOf(SEPARATOR, index)) != -1) {
			if (lineCount == lineNum) break;
			lineCount++;
			index++;
		}
		if (index == -1) {
			return builder.length();
		}
		return index;
	}
	
}
