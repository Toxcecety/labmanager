package fr.utbm.ciad.labmanager.utils.names.SorensenDice;

import fr.utbm.ciad.labmanager.utils.names.AbstractConferenceNameComparator;
import info.debatty.java.stringsimilarity.SorensenDice;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

@Component
@Primary
public class SorensenDiceConferenceNameComparator extends AbstractConferenceNameComparator {

    private static final double SIMILARITY_LEVEL = 0.7;

    public SorensenDiceConferenceNameComparator() {
        setSimilarityLevel(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new SorensenDice();
    }
}
