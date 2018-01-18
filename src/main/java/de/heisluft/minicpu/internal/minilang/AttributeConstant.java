package de.heisluft.minicpu.internal.minilang;

class AttributeConstant extends Attribute {
    private final int value;

    AttributeConstant(int value) {
        this.value = value;
    }

    @Override
    void load(AssemblerText text) {
        text.setDirective(null, "LOADI", "" + this.value);
    }

    @Override
    void operate(AssemblerText text, String string) {
        text.setDirective(null, string + "I", "" + this.value);
    }
}

