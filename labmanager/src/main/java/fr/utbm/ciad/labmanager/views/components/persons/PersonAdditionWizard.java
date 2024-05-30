package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Html;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.data.member.Gender;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.phones.PhoneNumberField;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import io.overcoded.vaadin.wizard.AbstractFormWizardStep;
import io.overcoded.vaadin.wizard.WizardStep;
import io.overcoded.vaadin.wizard.config.WizardConfigurationProperties;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Arrays;
import java.util.List;

import static fr.utbm.ciad.labmanager.views.ViewConstants.GRAVATAR_ICON;

@Route(value = "test", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class PersonAdditionWizard extends AbstractLabManagerWizard<PersonAddition> {
    public PersonAdditionWizard(@Autowired PersonService personService) {
        this(personService,
                defaultWizardConfiguration(null, false),
                new PersonAddition());
    }
    public boolean isNewEntity(){
        return true;
    }

    protected PersonAdditionWizard(PersonService personService, WizardConfigurationProperties properties, PersonAddition context) {
        super(properties, context, Arrays.asList(
                new PersonAdditionPersonalInfoStep(context),
                new PersonContactInformation(context)));
    }

    private PersonAdditionWizard(WizardConfigurationProperties properties, PersonAddition context, List<WizardStep<PersonAddition>> steps) {
        super(properties, context, steps);
    }

    protected static class PersonAdditionPersonalInfoStep extends AbstractFormWizardStep<PersonAddition> {

        private ComboBox<Integer> yearPicker;

        public PersonAdditionPersonalInfoStep(PersonAddition context) {
            super(context, "test", 1);
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
            final var content = ComponentFactory.newColumnForm(2);

            TextField lastname = new TextField();
            lastname.setRequired(true);
            lastname.setClearButtonVisible(true);
            content.add(lastname);

            TextField firstname = new TextField();
            firstname.setRequired(true);
            firstname.setClearButtonVisible(true);
            content.add(firstname);

            ComboBox<Gender> gender = new ComboBox<>();
            gender.setItems(Gender.values());
//            gender.setItemLabelGenerator();
            gender.setValue(Gender.NOT_SPECIFIED);
            content.add(gender, 2);

//            ComboBox<CountryCode> nationality = ComponentFactory.newCountryComboBox();
//            nationality.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
//            content.add(nationality, 2);

            TextField gravatarId = ComponentFactory.newClickableIconTextField(Person.GRAVATAR_ROOT_URL, GRAVATAR_ICON);
            gravatarId.setPrefixComponent(VaadinIcon.CAMERA.create());
            gravatarId.setClearButtonVisible(true);
            gravatarId.addValidationStatusChangeListener(it -> {
//                updatePhoto();
            });
            content.add(gravatarId, 2);

            VerticalLayout photo = new VerticalLayout();
            photo.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10);
            photo.setAlignItems(FlexComponent.Alignment.CENTER);
            photo.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
            content.add(photo, 2);
//            updatePhoto();

//            DetailsWithErrorMark personalInformationDetails = createDetailsWithErrorMark(receiver, content, "personal", true); //$NON-NLS-1$
//
//            getEntityDataBinder().forField(this.lastname)
//                    .withConverter(new StringTrimer())
//                    .withValidator(new NotEmptyStringValidator(getTranslation("views.persons.last_name.error"))) //$NON-NLS-1$
//                    .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.lastname, this.personalInformationDetails))
//                    .bind(Person::getLastName, Person::setLastName);
//            getEntityDataBinder().forField(this.firstname)
//                    .withConverter(new StringTrimer())
//                    .withValidator(new NotEmptyStringValidator(getTranslation("views.persons.first_name.error"))) //$NON-NLS-1$
//                    .withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.firstname, this.personalInformationDetails))
//                    .bind(Person::getFirstName, Person::setFirstName);
//            getEntityDataBinder().forField(this.gender).bind(Person::getGender, Person::setGender);
//            getEntityDataBinder().forField(this.nationality).bind(Person::getNationality, Person::setNationality);
//            getEntityDataBinder().forField(this.gravatarId)
//                    .withConverter(new StringTrimer())
//                    .bind(Person::getGravatarId, Person::setGravatarId);
            form.add(content);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
        }
    }

    protected static class PersonContactInformation extends AbstractFormWizardStep<PersonAddition> {
        private ToggleButton toggleButton;

        public PersonContactInformation(PersonAddition context) {
            super(context, "Contact Information", 2);
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
            final var content = ComponentFactory.newColumnForm(1);

            TextField email = new TextField();
            email.setPrefixComponent(VaadinIcon.AT.create());
            email.setClearButtonVisible(true);
            content.add(email, 2);

            PhoneNumberField officePhone = new PhoneNumberField();
            officePhone.setClearButtonVisible(true);
            officePhone.setPrefixComponent(VaadinIcon.PHONE.create());
            content.add(officePhone, 2);

            PhoneNumberField mobilePhone = new PhoneNumberField();
            mobilePhone.setClearButtonVisible(true);
            mobilePhone.setPrefixComponent(VaadinIcon.MOBILE.create());
            content.add(mobilePhone, 2);

            TextField officeRoom = new TextField();
            officeRoom.setClearButtonVisible(true);
            officeRoom.setPrefixComponent(VaadinIcon.OFFICE.create());
            content.add(officeRoom, 2);

            //DetailsWithErrorMark contactInformationDetails = createDetailsWithErrorMark(form, content, "contact"); //$NON-NLS-1$

            entityBinder.forField(email)
                    .withConverter(new StringTrimer())
                    .withValidator(new EmailValidator("Pas valide", true)) //$NON-NLS-1$
                    //.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(email, contactInformationDetails))
                    .bind(Person::getEmail, Person::setEmail);
            entityBinder.forField(officePhone)
                    .withValidator(officePhone.getDefaultValidator())
                    //.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(officePhone, contactInformationDetails))
                    .bind(Person::getOfficePhone, Person::setOfficePhone);
            entityBinder.forField(mobilePhone)
                    .withValidator(mobilePhone.getDefaultValidator())
                    //.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(mobilePhone, contactInformationDetails))
                    .bind(Person::getMobilePhone, Person::setMobilePhone);
            entityBinder.forField(officeRoom)
                    .withConverter(new StringTrimer())
                    .bind(Person::getOfficeRoom, Person::setOfficeRoom);

            form.add(content);
        }

        @Override
        public void localeChange(LocaleChangeEvent event) {
            super.localeChange(event);
        }
    }


    private static final Binder<Person> entityBinder = new Binder<>(Person.class);
}
