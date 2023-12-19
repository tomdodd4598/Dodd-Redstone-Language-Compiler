package drlc.intermediate.component.type;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.TypeInfoFunction;
import drlc.intermediate.scope.Scope;

public class RawType {
	
	public final @NonNull String name;
	public final int size;
	
	private final @NonNull TypeInfoFunction supplier;
	
	public RawType(@NonNull String name, int size, @NonNull TypeInfoFunction supplier) {
		this.name = name;
		this.size = size;
		this.supplier = supplier;
	}
	
	public @NonNull TypeInfo getTypeInfo(ASTNode<?, ?> node, Scope scope, int referenceLevel) {
		return supplier.create(node, scope, referenceLevel);
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, size);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof RawType) {
			RawType other = (RawType) obj;
			return name.equals(other.name) && size == other.size;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
