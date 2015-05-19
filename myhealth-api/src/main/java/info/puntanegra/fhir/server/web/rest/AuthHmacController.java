package info.puntanegra.fhir.server.web.rest;

import info.puntanegra.fhir.server.domain.User;
import info.puntanegra.fhir.server.repository.SAMLRepository;
import info.puntanegra.fhir.server.security.hmac.TokenHandler;
import info.puntanegra.fhir.server.security.xauth.Token;
import info.puntanegra.fhir.server.service.UserService;
import info.puntanegra.fhir.server.web.rest.dto.SessionHolder;
import info.puntanegra.fhir.server.web.rest.dto.UserInfo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;
import javax.xml.transform.Source;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import ar.com.tunuyan.health.json.XmlToJsonConverter;
import ar.com.tunuyan.health.model.LoginInteraction;
import ar.com.tunuyan.health.scraping.ScrapingException;
import ar.com.tunuyan.health.scraping.ScrapingService;
import ar.com.tunuyan.health.ws.PcehrClient;

/**
 * Controller which authenticate and create an HMAC token. Once the user has
 * been validated on my.gov.au, a user is created in memory and a token is
 * returned. the user is required because of spring security.
 * 
 * The token is based on HMAC which contains all the information required to
 * re-construct the User without accessing an user store. This allows remote
 * services validate the token just using the same secret key.
 * 
 * @author jmiddleton
 *
 */
@RestController
@RequestMapping("/api")
public class AuthHmacController {

	private static final String NO_PASSWORD = "nopassword";

	public static final String SUCCESS = "SUCCESS";

	@Inject
	private PcehrClient authenticate;

	@Inject
	private ScrapingService scrapingSvc;

	@Inject
	private AuthenticationManager authenticationManager;

	@Inject
	private UserService userService;

	@Inject
	private SAMLRepository samlRepository;

	@RequestMapping(value = "/authenticate", method = RequestMethod.POST)
	public @ResponseBody String authenticateLogin(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response) {

		if (userInfo != null) {
			try {
				LoginInteraction interaction = scrapingSvc.authenticate(userInfo.getUsername(), userInfo.getPassword());
				String sq = interaction.getSecretQuestion();
				if (interaction != null && sq != null) {
					SessionHolder.putInteraction(sq, interaction);
					return "{\"status\":\"" + SUCCESS + "\", \"secretQuestion\":\"" + sq + "\"}";
				}
				return handleResponse("ERROR", "Login failed.");
			} catch (ScrapingException e) {
				return handleError(e.getMessage());
			}
		}
		return handleResponse("ERROR", "User information not provided.");
	}

	@RequestMapping(value = "/authenticateSecret", method = RequestMethod.POST)
	public @ResponseBody Token authenticateSQ(@RequestBody UserInfo userInfo, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		if (userInfo != null && userInfo.getSecretAnswer() != null) {
			Map<String, Object> data = new HashMap<String, Object>();

			try {
				LoginInteraction interaction = SessionHolder.getInteraction(userInfo.getSecretQuestion());
				interaction.setSecretAnswer(userInfo.getSecretAnswer());
				String agahSaml = scrapingSvc.authenticateSQ(interaction);

				data.put(PcehrClient.DOCUMENT_TYPE, "authenticate");
				data.put("username", userInfo.getUsername());
				data.put("agahSaml", agahSaml);

				Source authResponse = authenticate.invoke(null, data);
				String pcehrsaml = XmlToJsonConverter.convertXmlToString(authResponse);

				String saml = pcehrsaml.substring(pcehrsaml.indexOf("<wsse:Security"), pcehrsaml.indexOf("</wsse:Security>") + 16);

				if (saml != null && saml.length() > 0) {
					// TODO: create the user if doesn't exists
					User newUser = userService.createUserInformation(userInfo.getUsername(), NO_PASSWORD, userInfo.getUsername(), null,
							userInfo.getUsername() + "@myhealth.com.au", "en");
					newUser.setExpires(new Date().getTime() + 1000 * 60 * 60);

					UsernamePasswordAuthenticationToken token = new UsernamePasswordAuthenticationToken(userInfo.getUsername(), NO_PASSWORD);
					Authentication authentication = this.authenticationManager.authenticate(token);
					SecurityContextHolder.getContext().setAuthentication(authentication);

					samlRepository.save(userInfo.getUsername().toUpperCase(), saml);

					// TODO: cambiar la secret
					TokenHandler tokenHandler = new TokenHandler(DatatypeConverter.parseBase64Binary("myXAuthSecret"));
					String hmacToken = tokenHandler.createTokenForUser(newUser);
					return new Token(hmacToken, new Date().getTime() + 1000 * 60 * 60);
				}
			} catch (Exception e) {
				throw e;
			}
		}
		return null;
	}

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

}
