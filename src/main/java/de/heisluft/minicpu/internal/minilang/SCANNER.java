package de.heisluft.minicpu.internal.minilang;

class SCANNER {
    static final int eof = 0;
    static final int illegal = 1;
    static final int bezeichner = 2;
    static final int zahl = 3;
    static final int program_token = 10;
    static final int begin_token = 11;
    static final int end_token = 12;
    static final int var_token = 13;
    static final int if_token = 14;
    static final int then_token = 15;
    static final int else_token = 16;
    static final int while_token = 17;
    static final int do_token = 18;
    static final int repeat_token = 19;
    static final int until_token = 20;
    static final int for_token = 21;
    static final int to_token = 22;
    static final int by_token = 23;
    static final int strichpunkt = 30;
    static final int punkt = 31;
    static final int komma = 32;
    static final int zuweisung = 33;
    static final int plus = 34;
    static final int minus = 35;
    static final int mal = 36;
    static final int geteilt = 37;
    static final int rest = 38;
    static final int gleich = 39;
    static final int ungleich = 40;
    static final int kleiner = 41;
    static final int kleinergleich = 42;
    static final int groesser = 43;
    static final int groessergleich = 44;
    static final int klammerauf = 45;
    static final int klammerzu = 46;
    private char[] quelle;
    private int pos;
    private char ch;
    private int zahlenwert;
    private String name;

    SCANNER(String string) {
        this.quelle = string.toCharArray();
        this.pos = 0;
        this.NaechstesZeichen();
    }

    private void NaechstesZeichen() {
        this.ch = this.pos < this.quelle.length ? this.quelle[this.pos++] : '\u0000';
    }

    private int Bezeichner() {
        int n = this.pos - 1;
        int n2 = 0;
        while ('a' <= this.ch && this.ch <= 'z' || 'A' <= this.ch && this.ch <= 'Z' || '0' <= this.ch && this.ch <= '9' || this.ch == '_' || this.ch == '$') {
            this.NaechstesZeichen();
            ++n2;
        }
        this.name = new String(this.quelle, n, n2);
        TextToken tEXTTOKEN = TextToken.getInstance();
        if (tEXTTOKEN.testCode(this.name)) {
            return tEXTTOKEN.getToken(this.name);
        }
        return 2;
    }

    private void Kommentar() {
        do {
            this.NaechstesZeichen();
            if (this.ch != '*') continue;
            this.NaechstesZeichen();
        } while (this.ch != ')');
        this.NaechstesZeichen();
    }

    private void Zahl() {
        this.zahlenwert = 0;
        while ('0' <= this.ch && this.ch <= '9') {
            this.zahlenwert = this.zahlenwert * 10 + Character.digit(this.ch, 10);
            this.NaechstesZeichen();
        }
    }

    int NaechstesToken() {
        while (this.ch == ' ' || this.ch == '\t' || this.ch == '\r' || this.ch == '\n') {
            this.NaechstesZeichen();
        }
        if (this.ch == '\u0000') {
            this.NaechstesZeichen();
            return 0;
        }
        if (this.ch == ';') {
            this.NaechstesZeichen();
            return 30;
        }
        if (this.ch == '.') {
            this.NaechstesZeichen();
            return 31;
        }
        if (this.ch == ',') {
            this.NaechstesZeichen();
            return 32;
        }
        if (this.ch == ':') {
            this.NaechstesZeichen();
            if (this.ch == '=') {
                this.NaechstesZeichen();
                return 33;
            }
            return 1;
        }
        if (this.ch == '+') {
            this.NaechstesZeichen();
            return 34;
        }
        if (this.ch == '-') {
            this.NaechstesZeichen();
            return 35;
        }
        if (this.ch == '*') {
            this.NaechstesZeichen();
            return 36;
        }
        if (this.ch == '/') {
            this.NaechstesZeichen();
            return 37;
        }
        if (this.ch == '%') {
            this.NaechstesZeichen();
            return 38;
        }
        if (this.ch == '=') {
            this.NaechstesZeichen();
            return 39;
        }
        if (this.ch == '<') {
            this.NaechstesZeichen();
            if (this.ch == '=') {
                this.NaechstesZeichen();
                return 42;
            }
            if (this.ch == '>') {
                this.NaechstesZeichen();
                return 40;
            }
            return 41;
        }
        if (this.ch == '>') {
            this.NaechstesZeichen();
            if (this.ch == '=') {
                this.NaechstesZeichen();
                return 44;
            }
            return 43;
        }
        if (this.ch == '(') {
            this.NaechstesZeichen();
            if (this.ch == '*') {
                this.Kommentar();
                return this.NaechstesToken();
            }
            return 45;
        }
        if (this.ch == ')') {
            this.NaechstesZeichen();
            return 46;
        }
        if ('0' <= this.ch && this.ch <= '9') {
            this.Zahl();
            return 3;
        }
        if ('a' <= this.ch && this.ch <= 'z' || 'A' <= this.ch && this.ch <= 'Z' || this.ch == '_' || this.ch == '$') {
            return this.Bezeichner();
        }
        return 1;
    }

    String BezeichnerGeben() {
        return this.name;
    }

    int ZahlGeben() {
        return this.zahlenwert;
    }

    int PositionGeben() {
        return this.pos;
    }
}

