package fr.inria.diversify;

import java.io.File;
import java.io.IOException;
import java.util.*;

import fr.inria.diversify.diversification.*;
import fr.inria.diversify.factory.RandomFactory;
import fr.inria.diversify.statistic.CVLMetric;
import fr.inria.diversify.statistic.StatisticDiversification;
import fr.inria.diversify.transformation.TransformationParser;
import fr.inria.diversify.transformation.TransformationParserException;
import fr.inria.diversify.transformation.TransformationsWriter;
import fr.inria.diversify.buildSystem.AbstractBuilder;
import fr.inria.diversify.buildSystem.ant.AntBuilder;
import fr.inria.diversify.buildSystem.maven.MavenBuilder;
import fr.inria.diversify.transformation.query.*;
import fr.inria.diversify.transformation.query.ASTTransformationQuery;
import fr.inria.diversify.transformation.query.searchStrategy.SearchStrategy;
import fr.inria.diversify.util.DiversifyEnvironment;
import fr.inria.diversify.buildSystem.maven.MavenDependencyResolver;
import fr.inria.diversify.visu.Visu;
import javassist.NotFoundException;

import spoon.compiler.SpoonCompiler;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import fr.inria.diversify.coverage.CoverageReport;
import fr.inria.diversify.coverage.ICoverageReport;
import fr.inria.diversify.coverage.MultiCoverageReport;
import fr.inria.diversify.coverage.NullCoverageReport;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.query.ByteCodeTransformationQuery;
import fr.inria.diversify.util.DiversifyProperties;
import fr.inria.diversify.util.Log;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

/**
 * Main class for the sosie generator.
 * <p>
 * User: Simon
 * Date: 9/11/13
 * Time: 11:41 AM
 */
public class DiversifyMain {

    /**
     * The input program that we are about to sosiefy
     */
    private InputProgram inputProgram;

    /**
     * The input configuration given by the user is parsed by this class which helps other parts of the program to
     * interact with the input parameters
     */
    private InputConfiguration inputConfiguration;

    public DiversifyMain(String propertiesFile) throws Exception {

        new DiversifyProperties(propertiesFile);

        inputConfiguration = new InputConfiguration(propertiesFile);

        initLogLevel();
        if (DiversifyProperties.getProperty("builder").equals("maven")) {
            MavenDependencyResolver t = new MavenDependencyResolver();
            t.DependencyResolver(DiversifyProperties.getProperty("project") + "/pom.xml");
        }
        initSpoon();

        if (DiversifyProperties.getProperty("stat").equals("true")) {
            computeStatistic();

        } else {
            if (DiversifyProperties.getProperty("sosieOnMultiProject").equals("true")) {
//            sosieOnMultiProject();
            } else initAndRunBuilder();
        }
    }

    protected void initAndRunBuilder() throws Exception {
        AbstractDiversify abstractDiversify = initAbstractDiversify();

        TransformationQuery query = initTransformationQuery();
        abstractDiversify.setTransformationQuery(query);

        int n = Integer.parseInt(DiversifyProperties.getProperty("nbRun"));
        abstractDiversify.run(n);

        String repo = DiversifyProperties.getProperty("gitRepository");
        if (repo.equals("null")) abstractDiversify.printResult(DiversifyProperties.getProperty("result"));
        else abstractDiversify.printResult(DiversifyProperties.getProperty("result"), repo + "/sosie-exp");
    }

    protected AbstractDiversify initAbstractDiversify() throws Exception {
        AbstractDiversify ad;
        String projet = DiversifyProperties.getProperty("project");
        String src = DiversifyProperties.getProperty("src");
        if (DiversifyProperties.getProperty("transformation.type").equals("mutationToSosie"))
            ad = new DiversifyWithParent(projet, src);
        else if (DiversifyProperties.getProperty("sosie").equals("false")) ad = new Diversify(projet, src);
        else if (DiversifyProperties.getProperty("sosie").equals("classic")) {
            String testDir = DiversifyProperties.getProperty("testSrc");
            ad = new Sosie(projet, src, testDir);
        } else ad = new SosieWithParent(projet, src);

        String tmpDir = ad.init(projet, DiversifyProperties.getProperty("tmpDir"));
        ad.setBuilder(initBuilder(tmpDir));

        return ad;
    }


