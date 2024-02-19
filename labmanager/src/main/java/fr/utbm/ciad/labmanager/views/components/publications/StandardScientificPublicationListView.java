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

import java.util.Arrays;
import java.util.stream.Stream;

import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.addons.entities.AbstractPublicationListView;
import org.slf4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;

/** List all the scientific publications.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class StandardScientificPublicationListView extends AbstractPublicationListView {

	private static final long serialVersionUID = 157090087430635036L;

	private static final PublicationType[] SUPPORTED_PUBLICATION_TYPES = {
			PublicationType.INTERNATIONAL_BOOK,
			PublicationType.INTERNATIONAL_BOOK_CHAPTER,
			PublicationType.INTERNATIONAL_CONFERENCE_PAPER,
			PublicationType.INTERNATIONAL_JOURNAL_PAPER,
			PublicationType.INTERNATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE,
			PublicationType.INTERNATIONAL_ORAL_COMMUNICATION,
			PublicationType.INTERNATIONAL_POSTER,
			PublicationType.NATIONAL_BOOK,
			PublicationType.NATIONAL_BOOK_CHAPTER,
			PublicationType.NATIONAL_CONFERENCE_PAPER,
			PublicationType.NATIONAL_JOURNAL_PAPER,
			PublicationType.NATIONAL_JOURNAL_PAPER_WITHOUT_COMMITTEE,
			PublicationType.NATIONAL_ORAL_COMMUNICATION,
			PublicationType.NATIONAL_POSTER,
			PublicationType.HDR_THESIS,
			PublicationType.MASTER_THESIS,
			PublicationType.PHD_THESIS
	};

	/** Constructor.
	 *
	 * @param fileManager the manager of the filenames for the uploaded files.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (spring layer).
	 * @param publicationService the service for accessing the publications.
	 * @param logger the logger to use.
	 */
	public StandardScientificPublicationListView(
			DownloadableFileManager fileManager,
			AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
			PublicationService publicationService, Logger logger) {
		super(fileManager, authenticatedUser, messages, publicationService, logger,
				"views.publication.delete.title", //$NON-NLS-1$
				"views.publication.delete.message", //$NON-NLS-1$
				"views.publication.delete.success_message", //$NON-NLS-1$
				"views.publication.delete.error_message", //$NON-NLS-1$
				"views.authors"); //$NON-NLS-1$
		setDataProvider((service, pageRequest, filters) -> {
			return publicationService.getAllPublications(pageRequest, createJpaFilters(filters),
					this::initializeEntityFromJPA);
		});
		initializeDataInGrid(getGrid(), getFilters());
	}
	
	@Override
	protected Stream<PublicationType> getSupportedPublicationTypes() {
		return Arrays.asList(SUPPORTED_PUBLICATION_TYPES).stream();
	}

}
