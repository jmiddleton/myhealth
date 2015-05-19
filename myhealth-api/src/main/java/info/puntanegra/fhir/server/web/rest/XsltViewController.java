package info.puntanegra.fhir.server.web.rest;

import info.puntanegra.fhir.server.repository.SAMLRepository;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import ar.com.tunuyan.health.json.XmlToJsonConverter;
import ar.com.tunuyan.health.ws.PcehrClient;

/**
 * Render XSLT.
 * 
 * @author jmiddleton
 *
 */
@Controller
@RequestMapping("/api")
public class XsltViewController {

	@Inject
	private PcehrClient getDocument;

	@Inject
	private SAMLRepository samlRepository;

	@RequestMapping(value = "/Binary/", method = RequestMethod.GET)
	public @ResponseBody String getBinaryDocument(@RequestParam String repoId, @RequestParam String docId, @RequestParam String patient,
			HttpServletResponse res) throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("documentRepo", repoId);
		data.put("documentId", docId);
		data.put(PcehrClient.DOCUMENT_TYPE, "NEHTA_Generic_CDA_Stylesheet-1.2.9");
		data.put(PcehrClient.SAML_FILE, getSaml());

		Source response = getDocument.invoke(patient, data);

		res.setContentType("text/html");
		return XmlToJsonConverter.convertXmlToString(response);
	}

	private Object getSaml() {
		String name = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return samlRepository.get(name.toUpperCase());
	}

	// Total control - setup a model and return the view name yourself. Or
	// consider
	// subclassing ExceptionHandlerExceptionResolver (see below).
	@ExceptionHandler(Exception.class)
	public ModelAndView handleError(HttpServletRequest req, Exception exception) {
		ModelAndView mav = new ModelAndView();
		mav.addObject("exception", exception);
		mav.addObject("url", req.getRequestURL());
		mav.setViewName("error");
		return mav;
	}
}
