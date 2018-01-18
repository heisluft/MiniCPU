package de.heisluft.minicpu.internal;

import java.util.HashMap;
import java.util.Map;

class Parser {
    private Scanner scanner;
    private Memory memory;
    private ErrorProvider provider;
    private OpCodes codes = OpCodes.INSTANCE;
    private int current;
    private int address = 0;
    private HashMap<String, Integer> labels = new HashMap<>(40);
    private HashMap<Integer, String> fixes = new HashMap<>(80);

    Parser(Scanner scanner, Memory memory, ErrorProvider provider) {
        this.scanner = scanner;
        this.memory = memory;
        this.provider = provider;
        current = scanner.processNext();
    }

    void parse() {
        while (current != 0) {
            while (current == 5) current = scanner.processNext();
            if (current == 2) {
                String string = scanner.getIdentifier();
                int n = scanner.getPosition();
                current = scanner.processNext();
                if (current == 4) {
                    if (labels.containsKey(string)) {
                        provider.error("Marke doppelt vereinbart", scanner.getPosition());
                    } else {
                        labels.put(string, address);
                    }
                    current = scanner.processNext();
                    if (current == 2) {
                        string = scanner.getIdentifier();
                        n = scanner.getPosition();
                        current = scanner.processNext();
                    } else {
                        if (current == 0 || current == 5) continue;
                        provider.error("Bezeichner erwartet", scanner.getPosition());
                    }
                }
                if (codes.isValid(string)) {
                    int n2;
                    int n3;
                    int n4 = codes.getOpCode(string);
                    int n5 = 0;
                    if (n4 < 0) {
                        n2 = 1;
                        if (current == 7) {
                            n2 = -1;
                            current = scanner.processNext();
                        } else if (current == 6) {
                            current = scanner.processNext();
                        }
                        if (current == 3) {
                            memory.setWord(address, n2 * scanner.getCurrentInt());
                            ++address;
                            current = scanner.processNext();
                        } else {
                            memory.setWord(address, 0);
                            ++address;
                            provider.error("Zahl erwartet", scanner.getPosition());
                        }
                    } else if (n4 >= 300) {
                        n3 = 0;
                        n5 = 2;
                        n4 -= 300;
                        n2 = 1;
                        if (current == 2) {
                            string = scanner.getIdentifier();
                            if (labels.containsKey(string)) {
                                n3 = labels.get(string);
                            } else {
                                fixes.put(address + 1, string);
                            }
                            current = scanner.processNext();
                        } else {
                            if (current == 7) {
                                n2 = -1;
                                current = scanner.processNext();
                            } else if (current == 6) {
                                current = scanner.processNext();
                            }
                            if (current == 3) {
                                n3 = n2 * scanner.getCurrentInt();
                                current = scanner.processNext();
                            } else {
                                provider.error("Zahl erwartet", scanner.getPosition());
                            }
                        }
                        memory.setWord(address, n5 * 256 + n4);
                        ++address;
                        memory.setWord(address, n3);
                        ++address;
                    } else {
                        n3 = 0;
                        n5 = 0;
                        if (current == 9) {
                            current = scanner.processNext();
                            n5 = 3;
                            if (current == 2) {
                                string = scanner.getIdentifier();
                                if (labels.containsKey(string)) {
                                    n3 = labels.get(string);
                                } else {
                                    fixes.put(address + 1, string);
                                }
                                current = scanner.processNext();
                            } else if (current == 3) {
                                n3 = scanner.getCurrentInt();
                                current = scanner.processNext();
                            } else {
                                provider.error("Zahl oder Bezeichner erwartet", scanner.getPosition());
                            }
                            if (current == 10) {
                                current = scanner.processNext();
                            } else {
                                provider.error("')' erwartet", scanner.getPosition());
                            }
                        } else if (current == 2) {
                            n5 = 1;
                            string = scanner.getIdentifier();
                            if (labels.containsKey(string)) {
                                n3 = labels.get(string);
                            } else {
                                fixes.put(address + 1, string);
                            }
                            current = scanner.processNext();
                        } else if (current == 3) {
                            n5 = 1;
                            n3 = scanner.getCurrentInt();
                            current = scanner.processNext();
                        } else if (current == 8) {
                            n5 = 2;
                            current = scanner.processNext();
                            n2 = 1;
                            if (current == 2) {
                                string = scanner.getIdentifier();
                                if (labels.containsKey(string)) {
                                    n3 = labels.get(string);
                                } else {
                                    fixes.put(address + 1, string);
                                }
                                current = scanner.processNext();
                            } else {
                                if (current == 7) {
                                    n2 = -1;
                                    current = scanner.processNext();
                                } else if (current == 6) {
                                    current = scanner.processNext();
                                }
                                if (current == 3) {
                                    n3 = n2 * scanner.getCurrentInt();
                                    current = scanner.processNext();
                                } else {
                                    provider.error("Zahl erwartet", scanner.getPosition());
                                }
                            }
                        }
                        memory.setWord(address, n5 * 256 + n4);
                        ++address;
                        memory.setWord(address, n3);
                        ++address;
                    }
                    switch (n4) {
                        case 1: 
                        case 99: {
                            if (n5 == 0) break;
                            provider.error("Unzul\u00e4ssige Adressteile", scanner.getPosition());
                            break;
                        }
                        case 10: 
                        case 11: 
                        case 12: 
                        case 13: 
                        case 15: 
                        case 20: {
                            if (n5 != 0) break;
                            provider.error("Fehlernder Adressteil", scanner.getPosition());
                            break;
                        }
                        case 21: 
                        case 30: 
                        case 31: 
                        case 32: 
                        case 33: 
                        case 34: 
                        case 35: 
                        case 36: {
                            if (n5 == 0) {
                                provider.error("Fehlernder Adressteil", scanner.getPosition());
                                break;
                            }
                            if (n5 != 2) break;
                            provider.error("Unzul\u00e4ssige Adressart", scanner.getPosition());
                        }
                    }
                    if (current == 0 || current == 5) continue;
                    provider.error("\u00dcberfl\u00fcssige Adressteile", scanner.getPosition());
                    continue;
                }
                provider.error("Kein g\u00fcltiger Befehl: " + string, n);
                skipToken();
                continue;
            }
            if (current == 0) continue;
            provider.error("Bezeichner erwartet", scanner.getPosition());
            skipToken();
        }
        if (address > 65536) provider.error("Programm zu lang", scanner.getPosition());
        for (Map.Entry<Integer, String> entry : fixes.entrySet()) {
            if (labels.containsKey(entry.getValue())) {
                memory.setWord(entry.getKey(), labels.get(entry.getValue()));
                continue;
            }
            provider.error("Marke nicht definiert: " + entry.getValue(), scanner.getPosition());
        }
        if (provider.errored()) {
            if (address > 65536) {
                address = 65534;
                memory.setWord(65534, -1);
                memory.setWord(65535, -1);
            }
            for (int i = 0; i < address; ++i) memory.setWord(i, 0);
        }
    }

    private void skipToken() {
        while (current != 0 && current != 5)
            current = scanner.processNext();
    }
}

