/*
 * This file was automatically generated by EvoSuite
 */

package org.apache.commons.collections4.iterators;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.Iterator;
import org.apache.commons.collections4.iterators.ListIteratorWrapper;
import org.junit.BeforeClass;

public class ListIteratorWrapperEvoSuiteTest {

  @BeforeClass 
  public static void initEvoSuiteFramework(){ 
    org.evosuite.utils.LoggingUtils.setLoggingForJUnit(); 
  } 


  //Test case number: 0
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.iterators.ListIteratorWrapper.<init>(Ljava/util/Iterator;)V: I24 Branch 1 IFNONNULL L80 - false
   */

  @Test
  public void test0()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(548,"org.apache.commons.collections4.iterators.ListIteratorWrapperEvoSuiteTest.test0");
      ListIteratorWrapper<Object> listIteratorWrapper0 = null;
      try {
        listIteratorWrapper0 = new ListIteratorWrapper<Object>((Iterator<?>) null);
        fail("Expecting exception: NullPointerException");
      
      } catch(NullPointerException e) {
         //
         // Iterator must not be null
         //
      }
  }
}