package drlc.generate.drc1;

public class RedstoneMnemonics {
	
	public static final String HLT = format("HLT");
	
	public static final String NOP = format("NOP");
	public static final String LDAI = format("LDAI");
	public static final String NOTI = format("NOTI");
	public static final String ANDI = format("ANDI");
	public static final String ORI = format("ORI");
	public static final String XORI = format("XORI");
	public static final String ADDI = format("ADDI");
	public static final String SUBI = format("SUBI");
	public static final String LSHI = format("LSHI");
	public static final String RSHI = format("RSHI");
	
	public static final String OUT = format("OUT");
	public static final String LDALI = format("LDALI");
	public static final String NOTLI = format("NOTLI");
	public static final String ANDLI = format("ANDLI");
	public static final String ORLI = format("ORLI");
	public static final String XORLI = format("XORLI");
	public static final String ADDLI = format("ADDLI");
	public static final String SUBLI = format("SUBLI");
	
	public static final String LDBP = format("LDBP");
	public static final String LDSP = format("LDSP");
	public static final String PSHBP = format("PSHBP");
	public static final String POPBP = format("POPBP");
	public static final String MSPBP = format("MSPBP");
	public static final String ADDSP = format("ADDSP");
	public static final String SUBSP = format("SUBSP");
	
	public static final String DEA = format("DEA");
	public static final String DEB = format("DEB");
	public static final String STATB = format("STATB");
	public static final String STBTA = format("STBTA");
	
	public static final String CALL = format("CALL");
	public static final String RET = format("RET");
	
	public static final String PSHA = format("PSHA");
	public static final String POPA = format("POPA");
	
	public static final String STA = format("STA");
	public static final String LDA = format("LDA");
	public static final String NOT = format("NOT");
	public static final String AND = format("AND");
	public static final String OR = format("OR");
	public static final String XOR = format("XOR");
	public static final String ADD = format("ADD");
	public static final String SUB = format("SUB");
	public static final String LSH = format("LSH");
	public static final String RSH = format("RSH");
	
	public static final String STB = format("STB");
	public static final String LDB = format("LDB");
	
	public static final String LDEZ = format("LDEZ");
	public static final String LDNEZ = format("LDNEZ");
	public static final String LDLZ = format("LDLZ");
	public static final String LDLEZ = format("LDLEZ");
	public static final String LDMZ = format("LDMZ");
	public static final String LDMEZ = format("LDMEZ");
	
	public static final String LDNOT = format("LDNOT");
	public static final String LDNEG = format("LDNEG");
	
	public static final String JMP = format("JMP");
	public static final String JEZ = format("JEZ");
	public static final String JNEZ = format("JNEZ");
	public static final String JLZ = format("JLZ");
	public static final String JLEZ = format("JLEZ");
	public static final String JMZ = format("JMZ");
	public static final String JMEZ = format("JMEZ");
	
	public static final String STAPB = format("STAPB");
	public static final String LDAPB = format("LDAPB");
	public static final String NOTPB = format("NOTPB");
	public static final String ANDPB = format("ANDPB");
	public static final String ORPB = format("ORPB");
	public static final String XORPB = format("XORPB");
	public static final String ADDPB = format("ADDPB");
	public static final String SUBPB = format("SUBPB");
	public static final String LSHPB = format("LSHPB");
	public static final String RSHPB = format("RSHPB");
	
	public static final String STBPB = format("STBPB");
	public static final String LDBPB = format("LDBPB");
	public static final String LDIPB = format("LDIPB");
	
	public static final String STANB = format("STANB");
	public static final String LDANB = format("LDANB");
	public static final String NOTNB = format("NOTNB");
	public static final String ANDNB = format("ANDNB");
	public static final String ORNB = format("ORNB");
	public static final String XORNB = format("XORNB");
	public static final String ADDNB = format("ADDNB");
	public static final String SUBNB = format("SUBNB");
	public static final String LSHNB = format("LSHNB");
	public static final String RSHNB = format("RSHNB");
	
	public static final String STBNB = format("STBNB");
	public static final String LDBNB = format("LDBNB");
	public static final String LDINB = format("LDINB");
	
	public static final String MULI = format("MULI");
	public static final String MULLI = format("MULLI");
	public static final String MUL = format("MUL");
	public static final String MULPB = format("MULPB");
	public static final String MULNB = format("MULNB");
	
	public static final String DIVI = format("DIVI");
	public static final String DIVLI = format("DIVLI");
	public static final String DIV = format("DIV");
	public static final String DIVPB = format("DIVPB");
	public static final String DIVNB = format("DIVNB");
	
	public static final String MODI = format("MODI");
	public static final String MODLI = format("MODLI");
	public static final String MOD = format("MOD");
	public static final String MODPB = format("MODPB");
	public static final String MODNB = format("MODNB");
	
	private static String format(String mnemonic) {
		return String.format("%-4s", mnemonic);
	}
}
