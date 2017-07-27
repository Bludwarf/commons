package fr.bludwarf.commons;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringBuilderTest
{

	@Test
	public void testIndent()
	{
		final StringBuilder sb = new StringBuilder();
		sb.append("fn(ok) {").newLine()
		.indent().append("return;").newLine()
		.unindent().append("}");
		final String newLine = StringBuilder.getNewLine();
		assertEquals("fn(ok) {" + newLine +"\treturn;" + newLine +"}", sb.toString());
	}

}
