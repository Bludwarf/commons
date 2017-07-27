package fr.bludwarf.commons.formatters;

public interface MapFormatter<K, E>
{
	/**
	 * @param e
	 * @param i index (indexé-0) de l'élément dans le parcours de la collection
	 * @return
	 */
	String format(final K key, final E e, final int i);
}
