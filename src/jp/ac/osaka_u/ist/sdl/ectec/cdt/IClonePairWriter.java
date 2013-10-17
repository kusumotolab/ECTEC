package jp.ac.osaka_u.ist.sdl.ectec.cdt;

import java.util.Collection;
import java.util.Map;

public interface IClonePairWriter {

	public void write(Collection<ClonePair> clonePairs,
			Map<Long, InstantFileInfo> files) throws Exception;

}
