package fr.bludwarf.commons;

import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import fr.bludwarf.commons.io.FileUtils;

/**
 * Enrichissement du {@link StringBuilder} Java.
 * 
 * <p>Exemple d'utilisation : </p>
 * <pre>
 * final StringBuilder sb = new StringBuilder()
 *     .append("Usage : <cmd1> <cmd2> <params...>").newLine()
 *     .append("---------------------------------").newLine()
 *     .append("    - Génération de cnf OpenSSL : openssl gencnf <fichier de certif>").newLine()
 *     .newLine()
 * ;
 * </pre>
 * @author MLAVIGNE
 * @since 9 janv. 2014
 */
public class StringBuilder implements Appendable, CharSequence, Serializable
{
	/***/
	private static final long serialVersionUID = 1680983969489087529L;
	private java.lang.StringBuilder sb;
	private int indent = 0;
	private String indentor = "";
	
	public StringBuilder()
	{
		sb = new java.lang.StringBuilder();
	}
	
	public StringBuilder(CharSequence seq)
	{
		sb = new java.lang.StringBuilder(seq);
	}
	
    public <T> StringBuilder(List<T> list)
    {
        this();
        appendLines(list);
    }

	public StringBuilder append(final CharSequence s)
	{
		sb.append(s);
		return this;
	}
	
	/**
	 * Remplace automatiquement les nouvelles lignes (+ indentation)
	 * @param s
	 * @return
	 */
	public StringBuilder append(final String s)
	{
		sb.append(s.replaceAll("\\r?\\n", getNewLine() + indentor)); // FIXME : étendre cette modification à toutes les entrées possibles
		return this;
	}

	public StringBuilder append(char c) throws IOException
	{
		sb.append(c);
		return this;
	}

	public StringBuilder append(CharSequence s, int start, int end)
			throws IOException
	{
		sb.append(s, start, end);
		return this;
	}

    /**
     * Écrit le contenu du Stream dans le StringBuilder (ne se contente pas de faire un toString sur le stream).
     * @param is
     * @return
     * @throws IOException
     */
    public StringBuilder append(InputStream is) throws IOException
    {
        sb.append(FileUtils.readInputStreamToString(is));
        return this;
    }

    /**
     * Écrit le contenu du Stream dans le StringBuilder (ne se contente pas de faire un toString sur le stream).
     * @param is
     * @return
     * @throws IOException
     */
    public StringBuilder append(InputStream is, final String encoding) throws IOException
    {
        sb.append(FileUtils.readInputStreamToString(is, encoding));
        return this;
    }

	public char charAt(int index)
	{
		return sb.charAt(index);
	}

	public int length()
	{
		return sb.length();
	}

	public CharSequence subSequence(int start, int end)
	{
		return sb.subSequence(start, end);
	}
	
	@Override
	public String toString()
	{
		return sb.toString();
	}

	public StringBuilder newLine()
	{
		return append(getNewLine());
	}

	/**
	 * @return
	 */
	public static String getNewLine()
	{
		return System.getProperty("line.separator");
	}

	public StringBuilder newLines(final int n)
	{
		if (n <= 0)
			return this;
		
		StringBuilder sb = null;
		for (int i = 0; i < n; ++i)
		{
			sb = newLine();
		}
		return sb;
	}

	private StringBuilder appendIndent()
	{
		return append(indentor);
	}
	
	/**
	 * @return incrémente l'indentation et insère une chaine d'indentation
	 */
	public StringBuilder indent()
	{
		++indent;
		indentor += '\t';
		return appendIndent();
	}
	
	/**
	 * @return décrémente l'indentation et supprimer une tabulation
	 */
	public StringBuilder unindent()
	{
		if (indent > 0)
		{
			--indent;
			indentor = indentor.substring(1);
			
			// Delete last tab
			final int last = sb.length() - 1;
			if (last >= 0 && sb.charAt(last) == '\t')
			{
				sb.deleteCharAt(last);
			}
		}
		return this;
	}

	/**
	 * @param sb
	 * @param previsions
	 * @return 
	 */
	public <T> StringBuilder appendLines(final Collection<T> elements)
	{
		for (final Object o : elements)
		{
			append(o.toString());
			newLine();
		}
		return this;
	}public StringBuilder appendLine(final CharSequence s)
    {
        sb.append(s);
        return newLine();
    }

    /**
     * @param sb
     * @param previsions
     * @return 
     */
    public <T> StringBuilder appendLines(final List<T> list)
    {
        for (final Object o : list)
        {
            append(o.toString());
            newLine();
        }
        return this;
    }

    /**
     * @param sb
     * @param previsions
     * @return 
     */
    public <T> StringBuilder appendLines(CharSequence indent, final List<T> list)
    {
        for (final Object o : list)
        {
            append(indent).append(o.toString()).newLine();
        }
        return this;
    }

	public <T> StringBuilder append(final Collection<T> elements, final String sep)
	{
		return append(StringUtils.join(elements, sep));
	}

	public <T> StringBuilder append(final Collection<T> elements, final String sep, final String pattern)
	{
		return append(StringUtils.join(elements, sep, pattern));
	}

	public <K, T> StringBuilder append(final Map<K, T> map, final String sep)
	{
		return append(StringUtils.join(map, sep));
	}
}
