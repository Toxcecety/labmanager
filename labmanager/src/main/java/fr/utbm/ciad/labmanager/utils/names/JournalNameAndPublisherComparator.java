package fr.utbm.ciad.labmanager.utils.names;

/**
 * Utilities for comparing Journal names and publishers.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public interface JournalNameAndPublisherComparator {

    /**
     * Compute and replies the similarity between the names and publishers of two journals.
     *
     * @param name1      the first name.
     * @param publisher1 the first publisher.
     * @param name2      the second name.
     * @param publisher2 the second publisher.
     * @return the level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarity(String name1, String publisher1, String name2, String publisher2);

    /**
     * Replies the similarity level to consider for assuming that two names are similar.
     *
     * @return the minimum level of similarity. {@code 0} means that the names are not
     * similar, and {@code 1} means that they are totally equal.
     */
    double getSimilarityLevel();

    /**
     * Change the similarity level to consider for assuming that two names are similar.
     *
     * @param similarityLevel the minimum level of similarity. {@code 0} means that the names are not
     *                        similar, and {@code 1} means that they are totally equal.
     */
    void setSimilarityLevel(double similarityLevel);

    /**
     * Check name similarity between the names and the acronyms of two organizations.
     *
     * @param name1      the first name.
     * @param publisher1 the first publisher.
     * @param name2      the second name.
     * @param publisher2 the second publisher.
     * @return {@code true} if the two given names are similar.
     */
    default boolean isSimilar(String name1, String publisher1, String name2, String publisher2) {
        return getSimilarity(name1, publisher1, name2, publisher2) >= getSimilarityLevel();
    }
}
