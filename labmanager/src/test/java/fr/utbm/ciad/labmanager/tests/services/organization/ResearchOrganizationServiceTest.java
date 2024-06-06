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

package fr.utbm.ciad.labmanager.tests.services.organization;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeast;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddress;
import fr.utbm.ciad.labmanager.data.organization.OrganizationAddressRepository;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganization;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationRepository;
import fr.utbm.ciad.labmanager.data.organization.ResearchOrganizationType;
import fr.utbm.ciad.labmanager.services.organization.ResearchOrganizationService;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.names.OrganizationNameComparator;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;

/** Tests for {@link ResearchOrganizationService}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class ResearchOrganizationServiceTest {

	private ResearchOrganization orga0;

	private ResearchOrganization orga1;

	private ResearchOrganization orga2;

	private ResearchOrganization orga3;

	private MessageSourceAccessor messages;

	private OrganizationAddressRepository addressRepository;

	private ResearchOrganizationRepository organizationRepository;

	private OrganizationNameComparator organizationNameComparator;

	private SessionFactory sessionFactory;

	private ResearchOrganizationService test;

	private DownloadableFileManager fileManager;

	@BeforeEach
	public void setUp() {
		this.messages = mock(MessageSourceAccessor.class);
		this.addressRepository = mock(OrganizationAddressRepository.class);
		this.organizationRepository = mock(ResearchOrganizationRepository.class);
		this.organizationNameComparator = mock(OrganizationNameComparator.class);
		this.sessionFactory = mock(SessionFactory.class);
		this.fileManager = mock(DownloadableFileManager.class);
		this.test = new ResearchOrganizationService(this.addressRepository, this.organizationRepository, this.fileManager,
				this.organizationNameComparator,
				this.messages, new Constants(), this.sessionFactory);

		// Prepare some organizations to be inside the repository
		// The lenient configuration is used to configure the mocks for all the tests
		// at the same code location for configuration simplicity
		this.orga0 = mock(ResearchOrganization.class);
		lenient().when(this.orga0.getId()).thenReturn(123l);
		lenient().when(this.orga0.getAcronym()).thenReturn("O1");
		lenient().when(this.orga0.getName()).thenReturn("N1");
		this.orga1 = mock(ResearchOrganization.class);
		lenient().when(this.orga1.getId()).thenReturn(234l);
		lenient().when(this.orga1.getAcronym()).thenReturn("O2");
		lenient().when(this.orga1.getName()).thenReturn("N2");
		this.orga2 = mock(ResearchOrganization.class);
		lenient().when(this.orga2.getId()).thenReturn(345l);
		lenient().when(this.orga2.getAcronym()).thenReturn("O3");
		lenient().when(this.orga2.getName()).thenReturn("N3");
		this.orga3 = mock(ResearchOrganization.class);
		lenient().when(this.orga3.getId()).thenReturn(456l);
		lenient().when(this.orga3.getAcronym()).thenReturn("O4");
		lenient().when(this.orga3.getName()).thenReturn("N4");

		lenient().when(this.organizationRepository.findAll()).thenReturn(
				Arrays.asList(this.orga0, this.orga1, this.orga2, this.orga3));
		lenient().when(this.organizationRepository.findById(anyLong())).then(it -> {
			var n = ((Number) it.getArgument(0)).longValue();
			if (n == 123l) {
				return Optional.of(this.orga0);
			} else if (n == 234l) {
				return Optional.of(this.orga1);
			} else if (n == 345l) {
				return Optional.of(this.orga2);
			} else if (n == 456l) {
				return Optional.of(this.orga3);
			}
			return Optional.empty();
		});
	}

	@Test
	public void getAllResearchOrganizations() {
		final List<ResearchOrganization> list = this.test.getAllResearchOrganizations();
		assertNotNull(list);
		assertEquals(4, list.size());
		assertSame(this.orga0, list.get(0));
		assertSame(this.orga1, list.get(1));
		assertSame(this.orga2, list.get(2));
		assertSame(this.orga3, list.get(3));
	}

	@Test
	public void getResearchOrganizationById() {
		assertTrue(this.test.getResearchOrganizationById(-4756).isEmpty());
		assertTrue(this.test.getResearchOrganizationById(0).isEmpty());
		assertSame(this.orga0, this.test.getResearchOrganizationById(123).get());
		assertSame(this.orga1, this.test.getResearchOrganizationById(234).get());
		assertSame(this.orga2, this.test.getResearchOrganizationById(345).get());
		assertSame(this.orga3, this.test.getResearchOrganizationById(456).get());
		assertTrue(this.test.getResearchOrganizationById(7896).isEmpty());
	}

	@Test
	public void getResearchOrganizationByAcronym() {
		when(this.organizationRepository.findDistinctByAcronym(any())).thenAnswer(it -> {
			if (this.orga0.getAcronym().equals(it.getArgument(0))) {
				return Optional.of(this.orga0);
			}
			if (this.orga1.getAcronym().equals(it.getArgument(0))) {
				return Optional.of(this.orga1);
			}
			if (this.orga2.getAcronym().equals(it.getArgument(0))) {
				return Optional.of(this.orga2);
			}
			if (this.orga3.getAcronym().equals(it.getArgument(0))) {
				return Optional.of(this.orga3);
			}
			return Optional.empty();
		});
		assertTrue(this.test.getResearchOrganizationByAcronym(null).isEmpty());
		assertTrue(this.test.getResearchOrganizationByAcronym("").isEmpty());
		assertSame(this.orga0, this.test.getResearchOrganizationByAcronym("O1").get());
		assertSame(this.orga1, this.test.getResearchOrganizationByAcronym("O2").get());
		assertSame(this.orga2, this.test.getResearchOrganizationByAcronym("O3").get());
		assertSame(this.orga3, this.test.getResearchOrganizationByAcronym("O4").get());
		assertTrue(this.test.getResearchOrganizationByAcronym("O5").isEmpty());
	}

	@Test
	public void getResearchOrganizationByName() {
		when(this.organizationRepository.findDistinctByName(any())).thenAnswer(it -> {
			if (this.orga0.getName().equals(it.getArgument(0))) {
				return Optional.of(this.orga0);
			}
			if (this.orga1.getName().equals(it.getArgument(0))) {
				return Optional.of(this.orga1);
			}
			if (this.orga2.getName().equals(it.getArgument(0))) {
				return Optional.of(this.orga2);
			}
			if (this.orga3.getName().equals(it.getArgument(0))) {
				return Optional.of(this.orga3);
			}
			return Optional.empty();
		});
		assertTrue(this.test.getResearchOrganizationByName(null).isEmpty());
		assertTrue(this.test.getResearchOrganizationByName("").isEmpty());
		assertSame(this.orga0, this.test.getResearchOrganizationByName("N1").get());
		assertSame(this.orga1, this.test.getResearchOrganizationByName("N2").get());
		assertSame(this.orga2, this.test.getResearchOrganizationByName("N3").get());
		assertSame(this.orga3, this.test.getResearchOrganizationByName("N4").get());
		assertTrue(this.test.getResearchOrganizationByName("N5").isEmpty());
	}

	@Test
	public void createResearchOrganization() throws Exception {
		List<Long> addrs = Collections.emptyList();
		Set<OrganizationAddress> raddrs = Collections.emptySet();
		final Optional<ResearchOrganization> res = this.test.createResearchOrganization(true, "NA", "NN", true, "NRNSR", "NNI", "ND",
				ResearchOrganizationType.FACULTY, "NURL", CountryCode.GERMANY, addrs, Collections.emptySet(), null);
		assertNotNull(res);
		assertNotNull(res.get());

		final ArgumentCaptor<ResearchOrganization> arg = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, only()).save(arg.capture());
		final ResearchOrganization actual = arg.getValue();
		assertNotNull(actual);
		assertTrue(actual.isValidated());
		assertEquals("NA", actual.getAcronym());
		assertEquals("NN", actual.getName());
		assertTrue(actual.isMajorOrganization());
		assertEquals("NNI", actual.getNationalIdentifier());
		assertEquals("NRNSR", actual.getRnsr());
		assertEquals("ND", actual.getDescription());
		assertSame(ResearchOrganizationType.FACULTY, actual.getType());
		assertEquals("NURL", actual.getOrganizationURL());
		assertSame(CountryCode.GERMANY, actual.getCountry());
	}

	@Test
	public void updateResearchOrganization() throws Exception {
		List<Long> addrs = Collections.emptyList();
		Set<OrganizationAddress> raddrs = Collections.emptySet();
		Optional<ResearchOrganization> res = this.test.updateResearchOrganization(234l, true, "NA", "NN", true, "NRNSR", "NNI", "ND",
				ResearchOrganizationType.FACULTY, "NURL", CountryCode.GERMANY, addrs, Collections.emptySet(), null, false);
		assertNotNull(res);
		assertNotNull(res.get());

		final ArgumentCaptor<Long> arg0 = ArgumentCaptor.forClass(Long.class);
		verify(this.organizationRepository, atLeastOnce()).findById(arg0.capture());
		Long actual0 = arg0.getValue();
		assertNotNull(actual0);
		assertEquals(234, actual0);

		final ArgumentCaptor<ResearchOrganization> arg1 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, atLeastOnce()).save(arg1.capture());
		final ResearchOrganization actual1 = arg1.getValue();
		assertSame(this.orga1, actual1);

		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);
		final ArgumentCaptor<ResearchOrganizationType> arg3 = ArgumentCaptor.forClass(ResearchOrganizationType.class);
		final ArgumentCaptor<CountryCode> arg4 = ArgumentCaptor.forClass(CountryCode.class);

		verify(this.orga1, atLeastOnce()).setValidated(eq(true));

		verify(this.orga1, atLeastOnce()).setAcronym(arg2.capture());
		assertEquals("NA", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setName(arg2.capture());
		assertEquals("NN", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setMajorOrganization(eq(true));

		verify(this.orga1, atLeastOnce()).setRnsr(arg2.capture());
		assertEquals("NRNSR", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setNationalIdentifier(arg2.capture());
		assertEquals("NNI", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setDescription(arg2.capture());
		assertEquals("ND", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setDescription(arg2.capture());
		assertEquals("ND", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setType(arg3.capture());
		assertEquals(ResearchOrganizationType.FACULTY, arg3.getValue());

		verify(this.orga1, atLeastOnce()).setOrganizationURL(arg2.capture());
		assertEquals("NURL", arg2.getValue());

		verify(this.orga1, atLeastOnce()).setCountry(arg4.capture());
		assertEquals(CountryCode.GERMANY, arg4.getValue());
	}

	@Test
	public void linkSubOrganization() {
		final Set<ResearchOrganization> suborgas = new HashSet<>();
		when(this.orga1.getSubOrganizations()).thenReturn(suborgas);

		final boolean r = this.test.linkSubOrganization(234, 456);
		assertTrue(r);

		verify(this.orga3).getSuperOrganizations();

		assertEquals(1, suborgas.size());
		assertTrue(suborgas.contains(this.orga3));

		final ArgumentCaptor<ResearchOrganization> arg1 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, atLeast(2)).save(arg1.capture());
		final ResearchOrganization actual0 = arg1.getAllValues().get(0);
		final ResearchOrganization actual1 = arg1.getAllValues().get(1);
		assertSame(this.orga3, actual0);
		assertSame(this.orga1, actual1);
	}

	@Test
	public void linkSubOrganization_invalidSub() {
		final boolean r = this.test.linkSubOrganization(234, 1);
		assertFalse(r);
	}

	@Test
	public void linkSubOrganization_invalidSup() {
		final boolean r = this.test.linkSubOrganization(1, 234);
		assertFalse(r);
	}

	@Test
	public void linkSubOrganization_invalidBoth() {
		final boolean r = this.test.linkSubOrganization(1, 2);
		assertFalse(r);
	}

	@Test
	public void linkSubOrganization_same() {
		final boolean r = this.test.linkSubOrganization(234, 234);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization() {
		final Set<ResearchOrganization> suborgas = new HashSet<>();
		when(this.orga1.getSubOrganizations()).thenReturn(suborgas);
		suborgas.add(this.orga3);

		final boolean r = this.test.unlinkSubOrganization(234, 456);

		assertTrue(r);

		verify(this.orga3).getSuperOrganizations();

		assertTrue(suborgas.isEmpty());

		final ArgumentCaptor<ResearchOrganization> arg1 = ArgumentCaptor.forClass(ResearchOrganization.class);
		verify(this.organizationRepository, atLeast(2)).save(arg1.capture());
		final ResearchOrganization actual0 = arg1.getAllValues().get(0);
		final ResearchOrganization actual1 = arg1.getAllValues().get(1);
		assertSame(this.orga1, actual0);
		assertSame(this.orga3, actual1);
	}

	@Test
	public void unlinkSubOrganization_invalidSup() {
		final boolean r = this.test.unlinkSubOrganization(1, 456);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization_invalidBoth() {
		final boolean r = this.test.unlinkSubOrganization(1, 2);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization_same() {
		final boolean r = this.test.unlinkSubOrganization(456, 456);
		assertFalse(r);
	}

	@Test
	public void unlinkSubOrganization_validButNotLinked() {
		final Set<ResearchOrganization> suborgas = new HashSet<>();
		when(this.orga1.getSubOrganizations()).thenReturn(suborgas);
		final boolean r = this.test.unlinkSubOrganization(234, 456);
		assertFalse(r);
	}

}
