package drlc;

import java.util.*;
import java.util.Map.Entry;

import org.eclipse.jdt.annotation.*;

import drlc.intermediate.*;
import drlc.intermediate.action.*;
import drlc.intermediate.ast.ASTNode;
import drlc.intermediate.component.*;
import drlc.intermediate.component.data.DataId;
import drlc.intermediate.component.type.*;
import drlc.intermediate.component.value.*;
import drlc.intermediate.routine.Routine;
import drlc.intermediate.scope.*;
import drlc.low.drc1.*;

public abstract class Generator {
	
	public static interface GeneratorConstructor extends java.util.function.Function<String, Generator> {}
	
	public static final Map<String, String> NAME_MAP = new LinkedHashMap<>();
	public static final Map<String, GeneratorConstructor> CONSTRUCTOR_MAP = new LinkedHashMap<>();
	
	static {
		put("i", "Intermediate", IntermediateGenerator::new);
		put("s1", "DRC1 Assembly", RedstoneAssemblyGenerator::new);
		put("oc1", "DRC1 OC Input", RedstoneOCGenerator::new);
	}
	
	private static void put(String id, String name, GeneratorConstructor constructor) {
		NAME_MAP.put(id, name);
		CONSTRUCTOR_MAP.put(id, constructor);
	}
	
	protected final String outputFile;
	
	public @SuppressWarnings("null") @NonNull BoolTypeInfo boolTypeInfo = null;
	public @SuppressWarnings("null") @NonNull IntTypeInfo intTypeInfo = null;
	public @SuppressWarnings("null") @NonNull NatTypeInfo natTypeInfo = null;
	public @SuppressWarnings("null") @NonNull CharTypeInfo charTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull TupleTypeInfo unitTypeInfo = null;
	public @SuppressWarnings("null") @NonNull TypeInfo voidTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull IntTypeInfo rootReturnTypeInfo = null;
	public @SuppressWarnings("null") @NonNull FunctionPointerTypeInfo mainFunctionTypeInfo = null;
	
	public @SuppressWarnings("null") @NonNull TupleValue unitValue = null;
	public @SuppressWarnings("null") @NonNull AddressValue nullValue = null;
	
	public @SuppressWarnings("null") @NonNull BoolValue falseValue = null;
	public @SuppressWarnings("null") @NonNull BoolValue trueValue = null;
	
	public Generator(String outputFile) {
		this.outputFile = outputFile;
	}
	
	public void init() {
		addBuiltInTypes();
		addBuiltInConstants();
		addBuiltInVariables();
		addBuiltInFunctions();
	}
	
	public void addBuiltInTypes() {
		Main.rootScope.addTypeAlias(null, Global.BOOL, boolTypeInfo = boolTypeInfo());
		Main.rootScope.addTypeAlias(null, Global.INT, intTypeInfo = intTypeInfo());
		Main.rootScope.addTypeAlias(null, Global.NAT, natTypeInfo = natTypeInfo());
		Main.rootScope.addTypeAlias(null, Global.CHAR, charTypeInfo = charTypeInfo());
		
		unitTypeInfo = new TupleTypeInfo(null, new ArrayList<>(), new ArrayList<>());
		Main.rootScope.addTypeAlias(null, Global.VOID, voidTypeInfo = unitTypeInfo.addressOf(null, true));
		
		Main.rootScope.addStructTypeDef(null, Global.BOOLS, Helpers.arrayList(boolTypeInfo(true), natTypeInfo), Helpers.arrayList(Global.PTR, Global.LEN));
		Main.rootScope.addStructTypeDef(null, Global.INTS, Helpers.arrayList(intTypeInfo(true), natTypeInfo), Helpers.arrayList(Global.PTR, Global.LEN));
		Main.rootScope.addStructTypeDef(null, Global.NATS, Helpers.arrayList(natTypeInfo(true), natTypeInfo), Helpers.arrayList(Global.PTR, Global.LEN));
		Main.rootScope.addStructTypeDef(null, Global.CHARS, Helpers.arrayList(charTypeInfo(true), natTypeInfo), Helpers.arrayList(Global.PTR, Global.LEN));
		
		rootReturnTypeInfo = intTypeInfo;
		mainFunctionTypeInfo = new FunctionPointerTypeInfo(null, new ArrayList<>(), unitTypeInfo, new ArrayList<>());
		
		unitValue = new TupleValue(null, unitTypeInfo, new ArrayList<>());
		nullValue = addressValue(voidTypeInfo, 0);
		
		falseValue = boolValue(false);
		trueValue = boolValue(true);
	}
	
