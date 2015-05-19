package ar.com.tunuyan.health.utils;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XmlHelper {

	private NamespaceContextMap context;

	public NodeList getAsXMLList(Node node, String path) {

		XPath xpath = getXPath();
		xpath.setNamespaceContext(getNamespaceContext());
		NodeList res = null;
		try {
			res = (NodeList) xpath.evaluate(path, node, XPathConstants.NODESET);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}

	public Node getAsXMLElement(Node node, String path) {

		XPath xpath = getXPath();
		xpath.setNamespaceContext(getNamespaceContext());
		Node res = null;
		try {
			res = (Node) xpath.evaluate(path, node, XPathConstants.NODE);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}

	public String getAsString(Node node, String path) {

		XPath xpath = getXPath();
		xpath.setNamespaceContext(getNamespaceContext());
		String res = null;
		try {
			res = (String) xpath.evaluate(path, node, XPathConstants.STRING);
		} catch (XPathExpressionException e) {
			e.printStackTrace();
			return null;
		}
		return res;
	}

	private static XPath getXPath() {
		return XPathFactory.newInstance().newXPath();
	}

	public NamespaceContext getNamespaceContext() {
		// NamespaceContext context = new NamespaceContextMap("ns4",
		// "urn:hl7-org:v3", "ns3", "http://common.pna.ws.pcehr.au/",
		// "ns9", "http://common.htb.ws.pcehr.au/", "ns5",
		// "http://ns.electronichealth.net.au/Ci/Cda/Extensions/3.0",
		// "ns6", "urn:oasis:names:tc:ebxml-regrep:xsd:rim:3.0", "ns10",
		// "http://view.htb.ws.pcehr.au/", "ns7",
		// "urn:oasis:names:tc:ebxml-regrep:xsd:rs:3.0", "ns8",
		// "urn:oasis:names:tc:ebxml-regrep:xsd:query:3.0");
		return context;
	}

	public void setNamespaceContext(String... mappingPairs) {
		context = new NamespaceContextMap(mappingPairs);
	}
}
