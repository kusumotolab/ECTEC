package jp.ac.osaka_u.ist.sdl.ectec.settings;

import static jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrintLevel.NONE;
import static jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrintLevel.LITTLE;
import static jp.ac.osaka_u.ist.sdl.ectec.settings.MessagePrintLevel.VERBOSE;

import java.io.PrintStream;

/**
 * A class to print the state of the processing
 * 
 * @author k-hotta
 * 
 */
public class MessagePrinter {

	/**
	 * the level of output <br>
	 * this field should be initialized by calling
	 * {@link MessagePrinter#setLevel(MessagePrintLevel) setLevel}
	 */
	private static MessagePrintLevel level = LITTLE;

	/**
	 * the output stream
	 */
	private static PrintStream out = System.out;

	/**
	 * the error stream
	 */
	private static PrintStream err = System.err;

	/**
	 * set the level of output
	 * 
	 * @param newLevel
	 */
	public static void setLevel(final MessagePrintLevel newLevel) {
		level = newLevel;
	}

	/**
	 * set the output stream
	 * 
	 * @param newOut
	 */
	public static void setOut(final PrintStream newOut) {
		out = newOut;
	}

	/**
	 * set the error stream
	 * 
	 * @param newErr
	 */
	public static void setErr(final PrintStream newErr) {
		err = newErr;
	}

	/**
	 * call {@link PrintStream#println() println()} for the specified output
	 * stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println() {
		if (level == VERBOSE) {
			out.println();
		}
	}

	/**
	 * call {@link PrintStream#println(String) println(String)} for the
	 * specified output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final String str) {
		if (level == VERBOSE) {
			out.println(str);
		}
	}

	/**
	 * call {@link PrintStream#println(boolean) println(boolean)} for the
	 * specified output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final boolean b) {
		if (level == VERBOSE) {
			out.println(b);
		}
	}

	/**
	 * call {@link PrintStream#println(char) println(char)} for the specified
	 * output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final char c) {
		if (level == VERBOSE) {
			out.println(c);
		}
	}

	/**
	 * call {@link PrintStream#println(char[]) println(char[])} for the
	 * specified output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final char[] s) {
		if (level == VERBOSE) {
			out.println(s);
		}
	}

	/**
	 * call {@link PrintStream#println(double) println(double)} for the
	 * specified output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final double d) {
		if (level == VERBOSE) {
			out.println(d);
		}
	}

	/**
	 * call {@link PrintStream#println(float) println(float)} for the specified
	 * output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final float f) {
		if (level == VERBOSE) {
			out.println(f);
		}
	}

	/**
	 * call {@link PrintStream#println(int) println(int)} for the specified
	 * output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final int i) {
		if (level == VERBOSE) {
			out.println(i);
		}
	}

	/**
	 * call {@link PrintStream#println(l) println(l)} for the specified output
	 * stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final long l) {
		if (level == VERBOSE) {
			out.println(l);
		}
	}

	/**
	 * call {@link PrintStream#println(obj) println(obj)} for the specified
	 * output stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void println(final Object obj) {
		if (level == VERBOSE) {
			out.println(obj);
		}
	}

	/**
	 * call {@link PrintStream#println() println()} for the specified error
	 * stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln() {
		if (level == VERBOSE) {
			err.println();
		}
	}

	/**
	 * call {@link PrintStream#println(String) println(String)} for the
	 * specified error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final String str) {
		if (level == VERBOSE) {
			err.println(str);
		}
	}

	/**
	 * call {@link PrintStream#println(boolean) println(boolean)} for the
	 * specified error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final boolean b) {
		if (level == VERBOSE) {
			err.println(b);
		}
	}

	/**
	 * call {@link PrintStream#println(char) println(char)} for the specified
	 * error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final char c) {
		if (level == VERBOSE) {
			err.println(c);
		}
	}

	/**
	 * call {@link PrintStream#println(char[]) println(char[])} for the
	 * specified error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final char[] s) {
		if (level == VERBOSE) {
			err.println(s);
		}
	}

	/**
	 * call {@link PrintStream#println(double) println(double)} for the
	 * specified error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final double d) {
		if (level == VERBOSE) {
			err.println(d);
		}
	}

	/**
	 * call {@link PrintStream#println(float) println(float)} for the specified
	 * error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final float f) {
		if (level == VERBOSE) {
			err.println(f);
		}
	}

	/**
	 * call {@link PrintStream#println(int) println(int)} for the specified
	 * error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final int i) {
		if (level == VERBOSE) {
			err.println(i);
		}
	}

	/**
	 * call {@link PrintStream#println(l) println(l)} for the specified error
	 * stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final long l) {
		if (level == VERBOSE) {
			err.println(l);
		}
	}

	/**
	 * call {@link PrintStream#println(obj) println(obj)} for the specified
	 * error stream <br>
	 * this works only if the level is {@link MessagePrintLevel#VERBOSE VERBOSE}
	 */
	public static void ePrintln(final Object obj) {
		if (level == VERBOSE) {
			err.println(obj);
		}
	}

