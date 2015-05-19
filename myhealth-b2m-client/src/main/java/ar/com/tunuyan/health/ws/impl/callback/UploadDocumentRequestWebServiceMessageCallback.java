package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.net.URISyntaxException;

import javax.xml.soap.SOAPBody;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMResult;

import org.springframework.ws.WebServiceMessage;
import org.springframework.ws.soap.SoapHeader;
import org.springframework.ws.soap.SoapMessage;
import org.springframework.ws.soap.saaj.SaajSoapMessage;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UploadDocumentRequestWebServiceMessageCallback extends AbstractRequestWebServiceMessageCallback {

	public UploadDocumentRequestWebServiceMessageCallback(String action) throws URISyntaxException {
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
			ownerDocument.adoptNode(samlNode);
			headerNode.appendChild(ownerDocument.importNode(samlNode, true));
		}

		if (this.payloadRequest != null) {
			try {
				Node root = convertToDocument(this.payloadRequest);
				SOAPBody body = ((SaajSoapMessage) message).getSaajMessage().getSOAPBody();
				Document bodyDocument = body.getOwnerDocument();
				NodeList list = root.getChildNodes();
				for (int i = 0; i < list.getLength(); i++) {
					Node item = list.item(i);
					if (item.getNodeType() == Node.ELEMENT_NODE) {
						body.appendChild(bodyDocument.importNode(item, true));
					}
				}
				prettyPrint(ownerDocument);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}