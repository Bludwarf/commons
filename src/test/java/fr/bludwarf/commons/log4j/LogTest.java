package fr.bludwarf.commons.log4j;

import static org.junit.Assert.*;

import org.junit.Test;

public class LogTest
{
	
	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(LogTest.class);

	@Test
	public void test()
	{
		LOG.warn("WARN MESSAGE");
	}

}
