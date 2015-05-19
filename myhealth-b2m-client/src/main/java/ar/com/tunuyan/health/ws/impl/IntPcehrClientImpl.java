package ar.com.tunuyan.health.ws.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;

import javax.xml.transform.TransformerException;

import org.springframework.stereotype.Service;
import org.springframework.ws.client.core.WebServiceMessageCallback;

import ar.com.tunuyan.health.ws.PcehrClient;
import ar.com.tunuyan.health.ws.impl.callback.IntWebServiceMessageCallback;
import ar.com.tunuyan.health.ws.impl.callback.ResponseWebServiceMessageCallback;

/**
 * TODO: Complete implementation
 * 
 * @author jmiddleton
 *
 */
@Service
public class IntPcehrClientImpl extends AbstractPcehrClient implements PcehrClient {

	@Override
	protected ResponseWebServiceMessageCallback doCreateResponseCallback(String docType) throws IOException, TransformerException {
		return null;
	}

	@Override
	protected WebServiceMessageCallback doCreateRequestCallback(String ihi, Map<String, Object> data, String payload) throws URISyntaxException {
		IntWebServiceMessageCallback requestCallback = new IntWebServiceMessageCallback(this.action);
		//requestCallback.setIntPcehrHeader((IntPCEHRHeader) data.get("intPcehrHeader"));
		requestCallback.setPayloadRequest(payload);
		return requestCallback;
	}

	@Override
	protected String getTemplate(String code) {
		// TODO Auto-generated method stub
		return null;
	}

}
