package ar.com.tunuyan.health.ws;

import java.util.Map;

import javax.xml.transform.Source;

public interface PcehrClient {
	public static final String VM_SUFFIX = ".vm";
	public static final String XML_SUFFIX = ".xml";
	public static final String SAML_FILE = "SAML_FILE";
	public static final String TEMPLATE = "_VM_TEMPLATE";
	public static final String DOCUMENT_TYPE = "_DOCUMENT_TYPE";

	Source invoke(String ihi, Map<String, Object> data) throws PcehrClientException;

}
