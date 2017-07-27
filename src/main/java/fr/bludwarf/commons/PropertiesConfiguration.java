package fr.bludwarf.commons;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;
import java.util.regex.Pattern;

import javax.xml.transform.stream.StreamSource;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.lang.text.StrSubstitutor;
import org.apache.log4j.Logger;

import fr.bludwarf.commons.exceptions.LoadConfigurationException;

/**
 * Les classe implémentantes doivent respecter le Design Pattern "Singleton".
 * 
 * <p>
 * Vous pouvez avoir un fichier de properties pour la prod et un autre pour les tests rangés dans :
 * <ul>
 * <li>src/main/resources : pour la prod
 * <li>src/test/resources : pour les tests
 * </ul>
 * Le sous-dossier ou sera stocké une copie des propriété de l'utilisateur est indiqué par {@link #getUserPropertiesFolder()} (rangé dans user.home)
 * </p>
 * 
 * <p>
 * Les cas suivants sont gérés :
 * <li>fichier dans le JAR executé
 * <li>include au sein du même JAR
 * </p>
 * 
 * @author MLAVIGNE
 */
public abstract class PropertiesConfiguration extends org.apache.commons.configuration.PropertiesConfiguration
{

    public static final String PATH_RUN = "/target/classes";
	public static final String PATH_TEST = "/target/test-classes";
	
	public HashMap<String, Object> savedValues = new HashMap<String, Object>();

	/** Log (org.apache.log4j) */
    protected static Logger LOG = Logger
        .getLogger(PropertiesConfiguration.class);
	
	/** Patterns */
	private final HashMap<String, Pattern> _patterns = new HashMap<String, Pattern>();

    protected boolean _init = false;

	private File _userFile;
	
	private Env _env = null;
	
	public static enum Env
	{
		PROD,
		
		/** Tests JUnit */
		TEST,
		
		/** Run dans Eclipse ou build Maven */
		RUN
	}

    /**
     * Par défaut on force toutes les propriétés a être présentes dans le fichier (voir {@link #setThrowExceptionOnMissing(boolean)}).
     * 
     * <p>
     * En prod, créé un fichier de properties pour l'utilisateur dans ${user.home}/${{@link #getUserPropertiesFolder()}}/${nomDuFichierParDéfaut}.
     * Pour que ce fichier soit enregistré automatiquement ajouter la propriété dans le properties de base : PropertiesConfiguration.autoSave = true.
     * Tant que le fichier user existe, il est utilisé à la place des properties par défaut (même s'il manque des properties => TODO : faire un DefaultConfigurationBuilder)
     * </p>
     * 
     * @param defaultPropertiesFilename Exemple si le fichier se trouve dans "src/main/resources/config/monFichier.properties" ou dans
     * "src/test/resources/config/monFichier.properties" : "config/monFichier.properties"
     * @throws ConfigurationException
     */
    public PropertiesConfiguration(final String defaultPropertiesFilename) throws ConfigurationException
    {
        super();
//        super(defaultPropertiesFilename);

//        // Chargement du fichier depuis le JAR
//        try
//        {
//            load();
//        }
//        catch (final Exception e)
//        {
//            throw new LoadConfigurationException(String.format(
//                "Impossible de charger le fichier de configuration %s depuis le JAR",
//                defaultPropertiesFilename),
//                e);
//        }
        
        // Pour éviter d'avoir des propriétés dupliquées on charge uniquement le proprerties user s'il existe
        setThrowExceptionOnMissing(true);
        
        // Chargement du fichier user si existant (uniquement en PROD)
        if (getEnv(defaultPropertiesFilename) == Env.PROD)
        {
	        try
	        {
		        loadFromUser(defaultPropertiesFilename);
		    }
		    catch (final Exception e)
		    {
		        throw new LoadConfigurationException(String.format(
		            "Impossible de charger le fichier de configuration %s de l'utilisateur",
		            getUserPropertiesFile(defaultPropertiesFilename).getPath()),
		            e);
		    }
        }
        else
        {
        	load(defaultPropertiesFilename);
        }
    }

