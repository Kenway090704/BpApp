package com.bemetoy.stub.model;

import com.bemetoy.stub.app.AppCore;

public class AccountNotReadyException extends RuntimeException {

	public static final int DEFAULT_EXCEPTION = 0;
	public static final int DBVER_EXCEPTION = 1;

	private final int exceptionType;

	public AccountNotReadyException() {
		super(AppCore.getResetUIDStack());
		exceptionType = DEFAULT_EXCEPTION;
	}

	public AccountNotReadyException(final int exceptionType) {
		super(AppCore.getResetUIDStack());
		this.exceptionType = exceptionType;
	}

	public boolean isDBVerException() {
		if (exceptionType == DBVER_EXCEPTION) {
			return true;
		}
		return false;
	}

}
