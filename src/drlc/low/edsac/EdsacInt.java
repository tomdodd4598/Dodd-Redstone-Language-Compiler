package drlc.low.edsac;

public class EdsacInt {
	
	public static final EdsacInt ZERO = of(0);
	public static final EdsacInt ONE = of(1);
	
	public static final EdsacInt MIN_VALUE = of(0x400000000L);
	public static final EdsacInt MAX_VALUE = of(0x3FFFFFFFFL);
	
	public static final EdsacInt SHORT_MASK = of(0x1FFFF);
	public static final EdsacInt CHAR_MASK = of(0x1F000);
	public static final EdsacInt SANDWICH_MASK = of(0x20000);
	
	private final long internal;
	
	private EdsacInt(long value) {
		internal = Long.remainderUnsigned(value, 0x800000000L);
	}
	
	public static EdsacInt of(long value) {
		return new EdsacInt(value);
	}
	
	public static EdsacInt fromChar(byte value) {
		return of(value << 12).and(CHAR_MASK);
	}
	
	public long toLong() {
		return internal;
	}
	
	public long toSigned() {
		return (internal << 29) / 0x20000000L;
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
		return Long.compare(internal << 29, other.internal << 29);
	}
	
	public int compareUnsigned(EdsacInt other) {
		return Long.compareUnsigned(internal << 29, other.internal << 29);
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
		return of(internal << other.internal);
	}
	
	public EdsacInt rightShift(EdsacInt other) {
		return of(toSigned() >> other.internal);
	}
	
	public EdsacInt divideUnsigned(EdsacInt other) {
		return of(internal / other.internal);
	}
	
	public EdsacInt remainderUnsigned(EdsacInt other) {
		return of(internal % other.internal);
	}
	
	public EdsacInt rightShiftUnsigned(EdsacInt other) {
		return of(internal >>> other.internal);
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
	
	public boolean isLong() {
		return internal > SHORT_MASK.internal;
	}
	
	public EdsacInt highBits() {
		return rightShiftUnsigned(of(18)).lowBits();
	}
	
	public EdsacInt lowBits() {
		return and(SHORT_MASK);
	}
	
	public int toChar() {
		return (int) (and(CHAR_MASK).toLong() >> 12);
	}
	
	public String toAssembly() {
		if (isLong()) {
			return lowBits().toAssembly() + highBits().toAssembly();
		}
		else {
			return EdsacOpcodes.get(this) + Long.toUnsignedString(internal >>> 2) + ((internal & 1) == 0 ? EdsacOpcodes.SHORT : EdsacOpcodes.LONG);
		}
	}
	
	public boolean requiresSandwich() {
		return !and(SANDWICH_MASK).equals(ZERO);
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
