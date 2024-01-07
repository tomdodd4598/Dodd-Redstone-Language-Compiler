package drlc;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.NonNull;

import drlc.intermediate.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.*;

public abstract class Generator {
	
	public static interface GeneratorConstructor extends java.util.function.Function<String, Generator> {}
	
	public static final Map<String, String> NAME_MAP = new LinkedHashMap<>();
	public static final Map<String, GeneratorConstructor> CONSTRUCTOR_MAP = new LinkedHashMap<>();
	
	static {
		put("i", "Intermediate", IntermediateGenerator::new);
		put("s1", "DRC1 Assembly", drlc.low.drc1.RedstoneAssemblyGenerator::new);
		put("oc1", "DRC1 OC Input", drlc.low.drc1.RedstoneOCGenerator::new);
	}
	
	private static void put(String id, String name, GeneratorConstructor constructor) {
		NAME_MAP.put(id, name);
		CONSTRUCTOR_MAP.put(id, constructor);
	}
	
	protected final String outputFile;
	
	public final Map<String, Directive> directiveMap = new HashMap<>();
	
	public @SuppressWarnings("null") @NonNull BoolTypeInfo boolTypeInfo = null;
	public @SuppressWarnings("null") @NonNull IntTypeInfo intTypeInfo = null;
	public @SuppressWarnings("null") @NonNull NatTypeInfo natTypeInfo = null;
	public @SuppressWarnings("null") @NonNull CharTypeInfo charTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull TupleTypeInfo voidTypeInfo = null;
	public @SuppressWarnings("null") @NonNull TupleTypeInfo wildcardPtrTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull ArrayTypeInfo emptyArrayTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull IntTypeInfo indexTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull IntTypeInfo rootReturnTypeInfo = null;
	public @SuppressWarnings("null") @NonNull FunctionPointerTypeInfo mainFunctionTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull TupleValue unitValue = null;
	public @SuppressWarnings("null") @NonNull AddressValue nullValue = null;
	
	public @SuppressWarnings("null") @NonNull ArrayValue emptyArrayValue = null;
	
	public @SuppressWarnings("null") @NonNull BoolValue falseValue = null;
	public @SuppressWarnings("null") @NonNull BoolValue trueValue = null;
	
