package drlc.intermediate.component.info;

import java.util.*;

import drlc.intermediate.ast.ASTNode;

public class IterativeSectionInfo {
	
	Integer continueJumpTargetSectionId = null;
	public final Set<Integer> continueJumpIterativeSectionIds = new HashSet<>();
	
	Integer bodyJumpTargetSectionId = null;
	public final Set<Integer> bodyJumpIterativeSectionIds = new HashSet<>();
	
	Integer breakJumpTargetSectionId = null;
	public final Set<Integer> breakJumpIterativeSectionIds = new HashSet<>();
	
	public IterativeSectionInfo() {
		
	}
	
	public void setContinueJumpTargetSectionId(ASTNode node, Integer sectionId) {
		if (sectionId == null && continueJumpTargetSectionId == null) {
			throw node.error("Iterative continue target section is already null!");
		}
		else if (sectionId != null && continueJumpTargetSectionId != null) {
			throw node.error("Iterative continue target section can not be overwritten!");
		}
		else {
			continueJumpTargetSectionId = sectionId;
		}
	}
	
	public Integer getContinueJumpTargetSectionId(ASTNode node) {
		if (continueJumpTargetSectionId == null) {
			throw node.error("Iterative continue target section is null!");
		}
		else {
			return continueJumpTargetSectionId;
		}
	}
	
	public Integer[] getContinueJumpIterativeSectionIds(ASTNode node) {
		if (continueJumpTargetSectionId == null) {
			throw node.error("Iterative continue target section was unexpectedly null while getting continue jump sections array!");
		}
		else if (bodyJumpTargetSectionId == null && !bodyJumpIterativeSectionIds.isEmpty()) {
			throw node.error("Iterative body target section was unexpectedly null while getting continue jump sections array!");
		}
		else if (breakJumpTargetSectionId == null) {
			throw node.error("Iterative break target section was unexpectedly null while getting continue jump sections array!");
		}
		else {
			Integer[] sections = continueJumpIterativeSectionIds.toArray(new Integer[0]);
			continueJumpIterativeSectionIds.clear();
			return sections;
		}
	}
	
	public void setBodyJumpTargetSectionId(ASTNode node, Integer sectionId) {
		if (sectionId == null && bodyJumpTargetSectionId == null) {
			throw node.error("Iterative body target section is already null!");
		}
		else if (sectionId != null && bodyJumpTargetSectionId != null) {
			throw node.error("Iterative body target section can not be overwritten!");
		}
		else {
			bodyJumpTargetSectionId = sectionId;
		}
	}
	
	public Integer getBodyJumpTargetSectionId(ASTNode node) {
		if (bodyJumpTargetSectionId == null) {
			throw node.error("Iterative body target section is null!");
		}
		else {
			return bodyJumpTargetSectionId;
		}
	}
	
	public Integer[] getBodyJumpIterativeSectionIds(ASTNode node) {
		if (continueJumpTargetSectionId == null) {
			throw node.error("Iterative continue target section was unexpectedly null while getting body jump sections array!");
		}
		else if (bodyJumpTargetSectionId == null && !bodyJumpIterativeSectionIds.isEmpty()) {
			throw node.error("Iterative body target section was unexpectedly null while getting body jump sections array!");
		}
		else if (breakJumpTargetSectionId == null) {
			throw node.error("Iterative break target section was unexpectedly null while getting body jump sections array!");
		}
		else {
			Integer[] sections = bodyJumpIterativeSectionIds.toArray(new Integer[0]);
			bodyJumpIterativeSectionIds.clear();
			return sections;
		}
	}
	
	public void setBreakJumpTargetSectionId(ASTNode node, Integer sectionId) {
		if (sectionId == null && breakJumpTargetSectionId == null) {
			throw node.error("Iterative break target section is already null!");
		}
		else if (sectionId != null && breakJumpTargetSectionId != null) {
			throw node.error("Iterative break target section can not be overwritten!");
		}
		else {
			breakJumpTargetSectionId = sectionId;
		}
	}
	
	public Integer getBreakJumpTargetSectionId(ASTNode node) {
		if (breakJumpTargetSectionId == null) {
			throw node.error("Iterative break target section is null!");
		}
		else {
			return breakJumpTargetSectionId;
		}
	}
	
	public Integer[] getBreakJumpIterativeSectionIds(ASTNode node) {
		if (continueJumpTargetSectionId == null) {
			throw node.error("Iterative continue target section was unexpectedly null while getting break jump sections array!");
		}
		else if (bodyJumpTargetSectionId == null && !bodyJumpIterativeSectionIds.isEmpty()) {
			throw node.error("Iterative body target section was unexpectedly null while getting break jump sections array!");
		}
		else if (breakJumpTargetSectionId == null) {
			throw node.error("Iterative break target section was unexpectedly null while getting break jump sections array!");
		}
		else {
			Integer[] sections = breakJumpIterativeSectionIds.toArray(new Integer[0]);
			breakJumpIterativeSectionIds.clear();
			return sections;
		}
	}
}
