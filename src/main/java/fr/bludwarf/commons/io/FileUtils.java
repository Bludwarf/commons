package fr.bludwarf.commons.io;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.Properties;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.ConfigurationUtils;
import org.apache.commons.configuration.FileSystem;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.NotImplementedException;
import org.apache.commons.lang.text.StrSubstitutor;

import fr.bludwarf.commons.StringUtils;

public class FileUtils extends org.apache.commons.io.FileUtils
{
    public static final String UNIX_LINE_BREAK = "\n";
    public static final String MAC_LINE_BREAK = "\r";
    public static final String WINDOWS_LINE_BREAK = MAC_LINE_BREAK + UNIX_LINE_BREAK;
    
    public static final File CURRENT_FOLDER = new File(".");
	private static File _tempDir = null;
	public static String UTF8 = StringUtils.UTF8;
    
    protected static final Pattern LINE_BREAK_DETECT = Pattern.compile("(\\r?\\n)|\\r");
    protected static final String UNIX_LINE_BREAK_PATTERN = "(?<!\\r)\\n";
    protected static final String MAC_LINE_BREAK_PATTERN = "\\r(?!\\n)";

    /**
     * @param name
     * @param dir c'est un dossier qu'on veut créer ?
     * @return
     * @throws IOException
     * @throws ConfigurationException
     */
    public static File createTempFile(final String name, final boolean dir) throws IOException,
        ConfigurationException
    {
        if (_tempDir == null)
        {
//            if (DPConfProperties.getInstance().containsKey("temp"))
//            {
//                _tempDir = new File(DPConfProperties.getInstance().getString("temp"), "exs");
//                deleteDirectory(_tempDir);
//                _tempDir.mkdirs();
//            }
//            else
//            {
                _tempDir = File.createTempFile("platformizer-", "-tmp").getParentFile();
//            }
            
            _tempDir.deleteOnExit();
        }

        File tempFile = new File(_tempDir, name);

        // On crée un dossier unique pour éviter les collisions entre les fichiers/dossiers (export.xml, ...)
        while (tempFile.exists())
        {
            final File uniqueFolder = new File(_tempDir, UUID.randomUUID().toString());
            uniqueFolder.deleteOnExit();
            tempFile = new File(uniqueFolder, name);
        }

        // Dossier parent temporaire + création du chemin
        tempFile.getParentFile().deleteOnExit();
        tempFile.getParentFile().mkdirs();

        // Fichier ?
        if (!dir)
        {
            tempFile.createNewFile();
        }
        else
        {
            tempFile.mkdir();
        }

        // Temporaire
        tempFile.deleteOnExit();

        return tempFile;
    }
    
    public static File createTempFile(final String name) throws IOException, ConfigurationException
    {
    	return createTempFile(name, false);
    }

    /**
     * @param dir c'est un dossier qu'on veut créer ?
     * @return
     * @throws IOException
     * @throws ConfigurationException
     */
    public static File createTempFile(final boolean dir) throws IOException,
        ConfigurationException
    {
        return createTempFile(UUID.randomUUID().toString(), dir);
    }
    
