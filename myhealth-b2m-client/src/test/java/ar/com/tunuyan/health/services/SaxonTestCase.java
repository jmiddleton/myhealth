package ar.com.tunuyan.health.services;

import java.io.File;
import java.io.StringWriter;

import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.junit.Test;

import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;

public class SaxonTestCase {

	// @Test
	public void testGetRecordList() throws Exception {

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslt = new StreamSource(
				new File(
						"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/main/resources/META-INF/xslt/getRecordList_1.0.xslt"));
		Transformer transformer = factory.newTransformer(xslt);

		Source xml = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-web/recordListResponse.xml"));
		StringWriter stringWriter = new StringWriter();
		transformer.transform(xml, new StreamResult(stringWriter));

		System.out.println(stringWriter.getBuffer());

		FhirContext ctx = FhirContext.forDstu2();

		IResource resource = ctx.newXmlParser().parseResource(stringWriter.getBuffer().toString());
		String encoded = ctx.newJsonParser().encodeResourceToString(resource);
		System.out.println(encoded);
		// ((Patient)((Bundle.Entry)((Bundle)resource).getEntry().iterator().next()).getResource()).getGenderElement()
	}

	// @Test
	public void testGetDocumentList() throws Exception {

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslt = new StreamSource(
				new File(
						"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/main/resources/META-INF/xslt/getDocumentList_1.0.xslt"));
		Transformer transformer = factory.newTransformer(xslt);

		Source xml = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/test/resources/getDocList_Response.xml"));
		StringWriter stringWriter = new StringWriter();
		transformer.transform(xml, new StreamResult(stringWriter));

		System.out.println(stringWriter.getBuffer());

		FhirContext ctx = FhirContext.forDstu2();

		IResource resource = ctx.newXmlParser().parseResource(stringWriter.getBuffer().toString());
		String encoded = ctx.newJsonParser().encodeResourceToString(resource);
		System.out.println(encoded);
	}

	//@Test
	public void testGetDocument() throws Exception {

		String docType = "100.16685";

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslt = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/main/resources/META-INF/xslt/"
						+ docType + "_v1.0.xslt"));
		Transformer transformer = factory.newTransformer(xslt);

		Source xml = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/test/resources/getDocPHS_Response.xml"));
		StringWriter stringWriter = new StringWriter();
		transformer.transform(xml, new StreamResult(stringWriter));

		System.out.println(stringWriter.getBuffer());

		FhirContext ctx = FhirContext.forDstu2();

		IResource resource = ctx.newXmlParser().parseResource(stringWriter.getBuffer().toString());
		String encoded = ctx.newJsonParser().encodeResourceToString(resource);
		System.out.println(encoded);
	}

	// @Test
	public void testGetView() throws Exception {

		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslt = new StreamSource(
				new File(
						"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/main/resources/META-INF/xslt/getView_1.0.xslt"));
		Transformer transformer = factory.newTransformer(xslt);

		Source xml = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/test/resources/getViewMOV_Response.xml"));
		StringWriter stringWriter = new StringWriter();
		transformer.transform(xml, new StreamResult(stringWriter));

		System.out.println(stringWriter.getBuffer());

		FhirContext ctx = FhirContext.forDstu2();

		IResource resource = ctx.newXmlParser().parseResource(stringWriter.getBuffer().toString());
		String encoded = ctx.newJsonParser().encodeResourceToString(resource);
		System.out.println(encoded);
	}
	
	//@Test
	public void testBMIOV() throws Exception {
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslt = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/main/resources/META-INF/xslt/100.16872_v1.0.xslt"));
		Transformer transformer = factory.newTransformer(xslt);

		Source xml = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/test/resources/getViewOV_Response.xml"));
		StringWriter stringWriter = new StringWriter();
		transformer.transform(xml, new StreamResult(stringWriter));

		System.out.println(stringWriter.getBuffer());
		
		FhirContext ctx = FhirContext.forDstu2();

		IResource resource = ctx.newXmlParser().parseResource(stringWriter.getBuffer().toString());
		String encoded = ctx.newJsonParser().encodeResourceToString(resource);
		System.out.println(encoded);
	}
	
	//@Test
	public void testSimpleOV() throws Exception {
		System.setProperty("javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");

		TransformerFactory factory = TransformerFactory.newInstance();
		Source xslt = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/main/resources/META-INF/xslt/100.16872_v1.0.xslt"));
		Transformer transformer = factory.newTransformer(xslt);

		Source xml = new StreamSource(new File(
				"/Users/jmiddleton/Documents/workspace-angularjs/mipatojo-services/src/test/resources/getViewOV_Response2.xml"));
		StringWriter stringWriter = new StringWriter();
		transformer.transform(xml, new StreamResult(stringWriter));

		System.out.println(stringWriter.getBuffer());
	}
}
