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

package fr.utbm.ciad.labmanager.views.components.persons;

import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.user.UserService.UserEditingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/** Editor of person information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited person. This editor is used in a wizard
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public final class EmbeddedPersonEditorWizard extends PersonEditor {

	private static final long serialVersionUID = 3928100811567654630L;

	private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedPersonEditorWizard.class);

	/** Constructor.
	 *
	 * @param userContext the editing context for the user.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (Spring layer).
	 */
	public EmbeddedPersonEditorWizard(UserEditingContext userContext, AuthenticatedUser authenticatedUser,
									  MessageSourceAccessor messages, @Autowired PersonService personService) {
		super(userContext, false, authenticatedUser, messages, LOGGER, personService);
		createEditorContentAndLinkBeans();
	}

}
