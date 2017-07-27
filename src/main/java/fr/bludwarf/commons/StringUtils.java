package fr.bludwarf.commons;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import org.apache.commons.io.IOUtils;

import fr.bludwarf.commons.formatters.CollectionFormatter;
import fr.bludwarf.commons.formatters.MapFormatter;

public class StringUtils extends org.apache.commons.lang.StringUtils
{

	public static final String MAP_PATTERN = "{0}={1}";
	public static final String SEP = ", ";
	public static final String MAP_SEP = SEP;
    public static final String STRIP_PADSTRING = "...";
    public static final String NEW_LINE = IOUtils.LINE_SEPARATOR;
    public static final String LINE_SEPARATOR = IOUtils.LINE_SEPARATOR;
	public static final String UTF8 = "UTF-8";

	/**
	 * <p>Exemple d'utilisation : </p>
	 * <pre>
	 * StringUtils.join(joueurs, "; ", new StringUtils.Formatter&lt;Joueur&gt;() {
	 * 	public String format(Joueur e)
	 * 	{
	 * 		return e.getEmail();
	 * 	}});
	 * </pre>
	 * @param map
	 * @param sep
	 * @return
	 */
	public static <T extends Comparable<T>> String join(HashMap<T, Integer> map, final String sep)
	{
		final StringBuilder sb = new StringBuilder();
		
		TreeSet<T> ordered = new TreeSet<T>();
		ordered.addAll(map.keySet());
		
		boolean first = true;
		for (final T key : ordered)
		{
			if (!first)
			{
				sb.append(sep);
			}
			final Integer nb = map.get(key);
			sb.append(String.format("%s=%s", key.toString(), nb));
			if (first)
			{
				first = false;
			}
		}
		
		return sb.toString();
	}

	/**
	 * Équivalent à {@link #join(Collection, String)} mais formatte chaque élément avant de faire le join.
	 * @param coll
	 * @param sep
	 * @param formatter exemple : <pre>new StringUtils.Formatter&lt;String&gt;() {...}</pre>
	 * @return
	 */
	// FIXME : V1.0 : on a supprimé T comparable => OK ?
	public static <T> String join(Collection<T> coll, final String sep, final CollectionFormatter<T> formatter)
	{
		final StringBuilder sb = new StringBuilder();

		int i = 0;
		for (final T e : coll)
		{
			if (i > 0) sb.append(sep);
			sb.append(formatter.format(e, i));
			++i;
		}

		return sb.toString();
	}

	/**
	 * Équivalent à {@link #join(Collection, String)} mais formatte chaque élément avant de faire le join.
	 * @param coll
	 * @param sep
	 * @param pattern {@link MessageFormat format} de chaque élément avec {0}:l'élément et {1}:l'index de l'élément. ('{' pour échapper)
	 * @return
	 */
	public static <T> String join(Collection<T> coll, final String sep, final String pattern)
	{
		final StringBuilder sb = new StringBuilder();

		int i = 0;
		for (final T e : coll)
		{
			if (i > 0) sb.append(sep);
			sb.append(MessageFormat.format(pattern, e, i));
			++i;
		}

		return sb.toString();
	}

	/**
	 * Équivalent à {@link #join(Collection, String)} mais formatte chaque élément avant de faire le join.
	 * @param coll
	 * @param sep
	 * @param formatter exemple : <pre>new StringUtils.Formatter&lt;String&gt;() {...}</pre>
	 * @return
	 */
	// FIXME : V1.0 : on a supprimé K comparable<K> => OK ?
	public static <K, T> String join(Map<K,T> map, final String sep, final MapFormatter<K,T> formatter)
	{
		final StringBuilder sb = new StringBuilder();

		int i = 0;
		for (final K key : map.keySet())
		{
			final T e = map.get(key);
			if (i > 0) sb.append(sep);
			sb.append(formatter.format(key, e, i));
			++i;
		}

		return sb.toString();
	}
	
	/**
	 * @param map
	 * @param sep
	 * @param pattern avec {0}:clé {1}:valeur {2}:0-index
	 * @return
	 */
	public static <K, T> String join(Map<K,T> map, final String sep, final String pattern)
	{
		final StringBuilder sb = new StringBuilder();

		int i = 0;
		for (final K key : map.keySet())
		{
			final T e = map.get(key);
			if (i > 0) sb.append(sep);
			sb.append(MessageFormat.format(pattern, key, e, i));
			++i;
		}

		return sb.toString();
	}
	
	/**
	 * Retourne "clé=valeur" pour chaque entrée de la map
	 * @param map
	 * @param sep
	 * @return
	 */
	public static <K, T> String join(Map<K,T> map, final String sep)
	{
		return join(map, sep, MAP_PATTERN);
	}
	
	/**
	 * Retourne "clé=valeur" pour chaque entrée de la map. Séparateur = ", "
	 * @param map
	 * @return
	 */
	public static <K, T> String join(Map<K,T> map)
	{
		return join(map, MAP_SEP, MAP_PATTERN);
	}

	public static <T> String join(HashMap<T, Integer> map)
	{
		return join(map, SEP);
	}

	public static String join(List<String> elements)
	{
		return join(elements, SEP);
	}
	
	/**
	 * @param str1
	 * @param str2
	 * @return la correspondance entre deux chaine en % à partir de la distance de Levenshtein (100% chaines égales)
	 */
	public static double getLevenshteinMatching(String str1, String str2)
	{
		final int leven = StringUtils.getLevenshteinDistance(str1, str2);
		return 100 - (double) leven / Math.max(str1.length(), str2.length()) * 100;
	}

    /**
     * Coupe une chaine au milieu si elle dépasse une taille max.
     * 
     * <p>
     * Équivalent à {@link #abbreviateMiddle(String, String, int) abbreviateMiddle(str, "...", length)}.
     * </p>
     * 
     * @param str chaine à couper
     * @param length taille max
     * @return la chaine coupée au milieu (on ajoute "..." au milieu pour indiquer la coupure)
     * 
     * @see #abbreviateMiddle(String, String, int)
     */
    public static String abbreviateMiddle(final String str, final int length)
    {
        return abbreviateMiddle(str, STRIP_PADSTRING, length);
    }
    
    /**
     * @param elements
     * @return la taille max. de tous les éléments de la collection convertis en String
     */
    public static int getMaxLength(final Collection<?> elements)
    {
    	int max = 0;
    	
    	for (final Object obj : elements)
    	{
    		if (obj != null)
    		{
    			final int length = obj.toString().length();
    			if (length > max)
    			{
    				max = length;
    			}
    		}
    	}
    	
    	return max;
    }
}
