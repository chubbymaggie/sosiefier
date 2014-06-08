package fr.inria.diversify.transformation.query;

import fr.inria.diversify.codeFragment.CodeFragmentList;
import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.codeFragment.Statement;
import fr.inria.diversify.coverage.ICoverageReport;
import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.factory.IRandomFactory;
import fr.inria.diversify.random.IRandom;
import fr.inria.diversify.random.Random;
import fr.inria.diversify.transformation.ast.ASTAdd;
import fr.inria.diversify.transformation.ast.ASTDelete;
import fr.inria.diversify.transformation.ast.ASTReplace;
import fr.inria.diversify.transformation.ast.ASTTransformation;
import fr.inria.diversify.transformation.query.TransformationQuery;
import fr.inria.diversify.transformation.query.searchStrategy.SearchStrategy;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * A transformation query over the AST
 * <p>
 * User: Simon
 * Modified: Marcelino
 * Date: 7/9/13
 * Time: 10:02 AM
 */
public class ASTTransformationQuery extends TransformationQuery {
    protected ICoverageReport coverageReport;

    /**
     * Class of the code fragment. Defaults to Statement
     */
    protected Class codeFragmentClass = Statement.class;

    /**
     * Code fragments over we are working right now
     */
    protected CodeFragmentList codeFragments;

    /**
     * Indicates if we do solely simple transformations. Defaults to the simple ones
     */
    protected boolean stupid = true;

    /**
     * Random engine factory to finer grain control of the random generation
     */
    IRandomFactory randomFactory;

    /**
     *  Search strategy to find transplantation points
     */
    protected SearchStrategy transplantationPointStrategy;

    /**
     * Search strategy to find a transplant
     */
    protected SearchStrategy transplantStrategy;

    /**
     * Short constructor assuming the fragment class to be statement and the transformation to be stupid
     *
     * @param inputProgram Input program over the queries are going to be made
     * @param random A random engine factory
     */
    public ASTTransformationQuery(InputProgram inputProgram, IRandomFactory random) {
        coverageReport = inputProgram.getCoverageReport();
        codeFragments = inputProgram.getCodeFragments();
        //This we assume be defect
        codeFragmentClass = Statement.class;
        randomFactory = random;
    }

    /**
     * Long constructor assuming nothing
     * @param inputProgram Input Input program over the queries are going to be made
     * @param fragmentClass Class of the fragments
     * @param isStupid Is this a stupid transformation?
     */
    public ASTTransformationQuery(InputProgram inputProgram,
                                  Class fragmentClass, boolean isStupid, IRandomFactory random) {
        coverageReport = inputProgram.getCoverageReport();
        codeFragments = inputProgram.getCodeFragments();
        //
        codeFragmentClass = fragmentClass;
        stupid = isStupid;
        randomFactory = random;
    }


    @Override
    public void setType(String type) {

    }

    /**
     * Builds a random Transformation
     *
     * @return
     * @throws Exception
     */
    public ASTTransformation buildTransformation() throws Exception {
        IRandom r = randomFactory.buildRandomizer();
        ASTTransformation t = null;
        int i = r.nextInt(stupid ? 15 : 5);

        //All the methods regarding construction of transformation
        //should be declared as protected. Otherwise is a violation of the SOLID principle
        //because we may use this class as a Transformation factory, allowing the class
        //to have multiple responsibilities

        switch (i) {
            case 0:
            case 1:
                return replace();
            case 2:
            case 3:
                return add();
            case 4:
                return delete();
            case 5:
            case 6:
            case 7:
                return replaceRandom();
            case 8:
            case 9:
            case 10:
                return addRandom();
            case 11:
                return replaceWittgenstein();
            case 12:
                return addWittgenstein();
            case 13: {
                t = replace();
                t.setName("replaceReaction");
                return t;
            }
            case 14: {
                t = add();
                t.setName("addReaction");
                return t;
            }
        }
        return null;
    }

