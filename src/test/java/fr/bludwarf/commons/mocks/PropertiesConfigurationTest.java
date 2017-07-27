package fr.bludwarf.commons.mocks;

import static org.junit.Assert.*;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.Test;

import fr.bludwarf.commons.PropertiesForJUnit;

public class PropertiesConfigurationTest
{

	@Test
	public void testGetStringString() throws Exception
	{
		assertEquals("testValue_default", PropertiesForJUnit.getInstance().getString("testKey"));
	}

	@Test
	public void testGetPattern() throws Exception
	{
		final Pattern pattern = PropertiesForJUnit.getInstance().getPattern("testPattern");
		Matcher matcher = pattern.matcher("0678941912");
		assertTrue(matcher.matches());

		matcher = pattern.matcher("678941912");
		assertFalse(matcher.matches());
	}

	@Test
	public void testTestOnlyKey() throws Exception
	{
		assertTrue(PropertiesForJUnit.getInstance().getBoolean("testOnlyKey"));
	}

	@Test
	public void testGetStringAndFormat() throws Exception
	{
		assertEquals("test.xml", PropertiesForJUnit.getInstance().getStringAndFormat("formatString", "xml", "test"));
	}

	@Test
	public void testInclude() throws Exception
	{
		assertTrue(PropertiesForJUnit.getInstance().getBoolean("includedKey"));
	}
	
	@AfterClass
	public static void tearDownAll() throws Exception
	{
		final File userFile = PropertiesForJUnit.getInstance().getUserPropertiesFile(PropertiesForJUnit.FILE);
		// En test le userFile ne doit jamais être créé
		assertFalse(userFile.exists());
	}

	// TODO : comment recharger directement le fichier default quand le fichier user n'existe plus
//	@Test
//	public void testSaveForUser() throws Exception
//	{
//		final PropertiesForJUnit props = PropertiesForJUnit.getInstance();
//		final String defaultValue = props.getString("testKey");
//		final String userValue = "userValue";
//		props.setProperty("testKey", userValue);
//		props.save();
//		
//		assertEquals(userValue, props.getString("testKey"));
//		
//		// Suppr
//		assertTrue("Impossible de suppr les props utilisateur", props.getUserPropertiesFile().delete());
//		props.reload();
//		assertEquals(defaultValue, props.getString("testKey"));
//	}

}
