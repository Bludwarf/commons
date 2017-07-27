package fr.bludwarf.commons.lang;

import java.io.File;
import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.bludwarf.commons.io.FileUtils;

/**
 * @author MLAVIGNE
 * @since 11 févr. 2014
 */
public class JARUtils
{
    
    public static final String JAR_INTERNAL_PATH = "!/";
    /** Log (org.apache.log4j) */
    protected static Logger LOG = Logger.getLogger(JARUtils.class);
    
    public static URL getResource(final File jar, final String path) throws MalformedURLException
    {
        return getResource(
            getURL(jar), path);         
    }
    
    public static URL getResource(final URL jarURL, final String path) throws MalformedURLException
    {
        if ("jar".equals(jarURL.getProtocol()))
        {
            return jarURL;
        }
        else
        {
            return new URL("jar:" + jarURL + "!/" + path);
        }
    }
    
    public static URL getResource(final String jarPath, final String path) throws MalformedURLException
    {
        final URL jarURL = getURL(jarPath);
        return new URL("jar:" + jarURL + "!/" + path);
    }
    
    public static List<String> list(final URL folderInJAR) throws IOException
    {
        List<String> resources = new ArrayList<String>();

        // Préfixe des entrées dans le JAR correspondant à tous les fils du dossier 
        final String folder = JARUtils.getEntryPath(folderInJAR);
        final String prefix = folder + "/";
        
        JarFile jar = ((JarURLConnection) folderInJAR.openConnection()).getJarFile();
        Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements())
        {
            JarEntry entry = e.nextElement();
            final String name = entry.getName();
            final String relativeName = StringUtils.substringAfter(name, prefix);
            if (StringUtils.isNotEmpty(relativeName))
            {
//                // FIXME : si folder contient "/" ?
//                resources.add(folder + "/" + relativeName);
                resources.add(relativeName);
            }
        }
        
        return resources;
    }
    
    public static List<String> list(final URL jarURL, final String folder) throws IOException
    {
        List<String> resources = new ArrayList<String>();
        final URL folderURL = getResource(jarURL, folder);

        // Préfixe des entrées dans le JAR correspondant à tous les fils du dossier 
        final String prefix = folder + "/";
        
        JarFile jar = ((JarURLConnection) folderURL.openConnection()).getJarFile();
        Enumeration<JarEntry> e = jar.entries();
        while (e.hasMoreElements())
        {
            JarEntry entry = e.nextElement();
            final String name = entry.getName();
            final String relativeName = StringUtils.substringAfter(name, prefix);
            if (StringUtils.isNotEmpty(relativeName))
            {
                // FIXME : si folder contient "/" ?
                resources.add(folder + "/" + relativeName);
            }
        }
        
        return resources;
    }
    
    public static List<String> list(final File jar, final String folder) throws IOException
    {
        return list(
            getURL(jar), folder);
    }
    
    /**
     * @param jarPath
     * @param folder
     * @return la liste des chemins relatifs au JAR dans le dossier spécifié
     * @throws IOException
     */
    public static List<String> list(final String jarPath, final String folder) throws IOException
    {
        return list(
            getURL(jarPath), folder);
    }
    
    public static URL getURL(final String jarPath)
    {
        return FileUtils.getResource(jarPath);
    }
    
    public static URL getURL(final File jar) throws MalformedURLException
    {
        return jar.toURI().toURL();
    }

    /**
     * @param url
     * @return le chemin à l'intérieur du JAR désigné par l'URL
     */
    public static String getEntryPath(URL url)
    {
        return StringUtils.substringAfter(url.getPath(), JAR_INTERNAL_PATH);
    }
}
