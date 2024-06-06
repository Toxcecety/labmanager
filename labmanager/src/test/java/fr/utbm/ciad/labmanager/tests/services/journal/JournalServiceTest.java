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

package fr.utbm.ciad.labmanager.tests.services.journal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyFloat;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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

import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.journal.JournalQualityAnnualIndicators;
import fr.utbm.ciad.labmanager.data.journal.JournalQualityAnnualIndicatorsRepository;
import fr.utbm.ciad.labmanager.data.journal.JournalRepository;
import fr.utbm.ciad.labmanager.data.publication.AbstractJournalBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.JournalBasedPublication;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaper;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaperRepository;
import fr.utbm.ciad.labmanager.services.journal.JournalService;
import fr.utbm.ciad.labmanager.tests.utils.TestUtils;
import fr.utbm.ciad.labmanager.utils.io.network.DirectNetConnection;
import fr.utbm.ciad.labmanager.utils.io.network.NetConnection;
import fr.utbm.ciad.labmanager.utils.io.scimago.OnlineScimagoPlatform;
import fr.utbm.ciad.labmanager.utils.io.scimago.ScimagoPlatform;
import fr.utbm.ciad.labmanager.utils.io.wos.WebOfSciencePlatform;
import fr.utbm.ciad.labmanager.utils.names.JournalNameAndPublisherComparator;
import fr.utbm.ciad.labmanager.utils.ranking.QuartileRanking;
import org.arakhne.afc.progress.DefaultProgression;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIf;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;

