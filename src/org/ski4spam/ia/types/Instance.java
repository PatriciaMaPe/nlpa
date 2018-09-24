/* Copyright (C) 2002 Univ. of Massachusetts Amherst, Computer Science Dept.
   This file is part of "MALLET" (MAchine Learning for LanguagE Toolkit).
   http://www.cs.umass.edu/~mccallum/mallet
   This software is provided under the terms of the Common Public License,
   version 1.0, as published by http://www.opensource.org.  For further
   information, see the file `LICENSE' included with this distribution. */
package org.ski4spam.ia.types;

import org.ski4spam.pipe.Pipe;

import java.io.File;
import java.io.Serializable;
import java.util.Hashtable;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
//import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.text.StringEscapeUtils;


/**
 * A machine learning "example" to be used in training, testing or
 * performance of various machine learning algorithms.
 *
 * <p>An instance contains four generic fields of predefined name:
 * "data", "target", "name", and "source".   "Data" holds the data represented
 * `by the instance, "target" is often a label associated with the instance,
 * "name" is a short identifying name for the instance (such as a filename),
 * and "source" is human-readable sourceinformation, (such as the original text).
 *
 * <p> Each field has no predefined type, and may change type as the instance
 * is processed. For example, the data field may start off being a string that
 * represents a file name and then be processed by a {@link org.ski4spam.pipe.Pipe} into a CharSequence
 * representing the contents of the file, and eventually to a feature vector
 * holding indices into an {@link org.ski4spam.ia.types.Alphabet} holding words found in the file.
 * It is up to each pipe which fields in the Instance it modifies; the most common
 * case is that the pipe modifies the data field.
 *
 * <p>Generally speaking, there are two modes of operation for
 * Instances.  (1) An instance gets created and passed through a
 * Pipe, and the resulting data/target/name/source fields are used.
 * This is generally done for training instances.  (2) An instance
 * gets created with raw values in its slots, then different users
 * of the instance call newPipedCopy() with their respective
 * different pipes.  This might be done for test instances at
 * "performance" time.
 *
 * <p>Instances can be made immutable if locked.
 * Although unlocked Instances are mutable, typically the only code that
 * changes the values in the four slots is inside Pipes.
 *
 * <p> Note that constructing an instance with a pipe argument means
 * "Construct the instance and then run it through the pipe".
 * {@link es.uvigo.esei.ia.types.InstanceList} uses this method
 * when adding instances through a pipeInputIterator.
 *
 * @author Andrew McCallum <a href="mailto:mccallum@cs.umass.edu">mccallum@cs.umass.edu</a>
 * @modified José Ramón Méndez Reboredo
 * @see Pipe
 * @see Alphabet
 * @see InstanceList
 */

public class Instance implements Serializable {

    /**
     * Serial version UID
     */
    private static final long serialVersionUID = -8139659995227189017L;
    //Pipe pipe = null; // The Pipe through which this instance had its fields set
    Map<String, Object> properties = new ConcurrentHashMap<>();
   //PropertyList properties = null;
    boolean locked = false;
    //   private static Logger logger = MalletLogger.getLogger(Instance.class.getName());
    private Object data; // The input data in digested form, e.g. a FeatureVector
    private Object target; // The output data in digested form, e.g. a Label
    private Object name; // A readable name of the source, e.g. for ML error analysis
    private Object source; /* The input in a reproducable form, e.g. enabling re-print of
    string w/ POS tags, usually without target information,
    e.g. an un-annotated RegionList. */

    /**
     * Represents whether the instance is valid or not
     */
    private boolean isValid = true;
	
	private static final String CSV_SEP=";";

    public Instance(Object data, Object target, Object name, Object source) {
        this.data = data;
        this.target = target;
        this.name = name;
        this.source = source;
    }


    public Object getData() {
        return data == null ? "NULL" : data;
    }

    public void setData(Object d) {
        if (!locked)
            data = d;
        else
            throw new IllegalStateException("Instance is locked.");
    }

    public Object getTarget() {
        return target == null ? "NULL" : target;
    }

    public void setTarget(Object t) {
        if (!locked)
            target = t;
        else
            throw new IllegalStateException("Instance is locked.");
    }

    public Object getName() {
        return name == null ? "NULL" : name;
    }

    public void setName(Object n) {
        if (!locked)
            name = n;
        else
            throw new IllegalStateException("Instance is locked.");
    }

    public Object getSource() {
        return source == null ? "NULL" : source;
    }

    public void setSource(Object s) {
        if (!locked)
            source = s;
        else
            throw new IllegalStateException("Instance is locked.");
    }

    public Map<String, Object> getProperties() {
        return properties;
    }

    // Setting and getting properties
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }

    public Object getProperty(String key) {
        return properties.get(key);
    }

    public boolean hasProperty(String key) {
        return (properties != null && properties.containsKey(key));
    }

    /**
     * String representation of a instance
     */
    public String toString() {
        if (name instanceof File)
            return ((File) name).getAbsolutePath();
        else if (name instanceof String)
            return (String) name;
        else return name.toString();
    }

    /**
     * Marks instance as invalid
     */
    public void invalidate() {
        this.isValid = false;
    }

    /**
     * Determine whether the instance is valid or not
     *
     * @return A boolean indicating if the instance is valid or not
     */
    public boolean isValid() {
        return isValid;
    }

}