	/**
	 * call {@link PrintStream#println() println()} for the specified output
	 * stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln() {
		if (level != NONE) {
			out.println();
		}
	}

	/**
	 * call {@link PrintStream#println(String) println(String)} for the
	 * specified output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final String str) {
		if (level == VERBOSE) {
			out.println(str);
		}
	}

	/**
	 * call {@link PrintStream#println(boolean) println(boolean)} for the
	 * specified output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final boolean b) {
		if (level == VERBOSE) {
			out.println(b);
		}
	}

	/**
	 * call {@link PrintStream#println(char) println(char)} for the specified
	 * output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final char c) {
		if (level == VERBOSE) {
			out.println(c);
		}
	}

	/**
	 * call {@link PrintStream#println(char[]) println(char[])} for the
	 * specified output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final char[] s) {
		if (level == VERBOSE) {
			out.println(s);
		}
	}

	/**
	 * call {@link PrintStream#println(double) println(double)} for the
	 * specified output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final double d) {
		if (level == VERBOSE) {
			out.println(d);
		}
	}

	/**
	 * call {@link PrintStream#println(float) println(float)} for the specified
	 * output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final float f) {
		if (level == VERBOSE) {
			out.println(f);
		}
	}

	/**
	 * call {@link PrintStream#println(int) println(int)} for the specified
	 * output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final int i) {
		if (level == VERBOSE) {
			out.println(i);
		}
	}

	/**
	 * call {@link PrintStream#println(l) println(l)} for the specified output
	 * stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final long l) {
		if (level == VERBOSE) {
			out.println(l);
		}
	}

	/**
	 * call {@link PrintStream#println(obj) println(obj)} for the specified
	 * output stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void stronglyPrintln(final Object obj) {
		if (level == VERBOSE) {
			out.println(obj);
		}
	}

	/**
	 * call {@link PrintStream#println() println()} for the specified error
	 * stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln() {
		if (level == VERBOSE) {
			err.println();
		}
	}

	/**
	 * call {@link PrintStream#println(String) println(String)} for the
	 * specified error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final String str) {
		if (level == VERBOSE) {
			err.println(str);
		}
	}

	/**
	 * call {@link PrintStream#println(boolean) println(boolean)} for the
	 * specified error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final boolean b) {
		if (level == VERBOSE) {
			err.println(b);
		}
	}

	/**
	 * call {@link PrintStream#println(char) println(char)} for the specified
	 * error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final char c) {
		if (level == VERBOSE) {
			err.println(c);
		}
	}

	/**
	 * call {@link PrintStream#println(char[]) println(char[])} for the
	 * specified error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final char[] s) {
		if (level == VERBOSE) {
			err.println(s);
		}
	}

	/**
	 * call {@link PrintStream#println(double) println(double)} for the
	 * specified error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final double d) {
		if (level == VERBOSE) {
			err.println(d);
		}
	}

	/**
	 * call {@link PrintStream#println(float) println(float)} for the specified
	 * error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final float f) {
		if (level == VERBOSE) {
			err.println(f);
		}
	}

	/**
	 * call {@link PrintStream#println(int) println(int)} for the specified
	 * error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final int i) {
		if (level == VERBOSE) {
			err.println(i);
		}
	}

	/**
	 * call {@link PrintStream#println(l) println(l)} for the specified error
	 * stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final long l) {
		if (level == VERBOSE) {
			err.println(l);
		}
	}

	/**
	 * call {@link PrintStream#println(obj) println(obj)} for the specified
	 * error stream <br>
	 * this works if the level is {@link MessagePrintLevel#LITTLE LITTLE} or
	 * stronger
	 */
	public static void eStronglyPrintln(final Object obj) {
		if (level == VERBOSE) {
			err.println(obj);
		}
	}

}
