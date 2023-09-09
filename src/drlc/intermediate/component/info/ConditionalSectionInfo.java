package drlc.intermediate.component.info;

import java.util.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.routine.Routine;

public class ConditionalSectionInfo {
	
	public boolean sectionStart = true;
	Boolean hasElseSection = null, executeIfCondition = null;
	
	Integer elseJumpSectionId = null;
	
	final Set<Integer> exitJumpSectionIds = new HashSet<>();
	
	public ConditionalSectionInfo() {
		
	}
	
	public void setHasElseSection(ASTNode node, boolean elseSection) {
		if (hasElseSection != null) {
			throw node.error("Else section boolean can not be overwritten!");
		}
		else {
			hasElseSection = elseSection;
		}
	}
	
	public boolean getHasElseSection(ASTNode node) {
		boolean elseSection = hasElseSection == null ? false : hasElseSection.booleanValue();
		hasElseSection = null;
		return elseSection;
	}
	
	public void setExecuteIfCondition(ASTNode node, boolean ifCondition) {
		if (executeIfCondition != null) {
			throw node.error("Execute if condition boolean can not be overwritten!");
		}
		else {
			executeIfCondition = ifCondition;
		}
	}
	
	public boolean getExecuteIfCondition(ASTNode node) {
		if (executeIfCondition == null) {
			throw node.error("Execute if condition boolean is null!");
		}
		else {
			boolean elseSection = executeIfCondition.booleanValue();
			executeIfCondition = null;
			return elseSection;
		}
	}
	
	public int getElseJumpSectionId(ASTNode node) {
		if (elseJumpSectionId == null) {
			throw node.error("Conditional section for else jump not defined!");
		}
		else {
			int sectionId = elseJumpSectionId;
			elseJumpSectionId = null;
			return sectionId;
		}
	}
	
	public void setElseJumpSectionId(ASTNode node, Routine routine) {
		if (elseJumpSectionId != null) {
			throw node.error("Conditional section for else jump can not be overwritten!");
		}
		else {
			elseJumpSectionId = routine.sectionId;
		}
	}
	
	public Integer[] getExitJumpSectionIds(ASTNode node) {
		if (exitJumpSectionIds.isEmpty()) {
			throw node.error("Attempted to get empty array of conditional sections for exit jump!");
		}
		else {
			Integer[] sections = exitJumpSectionIds.toArray(new Integer[0]);
			exitJumpSectionIds.clear();
			return sections;
		}
	}
	
	public void addExitJumpSection(ASTNode node, Routine routine) {
		if (exitJumpSectionIds.contains(routine.sectionId)) {
			throw node.error("Attempted to add conditional section for exit jump more than once!");
		}
		exitJumpSectionIds.add(routine.sectionId);
	}
}
