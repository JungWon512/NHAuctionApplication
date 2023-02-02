package com.nh.controller.model;

import javax.annotation.Nonnull;

public class SettingSounds {
	private String msg;

	public SettingSounds(@Nonnull String msg) {
		this.msg = msg;
	}

	public void setMessage(@Nonnull String msg) {
		this.msg = msg;
	}

	public String getMessage() {
		return this.msg;
	}
}
