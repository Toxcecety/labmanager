package fr.utbm.ciad.labmanager.utils.names.jaccar;

import fr.utbm.ciad.labmanager.utils.names.AbstractJournalNameAndPublisherComparator;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.stereotype.Component;

/** Utilities for comparing journal names and publishers using the Jaccar algorithm.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
public class JaccarJournalNameAndPublisherComparator extends AbstractJournalNameAndPublisherComparator {

    private static final double DEFAULT_SIMILARITY_LEVEL = 0.65;

    public JaccarJournalNameAndPublisherComparator() {
        setSimilarityLevel(DEFAULT_SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new Jaccard();
    }
}
