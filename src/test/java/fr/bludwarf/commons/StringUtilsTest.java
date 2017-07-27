package fr.bludwarf.commons;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class StringUtilsTest
{

	@Test
	public void testStripMid()
	{
		assertEquals("cong...ions", 		StringUtils.abbreviateMiddle("congratulations", 11));
		assertEquals("congratulations", 	StringUtils.abbreviateMiddle("congratulations", 15));
		assertEquals("congra...tions", 		StringUtils.abbreviateMiddle("congratulations", 14));
	}

	@Test
	public void testJoinCollectionStringString()
	{
		List<String> list = new ArrayList<String>();
		list.add("ok");
		list.add("ko");
		
		assertEquals("[0=ok][1=ko]",		StringUtils.join(list, "", "[{1}={0}]"));
		assertEquals("[0=ok],[1=ko]",		StringUtils.join(list, ",", "[{1}={0}]"));
		assertEquals("[{0}=ok],[{1}=ko]",	StringUtils.join(list, ",", "['{'{1}'}'={0}]"));
	}

}
