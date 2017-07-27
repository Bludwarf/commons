package fr.bludwarf.commons.xml;

import static fr.bludwarf.commons.io.FileUtils.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

public abstract class XMLRepository<T, E extends ElementXML<T>>
{
	
	protected static Logger LOG = Logger.getLogger(XMLRepository.class);
	T _obj = null;

	public XMLRepository()
	{
	}
	
	public T load() throws Exception
	{
		if (_obj == null)
		{
			init();
		}
		return _obj;
	}

	/**
	 * Si le fichier XML n'existe pas alors on crée un repo vide
	 * @throws Exception
	 * @throws IOException
	 */
	protected void init() throws Exception, IOException
	{
		final Class<E> xmlClass = getXMLClass();
		// FIXME : erreur lorsque le repo est vide
		
		// Fichier existant ?
		if (fileExists())
		{
			_obj = ElementXML.load(getInput(), xmlClass);
		}
		else
		{
			_obj = ElementXML.init(xmlClass);
		}
		
		if (_obj == null) throw new NotImplementedException(String.format("La méthode toObject() de la classe %s n'a pas dû être implémentée car l'objet initialisé est null", xmlClass.getName()));
		
		addDefaultElements(this._obj);
	}

	/**
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public Class<E> getXMLClass()
	{
		return (Class<E>) getXmlBinder().getClass();
	}
	
	/**
	 * Ajoute les éléments par défaut dans le repository au moment de l'initialisation
	 */
	protected void addDefaultElements(final T elements)
	{
		// implémentée par les classes filles si besoin
	}

	/**
	 * Si le fichier XML n'existe pas on le crée dans le dossier courant
	 * @throws Exception
	 */
	public void save() throws Exception
	{
		if (_obj != null)
		{
			// FIXME : comme actuellement le BOM UTF-8 n'est pas ajouté on utilise uniquement un File
			final File file = new File(".", getFile());
			
			// Fichier existant ?
			if (fileExists())
			{
//				ElementXML.save(_obj, getFile(), getXMLClass());
				ElementXML.save(_obj, file, getXMLClass());
			}
			else
			{
				ElementXML.save(_obj, file, getXMLClass());
			}
		}
	}
	
	/**
	 * <b>Attention : </b>Ne pas stocker un unique XMLBinder pour cette classe mais en créer un nouveau à chaque appel de cette méthode
	 * @return 
	 */
	public abstract E getXmlBinder();

	/**
	 * @return chemin vers le fichier contenant les données (peut-être de la forme "src/test/resources/file.xml" ou juste "file.xml")
	 * 
	 * <p>
	 * Dans le cas ou on indique la forme simple (sans "src/test") et qu'on appelle {@link #save()} seul le fichier dans "target" sera modifié et pas celui dans "src/test".
	 * </p>
	 */
	protected abstract String getFile();
	
	private InputStream getInput() throws IOException
	{
		return getResourceAsStream(getFile());
	}
	
	private OutputStream getOutput() throws IOException
	{
		return getResourceAsOutputStream(getFile());
	}
	
	private boolean fileExists()
	{
		return resourceExists(getFile());
	}
	
	/**
	 * @return les éléments avec chargement automatique si vide
	 * @throws Exception 
	 */
	public T getElements() throws Exception
	{
		return load();
	}
}
