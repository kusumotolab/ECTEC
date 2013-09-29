package jp.ac.osaka_u.ist.sdl.ectec.analyzer.genealogydetector;

import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.concurrent.atomic.AtomicLong;

import jp.ac.osaka_u.ist.sdl.ectec.data.ElementLinkInfo;

public class ElementChain<L extends ElementLinkInfo> {

	/**
	 * the counter to have the number of created elements
	 */
	private static final AtomicLong count = new AtomicLong(0);

	/**
	 * the id
	 */
	private final long id;

	/**
	 * the revisions
	 */
	private final SortedSet<Long> revisions;

	/**
	 * the concatenated links
	 */
	private final Set<Long> links;

	/**
	 * the concatenated elements
	 */
	private final Set<Long> elements;

	public ElementChain(final L link) {
		this.id = count.getAndIncrement();
		this.revisions = new TreeSet<Long>();
		this.links = new TreeSet<Long>();
		this.elements = new TreeSet<Long>();
		this.revisions.add(link.getBeforeRevisionId());
		this.revisions.add(link.getAfterRevisionId());
		this.links.add(link.getId());
		this.elements.add(link.getBeforeElementId());
		this.elements.add(link.getAfterElementId());
	}

	public final long getId() {
		return id;
	}

	public final Set<Long> getLinks() {
		return links;
	}

	public final Set<Long> getElements() {
		return elements;
	}

	public final SortedSet<Long> getRevisions() {
		return revisions;
	}

	public final long getFirstRevision() {
		return revisions.first();
	}

	public final long getLastRevision() {
		return revisions.last();
	}

	public void invite(final L anotherLink) {
		synchronized (revisions) {
			this.revisions.add(anotherLink.getBeforeRevisionId());
			this.revisions.add(anotherLink.getAfterRevisionId());
		}
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

	public static void resetCounter() {
		count.set(0);
	}

}
