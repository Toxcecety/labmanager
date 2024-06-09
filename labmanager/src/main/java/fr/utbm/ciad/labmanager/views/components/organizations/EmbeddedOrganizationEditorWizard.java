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

package fr.utbm.ciad.labmanager.views.components.organizations;

import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.services.AbstractEntityService.EntityEditingContext;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import org.aspectj.weaver.ast.Or;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.MessageSourceAccessor;

/** Editor of organization information that may be embedded. This editor does not provide
 * the components for saving the information. It is the role of the component that
 * is embedding this editor to save the edited organization. This editor is a wizard.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public final class EmbeddedOrganizationEditorWizard extends OrganizationEditor{

    private static final long serialVersionUID = -5119278327562827799L;

    private static final Logger LOGGER = LoggerFactory.getLogger(EmbeddedOrganizationEditor.class);

    /** Constructor.
     *
     * @param context the context for editing the entity.
     * @param fileManager the manager of files at the server-side.
     * @param authenticatedUser the connected user.
     * @param messages the accessor to the localized messages (Spring layer).
     * @param organizationService the service for accessing the organizations.
     * @param addressService the service for accessing the organization addresses.
     */
    public EmbeddedOrganizationEditorWizard(EntityEditingContext<ResearchOrganization> context,
                                      DownloadableFileManager fileManager,
                                      AuthenticatedUser authenticatedUser, MessageSourceAccessor messages,
                                      ResearchOrganizationService organizationService,
                                      OrganizationAddressService addressService) {
        super(context, false, fileManager, authenticatedUser, messages, LOGGER, organizationService, addressService);
        createEditorContentAndLinkBeans();
    }

}
