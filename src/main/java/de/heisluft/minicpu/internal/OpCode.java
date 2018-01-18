package de.heisluft.minicpu.internal;

public interface OpCode {
	int NOOP = 0;
	int RESET = 1;
	int ADD = 10;
	int SUB = 11;
	int MUL = 12;
	int DIV = 13;
	int MOD = 14;
	int CMP = 15;
	int LOAD = 20;
	int STORE = 21;
	int JGT = 30;
	int JGE = 31;
	int JLT = 32;
	int JLE = 33;
	int JEG = 34;
	int JNE = 35;
	int JMP = 36;
	int JOV = 37;
	int AND = 40;
	int OR = 41;
	int XOR = 42;
	int SHL = 43;
	int SHR = 44;
	int SHRA = 45;
	int NOT = 46;
	int HALT = 99;
	int ILL = -1;
	int NOADR = 0;
	int ABSADR = 1;
	int IMMEDADR = 2;
	int INDADR = 3;
}