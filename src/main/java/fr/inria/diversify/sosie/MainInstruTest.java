package fr.inria.diversify.sosie;

    import fr.inria.diversify.sosie.logger.processor.TestLoggingInstrumenter;
import spoon.processing.ProcessingManager;
import spoon.reflect.Factory;
import spoon.support.DefaultCoreFactory;
import spoon.support.JavaOutputProcessor;
import spoon.support.QueueProcessingManager;
import spoon.support.StandardEnvironment;
import spoon.support.builder.SpoonBuildingManager;

import java.io.File;
import java.io.IOException;

/**
 * User: Simon
 * Date: 7/22/13
 * Time: 2:03 PM
 */
public class MainInstruTest {
    public static void main(String[] args) throws Exception {
        new MainInstruTest(args[0], args[1]);
    }

    public MainInstruTest(String srcDirectory, String outputDir) throws Exception {
        initSpoon(srcDirectory, outputDir);
    }

    protected void initSpoon(String srcDirectory, String outputDir) {

        StandardEnvironment env = new StandardEnvironment();
//        int javaVersion = Integer.parseInt(DiversifyProperties.getProperty("javaVersion"));
//        env.setComplianceLevel(javaVersion);
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
        TestLoggingInstrumenter processor = new TestLoggingInstrumenter();
        pm.addProcessor(processor);
        pm.process();

        pm = new QueueProcessingManager(factory);
        JavaOutputProcessor p2 = new JavaOutputProcessor(new File(outputDir));
        env.useSourceCodeFragments(true);
        pm.addProcessor(p2);
        pm.process();
    }
}