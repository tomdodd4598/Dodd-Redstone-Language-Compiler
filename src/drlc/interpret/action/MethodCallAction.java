package drlc.interpret.action;

import java.util.*;

import drlc.*;
import drlc.node.Node;

public class MethodCallAction extends SubroutineCallAction implements IValueAction {
	
	public MethodCallAction(Node node, String name, String... args) {
		super(node, name, args);
	}
	
	protected MethodCallAction copy(Node node, String name, String... args) {
		return new MethodCallAction(node, name, args);
	}
	
	@Override
	public String[] lValues() {
		return new String[] {};
	}
	
	@Override
	public String[] rValues() {
		return args;
	}
	
	@Override
	public boolean canRemove() {
		return false;
	}
	
	@Override
	public boolean canReplaceRValue() {
		return true;
	}
	
	@Override
	public String getRValueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceRValue(String replaceTarget, String rValueReplacer) {
		String[] replaceArgs = Arrays.copyOf(args, args.length);
		for (int i = 0; i < args.length; i++) {
			if (args[i].equals(replaceTarget)) {
				replaceArgs[i] = rValueReplacer;
				return copy(null, name, replaceArgs);
			}
		}
		throw new IllegalArgumentException(String.format("No method call action argument %s matched replacement target %s!", Arrays.toString(args), replaceTarget));
	}
	
	@Override
	public boolean canReplaceLValue() {
		return false;
	}
	
	@Override
	public String getLValueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceLValue(String replaceTarget, String lValueReplacer) {
		return null;
	}
	
	@Override
	public boolean canReorderRValues() {
		return false;
	}
	
	@Override
	public Action swapRValues(int i, int j) {
		String[] swapArgs = Arrays.copyOf(args, args.length);
		swapArgs[i] = args[j];
		swapArgs[j] = args[i];
		return new MethodCallAction(null, name, swapArgs);
	}
	
	@Override
	public Action replaceRegIds(Map<String, String> regIdMap) {
		String[] args = this.args;
		for (int i = 0; i < args.length; i++) {
			if (Helper.isRegId(args[i]) && regIdMap.containsKey(args[i])) {
				args[i] = regIdMap.get(args[i]);
			}
		}
		
		for (int i = 0; i < args.length; i++) {
			if (!args[i].equals(this.args[i])) {
				return copy(null, name, args);
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(Global.CALL).append(" ").append(name);
		for (String arg : args) {
			builder.append(" ").append(arg);
		}
		return builder.toString();
	}
}