	public Generator(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void init() {
		addBuiltInTypes();
		addBuiltInDirectives();
		addBuiltInConstants();
		addBuiltInVariables();
		addBuiltInFunctions();
	}
	
	public void addBuiltInTypes() {
		Main.rootScope.addRawType(null, new RawType(Global.BOOL, 1, new HashMap<>(), BoolTypeInfo::new));
		Main.rootScope.addRawType(null, new RawType(Global.INT, getWordSize(), new HashMap<>(), IntTypeInfo::new));
		Main.rootScope.addRawType(null, new RawType(Global.NAT, getWordSize(), new HashMap<>(), NatTypeInfo::new));
		Main.rootScope.addRawType(null, new RawType(Global.CHAR, 1, new HashMap<>(), CharTypeInfo::new));
		
		boolTypeInfo = boolTypeInfo();
		intTypeInfo = intTypeInfo();
		natTypeInfo = natTypeInfo();
		charTypeInfo = charTypeInfo();
		
		voidTypeInfo = new TupleTypeInfo(null, new ArrayList<>(), new ArrayList<>());
		wildcardPtrTypeInfo = new TupleTypeInfo(null, Arrays.asList(false), new ArrayList<>());
		
		emptyArrayTypeInfo = new ArrayTypeInfo(null, new ArrayList<>(), wildcardPtrTypeInfo, 0);
		
		indexTypeInfo = intTypeInfo;
		
		rootReturnTypeInfo = intTypeInfo;
		mainFunctionTypeInfo = new FunctionPointerTypeInfo(null, new ArrayList<>(), voidTypeInfo, new ArrayList<>());
		
		unitValue = new TupleValue(null, voidTypeInfo, new ArrayList<>());
		
		emptyArrayValue = new ArrayValue(null, emptyArrayTypeInfo, new ArrayList<>());
		
		falseValue = boolValue(false);
		trueValue = boolValue(true);
	}
	
	public void addBuiltInDirectives() {}
	
	public void addBuiltInConstants() {
		Main.rootScope.addConstant(null, new Constant(Global.NULL, wildcardAddressValue(0)), false);
	}
	
	public void addBuiltInVariables() {}
	
	protected void addBuiltInFunction(@NonNull String name, @NonNull TypeInfo returnTypeInfo, DeclaratorInfo... params) {
		Function function = new Function(null, name, true, returnTypeInfo, Arrays.asList(params));
		Main.rootScope.addFunction(null, function, false);
		Main.rootScope.addRoutine(null, new FunctionRoutine(null, function));
	}
	
	public void addBuiltInFunctions() {
		addBuiltInFunction(Global.INBOOL, boolTypeInfo);
		addBuiltInFunction(Global.ININT, intTypeInfo);
		addBuiltInFunction(Global.INNAT, natTypeInfo);
		addBuiltInFunction(Global.INCHAR, charTypeInfo);
		
		addBuiltInFunction(Global.OUTBOOL, voidTypeInfo, Helpers.builtInDeclarator("b", boolTypeInfo));
		addBuiltInFunction(Global.OUTINT, voidTypeInfo, Helpers.builtInDeclarator("i", intTypeInfo));
		addBuiltInFunction(Global.OUTNAT, voidTypeInfo, Helpers.builtInDeclarator("n", natTypeInfo));
		addBuiltInFunction(Global.OUTCHAR, voidTypeInfo, Helpers.builtInDeclarator("c", charTypeInfo));
	}
	
	public @NonNull Value binaryOp(ASTNode<?, ?> node, @NonNull Value left, @NonNull BinaryOpType opType, @NonNull Value right) {
		TypeInfo leftType = left.typeInfo, rightType = right.typeInfo;
		
		if (left instanceof AddressValue || right instanceof AddressValue) {
			return addressBinaryOp(node, left, opType, right);
		}
		else if (leftType instanceof BasicTypeInfo) {
			checkBasicBinaryOp(node, leftType, opType, rightType);
			
			if (leftType.equals(boolTypeInfo)) {
				if (!rightType.equals(boolTypeInfo)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				BoolValue boolLeft = (BoolValue) left, boolRight = (BoolValue) right;
				switch (opType) {
					case LOGICAL_AND:
						return boolValue(boolLeft.value && boolRight.value);
					case LOGICAL_OR:
						return boolValue(boolLeft.value || boolRight.value);
					case EQUAL_TO:
						return boolValue(boolLeft.value == boolRight.value);
					case NOT_EQUAL_TO:
						return boolValue(boolLeft.value != boolRight.value);
					case LESS_THAN:
						return boolValue(Boolean.compare(boolLeft.value, boolRight.value) < 0);
					case LESS_OR_EQUAL:
						return boolValue(Boolean.compare(boolLeft.value, boolRight.value) <= 0);
					case MORE_THAN:
						return boolValue(Boolean.compare(boolLeft.value, boolRight.value) > 0);
					case MORE_OR_EQUAL:
						return boolValue(Boolean.compare(boolLeft.value, boolRight.value) >= 0);
					case PLUS:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case AND:
						return boolValue(boolLeft.value & boolRight.value);
					case OR:
						return boolValue(boolLeft.value | boolRight.value);
					case XOR:
						return boolValue(boolLeft.value ^ boolRight.value);
					case MINUS:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else if (leftType.equals(intTypeInfo)) {
				if ((opType.isShift() && !rightType.isWord()) || (!opType.isShift() && !rightType.equals(intTypeInfo))) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				else {
					return intIntBinaryOp(node, (IntValue) left, opType, right.toInt(node));
				}
			}
			else if (leftType.equals(natTypeInfo)) {
				if ((opType.isShift() && !rightType.isWord()) || (!opType.isShift() && !rightType.equals(natTypeInfo))) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				else {
					return natNatBinaryOp(node, (NatValue) left, opType, right.toNat(node));
				}
			}
			else if (leftType.equals(charTypeInfo)) {
				if (!rightType.equals(charTypeInfo)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				CharValue charLeft = (CharValue) left, charRight = (CharValue) right;
				switch (opType) {
					case EQUAL_TO:
						return boolValue(charLeft.value == charRight.value);
					case NOT_EQUAL_TO:
						return boolValue(charLeft.value != charRight.value);
					case LESS_THAN:
						return boolValue(charLeft.value < charRight.value);
					case LESS_OR_EQUAL:
						return boolValue(charLeft.value <= charRight.value);
					case MORE_THAN:
						return boolValue(charLeft.value > charRight.value);
					case MORE_OR_EQUAL:
						return boolValue(charLeft.value >= charRight.value);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
		}
		throw undefinedBinaryOp(node, leftType, opType, rightType);
	}
	
	protected @NonNull Value addressBinaryOp(ASTNode<?, ?> node, @NonNull Value left, @NonNull BinaryOpType opType, @NonNull Value right) {
		TypeInfo leftType = left.typeInfo, rightType = right.typeInfo;
		boolean plusOrMinus = opType == BinaryOpType.PLUS || opType == BinaryOpType.MINUS;
		
		if (left instanceof AddressValue) {
			AddressValue leftAddress = (AddressValue) left;
			
			if (right instanceof AddressValue) {
				if (opType == BinaryOpType.MINUS && !leftType.equals(rightType)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				AddressValue rightAddress = (AddressValue) right;
				
				switch (opType) {
					case LOGICAL_AND:
					case LOGICAL_OR:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case EQUAL_TO:
						return boolValue(leftAddress.address == rightAddress.address);
					case NOT_EQUAL_TO:
						return boolValue(leftAddress.address != rightAddress.address);
					case LESS_THAN:
						return boolValue(leftAddress.address < rightAddress.address);
					case LESS_OR_EQUAL:
						return boolValue(leftAddress.address <= rightAddress.address);
					case MORE_THAN:
						return boolValue(leftAddress.address > rightAddress.address);
					case MORE_OR_EQUAL:
						return boolValue(leftAddress.address >= rightAddress.address);
					case PLUS:
					case AND:
					case OR:
					case XOR:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case MINUS:
						return indexValue((leftAddress.address - rightAddress.address) / leftType.getAddressOffsetSize(node));
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else {
				if (plusOrMinus && rightType.isWord()) {
					int size = leftType.getAddressOffsetSize(node);
					return addressValue(leftType, opType == BinaryOpType.PLUS ? leftAddress.address + right.longValue(node) * size : leftAddress.address - right.longValue(node) * size);
				}
				else {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
			}
		}
		else {
			if (right instanceof AddressValue) {
				AddressValue rightAddress = (AddressValue) right;
				if (plusOrMinus && leftType.isWord()) {
					int size = leftType.getAddressOffsetSize(node);
					return addressValue(rightType, opType == BinaryOpType.PLUS ? left.longValue(node) * size + rightAddress.address : left.longValue(node) * size - rightAddress.address);
				}
				else {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
			}
			else {
				throw Helpers.nodeError(node, "Unexpectedly used address binary op \"%s\" on expressions of types \"%s\" and \"%s\"!", opType, leftType, rightType);
			}
		}
	}
	
	public @NonNull Value intIntBinaryOp(ASTNode<?, ?> node, IntValue left, @NonNull BinaryOpType opType, IntValue right) {
		switch (opType) {
			case EQUAL_TO:
				return boolValue(left.value == right.value);
			case NOT_EQUAL_TO:
				return boolValue(left.value != right.value);
			case LESS_THAN:
				return boolValue(left.value < right.value);
			case LESS_OR_EQUAL:
				return boolValue(left.value <= right.value);
			case MORE_THAN:
				return boolValue(left.value > right.value);
			case MORE_OR_EQUAL:
				return boolValue(left.value >= right.value);
			case PLUS:
				return intValue(left.value + right.value);
			case AND:
				return intValue(left.value & right.value);
			case OR:
				return intValue(left.value | right.value);
			case XOR:
				return intValue(left.value ^ right.value);
			case MINUS:
				return intValue(left.value - right.value);
			case MULTIPLY:
				return intValue(left.value * right.value);
			case DIVIDE:
				if (right.value == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return intValue(left.value / right.value);
			case REMAINDER:
				if (right.value == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return intValue(left.value % right.value);
			case LEFT_SHIFT:
				return intValue(left.value << right.intValue(node));
			case RIGHT_SHIFT:
				return intValue(left.value >> right.intValue(node));
			case LEFT_ROTATE:
				return intValue(Long.rotateLeft(left.value, right.intValue(node)));
			case RIGHT_ROTATE:
				return intValue(Long.rotateRight(left.value, right.intValue(node)));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	public @NonNull Value natNatBinaryOp(ASTNode<?, ?> node, NatValue left, @NonNull BinaryOpType opType, NatValue right) {
		switch (opType) {
			case EQUAL_TO:
				return boolValue(left.value == right.value);
			case NOT_EQUAL_TO:
				return boolValue(left.value != right.value);
			case LESS_THAN:
				return boolValue(Long.compareUnsigned(left.value, right.value) < 0);
			case LESS_OR_EQUAL:
				return boolValue(Long.compareUnsigned(left.value, right.value) <= 0);
			case MORE_THAN:
				return boolValue(Long.compareUnsigned(left.value, right.value) > 0);
			case MORE_OR_EQUAL:
				return boolValue(Long.compareUnsigned(left.value, right.value) >= 0);
			case PLUS:
				return natValue(left.value + right.value);
			case AND:
				return natValue(left.value & right.value);
			case OR:
				return natValue(left.value | right.value);
			case XOR:
				return natValue(left.value ^ right.value);
			case MINUS:
				return natValue(left.value - right.value);
			case MULTIPLY:
				return natValue(left.value * right.value);
			case DIVIDE:
				if (right.value == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return natValue(Long.divideUnsigned(left.value, right.value));
			case REMAINDER:
				if (right.value == 0) {
					throw Helpers.nodeError(node, "Can not divide by zero!");
				}
				return natValue(Long.remainderUnsigned(left.value, right.value));
			case LEFT_SHIFT:
				return natValue(left.value << right.intValue(node));
			case RIGHT_SHIFT:
				return natValue(left.value >>> right.intValue(node));
			case LEFT_ROTATE:
				return natValue(Long.rotateLeft(left.value, right.intValue(node)));
			case RIGHT_ROTATE:
				return natValue(Long.rotateRight(left.value, right.intValue(node)));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	public @NonNull Value unaryOp(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull Value value) {
		TypeInfo typeInfo = value.typeInfo;
		if (value instanceof AddressValue) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		else if (typeInfo instanceof BasicTypeInfo) {
			if (typeInfo.equals(boolTypeInfo)) {
				BoolValue boolValue = (BoolValue) value;
				switch (opType) {
					case MINUS:
						throw undefinedUnaryOp(node, opType, typeInfo);
					case NOT:
						return boolValue(!boolValue.value);
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(intTypeInfo)) {
				return intUnaryOp(node, opType, (IntValue) value);
			}
			else if (typeInfo.equals(natTypeInfo)) {
				return natUnaryOp(node, opType, (NatValue) value);
			}
			else if (typeInfo.equals(charTypeInfo)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	public @NonNull Value intUnaryOp(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull IntValue value) {
		switch (opType) {
			case MINUS:
				return intValue(-value.value);
			case NOT:
				return intValue(~value.value);
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	public @NonNull Value natUnaryOp(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull NatValue value) {
		switch (opType) {
			case MINUS:
				throw undefinedUnaryOp(node, opType, value.typeInfo);
			case NOT:
				return natValue(~value.value);
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	public @NonNull BoolValue boolValue(boolean value) {
		return new BoolValue(null, value);
	}
	
	public @NonNull IntValue intValue(long value) {
		return new IntValue(null, value);
	}
	
	public @NonNull NatValue natValue(long value) {
		return new NatValue(null, value);
	}
	
	public @NonNull CharValue charValue(char value) {
		return new CharValue(null, value);
	}
	
	public @NonNull AddressValue addressValue(@NonNull TypeInfo typeInfo, long address) {
		return new AddressValue(null, typeInfo, address);
	}
	
	public @NonNull AddressValue wildcardAddressValue(long address) {
		return new AddressValue(null, wildcardPtrTypeInfo, address);
	}
	
	public @NonNull IntValue indexValue(long value) {
		return intValue(value);
	}
	
	public @NonNull TypeInfo binaryOpTypeInfo(ASTNode<?, ?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		if (leftType.isAddress() || rightType.isAddress()) {
			return addressBinaryOpTypeInfo(node, leftType, opType, rightType);
		}
		else if (leftType instanceof BasicTypeInfo) {
			checkBasicBinaryOp(node, leftType, opType, rightType);
			
			if (leftType.equals(boolTypeInfo)) {
				if (!rightType.equals(boolTypeInfo)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case LOGICAL_AND:
					case LOGICAL_OR:
					case EQUAL_TO:
					case NOT_EQUAL_TO:
					case LESS_THAN:
					case LESS_OR_EQUAL:
					case MORE_THAN:
					case MORE_OR_EQUAL:
						return boolTypeInfo;
					case PLUS:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case AND:
					case OR:
					case XOR:
						return boolTypeInfo;
					case MINUS:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else if (leftType.isWord()) {
				if ((opType.isShift() && !rightType.isWord()) || (!opType.isShift() && !rightType.equals(leftType))) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case LOGICAL_AND:
					case LOGICAL_OR:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case EQUAL_TO:
					case NOT_EQUAL_TO:
					case LESS_THAN:
					case LESS_OR_EQUAL:
					case MORE_THAN:
					case MORE_OR_EQUAL:
						return boolTypeInfo;
					case PLUS:
					case AND:
					case OR:
					case XOR:
					case MINUS:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						return leftType;
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else if (leftType.equals(charTypeInfo)) {
				if (!rightType.equals(charTypeInfo)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case LOGICAL_AND:
					case LOGICAL_OR:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case EQUAL_TO:
					case NOT_EQUAL_TO:
					case LESS_THAN:
					case LESS_OR_EQUAL:
					case MORE_THAN:
					case MORE_OR_EQUAL:
						return boolTypeInfo;
					case PLUS:
					case AND:
					case OR:
					case XOR:
					case MINUS:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
		}
		throw undefinedBinaryOp(node, leftType, opType, rightType);
	}
	
	protected @NonNull TypeInfo addressBinaryOpTypeInfo(ASTNode<?, ?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		boolean plusOrMinus = opType == BinaryOpType.PLUS || opType == BinaryOpType.MINUS;
		
		if (leftType.isAddress()) {
			if (rightType.isAddress()) {
				if (opType == BinaryOpType.MINUS && !leftType.equals(rightType)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case LOGICAL_AND:
					case LOGICAL_OR:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case EQUAL_TO:
					case NOT_EQUAL_TO:
					case LESS_THAN:
					case LESS_OR_EQUAL:
					case MORE_THAN:
					case MORE_OR_EQUAL:
						return boolTypeInfo;
					case PLUS:
					case AND:
					case OR:
					case XOR:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case MINUS:
						return indexTypeInfo;
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else {
				if (plusOrMinus && rightType.isWord()) {
					return leftType;
				}
				else {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
			}
		}
		else {
			if (rightType.isAddress()) {
				if (plusOrMinus && leftType.isWord()) {
					return rightType;
				}
				else {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
			}
			else {
				throw Helpers.nodeError(node, "Unexpectedly used address binary op \"%s\" on expressions of types \"%s\" and \"%s\"!", opType, leftType, rightType);
			}
		}
	}
	
	public void binaryOp(ASTNode<?, ?> node, @NonNull Routine routine, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType, DataId target, DataId arg1, DataId arg2) {
		if (leftType.isAddress() || rightType.isAddress()) {
			addressBinaryOp(node, routine, leftType, opType, rightType, target, arg1, arg2);
			return;
		}
		else if (leftType instanceof BasicTypeInfo) {
			checkBasicBinaryOp(node, leftType, opType, rightType);
			
			if (leftType.equals(boolTypeInfo)) {
				if (!rightType.equals(boolTypeInfo)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.BOOL_EQUAL_TO_BOOL.action(node, target, arg1, arg2));
						return;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.BOOL_NOT_EQUAL_TO_BOOL.action(node, target, arg1, arg2));
						return;
					case LESS_THAN:
						routine.addAction(BinaryActionType.BOOL_LESS_THAN_BOOL.action(node, target, arg1, arg2));
						return;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.BOOL_LESS_OR_EQUAL_BOOL.action(node, target, arg1, arg2));
						return;
					case MORE_THAN:
						routine.addAction(BinaryActionType.BOOL_MORE_THAN_BOOL.action(node, target, arg1, arg2));
						return;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.BOOL_MORE_OR_EQUAL_BOOL.action(node, target, arg1, arg2));
						return;
					case PLUS:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case AND:
						routine.addAction(BinaryActionType.BOOL_AND_BOOL.action(node, target, arg1, arg2));
						return;
					case OR:
						routine.addAction(BinaryActionType.BOOL_OR_BOOL.action(node, target, arg1, arg2));
						return;
					case XOR:
						routine.addAction(BinaryActionType.BOOL_XOR_BOOL.action(node, target, arg1, arg2));
						return;
					case MINUS:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else if (leftType.equals(intTypeInfo)) {
				if ((opType.isShift() && !rightType.isWord()) || (!opType.isShift() && !rightType.equals(intTypeInfo))) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.INT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.INT_NOT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return;
					case LESS_THAN:
						routine.addAction(BinaryActionType.INT_LESS_THAN_INT.action(node, target, arg1, arg2));
						return;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_LESS_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return;
					case MORE_THAN:
						routine.addAction(BinaryActionType.INT_MORE_THAN_INT.action(node, target, arg1, arg2));
						return;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_MORE_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return;
					case PLUS:
						routine.addAction(BinaryActionType.INT_PLUS_INT.action(node, target, arg1, arg2));
						return;
					case AND:
						routine.addAction(BinaryActionType.INT_AND_INT.action(node, target, arg1, arg2));
						return;
					case OR:
						routine.addAction(BinaryActionType.INT_OR_INT.action(node, target, arg1, arg2));
						return;
					case XOR:
						routine.addAction(BinaryActionType.INT_XOR_INT.action(node, target, arg1, arg2));
						return;
					case MINUS:
						routine.addAction(BinaryActionType.INT_MINUS_INT.action(node, target, arg1, arg2));
						return;
					case MULTIPLY:
						routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, target, arg1, arg2));
						return;
					case DIVIDE:
						routine.addAction(BinaryActionType.INT_DIVIDE_INT.action(node, target, arg1, arg2));
						return;
					case REMAINDER:
						routine.addAction(BinaryActionType.INT_REMAINDER_INT.action(node, target, arg1, arg2));
						return;
					case LEFT_SHIFT:
						routine.addAction(BinaryActionType.INT_LEFT_SHIFT_INT.action(node, target, arg1, arg2));
						return;
					case RIGHT_SHIFT:
						routine.addAction(BinaryActionType.INT_RIGHT_SHIFT_INT.action(node, target, arg1, arg2));
						return;
					case LEFT_ROTATE:
						routine.addAction(BinaryActionType.INT_LEFT_ROTATE_INT.action(node, target, arg1, arg2));
						return;
					case RIGHT_ROTATE:
						routine.addAction(BinaryActionType.INT_RIGHT_ROTATE_INT.action(node, target, arg1, arg2));
						return;
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else if (leftType.equals(natTypeInfo)) {
				if ((opType.isShift() && !rightType.isWord()) || (!opType.isShift() && !rightType.equals(natTypeInfo))) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.INT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.INT_NOT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return;
					case LESS_THAN:
						routine.addAction(BinaryActionType.NAT_LESS_THAN_NAT.action(node, target, arg1, arg2));
						return;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.NAT_LESS_OR_EQUAL_NAT.action(node, target, arg1, arg2));
						return;
					case MORE_THAN:
						routine.addAction(BinaryActionType.NAT_MORE_THAN_NAT.action(node, target, arg1, arg2));
						return;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.NAT_MORE_OR_EQUAL_NAT.action(node, target, arg1, arg2));
						return;
					case PLUS:
						routine.addAction(BinaryActionType.INT_PLUS_INT.action(node, target, arg1, arg2));
						return;
					case AND:
						routine.addAction(BinaryActionType.INT_AND_INT.action(node, target, arg1, arg2));
						return;
					case OR:
						routine.addAction(BinaryActionType.INT_OR_INT.action(node, target, arg1, arg2));
						return;
					case XOR:
						routine.addAction(BinaryActionType.INT_XOR_INT.action(node, target, arg1, arg2));
						return;
					case MINUS:
						routine.addAction(BinaryActionType.INT_MINUS_INT.action(node, target, arg1, arg2));
						return;
					case MULTIPLY:
						routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, target, arg1, arg2));
						return;
					case DIVIDE:
						routine.addAction(BinaryActionType.NAT_DIVIDE_NAT.action(node, target, arg1, arg2));
						return;
					case REMAINDER:
						routine.addAction(BinaryActionType.NAT_REMAINDER_NAT.action(node, target, arg1, arg2));
						return;
					case LEFT_SHIFT:
						routine.addAction(BinaryActionType.INT_LEFT_SHIFT_INT.action(node, target, arg1, arg2));
						return;
					case RIGHT_SHIFT:
						routine.addAction(BinaryActionType.NAT_RIGHT_SHIFT_INT.action(node, target, arg1, arg2));
						return;
					case LEFT_ROTATE:
						routine.addAction(BinaryActionType.INT_LEFT_ROTATE_INT.action(node, target, arg1, arg2));
						return;
					case RIGHT_ROTATE:
						routine.addAction(BinaryActionType.INT_RIGHT_ROTATE_INT.action(node, target, arg1, arg2));
						return;
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else if (leftType.equals(charTypeInfo)) {
				if (!rightType.equals(charTypeInfo)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.CHAR_EQUAL_TO_CHAR.action(node, target, arg1, arg2));
						return;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.CHAR_NOT_EQUAL_TO_CHAR.action(node, target, arg1, arg2));
						return;
					case LESS_THAN:
						routine.addAction(BinaryActionType.CHAR_LESS_THAN_CHAR.action(node, target, arg1, arg2));
						return;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.CHAR_LESS_OR_EQUAL_CHAR.action(node, target, arg1, arg2));
						return;
					case MORE_THAN:
						routine.addAction(BinaryActionType.CHAR_MORE_THAN_CHAR.action(node, target, arg1, arg2));
						return;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.CHAR_MORE_OR_EQUAL_CHAR.action(node, target, arg1, arg2));
						return;
					case PLUS:
					case AND:
					case OR:
					case XOR:
					case MINUS:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
		}
		throw undefinedBinaryOp(node, leftType, opType, rightType);
	}
	
	protected void addressBinaryOp(ASTNode<?, ?> node, @NonNull Routine routine, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType, DataId target, DataId arg1, DataId arg2) {
		boolean plusOrMinus = opType == BinaryOpType.PLUS || opType == BinaryOpType.MINUS;
		
		if (leftType.isAddress()) {
			if (rightType.isAddress()) {
				if (opType == BinaryOpType.MINUS && !leftType.equals(rightType)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.INT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.INT_NOT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return;
					case LESS_THAN:
						routine.addAction(BinaryActionType.INT_LESS_THAN_INT.action(node, target, arg1, arg2));
						return;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_LESS_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return;
					case MORE_THAN:
						routine.addAction(BinaryActionType.INT_MORE_THAN_INT.action(node, target, arg1, arg2));
						return;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_MORE_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return;
					case PLUS:
					case AND:
					case OR:
					case XOR:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					case MINUS:
						DataId raw = routine.nextRegId(intTypeInfo);
						routine.addAction(BinaryActionType.INT_MINUS_INT.action(node, raw, arg1, arg2));
						routine.addAction(BinaryActionType.INT_DIVIDE_INT.action(node, target, raw, indexValue(leftType.getAddressOffsetSize(node)).dataId()));
						return;
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, leftType, opType, rightType);
					default:
						throw unknownBinaryOpType(node, leftType, opType, rightType);
				}
			}
			else {
				if (plusOrMinus && rightType.isWord()) {
					DataId offset = routine.nextRegId(intTypeInfo);
					routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, offset, arg2, indexValue(leftType.getAddressOffsetSize(node)).dataId()));
					routine.addAction((opType == BinaryOpType.PLUS ? BinaryActionType.INT_PLUS_INT : BinaryActionType.INT_MINUS_INT).action(node, target, arg1, offset));
					return;
				}
				else {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
			}
		}
		else {
			if (rightType.isAddress()) {
				if (plusOrMinus && leftType.isWord()) {
					DataId offset = routine.nextRegId(intTypeInfo);
					routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, offset, arg1, indexValue(rightType.getAddressOffsetSize(node)).dataId()));
					routine.addAction((opType == BinaryOpType.PLUS ? BinaryActionType.INT_PLUS_INT : BinaryActionType.INT_MINUS_INT).action(node, target, offset, arg2));
					return;
				}
				else {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
			}
			else {
				throw Helpers.nodeError(node, "Unexpectedly used address binary op \"%s\" on expressions of types \"%s\" and \"%s\"!", opType, leftType, rightType);
			}
		}
	}
	
	public void checkBasicBinaryOp(ASTNode<?, ?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
				if (leftType.isAddress() || rightType.isAddress()) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				break;
			case EQUAL_TO:
			case NOT_EQUAL_TO:
			case LESS_THAN:
			case LESS_OR_EQUAL:
			case MORE_THAN:
			case MORE_OR_EQUAL:
				break;
			case PLUS:
				if (leftType.isAddress() && rightType.isAddress()) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				break;
			case AND:
			case OR:
			case XOR:
				if (leftType.isAddress() || rightType.isAddress()) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				break;
			case MINUS:
				break;
			case MULTIPLY:
			case DIVIDE:
			case REMAINDER:
			case LEFT_SHIFT:
			case RIGHT_SHIFT:
			case LEFT_ROTATE:
			case RIGHT_ROTATE:
				if (leftType.isAddress() || rightType.isAddress()) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				break;
			default:
				throw unknownBinaryOpType(node, leftType, opType, rightType);
		}
	}
	
	protected RuntimeException undefinedBinaryOp(ASTNode<?, ?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		return Helpers.nodeError(node, "Binary op \"%s\" can not act on expressions of types \"%s\" and \"%s\"!", opType, leftType, rightType);
	}
	
	protected RuntimeException unknownBinaryOpType(ASTNode<?, ?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		return Helpers.nodeError(node, "Attempted to write an expression including a binary op of unknown type!");
	}
	
	public @NonNull TypeInfo unaryOpTypeInfo(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		if (typeInfo.isAddress()) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		else if (typeInfo instanceof BasicTypeInfo) {
			if (typeInfo.equals(boolTypeInfo)) {
				switch (opType) {
					case MINUS:
						throw undefinedUnaryOp(node, opType, typeInfo);
					case NOT:
						return boolTypeInfo;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(intTypeInfo)) {
				switch (opType) {
					case MINUS:
					case NOT:
						return intTypeInfo;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(natTypeInfo)) {
				switch (opType) {
					case MINUS:
						throw undefinedUnaryOp(node, opType, typeInfo);
					case NOT:
						return natTypeInfo;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(charTypeInfo)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	public void unaryOp(ASTNode<?, ?> node, @NonNull Routine routine, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo, DataId target, DataId arg) {
		if (typeInfo.isAddress()) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		else if (typeInfo instanceof BasicTypeInfo) {
			if (typeInfo.equals(boolTypeInfo)) {
				switch (opType) {
					case MINUS:
						throw undefinedUnaryOp(node, opType, typeInfo);
					case NOT:
						routine.addAction(UnaryActionType.NOT_BOOL.action(node, target, arg));
						return;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(intTypeInfo)) {
				switch (opType) {
					case MINUS:
						routine.addAction(UnaryActionType.MINUS_INT.action(node, target, arg));
						return;
					case NOT:
						routine.addAction(UnaryActionType.NOT_INT.action(node, target, arg));
						return;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(natTypeInfo)) {
				switch (opType) {
					case MINUS:
						throw undefinedUnaryOp(node, opType, typeInfo);
					case NOT:
						routine.addAction(UnaryActionType.NOT_INT.action(node, target, arg));
						return;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(charTypeInfo)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	protected RuntimeException undefinedUnaryOp(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		return Helpers.nodeError(node, "Unary op \"%s\" can not act on an expression of type \"%s\"!", opType, typeInfo);
	}
	
	protected RuntimeException unknownUnaryOpType(ASTNode<?, ?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		return Helpers.nodeError(node, "Attempted to write an expression including a unary op of unknown type!");
	}
	
	public abstract int getWordSize();
	
	public abstract int getFunctionSize();
	
	public abstract int getAddressSize();
	
	public @NonNull BoolTypeInfo boolTypeInfo(Boolean... referenceMutability) {
		return new BoolTypeInfo(null, Arrays.asList(referenceMutability), Main.rootScope);
	}
	
	public @NonNull IntTypeInfo intTypeInfo(Boolean... referenceMutability) {
		return new IntTypeInfo(null, Arrays.asList(referenceMutability), Main.rootScope);
	}
	
	public @NonNull NatTypeInfo natTypeInfo(Boolean... referenceMutability) {
		return new NatTypeInfo(null, Arrays.asList(referenceMutability), Main.rootScope);
	}
	
	public @NonNull CharTypeInfo charTypeInfo(Boolean... referenceMutability) {
		return new CharTypeInfo(null, Arrays.asList(referenceMutability), Main.rootScope);
	}
	
	public abstract void generate();
	
	public void generateRootRoutine() {
		if (!Main.rootScope.routineExists(Global.MAIN_ROUTINE, true)) {
			throw Helpers.error("Main function not found in root scope!");
		}
		
		@NonNull Value main = Main.rootScope.getConstant(null, Global.MAIN_ROUTINE).value;
		if (!main.typeInfo.canImplicitCastTo(mainFunctionTypeInfo)) {
			throw Helpers.error("Main function must have type \"%s\"!", mainFunctionTypeInfo);
		}
		
		Main.rootRoutine.addFunctionAction(null, Main.rootScope.getFunction(null, Global.MAIN_ROUTINE), Main.rootRoutine.nextRegId(voidTypeInfo), main.dataId(), new ArrayList<>(), Main.rootScope);
	}
	
	public void optimizeIntermediate() {
		boolean flag = true;
		while (flag) {
			flag = false;
			Set<Entry<String, Routine>> entrySet = new LinkedHashSet<>();
			Main.rootScope.routineEntryIterable(true).forEach(entrySet::add);
			
			for (Entry<String, Routine> entry : entrySet) {
				Routine routine = entry.getValue();
				if (routine.isFunctionRoutine() && !routine.getFunction().isRequired()) {
					flag = true;
					routine.scope.removeRoutine(null, entry.getKey(), routine);
					((FunctionRoutine) routine).function.setUnused();
				}
			}
		}
		
		for (Routine routine : Main.rootScope.routineIterable(true)) {
			flag = true;
			while (flag) {
				flag = IntermediateOptimization.removeNoOps(routine);
				flag |= IntermediateOptimization.removeDeadActions(routine);
				flag |= IntermediateOptimization.removeEmptySections(routine);
				flag |= IntermediateOptimization.concatenateSections(routine);
				flag |= IntermediateOptimization.simplifyJumps(routine);
				flag |= IntermediateOptimization.compressRegisters(routine);
				flag |= IntermediateOptimization.reorderRvalues(routine);
				flag |= IntermediateOptimization.foldRvalues(routine);
				flag |= IntermediateOptimization.simplifyBinaryOps(routine);
				flag |= IntermediateOptimization.simplifyDereferences(routine);
				flag |= IntermediateOptimization.removeUnusedAssignments(routine);
				flag |= IntermediateOptimization.orderRegisters(routine);
			}
		}
	}
}
