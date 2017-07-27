package fr.bludwarf.commons.exceptions;

public class LoadConfigurationException extends RuntimeException
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8043150458854956553L;

	public LoadConfigurationException()
	{}

	public LoadConfigurationException(String message)
	{
		super(message);
	}

	public LoadConfigurationException(Throwable cause)
	{
		super(cause);
	}

	public LoadConfigurationException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
