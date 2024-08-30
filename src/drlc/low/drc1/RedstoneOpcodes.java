package drlc.low.drc1;

import java.util.*;

import drlc.Helpers;

public class RedstoneOpcodes {
	
	private static final Map<String, String> MAP = new HashMap<>();
	
	static {
		put(RedstoneMnemonics.HLT, 0xFF);
		
		put(RedstoneMnemonics.NOP, 0x00);
		put(RedstoneMnemonics.LDAI, 0x01);
		put(RedstoneMnemonics.NOTI, 0x02);
		put(RedstoneMnemonics.ANDI, 0x03);
		put(RedstoneMnemonics.ORI, 0x04);
		put(RedstoneMnemonics.XORI, 0x05);
		put(RedstoneMnemonics.ADDI, 0x06);
		put(RedstoneMnemonics.SUBI, 0x07);
		put(RedstoneMnemonics.LSHI, 0x08);
		put(RedstoneMnemonics.RSHI, 0x09);
		
		put(RedstoneMnemonics.ADDBI, 0x0A);
		put(RedstoneMnemonics.SUBBI, 0x0B);
		
		put(RedstoneMnemonics.OUT, 0x10);
		put(RedstoneMnemonics.LDALI, 0x11);
		put(RedstoneMnemonics.NOTLI, 0x12);
		put(RedstoneMnemonics.ANDLI, 0x13);
		put(RedstoneMnemonics.ORLI, 0x14);
		put(RedstoneMnemonics.XORLI, 0x15);
		put(RedstoneMnemonics.ADDLI, 0x16);
		put(RedstoneMnemonics.SUBLI, 0x17);
		
		put(RedstoneMnemonics.LDBP, 0x20);
		put(RedstoneMnemonics.LDSP, 0x21);
		put(RedstoneMnemonics.PSHBP, 0x22);
		put(RedstoneMnemonics.POPBP, 0x23);
		put(RedstoneMnemonics.MSPBP, 0x24);
		put(RedstoneMnemonics.ADDSP, 0x25);
		put(RedstoneMnemonics.SUBSP, 0x26);
		
		put(RedstoneMnemonics.DEA, 0x2A);
		put(RedstoneMnemonics.DEB, 0x2B);
		put(RedstoneMnemonics.STATB, 0x2C);
		put(RedstoneMnemonics.STBTA, 0x2D);
		
		put(RedstoneMnemonics.CALL, 0x30);
		put(RedstoneMnemonics.CALLF, 0x31);
		put(RedstoneMnemonics.RET, 0x32);
		
		put(RedstoneMnemonics.PSHA, 0x3A);
		put(RedstoneMnemonics.POPA, 0x3B);
		
		put(RedstoneMnemonics.STA, 0x40);
		put(RedstoneMnemonics.LDA, 0x41);
		put(RedstoneMnemonics.NOT, 0x42);
		put(RedstoneMnemonics.AND, 0x43);
		put(RedstoneMnemonics.OR, 0x44);
		put(RedstoneMnemonics.XOR, 0x45);
		put(RedstoneMnemonics.ADD, 0x46);
		put(RedstoneMnemonics.SUB, 0x47);
		put(RedstoneMnemonics.LSH, 0x48);
		put(RedstoneMnemonics.RSH, 0x49);
		
		put(RedstoneMnemonics.STB, 0x4A);
		put(RedstoneMnemonics.LDB, 0x4B);
		
		put(RedstoneMnemonics.LDEZ, 0x51);
		put(RedstoneMnemonics.LDNEZ, 0x52);
		put(RedstoneMnemonics.LDLZ, 0x53);
		put(RedstoneMnemonics.LDLEZ, 0x54);
		put(RedstoneMnemonics.LDMZ, 0x55);
		put(RedstoneMnemonics.LDMEZ, 0x56);
		
		put(RedstoneMnemonics.LDNOT, 0x5A);
		put(RedstoneMnemonics.LDNEG, 0x5B);
		
		put(RedstoneMnemonics.JMP, 0x70);
		put(RedstoneMnemonics.JEZ, 0x71);
		put(RedstoneMnemonics.JNEZ, 0x72);
		put(RedstoneMnemonics.JLZ, 0x73);
		put(RedstoneMnemonics.JLEZ, 0x74);
		put(RedstoneMnemonics.JMZ, 0x75);
		put(RedstoneMnemonics.JMEZ, 0x76);
		
		put(RedstoneMnemonics.JMPL, 0x77);
		put(RedstoneMnemonics.JEZL, 0x78);
		put(RedstoneMnemonics.JNEZL, 0x79);
		put(RedstoneMnemonics.JLZL, 0x7A);
		put(RedstoneMnemonics.JLEZL, 0x7B);
		put(RedstoneMnemonics.JMZL, 0x7C);
		put(RedstoneMnemonics.JMEZL, 0x7D);
		
		put(RedstoneMnemonics.STAPB, 0x80);
		put(RedstoneMnemonics.LDAPB, 0x81);
		put(RedstoneMnemonics.NOTPB, 0x82);
		put(RedstoneMnemonics.ANDPB, 0x83);
		put(RedstoneMnemonics.ORPB, 0x84);
		put(RedstoneMnemonics.XORPB, 0x85);
		put(RedstoneMnemonics.ADDPB, 0x86);
		put(RedstoneMnemonics.SUBPB, 0x87);
		put(RedstoneMnemonics.LSHPB, 0x88);
		put(RedstoneMnemonics.RSHPB, 0x89);
		
		put(RedstoneMnemonics.STBPB, 0x8A);
		put(RedstoneMnemonics.LDBPB, 0x8B);
		put(RedstoneMnemonics.LDIPB, 0x8C);
		put(RedstoneMnemonics.ADDIPB, 0x8D);
		put(RedstoneMnemonics.SUBIPB, 0x8E);
		
		put(RedstoneMnemonics.STANB, 0xA0);
		put(RedstoneMnemonics.LDANB, 0xA1);
		put(RedstoneMnemonics.NOTNB, 0xA2);
		put(RedstoneMnemonics.ANDNB, 0xA3);
		put(RedstoneMnemonics.ORNB, 0xA4);
		put(RedstoneMnemonics.XORNB, 0xA5);
		put(RedstoneMnemonics.ADDNB, 0xA6);
		put(RedstoneMnemonics.SUBNB, 0xA7);
		put(RedstoneMnemonics.LSHNB, 0xA8);
		put(RedstoneMnemonics.RSHNB, 0xA9);
		
		put(RedstoneMnemonics.STBNB, 0xAA);
		put(RedstoneMnemonics.LDBNB, 0xAB);
		put(RedstoneMnemonics.LDINB, 0xAC);
		put(RedstoneMnemonics.ADDINB, 0xAD);
		put(RedstoneMnemonics.SUBINB, 0xAE);
		
		put(RedstoneMnemonics.STAL, 0xB0);
		put(RedstoneMnemonics.LDAL, 0xB1);
		put(RedstoneMnemonics.NOTL, 0xB2);
		put(RedstoneMnemonics.ANDL, 0xB3);
		put(RedstoneMnemonics.ORL, 0xB4);
		put(RedstoneMnemonics.XORL, 0xB5);
		put(RedstoneMnemonics.ADDL, 0xB6);
		put(RedstoneMnemonics.SUBL, 0xB7);
		put(RedstoneMnemonics.LSHL, 0xB8);
		put(RedstoneMnemonics.RSHL, 0xB9);
		
		put(RedstoneMnemonics.STBL, 0xBA);
		put(RedstoneMnemonics.LDBL, 0xBB);
		
		put(RedstoneMnemonics.MULI, 0xC0);
		put(RedstoneMnemonics.MULLI, 0xC1);
		put(RedstoneMnemonics.MUL, 0xC2);
		put(RedstoneMnemonics.MULPB, 0xC3);
		put(RedstoneMnemonics.MULNB, 0xC4);
		put(RedstoneMnemonics.MULL, 0xC5);
		
		put(RedstoneMnemonics.DIVI, 0xD0);
		put(RedstoneMnemonics.DIVLI, 0xD1);
		put(RedstoneMnemonics.DIV, 0xD2);
		put(RedstoneMnemonics.DIVPB, 0xD3);
		put(RedstoneMnemonics.DIVNB, 0xD4);
		put(RedstoneMnemonics.DIVL, 0xD5);
		
		put(RedstoneMnemonics.REMI, 0xE0);
		put(RedstoneMnemonics.REMLI, 0xE1);
		put(RedstoneMnemonics.REM, 0xE2);
		put(RedstoneMnemonics.REMPB, 0xE3);
		put(RedstoneMnemonics.REMNB, 0xE4);
		put(RedstoneMnemonics.REML, 0xE5);
	}
	
	private static void put(String mnemonic, int opcode) {
		MAP.put(mnemonic, Helpers.toBinary(opcode, 8));
	}
	
	public static String get(String mnemonic) {
		return MAP.get(mnemonic);
	}
}
