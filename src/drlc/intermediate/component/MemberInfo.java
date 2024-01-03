package drlc.intermediate.component;

import java.util.Objects;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.component.type.TypeInfo;

public class MemberInfo {
	
	public final @NonNull String name;
	public final @NonNull TypeInfo typeInfo;
	public final int index;
	public final int offset;
	
	public MemberInfo(@NonNull String name, @NonNull TypeInfo typeInfo, int index, int offset) {
		this.name = name;
		this.typeInfo = typeInfo;
		this.index = index;
		this.offset = offset;
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(name, index, offset);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof MemberInfo) {
			MemberInfo other = (MemberInfo) obj;
			return name.equals(other.name) && index == other.index && offset == other.offset;
		}
		else {
			return false;
		}
	}
	
	@Override
	public String toString() {
		return name;
	}
}
