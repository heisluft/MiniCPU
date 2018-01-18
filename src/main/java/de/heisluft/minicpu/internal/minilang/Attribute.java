package de.heisluft.minicpu.internal.minilang;

abstract class Attribute {
    Attribute() {}

    abstract void load(AssemblerText text);

    abstract void operate(AssemblerText text, String string);
}

