package fr.inria.diversify.transformation.query;

import fr.inria.diversify.diversification.InputProgram;
import fr.inria.diversify.transformation.Transformation;
import fr.inria.diversify.transformation.TransformationJsonParser;
import fr.inria.diversify.transformation.TransformationParserException;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * Created by Simon on 27/11/14.
 */
public class FromListQuery extends TransformationQuery {

    private final List<Transformation> transformations;
    boolean removeAfterQuery = true;
    boolean shuffle = false;

    public FromListQuery(InputProgram inputProgram) throws TransformationParserException {
        super(inputProgram);
        TransformationJsonParser parser = new TransformationJsonParser(false, getInputProgram());
        File f = new File(getInputProgram().getPreviousTransformationsPath());
        Collection<Transformation> ts;
        if (f.isDirectory()) {
            ts = parser.parseDir(f.getAbsolutePath());
        } else {
            ts = parser.parseFile(f);
        }
        transformations = new ArrayList(ts);
    }

    public FromListQuery(InputProgram inputProgram, int rangeMin, int rangeMax) throws TransformationParserException {
        super(inputProgram);
        TransformationJsonParser parser = new TransformationJsonParser(false, getInputProgram());
        File f = new File(getInputProgram().getPreviousTransformationsPath());
        Collection<Transformation> ts;
        if (f.isDirectory()) {
            ts = parser.parseDir(f.getAbsolutePath());
        } else {
            ts = parser.parseFile(f);
        }
        ArrayList<Transformation> tmp = new ArrayList(ts);
        transformations = new ArrayList<>();
        for(int i = rangeMin; i < Math.min(rangeMax, tmp.size())  ; i++) {
             transformations.add(tmp.get(i));
        }
    }

    @Override
    public Transformation query() throws QueryException {
        int index = 0;
        if(shuffle) {
            Random r = new Random();
            index = r.nextInt(transformations.size());
        }
        if(removeAfterQuery) {
            return transformations.remove(index);
        } else {
            return transformations.get(index);
        }
    }

    public boolean hasNextTransformation() {
        return !transformations.isEmpty();
    }

    public void setShuffle(boolean shuffle) {
        this.shuffle = shuffle;
    }

    public void setRemoveAfterQuery(boolean removeAfterQuery) {
        this.removeAfterQuery = removeAfterQuery;
    }
}
