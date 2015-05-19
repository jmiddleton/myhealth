package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;

public class AuthenticateWebServiceMessageCallback extends ResponseWebServiceMessageCallback {

	public AuthenticateWebServiceMessageCallback() {
	}

	@Override
	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		this.stringWriter = new StringWriter();
		TransformerFactory factory = TransformerFactory.newInstance();
		Transformer transformer = factory.newTransformer();
		transformer.transform(((SaajSoapMessage) message).getSoapHeader().getSource(), new StreamResult(stringWriter));
	}
}