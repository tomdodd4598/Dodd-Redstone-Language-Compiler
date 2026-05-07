package drlc.intermediate.component.value;

import java.util.Objects;
import java.util.function.Consumer;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Helpers;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.type.FunctionItemTypeInfo;
import drlc.intermediate.scope.Scope;

public class FunctionItemValue extends Value<FunctionItemTypeInfo> {
	
	public final String name;
	public final Scope scope;
	
	public FunctionItemValue(ASTNode<?> node, @NonNull FunctionItemTypeInfo typeInfo, String name, Scope scope) {
		super(node, typeInfo);
		this.name = name;
		this.scope = scope;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(typeInfo, name, scope);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof FunctionItemValue other) {
			return typeInfo.equals(other.typeInfo) && name.equals(other.name) && scope.equals(other.scope);
		}
		else {
			return false;
		}
	}
	
	@Override
	public String valueString() {
		return Helpers.scopeStringPrefix(scope) + name;
	}
	
	@Override
	public @NonNull Function getDirectFunction() {
		return typeInfo.function;
	}
	
	@Override
	public void forEachFunction(Consumer<Function> consumer) {
		consumer.accept(typeInfo.function);
	}
}
