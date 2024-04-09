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

package fr.utbm.ciad.labmanager.tests.services.publication.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.same;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationLanguage;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaper;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaperRepository;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.publication.type.JournalPaperService;
import fr.utbm.ciad.labmanager.utils.doi.DefaultDoiTools;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.io.hal.DefaultHalTools;
import org.hibernate.SessionFactory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.support.MessageSourceAccessor;

/** Tests for {@link JournalPaperService}.
 * 
 * @author $Author: sgalland$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@SuppressWarnings("all")
@ExtendWith(MockitoExtension.class)
public class JournalPaperServiceTest {

	private JournalPaper pub0;

	private JournalPaper pub1;

	private JournalPaper pub2;

	private Publication base;

	private MessageSourceAccessor messages;

	private DownloadableFileManager downloadableFileManager;

	private JournalPaperRepository repository;

	private MembershipService membershipService;

	private JournalPaperService test;

	@BeforeEach
	public void setUp() {
		this.messages = mock(MessageSourceAccessor.class);
		this.downloadableFileManager = mock(DownloadableFileManager.class);
		this.repository = mock(JournalPaperRepository.class);
		this.membershipService = mock(MembershipService.class);
		this.test = new JournalPaperService(this.downloadableFileManager, new DefaultDoiTools(), new DefaultHalTools(), this.repository, this.membershipService, this.messages, new Constants(), mock(SessionFactory.class));

		// Prepare some publications to be inside the repository
		// The lenient configuration is used to configure the mocks for all the tests
		// at the same code location for configuration simplicity
		this.pub0 = mock(JournalPaper.class);
		lenient().when(this.pub0.getId()).thenReturn(123l);
		this.pub1 = mock(JournalPaper.class);
		lenient().when(this.pub1.getId()).thenReturn(234l);
		this.pub2 = mock(JournalPaper.class);
		lenient().when(this.pub2.getId()).thenReturn(345l);

		lenient().when(this.repository.findAll()).thenReturn(
				Arrays.asList(this.pub0, this.pub1, this.pub2));
		lenient().when(this.repository.findById(anyLong())).thenAnswer(it -> {
			var n = ((Number) it.getArgument(0)).longValue();
			if (n == 123l) {
				return Optional.of(this.pub0);
			} else if (n == 234l) {
				return Optional.of(this.pub1);
			} else if (n == 345l) {
				return Optional.of(this.pub2);
			}
			return Optional.empty();
		});

		this.base = mock(Publication.class);
		lenient().when(this.base.getId()).thenReturn(4567l);
	}

	@Test
	public void getAllJournalPapers() {
		final List<JournalPaper> list = this.test.getAllJournalPapers();
		assertNotNull(list);
		assertEquals(3, list.size());
		assertSame(this.pub0, list.get(0));
		assertSame(this.pub1, list.get(1));
		assertSame(this.pub2, list.get(2));
	}

	@Test
	public void getJournalPaper() {
		assertNull(this.test.getJournalPaper(-4756));
		assertNull(this.test.getJournalPaper(0));
		assertSame(this.pub0, this.test.getJournalPaper(123));
		assertSame(this.pub1, this.test.getJournalPaper(234));
		assertSame(this.pub2, this.test.getJournalPaper(345));
		assertNull(this.test.getJournalPaper(7896));
	}

	@Test
	public void createJournalPaper() {
		Journal jour = mock(Journal.class);
		final JournalPaper actual = this.test.createJournalPaper(pub0,
				"volume0", "number0", "pages0", "series0", jour);

		assertEquals("volume0", actual.getVolume());
		assertEquals("number0", actual.getNumber());
		assertEquals("pages0", actual.getPages());
		assertEquals("series0", actual.getSeries());
		assertSame(jour, actual.getJournal());

		verify(this.repository).save(actual);
	}

	@Test
	public void updateJournalPaper() {
		final Journal jour = mock(Journal.class);
		this.test.updateJournalPaper(234,
				"title0", PublicationType.INTERNATIONAL_JOURNAL_PAPER, LocalDate.parse("2022-07-22"), 2022, "abstractText0",
				"keywords0", "doi:doi/0", "hal-123", "dblpUrl0", "extraUrl0",
				PublicationLanguage.ITALIAN, "pdfContent0", "awardContent0", "pathToVideo0",
				"volume0", "number0", "pages0", "series0", jour);

		verifyNoInteractions(this.pub0);

		verify(this.pub1).setVolume(eq("volume0"));
		verify(this.pub1).setNumber(eq("number0"));
		verify(this.pub1).setPages(eq("pages0"));
		verify(this.pub1).setSeries(eq("series0"));
		verify(this.pub1).setJournal(same(jour));

		verifyNoInteractions(this.pub2);
	}

	@Test
	public void removeJournalPaper() {
		this.test.removeJournalPaper(345);

		verify(this.repository).deleteById(345l);
	}

}
