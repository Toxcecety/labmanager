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

package fr.utbm.ciad.labmanager.data.publication.type;

import java.io.IOException;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;
import fr.utbm.ciad.labmanager.data.EntityUtils;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.publication.ConferenceBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.utils.HashCodeUtils;
import fr.utbm.ciad.labmanager.utils.RequiredFieldInForm;
import fr.utbm.ciad.labmanager.utils.ranking.CoreRanking;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import org.apache.jena.ext.com.google.common.base.Strings;
import org.springframework.context.support.MessageSourceAccessor;

/** Keynote in a conference or a workshop.
 *
 * <p>This type has no equivalent in the BibTeX types. It is inspired by: {@code inproceedings}, {@code conference}.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Entity
@DiscriminatorValue("KeyNote")
public class KeyNote extends Publication implements ConferenceBasedPublication {

	private static final long serialVersionUID = -6657050556989265460L;

	/** Reference to the conference.
	 *
	 * @since 3.6
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	private Conference conference;

	/** Number of the conference occurrence.
	 *
	 * @since 3.6
	 */
	@Column
	private int conferenceOccurrenceNumber;

	/** List of names of the editors of the proceedings of the event.
	 * The list of names is usually a sequence of names separated by {@code AND}, and each name has the format {@code LAST, VON, FIRST}.
	 */
	@Column(length = EntityUtils.LARGE_TEXT_SIZE)
	private String editors;

	/** Name of the institution that has organized the event.
	 */
	@Column
	private String organization;

	/** Geographical location of the event. Usually, it is a city and a country.
	 */
	@Column
	private String address;

	/** Construct a conference paper with the given values.
	 *
	 * @param publication the publication to copy.
	 * @param conference the reference to the conference
	 * @param conferenceOccurrenceNumber the number of the conference's occurrence.
	 * @param editors the list of the names of the editors. Each name may have the format {@code LAST, VON, FIRST} and the names may be separated
	 *     with {@code AND}.
	 * @param orga the name of the organization institution.
	 * @param address the geographical location of the event, usually a city and a country.
	 */
	public KeyNote(Publication publication, Conference conference, int conferenceOccurrenceNumber, String editors, String orga, String address) {
		super(publication);
		this.conference = conference;
		this.conferenceOccurrenceNumber = conferenceOccurrenceNumber;
		this.editors = editors;
		this.organization = orga;
		this.address = address;
	}

	/** Construct an empty conference paper.
	 */
	public KeyNote() {
		//
	}

	@Override
	public int hashCode() {
		int h = super.hashCode();
		h = HashCodeUtils.add(h, this.conference);
		h = HashCodeUtils.add(h, this.conferenceOccurrenceNumber);
		h = HashCodeUtils.add(h, this.editors);
		h = HashCodeUtils.add(h, this.organization);
		h = HashCodeUtils.add(h, this.address);
		return h;
	}

	@Override
	public boolean equals(Object obj) {
		if (!super.equals(obj)) {
			return false;
		}
		final KeyNote other = (KeyNote) obj;
		if (!Objects.equals(this.conference, other.conference)) {
			return false;
		}
		if (this.conferenceOccurrenceNumber != other.conferenceOccurrenceNumber) {
			return false;
		}
		if (!Objects.equals(this.editors, other.editors)) {
			return false;
		}
		if (!Objects.equals(this.organization, other.organization)) {
			return false;
		}
		if (!Objects.equals(this.address, other.address)) {
			return false;
		}
		return true;
	}

	@Override
	public void forEachAttribute(MessageSourceAccessor messages, Locale locale, AttributeConsumer consumer) throws IOException {
		super.forEachAttribute(consumer);
		if (getConferenceOccurrenceNumber() > 0) {
			consumer.accept("conferenceOcccurrenceNumber", Integer.valueOf(getConferenceOccurrenceNumber())); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getEditors())) {
			consumer.accept("editors", getEditors()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getOrganization())) {
			consumer.accept("organization", getOrganization()); //$NON-NLS-1$
		}
		if (!Strings.isNullOrEmpty(getAddress())) {
			consumer.accept("address", getAddress()); //$NON-NLS-1$
		}
	}

	@Override
	@JsonIgnore
	public String getWherePublishedShortDescription() {
		final StringBuilder buf = new StringBuilder();
		buf.append(getPublicationTarget());
		if (!Strings.isNullOrEmpty(getOrganization())) {
			buf.append(", "); //$NON-NLS-1$
			buf.append(getOrganization());
		}
		if (!Strings.isNullOrEmpty(getAddress())) {
			buf.append(", "); //$NON-NLS-1$
			buf.append(getAddress());
		}
		if (!Strings.isNullOrEmpty(getISBN())) {
			buf.append(", ISBN "); //$NON-NLS-1$
			buf.append(getISBN());
		}
		if (!Strings.isNullOrEmpty(getISSN())) {
			buf.append(", ISSN "); //$NON-NLS-1$
			buf.append(getISSN());
		}
		return buf.toString();
	}

	private static void appendConferenceName(StringBuilder buf, Conference conference, int year) {
		buf.append(conference.getName());
		if (!Strings.isNullOrEmpty(conference.getAcronym())) {
			buf.append(" (").append(conference.getAcronym()).append("-").append(year % 100).append(")"); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
		}
	}

	@Override
	public String getPublicationTarget() {
		final StringBuilder buf = new StringBuilder();
		final Conference conference = getConference();
		if (conference != null) {
			final int number = getConferenceOccurrenceNumber();
			if (number > 1) {
				buf.append(number).append(ConferenceBasedPublication.getNumberDecorator(number, getMajorLanguage())).append(" "); //$NON-NLS-1$;
			}
			final int year = getPublicationYear();
			appendConferenceName(buf, conference, year);
			Conference enclosingConference = conference.getEnclosingConference();
			if (enclosingConference != null) {
				final Set<Integer> identifiers = new HashSet<>();
				identifiers.add(Integer.valueOf(conference.getId()));
				while (enclosingConference != null && identifiers.add(Integer.valueOf(enclosingConference.getId()))) {
					buf.append(", ").append("in").append(" "); //$NON-NLS-1$ //$NON-NLS-2$ //$NON-NLS-3$
					appendConferenceName(buf, enclosingConference, year);
					enclosingConference = enclosingConference.getEnclosingConference();
				}
			}
		}
		return buf.toString();
	}

	@Override
	@RequiredFieldInForm
	public Conference getConference() {
		return this.conference;
	}

	@Override
	public void setConference(Conference conference) {
		this.conference = conference;
	}

	@Override
	@RequiredFieldInForm
	public int getConferenceOccurrenceNumber() {
		return this.conferenceOccurrenceNumber;
	}

	@Override
	public void setConferenceOccurrenceNumber(int number) {
		if (number > 0) {
			this.conferenceOccurrenceNumber = number;
		} else {
			this.conferenceOccurrenceNumber = 0;
		}
	}

	/** Change the number of the occurrence of the conference in which the publication was published.
	 * <p>
	 * In the example of the "14th International Conference on Systems", the occurrence number is "14".
	 *
	 * @param number the conference occurrence number.
	 * @see #setConference(Conference)
	 */
	public final void setConferenceOccurrenceNumber(Number number) {
		if (number == null) {
			setConferenceOccurrenceNumber(0);
		} else {
			setConferenceOccurrenceNumber(number.intValue());
		}
	}

	/** Replies the editors of the proceedings of the event (conference or workshop) in which the publication was published.
	 * The editors is usually a list of names, separated by {@code AND}, and each name has the format {@code LAST, VON, FIRST}.
	 *
	 * @return the editor names.
	 */
	public String getEditors() {
		return this.editors;
	}

	/** Change the editors of the proceedings of the event (conference or workshop) in which the publication was published.
	 * The editors is usually a list of names, separated by {@code AND}, and each name has the format {@code LAST, VON, FIRST}.
	 *
	 * @param names the editor names.
	 */
	public void setEditors(String names) {
		this.editors = Strings.emptyToNull(names);
	}

	/** Replies the name of the organization of the event (conference or workshop) in which the publication was published.
	 *
	 * @return the organization name.
	 */
	public String getOrganization() {
		return this.organization;
	}

	/** Change the name of the organization of the event (conference or workshop) in which the publication was published.
	 *
	 * @param name the organization name.
	 */
	public void setOrganization(String name) {
		this.organization = Strings.emptyToNull(name);
	}

	/** Replies the geographic location of the event (conference or workshop) in which the publication was published.
	 * The location is usually a city and a country.
	 *
	 * @return the address.
	 */
	public String getAddress() {
		return this.address;
	}

	/** Change the geographic location of the event (conference or workshop) in which the publication was published.
	 * The location is usually a city and a country.
	 *
	 * @param address the address.
	 */
	public void setAddress(String address) {
		this.address = Strings.emptyToNull(address);
	}

	@Override
	public CoreRanking getCoreRanking() {
		final Conference conference = getConference();
		if (conference != null) {
			return conference.getCoreIndexByYear(getPublicationYear());
		}
		return CoreRanking.NR;
	}
	
	@Override
	public boolean isRanked() {
		final Conference conference = getConference();
		if (conference != null) {
			return conference.getCoreIndexByYear(getPublicationYear()) != CoreRanking.NR;
		}
		return false;
	}

	@Override
	public Boolean getOpenAccess() {
		final Conference conference = getConference();
		if (conference != null) {
			return conference.getOpenAccess();
		}
		return null;
	}

	/** Replies the name of the publisher.
	 * This functions delegates to the conference.
	 *
	 * @return the publisher name.
	 * @deprecated see {@link Conference#getPublisher()}
	 */
	@Deprecated(forRemoval = true, since = "3.6")
	public String getPublisher() {
		final Conference conference = getConference();
		if (conference != null) {
			return conference.getPublisher();
		}
		return null;
	}

	/** Change the name of the publisher.
	 * This functions delegates to the conference.
	 *
	 * @param name the publisher name.
	 * @deprecated see {@link Conference#setPublisher(String)}
	 */
	@Deprecated(forRemoval = true, since = "3.6")
	public void setPublisher(String name) {
		final Conference conference = getConference();
		if (conference != null) {
			conference.setPublisher(name);
		}
	}

	/** Replies the ISBN number that is associated to this publication.
	 * This functions delegates to the conference.
	 *
	 * @return the ISBN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/ISBN"
	 * @deprecated See {@link Conference#getISBN()}
	 */
	@Override
	@Deprecated(since = "3.6")
	public String getISBN() {
		final Conference conference = getConference();
		if (conference != null) {
			return conference.getISBN();
		}
		return null;
	}

	/** Change the ISBN number that is associated to this publication.
	 * This functions delegates to the conference.
	 *
	 * @param isbn the ISBN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/ISBN"
	 * @deprecated See {@link Conference#setISBN(String)}
	 */
	@Override
	@Deprecated(since = "3.6")
	public void setISBN(String isbn) {
		final Conference conference = getConference();
		if (conference != null) {
			conference.setISBN(isbn);
		}
	}

	/** Replies the ISSN number that is associated to this publication.
	 * This functions delegates to the conference.
	 *
	 * @return the ISSN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
	 * @deprecated See {@link Conference#getISSN()}
	 */
	@Override
	@Deprecated(since = "3.6")
	public String getISSN() {
		final Conference conference = getConference();
		if (conference != null) {
			return conference.getISSN();
		}
		return null;
	}

	/** Change the ISSN number that is associated to this publication.
	 * This functions delegates to the conference.
	 *
	 * @param issn the ISSN number or {@code null}.
	 * @see "https://en.wikipedia.org/wiki/International_Standard_Serial_Number"
	 * @deprecated See {@link Conference#setISSN(String)}
	 */
	@Override
	@Deprecated(since = "3.6")
	public final void setISSN(String issn) {
		final Conference conference = getConference();
		if (conference != null) {
			conference.setISSN(issn);
		}
	}

}
