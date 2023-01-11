package drlc.intermediate.component.info;

import java.util.*;

import drlc.intermediate.routine.Routine;
import drlc.node.Node;

public class ConditionalSectionInfo {
	
	public boolean sectionStart = true;
	Boolean hasElseSection = null, executeIfCondition = null;
	
	int sectionCounter = 0;
	Integer elseJumpSectionId = null;
	
	final Set<Integer> exitJumpSectionIds = new HashSet<>();
	
	public ConditionalSectionInfo() {}
	
	public void setHasElseSection(Node node, boolean elseSection) {
		if (hasElseSection != null) {
			throw new IllegalArgumentException(String.format("Else section boolean can not be overwritten! %s", node));
		}
		else {
			hasElseSection = elseSection;
		}
	}
	
	public boolean getHasElseSection(Node node) {
		boolean elseSection = hasElseSection == null ? false : hasElseSection.booleanValue();
		hasElseSection = null;
		return elseSection;
	}
	
	public void setExecuteIfCondition(Node node, boolean ifCondition) {
		if (executeIfCondition != null) {
			throw new IllegalArgumentException(String.format("Execute if condition boolean can not be overwritten! %s", node));
		}
		else {
			executeIfCondition = ifCondition;
		}
	}
	
	public boolean getExecuteIfCondition(Node node) {
		if (executeIfCondition == null) {
			throw new IllegalArgumentException(String.format("Execute if condition boolean is null! %s", node));
		}
		else {
			boolean elseSection = executeIfCondition.booleanValue();
			executeIfCondition = null;
			return elseSection;
		}
	}
	
	public void incrementSectionLength(Node node) {
		++sectionCounter;
	}
	
	void decrementSectionLength(Node node) {
		if (sectionCounter <= 0) {
			throw new IllegalArgumentException(String.format("Conditional section counter was decremented too much! %s", node));
		}
		else {
			--sectionCounter;
		}
	}
	
	public int getElseJumpSectionId(Node node) {
		if (elseJumpSectionId == null) {
			throw new IllegalArgumentException(String.format("Conditional section for else jump not defined! %s", node));
		}
		else {
			int sectionId = elseJumpSectionId;
			elseJumpSectionId = null;
			return sectionId;
		}
	}
	
	public void setElseJumpSectionId(Node node, Routine routine) {
		if (elseJumpSectionId != null) {
			throw new IllegalArgumentException(String.format("Conditional section for else jump can not be overwritten! %s", node));
		}
		else {
			elseJumpSectionId = routine.sectionId;
		}
	}
	
	public Integer[] getExitJumpSectionIds(Node node) {
		if (exitJumpSectionIds.isEmpty()) {
			throw new IllegalArgumentException(String.format("Attempted to get empty array of conditional sections for exit jump! %s", node));
		}
		else {
			Integer[] sections = exitJumpSectionIds.toArray(new Integer[exitJumpSectionIds.size()]);
			exitJumpSectionIds.clear();
			return sections;
		}
	}
	
	public void addExitJumpSection(Node node, Routine routine) {
		decrementSectionLength(node);
		if (exitJumpSectionIds.contains(routine.sectionId)) {
			throw new IllegalArgumentException(String.format("Attempted to add conditional section for exit jump more than once! %s", node));
		}
		exitJumpSectionIds.add(routine.sectionId);
	}
}
