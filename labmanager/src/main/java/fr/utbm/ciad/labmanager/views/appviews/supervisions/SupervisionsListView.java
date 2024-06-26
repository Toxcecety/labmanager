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

package fr.utbm.ciad.labmanager.views.appviews.supervisions;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.data.user.UserRole;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.organization.OrganizationAddressService;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.services.scientificaxis.ScientificAxisService;
import fr.utbm.ciad.labmanager.services.supervision.SupervisionService;
import fr.utbm.ciad.labmanager.services.user.UserService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.supervisions.StandardSupervisionListView;
import jakarta.annotation.security.RolesAllowed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/** Enable to edit the supervisions for all the persons.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "supervisions", layout = MainLayout.class)
@RolesAllowed({UserRole.RESPONSIBLE_GRANT, UserRole.ADMIN_GRANT})
public class SupervisionsListView extends StandardSupervisionListView implements HasDynamicTitle {

	private static final long serialVersionUID = 7290371153524168134L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SupervisionsListView.class);

	/** Constructor.
	 * 
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (spring layer).
	 * @param supervisionService the service for accessing the supervisions.
	 * @param membershipService the service for accessing the membership JPA entities.
	 * @param personService the service for accessing the person JPA entities.
	 * @param userService the service for accessing the connected user JPA entities.
	 * @param organizationService the service for accessing the organization JPA entities.
	 * @param addressService the service for accessing the organization address JPA entities.
	 * @param axisService the service for accessing the scientific axis JPA entities.
	 */
	public SupervisionsListView(
			@Autowired AuthenticatedUser authenticatedUser,
			@Autowired MessageSourceAccessor messages,
			@Autowired SupervisionService supervisionService,
			@Autowired MembershipService membershipService,
			@Autowired PersonService personService,
			@Autowired UserService userService,
			@Autowired ResearchOrganizationService organizationService,
			@Autowired OrganizationAddressService addressService,
			@Autowired ScientificAxisService axisService) {
		super(authenticatedUser, messages, supervisionService, membershipService, personService, userService,
				organizationService, addressService, axisService, LOGGER);
	}

	@Override
	public String getPageTitle() {
		return getTranslation("views.supervision.supervisions.list"); //$NON-NLS-1$
	}

}
