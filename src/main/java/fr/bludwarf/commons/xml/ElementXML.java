package fr.bludwarf.commons.xml;

import static fr.bludwarf.commons.StringUtils.UTF8;
import static fr.bludwarf.commons.io.FileUtils.*;
import static javax.xml.bind.DatatypeConverter.parseDateTime;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.FieldPosition;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.core.Persister;
import org.simpleframework.xml.strategy.Strategy;
import org.simpleframework.xml.strategy.TreeStrategy;
import org.simpleframework.xml.stream.Format;
import org.simpleframework.xml.transform.RegistryMatcher;
import org.simpleframework.xml.transform.Transform;
import org.w3c.dom.Document;

import fr.bludwarf.commons.io.FileUtils;
import fr.bludwarf.commons.xml.exceptions.XMLLoadException;


// TODO : créer une méthode static loadFromXML dans le type T pour créer automatiquement l'objet ElementXML xml correspondant puis l'appel à xml.loadFromXML 


/**
 * <b>La classe implémentante doit définir le constructeur par défaut</b>
 * 
 * <p>
 * Pour charger un élément depuis un fichier :
 * </p>
 * <pre>
 * final File file = new File("src/test/resources/xml/etat-158535-maq-ile.xml");
 * final ElementXML&lt;T&gt; xml = new ElementXML&lt;T&gt;();
 * final T obj = xml.load(file);
 * </pre>
 *
 * @param <T>
 * 
 * @author MLAVIGNE
 */
public abstract class ElementXML<T>
{
	protected static Logger LOG = Logger.getLogger(ElementXML.class);
	protected static Format UTF8_FORMAT = new Format(4, "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>");
	
	@Attribute(required=false)
	String noNamespaceSchemaLocation;

	/**
	 * Format des dates (avec TimeZone) XML
	 * Attention : format ne marche pas avec les heure GMT+-0 
	 */
	protected static final SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
	{ 
	    private static final long serialVersionUID = -5177352001433659085L;
		public Date parse(String source, ParsePosition pos)
	    {    
	        return super.parse(source.replaceFirst("Z$", "+0000").replaceFirst(":(?=[0-9]{2}$)",""),pos);
	    }
		public StringBuffer format(Date date, StringBuffer toAppendTo, FieldPosition pos)
		{
			final StringBuffer format = super.format(date, toAppendTo, pos);
			final int length = format.length();
			if (format.charAt(length - 1) != 'Z')
				format.insert(length - 2, ':');
			return format;
		};
	};
	
	public static final DateFormatTransformer DATE_TRANSFORM = new DateFormatTransformer(SDF);

	private DateFormatTransformer _dateFormatTransformer = DATE_TRANSFORM;

	private boolean _loaded;
	
	public static class DateFormatTransformer implements Transform<Date>
	{
		
//		private SimpleDateFormat _sdf;

		public DateFormatTransformer(SimpleDateFormat sdf)
		{
//			_sdf = sdf;
		}

		public Date read(String value) throws Exception
		{
			try
			{
				return parseDateTime(value).getTime();
			}
			catch (final IllegalArgumentException e)
			{
				LOG.error("Impossible de parser la date : " + value, e);
				throw e;
			}
//			return _sdf.parse(value);
		}

		public String write(Date value) throws Exception
		{
			final Calendar cal = Calendar.getInstance();
			cal.setTime(value);
			return javax.xml.bind.DatatypeConverter.printDateTime(cal);
//			return _sdf.format(value);
		}

	}

	/**
	 * @return
	 */
	public Persister getPersister(boolean utf8)
	{
        Strategy strategy = new TreeStrategy("java-class", "len"); // clazz substitute for class, and len for array lengths
        RegistryMatcher m = new RegistryMatcher();
        m.bind(Date.class, _dateFormatTransformer);
        if (utf8)
        {
        	return new Persister(strategy, m, UTF8_FORMAT);
        }
        else
        {
        	return new Persister(strategy, m);
        }
    }

	public ElementXML()
	{
		super();
	}

	public ElementXML(T obj) throws Exception
	{
		super();
		fromObject(obj);
	}

	public ElementXML(SimpleDateFormat sdf)
	{
		super();
		_dateFormatTransformer = new DateFormatTransformer(sdf);
	}

	/**
	 * Charge un objet à partir d'un fichier XML. Pour charger un nouveau fichier, créez une nouvelle instance d'ElementXML.
	 * @param file
	 * @throws XMLLoadException
	 * 
	 * @see {@link #load(InputStream)}
	 */
	public T load(final File file) throws Exception
	{
	    try
		{
	    	if (_loaded)
	    		throw new XMLLoadException("Impossible de charger deux fois la même instance d'ElementXML avec SimpleXML. Veuillez créer une nouvelle instance.");
			getPersister(true).read(this, file);
			_loaded = true;
			return toObject();
		} catch (Exception e)
		{
			throw new XMLLoadException("Erreur lors du chargement du fichier XML : " + file.getPath(), e);
		}
	}

	/**
	 * Charge un objet à partir d'un fichier XML
	 * @param file
	 * @throws XMLLoadException
	 * 
	 * @see {@link #load(File)}
	 */
	public T load(final InputStream is) throws Exception
	{
	    try
		{
	    	if (_loaded)
	    		throw new XMLLoadException("Impossible de charger deux fois la même instance d'ElementXML avec SimpleXML. Veuillez créer une nouvelle instance.");
			getPersister(true).read(this, is);
			_loaded = true;
			return toObject();
		} catch (Exception e)
		{
			throw new XMLLoadException("Erreur lors du chargement du flux XML : " + is, e);
		}
	}

