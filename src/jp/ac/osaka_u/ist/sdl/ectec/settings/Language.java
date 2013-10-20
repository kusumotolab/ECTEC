package jp.ac.osaka_u.ist.sdl.ectec.settings;

import java.io.InputStream;

import jp.ac.osaka_u.ist.sdl.ectec.cdt.CPPLexer;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.JavaLexer;
import jp.ac.osaka_u.ist.sdl.ectec.cdt.Lexer;

/**
 * An enum that represents target programming languages
 * 
 * @author k-hotta
 * 
 */
public enum Language {

	JAVA("java", new String[] { ".java" }),

	CPP("cpp", new String[] { ".c", ".cpp", ".h", "hpp" }),

	OTHER("n/a", new String[] {});

	/**
	 * the string representation of this language
	 */
	private final String str;

	/**
	 * the suffixes for this language
	 */
	private final String[] suffixes;

	private Language(final String str, final String[] suffixes) {
		this.str = str;
		this.suffixes = suffixes;
	}

	public final String getStr() {
		return str;
	}

	/**
	 * get the corresponding language for the given string
	 * 
	 * @param str
	 * @return
	 */
	public static final Language getCorrespondingLanguage(final String str) {
		if (str.equalsIgnoreCase(JAVA.getStr())) {
			return JAVA;
		} else {
			return OTHER;
		}
	}

	/**
	 * check whether the specified file is a target source file
	 * 
	 * @param fileName
	 * @return
	 */
	public boolean isTarget(final String fileName) {
		for (final String suffix : suffixes) {
			if (fileName.endsWith(suffix)) {
				return true;
			}
		}

		return false;
	}

	/**
	 * create a new lexer
	 * 
	 * @param in
	 * @return
	 */
	public Lexer createLexer(final InputStream in) {
		if (this == JAVA) {
			return new JavaLexer(in);
		} else if (this == CPP) {
			return new CPPLexer(in);
		} else {
			return null;
		}
	}

}
