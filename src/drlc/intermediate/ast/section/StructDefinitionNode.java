package drlc.intermediate.ast.section;

import java.util.*;

import org.eclipse.jdt.annotation.NonNull;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.ast.element.DeclaratorNode;
import drlc.intermediate.component.MemberInfo;
import drlc.intermediate.component.type.*;
import drlc.intermediate.scope.Scope;

public class StructDefinitionNode extends StaticSectionNode<Scope> {
	
	public final @NonNull String name;
	public final @NonNull List<DeclaratorNode> componentNodes;
	
	@SuppressWarnings("null")
	public @NonNull TypeDef typeDef = null;
	
	public StructDefinitionNode(Source source, @NonNull String name, @NonNull List<DeclaratorNode> componentNodes) {
		super(source);
		this.name = name;
		this.componentNodes = componentNodes;
		
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
	
	@SuppressWarnings("null")
	@Override
	public void defineTypes(ASTNode<?> parent) {
		Map<String, MemberInfo> memberMap = new LinkedHashMap<>();
		List<TypeInfo> typeInfos = new ArrayList<>();
		typeDef = new TypeDef(name, 0, memberMap, (n, r, s) -> new StructTypeInfo(n, r, typeInfos, s, name));
		
		scope.addTypeDef(this, typeDef);
		
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
		
		for (DeclaratorNode componentNode : componentNodes) {
			componentNode.typeNode.setTypeInfo();
		}
		
		for (DeclaratorNode componentNode : componentNodes) {
			typeInfos.add(componentNode.typeNode.getTypeInfo());
		}
		
		typeDef.size = Helpers.sumToInt(typeInfos, TypeInfo::getSize);
		
		int count = componentNodes.size(), offset = 0;
		for (int i = 0; i < count; ++i) {
			@NonNull String memberName = componentNodes.get(i).name;
			if (memberMap.containsKey(memberName)) {
				throw error("Struct \"%s\" already has member \"%s\"!", name, memberName);
			}
			else {
				@NonNull TypeInfo typeInfo = typeInfos.get(i);
				memberMap.put(memberName, new MemberInfo(memberName, typeInfo, i, offset));
				offset += typeInfo.getSize();
			}
		}
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
	public void trackFunctions(ASTNode<?> parent) {
		
	}
	
	@Override
	public void generateIntermediate(ASTNode<?> parent) {
		routine.typeDefMap.put(typeDef.toString(), typeDef.getTypeInfo(this, new ArrayList<>(), scope));
	}
}
