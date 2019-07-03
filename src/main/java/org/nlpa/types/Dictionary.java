/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.nlpa.types;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A dictionary of Strings
 *
 * @author María Novo
 * @author José Ramón Méndez Reboredo
 */
public class Dictionary implements Iterable<String> {

    /**
     * A logger for logging purposes
     */
    private static final Logger logger = LogManager.getLogger(Dictionary.class);

    /**
     * The information storage for the dictionary. Only a Hashset of strings
     * is required
     */
    private Set<String> textHashSet;

    /**
     * A instance of the Dictionary to implement a singleton pattern
     */
    private static Dictionary dictionary = null;

    /**
     * The default constructor
     */
    private Dictionary() {
        textHashSet = new LinkedHashSet<>();
    }

    /**
     * Retrieve the System Dictionary
     *
     * @return The default dictionary for the system
     */
    public static Dictionary getDictionary() {
        if (dictionary == null) {
            dictionary = new Dictionary();
        }
        return dictionary;
    }

    /**
     * Add a string to dictionary
     *
     * @param text The new text to add to the dictionary
     */
    public void add(String text) {
        textHashSet.add(text);
    }

    /**
     * Determines if a text is included in the dictionary
     *
     * @param text the text to check
     * @return a boolean indicating whether the text is included in the
     * dictionary or not
     */
    public boolean isIncluded(String text) {
        return textHashSet.contains(text);

    }

    /**
     * Achieves an iterator to iterate through the stored text
     *
     * @return an iterator
     */
    @Override
    public Iterator<String> iterator() {
        return this.textHashSet.iterator();
    }

     /**
     * Returns the size of the dictionary
     * @return the size of the dictionary
     */
    public int size(){
        return this.textHashSet.size();
    }

    /**
     * Save data to a file
     *
     * @param filename File name where the data is saved
     */
    public void writeToDisk(String filename) {
        try (FileOutputStream outputFile = new FileOutputStream(filename);
                BufferedOutputStream buffer = new BufferedOutputStream(outputFile);
                ObjectOutputStream output = new ObjectOutputStream(buffer);) {

            output.writeObject(this.textHashSet);
            output.flush();
            output.close();
        } catch (Exception ex) {
            logger.error("[WRITE TO DISK] " + ex.getMessage());
        }
    }

    /**
     * Retrieve data from file
     *
     * @param filename File name to retrieve data
     */
    public void readFromDisk(String filename) {
        File file = new File(filename);
        try (BufferedInputStream buffer = new BufferedInputStream(new FileInputStream(file))) {
            ObjectInputStream input = new ObjectInputStream(buffer);

            this.textHashSet = (LinkedHashSet<String>) input.readObject();
        } catch (Exception ex) {
            logger.error("[READ FROM DISK] " + ex.getMessage());
        }
    }
}