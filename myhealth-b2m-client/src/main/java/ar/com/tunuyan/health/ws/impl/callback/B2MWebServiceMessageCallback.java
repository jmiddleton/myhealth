package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class B2MWebServiceMessageCallback extends AbstractRequestWebServiceMessageCallback {

	public B2MWebServiceMessageCallback(String action) throws URISyntaxException {
		super(action);
	}

	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		super.doWithMessage(message);

		SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();
		DOMResult xmlHeader = (DOMResult) soapHeader.getResult();
		Node headerNode = xmlHeader.getNode();
		Document ownerDocument = headerNode.getOwnerDocument();
		Element pcehrHeader = ownerDocument.createElementNS(PCEHR_COMMON_NS, "PCEHRMobileHeader");
		pcehrHeader.setPrefix("ns");

		if (this.ihiNumber != null && this.ihiNumber.length() > 0) {
			Element ihiNumber = ownerDocument.createElementNS(PCEHR_COMMON_NS, "ihiNumber");
			ihiNumber.setPrefix("ns");
			pcehrHeader.appendChild(ihiNumber);
			ihiNumber.appendChild(ownerDocument.createTextNode(this.ihiNumber));
			headerNode.appendChild(pcehrHeader);
		}

		if (this.saml != null && this.saml.length() > 0) {
			Node samlNode = convertToDocument(this.saml);
			if(samlNode==null){
				throw new TransformerException("invalid SAML");
			}
			ownerDocument.adoptNode(samlNode);
			headerNode.appendChild(ownerDocument.importNode(samlNode, true));
		}

		if (this.payloadRequest != null) {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(new StreamSource(new StringReader(this.payloadRequest)), message.getPayloadResult());
		}
	}
}