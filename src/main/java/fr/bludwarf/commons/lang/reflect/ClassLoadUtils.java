package fr.bludwarf.commons.lang.reflect;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import fr.bludwarf.commons.io.FileUtils;
import fr.bludwarf.commons.lang.JARUtils;

// FIXME : à tester
/**
 * @author MLAVIGNE
 * @since 11 févr. 2014
 */
public class ClassLoadUtils
{
    
    /** Log (org.apache.log4j) */
    protected static Logger LOG = Logger.getLogger(ClassLoadUtils.class);
    
    /**
     * Takes a given url and creates a list which contains 
     * all children of the given url. 
     * (Works with Files and JARs).
     * 
     * @author http://andreas.haufler.info/2012/01/iterating-over-all-classes-with.html
     */
    public static List<String> getChildren(URL url)
    {
        List<String> result = new ArrayList<String>();
        
        if (url == null)
        {
            LOG.warn("URL null");
            return result;
        }
        
        if ("file".equals(url.getProtocol()))
        {
            File file = new File(url.getPath());
            if (!file.isDirectory())
            {
                file = file.getParentFile();
            }
            addFiles(file, result, file);
        }
        else if ("jar".equals(url.getProtocol()))
        {
            try
            {
                result.addAll(JARUtils.list(url));
            }
            catch (IOException e)
            {
                LOG.warn(e);
            }
        }
        return result;
    }
    
    /**
     * Collects all children of the given file into the given 
     * result list. The resulting string is the relative path
     * from the given reference.
     * 
     * @author http://andreas.haufler.info/2012/01/iterating-over-all-classes-with.html
     */
    private static void addFiles(File file, List<String> result, File reference)
    {
        if (!file.exists() || !file.isDirectory())
        {
            return;
        }
        for (File child : file.listFiles())
        {
            if (child.isDirectory())
            {
                addFiles(child, result, reference);
            }
            else
            {
                String path = null;
                while (child != null && !child.equals(reference))
                {
                    if (path != null)
                    {
                        path = child.getName() + "/" + path;
                    }
                    else
                    {
                        path = child.getName();
                    }
                    child = child.getParentFile();
                }
                result.add(path);
            }
        }
    }

    /**
     * @param packageName nom du package avec "/" ou "." (exemple: "com.bytel.exs")
     * @return
     */
    public static List<String> getChildren(String packageName)
    {
        return getChildren(
            FileUtils.getResource(packageName.replace(".", "/")));
    }

    /**
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     * 
     * @see {@link #getChildren(String)}
     */
    public static List<Class<?>> getClasses(String packageName) throws ClassNotFoundException
    {
        return getClasses(packageName, Object.class);
    }

    /**
     * @param packageName
     * @return
     * @throws ClassNotFoundException
     * 
     * @see {@link #getChildren(String)}
     */
    public static <T> List<Class<? extends T>> getClasses(String packageName, final Class<T> superClass) throws ClassNotFoundException
    {
        final List<Class<? extends T>> classes = new ArrayList<Class<? extends T>>();
        final List<String> errors = new ArrayList<String>();
        
        final List<String> classNames = getChildren(packageName);
        for (final String className : classNames)
        {
            try
            {
                final Class<?> clazz = getClassFromPackageName(packageName, className);
                
                try
                {
                    final Class<? extends T> childClass = getChildClass(clazz, superClass);
                    classes.add(childClass);
                }
                catch (final ClassCastException e)
                {
                    LOG.warn(String.format(
                        "On ajoute pas la classe %s du package %s car elle n'hérite pas de %s",
                        clazz.getSimpleName(),
                        packageName,
                        superClass.getName()));
                }
            }
            catch (ClassNotFoundException e)
            {
                errors.add(e.getMessage());
                LOG.error(e);
            }
        }
        
        if (!errors.isEmpty())
        {
            throw new ClassNotFoundException(String.format(
                "%s classe(s) sur %s n'ont pas pu être trouvées (voir log)",
                errors.size(),
                classNames.size())); 
        }
        
        return classes;
    }

    /**
     * @param <T>
     * @param clazz
     * @param superClass
     * @return
     * @throws ClassCastException
     */
    @SuppressWarnings("unchecked")
    public static <T> Class<? extends T> getChildClass(final Class<?> clazz, final Class<T> superClass) throws ClassCastException
    {
        if (clazz == superClass || !superClass.isAssignableFrom(clazz))
        {
            throw new ClassCastException(String.format(
                "La classe \"%s\" doit hériter de %s",
                clazz.getName(),
                superClass.getName()));
        }
        
        final Class<? extends T> childClass = (Class<? extends T>) clazz;
        return childClass;
    }

    /**
     * @param className
     * @return
     * @throws ClassNotFoundException
     */
    public static Class<?> getClassFromPackageName(final String packageName, final String className) throws ClassNotFoundException
    {
        String name = StringUtils.stripEnd(packageName, "/") + "/" +
            StringUtils.stripEnd(className, ".class");
        name = name.replace("/", ".");
        return Class.forName(name);
    }
}
