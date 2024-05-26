package fr.utbm.ciad.labmanager.utils.names;

public interface JournalNameAndPublisherComparator {

        double getSimilarity(String name1, String publisher1, String name2, String publisher2);

        double getSimilarityLevel();

        void setSimilarityLevel(double similarityLevel);

        default boolean isSimilar(String name1, String publisher1, String name2, String publisher2) {
            return getSimilarity(name1, publisher1, name2, publisher2) >= getSimilarityLevel();
        }
}
