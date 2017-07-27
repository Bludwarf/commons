package fr.bludwarf.commons.lang;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.junit.Test;

import fr.bludwarf.commons.io.FileUtils;

public class JARUtilsTest
{
    
    public static String JAR = "jar/jar-test.jar";
    public static File JAR_FILE = new File(JAR);
    
    @Test
    public void testGetFileInJar() throws Exception
    {
        final URL url = JARUtils.getResource(JAR, "com/bytel/exs/sources/generators");
        final String urlStr = url.toString();
		assertTrue(urlStr, urlStr.startsWith("jar:file:/"));
        assertTrue(urlStr, urlStr.contains(JAR));
        assertTrue(urlStr, urlStr.endsWith("!/com/bytel/exs/sources/generators"));
    }
    
    @Test
    public void testGetFileInJarString() throws Exception
    {
        final URL url = JARUtils.getResource(JAR, "META-INF/maven/com.bytel.exs/exs-sources/pom.xml");
//        System.out.println(url);
        final String pomXML = FileUtils.readInputStreamToString(url.openStream());
//        System.out.println(pomXML);
        assertTrue(pomXML, pomXML.startsWith("<project xmlns=\"http:"));
    }
    
    @Test
    public void testList() throws Exception
    {
        final List<String> pathes = JARUtils.list(JAR, "com/bytel/exs/sources/generators");
        assertEquals("com/bytel/exs/sources/generators/Sauvegarde.class", pathes.get(0));
        assertEquals("com/bytel/exs/sources/generators/SauvegardeDaily.class", pathes.get(1));
        assertEquals("com/bytel/exs/sources/generators/ServicesListConf.class", pathes.get(2));
    }
    
}
