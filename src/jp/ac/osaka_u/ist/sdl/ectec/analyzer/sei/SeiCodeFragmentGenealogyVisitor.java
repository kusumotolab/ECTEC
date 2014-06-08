package jp.ac.osaka_u.ist.sdl.ectec.analyzer.sei;

import java.util.List;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.ElementVisitor;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentGenealogyInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CodeFragmentLinkInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.CombinedRevisionInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RepositoryInfo;
import jp.ac.osaka_u.ist.sdl.ectec.analyzer.data.RevisionInfo;

public class SeiCodeFragmentGenealogyVisitor extends ElementVisitor {

	private String outputLine = null;

	public final String getOutputLine() {
		return outputLine;
	}
 	
	@Override
	public void visit(final CodeFragmentGenealogyInfo element) {
		final CodeFragmentInfo startFragment = getStartFragment(
				element.getStartCombinedRevision(), element.getFragments());
		final CodeFragmentInfo endFragment = getEndFragment(
				element.getEndCombinedRevision(), element.getFragments());

		final RepositoryInfo firstRepository = startFragment.getOwnerFile()
				.getOwnerRepository();
		final RepositoryInfo lastRepository = endFragment.getOwnerFile()
				.getOwnerRepository();

		final RevisionInfo firstRevision = startFragment
				.getStartCombinedRevision()
				.getOriginalRevision(firstRepository);
		final RevisionInfo lastRevision = endFragment.getEndCombinedRevision()
				.getOriginalRevision(lastRepository);

		int firstRepoChange = 0;
		int otherRepoChange = 0;

		for (final CodeFragmentLinkInfo fragmentLink : element.getLinks()) {
			if (fragmentLink.isChanged()) {
				if (fragmentLink.getBeforeFragment().getOwnerFile()
						.getOwnerRepository().equals(firstRepository)) {
					if (fragmentLink.getAfterFragment().getOwnerFile()
							.getOwnerRepository().equals(firstRepository)) {
						firstRepoChange++;
					}
				} else {
					otherRepoChange++;
				}
			}
		}

		final StringBuilder builder = new StringBuilder();
		builder.append(element.getId() + ",");
		builder.append(firstRepository.getName() + ",");
		builder.append(firstRevision.getIdentifier() + ",");
		builder.append(startFragment.getOwnerFile().getPath() + ",");
		builder.append(startFragment.getStartLine() + ",");
		builder.append(startFragment.getEndLine() + ",");
		builder.append(lastRepository.getName() + ",");
		builder.append(lastRevision.getIdentifier() + ",");
		builder.append(endFragment.getOwnerFile().getPath() + ",");
		builder.append(endFragment.getStartLine() + ",");
		builder.append(endFragment.getEndLine() + ",");
		builder.append(firstRepoChange + ",");
		builder.append(otherRepoChange);

		outputLine = builder.toString();
	}

	private final CodeFragmentInfo getStartFragment(
			final CombinedRevisionInfo startCombinedRevision,
			final List<CodeFragmentInfo> fragments) {
		for (final CodeFragmentInfo fragment : fragments) {
			if (fragment.getStartCombinedRevision().equals(
					startCombinedRevision)) {
				return fragment;
			}
		}

		return null;
	}

	private final CodeFragmentInfo getEndFragment(
			final CombinedRevisionInfo endCombinedRevision,
			final List<CodeFragmentInfo> fragments) {
		for (final CodeFragmentInfo fragment : fragments) {
			if (fragment.getEndCombinedRevision().equals(endCombinedRevision)) {
				return fragment;
			}
		}

		return null;
	}

}
