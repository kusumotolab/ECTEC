package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sourceanalyzer.crd;

import java.util.ArrayList;
import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.data.CRD;
import jp.ac.osaka_u.ist.sdl.ectec.settings.Constants;

import org.eclipse.jdt.core.dom.ASTNode;

/**
 * A class to create a crd for a given node
 * 
 * @author k-hotta
 * 
 */
public abstract class AbstractCRDCreator<T extends ASTNode> {

	/**
	 * the node to be analyzed
	 */
	protected final T node;

	/**
	 * the crds for the ancestors of this node
	 */
	protected final List<CRD> ancestors;

	/**
	 * the type of the block
	 */
	protected final BlockType bType;

	public AbstractCRDCreator(final T node, final List<CRD> ancestors,
			final BlockType bType) {
		this.node = node;
		this.ancestors = ancestors;
		this.bType = bType;
	}

	/**
	 * create a new instance of CRD for this block
	 * 
	 * @return
	 */
	public CRD createCrd() {
		final String head = bType.getHead();
		final String anchor = getAnchor();

		final List<Long> ancestorIds = new ArrayList<Long>();
		for (final CRD ancestor : ancestors) {
			ancestorIds.add(ancestor.getId());
		}

		final MetricsCalculator cmCalculator = new MetricsCalculator();
		node.accept(cmCalculator);
		final int cm = cmCalculator.getCC() + cmCalculator.getFO();

		final String thisCrdStr = getStringCrdForThisBlock(head, anchor, cm);
		final String fullText = (ancestors.isEmpty()) ? thisCrdStr : ancestors
				.get(ancestors.size() - 1).getFullText() + thisCrdStr;

		return new CRD(bType, head, anchor, cm, ancestorIds, fullText);
	}

	/**
	 * get the string representation of THIS block
	 * 
	 * @param head
	 * @param anchor
	 * @param cm
	 * @return
	 */
	private String getStringCrdForThisBlock(final String head,
			final String anchor, final int cm) {
		final StringBuilder builder = new StringBuilder();

		builder.append(head + ",");
		builder.append(anchor + ",");
		builder.append(cm + Constants.LINE_SEPARATOR);

		return builder.toString();
	}

	/**
	 * get the anchor of the block
	 * 
	 * @return
	 */
	protected abstract String getAnchor();

}
