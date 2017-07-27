package fr.bludwarf.commons.mocks;

import java.io.File;

import fr.bludwarf.commons.xml.XMLRepository;

public class FooRepository extends XMLRepository<Foo, FooXML>
{

	public FooRepository()
	{
		super();
	}

	@Override
	public FooXML getXmlBinder()
	{
		return new FooXML();
	}

	@Override
	protected String getFile()
	{
//		return new File("src/test/resources/xml/foo.xml");
		return "src/test/resources/xml/foo.xml";
	}

}
