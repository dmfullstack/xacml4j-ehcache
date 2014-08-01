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

/**
 * Policy Information Point cache provider implementation that uses
 * EhCache as a back-end for attribute and content caches.
 *
 * @author Giedrius Trumpickas
 * @author Valdas Sevelis
 */
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
