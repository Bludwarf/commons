package fr.bludwarf.commons.xml;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import fr.bludwarf.commons.mocks.Foo;
import fr.bludwarf.commons.mocks.FooRepository;
import fr.bludwarf.commons.mocks.FooXML;

public class XMLRepositoryTest
{

	@Test
	public void testLoad() throws Exception
	{
		final FooRepository repo = new FooRepository();
		final Foo obj = repo.load();
		assertEquals("TheName éì", obj.getName());
		assertEquals(123456789, obj.getValue());
	}

}
