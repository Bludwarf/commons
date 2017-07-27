package fr.bludwarf.commons;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateUtils
{

    /**
     * La date qu'il sera dans &lt;amount&gt; avec comme unité de temps &lt;field&gt;.
     * 
     * <p>Exemple : </p>
     * <pre>getDate(5, Calendar.SECOND)</pre>
     * <p>Renvoie la date qu'il sera dans 5 secondes</p>
     * 
     * @param amount
     * @param field
     * @return
     */
    public static Date getDate(int amount, int field)
    {
        final Calendar cal = Calendar.getInstance();
        cal.add(field, amount);
        return cal.getTime();
    }
    
    /**
     * @param date
     * @param min
     * @param max
     * @return [min,max] modifiés
     */
    public static Date[] modifyBounds(final Date date, Date min, Date max)
    {
        if (min == null || date.compareTo(min) < 0)
        {
            min = date;
        }
        if (max == null || date.compareTo(max) > 0)
        {
            max = date;
        }
        return new Date[]{min, max};
    }

    /**
     * @param d1 peut être <code>null</code> dans ce cas d2 est renvoyé
     * @param d2 peut être <code>null</code> dans ce cas d1 est renvoyé
     * @return d2 ssi d2 < d1 ou d1 <code>null</code>
     */
    public static Date min(Date d1, Date d2)
    {
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        if (d2.compareTo(d1) < 0)
        {
            return d2;
        }
        else
        {
            return d1;
        }
    }

    /**
     * @param d1 peut être <code>null</code> dans ce cas d2 est renvoyé
     * @param d2 peut être <code>null</code> dans ce cas d1 est renvoyé
     * @return d2 ssi d2 > d1 ou d1 <code>null</code>
     */
    public static Date max(Date d1, Date d2)
    {
        if (d1 == null) return d2;
        if (d2 == null) return d1;
        if (d2.compareTo(d1) > 0)
        {
            return d2;
        }
        else
        {
            return d1;
        }
    }

    public static final SimpleDateFormat DATE_SDF = new SimpleDateFormat("dd/MM/yyyy");
    public static final SimpleDateFormat DATE_TIME_SDF = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
    
}