    /**
     * Créé automatiquement un fichier de properties pour cet utilisateur
     * @throws ConfigurationException
     * @throws IOException 
     */
    protected void loadFromUser(final String defaultPropertiesFilename) throws ConfigurationException
	{
    	if (LOG.isInfoEnabled()) LOG.info(String.format("Chargement ou création des propriétés utilisateur (resource par défaut = %s)",
    		defaultPropertiesFilename));
    	
    	// FIXME : pas encore au point : props dupliquées à cause du chargement double
		final File userFile = getUserPropertiesFile(defaultPropertiesFilename);
		if (userFile.exists())
		{
	    	if (LOG.isInfoEnabled()) LOG.info("Chargement des propriétés utilisateur déjà existantes : " + userFile.getAbsolutePath());
	    	
			setFile(userFile);
//			try
//			{
//				setURL(userFile.toURI().toURL());
//			} catch (MalformedURLException e)
//			{
//				// TODO Auto-generated catch block
//				if (logger.isDebugEnabled())
//				{ 
//					logger.debug(e);
//				}
//			}
			// FIXME : si on fait un load() toutes les propriétés sont dupliquées au lieu d'être remplacées
			load(userFile);
			
	    	if (LOG.isInfoEnabled()) LOG.info("Propriétés utilisateur chargées");
		}
		else
		{
	    	if (LOG.isInfoEnabled()) LOG.info(String.format(
	    		"Création des propriétés utilisateur : %s à partir des propriétés par défaut (resource = %s)",
	    		userFile.getAbsolutePath(),
	    		defaultPropertiesFilename));

			if (LOG.isDebugEnabled()) LOG.debug("load(defaultPropertiesFilename)");
			load(defaultPropertiesFilename);

			if (LOG.isDebugEnabled()) LOG.debug("userFile.getParentFile().mkdirs() avec userFile.getParentFile() = " + userFile.getParentFile());
			userFile.getParentFile().mkdirs();
			
			setHeader("Fichier de properties utilisateur généré à partir des paramètres par défaut : " + getURL());
			setFile(userFile);
//			setProperty("PropertiesConfiguration.autoSave", true);
			save();
			
	    	if (LOG.isInfoEnabled()) LOG.info("Propriétés utilisateur créées dans le fichier : " + userFile.getAbsolutePath());
		}

		final String autoSaveKey = "PropertiesConfiguration.autoSave";
		if (containsKey(autoSaveKey))
		{
			setAutoSave(getBoolean(autoSaveKey));
		}
		savedValues.clear();
	}

    /**
     * @param key
     * @param args arguments a passer a la methode {@link String#format(String, Object...)} qui est appelee apres recuperation
     * de la valeur associe a key
     * @return
     * @throws ConfigurationException
     */
    public StreamSource getResourceAsStreamFromKey(final String key, final Object ... args)
        throws ConfigurationException
    {
        String filename = String.format(getString(key), args);
        return getResourceAsStream(filename);
    }

    public static StreamSource getResourceAsStream(final String filename) throws ConfigurationException
    {
        final String filename0;
        if (!filename.startsWith("/"))
        {
            filename0 = "/" + filename;
        }
        else
        {
            filename0 = filename;
        }

        final InputStream is = PropertiesConfiguration.class.getResourceAsStream(filename0);
        if (is == null)
        {
            throw new ConfigurationException(String.format(
                "Impossible de charger la ressource %s",
                filename0));
        }

        // Stream Source
        final StreamSource src = new StreamSource(is);
        src.setSystemId(filename0);

        return src;
    }
    
    /**
     * @param userHome dossier de l'utilisateur courant = System.getProperty("user.home")
     * @return (<code>null</code> si dossier = dossier de l'appli) le dossier contenant le fichier de properties. Ce dossier peut se trouver dans:
     * <ul>
     * <li>userHome : le dossier de l'utilisateur courant = System.getProperty("user.home")
     * </ul>
     */
    protected String getUserPropertiesFolder(final String userHome)
    {
    	return null;
    }

