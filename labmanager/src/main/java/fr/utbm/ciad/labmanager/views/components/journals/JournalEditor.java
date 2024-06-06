package fr.utbm.ciad.labmanager.views.components.journals;

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
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.ranking.JournalAnnualRankingField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IsbnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IssnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.*;

public class JournalEditor extends AbstractEntityEditor<Journal> {

    private TextField name;

    private RadioButtonGroup<Boolean> openAccess;

    private TextField journalUrl;

    private TextField publisherName;

    private TextField publisherAddress;

    private TextField issn;

    private TextField isbn;

    private TextField wosId;

    private TextField wosCategory;

    private TextField scimagoId;

    private TextField scimagoCategory;

    private JournalAnnualRankingField rankings;

    private JournalEditorComponentWizard journalEditorComponentWizard;


    /** Constructor.
     *
     * @param context the editing context for the conference.
     * @param relinkEntityWhenSaving indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *     be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param authenticatedUser the connected user.
     * @param messages the accessor to the localized messages (Spring layer).
     * @param logger the logger to be used by this view.
     */
    public JournalEditor(AbstractEntityService.EntityEditingContext<Journal> context, boolean relinkEntityWhenSaving,
                                 AuthenticatedUser authenticatedUser, MessageSourceAccessor messages, Logger logger) {
        super(Journal.class, authenticatedUser, messages, logger,
                "views.journals.administration_details", //$NON-NLS-1$
                "views.journals.administration.validated_organization", //$NON-NLS-1$
                context, relinkEntityWhenSaving);
        if (isBaseAdmin()) {
            journalEditorComponentWizard = new JournalEditorComponentWizard(createDescriptionDetails(), createRankingDetails(), createPublisherDetails(), createAdministrationComponents((Consumer<FormLayout>) null, it -> it.bind(Journal::isValidated, Journal::setValidated)));
        }
        journalEditorComponentWizard = new JournalEditorComponentWizard(createDescriptionDetails(), createRankingDetails(), createPublisherDetails());

    }

    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        rootContainer.add(journalEditorComponentWizard);
    }

    /** Create the section for editing the description of the journal.
     *
     * @param rootContainer the container.
     */
    protected VerticalLayout createDescriptionDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

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

        this.journalUrl = new TextField();
        this.journalUrl.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        this.journalUrl.setClearButtonVisible(true);
        content.add(this.journalUrl, 2);


        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.journals.name.error"))) //$NON-NLS-1$
                .bind(Journal::getJournalName, Journal::setJournalName);
        getEntityDataBinder().forField(this.openAccess)
                .bind(Journal::getOpenAccess, Journal::setOpenAccess);
        getEntityDataBinder().forField(this.journalUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(Journal::getJournalURL, Journal::setJournalURL);

        verticalLayout.add(content);
        return verticalLayout;
    }

    private void setOpenAccessRenderer() {
        this.openAccess.setRenderer(new ComponentRenderer<>(it -> {
            final Span span = new Span();
            if (it == null) {
                span.setText(getTranslation("views.journals.open_access.indeterminate")); //$NON-NLS-1$
            } else if (it == Boolean.TRUE) {
                span.setText(getTranslation("views.journals.open_access.yes")); //$NON-NLS-1$
            } else {
                span.setText(getTranslation("views.journals.open_access.no")); //$NON-NLS-1$
            }
            return span;
        }));
    }

    /** Create the section for editing the publishing information of the journal.
     *
     * @param rootContainer the container.
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

        this.publisherAddress = new TextField();
        this.publisherAddress.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        this.publisherAddress.setRequired(true);
        this.publisherAddress.setClearButtonVisible(true);
        content.add(this.publisherAddress, 2);

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
                .withValidator(new NotEmptyStringValidator(getTranslation("views.journals.publisher_name.error"))) //$NON-NLS-1$
                .bind(Journal::getPublisher, Journal::setPublisher);
        getEntityDataBinder().forField(this.publisherAddress)
                .withConverter(new StringTrimer())
                .bind(Journal::getAddress, Journal::setAddress);
        getEntityDataBinder().forField(this.issn)
                .withConverter(new StringTrimer())
                .withValidator(new IssnValidator(getTranslation("views.journals.issn.error"), true)) //$NON-NLS-1$
                .bind(Journal::getISSN, Journal::setISSN);
        getEntityDataBinder().forField(this.isbn)
                .withConverter(new StringTrimer())
                .withValidator(new IsbnValidator(getTranslation("views.journals.isbn.error"), true)) //$NON-NLS-1$
                .bind(Journal::getISBN, Journal::setISBN);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Create the section for editing the ranking information of the journal.
     *
     * @param rootContainer the container.
     */
    protected VerticalLayout createRankingDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        this.wosId = ComponentFactory.newClickableIconTextField(Person.WOS_BASE_URL, WOS_ICON);
        this.wosId.setPrefixComponent(VaadinIcon.HASH.create());
        this.wosId.setClearButtonVisible(true);
        content.add(this.wosId, 2);

        this.wosCategory = ComponentFactory.newClickableIconTextField(Person.WOS_BASE_URL, WOS_ICON);
        this.wosCategory.setPrefixComponent(VaadinIcon.FILE_TREE_SMALL.create());
        this.wosCategory.setClearButtonVisible(true);
        content.add(this.wosCategory, 2);

        this.scimagoId = ComponentFactory.newClickableIconTextField(SCIMAGO_BASE_URL, SCIMAGO_ICON);
        this.scimagoId.setPrefixComponent(VaadinIcon.HASH.create());
        this.scimagoId.setClearButtonVisible(true);
        content.add(this.scimagoId, 2);

        this.scimagoCategory = ComponentFactory.newClickableIconTextField(SCIMAGO_BASE_URL, SCIMAGO_ICON);
        this.scimagoCategory.setPrefixComponent(VaadinIcon.FILE_TREE_SMALL.create());
        this.scimagoCategory.setClearButtonVisible(true);
        content.add(this.scimagoCategory, 2);

        this.rankings = new JournalAnnualRankingField();
        content.add(this.rankings, 2);

        getEntityDataBinder().forField(this.wosId)
                .withConverter(new StringTrimer())
                .bind(Journal::getWosId, Journal::setWosId);
        getEntityDataBinder().forField(this.wosCategory)
                .withConverter(new StringTrimer())
                .bind(Journal::getWosCategory, Journal::setWosCategory);
        getEntityDataBinder().forField(this.scimagoId)
                .withConverter(new StringTrimer())
                .bind(Journal::getScimagoId, Journal::setScimagoId);
        getEntityDataBinder().forField(this.scimagoCategory)
                .withConverter(new StringTrimer())
                .bind(Journal::getScimagoCategory, Journal::setScimagoCategory);
        getEntityDataBinder().forField(this.rankings)
                .bind(Journal::getQualityIndicators, Journal::setQualityIndicators);

        verticalLayout.add(content);
        return verticalLayout;
    }

    @Override
    protected String computeSavingSuccessMessage() {
        return getTranslation("views.journals.save_success", //$NON-NLS-1$
                getEditedEntity().getJournalName());
    }

    @Override
    protected String computeValidationSuccessMessage() {
        return getTranslation("views.journals.validation_success", //$NON-NLS-1$
                getEditedEntity().getJournalName());
    }

    @Override
    protected String computeDeletionSuccessMessage() {
        return getTranslation("views.journals.delete_success2", //$NON-NLS-1$
                getEditedEntity().getJournalName());
    }

    @Override
    protected String computeSavingErrorMessage(Throwable error) {
        return getTranslation("views.journals.save_error", //$NON-NLS-1$
                getEditedEntity().getJournalName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeValidationErrorMessage(Throwable error) {
        return getTranslation("views.journals.validation_error", //$NON-NLS-1$
                getEditedEntity().getJournalName(), error.getLocalizedMessage());
    }

    @Override
    protected String computeDeletionErrorMessage(Throwable error) {
        return getTranslation("views.journals.delete_error2", //$NON-NLS-1$
                getEditedEntity().getJournalName(), error.getLocalizedMessage());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);

        this.name.setLabel(getTranslation("views.journals.name")); //$NON-NLS-1$
        this.openAccess.setLabel(getTranslation("views.journals.open_access")); //$NON-NLS-1$
        // Force the refreshing of the radio button items
        setOpenAccessRenderer();
        this.journalUrl.setLabel(getTranslation("views.journals.url")); //$NON-NLS-1$

        this.wosId.setLabel(getTranslation("views.journals.wos.id")); //$NON-NLS-1$
        this.wosId.setHelperText(getTranslation("views.journals.wos.id.help")); //$NON-NLS-1$
        this.wosCategory.setLabel(getTranslation("views.journals.wos.category")); //$NON-NLS-1$
        this.wosCategory.setHelperText(getTranslation("views.journals.wos.category.help")); //$NON-NLS-1$
        this.scimagoId.setLabel(getTranslation("views.journals.scimago.id")); //$NON-NLS-1$
        this.scimagoId.setHelperText(getTranslation("views.journals.scimago.id.help")); //$NON-NLS-1$
        this.scimagoCategory.setLabel(getTranslation("views.journals.scimago.category")); //$NON-NLS-1$
        this.scimagoCategory.setHelperText(getTranslation("views.journals.scimago.category.help")); //$NON-NLS-1$
        this.rankings.setLabel(getTranslation("views.journals.rankings")); //$NON-NLS-1$
        this.rankings.setHelperText(getTranslation("views.journals.rankings.help")); //$NON-NLS-1$

        this.publisherName.setLabel(getTranslation("views.journals.publisher_name")); //$NON-NLS-1$
        this.publisherName.setHelperText(getTranslation("views.journals.publisher_name.help")); //$NON-NLS-1$
        this.publisherAddress.setLabel(getTranslation("views.journals.publisher_address")); //$NON-NLS-1$
        this.issn.setLabel(getTranslation("views.journals.issn")); //$NON-NLS-1$
        this.isbn.setLabel(getTranslation("views.journals.isbn")); //$NON-NLS-1$
    }
}
