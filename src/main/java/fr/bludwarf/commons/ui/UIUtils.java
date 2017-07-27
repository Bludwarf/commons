package fr.bludwarf.commons.ui;

import javax.swing.JOptionPane;

public class UIUtils
{
	/**
	 * @return
	 */
	public static String prompt(final String question)
	{
		return JOptionPane.showInputDialog(
		    null,
		    question,
		    "Question");
	}
}
