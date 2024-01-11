package drlc.intermediate.component.value;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.TypeInfo;

public class AddressValue extends Value<TypeInfo> {
	
	public final long address;
	
	public AddressValue(ASTNode<?> node, @NonNull TypeInfo typeInfo, long address) {
		super(node, typeInfo);
		this.address = address;
	}
	
	@Override
	public long longValue(ASTNode<?> node) {
		return address;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, address);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof AddressValue) {
			AddressValue other = (AddressValue) obj;
			return typeInfo.equals(other.typeInfo) && address == other.address;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.toHex(address);
	}
}
