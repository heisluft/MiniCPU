package de.heisluft.minicpu.internal.minilang;

class AttributeLoaded extends Attribute {
    AttributeLoaded() {}

    @Override
    void load(AssemblerText text) {}

    @Override
    void operate(AssemblerText text, String string) {
        text.setDirective(null, string + "ill", null);
    }
}

