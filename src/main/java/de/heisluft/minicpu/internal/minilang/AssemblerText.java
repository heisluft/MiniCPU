package de.heisluft.minicpu.internal.minilang;

class AssemblerText {
    private String text = "";

    AssemblerText() {}

    void setDirective(String string, String string2, String string3) {
        String string4 = string != null ? string + ":" : "";
        string4 = string4 + "\t";
        if (string2 != null) {
            string4 = string4 + string2;
            if (string3 != null) {
                string4 = string4 + "\t" + string3;
            }
        }
        string4 = string4 + "\n";
        text = text + string4;
    }

    String getText() {
        return text;
    }
}