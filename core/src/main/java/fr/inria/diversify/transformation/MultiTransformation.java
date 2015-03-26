package fr.inria.diversify.transformation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.*;

/**
 * Created by Simon on 28/01/15.
 */
public class MultiTransformation extends Transformation {
    Collection<Transformation> transformations;


    public  MultiTransformation(boolean set) {
        if(set) {
            transformations = new HashSet<>();
        } else {
            transformations = new LinkedList<>();
        }
        type = "multi";
        name= "multi";
        failures = new ArrayList<>();
    }

    public MultiTransformation() {
        type = "multi";
        name= "multi";
        transformations = new HashSet<>();
    }

    @Override
    public JSONObject toJSONObject() throws JSONException {
        JSONObject object = super.toJSONObject();
        final JSONArray array = new JSONArray();
        object.put("transformations",array);
       for(Transformation trans : transformations) {
           array.put(trans.toJSONObject());
       }

        return object;
    }

    @Override
    public String getTransformationString() throws Exception {
        return null;
    }

    @Override
    public void apply(String srcDir) throws Exception {
        for(Transformation transformation: transformations) {
            transformation.apply(srcDir);
        }
    }

    @Override
    public void applyWithParent(String srcDir) throws Exception {
        for(Transformation transformation: transformations) {
            transformation.applyWithParent(srcDir);
        }

    }

    @Override
    public void restore(String srcDir) throws Exception {
        for(Transformation transformation: transformations) {
            transformation.restore(srcDir);
        }
    }

    public void add(Transformation transformation) {
        transformations.add(transformation);
    }

    public void addAll(Collection<Transformation> transformations) {
        this.transformations.addAll(transformations);
    }

    public boolean remove(Transformation transformation) {
        return transformations.remove(transformation);
    }

    public int size() {
        return transformations.size();
    }

    public  MultiTransformation clone() {
        MultiTransformation transformation = new MultiTransformation(transformations instanceof Set);
        transformation.transformations = new ArrayList<>(transformations);
        transformation.status = status;

        return transformation;
    }

    public  int hashCode() {
        return super.hashCode() * transformations.hashCode();
    }

    public boolean equals(Object other) {
        if(other == null)
            return false;
        if(!this.getClass().isAssignableFrom(other.getClass()))
            return  false;
        MultiTransformation otherMulti = (MultiTransformation)other;

        if(!equalParent(otherMulti.parent))
            return false;

        return status == otherMulti.status &&
                name.equals(otherMulti.name) &&
                failures.equals(otherMulti.failures) &&
                transformations.equals(otherMulti.transformations);
    }

    public String toString() {
        String ret = "";

        for(Transformation transformation : transformations) {
            ret += transformations.toString() +"\n";
        }
        return ret;
    }
}