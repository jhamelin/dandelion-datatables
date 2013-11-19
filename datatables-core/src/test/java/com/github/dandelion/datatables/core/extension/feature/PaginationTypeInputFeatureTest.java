package com.github.dandelion.datatables.core.extension.feature;

import static org.fest.assertions.Assertions.assertThat;
import static org.fest.assertions.MapAssert.entry;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.github.dandelion.core.asset.web.AssetsRequestContext;
import com.github.dandelion.datatables.core.constants.DTConstants;
import com.github.dandelion.datatables.core.exception.ExtensionLoadingException;
import com.github.dandelion.datatables.core.extension.AbstractExtensionTest;
import com.github.dandelion.datatables.core.extension.Extension;

public class PaginationTypeInputFeatureTest  extends AbstractExtensionTest {

	@Test
	public void shoud_load_the_extension() throws ExtensionLoadingException {
		
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(new PaginationTypeInputFeature())));

		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).hasSize(1);
		assertThat(mainConfig).includes(entry(DTConstants.DT_PAGINATION_TYPE, "input"));
	}
}