package fr.utbm.ciad.labmanager.views.components.addons;

import static fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory.getTranslation;

/**
 * Enum that represents the different types of similarity errors that can be found when importing data.
 * Each type of error has a message that will be displayed to the user.
 * The error can be a warning or an error, depending on the type of error.
 *
 * @author $Author: jferlin$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public enum SimilarityError {

    /**
     * No error found
     */
    NO_ERROR {
        @Override
        public String getSimilarityErrorMessage() {
            return "";
        }

        @Override
        public boolean isSimilarityError() {
            return false;
        }

        @Override
        public boolean isSimilarityWarning() {
            return false;
        }
    },

    /**
     * Error when the title of the entity is the same as another entity
     */
    SAME_TITLE {
        @Override
        public String getSimilarityErrorMessage() {
            return getTranslation("views.import.error.same_title");
        }

        @Override
        public boolean isSimilarityError() {
            return true;
        }

        @Override
        public boolean isSimilarityWarning() {
            return false;
        }
    },

    /**
     * Error when the title and the authors of the entity are the same as another entity
     */
    SAME_TITLE_AND_AUTHORS {
        @Override
        public String getSimilarityErrorMessage() {
            return getTranslation("views.import.error.same_title_and_authors");
        }

        @Override
        public boolean isSimilarityError() {
            return true;
        }

        @Override
        public boolean isSimilarityWarning() {
            return false;
        }
    },

    /**
     * Error when the title of the entity are the same as another entity but the authors are different
     */
    SAME_TITLE_BUT_DIFFERENT_AUTHORS {
        @Override
        public String getSimilarityErrorMessage() {
            return getTranslation("views.import.error.same_title_different_authors");
        }

        @Override
        public boolean isSimilarityError() {
            return false;
        }

        @Override
        public boolean isSimilarityWarning() {
            return true;
        }
    },

    /**
     * Error when the title and the publisher of the entity are the same as another entity
     */
    SAME_TITLE_AND_PUBLISHER {
        @Override
        public String getSimilarityErrorMessage() {
            return getTranslation("views.import.error.same_title_and_publisher");
        }

        @Override
        public boolean isSimilarityError() {
            return true;
        }

        @Override
        public boolean isSimilarityWarning() {
            return false;
        }
    },

    /**
     * Error when the title of the entity are the same as another entity but the publisher are different
     */
    SAME_NAME {
        @Override
        public String getSimilarityErrorMessage() {
            return getTranslation("views.import.error.same_name");
        }

        @Override
        public boolean isSimilarityError() {
            return true;
        }

        @Override
        public boolean isSimilarityWarning() {
            return false;
        }
    },

    /**
     * Error when the title and the acronym of the entity are the same as another entity
     */
    SAME_NAME_AND_ACRONYM {
        @Override
        public String getSimilarityErrorMessage() {
            return getTranslation("views.import.error.same_name_and_acronym");
        }

        @Override
        public boolean isSimilarityError() {
            return true;
        }

        @Override
        public boolean isSimilarityWarning() {
            return false;
        }
    };

    /**
     * Get the message of the error
     *
     * @return the message of the error
     */
    public abstract String getSimilarityErrorMessage();

    /**
     * Check if the error is an error
     *
     * @return true if the error is an error, false otherwise
     */
    public abstract boolean isSimilarityError();

    /**
     * Check if the error is a warning
     *
     * @return true if the error is a warning, false otherwise
     */
    public abstract boolean isSimilarityWarning();
}
