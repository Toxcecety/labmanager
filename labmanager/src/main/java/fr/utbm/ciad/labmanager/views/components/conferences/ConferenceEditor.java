package fr.utbm.ciad.labmanager.views.components.conferences;

import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.ranking.ConferenceAnnualRankingField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IsbnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IssnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.CORE_PORTAL_BASE_URL;
import static fr.utbm.ciad.labmanager.views.ViewConstants.CORE_PORTAL_ICON;

/** Implementation for the editor of the information related to a scientific conference. It is directly linked for
 * using it with a wizard.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class ConferenceEditor extends AbstractEntityEditor<Conference> {
    private static final long serialVersionUID = 6750040717745583722L;

    private TextField acronym;

    private TextField name;

    private RadioButtonGroup<Boolean> openAccess;

    private TextField conferenceUrl;

    private TextField coreId;

    private ConferenceAnnualRankingField rankings;

    private TextField publisherName;

    private TextField issn;

    private TextField isbn;

    private ConferenceEditorComponentWizard conferenceEditorComponentWizard;

    /** Constructor.
     *
     * @param context the editing context for the conference.
     * @param relinkEntityWhenSaving indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *     be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param authenticatedUser the connected user.
     * @param messages the accessor to the localized messages (Spring layer).
     * @param logger the logger to be used by this view.
     */
    public ConferenceEditor(AbstractEntityService.EntityEditingContext<Conference> context, boolean relinkEntityWhenSaving,
                                    AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, Logger logger) {
        super(Conference.class, authenticatedUser, messages, logger,
                "views.conferences.administration_details", //$NON-NLS-1$
                "views.conferences.administration.validated_conference", //$NON-NLS-1$
                context, relinkEntityWhenSaving);
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
            conferenceEditorComponentWizard = new ConferenceEditorComponentWizard(
                    createDescriptionDetails(),
                    createRankingDetails(),
                    createPublisherDetails(),
                    createAdministrationComponents((Consumer<FormLayout>) null, it -> it.bind(Conference::isValidated, Conference::setValidated)));
        }else{
            conferenceEditorComponentWizard = new ConferenceEditorComponentWizard(
                    createDescriptionDetails(),
                    createRankingDetails(),
                    createPublisherDetails());
        }
        rootContainer.add(conferenceEditorComponentWizard);
    }

    /** Create the section for editing the description of the conference.
     *
     * @return The content.
     */
    protected VerticalLayout createDescriptionDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        this.acronym = new TextField();
        this.acronym.setPrefixComponent(VaadinIcon.HASH.create());
        this.acronym.setRequired(true);
        this.acronym.setClearButtonVisible(true);
        content.add(this.acronym, 2);

        this.name = new TextField();
        this.name.setPrefixComponent(VaadinIcon.HASH.create());
        this.name.setRequired(true);
        this.name.setClearButtonVisible(true);
        content.add(this.name, 2);

        this.openAccess = new RadioButtonGroup<>();
        this.openAccess.setItems(null, Boolean.TRUE, Boolean.FALSE);
        this.openAccess.setValue(null);
        setOpenAccessRenderer();
        content.add(this.openAccess, 2);

        this.conferenceUrl = new TextField();
        this.conferenceUrl.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        this.conferenceUrl.setClearButtonVisible(true);
        content.add(this.conferenceUrl, 2);

        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.acronym)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.conferences.acronym.error"))) //$NON-NLS-1$
                .bind(Conference::getAcronym, Conference::setAcronym);
        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.conferences.name.error"))) //$NON-NLS-1$
                .bind(Conference::getName, Conference::setName);
        getEntityDataBinder().forField(this.openAccess)
                .bind(Conference::getOpenAccess, Conference::setOpenAccess);
        getEntityDataBinder().forField(this.conferenceUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Conference::getConferenceURL, Conference::setConferenceURL);

        verticalLayout.add(content);
        return verticalLayout;
    }

    private void setOpenAccessRenderer() {
        this.openAccess.setRenderer(new ComponentRenderer<>(it -> {
            final var span = new Span();
            if (it == null) {
                span.setText(getTranslation("views.conferences.open_access.indeterminate")); //$NON-NLS-1$
            } else if (it == Boolean.TRUE) {
                span.setText(getTranslation("views.conferences.open_access.yes")); //$NON-NLS-1$
            } else {
                span.setText(getTranslation("views.conferences.open_access.no")); //$NON-NLS-1$
            }
            return span;
        }));
    }

    /** Create the section for editing the ranking information of the conference.
     *
     * @return The content.
     */
    protected VerticalLayout createRankingDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        this.coreId = ComponentFactory.newClickableIconTextField(CORE_PORTAL_BASE_URL, CORE_PORTAL_ICON);
        this.coreId.setPrefixComponent(VaadinIcon.HASH.create());
        this.coreId.setClearButtonVisible(true);
        content.add(this.coreId, 2);

        this.rankings = new ConferenceAnnualRankingField();
        content.add(this.rankings, 2);


        getEntityDataBinder().forField(this.coreId)
                .withConverter(new StringTrimer())
                .bind(Conference::getCoreId, Conference::setCoreId);
        getEntityDataBinder().forField(this.rankings)
                .bind(Conference::getQualityIndicators, Conference::setQualityIndicators);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Create the section for editing the publishing information of the conference.
     *
     * @return The content.
     */
    protected VerticalLayout createPublisherDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        this.publisherName = new TextField();
        this.publisherName.setPrefixComponent(VaadinIcon.OPEN_BOOK.create());
        this.publisherName.setRequired(true);
        this.publisherName.setClearButtonVisible(true);
        content.add(this.publisherName, 2);

        this.issn = new TextField();
        this.issn.setPrefixComponent(VaadinIcon.HASH.create());
        this.issn.setClearButtonVisible(true);
        content.add(this.issn, 1);

        this.isbn = new TextField();
        this.isbn.setPrefixComponent(VaadinIcon.HASH.create());
        this.isbn.setClearButtonVisible(true);
        content.add(this.isbn, 1);

        getEntityDataBinder().forField(this.publisherName)
                .withConverter(new StringTrimer())
                .bind(Conference::getPublisher, Conference::setPublisher);
        getEntityDataBinder().forField(this.issn)
                .withConverter(new StringTrimer())
                .withValidator(new IssnValidator(getTranslation("views.conferences.issn.error"), true)) //$NON-NLS-1$
                .bind(Conference::getISSN, Conference::setISSN);
        getEntityDataBinder().forField(this.isbn)
                .withConverter(new StringTrimer())
                .withValidator(new IsbnValidator(getTranslation("views.conferences.isbn.error"), true)) //$NON-NLS-1$
                .bind(Conference::getISBN, Conference::setISBN);

        verticalLayout.add(content);
        return verticalLayout;
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.conferences.save_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.conferences.validation_success", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.conferences.delete_success2", //$NON-NLS-1$
                getEditedEntity().getName());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.conferences.save_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.conferences.validation_error", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.conferences.delete_error2", //$NON-NLS-1$
                getEditedEntity().getName(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

        this.acronym.setLabel(getTranslation("views.conferences.acronym")); //$NON-NLS-1$
        this.acronym.setHelperText(getTranslation("views.conferences.acronym.help")); //$NON-NLS-1$
        this.name.setLabel(getTranslation("views.conferences.name")); //$NON-NLS-1$
        this.name.setHelperText(getTranslation("views.conferences.name.help")); //$NON-NLS-1$
        this.openAccess.setLabel(getTranslation("views.conferences.open_access")); //$NON-NLS-1$
        // Force the refreshing of the radio button items
        setOpenAccessRenderer();
        this.conferenceUrl.setLabel(getTranslation("views.conferences.url")); //$NON-NLS-1$

        this.coreId.setLabel(getTranslation("views.conferences.core.id")); //$NON-NLS-1$
        this.coreId.setHelperText(getTranslation("views.conferences.core.id.help")); //$NON-NLS-1$
        this.rankings.setLabel(getTranslation("views.conferences.rankings")); //$NON-NLS-1$
        this.rankings.setHelperText(getTranslation("views.conferences.rankings.help")); //$NON-NLS-1$

        this.publisherName.setLabel(getTranslation("views.conferences.publisher_name")); //$NON-NLS-1$
        this.publisherName.setHelperText(getTranslation("views.conferences.publisher_name.help")); //$NON-NLS-1$
        this.issn.setLabel(getTranslation("views.journals.issn")); //$NON-NLS-1$
        this.isbn.setLabel(getTranslation("views.journals.isbn")); //$NON-NLS-1$
    }
}
