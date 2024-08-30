package drlc.intermediate.component.type;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Main;
import drlc.intermediate.ast.ASTNode;

public abstract class BasicTypeInfo extends TypeInfo {
	
	public final @NonNull String name;
	public int size;
	
	public BasicTypeInfo(ASTNode<?> node, List<Boolean> referenceMutability, @NonNull String name, int size) {
		super(node, referenceMutability);
		this.name = name;
		this.size = size;
	}
	
	@Override
	public int getSize() {
		return isAddress() ? Main.generator.getAddressSize() : size;
	}
	
	@Override
	public void collectTypeDefs(Set<TypeDef> typeDefs) {}
	
	@Override
	public int hashCode() {
		return Objects.hash(referenceMutability, name, size);
	}
	
	@Override
	public boolean equalsOther(Object obj, boolean ignoreReferenceMutability) {
		if (obj instanceof BasicTypeInfo other) {
			boolean equalReferenceMutability = ignoreReferenceMutability || referenceMutability.equals(other.referenceMutability);
			return name.equals(other.name) && size == other.size && equalReferenceMutability;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String rawString() {
		return name;
	}
}
