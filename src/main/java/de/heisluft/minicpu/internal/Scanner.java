package de.heisluft.minicpu.internal;

class Scanner {
    private static final int EOF = 0;
    private static final int ILLEGAL = 1;
    private static final int IDENTIFIER = 2;
    private static final int DIGIT = 3;
    private static final int COLON = 4;
    private static final int NEW_LINE = 5;
    private static final int PLUS = 6;
    private static final int MINUS = 7;
    private static final int DOLLAR_SIGN = 8;
    private static final int klammerauf = 9;
    private static final int klammerzu = 10;
    private char[] source;
    private int position = 0;
    private char currentChar;
    private int currentInt;
    private String name;

    Scanner(String source) {
        this.source = source.toCharArray();
        loadNext();
    }

    private void loadNext() {
        currentChar = position < source.length ? source[position++] : '\u0000';
    }

    private void processIdentifier() {
        int count = 0;
        while ('a' <= currentChar && currentChar <= 'z' || 'A' <= currentChar && currentChar <= 'Z'
                || '0' <= currentChar && currentChar <= '9' || currentChar == '_' || currentChar == '$') {
            loadNext();
            count++;
        }
        name = new String(source, position - 1, count);
    }

    private void processDigit() {
        currentInt = 0;
        while ('0' <= currentChar && currentChar <= '9') {
            currentInt = currentInt * 10 + Character.digit(currentChar, 10);
            loadNext();
        }
    }

    private boolean processHexInt() {
        currentInt = 0;
        if ('0' <= currentChar && currentChar <= '9' || 'A' <= currentChar && currentChar <= 'F'
                || 'a' <= currentChar && currentChar <= 'f') {
            while ('0' <= currentChar && currentChar <= '9' || 'A' <= currentChar && currentChar <= 'F'
                    || 'a' <= currentChar && currentChar <= 'f') {
                currentInt = currentInt * 16 + Character.digit(currentChar, 16);
                loadNext();
            }
            return true;
        }
        return false;
    }

    int processNext() {
        while (currentChar == ' ' || currentChar == '\t') loadNext();
        if (currentChar == '#') {
            loadNext();
            while (currentChar != '\r' && currentChar != '\n' && currentChar != '\u0000') loadNext();
        }
        if (currentChar == '\u0000') {
            loadNext();
            return EOF;
        }
        if (currentChar == ':') {
            loadNext();
            return COLON;
        }
        if (currentChar == '(') {
            loadNext();
            return klammerauf;
        }
        if (currentChar == ')') {
            loadNext();
            return klammerzu;
        }
        if (currentChar == '\r') {
            loadNext();
            if (currentChar == '\n') loadNext();
            return NEW_LINE;
        }
        if (currentChar == '\n') {
            loadNext();
            return NEW_LINE;
        }
        if (currentChar == '+') {
            loadNext();
            return PLUS;
        }
        if (currentChar == '-') {
            loadNext();
            return MINUS;
        }
        if (currentChar == '$') {
            loadNext();
            return DOLLAR_SIGN;
        }
        if ('1' <= currentChar && currentChar <= '9') {
            processDigit();
            return DIGIT;
        }
        if ('0' == currentChar) {
            loadNext();
            if (Character.toLowerCase(currentChar) == 'x') {
                loadNext();
                if (!processHexInt()) return ILLEGAL;
            } else processDigit();
            return DIGIT;
        }
        if ('a' <= currentChar && currentChar <= 'z' || 'A' <= currentChar && currentChar <= 'Z' || currentChar == '_'
                || currentChar == '$') {
            processIdentifier();
            return IDENTIFIER;
        }
        loadNext();
        return ILLEGAL;
    }

    String getIdentifier() {
        return name;
    }

    int getCurrentInt() {
        return currentInt;
    }

    int getPosition() {
        return position;
    }
}

