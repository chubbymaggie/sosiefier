/*
 * This file was automatically generated by EvoSuite
 */

package org.apache.commons.collections4.collection;

import org.junit.Test;
import static org.junit.Assert.*;
import java.lang.reflect.Array;
import java.util.Collection;
import java.util.LinkedList;
import org.apache.commons.collections4.FunctorException;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.Predicate;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.collections4.collection.IndexedCollection;
import org.apache.commons.collections4.collection.PredicatedCollection;
import org.apache.commons.collections4.collection.TransformedCollection;
import org.apache.commons.collections4.collection.UnmodifiableCollection;
import org.apache.commons.collections4.functors.ConstantTransformer;
import org.apache.commons.collections4.functors.EqualPredicate;
import org.apache.commons.collections4.functors.InvokerTransformer;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.junit.BeforeClass;

public class AbstractCollectionDecoratorEvoSuiteTest {

  @BeforeClass 
  public static void initEvoSuiteFramework(){ 
    org.evosuite.utils.LoggingUtils.setLoggingForJUnit(); 
  } 


  //Test case number: 0
  /*
   * 5 covered goals:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.iterator()Ljava/util/Iterator;: root-Branch
   * 2 org.apache.commons.collections4.collection.AbstractCollectionDecorator.containsAll(Ljava/util/Collection;)Z: root-Branch
   * 3 org.apache.commons.collections4.collection.AbstractCollectionDecorator.decorated()Ljava/util/Collection;: root-Branch
   * 4 org.apache.commons.collections4.collection.AbstractCollectionDecorator.size()I: root-Branch
   * 5 org.apache.commons.collections4.collection.AbstractCollectionDecorator.<init>(Ljava/util/Collection;)V: I7 Branch 1 IFNONNULL L66 - true
   */

  @Test
  public void test0()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(931,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test0");
      LinkedList<String> linkedList0 = new LinkedList<String>();
      CircularFifoQueue<Object> circularFifoQueue0 = new CircularFifoQueue<Object>();
      ConstantTransformer<Object, String> constantTransformer0 = new ConstantTransformer<Object, String>("The collection must not be null");
      MultiValueMap<String, Object> multiValueMap0 = new MultiValueMap<String, Object>();
      IndexedCollection<String, Object> indexedCollection0 = new IndexedCollection<String, Object>((Collection<Object>) circularFifoQueue0, (Transformer<Object, String>) constantTransformer0, (MultiMap<String, Object>) multiValueMap0, true);
      TransformedCollection<String> transformedCollection0 = TransformedCollection.transformingCollection((Collection<String>) linkedList0, (Transformer<? super String, ? extends String>) constantTransformer0);
      boolean boolean0 = transformedCollection0.containsAll((Collection<?>) indexedCollection0);
      assertEquals(false, boolean0);
  }

  //Test case number: 1
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.contains(Ljava/lang/Object;)Z: root-Branch
   */

  @Test
  public void test1()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(932,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test1");
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      CircularFifoQueue<Integer> circularFifoQueue0 = new CircularFifoQueue<Integer>(250);
      circularFifoQueue0.toString();
      EqualPredicate<Object> equalPredicate0 = new EqualPredicate<Object>((Object) "[]");
      PredicatedCollection<Integer> predicatedCollection0 = PredicatedCollection.predicatedCollection((Collection<Integer>) linkedList0, (Predicate<? super Integer>) equalPredicate0);
      assertNotNull(predicatedCollection0);
      
      boolean boolean0 = predicatedCollection0.contains((Object) predicatedCollection0);
      assertEquals(true, boolean0);
  }

  //Test case number: 2
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.clear()V: root-Branch
   */

  @Test
  public void test2()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(937,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test2");
      CircularFifoQueue<String> circularFifoQueue0 = new CircularFifoQueue<String>(1521);
      Class<Object>[] classArray0 = (Class<Object>[]) Array.newInstance(Class.class, 9);
      InvokerTransformer<String, Object> invokerTransformer0 = new InvokerTransformer<String, Object>("r", (Class<?>[]) classArray0, (Object[]) classArray0);
      IndexedCollection<Object, String> indexedCollection0 = IndexedCollection.uniqueIndexedCollection((Collection<String>) circularFifoQueue0, (Transformer<String, Object>) invokerTransformer0);
      indexedCollection0.clear();
      assertEquals("[]", indexedCollection0.toString());
  }

  //Test case number: 3
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.isEmpty()Z: root-Branch
   */

  @Test
  public void test3()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(938,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test3");
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      CircularFifoQueue<Integer> circularFifoQueue0 = new CircularFifoQueue<Integer>(250);
      circularFifoQueue0.toString();
      EqualPredicate<Object> equalPredicate0 = new EqualPredicate<Object>((Object) "[]");
      PredicatedCollection<Integer> predicatedCollection0 = PredicatedCollection.predicatedCollection((Collection<Integer>) linkedList0, (Predicate<? super Integer>) equalPredicate0);
      boolean boolean0 = predicatedCollection0.isEmpty();
      assertEquals(true, boolean0);
  }

