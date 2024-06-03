package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import io.overcoded.vaadin.wizard.AbstractFormWizardStep;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;


public class PersonAdditionWizard extends AbstractLabManagerWizard<Person> {


    public PersonAdditionWizard(@Autowired PersonService personService, VerticalLayout personalInformationComponents, VerticalLayout contactInformationComponents, VerticalLayout researcherIdsComponents, VerticalLayout biographyComponents, VerticalLayout socialLinksComponents) {
        this(personService,
                defaultWizardConfiguration(null, false),
                new Person(), personalInformationComponents, contactInformationComponents, researcherIdsComponents, biographyComponents, socialLinksComponents);
    }

    public boolean isNewEntity(){
        return true;
    }

    protected PersonAdditionWizard(PersonService personService, WizardConfigurationProperties properties, Person context, VerticalLayout personalInformationComponents, VerticalLayout contactInformationComponents, VerticalLayout researcherIdsComponents, VerticalLayout biographyComponents, VerticalLayout socialLinksComponents) {
        super(properties, context, Arrays.asList(
                new PersonalInformationComponents(context, personalInformationComponents),
                new PersonContactInformation(context, contactInformationComponents),
                new PersonResearcherIds(context, researcherIdsComponents),
                new PersonBiography(context, biographyComponents),
                new PersonSocialLinks(context, socialLinksComponents)));
    }

    private PersonAdditionWizard(WizardConfigurationProperties properties, Person context, List<WizardStep<Person>> steps) {
        super(properties, context, steps);
    }


    protected static class PersonalInformationComponents extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;
        public PersonalInformationComponents(Person context, VerticalLayout content) {
            super(context, "test", 1);
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

    protected static class PersonContactInformation extends AbstractFormWizardStep<Person> {

        private VerticalLayout content;

        public PersonContactInformation(Person context, VerticalLayout content) {
            super(context, "Contact Information", 2);
            this.content = content;
        }

        @Override
        protected Html getInformationMessage() {
            return null;
        }

        @Override
        public boolean isValid() {
            return false;
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
            super(context, "test", 3);
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
            super(context, "test", 4);
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
            super(context, "test", 5);
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
