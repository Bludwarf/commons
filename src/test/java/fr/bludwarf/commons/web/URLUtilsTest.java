package fr.bludwarf.commons.web;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

import fr.bludwarf.commons.io.FileUtils;

public class URLUtilsTest
{

	@Test
	public void testLinkCommunicator()
	{
		List<String> mails = new ArrayList<String>();
		mails.add("babacar.ndiaye@capgemini.com");
		
		// 1 personne		
		assertEquals("im:<sip:babacar.ndiaye@capgemini.com>", URLUtils.communicator(mails));
		
		// 2 personnes
		mails.add("youssef.izoughar@capgemini.com");
		assertEquals("im:<sip:babacar.ndiaye@capgemini.com><sip:youssef.izoughar@capgemini.com>", URLUtils.communicator(mails));
		
		// 2 personnes + sujet
		Map<String, String> params = new HashMap<String, String>();
		params.put("subject", "Soccer des yéyés");
		assertEquals("im:<sip:babacar.ndiaye@capgemini.com><sip:youssef.izoughar@capgemini.com>?subject=Soccer%20des%20y%C3%A9y%C3%A9s", URLUtils.communicator(mails, params));
	}

	@Test
	public void testLinkMail()
	{
		List<String> mails = new ArrayList<String>();
		mails.add("babacar.ndiaye@capgemini.com");
		
		// 1 personne		
		assertEquals("mailto:babacar.ndiaye@capgemini.com", URLUtils.mail(mails));
		
		// 2 personnes
		mails.add("youssef.izoughar@capgemini.com");
		assertEquals("mailto:babacar.ndiaye@capgemini.com;youssef.izoughar@capgemini.com", URLUtils.mail(mails));
		
		// 2 personnes + sujet
		Map<String, String> params = new HashMap<String, String>();
		params.put("subject", "Soccer des yéyés");
		assertEquals("mailto:babacar.ndiaye@capgemini.com;youssef.izoughar@capgemini.com?subject=Soccer%20des%20y%C3%A9y%C3%A9s", URLUtils.mail(mails, params));
	}

	@Test
	public void testLinkMailHTML() throws Exception
	{
		List<String> mails = new ArrayList<String>();
		mails.add("babacar.ndiaye@capgemini.com");
		mails.add("youssef.izoughar@capgemini.com");
		
		final String body = FileUtils.readResourceToString("html/mail.htm", "UTF-8");
		
		assertEquals("mailto:babacar.ndiaye@capgemini.com;youssef.izoughar@capgemini.com?body=%3Chtml%3E%0D%0A%3Chead%3E%0D%0A%09%3Cmeta%20http-equiv%3D%22Content-Type%22%20content%3D%22text%2Fhtml%3B%20charset%3Dutf-8%22%2F%3E%0D%0A%3C%2Fhead%3E%0D%0A%3Cbody%3E%0D%0A%09%3Cp%3EH%C3%A9h%C3%A9%20%21%20Ceci%20est%20une%20page%20de%20%3Cb%3Etest%3C%2Fb%3E%3C%2Fp%3E%0D%0A%3C%2Fbody%3E%0D%0A%3C%2Fhtml%3E&subject=Soccer",
			URLUtils.mail(mails, "Soccer", body));
	}

}