  //Test case number: 4
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.remove(Ljava/lang/Object;)Z: root-Branch
   */

  @Test
  public void test4()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(939,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test4");
      LinkedList<String> linkedList0 = new LinkedList<String>();
      Class<Integer> class0 = Integer.class;
      ConstantTransformer<Object, String> constantTransformer0 = new ConstantTransformer<Object, String>("The collection must not be null");
      TransformedCollection<String> transformedCollection0 = TransformedCollection.transformingCollection((Collection<String>) linkedList0, (Transformer<? super String, ? extends String>) constantTransformer0);
      boolean boolean0 = transformedCollection0.remove((Object) class0);
      assertEquals(false, boolean0);
  }

  //Test case number: 5
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.add(Ljava/lang/Object;)Z: root-Branch
   */

  @Test
  public void test5()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(940,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test5");
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      CircularFifoQueue<Integer> circularFifoQueue0 = new CircularFifoQueue<Integer>(250);
      circularFifoQueue0.toString();
      Class<Integer>[] classArray0 = (Class<Integer>[]) Array.newInstance(Class.class, 7);
      InvokerTransformer<Integer, String> invokerTransformer0 = new InvokerTransformer<Integer, String>("[]", (Class<?>[]) classArray0, (Object[]) classArray0);
      IndexedCollection<String, Integer> indexedCollection0 = IndexedCollection.uniqueIndexedCollection((Collection<Integer>) linkedList0, (Transformer<Integer, String>) invokerTransformer0);
      // Undeclared exception!
      try {
        indexedCollection0.add((Integer) 250);
        fail("Expecting exception: FunctorException");
      
      } catch(FunctorException e) {
         //
         // InvokerTransformer: The method '[]' on 'class java.lang.Integer' does not exist
         //
      }
  }

  //Test case number: 6
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.toString()Ljava/lang/String;: root-Branch
   */

  @Test
  public void test6()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(941,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test6");
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      CircularFifoQueue<Integer> circularFifoQueue0 = new CircularFifoQueue<Integer>(250);
      String string0 = circularFifoQueue0.toString();
      EqualPredicate<Object> equalPredicate0 = new EqualPredicate<Object>((Object) "[]");
      PredicatedCollection<Integer> predicatedCollection0 = PredicatedCollection.predicatedCollection((Collection<Integer>) linkedList0, (Predicate<? super Integer>) equalPredicate0);
      String string1 = predicatedCollection0.toString();
      assertSame(string1, string0);
  }

  //Test case number: 7
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.removeAll(Ljava/util/Collection;)Z: root-Branch
   */

  @Test
  public void test7()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(942,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test7");
      LinkedList<String> linkedList0 = new LinkedList<String>();
      CircularFifoQueue<Object> circularFifoQueue0 = new CircularFifoQueue<Object>();
      ConstantTransformer<Object, String> constantTransformer0 = new ConstantTransformer<Object, String>("The collection must not be null");
      MultiValueMap<String, Object> multiValueMap0 = new MultiValueMap<String, Object>();
      IndexedCollection<String, Object> indexedCollection0 = new IndexedCollection<String, Object>((Collection<Object>) circularFifoQueue0, (Transformer<Object, String>) constantTransformer0, (MultiMap<String, Object>) multiValueMap0, true);
      TransformedCollection<Object> transformedCollection0 = TransformedCollection.transformingCollection((Collection<Object>) indexedCollection0, (Transformer<? super Object, ?>) constantTransformer0);
      boolean boolean0 = transformedCollection0.removeAll((Collection<?>) linkedList0);
      assertEquals(false, boolean0);
  }

  //Test case number: 8
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.hashCode()I: root-Branch
   */

  @Test
  public void test8()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(943,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test8");
      LinkedList<String> linkedList0 = new LinkedList<String>();
      ConstantTransformer<Object, String> constantTransformer0 = new ConstantTransformer<Object, String>("The collection must not be null");
      TransformedCollection<String> transformedCollection0 = TransformedCollection.transformingCollection((Collection<String>) linkedList0, (Transformer<? super String, ? extends String>) constantTransformer0);
      int int0 = transformedCollection0.hashCode();
      assertEquals(1, int0);
  }

