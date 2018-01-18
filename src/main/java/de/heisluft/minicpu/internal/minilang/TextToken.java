package de.heisluft.minicpu.internal.minilang;

import java.util.HashMap;

class TextToken {
    private HashMap<String, Integer> tokentabelle = new HashMap<>(20);
    private static TextToken instance = new TextToken();

    private TextToken() {
        tokentabelle.put("PROGRAM", 10);
        tokentabelle.put("BEGIN", 11);
        tokentabelle.put("END", 12);
        tokentabelle.put("VAR", 13);
        tokentabelle.put("IF", 14);
        tokentabelle.put("THEN", 15);
        tokentabelle.put("ELSE", 16);
        tokentabelle.put("WHILE", 17);
        tokentabelle.put("DO", 18);
        tokentabelle.put("REPEAT", 19);
        tokentabelle.put("UNTIL", 20);
        tokentabelle.put("FOR", 21);
        tokentabelle.put("TO", 22);
        tokentabelle.put("BY", 23);
    }

    static TextToken getInstance() {
        return instance;
    }

    boolean testCode(String string) {
        return tokentabelle.containsKey(string);
    }

    int getToken(String string) {
        return tokentabelle.get(string);
    }
}

