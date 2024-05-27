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

package fr.utbm.ciad.labmanager.services.publication;

import java.io.IOException;
import java.io.Reader;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Pattern;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.conference.Conference;
import fr.utbm.ciad.labmanager.data.conference.ConferenceRepository;
import fr.utbm.ciad.labmanager.data.journal.Journal;
import fr.utbm.ciad.labmanager.data.journal.JournalRepository;
import fr.utbm.ciad.labmanager.data.member.Person;
import fr.utbm.ciad.labmanager.data.member.PersonRepository;
import fr.utbm.ciad.labmanager.data.publication.*;
import fr.utbm.ciad.labmanager.data.publication.type.Book;
import fr.utbm.ciad.labmanager.data.publication.type.BookChapter;
import fr.utbm.ciad.labmanager.data.publication.type.ConferencePaper;
import fr.utbm.ciad.labmanager.data.publication.type.JournalEdition;
import fr.utbm.ciad.labmanager.data.publication.type.JournalPaper;
import fr.utbm.ciad.labmanager.data.publication.type.KeyNote;
import fr.utbm.ciad.labmanager.data.publication.type.MiscDocument;
import fr.utbm.ciad.labmanager.data.publication.type.Patent;
import fr.utbm.ciad.labmanager.data.publication.type.Report;
import fr.utbm.ciad.labmanager.data.publication.type.Thesis;
import fr.utbm.ciad.labmanager.data.scientificaxis.ScientificAxis;
import fr.utbm.ciad.labmanager.services.member.MembershipService;
import fr.utbm.ciad.labmanager.services.member.PersonService;
import fr.utbm.ciad.labmanager.services.publication.type.BookChapterService;
import fr.utbm.ciad.labmanager.services.publication.type.BookService;
import fr.utbm.ciad.labmanager.services.publication.type.ConferencePaperService;
import fr.utbm.ciad.labmanager.services.publication.type.JournalEditionService;
import fr.utbm.ciad.labmanager.services.publication.type.JournalPaperService;
import fr.utbm.ciad.labmanager.services.publication.type.KeyNoteService;
import fr.utbm.ciad.labmanager.services.publication.type.MiscDocumentService;
import fr.utbm.ciad.labmanager.services.publication.type.PatentService;
import fr.utbm.ciad.labmanager.services.publication.type.ReportService;
import fr.utbm.ciad.labmanager.services.publication.type.ThesisService;
import fr.utbm.ciad.labmanager.utils.ComposedException;
import fr.utbm.ciad.labmanager.utils.io.ExporterConfigurator;
import fr.utbm.ciad.labmanager.utils.io.bibtex.BibTeX;
import fr.utbm.ciad.labmanager.utils.io.bibtex.ConferenceFake;
import fr.utbm.ciad.labmanager.utils.io.bibtex.JournalFake;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.io.html.HtmlDocumentExporter;
import fr.utbm.ciad.labmanager.utils.io.json.JsonExporter;
import fr.utbm.ciad.labmanager.utils.io.od.OpenDocumentTextPublicationExporter;
import fr.utbm.ciad.labmanager.utils.io.ris.RIS;
import fr.utbm.ciad.labmanager.utils.names.PersonNameParser;
import jakarta.transaction.Transactional;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.arakhne.afc.progress.DefaultProgression;
import org.arakhne.afc.progress.Progression;
import org.arakhne.afc.vmutil.FileSystem;
import org.eclipse.xtext.xbase.lib.Procedures.Procedure2;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Service for managing the publications.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class PublicationService extends AbstractPublicationService {

	private static final String MESSAGE_PREFIX = "publicationService."; //$NON-NLS-1$

	private PublicationRepository publicationRepository;

	private AuthorshipRepository authorshipRepository;

	private JournalRepository journalRepository;

	private ConferenceRepository conferenceRepository;

	private PersonRepository personRepository;

	private PersonService personService;

	private PersonNameParser nameParser;

	private BibTeX bibtex;

	private RIS ris;

	private HtmlDocumentExporter html;

	private OpenDocumentTextPublicationExporter odt;

	private JsonExporter json;

	private DownloadableFileManager fileManager;

	private PrePublicationFactory prePublicationFactory;

	private MembershipService membershipService;

	private BookService bookService;

	private BookChapterService bookChapterService;

	private ConferencePaperService conferencePaperService;

	private JournalEditionService journalEditionService;

	private JournalPaperService journalPaperService;

	private KeyNoteService keyNoteService;

	private MiscDocumentService miscDocumentService;

	private PatentService patentService;

	private ReportService reportService;

	private ThesisService thesisService;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param publicationRepository the publication repository.
	 * @param prePublicationFactory factory of pre-publications.
	 * @param authorshipService the service for managing the authorships.
	 * @param authorshipRepository authorshipRepository the repository of authorships.
	 * @param personService the service for managing the persons.
	 * @param personRepository the repository of the persons.
	 * @param journalRepository the repository of the journals.
	 * @param conferenceRepository the repository of the conferences.
	 * @param nameParser the parser of person names.
	 * @param bibtex the tool for managing BibTeX source.
	 * @param ris the tool for managing RIS source.
	 * @param html the tool for exporting to HTML.
	 * @param odt the tool for exporting to Open Document Text.
	 * @param json the tool for exporting to JSON.
	 * @param fileManager the manager of downloadable files.
	 * @param membershipService the service for managing memberships.
	 * @param bookService the service for books.
	 * @param bookChapterService the service for book chapters.
	 * @param conferencePaperService the service for conference papers.
	 * @param journalEditionService the service for journal editions.
	 * @param journalPaperService the service for journal papers.
	 * @param keyNoteService the service for key-notes.
	 * @param miscDocumentService the service for misc documents.
	 * @param patentService the service for patents.
	 * @param reportService the service for reports.
	 * @param thesisService the service for theses.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public PublicationService(
			@Autowired PublicationRepository publicationRepository,
			@Autowired PrePublicationFactory prePublicationFactory,
			@Autowired AuthorshipRepository authorshipRepository,
			@Autowired PersonService personService, @Autowired PersonRepository personRepository,
			@Autowired JournalRepository journalRepository,
			@Autowired ConferenceRepository conferenceRepository,
			@Autowired PersonNameParser nameParser,
			@Autowired BibTeX bibtex,
			@Autowired RIS ris,
			@Autowired HtmlDocumentExporter html,
			@Autowired OpenDocumentTextPublicationExporter odt,
			@Autowired JsonExporter json,
			@Autowired DownloadableFileManager fileManager,
			@Autowired MembershipService membershipService,
			@Autowired BookService bookService,
			@Autowired BookChapterService bookChapterService,
			@Autowired ConferencePaperService conferencePaperService,
			@Autowired JournalEditionService journalEditionService,
			@Autowired JournalPaperService journalPaperService,
			@Autowired KeyNoteService keyNoteService,
			@Autowired MiscDocumentService miscDocumentService,
			@Autowired PatentService patentService,
			@Autowired ReportService reportService,
			@Autowired ThesisService thesisService,
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired SessionFactory sessionFactory) {			
		super(messages, constants, sessionFactory);
		this.publicationRepository = publicationRepository;
		this.prePublicationFactory = prePublicationFactory;
		this.authorshipRepository = authorshipRepository;
		this.personRepository = personRepository;
		this.personService = personService;
		this.journalRepository = journalRepository;
		this.conferenceRepository = conferenceRepository;
		this.nameParser = nameParser;
		this.bibtex = bibtex;
		this.ris = ris;
		this.html = html;
		this.odt = odt;
		this.json = json;
		this.fileManager = fileManager;
		this.membershipService = membershipService;
		this.bookService = bookService;
		this.bookChapterService = bookChapterService;
		this.conferencePaperService = conferencePaperService;
		this.journalEditionService = journalEditionService;
		this.journalPaperService = journalPaperService;
		this.keyNoteService = keyNoteService;
		this.miscDocumentService = miscDocumentService;
		this.patentService = patentService;
		this.reportService = reportService;
		this.thesisService = thesisService;
	}

	/** Load the components of the given publications in a JPA session. The loaded components are the authors, the quality indicators for journals or conferences.
	 *
	 * @param publications the publications to load.
	 * @since 4.0
	 */
	@Transactional
	public void loadPublicationsInMemory(Set<Publication> publications) {
		inSession(session -> {
			for (final var publication : publications) {
				if (publication.getId() != 0l) {
					session.load(publication, Long.valueOf(publication.getId()));

					Hibernate.initialize(publication.getAuthorshipsRaw());
					for (final var authorship : publication.getAuthorshipsRaw()) {
						Hibernate.initialize(authorship.getPerson());
						Hibernate.initialize(authorship.getPerson().getMemberships());
					}

					if (publication instanceof JournalBasedPublication paper) {
						Hibernate.initialize(paper.getJournal());
						Hibernate.initialize(paper.getJournal().getQualityIndicators());
					} else if (publication instanceof ConferenceBasedPublication paper) {
						var conf = paper.getConference();
						while (conf != null) {
							Hibernate.initialize(conf);
							Hibernate.initialize(conf.getQualityIndicators());
							conf = conf.getEnclosingConference();
						}
					}
				}
			}
		});
	}

	/** Replies the factory for pre-publications. A pre-publication is a publication in which only the shared properties are set. It does not correspond to a concrete publication instance.
	 *
	 * @return the factory.
	 * @since 4.0
	 */
	public PrePublicationFactory getPrePublicationFactory() {
		return this.prePublicationFactory;
	}
	
	/** Replies all the publications from the database.
	 *
	 * @return the publications.
	 */
	public List<Publication> getAllPublications() {
		return this.publicationRepository.findAll();
	}

	/** Replies all the publications from the database.
	 *
	 * @param filter the filter of publications.
	 * @return the publications.
	 * @since 4.0
	 */
	public List<Publication> getAllPublications(Specification<Publication> filter) {
		return this.publicationRepository.findAll(filter);
	}

	/** Replies all the publications from the database.
	 *
	 * @param filter the filter of publications.
	 * @param sortOrder the order specification to use for sorting the publications.
	 * @return the publications.
	 * @since 4.0
	 */
	public List<Publication> getAllPublications(Specification<Publication> filter, Sort sortOrder) {
		return this.publicationRepository.findAll(filter, sortOrder);
	}

	/** Replies all the publications from the database.
	 *
	 * @param sortOrder the order specification to use for sorting the publications.
	 * @return the publications.
	 * @since 4.0
	 */
	public List<Publication> getAllPublications(Sort sortOrder) {
		return this.publicationRepository.findAll(sortOrder);
	}

	/** Replies all the publications from the database.
	 *
	 * @param pageable the manager of pages.
	 * @return the publications.
	 * @since 4.0
	 */
	public Page<Publication> getAllPublications(Pageable pageable) {
		return this.publicationRepository.findAll(pageable);
	}

	/** Replies all the publications from the database.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of publications.
	 * @return the publications.
	 * @since 4.0
	 */
	public Page<Publication> getAllPublications(Pageable pageable, Specification<Publication> filter) {
		return this.publicationRepository.findAll(filter, pageable);
	}

	/** Replies all the publications from the database.
	 *
	 * @param pageable the manager of pages.
	 * @param filter the filter of publications.
	 * @param callback is invoked on each entity in the context of the JPA session. It may be used for forcing the loading of some lazy-loaded data.
	 * @return the publications.
	 * @since 4.0
	 */
	@Transactional
	public Page<Publication> getAllPublications(Pageable pageable, Specification<Publication> filter, Consumer<Publication> callback) {
		final var page = this.publicationRepository.findAll(filter, pageable);
		if (callback != null) {
			page.forEach(callback);
		}
		return page;
	}

	/** Replies all the publications that have the given maximum age.
	 *
	 * @param maxAge the maximum age of the publications.
	 * @return the publications.
	 */
	public List<Publication> getPublicationsOfAge(int maxAge) {
		final var startDate = LocalDate.of(LocalDate.now().getYear() - maxAge, 1, 1);
		final var endDate = LocalDate.now();
		return this.publicationRepository.findAll().parallelStream()
				.filter(it -> isActiveIn(it, startDate, endDate))
				.collect(Collectors.toList());
	}

	private static boolean isActiveIn(Publication publication, LocalDate windowStart, LocalDate windowEnd) {
		assert windowStart != null;
		assert windowEnd != null;
		final var startYear = windowStart.getYear();
		final var endYear = windowEnd.getYear();
		final var year = publication.getPublicationYear();
		return year >= startYear && year <= endYear;
	}

	/** Replies all the publications from the database that are attached to the given person.
	 *
	 * @param identifier the identifier of the person.
	 * @return the publications.
	 */
	public List<Publication> getPublicationsByPersonId(long identifier) {
		return this.publicationRepository.findAllByAuthorshipsPersonId(identifier);
	}

	/** Replies all the publications from the database that are attached to the person with the given webpage identifier.
	 *
	 * @param identifier the identifier of the person's webpage.
	 * @return the publications.
	 */
	public List<Publication> getPublicationsByPersonWebPageId(String identifier) {
		return this.publicationRepository.findAllByAuthorshipsPersonWebPageId(identifier);
	}

	/** Replies all the publications from the database that are attached to a person involved in the given organization.
	 *
	 * @param identifier the identifier of the organization.
	 * @param includeSubOrganizations indicates if the members of the suborganizations are considered.
	 * @param filterAuthorshipsWithActiveMemberships indicates if the authorships must correspond to active memberships.
	 * @return the publications.
	 */
	public Set<Publication> getPublicationsByOrganizationId(long identifier, boolean includeSubOrganizations,
			boolean filterAuthorshipsWithActiveMemberships) {
		final Set<Person> members;
		if (includeSubOrganizations) {
			members = this.membershipService.getMembersOf(identifier);
		} else {
			members = this.membershipService.getDirectMembersOf(identifier);
		}
		final var identifiers = members.stream().map(it -> Long.valueOf(it.getId())).collect(Collectors.toUnmodifiableSet());
		final var publications = this.publicationRepository.findAllByAuthorshipsPersonIdIn(identifiers);
		if (filterAuthorshipsWithActiveMemberships) {
			return filterPublicationsWithMemberships(publications, identifier, includeSubOrganizations);
		}
		return publications;
	}

	/** Replies the publication with the given identifier.
	 *
	 * @param identifier the identifier of the publication.
	 * @return the publication, or {@code null} if not found.
	 */
	public Publication getPublicationById(long identifier) {
		final var byId = this.publicationRepository.findById(Long.valueOf(identifier));
		return byId.orElse(null);
	}

	/** Replies the publications with the given identifiers.
	 *
	 * @param identifiers the identifiers of the publications.
	 * @return the publications.
	 */
	public List<Publication> getPublicationsByIds(List<Long> identifiers) {
		return this.publicationRepository.findAllById(identifiers);
	}

	/** Replies the publications with the given title.
	 *
	 * @param title the title of the publications.
	 * @return the publications.
	 */
	public List<Publication> getPublicationsByTitle(String title) {
		if (Strings.isNullOrEmpty(title)) {
			return Collections.emptyList();
		}
		return this.publicationRepository.findAllByTitleIgnoreCase(title);
	}

	/** Replies the publications for the given year.
	 *
	 * @param year the year of publication.
	 * @return the list of publications.
	 * @since 3.6
	 */
	public List<Publication> getPublicationsByYear(int year) {
		return this.publicationRepository.findAllByPublicationYear(Integer.valueOf(year));
	}

	/** Replies the authors of the publication with the given identifier.
	 * 
	 * @param publicationId the identifier of the publication.
	 * @return the authors.
	 */
	public List<Person> getAuthorsFor(long publicationId) {
		return this.personRepository.findByAuthorshipsPublicationIdOrderByAuthorshipsAuthorRank(publicationId);
	}

	/** Replies the authorships of the publication with the given identifier.
	 * 
	 * @param publicationId the identifier of the publication.
	 * @return the authorships.
	 */
	public List<Authorship> getAuthorshipsFor(long publicationId) {
		return this.authorshipRepository.findByPublicationId(publicationId);
	}

	/** Replies the count of the publication per year.
	 *
	 * @return the years.
	 *
	 * */
	public List<Long> getCountPublicationsByYear(){
		return this.publicationRepository.countPublicationsByYear();
	}

	/** Replies the years.
	 *
	 * @return the years.
	 *
	 *
	 * */
	public List<Integer> getAllYears(){
		return this.publicationRepository.findDistinctPublicationYears();
	}

	/** Replies the count of the publication per year.
	 *
	 * @return the years.
	 *
	 *
	 */
	public List<Map<String,Long>> getCountPublicationByTypeByYear(PublicationType type){
		return this.publicationRepository.countPublicationsByYearForTypeOrdered(type);
	}

	public List<PublicationType> getAllType(){
		return this.publicationRepository.findAllDistinctPublicationTypes();
	}

	public Integer getCountPublicationByTypeByYearV2(PublicationType type, Integer year){
		return this.publicationRepository.countPublicationsForTypeAndYearV2(type,year);
	}

	public List<String> getAllCategories(){
		List<PublicationType> publicationTypes = getAllType();
		List<String> publicationCategories = new ArrayList<>();
		for(PublicationType publicationType : publicationTypes){
			if(!publicationCategories.contains(publicationType.getCategory(true).toString())){
				publicationCategories.add(publicationType.getCategory(true).toString());
			}
		}
		return publicationCategories;
	}

    /**
     * Link a person and a publication.
     * The person is added at the given position in the list of the authors.
     * If this list contains authors with a rank greater than or equals to the given rank,
     * the ranks of these authors is incremented.
     *
     * @param personId                   the identifier of the person.
     * @param publicationId              the identifier of the publication.
     * @param rank                       the position of the person in the list of authors. To be sure to add the authorship at the end,
     *                                   pass {@link Integer#MAX_VALUE}.
     * @param updateOtherAuthorshipRanks indicates if the authorships ranks are re-arranged in order to be consistent.
     *                                   If it is {@code false}, the given rank as argument is put into the authorship without change.
     * @return the added authorship
     */


    public Authorship addAuthorship(long personId, long publicationId, int rank, boolean updateOtherAuthorshipRanks) {
		final var optPerson = this.personRepository.findById(Long.valueOf(personId));
		if (optPerson.isPresent()) {
			final var optPub = this.publicationRepository.findById(Long.valueOf(publicationId));
			if (optPub.isPresent()) {
				final var publication = optPub.get();
				// No need to add the authorship if the person is already linked to the publication
				final var currentAuthors = publication.getAuthorshipsRaw();
				final var ro = currentAuthors.stream().filter(
						it -> it.getPerson().getId() == personId).findAny();
				if (ro.isEmpty()) {
					final var person = optPerson.get();
					final var authorship = new Authorship();
					authorship.setPerson(person);
					authorship.setPublication(publication);
					final int realRank;
					if (updateOtherAuthorshipRanks) {
						if (rank > currentAuthors.size()) {
							// Insert at the end
							realRank = currentAuthors.size();
						} else {
							// Need to be inserted
							realRank = rank < 0 ? 0 : rank;
							for (final var currentAuthor : currentAuthors) {
								final var orank = currentAuthor.getAuthorRank();
								if (orank >= rank) {
									currentAuthor.setAuthorRank(orank + 1);
									this.authorshipRepository.save(currentAuthor);
								}
							}
						}
					} else {
						realRank = rank;
					}
					authorship.setAuthorRank(realRank);
					currentAuthors.add(authorship);
					publication.getAuthorshipsRaw().add(authorship);
					this.authorshipRepository.save(authorship);
					//this.personRepository.save(person);
					this.publicationRepository.save(publication);
					return authorship;
				}
			}
		}
		return null;
	}

	/** Remove the publication with the given identifier.
	 *
	 * @param identifier the identifier of the publication to remove.
	 * @param removeAssociatedFiles indicates if the associated files (PDF, Award...) should be also deleted.
	 */
	public void removePublication(long identifier, boolean removeAssociatedFiles) {
		final var id = Long.valueOf(identifier);
		final var optPublication = this.publicationRepository.findById(id);
		if (optPublication.isPresent()) {
			final var publication = optPublication.get();
			final var iterator = publication.getAuthorships().iterator();
			while (iterator.hasNext()) {
				var autship = iterator.next();
				final var person = autship.getPerson();
				if (person != null) {
					person.getAuthorships().remove(autship);
					autship.setPerson(null);
					this.personRepository.save(person);
				}
				autship.setPublication(null);
				iterator.remove();
				this.authorshipRepository.save(autship);
			}
			publication.getAuthorshipsRaw().clear();
			publication.setScientificAxes(null);
			this.publicationRepository.deleteById(id);
			if (removeAssociatedFiles) {
				try {
					this.fileManager.deletePublicationPdfFile(identifier);
				} catch (Throwable ex) {
					// Silent
				}
				try {
					this.fileManager.deletePublicationAwardPdfFile(identifier);
				} catch (Throwable ex) {
					// Silent
				}
			}
		}
	}

	/** Remove the publications with the given identifiers.
	 *
	 * @param identifiers the identifiers of the publications to remove.
	 * @param removeAssociatedFiles indicates if the associated files (PDF, Award...) should be also deleted.
	 * @since 2.4
	 */
	@Transactional
	public void removePublications(Collection<Long> identifiers, boolean removeAssociatedFiles) {
		final var publications = this.publicationRepository.findAllByIdIn(identifiers);
		if (!publications.isEmpty()) {
			for (final var publication : publications) {
				final var id = publication.getId();
				final var iterator = publication.getAuthorships().iterator();
				while (iterator.hasNext()) {
					final var autship = iterator.next();
					final var person = autship.getPerson();
					if (person != null) {
						person.getAuthorships().remove(autship);
						autship.setPerson(null);
						this.personRepository.save(person);
					}
					autship.setPublication(null);
					iterator.remove();
					this.authorshipRepository.save(autship);
				}
				publication.getAuthorshipsRaw().clear();
				publication.setScientificAxes(null);
				this.publicationRepository.deleteById(Long.valueOf(id));
				if (removeAssociatedFiles) {
					try {
						this.fileManager.deletePublicationPdfFile(id);
					} catch (Throwable ex) {
						// Silent
					}
					try {
						this.fileManager.deletePublicationAwardPdfFile(id);
					} catch (Throwable ex) {
						// Silent
					}
				}
			}
		}
	}

	/** Remove the publications with the given identifiers.
	 *
	 * @param identifiers the identifiers of the publications to remove.
	 * @since 4.0
	 */
	@Transactional
	public void removePublications(Collection<Long> identifiers) {
		final var publications = this.publicationRepository.findAllByIdIn(identifiers);
		if (!publications.isEmpty()) {
			for (final var publication : publications) {
				final var id = publication.getId();
				final var iterator = publication.getAuthorships().iterator();
				while (iterator.hasNext()) {
					final var autship = iterator.next();
					final var person = autship.getPerson();
					if (person != null) {
						person.getAuthorships().remove(autship);
						autship.setPerson(null);
						this.personRepository.save(person);
					}
					autship.setPublication(null);
					iterator.remove();
					this.authorshipRepository.save(autship);
				}
				publication.getAuthorshipsRaw().clear();
				publication.setScientificAxes(null);
				this.publicationRepository.deleteById(Long.valueOf(id));
			}
		}
	}

	/** Save the given publications into the database.
	 * If one publication has a temporary list of authors, the corresponding authors are
	 * explicitly created into the database.
	 *
	 * @param publications the list of publications to save in the database.
	 */
	public void save(Publication... publications) {
		save(Arrays.asList(publications));
	}

	/** Save the given publications into the database.
	 * If one publication has a temporary list of authors, the corresponding authors are
	 * explicitly created into the database.
	 *
	 * @param publications the list of publications to save in the database.
	 */
	public void save(List<? extends Publication> publications) {
		for (final var publication : publications) {
			save(publication);
		}
	}

	/** Save the given publication into the database.
	 * If publication has a temporary list of authors, the corresponding authors are
	 * explicitly created into the database. However, this function does not manage the update of the author list.
	 *
	 * @param publication publication to save in the database.
	 * @return the instance of the publication after saving. It may differ from the instance that is provided as argument.
	 * @since 4.0
	 */
	public Publication save(Publication publication) {
		// Remove old authorships
		final var authors = publication.getTemporaryAuthors();
		publication.setTemporaryAuthors(null);
		final var newPublication = this.publicationRepository.save(publication);
		if (publication instanceof JournalBasedPublication jpublication) {
			final var jour = jpublication.getJournal();
			if (jour != null) {
				this.journalRepository.save(jour);
			}
		} else if (publication instanceof ConferenceBasedPublication cpublication) {
			final var conf = cpublication.getConference();
			if (conf != null) {
				this.conferenceRepository.save(conf);
			}
		}
		if (authors != null) {
			// Create the list of authors from the temporary (not yet saved) list. 
			var rank = 0;
			for (final var author : authors) {
				this.personRepository.save(author);
				addAuthorship(author.getId(), publication.getId(), rank, false);
				++rank;
			}
		}
		return newPublication;
	}

	/** Parse the given BibTeX file and extract the publications without adding them in the database.
	 * The format of the BibTeX is a standard that is briefly described
	 * on {@link "https://en.wikipedia.org/wiki/BibTeX"}.
	 * If multiple BibTeX entries are defined into the given input string, each of them is subject
	 * of an exportation tentative.
	 *
	 * @param bibtex the stream that contains the BibTeX description of the publications.
	 * @param keepBibTeXId indicates if the BibTeX keys should be used as the
	 *     {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
	 *     If this argument is {@code true}, the BibTeX keys are provided to the publication.
	 *     If this argument is {@code false}, the BibTeX keys are ignored.
	 * @param assignRandomId indicates if a random identifier will be assigned to the created entities.
	 *     If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
	 *     If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
	 * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
	 *     the list of the persons. If {@code false}, the list of persons could contain no organization member.
	 * @param createMissedJournal if {@code true} the missed journals from the JPA database will be automatically the subject
	 *     of the creation of a {@link JournalFake journal fake} for the caller. If {@code false}, an exception is thrown when
	 *     a journal is missed from the JPA database.
	 * @param createMissedConference if {@code true} the missed conferences from the JPA database will be automatically the subject
	 *     of the creation of a {@link ConferenceFake conference fake} for the caller. If {@code false}, an exception is thrown when
	 *     a conference is missed from the JPA database.
	 * @return the list of the publications that are successfully extracted.
	 * @throws Exception if it is impossible to parse the given BibTeX source.
	 * @see BibTeX
	 * @see "https://en.wikipedia.org/wiki/BibTeX"
	 */
	public List<Publication> readPublicationsFromBibTeX(Reader bibtex, boolean keepBibTeXId, boolean assignRandomId,
			boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference) throws Exception {
		return this.bibtex.extractPublications(bibtex, keepBibTeXId, assignRandomId, ensureAtLeastOneMember, createMissedJournal,
				createMissedConference);
	}

	/** Parse the given RIS file and extract the publications without adding them in the database.
	 * The format of the RIS is a standard that is briefly described
	 * on {@link "https://en.wikipedia.org/wiki/RIS_(file_format)"}.
	 * If multiple RIS entries are defined into the given input string, each of them is subject
	 * of an exportation tentative.
	 *
	 * @param ris the stream that contains the RIS description of the publications.
	 * @param keepRisId indicates if the RIS reference id should be used as the
	 *     {@link Publication#getPreferredStringId() preferred string-based ID} of the publication.
	 *     If this argument is {@code true}, the reference ids are provided to the publication.
	 *     If this argument is {@code false}, the reference ids are ignored.
	 * @param assignRandomId indicates if a random identifier will be assigned to the created entities.
	 *     If this argument is {@code true}, a numeric id will be computed and assign to all the JPA entities.
	 *     If this argument is {@code false}, the ids of the JPA entities will be the default values, i.e., {@code 0}.
	 * @param ensureAtLeastOneMember if {@code true}, at least one member of a research organization is required from the
	 *     the list of the persons. If {@code false}, the list of persons could contain no organization member.
	 * @param createMissedJournal if {@code true} the missed journals from the JPA database will be automatically the subject
	 *     of the creation of a {@link JournalFake journal fake} for the caller. If {@code false}, an exception is thrown when
	 *     a journal is missed from the JPA database.
	 * @param createMissedConference if {@code true} the missed conferences from the JPA database will be automatically the subject
	 *     of the creation of a {@link ConferenceFake conference fake} for the caller. If {@code false}, an exception is thrown when
	 *     a conference is missed from the JPA database.
	 * @param locale the locale to use.
	 * @return the list of the publications that are successfully extracted.
	 * @throws Exception if it is impossible to parse the given BibTeX source.
	 * @see RIS
	 * @see "https://en.wikipedia.org/wiki/RIS_(file_format)"
	 * @since 3.8
	 */
	public List<Publication> readPublicationsFromRIS(Reader ris, boolean keepRisId, boolean assignRandomId,
			boolean ensureAtLeastOneMember, boolean createMissedJournal, boolean createMissedConference,
			Locale locale) throws Exception {
		return this.ris.extractPublications(ris, keepRisId, assignRandomId, ensureAtLeastOneMember, createMissedJournal,
				createMissedConference, locale);
	}

	/** Import publications from a BibTeX string. The format of the BibTeX is a standard that is briefly described
	 * on {@link "https://en.wikipedia.org/wiki/BibTeX"}.
	 * If multiple BibTeX entries are defined into the given input string, each of them is subject
	 * of an importation tentative. If the import process is successful, the database identifier of the publication
	 * is replied.
	 *
	 * @param bibtex the stream that contains the BibTeX description of the publications.
	 * @param importedEntriesWithExpectedType a map that list the entries to import (keys corresponds to the BibTeX keys) and the
	 *      expected publication type (as the map values) or {@code null} map value if we accept the "default" publication type.
	 *      If this argument is {@code null} or the map is empty, then all the BibTeX entries will be imported.
	 * @param createMissedJournals indicates if the missed journals in the database should be created on-the-fly from
	 *     the BibTeX data.
	 * @param createMissedConferences indicates if the missed conferences in the database should be created on-the-fly from
	 *     the BibTeX data.
	 * @param locale the locale to use.
	 * @return the list of the identifiers of the publications that are successfully imported.
	 * @throws Exception if it is impossible to parse the given BibTeX source.
	 * @see BibTeX
	 * @see "https://en.wikipedia.org/wiki/BibTeX"
	 */
	public List<Long> importBibTeXPublications(Reader bibtex, Map<String, PublicationType> importedEntriesWithExpectedType,
			boolean createMissedJournals, boolean createMissedConferences, Locale locale) throws Exception {
		// Holds the publications that we are trying to import.
		// The publications are not yet imported into the database.
		final var importablePublications = readPublicationsFromBibTeX(bibtex, true, false, true,
				createMissedJournals, createMissedConferences);
		return importPublications(importablePublications, importedEntriesWithExpectedType, locale);
	}

	/** Import publications from a RIS string. The format of the RIS is a standard that is briefly described
	 * on {@link "https://en.wikipedia.org/wiki/RIS_(file_format)"}.
	 * If multiple RIS entries are defined into the given input string, each of them is subject
	 * of an importation tentative. If the import process is successful, the database identifier of the publication
	 * is replied.
	 *
	 * @param ris the stream that contains the RIS description of the publications.
	 * @param importedEntriesWithExpectedType a map that list the entries to import (keys corresponds to the RIS identifiers) and the
	 *      expected publication type (as the map values) or {@code null} map value if we accept the "default" publication type.
	 *      If this argument is {@code null} or the map is empty, then all the RIS entries will be imported.
	 * @param createMissedJournals indicates if the missed journals in the database should be created on-the-fly from
	 *     the RIS data.
	 * @param createMissedConferences indicates if the missed conferences in the database should be created on-the-fly from
	 *     the RIS data.
	 * @param locale the locale to use.
	 * @return the list of the identifiers of the publications that are successfully imported.
	 * @throws Exception if it is impossible to parse the given RIS source.
	 * @see RIS
	 * @see "https://en.wikipedia.org/wiki/RIS_(file_format)"
	 * @since 3.8
	 */
	public List<Long> importRISPublications(Reader ris, Map<String, PublicationType> importedEntriesWithExpectedType,
			boolean createMissedJournals, boolean createMissedConferences, Locale locale) throws Exception {
		// Holds the publications that we are trying to import.
		// The publications are not yet imported into the database.
		final var importablePublications = readPublicationsFromRIS(ris, true, false, true,
				createMissedJournals, createMissedConferences, locale);
		return importPublications(importablePublications, importedEntriesWithExpectedType, locale);
	}

	private List<Long> importPublications(List<Publication> importablePublications,
			Map<String, PublicationType> importedEntriesWithExpectedType,
			Locale locale) throws Exception {
		//Holds the IDs of the successfully imported IDs. We'll need it for type differentiation later.
		final var importedPublicationIdentifiers = new ArrayList<Long>();

		//We are going to try to import every publication in the list
		final var errors = new LinkedList<Throwable>();
		final var forceImport = importedEntriesWithExpectedType == null || importedEntriesWithExpectedType.isEmpty();
		for (final var publication : importablePublications) {
			try {
				// Test if this publication should be imported
				final boolean isImport;
				final PublicationType expectedType;
				if (forceImport) {
					isImport = true;
					if (importedEntriesWithExpectedType != null) {
						expectedType = importedEntriesWithExpectedType.get(publication.getPreferredStringId());
					} else {
						expectedType = null;
					}
				} else {
					assert importedEntriesWithExpectedType != null;
					isImport = importedEntriesWithExpectedType.containsKey(publication.getPreferredStringId());
					if (isImport) {
						expectedType = importedEntriesWithExpectedType.get(publication.getPreferredStringId());
					} else {
						expectedType = null;
					}
				}
				if (isImport) {
					// Change the publication type according to the expected type provided as argument of this function
					if (expectedType != null) {
						if (!publication.getType().isCompatibleWith(expectedType)) {
							throw new IllegalArgumentException(
									getMessage(locale, MESSAGE_PREFIX + "IncompatibleBibTeXEntryType", //$NON-NLS-1$
											publication.getPreferredStringId(),
											publication.getType().name(),
											publication.getType().getLabel(getMessageSourceAccessor(), locale),
											expectedType.name(),
											expectedType.getLabel(getMessageSourceAccessor(), locale)));
						}
						publication.setType(expectedType);
					}

					// Create the journal or the conference if it is missed
					if (publication instanceof JournalBasedPublication jbpub) {
						if (jbpub.getJournal() != null && jbpub.getJournal().isFakeEntity()) {
							final var journal = new Journal(jbpub.getJournal());
							this.journalRepository.save(journal);
							jbpub.setJournal(journal);
						}
					} else if (publication instanceof ConferenceBasedPublication cbpub) {
						if (cbpub.getConference() != null && cbpub.getConference().isFakeEntity()) {
							final var conference = new Conference(cbpub.getConference());
							this.conferenceRepository.save(conference);
							cbpub.setConference(conference);
						}
					}

					// Add the publication to the database and get the new assigned identifier
					this.publicationRepository.save(publication);
					final var publicationId = publication.getId();
					final var publicationIdObj = Long.valueOf(publicationId);

					// Adding the id of the current publication to the list
					importedPublicationIdentifiers.add(publicationIdObj);

					// For every authors assigned to this publication, save them into the database
					final var authors = publication.getAuthors();
					publication.setTemporaryAuthors(null);
					int rank = 0;
					for (final var author : authors) {
						try {
							// Search for a person with a "similar name"
							var personId = this.personService.getPersonIdBySimilarName(
									author.getFirstName(), author.getLastName());
							// Create new author if is not inside the database.
							// If we've already got the author with the abbreviated first name in DB, 
							// but the one parsed have the full version, it creates a new author
							if (personId == 0) {
								this.personRepository.save(author);
								personId = author.getId();
							}
							// Assigning authorship
							addAuthorship(personId, publicationId, rank, false);
							this.publicationRepository.save(publication);

							// Check if the newly imported pub has at least one authorship.
							// If not, it's a bad case and the pub have to be removed and marked as failed
							if (getAuthorsFor(publicationId).isEmpty()) {
								throw new IllegalArgumentException("No author for publication id=" + publicationId); //$NON-NLS-1$
							}
							++rank;
						} catch (Exception ex) {
							// Even if a larger try catch for exceptions exists, we need to delete
							// first the imported publication and linked authorship
							importedPublicationIdentifiers.remove(publicationIdObj);
							for (final var toRemove : this.authorshipRepository.findByPublicationId(publicationId)) {
								this.authorshipRepository.deleteById(Long.valueOf(toRemove.getId()));
							}
							this.publicationRepository.deleteById(publicationIdObj);
							throw ex;
						}
					}
				}
			} catch (Throwable ex) {
				final var ex0 = new IllegalArgumentException("Unable to import the publication: " //$NON-NLS-1$
						+ publication.getTitle() + ". " + ex.getLocalizedMessage(), ex); //$NON-NLS-1$
				getLogger().error(ex0.getLocalizedMessage(), ex0);
				errors.add(ex0);
			}
		}
		if (!errors.isEmpty()) {
			throw new ComposedException(errors);
		}
		return importedPublicationIdentifiers;
	}

	/**
	 * Export function for BibTeX using a list of publication identifiers.
	 *
	 * @param publications the array of publications that should be exported.
	 * @param configurator the configurator of the exporter.
	 * @param progression the progression indicator to be used.
	 * @return the BibTeX description of the publications with the given identifiers.
	 */
	public String exportBibTeX(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression) {
		if (publications == null) {
			return null;
		}
		return this.bibtex.exportPublications(publications, configurator, progression);
	}

	/**
	 * Export function for RIS using a list of publication identifiers.
	 *
	 * @param publications the array of publications that should be exported.
	 * @param configurator the configurator of the exporter.
	 * @param progression the progression indicator to be used.
	 * @return the RIS description of the publications with the given identifiers.
	 * @since 3.7
	 */
	public String exportRIS(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression) {
		if (publications == null) {
			return null;
		}
		return this.ris.exportPublications(publications, configurator, progression);
	}

	/**
	 * Export function for HTML using a list of publication identifiers.
	 *
	 * @param publications the array of publications that should be exported.
	 * @param configurator the configurator of the exporter.
	 * @param progression the progression indicator to be used.
	 * @return the HTML description of the publications with the given identifiers.
	 * @throws Exception if it is impossible to generate the HTML for the publications.
	 */
	public String exportHtml(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression) throws Exception {
		if (publications == null) {
			return null;
		}
		return this.html.exportPublications(publications, configurator, progression);
	}

	/**
	 * Export function for Open Document Text using a list of publication identifiers.
	 *
	 * @param publications the array of publications that should be exported.
	 * @param configurator the configurator of the exporter.
	 * @param progression the progression indicator to be used.
	 * @return the ODT description of the publications with the given identifiers.
	 * @throws Exception if it is impossible to generate the ODT for the publications.
	 */
	public byte[] exportOdt(Collection<? extends Publication> publications, ExporterConfigurator configurator, Progression progression) throws Exception {
		if (publications == null) {
			return null;
		}
		return this.odt.exportPublications(publications, configurator, progression);
	}

	/**
	 * Export function for JSON using a list of publication identifiers.
	 *
	 * @param publications the array of publications that should be exported.
	 * @param configurator the configurator of the exporter.
	 * @param progression the progression indicator to be used.
	 * @param rootKeys the sequence of keys for building the root of the tree. The exported data is then
	 *     output into the last created node with the {@code rootKeys}.
	 * @return the JSON description of the publications with the given identifiers.
	 * @throws Exception if it is impossible to generate the JSON for the publications.
	 */
	public String exportJson(Collection<? extends Publication> publications, ExporterConfigurator configurator,
			Progression progression, String... rootKeys) throws Exception {
		if (publications == null) {
			return null;
		}
		return this.json.exportPublicationsWithRootKeys(publications, configurator, progression, rootKeys);
	}

	/**
	 * Export function for JSON using a list of publication identifiers.
	 *
	 * @param publications the array of publications that should be exported.
	 * @param configurator the configurator of the exporter.
	 * @param progression the progression indicator to be used.
	 * @param callback a function that is invoked for each publication for giving the opportunity
	 *     to fill up the Json node of the publication.
	 * @param rootKeys the sequence of keys for building the root of the tree. The exported data is then
	 *     output into the last created node with the {@code rootKeys}.
	 * @return the Jackson JSON description of the publications with the given identifiers.
	 * @throws Exception if it is impossible to generate the JSON for the publications.
	 */
	public JsonNode exportJsonAsTree(Collection<? extends Publication> publications, ExporterConfigurator configurator,
			Progression progression, Procedure2<Publication, ObjectNode> callback, String... rootKeys) throws Exception {
		if (publications == null) {
			progression.end();
			return null;
		}
		return this.json.exportPublicationsAsTreeWithRootKeys(publications, configurator, progression, callback, rootKeys);
	}

	/** Get the journal instance that is corresponding to the identifier from the given map for an attribute with the given name.
	 * <p>This function generates an exception if the journal is {@code null}.
	 *
	 * @param attributes the set of attributes
	 * @param name the name to search for.
	 * @return the value
	 */
	protected Journal ensureJournalInstance(Map<String, String> attributes, String name) {
		final var journalIdStr = ensureString(attributes, name);
		if (Strings.isNullOrEmpty(journalIdStr)) {
			throw new IllegalArgumentException("Missed journal: " + name); //$NON-NLS-1$
		}
		int journalId;
		try {
			journalId = Integer.parseInt(journalIdStr);
		} catch (Throwable ex) {
			journalId = 0;
		}
		if (journalId == 0) {
			throw new IllegalArgumentException("Missed journal: " + name); //$NON-NLS-1$
		}
		final var optJournal = this.journalRepository.findById(Long.valueOf(journalId));
		if (optJournal.isEmpty()) {
			throw new IllegalArgumentException("Unknown journal: " + name); //$NON-NLS-1$
		}
		return optJournal.get();
	}

	/** Get the conference instance that is corresponding to the identifier from the given map for an attribute with the given name.
	 * <p>This function generates an exception if the conference is {@code null}.
	 *
	 * @param attributes the set of attributes
	 * @param name the name to search for.
	 * @return the value
	 */
	protected Conference ensureConferenceInstance(Map<String, String> attributes, String name) {
		final var conferenceIdStr = ensureString(attributes, name);
		if (Strings.isNullOrEmpty(conferenceIdStr)) {
			throw new IllegalArgumentException("Missed conference: " + name); //$NON-NLS-1$
		}
		int conferenceId;
		try {
			conferenceId = Integer.parseInt(conferenceIdStr);
		} catch (Throwable ex) {
			conferenceId = 0;
		}
		if (conferenceId == 0) {
			throw new IllegalArgumentException("Missed conference: " + name); //$NON-NLS-1$
		}
		final var optConference = this.conferenceRepository.findById(Long.valueOf(conferenceId));
		if (optConference.isEmpty()) {
			throw new IllegalArgumentException("Unknown conference: " + name); //$NON-NLS-1$
		}
		return optConference.get();
	}

	/** Create a publication in the database from values stored in the given map.
	 * This function ignore the attributes related to uploaded files.
	 *
	 * @param validated indicates if the publication is validated by a local authority.
	 * @param attributes the values of the attributes for the publication's creation.
	 * @param authors the list of authors. It is a list of database identifiers (for known persons) and full name
	 *     (for unknown persons). It is assumed that this list contains at least one author that is associated to a research organization.
	 * @param downloadablePDF the uploaded PDF file for the publication.
	 * @param downloadableAwardCertificate the uploaded Award certificate for the publication.
	 * @param scientificAxes the list of scientific axes that are associated to the project. 
	 * @return the created publication.
	 * @throws IOException if the uploaded files cannot be treated correctly.
	 */
	public Optional<Publication> createPublicationFromMap(
			boolean validated,
			Map<String, String> attributes,
			List<String> authors, MultipartFile downloadablePDF, MultipartFile downloadableAwardCertificate,
			List<ScientificAxis> scientificAxes) throws IOException {
		final var typeEnum = PublicationType.valueOfCaseInsensitive(ensureString(attributes, "type")); //$NON-NLS-1$
		final var languageEnum = PublicationLanguage.valueOfCaseInsensitive(ensureString(attributes, "majorLanguage")); //$NON-NLS-1$
		final var date = optionalDate(attributes, "publicationDate"); //$NON-NLS-1$;
		final var year = ensureYear(attributes, "publicationDate"); //$NON-NLS-1$;

		// First step : create the publication
		final var publication = this.prePublicationFactory.createPrePublication(
				typeEnum,
				ensureString(attributes, "title"), //$NON-NLS-1$
				optionalString(attributes, "abstractText"), //$NON-NLS-1$
				optionalString(attributes, "keywords"), //$NON-NLS-1$
				date, year,
				optionalString(attributes, "isbn"), //$NON-NLS-1$
				optionalString(attributes, "issn"), //$NON-NLS-1$
				optionalString(attributes, "doi"), //$NON-NLS-1$
				optionalString(attributes, "halId"), //$NON-NLS-1$
				optionalString(attributes, "extraURL"), //$NON-NLS-1$
				optionalString(attributes, "videoURL"), //$NON-NLS-1$
				optionalString(attributes, "dblpURL"), //$NON-NLS-1$
				optionalString(attributes, "pathToDownloadablePDF"), //$NON-NLS-1$
				optionalString(attributes, "pathToDownloadableAwardCertificate"), //$NON-NLS-1$
				languageEnum);

		// Second step: save late attributes of the fake publication
		publication.setValidated(validated);
		publication.setPublicationYear(year);
		publication.setManualValidationForced(optionalBoolean(attributes, "manualValidationForced")); //$NON-NLS-1$

		// Third step : create the specific publication type
		final var publicationClass = typeEnum.getInstanceType();
		final Publication createdPublication;

		if (publicationClass.equals(Book.class)) {
			createdPublication = this.bookService.createBook(publication,
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "edition"), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					optionalString(attributes, "publisher"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(BookChapter.class)) {
			createdPublication = this.bookChapterService.createBookChapter(publication,
					ensureString(attributes, "bookTitle"), //$NON-NLS-1$
					optionalString(attributes, "chapterNumber"), //$NON-NLS-1$
					optionalString(attributes, "edition"), //$NON-NLS-1$
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					optionalString(attributes, "publisher"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(ConferencePaper.class)) {
			createdPublication = this.conferencePaperService.createConferencePaper(publication,
					ensureConferenceInstance(attributes, "conference"), //$NON-NLS-1$
					optionalInt(attributes, "conferenceOccurrenceNumber", 0), //$NON-NLS-1$
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					optionalString(attributes, "organization"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(JournalEdition.class)) {
			createdPublication = this.journalEditionService.createJournalEdition(publication,
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					ensureJournalInstance(attributes, "journal")); //$NON-NLS-1$
		} else if (publicationClass.equals(JournalPaper.class)) {
			createdPublication = this.journalPaperService.createJournalPaper(publication,
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					ensureJournalInstance(attributes, "journal")); //$NON-NLS-1$
		} else if (publicationClass.equals(KeyNote.class)) {
			createdPublication = this.keyNoteService.createKeyNote(publication,
					ensureConferenceInstance(attributes, "conference"), //$NON-NLS-1$
					optionalInt(attributes, "conferenceOccurrenceNumber", 0), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "organization"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(MiscDocument.class)) {
			createdPublication = this.miscDocumentService.createMiscDocument(publication,
					optionalString(attributes, "number"), //$NON-NLS-1$
					ensureString(attributes, "howPublished"), //$NON-NLS-1$
					optionalString(attributes, "documentType"), //$NON-NLS-1$
					optionalString(attributes, "organization"), //$NON-NLS-1$
					optionalString(attributes, "publisher"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(Patent.class)) {
			createdPublication = this.patentService.createPatent(publication,
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "documentType"), //$NON-NLS-1$
					ensureString(attributes, "institution"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(Report.class)) {
			createdPublication = this.reportService.createReport(publication,
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "documentType"), //$NON-NLS-1$
					ensureString(attributes, "institution"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(Thesis.class)) {
			createdPublication = this.thesisService.createThesis(publication,
					ensureString(attributes, "institution"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else {
			throw new IllegalArgumentException("Unsupported publication type: " + typeEnum); //$NON-NLS-1$
		}

		// Fourth step: create the authors and link them to the publication
		updateAuthorList(true, createdPublication, authors);

		// Fifth step: update the links to other JPA entities
		updateScientificAxes(true, createdPublication, scientificAxes);

		// Sixth step: update the associated PDF files
		updateUploadedPDFs(createdPublication, attributes, downloadablePDF, downloadableAwardCertificate, true);

		getLogger().info("Publication instance " + createdPublication.getId() + " created of type " + publicationClass.getSimpleName()); //$NON-NLS-1$ //$NON-NLS-2$

		return Optional.of(createdPublication);
	}

	/** Update an existing publication in the database from values stored in the given map.
	 *
	 * @param id the identifier of the publication.
	 * @param validated indicates if the publication is validated by a local authority.
	 * @param attributes the values of the attributes for the publication's creation.
	 * @param authors the list of authors. It is a list of database identifiers (for known persons) and full name
	 *     (for unknown persons). It is assumed that this list contains at least one author that is associated to a research organization.
	 * @param downloadablePDF the uploaded PDF file for the publication.
	 * @param downloadableAwardCertificate the uploaded Award certificate for the publication.
	 * @param scientificAxes the list of scientific axes that are associated to the project. 
	 * @return the updated publication.
	 * @throws IOException if the uploaded files cannot be treated correctly.
	 */
	public Optional<Publication> updatePublicationFromMap(long id, boolean validated, Map<String, String> attributes,
			List<String> authors, MultipartFile downloadablePDF, MultipartFile downloadableAwardCertificate,
			List<ScientificAxis> scientificAxes) throws IOException {
		final var typeEnum = PublicationType.valueOfCaseInsensitive(ensureString(attributes, "type")); //$NON-NLS-1$
		// First step : find the publication
		var optPublication = this.publicationRepository.findById(Long.valueOf(id));
		if (optPublication.isEmpty()) {
			throw new IllegalArgumentException("Publication not found with id: " + id); //$NON-NLS-1$
		}
		final var publication = optPublication.get();
		// Second step: check for any change of publication type
		if (isInstanceTypeChangeNeeded(publication, typeEnum)) {
			removePublication(id, false);
			optPublication = createPublicationFromMap(validated, attributes, authors, downloadablePDF, downloadableAwardCertificate, scientificAxes);
			if (optPublication.isPresent()) {
				final var newPublication = optPublication.get();
				final var newId = newPublication.getId();
				final var associatedFilesChanged = new MutableBoolean(false);
				this.fileManager.moveFiles(id, newId, (key, source, target) -> {
					switch (key) {
					case "pdf": //$NON-NLS-1$
						if (downloadablePDF != null && downloadablePDF.isEmpty()) {
							// PDF file was associated and no new one was provided in the POST query.
							// Re-associate the previously associated file.
							newPublication.setPathToDownloadablePDF(target);
							associatedFilesChanged.setTrue();
						}
						break;
					case "award": //$NON-NLS-1$
						if (downloadableAwardCertificate != null && downloadableAwardCertificate.isEmpty()) {
							// PDF file was associated and no new one was provided in the POST query.
							// Re-associate the previously associated file.
							newPublication.setPathToDownloadableAwardCertificate(target);
							associatedFilesChanged.setTrue();
						}
						break;
					default:
						// silent
					}
				});
				if (associatedFilesChanged.isTrue()) {
					save(newPublication);
				}
			}
			return optPublication;
		}
		// Third step: update of an existing publication
		updateExistingPublicationFromMap(publication, typeEnum, validated, attributes, authors,
				downloadablePDF, downloadableAwardCertificate, scientificAxes);
		return optPublication;
	}

	private static boolean isInstanceTypeChangeNeeded(Publication publication, PublicationType expectedType) {
		final var clazz = expectedType.getInstanceType();
		return !clazz.isInstance(publication);
	}

	/** Update an existing publication in the database from values stored in the given map.
	 *
	 * @param publication the publication.
	 * @param type the type of the publication to be set-up.
	 * @param validated indicates if the publication is validated by a local authority.
	 * @param attributes the values of the attributes for the publication's creation.
	 * @param authors the list of authors. It is a list of database identifiers (for known persons) and full name
	 *     (for unknown persons).
	 * @param downloadablePDF the uploaded PDF file for the publication.
	 * @param downloadableAwardCertificate the uploaded Award certificate for the publication.
	 * @param scientificAxes the list of scientific axes that are associated to the project. 
	 * @throws IOException if the uploaded files cannot be treated correctly.
	 */
	protected void updateExistingPublicationFromMap(Publication publication, PublicationType type, boolean validated,
			Map<String, String> attributes, List<String> authors, MultipartFile downloadablePDF,
			MultipartFile downloadableAwardCertificate, List<ScientificAxis> scientificAxes) throws IOException {
		final var languageEnum = PublicationLanguage.valueOfCaseInsensitive(ensureString(attributes, "majorLanguage")); //$NON-NLS-1$
		final var date = optionalDate(attributes, "publicationDate"); //$NON-NLS-1$;
		final var year = ensureYear(attributes, "publicationDate"); //$NON-NLS-1$;

		// First step: Update the specific fields.
		publication.setManualValidationForced(optionalBoolean(attributes, "manualValidationForced")); //$NON-NLS-1$
		publication.setValidated(validated);

		// Second step: Update the list of authors.
		updateAuthorList(false, publication, authors);

		// Third step: treat associated files
		updateScientificAxes(false, publication, scientificAxes);

		// Fourth step: Update the list of authors.
		updateUploadedPDFs(publication, attributes, downloadablePDF, downloadableAwardCertificate, false);

		// Fifth step : update the specific publication
		final var publicationClass = type.getInstanceType();

		if (publicationClass.equals(Book.class)) {
			this.bookService.updateBook(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "edition"), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					optionalString(attributes, "publisher"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(BookChapter.class)) {
			this.bookChapterService.updateBookChapter(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					ensureString(attributes, "bookTitle"), //$NON-NLS-1$
					optionalString(attributes, "chapterNumber"), //$NON-NLS-1$
					optionalString(attributes, "edition"), //$NON-NLS-1$
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					optionalString(attributes, "publisher"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(ConferencePaper.class)) {
			this.conferencePaperService.updateConferencePaper(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					ensureConferenceInstance(attributes, "conference"), //$NON-NLS-1$
					optionalInt(attributes, "conferenceOccurrenceNumber", 0), //$NON-NLS-1$
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					optionalString(attributes, "organizer"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(JournalEdition.class)) {
			final Journal journal = ensureJournalInstance(attributes, "journal"); //$NON-NLS-1$
			this.journalEditionService.updateJournalEdition(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					journal);
		} else if (publicationClass.equals(JournalPaper.class)) {
			final Journal journal = ensureJournalInstance(attributes, "journal"); //$NON-NLS-1$
			this.journalPaperService.updateJournalPaper(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					optionalString(attributes, "volume"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "pages"), //$NON-NLS-1$
					optionalString(attributes, "series"), //$NON-NLS-1$
					journal);
		} else if (publicationClass.equals(KeyNote.class)) {
			this.keyNoteService.updateKeyNote(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					ensureConferenceInstance(attributes, "conference"), //$NON-NLS-1$
					optionalInt(attributes, "conferenceOccurrenceNumber", 0), //$NON-NLS-1$
					optionalString(attributes, "editors"), //$NON-NLS-1$
					optionalString(attributes, "organization"), //$NON-NLS-1$
					optionalString(attributes, "organization")); //$NON-NLS-1$
		} else if (publicationClass.equals(MiscDocument.class)) {
			this.miscDocumentService.updateMiscDocument(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					ensureString(attributes, "howPublished"), //$NON-NLS-1$
					optionalString(attributes, "documentType"), //$NON-NLS-1$
					optionalString(attributes, "organization"), //$NON-NLS-1$
					optionalString(attributes, "publisher"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(Patent.class)) {
			this.patentService.updatePatent(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "documentType"), //$NON-NLS-1$
					ensureString(attributes, "institution"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(Report.class)) {
			this.reportService.updateReport(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					optionalString(attributes, "number"), //$NON-NLS-1$
					optionalString(attributes, "documentType"), //$NON-NLS-1$
					ensureString(attributes, "institution"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else if (publicationClass.equals(Thesis.class)) {
			this.thesisService.updateThesis(
					publication.getId(),
					ensureString(attributes, "title"), //$NON-NLS-1$
					type,
					date, year,
					optionalString(attributes, "abstractText"), //$NON-NLS-1$
					optionalString(attributes, "keywords"), //$NON-NLS-1$
					optionalString(attributes, "doi"), //$NON-NLS-1$
					optionalString(attributes, "halId"), //$NON-NLS-1$
					optionalString(attributes, "isbn"), //$NON-NLS-1$
					optionalString(attributes, "issn"), //$NON-NLS-1$
					optionalString(attributes, "dblpURL"), //$NON-NLS-1$
					optionalString(attributes, "extraURL"), //$NON-NLS-1$
					languageEnum,
					publication.getPathToDownloadablePDF(),
					publication.getPathToDownloadableAwardCertificate(),
					optionalString(attributes, "videoURL"), //$NON-NLS-1$
					ensureString(attributes, "institution"), //$NON-NLS-1$
					optionalString(attributes, "address")); //$NON-NLS-1$
		} else {
			throw new IllegalArgumentException("Unsupported publication type: " + type); //$NON-NLS-1$
		}

		getLogger().info("Publication instance updated: " + publication.getId()); //$NON-NLS-1$
	}

	/** Update the references to the downloadable files for the given publication based on the 
	 * inputs.
	 * The just-uploaded files are given as argument.
	 * The attributes may contains two boolean files named {@code @pathToDownloadablePDF_previousValue}
	 * or {@code @pathToDownloadableAwardCertificate_previousValue} that indicates if the publication
	 * has associated files before the update query.
	 * 
	 * @param publication the publication to update.
	 * @param attributes the values of the attributes for the publication's creation.
	 * @param downloadablePDF the uploaded PDF file for the publication.
	 * @param downloadableAwardCertificate the uploaded Award certificate for the publication.
	 * @param saveInDb indicates if the changes must be saved in the database.
	 * @throws IOException if the uploaded files cannot be treated correctly.
	 */
	protected void updateUploadedPDFs(Publication publication, Map<String, String> attributes,
			MultipartFile downloadablePDF, MultipartFile downloadableAwardCertificate, boolean saveInDb) throws IOException {
		// Treat the uploaded files
		var hasUploaded = false;
		final var expliteRemove0 = optionalBoolean(attributes, "@fileUpload_removed_pathToDownloadablePDF"); //$NON-NLS-1$
		if (expliteRemove0) {
			try {
				this.fileManager.deletePublicationPdfFile(publication.getId());
			} catch (Throwable ex) {
				// Silent
			}
			publication.setPathToDownloadablePDF(null);
			hasUploaded = true;
		}
		if (downloadablePDF != null && !downloadablePDF.isEmpty()) {
			final var pdfFilename = this.fileManager.makePdfFilename(publication.getId());
			final var jpgFilename = this.fileManager.makePdfPictureFilename(publication.getId());
			this.fileManager.savePdfAndThumbnailFiles(pdfFilename, jpgFilename, downloadablePDF);
			publication.setPathToDownloadablePDF(pdfFilename.getPath());
			hasUploaded = true;
			getLogger().info("PDF uploaded at: " + pdfFilename.getPath()); //$NON-NLS-1$
		}
		final boolean expliteRemove1 = optionalBoolean(attributes, "@fileUpload_removed_pathToDownloadableAwardCertificate"); //$NON-NLS-1$
		if (expliteRemove1) {
			try {
				this.fileManager.deletePublicationAwardPdfFile(publication.getId());
			} catch (Throwable ex) {
				// Silent
			}
			publication.setPathToDownloadableAwardCertificate(null);
			hasUploaded = true;
		}
		if (downloadableAwardCertificate != null && !downloadableAwardCertificate.isEmpty()) {
			final var pdfFilename = this.fileManager.makeAwardFilename(publication.getId());
			final var jpgFilename = this.fileManager.makeAwardPictureFilename(publication.getId());
			this.fileManager.savePdfAndThumbnailFiles(pdfFilename, jpgFilename, downloadableAwardCertificate);
			publication.setPathToDownloadableAwardCertificate(pdfFilename.getPath());
			hasUploaded = true;
			getLogger().info("Award certificate uploaded at: " + pdfFilename.getPath()); //$NON-NLS-1$
		}
		if (saveInDb && hasUploaded) {
			save(publication);
		}
	}

	private void updateScientificAxes(boolean creation, Publication publication, List<ScientificAxis> axes) {
		publication.setScientificAxes(axes);
		this.publicationRepository.save(publication);
	}

	private void updateAuthorList(boolean creation, Publication publication, List<String> authors) {
		// First step: Update the list of authors.
		final var oldAuthorships = creation ? Collections.<Authorship>emptyList() : getAuthorshipsFor(publication.getId());
		Collector<Authorship, ?, Map<Long, Authorship>> col = Collectors.toMap(
				it -> Long.valueOf(it.getPerson().getId()),
				it -> it);
		final var oldIds = oldAuthorships.stream().collect(col);
		final var idPattern = Pattern.compile("\\d+"); //$NON-NLS-1$
		var rank = 0;
		for (final var author : authors) {
			Person person = null;
			var authorId = 0l;
			if (idPattern.matcher(author).matches()) {
				// Numeric value means that the person is known.
				try {
					authorId = Integer.parseInt(author);
				} catch (Throwable ex) {
					// Silent
				}
			}
			if (authorId == 0) {
				// The author seems to be not in the database already. Check it based on the name.
				final var firstName = this.nameParser.parseFirstName(author);
				final var lastName = this.nameParser.parseLastName(author);
				authorId = this.personService.getPersonIdByName(firstName, lastName);
				if (authorId == 0) {
					// Now, it is sure that the person is unknown
					person = this.personService.createPerson(firstName, lastName);
					getLogger().info("New person \"" + author + "\" created with id: " + authorId); //$NON-NLS-1$ //$NON-NLS-2$
				} else {
					final var optPers = this.personRepository.findById(Long.valueOf(authorId));
					if (optPers.isEmpty()) {
						throw new IllegalArgumentException("Unknown person with id: " + authorId); //$NON-NLS-1$
					}
					person = optPers.get();
				}
			} else {
				// Check if the given author identifier corresponds to a known person.
				final var optPers = this.personRepository.findById(Long.valueOf(authorId));
				if (optPers.isEmpty()) {
					throw new IllegalArgumentException("Unknown person with id: " + authorId); //$NON-NLS-1$
				}
				person = optPers.get();
			}
			assert person != null;
			oldIds.remove(Long.valueOf(person.getId()));
			final var fperson = person;
			final var optAut = oldAuthorships.stream().filter(it -> it.getPerson().getId() == fperson.getId()).findFirst();
			if (optAut.isPresent()) {
				// Author is already present in the authorships
				final var authorship = optAut.get();
				authorship.setPerson(person);
				authorship.setAuthorRank(rank);
				authorship.setPublication(publication);
				this.authorshipRepository.save(authorship);
				oldIds.remove(Long.valueOf(authorship.getId()));
				getLogger().info("Author \"" + person.getFullName() //$NON-NLS-1$
				+ "\" updated for the publication with id " //$NON-NLS-1$
				+ publication.getId());
			} else {
				// Author was not associated yet
				addAuthorship(person.getId(), publication.getId(), rank, false);
				getLogger().info("Author \"" + person.getFullName()+ "\" added to publication with id " + publication.getId()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			++rank;
		}
		// Remove the old author ships
		for (final var oldAutshp : oldIds.values()) {
			publication.getAuthorshipsRaw().remove(oldAutshp);
			final var oldAuthor = oldAutshp.getPerson();
			oldAuthor.getAuthorships().remove(oldAutshp);
			oldAutshp.setPerson(null);
			oldAutshp.setPublication(null);
			this.personRepository.save(oldAuthor);
			this.authorshipRepository.deleteById(Long.valueOf(oldAutshp.getId()));
		}
		this.publicationRepository.save(publication);
		this.authorshipRepository.flush();
	}

	/** Replies the list of the publications that are associated to the conference with the given identifier.
	 *
	 * @param conferenceId the identifier of the conference.
	 * @return the list of the associated publications.
	 * @since 3.6
	 */
	public List<ConferenceBasedPublication> getPublicationsForConference(long conferenceId) {
		return this.publicationRepository.findAll().stream()
				.filter(it -> it instanceof ConferenceBasedPublication)
				.map(it -> (ConferenceBasedPublication) it)
				.filter(it -> it.getConference() != null && it.getConference().getId() == conferenceId)
				.collect(Collectors.toList());
	}

	/** Replies if the given id corresponds to an author.
	 *
	 * @param id the identifier of the person.
	 * @return {@code true} if the person is an author.
	 * @since 3.6
	 */
	public boolean isAuthor(long id) {
		return !this.publicationRepository.findAllByAuthorshipsPersonId(id).isEmpty();
	}

	/** Copy the publication to be of the given target type.
	 *
	 * @param publication the publication to copy.
	 * @param target the target type.
	 * @return the result of the copy.
	 */
	public Publication transform(Publication publication, PublicationType target) {
		final Class<? extends Publication> targetType = target.getInstanceType();

		final String address;
		final String bookTitle;
		final String chapterNumber;
		final Conference conference;
		final String edition;
		final String editors;
		final String howPublished;
		final String institution;
		final Journal journal;
		final String number;
		final int occurrence;
		final String organization;
		final String pages;
		final String publisher;
		final String series;
		final String type;
		final String volume;
		
		if (publication instanceof JournalPaper journalPaper) {
			address = null;
			bookTitle = journalPaper.getJournal().getJournalName();
			chapterNumber = null;
			conference = null;
			edition = null;
			editors = null;
			howPublished = journalPaper.getJournal().getJournalName();
			institution = journalPaper.getJournal().getPublisher();
			journal = journalPaper.getJournal();
			number = journalPaper.getNumber();
			occurrence = 0;
			organization = journal.getPublisher();
			pages = journalPaper.getPages();
			publisher = journal.getPublisher();
			series = journalPaper.getSeries();
			type = null;
			volume = journalPaper.getVolume();
		} else if (publication instanceof JournalEdition journalEdition) {
			address = null;
			bookTitle = journalEdition.getJournal().getJournalName();
			chapterNumber = null;
			conference = null;
			edition = null;
			editors = null;
			howPublished = journalEdition.getJournal().getJournalName();
			institution = journalEdition.getJournal().getPublisher();
			journal = journalEdition.getJournal();
			number = journalEdition.getNumber();
			occurrence = 0;
			organization = null;
			pages = journalEdition.getPages();
			publisher = journal.getPublisher();
			series = null;
			type = null;
			volume = journalEdition.getVolume();
		} else if (publication instanceof ConferencePaper conferencePaper) {
			address = conferencePaper.getAddress();
			bookTitle = conferencePaper.getConference().getName();
			chapterNumber = null;
			conference = conferencePaper.getConference();
			edition = null;
			editors = conferencePaper.getEditors();
			howPublished = conferencePaper.getConference().getName();
			institution = conferencePaper.getOrganization();
			journal = null;
			number = conferencePaper.getNumber();
			occurrence = conferencePaper.getConferenceOccurrenceNumber();
			organization = conferencePaper.getOrganization();
			pages = conferencePaper.getPages();
			publisher = conference.getPublisher();
			series = conferencePaper.getSeries();
			type = null;
			volume = conferencePaper.getVolume();
		} else if (publication instanceof KeyNote keynote) {
			address = keynote.getAddress();
			bookTitle = keynote.getConference().getName();
			chapterNumber = null;
			conference = keynote.getConference();
			edition = null;
			editors = keynote.getEditors();
			howPublished = keynote.getConference().getName();
			institution = keynote.getOrganization();
			journal = null;
			number = null;
			occurrence = keynote.getConferenceOccurrenceNumber();
			organization = keynote.getOrganization();
			pages = null;
			publisher = conference.getPublisher();
			series = null;
			type = null;
			volume = null;
		} else if (publication instanceof Book book) {
			address = book.getAddress();
			bookTitle = null;
			chapterNumber = null;
			conference = null;
			edition = book.getEdition();
			editors = book.getEditors();
			howPublished = book.getPublisher();
			institution = book.getPublisher();
			journal = null;
			number = book.getNumber();
			occurrence = 0;
			organization = book.getPublisher();
			pages = book.getPages();
			publisher = book.getPublisher();
			series = book.getSeries();
			type = null;
			volume = book.getVolume();
		} else if (publication instanceof BookChapter chapter) {
			address = chapter.getAddress();
			bookTitle = chapter.getBookTitle();
			chapterNumber = chapter.getChapterNumber();
			conference = null;
			edition = chapter.getEdition();
			editors = chapter.getEditors();
			howPublished = chapter.getPublisher();
			institution = chapter.getPublisher();
			journal = null;
			number = chapter.getNumber();
			occurrence = 0;
			organization = chapter.getPublisher();
			pages = chapter.getPages();
			publisher = chapter.getPublisher();
			series = chapter.getSeries();
			type = null;
			volume = chapter.getVolume();
		} else if (publication instanceof Patent patent) {
			address = patent.getAddress();
			bookTitle = null;
			chapterNumber = null;
			conference = null;
			edition = null;
			editors = null;
			howPublished = patent.getInstitution();
			institution = patent.getInstitution();
			journal = null;
			number = patent.getPatentNumber();
			occurrence = 0;
			organization = patent.getInstitution();
			pages = null;
			publisher = patent.getInstitution();
			series = patent.getPatentType();
			type = patent.getPatentType();
			volume = null;
		} else if (publication instanceof Report report) {
			address = report.getAddress();
			bookTitle = null;
			chapterNumber = null;
			conference = null;
			edition = null;
			editors = null;
			howPublished = report.getInstitution();
			institution = report.getInstitution();
			journal = null;
			number = report.getReportNumber();
			occurrence = 0;
			organization = report.getInstitution();
			pages = null;
			publisher = report.getInstitution();
			series = report.getReportType();
			type = report.getReportType();
			volume = null;
		} else if (publication instanceof Thesis thesis) {
			address = thesis.getAddress();
			bookTitle = null;
			chapterNumber = null;
			conference = null;
			edition = null;
			editors = null;
			howPublished = thesis.getInstitution();
			institution = thesis.getInstitution();
			journal = null;
			number = null;
			occurrence = 0;
			organization = thesis.getInstitution();
			pages = null;
			publisher = thesis.getInstitution();
			series = null;
			type = null;
			volume = null;
		} else if (publication instanceof MiscDocument document) {
			address = document.getAddress();
			bookTitle = null;
			chapterNumber = null;
			conference = null;
			edition = null;
			editors = null;
			howPublished = document.getHowPublished();
			institution = document.getOrganization();
			journal = null;
			number = document.getDocumentNumber();
			occurrence = 0;
			organization = document.getOrganization();
			pages = null;
			publisher = document.getPublisher();
			series = null;
			type = document.getDocumentType();
			volume = null;
		} else if (publication instanceof PrePublication) {
			address = null;
			bookTitle = null;
			chapterNumber = null;
			conference = null;
			edition = null;
			editors = null;
			howPublished = null;
			institution = null;
			journal = null;
			number = null;
			occurrence = 0;
			organization = null;
			pages = null;
			publisher = null;
			series = null;
			type = null;
			volume = null;
		} else {
			throw new IllegalArgumentException();
		}

		final var prePublication = this.prePublicationFactory.createPrePublication(
				target,
				publication.getTitle(),
				publication.getAbstractText(), publication.getKeywords(),
				publication.getPublicationDate(), publication.getPublicationYear(),
				publication.getISBN(), publication.getISSN(),
				publication.getDOI(), publication.getHalId(),
				publication.getExtraURL(), publication.getVideoURL(),
				publication.getDblpURL(), publication.getPathToDownloadablePDF(),
				publication.getPathToDownloadableAwardCertificate(),
				publication.getMajorLanguage());

		final Publication newPublication;
		if (JournalPaper.class.equals(targetType)) {
			newPublication = this.journalPaperService.createJournalPaper(prePublication, volume, number, pages, series, journal, false);
		} else if (JournalEdition.class.equals(targetType)) {
			newPublication = this.journalEditionService.createJournalEdition(prePublication, volume, number, pages, journal, false);
		} else if (ConferencePaper.class.equals(targetType)) {
			newPublication = this.conferencePaperService.createConferencePaper(prePublication, conference, occurrence, volume, number, pages, editors, series, organization, address, false);
		} else if (KeyNote.class.equals(targetType)) {
			newPublication = this.keyNoteService.createKeyNote(prePublication, conference, occurrence, editors, organization, address, false);
		} else if (Book.class.equals(targetType)) {
			newPublication = this.bookService.createBook(prePublication, volume, number, pages, edition, editors, series, publisher, address, false);
		} else if (BookChapter.class.equals(targetType)) {
			newPublication = this.bookChapterService.createBookChapter(prePublication, bookTitle, chapterNumber, edition, volume, number, pages, editors, series, publisher, address, false);
		} else if (Patent.class.equals(targetType)) {
			newPublication = this.patentService.createPatent(prePublication, number, type, institution, address, false);
		} else if (Report.class.equals(targetType)) {
			newPublication = this.reportService.createReport(prePublication, number, type, institution, address, false);
		} else if (Thesis.class.equals(targetType)) {
			newPublication = this.thesisService.createThesis(prePublication, institution, address, false);
		} else if (MiscDocument.class.equals(targetType)) {
			newPublication = this.miscDocumentService.createMiscDocument(prePublication, number, howPublished, type, organization, publisher, address, false);
		} else {
			throw new IllegalArgumentException();
		}

		// Finalize the creation of the new publication
		newPublication.setTemporaryAuthors(publication.getAuthors());
		newPublication.setPublicationYear(publication.getPublicationYear());
		newPublication.setValidated(publication.isValidated());
		newPublication.setManualValidationForced(publication.getManualValidationForced());

		return newPublication;
	}

	/** Save a new publication or update an existing publication.
	 *
	 * @param changedPublication the publication after changes that should be to be saved.
	 * @param originalPublication the publication before change that could be removed it is no more needed.
	 * @return the saved publication in the database.
	 * @throws IOException error when it is impossible to save or rename the associated files.
	 */
	@Transactional
	public Publication saveOrUpdatePublication(Publication changedPublication, Publication originalPublication) throws IOException {
		final var typeEnum = changedPublication.getType();
		if (originalPublication != null && !typeEnum.isCompatibleWith(originalPublication.getType())) {
			// Save the publication
			final var savedPublication = save(changedPublication);
			// Remove the publication without removing the associated files
			final var oldId = originalPublication.getId();
			removePublication(oldId, false);
			// Rename the associated files
			final var newId = savedPublication.getId();
			final var associatedFilesChanged = new MutableBoolean(false);
			this.fileManager.moveFiles(oldId, newId, (key, source, target) -> {
				switch (key) {
				case "pdf": //$NON-NLS-1$
					if (!Strings.isNullOrEmpty(originalPublication.getPathToDownloadablePDF())) {
						// PDF file was associated and no new one was provided.
						// Re-associate the previously associated file.
						savedPublication.setPathToDownloadablePDF(target);
						associatedFilesChanged.setTrue();
					}
					break;
				case "award": //$NON-NLS-1$
					if (!Strings.isNullOrEmpty(originalPublication.getPathToDownloadableAwardCertificate())) {
						// PDF file was associated and no new one was provided.
						// Re-associate the previously associated file.
						savedPublication.setPathToDownloadableAwardCertificate(target);
						associatedFilesChanged.setTrue();
					}
					break;
				default:
					// silent
				}
			});
			if (associatedFilesChanged.isTrue()) {
				save(savedPublication);
			}
			return savedPublication;
		}

		// Update of an existing publication
		
		// Update the list of authors.
		final var newAuthors = changedPublication.getTemporaryAuthors();
		changedPublication.setTemporaryAuthors(null);
		final Publication savedPublication;
		if (newAuthors != null) {
			savedPublication = updateAuthorListAndSave(changedPublication, newAuthors);
		} else {
			savedPublication = save(changedPublication);
		}

		getLogger().info("Publication instance updated: " + savedPublication.getId()); //$NON-NLS-1$
		
		return savedPublication;
	}

	private Publication updateAuthorListAndSave(Publication publication, List<Person> authors) {
		// Save the publication before changing the authors
		Publication savedPublication = this.publicationRepository.save(publication);

		// Update the list of authors.
		Collector<Authorship, ?, Map<Long, Authorship>> col = Collectors.toMap(
				it -> Long.valueOf(it.getPerson().getId()),
				it -> it);
		final var oldIds = new HashMap<>(publication.getAuthorshipsRaw().stream().collect(col));
		var rank = 0;
		for (final var author : authors) {
			assert author != null;
			oldIds.remove(Long.valueOf(author.getId()));
			final var fperson = author;
			final var optAut = publication.getAuthorshipsRaw().stream().filter(it -> it.getPerson().getId() == fperson.getId()).findFirst();
			if (optAut.isPresent()) {
				// Author is already present in the authorships
				final var authorship = optAut.get();
				authorship.setAuthorRank(rank);
				this.authorshipRepository.save(authorship);
				oldIds.remove(Long.valueOf(authorship.getId()));
				getLogger().info("Author \"" + author.getFullName() + "\" updated for the publication with id " + publication.getId()); //$NON-NLS-1$ //$NON-NLS-2$
			} else {
				// Author was not associated yet
				addAuthorship(author, publication, rank);
				getLogger().info("Author \"" + author.getFullName()+ "\" added to publication with id " + publication.getId()); //$NON-NLS-1$ //$NON-NLS-2$
			}
			++rank;
		}

		// Remove the old authorships
		for (final var oldAutshp : oldIds.values()) {
			publication.getAuthorshipsRaw().remove(oldAutshp);
			final var oldAuthor = oldAutshp.getPerson();
			oldAuthor.getAuthorships().remove(oldAutshp);
			this.personRepository.save(oldAuthor);
			savedPublication = this.publicationRepository.save(publication);
			this.authorshipRepository.deleteById(Long.valueOf(oldAutshp.getId()));
		}
		// Save the publication
		return savedPublication;
	}

	private Authorship addAuthorship(Person person, Publication publication, int rank) {
		// No need to add the authorship if the person is already linked to the publication
		final var currentAuthors = publication.getAuthorshipsRaw();
		final var ro = currentAuthors.stream().filter(it -> it.getPerson().getId() == person.getId()).findAny();
		if (ro.isEmpty()) {
			final var authorship = new Authorship();
			authorship.setPerson(person);
			authorship.setPublication(publication);
			authorship.setAuthorRank(rank);

			currentAuthors.add(authorship);
			person.getAuthorships().add(authorship);

			this.authorshipRepository.save(authorship);
			return authorship;
		}
		return null;
	}

	/** Generate the thumbnails for the given publications.
	 *
	 * @param publications the list of publications
	 * @param locale the locale to be used for the progress messages.
	 * @param progress the progression indicator to be used during the process.
	 * @throws IOException if some thumbnail cannot be generated
	 */
	public void generateThumbnails(List<Publication> publications, Locale locale, Progression progress) throws IOException {
		final var progress0 = progress == null ? new DefaultProgression() : progress;
		final var existingThumbnails = this.fileManager.getThumbailFiles();
		progress0.setProperties(0, 0, existingThumbnails.totalSize() + publications.size() * 2, false);
		while (existingThumbnails.hasNext()) {
			final var thumbnail = existingThumbnails.next();
			progress0.setComment(getMessage(locale, MESSAGE_PREFIX + "deleteThumbnail", thumbnail.getName())); //$NON-NLS-1$
			thumbnail.delete();
			progress0.increment();
		}
		for (final var publication : publications) {
			progress0.setComment(getMessage(locale, MESSAGE_PREFIX + "generateThumbnail", publication.getTitle())); //$NON-NLS-1$
			final var paperFile = publication.getPathToDownloadablePDF();
			if (!Strings.isNullOrEmpty(paperFile)) {
				final var file = FileSystem.convertStringToFile(paperFile);
				if (file != null) {
					this.fileManager.regenerateThumbnail(file);
				}
			}
			progress0.increment();
			final var awardFile = publication.getPathToDownloadableAwardCertificate();
			if (!Strings.isNullOrEmpty(awardFile)) {
				final var file = FileSystem.convertStringToFile(awardFile);
				if (file != null) {
					this.fileManager.regenerateThumbnail(file);
				}
			}
			progress0.increment();
		}
		progress0.end();
	}

	@Override
	public EntityEditingContext<Publication> startEditing(Publication publication) {
		assert publication != null;
		// Force initialization of the internal properties that are needed for editing
		if (publication.getId() != 0l) {
			inSession(session -> {
				session.load(publication, Long.valueOf(publication.getId()));
				if (publication instanceof JournalBasedPublication journalPub) {
					Hibernate.initialize(journalPub.getJournal());
				}
				if (publication instanceof ConferenceBasedPublication conferencePub) {
					Hibernate.initialize(conferencePub.getConference());
				}
				Hibernate.initialize(publication.getScientificAxes());
				// Load all the authorships and the associated person for enabling the list of authors
				Hibernate.initialize(publication.getAuthorshipsRaw());
				publication.getAuthorshipsRaw().forEach(it -> {
					Hibernate.initialize(it.getPerson().getAuthorships());
				});
			});
		}
		return new EditingContext(publication);
	}

	@Override
	public EntityDeletingContext<Publication> startDeletion(Set<Publication> publications) {
		assert publications != null && !publications.isEmpty();
		// Force loading of the linked entities
		inSession(session -> {
			for (final var publication : publications) {
				if (publication.getId() != 0l) {
					session.load(publication, publication.getId());
					Hibernate.initialize(publication.getAuthorships());
					for (final var authorship : publication.getAuthorships()) {
						Hibernate.initialize(authorship.getPerson());
						Hibernate.initialize(authorship.getPerson().getAuthorships());
					}
					Hibernate.initialize(publication.getScientificAxes());
				}
			}
		});
		return new DeletingContext(publications);
	}

	/** Context for editing a {@link Publication}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityWithServerFilesEditingContext<Publication> {

		private static final long serialVersionUID = 279291279949905796L;

		private final UploadedFileTracker<Publication> pathToPdfFile;

		private final UploadedFileTracker<Publication> pathToAwardFile;

		/** Constructor.
		 *
		 * @param publication the edited publication.
		 */
		EditingContext(Publication publication) {
			super(publication);
			this.pathToPdfFile = newUploadedFileTracker(publication,
					Publication::getPathToDownloadablePDF,
					(id, savedPath) -> PublicationService.this.fileManager.deletePublicationPdfFile(id.longValue()),
					(oldId, newId) -> PublicationService.this.fileManager.moveFiles(oldId.longValue(), newId.longValue(), null));
			this.pathToAwardFile = newUploadedFileTracker(publication,
					Publication::getPathToDownloadableAwardCertificate,
					(id, savedPath) -> PublicationService.this.fileManager.deletePublicationAwardPdfFile(id.longValue()),
					(oldId, newId) -> PublicationService.this.fileManager.moveFiles(oldId.longValue(), newId.longValue(), null));
		}

		@Override
		protected Publication writeInJPA(Publication entity, boolean initialSaving) {
			try {
				return PublicationService.this.saveOrUpdatePublication(entity, this.entity);
			} catch (IOException ex) {
				throw new RuntimeException(ex);
			}
		}

		@Override
		protected void deleteOrRenameAssociatedFiles(long oldId) throws IOException {
			this.pathToPdfFile.deleteOrRenameFile(oldId, this.entity);
			this.pathToAwardFile.deleteOrRenameFile(oldId, this.entity);
		}

		@Override
		protected boolean prepareAssociatedFileUpload() throws IOException {
			final var doUpload1 = !this.pathToPdfFile.deleteFile(this.entity);
			final var doUpload2 = !this.pathToAwardFile.deleteFile(this.entity);
			return doUpload1 || doUpload2;
		}

		@Override
		protected void postProcessAssociatedFiles() throws IOException {
			this.pathToPdfFile.resetPathMemory(this.entity);
			this.pathToAwardFile.resetPathMemory(this.entity);
		}

		@Override
		public EntityDeletingContext<Publication> createDeletionContext() {
			return PublicationService.this.startDeletion(Collections.singleton(this.entity));
		}

	}

	/** Context for deleting a {@link Publication}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<Publication> {

		private static final long serialVersionUID = -3369716394627059296L;

		/** Constructor.
		 *
		 * @param publications the publications to delete.
		 */
		protected DeletingContext(Set<Publication> publications) {
			super(publications);
		}
		
		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			// Do the deletion
			for (final var publication : getEntities()) {
				// Unlink the authorships and author related entities
				final var id = publication.getId();
                for (Authorship authorship : publication.getAuthorships()) {
                    PublicationService.this.authorshipRepository.deleteById(authorship.getId());
                }
				publication.getAuthorshipsRaw().clear();
				publication.setScientificAxes(null);

				// Save the entity
				PublicationService.this.publicationRepository.deleteById(Long.valueOf(id));
				
				// Delete file managers
				final var pubid = publication.getId();
				PublicationService.this.fileManager.deletePublicationPdfFile(pubid);
				PublicationService.this.fileManager.deletePublicationAwardPdfFile(pubid);
			}
		}

	}

}
