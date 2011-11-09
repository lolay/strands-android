/*
 * Created by Lolay, Inc.
 * Copyright 2011 Lolay, Inc. All rights reserved.
 */
package com.lolay.android.progress;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

public class LolayProgressManagedView {
	private int activeVisibility;
	private int inactiveVisibility;
	
	public int getActiveVisibility() {
		return activeVisibility;
	}
	public int getInactiveVisibility() {
		return inactiveVisibility;
	}
	
	public LolayProgressManagedView(int activeVisibility, int inactiveVisibility) {
		this.activeVisibility = activeVisibility;
		this.inactiveVisibility = inactiveVisibility;
	}
	
	@Override
	public int hashCode() {
		return HashCodeBuilder.reflectionHashCode(this);
	}

	@Override
	public boolean equals(Object that) {
		return EqualsBuilder.reflectionEquals(this, that);
	}

	@Override
	public String toString() {
	   return ToStringBuilder.reflectionToString(this);
	}
}
