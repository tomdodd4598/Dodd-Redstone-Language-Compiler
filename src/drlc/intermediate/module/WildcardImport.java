package drlc.intermediate.module;

import java.util.*;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Path;
import drlc.intermediate.scope.Scope;

public class WildcardImport {
	
	public final @NonNull Scope scope;
	public final @NonNull Path path;
	
	protected final Set<String> resolvingModuleNames = new HashSet<>();
	protected final Set<String> resolvingTypeNames = new HashSet<>();
	protected final Set<String> resolvingValueNames = new HashSet<>();
	
	public WildcardImport(@NonNull Scope scope, @NonNull Path path) {
		this.scope = scope;
		this.path = path;
	}
	
	private @Nullable Scope tryGetTargetScope(ASTNode<?> node) {
		return scope.tryGetPathScope(node, path, null);
	}
	
	public @Nullable Scope tryGetModule(ASTNode<?> node, String name) {
		if (!resolvingModuleNames.add(name)) {
			return null;
		}
		try {
			Scope targetScope = tryGetTargetScope(node);
			return targetScope == null ? null : targetScope.tryGetLocalModule(node, name, null);
		}
		finally {
			resolvingModuleNames.remove(name);
		}
	}
	
	public @Nullable TypeEntry tryGetTypeEntry(ASTNode<?> node, String name) {
		if (!resolvingTypeNames.add(name)) {
			return null;
		}
		try {
			Scope targetScope = tryGetTargetScope(node);
			return targetScope == null ? null : targetScope.tryGetLocalTypeEntry(node, name, null);
		}
		finally {
			resolvingTypeNames.remove(name);
		}
	}
	
	public @Nullable ValueEntry tryGetValueEntry(ASTNode<?> node, String name) {
		if (!resolvingValueNames.add(name)) {
			return null;
		}
		try {
			Scope targetScope = tryGetTargetScope(node);
			return targetScope == null ? null : targetScope.tryGetLocalValueEntry(node, name, null);
		}
		finally {
			resolvingValueNames.remove(name);
		}
	}
	
	public void collectModuleNames(ASTNode<?> node, Set<String> names, Set<WildcardImport> visited) {
		if (visited.add(this)) {
			Scope targetScope = tryGetTargetScope(node);
			if (targetScope != null) {
				targetScope.collectLocalModuleNames(node, names, visited);
			}
		}
	}
	
	public void collectTypeNames(ASTNode<?> node, Set<String> names, Set<WildcardImport> visited) {
		if (visited.add(this)) {
			Scope targetScope = tryGetTargetScope(node);
			if (targetScope != null) {
				targetScope.collectLocalTypeNames(node, names, visited);
			}
		}
	}
	
	public void collectValueNames(ASTNode<?> node, Set<String> names, Set<WildcardImport> visited) {
		if (visited.add(this)) {
			Scope targetScope = tryGetTargetScope(node);
			if (targetScope != null) {
				targetScope.collectLocalValueNames(node, names, visited);
			}
		}
	}
	
	public void check(ASTNode<?> node) {
		scope.getPathScope(node, path, null);
		
		Set<String> moduleNames = new LinkedHashSet<>();
		collectModuleNames(node, moduleNames, new HashSet<>());
		for (String name : moduleNames) {
			scope.tryGetLocalModule(node, name, null);
		}
		
		Set<String> typeNames = new LinkedHashSet<>();
		collectTypeNames(node, typeNames, new HashSet<>());
		for (String name : typeNames) {
			scope.tryGetLocalTypeEntry(node, name, null);
		}
		
		Set<String> valueNames = new LinkedHashSet<>();
		collectValueNames(node, valueNames, new HashSet<>());
		for (String name : valueNames) {
			scope.tryGetLocalValueEntry(node, name, null);
		}
	}
}
