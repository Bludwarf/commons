package fr.bludwarf.commons.lang.reflect;

import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import junit.extensions.ActiveTestSuite;
import junit.extensions.RepeatedTest;
import junit.extensions.TestDecorator;
import junit.extensions.TestSetup;

import org.junit.Test;

import fr.bludwarf.commons.exceptions.LoadConfigurationException;
import fr.bludwarf.commons.lang.reflect.children.Child1;
import fr.bludwarf.commons.lang.reflect.children.Child2;
import fr.bludwarf.commons.lang.reflect.children.GrandChild;
import fr.bludwarf.commons.lang.reflect.children.NotChild;

public class ClassLoadUtilsTest
{
    
    @Test
    public void testGetChildren() throws Exception
    {
        final List<String> children = ClassLoadUtils.getChildren("fr/bludwarf/commons/exceptions");
        assertTrue(children.size() >= 1);
    }
    
    @Test
    public void testGetClasses() throws Exception
    {
        final List<Class<?>> children = ClassLoadUtils.getClasses("fr/bludwarf/commons/exceptions");
        assertTrue(children.contains(LoadConfigurationException.class));
    }
    
    @Test
    public void testGetChildrenJAR() throws Exception
    {
        final List<Class<?>> children = ClassLoadUtils.getClasses("junit.extensions");
        
        final List<Class<?>> expected = new ArrayList<Class<?>>();
        expected.add(ActiveTestSuite.class);
        expected.add(RepeatedTest.class);
        expected.add(TestDecorator.class);
        expected.add(TestSetup.class);
        
        assertTrue(children.containsAll(expected));
    }
    
    @Test
    public void testGetChildrenSuperclass() throws Exception
    {
        final List<Class<? extends SuperClass>> children = ClassLoadUtils.getClasses("fr.bludwarf.commons.lang.reflect.children", SuperClass.class);
        
        final List<Class<? extends SuperClass>> expected = new ArrayList<Class<? extends SuperClass>>();
        expected.add(Child1.class);
        expected.add(Child2.class);
        expected.add(GrandChild.class);
        
        assertTrue(children.containsAll(expected));
        
        // Not contains
        assertFalse(children.contains(NotChild.class));
    }
    
}
