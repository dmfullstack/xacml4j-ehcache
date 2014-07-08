package org.xacml4j.ehcache;

import static org.easymock.EasyMock.expect;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

import java.util.Arrays;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Node;
import org.xacml4j.v30.AttributeReferenceKey;
import org.xacml4j.v30.AttributeSelectorKey;
import org.xacml4j.v30.Categories;
import org.xacml4j.v30.spi.pip.AttributeResolverDescriptor;
import org.xacml4j.v30.spi.pip.AttributeResolverDescriptorBuilder;
import org.xacml4j.v30.spi.pip.AttributeSet;
import org.xacml4j.v30.spi.pip.Content;
import org.xacml4j.v30.spi.pip.ContentResolverDescriptor;
import org.xacml4j.v30.spi.pip.ContentResolverDescriptorBuilder;
import org.xacml4j.v30.spi.pip.ResolverContext;
import org.xacml4j.v30.types.StringExp;
import org.xacml4j.v30.types.XacmlTypes;

import com.google.common.collect.ImmutableList;

import net.sf.ehcache.Cache;
import net.sf.ehcache.CacheManager;

public class PolicyInformationPointEHCacheProviderTest {

	private IMocksControl mockCtl;
	private PolicyInformationPointEHCacheProvider pipCache;
	private CacheManager cacheManager;

	@Before
	public void setup() {
		mockCtl = EasyMock.createStrictControl();
		cacheManager = CacheManager.create();
		cacheManager.addCache("attributes-cache");
		cacheManager.addCache("content-cache");

		Cache attributesCache = cacheManager.getCache("attributes-cache");
		Cache contentCache = cacheManager.getCache("content-cache");

		pipCache = new PolicyInformationPointEHCacheProvider(attributesCache, contentCache);
	}

	@After
	public void shutdown() {
		cacheManager.shutdown();
	}

	@Test
	public void storeAttributesInCacheAndGetThem() {
		AttributeResolverDescriptor attributeDescriptor = AttributeResolverDescriptorBuilder
				.builder("descriptor-id", "name", null, Categories.SUBJECT_ACCESS)
				.attribute("a1", XacmlTypes.STRING)
				.cache(100)
				.build();
		ResolverContext ctx = mockCtl.createMock(ResolverContext.class);
		expect(ctx.getDescriptor()).andStubReturn(attributeDescriptor);
		expect(ctx.getKeys()).andStubReturn(ImmutableList.of(
				StringExp.bag().value("k1", "k1").build(),
				StringExp.bag().value("k3", "k4").build()));

		AttributeSet storedAttributes = AttributeSet
				.builder(attributeDescriptor)
				.attribute("a1", StringExp.bag().value("a", "b").build())
				.build();

		mockCtl.replay();
		pipCache.putAttributes(ctx, storedAttributes);
		AttributeSet retrievedAttributes = pipCache.getAttributes(ctx);
		mockCtl.verify();

		assertThat(retrievedAttributes, notNullValue());
		assertThat(retrievedAttributes.get("a1"), equalTo(StringExp.bag().value("a", "b").build()));
	}

	@Test
	public void getAttributesFromEmptyCache() {
		AttributeResolverDescriptor attributeDescriptor = AttributeResolverDescriptorBuilder
				.builder("descriptor-id", "name", null, Categories.SUBJECT_ACCESS)
				.attribute("a1", XacmlTypes.STRING)
				.cache(100)
				.build();
		ResolverContext ctx = mockCtl.createMock(ResolverContext.class);
		expect(ctx.getDescriptor()).andStubReturn(attributeDescriptor);
		expect(ctx.getKeys()).andStubReturn(ImmutableList.of(
				StringExp.bag().value("k1", "k1").build(),
				StringExp.bag().value("k3", "k4").build()));

		mockCtl.replay();
		AttributeSet retrievedAttributes = pipCache.getAttributes(ctx);
		mockCtl.verify();

		assertThat(retrievedAttributes, nullValue());
	}

	@Test
	public void storeContentInCacheAndGetIt() {
		AttributeReferenceKey k1 = AttributeSelectorKey
				.builder()
				.contextSelectorId("id1")
				.xpath("/test")
				.category(Categories.SUBJECT_ACCESS)
				.dataType(XacmlTypes.STRING)
				.build();
		ContentResolverDescriptor contentDescriptor = ContentResolverDescriptorBuilder
				.builder("descriptor-id", "name", Categories.SUBJECT_ACCESS)
				.keys(Arrays.asList(k1))
				.cache(100)
				.build();
		Node testNode = mockCtl.createMock(Node.class);
		Content storedContent = Content
				.builder()
				.resolver(contentDescriptor)
				.content(testNode)
				.build();

		ResolverContext ctx = mockCtl.createMock(ResolverContext.class);
		expect(ctx.getDescriptor()).andStubReturn(contentDescriptor);
		expect(ctx.getKeys()).andStubReturn(ImmutableList.of(
				StringExp.bag().value("k1", "k1").build(),
				StringExp.bag().value("k3", "k4").build()));

		mockCtl.replay();
		pipCache.putContent(ctx, storedContent);
		Content retrievedContent = pipCache.getContent(ctx);
		mockCtl.verify();

		assertThat(retrievedContent, notNullValue());
		assertThat(retrievedContent.getContent(), equalTo(testNode));
	}

	@Test
	public void getContentFromEmptyCache() {
		AttributeReferenceKey k1 = AttributeSelectorKey
				.builder()
				.contextSelectorId("id1")
				.xpath("/test")
				.category(Categories.SUBJECT_ACCESS)
				.dataType(XacmlTypes.STRING)
				.build();
		ContentResolverDescriptor contentDescriptor = ContentResolverDescriptorBuilder
				.builder("descriptor-id", "name", Categories.SUBJECT_ACCESS)
				.keys(Arrays.asList(k1))
				.cache(100)
				.build();

		ResolverContext ctx = mockCtl.createMock(ResolverContext.class);
		expect(ctx.getDescriptor()).andStubReturn(contentDescriptor);
		expect(ctx.getKeys()).andStubReturn(ImmutableList.of(
				StringExp.bag().value("k1", "k1").build(),
				StringExp.bag().value("k3", "k4").build()));

		mockCtl.replay();
		Content retrievedContent = pipCache.getContent(ctx);
		mockCtl.verify();

		assertThat(retrievedContent, nullValue());
	}
}