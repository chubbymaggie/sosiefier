package fr.inria.diversify;

import fr.inria.diversify.codeFragment.CodeFragmentList;
import fr.inria.diversify.codeFragmentProcessor.StatementProcessor;
import fr.inria.diversify.coverage.CoverageReport;
import fr.inria.diversify.coverage.ICoverageReport;
import fr.inria.diversify.coverage.MultiCoverageReport;
import fr.inria.diversify.coverage.NullCoverageReport;
import fr.inria.diversify.diversification.Builder;
import fr.inria.diversify.diversification.Diversify;
import fr.inria.diversify.diversification.Sosie;
import fr.inria.diversify.diversification.TestSosie;
import fr.inria.diversify.statistic.StatisticCodeFragment;
import fr.inria.diversify.statistic.StatisticDiversification;
import fr.inria.diversify.statistic.Util;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.TransformationParser;
import fr.inria.diversify.transformation.TransformationsWriter;
import fr.inria.diversify.transformation.query.AbstractTransformationQuery;
import fr.inria.diversify.transformation.query.TransformationQuery;
import fr.inria.diversify.transformation.query.TransformationQueryTL;
import fr.inria.diversify.util.DiversifyProperties;
import fr.inria.diversify.util.GitUtil;
import fr.inria.diversify.util.Log;
import org.json.JSONException;
import spoon.processing.ProcessingManager;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonBuildingManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Simon
 * Date: 9/11/13
 * Time: 11:41 AM
 */
public class DiversifyMain {
    private CodeFragmentList statements;

    public DiversifyMain(String propertiesFile) throws Exception {
        new DiversifyProperties(propertiesFile);

        initLogLevel();
        initSpoon();
//        Log.info("number of cpu: "+numberOfCpu());
        Log.info("number of statement: " + statements.size());

        if (DiversifyProperties.getProperty("sosie").equals("true"))
            buildSosie();
        else if (DiversifyProperties.getProperty("sosieOnMultiProject").equals("true"))
            sosieOnMultiProject();
        else
            runDiversification();

        if (DiversifyProperties.getProperty("stat").equals("true")) {
            computeStatistic();
        }
    }

    protected void buildSosie() throws Exception {
        Sosie d = new Sosie(initTransformationQuery(), DiversifyProperties.getProperty("project"));
        initAndRunBuilder(d);
    }

    protected void runDiversification() throws Exception {
        Diversify d = new Diversify(initTransformationQuery(), DiversifyProperties.getProperty("project"));
        String git = DiversifyProperties.getProperty("gitRepository");
        if (!git.equals("")) {
            GitUtil.initGit(git);
        }
        initAndRunBuilder(d);

        d.printResult(DiversifyProperties.getProperty("result"), git + "/diversify-exp");
    }

    protected void sosieOnMultiProject() throws Exception {
        TestSosie d = new TestSosie(initTransformationQuery(), DiversifyProperties.getProperty("project"));

        List<String> list = new ArrayList<String>();
        for (String mvn : DiversifyProperties.getProperty("mavenProjects").split(System.getProperty("path.separator")))
            list.add(mvn);
        d.setMavenProject(list);

        initAndRunBuilder(d);
    }

    protected void initAndRunBuilder(Builder builder) throws Exception {
        builder.setSourceDirectory(DiversifyProperties.getProperty("src"));

        int t = Integer.parseInt(DiversifyProperties.getProperty("timeOut"));
        if (t == -1)
            builder.initTimeOut();
        else
            builder.setTimeOut(t);

        builder.setTmpDirectory(DiversifyProperties.getProperty("outputDir"));

        if (DiversifyProperties.getProperty("clojure").equals("true"))
            builder.setClojureTest(true);

        //TODO refactor
        if (DiversifyProperties.getProperty("nbRun").equals("all")) {
            Util util = new Util(statements);
            if (DiversifyProperties.getProperty("transformation.type").equals("replace"))
                builder.run(util.getAllReplace());
            if (DiversifyProperties.getProperty("transformation.type").equals("add"))
                builder.run(util.getAllAdd());
            if (DiversifyProperties.getProperty("transformation.type").equals("delete"))
                builder.run(util.getAllDelete());
        } else {
            int n = Integer.parseInt(DiversifyProperties.getProperty("nbRun"));
            builder.run(n);
        }
    }

    protected AbstractTransformationQuery initTransformationQuery() throws IOException, JSONException {
        ICoverageReport rg = initCoverageReport();

        AbstractTransformationQuery atq;
        String transformation = DiversifyProperties.getProperty("transformation.directory");
        if (transformation != null) {
            TransformationParser tf = new TransformationParser(statements);
            List<Transformation> list = tf.parseDir(transformation);
            atq = new TransformationQueryTL(list, rg, statements);
        } else {
            atq = new TransformationQuery(rg, statements);
        }
        atq.setType(DiversifyProperties.getProperty("transformation.type"));
        int n = Integer.parseInt(DiversifyProperties.getProperty("transformation.size"));
        atq.setNbTransformation(n);

        return atq;
    }

