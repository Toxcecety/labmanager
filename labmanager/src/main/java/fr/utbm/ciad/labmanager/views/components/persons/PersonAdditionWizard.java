package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import io.overcoded.vaadin.wizard.AbstractFormWizardStep;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.Arrays;
import java.util.List;


public class PersonAdditionWizard extends AbstractLabManagerWizard<Person> {


    public PersonAdditionWizard(@Autowired PersonService personService, VerticalLayout personalInformationComponents, VerticalLayout contactInformationComponents, VerticalLayout researcherIdsComponents, VerticalLayout biographyComponents, VerticalLayout indexesComponents, VerticalLayout socialLinksComponents) {
        this(personService,
                defaultWizardConfiguration(null, false),
                new Person(), personalInformationComponents, contactInformationComponents, researcherIdsComponents, biographyComponents, indexesComponents,socialLinksComponents);
    }

    public PersonAdditionWizard(PersonService personService, VerticalLayout personalInformationComponents, VerticalLayout contactInformationComponents, VerticalLayout researcherIdsComponents, VerticalLayout biographyComponents, VerticalLayout indexesComponents, VerticalLayout socialLinksComponents, VerticalLayout administrationComponents) {
        this(personService,
                defaultWizardConfiguration(null, false),
                new Person(), personalInformationComponents, contactInformationComponents, researcherIdsComponents, biographyComponents, indexesComponents,socialLinksComponents,administrationComponents);
    }

    public boolean isNewEntity(){
        return true;
    }

    protected PersonAdditionWizard(PersonService personService, WizardConfigurationProperties properties, Person context, VerticalLayout personalInformationComponents, VerticalLayout contactInformationComponents, VerticalLayout researcherIdsComponents, VerticalLayout biographyComponents, VerticalLayout indexesComponents, VerticalLayout socialLinksComponents) {
        super(properties, context, Arrays.asList(
                new PersonalInformationComponents(context, personalInformationComponents),
                new PersonContactInformation(context, contactInformationComponents),
                new PersonResearcherIds(context, researcherIdsComponents),
                new PersonBiography(context, biographyComponents),
                new PersonIndexes(context, indexesComponents),
                new PersonSocialLinks(context, socialLinksComponents)));
    }

    protected PersonAdditionWizard(PersonService personService, WizardConfigurationProperties properties, Person context, VerticalLayout personalInformationComponents, VerticalLayout contactInformationComponents, VerticalLayout researcherIdsComponents, VerticalLayout biographyComponents, VerticalLayout indexesComponents, VerticalLayout socialLinksComponents, VerticalLayout administrationComponents) {
        super(properties, context, Arrays.asList(
                new PersonalInformationComponents(context, personalInformationComponents),
                new PersonContactInformation(context, contactInformationComponents),
                new PersonResearcherIds(context, researcherIdsComponents),
                new PersonBiography(context, biographyComponents),
                new PersonIndexes(context, indexesComponents),
                new PersonSocialLinks(context, socialLinksComponents),
                new PersonAdministration(context, administrationComponents)));
    }


    protected static class PersonalInformationComponents extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;
        public PersonalInformationComponents(Person context, VerticalLayout content) {
            super(context, content.getTranslation("views.persons.personal_informations"), 1);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            List<Component> components = content.getChildren().toList();
            TextField lastName = (TextField) components.get(0).getChildren().toList().get(0);
            TextField firstName = (TextField) components.get(0).getChildren().toList().get(1);

            if(lastName.isEmpty() || firstName.isEmpty()){
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
            TextField lastName = (TextField) components.get(0).getChildren().toList().get(0);
            TextField firstName = (TextField) components.get(0).getChildren().toList().get(1);

            lastName.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });
            firstName.addValueChangeListener(event -> {
                isValid();
                updateButtonStateForNextStep();
            });
            form.add(content);
        }


    }

    protected static class PersonContactInformation extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;

        public PersonContactInformation(Person context, VerticalLayout content) {
            super(context, content.getTranslation("views.persons.contact_informations"), 2);
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

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
        }

    }

    protected static class PersonResearcherIds extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;
        public PersonResearcherIds(Person context, VerticalLayout content) {
            super(context, content.getTranslation("views.persons.researcher_ids"), 3);
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
    protected static class PersonBiography extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;
        public PersonBiography(Person context, VerticalLayout content) {
            super(context, content.getTranslation("views.persons.biography_details"), 4);
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

    protected static class PersonIndexes extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;
        public PersonIndexes(Person context, VerticalLayout content) {
            super(context, content.getTranslation("views.persons.indexes"), 5);
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

    protected static class PersonSocialLinks extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;
        public PersonSocialLinks(Person context, VerticalLayout content) {
            super(context, content.getTranslation("views.persons.social_links"), 6);
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

    protected static class PersonAdministration extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;
        public PersonAdministration(Person context, VerticalLayout content) {
            super(context, content.getTranslation("views.persons.administration_details"), 7);
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
