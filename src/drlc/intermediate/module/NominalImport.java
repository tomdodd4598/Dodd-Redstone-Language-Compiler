package drlc.intermediate.module;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Path;
import drlc.intermediate.scope.Scope;

public class NominalImport {
	
	public final @NonNull Scope scope;
	public final @NonNull Path path;
	public final @Nullable String alias;
	
	protected boolean resolvingModule = false;
	protected boolean resolvingTypeEntry = false;
	protected boolean resolvingValueEntry = false;
	
	public NominalImport(@NonNull Scope scope, @NonNull Path path, @Nullable String alias) {
		this.scope = scope;
		this.path = path;
		this.alias = alias;
	}
	
	private boolean isSpecialPathSegment(String segment) {
		return segment.equals(Global.ROOT) || segment.equals(Global.SELF) || segment.equals(Global.SUPER);
	}
	
	private boolean isSpecialModuleImport() {
		return isSpecialPathSegment(path.name);
	}
	
	private void checkSpecialModuleImportPath(ASTNode<?> node) {
		if (path.name.equals(Global.SELF)) {
			return;
		}
		for (String segment : path.prefix) {
			if (!isSpecialPathSegment(segment)) {
				throw Helpers.nodeError(node, "Special path segment \"%s\" must appear before standard path segments in import \"%s\"!", path.name, path);
			}
		}
	}
	
	public @NonNull String resolveModuleLocalName(@Nullable Scope module) {
		if (alias != null) {
			return alias;
		}
		if (path.name.equals(Global.SELF)) {
			for (int i = path.prefix.size() - 1; i >= 0; --i) {
				String segment = path.prefix.get(i);
				if (!isSpecialPathSegment(segment)) {
					return segment;
				}
			}
		}
		return module == null ? path.name : module.name;
	}
	
	public @NonNull String resolveLocalName(ASTNode<?> node) {
		if (alias != null) {
			return alias;
		}
		if (!isSpecialModuleImport()) {
			return path.name;
		}
		return resolveModuleLocalName(resolveModule(node));
	}
	
	public @Nullable Scope tryResolveModuleAsLocalName(ASTNode<?> node, String name) {
		Scope module = tryResolveModule(node);
		if (module == null || !resolveModuleLocalName(module).equals(name)) {
			return null;
		}
		return module;
	}
	
	public boolean matchesImportedLocalName(ASTNode<?> node, String name) {
		return !isSpecialModuleImport() && resolveLocalName(node).equals(name);
	}
	
	private @Nullable Scope tryGetTargetScope(ASTNode<?> node) {
		return scope.tryGetPathScope(node, path, this);
	}
	
	public @Nullable Scope tryResolveModule(ASTNode<?> node) {
		if (resolvingModule) {
			return null;
		}
		return resolveModule(node);
	}
	
	public @Nullable Scope resolveModule(ASTNode<?> node) {
		if (resolvingModule) {
			throw Helpers.nodeError(node, "Module import \"%s\" is circular!", path);
		}
		resolvingModule = true;
		try {
			Scope targetScope = tryGetTargetScope(node);
			if (targetScope == null) {
				return null;
			}
			String name = path.name;
			if (name.equals(Global.ROOT)) {
				checkSpecialModuleImportPath(node);
				return Main.rootScope;
			}
			else if (name.equals(Global.SELF)) {
				return targetScope.getCurrentModule();
			}
			else if (name.equals(Global.SUPER)) {
				checkSpecialModuleImportPath(node);
				return targetScope.getSuperModule(node);
			}
			else {
				return targetScope.tryGetLocalModule(node, name, this);
			}
		}
		finally {
			resolvingModule = false;
		}
	}
	
	public @Nullable TypeEntry resolveTypeEntry(ASTNode<?> node) {
		if (isSpecialModuleImport()) {
			return null;
		}
		if (resolvingTypeEntry) {
			throw Helpers.nodeError(node, "Type import \"%s\" is circular!", path);
		}
		resolvingTypeEntry = true;
		try {
			Scope targetScope = tryGetTargetScope(node);
			return targetScope == null ? null : targetScope.tryGetLocalTypeEntry(node, path.name, this);
		}
		finally {
			resolvingTypeEntry = false;
		}
	}
	
	public @Nullable TypeEntry tryResolveTypeEntry(ASTNode<?> node) {
		if (resolvingTypeEntry) {
			return null;
		}
		return resolveTypeEntry(node);
	}
	
	public @Nullable ValueEntry resolveValueEntry(ASTNode<?> node) {
		if (isSpecialModuleImport()) {
			return null;
		}
		if (resolvingValueEntry) {
			throw Helpers.nodeError(node, "Value import \"%s\" is circular!", path);
		}
		resolvingValueEntry = true;
		try {
			Scope targetScope = tryGetTargetScope(node);
			return targetScope == null ? null : targetScope.tryGetLocalValueEntry(node, path.name, this);
		}
		finally {
			resolvingValueEntry = false;
		}
	}
	
	public @Nullable ValueEntry tryResolveValueEntry(ASTNode<?> node) {
		if (resolvingValueEntry) {
			return null;
		}
		return resolveValueEntry(node);
	}
	
	public void check(ASTNode<?> node) {
		Scope targetScope = scope.getPathScope(node, path, this);
		
		boolean resolved = false;
		
		Scope module = resolveModule(node);
		if (module != null) {
			String localName = resolveModuleLocalName(module);
			if (localName.equals(Global.ROOT)) {
				throw Helpers.nodeError(node, "Root import must be aliased!");
			}
			scope.tryGetLocalModule(node, localName, null);
			resolved = true;
		}
		
		TypeEntry typeEntry = resolveTypeEntry(node);
		if (typeEntry != null) {
			scope.tryGetLocalTypeEntry(node, resolveLocalName(node), null);
			resolved = true;
		}
		
		ValueEntry valueEntry = resolveValueEntry(node);
		if (valueEntry != null) {
			scope.tryGetLocalValueEntry(node, resolveLocalName(node), null);
			resolved = true;
		}
		
		if (!resolved) {
			throw Helpers.nodeError(node, "Could not find \"%s\" in \"%s\" while importing \"%s\"!", path.name, targetScope.name, path);
		}
	}
}
