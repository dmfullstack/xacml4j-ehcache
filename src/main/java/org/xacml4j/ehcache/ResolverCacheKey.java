package org.xacml4j.ehcache;

import java.io.Serializable;
import java.util.List;

import org.xacml4j.v30.BagOfAttributeExp;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

/**
 * A key used to identify uniquely resolver
 * resolution result
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
