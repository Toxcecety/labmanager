/*
 * $Id$
 * 
 * Copyright (c) 2019-22, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
 * All rights reserved.
 *
 * This software is the confidential and proprietary information
 * of the CIAD laboratory and the Université de Technologie
 * de Belfort-Montbéliard ("Confidential Information").
 * You shall not disclose such Confidential Information and shall use
 * it only in accordance with the terms of the license agreement
 * you entered into with the CIAD-UTBM.
 * 
 * http://www.ciad-lab.fr/
 */

package fr.ciadlab.labmanager.entities.teaching;

import java.time.LocalDate;
import java.util.Comparator;

import fr.ciadlab.labmanager.entities.member.PersonComparator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

/** Comparator of teaching activities. The order of the activities depends on the implementation
 * of this interface.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 3.4
 */
@Component
@Primary
public class TeachingActivityComparator implements Comparator<TeachingActivity> {

	private final PersonComparator personComparator;

	/** Constructor.
	 *
	 * @param personComparator the comparator of persons.
	 */
	public TeachingActivityComparator(@Autowired PersonComparator personComparator) {
		this.personComparator = personComparator;
	}
	
	@Override
	public int compare(TeachingActivity a1, TeachingActivity a2) {
		if (a1 == a2) {
			return 0;
		}
		if (a1 == null) {
			return Integer.MIN_VALUE;
		}
		if (a2 == null) {
			return Integer.MAX_VALUE;
		}
		int cmp = this.personComparator.compare(a1.getPerson(), a2.getPerson());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareLevel(a1.getLevel(), a2.getLevel());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareDate(a1.getEndDate(), a2.getEndDate());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareDate(a1.getStartDate(), a2.getStartDate());
		if (cmp != 0) {
			return cmp;
		}
		cmp = compareStudentType(a1.getStudentType(), a2.getStudentType());
		if (cmp != 0) {
			return cmp;
		}
		cmp = StringUtils.compare(a1.getCode(), a2.getCode());
		if (cmp != 0) {
			return cmp;
		}
		cmp = StringUtils.compare(a1.getTitle(), a2.getTitle());
		if (cmp != 0) {
			return cmp;
		}
		return Integer.compare(a1.getId(), a2.getId());
	}

	/** Null-safe comparison the two teaching activity's levels.
	 * 
	 * @param v0 the first value.
	 * @param v1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareLevel(TeachingActivityLevel v0, TeachingActivityLevel v1) {
		if (v0 == v1) {
			return 0;
		}
		if (v0 == null) {
			return Integer.MIN_VALUE;
		}
		if (v1 == null) {
			return Integer.MAX_VALUE;
		}
		return v0.compareTo(v1);
	}

	/** Null-safe comparison the two student types.
	 * 
	 * @param v0 the first value.
	 * @param v1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareStudentType(StudentType v0, StudentType v1) {
		if (v0 == v1) {
			return 0;
		}
		if (v0 == null) {
			return Integer.MIN_VALUE;
		}
		if (v1 == null) {
			return Integer.MAX_VALUE;
		}
		return v0.compareTo(v1);
	}

	/** Null-safe comparison the two dates.
	 * 
	 * @param s0 the first value.
	 * @param s1 the second value.
	 * @return the result of the comparison.
	 */
	protected static int compareDate(LocalDate d0, LocalDate d1) {
		if (d0 == d1) {
			return 0;
		}
		if (d0 == null) {
			return Integer.MIN_VALUE;
		}
		if (d1 == null) {
			return Integer.MAX_VALUE;
		}
		return d1.compareTo(d0);
	}

}