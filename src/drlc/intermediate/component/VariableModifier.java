package drlc.intermediate.component;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Global;

public class VariableModifier {
	
	public static final @NonNull VariableModifier ROOT = new VariableModifier(true, true);
	public static final @NonNull VariableModifier BUILT_IN = new VariableModifier(false, true);
	
	public final boolean _static, mutable;
	
	public VariableModifier(boolean _static, boolean mutable) {
		this._static = _static;
		this.mutable = mutable;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(_static, mutable);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof VariableModifier other) {
			return _static == other._static && mutable == other.mutable;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		if (_static) {
			sb.append(Global.STATIC).append(" ");
		}
		if (mutable) {
			sb.append(Global.MUT).append(" ");
		}
		return sb.toString();
	}
	
	public String routineString() {
		StringBuilder sb = new StringBuilder();
		if (_static) {
			sb.append(Global.STATIC).append(" ");
		}
		return sb.toString();
	}
}
