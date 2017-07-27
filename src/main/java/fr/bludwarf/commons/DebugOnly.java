package fr.bludwarf.commons;

import org.apache.commons.configuration.ConfigurationException;

public class DebugOnly extends PropertiesConfiguration
{

	public DebugOnly(String defaultPropertiesFilename)
			throws ConfigurationException
	{
		super(defaultPropertiesFilename);
	}

	@Override
	protected String getUserPropertiesFolder(String userHome)
	{
		return userHome + "/debugOnly";
	}
	
	public static void main(final String[] args) throws ConfigurationException
	{
		final DebugOnly d = new DebugOnly("properties/fr-bludwarf-commons.properties");
		d.getBoolean("includedKey");
	}

}
