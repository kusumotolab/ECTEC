package jp.ac.osaka_u.ist.sdl.ectec.detector.linker;

import java.util.HashMap;
import java.util.Map;

import jp.ac.osaka_u.ist.sdl.ectec.db.data.BlockType;
import jp.ac.osaka_u.ist.sdl.ectec.db.data.DBCrdInfo;

/**
 * A class that judges whether two code fragments satisfy the conditions to be
 * linked
 * 
 * @author k-hotta
 * 
 */
public class FragmentLinkConditionUmpire {

	/**
	 * the threshold for similarity
	 */
	private final double similarityThreshold;

	public FragmentLinkConditionUmpire(final double similarityThreshold) {
		this.similarityThreshold = similarityThreshold;
	}

	public final boolean satisfyAllConditions(final DBCrdInfo beforeCrd,
			final DBCrdInfo afterCrd, final double similarity) {
		final boolean crdCondition = satisfyCrdConditions(beforeCrd, afterCrd);
		final boolean similarityCondition = (similarity >= similarityThreshold);

		return crdCondition && similarityCondition;
	}

	public final boolean satisfyCrdConditions(final DBCrdInfo beforeCrd,
			final DBCrdInfo afterCrd) {
		// return false if the types of two fragments are not equal
		if (beforeCrd.getType() != afterCrd.getType()) {
			return false;
		}

		// switch by the type of the block
		switch (beforeCrd.getType()) {
		case METHOD:
			if (!satisfyMethodConditions(beforeCrd, afterCrd)) {
				return false;
			}
			break;
		case IF:
			if (!satisfyConditionalBlockConditions(beforeCrd, afterCrd)) {
				return false;
			}
			break;
		case FOR:
			if (!satisfyConditionalBlockConditions(beforeCrd, afterCrd)) {
				return false;
			}
			break;
		case DO:
			if (!satisfyConditionalBlockConditions(beforeCrd, afterCrd)) {
				return false;
			}
			break;
		case ENHANCED_FOR:
			if (!satisfyConditionalBlockConditions(beforeCrd, afterCrd)) {
				return false;
			}
			break;
		case WHILE:
			if (!satisfyConditionalBlockConditions(beforeCrd, afterCrd)) {
				return false;
			}
			break;
		default:
			break;
		}

		return true;
	}

	public final boolean satisfyConditionalBlockConditions(final DBCrdInfo beforeCrd,
			final DBCrdInfo afterCrd) {
		final BlockType beforeType = beforeCrd.getType();
		final BlockType afterType = afterCrd.getType();
		final String beforeNormalizedAnchor = beforeCrd.getNormalizedAnchor();
		final String afterNormalizedAnchor = afterCrd.getNormalizedAnchor();

		return (beforeType == afterType)
				&& (beforeNormalizedAnchor.equals(afterNormalizedAnchor));
	}

	/**
	 * return true if either <br>
	 * names of methods are equal to each other or <br>
	 * all types of arguments are equal to each other
	 * 
	 * @param beforeCrd
	 * @param afterCrd
	 * @return
	 */
	public final boolean satisfyMethodConditions(final DBCrdInfo beforeCrd,
			final DBCrdInfo afterCrd) {
		final String beforeAnchor = beforeCrd.getAnchor();
		final String afterAnchor = afterCrd.getAnchor();
		final int beforeLeftParenIndex = beforeAnchor.indexOf("(");
		final int afterLeftParenIndex = afterAnchor.indexOf("(");
		final int beforeRightParenIndex = beforeAnchor.indexOf(")",
				beforeLeftParenIndex);
		final int afterRightParenIndex = afterAnchor.indexOf(")",
				afterLeftParenIndex);

		final String beforeName = beforeAnchor.substring(0,
				beforeLeftParenIndex);
		final String afterName = afterAnchor.substring(0, afterLeftParenIndex);

		final boolean sameName = beforeName.equals(afterName);

		final String beforeParameters = beforeAnchor.substring(
				beforeLeftParenIndex + 1, beforeRightParenIndex);
		final String afterParameters = afterAnchor.substring(
				afterLeftParenIndex + 1, afterRightParenIndex);

		final boolean sameParameter = isSameParameter(beforeParameters,
				afterParameters);

		return sameName || sameParameter;
	}

	private boolean isSameParameter(final String parameter1,
			final String parameter2) {
		final String[] splitedParameter1 = parameter1.split(",");
		final String[] splitedParameter2 = parameter2.split(",");

		if (splitedParameter1.length != splitedParameter2.length) {
			return false;
		}

		final Map<String, Integer> typeCount1 = new HashMap<String, Integer>();
		final Map<String, Integer> typeCount2 = new HashMap<String, Integer>();

		for (final String type : splitedParameter1) {
			int current = 1;
			if (typeCount1.containsKey(type)) {
				current += typeCount1.get(type);
				typeCount1.remove(type);
			}
			typeCount1.put(type, current);
		}

		for (final String type : splitedParameter2) {
			int current = 1;
			if (typeCount2.containsKey(type)) {
				current += typeCount2.get(type);
				typeCount2.remove(type);
			}
			typeCount2.put(type, current);
		}

		return typeCount1.equals(typeCount2);
	}

}
