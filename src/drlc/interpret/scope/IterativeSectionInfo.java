package drlc.interpret.scope;

import java.util.*;

import drlc.node.Node;

public class IterativeSectionInfo {
	
	String continueJumpSectionId = null;
	public final Set<Integer> exitJumpIterativeSectionIds = new HashSet<>();
	
	public IterativeSectionInfo() {}
	
	public void setIterativeContinueJumpSectionId(Node node, String sectionId) {
		if (sectionId == null && continueJumpSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative continue section is already null! %s", node));
		}
		else if (sectionId != null && continueJumpSectionId != null) {
			throw new IllegalArgumentException(String.format("Iterative continue section can not be overwritten! %s", node));
		}
		else {
			continueJumpSectionId = sectionId;
		}
	}
	
	public String getIterativeContinueJumpSectionId(Node node) {
		if (continueJumpSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative continue section is null! %s", node));
		}
		else {
			return continueJumpSectionId;
		}
	}
	
	public Integer[] getExitJumpIterativeSectionIds(Node node) {
		if (exitJumpIterativeSectionIds.isEmpty()) {
			throw new IllegalArgumentException(String.format("Attempted to get empty array of iterative sections for exit jump! %s", node));
		}
		else {
			Integer[] sections = exitJumpIterativeSectionIds.toArray(new Integer[exitJumpIterativeSectionIds.size()]);
			exitJumpIterativeSectionIds.clear();
			return sections;
		}
	}
}
