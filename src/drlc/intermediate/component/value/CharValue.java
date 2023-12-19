package drlc.intermediate.component.value;

import java.util.Objects;

import drlc.*;
import drlc.intermediate.ast.ASTNode;

public class CharValue extends BasicValue {
	
	public final byte value;
	
	public CharValue(ASTNode<?, ?> node, char value) {
		super(node, Main.generator.charTypeInfo);
		this.value = (byte) value;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, value);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof CharValue) {
			CharValue other = (CharValue) obj;
			return typeInfo.equals(other.typeInfo) && value == other.value;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.charToString((char) value);
	}
}
