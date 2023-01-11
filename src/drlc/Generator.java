package drlc;

import java.util.*;
import java.util.Map.Entry;

import drlc.intermediate.*;
import drlc.intermediate.action.binary.BinaryActionType;
import drlc.intermediate.action.unary.UnaryActionType;
import drlc.intermediate.component.*;
import drlc.intermediate.component.constant.*;
import drlc.intermediate.component.info.FunctionModifierInfo;
import drlc.intermediate.component.type.*;
import drlc.intermediate.routine.*;
import drlc.node.Node;

public abstract class Generator {
	
	public static final Map<String, Class<? extends Generator>> CLASS_MAP = new LinkedHashMap<>();
	public static final Map<String, String> NAME_MAP = new HashMap<>();
	
	static {
		put("i", IntermediateGenerator.class, "Intermediate");
		put("s1", drlc.low.drc1.RedstoneAssemblyGenerator.class, "DRC1 Assembly");
		// put("b1", drlc.generate.drc1.RedstoneBinaryGenerator.class, "DRC1 Binary");
		// put("h1", drlc.generate.drc1.RedstoneHexadecimalGenerator.class, "DRC1 Hexadecimal");
		put("oc1", drlc.low.drc1.RedstoneOCGenerator.class, "DRC1 OC Input");
	}
	
	private static void put(String id, Class<? extends Generator> clazz, String name) {
		CLASS_MAP.put(id, clazz);
		NAME_MAP.put(id, name);
	}
	
	protected final String outputFile;
	
	public final Program program;
	
	public final Set<String> directiveSet = new HashSet<>();
	public final Map<String, Function> builtInFunctionMap = new HashMap<>();
	
	public VoidTypeInfo voidTypeInfo;
	public BoolTypeInfo boolTypeInfo;
	public IntTypeInfo intTypeInfo;
	public NatTypeInfo natTypeInfo;
	public CharTypeInfo charTypeInfo;
	
	public VoidTypeInfo wildcardPtrTypeInfo;
	public IntTypeInfo indexTypeInfo;
	
	public VoidConstant voidConstant;
	public VoidConstant nullConstant;
	
	public Generator(String outputFile) {
		this.outputFile = outputFile;
		program = new Program(this);
		directiveSet.addAll(Global.DIRECTIVES);
	}
	
	public void astInit() {
		program.rootScope = new Scope(null, this, null);
	}
	
	public void astFinalize() {
		for (Routine routine : program.routineMap.values()) {
			routine.flattenSections();
			routine.fixUpLabelJumps();
		}
	}
	
	public void addBuiltInTypes(Node node) {
		program.rootScope.addType(node, new Type(Global.VOID, 0));
		program.rootScope.addType(node, new Type(Global.BOOL, 1));
		program.rootScope.addType(node, new Type(Global.INT, getWordSize()));
		program.rootScope.addType(node, new Type(Global.NAT, getWordSize()));
		program.rootScope.addType(node, new Type(Global.CHAR, 1));
		program.rootScope.addType(node, new Type(Global.FN, getAddressSize()));
		
		voidTypeInfo = voidTypeInfo(0);
		boolTypeInfo = boolTypeInfo(0);
		intTypeInfo = intTypeInfo(0);
		natTypeInfo = natTypeInfo(0);
		charTypeInfo = charTypeInfo(0);
		
		wildcardPtrTypeInfo = new VoidTypeInfo(null, program.rootScope, 1);
		indexTypeInfo = new IntTypeInfo(null, program.rootScope, 0);
		
		voidConstant = new VoidConstant(null, voidTypeInfo);
		nullConstant = voidConstant(1, 0);
	}
	
	public void addBuiltInConstants(Node node) {}
	
	public void addBuiltInVariables(Node node) {}
	
	public void addBuiltInFunctions(Node node) {
		builtInFunctionMap.put(Global.INCHAR, new Function(null, Global.INCHAR, true, new FunctionModifierInfo(), charTypeInfo, Helpers.params(), true));
		builtInFunctionMap.put(Global.ININT, new Function(null, Global.ININT, true, new FunctionModifierInfo(), intTypeInfo, Helpers.params(), true));
		builtInFunctionMap.put(Global.OUTCHAR, new Function(null, Global.OUTCHAR, true, new FunctionModifierInfo(), voidTypeInfo, Helpers.params(Helpers.builtInParam("c", charTypeInfo)), true));
		builtInFunctionMap.put(Global.OUTINT, new Function(null, Global.OUTINT, true, new FunctionModifierInfo(), voidTypeInfo, Helpers.params(Helpers.builtInParam("x", intTypeInfo)), true));
		builtInFunctionMap.put(Global.ARGV, new Function(null, Global.ARGV, true, new FunctionModifierInfo(), intTypeInfo, Helpers.params(Helpers.builtInParam("index", indexTypeInfo)), true));
		
		for (Entry<String, Function> entry : builtInFunctionMap.entrySet()) {
			addBuiltInFunction(node, entry.getKey(), entry.getValue());
		}
	}
	
