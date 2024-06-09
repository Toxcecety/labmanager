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

import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.conference.ConferenceService;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/** Editor of publication information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited publication. This editor is used for a wizard.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public final class EmbeddedPublicationEditorWizard extends PublicationEditor {

	private static final long serialVersionUID = 7233675942546351403L;

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedPublicationEditorWizard.class);

	/** Constructor.
	 *
	 * @param context the context for editing the entity.
	 * @param supportedTypes list of publication types that are supported by the editor. Only the publications of a type from this list could be edited.
	 * @param enableTypeSelector indicates if the type selector is enabled or disabled.
	 * @param fileManager the manager of files at the server-side.
	 * @param publicationService the service for accessing the JPA entities for publications.
	 * @param personService the service for accessing the JPA entities for persons.
	 * @param userService the service for accessing the JPA entities for users.
	 * @param journalService the service for accessing the JPA entities for journal.
	 * @param conferenceService the service for accessing the JPA entities for conference.
	 * @param axisService service for accessing to the JPA entities of scientific axes. 
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (Spring layer).
	 * @param personCreationLabelKey the key that is used for retrieving the text for creating a new person and associating it to the publication.
	 * @param personFieldLabelKey the key that is used for retrieving the text for the label of the author/editor field.
	 * @param personFieldHelperLabelKey the key that is used for retrieving the text for the helper of the author/editor field.
	 * @param personNullErrorKey the key that is used for retrieving the text of the author/editor null error.
	 * @param personDuplicateErrorKey the key that is used for retrieving the text of the author/editor duplicate error.
	 */
	public EmbeddedPublicationEditorWizard(EntityEditingContext<Publication> context,
										   PublicationType[] supportedTypes, boolean enableTypeSelector,
										   DownloadableFileManager fileManager, PublicationService publicationService,
										   PersonService personService, UserService userService,
										   JournalService journalService, ConferenceService conferenceService,
										   ScientificAxisService axisService, AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
										   String personCreationLabelKey, String personFieldLabelKey, String personFieldHelperLabelKey,
										   String personNullErrorKey, String personDuplicateErrorKey) {
		super(context, supportedTypes, false, enableTypeSelector, fileManager, publicationService, personService, userService,
				journalService, conferenceService, axisService, authenticatedUser, messages, LOGGER, personCreationLabelKey,
				personFieldLabelKey, personFieldHelperLabelKey, personNullErrorKey, personDuplicateErrorKey);
		createEditorContentAndLinkBeans();
	}

}