    public static String readInputStreamToString(InputStream is) throws IOException
    {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer);
        return writer.toString();
    }
    
    /**
     * @see #readInputStreamToString(InputStream)
     */
    public static String readInputStreamToString(InputStream is, final String encoding) throws IOException
    {
        StringWriter writer = new StringWriter();
        IOUtils.copy(is, writer, encoding);
        return writer.toString();
    }
	
	/* TODO :
	public static InputStream getInputStreamFromString(String string)
    {
        return new ByteArrayInputStream(string.getBytes());
    }
	*/
    

    

    /**
     * Charge un fichier en le cherchant dans le classpath.
     * 
     * <p>
     * Si le fichier provient d'un JAR chargé manuellement il faut alors implémenter une copie de getResourceAsStream dans une des classe de ce JAR.
     * Le code de cette copie est alors :
     * </p>
     * <pre>
     * public static InputStream getResourceAsStream(final String filename) throws IOException
     * {
     *     return JARPropertiesConfiguration.getResourceAsStream(filename);
     * }
     * </pre>
     * 
     * @param filename chemin vers le fichier, sans le chemin du dossier source.
     * <p>
     * Exemple utiliser simplement "supervision/serviceTemplatesFromDPConf.xsl"
     * pour récupérer le fichier "src/main/xsl/supervision/serviceTemplatesFromDPConf.xsl"
     * </p>
     * @return
     * @throws IOException impossible de charger la ressource 
     */
    public static InputStream getResourceAsStream(final String filename) throws IOException
    {
        final URL url = getResource(filename);
        final FileSystem fs = FileSystem.getDefaultFileSystem();
        
        InputStream is;
        try
        {
            is = fs.getInputStream(url);
        }
        catch (ConfigurationException e)
        {
            throw new IOException(String.format(
                "Impossible de charger la ressource %s depuis la classe %s",
                filename,
                FileUtils.class.getName()), e);
        }
        
        if (is == null)
        {
            throw new IOException(String.format(
                "Impossible de trouver la ressource %s chargée depuis la classe %s",
                filename,
                FileUtils.class.getName()));
        }

        return is;
    }
    
    /**
     * @param file créé automatiquement si non existant
     * @return
     * @throws IOException
     */
    public static OutputStream getOutputStream(final File file) throws IOException
    {
        if (!file.exists())
        {
            file.createNewFile();
        }
        return getResourceAsOutputStream(file.getPath());
    }

    public static OutputStream getResourceAsOutputStream(final String filename) throws IOException
    {        
        final URL url = getResource(filename);
        final FileSystem fs = FileSystem.getDefaultFileSystem();
        
        OutputStream st;
        try
        {
            st = fs.getOutputStream(url);
        }
        catch (ConfigurationException e)
        {
            throw new IOException(String.format(
                "Impossible de charger la ressource %s depuis la classe %s",
                filename,
                FileUtils.class.getName()), e);
        }
        
        if (st == null)
        {
            throw new IOException(String.format(
                "Impossible de trouver la ressource %s chargée depuis la classe %s",
                filename,
                FileUtils.class.getName()));
        }

        return st;
    }
    
    /**
     * @param filename chemin vers le fichier, sans le chemin du dossier source.
     * <p>
     * Exemple utiliser simplement "supervision/serviceTemplatesFromDPConf.xsl"
     * pour récupérer le fichier "src/main/xsl/supervision/serviceTemplatesFromDPConf.xsl"
     * </p>
     * @return
     * @throws ConfigurationException impossible de charger la ressource 
     */
    public static StreamSource getResourceAsStreamSource(final String filename) throws IOException
    {
        final InputStream is = getResourceAsStream(filename);

        // Stream Source
        final StreamSource src = new StreamSource(is);
        src.setSystemId(filename);

        return src;
    }
    
    // FIXME : à tester
    public static boolean resourceExists(final String filename)
    {
    	return getResource(filename) != null;
    }

    public static URL getResource(final String filename)
    {
        return ConfigurationUtils.locate(filename);
    }

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 * 
	 * @see {@link #readFileToString(File)}
	 */
	public static String readFileToString(final String filename) throws IOException
	{
		final URL resource = getResource(filename);
		final File file = new File(resource.getFile());
		return readFileToString(file);
	}

	/**
	 * @param filename
	 * @return
	 * @throws IOException
	 * 
	 * @see {@link #readInputStreamToString(InputStream)}
	 */
	public static String readResourceToString(final String resource) throws IOException
	{
		final InputStream input = getResourceAsStream(resource);
		return readInputStreamToString(input);
	}

	/**
	 * @see #readResourceToString(String)
	 */
	public static String readResourceToString(final String resource, final String charset) throws IOException
	{
		final InputStream input = getResourceAsStream(resource);
		return readInputStreamToString(input, charset);
	}

	public static void addBOM(File file) throws IOException
	{
		final byte[] bytes    = readFileToByteArray(file);
		final byte[] newBytes = new byte[bytes.length + 3];
		newBytes[0] = (byte) 0xEF;
		newBytes[1] = (byte) 0xBB;
		newBytes[2] = (byte) 0xBF;
		System.arraycopy(bytes, 0, newBytes, 3, bytes.length);
		writeByteArrayToFile(file, newBytes);
	}

	public static void addBOM(OutputStream os)
	{
		throw new NotImplementedException();
	}
	
	/**
	 * Encodage par défaut : UTF-8
	 * @param props propriétés à substituer dans le fichier de template (de la forme ${propriété})
	 * @param template fichier de template avec une 2e extension (par exemple ".template") à supprimer lors de la génération du fichier final
	 * @return un fichier généré en substituant toutes les propriétés dans le template. Le fichier final est le même que le fichier de template sans la 2e extension (par exemple ".template")
	 * @throws IOException 
	 */
	public static File writeTemplate(final Properties props, final File template) throws IOException
	{
		// remplacement des propriétés dans le template
		String content = FileUtils.readFileToString(template, FileUtils.UTF8);
		content = StrSubstitutor.replace(content, props);
		
		// out
		final File file = new File(FilenameUtils.removeExtension(template.getPath()));
		FileUtils.writeStringToFile(file, content, FileUtils.UTF8);
		
		return file;
	}

	public static File createBackup(File file) throws IOException, ConfigurationException
	{
		final File backup = createTempFile(file.getPath());
		FileUtils.copyFile(file, backup);
		return backup;
	}

    /**
     * @param filename
     * @return <code>null</code> si fichier introuvable
     */
    public static File getFile(String filename)
    {
        URL url = getResource(filename);
        if (url == null) return null;
        return new File(url.getFile());
//        return ConfigurationUtils.getFile(null, filename);
    }

    public static InputStream getInputStreamFromString(String string)
    {
        return IOUtils.toInputStream(string);
    }

    public static File writeTemplateToFile(File template, Properties props) throws IOException
    {
        final File out = new File(FilenameUtils.removeExtension(template.getPath()));
        return writeTemplateToFile(template, props, out);
    }

    public static File writeTemplateToFile(File template, Properties props, File out) throws IOException
    {
        final String generatedContent = readTemplate(template, props);
        writeStringToFile(out, generatedContent);
        return out;
    }

    public static File writeTemplateToFile(String template, Properties props, File out) throws IOException
    {
        final File templateFile = getFile(template);
        return writeTemplateToFile(templateFile, props, out);
    }

    public static String readTemplate(File template, Properties props) throws IOException
    {
        final String content = readFileToString(template);
        String generatedContent = StrSubstitutor.replace(content, props);
        generatedContent = normalizeLineBreaks(generatedContent);
        return generatedContent;
    }

    public static String readTemplate(String template, Properties props) throws IOException
    {
        return readTemplate(getFile(template), props);
    }

    /**
     * Prend comme référence le type de saut de ligne de la 1ère ligne et l'applique pour toutes les lignes du fichier.
     * Cela permet d'avoir un fichier avec le même type de saut de ligne du début jusqu'à la fin.
     * 
     * <p><b>Attention</b> : seuls les retour Windows et Unix sont pris en compte</p>
     * 
     * @param data
     * @return
     */
    public static String normalizeLineBreaks(String data)
    {
        final Matcher m = LINE_BREAK_DETECT.matcher(data);
        
        if (m.find())
        {
            final String br = m.group(0);
            return normalizeLineBreaks(data, br);
        }
        else
        {
            // Aucun saut de ligne
            return data;
        }
    }

    public static String normalizeLineBreaks(String data, String br)
    {
        String out = data;
        if (!br.equals(WINDOWS_LINE_BREAK))
        {
            out = out.replace(WINDOWS_LINE_BREAK, br);
        }
        if (!br.equals(MAC_LINE_BREAK))
        {
            out = out.replaceAll(MAC_LINE_BREAK_PATTERN, br);
        }
        if (!br.equals(UNIX_LINE_BREAK))
        {
            out = out.replaceAll(UNIX_LINE_BREAK_PATTERN, br);
        }
        return out;
    }

    public static void normalizeLineBreaks(File file) throws IOException
    {
        final String data = readFileToString(file);
        writeStringToFile(file, normalizeLineBreaks(data));
    }
}

