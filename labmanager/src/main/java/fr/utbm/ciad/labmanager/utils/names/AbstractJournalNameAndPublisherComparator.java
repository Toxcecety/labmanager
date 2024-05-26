package fr.utbm.ciad.labmanager.utils.names;

import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;

/**
 * Abstract implementation of utilities for comparing journal names and publishers.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public abstract class AbstractJournalNameAndPublisherComparator implements JournalNameAndPublisherComparator {

    private double similarityLevel;

    @Override
    public double getSimilarityLevel() {
        return this.similarityLevel;
    }

    @Override
    public void setSimilarityLevel(double similarityLevel) {
        if (similarityLevel < 0.0) {
            this.similarityLevel = 0.0;
        } else {
            this.similarityLevel = Math.min(similarityLevel, 1.0);
        }
    }

    protected abstract NormalizedStringSimilarity createStringSimilarityComputer();

    @Override
    public double getSimilarity(String name1, String publisher1, String name2, String publisher2) {
        final var similarityComputer = createStringSimilarityComputer();
        final var s1 = similarityComputer.similarity(name1, name2);
        final var s2 = similarityComputer.similarity(publisher1, publisher2);
        return Math.max(s1, s2);
    }

    protected double getSimilarity(NormalizedStringSimilarity matcher, String str1, String str2) {
        return matcher.similarity(str1, str2);
    }
}
