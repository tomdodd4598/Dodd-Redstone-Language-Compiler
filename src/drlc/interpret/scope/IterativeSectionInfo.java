package drlc.interpret.scope;

import java.util.*;

import drlc.node.Node;

public class IterativeSectionInfo {
	
	String continueJumpTargetSectionId = null;
	public final Set<Integer> continueJumpIterativeSectionIds = new HashSet<>();
	
	String bodyJumpTargetSectionId = null;
	public final Set<Integer> bodyJumpIterativeSectionIds = new HashSet<>();
	
	String breakJumpTargetSectionId = null;
	public final Set<Integer> breakJumpIterativeSectionIds = new HashSet<>();
	
	public IterativeSectionInfo() {}
	
	public void setContinueJumpTargetSectionId(Node node, String sectionId) {
		if (sectionId == null && continueJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative continue target section is already null! %s", node));
		}
		else if (sectionId != null && continueJumpTargetSectionId != null) {
			throw new IllegalArgumentException(String.format("Iterative continue target section can not be overwritten! %s", node));
		}
		else {
			continueJumpTargetSectionId = sectionId;
		}
	}
	
	public String getContinueJumpTargetSectionId(Node node) {
		if (continueJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative continue target section is null! %s", node));
		}
		else {
			return continueJumpTargetSectionId;
		}
	}
	
	public Integer[] getContinueJumpIterativeSectionIds(Node node) {
		if (continueJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative continue target section was unexpectedly null while getting continue jump sections array! %s", node));
		}
		else if (bodyJumpTargetSectionId == null && !bodyJumpIterativeSectionIds.isEmpty()) {
			throw new IllegalArgumentException(String.format("Iterative body target section was unexpectedly null while getting continue jump sections array! %s", node));
		}
		else if (breakJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative break target section was unexpectedly null while getting continue jump sections array! %s", node));
		}
		else {
			Integer[] sections = continueJumpIterativeSectionIds.toArray(new Integer[continueJumpIterativeSectionIds.size()]);
			continueJumpIterativeSectionIds.clear();
			return sections;
		}
	}
	
	public void setBodyJumpTargetSectionId(Node node, String sectionId) {
		if (sectionId == null && bodyJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative body target section is already null! %s", node));
		}
		else if (sectionId != null && bodyJumpTargetSectionId != null) {
			throw new IllegalArgumentException(String.format("Iterative body target section can not be overwritten! %s", node));
		}
		else {
			bodyJumpTargetSectionId = sectionId;
		}
	}
	
	public String getBodyJumpTargetSectionId(Node node) {
		if (bodyJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative body target section is null! %s", node));
		}
		else {
			return bodyJumpTargetSectionId;
		}
	}
	
	public Integer[] getBodyJumpIterativeSectionIds(Node node) {
		if (continueJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative continue target section was unexpectedly null while getting body jump sections array! %s", node));
		}
		else if (bodyJumpTargetSectionId == null && !bodyJumpIterativeSectionIds.isEmpty()) {
			throw new IllegalArgumentException(String.format("Iterative body target section was unexpectedly null while getting body jump sections array! %s", node));
		}
		else if (breakJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative break target section was unexpectedly null while getting body jump sections array! %s", node));
		}
		else {
			Integer[] sections = bodyJumpIterativeSectionIds.toArray(new Integer[bodyJumpIterativeSectionIds.size()]);
			bodyJumpIterativeSectionIds.clear();
			return sections;
		}
	}
	
	public void setBreakJumpTargetSectionId(Node node, String sectionId) {
		if (sectionId == null && breakJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative break target section is already null! %s", node));
		}
		else if (sectionId != null && breakJumpTargetSectionId != null) {
			throw new IllegalArgumentException(String.format("Iterative break target section can not be overwritten! %s", node));
		}
		else {
			breakJumpTargetSectionId = sectionId;
		}
	}
	
	public String getBreakJumpTargetSectionId(Node node) {
		if (breakJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative break target section is null! %s", node));
		}
		else {
			return breakJumpTargetSectionId;
		}
	}
	
	public Integer[] getBreakJumpIterativeSectionIds(Node node) {
		if (continueJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative continue target section was unexpectedly null while getting break jump sections array! %s", node));
		}
		else if (bodyJumpTargetSectionId == null && !bodyJumpIterativeSectionIds.isEmpty()) {
			throw new IllegalArgumentException(String.format("Iterative body target section was unexpectedly null while getting break jump sections array! %s", node));
		}
		else if (breakJumpTargetSectionId == null) {
			throw new IllegalArgumentException(String.format("Iterative break target section was unexpectedly null while getting break jump sections array! %s", node));
		}
		else {
			Integer[] sections = breakJumpIterativeSectionIds.toArray(new Integer[breakJumpIterativeSectionIds.size()]);
			breakJumpIterativeSectionIds.clear();
			return sections;
		}
	}
}
