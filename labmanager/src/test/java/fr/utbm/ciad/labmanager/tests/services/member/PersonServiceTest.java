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

package fr.utbm.ciad.labmanager.tests.services.member;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.member.Gender;
import fr.utbm.ciad.labmanager.data.member.Membership;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.member.PersonRepository;
import fr.utbm.ciad.labmanager.data.member.WebPageNaming;
import fr.utbm.ciad.labmanager.data.publication.AuthorshipRepository;
import fr.utbm.ciad.labmanager.data.publication.PublicationRepository;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.gscholar.GoogleScholarPlatform;
import fr.utbm.ciad.labmanager.utils.io.scopus.ScopusPlatform;
import fr.utbm.ciad.labmanager.utils.io.wos.WebOfSciencePlatform;
import fr.utbm.ciad.labmanager.utils.names.DefaultPersonNameParser;
import fr.utbm.ciad.labmanager.utils.names.PersonNameComparator;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import fr.utbm.ciad.labmanager.utils.names.SorensenDice.SorensenDicePersonNameComparator;
import fr.utbm.ciad.labmanager.utils.phone.PhoneNumber;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;

/** Tests for {@link PersonService}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class PersonServiceTest {

	private Person pers0;

	private Person pers1;

	private Person pers2;

	private Person pers3;

	private SessionFactory sessionFactory;

	private MessageSourceAccessor messages;

	private PublicationRepository publicationRepository;

	private AuthorshipRepository authorshipRepository;

	private PersonRepository personRepository;

	private PersonNameParser nameParser;

	private PersonNameComparator nameComparator;

	private GoogleScholarPlatform googlePlatfom;

	private ScopusPlatform scopusPlatfom;

	private WebOfSciencePlatform wosPlatfom;

	private PersonService test;

	@BeforeEach
	public void setUp() {
		this.sessionFactory = mock(SessionFactory.class);
		this.messages = mock(MessageSourceAccessor.class);
		this.publicationRepository = mock(PublicationRepository.class);
		this.authorshipRepository = mock(AuthorshipRepository.class);
		this.personRepository = mock(PersonRepository.class);
		this.googlePlatfom = mock(GoogleScholarPlatform.class);
		this.scopusPlatfom = mock(ScopusPlatform.class);
		this.wosPlatfom = mock(WebOfSciencePlatform.class);
		// Create a real parser instance to be used in the test
		this.nameParser = new DefaultPersonNameParser();
		// Create a real comparator instance to be used in the test
		this.nameComparator = new SorensenDicePersonNameComparator(this.nameParser);
		this.test = new PersonService(this.publicationRepository, this.authorshipRepository, this.personRepository,
				this.googlePlatfom, this.scopusPlatfom, this.wosPlatfom, this.nameParser, this.nameComparator,
				this.messages, new Constants(), this.sessionFactory);

		// Prepare some persons to be inside the repository
		// The lenient configuration is used to configure the mocks for all the tests
		// at the same code location for configuration simplicity
		this.pers0 = mock(Person.class, "pers0");
		lenient().when(this.pers0.getId()).thenReturn(123l);
		lenient().when(this.pers0.getFirstName()).thenReturn("F1");
		lenient().when(this.pers0.getLastName()).thenReturn("L1");
		this.pers1 = mock(Person.class, "pers1");
		lenient().when(this.pers1.getId()).thenReturn(234l);
		lenient().when(this.pers1.getFirstName()).thenReturn("F2");
		lenient().when(this.pers1.getLastName()).thenReturn("L2");
		this.pers2 = mock(Person.class, "pers2");
		lenient().when(this.pers2.getId()).thenReturn(345l);
		lenient().when(this.pers2.getFirstName()).thenReturn("F3");
		lenient().when(this.pers2.getLastName()).thenReturn("L3");
		this.pers3 = mock(Person.class, "pers3");
		lenient().when(this.pers3.getId()).thenReturn(456l);
		lenient().when(this.pers3.getFirstName()).thenReturn("F4");
		lenient().when(this.pers3.getLastName()).thenReturn("L4");

		lenient().when(this.personRepository.findAll()).thenReturn(
				Arrays.asList(this.pers0, this.pers1, this.pers2, this.pers3));
		lenient().when(this.personRepository.findById(anyLong())).then(it -> {
			var n = ((Number) it.getArgument(0)).longValue();
			if (n == 123l) {
				return Optional.of(this.pers0);
			} else if (n == 234l) {
				return Optional.of(this.pers1);
			} else if (n == 345l) {
				return Optional.of(this.pers2);
			} else if (n == 456l) {
				return Optional.of(this.pers3);
			}
			return Optional.empty();
		});
		lenient().when(this.personRepository.findByFirstNameAndLastName(anyString(), anyString())).then(it -> {
			if ("F1".equals(it.getArgument(0)) && "L1".equals(it.getArgument(1))) {
				return Collections.singleton(this.pers0);
			}
			if ("F2".equals(it.getArgument(0)) && "L2".equals(it.getArgument(1))) {
				return Collections.singleton(this.pers1);
			}
			if ("F3".equals(it.getArgument(0)) && "L3".equals(it.getArgument(1))) {
				return Collections.singleton(this.pers2);
			}
			if ("F4".equals(it.getArgument(0)) && "L4".equals(it.getArgument(1))) {
				return Collections.singleton(this.pers3);
			}
			return Collections.emptySet();
		});
	}

	@Test
	public void getAllPersons() {
		final List<Person> list = this.test.getAllPersons();
		assertNotNull(list);
		assertEquals(4, list.size());
		assertSame(this.pers0, list.get(0));
		assertSame(this.pers1, list.get(1));
		assertSame(this.pers2, list.get(2));
		assertSame(this.pers3, list.get(3));
	}

	@Test
	public void getPersonById() {
		assertNull(this.test.getPersonById(-4756));
		assertNull(this.test.getPersonById(0));
		assertSame(this.pers0, this.test.getPersonById(123));
		assertSame(this.pers1, this.test.getPersonById(234));
		assertSame(this.pers2, this.test.getPersonById(345));
		assertSame(this.pers3, this.test.getPersonById(456));
		assertNull(this.test.getPersonById(7896));
	}

	@Test
	public void getPersonIdByName() {
		assertEquals(0, this.test.getPersonIdByName(null, null));
		assertEquals(0, this.test.getPersonIdByName(null, ""));
		assertEquals(0, this.test.getPersonIdByName("", null));
		assertEquals(0, this.test.getPersonIdByName(null, "x"));
		assertEquals(0, this.test.getPersonIdByName("x", null));
		assertEquals(0, this.test.getPersonIdByName("", "x"));
		assertEquals(0, this.test.getPersonIdByName("x", ""));
		assertEquals(0, this.test.getPersonIdByName("", ""));
		assertEquals(0, this.test.getPersonIdByName("x", "y"));
		assertEquals(123, this.test.getPersonIdByName("F1", "L1"));
		assertEquals(234, this.test.getPersonIdByName("F2", "L2"));
		assertEquals(345, this.test.getPersonIdByName("F3", "L3"));
		assertEquals(456, this.test.getPersonIdByName("F4", "L4"));
	}

	@Test
	public void getPersonBySimilarName() {
		assertNull(this.test.getPersonBySimilarName(null, null));
		assertNull(this.test.getPersonBySimilarName(null, ""));
		assertNull(this.test.getPersonBySimilarName("", null));
		assertNull(this.test.getPersonBySimilarName(null, "x"));
		assertNull(this.test.getPersonBySimilarName("x", null));
		assertNull(this.test.getPersonBySimilarName("", "x"));
		assertNull(this.test.getPersonBySimilarName("x", ""));
		assertNull(this.test.getPersonBySimilarName("", ""));
		assertNull(this.test.getPersonBySimilarName("x", "y"));
		assertSame(this.pers0, this.test.getPersonBySimilarName("F1", "L1"));
		assertSame(this.pers1, this.test.getPersonBySimilarName("F2", "L2"));
		assertSame(this.pers2, this.test.getPersonBySimilarName("F3", "L3"));
		assertSame(this.pers3, this.test.getPersonBySimilarName("F4", "L4"));
		assertSame(this.pers0, this.test.getPersonBySimilarName("F.", "L1"));
		assertSame(this.pers1, this.test.getPersonBySimilarName("F.", "L2"));
		assertSame(this.pers2, this.test.getPersonBySimilarName("F.", "L3"));
		assertSame(this.pers3, this.test.getPersonBySimilarName("F.", "L4"));
		assertSame(this.pers0, this.test.getPersonBySimilarName("F", "L1"));
		assertSame(this.pers1, this.test.getPersonBySimilarName("F", "L2"));
		assertSame(this.pers2, this.test.getPersonBySimilarName("F", "L3"));
		assertSame(this.pers3, this.test.getPersonBySimilarName("F", "L4"));
	}

	@Test
	public void getPersonIdBySimilarName() {
		assertEquals(0, this.test.getPersonIdBySimilarName(null, null));
		assertEquals(0, this.test.getPersonIdBySimilarName(null, ""));
		assertEquals(0, this.test.getPersonIdBySimilarName("", null));
		assertEquals(0, this.test.getPersonIdBySimilarName(null, "x"));
		assertEquals(0, this.test.getPersonIdBySimilarName("x", null));
		assertEquals(0, this.test.getPersonIdBySimilarName("", "x"));
		assertEquals(0, this.test.getPersonIdBySimilarName("x", ""));
		assertEquals(0, this.test.getPersonIdBySimilarName("", ""));
		assertEquals(0, this.test.getPersonIdBySimilarName("x", "y"));
		assertEquals(123, this.test.getPersonIdBySimilarName("F1", "L1"));
		assertEquals(234, this.test.getPersonIdBySimilarName("F2", "L2"));
		assertEquals(345, this.test.getPersonIdBySimilarName("F3", "L3"));
		assertEquals(456, this.test.getPersonIdBySimilarName("F4", "L4"));
		assertEquals(123, this.test.getPersonIdBySimilarName("F.", "L1"));
		assertEquals(234, this.test.getPersonIdBySimilarName("F.", "L2"));
		assertEquals(345, this.test.getPersonIdBySimilarName("F.", "L3"));
		assertEquals(456, this.test.getPersonIdBySimilarName("F.", "L4"));
		assertEquals(123, this.test.getPersonIdBySimilarName("F", "L1"));
		assertEquals(234, this.test.getPersonIdBySimilarName("F", "L2"));
		assertEquals(345, this.test.getPersonIdBySimilarName("F", "L3"));
		assertEquals(456, this.test.getPersonIdBySimilarName("F", "L4"));
	}

	@Test
	public void createPerson() {
		final PhoneNumber num1 = new PhoneNumber(CountryCode.FRANCE, "123456789");
		final PhoneNumber num2 = new PhoneNumber(CountryCode.FRANCE, "987654321");
		final Person person = this.test.createPerson(true, "NFN", "NLN", Gender.FEMALE, "NE", num1, num2, "NR1",
				"NGRAV", "NORCID", "NRID", "NSCOPID", "NGSC", "NHAL", "NLIN", "NGIT", "NRGID", "NADSI", "NFB", "NDBLP", "NACA",
				"NCORDIS", WebPageNaming.EMAIL_ID, 159, 357, 7854, 2159, 2357, 27854);

		assertNotNull(person);

		final ArgumentCaptor<Person> arg = ArgumentCaptor.forClass(Person.class);
		verify(this.personRepository, only()).save(arg.capture());
		final Person actual = arg.getValue();
		assertNotNull(actual);
		assertSame(person, actual);
		assertTrue(actual.isValidated());
		assertEquals("NFN", actual.getFirstName());
		assertEquals("NLN", actual.getLastName());
		assertSame(Gender.FEMALE, actual.getGender());
		assertEquals("NE", actual.getEmail());
		assertSame(num1, actual.getOfficePhone());
		assertSame(num2, actual.getMobilePhone());
		assertEquals("NR1", actual.getOfficeRoom());
		assertEquals("NGRAV", actual.getGravatarId());
		assertEquals("NORCID", actual.getORCID());
		assertEquals("NRID", actual.getResearcherId());
		assertEquals("NSCOPID", actual.getScopusId());
		assertEquals("NGSC", actual.getGoogleScholarId());
		assertEquals("NHAL", actual.getIdhal());
		assertEquals("NLIN", actual.getLinkedInId());
		assertEquals("NGIT", actual.getGithubId());
		assertEquals("NRGID", actual.getResearchGateId());
		assertEquals("NADSI", actual.getAdScientificIndexId());
		assertEquals("NFB", actual.getFacebookId());
		assertEquals("NDBLP", actual.getDblpURL());
		assertEquals("NACA", actual.getAcademiaURL());
		assertEquals("NCORDIS", actual.getCordisURL());
		assertSame(WebPageNaming.EMAIL_ID, actual.getWebPageNaming());
		assertEquals(159, actual.getGoogleScholarHindex());
		assertEquals(357, actual.getWosHindex());
		assertEquals(7854, actual.getScopusHindex());
		assertEquals(2159, actual.getGoogleScholarCitations());
		assertEquals(2357, actual.getWosCitations());
		assertEquals(27854, actual.getScopusCitations());
	}

	@Test
	public void updatePerson() {
		final PhoneNumber num1 = new PhoneNumber(CountryCode.FRANCE, "123456789");
		final PhoneNumber num2 = new PhoneNumber(CountryCode.FRANCE, "987654321");
		Person person = this.test.updatePerson(234l, true, "NFN", "NLN", Gender.FEMALE, "NE", num1, num2, "NR1",
				"NGRAV", "NORCID", "NRID", "NSCOPID", "NGSC", "NHAL", "NLIN", "NGIT", "NRGID", "NADSI", "NFB", "NDBLP", "NACA",
				"NCORDIS", WebPageNaming.EMAIL_ID, 159, 357, 7854, 2159, 2357, 27854);

		assertNotNull(person);

		final ArgumentCaptor<Long> arg0 = ArgumentCaptor.forClass(Long.class);
		verify(this.personRepository, atLeastOnce()).findById(arg0.capture());
		Long actual0 = arg0.getValue();
		assertNotNull(actual0);
		assertEquals(234, actual0);

		final ArgumentCaptor<Person> arg1 = ArgumentCaptor.forClass(Person.class);
		verify(this.personRepository, atLeastOnce()).save(arg1.capture());
		final Person actual1 = arg1.getValue();
		assertSame(this.pers1, actual1);

		verify(this.pers1, atLeastOnce()).setValidated(eq(true));
		verify(this.pers1, atLeastOnce()).setFirstName(eq("NFN"));
		verify(this.pers1, atLeastOnce()).setLastName(eq("NLN"));
		verify(this.pers1, atLeastOnce()).setGender(same(Gender.FEMALE));
		verify(this.pers1, atLeastOnce()).setEmail(eq("NE"));
		verify(this.pers1, atLeastOnce()).setOfficePhone(same(num1));
		verify(this.pers1, atLeastOnce()).setMobilePhone(same(num2));
		verify(this.pers1, atLeastOnce()).setOfficeRoom(eq("NR1"));
		verify(this.pers1, atLeastOnce()).setGravatarId(eq("NGRAV"));
		verify(this.pers1, atLeastOnce()).setORCID(eq("NORCID"));
		verify(this.pers1, atLeastOnce()).setResearcherId(eq("NRID"));
		verify(this.pers1, atLeastOnce()).setScopusId(eq("NSCOPID"));
		verify(this.pers1, atLeastOnce()).setGoogleScholarId(eq("NGSC"));
		verify(this.pers1, atLeastOnce()).setIdhal(eq("NHAL"));
		verify(this.pers1, atLeastOnce()).setLinkedInId(eq("NLIN"));
		verify(this.pers1, atLeastOnce()).setGithubId(eq("NGIT"));
		verify(this.pers1, atLeastOnce()).setResearchGateId(eq("NRGID"));
		verify(this.pers1, atLeastOnce()).setAdScientificIndexId("NADSI");
		verify(this.pers1, atLeastOnce()).setFacebookId(eq("NFB"));
		verify(this.pers1, atLeastOnce()).setDblpURL(eq("NDBLP"));
		verify(this.pers1, atLeastOnce()).setAcademiaURL(eq("NACA"));
		verify(this.pers1, atLeastOnce()).setCordisURL(eq("NCORDIS"));
		verify(this.pers1, atLeastOnce()).setWebPageNaming(same(WebPageNaming.EMAIL_ID));
		verify(this.pers1, atLeastOnce()).setGoogleScholarHindex(eq(159));
		verify(this.pers1, atLeastOnce()).setWosHindex(eq(357));
		verify(this.pers1, atLeastOnce()).setScopusHindex(eq(7854));
		verify(this.pers1, atLeastOnce()).setGoogleScholarCitations(eq(2159));
		verify(this.pers1, atLeastOnce()).setWosCitations(eq(2357));
		verify(this.pers1, atLeastOnce()).setScopusCitations(eq(27854));
	}

	@Test
	public void removePerson() {
		this.test.removePerson(234);

		final ArgumentCaptor<Long> arg = ArgumentCaptor.forClass(Long.class);

		verify(this.personRepository, atLeastOnce()).findById(arg.capture());
		Long actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);

		verify(this.personRepository, atLeastOnce()).deleteById(arg.capture());
		actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);
	}

	@Test
	public void extractPersonsFrom_null() {
		List<Person> list = this.test.extractPersonsFrom(null, false, false, false);
		assertTrue(list.isEmpty());
	}

	@Test
	public void extractPersonsFrom_empty() {
		List<Person> list = this.test.extractPersonsFrom("", false, false, false);
		assertTrue(list.isEmpty());
	}

	@Test
	public void extractPersonsFrom_unkwownPersons() {
		List<Person> list = this.test.extractPersonsFrom("L1a, F1a and L2a, F2a and L3a, F3a", false, false, false);
		assertEquals(3, list.size());
		assertEquals(0, list.get(0).getId());
		assertEquals("F1a", list.get(0).getFirstName());
		assertEquals("L1a", list.get(0).getLastName());
		assertEquals(0, list.get(1).getId());
		assertEquals("F2a", list.get(1).getFirstName());
		assertEquals("L2a", list.get(1).getLastName());
		assertEquals(0, list.get(2).getId());
		assertEquals("F3a", list.get(2).getFirstName());
		assertEquals("L3a", list.get(2).getLastName());
	}

	@Test
	public void extractPersonsFrom_kwownPersons() {
		List<Person> list = this.test.extractPersonsFrom("L1, F1 and L2, F2 and L3, F3", false, false, false);
		assertEquals(3, list.size());
		assertEquals(123, list.get(0).getId());
		assertEquals("F1", list.get(0).getFirstName());
		assertEquals("L1", list.get(0).getLastName());
		assertEquals(234, list.get(1).getId());
		assertEquals("F2", list.get(1).getFirstName());
		assertEquals("L2", list.get(1).getLastName());
		assertEquals(345, list.get(2).getId());
		assertEquals("F3", list.get(2).getFirstName());
		assertEquals("L3", list.get(2).getLastName());
	}

	@Test
	public void extractPersonsFrom_kwownAndUknownPersons() {
		List<Person> list = this.test.extractPersonsFrom("L1, F1 and L2a, F2a and L3, F3", false, false, false);
		assertEquals(3, list.size());
		assertEquals(123, list.get(0).getId());
		assertEquals("F1", list.get(0).getFirstName());
		assertEquals("L1", list.get(0).getLastName());
		assertEquals(0, list.get(1).getId());
		assertEquals("F2a", list.get(1).getFirstName());
		assertEquals("L2a", list.get(1).getLastName());
		assertEquals(345, list.get(2).getId());
		assertEquals("F3", list.get(2).getFirstName());
		assertEquals("L3", list.get(2).getLastName());
	}

	@Test
	public void containsAMember_oneMember_name() {
		when(this.pers0.getMemberships()).thenReturn(Collections.singleton(mock(Membership.class)));
		boolean r = this.test.containsAMember(Arrays.asList("A, B", "L1, F1"), false);
		assertTrue(r);
	}

	@Test
	public void containsAMember_oneMember_id() {
		when(this.pers0.getMemberships()).thenReturn(Collections.singleton(mock(Membership.class)));
		boolean r = this.test.containsAMember(Arrays.asList("A, B", "123"), false);
		assertTrue(r);
	}

	@Test
	public void containsAMember_noMember_name_noMembership() {
		boolean r = this.test.containsAMember(Arrays.asList("A, B", "L1, F1"), false);
		assertFalse(r);
	}

	@Test
	public void containsAMember_oneMember_id_noMembership() {
		boolean r = this.test.containsAMember(Arrays.asList("A, B", "123"), false);
		assertFalse(r);
	}

	@Test
	public void containsAMember_noMember_name() {
		boolean r = this.test.containsAMember(Arrays.asList("A, B", "X, y"), false);
		assertFalse(r);
	}

	@Test
	public void containsAMember_noMember_id() {
		boolean r = this.test.containsAMember(Arrays.asList("A, B", "1234560"), false);
		assertFalse(r);
	}

}
