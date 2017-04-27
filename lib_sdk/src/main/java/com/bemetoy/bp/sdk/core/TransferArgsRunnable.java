package com.bemetoy.bp.sdk.core;

/**
 * 
 * @author AlbieLiang
 *
 */
public abstract class TransferArgsRunnable extends ArgsTransferHelper implements
		Runnable {
	public TransferArgsRunnable(Object... args) {
		super(args);
	}
}
