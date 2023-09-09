package drlc.intermediate.component.type;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.scope.Scope;

public abstract class BasicTypeInfo extends TypeInfo {
	
	public final @NonNull RawType rawType;
	
	protected BasicTypeInfo(ASTNode node, @NonNull RawType rawType, int referenceLevel) {
		super(node, referenceLevel);
		this.rawType = rawType;
	}
	
	public BasicTypeInfo(ASTNode node, Scope scope, @NonNull String rawTypeName, int referenceLevel) {
		this(node, scope.getRawType(node, rawTypeName), referenceLevel);
	}
	
	@Override
	public boolean exists(Scope scope) {
		return scope.rawTypeExists(rawType.name);
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : rawType.size;
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
