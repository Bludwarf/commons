package fr.bludwarf.commons.mocks;

import org.simpleframework.xml.Element;
import org.simpleframework.xml.Root;

import fr.bludwarf.commons.xml.ElementXML;

@Root(name="foo")
public class FooXML extends ElementXML<Foo>
{
	
	public FooXML()
	{
		super();
	}

	@Element
	private String name;
	
	@Element
	private int value;

	@Override
	public Foo toObject() throws Exception
	{
		final Foo obj = new Foo();
		obj.setName(name);
		obj.setValue(value);
		return obj;
	}

	@Override
	public void fromObject(Foo obj) throws Exception
	{
		name = obj.getName();
		value = obj.getValue();
	}

}
