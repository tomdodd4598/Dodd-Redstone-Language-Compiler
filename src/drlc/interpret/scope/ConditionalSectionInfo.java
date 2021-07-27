package drlc.interpret.scope;

import java.util.*;

import drlc.interpret.routine.Routine;
import drlc.node.Node;

public class ConditionalSectionInfo {
	
	Boolean hasElseBlock = null, executeIfCondition = null;
	Integer sectionLength = null, elseJumpSectionId = null;
	final Set<Integer> exitJumpSectionIds = new HashSet<>();
	
	public ConditionalSectionInfo() {}
	
	public void setHasElseBlock(Node node, boolean elseBlock) {
		if (hasElseBlock != null) {
			throw new IllegalArgumentException(String.format("Else block boolean can not be overwritten! %s", node));
		}
		else {
			hasElseBlock = elseBlock;
		}
	}
	
	public boolean getHasElseBlock(Node node) {
		if (hasElseBlock == null) {
			throw new IllegalArgumentException(String.format("Else block boolean is null! %s", node));
		}
		else {
			boolean elseBlock = hasElseBlock.booleanValue();
			hasElseBlock = null;
			return elseBlock;
		}
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
			boolean elseBlock = executeIfCondition.booleanValue();
			executeIfCondition = null;
			return elseBlock;
		}
	}
	
	public void setSectionLength(Node node, int length) {
		if (sectionLength != null) {
			throw new IllegalArgumentException(String.format("Conditional section length can not be overwritten! %s", node));
		}
		else {
			sectionLength = length;
		}
	}
	
	void decrementSectionLength(Node node) {
		if (sectionLength == null) {
			throw new IllegalArgumentException(String.format("Conditional section length is null! %s", node));
		}
		else if (sectionLength <= 0) {
			throw new IllegalArgumentException(String.format("Conditional section length was decremented too much! %s", node));
		}
		else {
			sectionLength = new Integer(sectionLength.intValue() - 1);
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
		else if (sectionLength == 0) {
			sectionLength = null;
		}
		exitJumpSectionIds.add(routine.sectionId);
	}
}
