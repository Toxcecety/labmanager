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

package fr.utbm.ciad.labmanager.views;

/** Constants for the views.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public final class ViewConstants {

	/** Default height in pixels of the input list.
	 */
	public static final float DEFAULT_LIST_HEIGHT = 150f;

	/** Default height in pixels of the text area.
	 */
	public static final float DEFAULT_TEXT_AREA_HEIGHT = 200f;

	/** URL of the online manuals.
	 */
	public static final String ONLINE_MANUAL_URL = "https://www.ciad-lab.fr/docs/"; //$NON-NLS-1$
	
	/** Path to the icon of Gravatar.
	 */
	public static final String GRAVATAR_ICON = "/icons/gravatar.svg"; //$NON-NLS-1$

	/** Path to the icon of ORCID.
	 */
	public static final String ORCID_ICON = "/icons/orcid.svg"; //$NON-NLS-1$

	/** Path to the icon of Scopus.
	 */
	public static final String SCOPUS_ICON = "/icons/scopus.svg"; //$NON-NLS-1$

	/** Path to the icon of Web-of-Science.
	 */
	public static final String WOS_ICON = "/icons/wos.svg"; //$NON-NLS-1$

	/** Path to the icon of Scimago.
	 */
	public static final String SCIMAGO_ICON = "/icons/scimago.svg"; //$NON-NLS-1$

	/** URL of the Scimago platform, with the terminal slash character.
	 */
	public static final String SCIMAGO_BASE_URL = "https://www.scimagojr.com/"; //$NON-NLS-1$

	/** Path to the icon of CORE Portal.
	 */
	public static final String CORE_PORTAL_ICON = "/icons/coreportal.svg"; //$NON-NLS-1$

	/** URL of the CORE portal, with the terminal slash character.
	 */
	public static final String CORE_PORTAL_BASE_URL = "https://www.core.edu.au/conference-portal/"; //$NON-NLS-1$

	/** Path to the icon of Google Scholar.
	 */
	public static final String GSCHOLAR_ICON = "/icons/googlescholar.svg"; //$NON-NLS-1$

	/** Path to the icon of ResearchGate.
	 */
	public static final String RESEARCHGATE_ICON = "/icons/researchgate.svg"; //$NON-NLS-1$

	/** Path to the icon of AD Scientific Index.
	 */
	public static final String ADSCIENTIFICINDEX_ICON = "/icons/adscientificindex.svg"; //$NON-NLS-1$

	/** Path to the icon of DBLP.
	 */
	public static final String DBLP_ICON = "/icons/dblp.svg"; //$NON-NLS-1$

	/** URL of DBLP, with the terminal slash character.
	 */
	public static final String DBLP_BASE_URL = "http://dblp.uni-trier.de/"; //$NON-NLS-1$

	/** Path to the icon of Academia.edu.
	 */
	public static final String ACADEMIA_EDU_ICON = "/icons/academia-edu.svg"; //$NON-NLS-1$

	/** URL of Academia.edu, with the terminal slash character.
	 */
	public static final String ACADEMIA_EDU_BASE_URL = "https://multiagent.academia.edu/"; //$NON-NLS-1$

	/** Path to the icon of EU CORDIS.
	 */
	public static final String EU_CORDIS_ICON = "/icons/europe.svg"; //$NON-NLS-1$

	/** URL of EU CORDIS, with the terminal slash character.
	 */
	public static final String EU_CORDIS_BASE_URL = "https://cordis.europa.eu/"; //$NON-NLS-1$

	/** Path to the icon of LinkedIn.
	 */
	public static final String LINKEDIN_ICON = "/icons/linkedin.svg"; //$NON-NLS-1$

	/** Path to the icon of GitHub.
	 */
	public static final String GITHUB_ICON = "/icons/github.svg"; //$NON-NLS-1$

	/** Path to the icon of GitFacebook.
	 */
	public static final String FACEBOOK_ICON = "/icons/facebook.svg"; //$NON-NLS-1$

	/** Path to the icon of DOI.
	 */
	public static final String DOI_ICON = "/icons/doi.svg"; //$NON-NLS-1$

	/** URL of the DOI portal, with the terminal slash character.
	 */
	public static final String DOI_BASE_URL = "https://doi.org/"; //$NON-NLS-1$

	/** Path to the icon of HAL.
	 */
	public static final String HAL_ICON = "/icons/hal.svg"; //$NON-NLS-1$

	/** URL of the HAL portal, with the terminal slash character.
	 */
	public static final String HAL_BASE_URL = "https://hal.science/"; //$NON-NLS-1$

	/** Default size of a page in the Vaadin grids.
	 */
	public static final int GRID_PAGE_SIZE = 100;

	/** Default size of an icon in pixels.
	 */
	public static final int ICON_SIZE = 16;

	/** Default size of an photo in pixels.
	 */
	public static final int PHOTO_SIZE = 75;

	/** Root attribute name for the UI preferences.
	 * @since 4.0
	 */
	public static final String PREFERENCE_ROOT = ViewConstants.class.getPackage().getName() + ".preferences."; //$NON-NLS-1$

	/** Root attribute name for the details section opening.
	 * @since 4.0
	 */
	public static final String DETAILS_SECTION_OPENING_ROOT = PREFERENCE_ROOT + "details_open."; //$NON-NLS-1$

	/** Default minimal size of a two column form.
	 * @since 4.0
	 */
	public static final String DEFAULT_MINIMAL_WIDTH_FOR_2_COLUMNS_FORM = "500px"; //$NON-NLS-1$

	/** Root attribute name for the filter checkbox for the authenticated user.
	 * @since 4.0
	 */
	public static final String AUTHENTICATED_USER_FILTER_ROOT = PREFERENCE_ROOT + "authenticated_user_filter."; //$NON-NLS-1$

	/** Root attribute name for the filter checkbox for the default organization.
	 * @since 4.0
	 */
	public static final String DEFAULT_ORGANIZATION_FILTER_ROOT = PREFERENCE_ROOT + "default_organization_filter."; //$NON-NLS-1$

	private ViewConstants() {
		//
	}

}
