/*
 * Decompiled with CFR 0_123.
 */
package de.heisluft.minicpu.internal.minilang;

import de.heisluft.minicpu.internal.ErrorProvider;

import java.util.HashSet;

public class PARSER {
    private SCANNER scanner;
    private ErrorProvider fehler;
    private int aktToken;
    private String programmname;
    private HashSet<String> variable;
    private AssemblerText ausgabe;
    private int akthilfsplatz;
    private int maxhilfsplatz;
    private int markenNummer;

    public PARSER(String string, ErrorProvider fEHLERVERWALTUNG) {
        this.scanner = new SCANNER(string);
        this.fehler = fEHLERVERWALTUNG;
        this.aktToken = this.scanner.NaechstesToken();
        this.programmname = "";
        this.variable = new HashSet();
        this.ausgabe = new AssemblerText();
        this.akthilfsplatz = 0;
        this.maxhilfsplatz = 0;
        this.markenNummer = 0;
    }

    public String Parse() {
        this.Program();
        this.VariableAusgeben();
        return this.ausgabe.getText();
    }

    private void Program() {
        if (this.aktToken == 10) {
            this.aktToken = this.scanner.NaechstesToken();
            if (this.aktToken == 2) {
                this.programmname = this.scanner.BezeichnerGeben();
                this.aktToken = this.scanner.NaechstesToken();
            } else {
                this.fehler.error("Bezeichner erwartet", this.scanner.PositionGeben());
            }
            if (this.aktToken == 30) {
                this.aktToken = this.scanner.NaechstesToken();
            } else {
                this.fehler.error("';' erwartet", this.scanner.PositionGeben());
            }
            if (this.aktToken == 13) {
                this.Variablenvereinbarung();
            }
            if (this.aktToken == 11) {
                this.aktToken = this.scanner.NaechstesToken();
                this.Block();
                if (this.aktToken == 12) {
                    this.aktToken = this.scanner.NaechstesToken();
                    if (this.aktToken == 2) {
                        if (!this.programmname.equals(this.scanner.BezeichnerGeben())) {
                            this.fehler.error("Programmname erwartet", this.scanner.PositionGeben());
                        }
                        this.aktToken = this.scanner.NaechstesToken();
                        if (this.aktToken == 31) {
                            this.aktToken = this.scanner.NaechstesToken();
                            if (this.aktToken != 0) {
                                this.fehler.error("Unzul\u00e4ssige Zeichen am Programmende", this.scanner.PositionGeben());
                            }
                        } else {
                            this.fehler.error("'.' erwartet", this.scanner.PositionGeben());
                        }
                    } else {
                        this.fehler.error("Bezeichner erwartet", this.scanner.PositionGeben());
                    }
                } else {
                    this.fehler.error("'END' erwartet", this.scanner.PositionGeben());
                }
            } else {
                this.fehler.error("'BEGIN' erwartet", this.scanner.PositionGeben());
            }
        } else {
            this.fehler.error("'PROGRAM' erwartet", this.scanner.PositionGeben());
        }
        this.ausgabe.setDirective(null, "HOLD", null);
    }

    private void Variablenvereinbarung() {
        this.aktToken = this.scanner.NaechstesToken();
        if (this.aktToken != 2) {
            this.fehler.error("Bezeichner erwartet", this.scanner.PositionGeben());
            this.SkipBisStrichpunkt();
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            do {
                String string;
                if (this.variable.contains(string = this.scanner.BezeichnerGeben())) {
                    this.fehler.error("Bezeichner schon vereinbart", this.scanner.PositionGeben());
                } else {
                    this.variable.add(string);
                }
                this.aktToken = this.scanner.NaechstesToken();
                if (this.aktToken != 32) continue;
                this.aktToken = this.scanner.NaechstesToken();
            } while (this.aktToken == 2);
            if (this.aktToken == 30) {
                this.aktToken = this.scanner.NaechstesToken();
            } else {
                this.fehler.error("';' erwartet", this.scanner.PositionGeben());
                this.SkipBisStrichpunkt();
                this.aktToken = this.scanner.NaechstesToken();
            }
        }
    }

    private void Block() {
        while (!this.BlockendeTesten() && this.aktToken != 0) {
            if (this.aktToken == 2) {
                this.Zuweisung();
            } else if (this.aktToken == 14) {
                this.BedingteAnweisung();
            } else if (this.aktToken == 17) {
                this.WiederholungEingang();
            } else if (this.aktToken == 21) {
                this.WiederholungZaehl();
            } else if (this.aktToken == 19) {
                this.WiederholungEnde();
            }
            if (this.aktToken == 30) {
                this.aktToken = this.scanner.NaechstesToken();
                continue;
            }
            if (this.BlockendeTesten()) continue;
            this.fehler.error("';' erwartet", this.scanner.PositionGeben());
            if (this.aktToken == 2 || this.aktToken == 14 || this.aktToken == 17 || this.aktToken == 21 || this.aktToken == 19) continue;
            this.aktToken = this.scanner.NaechstesToken();
        }
    }

