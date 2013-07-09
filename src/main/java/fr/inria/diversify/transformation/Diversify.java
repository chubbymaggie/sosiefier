package fr.inria.diversify.transformation;

import fr.inria.diversify.codeFragment.CodeFragmentList;
import fr.inria.diversify.runtest.ICoverageReport;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * User: Simon
 * Date: 5/2/13
 * Time: 5:39 PM
 */
public class Diversify {

    protected String projectDir;
    protected String tmpDir;
    protected CodeFragmentList codeFragments;
    protected ICoverageReport coverageReport;
    protected List<Transformation> transformations;
    protected Set<Thread> threadSet;
    protected String srcDir;
    protected boolean clojureTest;
    protected int timeOut;

    public Diversify(CodeFragmentList codeFragments, ICoverageReport coverageReport, String projectDir) {
        this.coverageReport = coverageReport;
        this.codeFragments = codeFragments;
        this.tmpDir = "output_diversify";

        this.srcDir = "src/main/java";
        this.projectDir = projectDir;
        clojureTest = false;
        timeOut = 200;

    }

    public void run(int n) throws Exception {
        transformations = new ArrayList<Transformation>();
        int error = 0;
        String dir = prepare(projectDir, tmpDir);
        for (int i = 0; i < n; i++) {
            System.out.println(i);
            initThreadGroup();
            System.out.println("output dir: " + dir + "/" + srcDir);
            TransformationQuery transQuery = new TransformationQuery(coverageReport, codeFragments);
            Transformation tf = null;
            try {
                tf = transQuery.randomDelete();
                tf.apply(dir + "/" + srcDir);
                int failures = runTest(dir);
                tf.setJUnitResult(failures);
                transformations.add(tf);

            } catch (Exception e) {
                System.out.println("compile error " + (error++));
            }
            tf.restore(dir + "/" + srcDir);
            killUselessThread();

        }
    }

    public void printResult(String output) {
        try {
            writeTransformation(output + System.currentTimeMillis() + "_transformation.json");
        } catch (Exception e) {
            e.printStackTrace();
        }
//        StatisticDiversification stat = new StatisticDiversification(transformations);
//        stat.writeStat(output);
    }

    public void writeTransformation(String FileName) throws IOException, JSONException {
        if (transformations.isEmpty())
            return;
        BufferedWriter out = new BufferedWriter(new FileWriter(FileName));
        JSONArray obj = new JSONArray();
        for (int i = 0; i < transformations.size(); i++) {
            try {
                obj.put(transformations.get(i).toJSONObject());
            } catch (Exception e) {
            }
        }
        out.write(obj.toString());
        out.newLine();
        out.close();
    }

    protected String prepare(String dirSource, String dirTarget) throws IOException, InterruptedException {
        String dir = dirTarget + "/tmp_" + System.currentTimeMillis();
//        FileUtils.copyDirectory(new File(dirSource), new File(dir));
        copyDirectory(new File(dirSource), new File(dir));
        return dir;
    }

    protected Integer runTest(String directory) throws InterruptedException, CompileException {
        RunMaven rt = new RunMaven(directory, "test", clojureTest);
        rt.start();
        int count = 0;
        while (rt.getFailures() == null && count < timeOut) {
            count++;
            Thread.sleep(1000);
        }
        System.out.println(rt.getCompileError() + " " + rt.allTestRun() + " " + rt.getFailures());
        if (rt.getCompileError())
            throw new CompileException("error ");

        if (!rt.allTestRun())
            return -1;
        return rt.getFailures();
    }

    protected void initThreadGroup() {
        threadSet = Thread.getAllStackTraces().keySet();
    }

    protected void killUselessThread() {
        for (Thread thread : Thread.getAllStackTraces().keySet()) {
            if (!threadSet.contains(thread)) {
                thread.stop();
            }
        }
    }

    protected void copyDirectory(File sourceLocation, File targetLocation) throws IOException {
        if (sourceLocation.isDirectory()) {
            if (!targetLocation.exists()) {
                targetLocation.mkdir();
            }

            String[] children = sourceLocation.list();
            for (int i = 0; i < children.length; i++) {
                copyDirectory(new File(sourceLocation, children[i]),
                        new File(targetLocation, children[i]));
            }
        } else {

            InputStream in = new FileInputStream(sourceLocation);
            OutputStream out = new FileOutputStream(targetLocation);

            // Copy the bits from instream to outstream
            byte[] buf = new byte[1024];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
        }
    }

    public void setTmpDirectory(String tmpDir) {
        this.tmpDir = tmpDir;
    }

    public void setClojureTest(Boolean clojureTest) {
        this.clojureTest = clojureTest;
    }

    public void setTimeOut(int timeOut) {
        this.timeOut = timeOut;
    }

    public void setSourceDirectory(String sourceDirectory) {
        this.srcDir = sourceDirectory;
    }
}
