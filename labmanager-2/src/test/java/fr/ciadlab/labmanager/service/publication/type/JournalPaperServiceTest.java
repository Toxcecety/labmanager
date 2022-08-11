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

package fr.ciadlab.labmanager.service.publication.type;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;

import java.sql.Date;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import fr.ciadlab.labmanager.entities.publication.Publication;
import fr.ciadlab.labmanager.entities.publication.PublicationLanguage;
import fr.ciadlab.labmanager.entities.publication.PublicationType;
import fr.ciadlab.labmanager.entities.publication.type.JournalPaper;
import fr.ciadlab.labmanager.repository.publication.type.JournalPaperRepository;
import fr.ciadlab.labmanager.utils.files.DownloadableFileManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

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

	private DownloadableFileManager downloadableFileManager;

	private JournalPaperRepository repository;

	private JournalPaperService test;

	@BeforeEach
	public void setUp() {
		this.downloadableFileManager = mock(DownloadableFileManager.class);
		this.repository = mock(JournalPaperRepository.class);
		this.test = new JournalPaperService(this.downloadableFileManager, this.repository);

		// Prepare some publications to be inside the repository
		// The lenient configuration is used to configure the mocks for all the tests
		// at the same code location for configuration simplicity
		this.pub0 = mock(JournalPaper.class);
		lenient().when(this.pub0.getId()).thenReturn(123);
		this.pub1 = mock(JournalPaper.class);
		lenient().when(this.pub1.getId()).thenReturn(234);
		this.pub2 = mock(JournalPaper.class);
		lenient().when(this.pub2.getId()).thenReturn(345);

		lenient().when(this.repository.findAll()).thenReturn(
				Arrays.asList(this.pub0, this.pub1, this.pub2));
		lenient().when(this.repository.findById(anyInt())).thenAnswer(it -> {
			switch (((Integer) it.getArgument(0)).intValue()) {
			case 123:
				return Optional.of(this.pub0);
			case 234:
				return Optional.of(this.pub1);
			case 345:
				return Optional.of(this.pub2);
			}
			return Optional.empty();
		});

		this.base = mock(Publication.class);
		lenient().when(this.base.getId()).thenReturn(4567);
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
		final JournalPaper actual = this.test.createJournalPaper(pub0,
				"volume0", "number0", "pages0", "series0");

		assertEquals("volume0", actual.getVolume());
		assertEquals("number0", actual.getNumber());
		assertEquals("pages0", actual.getPages());
		assertEquals("series0", actual.getSeries());

		verify(this.repository).save(actual);
	}

	@Test
	public void updateJournalPaper() {
		this.test.updateJournalPaper(234,
				"title0", PublicationType.ARTISTIC_PRODUCTION, Date.valueOf("2022-07-22"), "abstractText0",
				"keywords0", "doi0", "isbn0", "issn0", "dblpUrl0", "extraUrl0",
				PublicationLanguage.ITALIAN, "pdfContent0", "awardContent0", "pathToVideo0",
				"volume0", "number0", "pages0", "series0");

		verifyNoInteractions(this.pub0);

		verify(this.pub1).setVolume("volume0");
		verify(this.pub1).setNumber("number0");
		verify(this.pub1).setPages("pages0");
		verify(this.pub1).setSeries("series0");

		verifyNoInteractions(this.pub2);
	}

	@Test
	public void removeJournalPaper() {
		this.test.removeJournalPaper(345);

		verify(this.repository).deleteById(345);
	}

}
