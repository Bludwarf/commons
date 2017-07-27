package fr.bludwarf.commons.web;

import java.io.IOException;
import java.net.CookieHandler;
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.bludwarf.commons.StringUtils;

/**
 * Gestion basique des Cookies.
 * 
 * <ul>
 * <li>Gestion par serveur
 * <li>Aucune gestion des temps de rétention
 * <li>Aucune gestion du "path"
 * </ul>
 * 
 * @author MLAVIGNE
 */
public class WebConnectorCookieHandler extends CookieHandler
{

	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WebConnectorCookieHandler.class);
	public static final String COOKIE_SEP = "=";
	public static final String HEADER_SEP = ";";
	
	/** URL.getHost() -> (key, value)* */
	private Map<String, Map<String, String>> _cookies;

	public WebConnectorCookieHandler()
	{
		super();
	}

	@Override
	public Map<String, List<String>> get(URI uri,
			Map<String, List<String>> requestHeaders) throws IOException
	{
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void put(URI uri, Map<String, List<String>> responseHeaders)
			throws IOException
	{
		// TODO Auto-generated method stub

	}

	/**
	 * Peut ouvrir la connexion si les cookies pour cette URL ne sont pas connus.
	 * @param con connexion non ouverte
	 * @return
	 */
	public URLConnection addCookies(final URLConnection con)
	{
		final URL url = con.getURL();
		Map<String, String> cookies = getCookies(url);
		
		// Cookies vide => on doit se connecter pour récupérer les bons cookies
		if (cookies.isEmpty())
		{
			if (LOG.isDebugEnabled()) LOG.debug(String.format("Pas de cookies stockés pour l'URL \"%s\" => on se connecte pour les récupérer", url));
			final Map<String, String> cookiesRecus = getCookies(con);
			setCookies(cookiesRecus, url);
			if (LOG.isDebugEnabled()) LOG.debug(String.format("Cookies reçus : ", cookiesRecus));
		}
		
		// Cookies pour cette URL déjà connus => on les ajoute avant de se connecter 
		else
		{
			if (LOG.isDebugEnabled()) LOG.debug(String.format("Cookies stockés pour l'URL \"%s\" : ", url, cookies));
			for (final String key : cookies.keySet())
	    	{
	    		con.addRequestProperty("Cookie", getCookieHeader(key, cookies));
	    	}
		}		
		
		return con;
	}

	private void setCookies(Map<String, String> cookiesRecus, final URL url)
	{
		getCookies(url).clear();
		getCookies(url).putAll(cookiesRecus);
	}

	/**
	 * <b>Ouvre la connexion</b>
	 * @param con
	 * @return Cookies reçus de cette connexion
	 */
	private static Map<String, String> getCookies(URLConnection con)
	{		
    	// Cookies reçus
		final Map<String, String> cookies = new HashMap<String, String>();
    	final List<String> setCookies = con.getHeaderFields().get("Set-Cookie");
    	if (setCookies != null) {
	    	for (final String setCookie : setCookies)
	    	{
	    		if (setCookie.contains(COOKIE_SEP))
	    		{
	    			final String key = StringUtils.substringBefore(setCookie, COOKIE_SEP);
	    			final String values = StringUtils.substringAfter(setCookie, COOKIE_SEP);
	    			final String value = StringUtils.substringBefore(values, HEADER_SEP).trim();
					cookies.put(key, value);
	    		}
	    	}
    	}
    	
    	return cookies;
	}

	/**
	 * Gestion de base : uniquement clé=valeur sans prise en compte du temps de vie
	 * @param url
	 * @return
	 */
	protected Map<String, String> getCookies(URL url)
	{
		if (_cookies == null)
		{
			_cookies = new HashMap<String, Map<String,String>>();
		}

//		final String key = StringUtils.substringBefore(url.toString(), "?");
		final String key = url.getHost();
		Map<String,String> cookiesForURL = _cookies.get(key);
		if (cookiesForURL == null)
		{
			cookiesForURL = new HashMap<String, String>();
			_cookies.put(key, cookiesForURL);
		}
		
    	return cookiesForURL;
	}

	private static String getCookieHeader(final String key, final Map<String, String> cookies)
	{
		return String.format("%s%s%s", key, COOKIE_SEP, cookies.get(key));
	}

}
