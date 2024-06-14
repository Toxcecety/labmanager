package fr.utbm.ciad.labmanager.utils.names;

import com.google.common.base.Strings;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;

/**
 * Abstract implementation of utilities for comparing publication names.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public abstract class AbstractPublicationNameComparator implements PublicationNameComparator {

    private double similarityLevel;

    /**
     * Returns the current similarity level.
     *
     * @return The current similarity level.
     */
    @Override
    public double getSimilarityLevel() {
        return this.similarityLevel;
    }

    /**
     * Sets the similarity level. If the provided value is less than 0.0, the similarity level is set to 0.0.
     * If the provided value is greater than 1.0, the similarity level is set to 1.0.
     * Otherwise, the similarity level is set to the provided value.
     *
     * @param similarityLevel The similarity level to set.
     */
    @Override
    public void setSimilarityLevel(double similarityLevel) {
        if (similarityLevel < 0.0) {
            this.similarityLevel = 0.0;
        } else {
            this.similarityLevel = Math.min(similarityLevel, 1.0);
        }
    }

    /**
     * Abstract method to create a string similarity computer.
     * Subclasses must implement this method to provide a specific string similarity computer.
     *
     * @return A string similarity computer.
     */
    protected abstract NormalizedStringSimilarity createStringSimilarityComputer();

    /**
     * Computes the similarity between two titles using the string similarity computer created by createStringSimilarityComputer.
     *
     * @param title1 The first title.
     * @param title2 The second title.
     * @return The similarity between the two titles.
     */
    @Override
    public double getSimilarity(String title1, String title2) {
        final var similarityComputer = createStringSimilarityComputer();
        return similarityComputer.similarity(title1, title2);
    }

    /**
     * Computes the similarity between two strings using a provided string similarity computer.
     * If either string is null or empty, the method returns 1.0.
     *
     * @param matcher The string similarity computer to use.
     * @param str1    The first string.
     * @param str2    The second string.
     * @return The similarity between the two strings.
     */
    protected double getSimilarity(NormalizedStringSimilarity matcher, String str1, String str2) {
        if (Strings.isNullOrEmpty(str1) || Strings.isNullOrEmpty(str2)) {
            return 1.0;
        }
        return matcher.similarity(str1, str2);
    }
}
