package fr.bludwarf.commons;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

public class Maps
{

	/**
	 * Tri par clé
	 * @param map map
	 * @return
	 */
	public static <K,V> SortedMap<K, V> sortByKeys(Map<K, V> map)
	{
		if (map instanceof SortedMap)
			return (TreeMap<K, V>) map;
		
		SortedMap<K, V> tri = new TreeMap<K, V>();
		tri.putAll(map);
		return tri;
	}
}
