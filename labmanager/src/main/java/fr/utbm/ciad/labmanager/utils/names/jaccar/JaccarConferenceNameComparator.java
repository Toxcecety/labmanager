package fr.utbm.ciad.labmanager.utils.names.jaccar;

import fr.utbm.ciad.labmanager.utils.names.AbstractConferenceNameComparator;
import info.debatty.java.stringsimilarity.Jaccard;
import info.debatty.java.stringsimilarity.interfaces.NormalizedStringSimilarity;
import org.springframework.stereotype.Component;

@Component
public class JaccarConferenceNameComparator extends AbstractConferenceNameComparator {

    private static final double SIMILARITY_LEVEL = 0.7;

    public JaccarConferenceNameComparator() {
        setSimilarityLevel(SIMILARITY_LEVEL);
    }

    @Override
    protected NormalizedStringSimilarity createStringSimilarityComputer() {
        return new Jaccard();
    }
}
