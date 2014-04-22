package fr.inria.diversify.transformation.cvl;

import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.cu.SourceCodeFragment;
import spoon.reflect.cu.SourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtSimpleType;

import java.io.*;

/**
 * User: Simon
 * Date: 25/02/14
 * Time: 15:05
 */
public class ObjectExistence extends CVLTransformation {

    public ObjectExistence() {
        type= "cvl";
        name = "objectExistence";
    }

    public void apply(String srcDir) throws Exception {

        if(transformationPoint instanceof CtPackage)
            applyToPackage(srcDir);
        else if(transformationPoint instanceof CtSimpleType)
            applyToClass(srcDir, transformationPoint);
        else {
            addSourceCode();
            printJavaFile(srcDir);
        }
        removeSourceCode();
    }

    @Override
    public void addSourceCode() throws Exception {
        logInfo();

        SourcePosition sp = transformationPoint.getPosition();
        CompilationUnit compileUnit = sp.getCompilationUnit();

        if(transformationPoint instanceof CtStatement && !(transformationPoint instanceof CtExpression))  {
            compileUnit.addSourceCodeFragment(new SourceCodeFragment(compileUnit.beginOfLineIndex(sp.getSourceStart()), "/** nodeType: "+transformationPoint.getClass()+"  \n", 0));
            compileUnit.addSourceCodeFragment(new SourceCodeFragment(compileUnit.nextLineIndex(sp.getSourceEnd()), "**/\n", 0));
        }
        else {
            int[] index = findIndex();
            compileUnit.addSourceCodeFragment(new SourceCodeFragment(index[0], "/**", 0));
            compileUnit.addSourceCodeFragment(new SourceCodeFragment(index[1] + 1, "**/", 0));
        }

    }

    protected int[] findIndex() throws IOException {
        SourcePosition sp = transformationPoint.getPosition();
        FileInputStream fstream = new FileInputStream(sp.getFile());
        BufferedReader br = new BufferedReader(new InputStreamReader(fstream));

        int s;
        if(sp.getSourceStart() < 100)
            s = sp.getSourceStart();
        else
            s = 100;

        br.skip(sp.getSourceStart() - s);
        char before[] = new char[s];
        for(int i = 0; i < s; i++)
            before[i] = (char)br.read();

        int r;
        s = 0;
        br.skip(sp.getSourceEnd() - sp.getSourceStart()+1);
        char after[] = new char[100];
        while((r = br.read()) != -1 && s < 100) {
            after[s] = (char)r;
            s++;
        }

        int index[] = new int[2];
        index[0] = sp.getSourceStart();
        index[1] = sp.getSourceEnd();

        for(int i = 0; i < after.length; i++) {
            if(after[i] == '.') {
                index[1] = sp.getSourceEnd() + i + 1;
                return index;
            }
            if(!(after[i] == '\n' || after[i] == '\t' || after[i] == ' '))
                break;
        }

        for(int i = 1; i <= before.length; i++) {
            char c = before[before.length - i];
            if(c == '=') {
                index[0] = sp.getSourceStart() - i;
                return index;
            }
            if(!(c == '\n' || c == '\t' || c == ' '))
                break;
        }

        for(int i = 1; i <= before.length; i++) {
            char c = before[before.length - i];
            if(c == ',') {
                index[0] = sp.getSourceStart() - i;
                return index;
            }
            if(!(c == '\n' || c == '\t' || c == ' '))
                break;
        }

        for(int i = 0; i < after.length; i++) {
            if(after[i] == ',') {
                index[1] = sp.getSourceEnd() + i +1;
                return index;
            }
            if(!(after[i] == '\n' || after[i] == '\t' || after[i] == ' '))
                break;
        }

        return  index;
    }

    protected void applyToClass(String srcDir, CtElement cl) throws IOException {

        SourcePosition sp = cl.getPosition();
        CompilationUnit compileUnit = sp.getCompilationUnit();

        compileUnit.addSourceCodeFragment(new SourceCodeFragment(sp.getSourceStart(), "/**", 0));
        compileUnit.addSourceCodeFragment(new SourceCodeFragment(sp.getSourceEnd()+1, "**/", 0));

        printJavaFile(srcDir);
        removeSourceCode();
    }

    protected void applyToPackage(String srcDir) throws IOException {
        for(CtSimpleType<?> cl : ((CtPackage) transformationPoint).getTypes()) {
            applyToClass(srcDir, cl);
            printJavaFile(srcDir);
            removeSourceCode(cl);
        }
    }

    @Override
    public void restore(String srcDir) throws Exception {
        if(transformationPoint instanceof CtPackage)
            restorePackage(srcDir);
        else if(transformationPoint instanceof CtSimpleType)
            restoreClass(srcDir, (CtSimpleType)transformationPoint);
        else {
            removeSourceCode();
            printJavaFile(srcDir);
        }
    }

    protected void restorePackage(String srcDir) throws IOException {
        for(CtSimpleType<?> cl : ((CtPackage) transformationPoint).getTypes()) {
            restoreClass(srcDir, cl);
            printJavaFile(srcDir,cl);
        }
    }

    protected void restoreClass(String srcDir, CtSimpleType cl) throws IOException {
        printJavaFile(srcDir,cl);
    }

    protected void   removeSourceCode(CtSimpleType cl) {
          CompilationUnit compileUnit = cl.getPosition().getCompilationUnit();
              if(compileUnit.getSourceCodeFraments() != null)
                    compileUnit.getSourceCodeFraments().clear();
    }

    public boolean equals(Object other) {
        if(other == null)
            return false;
        if(!this.getClass().isAssignableFrom(other.getClass()))
            return  false;

        ObjectExistence o = (ObjectExistence)other;

        try {
        return stmtType().equals(o.stmtType())
                && transformationPoint.equals(o.transformationPoint);
        } catch (Exception e) {}
        return false;
    }
}