    protected ASTReplace replace() throws Exception {
        ASTReplace tf = new ASTReplace();
        CodeFragment cfToReplace = null;
        CodeFragment cfReplacedBy = null;

        while (cfReplacedBy == null) {
            cfToReplace = findRandomFragmentToReplace(true);
            cfReplacedBy = getCodeFragmentReplacedBy(cfToReplace, false);
        }
        tf.setTransplantationPoint(cfToReplace);
        tf.setCodeFragmentToReplace(cfReplacedBy);
        return tf;
    }

    protected ASTReplace replaceWittgenstein() throws Exception {
        ASTReplace tf = new ASTReplace();
        CodeFragment cfToReplace = null;
        CodeFragment cfReplacedBy = null;

        while (cfReplacedBy == null) {
            cfToReplace = findRandomFragmentToReplace(true);
            cfReplacedBy = getCodeFragmentReplacedBy(cfToReplace, true);
        }
        tf.setName("replaceWittgenstein");
        tf.setTransplantationPoint(cfReplacedBy);
        tf.setCodeFragmentToReplace(cfToReplace);
        return tf;
    }

    protected ASTReplace replaceRandom() throws Exception {
        ASTReplace tf = new ASTReplace();
        tf.setTransplantationPoint(findRandomFragmentToReplace(true));
        tf.setCodeFragmentToReplace(findRandomFragmentToReplace(false));
        tf.setName("replaceRandom");
        return tf;
    }

    protected ASTAdd addRandom() throws Exception {
        ASTAdd tf = new ASTAdd();
        tf.setTransplantationPoint(findRandomFragmentToReplace(true));
        tf.setCodeFragmentToAdd(findRandomFragmentToReplace(false));
        tf.setName("addRandom");
        return tf;
    }

    protected ASTReplace replace(CodeFragment cfToReplace, boolean varNameMatch) throws Exception {
        ASTReplace tf = new ASTReplace();

        CodeFragment cfReplacedBy = getCodeFragmentReplacedBy(cfToReplace, varNameMatch);
        if (cfReplacedBy == null)
            new Exception("pas de candidat pour " + cfToReplace);
        tf.setTransplantationPoint(cfToReplace);
        tf.setCodeFragmentToReplace(cfReplacedBy);
        return tf;
    }

//    public ASTReplace notContextReplace(CodeFragment cfToReplace) throws Exception {
//        Random r = new Random();
//        ASTReplace tf = new ASTReplace();
//        tf.setType("notContextReplace");
//        int size = codeFragments.size();
//        CodeFragment cfReplacedBy = codeFragments.get(r.nextInt(size));
//        tf.setTransplantationPoint(cfToReplace);
//        tf.setCodeFragmentToReplace(cfReplacedBy);
//        return tf;
//    }

    protected ASTAdd addWittgenstein() throws Exception {
        ASTAdd tf = new ASTAdd();
        CodeFragment cfToReplace = null;
        CodeFragment cfReplacedBy = null;

        while (cfReplacedBy == null) {
            cfToReplace = findRandomFragmentToReplace(true);
            cfReplacedBy = getCodeFragmentReplacedBy(cfToReplace, true);
        }
        tf.setTransplantationPoint(cfReplacedBy);
        tf.setCodeFragmentToAdd(cfToReplace);
        return tf;
    }

    protected ASTAdd add() throws Exception {
        ASTAdd tf = new ASTAdd();
        CodeFragment cfToReplace = null;
        CodeFragment cfReplacedBy = null;

        while (cfReplacedBy == null) {
            cfToReplace = findRandomFragmentToReplace(true);
            cfReplacedBy = getCodeFragmentReplacedBy(cfToReplace, false);
        }
        tf.setTransplantationPoint(cfToReplace);
        tf.setCodeFragmentToAdd(cfReplacedBy);
        return tf;
    }

