package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.normalizer;

public class StringCreateVisitor extends ExtendedNaiveASTFlattener {

	private String str = null;

	public StringCreateVisitor() {
		super();
	}

	public String getString() {
		if (str == null) {
			str = getStringWhiteSpacesRemoved();
		}
		return str;
	}

	public StringBuffer getBuffer() {
		return super.buffer;
	}

	protected String getStringWhiteSpacesRemoved() {
		// String before = builder.toString();
		String before = buffer.toString();
		StringBuilder tmpBuilder = new StringBuilder();

		for (String line : before.split("\n")) {
			if (line.isEmpty()) {
				continue;
			}
			tmpBuilder.append(line.trim() + "\n");
		}

		return tmpBuilder.toString();
	}

}
