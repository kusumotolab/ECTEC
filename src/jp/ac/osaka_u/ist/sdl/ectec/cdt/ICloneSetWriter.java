package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.Collection;
import java.util.Map;

public interface ICloneSetWriter {

	public void write(Collection<CloneSet> cloneSets,
			Map<Long, InstantFileInfo> files) throws Exception;

}