    protected ASTAdd add(CodeFragment cfToReplace, boolean varNameMatch) throws Exception {
        ASTAdd tf = new ASTAdd();

        CodeFragment cfReplacedBy = getCodeFragmentReplacedBy(cfToReplace, varNameMatch);
        if (cfReplacedBy == null)
            new Exception("pas de candidat pour " + cfToReplace);
        tf.setTransplantationPoint(cfToReplace);
        tf.setCodeFragmentToAdd(cfReplacedBy);
        return tf;
    }

//    public ASTAdd notContextAdd(CodeFragment cfToReplace) throws Exception {
//        Random r = new Random();
//        ASTAdd tf = new ASTAdd();
//        tf.setName("notContextAdd");
//        int size = codeFragments.size();
//        CodeFragment cfReplacedBy = codeFragments.get(r.nextInt(size));
//        tf.setTransplantationPoint(cfToReplace);
//        tf.setCodeFragmentToAdd(cfReplacedBy);
//        return tf;
//    }

    protected ASTDelete delete() throws Exception {
        ASTDelete tf = new ASTDelete();
        CodeFragment cfToDelete = null;
        while (cfToDelete == null) {
            cfToDelete = findRandomFragmentToReplace(true);
            if (cfToDelete.getCtCodeFragment() instanceof CtReturn)
                cfToDelete = null;
        }
        tf.setTransplantationPoint(cfToDelete);
        return tf;
    }


    protected ASTDelete delete(CodeFragment cfToDelete) throws Exception {
        ASTDelete tf = new ASTDelete();
        tf.setTransplantationPoint(cfToDelete);
        return tf;
    }

    protected CodeFragment getCodeFragmentReplacedBy(CodeFragment cfToReplace, boolean varNameMatch) throws InstantiationException, IllegalAccessException {
        CodeFragment cfReplacedBy = null;
        if (cfReplacedBy == null) {
            cfReplacedBy = findRandomFragmentCandidate(cfToReplace, varNameMatch);
        }
        return cfReplacedBy;
    }

    /**
     * Find code fragments to replace, i.e, transplantation points
     *
     * @param withCoverage Indicates if the transplantation points must have coverage by the test suite.
     * @return
     */
    protected CodeFragment findRandomFragmentToReplace(boolean withCoverage) {
        IRandom r = randomFactory.buildRandomizer();
        int size = codeFragments.size();
        CodeFragment stmt = codeFragments.get(r.nextInt(size));

        while (withCoverage && coverageReport.codeFragmentCoverage(stmt) == 0)
            stmt = codeFragments.get(r.nextInt(size));
        return stmt;
    }

    /**
     * Find a random code fragment candidate to replace
     *
     *
     * @param cf
     * @param varNameMatch
     * @return
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    protected CodeFragment findRandomFragmentCandidate(CodeFragment cf,
                                                       boolean varNameMatch) throws IllegalAccessException, InstantiationException {
        List<CodeFragment> list = new ArrayList<CodeFragment>();
        for (CodeFragment codeFragment : getAllUniqueCodeFragments())
            if (cf.isReplace(codeFragment, varNameMatch) && !codeFragment.equalString().equals(cf.equalString())) {
                list.add(codeFragment);
            }

        if (list.isEmpty())
            return null;

        Random r = new Random();
        CtCodeElement tmp = (CtCodeElement) copyElem(list.get(r.nextInt(list.size())).getCtCodeFragment());
        CodeFragment ret = (CodeFragment) codeFragmentClass.newInstance();
        ret.init(tmp);
        return ret;
    }

    protected CtElement copyElem(CtElement elem) {
        Factory factory = elem.getFactory();
        CtElement tmp = factory.Core().clone(elem);
        tmp.setParent(elem.getParent());
        return tmp;
    }

    protected Collection<CodeFragment> getAllUniqueCodeFragments() {
        return codeFragments.getUniqueCodeFragmentList();
    }

}