    protected void initSpoon() {
        String srcDirectory = DiversifyProperties.getProperty("project") + "/" + DiversifyProperties.getProperty("src");

        StandardEnvironment env = new StandardEnvironment();
        int javaVersion = Integer.parseInt(DiversifyProperties.getProperty("javaVersion"));
        env.setComplianceLevel(javaVersion);
        env.setVerbose(true);
        env.setDebug(true);

        DefaultCoreFactory f = new DefaultCoreFactory();
        Factory factory = new Factory(f, env);
        SpoonBuildingManager builder = new SpoonBuildingManager(factory);

        for (String dir : srcDirectory.split(System.getProperty("path.separator")))
            try {
                builder.addInputSource(new File(dir));
            } catch (IOException e) {
                e.printStackTrace();
            }
        try {
            builder.build();
        } catch (Exception e) {
            e.printStackTrace();
        }
        ProcessingManager pm = new QueueProcessingManager(factory);
        StatementProcessor processor = new StatementProcessor();
        pm.addProcessor(processor);
        pm.process();

        statements = processor.getStatements();
    }

    protected ICoverageReport initCoverageReport() throws IOException {
        ICoverageReport icr;
        String jacocoFile = DiversifyProperties.getProperty("jacoco");
        String classes;
        if (DiversifyProperties.getProperty("jacoco.classes") != null)
            classes = DiversifyProperties.getProperty("jacoco.classes");
        else
            classes = DiversifyProperties.getProperty("project") + "/" + DiversifyProperties.getProperty("classes");

        if (jacocoFile != null) {
            File file = new File(jacocoFile);
            if (file.isDirectory())
                icr = new MultiCoverageReport(classes, file);
            else
                icr = new CoverageReport(classes, file);
        } else
            icr = new NullCoverageReport();

        icr.create();
        return icr;
    }

    protected void computeStatistic() throws IOException, JSONException {
        String out = DiversifyProperties.getProperty("result");
        computeCodeFragmentStatistic(out);

        String transDir = DiversifyProperties.getProperty("transformation.directory");
        if (transDir != null) {
            computeDiversifyStat(transDir, out);
        }
        computeOtherStat();
    }

    protected void computeDiversifyStat(String transDir, String fileName) throws IOException, JSONException {
        TransformationParser tf = new TransformationParser(statements);
        List<Transformation> transformations = tf.parseDir(transDir);
        TransformationsWriter write = new TransformationsWriter(transformations, fileName);

        String name = write.writeAllTransformation(null);
        statForR(name);
        name = write.writeAllTransformation("add");
        statForR(name);
        name = write.writeAllTransformation("delete");
        statForR(name);
        name = write.writeAllTransformation("replace");
        statForR(name);

        name = write.writeGoodTransformation(null);
        statForR(name);
        name = write.writeGoodTransformation("add");
        statForR(name);
        name = write.writeGoodTransformation("delete");
        statForR(name);
        name = write.writeGoodTransformation("replace");
        statForR(name);

//        StatisticDiversification sd = new StatisticDiversification(transformations, statements);
//        sd.writeStat(fileName);
        computeOtherStat();
    }

    protected void statForR(String fileName) throws IOException, JSONException {
        TransformationParser tf = new TransformationParser(statements);
        Log.debug("parse fileName: {}",fileName);
        List<Transformation> transformations = tf.parseFile(new File(fileName));
        StatisticDiversification sd = new StatisticDiversification(transformations, statements);
        Log.debug("number of transformation: {}",transformations.size());
        String name = fileName.split(".json")[0];
        sd.writeStat(name);

    }

    protected void computeOtherStat() {
        Util stat = new Util(statements);
        System.out.println("number of possible code fragment replace: " + stat.numberOfDiversification());
        System.out.println("number of not possible code fragment replace/add: " + stat.numberOfNotDiversification());
        System.out.println("number of possible code fragment add: " + stat.getAllAdd().size());
        System.out.println("number of possible code fragment delete: " + stat.getAllDelete().size());
    }

    protected void computeCodeFragmentStatistic(String output) {
        StatisticCodeFragment stat = new StatisticCodeFragment(statements);
        try {
            stat.writeStatistic(output);
        } catch (IOException e) {
            Log.error("computeCodeFragmentStatistic ", e);
        }
    }

    protected void initLogLevel() {
        int level = Integer.parseInt(DiversifyProperties.getProperty("logLevel"));
        Log.set(level);
    }
}