package drlc.intermediate.component.data;

import org.eclipse.jdt.annotation.*;

import drlc.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.Function;
import drlc.intermediate.component.type.TypeInfo;
import drlc.intermediate.scope.Scope;

public abstract class DataId {
	
	public final Scope scope;
	public final int dereferenceLevel;
	public final @NonNull TypeInfo typeInfo;
	
	protected DataId(Scope scope, int dereferenceLevel, @NonNull TypeInfo typeInfo) {
		this.scope = scope;
		this.dereferenceLevel = dereferenceLevel;
		
		int minimumDereferenceLevel = minimumDereferenceLevel();
		if (dereferenceLevel < minimumDereferenceLevel) {
			throw Helpers.error("Dereference level for data ID \"%s\" can not be less than %d, but was equal to %d!", this, minimumDereferenceLevel, dereferenceLevel);
		}
		
		this.typeInfo = typeInfo;
	}
	
	protected abstract int minimumDereferenceLevel();
	
	public boolean isAddress() {
		return dereferenceLevel < 0;
	}
	
	public boolean isDereferenced() {
		return dereferenceLevel > 0;
	}
	
	public abstract int getOffset();
	
	public @Nullable Function getFunction() {
		return null;
	}
	
	protected Long scopeId() {
		return scope == null ? null : scope.globalId;
	}
	
	public abstract @NonNull DataId addDereference(ASTNode<?> node);
	
	public abstract @NonNull DataId removeDereference(ASTNode<?> node);
	
	public @NonNull TransientDataId getTransient(ASTNode<?> node) {
		if (dereferenceLevel == 0) {
			return new TransientDataId(typeInfo);
		}
		else {
			throw Helpers.nodeError(node, "Attempted to replace data ID \"%s\" with transient data ID!", this);
		}
	}
	
	public abstract boolean isIndexed();
	
	public abstract @NonNull DataId atOffset(ASTNode<?> node, int offset, @NonNull TypeInfo expectedTypeInfo);
	
	public abstract @Nullable DataId getRawReplacer(ASTNode<?> node, DataId rawInternal);
	
	public abstract boolean isCompressable();
	
	public abstract boolean isRepeatable(boolean lvalue);
	
	public abstract int hashCode(boolean raw);
	
	@Override
	public int hashCode() {
		return hashCode(false);
	}
	
	public abstract boolean equalsOther(Object obj, boolean raw, boolean low);
	
	@Override
	public boolean equals(Object obj) {
		return equalsOther(obj, false, false);
	}
	
	protected abstract String rawString();
	
	protected String toStringPrefix() {
		return (dereferenceLevel == 0 || getOffset() == 0 ? "" : Global.BRACE_START) + (dereferenceLevel >= 0 ? Helpers.dereferenceString(dereferenceLevel) : Global.ADDRESS_OF);
	}
	
	@Override
	public String toString() {
		return toStringPrefix() + Helpers.scopeStringPrefix(scope) + rawString();
	}
	
	public String opErrorString() {
		return toStringPrefix() + rawString() + Global.TYPE_ANNOTATION_PREFIX + " " + typeInfo.routineString();
	}
	
	public RawDataId raw() {
		return new RawDataId(this);
	}
	
	public LowDataId low() {
		return new LowDataId(this);
	}
	
	private static class ReducedDataId {
		
		public final DataId internal;
		
		protected ReducedDataId(DataId internal) {
			this.internal = internal;
		}
		
		@Override
		public int hashCode() {
			return internal.hashCode(true);
		}
		
		@Override
		public String toString() {
			return internal.toString();
		}
	}
	
	public static class RawDataId extends ReducedDataId {
		
		private RawDataId(DataId internal) {
			super(internal);
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof RawDataId && internal.equalsOther(((RawDataId) obj).internal, true, false);
		}
	}
	
	public static class LowDataId extends ReducedDataId {
		
		private LowDataId(DataId internal) {
			super(internal);
		}
		
		@Override
		public boolean equals(Object obj) {
			return obj instanceof LowDataId && internal.equalsOther(((LowDataId) obj).internal, false, true);
		}
	}
}
