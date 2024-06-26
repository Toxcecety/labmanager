/*
 * $Id$
 * 
 * Copyright (c) 2019-2024, CIAD Laboratory, Universite de Technologie de Belfort Montbeliard
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

package fr.utbm.ciad.labmanager.tests.data.teaching;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

import fr.utbm.ciad.labmanager.configuration.messages.BaseMessageSource;
import fr.utbm.ciad.labmanager.data.teaching.TeachingActivityType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.context.support.MessageSourceAccessor;

/** Tests for {@link TeachingActivityType}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class TeachingActivityTypeTest {

	private List<TeachingActivityType> items;

	private MessageSourceAccessor messages;

	@BeforeEach
	public void setUp() {
		this.messages = BaseMessageSource.getGlobalMessageAccessor();
		this.items = new ArrayList<>();
		this.items.addAll(Arrays.asList(TeachingActivityType.values()));
	}

	private TeachingActivityType cons(TeachingActivityType status) {
		assertTrue(this.items.remove(status), "Expecting enumeration item: " + status.toString());
		return status;
	}

	private void assertAllTreated() {
		assertTrue(this.items.isEmpty(), "Missing enumeration items: " + this.items.toString());
	}

	@Test
	public void valueOfCaseInsensitive_String_special() {
		assertThrows(IllegalArgumentException.class, () -> TeachingActivityType.valueOfCaseInsensitive(null));
		assertThrows(IllegalArgumentException.class, () -> TeachingActivityType.valueOfCaseInsensitive(""));
	}

	@Test
	public void valueOfCaseInsensitive_String_upperCase() {
		assertEquals(cons(TeachingActivityType.LECTURES), TeachingActivityType.valueOfCaseInsensitive("LECTURES"));
		assertEquals(cons(TeachingActivityType.INTEGRATED_COURSES), TeachingActivityType.valueOfCaseInsensitive("INTEGRATED_COURSES"));
		assertEquals(cons(TeachingActivityType.TUTORIALS), TeachingActivityType.valueOfCaseInsensitive("TUTORIALS"));
		assertEquals(cons(TeachingActivityType.PRACTICAL_WORKS), TeachingActivityType.valueOfCaseInsensitive("PRACTICAL_WORKS"));
		assertEquals(cons(TeachingActivityType.GROUP_SUPERVISION), TeachingActivityType.valueOfCaseInsensitive("GROUP_SUPERVISION"));
		assertEquals(cons(TeachingActivityType.PROJECT_SUPERVISION), TeachingActivityType.valueOfCaseInsensitive("PROJECT_SUPERVISION"));
		assertAllTreated();
		assertThrows(IllegalArgumentException.class, () -> TeachingActivityType.valueOfCaseInsensitive("XYZ"));
	}

	@Test
	public void valueOfCaseInsensitive_String_lowerCase() {
		assertEquals(cons(TeachingActivityType.LECTURES), TeachingActivityType.valueOfCaseInsensitive("lectures"));
		assertEquals(cons(TeachingActivityType.INTEGRATED_COURSES), TeachingActivityType.valueOfCaseInsensitive("Integrated_Courses"));
		assertEquals(cons(TeachingActivityType.TUTORIALS), TeachingActivityType.valueOfCaseInsensitive("Tutorials"));
		assertEquals(cons(TeachingActivityType.PRACTICAL_WORKS), TeachingActivityType.valueOfCaseInsensitive("Practical_Works"));
		assertEquals(cons(TeachingActivityType.GROUP_SUPERVISION), TeachingActivityType.valueOfCaseInsensitive("GRoup_Supervision"));
		assertEquals(cons(TeachingActivityType.PROJECT_SUPERVISION), TeachingActivityType.valueOfCaseInsensitive("Project_Supervision"));
		assertAllTreated();
		assertThrows(IllegalArgumentException.class, () -> TeachingActivityType.valueOfCaseInsensitive("xyz"));
	}

	@Test
	public void getLabel_Locale_US() throws Exception {
		assertEquals("Lectures", cons(TeachingActivityType.LECTURES).getLabel(this.messages, Locale.US));
		assertEquals("Integrated Courses", cons(TeachingActivityType.INTEGRATED_COURSES).getLabel(this.messages, Locale.US));
		assertEquals("Tutorials", cons(TeachingActivityType.TUTORIALS).getLabel(this.messages, Locale.US));
		assertEquals("Practical Works", cons(TeachingActivityType.PRACTICAL_WORKS).getLabel(this.messages, Locale.US));
		assertEquals("Group Supervision", cons(TeachingActivityType.GROUP_SUPERVISION).getLabel(this.messages, Locale.US));
		assertEquals("Project Supervision", cons(TeachingActivityType.PROJECT_SUPERVISION).getLabel(this.messages, Locale.US));
		assertAllTreated();
	}

	@Test
	public void getHetdFactor_internal() {
		assertEquals(1.5f, cons(TeachingActivityType.LECTURES).getHetdFactor(false));
		assertEquals(1.25f, cons(TeachingActivityType.INTEGRATED_COURSES).getHetdFactor(false));
		assertEquals(1f, cons(TeachingActivityType.TUTORIALS).getHetdFactor(false));
		assertEquals(1f, cons(TeachingActivityType.PRACTICAL_WORKS).getHetdFactor(false));
		assertEquals(1f, cons(TeachingActivityType.GROUP_SUPERVISION).getHetdFactor(false));
		assertEquals(1f, cons(TeachingActivityType.PROJECT_SUPERVISION).getHetdFactor(false));
		assertAllTreated();
	}
	
	@Test
	public void getHetdFactor_external() {
		assertEquals(1.5f, cons(TeachingActivityType.LECTURES).getHetdFactor(true));
		assertEquals(1.25f, cons(TeachingActivityType.INTEGRATED_COURSES).getHetdFactor(true));
		assertEquals(1f, cons(TeachingActivityType.TUTORIALS).getHetdFactor(true));
		assertEquals(2f/3f, cons(TeachingActivityType.PRACTICAL_WORKS).getHetdFactor(true));
		assertEquals(2f/3f, cons(TeachingActivityType.GROUP_SUPERVISION).getHetdFactor(true));
		assertEquals(2f/3f, cons(TeachingActivityType.PROJECT_SUPERVISION).getHetdFactor(true));
		assertAllTreated();
	}

	@Test
	public void convertHoursToHetd_internal() {
		assertEquals(3f, cons(TeachingActivityType.LECTURES).convertHoursToHetd(2f, false));
		assertEquals(2.5f, cons(TeachingActivityType.INTEGRATED_COURSES).convertHoursToHetd(2f, false));
		assertEquals(2f, cons(TeachingActivityType.TUTORIALS).convertHoursToHetd(2f, false));
		assertEquals(2f, cons(TeachingActivityType.PRACTICAL_WORKS).convertHoursToHetd(2f, false));
		assertEquals(2f, cons(TeachingActivityType.GROUP_SUPERVISION).convertHoursToHetd(2f, false));
		assertEquals(2f, cons(TeachingActivityType.PROJECT_SUPERVISION).convertHoursToHetd(2f, false));
		assertAllTreated();
	}

	@Test
	public void convertHoursToHetd_external() {
		assertEquals(3f, cons(TeachingActivityType.LECTURES).convertHoursToHetd(2f, true));
		assertEquals(2.5f, cons(TeachingActivityType.INTEGRATED_COURSES).convertHoursToHetd(2f, true));
		assertEquals(2f, cons(TeachingActivityType.TUTORIALS).convertHoursToHetd(2f, true));
		assertEquals(4f/3f, cons(TeachingActivityType.PRACTICAL_WORKS).convertHoursToHetd(2f, true));
		assertEquals(4f/3f, cons(TeachingActivityType.GROUP_SUPERVISION).convertHoursToHetd(2f, true));
		assertEquals(4f/3f, cons(TeachingActivityType.PROJECT_SUPERVISION).convertHoursToHetd(2f, true));
		assertAllTreated();
	}

}
