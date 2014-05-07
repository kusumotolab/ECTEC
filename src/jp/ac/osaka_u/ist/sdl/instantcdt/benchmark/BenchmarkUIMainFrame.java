package jp.ac.osaka_u.ist.sdl.instantcdt.benchmark;

import java.awt.BorderLayout;
import java.awt.Color;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;
import javax.swing.text.DefaultHighlighter;
import javax.swing.text.DefaultStyledDocument;
import javax.swing.text.Highlighter;
import javax.swing.text.StyleContext;


public class BenchmarkUIMainFrame extends JFrame {

	private JPanel contentPane;
	private JTable detectedTable;
	private JTable undetectedTable;

	private Map<Integer, CloneReference> referencesMap;
	private Map<Integer, CloneCandidate> candidatesMap;
	private String dir;

	private static final String[] columnNames1 = new String[] { "id", "path1",
			"start1", "end1", "path2", "start2", "end2", "candidate id" };
	private static final String[] columnNames2 = new String[] { "id", "path1",
			"start1", "end1", "path2", "start2", "end2" };

	private static final Color referenceColor = new Color(255, 100, 100, 135);
	private static final Color candidateColor = new Color(255, 100, 100);
	private JTextPane rightTextPane;
	private JScrollPane scrollPane_3;
	private JTextPane leftTextPane;

	/**
	 * Create the frame.
	 */
	public BenchmarkUIMainFrame(
			final Map<CloneCandidate, CloneReference> detected,
			final Collection<CloneReference> undetected,
			final Map<Integer, CloneReference> referencesMap,
			final Map<Integer, CloneCandidate> candidatesMap, final String dir) {
		this.referencesMap = referencesMap;
		this.candidatesMap = candidatesMap;
		this.dir = dir;

		try {
			// ルックアンドフィールをシステムのものに設定
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
			SwingUtilities.updateComponentTreeUI(this);
		} catch (Exception e) {
			System.err.println("can't find System's Look&Feel");
		}
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JSplitPane splitPane = new JSplitPane();
		contentPane.add(splitPane, BorderLayout.CENTER);

		JSplitPane splitPane_1 = new JSplitPane();
		splitPane.setRightComponent(splitPane_1);
		
		JScrollPane scrollPane_2 = new JScrollPane();
		splitPane_1.setLeftComponent(scrollPane_2);
		
		leftTextPane = new JTextPane();
		scrollPane_2.setViewportView(leftTextPane);
		
		scrollPane_3 = new JScrollPane();
		splitPane_1.setRightComponent(scrollPane_3);
		
		rightTextPane = new JTextPane();
		scrollPane_3.setViewportView(rightTextPane);

		JSplitPane splitPane_2 = new JSplitPane();
		splitPane_2.setOrientation(JSplitPane.VERTICAL_SPLIT);
		splitPane.setLeftComponent(splitPane_2);

		JScrollPane scrollPane = new JScrollPane();
		splitPane_2.setLeftComponent(scrollPane);

		detectedTable = new JTable();
		scrollPane.setViewportView(detectedTable);

		JScrollPane scrollPane_1 = new JScrollPane();
		splitPane_2.setRightComponent(scrollPane_1);

		undetectedTable = new JTable();
		scrollPane_1.setViewportView(undetectedTable);

		final DefaultTableModel detectedModel = new DefaultTableModel(
				columnNames1, 0);
		for (final Map.Entry<CloneCandidate, CloneReference> entry : detected
				.entrySet()) {
			final CloneReference detectedReference = entry.getValue();
			detectedModel.addRow(makeRow(detectedModel, detectedReference,
					entry.getKey().getId()));
		}
		detectedTable.setModel(detectedModel);
		JTableHeader header = detectedTable.getTableHeader();
		header.setReorderingAllowed(false);
		detectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		detectedTable.getSelectionModel().addListSelectionListener(
				new DetectedPairSelectionActionListener());

		final DefaultTableModel undetectedModel = new DefaultTableModel(
				columnNames2, 0);
		for (final CloneReference undetectedReference : undetected) {
			undetectedModel
					.addRow(makeRow(undetectedModel, undetectedReference));
		}
		undetectedTable.setModel(undetectedModel);
		JTableHeader header2 = undetectedTable.getTableHeader();
		header2.setReorderingAllowed(false);
		undetectedTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		undetectedTable.getSelectionModel().addListSelectionListener(
				new UndetectedPairSelectionActionListener());
	}

	private Object[] makeRow(final DefaultTableModel model,
			final BenchmarkClonePair clonePair) {
		final int id = clonePair.getId();
		final String path1 = clonePair.getFragment1().getOwnerFile();
		final int start1 = clonePair.getFragment1().getStartLine();
		final int end1 = clonePair.getFragment1().getEndLine();
		final String path2 = clonePair.getFragment2().getOwnerFile();
		final int start2 = clonePair.getFragment2().getStartLine();
		final int end2 = clonePair.getFragment2().getEndLine();

		return new Object[] { id, path1, start1, end1, path2, start2, end2 };
	}

