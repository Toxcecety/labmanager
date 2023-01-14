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

package fr.ciadlab.labmanager.utils.funding;

import java.util.Locale;

import com.google.common.base.Strings;
import fr.ciadlab.labmanager.configuration.BaseMessageSource;
import org.springframework.context.support.MessageSourceAccessor;

/** The enumeration {@code FundingScheme} provides a list of well-known funding sources.
 * The order of the items (their ordinal values) is frm the less important to the most important.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 2.1
 */
public enum FundingScheme {
	/** No funding scheme.
	 */
	NOT_FUNDED {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Funds are provided by the person himself.
	 */
	SELF_FUNDING {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** A local institution is funding.
	 */
	LOCAL_INSTITUTION {
		@Override
		public boolean isRegional() {
			return true;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** The hosting organization is funding on its own budgets.
	 */
	HOSTING_ORGANIZATION {
		@Override
		public boolean isRegional() {
			return true;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** A french university is funding.
	 */
	FRENCH_UNIVERSITY {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Other source of funding from France.
	 */
	FRENCH_OTHER {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return true;
		}
	},
	/** Funds are provided in the context of a CARNOT Institution and managed by ANR.
	 *
	 * @see "https://www.instituts-carnot.eu/fr"
	 * @see "https://www.anrt.asso.fr/fr"
	 */
	CARNOT {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** The regional council of Burgundy Franche Comte is funding.
	 */
	REGION_BFC {
		@Override
		public boolean isRegional() {
			return true;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** "Contrat Plan Etat Région".
	 *
	 * @see "http://www.datar.gouv.fr/contrats-etat-regions"
	 */
	CPER {
		@Override
		public boolean isRegional() {
			return true;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Fonds uniques interministériels.
	 *
	 * @see "https://www.entreprises.gouv.fr/fr/innovation/poles-de-competitivite/presentation-des-poles-de-competitivite"
	 */
	FUI {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** ADEME.
	 *
	 * @see "https://www.ademe.fr/"
	 */
	ADEME {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** French ANR is funding.
	 *
	 * @see "https://anr.fr/"
	 */
	ANR {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** 
	 * I-SITE (Initiatives-Science – Innovation –Territoires – Economie) 
	 */
	ISITE {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** 
	 * IDEX (Initiatives d’Excellence) 
	 */
	IDEX {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** 
	 * PIA (Plan d'Investissement d'Avenir) 
	 */
	PIA {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Funds are provided in the context of a CIFRE by ANRT.
	 *
	 * @see #FRENCH_COMPANY
	 * @see "https://www.enseignementsup-recherche.gouv.fr/fr/les-cifre-46510"
	 * @see "https://www.anrt.asso.fr/fr"
	 */
	CIFRE {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** A private company is funding, but outside the scope of a {@link #CIFRE}.
	 *
	 * @see #CIFRE
	 */
	FRENCH_COMPANY {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return true;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return true;
		}
	},
	/** An EU university is funding.
	 */
	EU_UNIVERSITY {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Other source of funding from Europe.
	 */
	EU_OTHER {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return true;
		}
	},
	/** "Fonds européen de développement régional".
	 *
	 * @see "https://ec.europa.eu/info/funding-tenders/find-funding/eu-funding-programmes/european-regional-development-fund-erdf_fr"
	 */
	FEDER {
		@Override
		public boolean isRegional() {
			return true;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** EUREKA project is funding.
	 */
	EUREKA {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** EUROSTAR project is funding.
	 */
	EUROSTAR {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** European COST action.
	 *
	 * @see "https://www.cost.eu/"
	 */
	COST_ACTION {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** JPI Urban Europe.
	 *
	 * @see "https://jpi-urbaneurope.eu/"
	 */
	JPIEU {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** INTERREG project is funding.
	 */
	INTERREG {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Pôles européens d’innovation numérique.
	 *
	 * @see "https://digital-strategy.ec.europa.eu/fr/activities/edihs"
	 */
	EDIH {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** H2020 project is funding.
	 */
	H2020 {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Horizon Europe funds.
	 */
	HORIZON_EUROPE {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** An european company is directly funding.
	 */
	EU_COMPANY {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return true;
		}
		@Override
		public boolean isInternational() {
			return false;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return true;
		}
	},
	/** An international university is funding.
	 */
	INTERNATIONAL_UNIVERSITY {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Another source of funding at international level.
	 */
	INTERNTATIONAL_OTHER {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return true;
		}
	},
	/** International mobility programme from CDEFI, i.e. FITEC, e.g. ARFITEC or BRAFITEC.
	 *
	 * @see "http://www.cdefi.fr/fr/activites/les-programmes-de-mobilite-internationale"
	 */
	FITEC {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Campus France is funding.
	 *
	 * @see "https://www.campusfrance.org/fr"
	 */
	CAMPUS_FRANCE {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Nicolas Baudin Programme for mobiltiy and internships.
	 *
	 * @see "https://au.ambafrance.org/Initiative-stages-en-France"
	 */
	NICOLAS_BAUDIN {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Funding is provided by the CONACYT possibly with Campus France.
	 *
	 * @see "https://conacyt.mx/"
	 */
	CONACYT {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Funds are provided by the Chinease Scholarship Council (CSC).
	 *
	 * @see "https://www.chinesescholarshipcouncil.com/"
	 */
	CSC {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** Partenariats Hubert Curien (PHC) possibly through Campus France.
	 *
	 * @see "https://www.campusfrance.org/fr/phc"
	 */
	PHC {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return true;
		}
		@Override
		public boolean isNotAcademic() {
			return false;
		}
	},
	/** An international company is directly funding.
	 */
	INTERNATIONAL_COMPANY {
		@Override
		public boolean isRegional() {
			return false;
		}
		@Override
		public boolean isNational() {
			return false;
		}
		@Override
		public boolean isEuropean() {
			return false;
		}
		@Override
		public boolean isInternational() {
			return true;
		}
		@Override
		public boolean isCompetitive() {
			return false;
		}
		@Override
		public boolean isNotAcademic() {
			return true;
		}
	};

	private static final String MESSAGE_PREFIX = "fundingScheme."; //$NON-NLS-1$

	private MessageSourceAccessor messages;

	/** Replies the message accessor to be used.
	 *
	 * @return the accessor.
	 */
	public MessageSourceAccessor getMessageSourceAccessor() {
		if (this.messages == null) {
			this.messages = BaseMessageSource.getStaticMessageSourceAccessor();
		}
		return this.messages;
	}

	/** Change the message accessor to be used.
	 *
	 * @param messages the accessor.
	 */
	public void setMessageSourceAccessor(MessageSourceAccessor messages) {
		this.messages = messages;
	}

	/** Replies the label of the funding scheme in the current language.
	 *
	 * @return the label of the funding scheme in the current language.
	 */
	public String getLabel() {
		final String label = getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + name());
		return Strings.nullToEmpty(label);
	}

	/** Replies the label of the funding scheme in the given language.
	 *
	 * @param locale the locale to use.
	 * @return the label of the funding scheme in the given  language.
	 */
	public String getLabel(Locale locale) {
		final String label = getMessageSourceAccessor().getMessage(MESSAGE_PREFIX + name(), locale);
		return Strings.nullToEmpty(label);
	}

	/** Replies the CoNRS section that corresponds to the given name, with a case-insensitive
	 * test of the name.
	 *
	 * @param name the name of the CoNRS section, to search for.
	 * @return the CoNRS section.
	 * @throws IllegalArgumentException if the given name does not corresponds to a CoNRS section.
	 */
	public static FundingScheme valueOfCaseInsensitive(String name) {
		if (!Strings.isNullOrEmpty(name)) {
			for (final FundingScheme section : values()) {
				if (name.equalsIgnoreCase(section.name())) {
					return section;
				}
			}
		}
		throw new IllegalArgumentException("Invalid funding scheme: " + name); //$NON-NLS-1$
	}

	/** Indicates if the funding from a regional institution.
	 *
	 * @return {@code true} if the funding is regional.
	 */
	public abstract boolean isRegional();

	/** Indicates if the funding from a national institution.
	 *
	 * @return {@code true} if the funding is national.
	 */
	public abstract boolean isNational();

	/** Indicates if the funding from a european institution.
	 *
	 * @return {@code true} if the funding is european.
	 */
	public abstract boolean isEuropean();

	/** Indicates if the funding from a international institution.
	 *
	 * @return {@code true} if the funding is international.
	 */
	public abstract boolean isInternational();

	/** Indicates if the funding is with competitive project call.
	 *
	 * @return {@code true} if competitive project call.
	 * @since 3.0
	 */
	public abstract boolean isCompetitive();

	/** Indicates if the funding is by not academic institution.
	 *
	 * @return {@code true} if not academic funder.
	 * @since 3.0
	 */
	public abstract boolean isNotAcademic();

	/** Replies the ordinal number of this item in reverse order.
	 *
	 * @return the ordinal from the end
	 * @see #ordinal()
	 * @since 3.0
	 */
	public int reverseOrdinal() {
		return values().length - ordinal() - 1;
	}

}
