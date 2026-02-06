package drlc.low.edsac;

public class EdsacInt {
	
	public static final int BITS = 17;
	
	public static final EdsacInt ZERO = of(0L);
	public static final EdsacInt ONE = of(1L);
	
	public static final EdsacInt MIN_VALUE = of(0x10000L);
	public static final EdsacInt MAX_VALUE = of(0x0FFFFL);
	
	public static final EdsacInt CHAR_MASK = of(0x1F000L);
	
	private final long internal;
	
	private EdsacInt(long value) {
		internal = value & 0x1FFFFL;
	}
	
	public static EdsacInt of(long value) {
		return new EdsacInt(value);
	}
	
	public long toLong() {
		return internal;
	}
	
	public long toSigned() {
		return (internal << 47) >> 47;
	}
	
	public EdsacInt minus() {
		return of(-internal);
	}
	
	public EdsacInt not() {
		return of(~internal);
	}
	
	@Override
	public boolean equals(Object other) {
		return other instanceof EdsacInt intValue && internal == intValue.internal;
	}
	
	public int compare(EdsacInt other) {
		return Long.compare(toSigned(), other.toSigned());
	}
	
	public int compareUnsigned(EdsacInt other) {
		return Long.compareUnsigned(internal, other.internal);
	}
	
	public EdsacInt plus(EdsacInt other) {
		return of(internal + other.internal);
	}
	
	public EdsacInt and(EdsacInt other) {
		return of(internal & other.internal);
	}
	
	public EdsacInt or(EdsacInt other) {
		return of(internal | other.internal);
	}
	
	public EdsacInt xor(EdsacInt other) {
		return of(internal ^ other.internal);
	}
	
	public EdsacInt minus(EdsacInt other) {
		return of(internal - other.internal);
	}
	
	public EdsacInt multiply(EdsacInt other) {
		return of(internal * other.internal);
	}
	
	public EdsacInt divide(EdsacInt other) {
		return of(toSigned() / other.toSigned());
	}
	
	public EdsacInt remainder(EdsacInt other) {
		return of(toSigned() % other.toSigned());
	}
	
	public EdsacInt leftShift(EdsacInt other) {
		return of(internal << EdsacCode.shiftBits(other.internal));
	}
	
	public EdsacInt rightShift(EdsacInt other) {
		return of(toSigned() >> EdsacCode.shiftBits(other.internal));
	}
	
	public EdsacInt divideUnsigned(EdsacInt other) {
		return of(internal / other.internal);
	}
	
	public EdsacInt remainderUnsigned(EdsacInt other) {
		return of(internal % other.internal);
	}
	
	public EdsacInt rightShiftUnsigned(EdsacInt other) {
		return of(internal >>> EdsacCode.shiftBits(other.internal));
	}
	
	public boolean isPowerOfTwo() {
		long signed = toSigned();
		return signed > 0 && ((signed & (signed - 1)) == 0);
	}
	
	public EdsacInt log2() {
		long signed = toSigned();
		if (signed > 0) {
			int log = 0;
			if (signed >= 4294967296L) {
				signed >>>= 32;
				log += 32;
			}
			if (signed >= 65536) {
				signed >>>= 16;
				log += 16;
			}
			if (signed >= 256) {
				signed >>>= 8;
				log += 8;
			}
			if (signed >= 16) {
				signed >>>= 4;
				log += 4;
			}
			if (signed >= 4) {
				signed >>>= 2;
				log += 2;
			}
			return of(log + (signed >>> 1));
		}
		else {
			throw new IllegalArgumentException(String.format("Attempted to calculate logarithm of non-positive number %s!", signed));
		}
	}
	
	public EdsacChar toChar() {
		return EdsacChar.of((byte) (and(CHAR_MASK).toLong() >> 12));
	}
	
	public String toAssembly() {
		return EdsacOpcodes.get(this) + EdsacCode.instructionArgument(internal >>> 1) + ((internal & 1) == 0 ? EdsacOpcodes.SHORT : EdsacOpcodes.LONG);
	}
	
	@Override
	public int hashCode() {
		return Long.hashCode(internal);
	}
	
	@Override
	public String toString() {
		return Long.toString(toSigned());
	}
}
