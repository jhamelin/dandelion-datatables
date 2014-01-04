package com.github.dandelion.datatables.core.extension.feature;

import static org.fest.assertions.Assertions.assertThat;

import java.util.Arrays;
import java.util.HashSet;

import org.junit.Test;

import com.github.dandelion.core.asset.web.AssetsRequestContext;
import com.github.dandelion.datatables.core.callback.CallbackType;
import com.github.dandelion.datatables.core.configuration.Scope;
import com.github.dandelion.datatables.core.configuration.TableConfig;
import com.github.dandelion.datatables.core.export.ExportConf;
import com.github.dandelion.datatables.core.export.ExportLinkPosition;
import com.github.dandelion.datatables.core.export.HttpMethod;
import com.github.dandelion.datatables.core.extension.AbstractExtensionTest;
import com.github.dandelion.datatables.core.extension.Extension;

public class ExportFeatureTest extends AbstractExtensionTest {

	private ExportFeature exportFeature;

	@Test
	public void shoud_handle_filter_export() {
		
		exportFeature = new ExportFeature();
		
		ExportConf exportConf = new ExportConf("csv");
		exportConf.setUrl("/myExportUrl");
		exportConf.setLabel("CSV");
		table.getTableConfiguration().getExportConfiguration().put("csv", exportConf);
		table.getTableConfiguration().set(TableConfig.EXPORT_LINK_POSITIONS,
				new HashSet<ExportLinkPosition>(Arrays.asList(ExportLinkPosition.TOP_RIGHT)));
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(exportFeature)));

		assertThat(exportFeature.getBeforeAll()).isNull();
		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).isEmpty();
		assertThat(mainConfig.get(CallbackType.INIT.getName()).toString())
			.isEqualTo("function(oSettings,json) {\n      $('#fakeId_wrapper').prepend('<div class=\"dandelion_dataTables_export\" style=\"float:right;\"><a style=\"margin-left:2px;\" href=\"/myExportUrl\">CSV</a></div>');\n\n   }");
	}
	
	@Test
	public void shoud_handle_controller_export_with_GET_and_no_extra_param() {
		
		exportFeature = new ExportFeature();
		
		ExportConf exportConf = new ExportConf("csv");
		exportConf.setHasCustomUrl(true);
		exportConf.setUrl("/myExportUrl");
		exportConf.setLabel("CSV");
		table.getTableConfiguration().getExportConfiguration().put("csv", exportConf);
		table.getTableConfiguration().set(TableConfig.EXPORT_LINK_POSITIONS,
				new HashSet<ExportLinkPosition>(Arrays.asList(ExportLinkPosition.TOP_RIGHT)));
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(exportFeature)));
		
		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).isEmpty();
		assertThat(exportFeature.getBeforeAll().toString()).isEqualTo("function ddl_dt_launch_export_fakeId_csv(){\n   window.location=\"/myExportUrl?\" + decodeURIComponent($.param(oTable_fakeId.oApi._fnAjaxParameters(oTable_fakeId.fnSettings())).replace(/\\+/g,' '));\n}\n");
	}
	
	@Test
	public void shoud_handle_controller_export_with_GET_existing_params_and_no_extra_param() {
		
		exportFeature = new ExportFeature();
		
		ExportConf exportConf = new ExportConf("csv");
		exportConf.setHasCustomUrl(true);
		exportConf.setUrl("/myExportUrl?existingParam=val");
		exportConf.setLabel("CSV");
		table.getTableConfiguration().getExportConfiguration().put("csv", exportConf);
		table.getTableConfiguration().set(TableConfig.EXPORT_LINK_POSITIONS,
				new HashSet<ExportLinkPosition>(Arrays.asList(ExportLinkPosition.TOP_RIGHT)));
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(exportFeature)));
		
		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).isEmpty();
		assertThat(exportFeature.getBeforeAll().toString()).isEqualTo("function ddl_dt_launch_export_fakeId_csv(){\n   window.location=\"/myExportUrl?existingParam=val&\" + decodeURIComponent($.param(oTable_fakeId.oApi._fnAjaxParameters(oTable_fakeId.fnSettings())).replace(/\\+/g,' '));\n}\n");
	}
	
	@Test
	public void shoud_handle_controller_export_with_POST_and_no_extra_param() {
		
		exportFeature = new ExportFeature();
		
		ExportConf exportConf = new ExportConf("csv");
		exportConf.setHasCustomUrl(true);
		exportConf.setUrl("/myExportUrl");
		exportConf.setMethod(HttpMethod.POST);
		exportConf.setLabel("CSV");
		table.getTableConfiguration().getExportConfiguration().put("csv", exportConf);
		table.getTableConfiguration().set(TableConfig.EXPORT_LINK_POSITIONS,
				new HashSet<ExportLinkPosition>(Arrays.asList(ExportLinkPosition.TOP_RIGHT)));
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(exportFeature)));
		
		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).contains(Scope.DDL_DT_EXPORT.getScopeName());
		assertThat(exportFeature.getBeforeAll().toString()).isEqualTo("function ddl_dt_launch_export_fakeId_csv(){\n   $.download('/myExportUrl', decodeURIComponent($.param(oTable_fakeId.oApi._fnAjaxParameters(oTable_fakeId.fnSettings())).replace(/\\+/g,' '),'POST'));\n}\n");
	}
	
	@Test
	public void shoud_handle_controller_export_with_GET_and_extra_param() {
		
		exportFeature = new ExportFeature();
		
		ExportConf exportConf = new ExportConf("csv");
		exportConf.setHasCustomUrl(true);
		exportConf.setUrl("/myExportUrl");
		exportConf.setLabel("CSV");
		table.getTableConfiguration().getExportConfiguration().put("csv", exportConf);
		table.getTableConfiguration().set(TableConfig.AJAX_SERVERPARAM, "param1=val1&param2=val2");
		table.getTableConfiguration().set(TableConfig.EXPORT_LINK_POSITIONS,
				new HashSet<ExportLinkPosition>(Arrays.asList(ExportLinkPosition.TOP_RIGHT)));
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(exportFeature)));
		
		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).isEmpty();
		assertThat(exportFeature.getBeforeAll().toString()).isEqualTo("function ddl_dt_launch_export_fakeId_csv(){\n   var aoData = oTable_fakeId.oApi._fnAjaxParameters(oTable_fakeId.fnSettings());\n   param1=val1&param2=val2(aoData);\n   window.location=\"/myExportUrl?\" + decodeURIComponent($.param(aoData)).replace(/\\+/g,' ');\n}\n");
	}
	
	@Test
	public void shoud_handle_controller_export_with_GET_existing_params_and_extra_param() {
		
		exportFeature = new ExportFeature();
		
		ExportConf exportConf = new ExportConf("csv");
		exportConf.setHasCustomUrl(true);
		exportConf.setUrl("/myExportUrl?existingParam=val");
		exportConf.setLabel("CSV");
		table.getTableConfiguration().getExportConfiguration().put("csv", exportConf);
		table.getTableConfiguration().set(TableConfig.AJAX_SERVERPARAM, "param1=val1&param2=val2");
		table.getTableConfiguration().set(TableConfig.EXPORT_LINK_POSITIONS,
				new HashSet<ExportLinkPosition>(Arrays.asList(ExportLinkPosition.TOP_RIGHT)));
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(exportFeature)));
		
		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).isEmpty();
		assertThat(exportFeature.getBeforeAll().toString()).isEqualTo("function ddl_dt_launch_export_fakeId_csv(){\n   var aoData = oTable_fakeId.oApi._fnAjaxParameters(oTable_fakeId.fnSettings());\n   param1=val1&param2=val2(aoData);\n   window.location=\"/myExportUrl?existingParam=val&\" + decodeURIComponent($.param(aoData)).replace(/\\+/g,' ');\n}\n");
	}
	
	@Test
	public void shoud_handle_controller_export_with_POST_and_extra_param() {
		
		exportFeature = new ExportFeature();
		
		ExportConf exportConf = new ExportConf("csv");
		exportConf.setHasCustomUrl(true);
		exportConf.setUrl("/myExportUrl");
		exportConf.setMethod(HttpMethod.POST);
		exportConf.setLabel("CSV");
		table.getTableConfiguration().getExportConfiguration().put("csv", exportConf);
		table.getTableConfiguration().set(TableConfig.AJAX_SERVERPARAM, "param1=val1&param2=val2");
		table.getTableConfiguration().set(TableConfig.EXPORT_LINK_POSITIONS,
				new HashSet<ExportLinkPosition>(Arrays.asList(ExportLinkPosition.TOP_RIGHT)));
		extensionProcessor.process(new HashSet<Extension>(Arrays.asList(exportFeature)));
		
		assertThat(AssetsRequestContext.get(table.getTableConfiguration().getRequest()).getScopes(true)).contains(Scope.DDL_DT_EXPORT.getScopeName());
		assertThat(exportFeature.getBeforeAll().toString()).isEqualTo("function ddl_dt_launch_export_fakeId_csv(){\n   var aoData = oTable_fakeId.oApi._fnAjaxParameters(oTable_fakeId.fnSettings());\n   param1=val1&param2=val2(aoData);\n   $.download('/myExportUrl', decodeURIComponent($.param(aoData)).replace(/\\+/g,' '),'POST');\n}\n");
	}
}