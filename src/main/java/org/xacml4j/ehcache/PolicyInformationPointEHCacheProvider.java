package org.xacml4j.ehcache;

import java.util.List;
import java.util.Map;

import org.xacml4j.v30.BagOfAttributeExp;
import org.xacml4j.v30.spi.pip.AttributeResolverDescriptor;
import org.xacml4j.v30.spi.pip.AttributeSet;
import org.xacml4j.v30.spi.pip.BasePolicyInformationPointCacheProvider;
import org.xacml4j.v30.spi.pip.Content;
import org.xacml4j.v30.spi.pip.ContentResolverDescriptor;
import org.xacml4j.v30.spi.pip.ResolverDescriptor;

import com.google.common.base.Preconditions;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;

public class PolicyInformationPointEHCacheProvider extends BasePolicyInformationPointCacheProvider {
	private Cache attributesCache;
	private Cache contentCache;

	public PolicyInformationPointEHCacheProvider(
			Cache attributesCache,
			Cache contentCache) {
		Preconditions.checkNotNull(attributesCache);
		Preconditions.checkNotNull(contentCache);
		this.attributesCache = attributesCache;
		this.contentCache = contentCache;
	}

	@Override
	@SuppressWarnings("unchecked")
	protected AttributeSet doGetAttributes(AttributeResolverDescriptor d,
	                                       List<BagOfAttributeExp> keys) {
		Object k = createKey(d, keys);
		Element e = attributesCache.get(k);
		if (e == null) {
			return null;
		}
		Object v = e.getObjectValue();
		if (v == null) {
			return null;
		}
		return AttributeSet
				.builder(d)
				.attributes((Map<String, BagOfAttributeExp>) v)
				.build();
	}

	@Override
	protected void doPutAttributes(AttributeResolverDescriptor d, List<BagOfAttributeExp> keys,
	                               AttributeSet v) {
		Object k = createKey(d, keys);
		Element e = new Element(k, v.toMap());
		e.setTimeToLive(d.getPreferredCacheTTL());
		attributesCache.put(e);
	}

	@Override
	protected Content doGetContent(ContentResolverDescriptor d,
	                               List<BagOfAttributeExp> keys) {
		Object k = createKey(d, keys);
		Element e = contentCache.get(k);
		if (e == null) {
			return null;
		}
		Object v = e.getObjectValue();
		if (v == null) {
			return null;
		}
		return (Content) v;
	}

	@Override
	protected void doPutContent(ContentResolverDescriptor d,
	                            List<BagOfAttributeExp> keys, Content content) {
		Object k = createKey(d, keys);
		Element e = new Element(k, content);
		e.setTimeToLive(d.getPreferredCacheTTL());
		contentCache.put(e);
	}

	private Object createKey(ResolverDescriptor d, List<BagOfAttributeExp> keys) {
		return new ResolverCacheKey(d.getId(), keys);
	}
}
