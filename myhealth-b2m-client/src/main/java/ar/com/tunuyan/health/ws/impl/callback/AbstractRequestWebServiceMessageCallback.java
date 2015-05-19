package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URISyntaxException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.addressing.client.ActionCallback;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;

public abstract class AbstractRequestWebServiceMessageCallback extends ActionCallback {
	protected final Logger logger = LoggerFactory.getLogger(getClass());
	protected static final String PCEHR_COMMON_NS = "http://ns.electronichealth.net.au/pcehr/xsd/common/CommonCoreElements/1.0";
	protected String saml;
	protected String ihiNumber;
	protected String payloadRequest;

	public AbstractRequestWebServiceMessageCallback(String action) throws URISyntaxException {
		super(action);
	}

	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		super.doWithMessage(message);
	}

	protected void prettyPrint(Document xml) throws Exception {
		String str = nodeToString(xml);
		logger.trace(str);
	}

	protected String nodeToString(Document xml) throws TransformerConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(xml), new StreamResult(out));
		return out.toString();
	}

	protected Node convertToDocument(String saml) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(new InputSource(new StringReader(saml)));
			return document.getFirstChild();
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return null;
	}

	public void setSaml(String saml) {
		this.saml = saml;
	}

	public void setIhiNumber(String ihiNumber) {
		this.ihiNumber = ihiNumber;
	}

	public void setPayloadRequest(String payloadRequest) {
		this.payloadRequest = payloadRequest;
	}

}