    private void Zuweisung() {
        String string = this.scanner.BezeichnerGeben();
        this.aktToken = this.scanner.NaechstesToken();
        if (this.aktToken == 33) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("':=' erwartet", this.scanner.PositionGeben());
            if (this.aktToken == 39 || this.aktToken != 1) {
                this.aktToken = this.scanner.NaechstesToken();
            }
        }
        Attribute aTTRIBUT = this.AusdruckStrich();
        aTTRIBUT.load(this.ausgabe);
        this.ausgabe.setDirective(null, "STORE", string);
    }

    private Attribute AusdruckStrich() {
        int n;
        boolean bl = false;
        if (this.aktToken == 34) {
            this.aktToken = this.scanner.NaechstesToken();
        } else if (this.aktToken == 35) {
            this.aktToken = this.scanner.NaechstesToken();
            bl = true;
        }
        Attribute aTTRIBUT = this.AusdruckPunkt();
        if (bl) {
            n = 0;
            if (aTTRIBUT instanceof AttributeLoaded) {
                ++this.akthilfsplatz;
                if (this.akthilfsplatz > this.maxhilfsplatz) {
                    this.maxhilfsplatz = this.akthilfsplatz;
                }
                this.ausgabe.setDirective(null, "STORE", "hi$" + this.akthilfsplatz);
                aTTRIBUT = new AttributeVariable("hi$" + this.akthilfsplatz);
                n = 1;
            }
            this.ausgabe.setDirective(null, "LOADI", "0");
            aTTRIBUT.operate(this.ausgabe, "SUB");
            aTTRIBUT = new AttributeLoaded();
            this.akthilfsplatz -= n;
        }
        while (this.aktToken == 34 || this.aktToken == 35) {
            String string = "";
            if (this.aktToken == 34) {
                string = "ADD";
            } else if (this.aktToken == 35) {
                string = "SUB";
            }
            n = 0;
            if (aTTRIBUT instanceof AttributeLoaded) {
                ++this.akthilfsplatz;
                if (this.akthilfsplatz > this.maxhilfsplatz) {
                    this.maxhilfsplatz = this.akthilfsplatz;
                }
                this.ausgabe.setDirective(null, "STORE", "hi$" + this.akthilfsplatz);
                aTTRIBUT = new AttributeVariable("hi$" + this.akthilfsplatz);
                n = 1;
            }
            this.aktToken = this.scanner.NaechstesToken();
            Attribute aTTRIBUT2 = this.AusdruckPunkt();
            if (aTTRIBUT2 instanceof AttributeLoaded) {
                ++this.akthilfsplatz;
                if (this.akthilfsplatz > this.maxhilfsplatz) {
                    this.maxhilfsplatz = this.akthilfsplatz;
                }
                this.ausgabe.setDirective(null, "STORE", "hi$" + this.akthilfsplatz);
                aTTRIBUT2 = new AttributeVariable("hi$" + this.akthilfsplatz);
                ++n;
            }
            aTTRIBUT.load(this.ausgabe);
            aTTRIBUT2.operate(this.ausgabe, string);
            aTTRIBUT = new AttributeLoaded();
            this.akthilfsplatz -= n;
        }
        return aTTRIBUT;
    }

    private Attribute AusdruckPunkt() {
        Attribute aTTRIBUT = this.Faktor();
        while (this.aktToken == 36 || this.aktToken == 37 || this.aktToken == 38) {
            String string = "";
            if (this.aktToken == 36) {
                string = "MUL";
            } else if (this.aktToken == 37) {
                string = "DIV";
            } else if (this.aktToken == 38) {
                string = "MOD";
            }
            int n = 0;
            if (aTTRIBUT instanceof AttributeLoaded) {
                ++this.akthilfsplatz;
                if (this.akthilfsplatz > this.maxhilfsplatz) {
                    this.maxhilfsplatz = this.akthilfsplatz;
                }
                this.ausgabe.setDirective(null, "STORE", "hi$" + this.akthilfsplatz);
                aTTRIBUT = new AttributeVariable("hi$" + this.akthilfsplatz);
                n = 1;
            }
            this.aktToken = this.scanner.NaechstesToken();
            Attribute aTTRIBUT2 = this.Faktor();
            if (aTTRIBUT2 instanceof AttributeLoaded) {
                ++this.akthilfsplatz;
                if (this.akthilfsplatz > this.maxhilfsplatz) {
                    this.maxhilfsplatz = this.akthilfsplatz;
                }
                this.ausgabe.setDirective(null, "STORE", "hi$" + this.akthilfsplatz);
                aTTRIBUT2 = new AttributeVariable("hi$" + this.akthilfsplatz);
                ++n;
            }
            aTTRIBUT.load(this.ausgabe);
            aTTRIBUT2.operate(this.ausgabe, string);
            aTTRIBUT = new AttributeLoaded();
            this.akthilfsplatz -= n;
        }
        return aTTRIBUT;
    }

    private Attribute Faktor() {
        Attribute aTTRIBUT2;
        if (this.aktToken == 2) {
            aTTRIBUT2 = new AttributeVariable(this.scanner.BezeichnerGeben());
            this.aktToken = this.scanner.NaechstesToken();
        } else if (this.aktToken == 3) {
            aTTRIBUT2 = new AttributeConstant(this.scanner.ZahlGeben());
            this.aktToken = this.scanner.NaechstesToken();
        } else if (this.aktToken == 45) {
            this.aktToken = this.scanner.NaechstesToken();
            aTTRIBUT2 = this.AusdruckStrich();
            if (this.aktToken == 46) {
                this.aktToken = this.scanner.NaechstesToken();
            } else {
                this.fehler.error("')' erwartet", this.scanner.PositionGeben());
                if (!this.BlockendeTesten() && this.aktToken != 30) {
                    this.aktToken = this.scanner.NaechstesToken();
                }
            }
        } else {
            this.fehler.error("Bezeichner, Zahl oder '(' erwartet", this.scanner.PositionGeben());
            aTTRIBUT2 = new AttributeConstant(0);
            if (!this.BlockendeTesten()) {
                this.aktToken = this.scanner.NaechstesToken();
            }
        }
        return aTTRIBUT2;
    }

    private void Bedingung(int n) {
        Attribute aTTRIBUT = this.AusdruckStrich();
        String string = "";
        if (this.aktToken == 39) {
            string = "JMPNZ";
        } else if (this.aktToken == 40) {
            string = "JMPZ";
        } else if (this.aktToken == 41) {
            string = "JMPNN";
        } else if (this.aktToken == 42) {
            string = "JMPP";
        } else if (this.aktToken == 43) {
            string = "JMPNP";
        } else if (this.aktToken == 44) {
            string = "JMPN";
        } else {
            this.fehler.error("'=', '<>', '>', '>=', '<' oder '<=' erwartet", this.scanner.PositionGeben());
        }
        int n2 = 0;
        if (aTTRIBUT instanceof AttributeLoaded) {
            ++this.akthilfsplatz;
            if (this.akthilfsplatz > this.maxhilfsplatz) {
                this.maxhilfsplatz = this.akthilfsplatz;
            }
            this.ausgabe.setDirective(null, "STORE", "hi$" + this.akthilfsplatz);
            aTTRIBUT = new AttributeVariable("hi$" + this.akthilfsplatz);
            n2 = 1;
        }
        this.aktToken = this.scanner.NaechstesToken();
        Attribute aTTRIBUT2 = this.AusdruckStrich();
        if (aTTRIBUT2 instanceof AttributeLoaded) {
            ++this.akthilfsplatz;
            if (this.akthilfsplatz > this.maxhilfsplatz) {
                this.maxhilfsplatz = this.akthilfsplatz;
            }
            this.ausgabe.setDirective(null, "STORE", "hi$" + this.akthilfsplatz);
            aTTRIBUT2 = new AttributeVariable("hi$" + this.akthilfsplatz);
            ++n2;
        }
        aTTRIBUT.load(this.ausgabe);
        aTTRIBUT2.operate(this.ausgabe, "CMP");
        this.akthilfsplatz -= n2;
        this.ausgabe.setDirective(null, string, "M$" + n);
    }

    private void BedingteAnweisung() {
        ++this.markenNummer;
        int n = this.markenNummer++;
        int n2 = this.markenNummer;
        this.aktToken = this.scanner.NaechstesToken();
        this.Bedingung(n);
        if (this.aktToken == 15) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'THEN' erwartet", this.scanner.PositionGeben());
        }
        this.Block();
        if (this.aktToken == 16) {
            this.aktToken = this.scanner.NaechstesToken();
            this.ausgabe.setDirective(null, "JMP", "M$" + n2);
            this.ausgabe.setDirective("M$" + n, null, null);
            this.Block();
            this.ausgabe.setDirective("M$" + n2, null, null);
        } else {
            this.ausgabe.setDirective("M$" + n, null, null);
        }
        if (this.aktToken == 12) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'END' erwartet", this.scanner.PositionGeben());
        }
    }

    private void WiederholungEingang() {
        ++this.markenNummer;
        int n = this.markenNummer++;
        int n2 = this.markenNummer;
        this.ausgabe.setDirective("M$" + n, null, null);
        this.aktToken = this.scanner.NaechstesToken();
        this.Bedingung(n2);
        if (this.aktToken == 18) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'DO' erwartet", this.scanner.PositionGeben());
        }
        this.Block();
        this.ausgabe.setDirective(null, "JMP", "M$" + n);
        this.ausgabe.setDirective("M$" + n2, null, null);
        if (this.aktToken == 12) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'END' erwartet", this.scanner.PositionGeben());
        }
    }

    private void WiederholungEnde() {
        int n = ++this.markenNummer;
        this.ausgabe.setDirective("M$" + n, null, null);
        this.aktToken = this.scanner.NaechstesToken();
        this.Block();
        if (this.aktToken == 20) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'UNTIL' erwartet", this.scanner.PositionGeben());
        }
        this.Bedingung(n);
    }

    private void WiederholungZaehl() {
        int n;
        String string;
        ++this.markenNummer;
        int n2 = this.markenNummer++;
        int n3 = this.markenNummer;
        ++this.akthilfsplatz;
        if (this.akthilfsplatz > this.maxhilfsplatz) {
            this.maxhilfsplatz = this.akthilfsplatz;
        }
        String string2 = "hi$" + this.akthilfsplatz;
        this.aktToken = this.scanner.NaechstesToken();
        if (this.aktToken == 2) {
            string = this.scanner.BezeichnerGeben();
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("Bezeichner erwartet", this.scanner.PositionGeben());
            string = "dummy";
        }
        if (this.aktToken == 33) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("':=' erwartet", this.scanner.PositionGeben());
            if (this.aktToken == 39 || this.aktToken != 1) {
                this.aktToken = this.scanner.NaechstesToken();
            }
        }
        Attribute aTTRIBUT = this.AusdruckStrich();
        aTTRIBUT.load(this.ausgabe);
        this.ausgabe.setDirective(null, "STORE", string);
        if (this.aktToken == 22) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'TO' erwartet", this.scanner.PositionGeben());
        }
        aTTRIBUT = this.AusdruckStrich();
        aTTRIBUT.load(this.ausgabe);
        this.ausgabe.setDirective(null, "STORE", string2);
        if (this.aktToken == 23) {
            this.aktToken = this.scanner.NaechstesToken();
            boolean bl = false;
            if (this.aktToken == 34) {
                this.aktToken = this.scanner.NaechstesToken();
            } else if (this.aktToken == 35) {
                this.aktToken = this.scanner.NaechstesToken();
                bl = true;
            }
            if (this.aktToken == 3) {
                n = this.scanner.ZahlGeben();
                this.aktToken = this.scanner.NaechstesToken();
            } else {
                this.fehler.error("Zahl erwartet", this.scanner.PositionGeben());
                n = 1;
            }
            if (bl) {
                n = - n;
            }
            if (n == 0) {
                this.fehler.error("Die Schrittweite darf nicht 0 sein", this.scanner.PositionGeben());
                n = 1;
            }
        } else {
            n = 1;
        }
        if (this.aktToken == 18) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'DO' erwartet", this.scanner.PositionGeben());
        }
        this.ausgabe.setDirective("M$" + n2, null, null);
        this.ausgabe.setDirective(null, "LOAD", string);
        this.ausgabe.setDirective(null, "CMP", string2);
        if (n > 0) {
            this.ausgabe.setDirective(null, "JMPP", "M$" + n3);
        } else {
            this.ausgabe.setDirective(null, "JMPN", "M$" + n3);
        }
        this.Block();
        this.ausgabe.setDirective(null, "LOAD", string);
        this.ausgabe.setDirective(null, "ADDI", "" + n);
        this.ausgabe.setDirective(null, "JMPV", "M$" + n3);
        this.ausgabe.setDirective(null, "STORE", string);
        this.ausgabe.setDirective(null, "JMP", "M$" + n2);
        this.ausgabe.setDirective("M$" + n3, null, null);
        if (this.aktToken == 12) {
            this.aktToken = this.scanner.NaechstesToken();
        } else {
            this.fehler.error("'END' erwartet", this.scanner.PositionGeben());
        }
        --this.akthilfsplatz;
    }

    private boolean BlockendeTesten() {
        return this.aktToken == 12 || this.aktToken == 16 || this.aktToken == 20;
    }

    private void SkipBisStrichpunkt() {
        while (this.aktToken != 30 && this.aktToken != 30) {
            this.aktToken = this.scanner.NaechstesToken();
        }
    }

    private void VariableAusgeben() {
        for (String string : this.variable) {
            this.ausgabe.setDirective(string, "WORD", "0");
        }
        for (int i = 1; i <= this.maxhilfsplatz; ++i) {
            this.ausgabe.setDirective("hi$" + i, "WORD", "0");
        }
    }
}

