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

import fr.utbm.ciad.labmanager.data.publication.SorensenDicePublicationComparator;
import org.junit.jupiter.api.BeforeEach;

/** Tests for {@link SorensenDicePublicationComparator}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
public class SorensenDicePublicationComparatorTest extends AbstractPublicationComparatorTest {

	@BeforeEach
	public void setUp() {
		super.setUp();
		this.test = new SorensenDicePublicationComparator();
	}

}
