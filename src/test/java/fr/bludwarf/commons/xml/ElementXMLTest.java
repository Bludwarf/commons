package fr.bludwarf.commons.xml;

import static fr.bludwarf.commons.xml.ElementXML.load;
import static fr.bludwarf.commons.xml.ElementXML.save;
import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.Calendar;
import java.util.Date;

import org.junit.Test;

import fr.bludwarf.commons.mocks.Foo;
import fr.bludwarf.commons.mocks.FooXML;
import fr.bludwarf.commons.test.TestUtils;

public class ElementXMLTest
{

	@Test
	public void testFormatDateSDF()
	{
		final Calendar cal = getDate();
		assertEquals("2013-12-25T00:11:22+01:00", ElementXML.SDF.format(cal.getTime()));
	}

	@Test
	public void testFormatDate() throws Exception
	{
		final Calendar cal = getDate();
		assertEquals("2013-12-25T00:11:22+01:00", ElementXML.DATE_TRANSFORM.write(cal.getTime()));
	}

	@Test
	public void testFormatDateGMTSDF()
	{
		// TODO : testFormatDateGMT
//		final Calendar cal = getDate();
//		cal.setTimeZone(TimeZone.getTimeZone("GMT"));
//		assertEquals("2013-12-24T23:11:22Z", ElementXML.SDF.format(cal.getTime()));
	}

	@Test
	public void testParseDateSDF() throws Exception
	{
		final Date date = ElementXML.SDF.parse("2013-12-25T00:11:22+01:00");
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(2013, cal.get(Calendar.YEAR));
		assertEquals(11, cal.get(Calendar.MONTH));
		assertEquals(25, cal.get(Calendar.DATE));
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(11, cal.get(Calendar.MINUTE));
		assertEquals(22, cal.get(Calendar.SECOND));
	}

	@Test
	public void testParseDateGMTSDF() throws Exception
	{
		final Date date = ElementXML.SDF.parse("2013-12-25T00:11:22Z");
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(2013, cal.get(Calendar.YEAR));
		assertEquals(11, cal.get(Calendar.MONTH));
		assertEquals(25, cal.get(Calendar.DATE));
		assertEquals(1, cal.get(Calendar.HOUR_OF_DAY)); // GMT + 1
		assertEquals(11, cal.get(Calendar.MINUTE));
		assertEquals(22, cal.get(Calendar.SECOND));
	}

	@Test
	public void testParseDate() throws Exception
	{
		final Date date = ElementXML.DATE_TRANSFORM.read("2013-12-25T00:11:22+01:00");
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(2013, cal.get(Calendar.YEAR));
		assertEquals(11, cal.get(Calendar.MONTH));
		assertEquals(25, cal.get(Calendar.DATE));
		assertEquals(0, cal.get(Calendar.HOUR_OF_DAY));
		assertEquals(11, cal.get(Calendar.MINUTE));
		assertEquals(22, cal.get(Calendar.SECOND));
	}

	@Test
	public void testParseDateGMT() throws Exception
	{
		final Date date = ElementXML.DATE_TRANSFORM.read("2013-12-25T00:11:22Z");
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		assertEquals(2013, cal.get(Calendar.YEAR));
		assertEquals(11, cal.get(Calendar.MONTH));
		assertEquals(25, cal.get(Calendar.DATE));
		assertEquals(1, cal.get(Calendar.HOUR_OF_DAY)); // GMT + 1
		assertEquals(11, cal.get(Calendar.MINUTE));
		assertEquals(22, cal.get(Calendar.SECOND));
	}

	@Test
	public void testLoadFileClass() throws Exception
	{
		final Foo obj = ElementXML.load(new File("src/test/resources/xml/foo.xml"), FooXML.class);
		assertEquals("TheName éì", obj.getName());
		assertEquals(123456789, obj.getValue());
	}

	@Test
	public void testSaveObjFileClass() throws Exception
	{
		final File in = new File("src/test/resources/xml/foo.xml");
		final Foo obj = load(in, FooXML.class);

		final File out = new File("src/test/resources/xml/foo-out.xml");
		save(obj, out, FooXML.class);
		
		TestUtils.assertEqualsXML(in, out, true);
	}

	/**
	 * @return
	 */
	protected Calendar getDate()
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(2013, 11, 25, 0, 11, 22);
		cal.set(Calendar.MILLISECOND, 0);
		return cal;
	}

}
