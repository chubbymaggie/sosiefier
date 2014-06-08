package fr.inria.diversify.diversification;

import fr.inria.diversify.codeFragment.CodeFragment;
import fr.inria.diversify.codeFragment.Statement;
import fr.inria.diversify.transformation.query.searchStrategy.SearchStrategy;
import fr.inria.diversify.transformation.query.searchStrategy.SimpleRandomStrategy;
import fr.inria.diversify.util.Log;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * The input configuration class encapsulates all the data and asociated behavior we obtain from the input properties
 * given by the user.
 *
 * The InputConfiguration makes other objects life easier by not only being a data holder but also by implementing
 * behaviors related from this data as the building of new CodeFragments given the CodeFragmentClass property
 *
 * Created by marcel on 8/06/14.
 */
public class InputConfiguration {

    protected Properties prop;

    public InputConfiguration(String file) throws IOException, ClassNotFoundException {
        prop = new Properties();
        setDefaultProperties();
        prop.load(new FileInputStream(file));
        setCodeFragmentClass();
    }

    /**
     * Returns the transplantation point search strategy given by the user in the input parameters
     *
     * @return A SearchStrategy instance
     */
    public SearchStrategy getNewTransplantationPointStrategy() {
        return new SimpleRandomStrategy();
    }

    /**
     * Returns a new code fragment given the processing level set by the user in the input properties
     *
     * Defaults to statement in case of any error
     *
     * @return
     */
    public CodeFragment getNewCodeFragment()  {
        CodeFragment result;
        try {
            Class cl = Class.forName(prop.getProperty("CodeFragmentClass"));
            result = (CodeFragment)cl.newInstance();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException e) {
            result = new Statement();
            Log.warn("Unable to create the code fragment requested, resolved to Statement level", e);
            e.printStackTrace();
        }
        return result;
    }

    protected void setCodeFragmentClass() {
        if(prop.getProperty("processor").equals("fr.inria.diversify.codeFragmentProcessor.StatementProcessor"))
            prop.setProperty("CodeFragmentClass", "fr.inria.diversify.codeFragment.Statement");
        if(prop.getProperty("processor").equals("fr.inria.diversify.codeFragmentProcessor.ExpressionProcessor"))
            prop.setProperty("CodeFragmentClass", "fr.inria.diversify.codeFragment.Expression");
        if(prop.getProperty("processor").equals("fr.inria.diversify.codeFragmentProcessor.BlockProcessor"))
            prop.setProperty("CodeFragmentClass", "fr.inria.diversify.codeFragment.Block");
    }

    protected void setDefaultProperties() {
        prop.setProperty("src", "src/main/java");
        prop.setProperty("testSrc", "src/test/java");
        prop.setProperty("classes", "target/classes");
        prop.setProperty("clojure","false");
        prop.setProperty("javaVersion", "5");
        prop.setProperty("transformation.type","all");
        prop.setProperty("transformation.size","1");
        prop.setProperty("stat", "false");
        prop.setProperty("sosie", "false");

        //tempory directory
        prop.setProperty("tmpDir", "tmpDir");

        //directory for output (ex sosie)
        prop.setProperty("outputDirectory", "output");

        //directory for output result (ex sosie)
        prop.setProperty("result", "output_diversify");

        //file name with path for result (ex json file, stat file)
        //if gitRepository != null all result are put in gitRepository/result
        prop.setProperty("result", "output_diversify");

        prop.setProperty("sosieOnMultiProject","false");
        prop.setProperty("timeOut","-1");
        prop.setProperty("logLevel", "2");
        prop.setProperty("gitRepository", "null");
        prop.setProperty("processor", "fr.inria.diversify.codeFragmentProcessor.StatementProcessor");
        prop.setProperty("syncroRange","0");
        prop.setProperty("newPomFile","");
        prop.setProperty("transformation.level","statement");
        prop.setProperty("builder","maven");
    }
}