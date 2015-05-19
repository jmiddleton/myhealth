package ar.com.tunuyan.health.scraping;

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
import ar.com.tunuyan.health.model.LoginInteraction;
import ar.com.tunuyan.health.ws.PcehrClient;

@ContextConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class ScrapingServiceTest {

	@Autowired
	private PcehrClient authenticate;
	
	@Autowired
	private PcehrClient getRecordList;

	@Autowired
	private ScrapingService scrapingSvc;

	@Test
	public void testAuthenticate() {

		long start = System.currentTimeMillis();
		System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> start: " + start);

		try {
			LoginInteraction interaction= scrapingSvc.authenticate("WG395514", "Catuti123$");
			String sq = interaction.getSecretQuestion();

			System.out.println(sq);

			// scrapingSvc.authenticateSQ("a" + sq.substring(1, sq.length()));
			interaction.setSecretAnswer("almirante brown");
			String saml= scrapingSvc.authenticateSQ(interaction);

			System.out.println("Response: " + saml);
			System.out.println("end: " + ((System.currentTimeMillis() - start)) + " millis.");

			Map<String, Object> data = new HashMap<String, Object>();
			data.put(PcehrClient.DOCUMENT_TYPE, "authenticate");
			data.put("agahSaml", saml);
			data.put("username", "WG395514");

			Source result = authenticate.invoke(null, data);
			Assert.assertNotNull(result);

			String pcehrsaml = XmlToJsonConverter.convertXmlToString(result);
			System.out.println(pcehrsaml.substring(pcehrsaml.indexOf("<wsse:Security"), pcehrsaml.indexOf("</wsse:Security>") + 16));
			System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>> end: " + (System.currentTimeMillis() - start) + " millis.");

			data.put(PcehrClient.DOCUMENT_TYPE, "getRecordList");
			data.put(PcehrClient.SAML_FILE, pcehrsaml);
			
			Source response = getRecordList.invoke(null, data);
		} catch (Exception e) {
			e.printStackTrace();
			Assert.fail(e.getMessage());
		}
	}

}
