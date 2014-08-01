package org.xacml4j.ehcache;

/*
 * #%L
 * Xacml4J Policy Information Point Caching with EhCache
 * %%
 * Copyright (C) 2009 - 2014 Xacml4J.org
 * %%
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Lesser Public License for more details.
 *
 * You should have received a copy of the GNU General Lesser Public
 * License along with this program.  If not, see
 * <http://www.gnu.org/licenses/lgpl-3.0.html>.
 * #L%
 */

import java.io.Serializable;
import java.util.List;

import org.xacml4j.v30.BagOfAttributeExp;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A key used to identify uniquely resolver resolution result
 *
 * @author Giedrius Trumpickas
 */
final class ResolverCacheKey implements Serializable {
	private static final long serialVersionUID = -6895205924708410228L;

	private final String resolverId;
	private final List<BagOfAttributeExp> keys;

	public ResolverCacheKey(String resolverId,
	                        List<BagOfAttributeExp> keys) {
		Preconditions.checkNotNull(resolverId, "ResolverID can not be null");
		this.resolverId = resolverId;
		this.keys = keys;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(resolverId, keys);
	}

	@Override
	public boolean equals(Object o) {
		if (o == this) {
			return true;
		}
		if (!(o instanceof ResolverCacheKey)) {
			return false;
		}
		ResolverCacheKey k = (ResolverCacheKey) o;
		return Objects.equal(resolverId, k.resolverId) && Objects.equal(keys, k.keys);
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this)
		              .add("id", resolverId)
		              .add("keys", keys.toString())
		              .toString();
	}
}