    protected AbstractBuilder initBuilder(String directory) throws Exception {
        AbstractBuilder rb;
        String src = DiversifyProperties.getProperty("src");
        if (DiversifyProperties.getProperty("builder").equals("maven")) {
            rb = new MavenBuilder(directory, src);
            rb.setPhase(new String[]{"clean", "test"});
        } else {
            rb = new AntBuilder(directory, DiversifyProperties.getProperty("builder.testTarget"));
            rb.setPhase(new String[]{"clean", DiversifyProperties.getProperty("builder.testTarget")});
        }
        int t = Integer.parseInt(DiversifyProperties.getProperty("timeOut"));
        if (t == -1) rb.initTimeOut();
        else rb.setTimeOut(t);

        String pomFile = DiversifyProperties.getProperty("newPomFile");
        if (!pomFile.equals("")) rb.initPom(pomFile);

        if (DiversifyProperties.getProperty("clojure").equals("true")) rb.setClojureTest(true);
        return rb;
    }

    /**
     * Initializes the InputProgram dataset
     */
    protected void initInputProgram(Factory factory) {
        inputProgram = new InputProgram();
        inputProgram.setFactory(factory);

        inputProgram.setCoverageReport(initCoverageReport());

        //TODO: See how get rid of the Environment static

        //TODO: See hot to get rid of the Properties static
        inputProgram.setTransformationPerRun(
            Integer.parseInt(DiversifyProperties.getProperty("transformation.nb", "1")));

        //Path to pervious transformations made to this input program
        inputProgram.setPreviousTransformationsPath(
                DiversifyProperties.getProperty("transformation.directory"));

        inputProgram.setClassesDir(DiversifyProperties.getProperty("project") + "/" +
                DiversifyProperties.getProperty("classes"));

        inputProgram.setCoverageDir(DiversifyProperties.getProperty("jacoco"));
    }

    protected TransformationQuery initTransformationQuery() throws ClassNotFoundException, NotFoundException, TransformationParserException {

        String type = DiversifyProperties.getProperty("transformation.type");

        SearchStrategy potStrategy = inputConfiguration.buildPotSearchStrategy();
        SearchStrategy transplantStrategy = inputConfiguration.buildTransplantSearchStrategy();

        switch (type) {
            case "mutation":
                return new MutationQuery(inputProgram);
            case "shuffle":
                return new ShuffleStmtQuery(inputProgram);
            case "other":
                return new OtherQuery(inputProgram);
            case "all":
                return new CompositeQuery(new MutationQuery(inputProgram),
                        new ASTTransformationQuery(inputProgram, potStrategy, transplantStrategy));
            case "cvl":
                return new CvlQuery(inputProgram);
            case "bytecode":
                return new ByteCodeTransformationQuery(inputProgram);
            case "mutationToSosie": {
                /*
                String jacocoFile = DiversifyProperties.getProperty("jacoco");
                String classes = DiversifyProperties.getProperty("project") + "/" + DiversifyProperties.getProperty("classes");
                String mutationDirectory = DiversifyProperties.getProperty("transformation.directory");
                */
                return new MutationToSosieQuery(inputProgram, potStrategy, transplantStrategy);
            }
            case "ADR": {
                Class cl = Class.forName(DiversifyProperties.getProperty("CodeFragmentClass"));
                return new ASTTransformationQuery(inputProgram,  potStrategy, transplantStrategy, cl, false);
            }
            case "ADRStupid": {
                Class cl = Class.forName(DiversifyProperties.getProperty("CodeFragmentClass"));
                return new ASTTransformationQuery(inputProgram,  potStrategy, transplantStrategy, cl, true);
            }
            /*
            case "list": {
                String transDirectory = DiversifyProperties.getProperty("transformation.directory");
                return new TransformationQueryFromList(inputProgram, new RandomFactory());
            }
            case "multi": {
                String transDirectory = DiversifyProperties.getProperty("transformation.directory");
                int nbTransformation = Integer.parseInt(DiversifyProperties.getProperty("transformation.nb"));
                return new ASTMultiTransformationQuery(inputProgram, new RandomFactory());
            }*/
        }
        return null;
    }

