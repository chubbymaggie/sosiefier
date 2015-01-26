package fr.inria.diversify.sosie;

import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.TransformationJsonParser;
import fr.inria.diversify.transformation.TransformationParserException;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Collection;
import java.util.Properties;

/**
 * A class to create a pool of sosies from a transformation directory
 * <p/>
 * Created by marodrig on 19/06/2014.
 */
public class SosiePoolCreator {

    InputProgram inputProgram;

    private Properties properties;

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public SosiePoolCreator(InputProgram inputProgram) {

        this.inputProgram = inputProgram;
        setProperties(new Properties());
        //Collecto only sosies, this is for the parser and should not be
        getProperties().setProperty("status", "0");
        //Types fo transformations that we want by default
        getProperties().setProperty("type", "adrStmt");
        //Names of the transformations that we want by default
        getProperties().setProperty("name", "replace replaceRandom replaceReaction replaceWittgenstein replaceSteroid delete");
    }

    /**
     * Test if two transformations defined by their JSONObjects are equals
     *
     * @param a A transformation defined by a JSONObject
     * @param b Another transformation defined by a JSONObject
     * @return True if they are equals
     */
    private boolean different(JSONObject a, JSONObject b) throws JSONException {
        boolean result = a.get("position").equals(b.get("position")) &&
                a.get("sourceCode").equals(b.get("sourceCode")) &&
                a.get("type").equals(b.get("type"));

        return !result;
    }

    /**
     * Clean the sosies to avoid repreated sosies
     *
     * @param sosies
     */
    private JSONArray cleanRepeated(JSONArray sosies) throws JSONException {
        JSONArray nonRepeated = new JSONArray();
        int index = 0;
        for (int i = 0; i < sosies.length(); i++) {
            boolean insert = true;

            JSONObject a = sosies.getJSONObject(i);
            JSONObject pa = a.getJSONObject("transplantationPoint");
            JSONObject ta = a.has("transplant") ? a.getJSONObject("transplant") : null;

            //Test that the Transplantation Point and the Transplantation are different
            if (ta == null || different(pa, ta)) {
                for (int j = 0; j < nonRepeated.length() && insert; j++) {
                    JSONObject b = nonRepeated.getJSONObject(j);
                    insert = different(
                            a.getJSONObject("transplantationPoint"),
                            b.getJSONObject("transplantationPoint"));

                    insert = insert || (a.has("transplant") != b.has("transplant"));

                    if (!insert && a.has("transplant")) {
                        insert = different(
                                a.getJSONObject("transplant"),
                                b.getJSONObject("transplant"));
                    }
                }


            }

            if (insert) {
                JSONObject obj = sosies.getJSONObject(i);
                index++;
                obj.put("tindex", index);
                nonRepeated.put(sosies.get(i));
            }
        }
        return nonRepeated;
    }

    /**
     * Creates the pool of transformations
     */
    public void create(String outputFile) throws TransformationParserException {
        try {
            //Check that all what we need is OK to fetch the transformations
            if (inputProgram.getPreviousTransformationsPath() == null) {
                throw new RuntimeException("Input program has no previous transformation information");
            }

            TransformationJsonParser parser = new TransformationJsonParser(false, inputProgram);
            //Collecto only sosies, this is for the parser and should not be changed.
            // Overwrite in case the user has changed this.
            getProperties().setProperty("status", "0");
            parser.setFilterProperties(properties);
            Collection<Transformation> ts;
            File f = new File(inputProgram.getPreviousTransformationsPath());
            if (f.isDirectory()) {
                ts = parser.parseDir(inputProgram.getPreviousTransformationsPath());
            } else {
                ts = parser.parseFile(f);
            }

            int index = 0;
            JSONArray array = new JSONArray();
            for (Transformation t : ts) {
                //Allow only sosies
                if (t.isSosie()) {
                    //Avoid repeated transformations
                    boolean unique = true;
                    JSONObject transJson = t.toJSONObject();
                    while (unique && array.length() > index) {
                        unique = !transJson.toString().equals(array.getJSONObject(index).toString());
                        index++;
                    }
                    String type = getProperties().getProperty("type", "");
                    String names = getProperties().getProperty("name", "");
                    if (unique &&
                            (type.equals("") || transJson.get("type").equals(type)) &&
                            (names.equals("") || names.contains((String) transJson.get("name")))) {
                        array.put(transJson);
                        transJson.put("tindex", index);
                        index++;
                    }
                }
            }
            FileWriter fw = new FileWriter(outputFile);
            JSONArray noRepeated = cleanRepeated(array);
            noRepeated.write(fw);
            fw.close();
        } catch (JSONException | IOException e) {
            throw new TransformationParserException(e);
        }

    }


}