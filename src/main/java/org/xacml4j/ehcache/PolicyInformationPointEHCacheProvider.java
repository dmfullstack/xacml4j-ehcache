package org.xacml4j.ehcache;

import java.util.List;
import java.util.Map;

import org.xacml4j.v30.BagOfAttributeExp;
import org.xacml4j.v30.spi.pip.AttributeResolverDescriptor;
import org.xacml4j.v30.spi.pip.AttributeSet;
import org.xacml4j.v30.spi.pip.BasePolicyInformationPointCacheProvider;
import org.xacml4j.v30.spi.pip.Content;
import org.xacml4j.v30.spi.pip.ResolverContext;
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
	protected AttributeSet doGetAttributes(ResolverContext context) {
		Object k = createKey(context.getDescriptor(), context.getKeys());
		Element e = attributesCache.get(k);
		if (e == null) {
			return null;
		}
		Object v = e.getObjectValue();
		if (v == null) {
			return null;
		}
		return AttributeSet
				.builder((AttributeResolverDescriptor)context.getDescriptor())
				.attributes((Map<String, BagOfAttributeExp>) v)
				.build();
	}

	@Override
	protected void doPutAttributes(ResolverContext context, AttributeSet v) {
		Object k = createKey(context.getDescriptor(), context.getKeys());
		Element e = new Element(k, v.toMap());
		e.setTimeToLive(context.getDescriptor().getPreferredCacheTTL());
		attributesCache.put(e);
	}

	@Override
	protected Content doGetContent(ResolverContext context) {
		Object k = createKey(context.getDescriptor(), context.getKeys());
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
	protected void doPutContent(ResolverContext context, Content content) {
		Object k = createKey(content.getDescriptor(), context.getKeys());
		Element e = new Element(k, content);
		e.setTimeToLive(content.getDescriptor().getPreferredCacheTTL());
		contentCache.put(e);
	}

	private Object createKey(ResolverDescriptor d, List<BagOfAttributeExp> keys) {
		return new ResolverCacheKey(d.getId(), keys);
	}
}