    public File getUserPropertiesFile(final String defaultPropertiesFilename)
    {
		if (_userFile == null)
		{
			final String userDir = getUserPropertiesFolder(System.getProperty("user.home"));
			if (LOG.isDebugEnabled()) LOG.debug("userDir = " + userDir);
				
			final File propFile = new File(defaultPropertiesFilename);
			final File userFile = new File(userDir, propFile.getName()).getAbsoluteFile();
			_userFile = userFile;
			
			if (LOG.isInfoEnabled()) LOG.info("Fichier de propriétés utilisateur : " + userFile.getPath());
		}
		return _userFile;
    }

	public Pattern getPattern(String key)
	{
		return getPattern(key, 0);
	}

	/**
	 * @param key
	 * @param flags voir flags autorisés pour la méthode {@link Pattern#compile(String, int)}
	 * @return
	 */
	public Pattern getPattern(String key, int flags)
	{
		// Pattern derrière la propriétés
		final String pattern = getString(key);
		
		if (!_patterns.containsKey(pattern))
		{
			final Pattern p = Pattern.compile(pattern, flags);
			_patterns.put(pattern, p);
			return p;
		}
		else
		{
			return _patterns.get(pattern);
		}
	}
	
	/**
	 * @return <ul>
	 * <li> {@link Env#RUN} si fichier chargé contient "src/main/"
	 * <li> {@link Env#TEST} si fichier chargé contient "src/test/"
	 * <li> {@link Env#PROD} sinon
	 * </ul>
	 */
	public Env getEnv(final String defaultPropertiesFilename)
	{
		if (_env == null)
		{
			final File file = new File(
				getClass().getClassLoader().getResource(defaultPropertiesFilename)
				.getFile());
			
			final String path = file.getPath();
			if (path.endsWith(getPathRun(defaultPropertiesFilename)))
				_env = Env.RUN;
			else if (path.endsWith(getPathTest(defaultPropertiesFilename)))
				_env = Env.TEST;
			else
			_env = Env.PROD;

			LOG.info("Environnement détecté : " + _env);
		}
		return _env;
	}

	/**
	 * @return
	 */
	private String getPathRun(final String defaultPropertiesFilename)
	{
		return new File(PATH_RUN, defaultPropertiesFilename).getPath();
	}

	/**
	 * @return
	 */
	private String getPathTest(final String defaultPropertiesFilename)
	{
		return new File(PATH_TEST, defaultPropertiesFilename).getPath();
	}

	/**
	 * Récupère une chaine à partir du nom de sa propriété et la formatte avec les args passés en paramètres.
	 * @param key
	 * @param args
	 * @return
	 * 
	 * @see #getString(String)
	 * @see String#format(String, Object...)
	 */
	public String getStringAndFormat(String key, Object ... args)
	{
		return String.format(getString(key), args);
	}

	/**
	 * Récupère la valeur d'une propriétés et remplace toutes les variables de la forme "${var}" si elles n'ont pas déjà été
	 * substituées par {@link #getString(String)}
	 * @param key
	 * @param props valeurs à substituer
	 * @return {@link #getString(String)}
	 * 
	 * @since 11 juil. 2014
	 */
	public String getString(String key, Properties props)
	{
		final String value = getString(key);
		return StrSubstitutor.replace(value, props);
	}

	/**
	 * Change la valeur d'une propriété jusqu'à l'appel de {@link #resetProperty(String)}
	 * @param string
	 * @param b
	 */
	public void setPropertyTemporarily(String key, boolean value)
	{
		savedValues.put(key, getProperty(key));
		setProperty(key, value);
	}

	/**
	 * Restaure la valeur précédemment sauvegardée par {@link #setPropertyTemporarily(String, boolean)}
	 * @param key
	 */
	public void resetProperty(String key)
	{
		if (savedValues.containsKey(key))
		{
			final Object value = savedValues.get(key);
			setProperty(key, value);
			savedValues.remove(key);
		}
	}

	/**
	 * Restaure toutes les propriétés sauvegardées par {@link #setPropertyTemporarily(String, boolean)}
	 * @param key
	 */
	public void resetProperties()
	{
		for (final String key : savedValues.keySet())
		{
			final Object value = savedValues.get(key);
			setProperty(key, value);
		}
		savedValues.clear();
	}	

}
