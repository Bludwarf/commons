package fr.bludwarf.commons.formatters;

public interface CollectionFormatter<T>
{
	/**
	 * @param e
	 * @param i index (indexé-0) de l'élément dans le parcours de la collection
	 * @return
	 */
	String format(final T e, final int i);
}
