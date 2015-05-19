package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringSource;

import ar.com.tunuyan.health.json.ConverterException;
import ar.com.tunuyan.health.json.XmlToJsonConverter;
import ar.com.tunuyan.health.ws.StylesheetCache;

public class SaxonResponseWebServiceMessageCallback extends ResponseWebServiceMessageCallback {

	protected final Logger logger = LoggerFactory.getLogger(getClass().getName());
	protected Resource xsltResource;

	public SaxonResponseWebServiceMessageCallback() {
	}

	@PostConstruct
	public void init() throws IOException, TransformerException {
		if (this.xsltResource == null) {
			throw new TransformerException("Xslt path not defined.");
		}
	}

	@Override
	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		this.stringWriter = new StringWriter();
		Source response = ((SaajSoapMessage) message).getPayloadSource();

		// TODO: use streams
		logger.debug("Start transforming response using Saxon...");
		try {
			Transformer transformer = StylesheetCache.newTransformer(xsltResource);
			transformer.transform(new StringSource(XmlToJsonConverter.convertXmlToString(response)), new StreamResult(
					stringWriter));
			logger.debug("Saxon transformation finished.");
		} catch (ConverterException e) {
			e.printStackTrace();
		}
	}

	public void setXslt(Resource xslt) throws IOException {
		this.xsltResource = xslt;
	}
}