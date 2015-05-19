package ar.com.tunuyan.health.ws.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.springframework.context.ResourceLoaderAware;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceMessageCallback;

import ar.com.tunuyan.health.ws.PcehrClient;
import ar.com.tunuyan.health.ws.impl.callback.B2MWebServiceMessageCallback;
import ar.com.tunuyan.health.ws.impl.callback.CdaResponseWebServiceMessageCallback;
import ar.com.tunuyan.health.ws.impl.callback.ResponseWebServiceMessageCallback;

@Service
public class B2MPcehrClientImpl extends AbstractPcehrClient implements PcehrClient, ResourceLoaderAware {

	private ResourceLoader resourceLoader;

	private String baseXsltPath;

	protected ResponseWebServiceMessageCallback doCreateResponseCallback(String docType) throws IOException, TransformerException {
		// TODO: lookup the transformer. Lazy loading
		ResponseWebServiceMessageCallback responseCallback = this.responseCallback;
		if (responseCallback == null) {

			if (docType != null && docType.length() > 0) {
				this.logger.debug("Creating Saxon transformer...");
				responseCallback = new CdaResponseWebServiceMessageCallback();
				// TODO: externalise the basepath
				Resource resource = this.resourceLoader.getResource(this.baseXsltPath + docType + ".xslt");
				((CdaResponseWebServiceMessageCallback) responseCallback).setXslt(resource);
				((CdaResponseWebServiceMessageCallback) responseCallback).init();
				this.logger.debug("Saxon transformer created.");
			} else {
				responseCallback = new ResponseWebServiceMessageCallback();
			}
		}
		return responseCallback;
	}

	protected WebServiceMessageCallback doCreateRequestCallback(String ihi, Map<String, Object> data, String payload) throws URISyntaxException {
		B2MWebServiceMessageCallback requestCallback = new B2MWebServiceMessageCallback(this.action);
		requestCallback.setIhiNumber(ihi);
		requestCallback.setSaml((String) data.get(SAML_FILE));
		requestCallback.setPayloadRequest(payload);
		return requestCallback;
	}

	@Override
	public void setResourceLoader(ResourceLoader loader) {
		this.resourceLoader = loader;
	}

	public void setBaseXsltPath(String baseXsltPath) {
		this.baseXsltPath = baseXsltPath;
	}

	@Override
	protected String getTemplate(String code) {
		if ("100.16872_v1.0".equals(code)) {
			return "/META-INF/getViewOV.vm";
		} else if ("100.16872_v1.0".equals(code)) {
			return "/META-INF/getViewOV.vm";
		} else if ("NEHTA_Generic_CDA_Stylesheet-1.2.9".equals(code) || "100.16685_v1.0".equals(code)) {
			return "/META-INF/getDocument.vm";
		} else if ("authenticate".equals(code)) {
			return "/META-INF/authenticate.vm";
		} else if ("getDocumentList".equals(code)) {
			return "/META-INF/getDocumentList.vm";
		} else if ("getRecordList".equals(code)) {
			return "/META-INF/getRecordList.xml";
		}

		return null;
	}

}
