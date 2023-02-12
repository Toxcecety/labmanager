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

package fr.ciadlab.labmanager.service.conference;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.awt.image.BufferedImage;
import java.net.URL;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import fr.ciadlab.labmanager.configuration.Constants;
import fr.ciadlab.labmanager.entities.conference.Conference;
import fr.ciadlab.labmanager.entities.conference.ConferenceQualityAnnualIndicators;
import fr.ciadlab.labmanager.entities.journal.Journal;
import fr.ciadlab.labmanager.entities.journal.JournalQualityAnnualIndicators;
import fr.ciadlab.labmanager.entities.publication.type.JournalPaper;
import fr.ciadlab.labmanager.io.core.CorePortal;
import fr.ciadlab.labmanager.io.scimago.OnlineScimagoPlatform;
import fr.ciadlab.labmanager.io.scimago.ScimagoPlatform;
import fr.ciadlab.labmanager.io.wos.WebOfSciencePlatform;
import fr.ciadlab.labmanager.repository.conference.ConferenceQualityAnnualIndicatorsRepository;
import fr.ciadlab.labmanager.repository.conference.ConferenceRepository;
import fr.ciadlab.labmanager.repository.journal.JournalQualityAnnualIndicatorsRepository;
import fr.ciadlab.labmanager.repository.journal.JournalRepository;
import fr.ciadlab.labmanager.repository.publication.type.JournalPaperRepository;
import fr.ciadlab.labmanager.utils.TestUtils;
import fr.ciadlab.labmanager.utils.net.DirectNetConnection;
import fr.ciadlab.labmanager.utils.net.NetConnection;
import fr.ciadlab.labmanager.utils.ranking.CoreRanking;
import fr.ciadlab.labmanager.utils.ranking.QuartileRanking;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;

