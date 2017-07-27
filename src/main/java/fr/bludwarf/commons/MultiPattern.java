package fr.bludwarf.commons;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.text.StrSubstitutor;

public class MultiPattern
{
    
    public static final String IP = "[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}\\.[0-9]{1,3}";
    
    private String _multiRegex;
    final private Map<String, String> _regexMap = new HashMap<String, String>();

    public MultiPattern(final String multiRegex)
    {
        _multiRegex = multiRegex;
    }

    public Pattern compile()
    {
        return Pattern.compile(getPattern());
    }

    public String getPattern()
    {
        return StrSubstitutor.replace(_multiRegex, _regexMap);
    }

    public Matcher matcher(CharSequence input)
    {        
        final Pattern p = compile();
        return p.matcher(input);
    }

    /**
     * @param var nom de la variable dans le multiPattern.
     * Terminé par "?" pour indiquer une variable optionnelle dans le pattern final.
     * Commencé par un "[" pour indiquer un pattern englobé par des "[]" et qui commence par une certaine chaine
     * @param pattern
     * @return le pattern réellement créé pour cette variable
     */
    public String setPattern(String var, String pattern)
    {
//        String pattern2 = String.format("(%s)", pattern);
        String var2 = var;
        String pattern2 = pattern;
        
        // Ajout de la fin si pattern englobé
        if (var.startsWith("["))
        {
            // TODO : commenter et optimiser
            if (pattern.equals(""))
            {
                pattern2 = "[^\\[]+";
            }
            else if (pattern.endsWith(".+"))
            {
                pattern2 = String.format("%s[^\\[]+", pattern2.subSequence(0, pattern2.length() - 2)); 
            }
        }
        
        // Pattern vide en entrée ?
        if (pattern2.equals(""))
        {
            pattern2 = ".+";
        }
        
        // Nouveau groupe
        pattern2 = String.format("(%s)", pattern2);
        
        
        // Englobe le reste de la chaine
        if (var.startsWith("["))
        {
            var2 = var2.substring(1);
            pattern2 = String.format("\\[%s\\]", pattern2);
        }
        // Var optionnelle ?
        if (var.endsWith("?"))
        {
            var2 = var2.substring(0, var2.length() - 1);
            pattern2 = String.format("(?:%s)?", pattern2);
        }
        
        _regexMap.put(var2, pattern2);
        return pattern2;
    }
    
//    /**
//     * Pattern qui matche une chaine du style "[XXYY]" ou :
//     * XX = startPattern
//     * YY = le reste de la chaine jusqu'au "]"
//     * @param var
//     * @param startPattern
//     * @return
//     */
//    private String setEnclosedPattern(String var, String startPattern)
//    {
//        return setPattern(var, String.format("\\[(%s[^\\[]+)\\]", startPattern));
//    }

    @Override
    public String toString()
    {
        return getPattern();
    }

    public String setPattern(String key)
    {
        return setPattern(key, "");
    }
    
}
