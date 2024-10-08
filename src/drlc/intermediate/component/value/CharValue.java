package drlc.intermediate.component.value;

import java.util.Objects;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.type.CharTypeInfo;

public class CharValue extends BasicValue<CharTypeInfo> {
	
	public final byte value;
	
	public CharValue(ASTNode<?> node, int value) {
		super(node, Main.generator.charTypeInfo);
		this.value = (byte) (value & 0x7F);
	}
	
	@Override
	public long longValue(ASTNode<?> node) {
		return value;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharValue other) {
			return typeInfo.equals(other.typeInfo) && value == other.value;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.charToString((char) Byte.toUnsignedInt(value));
	}
}
