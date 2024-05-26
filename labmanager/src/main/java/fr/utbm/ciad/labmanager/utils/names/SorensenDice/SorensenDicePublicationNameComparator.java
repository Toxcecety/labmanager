package fr.utbm.ciad.labmanager.utils.names.SorensenDice;

import fr.utbm.ciad.labmanager.utils.names.AbstractPublicationNameComparator;
import info.debatty.java.stringsimilarity.SorensenDice;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/**
 * Utilities for comparing publication names using the Sorensen Dice algorithm.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.2
 */
@Component
@Primary
public class SorensenDicePublicationNameComparator extends AbstractPublicationNameComparator {

    private static final double SIMILARITY_LEVEL = 0.7;

    /**
     * Constructor.
     */
    public SorensenDicePublicationNameComparator() {
        setSimilarityLevel(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new SorensenDice();
    }
}
