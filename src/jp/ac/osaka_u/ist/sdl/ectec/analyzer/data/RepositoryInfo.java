package jp.ac.osaka_u.ist.sdl.ectec.analyzer.data;

import jp.ac.osaka_u.ist.sdl.ectec.analyzer.ElementVisitor;
import jp.ac.osaka_u.ist.sdl.ectec.settings.VersionControlSystem;

/**
 * A class that represents repository
 * 
 * @author k-hotta
 * 
 */
public class RepositoryInfo extends AbstractElement implements
		Comparable<RepositoryInfo> {

	private final String name;

	private final String url;

	private final VersionControlSystem vcs;

	private final String userName;

	private final String passwd;

	public RepositoryInfo(final long id, final String name, final String url,
			final VersionControlSystem vcs, final String userName,
			final String passwd) {
		super(id);
		this.name = name;
		this.url = url;
		this.vcs = vcs;
		this.userName = userName;
		this.passwd = passwd;
	}

	@Override
	public int compareTo(RepositoryInfo another) {
		return ((Long) this.id).compareTo(another.getId());
	}

	public final String getName() {
		return name;
	}

	public final String getUrl() {
		return url;
	}

	public final VersionControlSystem getVcs() {
		return vcs;
	}

	public final String getUserName() {
		return userName;
	}

	public final String getPasswd() {
		return passwd;
	}

	@Override
	public void accept(final ElementVisitor visitor) {
		visitor.visit(this);
	}
	
}
