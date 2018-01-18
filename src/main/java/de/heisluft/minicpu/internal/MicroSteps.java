package de.heisluft.minicpu.internal;

enum MicroSteps {
    COMPLETE, FETCH_OPCODE, FETCH_ADDRESS, FETCH_INDIRECT, DECODE, EXECUTE_1, EXECUTE_2
}

