package ar.com.tunuyan.health.ws.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;

import org.apache.velocity.app.VelocityEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.velocity.VelocityEngineUtils;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.client.core.WebServiceTemplate;
import org.springframework.ws.soap.SoapFault;
import org.springframework.ws.soap.client.SoapFaultClientException;

import ar.com.tunuyan.health.json.ConverterException;
import ar.com.tunuyan.health.json.XmlToJsonConverter;
import ar.com.tunuyan.health.ws.PcehrClient;
import ar.com.tunuyan.health.ws.PcehrClientException;
import ar.com.tunuyan.health.ws.impl.callback.ResponseWebServiceMessageCallback;

/**
 * Abstract PCEHR client to access different Web Service API.
 * 
 * @author jmiddleton
 *
 */
public abstract class AbstractPcehrClient implements PcehrClient {

	protected final Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private WebServiceTemplate webServiceTemplate;
	@Autowired
	private VelocityEngine vmEngine;
	private String uri;
	protected String action;
	protected ResponseWebServiceMessageCallback responseCallback;

	public AbstractPcehrClient() {
		super();
	}

	@PostConstruct
	public void init() {

	}

	public Source invoke(String ihi, Map<String, Object> data) throws PcehrClientException {
		String payload = null;

		String docType = (String) data.get(DOCUMENT_TYPE);
		String template = getTemplate(docType);

		try {

			if (template != null && template.length() > 0) {
				if (template.endsWith(VM_SUFFIX)) {
					payload = VelocityEngineUtils.mergeTemplateIntoString(vmEngine, template, "UTF-8", data);
				} else {
					if (template.endsWith(XML_SUFFIX)) {
						payload = XmlToJsonConverter.readFile(template);
					} else {
						throw new NullPointerException("Template " + template + "not found.");
					}
				}
			}

			if (payload == null) {
				throw new NullPointerException("Template with errors.");
			}

			WebServiceMessageCallback requestCallback = doCreateRequestCallback(ihi, data, payload);
			ResponseWebServiceMessageCallback responseCallback = doCreateResponseCallback(docType);

			this.logger.debug("Calling service...");
			boolean success = this.webServiceTemplate.sendAndReceive(this.uri, requestCallback, responseCallback);
			this.logger.debug("Response received.");

			// TODO: check soap response to check for XDS.b errors
			if (success) {
				return responseCallback.getResult();
			}
			return null;
		} catch (SoapFaultClientException sfce) {
			SoapFault fault = sfce.getSoapFault();
			// TODO: return an exception with the errorCode and message
			try {
				logger.debug(XmlToJsonConverter.convertXmlToString(fault.getSource()));
			} catch (ConverterException e) {
				throw new PcehrClientException(e);
			}
			throw new PcehrClientException(fault.getFaultStringOrReason());
		} catch (Exception e) {
			throw new PcehrClientException(e);
		}
	}

	protected abstract ResponseWebServiceMessageCallback doCreateResponseCallback(String docType) throws IOException, TransformerException;

	protected abstract WebServiceMessageCallback doCreateRequestCallback(String ihi, Map<String, Object> data, String payload)
			throws URISyntaxException;

	protected abstract String getTemplate(String code);

	public void setUri(String uri) {
		this.uri = uri;
	}

	public void setResponseCallback(ResponseWebServiceMessageCallback responseCallback) {
		this.responseCallback = responseCallback;
	}

	public void setAction(String action) {
		this.action = action;
	}

}