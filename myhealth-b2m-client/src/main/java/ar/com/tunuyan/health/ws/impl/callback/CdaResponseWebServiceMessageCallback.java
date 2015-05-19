package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.io.StringWriter;

import javax.annotation.PostConstruct;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathException;
import javax.xml.xpath.XPathFactory;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.springframework.xml.transform.StringSource;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import ar.com.tunuyan.health.utils.NamespaceContextMap;
import ar.com.tunuyan.health.ws.StylesheetCache;

public class CdaResponseWebServiceMessageCallback extends SaxonResponseWebServiceMessageCallback {

	private static final String CDA_XML_ELEMENT_NAME = "//ns4:ClinicalDocument";
	private XPath xpath;

	public CdaResponseWebServiceMessageCallback() {
	}

	@PostConstruct
	public void init() throws IOException, TransformerException {
		super.init();

		this.xpath = XPathFactory.newInstance().newXPath();
		this.xpath.setNamespaceContext(new NamespaceContextMap("ns4", "urn:hl7-org:v3", "ns3", "http://common.pna.ws.pcehr.au/",
				"ns9", "http://common.htb.ws.pcehr.au/", "ns5", "http://ns.electronichealth.net.au/Ci/Cda/Extensions/3.0", "ns6",
				"urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ns10", "http://view.htb.ws.pcehr.au/", "ns7",
				"urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "ns8", "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0"));
	}

	@Override
	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		this.stringWriter = new StringWriter();
		Document document = ((SaajSoapMessage) message).getDocument();

		// TODO: use streams
		try {

			Node clinicalDocument = (Node) this.xpath.evaluate(CDA_XML_ELEMENT_NAME, document, XPathConstants.NODE);
			String cda = nodeToString(clinicalDocument);

			Transformer transformer = StylesheetCache.newTransformer(xsltResource);
			transformer.transform(new StringSource(cda), new StreamResult(stringWriter));
		} catch (XPathException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}