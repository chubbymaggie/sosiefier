/*
 * This file was automatically generated by EvoSuite
 */

package org.apache.commons.collections4.set;

import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.collections4.set.ListOrderedSet;
import org.junit.BeforeClass;

public class AbstractSerializableSetDecoratorEvoSuiteTest {

  @BeforeClass 
  public static void initEvoSuiteFramework(){ 
    org.evosuite.utils.LoggingUtils.setLoggingForJUnit(); 
  } 


  //Test case number: 0
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.set.AbstractSerializableSetDecorator.<init>(Ljava/util/Set;)V: root-Branch
   */

  @Test
  public void test0()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(1159,"org.apache.commons.collections4.set.AbstractSerializableSetDecoratorEvoSuiteTest.test0");
      ListOrderedSet<Integer> listOrderedSet0 = new ListOrderedSet<Integer>();
      assertEquals("[]", listOrderedSet0.toString());
  }
}