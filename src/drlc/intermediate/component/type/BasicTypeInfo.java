package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class BasicTypeInfo extends TypeInfo {
	
	public final @NonNull RawType rawType;
	
	protected BasicTypeInfo(ASTNode<?, ?> node, int referenceLevel, @NonNull RawType rawType) {
		super(node, referenceLevel);
		this.rawType = rawType;
		
		if (referenceLevel < 0) {
			throw Helpers.nodeError(node, "Reference level of type \"%s\" can not be negative!", rawString());
		}
	}
	
	public BasicTypeInfo(ASTNode<?, ?> node, int referenceLevel, Scope scope, @NonNull String rawTypeName) {
		this(node, referenceLevel, scope.getRawType(node, rawTypeName));
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
		return Objects.hash(referenceLevel, rawType);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceLevels) {
		if (obj instanceof BasicTypeInfo) {
			BasicTypeInfo other = (BasicTypeInfo) obj;
			boolean equalReferenceLevels = ignoreReferenceLevels || referenceLevel == other.referenceLevel;
			return rawType.equals(other.rawType) && equalReferenceLevels;
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