/** Tests for {@link ConferenceService}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class ConferenceServiceTest {

	private MessageSourceAccessor messages;

	private ConferenceRepository conferenceRepository;

	private ConferenceQualityAnnualIndicatorsRepository indicatorRepository;

	private CorePortal core;

	private SessionFactory sessionFactory;

	private ConferenceService test;

	@BeforeEach
	public void setUp() {
		this.messages = mock(MessageSourceAccessor.class);
		this.conferenceRepository = mock(ConferenceRepository.class);
		this.indicatorRepository = mock(ConferenceQualityAnnualIndicatorsRepository.class);
		this.core = mock(CorePortal.class);
		this.sessionFactory = mock(SessionFactory.class);
		Session session = mock(Session.class);
		lenient().when(session.getTransaction()).thenReturn(mock(Transaction.class));
		lenient().when(this.sessionFactory.openSession()).thenReturn(session);
		this.test = new ConferenceService(this.messages, new Constants(), this.conferenceRepository,
				this.indicatorRepository, this.core, this.sessionFactory);
	}

	@Test
	public void getAllConferences() {
		Conference conf0 = mock(Conference.class);
		Conference conf1 = mock(Conference.class);
		Conference conf2 = mock(Conference.class);
		when(this.conferenceRepository.findAll()).thenReturn(Arrays.asList(conf0, conf1, conf2));
		//
		final List<Conference> list = this.test.getAllConferences();
		//
		assertNotNull(list);
		assertEquals(3, list.size());
		assertSame(conf0, list.get(0));
		assertSame(conf1, list.get(1));
		assertSame(conf2, list.get(2));
	}

	@Test
	public void getConferenceById() {
		final Conference conf0 = mock(Conference.class);
		final Conference conf1 = mock(Conference.class);
		final Conference conf2 = mock(Conference.class);
		when(this.conferenceRepository.findById(any(Integer.class))).thenAnswer(it -> {
			switch ((int) it.getArgument(0)) {
			case 123:
				return Optional.of(conf0);
			case 234:
				return Optional.of(conf1);
			case 345:
				return Optional.of(conf2);
			}
			return Optional.empty();
		});
		//
		assertNull(this.test.getConferenceById(-4756));
		assertNull(this.test.getConferenceById(0));
		assertSame(conf0, this.test.getConferenceById(123));
		assertSame(conf1, this.test.getConferenceById(234));
		assertSame(conf2, this.test.getConferenceById(345));
		assertNull(this.test.getConferenceById(7896));
	}

	@Test
	public void getConferenceByName() {
		final Conference conf0 = mock(Conference.class);
		final Conference conf1 = mock(Conference.class);
		final Conference conf2 = mock(Conference.class);
		when(this.conferenceRepository.findByAcronymOrName(anyString())).thenAnswer(it -> {
			switch (it.getArgument(0).toString()) {
			case "N1":
				return Optional.of(conf0);
			case "N2":
				return Optional.of(conf1);
			case "N3":
				return Optional.of(conf2);
			}
			return Optional.empty();
		});
		//
		assertNull(this.test.getConferenceByName(null));
		assertNull(this.test.getConferenceByName(""));
		assertSame(conf0, this.test.getConferenceByName("N1"));
		assertSame(conf1, this.test.getConferenceByName("N2"));
		assertSame(conf2, this.test.getConferenceByName("N3"));
		assertNull(this.test.getConferenceByName("N4"));
	}

	@Test
	public void createConference() {
		final Optional<Conference> conferenceOpt = this.test.createConference(true, "NA", "NN", "NP", "NIB",  "NIS",
				Boolean.TRUE, "NURL", "NCORE");
		assertNotNull(conferenceOpt);
		Conference conference = conferenceOpt.get();
		assertNotNull(conference);

		final ArgumentCaptor<Conference> arg = ArgumentCaptor.forClass(Conference.class);
		verify(this.conferenceRepository, only()).save(arg.capture());
		final Conference actual = arg.getValue();
		assertNotNull(actual);
		assertSame(conference, actual);
		assertTrue(actual.isValidated());
		assertEquals("NA", actual.getAcronym());
		assertEquals("NN", actual.getName());
		assertEquals("NP", actual.getPublisher());
		assertEquals("NIB", actual.getISBN());
		assertEquals("NIS", actual.getISSN());
		assertTrue(actual.getOpenAccess());
		assertEquals("NURL", actual.getConferenceURL());
		assertEquals("NCORE", actual.getCoreId());
	}

	@Test
	public void removeConference() throws Exception {
		final Conference conf1 = mock(Conference.class);
		when(this.conferenceRepository.findById(any(Integer.class))).thenAnswer(it -> {
			switch ((int) it.getArgument(0)) {
			case 234:
				return Optional.of(conf1);
			}
			return Optional.empty();
		});
		//
		this.test.removeConference(234);

		final ArgumentCaptor<Integer> arg = ArgumentCaptor.forClass(Integer.class);

		verify(this.conferenceRepository, atLeastOnce()).findById(arg.capture());
		Integer actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);

		verify(this.conferenceRepository, atLeastOnce()).deleteById(arg.capture());
		actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);
	}

	@Test
	public void updateConference() {
		final Conference conf0 = mock(Conference.class);
		final Conference conf1 = mock(Conference.class);
		final Conference conf2 = mock(Conference.class);
		when(this.conferenceRepository.findById(any(Integer.class))).thenAnswer(it -> {
			switch ((int) it.getArgument(0)) {
			case 234:
				return Optional.of(conf1);
			}
			return Optional.empty();
		});

		this.test.updateConference(234, true, "NA", "NN", "NP", "NIB", "NIS", Boolean.TRUE, "NURL", "NCORE");

		final ArgumentCaptor<Integer> arg0 = ArgumentCaptor.forClass(Integer.class);
		verify(this.conferenceRepository, atLeastOnce()).findById(arg0.capture());
		Integer actual0 = arg0.getValue();
		assertNotNull(actual0);
		assertEquals(234, actual0);

		final ArgumentCaptor<Conference> arg1 = ArgumentCaptor.forClass(Conference.class);
		verify(this.conferenceRepository, atLeastOnce()).save(arg1.capture());
		final Conference actual1 = arg1.getValue();
		assertSame(conf1, actual1);

		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		verify(conf1, atLeastOnce()).setValidated(eq(true));
		verify(conf1, atLeastOnce()).setAcronym(eq("NA"));
		verify(conf1, atLeastOnce()).setName(eq("NN"));
		verify(conf1, atLeastOnce()).setPublisher(eq("NP"));
		verify(conf1, atLeastOnce()).setISBN(eq("NIB"));
		verify(conf1, atLeastOnce()).setISSN(eq("NIS"));
		verify(conf1, atLeastOnce()).setOpenAccess(eq(Boolean.TRUE));
		verify(conf1, atLeastOnce()).setConferenceURL(eq("NURL"));
		verify(conf1, atLeastOnce()).setCoreId(eq("NCORE"));
	}

	@Test
	public void setQualityIndicators() throws Exception {
		Conference conference = mock(Conference.class);
		ConferenceQualityAnnualIndicators indicators = mock(ConferenceQualityAnnualIndicators.class);
		when(conference.setCoreIndexByYear(anyInt(), any(CoreRanking.class))).thenReturn(indicators);
		//
		this.test.setQualityIndicators(conference, 2022, CoreRanking.A_STAR);
		verify(conference).setCoreIndexByYear(eq(2022), same(CoreRanking.A_STAR));
		verify(this.conferenceRepository).save(same(conference));
		verify(this.indicatorRepository).save(same(indicators));
	}

	@Test
	public void deleteQualityIndicators() throws Exception {
		Conference conference = mock(Conference.class);
		ConferenceQualityAnnualIndicators inds = mock(ConferenceQualityAnnualIndicators.class);
		Map<Integer, ConferenceQualityAnnualIndicators> indicators = new HashMap<>();
		indicators.put(2022, inds);
		when(conference.getQualityIndicators()).thenReturn(indicators);
		//
		this.test.deleteQualityIndicators(conference, 2022);
		//
		assertTrue(indicators.isEmpty());
		verify(this.conferenceRepository).save(same(conference));
		verify(this.indicatorRepository).delete(same(inds));
	}

}
