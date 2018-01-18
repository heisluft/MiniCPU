package de.heisluft.minicpu.internal;

class Register {
    private int value = 0;

    int getValue() {
        return value;
    }

    void setValue(int newValue) {
        while (newValue >= 32768)
            newValue -= 65536;
        while (newValue < -32768)
            newValue += 65536;
        value = newValue;
    }

    void increment() {
        value += 1;
        if (value > 32767)
            value -= 65536;
    }
}

