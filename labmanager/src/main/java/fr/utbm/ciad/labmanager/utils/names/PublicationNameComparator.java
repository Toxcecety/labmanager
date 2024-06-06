package fr.utbm.ciad.labmanager.utils.names;

/**
 * Utilities for comparing publication names.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public interface PublicationNameComparator {

    /**
     * Computes the similarity between two titles.
     * The implementation of this method depends on the specific string similarity algorithm used.
     *
     * @param title1 The first title.
     * @param title2 The second title.
     * @return The similarity between the two titles, as a double between 0.0 (completely dissimilar) and 1.0 (identical).
     */
    double getSimilarity(String title1, String title2);

    /**
     * Returns the current similarity level.
     * The similarity level is a threshold used to determine whether two titles are considered similar.
     *
     * @return The current similarity level, as a double between 0.0 and 1.0.
     */
    double getSimilarityLevel();

    /**
     * Sets the similarity level.
     * The similarity level is a threshold used to determine whether two titles are considered similar.
     *
     * @param similarityLevel The new similarity level, as a double between 0.0 and 1.0.
     */
    void setSimilarityLevel(double similarityLevel);

    /**
     * Checks if two titles are similar, based on the current similarity level.
     * Two titles are considered similar if their similarity (as computed by getSimilarity) is greater than or equal to the similarity level.
     *
     * @param title1 The first title.
     * @param title2 The second title.
     * @return True if the titles are similar, false otherwise.
     */
    default boolean isSimilar(String title1, String title2) {
        return getSimilarity(title1, title2) >= getSimilarityLevel();
    }
}
