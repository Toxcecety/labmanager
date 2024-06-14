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

package fr.utbm.ciad.labmanager.views.components.publications;

import com.google.common.base.Strings;
import com.helger.commons.io.stream.StringInputStream;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.Grid.Column;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.receivers.MultiFileMemoryBuffer;
import com.vaadin.flow.data.provider.CallbackDataProvider.FetchCallback;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.data.renderer.ComponentRenderer;
import com.vaadin.flow.function.SerializableBiConsumer;
import com.vaadin.flow.i18n.LocaleChangeEvent;
import com.vaadin.flow.spring.data.VaadinSpringDataHelpers;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityDeletingContext;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.bibtex.BibTeXConstants;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.io.od.OpenDocumentConstants;
import fr.utbm.ciad.labmanager.utils.io.ris.RISConstants;
import fr.utbm.ciad.labmanager.views.ViewConstants;
import fr.utbm.ciad.labmanager.views.components.addons.ComponentFactory;
import fr.utbm.ciad.labmanager.views.components.addons.SimilarityError;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeRenderer;
import fr.utbm.ciad.labmanager.views.components.addons.badges.BadgeState;
import fr.utbm.ciad.labmanager.views.components.addons.download.DownloadExtension;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityEditor;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractEntityListView;
import fr.utbm.ciad.labmanager.views.components.addons.wizard.AbstractLabManagerWizard;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.arakhne.afc.progress.Progression;
import org.hibernate.Hibernate;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.vaadin.lineawesome.LineAwesomeIcon;

