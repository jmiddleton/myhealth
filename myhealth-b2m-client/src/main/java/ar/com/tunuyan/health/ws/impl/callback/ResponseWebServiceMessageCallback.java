package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.client.core.WebServiceMessageCallback;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Node;

public class ResponseWebServiceMessageCallback implements WebServiceMessageCallback {

	protected StringWriter stringWriter = null;

	public ResponseWebServiceMessageCallback() {
	}

	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		this.stringWriter = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(((SaajSoapMessage) message).getPayloadSource(), new StreamResult(stringWriter));
	}

	public Source getResult() {
		return new StringSource(stringWriter.toString());
	}

	protected String nodeToString(Node node) throws TransformerConfigurationException, TransformerFactoryConfigurationError,
			TransformerException {
		Transformer tf = TransformerFactory.newInstance().newTransformer();
		tf.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
		tf.setOutputProperty(OutputKeys.ENCODING, "UTF-8");
		tf.setOutputProperty(OutputKeys.INDENT, "yes");
		Writer out = new StringWriter();
		tf.transform(new DOMSource(node), new StreamResult(out));
		return out.toString();
	}
}