    protected ICoverageReport initCoverageReport() {
        String jacocoFile = DiversifyProperties.getProperty("jacoco");
        String classes = DiversifyProperties.getProperty("project") + "/" + DiversifyProperties.getProperty("classes");

        ICoverageReport icr = null;
        if (jacocoFile != null) {
            try {
                File file = new File(jacocoFile);
                if (file.isDirectory()) icr = new MultiCoverageReport(classes, file);
                else icr = new CoverageReport(classes, file);
                icr.create();
                return icr;
            } catch (IOException e) {
                Log.warn("Unable to find coverage file or corrupt information: using NullCoverage");
            }
        }

        if (icr == null) {
            icr = new NullCoverageReport();
        }

        return icr;
    }


    protected void initSpoon() throws ClassNotFoundException, IllegalAccessException, InstantiationException {
        String srcDirectory = DiversifyProperties.getProperty("project") + "/" + DiversifyProperties.getProperty("src");

        StandardEnvironment env = new StandardEnvironment();
        int javaVersion = Integer.parseInt(DiversifyProperties.getProperty("javaVersion"));
        env.setComplianceLevel(javaVersion);
        env.setVerbose(true);
        env.setDebug(true);

        DefaultCoreFactory f = new DefaultCoreFactory();
        Factory factory = new FactoryImpl(f, env);
        SpoonCompiler compiler = new JDTBasedSpoonCompiler(factory);
        for (String dir : srcDirectory.split(System.getProperty("path.separator")))
            try {
                Log.debug("add {} to classpath", dir);
                compiler.addInputSource(new File(dir));
            } catch (IOException e) {
                Log.error("error in initSpoon", e);
            }
        try {
            compiler.build();
        } catch (Exception e) {
            e.printStackTrace();
        }

        initInputProgram(factory);
        DiversifyEnvironment.setFactory(factory);
    }

    protected void computeStatistic() throws Exception {
        String out = DiversifyProperties.getProperty("result");
//        computeCodeFragmentStatistic(out);

        String transDir = DiversifyProperties.getProperty("transformation.directory");
        if (transDir != null) {
            computeDiversifyStat(transDir, out);
        }
//        computeOtherStat();
    }

    protected void computeDiversifyStat(String transDir, String fileName) throws Exception {
        TransformationParser tf = new TransformationParser(true, inputProgram);
//        TransformationOldParser tf = new TransformationOldParser(true);
        Collection<Transformation> transformations = tf.parseDir(transDir);
        TransformationsWriter write = new TransformationsWriter(transformations, fileName);


        Log.debug("all transformation type : {}", getAllTransformationType(transformations));
        write.writeAllTransformation(null);
        StatisticDiversification sd = new StatisticDiversification(transformations);
        sd.writeStat(fileName);


        for (String type : getAllTransformationType(transformations))
            write.writeAllTransformation(type);

        write.writeGoodTransformation(null);

        for (String type : getAllTransformationType(transformations))
            write.writeGoodTransformation(type);


        CVLMetric cvlMetric = new CVLMetric(inputProgram);
        cvlMetric.printMetrics(fileName + "_cvlMetric.csv");

        Visu v = new Visu(fileName + "_visu/visu", inputProgram);
        v.writeJSON(transformations);

//        FailureMatrix matrix = new FailureMatrix(transformations,DiversifyProperties.getProperty("allTestFile"));
//        matrix.printAllMatrix(fileName);
    }

    protected Set<String> getAllTransformationType(Collection<Transformation> transformations) {
        Set<String> types = new HashSet<String>();
        for (Transformation t : transformations)
            types.add(t.getType());
        return types;
    }


    protected void initLogLevel() {
        int level = Integer.parseInt(DiversifyProperties.getProperty("logLevel"));
        Log.set(level);
    }

    protected void sosieOnMultiProject() throws Exception {
//        TestSosie d = new TestSosie(initTransformationQuery(), DiversifyProperties.getProperty("project"));
//
//        List<String> list = new ArrayList<String>();
//        for (String mvn : DiversifyProperties.getProperty("mavenProjects").split(System.getProperty("path.separator")))
//            list.add(mvn);
//        d.setMavenProject(list);
//
//        initAndRunBuilder(d);
    }

}