import java.io.*;
import java.nio.charset.Charset;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/** Abstract implementation of a list all the publications whatever the type of publication.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public abstract class AbstractPublicationListView extends AbstractEntityListView<Publication> {
	
	private static final long serialVersionUID = 6591553988384909013L;

	private static final String BIBTEX_FILENAME = "publication.bib"; //$NON-NLS-1$

	private static final String RIS_FILENAME = "publication.ris"; //$NON-NLS-1$

	private static final String ODT_FILENAME = "publication.odt"; //$NON-NLS-1$

	private static final String HTML_FILENAME = "publication.html"; //$NON-NLS-1$

	private static final String JSON_FILENAME = "publication.json"; //$NON-NLS-1$

	private static final String EMPTY = ""; //$NON-NLS-1$

	private final String authorsColumnLabelKey;

	private final String personCreationLabelKey;

	private final String personFieldLabelKey;

	private final String personFieldHelperLabelKey;

	private final String personNullErrorKey;

	private final String personDuplicateErrorKey;

	private final DownloadableFileManager fileManager;

	private final JournalService journalService;

	private final ConferenceService conferenceService;

	private final ScientificAxisService axisService;

	private final PersonService personService;

	private final UserService userService;
	
	private PublicationDataProvider dataProvider;

	private PublicationService publicationService;

	private Column<Publication> titleColumn;

	private Column<Publication> authorsColumn;

	private Column<Publication> publishingColumn;

	private Column<Publication> typeColumn;

	private Column<Publication> yearColumn;

	private Column<Publication> fileColumn;

	private Column<Publication> validationColumn;

	private MenuItem exportButton;

	private MenuItem exportBibTeXButton;

	private MenuItem exportRisButton;

	private MenuItem exportOdtButton;

	private MenuItem exportHtmlButton;

	private MenuItem exportHalButton;

	private MenuItem exportJsonButton;

	private MenuItem importButton;

	private MenuItem importBibTexButton;

	private MenuItem importRisButton;

	private MenuItem importJsonButton;

	private Dialog importDialog;

	private MenuItem regenerateThumbnailButton;

	/** Constructor.
	 *
	 * @param fileManager the manager of the filenames for the uploaded files.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (spring layer).
	 * @param publicationService the service for accessing the publications.
	 * @param personService the service for accessing the JPA entities for persons.
	 * @param userService the service for accessing the JPA entities for users.
	 * @param journalService the service for accessing the JPA entities for journal.
	 * @param conferenceService the service for accessing the JPA entities for conferences.
	 * @param axisService the service for accessing the JPA entities for scientific axes.
	 * @param logger the logger to use.
	 * @param deletionTitleMessageKey the key in the localized messages for the dialog box title related to a deletion.
	 * @param deletionMessageKey the key in the localized messages for the message related to a deletion.
	 * @param deletionSuccessMessageKey the key in the localized messages for the messages related to a deletion success.
	 * @param deletionErrorMessageKey the key in the localized messages for the messages related to an error of deletion.
	 * @param authorsColumnLabelKey the key in the localized messages for the messages related to the label of the authors column.
	 * @param personCreationLabelKey the key that is used for retrieving the text for creating a new person and associating it to the publication.
	 * @param personFieldLabelKey the key that is used for retrieving the text for the label of the author/editor field.
	 * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
	 * @param personNullErrorKey the key that is used for retrieving the text of the author/editor null error.
	 * @param personDuplicateErrorKey the key that is used for retrieving the text of the author/editor duplicate error.
	 */
	public AbstractPublicationListView(
			DownloadableFileManager fileManager,
			AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
			PublicationService publicationService, PersonService personService, UserService userService,
			JournalService journalService, ConferenceService conferenceService, ScientificAxisService axisService, Logger logger,
			String deletionTitleMessageKey, String deletionMessageKey,
			String deletionSuccessMessageKey, String deletionErrorMessageKey,
			String authorsColumnLabelKey, String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
			String personNullErrorKey, String personDuplicateErrorKey) {
		super(Publication.class, authenticatedUser, messages, logger,
				deletionTitleMessageKey, deletionMessageKey, deletionSuccessMessageKey, deletionErrorMessageKey);
		this.fileManager = fileManager;
		this.authorsColumnLabelKey = authorsColumnLabelKey;
		this.personCreationLabelKey = personCreationLabelKey;
		this.personFieldLabelKey = personFieldLabelKey;
		this.personFieldHelperLabelKey = personFieldHelperLabelKey;
		this.personNullErrorKey = personNullErrorKey;
		this.personDuplicateErrorKey = personDuplicateErrorKey;
		this.publicationService = publicationService;
		this.personService = personService;
		this.userService = userService;
		this.journalService = journalService;
		this.conferenceService = conferenceService;
		this.axisService = axisService;
	}

	/** Change the data provider;.
	 *
	 * @param dataProvider the data provider.
	 */
	protected void setDataProvider(PublicationDataProvider dataProvider) {
		this.dataProvider = dataProvider;
	}

	@Override
	protected void createHoverMenuBar(Publication entity, MenuBar menuBar) {
		super.createHoverMenuBar(entity, menuBar);

		final var bibTexImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_BIBTEX_BLACK_ICON);
		final var bibtexExporter = ComponentFactory.addIconItem(menuBar, bibTexImageResource, null, getTranslation("views.publication.export.bibtex"), null); //$NON-NLS-1$
		bindBibTeXExporter(bibtexExporter, entity);

		final var odtImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_ODT_BLACK_ICON);
		final var odtExporter = ComponentFactory.addIconItem(menuBar, odtImageResource, null, getTranslation("views.publication.export.odt"), null); //$NON-NLS-1$
		bindBibTeXExporter(odtExporter, entity);
	}

	/** Notify the user that the an error was encountered during exporting action.
	 *
	 * @param error the error.
	 */
	protected void notifyExportError(Throwable error) {
		final var ui = getUI().orElseThrow();
		if (ui != null) {
			ui.access(() -> {
				final var message = getTranslation("views.publication.export.error", error.getLocalizedMessage()); //$NON-NLS-1$
				getLogger().error(message, error);
				ComponentFactory.showErrorNotification(message);
			});
		}
	}

	@Override
	protected MenuBar createMenuBar() {
		var menu = super.createMenuBar();
		if (menu == null) {
			menu = new MenuBar(); 
			menu.addThemeVariants(MenuBarVariant.LUMO_ICON);
		}
		
		this.exportButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.SAVE_SOLID, null, null, null);
		this.exportButton.setEnabled(false);
		final var exportSubMenu = this.exportButton.getSubMenu();
		
		final var bibTexImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_BIBTEX_BLACK_ICON);
		this.exportBibTeXButton = ComponentFactory.addIconItem(exportSubMenu, bibTexImageResource, null, null, null);
		bindBibTeXExporter(this.exportBibTeXButton, null);

		final var risImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_RIS_BLACK_ICON);
		this.exportRisButton = ComponentFactory.addIconItem(exportSubMenu, risImageResource, null, null, null);
		bindRISExporter(this.exportRisButton, null);

		final var odtImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_ODT_BLACK_ICON);
		this.exportOdtButton = ComponentFactory.addIconItem(exportSubMenu, odtImageResource, null, null, null);
		bindODTExporter(this.exportOdtButton, null);

		final var htmlImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_HTML_BLACK_ICON);
		this.exportHtmlButton = ComponentFactory.addIconItem(exportSubMenu, htmlImageResource, null, null, null);
		bindHtmlExporter(this.exportHtmlButton, null);

		final var jsonImageResource = ComponentFactory.newStreamImage(ViewConstants.EXPORT_JSON_BLACK_ICON);
		this.exportJsonButton = ComponentFactory.addIconItem(exportSubMenu, jsonImageResource, null, null, null);
		bindJsonExporter(this.exportJsonButton, null);

		final var halImageResource = ComponentFactory.newStreamImage(ViewConstants.HAL_ICON);
		this.exportHalButton = ComponentFactory.addIconItem(exportSubMenu, halImageResource, null, null, null);
		this.exportHalButton.setEnabled(false);


		this.importButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.SAVE_SOLID, null, null, null);
		this.importButton.setEnabled(true);
		final var importSubMenu = this.importButton.getSubMenu();

		this.importBibTexButton = ComponentFactory.addIconItem(importSubMenu, bibTexImageResource, null, null, null);
		this.importBibTexButton.addClickListener(event -> openImportBibtexDialog());
		this.importBibTexButton.setEnabled(true);

		this.importRisButton = ComponentFactory.addIconItem(importSubMenu, risImageResource, null, null, null);
		this.importRisButton.addClickListener(event -> openImportRisDialog());
		this.importRisButton.setEnabled(true);

		this.importJsonButton = ComponentFactory.addIconItem(importSubMenu, jsonImageResource, null, null, null);
		this.importJsonButton.setEnabled(false);


		this.regenerateThumbnailButton = ComponentFactory.addIconItem(menu, LineAwesomeIcon.SYNC_ALT_SOLID, null, null, it -> openThumbnailRegenerationWizard());

		return menu;
	}

	/** Open the wizard for regenerating the publication thumbnails.
	 */
	protected void openThumbnailRegenerationWizard() {
		final var selection = getGrid().getSelectedItems();
		if (selection != null && !selection.isEmpty()) {
			final var identifiers = AbstractLabManagerWizard.buildQueryParameters(selection);
			getUI().ifPresent(ui -> ui.navigate(ThumbnailGeneratorWizard.class, identifiers));
		} else {
			getUI().ifPresent(ui -> ui.navigate(ThumbnailGeneratorWizard.class));
		}
	}

	/** Replies the default configurator for exporters.
	 *
	 * @return the configurator.
	 */
	protected ExporterConfigurator createExportConfigurator() {
		return new ExporterConfigurator(this.journalService, Locale.US);
	}

	/** Extend the given item with the exporter for BibTeX.
	 * 
	 * @param item the component to be extended.
	 * @param entity the entity to export or {@code null} if the current grid selection must be used.
	 */
	protected void bindBibTeXExporter(MenuItem item, Publication entity) {
		final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_BIBTEX_BLACK_ICON);
        DownloadExtension.extend(item)
	    	.withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
	    	.withProgressTitle(getTranslation("views.publication.export.bibtex")) //$NON-NLS-1$
        	.withFilename(() -> BIBTEX_FILENAME)
        	.withMimeType(() -> BibTeXConstants.MIME_TYPE_UTF8_VALUE)
	    	.withFailureListener(this::notifyExportError)
        	.withInputStreamFactory(progress -> exportBibTeX(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
	}
	
	/** Export the given publications in a BibTeX file.
	 *
	 * @param publications the publications to export.
	 * @param progression the progression indicator to be used.
	 * @return the input stream that contains the BibTeX data.
	 */
	protected InputStream exportBibTeX(Set<Publication> publications, Progression progression) {
		if (publications == null || publications.isEmpty()) {
			progression.end();
			notifyNotEntity();
			return null; 
		}
		// Force the loading of all the information about each publication
		this.publicationService.loadPublicationsInMemory(publications);
		final var configuration = createExportConfigurator();
		final var content = this.publicationService.exportBibTeX(publications, configuration, progression);
		return new StringInputStream(content, Charset.defaultCharset());
	}

	/** Extend the given item with the exporter for RIS.
	 * 
	 * @param item the component to be extended.
	 * @param entity the entity to export or {@code null} if the current grid selection must be used.
	 */
	protected void bindRISExporter(MenuItem item, Publication entity) {
		final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_RIS_BLACK_ICON);
        DownloadExtension.extend(item)
	    	.withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
	    	.withProgressTitle(getTranslation("views.publication.export.ris")) //$NON-NLS-1$
        	.withFilename(() -> RIS_FILENAME)
        	.withMimeType(() -> RISConstants.MIME_TYPE_UTF8_VALUE)
	    	.withFailureListener(this::notifyExportError)
        	.withInputStreamFactory(progress -> exportRIS(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
	}

	/** Export the given publications in a RIS file.
	 *
	 * @param publications the publications to export.
	 * @param progression the progression indicator to be used.
	 * @return the input stream that contains the RIS data.
	 */
	protected InputStream exportRIS(Set<Publication> publications, Progression progression) {
		if (publications == null || publications.isEmpty()) {
			progression.end();
			notifyNotEntity();
			return null; 
		}
		// Force the loading of all the information about each publication
		this.publicationService.loadPublicationsInMemory(publications);
		final var configuration = createExportConfigurator();
		final var content = this.publicationService.exportRIS(publications, configuration, progression);
		return new StringInputStream(content, Charset.defaultCharset());
	}

	/** Extend the given item with the exporter for ODT.
	 * 
	 * @param item the component to be extended.
	 * @param entity the entity to export or {@code null} if the current grid selection must be used.
	 */
	protected void bindODTExporter(MenuItem item, Publication entity) {
		final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_ODT_BLACK_ICON);
        DownloadExtension.extend(item)
	    	.withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
	    	.withProgressTitle(getTranslation("views.publication.export.odt")) //$NON-NLS-1$
        	.withFilename(() -> ODT_FILENAME)
        	.withMimeType(() -> OpenDocumentConstants.ODT_MIME_TYPE_VALUE)
	    	.withFailureListener(this::notifyExportError)
        	.withInputStreamFactory(progress -> exportODT(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
	}
	
	/** Export the given publications in a ODT file.
	 *
	 * @param publications the publications to export.
	 * @param progression the progression indicator to be used.
	 * @return the input stream that contains the ODT data.
	 * @throws Exception if the ODT data cannot be generated.
	 */
	protected InputStream exportODT(Set<Publication> publications, Progression progression) throws Exception {
		if (publications == null || publications.isEmpty()) {
			progression.end();
			notifyNotEntity();
			return null; 
		}
		// Force the loading of all the information about each publication
		this.publicationService.loadPublicationsInMemory(publications);
		final var configuration = createExportConfigurator();
		final var content = this.publicationService.exportOdt(publications, configuration, progression);
		return new ByteArrayInputStream(content);
	}

	/** Extend the given item with the exporter for HTML.
	 * 
	 * @param item the component to be extended.
	 * @param entity the entity to export or {@code null} if the current grid selection must be used.
	 */
	protected void bindHtmlExporter(MenuItem item, Publication entity) {
		final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_HTML_BLACK_ICON);
        DownloadExtension.extend(item)
	    	.withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
	    	.withProgressTitle(getTranslation("views.publication.export.html")) //$NON-NLS-1$
        	.withFilename(() -> HTML_FILENAME)
        	.withMimeType(() -> "text/html") //$NON-NLS-1$
	    	.withFailureListener(this::notifyExportError)
        	.withInputStreamFactory(progress -> exportHTML(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
	}

	/** Export the given publications in an HTML file.
	 *
	 * @param publications the publications to export.
	 * @param progression the progression indicator to be used.
	 * @return the input stream that contains the HTML data.
	 * @throws Exception if the HTML data cannot be generated.
	 */
	protected InputStream exportHTML(Set<Publication> publications, Progression progression) throws Exception {
		if (publications == null || publications.isEmpty()) {
			progression.end();
			notifyNotEntity();
			return null; 
		}
		// Force the loading of all the information about each publication
		this.publicationService.loadPublicationsInMemory(publications);
		final var configuration = createExportConfigurator();
		final var content = this.publicationService.exportHtml(publications, configuration, progression);
		return new StringInputStream(content, Charset.defaultCharset());
	}

	/** Extend the given item with the exporter for JSON.
	 * 
	 * @param item the component to be extended.
	 * @param entity the entity to export or {@code null} if the current grid selection must be used.
	 */
	protected void bindJsonExporter(MenuItem item, Publication entity) {
		final var icon = ComponentFactory.newStreamImage(ViewConstants.EXPORT_JSON_BLACK_ICON);
        DownloadExtension.extend(item)
	    	.withProgressIcon(new Image(icon, "")) //$NON-NLS-1$
	    	.withProgressTitle(getTranslation("views.publication.export.json")) //$NON-NLS-1$
        	.withFilename(() -> JSON_FILENAME)
        	.withMimeType(() -> Constants.JSON_MIME)
	    	.withFailureListener(this::notifyExportError)
        	.withInputStreamFactory(progress -> exportJSON(entity == null ? getGrid().getSelectedItems() : Collections.singleton(entity), progress));
	}

	/** Export the given publications in a JSON file.
	 *
	 * @param publications the publications to export.
	 * @param progression the progression indicator to be used.
	 * @return the input stream that contains the JSON data.
	 * @throws Exception if the JSON data cannot be generated.
	 */
	protected InputStream exportJSON(Set<Publication> publications, Progression progression) throws Exception {
		if (publications == null || publications.isEmpty()) {
			notifyNotEntity();
			return null; 
		}
		// Force the loading of all the information about each publication
		this.publicationService.loadPublicationsInMemory(publications);
		final var configuration = createExportConfigurator();
		final var content = this.publicationService.exportJson(publications, configuration, progression);
		return new StringInputStream(content, Charset.defaultCharset());
	}

	/**
	 * Sets up and displays the import dialog for importing publications.
	 * This method configures the dialog with a title and an import function that defines how the publications are imported.
	 * It creates an upload component for users to upload files, which are then processed by the provided import function.
	 * The dialog also includes "Cancel" and "Import" buttons for user interactions.
	 *
	 * @param dialogTitle    The title of the import dialog, displayed at the top of the dialog.
	 * @param importFunction A function that takes a {@link Reader} and returns a list of {@link Publication} objects.
	 *                       This function defines how the uploaded files are read and processed into publications.
	 */
	protected void setupImportDialog(String dialogTitle, Function<Reader, List<Publication>> importFunction) {
		Upload upload = getUploadDialog(importFunction);

		this.importDialog = new Dialog();
		this.importDialog.add(upload);
		this.importDialog.setHeaderTitle(dialogTitle);
		Button cancelButton = new Button(getTranslation("views.cancel"), e -> {
			upload.clearFileList();
			this.importDialog.close();
		});
		Button importButton = new Button(getTranslation("views.import"), e -> upload.getElement().callJsFunction("uploadFiles"));
		this.importDialog.getFooter().add(cancelButton, importButton);
		this.importDialog.setCloseOnOutsideClick(false);
		this.importDialog.setCloseOnEsc(false);
		this.importDialog.open();
	}

	/**
	 * Creates and configures an upload component for the import dialog.
	 * This method initializes the upload component with a {@link MultiFileMemoryBuffer} and configures it
	 * to not allow auto-uploading of files. It also sets up a listener to process the uploaded files
	 * using the provided import function once all files have been uploaded.
	 *
	 * @param importFunction A function that takes a {@link Reader} and returns a list of {@link Publication} objects.
	 *                       This function is used to read and process the uploaded files into publications.
	 * @return The configured {@link Upload} component ready to be added to the import dialog.
	 */
	private @NotNull Upload getUploadDialog(Function<Reader, List<Publication>> importFunction) {
		MultiFileMemoryBuffer buffer = new MultiFileMemoryBuffer();
		Upload upload = new Upload(buffer);
		upload.setDropAllowed(true);
		upload.setAutoUpload(false);

		upload.addAllFinishedListener(
				event -> {
					List<Publication> publications = processFiles(buffer, importFunction);

					Dialog gridImportDialog = new Dialog();
					gridImportDialog.setWidthFull();
					gridImportDialog.setHeight("auto");
					gridImportDialog.setHeaderTitle(getTranslation("views.import.dialog.title"));

					Grid<AbstractEntityEditor<Publication>> publicationGrid = new Grid<>();

					// Adding columns
					// Title column
					publicationGrid
							.addColumn(editor -> editor
									.getEditedEntity()
									.getTitle())
							.setHeader(getTranslation("views.import.grid.column.title"));
					// Authors column
					publicationGrid
							.addColumn(publication -> publication
									.getEditedEntity()
									.getAuthors()
									.stream()
									.map(Person::getFullName)
									.collect(Collectors.joining(", ")))
							.setHeader(getTranslation("views.import.grid.column.authors"));
					// Type column
					publicationGrid
							.addColumn(publication -> publication
									.getEditedEntity()
									.getType()
									.getCategory(false))
							.setHeader(getTranslation("views.import.grid.column.category"))
							.setAutoWidth(true)
							.setFlexGrow(0);
					// Status column
					publicationGrid
							.addColumn(createStatusComponentRenderer())
							.setHeader(getTranslation("views.import.grid.column.checked"))
							.setAutoWidth(true)
							.setFlexGrow(0);
					// Edition button column
					publicationGrid
							.addComponentColumn(editor -> {
								Button editButton = new Button(getTranslation("views.import.grid.edition.edit"));
								editButton.addClickListener(e -> {
									this.addEntity(
											editor,
											editor.getEditedEntity().getTitle(),
											false,
											(dialog, entity) ->
											{
												editor.getEditedEntity().setValidated(true);
												publicationGrid.getDataProvider().refreshAll();
											}
									);
								});
								return editButton;
							})
							.setHeader(getTranslation("views.import.grid.column.edit"))
							.setAutoWidth(true)
							.setFlexGrow(0);

					List<AbstractEntityEditor<Publication>> editors = publications.stream()
							.map(this::createPublicationEditor)
							.toList();

					publicationGrid.setItems(editors);

					Button closeButton = new Button(getTranslation("views.import.dialog.close"), e -> gridImportDialog.close());
					Button saveAllButton = new Button(getTranslation("views.import.dialog.save"), e -> {
						publicationGrid.getListDataView().getItems().forEach(editor -> {
							try {
								if (editor.getEditedEntity().isValidated()) {
									editor.getEditedEntity().setValidated(false);
									editor.save();
								}
							} catch (Exception ex) {
								getLogger().error("Error while saving publication", ex);
							}
						});
						gridImportDialog.close();
						refreshGrid();
					});
					gridImportDialog.add(publicationGrid);
					gridImportDialog.getFooter().add(closeButton, saveAllButton);

					// Open the dialog
					gridImportDialog.open();

					this.importDialog.close();
				});
		return upload;
	}

	/**
	 * List all the publications contained in the given file.
	 *
	 * @param buffer         the buffer that contains the file.
	 * @param importFunction the function that is used to import the publications.
	 * @return the list of publications.
	 */
	private List<Publication> processFiles(MultiFileMemoryBuffer buffer, Function<Reader, List<Publication>> importFunction) {
		List<Publication> publications = new ArrayList<>(Collections.emptyList());
		buffer.getFiles().forEach(file -> {
			try (InputStream fileData = buffer.getInputStream(file);
				 Reader reader = new BufferedReader(new InputStreamReader(fileData))) {

				// Read the publications from the file
				publications.addAll(importFunction.apply(reader));

			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
		return publications;
	}

	/**
	 * This is a SerializableBiConsumer functional interface implementation that updates the status of a publication.
	 * It takes a Span and an AbstractEntityEditor of Publication as inputs.
	 * The status of the publication is determined based on whether it is validated or not and whether it has similarity errors or warnings.
	 * The theme and text of the span are updated accordingly.
	 */
	private static final SerializableBiConsumer<Span, AbstractEntityEditor<Publication>> statusComponentUpdater = (Span span, AbstractEntityEditor<Publication> Editor) -> {
		SimilarityError error = Editor.isAlreadyInDatabase();
		String theme;
		String text;
		if (Editor.getEditedEntity().isValidated()) {
			theme = "badge success";
			text = span.getTranslation("views.import.grid.checked.checked");
		} else {
			if (error.isSimilarityError() || error.isSimilarityWarning()) {
				theme = "badge error";
				text = span.getTranslation("views.import.grid.checked.not_checked");
			} else {
				Editor.getEditedEntity().setValidated(true);
				theme = "badge success";
				text = span.getTranslation("views.import.grid.checked.checked");
			}
		}
		span.getElement().setAttribute("theme", theme);
		span.setText(text);
	};

	/**
	 * This method creates a ComponentRenderer for a Span and an AbstractEntityEditor of Publication.
	 * It uses the statusComponentUpdater defined above to update the status of the publication.
	 * The ComponentRenderer is used to render the status of the publication in the UI.
	 *
	 * @return A ComponentRenderer that can be used to render the status of a publication.
	 */
	private static ComponentRenderer<Span, AbstractEntityEditor<Publication>> createStatusComponentRenderer() {
		return new ComponentRenderer<>(Span::new, statusComponentUpdater);
	}

	/**
	 * Opens the import dialog for BibTeX files.
	 * This method sets up the import dialog specifically for importing BibTeX files by providing
	 * a title and an import function tailored for BibTeX. The import function uses the publication service
	 * to read publications from BibTeX format.
	 */
	protected void openImportBibtexDialog() {
		setupImportDialog(getTranslation("views.import.bibtex"), reader -> {
			try {
				return publicationService.readPublicationsFromBibTeX(reader, true, false, true, true, true);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}

	/**
	 * Opens the import dialog for RIS files.
	 * Similar to {@link #openImportBibtexDialog()}, but tailored for importing RIS files.
	 * The import function provided to the setup method uses the publication service to read publications from RIS format.
	 */
	protected void openImportRisDialog() {
		setupImportDialog(getTranslation("views.import.ris"), reader -> {
			try {
				return publicationService.readPublicationsFromRIS(reader, true, false, true, true, true, null);
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		});
	}


	@Override
	protected void onSelectionChange(Set<?> selection) {
		super.onSelectionChange(selection);
		final int size = selection.size();
		this.exportButton.setEnabled(size >= 1);
	}

	/** Replies the types of publications that are supported by this view.
	 *
	 * @return the types of supported publications.
	 */
	protected abstract Stream<PublicationType> getSupportedPublicationTypes(); 

	/** Replies the types of publications that are supported by this view.
	 *
	 * @return the types of supported publications.
	 */
	protected final PublicationType[] getSupportedPublicationTypeArray() {
		return getSupportedPublicationTypes().toArray(size -> new PublicationType[size]);
	}

	/** Create filters that correspond to the supported types.
	 *
	 * @param filters the additional filters.
	 * @return the full filters.
	 * @see #getSupportedPublicationTypes()
	 */
	protected Specification<Publication> createJpaFilters(Specification<Publication> filters) {
		final var filters0 = new PublicationTypeSpecification();
		final var filters1 = filters == null ? filters0 : filters0.and(filters);
		return filters1;
	}

	@Override
	protected AbstractFilters<Publication> createFilters() {
		return new PublicationFilters(getAuthenticatedUser(), this::refreshGrid);
	}

	/** Initialize the given JPA entity for being displayed in the list.
	 *
	 * @param entity the entity.
	 */
	@SuppressWarnings("static-method")
	protected void initializeEntityFromJPA(Publication entity) {
		// Force the loaded of the lazy data that is needed for rendering the table
		Hibernate.initialize(entity.getAuthorships());
		// Force the loading of all the data that is required for obtaining the place where the publication was published
		entity.getWherePublishedShortDescription();
	}

	@Override
	protected Grid<Publication> createGridInstance() {
		final var grid = super.createGridInstance();
		grid.addThemeVariants(GridVariant.LUMO_WRAP_CELL_CONTENT);
		return grid;
	}

	@Override
	protected boolean createGridColumns(Grid<Publication> grid) {
		this.titleColumn = grid.addColumn(it -> it.getTitle())
				.setAutoWidth(false)
				.setSortProperty("title"); //$NON-NLS-1$
		this.authorsColumn = grid.addColumn(new ComponentRenderer<>(this::getAuthorsComponent))
				.setAutoWidth(false);
		this.publishingColumn = grid.addColumn(new ComponentRenderer<>(this::getPublishingComponent))
				.setAutoWidth(false);
		this.typeColumn = grid.addColumn(new ComponentRenderer<>(this::getPublicationTypeComponent))
				.setAutoWidth(false)
				.setSortProperty("type"); //$NON-NLS-1$
		this.yearColumn = grid.addColumn(it -> Integer.toString(it.getPublicationYear()))
				.setAutoWidth(false)
				.setSortProperty("publicationYear"); //$NON-NLS-1$
		this.fileColumn = grid.addColumn(new BadgeRenderer<>((data, callback) -> {
			final var filename = data.getPathToDownloadablePDF();
			if (Strings.isNullOrEmpty(filename)) {
				callback.create(BadgeState.ERROR, getTranslation("views.publication.no_pdf"), null); //$NON-NLS-1$
			} else {
				callback.create(BadgeState.SUCCESS, getTranslation("views.publication.pdf"), filename); //$NON-NLS-1$
			}
		}))
				.setAutoWidth(false);
		this.validationColumn = grid.addColumn(new BadgeRenderer<>((data, callback) -> {
			if (data.isValidated()) {
				callback.create(BadgeState.SUCCESS, null, getTranslation("views.validated")); //$NON-NLS-1$
			} else {
				callback.create(BadgeState.ERROR, null, getTranslation("views.validable")); //$NON-NLS-1$
			}
		}))
				.setAutoWidth(true)
				.setFlexGrow(0)
				.setSortProperty("validated") //$NON-NLS-1$
				.setWidth("0%"); //$NON-NLS-1$
		// Create the hover tool bar only if administrator role
		return isAdminRole();
	}

	/** Replies the authors for the given publication.
	 *
	 * @param publication the publication to read.
	 * @return the authors.
	 */
	protected Component getAuthorsComponent(Publication publication) {
		final var authors = publication.getAuthors();
		final var list = new StringBuilder();
		for (final var author : authors) {
			if (author != null) {
				if (list.length() > 0) {
					list.append(", "); //$NON-NLS-1$
				}
				list.append(author.getFullName());
			}
		}
		return new Span(list.toString());
	}

	/** Replies the publishing details for the given publication.
	 *
	 * @param publication the publication to read.
	 * @return the publishing details.
	 */
	protected Component getPublishingComponent(Publication publication) {
		final var details = publication.getWherePublishedShortDescription();
		return new Span(details);
	}

	/** Replies the type's label for the given publication.
	 *
	 * @param publication the publication to read.
	 * @return the type's label.
	 */
	protected Component getPublicationTypeComponent(Publication publication) {
		final var type = publication.getType();
		final var label = type.getLabel(getMessageSourceAccessor(), getLocale());
		return new Span(label);
	}

	@Override
	protected List<Column<Publication>> getInitialSortingColumns() {
		return Arrays.asList(this.yearColumn, this.typeColumn, this.titleColumn);
	}

	@Override
	protected SortDirection getInitialSortingDirection(Column<Publication> column) {
		if (column == this.yearColumn) {
			return SortDirection.DESCENDING;
		}
		return SortDirection.ASCENDING;
	}

	@Override
	protected FetchCallback<Publication, Void> getFetchCallback(AbstractFilters<Publication> filters) {
		return query -> {
			return this.dataProvider.fetch(
					this.publicationService,
					VaadinSpringDataHelpers.toSpringPageRequest(query),
					filters).stream();
		};
	}
	
	@Override
	protected void addEntity() {
		final var now = LocalDate.now();
		final var emptyPublication = this.publicationService.getPrePublicationFactory().createPrePublication(
				getSupportedPublicationTypes().findFirst().get(),
				EMPTY, EMPTY, EMPTY, now, now.getYear(), EMPTY, EMPTY,
				EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, EMPTY, PublicationLanguage.getDefault());
		openPublicationWizardEditor(emptyPublication, getTranslation("views.publication.add_publication")); //$NON-NLS-1$
	}

    protected void addEntity(Publication entity,String fileName, int index, int size) {
        openPublicationEditor(entity, getTranslation("views.publication.import_publication", index, size, fileName)); //$NON-NLS-1$
    }

    protected void addEntity(Publication entity, String fileName, boolean saveInDatabase, SerializableBiConsumer<Dialog, Publication> refreshAll) {
        openPublicationEditor(entity, getTranslation("views.publication.import_publication", fileName), saveInDatabase, refreshAll); //$NON-NLS-1$
    }

	protected void addEntity(AbstractEntityEditor<Publication> editor, String fileName, boolean saveInDatabase, SerializableBiConsumer<Dialog, Publication> refreshAll) {
		openPublicationEditor(editor, getTranslation("views.publication.import_publication", fileName), saveInDatabase, refreshAll); //$NON-NLS-1$
	}

    @Override
    protected void edit(Publication publication) {
        openPublicationEditor(publication, getTranslation("views.publication.edit_publication", publication.getTitle()), true); //$NON-NLS-1$
    }

	/**
	 * Show the editor of a publication.
	 *
	 * @param publication    the publication to edit.
	 * @param title          the title of the editor.
	 * @param saveInDatabase indicates if the editor should save the publication in the database.
	 */
	protected void openPublicationEditor(Publication publication, String title, boolean saveInDatabase) {
		openPublicationEditor(publication, title, saveInDatabase, (dialog, entity) -> refreshGrid());
	}

	/**
	 * Show the editor of a publication.
	 *
	 * @param publication    the publication to edit.
	 * @param title          the title of the editor.
	 * @param saveInDatabase indicates if the editor should save the publication in the database.
	 * @param refreshAll     the function to call for refreshing all the data.
	 */
	protected void openPublicationEditor(Publication publication, String title, boolean saveInDatabase, SerializableBiConsumer<Dialog, Publication> refreshAll) {
		openPublicationEditor(createPublicationEditor(publication), title, saveInDatabase, refreshAll);
	}

	/**
	 * Show the editor of a publication.
	 *
	 * @param editor         the editor to show.
	 * @param title          the title of the editor.
	 * @param saveInDatabase indicates if the editor should save the publication in the database.
	 * @param refreshAll     the function to call for refreshing all the data.
	 */
	protected void openPublicationEditor(AbstractEntityEditor<Publication> editor, String title, boolean saveInDatabase, SerializableBiConsumer<Dialog, Publication> refreshAll) {
		final var newEntity = editor.isNewEntity();
		ComponentFactory.openEditionModalDialog(title, "views.check", editor, false,
				saveInDatabase,
				// Refresh the "old" item, even if its has been changed in the JPA database
				refreshAll,
				newEntity ? null : refreshAll);
	}

	/**
	 * Create an AbstractEntityEditor for the given publication.
	 *
	 * @param publication the publication to edit.
	 * @return the editor.
	 */
	protected void openPublicationWizardEditor(Publication publication, String title) {
		final var editor = new EmbeddedPublicationEditorWizard(
				this.publicationService.startEditing(publication),
				getSupportedPublicationTypeArray(),
				true,
				this.fileManager,
				this.publicationService,
				this.personService,
				this.userService,
				this.journalService,
				this.conferenceService,
				this.axisService,
				getAuthenticatedUser(),
				getMessageSourceAccessor(),
				this.personCreationLabelKey,
				this.personFieldLabelKey,
				this.personFieldHelperLabelKey,
				this.personNullErrorKey,
				this.personDuplicateErrorKey);
		final var newEntity = editor.isNewEntity();
		final SerializableBiConsumer<Dialog, Publication> refreshAll = (dialog, entity) -> refreshGrid();
		ComponentFactory.openEditionModalDialog(title, editor, false,
				// Refresh the "old" item, even if its has been changed in the JPA database
				refreshAll,
				newEntity ? null : refreshAll);
	}

	protected void openPublicationEditor(Publication publication, String title) {
		var editor =  new EmbeddedPublicationEditor(
				this.publicationService.startEditing(publication),
				getSupportedPublicationTypeArray(),
				true,
				this.fileManager,
				this.publicationService,
				this.personService,
				this.userService,
				this.journalService,
				this.conferenceService,
				this.axisService,
				getAuthenticatedUser(),
				getMessageSourceAccessor(),
				this.personCreationLabelKey,
				this.personFieldLabelKey,
				this.personFieldHelperLabelKey,
				this.personNullErrorKey,
				this.personDuplicateErrorKey);
	}

	@Override
	protected EntityDeletingContext<Publication> createDeletionContextFor(Set<Publication> entities) {
		return this.publicationService.startDeletion(entities);
	}

	@Override
	public void localeChange(LocaleChangeEvent event) {
		super.localeChange(event);
		this.titleColumn.setHeader(getTranslation("views.title")); //$NON-NLS-1$
		this.authorsColumn.setHeader(getTranslation(this.authorsColumnLabelKey));
		this.publishingColumn.setHeader(getTranslation("views.where_published")); //$NON-NLS-1$
		this.typeColumn.setHeader(getTranslation("views.type")); //$NON-NLS-1$
		this.yearColumn.setHeader(getTranslation("views.year")); //$NON-NLS-1$
		this.fileColumn.setHeader(getTranslation("views.files")); //$NON-NLS-1$
		this.validationColumn.setHeader(getTranslation("views.validated")); //$NON-NLS-1$
		if (this.exportButton != null) {
			ComponentFactory.setIconItemText(this.exportButton, getTranslation("views.export")); //$NON-NLS-1$
		}
		if (this.exportBibTeXButton != null) {
			ComponentFactory.setIconItemText(this.exportBibTeXButton, getTranslation("views.publication.export.bibtex")); //$NON-NLS-1$
		}
		if (this.exportRisButton != null) {
			ComponentFactory.setIconItemText(this.exportRisButton, getTranslation("views.publication.export.ris")); //$NON-NLS-1$
		}
		if (this.exportOdtButton != null) {
			ComponentFactory.setIconItemText(this.exportOdtButton, getTranslation("views.publication.export.odt")); //$NON-NLS-1$
		}
		if (this.exportHtmlButton != null) {
			ComponentFactory.setIconItemText(this.exportHtmlButton, getTranslation("views.publication.export.html")); //$NON-NLS-1$
		}
		if (this.exportHalButton != null) {
			ComponentFactory.setIconItemText(this.exportHalButton, getTranslation("views.publication.export.hal")); //$NON-NLS-1$
		}
		if (this.exportJsonButton != null) {
			ComponentFactory.setIconItemText(this.exportJsonButton, getTranslation("views.publication.export.json")); //$NON-NLS-1$
		}
		if (this.importButton != null) {
			ComponentFactory.setIconItemText(this.importButton, getTranslation("views.import")); //$NON-NLS-1$
		}
		if (this.importBibTexButton != null) {
			ComponentFactory.setIconItemText(this.importBibTexButton, getTranslation("views.publication.import.bibtex")); //$NON-NLS-1$
		}
		if (this.importRisButton != null) {
			ComponentFactory.setIconItemText(this.importRisButton, getTranslation("views.publication.import.ris")); //$NON-NLS-1$
		}
		if (this.importJsonButton != null) {
			ComponentFactory.setIconItemText(this.importJsonButton, getTranslation("views.publication.import.json")); //$NON-NLS-1$
		}
		if (this.regenerateThumbnailButton != null) {
			ComponentFactory.setIconItemText(this.regenerateThumbnailButton, getTranslation("views.publications.thumbnailGenerator")); //$NON-NLS-1$
		}
		refreshGrid();
	}

	/** UI and JPA filters for {@link AbstractPublicationListView}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected static class PublicationFilters extends AbstractAuthenticatedUserDataFilters<Publication> {

		private static final long serialVersionUID = -9105749553511198435L;

		private Checkbox includeTitles;

		private Checkbox includeYears;

		/** Constructor.
		 *
		 * @param user the connected user, or {@code null} if the filter does not care about a connected user.
		 * @param onSearch the callback function for running the filtering.
		 */
		public PublicationFilters(AuthenticatedUser user, Runnable onSearch) {
			super(user, onSearch);
		}

		@Override
		protected void buildOptionsComponent(HorizontalLayout options) {
			this.includeTitles = new Checkbox(true);
			this.includeYears = new Checkbox(true);

			options.add(this.includeTitles, this.includeYears);
		}

		@Override
		protected void resetFilters() {
			this.includeTitles.setValue(Boolean.TRUE);
			this.includeYears.setValue(Boolean.TRUE);
		}

		@Override
		protected Predicate buildPredicateForAuthenticatedUser(Root<Publication> root, CriteriaQuery<?> query,
				CriteriaBuilder criteriaBuilder, Person user) {
			return criteriaBuilder.equal(root.get("authorships").get("person"), user); //$NON-NLS-1$ //$NON-NLS-2$
		}

		@Override
		protected void buildQueryFor(String keywords, List<Predicate> predicates, Root<Publication> root,
				CriteriaBuilder criteriaBuilder) {
			if (this.includeTitles.getValue() == Boolean.TRUE) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.lower(root.get("title")), keywords)); //$NON-NLS-1$
			}
			if (this.includeYears.getValue() == Boolean.TRUE) {
				predicates.add(criteriaBuilder.like(criteriaBuilder.toString(root.get("publicationYear")), keywords)); //$NON-NLS-1$
			}
		}

		@Override
		public void localeChange(LocaleChangeEvent event) {
			super.localeChange(event);
			this.includeTitles.setLabel(getTranslation("views.filters.include_titles")); //$NON-NLS-1$
			this.includeYears.setLabel(getTranslation("views.filters.include_years")); //$NON-NLS-1$
		}

	}

	/** Specification that is validating publications of specific type.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	private class PublicationTypeSpecification implements Specification<Publication> {

		private static final long serialVersionUID = -2002042407216268430L;

		@Override
		public Predicate toPredicate(Root<Publication> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
			final var predicates = getSupportedPublicationTypes()
				.map(it -> criteriaBuilder.equal(root.get("type"), it)) //$NON-NLS-1$
				.toArray(size -> new Predicate[size]);
			return criteriaBuilder.or(predicates);
		}

	}

	/** Provider of data for publications to be displayed in the list of publications view.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	@FunctionalInterface
	protected interface PublicationDataProvider {

		/** Fetch publication data.
		 *
		 * @param publicationService the service to have access to the JPA.
		 * @param pageRequest the request for paging the data.
		 * @param filters the filters to apply for selecting the data.
		 * @return the lazy data page.
		 */
		Page<Publication> fetch(PublicationService publicationService, PageRequest pageRequest, AbstractFilters<Publication> filters);

	}

}
