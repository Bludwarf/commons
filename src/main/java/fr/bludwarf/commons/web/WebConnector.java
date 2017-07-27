package fr.bludwarf.commons.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

public abstract class WebConnector
{

	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(WebConnector.class);
	public static final int TIMEOUT = 5000;
	public static final String ENCODING_ISO = "ISO-8859-1";
	public static final String ENCODING_UTF = "UTF-8";
	
	private static WebConnectorCookieHandler COOKIE_HANDLER;
	
	private Queue<URLConnection> _openedConnections = new ArrayDeque<URLConnection>();
	private List<URL> _faultyGets = new ArrayList<URL>();

//	/**
//	 * @param url
//	 * @return
//	 * @throws IOException
//	 */
//	private static InputStream openStreamURL(final URL url) throws IOException
//	{
//		return openStreamURL(url, null);
//	}

	/**
	 * @param url
	 * @param cookies 
	 * @param getEncoding(cookie) TODO
	 * @return
	 * @throws IOException
	 */
	private InputStream openStreamURL(final URL url, Map<String, String> cookies) throws IOException
	{
		final HttpURLConnection con = (HttpURLConnection)getConnection(url, cookies);
		InputStream is = con.getInputStream();
		
		if (con.getResponseCode() != 200) {
			throw new RuntimeException("Le serveur a répondu par un HTTP "+con.getResponseCode());
		}
		
		return is;
	}

//	/**
//	 * @param con
//	 */
//	private void setCookies(final URLConnection con)
//	{
//		final Map<String, String> cookies = getCookies();
//    	final List<String> setCookies = con.getHeaderFields().get("Set-Cookie");
//    	for (final String setCookie : setCookies)
//    	{
//    		if (setCookie.contains(COOKIE_SEP))
//    		{
//    			final String key = StringUtils.substringBefore(setCookie, COOKIE_SEP);
//    			final String value = StringUtils.substringAfter(setCookie, COOKIE_SEP);
//				cookies.put(key, value);
//    		}
//    	}
//	}

	/**
	 * @param url
	 * @param cookies 
	 * @return
	 * @throws IOException
	 */
	private URLConnection getConnection(final URL url, Map<String, String> cookies) throws IOException
	{

		if (LOG.isDebugEnabled()) LOG.debug(String.format("getConnection(%s)", url));
		
		// Libération des connexions
		final int nbMax = getMaxConnections();
		while (nbMax > 0 && _openedConnections.size() >= nbMax)
		{
			_openedConnections.poll();
		}
		
//		if (_openedConnections.isEmpty())
//		{
			final URLConnection con = url.openConnection();
	    	con.setConnectTimeout(TIMEOUT);
	    	
	    	final WebConnectorCookieHandler ch = getCookieHandler();
	    	
	    	// Cookies fixes
	    	if (cookies != null) {
	    		ch.getCookies(url).putAll(cookies);
	    	}
	    	
	    	ch.addCookies(con);
	    	
			_openedConnections.add(con);
//		}
//		else
//		{
//			con = _openedConnections.get(0);
//		}
		
		return con;
	}

	/**
	 * @param con
	 * @return 
	 */
	public WebConnectorCookieHandler getCookieHandler()
	{
		if (COOKIE_HANDLER == null)
		{
			COOKIE_HANDLER = new WebConnectorCookieHandler();
		}
		return COOKIE_HANDLER;
	}

//	public Map<String, String> getCookies()
//	{
//		if (_cookies == null)
//		{
//			_cookies = new HashMap<String, String>();
//		}
//		return _cookies;
//	}

	public String get(final URL url) throws IOException
	{
		return get(url, false);
	}

	private String get(final URL url, final boolean retry) throws IOException
    {
		return get(url, retry, null);
    }

	public String get(final URL url, final Map<String, String> cookies) throws IOException
    {
		return get(url, false, cookies);
    }

	private String get(final URL url, final boolean retry, final Map<String, String> cookies) throws IOException
    {
		
		if (_faultyGets.contains(url))
		{
			throw new IOException(String.format(
				"L'URL %s a déjà retourné une erreur alors on ne la retente pas",
				url));
		}
		
		BufferedReader buf = null;
		InputStreamReader is = null;
		
		final char[] buffer = new char[4096];
        
		try
		{
	        try
			{
	            is = new InputStreamReader(openStreamURL(url, cookies), getEncoding());
				buf = new BufferedReader(is);
			}
			catch (final UnknownHostException e)
			{
				throw new UnknownHostException("Impossible de joindre l'URL " + url);
			}
			catch (IOException e)
			{
				// En cas d'erreur java.io.IOException: Server returned HTTP response code: 500[...]
				// demander à l'utilisateur le cookie ASP après l'avoir copié depuis Chrome
				
				final String msg500 = "Server returned HTTP response code: 500 ";
				if (!retry && e.getMessage().startsWith(msg500))
				{
					LOG.warn(String.format("On a reçu un HTTP 500, retry sur \"%s\"...", url), e);
					return get(url, true);
				}
				else
				{
					throw new IOException("Erreur 500 après un retry", e);
				}
			}
	        
	        StringBuilder sb = new StringBuilder();
	        int pos = -1;
	        while ((pos = buf.read(buffer)) != -1)
	        {
	        	sb.append(buffer, 0, pos);
	        }
	        
	        return sb.toString();
		}
		
		catch (final IOException e)
		{
        	_faultyGets.add(url);
        	throw e;
		}
		
        finally
        {
        	if (is != null)
        	{
        		is.close();
        	}
        	if (buf != null)
        	{
        		buf.close();
        	}
        }
    }

	/**
	 * @param encoding
	 * @return
	 */
	public String getEncoding()
	{
		return ENCODING_ISO;
	}
	
	public int getMaxConnections()
	{
		return 0;
	}
	
}
