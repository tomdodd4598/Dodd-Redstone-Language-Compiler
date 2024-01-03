package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class BasicTypeInfo extends TypeInfo {
	
	public final @NonNull RawType rawType;
	
	protected BasicTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, @NonNull RawType rawType) {
		super(node, referenceMutability);
		this.rawType = rawType;
	}
	
	public BasicTypeInfo(ASTNode<?, ?> node, List<Boolean> referenceMutability, Scope scope, @NonNull String rawTypeName) {
		this(node, referenceMutability, scope.getRawType(node, rawTypeName));
	}
	
	@Override
	public boolean exists(Scope scope) {
		return scope.rawTypeExists(rawType.name, false);
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : rawType.size;
	}
	
	@Override
	public void collectRawTypes(Set<RawType> rawTypes) {
		if (!isAddress()) {
			rawTypes.add(rawType);
		}
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, rawType);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof BasicTypeInfo) {
			BasicTypeInfo other = (BasicTypeInfo) obj;
			boolean equalReferenceMutability = ignoreReferenceMutability || referenceMutability.equals(other.referenceMutability);
			return rawType.equals(other.rawType) && equalReferenceMutability;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return rawType.toString();
	}
}
