/*
 * This file was automatically generated by EvoSuite
 */

package org.apache.commons.collections4.functors;

import org.junit.Test;
import static org.junit.Assert.*;
import org.apache.commons.collections4.Factory;
import org.apache.commons.collections4.functors.PrototypeFactory;
import org.junit.BeforeClass;

public class PrototypeFactoryEvoSuiteTest {

  @BeforeClass 
  public static void initEvoSuiteFramework(){ 
    org.evosuite.utils.LoggingUtils.setLoggingForJUnit(); 
  } 


  //Test case number: 0
  /*
   * 2 covered goals:
   * 1 org.apache.commons.collections4.functors.PrototypeFactory.prototypeFactory(Ljava/lang/Object;)Lorg/apache/commons/collections4/Factory;: I3 Branch 4 IFNONNULL L59 - true
   * 2 org.apache.commons.collections4.functors.PrototypeFactory.prototypeFactory(Ljava/lang/Object;)Lorg/apache/commons/collections4/Factory;: I73 Branch 5 IFEQ L74 - false
   */

  @Test
  public void test0()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(822,"org.apache.commons.collections4.functors.PrototypeFactoryEvoSuiteTest.test0");
      Class<String> class0 = String.class;
      Factory<Class<String>> factory0 = PrototypeFactory.prototypeFactory(class0);
      assertNotNull(factory0);
  }

  //Test case number: 1
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.functors.PrototypeFactory.prototypeFactory(Ljava/lang/Object;)Lorg/apache/commons/collections4/Factory;: I3 Branch 4 IFNONNULL L59 - false
   */

  @Test
  public void test1()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(823,"org.apache.commons.collections4.functors.PrototypeFactoryEvoSuiteTest.test1");
      Factory<Object> factory0 = PrototypeFactory.prototypeFactory((Object) null);
      assertNotNull(factory0);
  }

  //Test case number: 2
  /*
   * 2 covered goals:
   * 1 org.apache.commons.collections4.functors.PrototypeFactory.prototypeFactory(Ljava/lang/Object;)Lorg/apache/commons/collections4/Factory;: I73 Branch 5 IFEQ L74 - true
   * 2 org.apache.commons.collections4.functors.PrototypeFactory.prototypeFactory(Ljava/lang/Object;)Lorg/apache/commons/collections4/Factory;: I3 Branch 4 IFNONNULL L59 - true
   */

  @Test
  public void test2()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(824,"org.apache.commons.collections4.functors.PrototypeFactoryEvoSuiteTest.test2");
      Object object0 = new Object();
      // Undeclared exception!
      try {
        PrototypeFactory.prototypeFactory(object0);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // The prototype must be cloneable via a public clone method
         //
      }
  }
}