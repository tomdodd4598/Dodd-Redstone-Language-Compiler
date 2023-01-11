package drlc.intermediate.action;

import java.util.*;
import java.util.stream.Stream;

import drlc.*;
import drlc.intermediate.component.DataId;
import drlc.node.Node;

public class FunctionCallAction extends Action implements IValueAction {
	
	public final DataId target;
	public final DataId[] rvalues;
	
	public FunctionCallAction(Node node, DataId target, DataId name, DataId[] args) {
		super(node);
		if (name == null) {
			throw new IllegalArgumentException(String.format("Function call action name was null! %s", node));
		}
		else if (args == null) {
			throw new IllegalArgumentException(String.format("Function call action arguments were null! %s", node));
		}
		else {
			rvalues = new DataId[1 + args.length];
			rvalues[0] = name;
			for (int i = 0; i < args.length; ++i) {
				rvalues[i + 1] = args[i];
			}
		}
		if (target == null) {
			throw new IllegalArgumentException(String.format("Function call action target was null! %s", node));
		}
		else {
			this.target = target;
		}
	}
	
	public DataId getCallId() {
		return rvalues[0];
	}
	
	public DataId[] getArgs() {
		return Arrays.copyOfRange(rvalues, 1, rvalues.length);
	}
	
	public DataId getArg(int i) {
		return rvalues[1 + i];
	}
	
	protected FunctionCallAction copy(Node node, DataId target, DataId[] rvalues) {
		return new FunctionCallAction(node, target, rvalues[0], Arrays.copyOfRange(rvalues, 1, rvalues.length));
	}
	
	@Override
	public DataId[] lvalues() {
		return new DataId[] {target};
	}
	
	@Override
	public DataId[] rvalues() {
		return rvalues;
	}
	
	@Override
	public boolean canRemove() {
		return false;
	}
	
	@Override
	public boolean canReplaceRvalue() {
		return true;
	}
	
	@Override
	public DataId getRvalueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceRvalue(DataId replaceTarget, DataId rvalueReplacer) {
		DataId[] replaceRvalues = Arrays.copyOf(rvalues, rvalues.length);
		for (int i = 0; i < rvalues.length; ++i) {
			if (rvalues[i].equals(replaceTarget)) {
				replaceRvalues[i] = rvalueReplacer;
				return copy(null, target, replaceRvalues);
			}
		}
		throw new IllegalArgumentException(String.format("No function call action argument %s matched replacement target %s!", Arrays.toString(rvalues), replaceTarget));
	}
	
	@Override
	public boolean canReplaceLvalue() {
		return true;
	}
	
	@Override
	public DataId getLvalueReplacer() {
		return null;
	}
	
	@Override
	public Action replaceLvalue(DataId replaceTarget, DataId lvalueReplacer) {
		if (target.equals(replaceTarget)) {
			return copy(null, lvalueReplacer, rvalues);
		}
		else {
			throw new IllegalArgumentException(String.format("Function call action target %s doesn't match replacement target %s!", target, replaceTarget));
		}
	}
	
	@Override
	public boolean canReorderRvalues() {
		return false;
	}
	
	@Override
	public Action swapRvalues(int i, int j) {
		DataId[] swapRvalues = Arrays.copyOf(rvalues, rvalues.length);
		swapRvalues[i] = rvalues[j];
		swapRvalues[j] = rvalues[i];
		return copy(null, target, swapRvalues);
	}
	
	@Override
	public Action replaceRegIds(Map<DataId, DataId> regIdMap) {
		DataId replaceTarget = target.removeAllDereferences();
		DataId[] replaceRvalues = Stream.of(rvalues).map(DataId::removeAllDereferences).toArray(DataId[]::new);
		if (Helpers.isRegId(replaceTarget.raw) && regIdMap.containsKey(replaceTarget)) {
			replaceTarget = regIdMap.get(replaceTarget);
		}
		for (int i = 0; i < replaceRvalues.length; ++i) {
			if (Helpers.isRegId(replaceRvalues[i].raw) && regIdMap.containsKey(replaceRvalues[i])) {
				replaceRvalues[i] = regIdMap.get(replaceRvalues[i]);
			}
		}
		
		replaceTarget = replaceTarget.addDereferences(target.dereferenceLevel);
		for (int i = 0; i < replaceRvalues.length; ++i) {
			replaceRvalues[i] = replaceRvalues[i].addDereferences(rvalues[i].dereferenceLevel);
		}
		
		if (!replaceTarget.equalsOther(target, true)) {
			return copy(null, replaceTarget, replaceRvalues);
		}
		for (int i = 0; i < replaceRvalues.length; ++i) {
			if (!replaceRvalues[i].equalsOther(rvalues[i], true)) {
				return copy(null, replaceTarget, replaceRvalues);
			}
		}
		return null;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(target.raw).append(" = ").append(Global.CALL).append(' ').append(getCallId().raw);
		Helpers.appendArgs(builder, getArgs());
		return builder.toString();
	}
}