	public void addBuiltInConstants() {}
	
	public void addBuiltInVariables() {}
	
	protected void addBuiltInFunction(@NonNull String name, @NonNull TypeInfo returnTypeInfo, DeclaratorInfo... params) {
		Function function = Helpers.builtInFunction(name, returnTypeInfo, params);
		Main.rootScope.addFunction(null, function);
		Main.rootScope.addRoutine(null, new Routine(function));
		
		Scope functionScope = new FunctionScope(null, Main.rootScope);
		for (DeclaratorInfo param : params) {
			param.variable.scope = functionScope;
		}
	}
	
	public void addBuiltInFunctions() {
		addBuiltInFunction(Global.READ_BOOL, boolTypeInfo);
		addBuiltInFunction(Global.READ_INT, intTypeInfo);
		addBuiltInFunction(Global.READ_NAT, natTypeInfo);
		addBuiltInFunction(Global.READ_CHAR, charTypeInfo);
		
		addBuiltInFunction(Global.PRINT_BOOL, unitTypeInfo, Helpers.builtInDeclarator("x", boolTypeInfo));
		addBuiltInFunction(Global.PRINT_INT, unitTypeInfo, Helpers.builtInDeclarator("x", intTypeInfo));
		addBuiltInFunction(Global.PRINT_NAT, unitTypeInfo, Helpers.builtInDeclarator("x", natTypeInfo));
		addBuiltInFunction(Global.PRINT_CHAR, unitTypeInfo, Helpers.builtInDeclarator("x", charTypeInfo));
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
	
	public @NonNull CharValue charValue(int value) {
		return new CharValue(null, value);
	}
	
	public @NonNull AddressValue addressValue(@NonNull TypeInfo typeInfo, long address) {
		return new AddressValue(null, typeInfo, address);
	}
	
	public String getStringLiteral(ASTNode<?> node, String raw) {
		return raw;
	}
	
	public abstract int getWordSize();
	
	public abstract int getFunctionSize();
	
	public abstract int getAddressSize();
	
	public @NonNull Function getBuiltInFunction(ASTNode<?> node, String name) {
		return Main.rootScope.getFunction(node, name, false);
	}
	
	public @NonNull BoolTypeInfo boolTypeInfo(Boolean... referenceMutability) {
		return new BoolTypeInfo(null, Arrays.asList(referenceMutability));
	}
	
	public @NonNull IntTypeInfo intTypeInfo(Boolean... referenceMutability) {
		return new IntTypeInfo(null, Arrays.asList(referenceMutability));
	}
	
	public @NonNull NatTypeInfo natTypeInfo(Boolean... referenceMutability) {
		return new NatTypeInfo(null, Arrays.asList(referenceMutability));
	}
	
	public @NonNull CharTypeInfo charTypeInfo(Boolean... referenceMutability) {
		return new CharTypeInfo(null, Arrays.asList(referenceMutability));
	}
	
	// Binary Ops
	
	public @NonNull Value<?> binaryOp(ASTNode<?> node, @NonNull Value<?> left, @NonNull BinaryOpType opType, @NonNull Value<?> right) {
		TypeInfo leftType = left.typeInfo, rightType = right.typeInfo;
		
		if (left instanceof AddressValue || right instanceof AddressValue) {
			return addressBinaryOp(node, left, opType, right);
		}
		else if (leftType.equals(boolTypeInfo)) {
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
				return intIntBinaryOp(node, (IntValue) left, opType, intValue(right.longValue(node)));
			}
		}
		else if (leftType.equals(natTypeInfo)) {
			if ((opType.isShift() && !rightType.isWord()) || (!opType.isShift() && !rightType.equals(natTypeInfo))) {
				throw undefinedBinaryOp(node, leftType, opType, rightType);
			}
			else {
				return natNatBinaryOp(node, (NatValue) left, opType, natValue(right.longValue(node)));
			}
		}
		else if (leftType.equals(charTypeInfo)) {
			if (!rightType.equals(charTypeInfo)) {
				throw undefinedBinaryOp(node, leftType, opType, rightType);
			}
			
			CharValue charLeft = (CharValue) left, charRight = (CharValue) right;
			switch (opType) {
				case LOGICAL_AND:
				case LOGICAL_OR:
					throw undefinedBinaryOp(node, leftType, opType, rightType);
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
				case PLUS:
					return charValue(charLeft.value + charRight.value);
				case AND:
					return charValue(charLeft.value & charRight.value);
				case OR:
					return charValue(charLeft.value | charRight.value);
				case XOR:
					return charValue(charLeft.value ^ charRight.value);
				case MINUS:
					return charValue(charLeft.value - charRight.value);
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
		throw undefinedBinaryOp(node, leftType, opType, rightType);
	}
	
	protected @NonNull Value<?> addressBinaryOp(ASTNode<?> node, @NonNull Value<?> left, @NonNull BinaryOpType opType, @NonNull Value<?> right) {
		TypeInfo leftType = left.typeInfo, rightType = right.typeInfo;
		boolean plusOrMinus = opType.equals(BinaryOpType.PLUS) || opType.equals(BinaryOpType.MINUS);
		
		if (left instanceof AddressValue leftAddress) {
			if (right instanceof AddressValue rightAddress) {
				if (opType.equals(BinaryOpType.MINUS) && !leftType.equals(rightType)) {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
				
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
						return intValue((leftAddress.address - rightAddress.address) / leftType.getAddressOffsetSize(node));
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
					long rightLong = right.longValue(node);
					int size = leftType.getAddressOffsetSize(node);
					return addressValue(leftType, opType.equals(BinaryOpType.PLUS) ? leftAddress.address + rightLong * size : leftAddress.address - rightLong * size);
				}
				else {
					throw undefinedBinaryOp(node, leftType, opType, rightType);
				}
			}
		}
		else {
			if (right instanceof AddressValue rightAddress) {
				if (plusOrMinus && leftType.isWord()) {
					long leftLong = left.longValue(node);
					int size = leftType.getAddressOffsetSize(node);
					return addressValue(rightType, opType.equals(BinaryOpType.PLUS) ? leftLong * size + rightAddress.address : leftLong * size - rightAddress.address);
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
	
	public @NonNull Value<?> intIntBinaryOp(ASTNode<?> node, IntValue left, @NonNull BinaryOpType opType, IntValue right) {
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
				throw undefinedBinaryOp(node, left.typeInfo, opType, right.typeInfo);
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
				return intValue(left.value << right.value);
			case RIGHT_SHIFT:
				return intValue(left.value >> right.value);
			case LEFT_ROTATE:
				return intValue(Long.rotateLeft(left.value, right.intValue(node)));
			case RIGHT_ROTATE:
				return intValue(Long.rotateRight(left.value, right.intValue(node)));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	public @NonNull Value<?> natNatBinaryOp(ASTNode<?> node, NatValue left, @NonNull BinaryOpType opType, NatValue right) {
		switch (opType) {
			case LOGICAL_AND:
			case LOGICAL_OR:
				throw undefinedBinaryOp(node, left.typeInfo, opType, right.typeInfo);
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
				return natValue(left.value << right.value);
			case RIGHT_SHIFT:
				return natValue(left.value >>> right.value);
			case LEFT_ROTATE:
				return natValue(Long.rotateLeft(left.value, right.intValue(node)));
			case RIGHT_ROTATE:
				return natValue(Long.rotateRight(left.value, right.intValue(node)));
			default:
				throw unknownBinaryOpType(node, left.typeInfo, opType, right.typeInfo);
		}
	}
	
	public @NonNull TypeInfo binaryOpTypeInfo(ASTNode<?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		if (leftType.isAddress() || rightType.isAddress()) {
			return addressBinaryOpTypeInfo(node, leftType, opType, rightType);
		}
		else if (leftType.equals(boolTypeInfo)) {
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
					return charTypeInfo;
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
		throw undefinedBinaryOp(node, leftType, opType, rightType);
	}
	
	protected @NonNull TypeInfo addressBinaryOpTypeInfo(ASTNode<?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		boolean plusOrMinus = opType.equals(BinaryOpType.PLUS) || opType.equals(BinaryOpType.MINUS);
		
		if (leftType.isAddress()) {
			if (rightType.isAddress()) {
				if (opType.equals(BinaryOpType.MINUS) && !leftType.equals(rightType)) {
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
						return intTypeInfo;
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
	
	public void binaryOpAction(ASTNode<?> node, @NonNull Routine routine, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType, DataId target, DataId arg1, DataId arg2) {
		if (leftType.isAddress() || rightType.isAddress()) {
			addressBinaryOpAction(node, routine, leftType, opType, rightType, target, arg1, arg2);
			return;
		}
		else if (leftType.equals(boolTypeInfo)) {
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
					routine.addAction(BinaryActionType.CHAR_PLUS_CHAR.action(node, target, arg1, arg2));
					return;
				case AND:
					routine.addAction(BinaryActionType.CHAR_AND_CHAR.action(node, target, arg1, arg2));
					return;
				case OR:
					routine.addAction(BinaryActionType.CHAR_OR_CHAR.action(node, target, arg1, arg2));
					return;
				case XOR:
					routine.addAction(BinaryActionType.CHAR_XOR_CHAR.action(node, target, arg1, arg2));
					return;
				case MINUS:
					routine.addAction(BinaryActionType.CHAR_MINUS_CHAR.action(node, target, arg1, arg2));
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
		throw undefinedBinaryOp(node, leftType, opType, rightType);
	}
	
	protected void addressBinaryOpAction(ASTNode<?> node, @NonNull Routine routine, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType, DataId target, DataId arg1, DataId arg2) {
		boolean plusOrMinus = opType.equals(BinaryOpType.PLUS) || opType.equals(BinaryOpType.MINUS);
		
		if (leftType.isAddress()) {
			if (rightType.isAddress()) {
				if (opType.equals(BinaryOpType.MINUS) && !leftType.equals(rightType)) {
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
						routine.addAction(BinaryActionType.INT_DIVIDE_INT.action(node, target, raw, intValue(leftType.getAddressOffsetSize(node)).dataId()));
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
					DataId offset = routine.nextRegId(rightType);
					int offsetSize = leftType.getAddressOffsetSize(node);
					routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, offset, arg2, (rightType.equals(intTypeInfo) ? intValue(offsetSize) : natValue(offsetSize)).dataId()));
					routine.addAction((opType.equals(BinaryOpType.PLUS) ? BinaryActionType.INT_PLUS_INT : BinaryActionType.INT_MINUS_INT).action(node, target, arg1, offset));
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
					DataId offset = routine.nextRegId(leftType);
					int offsetSize = rightType.getAddressOffsetSize(node);
					routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, offset, arg1, (leftType.equals(intTypeInfo) ? intValue(offsetSize) : natValue(offsetSize)).dataId()));
					routine.addAction((opType.equals(BinaryOpType.PLUS) ? BinaryActionType.INT_PLUS_INT : BinaryActionType.INT_MINUS_INT).action(node, target, offset, arg2));
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
	
	protected RuntimeException undefinedBinaryOp(ASTNode<?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		return Helpers.nodeError(node, "Binary op \"%s\" can not act on expressions of types \"%s\" and \"%s\"!", opType, leftType, rightType);
	}
	
	protected RuntimeException unknownBinaryOpType(ASTNode<?> node, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType, @NonNull TypeInfo rightType) {
		return Helpers.nodeError(node, "Attempted to write an expression including a binary op of unknown type!");
	}
	
	// Unary Ops
	
	public @NonNull Value<?> unaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull Value<?> value) {
		TypeInfo typeInfo = value.typeInfo;
		if (value instanceof AddressValue) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		else if (typeInfo.equals(boolTypeInfo)) {
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
			byte byteValue = value.byteValue(node);
			switch (opType) {
				case MINUS:
					throw undefinedUnaryOp(node, opType, typeInfo);
				case NOT:
					return charValue(~byteValue);
				default:
					throw unknownUnaryOpType(node, opType, typeInfo);
			}
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	public @NonNull Value<?> intUnaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull IntValue value) {
		switch (opType) {
			case MINUS:
				return intValue(-value.value);
			case NOT:
				return intValue(~value.value);
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	public @NonNull Value<?> natUnaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull NatValue value) {
		switch (opType) {
			case MINUS:
				throw undefinedUnaryOp(node, opType, value.typeInfo);
			case NOT:
				return natValue(~value.value);
			default:
				throw unknownUnaryOpType(node, opType, value.typeInfo);
		}
	}
	
	public @NonNull TypeInfo unaryOpTypeInfo(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		if (typeInfo.isAddress()) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		else if (typeInfo.equals(boolTypeInfo)) {
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
			switch (opType) {
				case MINUS:
					throw undefinedUnaryOp(node, opType, typeInfo);
				case NOT:
					return charTypeInfo;
				default:
					throw unknownUnaryOpType(node, opType, typeInfo);
			}
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	public void unaryOpAction(ASTNode<?> node, @NonNull Routine routine, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo, DataId target, DataId arg) {
		if (typeInfo.isAddress()) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		else if (typeInfo.equals(boolTypeInfo)) {
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
			switch (opType) {
				case MINUS:
					throw undefinedUnaryOp(node, opType, typeInfo);
				case NOT:
					routine.addAction(UnaryActionType.NOT_CHAR.action(node, target, arg));
					return;
				default:
					throw unknownUnaryOpType(node, opType, typeInfo);
			}
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	protected RuntimeException undefinedUnaryOp(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		return Helpers.nodeError(node, "Unary op \"%s\" can not act on an expression of type \"%s\"!", opType, typeInfo);
	}
	
	protected RuntimeException unknownUnaryOpType(ASTNode<?> node, @NonNull UnaryOpType opType, @NonNull TypeInfo typeInfo) {
		return Helpers.nodeError(node, "Attempted to write an expression including a unary op of unknown type!");
	}
	
	// Type Casts
	
	public @Nullable Value<?> typeCast(ASTNode<?> node, @NonNull TypeInfo castType, @NonNull Value<?> value) {
		TypeInfo typeInfo = value.typeInfo;
		if (typeInfo instanceof ClosureTypeInfo closureTypeInfo) {
			if (typeInfo.isAddress()) {
				return null;
			}
			else if (closureTypeInfo.count == 0) {
				FunctionItemValue functionItemValue = closureTypeInfo.function.value;
				if (functionItemValue.typeInfo.copy(node, closureTypeInfo.referenceMutability).canImplicitCastTo(castType)) {
					return functionItemValue;
				}
			}
		}
		else if (typeInfo.canImplicitCastTo(castType)) {
			return value;
		}
		else if (typeInfo.isAddress()) {
			long valueLong = value.longValue(node);
			if (castType.isAddress()) {
				return addressValue(castType, valueLong);
			}
			else if (castType.equals(intTypeInfo)) {
				return intValue(addressToWordCast(node, valueLong));
			}
			else if (castType.equals(natTypeInfo)) {
				return natValue(addressToWordCast(node, valueLong));
			}
		}
		else if (typeInfo.equals(boolTypeInfo)) {
			long valueLong = boolToWordCast(node, value.boolValue(node));
			if (castType.equals(intTypeInfo)) {
				return intValue(valueLong);
			}
			else if (castType.equals(natTypeInfo)) {
				return natValue(valueLong);
			}
		}
		else if (typeInfo.isWord()) {
			long valueLong = value.longValue(node);
			if (castType.isAddress()) {
				return addressValue(castType, wordToAddressCast(node, valueLong));
			}
			else if (castType.equals(intTypeInfo)) {
				return intValue(valueLong);
			}
			else if (castType.equals(natTypeInfo)) {
				return natValue(valueLong);
			}
			else if (castType.equals(charTypeInfo)) {
				return charValue(wordToCharCast(node, valueLong));
			}
		}
		else if (typeInfo.equals(charTypeInfo)) {
			long valueLong = charToWordCast(node, value.byteValue(node));
			if (castType.equals(intTypeInfo)) {
				return intValue(valueLong);
			}
			else if (castType.equals(natTypeInfo)) {
				return natValue(valueLong);
			}
		}
		throw undefinedTypeCast(node, castType, typeInfo);
	}
	
	public long addressToWordCast(ASTNode<?> node, long valueLong) {
		return valueLong;
	}
	
	public long boolToWordCast(ASTNode<?> node, boolean valueBool) {
		return valueBool ? 1 : 0;
	}
	
	public long wordToAddressCast(ASTNode<?> node, long valueLong) {
		return valueLong;
	}
	
	public int wordToCharCast(ASTNode<?> node, long valueLong) {
		return (int) valueLong;
	}
	
	public long charToWordCast(ASTNode<?> node, byte valueByte) {
		return valueByte;
	}
	
	public void typeCastAction(ASTNode<?> node, Scope scope, @NonNull Routine routine, @NonNull TypeInfo castType, @NonNull TypeInfo typeInfo, DataId target, DataId arg) {
		if (typeInfo instanceof ClosureTypeInfo closureTypeInfo) {
			if (closureTypeInfo.count == 0) {
				FunctionItemValue functionItemValue = closureTypeInfo.function.value;
				if (functionItemValue.typeInfo.copy(node, closureTypeInfo.referenceMutability).canImplicitCastTo(castType)) {
					if (closureTypeInfo.isAddress()) {
						@NonNull TypeInfo rawCastType = castType.copy(node);
						@NonNull DataId dataId = closureTypeInfo.isAddress() ? scope.nextLocalDataId(routine, rawCastType) : routine.nextRegId(rawCastType);
						routine.addValueAssignmentAction(node, dataId, functionItemValue);
						dataId = routine.addSelfAddressAssignmentAction(node, scope, closureTypeInfo.getReferenceLevel(), dataId);
						routine.addAssignmentAction(node, target, dataId);
					}
					else {
						routine.addValueAssignmentAction(node, target, functionItemValue);
					}
					return;
				}
			}
		}
		else if (typeInfo.canImplicitCastTo(castType)) {
			routine.addAssignmentAction(node, target, arg);
			return;
		}
		else if (typeInfo.isAddress()) {
			if (castType.isAddress()) {
				routine.addAssignmentAction(node, target, arg);
				return;
			}
			else if (castType.isWord()) {
				addressToWordCastAction(node, routine, target, arg);
				return;
			}
		}
		else if (typeInfo.equals(boolTypeInfo)) {
			if (castType.isWord()) {
				boolToWordCastAction(node, routine, target, arg);
				return;
			}
		}
		else if (typeInfo.isWord()) {
			if (castType.isAddress()) {
				wordToAddressCastAction(node, routine, target, arg);
				return;
			}
			else if (castType.isWord()) {
				routine.addAssignmentAction(node, target, arg);
				return;
			}
			else if (castType.equals(charTypeInfo)) {
				wordToCharCastAction(node, routine, target, arg);
				return;
			}
		}
		else if (typeInfo.equals(charTypeInfo)) {
			if (castType.isWord()) {
				charToWordCastAction(node, routine, target, arg);
				return;
			}
		}
		throw undefinedTypeCast(node, castType, typeInfo);
	}
	
	public abstract void addressToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg);
	
	public abstract void boolToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg);
	
	public abstract void wordToAddressCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg);
	
	public abstract void wordToCharCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg);
	
	public abstract void charToWordCastAction(ASTNode<?> node, @NonNull Routine routine, DataId target, DataId arg);
	
	protected RuntimeException undefinedTypeCast(ASTNode<?> node, @NonNull TypeInfo castType, @NonNull TypeInfo typeInfo) {
		return Helpers.nodeError(node, "Can not cast expression of type \"%s\" to type \"%s\"!", typeInfo, castType);
	}
	
	// Type Inference
	
	public @Nullable TypeInfo binaryOpLeftInverseTypeInfo(ASTNode<?> node, @NonNull TypeInfo targetTypeInfo, @NonNull BinaryOpType opType) {
		if (targetTypeInfo.equals(boolTypeInfo)) {
			switch (opType) {
				case LOGICAL_AND:
				case LOGICAL_OR:
				case AND:
				case OR:
				case XOR:
					return boolTypeInfo;
				default:
					return null;
			}
		}
		else if (targetTypeInfo.isWord()) {
			switch (opType) {
				case PLUS:
				case AND:
				case OR:
				case XOR:
					return targetTypeInfo;
				case MINUS:
					return targetTypeInfo.equals(natTypeInfo) ? natTypeInfo : null;
				case MULTIPLY:
				case DIVIDE:
				case REMAINDER:
				case LEFT_SHIFT:
				case RIGHT_SHIFT:
				case LEFT_ROTATE:
				case RIGHT_ROTATE:
					return targetTypeInfo;
				default:
					return null;
			}
		}
		else if (targetTypeInfo.equals(charTypeInfo)) {
			switch (opType) {
				case PLUS:
				case AND:
				case OR:
				case XOR:
				case MINUS:
					return targetTypeInfo;
				default:
					return null;
			}
		}
		else {
			return null;
		}
	}
	
	public @Nullable TypeInfo binaryOpRightInverseTypeInfo(ASTNode<?> node, @Nullable TypeInfo targetTypeInfo, @NonNull TypeInfo leftType, @NonNull BinaryOpType opType) {
		if (leftType.equals(boolTypeInfo)) {
			return boolTypeInfo;
		}
		else if (leftType.isWord()) {
			return opType.isShift() ? null : leftType;
		}
		else if (leftType.equals(charTypeInfo)) {
			return charTypeInfo;
		}
		else if (leftType.isAddress() && intTypeInfo.equals(targetTypeInfo)) {
			return leftType;
		}
		else {
			return null;
		}
	}
	
	public @Nullable TypeInfo unaryOpInverseTypeInfo(ASTNode<?> node, @NonNull TypeInfo targetTypeInfo, @NonNull UnaryOpType opType) {
		if (targetTypeInfo.equals(boolTypeInfo) || targetTypeInfo.isWord() || targetTypeInfo.equals(charTypeInfo)) {
			return targetTypeInfo;
		}
		else {
			return null;
		}
	}
	
	// Code Generation
	
	public abstract void generate();
	
	public void generateRootRoutine() {
		Function mainFunction = null;
		for (Routine routine : Main.rootScope.routineMap.values()) {
			if (routine.function.name.equals(Global.MAIN)) {
				mainFunction = routine.function;
				break;
			}
		}
		
		if (mainFunction == null) {
			throw Helpers.error("Main function not found in root scope!");
		}
		mainFunction.setRequired();
		
		@NonNull Value<?> mainItem = mainFunction.value;
		if (!mainItem.typeInfo.canImplicitCastTo(mainFunctionTypeInfo)) {
			throw Helpers.error("Main function must have type \"%s\"!", mainFunctionTypeInfo);
		}
		
		Main.rootRoutine.addCallAction(null, Main.rootScope, mainFunction, Main.rootRoutine.nextRegId(unitTypeInfo), mainItem.dataId(), new ArrayList<>());
		Main.rootRoutine.destruction.add(new ExitAction(null, Main.generator.intValue(0).dataId()));
	}
	
	public void optimizeIntermediate() {
		boolean flag = true;
		while (flag) {
			flag = false;
			for (Entry<Function, Routine> entry : new LinkedHashSet<>(Main.rootScope.routineMap.entrySet())) {
				Routine routine = entry.getValue();
				if (!routine.function.isRequired()) {
					flag = true;
					Main.rootScope.removeRoutine(null, entry.getKey());
					routine.function.setUnused();
				}
			}
		}
		
		for (Routine routine : Main.rootScope.routineMap.values()) {
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
