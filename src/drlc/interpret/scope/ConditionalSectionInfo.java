package drlc.interpret.scope;

import java.util.HashSet;
import java.util.Set;

import drlc.interpret.routine.Routine;
import drlc.node.Node;

public class ConditionalSectionInfo {
	
	Boolean hasElseBlock = null;
	Integer conditionalSectionLength = null;
	Integer elseJumpConditionalSectionId = null;
	final Set<Integer> exitJumpConditionalSectionIds = new HashSet<>();
	
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
	
	public void setConditionalSectionLength(Node node, int length) {
		if (conditionalSectionLength != null) {
			throw new IllegalArgumentException(String.format("Conditional section length can not be overwritten! %s", node));
		}
		else {
			conditionalSectionLength = length;
		}
	}
	
	void decrementConditionalSectionLength(Node node) {
		if (conditionalSectionLength == null) {
			throw new IllegalArgumentException(String.format("Conditional section length is null! %s", node));
		}
		else if (conditionalSectionLength <= 0) {
			throw new IllegalArgumentException(String.format("Conditional section length was decremented too much! %s", node));
		}
		else {
			conditionalSectionLength = new Integer(conditionalSectionLength.intValue() - 1);
		}
	}
	
	public int getElseJumpConditionalSectionId(Node node) {
		if (elseJumpConditionalSectionId == null) {
			throw new IllegalArgumentException(String.format("Conditional section for else jump not defined! %s", node));
		}
		else {
			int sectionId = elseJumpConditionalSectionId;
			elseJumpConditionalSectionId = null;
			return sectionId;
		}
	}
	
	public void setElseJumpConditionalSectionId(Node node, Routine routine) {
		if (elseJumpConditionalSectionId != null) {
			throw new IllegalArgumentException(String.format("Conditional section for else jump can not be overwritten! %s", node));
		}
		else {
			elseJumpConditionalSectionId = routine.sectionId;
		}
	}
	
	public Integer[] getExitJumpConditionalSectionIds(Node node) {
		if (exitJumpConditionalSectionIds.isEmpty()) {
			throw new IllegalArgumentException(String.format("Attempted to get empty array of conditional sections for exit jump! %s", node));
		}
		else {
			Integer[] sections = exitJumpConditionalSectionIds.toArray(new Integer[exitJumpConditionalSectionIds.size()]);
			exitJumpConditionalSectionIds.clear();
			return sections;
		}
	}
	
	public void addExitJumpConditionalSection(Node node, Routine routine) {
		decrementConditionalSectionLength(node);
		if (exitJumpConditionalSectionIds.contains(routine.sectionId)) {
			throw new IllegalArgumentException(String.format("Attempted to add conditional section for exit jump more than once! %s", node));
		}
		else if (conditionalSectionLength == 0) {
			conditionalSectionLength = null;
		}
		exitJumpConditionalSectionIds.add(routine.sectionId);
	}
}
