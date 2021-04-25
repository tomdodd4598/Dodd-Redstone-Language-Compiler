package drlc.interpret.action;

import java.util.*;

import drlc.*;
import drlc.node.Node;

public class FunctionCallAction extends SubroutineCallAction implements IValueAction {
	
	public final String target;
	
	public FunctionCallAction(Node node, String target, String name, String... args) {
		super(node, name, args);
		if (target == null) {
			throw new IllegalArgumentException(String.format("Function call action target was null! %s", node));
		}
		else {
			this.target = target;
		}
	}
	
	protected FunctionCallAction copy(Node node, String target, String name, String... args) {
		return new FunctionCallAction(node, target, name, args);
	}
	
	@Override
	public String[] lValues() {
		return new String[] {target};
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
				return copy(null, target, name, replaceArgs);
			}
		}
		throw new IllegalArgumentException(String.format("No function call action argument %s matched replacement target %s!", Arrays.toString(args), replaceTarget));
	}
	
	@Override
	public boolean canReplaceLValue() {
		return true;
	}
	
	@Override
	public String getLValueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceLValue(String replaceTarget, String lValueReplacer) {
		if (target.equals(replaceTarget)) {
			return copy(null, lValueReplacer, name, args);
		}
		else {
			throw new IllegalArgumentException(String.format("Function call action target %s doesn't match replacement target %s!", target, replaceTarget));
		}
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
		return new FunctionCallAction(null, target, name, swapArgs);
	}
	
	@Override
	public Action replaceRegIds(Map<String, String> regIdMap) {
		String target = this.target;
		String[] args = this.args;
		if (Helper.isRegId(target) && regIdMap.containsKey(target)) {
			target = regIdMap.get(target);
		}
		for (int i = 0; i < args.length; i++) {
			if (Helper.isRegId(args[i]) && regIdMap.containsKey(args[i])) {
				args[i] = regIdMap.get(args[i]);
			}
		}
		
		if (!target.equals(this.target)) {
			return copy(null, target, name, args);
		}
		for (int i = 0; i < args.length; i++) {
			if (!args[i].equals(this.args[i])) {
				return copy(null, target, name, args);
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(target).append(" = ").append(Global.FUN).append(" ").append(name);
		for (String arg : args) {
			builder.append(" ").append(arg);
		}
		return builder.toString();
	}
}
