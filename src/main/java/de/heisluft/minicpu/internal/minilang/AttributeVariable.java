package de.heisluft.minicpu.internal.minilang;

class AttributeVariable extends Attribute {
    private String name;

    AttributeVariable(String name) {
        this.name = name;
    }

    @Override
    void load(AssemblerText text) {
        text.setDirective(null, "LOAD", this.name);
    }

    @Override
    void operate(AssemblerText text, String string) {
        text.setDirective(null, string, this.name);
    }
}