  //Test case number: 9
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.toArray([Ljava/lang/Object;)[Ljava/lang/Object;: root-Branch
   */

  @Test
  public void test9()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(944,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test9");
      CircularFifoQueue<String> circularFifoQueue0 = new CircularFifoQueue<String>(1521);
      Class<Object>[] classArray0 = (Class<Object>[]) Array.newInstance(Class.class, 9);
      MultiValueMap<Integer, Integer> multiValueMap0 = new MultiValueMap<Integer, Integer>();
      multiValueMap0.toString();
      InvokerTransformer<String, Integer> invokerTransformer0 = new InvokerTransformer<String, Integer>("{}", (Class<?>[]) classArray0, (Object[]) classArray0);
      IndexedCollection<Integer, String> indexedCollection0 = IndexedCollection.nonUniqueIndexedCollection((Collection<String>) circularFifoQueue0, (Transformer<String, Integer>) invokerTransformer0);
      Integer[] integerArray0 = new Integer[1];
      Integer[] integerArray1 = indexedCollection0.toArray(integerArray0);
      assertSame(integerArray0, integerArray1);
  }

  //Test case number: 10
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.retainAll(Ljava/util/Collection;)Z: root-Branch
   */

  @Test
  public void test10()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(933,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test10");
      CircularFifoQueue<String> circularFifoQueue0 = new CircularFifoQueue<String>(1521);
      Class<Object>[] classArray0 = (Class<Object>[]) Array.newInstance(Class.class, 9);
      CircularFifoQueue<Integer> circularFifoQueue1 = new CircularFifoQueue<Integer>();
      InvokerTransformer<Integer, Integer> invokerTransformer0 = new InvokerTransformer<Integer, Integer>("r", (Class<?>[]) classArray0, (Object[]) classArray0);
      TransformedCollection<Integer> transformedCollection0 = TransformedCollection.transformingCollection((Collection<Integer>) circularFifoQueue1, (Transformer<? super Integer, ? extends Integer>) invokerTransformer0);
      boolean boolean0 = transformedCollection0.retainAll((Collection<?>) circularFifoQueue0);
      assertEquals(false, boolean0);
  }

  //Test case number: 11
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.toArray()[Ljava/lang/Object;: root-Branch
   */

  @Test
  public void test11()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(934,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test11");
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      CircularFifoQueue<Integer> circularFifoQueue0 = new CircularFifoQueue<Integer>(250);
      circularFifoQueue0.toString();
      Class<Integer>[] classArray0 = (Class<Integer>[]) Array.newInstance(Class.class, 10);
      InvokerTransformer<Integer, String> invokerTransformer0 = new InvokerTransformer<Integer, String>("[]", (Class<?>[]) classArray0, (Object[]) classArray0);
      IndexedCollection<String, Integer> indexedCollection0 = IndexedCollection.uniqueIndexedCollection((Collection<Integer>) linkedList0, (Transformer<Integer, String>) invokerTransformer0);
      Object[] objectArray0 = indexedCollection0.toArray();
      assertNotNull(objectArray0);
  }

  //Test case number: 12
  /*
   * 1 covered goal:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.<init>(Ljava/util/Collection;)V: I7 Branch 1 IFNONNULL L66 - false
   */

  @Test
  public void test12()  throws Throwable  {
      // Undeclared exception!
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(935,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test12");
      try {
        UnmodifiableCollection.unmodifiableCollection((Collection<Object>) null);
        fail("Expecting exception: IllegalArgumentException");
      
      } catch(IllegalArgumentException e) {
         //
         // Collection must not be null
         //
      }
  }

  //Test case number: 13
  /*
   * 4 covered goals:
   * 1 org.apache.commons.collections4.collection.AbstractCollectionDecorator.equals(Ljava/lang/Object;)Z: I4 Branch 2 IF_ACMPEQ L149 - false
   * 2 org.apache.commons.collections4.collection.AbstractCollectionDecorator.equals(Ljava/lang/Object;)Z: I9 Branch 3 IFEQ L149 - true
   * 3 org.apache.commons.collections4.collection.AbstractCollectionDecorator.decorated()Ljava/util/Collection;: root-Branch
   * 4 org.apache.commons.collections4.collection.AbstractCollectionDecorator.<init>(Ljava/util/Collection;)V: I7 Branch 1 IFNONNULL L66 - true
   */

  @Test
  public void test13()  throws Throwable  {
		fr.inria.diversify.sosie.logger.LogWriter.writeTestStart(936,"org.apache.commons.collections4.collection.AbstractCollectionDecoratorEvoSuiteTest.test13");
      LinkedList<Integer> linkedList0 = new LinkedList<Integer>();
      CircularFifoQueue<Integer> circularFifoQueue0 = new CircularFifoQueue<Integer>(250);
      circularFifoQueue0.toString();
      EqualPredicate<Object> equalPredicate0 = new EqualPredicate<Object>((Object) "[]");
      PredicatedCollection<Integer> predicatedCollection0 = PredicatedCollection.predicatedCollection((Collection<Integer>) linkedList0, (Predicate<? super Integer>) equalPredicate0);
      Class<Integer>[] classArray0 = (Class<Integer>[]) Array.newInstance(Class.class, 10);
      boolean boolean0 = predicatedCollection0.equals((Object) classArray0);
      assertEquals(false, boolean0);
  }
}