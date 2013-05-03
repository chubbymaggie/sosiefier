package fr.inria.diversify.replace;

import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.codeFragment.CodeFragmentList;
import fr.inria.diversify.codeFragment.Statement;
import fr.inria.diversify.codeFragmentProcessor.StatementProcessor;
import fr.inria.diversify.codeFragmentProcessor.SubStatementVisitor;
import fr.inria.diversify.runtest.CoverageReport;
import org.json.JSONException;
import spoon.reflect.Factory;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtSimpleType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.*;

/**
 * User: Simon
 * Date: 4/25/13
 * Time: 2:42 PM
 */
public class Replace {

    protected String tmpDir;
    protected CoverageReport coverageReport;
    protected Factory factory;
    protected CodeFragmentList codeFragments;
    protected CodeFragment cfToReplace;
    protected CodeFragment cfReplacedBy;
    protected CtSimpleType<?> oldClass;
    protected CtSimpleType<?> newClass;


    public Replace(CodeFragmentList codeFragments, CoverageReport cr, String tmpDir) {
        this.tmpDir = tmpDir;
        this.codeFragments = codeFragments;

        this.factory = codeFragments.getCodeFragments().get(0).getCtCodeFragment().getFactory();
        this.coverageReport = cr;
    }

    public void setCodeFragmentToReplace(CodeFragment stmt) {
       cfToReplace = stmt;
    }


    protected CodeFragment getCodeFragmentToReplace() {
        if(cfToReplace == null)  {
//             choix d'une strategie de selection
            cfToReplace = randomCodeFragmentToReplace();
        }
        return cfToReplace;
    }

    protected CodeFragment getCodeFragmentReplacedBy() {
        if(cfReplacedBy == null)  {
//             choix d'une strategie de selection
            cfReplacedBy = findRandomCandidateStatement(cfToReplace);
        }
        return cfReplacedBy;
    }

    protected CodeFragment randomCodeFragmentToReplace(Class stmtType) {
        Random r = new Random();
        int size = getAllCodeFragments().size();
        CodeFragment s = getAllCodeFragments().get(r.nextInt(size));

        while (s.getClass() != stmtType && !coverageReport.statementCoverage(s))
            s = getAllCodeFragments().get(r.nextInt(size));
        return s;
    }

    protected CodeFragment randomCodeFragmentToReplace() {
        Random r = new Random();
        int size = getAllCodeFragments().size();
        CodeFragment stmt = getAllCodeFragments().get(r.nextInt(size));

        while(!coverageReport.statementCoverage(stmt))
            stmt = getAllCodeFragments().get(r.nextInt(size));
        return stmt;
    }

    protected CodeFragment findCodeFragment(String stmtString) {
        CodeFragment s = null;
        for (CodeFragment stmt : getAllCodeFragments())
            if (stmt.codeFragmentString().equals(stmtString))
                s = stmt;
        return s;
    }

    protected Statement findRandomCandidateStatement(CodeFragment stmt) {
        List<CodeFragment> list = new ArrayList<CodeFragment>();
        for (CodeFragment statement : getAllUniqueCodeFragments())
            if (stmt.isReplace(statement) && !statement.equalString().equals(stmt.equalString()))
               list.add(statement);

        if (list.isEmpty())
            return null;

       Random r = new Random();
        CtStatement tmp = (CtStatement)copyElem(list.get(r.nextInt(list.size())).getCtCodeFragment());
        return new Statement(tmp);
    }

    protected CtElement copyElem(CtElement elem) {
        CtElement tmp = factory.Core().clone(elem);
        tmp.setParent(elem.getParent());
        return tmp;
    }

    public Transformation replace() throws CompileException, IOException, JSONException {
        Transformation tf = new Transformation();
        while(cfReplacedBy == null) {
            cfToReplace = randomCodeFragmentToReplace();
            getCodeFragmentReplacedBy();
        }
        tf.setStatementToReplace(cfToReplace.toJSONObject());
        tf.setStatementReplacedBy(cfToReplace.toJSONObject());
        oldClass = factory.Core().clone(cfToReplace.getSourceClass());
        newClass = cfToReplace.getSourceClass();

        Statement tmp = new Statement((CtStatement)copyElem(cfReplacedBy.getCtCodeFragment()));

        Map<String,String> varMapping = cfToReplace.randomVariableMapping(tmp);
        tf.setVariableMapping(varMapping);
        System.out.println("random variable mapping: "+varMapping);
        cfToReplace.replace(tmp, varMapping);

        printJavaFile(tmpDir, newClass);
        compile(new File(tmpDir), newClass.getFactory());

        return tf;
    }



    public void restore() throws CompileException, IOException {
        List<CodeFragment> statementToRemove = new ArrayList<CodeFragment>();
        for (CodeFragment stmt : getAllCodeFragments())
              if(stmt.getCtCodeFragment().getParent(CtSimpleType.class).equals(newClass))
                  statementToRemove.add(stmt);

        SubStatementVisitor sub = new SubStatementVisitor();
        getAllCodeFragments().removeAll(statementToRemove);
        oldClass.accept(sub);
         StatementProcessor sp = new StatementProcessor();
        for (CtStatement stmt : sub.getStatements())
            sp.process(stmt);

        getAllCodeFragments().addAll(sp.getStatements().getCodeFragments());
        oldClass.setParent(newClass.getParent());

        System.out.println("oldClass "+oldClass.getSimpleName() + oldClass.getSignature());

        printJavaFile(tmpDir, oldClass);
        compile(new File(tmpDir), oldClass.getFactory());
    }

    public void printJavaFile(String repository, CtSimpleType<?> type) throws IOException {
        MyJavaOutputProcessor processor = new MyJavaOutputProcessor(new File(repository));
        processor.setFactory(type.getFactory());
        processor.createJavaFile(type);
    }

    //method a refaire
    public void compile(File directory, Factory f) throws CompileException {
//        JDTCompiler compiler = new JDTCompiler();
//        try {
//            compiler.compileSrc(f, allJavaFile(directory));
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        String tmp = "javac -encoding utf8 ";
        for (File file : allJavaFile(directory)) {
            tmp = tmp + file.toString() + " ";
        }
        try {
            Runtime r = Runtime.getRuntime();
            Process p = r.exec(tmp);
            BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            p.waitFor();
            String line;
            StringBuffer output = new StringBuffer();
            while ((line = reader.readLine()) != null) {
                output.append(line+"\n");
                if (line.contains(" error"))  {
                    reader.close();
                    throw new CompileException("error during compilation\n"+output);
                }
            }
            reader.close();
        } catch (IOException e) {
            throw new CompileException(e);
        } catch (InterruptedException e) {
            throw new CompileException(e);
        }
    }

    protected List<File> allJavaFile(File dir) {
        List<File> list = new ArrayList<File>();
        for (File file : dir.listFiles())
            if(file.isFile())     {
                if(file.getName().endsWith(".java"))
                    list.add(file);
            }
            else
                list.addAll(allJavaFile(file));
        return list;
    }

    protected List<CodeFragment> getAllCodeFragments() {
        return codeFragments.getCodeFragments();
    }

    protected Collection<CodeFragment> getAllUniqueCodeFragments() {
        return codeFragments.getUniqueCodeFragmentList();
    }
}