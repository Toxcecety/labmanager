package fr.utbm.ciad.labmanager.views.components.conferences;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.organizations.OrganizationEditorComponentWizard;
import io.overcoded.vaadin.wizard.AbstractFormWizardStep;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;

import java.util.Arrays;
import java.util.List;


/** Wizard for adding a conference.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class ConferenceEditorComponentWizard extends AbstractLabManagerWizard<Conference> {

    /** Constructor.
     *
     * @param descriptionDetailComponents the description detail components.
     * @param rankingDetailComponents the ranking detail components.
     * @param publisherDetailComponents the publisher detail components.
     */
    public ConferenceEditorComponentWizard(VerticalLayout descriptionDetailComponents, VerticalLayout rankingDetailComponents, VerticalLayout publisherDetailComponents) {
        this(defaultWizardConfiguration(null, false),
                new Conference(), descriptionDetailComponents, rankingDetailComponents, publisherDetailComponents);
    }

    /** Constructor.
     *
     * @param descriptionDetailComponents the description detail components.
     * @param rankingDetailComponents the ranking detail components.
     * @param publisherDetailComponents the publisher detail components.
     * @param administrationComponents the administration detail components.
     */
    public ConferenceEditorComponentWizard(VerticalLayout descriptionDetailComponents, VerticalLayout rankingDetailComponents, VerticalLayout publisherDetailComponents, VerticalLayout administrationComponents) {
        this(defaultWizardConfiguration(null, false),
                new Conference(), descriptionDetailComponents, rankingDetailComponents, publisherDetailComponents, administrationComponents);
    }

    public boolean isNewEntity() {
        return true;
    }

    /** Constructor.
     *
     * @param properties the properties of the wizard. This is used for configuring the wizard.
     * @param context the editing context for the conference.
     * @param descriptionDetailComponents the description detail components.
     * @param rankingDetailComponents the ranking detail components
     * @param publisherDetailComponents the publisher detail components
     */
    protected ConferenceEditorComponentWizard(WizardConfigurationProperties properties, Conference context, VerticalLayout descriptionDetailComponents, VerticalLayout rankingDetailComponents, VerticalLayout publisherDetailComponents) {
        super(properties, context, Arrays.asList(
                    new DescriptionDetailComponent(context, descriptionDetailComponents),
                    new RankingDetailComponent(context, rankingDetailComponents),
                    new PublisherDetailComponent(context, publisherDetailComponents)
            ));
        }

    /** Constructor.
     *
     * @param properties the properties of the wizard. This is used for configuring the wizard.
     * @param context the editing context for the conference.
     * @param descriptionDetailComponents the description detail components.
     * @param rankingDetailComponents the ranking detail components
     * @param publisherDetailComponents the publisher detail components
     * @param administrationComponents the administration detail components
     */
    protected ConferenceEditorComponentWizard(WizardConfigurationProperties properties, Conference context, VerticalLayout descriptionDetailComponents, VerticalLayout rankingDetailComponents, VerticalLayout publisherDetailComponents, VerticalLayout administrationComponents) {
        super(properties, context, Arrays.asList(
                new DescriptionDetailComponent(context, descriptionDetailComponents),
                new RankingDetailComponent(context, rankingDetailComponents),
                new PublisherDetailComponent(context, publisherDetailComponents),
                new ConferenceAdministration(context, administrationComponents)
            ));
    }


    /** Wizard step to input description details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.1
     */
    protected static class DescriptionDetailComponent extends AbstractFormWizardStep<Conference> {

        private VerticalLayout content;

        public DescriptionDetailComponent(Conference context, VerticalLayout content) {
            super(context, content.getTranslation("views.journals.description_informations"), 1);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            List<Component> components = content.getChildren().toList();
            TextField acronym = (TextField) components.get(0).getChildren().toList().get(0);
            TextField name = (TextField) components.get(0).getChildren().toList().get(1);

            if(acronym.isEmpty() || name.isEmpty()){
                return false;
            }

            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            List<Component> components = content.getChildren().toList();
            TextField acronym = (TextField) components.get(0).getChildren().toList().get(0);
            TextField name = (TextField) components.get(0).getChildren().toList().get(1);

            acronym.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });
            name.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });

            form.add(content);
        }

    }

    /** Wizard step to input ranking details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.1
     */
    protected static class RankingDetailComponent extends AbstractFormWizardStep<Conference> {

        private VerticalLayout content;

        public RankingDetailComponent(Conference context, VerticalLayout content) {
            super(context, content.getTranslation("views.conferences.ranking_informations"), 2);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            form.add(content);
        }

    }

    /** Wizard step to input publisher details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.1
     */
    protected static class PublisherDetailComponent extends AbstractFormWizardStep<Conference> {

        private VerticalLayout content;

        public PublisherDetailComponent(Conference context, VerticalLayout content) {
            super(context, content.getTranslation("views.conferences.publisher_informations"), 3);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            form.add(content);
        }

    }

    /** Wizard step to input administration details.
     *
     * @author $Author: sgalland$
     * @author $Author: erenon$
     * @version $Name$ $Revision$ $Date$
     * @mavengroupid $GroupId$
     * @mavenartifactid $ArtifactId$
     * @since 4.1
     */
    protected static class ConferenceAdministration extends AbstractFormWizardStep<Conference> {

        private VerticalLayout content;
        public ConferenceAdministration(Conference context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.administration_details"), 6);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {

            return true;
        }

        @Override
        protected boolean commitAfterContextUpdated() {
            return true;
        }

        @Override
        protected void createForm(FormLayout form) {
            form.add(content);
        }


    }

}