/** Tests for {@link JournalService}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class JournalServiceTest {

	private SessionFactory sessionFactory;
	
	private Journal jour0;

	private Journal jour1;

	private Journal jour2;

	private Journal jour3;

	private MessageSourceAccessor messages;

	private JournalRepository journalRepository;

	private JournalQualityAnnualIndicatorsRepository indicatorRepository;

	private JournalPaperRepository publicationRepository;

	private ScimagoPlatform scimago;

	private WebOfSciencePlatform wos;

	private NetConnection netConnection;

	private JournalNameAndPublisherComparator journalNameAndPublisherComparator;

	private JournalService test;

	@BeforeEach
	public void setUp() {
		this.sessionFactory = mock(SessionFactory.class);
		this.messages = mock(MessageSourceAccessor.class);
		this.journalRepository = mock(JournalRepository.class);
		this.publicationRepository = mock(JournalPaperRepository.class);
		this.indicatorRepository = mock(JournalQualityAnnualIndicatorsRepository.class);
		this.scimago = mock(ScimagoPlatform.class);
		this.wos = mock(WebOfSciencePlatform.class);
		this.netConnection = mock(NetConnection.class);
		this.journalNameAndPublisherComparator = mock(JournalNameAndPublisherComparator.class);
		this.test = new JournalService(this.journalRepository,
				this.indicatorRepository, this.publicationRepository, this.scimago, this.wos, this.netConnection, this.journalNameAndPublisherComparator,
				this.messages, new Constants(), this.sessionFactory);

		// Prepare some journals to be inside the repository
		// The lenient configuration is used to configure the mocks for all the tests
		// at the same code location for configuration simplicity
		this.jour0 = mock(Journal.class);
		lenient().when(this.jour0.getId()).thenReturn(123l);
		lenient().when(this.jour0.getJournalName()).thenReturn("N1");
		this.jour1 = mock(Journal.class);
		lenient().when(this.jour1.getId()).thenReturn(234l);
		lenient().when(this.jour1.getJournalName()).thenReturn("N2");
		this.jour2 = mock(Journal.class);
		lenient().when(this.jour2.getId()).thenReturn(345l);
		lenient().when(this.jour2.getJournalName()).thenReturn("N3");
		this.jour3 = mock(Journal.class);
		lenient().when(this.jour3.getId()).thenReturn(456l);
		lenient().when(this.jour3.getJournalName()).thenReturn("N4");

		lenient().when(this.journalRepository.findAll()).thenReturn(
				Arrays.asList(this.jour0, this.jour1, this.jour2, this.jour3));
		lenient().when(this.journalRepository.findById(anyLong())).then(it -> {
			var n = ((Number) it.getArgument(0)).longValue();
			if (n == 123l) {
				return Optional.of(this.jour0);
			} else if (n == 234) {
				return Optional.of(this.jour1);
			} else if (n == 345) {
				return Optional.of(this.jour2);
			} else if (n == 456l) {
				return Optional.of(this.jour3);
			}
			return Optional.empty();
		});
		lenient().when(this.journalRepository.findDistinctByJournalName(anyString())).then(it -> {
			switch (it.getArgument(0).toString()) {
			case "N1":
				return Collections.singleton(this.jour0);
			case "N2":
				return Collections.singleton(this.jour1);
			case "N3":
				return Collections.singleton(this.jour2);
			case "N4":
				return Collections.singleton(this.jour3);
			}
			return Collections.emptySet();
		});
	}

	@Test
	public void getAllJournals() {
		final List<Journal> list = this.test.getAllJournals();
		assertNotNull(list);
		assertEquals(4, list.size());
		assertSame(this.jour0, list.get(0));
		assertSame(this.jour1, list.get(1));
		assertSame(this.jour2, list.get(2));
		assertSame(this.jour3, list.get(3));
	}

	@Test
	public void getJournalById() {
		assertNull(this.test.getJournalById(-4756));
		assertNull(this.test.getJournalById(0));
		assertSame(this.jour0, this.test.getJournalById(123));
		assertSame(this.jour1, this.test.getJournalById(234));
		assertSame(this.jour2, this.test.getJournalById(345));
		assertSame(this.jour3, this.test.getJournalById(456));
		assertNull(this.test.getJournalById(7896));
	}

	@Test
	public void getJournalByName() {
		assertNull(this.test.getJournalByName(null));
		assertNull(this.test.getJournalByName(""));
		assertSame(this.jour0, this.test.getJournalByName("N1"));
		assertSame(this.jour1, this.test.getJournalByName("N2"));
		assertSame(this.jour2, this.test.getJournalByName("N3"));
		assertSame(this.jour3, this.test.getJournalByName("N4"));
		assertNull(this.test.getJournalByName("N5"));
	}

	@Test
	public void getJournalIdByName() {
		assertEquals(0, this.test.getJournalIdByName(null));
		assertEquals(0, this.test.getJournalIdByName(""));
		assertEquals(123, this.test.getJournalIdByName("N1"));
		assertEquals(234, this.test.getJournalIdByName("N2"));
		assertEquals(345, this.test.getJournalIdByName("N3"));
		assertEquals(456, this.test.getJournalIdByName("N4"));
		assertEquals(0, this.test.getJournalIdByName("N5"));
	}

	@Test
	public void createJournal() {
		final Journal journal = this.test.createJournal(true, "NN", "NA", "NP", "NISBN", "NISSN", Boolean.TRUE, "NURL", "NSCI", "NSC", "NWOS", "NWC");
		
		assertNotNull(journal);

		final ArgumentCaptor<Journal> arg = ArgumentCaptor.forClass(Journal.class);
		verify(this.journalRepository, only()).save(arg.capture());
		final Journal actual = arg.getValue();
		assertNotNull(actual);
		assertSame(journal, actual);
		assertTrue(actual.isValidated());
		assertEquals("NN", actual.getJournalName());
		assertEquals("NA", actual.getAddress());
		assertEquals("NP", actual.getPublisher());
		assertEquals("NISBN", actual.getISBN());
		assertEquals("NISSN", actual.getISSN());
		assertTrue(actual.getOpenAccess());
		assertEquals("NURL", actual.getJournalURL());
		assertEquals("NSCI", actual.getScimagoId());
		assertEquals("NSC", actual.getScimagoCategory());
		assertEquals("NWOS", actual.getWosId());
		assertEquals("NWC", actual.getWosCategory());
	}

	@Test
	public void removeJournal() throws Exception {
		this.test.removeJournal(234);

		final ArgumentCaptor<Long> arg = ArgumentCaptor.forClass(Long.class);

		verify(this.journalRepository, atLeastOnce()).findById(arg.capture());
		Long actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);

		verify(this.journalRepository, atLeastOnce()).deleteById(arg.capture());
		actual = arg.getValue();
		assertNotNull(actual);
		assertEquals(234, actual);
	}

	@Test
	public void updateJournal() {
		this.test.updateJournal(234, true, "NN", "NA", "NP", "NISBN", "NISSN", Boolean.TRUE, "NURL", "NSCI", "NSC", "NWOS", "NWC");

		final ArgumentCaptor<Long> arg0 = ArgumentCaptor.forClass(Long.class);
		verify(this.journalRepository, atLeastOnce()).findById(arg0.capture());
		Long actual0 = arg0.getValue();
		assertNotNull(actual0);
		assertEquals(234, actual0);

		final ArgumentCaptor<Journal> arg1 = ArgumentCaptor.forClass(Journal.class);
		verify(this.journalRepository, atLeastOnce()).save(arg1.capture());
		final Journal actual1 = arg1.getValue();
		assertSame(this.jour1, actual1);

		final ArgumentCaptor<String> arg2 = ArgumentCaptor.forClass(String.class);

		verify(this.jour1, atLeastOnce()).setValidated(eq(true));
		verify(this.jour1, atLeastOnce()).setJournalName(eq("NN"));
		verify(this.jour1, atLeastOnce()).setAddress(eq("NA"));
		verify(this.jour1, atLeastOnce()).setPublisher(eq("NP"));
		verify(this.jour1, atLeastOnce()).setISBN(eq("NISBN"));
		verify(this.jour1, atLeastOnce()).setISSN(eq("NISSN"));
		verify(this.jour1, atLeastOnce()).setOpenAccess(eq(Boolean.TRUE));
		verify(this.jour1, atLeastOnce()).setJournalURL(eq("NURL"));
		verify(this.jour1, atLeastOnce()).setScimagoId(eq("NSCI"));
		verify(this.jour1, atLeastOnce()).setScimagoCategory(eq("NSC"));
		verify(this.jour1, atLeastOnce()).setWosId(eq("NWOS"));
		verify(this.jour1, atLeastOnce()).setWosCategory(eq("NWC"));
	}

	@Test
	public void linkPaper() {
		final Set<AbstractJournalBasedPublication> papers = new HashSet<>();
		when(this.jour1.getPublishedPapers()).thenReturn(papers);
		final JournalPaper paper = mock(JournalPaper.class);
		when(this.publicationRepository.findById(anyLong())).then(it -> {
			if (((Number) it.getArgument(0)).longValue() == 1025l) {
				return Optional.of(paper);
			}
			return Optional.empty();
		});

		final boolean r = this.test.linkPaper(234, 1025);
		assertTrue(r);

		final ArgumentCaptor<Journal> arg0 = ArgumentCaptor.forClass(Journal.class);
		verify(paper, atLeastOnce()).setJournal(arg0.capture());
		assertSame(this.jour1, arg0.getValue());

		assertEquals(1, papers.size());
		assertTrue(papers.contains(paper));

		final ArgumentCaptor<Journal> arg1 = ArgumentCaptor.forClass(Journal.class);
		verify(this.journalRepository, atLeastOnce()).save(arg1.capture());
		final Journal actual0 = arg1.getValue();
		assertSame(this.jour1, actual0);

		final ArgumentCaptor<JournalPaper> arg2 = ArgumentCaptor.forClass(JournalPaper.class);
		verify(this.publicationRepository, atLeastOnce()).save(arg2.capture());
		final JournalPaper actual1 = arg2.getValue();
		assertSame(paper, actual1);
	}

	@Test
	public void linkPaper_invalidJournal() {
		final boolean r = this.test.linkPaper(1, 1025);
		assertFalse(r);
	}

	@Test
	public void linkPaper_invalidPaper() {
		final boolean r = this.test.linkPaper(234, 1);
		assertFalse(r);
	}

	@Test
	public void linkPaper_invalidBoth() {
		final boolean r = this.test.linkPaper(1, 2);
		assertFalse(r);
	}

	@Test
	public void unlinkPaper_Int() {
		final JournalPaper paper = mock(JournalPaper.class);
		when(paper.getJournal()).thenReturn(this.jour1);
		when(this.publicationRepository.findById(anyLong())).then(it -> {
			if (((Number) it.getArgument(0)).longValue() == 1025l) {
				return Optional.of(paper);
			}
			return Optional.empty();
		});

		final Set<AbstractJournalBasedPublication> papers = new HashSet<>();
		when(this.jour1.getPublishedPapers()).thenReturn(papers);
		papers.add(paper);

		final boolean r = this.test.unlinkPaper(1025);
		assertTrue(r);

		final ArgumentCaptor<Journal> arg0 = ArgumentCaptor.forClass(Journal.class);
		verify(paper, atLeastOnce()).setJournal(arg0.capture());
		assertNull(arg0.getValue());

		assertTrue(papers.isEmpty());

		final ArgumentCaptor<Journal> arg1 = ArgumentCaptor.forClass(Journal.class);
		verify(this.journalRepository, atLeastOnce()).save(arg1.capture());
		final Journal actual0 = arg1.getValue();
		assertSame(this.jour1, actual0);

		final ArgumentCaptor<JournalPaper> arg2 = ArgumentCaptor.forClass(JournalPaper.class);
		verify(this.publicationRepository, atLeastOnce()).save(arg2.capture());
		final JournalPaper actual1 = arg2.getValue();
		assertSame(paper, actual1);
	}

	@Test
	public void unlinkPaper_Int_invalidId() {
		final boolean r = this.test.unlinkPaper(14587);
		assertFalse(r);
	}

	@Test
	public void unlinkPaper_Int_notLinked() {
		final JournalPaper paper = mock(JournalPaper.class);
		when(this.publicationRepository.findById(anyLong())).then(it -> {
			if (((Number) it.getArgument(0)).longValue() == 1025l) {
				return Optional.of(paper);
			}
			return Optional.empty();
		});

		final boolean r = this.test.unlinkPaper(1025);
		assertFalse(r);
	}

	@Test
	protected void unlinkPaper_IntBoolean_trueSecondArgument() {
		final JournalPaper paper = mock(JournalPaper.class);
		when(paper.getJournal()).thenReturn(this.jour1);
		when(this.publicationRepository.findById(anyLong())).then(it -> {
			if (((Number) it.getArgument(0)).longValue() == 1025l) {
				return Optional.of(paper);
			}
			return Optional.empty();
		});

		final Set<AbstractJournalBasedPublication> papers = new HashSet<>();
		when(this.jour1.getPublishedPapers()).thenReturn(papers);
		papers.add(paper);

		final boolean r = this.test.unlinkPaper(1025, true);
		assertTrue(r);

		final ArgumentCaptor<Journal> arg0 = ArgumentCaptor.forClass(Journal.class);
		verify(paper, atLeastOnce()).setJournal(arg0.capture());
		assertNull(arg0.getValue());

		assertTrue(papers.isEmpty());

		final ArgumentCaptor<Journal> arg1 = ArgumentCaptor.forClass(Journal.class);
		verify(this.journalRepository, atLeastOnce()).save(arg1.capture());
		final Journal actual0 = arg1.getValue();
		assertSame(this.jour1, actual0);

		final ArgumentCaptor<JournalPaper> arg2 = ArgumentCaptor.forClass(JournalPaper.class);
		verify(this.publicationRepository, atLeastOnce()).save(arg2.capture());
		final JournalPaper actual1 = arg2.getValue();
		assertSame(paper, actual1);
	}

	@Test
	protected void unlinkPaper_IntBoolean_falseSecondArgument() {
		final JournalPaper paper = mock(JournalPaper.class);
		when(paper.getJournal()).thenReturn(this.jour1);
		when(this.publicationRepository.findById(anyLong())).then(it -> {
			if (((Long) it.getArgument(0)).longValue() == 1025l) {
				return Optional.of(paper);
			}
			return Optional.empty();
		});

		final Set<AbstractJournalBasedPublication> papers = new HashSet<>();
		when(this.jour1.getPublishedPapers()).thenReturn(papers);
		papers.add(paper);

		final boolean r = this.test.unlinkPaper(1025, false);
		assertTrue(r);

		final ArgumentCaptor<Journal> arg0 = ArgumentCaptor.forClass(Journal.class);
		verify(paper, atLeastOnce()).setJournal(arg0.capture());
		assertNull(arg0.getValue());

		assertTrue(papers.isEmpty());

		final ArgumentCaptor<Journal> arg1 = ArgumentCaptor.forClass(Journal.class);
		verify(this.journalRepository, never()).save(arg1.capture());

		final ArgumentCaptor<JournalPaper> arg2 = ArgumentCaptor.forClass(JournalPaper.class);
		verify(this.publicationRepository, never()).save(arg2.capture());
	}

	@Test
	public void downloadScimagoQuartileByJournalId_noId() throws Exception {
		final QuartileRanking r = this.test.downloadScimagoQuartileByJournalId(234, new DefaultProgression());
		assertNull(r);
	}

	@Test
	public void downloadScimagoQuartileByJournalId_Q1() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("a40000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour1.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournalId(234, new DefaultProgression());
		assertSame(QuartileRanking.Q1, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournalId_Q2() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("e80000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour1.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournalId(234, new DefaultProgression());
		assertSame(QuartileRanking.Q2, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournalId_Q3() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("fb0000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour1.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournalId(234, new DefaultProgression());
		assertSame(QuartileRanking.Q3, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournalId_Q4() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("dd0000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour1.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournalId(234, new DefaultProgression());
		assertSame(QuartileRanking.Q4, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournalId_noQ() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("000000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour1.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournalId(234, new DefaultProgression());
		assertNull(r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournal_nullJournal() throws Exception {
		final QuartileRanking r = this.test.downloadScimagoQuartileByJournal(null, new DefaultProgression());
		assertNull(r);
	}

	@Test
	public void downloadScimagoQuartileByJournal_Q1() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("a40000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour3.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournal(this.jour3, new DefaultProgression());
		assertSame(QuartileRanking.Q1, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournal_Q2() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("e80000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour3.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournal(this.jour3, new DefaultProgression());
		assertSame(QuartileRanking.Q2, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournal_Q3() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("fb0000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour3.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournal(this.jour3, new DefaultProgression());
		assertSame(QuartileRanking.Q3, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournal_Q4() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("dd0000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour3.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournal(this.jour3, new DefaultProgression());
		assertSame(QuartileRanking.Q4, r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	@Test
	public void downloadScimagoQuartileByJournal_noQ() throws Exception {
		when(this.scimago.getJournalPictureUrl(any())).thenReturn(new URL("https://www.scimagojr.com/journal_img.php?id=xyz"));
		final int col = Integer.parseInt("000000", 16);
		final BufferedImage img = mock(BufferedImage.class);
		doReturn(col).when(img).getRGB(anyInt(), anyInt());
		when(this.netConnection.getImageFromURL(any())).thenReturn(img);

		when(this.jour3.getScimagoId()).thenReturn("xyz");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournal(this.jour3, new DefaultProgression());
		assertNull(r);

		final ArgumentCaptor<URL> arg0 = ArgumentCaptor.forClass(URL.class);
		verify(this.netConnection, atLeastOnce()).getImageFromURL(arg0.capture());
		assertEquals("https://www.scimagojr.com/journal_img.php?id=xyz", arg0.getValue().toExternalForm());
	}

	private static boolean isNetworkEnable() {
		return TestUtils.isNetworkEnable();
	}

	@Test
	@EnabledIf("isNetworkEnable")
	public void downloadScimagoQuartileByJournal_fromInternet() throws Exception {
		// Force the connection to Internet
		this.scimago = new OnlineScimagoPlatform();
		this.netConnection = new DirectNetConnection();
		this.test = new JournalService(this.journalRepository, this.indicatorRepository,
				this.publicationRepository, this.scimago, this.wos, this.netConnection, this.journalNameAndPublisherComparator,
				this.messages, new Constants(), this.sessionFactory);

		// The following id is for the Int. Journal of Artificial Intelligence
		when(this.jour3.getScimagoId()).thenReturn("23675");

		final QuartileRanking r = this.test.downloadScimagoQuartileByJournal(this.jour3, new DefaultProgression());
		assertSame(QuartileRanking.Q1, r);
	}

	@Test
	public void setQualityIndicators() throws Exception {
		Journal journal = mock(Journal.class);
		JournalQualityAnnualIndicators indicators = mock(JournalQualityAnnualIndicators.class);
		when(journal.setImpactFactorByYear(anyInt(), anyFloat())).thenReturn(indicators);
		this.test.setQualityIndicators(journal, 2022, 123.456f, QuartileRanking.Q2, QuartileRanking.Q3);
		verify(journal).setImpactFactorByYear(eq(2022), eq(123.456f));
		verify(indicators).setScimagoQIndex(same(QuartileRanking.Q2));
		verify(indicators).setWosQIndex(same(QuartileRanking.Q3));
		verify(this.journalRepository).save(same(journal));
		verify(this.indicatorRepository).save(same(indicators));
	}

	@Test
	public void deleteQualityIndicators() throws Exception {
		Journal journal = mock(Journal.class);
		JournalQualityAnnualIndicators inds = mock(JournalQualityAnnualIndicators.class);
		Map<Integer, JournalQualityAnnualIndicators> indicators = new HashMap<>();
		indicators.put(2022, inds);
		when(journal.getQualityIndicators()).thenReturn(indicators);
		
		this.test.deleteQualityIndicators(journal, 2022);
		
		assertTrue(indicators.isEmpty());
		verify(this.journalRepository).save(same(journal));
		verify(this.indicatorRepository).delete(same(inds));
	}

}
