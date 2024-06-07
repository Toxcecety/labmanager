package fr.utbm.ciad.labmanager.utils.names.jaccar;

import fr.utbm.ciad.labmanager.utils.names.AbstractPublicationNameComparator;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.stereotype.Component;

/**
 * Utilities for comparing publication names using the Jaccar algorithm.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
public class JaccarPublicationNameComparator extends AbstractPublicationNameComparator {

    private static final double SIMILARITY_LEVEL = 0.65;

    public JaccarPublicationNameComparator() {
        setSimilarityLevel(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new Jaccard();
    }
}
