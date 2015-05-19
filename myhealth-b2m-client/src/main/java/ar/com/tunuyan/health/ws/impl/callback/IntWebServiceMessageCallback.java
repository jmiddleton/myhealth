package ar.com.tunuyan.health.ws.impl.callback;

import java.io.IOException;
import java.io.StringReader;
import java.net.URISyntaxException;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;

import org.springframework.ws.WebServiceMessage;

/**
 * TODO: Finish implementation.
 * 
 * @author jmiddleton
 *
 */
public class IntWebServiceMessageCallback extends AbstractRequestWebServiceMessageCallback {

	protected static final String PCEHR_COMMON_NS = "http://ns.electronichealth.net.au/pcehr/xsd/common/intCommonCoreElements/1.0";
	//protected IntPCEHRHeader intPcehrHeader;

	public IntWebServiceMessageCallback(String action) throws URISyntaxException {
		super(action);
	}

	public void doWithMessage(WebServiceMessage message) throws IOException, TransformerException {
		super.doWithMessage(message);

		//SoapHeader soapHeader = ((SoapMessage) message).getSoapHeader();
		// soapHeader.addNamespaceDeclaration("ns", PCEHR_COMMON_NS);

		//DOMResult xmlHeader = (DOMResult) soapHeader.getResult();
		//Node headerNode = xmlHeader.getNode();
		//Document ownerDocument = headerNode.getOwnerDocument();

		// TODO: validate internal header
		//if (this.intPcehrHeader != null) {
			//Document doc = XmlToJsonConverter.convertToDom(this.intPcehrHeader);
			//headerNode.appendChild(ownerDocument.importNode(doc.getFirstChild(), true));
		//}

		if (this.payloadRequest != null) {
			TransformerFactory factory = TransformerFactory.newInstance();
			Transformer transformer = factory.newTransformer();
			transformer.transform(new StreamSource(new StringReader(this.payloadRequest)), message.getPayloadResult());
		}
	}

//	public void setIntPcehrHeader(IntPCEHRHeader intPcehrHeader) {
//		this.intPcehrHeader = intPcehrHeader;
//	}
}