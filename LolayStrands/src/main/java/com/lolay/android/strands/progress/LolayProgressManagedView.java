//
//  Copyright 2011, 2012, 2013 Lolay, Inc.
//
//  Licensed under the Apache License, Version 2.0 (the "License");
//  you may not use this file except in compliance with the License.
//  You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
//  Unless required by applicable law or agreed to in writing, software
//  distributed under the License is distributed on an "AS IS" BASIS,
//  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//  See the License for the specific language governing permissions and
//  limitations under the License.
//
package com.lolay.android.strands.progress;

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
