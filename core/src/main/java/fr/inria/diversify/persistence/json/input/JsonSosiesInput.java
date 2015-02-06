package fr.inria.diversify.persistence.json.input;

import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.persistence.Header;
import fr.inria.diversify.persistence.PersistenceException;
import fr.inria.diversify.persistence.json.output.JsonHeaderOutput;
import fr.inria.diversify.persistence.json.output.JsonSectionOutput;
import fr.inria.diversify.transformation.Transformation;
import javafx.fxml.LoadException;
import org.json.JSONException;
import org.json.JSONObject;

import javax.script.Bindings;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

/**
 * Created by marodrig on 09/01/2015.
 */
public class JsonSosiesInput {

    public static final String ERROR   = "ERROR  :";
    public static final String WARNING = "WARNING:";
    public static final String INFO    = "INFO   :";
    public static final String DEBUG   = "DEBUG  :";

    /**
     * Path of the json file
     */
    private final String jsonPath;

    /**
     * Stream reader to obtain the JSON text from
     */
    private InputStreamReader streamReader;

    /**
     * JSON Object loaded
     */
    private JSONObject jsonObject;

    /**
     * Input program to obtain the code fragments for the transformations
     */
    private InputProgram inputProgram;

    /**
     * Errors from the reader
     */
    private Collection<String> errors;

    /**
     * Header from the reader
     */
    private Header header;

    /**
     * Visibles section for reading customization
     */
    private HashMap<String, JsonSectionInput> visibleSections;

    public JsonSosiesInput(InputStreamReader r, InputProgram inputProgram) {
        this("", inputProgram);
        this.streamReader = r;
    }

    public JsonSosiesInput(String jsonPath, InputProgram inputProgram) {
        this.jsonPath = jsonPath;
        this.inputProgram = inputProgram;
        initSections();
    }

    /**
     * Sets a section in the list of sections.
     * There is only one section per class in the output object
     * @param section Output Section to be set
     */
    public void setSection(Class<? extends JsonSectionInput> aClass, JsonSectionInput section) {
        visibleSections.put(aClass.getName(), section);
    }

    /**
     * Get the section from the list
     * @param aClass
     * @return
     */
    public JsonSectionInput getSection(Class<? extends JsonSectionInput> aClass){
        return visibleSections.get(aClass.getName());
    }


    protected void open() {
        BufferedReader br = null;
        try {
            if ( streamReader == null ) streamReader = new FileReader(jsonPath);
            br = new BufferedReader(streamReader);
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();
            while (line != null) {
                sb.append(line);
                line = br.readLine();
            }
            if (sb.length() == 0) throw new PersistenceException("Empty JSON. No lines to read");

            jsonObject = null;
            try {
                jsonObject = new JSONObject(sb.toString());
            } catch (JSONException e) {
                throw new PersistenceException("Unable to parse text into JSON file", e);
            }
        } catch (IOException e) {
            throw new PersistenceException("Unable to parse text file into JSON file", e);
        } finally {
            if (br != null) try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Init all sections in the input
     */
    private void initSections() {
        visibleSections = new HashMap<>();
        setSection(JsonFailuresInput.class, new JsonFailuresInput(inputProgram, jsonObject));
        setSection(JsonAstTransformationCollectionInput.class,
                new JsonAstTransformationCollectionInput(inputProgram, jsonObject));
        setSection(JsonHeaderInput.class, new JsonHeaderInput(inputProgram, jsonObject));
    }

    /**
     * Read the transformations from the JSON file
     * @return A collection the transformations
     */
    public Collection<Transformation> read() {
        open(); //Open the json file

        HashMap<Integer, Transformation> result = new HashMap<>();
        JsonFailuresInput failures = (JsonFailuresInput) getSection(JsonFailuresInput.class);
        failures.setJsonObject(jsonObject);
        failures.setInputProgram(inputProgram);
        failures.setErrors(getErrors());
        failures.read(result);

        JsonAstTransformationCollectionInput asts = (JsonAstTransformationCollectionInput)
                getSection(JsonAstTransformationCollectionInput.class);
        asts.setJsonObject(jsonObject);
        asts.setInputProgram(inputProgram);
        asts.setErrors(getErrors());
        asts.setFailures(failures.getFailures());
        asts.read(result);

        JsonHeaderInput headerInput = (JsonHeaderInput)getSection(JsonHeaderInput.class);
        headerInput.setJsonObject(jsonObject);
        headerInput.setInputProgram(inputProgram);
        headerInput.read(result);

        header = headerInput.getHeader();

        return result.values();
    }

    public void setInputProgram(InputProgram inputProgram) {
        this.inputProgram = inputProgram;
    }

    /**
     * Input program to obtain the code fragments for the transformations
     */
    public InputProgram getInputProgram() {
        return inputProgram;
    }

    /**
     * Errors during the loading process
     * @return
     */
    public Collection<String> getErrors() {
        if ( errors == null ) errors = new ArrayList<>();
        return errors;
    }

    public Header getHeader() {
        if ( header == null ) throw new PersistenceException("Handler unset. Must call read method first");
        return header;
    }


}