	public void save(File file) throws Exception
	{
	    final File parent = file.getParentFile();
	    if (!parent.exists())
	    {
	        parent.mkdirs();
	    }

	    File backup = null;
	    if (file.exists())
	    {
	    	backup = FileUtils.createBackup(file);
	    }
	    
	    try
	    {
	    	write(file);
	    }
	    catch (Exception e)
	    {
	    	if (backup != null)
	    	{
				LOG.error(String.format("Erreur lors de l'écriture du fichier XML %s => on restaure le backup depuis %s",
					file.getAbsolutePath(),
					backup.getAbsolutePath()));
				FileUtils.copyFile(backup, file);
	    	}
			throw e;
		}
	}

	public void save(OutputStream os) throws Exception
	{
	    getPersister(true).write(this, os, UTF8);
	    // FIXME : ajout du BOM
	    LOG.warn("TODO : ajout du BOM");
	}

	public void save(final T obj, File file) throws Exception
	{
		fromObject(obj);
	    final File parent = file.getParentFile();
	    if (!parent.exists())
	    {
	        parent.mkdirs();
	    }
	    write(file);
	}

	/**
	 * @param file
	 * @throws IOException
	 * @throws Exception
	 */
	private void write(File file) throws IOException, Exception
	{
		final OutputStream os = getOutputStream(file);
	    try
	    {
	    	getPersister(true).write(this, os, UTF8);
	    }
	    finally
	    {
	    	os.close();
	    }
	    
	    // Ajout du BOM
	    FileUtils.addBOM(file);
	}

	public abstract void fromObject(final T obj) throws Exception;

	public abstract T toObject()  throws Exception;
	
	public Date parseDate(String date) throws Exception
	{
		return _dateFormatTransformer.read(date);
	}
	
	public String formatDate(Date date) throws Exception
	{
		return _dateFormatTransformer.write(date);
	}
	
	public String formatDate(Calendar cal) throws Exception
	{
		return _dateFormatTransformer.write(cal.getTime());
	}
	
	/**
	 * Charge un fichier XML en s'aidant d'une classe de liaison de donnée
	 * @param file
	 * @param xmlClass
	 * @return l'objet parsé par la classe de liaison de donnée
	 * @throws Exception
	 * 
	 * @see {@link #load(File)}
	 * @see {@link #load(InputStream, Class)}
	 */
	public static <T, E extends ElementXML<T>> T load(final File file, final Class<E> xmlClass) throws Exception
	{
		final E xml = xmlClass.newInstance();
        xml.load(file);
        return xml.toObject();
	}
	
	/**
	 * Charge un Stream XML en s'aidant d'une classe de liaison de donnée
	 * @param is
	 * @param xmlClass
	 * @return l'objet parsé par la classe de liaison de donnée
	 * @throws Exception
	 * 
	 * @see {@link #load(InputStream)}
	 * @see {@link #load(File, Class)}
	 */
	public static <T, E extends ElementXML<T>> T load(final InputStream is, final Class<E> xmlClass) throws Exception
	{
		final E xml = xmlClass.newInstance();
        xml.load(is);
        return xml.toObject();
	}
	
	/**
	 * Charge un Stream XML en s'aidant d'une classe de liaison de donnée
	 * @param filename
	 * @param xmlClass
	 * @return l'objet parsé par la classe de liaison de donnée
	 * @throws Exception
	 * 
	 * @see {@link #load(InputStream)}
	 * @see {@link #load(File, Class)}
	 */
	public static <T, E extends ElementXML<T>> T load(final String filename, final Class<E> xmlClass) throws Exception
	{
		return load(getResourceAsStream(filename), xmlClass);
	}

	public static <T, E extends ElementXML<T>> void save(T obj, File file, Class<E> xmlClass) throws Exception
	{
		final E xml = xmlClass.newInstance();
		xml.fromObject(obj);
		xml.save(file);
	}

	public static <T, E extends ElementXML<T>> void save(T obj, OutputStream os, Class<E> xmlClass) throws Exception
	{
		final E xml = xmlClass.newInstance();
		xml.fromObject(obj);
		xml.save(os);
	}

	public static <T, E extends ElementXML<T>> void save(T obj, String filename, Class<E> xmlClass) throws Exception
	{
		save(obj, getResourceAsOutputStream(filename), xmlClass);
	}
	
	/**
	 * @param doc
	 * @param file
	 * 
	 * @author <a href="http://www.journaldev.com/1112/how-to-write-xml-file-in-java-dom-parser">http://www.journaldev.com/1112/how-to-write-xml-file-in-java-dom-parser</a>
	 * @author bludwarf@gmail.com
	 * @throws TransformerException 
	 */
	public static void save(Document doc, File file) throws TransformerException
	{
		TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        //for pretty print
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        DOMSource source = new DOMSource(doc);

        //write to console or file
        StreamResult result = new StreamResult(file);

        //write data
        transformer.transform(source, result);
	}
	
	/**
	 * Initialise un nouvel objet vide
	 * @param xmlClass
	 * @return
	 * @throws Exception
	 */
	public static <T, E extends ElementXML<T>> T init(final Class<E> xmlClass) throws Exception
	{
		final E xml = xmlClass.newInstance();
        return xml.toObject();
	}

}