package fr.utbm.ciad.labmanager.views.components.organizations;

import com.google.common.base.Strings;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationNameComparator;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationType;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.markdown.MarkdownField;
import fr.utbm.ciad.labmanager.views.components.addons.uploads.image.ServerSideUploadableImageField;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import fr.utbm.ciad.labmanager.views.components.addons.value.ComboListField;
import fr.utbm.ciad.labmanager.views.components.addons.value.LeftRightListsField;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

import java.util.function.Consumer;

import static fr.utbm.ciad.labmanager.views.ViewConstants.DBLP_BASE_URL;
import static fr.utbm.ciad.labmanager.views.ViewConstants.DBLP_ICON;

/** Implementation for the editor of the information related to an organization. It is directly linked for
 * using it with a wizard.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public abstract class AbstractOrganizationEditorWizard extends AbstractOrganizationEditor {

    private static final long serialVersionUID = -4405231976086480507L;

    private OrganizationEditorComponentWizard organizationEditorComponentWizard;

    /**
     * Constructor.
     *
     * @param context                            the context for editing the entity.
     * @param fileManager                        the manager of files at the server-side.
     * @param authenticatedUser                  the connected user.
     * @param messages                           the accessor to the localized messages (Spring layer).
     * @param logger                             the logger to be used by this view.
     * @param relinkEntityWhenSaving             indicates if the editor must be relink to the edited entity when it is saved. This new link may
     *                                           be required if the editor is not closed after saving in order to obtain a correct editing of the entity.
     * @param organizationService                the service for accessing the organizations.
     * @param addressService                     the service for accessing the organization addresses.
     */
    public AbstractOrganizationEditorWizard(AbstractEntityService.EntityEditingContext<ResearchOrganization> context, boolean relinkEntityWhenSaving,
                                            DownloadableFileManager fileManager, AuthenticatedUser authenticatedUser,
                                            MessageSourceAccessor messages, Logger logger,
                                            ResearchOrganizationService organizationService, OrganizationAddressService addressService) {
        super(context, relinkEntityWhenSaving, fileManager, authenticatedUser, messages, logger, organizationService, addressService);

    }

    /** Create the content of the editor.
     * This function should invoke {@link #createAdministrationComponents(VerticalLayout, Consumer, Consumer)}.
     *
     * @param rootContainer the container.
     * @see #createAdministrationComponents(VerticalLayout, Consumer, Consumer)
     */
    @Override
    protected void createEditorContent(VerticalLayout rootContainer) {
        if (isBaseAdmin()) {
            organizationEditorComponentWizard = new OrganizationEditorComponentWizard(createDescriptionDetails(),
                    createGeographicalDetails(), createIdentificationDetails(), createSuperStructureDetails(),
                    createCommunicationDetails(),createAdministrationComponents(
                    content -> {
                        this.majorOrganization = new ToggleButton();
                        content.add(this.majorOrganization, 1);

                        getEntityDataBinder().forField(this.majorOrganization)
                                .bind(ResearchOrganization::isMajorOrganization, ResearchOrganization::setMajorOrganization);
                    },
                    it -> it.bind(ResearchOrganization::isValidated, ResearchOrganization::setValidated)));

        }else{
            organizationEditorComponentWizard = new OrganizationEditorComponentWizard(createDescriptionDetails(),
                    createGeographicalDetails(), createIdentificationDetails(), createSuperStructureDetails(),
                    createCommunicationDetails());
        }
        rootContainer.add(organizationEditorComponentWizard);
    }

    /** Create the section for editing the description of the organization.
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
        content.add(this.acronym, 1);

        this.name = new TextField();
        this.name.setPrefixComponent(VaadinIcon.HASH.create());
        this.name.setRequired(true);
        this.name.setClearButtonVisible(true);
        content.add(this.name, 2);

        this.type = new ComboBox<>();
        this.type.setPrefixComponent(VaadinIcon.DIPLOMA.create());
        this.type.setItems(ResearchOrganizationType.values());
        this.type.setItemLabelGenerator(this::getTypeLabel);
        this.type.setValue(ResearchOrganizationType.DEFAULT);
        content.add(this.type, 2);

        getEntityDataBinder().forField(this.acronym)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.organizations.acronym.error"))) //$NON-NLS-1$
                .bind(ResearchOrganization::getAcronym, ResearchOrganization::setAcronym);
        getEntityDataBinder().forField(this.name)
                .withConverter(new StringTrimer())
                .withValidator(new NotEmptyStringValidator(getTranslation("views.organizations.name.error"))) //$NON-NLS-1$
                .bind(ResearchOrganization::getName, ResearchOrganization::setName);
        getEntityDataBinder().forField(this.type).bind(ResearchOrganization::getType, ResearchOrganization::setType);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Create the section for editing the geograpihcal information of the organization.
     *
     * @return The content.
     */
    protected VerticalLayout createGeographicalDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        this.addresses = new LeftRightListsField<>(ComponentFactory.toSerializableComparator(EntityUtils.getPreferredOrganizationAddressComparator()), this::openAddressEditor);
        this.addresses.setEntityLabelGenerator(it -> it.getName());
        this.addresses.setAvailableEntities(this.addressService.getAllAddresses());
        content.add(this.addresses, 2);

        this.country = ComponentFactory.newCountryComboBox(getLocale());
        this.country.setPrefixComponent(VaadinIcon.GLOBE_WIRE.create());
        content.add(this.country, 1);

        getEntityDataBinder().forField(this.addresses).bind(ResearchOrganization::getAddresses, ResearchOrganization::setAddresses);
        getEntityDataBinder().forField(this.country).bind(ResearchOrganization::getCountry, ResearchOrganization::setCountry);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Invoked for creating a new organization address.
     *
     * @param saver the callback that is invoked when the address is saved as JPA entity.
     */
    protected void openAddressEditor(Consumer<OrganizationAddress> saver) {
        final var newAddress = new OrganizationAddress();
        final var editor = new EmbeddedAddressEditor(
                this.addressService.startEditing(newAddress),
                this.fileManager,
                getAuthenticatedUser(), getMessageSourceAccessor());
        ComponentFactory.openEditionModalDialog(
                getTranslation("views.organizations.create_address"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    /** Create the section for editing the identification of the organization.
     *
     * @return The content.
     */
    protected VerticalLayout createIdentificationDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        this.nationalIdentifier = new TextField();
        this.nationalIdentifier.setPrefixComponent(VaadinIcon.HASH.create());
        this.nationalIdentifier.setClearButtonVisible(true);
        content.add(this.nationalIdentifier, 2);

        this.rnsr = new TextField();
        this.rnsr.setPrefixComponent(VaadinIcon.HASH.create());
        this.rnsr.setClearButtonVisible(true);
        content.add(this.rnsr, 2);

        getEntityDataBinder().forField(this.nationalIdentifier)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getNationalIdentifier, ResearchOrganization::setNationalIdentifier);
        getEntityDataBinder().forField(this.rnsr)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getRnsr, ResearchOrganization::setRnsr);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Create the section for editing the super structure of the organization.
     *
     * @return The content.
     */
    protected VerticalLayout createSuperStructureDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        final var currentId = getEditedEntity().getId();
        this.superStructures = new ComboListField<>(ComponentFactory.toSerializableComparator(new ResearchOrganizationNameComparator()), this::openOrganizationEditor);
        this.superStructures.setEntityRenderers(
                this::createNameString,
                new ComponentRenderer<>(this::createNameComponent),
                new ComponentRenderer<>(this::createNameComponent));
        this.superStructures.setAvailableEntities(query -> {
            return this.organizationService.getAllResearchOrganizations(
                    VaadinSpringDataHelpers.toSpringPageRequest(query),
                    new AbstractOrganizationEditor.OtherOrganizationSpecification(currentId),
                    null).stream();
        });
        content.add(this.superStructures, 2);

        getEntityDataBinder().forField(this.superStructures).bind(ResearchOrganization::getSuperOrganizations, ResearchOrganization::setSuperOrganizations);

        verticalLayout.add(content);
        return verticalLayout;
    }

    /** Invoked for creating a new organization.
     *
     * @param saver the callback that is invoked when the organization is saved as JPA entity.
     */
    protected void openOrganizationEditor(Consumer<ResearchOrganization> saver) {
        final var newOrganization = new ResearchOrganization();
        final var editor = new EmbeddedOrganizationEditor(
                this.organizationService.startEditing(newOrganization),
                this.fileManager,
                getAuthenticatedUser(), getMessageSourceAccessor(),
                this.organizationService,
                this.addressService);
        ComponentFactory.openEditionModalDialog(
                getTranslation("views.organizations.create_organization"), //$NON-NLS-1$
                editor, false,
                (dialog, entity) -> saver.accept(entity),
                null);
    }

    private String createNameString(ResearchOrganization organization) {
        final var buffer = new StringBuilder();
        final var acronym = organization.getAcronym();
        if (!Strings.isNullOrEmpty(acronym)) {
            buffer.append(acronym);
        }
        final var name = organization.getName();
        if (!Strings.isNullOrEmpty(name)) {
            if (buffer.length() > 0) {
                buffer.append(" - "); //$NON-NLS-1$
            }
            buffer.append(name);
        }
        return buffer.toString();
    }

    private Component createNameComponent(ResearchOrganization organization) {
        return ComponentFactory.newOrganizationAvatar(organization, this.fileManager);
    }

    /** Create the section for editing the communication of the organization.
     *
     * @return The content.
     */
    protected VerticalLayout createCommunicationDetails() {
        VerticalLayout verticalLayout = new VerticalLayout();
        verticalLayout.setSizeFull();
        final var content = ComponentFactory.newColumnForm(2);

        this.organizationUrl = new TextField();
        this.organizationUrl = ComponentFactory.newClickableIconTextField(DBLP_BASE_URL, DBLP_ICON);
        this.organizationUrl.setClearButtonVisible(true);
        this.organizationUrl.setPrefixComponent(VaadinIcon.EXTERNAL_LINK.create());
        content.add(this.organizationUrl, 2);

        this.description = new MarkdownField();
        content.add(this.description, 2);

        this.logo = new ServerSideUploadableImageField(this.fileManager,
                ext -> this.fileManager.makeOrganizationLogoFilename(getEditedEntity().getId(), ext));
        this.logo.setClearButtonVisible(true);
        content.add(this.logo, 2);

        final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

        getEntityDataBinder().forField(this.organizationUrl)
                .withConverter(new StringTrimer())
                .withValidator(new UrlValidator(invalidUrl, true))
                .bind(ResearchOrganization::getOrganizationURL, ResearchOrganization::setOrganizationURL);
        getEntityDataBinder().forField(this.description)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getDescription, ResearchOrganization::setDescription);
        getEntityDataBinder().forField(this.logo)
                .withConverter(new StringTrimer())
                .bind(ResearchOrganization::getPathToLogo, ResearchOrganization::setPathToLogo);

        verticalLayout.add(content);
        return verticalLayout;
    }

    private String getTypeLabel(ResearchOrganizationType type) {
        return type.getLabel(getMessageSourceAccessor(), getLocale());
    }

    @Override
    public void localeChange(LocaleChangeEvent event) {
        super.localeChange(event);
    }
}
