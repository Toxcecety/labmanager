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

package fr.utbm.ciad.labmanager.data.member;

import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationComparator;
import fr.utbm.ciad.labmanager.utils.Comparators;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.util.Comparator;

/** Comparator of memberships. Memberships are sorted by termination status, organization,
 * date (from future to past), person and CNU section.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Component
@Primary
public class ChronoMembershipComparator implements Comparator<Membership> {

	private PersonComparator personComparator;

	private ResearchOrganizationComparator organizationComparator;

	/** Constructor.
	 *
	 * @param personComparator the comparator of persons names.
	 * @param organizationComparator the comparator of research organizations.
	 */
	public ChronoMembershipComparator(@Autowired PersonComparator personComparator, @Autowired ResearchOrganizationComparator organizationComparator) {
		this.personComparator = personComparator;
		this.organizationComparator = organizationComparator;
	}

	@Override
	public int compare(Membership o1, Membership o2) {
		if (o1 == o2) {
			return 0;
		}
		if (o1 == null) {
			return Integer.MIN_VALUE;
		}
		if (o2 == null) {
			return Integer.MAX_VALUE;
		}
		var n = Integer.compare(period(o1), period(o2));
		if (n != 0) {
			return n;
		}
		n = this.organizationComparator.compare(o1.getDirectResearchOrganization(), o2.getDirectResearchOrganization());
		if (n != 0) {
			return n;
		}
		n = Comparators.compareDateRange(o1.getMemberSinceWhen(), o1.getMemberToWhen(), o2.getMemberSinceWhen(), o2.getMemberToWhen());
		if (n != 0) {
			// Negate the result for sorting from future to past
			return -n;
		}
		n = o1.getMemberStatus().compareTo(o2.getMemberStatus());
		if (n != 0) {
			return n;
		}
		n = this.personComparator.compare(o1.getPerson(), o2.getPerson());
		if (n != 0) {
			return n;
		}
		n = Comparators.compare(o1.getResponsibility(), o2.getResponsibility());
		if (n != 0) {
			return n;
		}
		n = Comparators.compare(o1.getCnuSection(), o2.getCnuSection());
		if (n != 0) {
			return n;
		}
		n = Comparators.compare(o1.getConrsSection(), o2.getConrsSection());
		if (n != 0) {
			return n;
		}
		return Comparators.compare(o1.getFrenchBap(), o2.getFrenchBap());
	}

	private static int period(Membership d) {
		if (d.isFormer()) {
			return 2;
		}
		if (d.isFuture()) {
			return 0;
		}
		return 1;
	}

}


