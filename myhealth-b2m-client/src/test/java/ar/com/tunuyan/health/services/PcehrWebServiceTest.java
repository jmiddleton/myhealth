package ar.com.tunuyan.health.services;

import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import ar.com.tunuyan.health.json.XmlToJsonConverter;
import ar.com.tunuyan.health.utils.OidGenerator;
import ar.com.tunuyan.health.ws.PcehrClient;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class PcehrWebServiceTest {

	@Autowired
	private PcehrClient getView;

	@Autowired
	private PcehrClient authenticate;

	@Autowired
	private PcehrClient getDocument;

	@Autowired
	private PcehrClient getRecordList;

	@Autowired
	private PcehrClient getDocumentList;

	@Autowired
	private PcehrClient uploadDocument;

	@Test
	public void testGetRecordList() {

		long start = System.currentTimeMillis();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> start: " + start);

		try {

			Map<String, Object> data = new HashMap<String, Object>();
			data.put(PcehrClient.DOCUMENT_TYPE, "getRecordList");
			data.put(PcehrClient.SAML_FILE, XmlToJsonConverter.readFile("/META-INF/pcehrSaml.xml"));

			Source response = getRecordList.invoke(null, data);
			System.out.println(response);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

	// @Test
	public void testGetView() throws Exception {
		assertNotNull(getView);
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("fromDate", "2013-04-08");
		data.put("toDate", "2015-04-08");
		data.put("observationType", "WEIGHT");
		data.put("referenceData", "WHO");

		data.put(PcehrClient.TEMPLATE, "/META-INF/getViewMOV.vm");
		data.put(PcehrClient.SAML_FILE, XmlToJsonConverter.readFile("/META-INF/pcehrSaml.xml"));

		Source source = getView.invoke("8003603460392146", data);
		assertNotNull(source);

	}

	// @Test
	public void testAuthenticate() throws Exception {
		assertNotNull(authenticate);
		Map<String, Object> data = new HashMap<String, Object>();

		data.put(PcehrClient.TEMPLATE, "/META-INF/authenticate.vm");
		data.put("agahSaml", "XXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		data.put("username", "MipatojoUser");

		Source source = authenticate.invoke(null, data);
		assertNotNull(source);

	}

	// @Test
	public void testRecordListError() throws Exception {
		assertNotNull(getView);
		Map<String, Object> data = new HashMap<String, Object>();
		data.put(PcehrClient.TEMPLATE, "/META-INF/getViewMOV.vm");
		data.put(PcehrClient.SAML_FILE, XmlToJsonConverter.readFile("/META-INF/pcehrSaml.xml"));

		try {
			getView.invoke("xxxxx", data);
			Assert.assertTrue(true);

		} catch (Exception e) {
			Assert.assertEquals("PCEHR_ERROR", e.getMessage());
		}
	}

	// @Test
	public void testCreateNote() {
		Map<String, Object> data = new HashMap<String, Object>();
		String givenName = "PCEHR";
		String familyName = "TESTER FOUR";

		data.put("title", "title");
		data.put("text", "text");
		data.put("date", new Date());
		data.put("oid", OidGenerator.genOID());
		data.put("ihi", "8003603459045713");
		data.put("givenName", givenName);
		data.put("familyName", familyName);
		data.put("repGivenName", "test");
		data.put("repFamilyName", "family test name");
		data.put("repoId", "1.2.36.1.2001.1007.1.8003640000000029");
		data.put(PcehrClient.TEMPLATE, "/META-INF/createNote.vm");

		try {
			data.put(PcehrClient.SAML_FILE, XmlToJsonConverter.readFile("/META-INF/pcehrSaml.xml"));

			Source response = uploadDocument.invoke("8003603459045713", data);
			Assert.assertTrue(response != null);
		} catch (Exception e) {
			Assert.assertEquals("PCEHR_ERROR", e.getMessage());
		}
	}

	//@Test
	public void testDocument() throws Exception {
		assertNotNull(getDocument);
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("documentRepo", "1.2.36.1.2001.1007.10.8003640002000050");
		data.put("documentId", "2.25.313862032022872013966737127168786706520");
		data.put(PcehrClient.TEMPLATE, "/META-INF/getDocument.vm");
		data.put(PcehrClient.SAML_FILE, XmlToJsonConverter.readFile("/META-INF/pcehrSaml.xml"));
		data.put(PcehrClient.DOCUMENT_TYPE, "100.16685_v1.0");

		try {
			Source response = getDocument.invoke("8003603460392146", data);

			String json = (String) XmlToJsonConverter.convertXmlToString(response);
			assertNotNull(json);
			System.out.println(json);

		} catch (Exception e) {
			Assert.assertEquals("PCEHR_ERROR", e.getMessage());
		}
	}

	// @Test
	public void testDocumentList() throws Exception {
		assertNotNull(getDocumentList);
		Map<String, Object> data = new HashMap<String, Object>();

		data.put("ihi", "8003603460392146");
		// data.put("documentType",
		// "<rim:Value>'100.16919^^NCTIS Data Components'</rim:Value>");
		data.put("documentType", "<rim:Value>'100.16685^^NCTIS Data Components'</rim:Value>");
		data.put(PcehrClient.TEMPLATE, "/META-INF/getDocumentList.vm");
		data.put(PcehrClient.SAML_FILE, XmlToJsonConverter.readFile("/META-INF/pcehrSaml.xml"));

		// 100.16650 Pharmaceutical Benefits Report
		// 100.16644 Medicare/DVA Benefits Report
		// 100.16681 Personal Health Note
		try {
			Source response = getDocumentList.invoke("8003603460392146", data);

			String json = (String) XmlToJsonConverter.convertXmlToString(response);
			assertNotNull(json);
			System.out.println(json);

		} catch (Exception e) {
			Assert.assertEquals("PCEHR_ERROR", e.getMessage());
		}
	}

}
