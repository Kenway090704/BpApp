package com.bemetoy.bp.sdk.core;

/**
 * The abstract class to inherit when you want to transfer arguments into the
 * object and you can invoke {@link #getArg(int)} to get them
 * 
 * @author AlbieLiang
 *
 */
public abstract class ArgsTransferHelper {

	private Object[] args;

	/**
	 * 
	 * @param args
	 *            arguments to transfer into the object and invoke
	 *            {@link #getArg(int)} to get them
	 */
	public ArgsTransferHelper(Object... args) {
		this.args = args;
	}

	public final Object getArg(int index) {
		if (index >= 0 && args != null && args.length > 0
				&& index < args.length) {
			return args[index];
		}
		return null;
	}
}