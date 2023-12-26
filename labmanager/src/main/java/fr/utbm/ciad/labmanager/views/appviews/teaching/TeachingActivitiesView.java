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

package fr.utbm.ciad.labmanager.views.appviews.teaching;

import com.vaadin.flow.router.HasDynamicTitle;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.components.security.AuthenticatedUser;
import fr.utbm.ciad.labmanager.services.teaching.TeachingService;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.views.components.MainLayout;
import fr.utbm.ciad.labmanager.views.components.teaching.StandardTeachingActivitiesListView;
import jakarta.annotation.security.PermitAll;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;

/** Enable to edit the teaching activities.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
@Route(value = "teaching", layout = MainLayout.class)
@PermitAll
public class TeachingActivitiesView extends StandardTeachingActivitiesListView implements HasDynamicTitle {

	private static final Logger LOGGER = LoggerFactory.getLogger(TeachingActivitiesView.class);

	/** Constructor.
	 *
	 * @param fileManager the manager of filenames for uploaded files.
	 * @param authenticatedUser the connected user.
	 * @param messages the accessor to the localized messages (spring layer).
	 * @param teachingService the service for accessing the teaching activities.
	 */
	public TeachingActivitiesView(
			@Autowired DownloadableFileManager fileManager,
			@Autowired AuthenticatedUser authenticatedUser, @Autowired MessageSourceAccessor messages,
			@Autowired TeachingService teachingService) {
		super(fileManager, authenticatedUser, messages, teachingService, LOGGER);
	}

	@Override
	public String getPageTitle() {
		return getTranslation("views.teaching_activities.activities.list"); //$NON-NLS-1$
	}

}
