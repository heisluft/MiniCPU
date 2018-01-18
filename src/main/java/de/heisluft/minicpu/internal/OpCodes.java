package de.heisluft.minicpu.internal;

import java.util.HashMap;

class OpCodes {
    private static final HashMap<String, Integer> CMD_TABLE = new HashMap<>(80);
    private static final HashMap<Integer, String> REVERSE_TABLE = new HashMap<>(40);
    static final OpCodes INSTANCE = new OpCodes();

    private OpCodes() {}

    static {
        CMD_TABLE.put("NOOP", 0);
        CMD_TABLE.put("HOLD", 99);
        CMD_TABLE.put("RESET", 1);
        CMD_TABLE.put("NOT", 46);
        CMD_TABLE.put("ADD", 10);
        CMD_TABLE.put("SUB", 11);
        CMD_TABLE.put("MUL", 12);
        CMD_TABLE.put("DIV", 13);
        CMD_TABLE.put("MOD", 14);
        CMD_TABLE.put("CMP", 15);
        CMD_TABLE.put("AND", 40);
        CMD_TABLE.put("OR", 41);
        CMD_TABLE.put("XOR", 42);
        CMD_TABLE.put("SHL", 43);
        CMD_TABLE.put("SHR", 44);
        CMD_TABLE.put("SHRA", 45);
        CMD_TABLE.put("LOAD", 20);
        CMD_TABLE.put("STORE", 21);
        CMD_TABLE.put("JGT", 30);
        CMD_TABLE.put("JGE", 31);
        CMD_TABLE.put("JLT", 32);
        CMD_TABLE.put("JLE", 33);
        CMD_TABLE.put("JEQ", 34);
        CMD_TABLE.put("JNE", 35);
        CMD_TABLE.put("JOV", 37);
        CMD_TABLE.put("JMPP", 30);
        CMD_TABLE.put("JMPNN", 31);
        CMD_TABLE.put("JMPN", 32);
        CMD_TABLE.put("JMPNP", 33);
        CMD_TABLE.put("JMPZ", 34);
        CMD_TABLE.put("JMPNZ", 35);
        CMD_TABLE.put("JMP", 36);
        CMD_TABLE.put("JMPV", 37);
        CMD_TABLE.put("ADDI", 310);
        CMD_TABLE.put("SUBI", 311);
        CMD_TABLE.put("MULI", 312);
        CMD_TABLE.put("DIVI", 313);
        CMD_TABLE.put("MODI", 314);
        CMD_TABLE.put("CMPI", 315);
        CMD_TABLE.put("ANDI", 340);
        CMD_TABLE.put("ORI", 341);
        CMD_TABLE.put("XORI", 342);
        CMD_TABLE.put("SHLI", 343);
        CMD_TABLE.put("SHRI", 344);
        CMD_TABLE.put("SHRAI", 345);
        CMD_TABLE.put("LOADI", 320);
        CMD_TABLE.put("WORD", -256);
        CMD_TABLE.put("noop", 0);
        CMD_TABLE.put("hold", 99);
        CMD_TABLE.put("reset", 1);
        CMD_TABLE.put("not", 46);
        CMD_TABLE.put("add", 10);
        CMD_TABLE.put("sub", 11);
        CMD_TABLE.put("mul", 12);
        CMD_TABLE.put("div", 13);
        CMD_TABLE.put("mod", 14);
        CMD_TABLE.put("cmp", 15);
        CMD_TABLE.put("and", 40);
        CMD_TABLE.put("or", 41);
        CMD_TABLE.put("xor", 42);
        CMD_TABLE.put("shl", 43);
        CMD_TABLE.put("shr", 44);
        CMD_TABLE.put("shra", 45);
        CMD_TABLE.put("load", 20);
        CMD_TABLE.put("store", 21);
        CMD_TABLE.put("jgt", 30);
        CMD_TABLE.put("jge", 31);
        CMD_TABLE.put("jlt", 32);
        CMD_TABLE.put("jle", 33);
        CMD_TABLE.put("jeq", 34);
        CMD_TABLE.put("jne", 35);
        CMD_TABLE.put("jov", 37);
        CMD_TABLE.put("jmpp", 30);
        CMD_TABLE.put("jmpnn", 31);
        CMD_TABLE.put("jmpn", 32);
        CMD_TABLE.put("jmpnp", 33);
        CMD_TABLE.put("jmpz", 34);
        CMD_TABLE.put("jmpnz", 35);
        CMD_TABLE.put("jmp", 36);
        CMD_TABLE.put("jmpv", 37);
        CMD_TABLE.put("addi", 310);
        CMD_TABLE.put("subi", 311);
        CMD_TABLE.put("muli", 312);
        CMD_TABLE.put("divi", 313);
        CMD_TABLE.put("modi", 314);
        CMD_TABLE.put("cmpi", 315);
        CMD_TABLE.put("andi", 340);
        CMD_TABLE.put("ori", 341);
        CMD_TABLE.put("xori", 342);
        CMD_TABLE.put("shli", 343);
        CMD_TABLE.put("shri", 344);
        CMD_TABLE.put("shrai", 345);
        CMD_TABLE.put("loadi", 320);
        CMD_TABLE.put("word", -256);
        REVERSE_TABLE.put(0, "NOOP");
        REVERSE_TABLE.put(99, "HOLD");
        REVERSE_TABLE.put(1, "RESET");
        REVERSE_TABLE.put(46, "NOT");
        REVERSE_TABLE.put(10, "ADD");
        REVERSE_TABLE.put(11, "SUB");
        REVERSE_TABLE.put(12, "MUL");
        REVERSE_TABLE.put(13, "DIV");
        REVERSE_TABLE.put(14, "MOD");
        REVERSE_TABLE.put(15, "CMP");
        REVERSE_TABLE.put(40, "AND");
        REVERSE_TABLE.put(41, "OR");
        REVERSE_TABLE.put(42, "XOR");
        REVERSE_TABLE.put(43, "SHL");
        REVERSE_TABLE.put(44, "SHR");
        REVERSE_TABLE.put(45, "SHRA");
        REVERSE_TABLE.put(20, "LOAD");
        REVERSE_TABLE.put(21, "STORE");
        REVERSE_TABLE.put(30, "JMPP");
        REVERSE_TABLE.put(31, "JMPNN");
        REVERSE_TABLE.put(32, "JMPN");
        REVERSE_TABLE.put(33, "JMPNP");
        REVERSE_TABLE.put(34, "JMPZ");
        REVERSE_TABLE.put(35, "JMPNZ");
        REVERSE_TABLE.put(36, "JMP");
        REVERSE_TABLE.put(37, "JMPV");
    }

    boolean isValid(String cmdToTest) {
        return CMD_TABLE.containsKey(cmdToTest);
    }

    int getOpCode(String string) {
        return CMD_TABLE.get(string);
    }

    String getMnemonic(int opCode) {
        if (opCode == -1) {
            return "";
        }
        if (REVERSE_TABLE.containsKey(opCode)) {
            return REVERSE_TABLE.get(opCode);
        }
        return "---";
    }
}

