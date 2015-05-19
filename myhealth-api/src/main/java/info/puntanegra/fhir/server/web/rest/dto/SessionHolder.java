package info.puntanegra.fhir.server.web.rest.dto;

import java.util.HashMap;
import java.util.Map;

import ar.com.tunuyan.health.model.LoginInteraction;

public class SessionHolder {

	private static Map<String, LoginInteraction> interactions = new HashMap<String, LoginInteraction>();
	private static Map<String, String> pcherSamls = new HashMap<String, String>();

	public static void putInteraction(String key, LoginInteraction interaction) {
		interactions.put(key, interaction);
	}

	public static LoginInteraction getInteraction(String key) {
		return interactions.get(key);
	}

	public static void putSaml(String key, String saml) {
		pcherSamls.put(key, saml);
	}

	public static String getSaml(String key) {
		return pcherSamls.get(key);
	}
}
