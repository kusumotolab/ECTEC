package jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector;

import java.util.Set;
import java.util.TreeSet;

import jp.ac.osaka_u.ist.sdl.ectec.data.ElementLinkInfo;

public class ElementChain<L extends ElementLinkInfo> {

	/**
	 * the concatenated links
	 */
	private final Set<Long> links;

	/**
	 * the concatenated elements
	 */
	private final Set<Long> elements;

	public ElementChain(final L link) {
		this.links = new TreeSet<Long>();
		this.elements = new TreeSet<Long>();
		this.links.add(link.getId());
		this.elements.add(link.getBeforeElementId());
		this.elements.add(link.getAfterElementId());
	}

	public final Set<Long> getLinks() {
		return links;
	}

	public final Set<Long> getElements() {
		return elements;
	}

	public void invite(final L anotherLink) {
		synchronized (links) {
			this.links.add(anotherLink.getId());
		}
		synchronized (elements) {
			this.elements.add(anotherLink.getBeforeElementId());
			this.elements.add(anotherLink.getAfterElementId());
		}
	}

	public boolean isFriend(final L targetLink) {
		if (this.elements.contains(targetLink.getBeforeElementId())
				|| this.elements.contains(targetLink.getAfterElementId())) {
			return true;
		} else {
			return false;
		}
	}

}
