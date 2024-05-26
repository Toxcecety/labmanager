package fr.utbm.ciad.labmanager.utils.names;

import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;

/**
 * Abstract implementation of utilities for comparing conference names.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$s
 * @since 4.1
 */
public abstract class AbstractConferenceNameComparator implements ConferenceNameComparator {

    private double similarityLevel;

    @Override
    public double getSimilarityLevel() {
        return similarityLevel;
    }

    @Override
    public void setSimilarityLevel(double similarityLevel) {
        if (similarityLevel < 0.0) {
            this.similarityLevel = 0.0;
        } else {
            this.similarityLevel = Math.min(similarityLevel, 1.0);
        }
    }

    /**
     * Create an instance of a string similarity computer.
     * This is a factory method.
     *
     * @return the string similarity computer.
     */
    protected abstract NormalizedStringSimilarity createStringSimilarityComputer();

    @Override
    public double getSimilarity(String acronym1, String name1, String acronym2, String name2) {
        final var similarityComputer = createStringSimilarityComputer();
        final var s1 = similarityComputer.similarity(acronym1, acronym2);
        final var s2 = similarityComputer.similarity(name1, name2);
        return Math.max(s1, s2);
    }

    /**
     * Replies the similarity of the two strings.
     *
     * @param matcher the string similarity computer to be used.
     * @param str1    the first string to compare.
     * @param str2    the second string to compare.
     * @return the level of similarity. {@code 0} means that the strings are not
     * similar, and {@code 1} means that they are totally equal.
     */
    protected double getSimilarity(NormalizedStringSimilarity matcher, String str1, String str2) {
        return matcher.similarity(str1, str2);
    }
}
