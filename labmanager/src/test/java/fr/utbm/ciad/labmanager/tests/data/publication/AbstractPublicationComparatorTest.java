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

package fr.utbm.ciad.labmanager.tests.data.publication;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationComparator;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import org.junit.jupiter.api.Test;

/** Abstract tests for {@link PublicationComparator}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public abstract class AbstractPublicationComparatorTest {

	protected static String DUMMY_TEXT = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Aenean commodo "
			+ "ligula eget dolor. Aenean massa. Cum sociis natoque penatibus et magnis dis parturient montes, "
			+ "nascetur ridiculus mus. Donec quam felis, ultricies nec, pellentesque eu, pretium quis, sem. "
			+ "Nulla consequat massa quis enim. Donec pede justo, fringilla vel, aliquet nec, vulputate eget, "
			+ "arcu. In enim justo, rhoncus ut, imperdiet a, venenatis vitae, justo. Nullam dictum felis eu pede "
			+ "mollis pretium. Integer tincidunt. Cras dapibus. Vivamus elementum semper nisi. Aenean vulputate "
			+ "eleifend tellus. Aenean leo ligula, porttitor eu, consequat vitae, eleifend ac, enim. Aliquam lorem "
			+ "ante, dapibus in, viverra quis, feugiat a, tellus. Phasellus viverra nulla ut metus varius laoreet. "
			+ "Quisque rutrum. Aenean imperdiet. Etiam ultricies nisi vel augue. Curabitur ullamcorper ultricies "
			+ "nisi. Nam eget dui. Etiam rhoncus. Maecenas tempus, tellus eget condimentum rhoncus, sem quam semper "
			+ "libero, sit amet adipiscing sem neque sed ipsum. Nam quam nunc, blandit vel, luctus pulvinar, hendrerit "
			+ "id, lorem. Maecenas nec odio et ante tincidunt tempus. Donec vitae sapien ut libero venenatis faucibus. "
			+ "Nullam quis ante. Etiam sit amet orci eget eros faucibus tincidunt. Duis leo. Sed fringilla mauris sit "
			+ "amet nibh. Donec sodales sagittis magna. Sed consequat, leo eget bibendum sodales, augue velit cursus nunc,";

	protected static String DUMMY_TEXT_2 = DUMMY_TEXT.toLowerCase();

	protected static String DUMMY_TEXT_3 = "Li Europan lingues es membres del sam familie. Lor separat existentie es un myth. "
			+ "Por scientie, musica, sport etc, litot Europa usa li sam vocabular. Li lingues differe solmen in li grammatica, "
			+ "li pronunciation e li plu commun vocabules. Omnicos directe al desirabilite de un nov lingua franca: On refusa "
			+ "continuar payar custosi traductores. At solmen va esser necessi far uniform grammatica, pronunciation e plu "
			+ "sommun paroles. Ma quande lingues coalesce, li grammatica del resultant lingue es plu simplic e regulari quam "
			+ "ti del coalescent lingues. Li nov lingua franca va esser plu simplic e regulari quam li existent Europan "
			+ "lingues. It va esser tam simplic quam Occidental in fact, it va esser Occidental. A un Angleso it va semblar "
			+ "un simplificat Angles, quam un skeptic Cambridge amico dit me que Occidental es.Li Europan lingues es membres "
			+ "del sam familie. Lor separat existentie es un myth. Por scientie, musica, sport etc, litot Europa usa li sam "
			+ "vocabular. Li lingues differe solmen in li grammatica, li pronunciation e li plu commun vocabules. Omnicos "
			+ "directe al desirabilite de un nov lingua franca: On refusa continuar payar custosi traductores. At solmen va "
			+ "esser necessi far uniform grammatica, pronunciation e plu sommun paroles.";

	protected Publication p0;

	protected Publication p1;

	protected Publication p2;

	protected Publication p3;

	protected PublicationComparator test;

	public void setUp() {
		this.p0 = mock(Publication.class);
		when(this.p0.getTitle()).thenReturn(DUMMY_TEXT);
		when(this.p0.getDOI()).thenReturn("xyz-0");
		when(this.p0.getType()).thenReturn(PublicationType.INTERNATIONAL_JOURNAL_PAPER);
		when(this.p0.getWherePublishedShortDescription()).thenReturn("short0");
		this.p1 = mock(Publication.class);
		when(this.p1.getTitle()).thenReturn(DUMMY_TEXT_3);
		when(this.p1.getDOI()).thenReturn("xyz-0");
		when(this.p1.getType()).thenReturn(PublicationType.INTERNATIONAL_JOURNAL_PAPER);
		when(this.p1.getWherePublishedShortDescription()).thenReturn("short1");
		this.p2 = mock(Publication.class);
		when(this.p2.getTitle()).thenReturn(DUMMY_TEXT_2);
		when(this.p2.getDOI()).thenReturn("xyz-0");
		when(this.p2.getType()).thenReturn(PublicationType.INTERNATIONAL_CONFERENCE_PAPER);
		when(this.p2.getWherePublishedShortDescription()).thenReturn("short1");
		this.p3 = mock(Publication.class);
		when(this.p3.getTitle()).thenReturn(DUMMY_TEXT_2);
		when(this.p3.getDOI()).thenReturn("xyz-0");
		when(this.p3.getType()).thenReturn(PublicationType.INTERNATIONAL_JOURNAL_PAPER);
		when(this.p3.getWherePublishedShortDescription()).thenReturn("short0");
	}


	@Test
	public void isSimilar_0() {
		assertTrue(this.test.isSimilar(null, null));
	}

	@Test
	public void isSimilar_1() {
		assertFalse(this.test.isSimilar(this.p0, null));
		assertFalse(this.test.isSimilar(this.p1, null));
		assertFalse(this.test.isSimilar(this.p2, null));
		assertFalse(this.test.isSimilar(this.p3, null));
	}

	@Test
	public void isSimilar_2() {
		assertFalse(this.test.isSimilar(null, this.p0));
		assertFalse(this.test.isSimilar(null, this.p1));
		assertFalse(this.test.isSimilar(null, this.p2));
		assertFalse(this.test.isSimilar(null, this.p3));
	}

	@Test
	public void isSimilar_3() {
		assertTrue(this.test.isSimilar(this.p0, this.p0));
		assertTrue(this.test.isSimilar(this.p1, this.p1));
		assertTrue(this.test.isSimilar(this.p2, this.p2));
		assertTrue(this.test.isSimilar(this.p3, this.p3));
	}

	@Test
	public void isSimilar_4() {
		assertFalse(this.test.isSimilar(this.p0, this.p1));
		assertTrue(this.test.isSimilar(this.p0, this.p2));
		assertTrue(this.test.isSimilar(this.p0, this.p3));
	}

	@Test
	public void isSimilar_5() {
		assertFalse(this.test.isSimilar(this.p1, this.p0));
		assertFalse(this.test.isSimilar(this.p1, this.p2));
		assertFalse(this.test.isSimilar(this.p1, this.p3));
	}

	@Test
	public void isSimilar_6() {
		assertTrue(this.test.isSimilar(this.p2, this.p0));
		assertFalse(this.test.isSimilar(this.p2, this.p1));
		assertTrue(this.test.isSimilar(this.p2, this.p3));
	}

	@Test
	public void isSimilar_7() {
		assertTrue(this.test.isSimilar(this.p3, this.p0));
		assertFalse(this.test.isSimilar(this.p3, this.p1));
		assertTrue(this.test.isSimilar(this.p3, this.p2));
	}

}
