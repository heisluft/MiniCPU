package de.heisluft.minicpu.internal;

public interface CpuWatcher {
	void reportCmd(String var1, String var2, String var3, String var4, String var5, String var6, boolean zero, boolean negative, boolean overflow, String var10, String var11, String var12, String[] var13, String[] var14, String[] var15, String[] var16);

	void reportError(String message);
}

