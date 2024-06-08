package fr.utbm.ciad.labmanager.views.components.organizations;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import fr.utbm.ciad.labmanager.views.components.journals.JournalEditorComponentWizard;
import io.overcoded.vaadin.wizard.AbstractFormWizardStep;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;

import java.util.Arrays;
import java.util.List;

public class OrganizationEditorComponentWizard extends AbstractLabManagerWizard<ResearchOrganization> {

    public OrganizationEditorComponentWizard(VerticalLayout descriptionDetailComponents, VerticalLayout geographicalDetailComponents, VerticalLayout identificationDetailComponents,VerticalLayout superStructureDetailComponents,VerticalLayout communicationDetailComponents) {
        this(defaultWizardConfiguration(null, false),
                new ResearchOrganization(), descriptionDetailComponents, geographicalDetailComponents, identificationDetailComponents,superStructureDetailComponents,communicationDetailComponents);
    }

    public OrganizationEditorComponentWizard(VerticalLayout descriptionDetailComponents, VerticalLayout geographicalDetailComponents, VerticalLayout identificationDetailComponents,VerticalLayout superStructureDetailComponents,VerticalLayout communicationDetailComponents, VerticalLayout administrationComponents) {
        this(defaultWizardConfiguration(null, false),
                new ResearchOrganization(), descriptionDetailComponents, geographicalDetailComponents, identificationDetailComponents,superStructureDetailComponents,communicationDetailComponents, administrationComponents);
    }

    public boolean isNewEntity() {
        return true;
    }

    protected OrganizationEditorComponentWizard(WizardConfigurationProperties properties, ResearchOrganization context, VerticalLayout descriptionDetailComponents, VerticalLayout geographicalDetailComponents, VerticalLayout identificationDetailComponents,VerticalLayout superStructureDetailComponents,VerticalLayout communicationDetailComponents) {
        super(properties, context, Arrays.asList(
                new DescriptionDetailComponent(context, descriptionDetailComponents),
                new GeographicalDetailComponent(context, geographicalDetailComponents),
                new IdentificationDetailComponent(context, identificationDetailComponents),
                new SuperStructureDetailComponent(context, superStructureDetailComponents),
                new CommunicationDetailComponent(context, communicationDetailComponents)
        ));
    }

    protected OrganizationEditorComponentWizard(WizardConfigurationProperties properties, ResearchOrganization context, VerticalLayout descriptionDetailComponents, VerticalLayout geographicalDetailComponents, VerticalLayout identificationDetailComponents,VerticalLayout superStructureDetailComponents,VerticalLayout communicationDetailComponents, VerticalLayout administrationComponents) {
        super(properties, context, Arrays.asList(
                new DescriptionDetailComponent(context, descriptionDetailComponents),
                new GeographicalDetailComponent(context, geographicalDetailComponents),
                new IdentificationDetailComponent(context, identificationDetailComponents),
                new SuperStructureDetailComponent(context, superStructureDetailComponents),
                new CommunicationDetailComponent(context, communicationDetailComponents),
                new OrganizationAdministration(context, administrationComponents)
        ));
    }


    protected static class DescriptionDetailComponent extends AbstractFormWizardStep<ResearchOrganization> {

        private VerticalLayout content;

        public DescriptionDetailComponent(ResearchOrganization context, VerticalLayout content) {
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

    protected static class GeographicalDetailComponent extends AbstractFormWizardStep<ResearchOrganization> {

        private VerticalLayout content;

        public GeographicalDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.geography_informations"), 2);
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

    protected static class IdentificationDetailComponent extends AbstractFormWizardStep<ResearchOrganization> {

        private VerticalLayout content;

        public IdentificationDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.identification_informations"), 3);
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

    protected static class SuperStructureDetailComponent extends AbstractFormWizardStep<ResearchOrganization> {

        private VerticalLayout content;

        public SuperStructureDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.super_structure_informations"), 4);
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

    protected static class CommunicationDetailComponent extends AbstractFormWizardStep<ResearchOrganization> {

        private VerticalLayout content;

        public CommunicationDetailComponent(ResearchOrganization context, VerticalLayout content) {
            super(context, content.getTranslation("views.organizations.communication_informations"), 5);
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

    protected static class OrganizationAdministration extends AbstractFormWizardStep<ResearchOrganization> {

        private VerticalLayout content;
        public OrganizationAdministration(ResearchOrganization context, VerticalLayout content) {
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