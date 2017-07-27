package fr.bludwarf.commons.web;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.client.utils.URIBuilder;

import fr.bludwarf.commons.StringBuilder;

public class URLUtils
{

	public static final String ENCODED_SPACE = "+";
	public static final String MAIL_ENCODED_SPACE = "%20";

	/**
	 * @see #communicator(List, Map)
	 */
	public static String communicator(final List<String> mails)
	{
		return communicator(mails, null);
	}
	
	/**
	 * @param mails
	 * @param headerFields <code>null</code> ou vide si aucun paramètre
	 * @return
	 * 
	 * @see #mail(List, Map)
	 */
	public static String communicator(final List<String> mails, final Map<String, String> headerFields)
	{
		final StringBuilder sb = new StringBuilder();

		sb.append("im:").append(mails, "", "<sip:{0}>");
		
		addHeaderFields(headerFields, sb, MAIL_ENCODED_SPACE);

		return sb.toString();
	}
	
	/**
	 * @see #mail(List, Map)
	 */
	public static String mail(final List<String> mails)
	{
		return mail(mails, (Map<String, String>) null);
	}
	
	/**
	 * Espaces encodés en "%20".
	 * @param mails
	 * @param headerFields <code>null</code> ou vide si aucun paramètre
	 * @param body texte (le code HTML sera affiché brut = code source)
	 * 
	 * @return
	 * 
	 * @see http://webdesign.about.com/od/beginningtutorials/a/aabegin100299.htm
	 */
	public static String mail(final List<String> mails, final String subject, final String body)
	{
		final Map<String, String> headerFields = new HashMap<String, String>(2);
		headerFields.put("subject", subject);
		headerFields.put("body", body);

		return mail(mails, headerFields);
	}
	
	/**
	 * Espaces encodés en "%20".
	 * @param mails
	 * 
	 * @return
	 * 
	 * @see http://webdesign.about.com/od/beginningtutorials/a/aabegin100299.htm
	 */
	public static String mail(final List<String> mails, final String subject)
	{
		final Map<String, String> headerFields = new HashMap<String, String>(1);
		headerFields.put("subject", subject);

		return mail(mails, headerFields);
	}
	
	/**
	 * Espaces encodés en "%20".
	 * @param mails
	 * @param headerFields <code>null</code> ou vide si aucun paramètre
	 * @return
	 * 
	 * @see http://webdesign.about.com/od/beginningtutorials/a/aabegin100299.htm
	 */
	public static String mail(final List<String> mails, final Map<String, String> headerFields)
	{
		final StringBuilder sb = new StringBuilder();

		sb.append("mailto:").append(mails, ";");
		
		addHeaderFields(headerFields, sb, MAIL_ENCODED_SPACE);

		return sb.toString();
	}

	/**
	 * @param headerFields
	 * @param sb
	 */
	protected static void addHeaderFields(
			final Map<String, String> headerFields, final StringBuilder sb)
	{
		addHeaderFields(headerFields, sb, null);
	}

	/**
	 * @param headerFields
	 * @param sb
	 * @param encodedSpace TODO
	 */
	protected static void addHeaderFields(
			final Map<String, String> headerFields, final StringBuilder sb, String encodedSpace)
	{
		if (headerFields != null && !headerFields.isEmpty())
		{
			final URIBuilder b = new URIBuilder();
			for (final String hf : headerFields.keySet())
			{
				String value = headerFields.get(hf);
				b.addParameter(hf, value);
			}
			
			// Pour Outlook par exemple, les "+" ne sont pas interprétés comme des espaces
			String params = b.toString();
			if (encodedSpace != null)
			{
				params = params.replace("+", MAIL_ENCODED_SPACE);
			}
			
			sb.append(params);
		}
	}
}
