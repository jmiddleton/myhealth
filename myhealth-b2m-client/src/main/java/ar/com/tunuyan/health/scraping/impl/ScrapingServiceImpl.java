package ar.com.tunuyan.health.scraping.impl;

import java.io.IOException;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import ar.com.tunuyan.health.model.LoginInteraction;
import ar.com.tunuyan.health.scraping.ScrapingException;
import ar.com.tunuyan.health.scraping.ScrapingService;

@Service
@Scope(value = ConfigurableBeanFactory.SCOPE_SINGLETON)
// TODO: refactor this service to be stateless. after authenticate returns the
// cookies, nextAction and SQ so the next step can be executed anywhere else not
// only in this server.
public class ScrapingServiceImpl implements ScrapingService {
	private final Logger logger = LoggerFactory.getLogger(getClass());

	private String basePath = "https://mobileproxy.ehealth.gov.au";
	private String url = "/sps/hub/ehr.sso";
	private String nextReferer = "/LoginServices/source/Login.jsp?finalURL=http%3A%2F%2Flogin.australia.gwy%2FLoginServices%2FAuthenticate.do";

	public ScrapingServiceImpl() {
	}

	public LoginInteraction authenticate(String user, String password) throws ScrapingException {
		LoginInteraction interaction = new LoginInteraction();

		try {
			System.setProperty("https.protocols", "TLSv1,SSLv3,SSLv2Hello");
			Connection conn = Jsoup.connect(this.basePath + this.url).userAgent("Mozilla/5.0");
			Document doc = conn.get();
			String title = doc.title();
			logger.debug("title : " + title);
			String action = getAction(doc);
			interaction.setCookies(conn.response().cookies());

			if (action != null && action.length() > 0) {
				// need http protocol
				conn = Jsoup.connect(this.basePath + action).data("userId", user).data("credential", password)
						.data("credentialType", "20").data("logButton", "Continue").userAgent("Mozilla/5.0")
						.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
						.header("Accept-Encoding", "gzip,deflate,sdch").header("Accept-Language", "en-US,en;q=0.8")
						.header("Content-Type", "application/x-www-form-urlencoded");
				doc = conn.cookies(interaction.getCookies()).post();

				// get page title
				title = doc.title();
				logger.debug("title : " + title);

				if (title != null && title.toLowerCase().startsWith("access denied")) {
					String error = getError(doc);
					throw new ScrapingException(error);
				}

				interaction.setNextAction(getAction(doc));

				// get all links
				Elements secretQuestions = doc.select("label.fieldLabelQuestion");
				for (Element sq : secretQuestions) {
					interaction.setSecretQuestion(sq.text());
					break;
				}
			}
			return interaction;
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
	}

	public String authenticateSQ(LoginInteraction interaction) throws ScrapingException {
		try {
			System.setProperty("https.protocols", "TLSv1,SSLv3,SSLv2Hello");
			Connection conn = Jsoup.connect(this.basePath + interaction.getNextAction())
					.data("credential", interaction.getSecretAnswer()).data("credentialType", "25").userAgent("Mozilla/5.0")
					.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8")
					.header("Accept-Encoding", "gzip,deflate,sdch").header("Accept-Language", "en-US,en;q=0.8")
					.header("Content-Type", "application/x-www-form-urlencoded");
			conn.referrer(this.basePath + this.nextReferer);
			Document doc = conn.cookies(interaction.getCookies()).post();

			String title = doc.title();
			logger.debug("title : " + title);

			Elements samls = doc.getElementsByAttributeValue("name", "SAMLResponse");
			String saml = null;
			for (Element s : samls) {
				logger.trace("SAML: " + saml);
				saml = s.attr("value");
			}

			if (saml == null || saml.length() == 0) {
				throw new ScrapingException("Invalid SAML");
			}

			return saml;
		} catch (IOException e) {
			throw new ScrapingException(e);
		}
	}

	private String getAction(Document doc) {
		String action = null;
		Elements actions = doc.select("form");
		for (Element sq : actions) {
			logger.debug("action : " + sq.attr("action"));
			action = sq.attr("action");
			break;
		}
		return action;
	}

	private String getError(Document doc) {
		Elements error = doc.select("div.msg-error");
		Elements desc = error.select("li");
		for (Element er : desc) {
			return er.html();
		}
		return "";
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public void setNextReferer(String nextReferer) {
		this.nextReferer = nextReferer;
	}

}
