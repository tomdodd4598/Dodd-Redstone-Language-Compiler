package drlc.intermediate.component;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.component.value.Value;
import drlc.intermediate.scope.Scope;

public class Constant {
	
	public final @NonNull String name;
	public final @NonNull Value value;
	
	public Scope scope;
	
	public Constant(@NonNull String name, @NonNull Value value) {
		this.name = name;
		this.value = value;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, value, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Constant) {
			Constant other = (Constant) obj;
			return name.equals(other.name) && value.equals(other.value) && scope.equals(other.scope);
		}
		else {
			return false;
		}
	}
}
