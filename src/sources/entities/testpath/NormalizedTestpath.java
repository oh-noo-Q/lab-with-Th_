package entities.testpath;

import entities.cfg.ConditionCfgNode;
import entities.cfg.ICfgNode;

public class NormalizedTestpath extends AbstractTestpath implements INormalizedTestpath {
	/**
	 *
	 */
	private static final long serialVersionUID = -7984311819565059228L;

	@Override
	public String getFullPath() {
		StringBuilder output = new StringBuilder();
		for (int i = 0; i < size() - 1; i++) {
			ICfgNode n = get(i);
			if (n instanceof ConditionCfgNode)
				output.append("(").append(n.getContent()).append(") ").append(ITestpathInCFG.SEPARATE_BETWEEN_NODES).append(" ");
			else
				output.append(n.getContent()).append(ITestpathInCFG.SEPARATE_BETWEEN_NODES).append(" ");
		}

		output.append(get(size() - 1).getContent());
		return output.toString();
	}

	@Override
	public NormalizedTestpath cast() {
		return this;
	}
}
