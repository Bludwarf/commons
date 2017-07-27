package fr.bludwarf.commons;

import org.apache.commons.configuration.ConfigurationException;

public class PropertiesForJUnit extends PropertiesConfiguration
{
	
	public static final String FILE = "properties/fr-bludwarf-commons.properties";
	/** instance */
	private static PropertiesForJUnit _instance = null;

	private PropertiesForJUnit() throws ConfigurationException
	{
		super(FILE);
	}

	/**
	 * @return l'instance de PropertiesForJUnit
	 * @throws ConfigurationException 
	 */
	public static final synchronized PropertiesForJUnit getInstance() throws ConfigurationException
	{
		if (_instance == null)
		{
			_instance = new PropertiesForJUnit();
		}
		return _instance;
	}

	@Override
	protected String getUserPropertiesFolder(final String userHome)
	{
		return userHome + "/tests/fr-bludwarf-commons";
	}

}