	protected void addBuiltInFunction(Node node, String name, Function function) {
		program.rootScope.addFunction(node, function, false);
		Routine routine = new FunctionRoutine(node, this, name, function);
		program.routineMap.put(name, routine);
		program.builtInRoutineMap.put(name, routine);
	}
	
	public abstract void handleDirectiveCall(Node node, String name, List<Constant> constantList);
	
	public Constant binaryOp(Node node, Scope scope, Constant left, BinaryOpType opType, Constant right) {
		TypeInfo leftInfo = left.typeInfo, rightInfo = right.typeInfo;
		if (leftInfo instanceof BuiltInTypeInfo) {
			checkBasicBinaryOp(node, opType, leftInfo, rightInfo);
			
			if (leftInfo.isAddress(node) || rightInfo.isAddress(node)) {
				return addressBinaryOp(node, scope, left, opType, right);
			}
			
			if (leftInfo.equals(voidTypeInfo)) {
				throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
			}
			else if (leftInfo.equals(boolTypeInfo)) {
				if (!rightInfo.equals(boolTypeInfo)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				BoolConstant boolLeft = (BoolConstant) left, boolRight = (BoolConstant) right;
				switch (opType) {
					case EQUAL_TO:
						return boolConstant(boolLeft.value.equals(boolRight.value));
					case NOT_EQUAL_TO:
						return boolConstant(!boolLeft.value.equals(boolRight.value));
					case LESS_THAN:
						return boolConstant(boolLeft.value.compareTo(boolRight.value) < 0);
					case LESS_OR_EQUAL:
						return boolConstant(boolLeft.value.compareTo(boolRight.value) <= 0);
					case MORE_THAN:
						return boolConstant(boolLeft.value.compareTo(boolRight.value) > 0);
					case MORE_OR_EQUAL:
						return boolConstant(boolLeft.value.compareTo(boolRight.value) >= 0);
					case PLUS:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					case AND:
						return boolConstant(boolLeft.value && boolRight.value);
					case OR:
						return boolConstant(boolLeft.value || boolRight.value);
					case XOR:
						return boolConstant(boolLeft.value ^ boolRight.value);
					case MINUS:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					case MULTIPLY:
						return boolConstant(boolLeft.value && boolRight.value);
					case DIVIDE:
					case REMAINDER:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
			else if (leftInfo.equals(intTypeInfo)) {
				if ((opType.isShift() && !rightInfo.isInteger(node)) || (!opType.isShift() && !rightInfo.equals(intTypeInfo))) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				else {
					return intIntBinaryOp(node, scope, (IntConstant) left, opType, (LongConstant<?, ?>) right);
				}
			}
			else if (leftInfo.equals(natTypeInfo)) {
				if ((opType.isShift() && !rightInfo.isInteger(node)) || (!opType.isShift() && !rightInfo.equals(natTypeInfo))) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				else {
					return natNatBinaryOp(node, scope, (NatConstant) left, opType, (LongConstant<?, ?>) right);
				}
			}
			else if (leftInfo.equals(charTypeInfo)) {
				if (!rightInfo.equals(charTypeInfo)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				CharConstant charLeft = (CharConstant) left, charRight = (CharConstant) right;
				switch (opType) {
					case EQUAL_TO:
						return boolConstant(charLeft.value.equals(charRight.value));
					case NOT_EQUAL_TO:
						return boolConstant(!charLeft.value.equals(charRight.value));
					case LESS_THAN:
						return boolConstant(charLeft.value < charRight.value);
					case LESS_OR_EQUAL:
						return boolConstant(charLeft.value <= charRight.value);
					case MORE_THAN:
						return boolConstant(charLeft.value > charRight.value);
					case MORE_OR_EQUAL:
						return boolConstant(charLeft.value >= charRight.value);
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
		}
		else if (leftInfo.isFunction()) {
			if (leftInfo.isAddress(node) || rightInfo.isAddress(node)) {
				return addressBinaryOp(node, scope, left, opType, right);
			}
			else {
				throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
			}
		}
		throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
	}
	
	protected Constant addressBinaryOp(Node node, Scope scope, Constant left, BinaryOpType opType, Constant right) {
		TypeInfo leftInfo = left.typeInfo, rightInfo = right.typeInfo;
		boolean plusOrMinus = opType == BinaryOpType.PLUS || opType == BinaryOpType.MINUS;
		if (plusOrMinus && (leftInfo.equals(wildcardPtrTypeInfo) || rightInfo.equals(wildcardPtrTypeInfo))) {
			throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
		}
		
		if (leftInfo.isAddress(node)) {
			if (rightInfo.isAddress(node)) {
				if (opType == BinaryOpType.MINUS && !leftInfo.equals(rightInfo)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				switch (opType) {
					case EQUAL_TO:
						return boolConstant(left.address.equals(right.address));
					case NOT_EQUAL_TO:
						return boolConstant(!left.address.equals(right.address));
					case LESS_THAN:
						return boolConstant(left.address < right.address);
					case LESS_OR_EQUAL:
						return boolConstant(left.address <= right.address);
					case MORE_THAN:
						return boolConstant(left.address > right.address);
					case MORE_OR_EQUAL:
						return boolConstant(left.address >= right.address);
					case PLUS:
					case AND:
					case OR:
					case XOR:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					case MINUS:
						return indexConstant((left.address - right.address) / leftInfo.getDerefSize(node, this));
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
			else {
				if (plusOrMinus && rightInfo.isInteger(node)) {
					int size = leftInfo.getDerefSize(node, this);
					return addressConstant(leftInfo, (opType == BinaryOpType.PLUS ? left.address + right.intValue(node) * size : left.address - right.intValue(node) * size));
				}
				else {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
			}
		}
		else {
			if (rightInfo.isAddress(node)) {
				if (plusOrMinus && leftInfo.isInteger(node)) {
					int size = leftInfo.getDerefSize(node, this);
					return addressConstant(rightInfo, (opType == BinaryOpType.PLUS ? left.intValue(node) * size + right.address : left.intValue(node) * size - right.address));
				}
				else {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
			}
			else {
				throw new IllegalArgumentException(String.format("Unexpectedly used address binary op \"%s\" on expressions of types \"%s\" and \"%s\"! %s", opType, leftInfo, rightInfo, node));
			}
		}
	}
	
	public Constant intIntBinaryOp(Node node, Scope scope, IntConstant left, BinaryOpType opType, LongConstant<?, ?> right) {
		switch (opType) {
			case EQUAL_TO:
				return boolConstant(left.value.equals(right.value));
			case NOT_EQUAL_TO:
				return boolConstant(!left.value.equals(right.value));
			case LESS_THAN:
				return boolConstant(left.value < right.value);
			case LESS_OR_EQUAL:
				return boolConstant(left.value <= right.value);
			case MORE_THAN:
				return boolConstant(left.value > right.value);
			case MORE_OR_EQUAL:
				return boolConstant(left.value >= right.value);
			case PLUS:
				return intConstant(left.value + right.value);
			case AND:
				return intConstant(left.value & right.value);
			case OR:
				return intConstant(left.value | right.value);
			case XOR:
				return intConstant(left.value ^ right.value);
			case MINUS:
				return intConstant(left.value - right.value);
			case LEFT_SHIFT:
				return intConstant(left.value << right.intValue(node));
			case RIGHT_SHIFT:
				return intConstant(left.value >> right.intValue(node));
			case LEFT_ROTATE:
				return intConstant(Long.rotateLeft(left.value, right.intValue(node)));
			case RIGHT_ROTATE:
				return intConstant(Long.rotateRight(left.value, right.intValue(node)));
			case MULTIPLY:
				return intConstant(left.value * right.value);
			case DIVIDE:
				return intConstant(left.value / right.value);
			case REMAINDER:
				return intConstant(left.value % right.value);
			default:
				throw unknownBinaryOpType(node, opType, left.typeInfo, right.typeInfo);
		}
	}
	
	public Constant natNatBinaryOp(Node node, Scope scope, NatConstant left, BinaryOpType opType, LongConstant<?, ?> right) {
		switch (opType) {
			case EQUAL_TO:
				return boolConstant(left.value.equals(right.value));
			case NOT_EQUAL_TO:
				return boolConstant(!left.value.equals(right.value));
			case LESS_THAN:
				return boolConstant(Long.compareUnsigned(left.value, right.value) < 0);
			case LESS_OR_EQUAL:
				return boolConstant(Long.compareUnsigned(left.value, right.value) <= 0);
			case MORE_THAN:
				return boolConstant(Long.compareUnsigned(left.value, right.value) > 0);
			case MORE_OR_EQUAL:
				return boolConstant(Long.compareUnsigned(left.value, right.value) >= 0);
			case PLUS:
				return natConstant(left.value + right.value);
			case AND:
				return natConstant(left.value & right.value);
			case OR:
				return natConstant(left.value | right.value);
			case XOR:
				return natConstant(left.value ^ right.value);
			case MINUS:
				return natConstant(left.value - right.value);
			case LEFT_SHIFT:
				return natConstant(left.value << right.value);
			case RIGHT_SHIFT:
				return natConstant(left.value >>> right.value);
			case LEFT_ROTATE:
				return natConstant(Long.rotateLeft(left.value, right.intValue(node)));
			case RIGHT_ROTATE:
				return natConstant(Long.rotateRight(left.value, right.intValue(node)));
			case MULTIPLY:
				return natConstant(left.value * right.value);
			case DIVIDE:
				return natConstant(Long.divideUnsigned(left.value, right.value));
			case REMAINDER:
				return natConstant(Long.remainderUnsigned(left.value, right.value));
			default:
				throw unknownBinaryOpType(node, opType, left.typeInfo, right.typeInfo);
		}
	}
	
	public Constant unaryOp(Node node, Scope scope, UnaryOpType opType, Constant constant) {
		TypeInfo typeInfo = constant.typeInfo;
		if (typeInfo instanceof BuiltInTypeInfo) {
			if (typeInfo.isAddress(node)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
			else if (typeInfo.equals(voidTypeInfo)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
			else if (typeInfo.equals(boolTypeInfo)) {
				BoolConstant boolConstant = (BoolConstant) constant;
				switch (opType) {
					case MINUS:
						throw undefinedUnaryOp(node, opType, typeInfo);
					case NOT:
						return boolConstant(!boolConstant.value);
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(intTypeInfo)) {
				return intUnaryOp(node, scope, opType, (IntConstant) constant);
			}
			else if (typeInfo.equals(natTypeInfo)) {
				return natUnaryOp(node, scope, opType, (NatConstant) constant);
			}
			else if (typeInfo.equals(charTypeInfo)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
		}
		else if (typeInfo.isFunction()) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	public Constant intUnaryOp(Node node, Scope scope, UnaryOpType opType, IntConstant constant) {
		switch (opType) {
			case MINUS:
				return intConstant(-constant.value);
			case NOT:
				return intConstant(~constant.value);
			default:
				throw unknownUnaryOpType(node, opType, constant.typeInfo);
		}
	}
	
	public Constant natUnaryOp(Node node, Scope scope, UnaryOpType opType, NatConstant constant) {
		switch (opType) {
			case MINUS:
				throw undefinedUnaryOp(node, opType, constant.typeInfo);
			case NOT:
				return natConstant(~constant.value);
			default:
				throw unknownUnaryOpType(node, opType, constant.typeInfo);
		}
	}
	
	public BoolConstant boolConstant(boolean value) {
		return new BoolConstant(null, boolTypeInfo, value);
	}
	
	public IntConstant intConstant(long value) {
		return new IntConstant(null, intTypeInfo, value);
	}
	
	public NatConstant natConstant(long value) {
		return new NatConstant(null, natTypeInfo, value);
	}
	
	public CharConstant charConstant(byte value) {
		return new CharConstant(null, charTypeInfo, value);
	}
	
	public VoidConstant voidConstant(int referenceLevel, long address) {
		return new VoidConstant(null, voidTypeInfo(referenceLevel), address, Helpers.Dummy.INSTANCE);
	}
	
	public BoolConstant boolConstant(int referenceLevel, long address) {
		return new BoolConstant(null, boolTypeInfo(referenceLevel), address, Helpers.Dummy.INSTANCE);
	}
	
	public IntConstant intConstant(int referenceLevel, long address) {
		return new IntConstant(null, intTypeInfo(referenceLevel), address, Helpers.Dummy.INSTANCE);
	}
	
	public NatConstant natConstant(int referenceLevel, long address) {
		return new NatConstant(null, natTypeInfo(referenceLevel), address, Helpers.Dummy.INSTANCE);
	}
	
	public CharConstant charConstant(int referenceLevel, long address) {
		return new CharConstant(null, charTypeInfo(referenceLevel), address, Helpers.Dummy.INSTANCE);
	}
	
	public VoidConstant wildcardPtrConstant(long address) {
		return new VoidConstant(null, wildcardPtrTypeInfo, address, Helpers.Dummy.INSTANCE);
	}
	
	public IntConstant indexConstant(long address) {
		return new IntConstant(null, intTypeInfo, address, Helpers.Dummy.INSTANCE);
	}
	
	public Constant addressConstant(TypeInfo typeInfo, long address) {
		if (typeInfo instanceof FunctionTypeInfo) {
			return new FunctionConstant(null, (FunctionTypeInfo) typeInfo, address);
		}
		else {
			switch (typeInfo.type.toString()) {
				case Global.VOID:
					return voidConstant(typeInfo.referenceLevel, address);
				case Global.BOOL:
					return boolConstant(typeInfo.referenceLevel, address);
				case Global.INT:
					return intConstant(typeInfo.referenceLevel, address);
				case Global.NAT:
					return natConstant(typeInfo.referenceLevel, address);
				case Global.CHAR:
					return charConstant(typeInfo.referenceLevel, address);
				default:
					return null;
			}
		}
	}
	
	public TypeInfo binaryOp(Node node, Scope scope, Routine routine, TypeInfo leftInfo, TypeInfo rightInfo, DataId target, DataId arg1, BinaryOpType opType, DataId arg2) {
		if (leftInfo instanceof BuiltInTypeInfo) {
			checkBasicBinaryOp(node, opType, leftInfo, rightInfo);
			
			if (leftInfo.isAddress(node) || rightInfo.isAddress(node)) {
				return addressBinaryOp(node, routine, leftInfo, rightInfo, target, arg1, opType, arg2);
			}
			else if (leftInfo.equals(voidTypeInfo)) {
				throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
			}
			else if (leftInfo.equals(boolTypeInfo)) {
				if (!rightInfo.equals(boolTypeInfo)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.BOOL_EQUAL_TO_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.BOOL_NOT_EQUAL_TO_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_THAN:
						routine.addAction(BinaryActionType.BOOL_LESS_THAN_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.BOOL_LESS_OR_EQUAL_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_THAN:
						routine.addAction(BinaryActionType.BOOL_MORE_THAN_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.BOOL_MORE_OR_EQUAL_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case PLUS:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					case AND:
						routine.addAction(BinaryActionType.BOOL_AND_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case OR:
						routine.addAction(BinaryActionType.BOOL_OR_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case XOR:
						routine.addAction(BinaryActionType.BOOL_XOR_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MINUS:
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					case MULTIPLY:
						routine.addAction(BinaryActionType.BOOL_MULTIPLY_BOOL.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case DIVIDE:
					case REMAINDER:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
			else if (leftInfo.equals(intTypeInfo)) {
				if ((opType.isShift() && !rightInfo.isInteger(node)) || (!opType.isShift() && !rightInfo.equals(intTypeInfo))) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.INT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.INT_NOT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_THAN:
						routine.addAction(BinaryActionType.INT_LESS_THAN_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_LESS_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_THAN:
						routine.addAction(BinaryActionType.INT_MORE_THAN_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_MORE_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case PLUS:
						routine.addAction(BinaryActionType.INT_PLUS_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case AND:
						routine.addAction(BinaryActionType.INT_AND_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case OR:
						routine.addAction(BinaryActionType.INT_OR_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case XOR:
						routine.addAction(BinaryActionType.INT_XOR_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case MINUS:
						routine.addAction(BinaryActionType.INT_MINUS_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case LEFT_SHIFT:
						routine.addAction(BinaryActionType.INT_LEFT_SHIFT_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case RIGHT_SHIFT:
						routine.addAction(BinaryActionType.INT_RIGHT_SHIFT_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case LEFT_ROTATE:
						routine.addAction(BinaryActionType.INT_LEFT_ROTATE_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case RIGHT_ROTATE:
						routine.addAction(BinaryActionType.INT_RIGHT_ROTATE_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case MULTIPLY:
						routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case DIVIDE:
						routine.addAction(BinaryActionType.INT_DIVIDE_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					case REMAINDER:
						routine.addAction(BinaryActionType.INT_REMAINDER_INT.action(node, target, arg1, arg2));
						return intTypeInfo;
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
			else if (leftInfo.equals(natTypeInfo)) {
				if ((opType.isShift() && !rightInfo.isInteger(node)) || (!opType.isShift() && !rightInfo.equals(natTypeInfo))) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.INT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.INT_NOT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_THAN:
						routine.addAction(BinaryActionType.NAT_LESS_THAN_NAT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.NAT_LESS_OR_EQUAL_NAT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_THAN:
						routine.addAction(BinaryActionType.NAT_MORE_THAN_NAT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.NAT_MORE_OR_EQUAL_NAT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case PLUS:
						routine.addAction(BinaryActionType.INT_PLUS_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case AND:
						routine.addAction(BinaryActionType.INT_AND_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case OR:
						routine.addAction(BinaryActionType.INT_OR_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case XOR:
						routine.addAction(BinaryActionType.INT_XOR_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case MINUS:
						routine.addAction(BinaryActionType.INT_MINUS_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case LEFT_SHIFT:
						routine.addAction(BinaryActionType.INT_LEFT_SHIFT_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case RIGHT_SHIFT:
						routine.addAction(BinaryActionType.NAT_RIGHT_SHIFT_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case LEFT_ROTATE:
						routine.addAction(BinaryActionType.INT_LEFT_ROTATE_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case RIGHT_ROTATE:
						routine.addAction(BinaryActionType.INT_RIGHT_ROTATE_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case MULTIPLY:
						routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case DIVIDE:
						routine.addAction(BinaryActionType.NAT_DIVIDE_NAT.action(node, target, arg1, arg2));
						return natTypeInfo;
					case REMAINDER:
						routine.addAction(BinaryActionType.NAT_REMAINDER_NAT.action(node, target, arg1, arg2));
						return natTypeInfo;
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
			else if (leftInfo.equals(charTypeInfo)) {
				if (!rightInfo.equals(charTypeInfo)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.CHAR_EQUAL_TO_CHAR.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.CHAR_NOT_EQUAL_TO_CHAR.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_THAN:
						routine.addAction(BinaryActionType.CHAR_LESS_THAN_CHAR.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.CHAR_LESS_OR_EQUAL_CHAR.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_THAN:
						routine.addAction(BinaryActionType.CHAR_MORE_THAN_CHAR.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.CHAR_MORE_OR_EQUAL_CHAR.action(node, target, arg1, arg2));
						return boolTypeInfo;
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
		}
		else if (leftInfo.isFunction()) {
			if (leftInfo.isAddress(node) || rightInfo.isAddress(node)) {
				return addressBinaryOp(node, routine, leftInfo, rightInfo, target, arg1, opType, arg2);
			}
			else {
				throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
			}
		}
		throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
	}
	
	protected TypeInfo addressBinaryOp(Node node, Routine routine, TypeInfo leftInfo, TypeInfo rightInfo, DataId target, DataId arg1, BinaryOpType opType, DataId arg2) {
		boolean plusOrMinus = opType == BinaryOpType.PLUS || opType == BinaryOpType.MINUS;
		if (plusOrMinus && (leftInfo.equals(wildcardPtrTypeInfo) || rightInfo.equals(wildcardPtrTypeInfo))) {
			throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
		}
		
		if (leftInfo.isAddress(node)) {
			if (rightInfo.isAddress(node)) {
				if (opType == BinaryOpType.MINUS && !leftInfo.equals(rightInfo)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				
				switch (opType) {
					case EQUAL_TO:
						routine.addAction(BinaryActionType.INT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case NOT_EQUAL_TO:
						routine.addAction(BinaryActionType.INT_NOT_EQUAL_TO_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_THAN:
						routine.addAction(BinaryActionType.INT_LESS_THAN_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case LESS_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_LESS_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_THAN:
						routine.addAction(BinaryActionType.INT_MORE_THAN_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case MORE_OR_EQUAL:
						routine.addAction(BinaryActionType.INT_MORE_OR_EQUAL_INT.action(node, target, arg1, arg2));
						return boolTypeInfo;
					case PLUS:
					case AND:
					case OR:
					case XOR:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					case MINUS:
						routine.addAction(BinaryActionType.INT_MINUS_INT.action(node, target, arg1, arg2));
						DataId raw = routine.currentRegId(node);
						
						routine.incrementRegId();
						routine.addImmediateRegisterAssignmentAction(node, leftInfo.getDerefSize(node, this));
						DataId size = routine.currentRegId(node);
						
						routine.incrementRegId();
						routine.addAction(BinaryActionType.INT_DIVIDE_INT.action(node, routine.currentRegId(node), raw, size));
						
						return indexTypeInfo;
					case LEFT_SHIFT:
					case RIGHT_SHIFT:
					case LEFT_ROTATE:
					case RIGHT_ROTATE:
					case MULTIPLY:
					case DIVIDE:
					case REMAINDER:
						throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
					default:
						throw unknownBinaryOpType(node, opType, leftInfo, rightInfo);
				}
			}
			else {
				if (plusOrMinus && rightInfo.isInteger(node)) {
					routine.incrementRegId();
					routine.addImmediateRegisterAssignmentAction(node, leftInfo.getDerefSize(node, this));
					DataId size = routine.currentRegId(node);
					
					routine.incrementRegId();
					routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, routine.currentRegId(node), arg2, size));
					DataId offset = routine.currentRegId(node);
					
					routine.addAction((opType == BinaryOpType.PLUS ? BinaryActionType.INT_PLUS_INT : BinaryActionType.INT_MINUS_INT).action(node, target, arg1, offset));
					
					return leftInfo;
				}
				else {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
			}
		}
		else {
			if (rightInfo.isAddress(node)) {
				if (plusOrMinus && leftInfo.isInteger(node)) {
					routine.incrementRegId();
					routine.addImmediateRegisterAssignmentAction(node, leftInfo.getDerefSize(node, this));
					DataId size = routine.currentRegId(node);
					
					routine.incrementRegId();
					routine.addAction(BinaryActionType.INT_MULTIPLY_INT.action(node, routine.currentRegId(node), arg1, size));
					DataId offset = routine.currentRegId(node);
					
					routine.addAction((opType == BinaryOpType.PLUS ? BinaryActionType.INT_PLUS_INT : BinaryActionType.INT_MINUS_INT).action(node, target, offset, arg2));
					
					return rightInfo;
				}
				else {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
			}
			else {
				throw new IllegalArgumentException(String.format("Unexpectedly used address binary op \"%s\" on expressions of types \"%s\" and \"%s\"! %s", opType, leftInfo, rightInfo, node));
			}
		}
	}
	
	public void checkBasicBinaryOp(Node node, BinaryOpType opType, TypeInfo leftInfo, TypeInfo rightInfo) {
		switch (opType) {
			case EQUAL_TO:
			case NOT_EQUAL_TO:
			case LESS_THAN:
			case LESS_OR_EQUAL:
			case MORE_THAN:
			case MORE_OR_EQUAL:
				break;
			case PLUS:
				if (leftInfo.isAddress(node) && rightInfo.isAddress(node)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				break;
			case AND:
			case OR:
			case XOR:
				if (leftInfo.isAddress(node) || rightInfo.isAddress(node)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				break;
			case MINUS:
				break;
			case LEFT_SHIFT:
			case RIGHT_SHIFT:
			case LEFT_ROTATE:
			case RIGHT_ROTATE:
			case MULTIPLY:
			case DIVIDE:
			case REMAINDER:
				if (leftInfo.isAddress(node) || rightInfo.isAddress(node)) {
					throw undefinedBinaryOp(node, opType, leftInfo, rightInfo);
				}
				break;
		}
	}
	
	protected RuntimeException undefinedBinaryOp(Node node, BinaryOpType opType, TypeInfo leftInfo, TypeInfo rightInfo) {
		return new IllegalArgumentException(String.format("Binary op \"%s\" can not act on expressions of types \"%s\" and \"%s\"! %s", opType, leftInfo, rightInfo, node));
	}
	
	protected RuntimeException unknownBinaryOpType(Node node, BinaryOpType opType, TypeInfo leftInfo, TypeInfo rightInfo) {
		return new IllegalArgumentException(String.format("Attempted to write an expression including a binary op of unknown type! %s", node));
	}
	
	public TypeInfo unaryOp(Node node, Scope scope, Routine routine, TypeInfo typeInfo, DataId target, UnaryOpType opType, DataId arg) {
		if (typeInfo instanceof BuiltInTypeInfo) {
			if (typeInfo.isAddress(node)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
			else if (typeInfo.equals(voidTypeInfo)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
			else if (typeInfo.equals(boolTypeInfo)) {
				switch (opType) {
					case MINUS:
						throw undefinedUnaryOp(node, opType, typeInfo);
					case NOT:
						routine.addAction(UnaryActionType.NOT_BOOL.action(node, target, arg));
						return typeInfo;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(intTypeInfo)) {
				switch (opType) {
					case MINUS:
						routine.addAction(UnaryActionType.MINUS_INT.action(node, target, arg));
						return typeInfo;
					case NOT:
						routine.addAction(UnaryActionType.NOT_INT.action(node, target, arg));
						return typeInfo;
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
						return typeInfo;
					default:
						throw unknownUnaryOpType(node, opType, typeInfo);
				}
			}
			else if (typeInfo.equals(charTypeInfo)) {
				throw undefinedUnaryOp(node, opType, typeInfo);
			}
		}
		else if (typeInfo.isFunction()) {
			throw undefinedUnaryOp(node, opType, typeInfo);
		}
		throw undefinedUnaryOp(node, opType, typeInfo);
	}
	
	protected RuntimeException undefinedUnaryOp(Node node, UnaryOpType opType, TypeInfo typeInfo) {
		return new IllegalArgumentException(String.format("Unary op \"%s\" can not act on an expression of type \"%s\"! %s", opType, typeInfo, node));
	}
	
	protected RuntimeException unknownUnaryOpType(Node node, UnaryOpType opType, TypeInfo typeInfo) {
		return new IllegalArgumentException(String.format("Attempted to write an expression including a unary op of unknown type! %s", node));
	}
	
	public abstract int getWordSize();
	
	public abstract int getAddressSize();
	
	public VoidTypeInfo voidTypeInfo(int referenceLevel) {
		return new VoidTypeInfo(null, program.rootScope, referenceLevel);
	}
	
	public BoolTypeInfo boolTypeInfo(int referenceLevel) {
		return new BoolTypeInfo(null, program.rootScope, referenceLevel);
	}
	
	public IntTypeInfo intTypeInfo(int referenceLevel) {
		return new IntTypeInfo(null, program.rootScope, referenceLevel);
	}
	
	public NatTypeInfo natTypeInfo(int referenceLevel) {
		return new NatTypeInfo(null, program.rootScope, referenceLevel);
	}
	
	public CharTypeInfo charTypeInfo(int referenceLevel) {
		return new CharTypeInfo(null, program.rootScope, referenceLevel);
	}
	
	public TypeInfo builtInTypeInfo(String typeName, int referenceLevel) {
		switch (typeName) {
			case Global.VOID:
				return voidTypeInfo(referenceLevel);
			case Global.BOOL:
				return boolTypeInfo(referenceLevel);
			case Global.INT:
				return intTypeInfo(referenceLevel);
			case Global.NAT:
				return natTypeInfo(referenceLevel);
			case Global.CHAR:
				return charTypeInfo(referenceLevel);
			default:
				return null;
		}
	}
	
	public abstract void generateRootParams(RootRoutine routine);
	
	public abstract void generate();
	
	public void optimizeIntermediate() {
		Map<String, Routine> map = program.routineMap;
		for (String name : new HashSet<>(map.keySet())) {
			Routine routine = map.get(name);
			if (routine.isFunctionRoutine() && !routine.getFunction().required) {
				map.remove(name);
			}
		}
		for (Routine routine : map.values()) {
			boolean flag = true;
			while (flag) {
				flag = IntermediateOptimization.removeNoOps(routine);
				flag |= IntermediateOptimization.removeDeadActions(routine);
				flag |= IntermediateOptimization.simplifySections(routine);
				flag |= IntermediateOptimization.simplifyJumps(routine);
				flag |= IntermediateOptimization.shiftActions(routine);
				flag |= IntermediateOptimization.replaceJumps(routine);
				flag |= IntermediateOptimization.compressRvalueRegisters(routine);
				flag |= IntermediateOptimization.compressLvalueRegisters(routine);
				flag |= IntermediateOptimization.reorderRvalues(routine);
				flag |= IntermediateOptimization.orderRegisters(routine);
				flag |= IntermediateOptimization.simplifyAddressDereferences(routine);
			}
		}
	}
}
