package drlc.intermediate.ast.section;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.Source;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.scope.Scope;

public class StructDefinitionNode extends StaticSectionNode<Scope> {
	
	public final @NonNull String name;
	public final @NonNull List<DeclaratorNode> componentNodes;
	public final boolean tupleStruct;
	protected final @NonNull Map<String, MemberInfo> memberMap = new LinkedHashMap<>();
	protected final @NonNull List<TypeInfo> typeInfos = new ArrayList<>();
	protected boolean valueConstructorDeclared = false;
	
	@SuppressWarnings("null")
	public @NonNull TypeDef typeDef = null;
	
	public StructDefinitionNode(Source source, @NonNull String name, @NonNull List<DeclaratorNode> componentNodes, boolean tupleStruct) {
		super(source);
		this.name = name;
		this.componentNodes = componentNodes;
		this.tupleStruct = tupleStruct;
		
		for (DeclaratorNode componentNode : componentNodes) {
			if (componentNode.typeNode == null) {
				throw error("Struct component types must be explicitly defined!");
			}
		}
	}
	
	@Override
	public void setScopes(ASTNode<?> parent) {
		scope = parent.scope;
		
		for (DeclaratorNode componentNode : componentNodes) {
			componentNode.setScopes(this);
		}
	}
	
	@SuppressWarnings("unused")
	@Override
	public void declareTypes(ASTNode<?> parent) {
		if (typeDef == null) {
			typeDef = new TypeDef(name, 0, memberMap, (n, r) -> new StructTypeInfo(n, r, typeInfos, typeDef));
			
			scope.addTypeDef(this, typeDef.name, typeDef);
		}
	}
	
	@Override
	public void defineTypes(ASTNode<?> parent) {
		declareTypes(parent);
		
		for (DeclaratorNode componentNode : componentNodes) {
			componentNode.defineTypes(this);
		}
		
		Set<TypeDef> typeDefs = new HashSet<>();
		for (DeclaratorNode componentNode : componentNodes) {
			componentNode.typeNode.collectTypeDefs(typeDefs);
		}
		if (typeDefs.contains(typeDef)) {
			throw error("Struct \"%s\" can not directly contain itself!", name);
		}
		
		typeInfos.clear();
		memberMap.clear();
		
		for (DeclaratorNode componentNode : componentNodes) {
			componentNode.typeNode.setTypeInfo();
		}
		
		for (DeclaratorNode componentNode : componentNodes) {
			typeInfos.add(componentNode.typeNode.getTypeInfo());
		}
		
		try {
			int size = 0;
			for (TypeInfo typeInfo : typeInfos) {
				size = Math.addExact(size, typeInfo.getSize());
			}
			typeDef.size = size;
		}
		catch (ArithmeticException e) {
			throw error("Size of struct \"%s\" is too large!", name);
		}
		
		int count = componentNodes.size(), offset = 0;
		for (int i = 0; i < count; ++i) {
			@NonNull String memberName = componentNodes.get(i).name;
			if (memberMap.containsKey(memberName)) {
				throw error("Struct \"%s\" already has member \"%s\"!", name, memberName);
			}
			else {
				@NonNull TypeInfo typeInfo = typeInfos.get(i);
				memberMap.put(memberName, new MemberInfo(memberName, typeInfo, i, offset));
				try {
					offset = Math.addExact(offset, typeInfo.getSize());
				}
				catch (ArithmeticException e) {
					throw error("Offset of member \"%s\" in struct \"%s\" is too large!", memberName, name);
				}
			}
		}
		
		declareValueConstructor();
	}
	
	protected void declareValueConstructor() {
		if (valueConstructorDeclared || (!tupleStruct && !componentNodes.isEmpty())) {
			return;
		}
		
		StructTypeInfo structTypeInfo = (StructTypeInfo) typeDef.getTypeInfo(this, new ArrayList<>());
		Value<?> value = componentNodes.isEmpty() ? new StructValue(this, structTypeInfo, new ArrayList<>()) : new StructConstructorValue(this, new StructConstructorTypeInfo(this, structTypeInfo), name, scope);
		scope.addConstant(this, name, new Constant(name, value));
		
		valueConstructorDeclared = true;
	}
	
	@Override
	public void declareExpressions(ASTNode<?> parent) {
		routine = parent.routine;
	}
	
	@Override
	public void defineExpressions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void checkTypes(ASTNode<?> parent) {
		
	}
	
	@Override
	public void foldConstants(ASTNode<?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		routine.typeDefMap.put(typeDef.toString(), typeDef.getTypeInfo(this, new ArrayList<>()));
	}
}
