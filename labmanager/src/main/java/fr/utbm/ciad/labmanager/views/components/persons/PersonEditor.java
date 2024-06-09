package fr.utbm.ciad.labmanager.views.components.persons;

import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.IntegerField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.validator.EmailValidator;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.theme.lumo.LumoIcon;
import com.vaadin.flow.theme.lumo.LumoUtility;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.member.Gender;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.member.WebPageNaming;
import fr.utbm.ciad.labmanager.data.user.User;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.phones.PhoneNumberField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.OrcidValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.*;

/** Implementation for the editor of the information related to a person. It is directly linked for
 * using it with a wizard.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class PersonEditor extends AbstractEntityEditor<Person> {

    private TextField lastname;

    private TextField firstname;

    private ComboBox<Gender> gender;

    private ComboBox<CountryCode> nationality;

    private TextField gravatarId;

    private VerticalLayout photo;

    private TextField email;

    private PhoneNumberField officePhone;

    private PhoneNumberField mobilePhone;

    private TextField officeRoom;

    private ToggleButton privateBiography;

    private MarkdownField biography;

    private TextField orcid;

    private TextField scopus;

    private TextField wos;

    private TextField gscholar;

    private IntegerField wosHindex;

    private IntegerField scopusHindex;

    private IntegerField gscholarHindex;

    private IntegerField wosCitations;

    private IntegerField scopusCitations;

    private IntegerField gscholarCitations;

    private TextField researchGate;

    private TextField adScientificIndex;

    private TextField dblp;

    private TextField academiaEdu;

    private TextField euCordis;

    private TextField linkedin;

    private TextField github;

    private TextField facebook;

    private ComboBox<WebPageNaming> webpageConvention;

    private TextField userLogin;

    private ComboBox<UserRole> userRole;

    private final Binder<User> userBinder = new Binder<>(User.class);

    private final UserService.UserEditingContext userContext;

    public PersonEditorComponentWizard personAdditionWizard;

    /**
     * Constructor.
     *
     * @param userContext            the editing context for the user.
     * @param relinkEntityWhenSaving indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                               be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param authenticatedUser      the connected user.
     * @param messages               the accessor to the localized messages (Spring layer).
     * @param logger                 the logger to be used by this view.
     */
    public PersonEditor(UserService.UserEditingContext userContext, boolean relinkEntityWhenSaving, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, Logger logger, @Autowired PersonService personService) {
        super(Person.class, authenticatedUser, messages, logger,
                "views.persons.administration_details", //$NON-NLS-1$
                "views.persons.administration.validated_person", //$NON-NLS-1$
                userContext.getPersonContext(), relinkEntityWhenSaving);
        this.userContext = userContext;

    }

    /** Create the content of the editor.
     * This function should invoke {@link #createAdministrationComponents(VerticalLayout, boolean, Consumer)}.
     *
     * @param rootContainer the container.
     * @see #createAdministrationComponents(VerticalLayout, boolean, Consumer)
     */
    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        if (isBaseAdmin()) {
            Consumer<FormLayout> builderCallback = null;
            if (isAdvancedAdmin()) {
                builderCallback = content -> {
                    this.webpageConvention = new ComboBox<>();
                    this.webpageConvention.setItems(WebPageNaming.values());
                    this.webpageConvention.setItemLabelGenerator(this::getWebPageNamingLabel);
                    this.webpageConvention.setValue(WebPageNaming.UNSPECIFIED);
                    content.add(this.webpageConvention, 2);

                    this.userLogin = new TextField();
                    this.userLogin.setClearButtonVisible(true);
                    this.userLogin.setPrefixComponent(VaadinIcon.HASH.create());
                    content.add(this.userLogin);

                    this.userRole = new ComboBox<>();
                    this.userRole.setItems(UserRole.values());
                    this.userRole.setItemLabelGenerator(this::getUserRoleLabel);
                    this.userRole.setPrefixComponent(VaadinIcon.MEDAL.create());
                    this.userRole.setValue(UserRole.USER);
                    content.add(this.userRole);

                    getEntityDataBinder().forField(this.webpageConvention).bind(Person::getWebPageNaming, Person::setWebPageNaming);
                    getUserDataBinder().forField(this.userLogin).bind(User::getLogin, User::setLogin);
                    getUserDataBinder().forField(this.userRole).bind(User::getRole, User::setRole);
                };
            }
            personAdditionWizard = new PersonEditorComponentWizard(createPersonalInformationComponents(),createContactInformationComponents(),createResearcherIdsComponents(),createBiographyComponents(),createIndexesComponents(),createSocialLinksComponents(),createAdministrationComponents( builderCallback, it -> it.bind(Person::isValidated, Person::setValidated)));

        }else{
            personAdditionWizard = new PersonEditorComponentWizard(createPersonalInformationComponents(),createContactInformationComponents(),createResearcherIdsComponents(),createBiographyComponents(),createIndexesComponents(),createSocialLinksComponents());
        }
        rootContainer.add(personAdditionWizard);
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.persons.validation_success", //$NON-NLS-1$
                getEditedEntity().getFullName());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.persons.delete_success2", //$NON-NLS-1$
                getEditedEntity().getFullName());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.persons.save_error", //$NON-NLS-1$
                getEditedEntity().getFullName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.persons.validation_error", //$NON-NLS-1$
                getEditedEntity().getFullName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.persons.save_success", //$NON-NLS-1$
                getEditedEntity().getFullName());
    }
    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.persons.delete_error2", //$NON-NLS-1$
                getEditedEntity().getFullName(), error.getLocalizedMessage());
    }

    /** Create the section for editing the description of the person.
     *
     * @return The content.
     */
    protected VerticalLayout createPersonalInformationComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        lastname = new TextField();
        lastname.setRequired(true);
        lastname.setClearButtonVisible(true);
        content.add(lastname);

        firstname = new TextField();
        firstname.setRequired(true);
        firstname.setClearButtonVisible(true);
        content.add(firstname);

        gender = new ComboBox<>();
        gender.setItems(Gender.values());
        gender.setItemLabelGenerator(this::getGenderLabel);
        gender.setValue(Gender.NOT_SPECIFIED);
        content.add(gender, 2);

        nationality = ComponentFactory.newCountryComboBox(getLocale());
        nationality.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        content.add(nationality, 2);

        gravatarId = ComponentFactory.newClickableIconTextField(Person.GRAVATAR_ROOT_URL, GRAVATAR_ICON);
        gravatarId.setPrefixComponent(VaadinIcon.CAMERA.create());
        gravatarId.setClearButtonVisible(true);
        gravatarId.addValidationStatusChangeListener(it -> {
            updatePhoto();
        });
        content.add(gravatarId, 2);

        photo = new VerticalLayout();
        photo.addClassNames(LumoUtility.Border.ALL, LumoUtility.BorderColor.CONTRAST_10);
        photo.setAlignItems(FlexComponent.Alignment.CENTER);
        photo.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        content.add(photo, 2);
        updatePhoto();


        getEntityDataBinder().forField(lastname)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.persons.last_name.error"))) //$NON-NLS-1$
                .bind(Person::getLastName, Person::setLastName);
        getEntityDataBinder().forField(firstname)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.persons.first_name.error"))) //$NON-NLS-1$
                .bind(Person::getFirstName, Person::setFirstName);
        getEntityDataBinder().forField(gender).bind(Person::getGender, Person::setGender);
        getEntityDataBinder().forField(nationality).bind(Person::getNationality, Person::setNationality);
        getEntityDataBinder().forField(gravatarId)
                .withConverter(new StringTrimer())
                .bind(Person::getGravatarId, Person::setGravatarId);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Create the components for entering the contact informations.
     *
     * @return The content.
     */
    protected VerticalLayout createContactInformationComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(1);

        this.email = new TextField();
        this.email.setPrefixComponent(VaadinIcon.AT.create());
        this.email.setClearButtonVisible(true);
        content.add(this.email, 2);

        this.officePhone = new PhoneNumberField();
        this.officePhone.setClearButtonVisible(true);
        this.officePhone.setPrefixComponent(VaadinIcon.PHONE.create());
        content.add(this.officePhone, 2);

        this.mobilePhone = new PhoneNumberField();
        this.mobilePhone.setClearButtonVisible(true);
        this.mobilePhone.setPrefixComponent(VaadinIcon.MOBILE.create());
        content.add(this.mobilePhone, 2);

        this.officeRoom = new TextField();
        this.officeRoom.setClearButtonVisible(true);
        this.officeRoom.setPrefixComponent(VaadinIcon.OFFICE.create());
        content.add(this.officeRoom, 2);


        getEntityDataBinder().forField(this.email)
                .withConverter(new StringTrimer())
                .withValidator(new EmailValidator(getTranslation("views.forms.email.invalid_format"), true)) //$NON-NLS-1$
                .bind(Person::getEmail, Person::setEmail);
        getEntityDataBinder().forField(this.officePhone)
                .withValidator(this.officePhone.getDefaultValidator())
                .bind(Person::getOfficePhone, Person::setOfficePhone);
        getEntityDataBinder().forField(this.mobilePhone)
                .withValidator(this.mobilePhone.getDefaultValidator())
                .bind(Person::getMobilePhone, Person::setMobilePhone);
        getEntityDataBinder().forField(this.officeRoom)
                .withConverter(new StringTrimer())
                .bind(Person::getOfficeRoom, Person::setOfficeRoom);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Create the components for entering the researcher identifiers.
     * The information includes:<ul>
     * <li>ORCID</li>
     * <li>Identifier on Scopus (Elsevier)</li>
     * <li>Identifier on ResearcherID, WoS or Publon</li>
     * <li>Identifier for Google Scholar</li>
     * </ul>
     *
     * @return The content.
     */
    protected VerticalLayout createResearcherIdsComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(2);

        this.orcid = ComponentFactory.newClickableIconTextField(Person.ORCID_BASE_URL, ORCID_ICON);
        this.orcid.setClearButtonVisible(true);
        this.orcid.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.orcid, 2);

        this.wos = ComponentFactory.newClickableIconTextField(Person.WOS_BASE_URL, WOS_ICON);
        this.wos.setClearButtonVisible(true);
        this.wos.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.wos, 2);

        this.scopus = ComponentFactory.newClickableIconTextField(Person.SCOPUS_BASE_URL, SCOPUS_ICON);
        this.scopus.setClearButtonVisible(true);
        this.scopus.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.scopus, 2);

        this.gscholar = ComponentFactory.newClickableIconTextField(Person.GSCHOLAR_BASE_URL, GSCHOLAR_ICON);
        this.gscholar.setClearButtonVisible(true);
        this.gscholar.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.gscholar, 2);


        getEntityDataBinder().forField(this.orcid)
                .withConverter(new StringTrimer())
                .withValidator(new OrcidValidator(getTranslation("views.persons.invalid_orcid"), //$NON-NLS-1$
                        getTranslation("views.persons.undesired_orcid"), true)) //$NON-NLS-1$
                .bind(Person::getORCID, Person::setORCID);
        getEntityDataBinder().forField(this.scopus)
                .withConverter(new StringTrimer())
                .bind(Person::getScopusId, Person::setScopusId);
        getEntityDataBinder().forField(this.wos)
                .withConverter(new StringTrimer())
                .bind(Person::getResearcherId, Person::setResearcherId);
        getEntityDataBinder().forField(this.gscholar)
                .withConverter(new StringTrimer())
                .bind(Person::getGoogleScholarId, Person::setGoogleScholarId);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Create the components for entering the researcher biography.
     *
     * @return The content.
     */
    protected VerticalLayout createBiographyComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(2);

        this.privateBiography = new ToggleButton();
        content.add(this.privateBiography, 2);

        this.biography = new MarkdownField();
        content.add(this.biography, 2);

        getEntityDataBinder().forField(this.privateBiography)
                .bind(Person::getPrivateBiography, Person::setPrivateBiography);
        getEntityDataBinder().forField(this.biography)
                .withConverter(new StringTrimer())
                .bind(Person::getBiography, Person::setBiography);

        verticalLayout.add(content);
        return verticalLayout;
    }

    protected void updatePhoto() {
        final var photoURL = getEditedEntity().getPhotoURL();
        this.photo.removeAll();
        if (photoURL != null) {
            final var url = photoURL.toExternalForm();
            final var image = new Image(url, url);
            image.setMinHeight(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            image.setMaxHeight(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            image.setMinWidth(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            image.setMaxWidth(ViewConstants.PHOTO_SIZE, Unit.PIXELS);
            this.photo.add(image);
        } else {
            this.photo = new VerticalLayout();
            final var size = new StringBuilder();
            size.append(ViewConstants.PHOTO_SIZE);
            size.append(Unit.PIXELS.getSymbol());
            final var image = LumoIcon.USER.create();
            image.setSize(size.toString());
            this.photo.add(image);
        }
    }

    protected String getGenderLabel(Gender gender) {
        return gender.getLabel(getMessageSourceAccessor(), getLocale());
    }

    /** Create the components for entering the researcher indicators and indexes.
     * The information includes:<ul>
     * <li>H-index on Web-of-Science (WoS)</li>
     * <li>H-index on Scopus</li>
     * <li>H-index on Google Scholar</li>
     * <li>Number of paper citations on Web-of-Science (WoS)</li>
     * <li>Number of paper citations on Scopus</li>
     * <li>Number of paper citations on Google Scholar</li>
     * </ul>
     *
     * @return The content.
     */
    protected VerticalLayout createIndexesComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content0 = ComponentFactory.newColumnForm(1);

        this.wosHindex = new IntegerField();
        this.wosHindex.setMin(0);
        this.wosHindex.setMax(100);
        this.wosHindex.setClearButtonVisible(true);
        this.wosHindex.setPrefixComponent(VaadinIcon.CONTROLLER.create());
        content0.add(this.wosHindex);

        this.scopusHindex = new IntegerField();
        this.scopusHindex.setMin(0);
        this.scopusHindex.setMax(100);
        this.scopusHindex.setClearButtonVisible(true);
        this.scopusHindex.setPrefixComponent(VaadinIcon.CONTROLLER.create());
        content0.add(this.scopusHindex);

        this.gscholarHindex = new IntegerField();
        this.gscholarHindex.setMin(0);
        this.gscholarHindex.setMax(100);
        this.gscholarHindex.setClearButtonVisible(true);
        this.gscholarHindex.setPrefixComponent(VaadinIcon.CONTROLLER.create());
        content0.add(this.gscholarHindex);

        final var content1 = ComponentFactory.newColumnForm(1);

        this.wosCitations = new IntegerField();
        this.wosCitations.setMin(0);
        this.wosCitations.setMax(100000);
        this.wosCitations.setClearButtonVisible(true);
        this.wosCitations.setPrefixComponent(VaadinIcon.TRENDING_UP.create());
        this.wosCitations.setSuffixComponent(new Div(new Text(getTranslation("views.persons.citations")))); //$NON-NLS-1$
        content1.add(this.wosCitations);

        this.scopusCitations = new IntegerField();
        this.scopusCitations.setMin(0);
        this.scopusCitations.setMax(100000);
        this.scopusCitations.setClearButtonVisible(true);
        this.scopusCitations.setPrefixComponent(VaadinIcon.TRENDING_UP.create());
        this.scopusCitations.setSuffixComponent(new Div(new Text(getTranslation("views.persons.citations")))); //$NON-NLS-1$
        content1.add(this.scopusCitations);

        this.gscholarCitations = new IntegerField();
        this.gscholarCitations.setMin(0);
        this.gscholarCitations.setMax(100000);
        this.gscholarCitations.setClearButtonVisible(true);
        this.gscholarCitations.setPrefixComponent(VaadinIcon.TRENDING_UP.create());
        this.gscholarCitations.setSuffixComponent(new Div(new Text(getTranslation("views.persons.citations")))); //$NON-NLS-1$
        content1.add(this.gscholarCitations);

        final var vl = new VerticalLayout(content0, content1);
        vl.setSpacing(false);


        getEntityDataBinder().forField(this.wosHindex).bind(Person::getWosHindex, Person::setWosHindex);
        getEntityDataBinder().forField(this.scopusHindex).bind(Person::getScopusHindex, Person::setScopusHindex);
        getEntityDataBinder().forField(this.gscholarHindex).bind(Person::getGoogleScholarHindex, Person::setGoogleScholarHindex);
        getEntityDataBinder().forField(this.wosCitations).bind(Person::getWosCitations, Person::setWosCitations);
        getEntityDataBinder().forField(this.scopusCitations).bind(Person::getScopusCitations, Person::setScopusCitations);
        getEntityDataBinder().forField(this.gscholarCitations).bind(Person::getGoogleScholarCitations, Person::setGoogleScholarCitations);

        verticalLayout.add(vl);
        return verticalLayout;
    }

    /** Create the components for entering the social links.
     * The information includes:<ul>
     * <li>Identifier on Research Gate</li>
     * <li>Identifier on AD Scientific Index</li>
     * <li>Internet address of the DBLP page</li>
     * <li>Internet address of the Academia.edu</li>
     * <li>Internet address of the EU CORDIS page</li>
     * <li>Identifier on Linked-In</li>
     * <li>Github Identifier</li>
     * <li>Facebook Identifier</li>
     * </ul>
     *
     * @return The content.
     */
    protected VerticalLayout createSocialLinksComponents() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();

        final var content = ComponentFactory.newColumnForm(1);

        this.researchGate = ComponentFactory.newClickableIconTextField(Person.RESEARCHGATE_BASE_URL, RESEARCHGATE_ICON);
        this.researchGate.setClearButtonVisible(true);
        this.researchGate.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.researchGate);

        this.adScientificIndex = ComponentFactory.newClickableIconTextField(Person.ADSCIENTIFICINDEX_BASE_URL, ADSCIENTIFICINDEX_ICON);
        this.adScientificIndex.setClearButtonVisible(true);
        this.adScientificIndex.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.adScientificIndex);

        this.dblp = ComponentFactory.newClickableIconTextField(DBLP_BASE_URL, DBLP_ICON);
        this.dblp.setClearButtonVisible(true);
        this.dblp.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        content.add(this.dblp);

        this.academiaEdu = ComponentFactory.newClickableIconTextField(ACADEMIA_EDU_BASE_URL, ACADEMIA_EDU_ICON);
        this.academiaEdu.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        this.academiaEdu.setClearButtonVisible(true);
        content.add(this.academiaEdu);

        this.euCordis = ComponentFactory.newClickableIconTextField(EU_CORDIS_BASE_URL, EU_CORDIS_ICON);
        this.euCordis.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        this.euCordis.setClearButtonVisible(true);
        content.add(this.euCordis);

        this.linkedin = ComponentFactory.newClickableIconTextField(Person.LINKEDIN_BASE_URL, LINKEDIN_ICON);
        this.linkedin.setClearButtonVisible(true);
        this.linkedin.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.linkedin);

        this.github = ComponentFactory.newClickableIconTextField(Person.GITHUB_BASE_URL, GITHUB_ICON);
        this.github.setClearButtonVisible(true);
        this.github.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.github);

        this.facebook = ComponentFactory.newClickableIconTextField(Person.FACEBOOK_BASE_URL, FACEBOOK_ICON);
        this.facebook.setClearButtonVisible(true);
        this.facebook.setPrefixComponent(VaadinIcon.HASH.create());
        content.add(this.facebook);


        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.researchGate)
                .withConverter(new StringTrimer())
                .bind(Person::getResearchGateId, Person::setResearchGateId);
        getEntityDataBinder().forField(this.adScientificIndex)
                .withConverter(new StringTrimer())
                .bind(Person::getAdScientificIndexId, Person::setAdScientificIndexId);
        getEntityDataBinder().forField(this.dblp)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Person::getDblpURL, Person::setDblpURL);
        getEntityDataBinder().forField(this.academiaEdu)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Person::getAcademiaURL, Person::setAcademiaURL);
        getEntityDataBinder().forField(this.euCordis)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Person::getCordisURL, Person::setCordisURL);
        getEntityDataBinder().forField(this.linkedin)
                .withConverter(new StringTrimer())
                .bind(Person::getLinkedInId, Person::setLinkedInId);
        getEntityDataBinder().forField(this.github)
                .withConverter(new StringTrimer())
                .bind(Person::getGithubId, Person::setGithubId);
        getEntityDataBinder().forField(this.facebook)
                .withConverter(new StringTrimer())
                .bind(Person::getFacebookId, Person::setFacebookId);

        verticalLayout.add(content);
        return verticalLayout;
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
        this.lastname.setLabel(getTranslation("views.persons.last_name")); //$NON-NLS-1$
        this.firstname.setLabel(getTranslation("views.persons.first_name")); //$NON-NLS-1$
        this.gender.setLabel(getTranslation("views.persons.gender")); //$NON-NLS-1$
        this.gender.setHelperText(getTranslation("views.persons.gender.helper")); //$NON-NLS-1$);
        this.gender.setItemLabelGenerator(this::getGenderLabel);
        this.nationality.setLabel(getTranslation("views.persons.nationality")); //$NON-NLS-1$
        this.nationality.setHelperText(getTranslation("views.persons.nationality.helper")); //$NON-NLS-1$);
        ComponentFactory.updateCountryComboBoxItems(this.nationality, getLocale());
        this.gravatarId.setLabel(getTranslation("views.persons.gravatar_id")); //$NON-NLS-1$
        this.gravatarId.setHelperText(getTranslation("views.persons.gravatar_id.helper")); //$NON-NLS-1$

        final String helpPhone = getTranslation("views.phonenumber.help"); //$NON-NLS-1$
        final String errPhone = getTranslation("views.phonenumber.invalid"); //$NON-NLS-1$

        this.email.setLabel(getTranslation("views.persons.email")); //$NON-NLS-1$
        this.email.setErrorMessage(getTranslation("views.persons.email.error")); //$NON-NLS-1$
        this.officePhone.setLabel(getTranslation("views.persons.officephone")); //$NON-NLS-1$
        this.officePhone.setDynamicHelperText(helpPhone);
        this.officePhone.setErrorMessage(errPhone);
        this.mobilePhone.setLabel(getTranslation("views.persons.mobilephone")); //$NON-NLS-1$
        this.mobilePhone.setDynamicHelperText(helpPhone);
        this.officeRoom.setLabel(getTranslation("views.persons.officeroom")); //$NON-NLS-1$
        this.officeRoom.setHelperText(getTranslation("views.persons.officeroom.helper")); //$NON-NLS-1$

        this.privateBiography.setLabel(getTranslation("views.persons.biography_private")); //$NON-NLS-1$
        this.biography.setLabel(getTranslation("views.persons.biography")); //$NON-NLS-1$

        this.orcid.setLabel(getTranslation("views.persons.orcid")); //$NON-NLS-1$
        this.orcid.setHelperText(getTranslation("views.persons.orcid.helper")); //$NON-NLS-1$
        this.scopus.setLabel(getTranslation("views.persons.scopus")); //$NON-NLS-1$
        this.scopus.setHelperText(getTranslation("views.persons.scopus.helper")); //$NON-NLS-1$
        this.wos.setLabel(getTranslation("views.persons.wos")); //$NON-NLS-1$
        this.wos.setHelperText(getTranslation("views.persons.wos.helper")); //$NON-NLS-1$
        this.gscholar.setLabel(getTranslation("views.persons.gscholar")); //$NON-NLS-1$
        this.gscholar.setHelperText(getTranslation("views.persons.gscholar.helper")); //$NON-NLS-1$


        this.wosHindex.setLabel(getTranslation("views.persons.wos_hindex")); //$NON-NLS-1$
        this.wosHindex.setHelperText(getTranslation("views.persons.wos_update.helper")); //$NON-NLS-1$
        this.scopusHindex.setLabel(getTranslation("views.persons.scopus_hindex")); //$NON-NLS-1$
        this.scopusHindex.setHelperText(getTranslation("views.persons.scopus_update.helper")); //$NON-NLS-1$
        this.gscholarHindex.setLabel(getTranslation("views.persons.gscholar_hindex")); //$NON-NLS-1$
        this.gscholarHindex.setHelperText(getTranslation("views.persons.gscholar_update.helper")); //$NON-NLS-1$
        this.wosCitations.setLabel(getTranslation("views.persons.wos_citations")); //$NON-NLS-1$
        this.wosCitations.setHelperText(getTranslation("views.persons.wos_update.helper")); //$NON-NLS-1$
        this.scopusCitations.setLabel(getTranslation("views.persons.scopus_citations")); //$NON-NLS-1$
        this.scopusCitations.setHelperText(getTranslation("views.persons.scopus_update.helper")); //$NON-NLS-1$
        this.gscholarCitations.setLabel(getTranslation("views.persons.gscholar_citations")); //$NON-NLS-1$
        this.gscholarCitations.setHelperText(getTranslation("views.persons.gscholar_update.helper")); //$NON-NLS-1$


        this.researchGate.setLabel(getTranslation("views.persons.researchgate")); //$NON-NLS-1$
        this.researchGate.setHelperText(getTranslation("views.persons.researchgate.helper")); //$NON-NLS-1$
        this.adScientificIndex.setLabel(getTranslation("views.persons.adscientificindex")); //$NON-NLS-1$
        this.dblp.setLabel(getTranslation("views.persons.dblp")); //$NON-NLS-1$
        this.academiaEdu.setLabel(getTranslation("views.persons.academia_edu")); //$NON-NLS-1$
        this.euCordis.setLabel(getTranslation("views.persons.eu_cordis")); //$NON-NLS-1$
        this.euCordis.setHelperText(getTranslation("views.persons.eu_cordis.helper")); //$NON-NLS-1$
        this.linkedin.setLabel(getTranslation("views.persons.linkedin")); //$NON-NLS-1$
        this.linkedin.setHelperText(getTranslation("views.persons.linkedin.helper")); //$NON-NLS-1$
        this.github.setLabel(getTranslation("views.persons.github")); //$NON-NLS-1$
        this.github.setHelperText(getTranslation("views.persons.github.helper")); //$NON-NLS-1$
        this.facebook.setLabel(getTranslation("views.persons.facebook")); //$NON-NLS-1$
        this.facebook.setHelperText(getTranslation("views.persons.facebook.helper")); //$NON-NLS-1$

        if (this.webpageConvention != null) {
            this.webpageConvention.setLabel(getTranslation("views.persons.administration.webpage_naming")); //$NON-NLS-1$
            this.webpageConvention.setItemLabelGenerator(this::getWebPageNamingLabel);
        }
        if (this.userLogin != null) {
            this.userLogin.setLabel(getTranslation("views.persons.administration.user_login")); //$NON-NLS-1$
            this.userLogin.setHelperText(getTranslation("views.persons.administration.user_login.help")); //$NON-NLS-1$
        }
        if (this.userRole != null) {
            this.userRole.setLabel(getTranslation("views.persons.administration.user_role")); //$NON-NLS-1$
            this.userRole.setHelperText(getTranslation("views.persons.administration.user_role.help")); //$NON-NLS-1$
            this.userRole.setItemLabelGenerator(this::getUserRoleLabel);
        }
    }

    private String getWebPageNamingLabel(WebPageNaming naming) {
        return naming.getLabel(getMessageSourceAccessor(), getLocale());
    }

    private String getUserRoleLabel(UserRole role) {
        return role.getLabel(getMessageSourceAccessor(), getLocale());
    }

    protected Binder<User> getUserDataBinder() {
        return this.userBinder;
    }

}
