package info.puntanegra.fhir.server.web.rest;

import info.puntanegra.fhir.server.repository.SAMLRepository;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.xml.transform.Source;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import ar.com.tunuyan.health.json.ConverterException;
import ar.com.tunuyan.health.json.XmlToJsonConverter;
import ar.com.tunuyan.health.ws.PcehrClient;
import ar.com.tunuyan.health.ws.PcehrClientException;
import ca.uhn.fhir.context.FhirContext;
import ca.uhn.fhir.model.api.IResource;

import com.codahale.metrics.annotation.Timed;

@Controller
@RequestMapping("/api")
public class RecordController {

	public static final String SUCCESS = "SUCCESS";

	@Inject
	private PcehrClient getView;

	@Inject
	private PcehrClient getDocumentList;

	@Inject
	private PcehrClient getDocument;

	@Inject
	private PcehrClient getRecordList;

	@Inject
	private SAMLRepository samlRepository;

	private FhirContext ctx = FhirContext.forDstu2();

	@RequestMapping(value = "/Patient", method = RequestMethod.GET)
	@Timed
	public @ResponseBody String getRecordList() throws Exception {

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(PcehrClient.DOCUMENT_TYPE, "getRecordList");
		data.put(PcehrClient.SAML_FILE, getSaml());

		Source response = null;
		try {
			response = getRecordList.invoke(null, data);

			IResource resource = ctx.newXmlParser().parseResource(XmlToJsonConverter.convertXmlToString(response));
			String encoded = ctx.newJsonParser().encodeResourceToString(resource);
			return encoded;
		} catch (Exception e) {
			throw e;
		}
	}

	// Observation/_search?subject%3APatient=7777705&name=3141-9%2C8302-2%2C8287-5%2C39156-5%2C18185-9%2C37362-1
	@RequestMapping(value = "/Observation/_search", method = RequestMethod.GET)
	@Timed
	public @ResponseBody String getObservation(@RequestParam String ihi, @RequestParam String code, @RequestParam String fromDate,
			@RequestParam String toDate, @RequestParam(required = false) String observationType, @RequestParam(required = false) String referenceData) {

		Map<String, Object> data = new HashMap<String, Object>();

		try {
			// ihi = getPcehrIHI(ihi);

			data.put("fromDate", fromDate);
			data.put("toDate", toDate);
			data.put("observationType", observationType);
			data.put("referenceData", referenceData);
			data.put(PcehrClient.DOCUMENT_TYPE, "100.16872_v1.0");
			data.put(PcehrClient.SAML_FILE, getSaml());

			Source response = getView.invoke(ihi, data);
			String xml = XmlToJsonConverter.convertXmlToString(response);

			// TODO: get the content type from the header
			if ("json".equals("json")) {
				IResource resource = ctx.newXmlParser().parseResource(xml);
				String encoded = ctx.newJsonParser().encodeResourceToString(resource);
				return encoded;
			}
			return xml;

		} catch (Exception e) {
			return handleError(e.getMessage());
		}
	}

	@RequestMapping(value = "/DocumentReference/", method = RequestMethod.GET)
	@Timed
	public @ResponseBody String getDocumentReference(@RequestParam(required = false) String type, @RequestParam String patient,
			@RequestParam(required = false) String _format) {

		// default
		if (type != null && type.length() == 0) {
			type = null;
		}

		try {

			Source response = internalGetDocumentList(patient, type);
			String xml = XmlToJsonConverter.convertXmlToString(response);

			// TODO: get the content type from the header
			if ("json".equals(_format)) {
				IResource resource = ctx.newXmlParser().parseResource(xml);
				String encoded = ctx.newJsonParser().encodeResourceToString(resource);
				return encoded;
			}
			return xml;
		} catch (Exception e) {
			return handleError(e.getMessage());
		}
	}

	@RequestMapping(value = "/ClinicalDocument/", method = RequestMethod.GET)
	@Timed
	public @ResponseBody String getClinicalDocument(@RequestParam String repoId, @RequestParam String docId, @RequestParam String patient,
			@RequestParam(required = false) String _format, @RequestParam String type) {

		try {
			Source response = doInvokeGetDocument(type, patient, repoId, docId);
			String xml = XmlToJsonConverter.convertXmlToString(response);

			// TODO: get the content type from the header
			if ("json".equals(_format)) {
				IResource resource = ctx.newXmlParser().parseResource(xml);
				String encoded = ctx.newJsonParser().encodeResourceToString(resource);
				return encoded;
			}
			return xml;

		} catch (Exception e) {
			return handleError(e.getMessage());
		}
	}

	private Source internalGetDocumentList(String ihi, String documentType) throws PcehrClientException {
		Map<String, Object> data = new HashMap<String, Object>();

		// TODO: create a registry with types and values
		String documentFilter = null;
		if (documentType != null) {
			documentFilter = "";
			String[] types = documentType.split(",");
			for (int i = 0; i < types.length; i++) {
				String filter = getFilter(types[i]);
				documentFilter = documentFilter + filter;
			}
		}

		data.put("ihi", ihi);
		data.put("documentType", documentFilter);
		data.put(PcehrClient.DOCUMENT_TYPE, "getDocumentList");
		data.put(PcehrClient.SAML_FILE, getSaml());

		Source response = getDocumentList.invoke(ihi, data);
		return response;
	}

	private String getFilter(String documentType) {
		if ("100.16812,100.16919,100.16920,100.16681,100.16870,100.16685".indexOf(documentType) >= 0) {
			return "<rim:Value>'" + documentType + "^^NCTIS Data Components'</rim:Value>";
		} else {
			return "<rim:Value>'" + documentType + "^^NCTIS'</rim:Value>";
		}
	}

	protected Source doInvokeGetDocument(String type, String ihi, String documentRepo, String documentId) throws PcehrClientException,
			ConverterException {
		Map<String, Object> data = new HashMap<String, Object>();
		data.put("documentRepo", documentRepo);
		data.put("documentId", documentId);
		data.put(PcehrClient.DOCUMENT_TYPE, type);
		data.put(PcehrClient.SAML_FILE, getSaml());

		return getDocument.invoke(ihi, data);
	}

	///////////////////////// private methods ///////////////////////

	protected String handleError(String message) {
		return handleResponse("ERROR", message);
	}

	protected String handleResponse(String status, String message) {
		if (message != null) {
			message = ", \"detail\": \"" + message + "\"";
		} else {
			message = "";
		}
		return "{\"status\":\"" + status + "\"" + message + "}";
	}

	private Object getSaml() {
		String name = (String) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		return this.samlRepository.get(name.toUpperCase());
	}
}