	private Object[] makeRow(final DefaultTableModel model,
			final BenchmarkClonePair clonePair, final int candidateId) {
		final int id = clonePair.getId();
		final String path1 = clonePair.getFragment1().getOwnerFile();
		final int start1 = clonePair.getFragment1().getStartLine();
		final int end1 = clonePair.getFragment1().getEndLine();
		final String path2 = clonePair.getFragment2().getOwnerFile();
		final int start2 = clonePair.getFragment2().getStartLine();
		final int end2 = clonePair.getFragment2().getEndLine();

		return new Object[] { id, path1, start1, end1, path2, start2, end2,
				candidateId };
	}

	private void setSource(final CloneReference reference,
			final CloneCandidate candidate) {
		if (candidate != null) {
			setSource(leftTextPane, reference.getFragment1(),
					candidate.getFragment1());
			setSource(rightTextPane, reference.getFragment2(),
					candidate.getFragment2());
		} else {
			setSource(leftTextPane, reference.getFragment1(), null);
			setSource(rightTextPane, reference.getFragment2(), null);
		}
	}

	private void setSource(final JTextPane textPane,
			final BenchmarkCloneFragment referenceFragment,
			final BenchmarkCloneFragment candidateFragment) {
		try {
			textPane.removeAll();
			StyleContext sc = new StyleContext();
			DefaultStyledDocument doc = new DefaultStyledDocument(sc);

			final String originalPath = referenceFragment.getOwnerFile();
			final String appendedPath = dir + "/" + originalPath;
			
			FileSystem fs = FileSystems.getDefault();
			final Path path = fs.getPath(appendedPath);
			final String tailoredPath = path.toString();
			
			BufferedReader reader = new BufferedReader(new FileReader(new File(
					tailoredPath)));

			String line;
			final StringBuilder builder = new StringBuilder();
			while ((line = reader.readLine()) != null) {
				builder.append(line + System.getProperty("line.separator"));
			}

			reader.close();

			final String src = builder.toString();

			doc.insertString(0, src, sc.getStyle(StyleContext.DEFAULT_STYLE));
			textPane.setDocument(doc);

			Highlighter highlighter = textPane.getHighlighter();
			highlighter.removeAllHighlights();

			Highlighter.HighlightPainter highlightPainter = new DefaultHighlighter.DefaultHighlightPainter(
					referenceColor);

			int indexOfFragmentStart = LineSeparatorChecker
					.getIndexOfLineStart(src, referenceFragment.getStartLine());
			int indexOfFragmentEnd = LineSeparatorChecker.getIndexOfLineStart(
					src, referenceFragment.getEndLine() + 1) - 1;

			highlighter.addHighlight(indexOfFragmentStart, indexOfFragmentEnd,
					highlightPainter);

			if (candidateFragment != null) {
				Highlighter.HighlightPainter highlightPainter2 = new DefaultHighlighter.DefaultHighlightPainter(
						candidateColor);

				int indexOfFragmentStart2 = LineSeparatorChecker
						.getIndexOfLineStart(src,
								candidateFragment.getStartLine());
				int indexOfFragmentEnd2 = LineSeparatorChecker
						.getIndexOfLineStart(src,
								candidateFragment.getEndLine() + 1) - 1;

				highlighter.addHighlight(indexOfFragmentStart2,
						indexOfFragmentEnd2, highlightPainter2);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private class DetectedPairSelectionActionListener implements
			ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			final int selected = detectedTable.getSelectedRow();
			if (selected == -1) {
				return;
			}

			final int id = (Integer) detectedTable.getValueAt(selected, 0);
			final CloneReference reference = referencesMap.get(id);
			if (reference == null) {
				return;
			}

			final int candidateId = (Integer) detectedTable.getValueAt(
					selected, 7);
			final CloneCandidate candidate = candidatesMap.get(candidateId);
			if (candidate == null) {
				System.err.println("cannot find the corresponding candidate");
				return;
			}

			setSource(reference, candidate);
		}

	}

	private class UndetectedPairSelectionActionListener implements
			ListSelectionListener {

		@Override
		public void valueChanged(ListSelectionEvent e) {
			final int selected = undetectedTable.getSelectedRow();
			if (selected == -1) {
				return;
			}

			final int id = (Integer) undetectedTable.getValueAt(selected, 0);
			final CloneReference reference = referencesMap.get(id);
			if (reference == null) {
				return;
			}

			setSource(reference, null);
		}

	}

}
