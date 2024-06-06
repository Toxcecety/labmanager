package fr.utbm.ciad.labmanager.views.components.journals;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationImp;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.publications.PublicationEditorComponentWizard;
import io.overcoded.vaadin.wizard.AbstractFormWizardStep;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;

import java.util.Arrays;
import java.util.List;

public class JournalEditorComponentWizard extends AbstractLabManagerWizard<Journal> {

    public JournalEditorComponentWizard(VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents) {
        this(defaultWizardConfiguration(null, false),
                new Journal(), descriptionDetailComponents, rankingInformationComponents, publisherInformationComponents);
    }

    public JournalEditorComponentWizard(VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents, VerticalLayout administrationComponents) {
        this(defaultWizardConfiguration(null, false),
                new Journal(), descriptionDetailComponents, rankingInformationComponents, publisherInformationComponents,administrationComponents);
    }

    public boolean isNewEntity(){
        return true;
    }

    protected JournalEditorComponentWizard(WizardConfigurationProperties properties, Journal context, VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents) {
        super(properties, context, Arrays.asList(
                new DescriptionDetailComponent(context,descriptionDetailComponents),
                new RankingInformationComponent(context, rankingInformationComponents),
                new PublisherInformationComponent(context, publisherInformationComponents)
                ));
    }

    protected JournalEditorComponentWizard(WizardConfigurationProperties properties, Journal context,VerticalLayout descriptionDetailComponents, VerticalLayout rankingInformationComponents, VerticalLayout publisherInformationComponents, VerticalLayout administrationComponents) {
        super(properties, context, Arrays.asList(
                new DescriptionDetailComponent(context,descriptionDetailComponents),
                new RankingInformationComponent(context, rankingInformationComponents),
                new PublisherInformationComponent(context, publisherInformationComponents),
                new JournalAdministration(context, administrationComponents)
                ));
    }

    protected static class DescriptionDetailComponent extends AbstractFormWizardStep<Journal> {

        private VerticalLayout content;
        public DescriptionDetailComponent(Journal context, VerticalLayout content) {
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
            TextField title = (TextField) components.get(0).getChildren().toList().get(0);

            if(title.isEmpty()){
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
            TextField title = (TextField) components.get(0).getChildren().toList().get(0);

            title.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });

            form.add(content);
        }

    }

    protected static class RankingInformationComponent extends AbstractFormWizardStep<Journal> {

        private VerticalLayout content;
        public RankingInformationComponent(Journal context, VerticalLayout content) {
            super(context, content.getTranslation("views.journals.ranking_informations"), 2);
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

    protected static class PublisherInformationComponent extends AbstractFormWizardStep<Journal> {

        private VerticalLayout content;
        public PublisherInformationComponent(Journal context, VerticalLayout content) {
            super(context, content.getTranslation("views.journals.publisher_informations"), 3);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            List<Component> components = content.getChildren().toList();
            TextField publisherName = (TextField) components.get(0).getChildren().toList().get(0);

            if(publisherName.isEmpty()){
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
            TextField publisherName = (TextField) components.get(0).getChildren().toList().get(0);

            publisherName.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });
            form.add(content);
        }

    }

    protected static class JournalAdministration extends AbstractFormWizardStep<Journal> {

        private VerticalLayout content;
        public JournalAdministration(Journal context, VerticalLayout content) {
            super(context, content.getTranslation("views.publication.administration_details"), 4);
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
