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

package fr.ciadlab.labmanager.indicators.project;

import java.time.LocalDate;

import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.entities.organization.ResearchOrganization;
import fr.ciadlab.labmanager.indicators.AbstractIndicator;
import fr.ciadlab.labmanager.utils.Unit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.stereotype.Component;

/** Count the number of projects with non-academic partners.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.2
 */
@Component
public class IndustrialProjectCountIndicator extends AbstractIndicator {

	/** Constructor.
	 *
	 * @param messages the provider of messages.
	 * @param constants the accessor to the constants.
	 */
	public IndustrialProjectCountIndicator(
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants) {
		super(messages, constants);
	}

	@Override
	public String getName() {
		return getMessage("industrialProjectCountIndicator.name"); //$NON-NLS-1$
	}

	@Override
	public String getLabel(Unit unit) {
		return getLabelWithYears("industrialProjectCountIndicator.label"); //$NON-NLS-1$
	}

	@Override
	public LocalDate getReferencePeriodStart() {
		return LocalDate.of(2016, 1, 1);
	}

	@Override
	public LocalDate getReferencePeriodEnd() {
		return LocalDate.of(2021, 12, 31);
	}

	@Override
	protected Number computeValue(ResearchOrganization organization) {
		return Integer.valueOf(57);
	}

}