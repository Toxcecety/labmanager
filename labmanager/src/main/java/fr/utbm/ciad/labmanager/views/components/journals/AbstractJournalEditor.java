/*
 * $Id$
 * 
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package fr.utbm.ciad.labmanager.views.components.journals;

import static fr.utbm.ciad.labmanager.views.ViewConstants.SCIMAGO_BASE_URL;
import static fr.utbm.ciad.labmanager.views.ViewConstants.SCIMAGO_ICON;
import static fr.utbm.ciad.labmanager.views.ViewConstants.WOS_ICON;

import com.vaadin.flow.component.dependency.Uses;
import com.vaadin.flow.component.details.Details;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.services.journal.JournalService.EditingContext;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.converters.StringTrimer;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMark;
import fr.utbm.ciad.labmanager.views.components.addons.details.DetailsWithErrorMarkStatusHandler;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IsbnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.IssnValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.NotEmptyStringValidator;
import fr.utbm.ciad.labmanager.views.components.addons.validators.UrlValidator;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/** Abstract implementation for the editor of the information related to a scientific journal.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Uses(Icon.class)
public abstract class AbstractJournalEditor extends AbstractEntityEditor<Journal> {

	private static final long serialVersionUID = -6217566556396821734L;

	private DetailsWithErrorMark descriptionDetails;

	private TextField name;

	private RadioButtonGroup<Boolean> openAccess;

	private TextField journalUrl;

	private DetailsWithErrorMark publisherDetails;

	private TextField publisherName;

	private TextField publisherAddress;

	private TextField issn;

	private TextField isbn;

	private Details rankingDetails;

	private TextField wosId;

	private TextField wosCategory;

	private TextField scimagoId;

	private TextField scimagoCategory;

	private final EditingContext context;

	/** Constructor.
	 *
	 * @param context the editing context for the conference.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (Spring layer).
	 * @param logger the logger to be used by this view.
	 */
	public AbstractJournalEditor(EditingContext context, AuthenticatedUser authenticatedUser,
			MessageSourceAccessor messages, Logger logger) {
		super(Journal.class, authenticatedUser, messages, logger,
				"views.journals.administration_details", //$NON-NLS-1$
				"views.journals.administration.validated_organization"); //$NON-NLS-1$
		this.context = context;
	}

	@Override
	public Journal getEditedEntity() {
		return this.context.getJournal();
	}

	@Override
	protected void createEditorContent(VerticalLayout rootContainer, boolean isAdmin) {
		createDescriptionDetails(rootContainer);
		createRankingDetails(rootContainer);
		createPublisherDetails(rootContainer);
		if (isAdmin) {
			createAdministrationComponents(rootContainer,
					null,
					it -> it.bind(Journal::isValidated, Journal::setValidated));
		}
	}

	/** Create the section for editing the description of the journal.
	 *
	 * @param rootContainer the container.
	 */
	protected void createDescriptionDetails(VerticalLayout rootContainer) {
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

		this.descriptionDetails = new DetailsWithErrorMark(content);
		this.descriptionDetails.setOpened(false);
		rootContainer.add(this.descriptionDetails);

		final var invalidUrl = getTranslation("views.urls.invalid_format"); //$NON-NLS-1$

		getEntityDataBinder().forField(this.name)
			.withConverter(new StringTrimer())
			.withValidator(new NotEmptyStringValidator(getTranslation("views.journals.name.error"))) //$NON-NLS-1$
			.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.name, this.descriptionDetails))
			.bind(Journal::getJournalName, Journal::setJournalName);
		getEntityDataBinder().forField(this.openAccess)
			.bind(Journal::getOpenAccess, Journal::setOpenAccess);
		getEntityDataBinder().forField(this.journalUrl)
			.withConverter(new StringTrimer())
			.withValidator(new UrlValidator(invalidUrl, true))
			.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.journalUrl, this.descriptionDetails))
			.bind(Journal::getJournalURL, Journal::setJournalURL);
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
	protected void createPublisherDetails(VerticalLayout rootContainer) {
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

		this.publisherDetails = new DetailsWithErrorMark(content);
		this.publisherDetails.setOpened(false);
		rootContainer.add(this.publisherDetails);

		getEntityDataBinder().forField(this.publisherName)
			.withConverter(new StringTrimer())
			.withValidator(new NotEmptyStringValidator(getTranslation("views.journals.publisher_name.error"))) //$NON-NLS-1$
			.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.publisherName, this.publisherDetails))
			.bind(Journal::getPublisher, Journal::setPublisher);
		getEntityDataBinder().forField(this.publisherAddress)
			.withConverter(new StringTrimer())
			.bind(Journal::getAddress, Journal::setAddress);
		getEntityDataBinder().forField(this.issn)
			.withConverter(new StringTrimer())
			.withValidator(new IssnValidator(getTranslation("views.journals.issn.error"), true)) //$NON-NLS-1$
			.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.issn, this.publisherDetails))
			.bind(Journal::getISSN, Journal::setISSN);
		getEntityDataBinder().forField(this.isbn)
			.withConverter(new StringTrimer())
			.withValidator(new IsbnValidator(getTranslation("views.journals.isbn.error"), true)) //$NON-NLS-1$
			.withValidationStatusHandler(new DetailsWithErrorMarkStatusHandler(this.isbn, this.publisherDetails))
			.bind(Journal::getISBN, Journal::setISBN);
	}

	/** Create the section for editing the ranking information of the journal.
	 *
	 * @param rootContainer the container.
	 */
	protected void createRankingDetails(VerticalLayout rootContainer) {
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

		this.rankingDetails = new Details("", content); //$NON-NLS-1$
		this.rankingDetails.setOpened(false);
		rootContainer.add(this.rankingDetails);

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
	}

	@Override
	protected void doSave() throws Exception {
		this.context.save();
	}

	@Override
	public void notifySaved() {
		notifySaved(getTranslation("views.journals.save_success", //$NON-NLS-1$
				getEditedEntity().getJournalName()));
	}

	@Override
	public void notifyValidated() {
		notifyValidated(getTranslation("views.journals.validation_success", //$NON-NLS-1$
				getEditedEntity().getJournalName()));
	}

	@Override
	public void notifySavingError(Throwable error) {
		notifySavingError(error, getTranslation("views.journals.save_error", //$NON-NLS-1$ 
				getEditedEntity().getJournalName(), error.getLocalizedMessage()));
	}

	@Override
	public void notifyValidationError(Throwable error) {
		notifyValidationError(error, getTranslation("views.journals.validation_error", //$NON-NLS-1$ 
				getEditedEntity().getJournalName(), error.getLocalizedMessage()));
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		super.localeChange(event);

		this.descriptionDetails.setSummaryText(getTranslation("views.journals.description_informations")); //$NON-NLS-1$
		this.name.setLabel(getTranslation("views.journals.name")); //$NON-NLS-1$
		this.openAccess.setLabel(getTranslation("views.journals.open_access")); //$NON-NLS-1$
		// Force the refreshing of the radio button items
		setOpenAccessRenderer();
		this.journalUrl.setLabel(getTranslation("views.journals.url")); //$NON-NLS-1$

		this.rankingDetails.setSummaryText(getTranslation("views.journals.ranking_informations")); //$NON-NLS-1$
		this.wosId.setLabel(getTranslation("views.journals.wos.id")); //$NON-NLS-1$
		this.wosId.setHelperText(getTranslation("views.journals.wos.id.help")); //$NON-NLS-1$
		this.wosCategory.setLabel(getTranslation("views.journals.wos.category")); //$NON-NLS-1$
		this.wosCategory.setHelperText(getTranslation("views.journals.wos.category.help")); //$NON-NLS-1$
		this.scimagoId.setLabel(getTranslation("views.journals.scimago.id")); //$NON-NLS-1$
		this.scimagoId.setHelperText(getTranslation("views.journals.scimago.id.help")); //$NON-NLS-1$
		this.scimagoCategory.setLabel(getTranslation("views.journals.scimago.category")); //$NON-NLS-1$
		this.scimagoCategory.setHelperText(getTranslation("views.journals.scimago.category.help")); //$NON-NLS-1$

		this.publisherDetails.setSummaryText(getTranslation("views.journals.publisher_informations")); //$NON-NLS-1$
		this.publisherName.setLabel(getTranslation("views.journals.publisher_name")); //$NON-NLS-1$
		this.publisherName.setHelperText(getTranslation("views.journals.publisher_name.help")); //$NON-NLS-1$
		this.publisherAddress.setLabel(getTranslation("views.journals.publisher_address")); //$NON-NLS-1$
		this.issn.setLabel(getTranslation("views.journals.issn")); //$NON-NLS-1$
		this.isbn.setLabel(getTranslation("views.journals.isbn")); //$NON-NLS-1$
	}

}
