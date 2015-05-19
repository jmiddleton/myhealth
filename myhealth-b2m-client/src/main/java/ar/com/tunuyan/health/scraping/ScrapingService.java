package ar.com.tunuyan.health.scraping;

import ar.com.tunuyan.health.model.LoginInteraction;

public interface ScrapingService {

	LoginInteraction authenticate(String user, String password) throws ScrapingException;

	String authenticateSQ(LoginInteraction interaction) throws ScrapingException;

	String getUrl();

	String getBasePath();

}