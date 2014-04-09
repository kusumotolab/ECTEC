package jp.ac.osaka_u.ist.sdl.ectec;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

public class ErrorLoggerFormatter extends Formatter {

	private final SimpleDateFormat sdFormat = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss.SSS");

	@Override
	public String format(LogRecord rec) {
		final StringBuilder builder = new StringBuilder();

		builder.append("[");

		builder.append(sdFormat.format(new Date(rec.getMillis())));
		builder.append("] ");
		
		if (rec.getLevel() == Level.FINEST) {
			builder.append("FINEST");
		} else if (rec.getLevel() == Level.FINER) {
			builder.append("FINER");
		} else if (rec.getLevel() == Level.FINE) {
			builder.append("FINE");
		} else if (rec.getLevel() == Level.CONFIG) {
			builder.append("CONFIG");
		} else if (rec.getLevel() == Level.INFO) {
			builder.append("INFO");
		} else if (rec.getLevel() == Level.WARNING) {
			builder.append("WARNING");
		} else if (rec.getLevel() == Level.SEVERE) {
			builder.append("SEVERE");
		} else {
			builder.append(Integer.toString(rec.getLevel().intValue()));
			builder.append("");
		}
		
		builder.append("\n");
		
		builder.append(rec.getMessage());
		builder.append("\n");

		return builder.toString();
	}

}
