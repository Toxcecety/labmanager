package fr.utbm.ciad.labmanager.utils.names.SorensenDice;

import fr.utbm.ciad.labmanager.utils.names.AbstractJournalNameAndPublisherComparator;
import info.debatty.java.stringsimilarity.SorensenDice;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Utilities for comparing journal names and publishers using the Sorensen Dice algorithm.
 *
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
@Primary
public class SorensenDiceJournalNameAndPublisherComparator extends AbstractJournalNameAndPublisherComparator {

    private static final double DEFAULT_SIMILARITY_LEVEL = 0.7;

    /**
     * Constructor.
     */
    public SorensenDiceJournalNameAndPublisherComparator() {
        setSimilarityLevel(DEFAULT_SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new SorensenDice();
    }
}
