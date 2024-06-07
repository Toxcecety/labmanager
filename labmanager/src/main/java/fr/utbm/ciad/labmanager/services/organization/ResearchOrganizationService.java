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

package fr.utbm.ciad.labmanager.services.organization;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;

import com.google.common.base.Strings;
import fr.utbm.ciad.labmanager.configuration.Constants;
import fr.utbm.ciad.labmanager.data.organization.*;
import fr.utbm.ciad.labmanager.services.AbstractEntityService;
import fr.utbm.ciad.labmanager.services.DeletionStatus;
import fr.utbm.ciad.labmanager.utils.country.CountryCode;
import fr.utbm.ciad.labmanager.utils.io.filemanager.DownloadableFileManager;
import fr.utbm.ciad.labmanager.utils.names.OrganizationNameComparator;
import jakarta.transaction.Transactional;
import org.arakhne.afc.vmutil.FileSystem;
import org.hibernate.Hibernate;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

/** Service for research organizations.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
@Service
public class ResearchOrganizationService extends AbstractEntityService<ResearchOrganization> {

	private final OrganizationAddressRepository addressRepository;

	private final ResearchOrganizationRepository organizationRepository;

	private DownloadableFileManager fileManager;

	private OrganizationNameComparator organizationComparator;

	/** Constructor for injector.
	 * This constructor is defined for being invoked by the IOC injector.
	 *
	 * @param addressRepository the address repository.
	 * @param organizationRepository the organization repository.
	 * @param fileManager the manager of the uploaded and downloadable files.
	 * @param messages the provider of localized messages.
	 * @param constants the accessor to the live constants.
	 * @param sessionFactory the Hibernate session factory.
	 */
	public ResearchOrganizationService(
			@Autowired OrganizationAddressRepository addressRepository,
			@Autowired ResearchOrganizationRepository organizationRepository,
			@Autowired DownloadableFileManager fileManager,
			@Autowired OrganizationNameComparator organizationComparator,
			@Autowired MessageSourceAccessor messages,
			@Autowired Constants constants,
			@Autowired SessionFactory sessionFactory) {			
		super(messages, constants, sessionFactory);
		this.addressRepository = addressRepository;
		this.organizationRepository = organizationRepository;
		this.fileManager = fileManager;
		this.organizationComparator = organizationComparator;
	}

	/** Replies the file manager used by this service.
	 *
	 * @return the file manager.
	 * @since 4.0
	 */
	public DownloadableFileManager getFileManager() {
		return this.fileManager;
	}

	/** Replies all the research organizations.
	 *
	 * @return the research organizations.
	 */
	public List<ResearchOrganization> getAllResearchOrganizations() {
		return this.organizationRepository.findAll();
	}

	/** Replies all the research organizations.
	 *
	 * @param filter the filter of organizations.
	 * @return the research organizations.
	 * @since 4.0
	 */
	public List<ResearchOrganization> getAllResearchOrganizations(Specification<ResearchOrganization> filter) {
		return this.organizationRepository.findAll(filter);
	}

	/** Replies all the research organizations.
	 *
	 * @param filter the filter of organizations.
	 * @param sortOrder the order specification to use for sorting the organizations.
	 * @return the research organizations.
	 * @since 4.0
	 */
	public List<ResearchOrganization> getAllResearchOrganizations(Specification<ResearchOrganization> filter, Sort sortOrder) {
		return this.organizationRepository.findAll(filter, sortOrder);
	}

	/** Replies all the research organizations.
	 *
	 * @param sortOrder the order specification to use for sorting the organizations.
	 * @return the research organizations.
	 * @since 4.0
	 */
	public List<ResearchOrganization> getAllResearchOrganizations(Sort sortOrder) {
		return this.organizationRepository.findAll(sortOrder);
	}

	/** Replies all the research organizations.
	 *
	 * @param pageable the manager of the pages.
	 * @return the research organizations.
	 * @since 4.0
	 */
	public Page<ResearchOrganization> getAllResearchOrganizations(Pageable pageable) {
		return this.organizationRepository.findAll(pageable);
	}

	/** Replies all the research organizations.
	 *
	 * @param pageable the manager of the pages.
	 * @param filter the filter of organizations.
	 * @return the research organizations.
	 * @since 4.0
	 */
	public Page<ResearchOrganization> getAllResearchOrganizations(Pageable pageable, Specification<ResearchOrganization> filter) {
		return this.organizationRepository.findAll(filter, pageable);
	}

	/** Replies all the research organizations.
	 *
	 * @param pageable the manager of the pages.
	 * @param filter the filter of organizations.
	 * @param initializer callback function that is invoked for each loaded organization to initialize its properties.
	 * @return the research organizations.
	 * @since 4.0
	 */
	@Transactional
	public Page<ResearchOrganization> getAllResearchOrganizations(Pageable pageable, Specification<ResearchOrganization> filter, Consumer<ResearchOrganization> initializer) {
		final var page = this.organizationRepository.findAll(filter, pageable);
		if (initializer != null) {
			page.forEach(initializer);
		}
		return page;
	}

	/** Replies all the super organizations of the given research organization.
	 *
	 * @param subOrganization the sub organization to start from.
	 * @param pageable the manager of the pages.
	 * @param filter the filter of organizations.
	 * @param initializer callback function that is invoked for each loaded organization to initialize its properties.
	 * @return the research organizations.
	 * @since 4.0
	 */
	@Transactional
	public Page<ResearchOrganization> getSuperResearchOrganizations(ResearchOrganization subOrganization, Pageable pageable, Specification<ResearchOrganization> filter, Consumer<ResearchOrganization> initializer) {
		final var page = this.organizationRepository.findSuperOrganizations(subOrganization, pageable, filter);
		if (initializer != null) {
			page.forEach(initializer);
		}
		return page;
	}

	/** Replies the research organization with the given identifier.
	 *
	 * @param identifier the identifier to search for.
	 * @return the research organization.
	 */
	public Optional<ResearchOrganization> getResearchOrganizationById(long identifier) {
		return this.organizationRepository.findById(Long.valueOf(identifier));
	}

	/** Replies the research organization with the given acronym.
	 *
	 * @param acronym the acronym to search for.
	 * @return the research organization.
	 */
	public Optional<ResearchOrganization> getResearchOrganizationByAcronym(String acronym) {
		return this.organizationRepository.findDistinctByAcronym(acronym);
	}

	/** Replies the default research organization. This function gets the acronym of the default organization from the application configuration.
	 *
	 * @return the default research organization, or {@code null} if it is not defined.
	 * @see Constants#getDefaultOrganization()
	 * @since 4.0
	 */
	public ResearchOrganization getDefaultOrganization() {
		return getResearchOrganizationByAcronymOrName(getApplicationConstants().getDefaultOrganization()).get();
	}

	/** Replies the default research super organization. This function gets the acronym of the default organization from the application configuration.
	 *
	 * @return the default super organization, or {@code null} if it is not defined.
	 * @see Constants#getDefaultSuperOrganization()
	 * @since 4.0
	 */
	public ResearchOrganization getDefaultSuperOrganization() {
		return getResearchOrganizationByAcronymOrName(getApplicationConstants().getDefaultSuperOrganization()).get();
	}

	/** Replies the default LEAR organization. This function gets the acronym of the default organization from the application configuration.
	 *
	 * @return the default LEAR organization, or {@code null} if it is not defined.
	 * @see Constants#getDefaultLearOrganization()
	 * @since 4.0
	 */
	public ResearchOrganization getDefaultLearOrganization() {
		return getResearchOrganizationByAcronymOrName(getApplicationConstants().getDefaultLearOrganization()).get();
	}

	/** Replies the research organization with the given name.
	 *
	 * @param name the name to search for.
	 * @return the research organization.
	 */
	public Optional<ResearchOrganization> getResearchOrganizationByName(String name) {
		return this.organizationRepository.findDistinctByName(name);
	}

	/** Replies the research organization with the given acronym or name.
	 *
	 * @param text the text to search for.
	 * @return the research organization.
	 */
	public Optional<ResearchOrganization> getResearchOrganizationByAcronymOrName(String text) {
		return this.organizationRepository.findDistinctByAcronymOrName(text, text);
	}

	public long getResearchOrganizationIdBySimilarAcronymOrName(String acronyme, String name) {
		var orga = getResearchOrganizationBySimilarAcronymOrName(acronyme, name);
		if (orga != null) {
			return orga.getId();
		} else {
			return 0;
		}
	}

	public ResearchOrganization getResearchOrganizationBySimilarAcronymOrName(String acronyme, String name) {
		if (!Strings.isNullOrEmpty(acronyme) || !Strings.isNullOrEmpty(name)) {
			for (final var orga : this.organizationRepository.findAll()) {
				if (this.organizationComparator.isSimilar(acronyme, name, orga.getAcronym(), orga.getName())) {
					return orga;
				}
			}
		}
		return null;
	}

	/** Create a research organization.
	 *
	 * @param validated indicates if the journal is validated by a local authority.
	 * @param acronym the new acronym for the research organization.
	 * @param name the new name for the research organization.
	 * @param isMajor indicates if the organization is a major organization.
	 * @param rnsr the number of the organization in the RNSR.
	 * @param nationalIdentifier the number of the organization for the national minitry of research.
	 * @param description the new description for the research organization.
	 * @param type the type of the research organization.
	 * @param organizationURL the web-site URL of the research organization.
	 * @param country the country of the research organization.
	 * @param addresses the identifiers of the addresses of the organization.
	 * @param superOrganizations the identifiers of the super organizations.
	 * @param pathToLogo the uploaded logo of the organization, if any.
	 * @return the created organization in the database.
	 * @throws IOException if the logo cannot be uploaded or removed.
	 */
	public Optional<ResearchOrganization> createResearchOrganization(boolean validated, String acronym, String name,
			boolean isMajor,
			String rnsr, String nationalIdentifier, String description,
			ResearchOrganizationType type, String organizationURL, CountryCode country, List<Long> addresses,
			Set<Long> superOrganizations,
			MultipartFile pathToLogo) throws IOException {
		final var sres = new HashSet<ResearchOrganization>();
		if (superOrganizations != null && !superOrganizations.isEmpty()) {
			for (final var sorgId : superOrganizations) {
				final var sorg = this.organizationRepository.findById(sorgId);
				if (sres.isEmpty()) {
					throw new IllegalArgumentException("Research organization not found with id: " + sorgId); //$NON-NLS-1$
				}
				sres.add(sorg.get());
			}
		}
		//
		final Set<OrganizationAddress> adrs;
		if (addresses != null && !addresses.isEmpty()) {
			adrs = this.addressRepository.findAllByIdIn(addresses);
		} else {
			adrs = null;
		}
		//
		final var res = new ResearchOrganization();
		res.setAcronym(Strings.emptyToNull(acronym));
		res.setName(Strings.emptyToNull(name));
		res.setMajorOrganization(isMajor);
		res.setRnsr(Strings.emptyToNull(rnsr));
		res.setNationalIdentifier(Strings.emptyToNull(nationalIdentifier));
		res.setDescription(Strings.emptyToNull(description));
		res.setType(type);
		res.setOrganizationURL(Strings.emptyToNull(organizationURL));
		res.setCountry(country);
		res.setValidated(validated);
		if (adrs != null && !adrs.isEmpty()) {
			res.setAddresses(adrs);
		}
		if (!sres.isEmpty()) {
			res.setSuperOrganizations(sres);
		}
		this.organizationRepository.save(res);
		//
		if (updateLogo(res, false, pathToLogo)) {
			this.organizationRepository.save(res);
		}
		return Optional.of(res);
	}

	/** Change the information associated to a research organization.
	 *
	 * @param identifier the identifier of the research organization to be updated.
	 * @param validated indicates if the journal is validated by a local authority.
	 * @param acronym the new acronym for the research organization.
	 * @param name the new name for the research organization.
	 * @param isMajor indicates if the organization is a major organization.
	 * @param rnsr the number of the organization in the RNSR.
	 * @param nationalIdentifier the identifier of the organization for the national ministry of research.
	 * @param description the new description for the research organization.
	 * @param type the type of the research organization.
	 * @param organizationURL the web-site URL of the research organization.
	 * @param country the country of the research organization.
	 * @param addresses the identifiers of the addresses of the organization.
	 * @param superOrganizations the identifiers of the super organizations.
	 * @param pathToLogo the uploaded logo of the organization, if any.
	 * @param removePathToLogo indicates if the path to the logo in the database should be removed, possibly before saving a new logo.
	 * @return the organization object that was updated.
	 * @throws IOException if the logo cannot be uploaded or removed.
	 */
	public Optional<ResearchOrganization> updateResearchOrganization(long identifier, boolean validated, String acronym,
			String name, boolean isMajor, String rnsr, String nationalIdentifier, String description,
			ResearchOrganizationType type, String organizationURL, CountryCode country, List<Long> addresses,
			Set<Long> superOrganizations, MultipartFile pathToLogo, boolean removePathToLogo) throws IOException  {
		final var res = this.organizationRepository.findById(Long.valueOf(identifier));
		if (res.isPresent()) {
			final var sres = new HashSet<ResearchOrganization>();
			for (final var orgaId : superOrganizations) {
				final var sorg = this.organizationRepository.findById(orgaId);
				if (sorg.isEmpty()) {
					throw new IllegalArgumentException("Research organization not found with id: " + orgaId); //$NON-NLS-1$
				}
				sres.add(sorg.get());
			}
			//
			final Set<OrganizationAddress> adrs;
			if (addresses != null && !addresses.isEmpty()) {
				adrs = this.addressRepository.findAllByIdIn(addresses);
			} else {
				adrs = null;
			}
			//
			final var organization = res.get();
			if (!Strings.isNullOrEmpty(acronym)) {
				organization.setAcronym(acronym);
			}
			if (!Strings.isNullOrEmpty(name)) {
				organization.setName(name);
			}
			organization.setMajorOrganization(isMajor);
			organization.setRnsr(Strings.emptyToNull(rnsr));
			organization.setNationalIdentifier(Strings.emptyToNull(nationalIdentifier));
			organization.setDescription(Strings.emptyToNull(description));
			organization.setType(type);
			organization.setOrganizationURL(Strings.emptyToNull(organizationURL));
			organization.setCountry(country);
			organization.setAddresses(adrs);
			organization.setValidated(validated);
			if (!sres.isEmpty()) {
				organization.setSuperOrganizations(sres);
			}
			//
			this.organizationRepository.save(organization);
			//
			if (updateLogo(organization, removePathToLogo, pathToLogo)) {
				this.organizationRepository.save(organization);
			}
		}
		return res;
	}

	private boolean updateLogo(ResearchOrganization organization, boolean explicitRemove, MultipartFile uploadedFile) throws IOException {
		final String ext;
		if (uploadedFile != null) {
			ext = FileSystem.extension(uploadedFile.getOriginalFilename());
		} else {
			final var filename = organization.getPathToLogo();
			if (!Strings.isNullOrEmpty(filename)) {
				ext = FileSystem.extension(filename);
			} else {
				ext = null;
			}
		}
		return updateUploadedFile(explicitRemove, uploadedFile,
				"Organization logo uploaded at: ", //$NON-NLS-1$
				it -> organization.setPathToLogo(it),
				() -> this.fileManager.makeOrganizationLogoFilename(organization.getId(), ext),
				() -> this.fileManager.deleteOrganizationLogo(organization.getId(), ext),
				(fn, th) -> this.fileManager.saveImage(fn, uploadedFile));
	}

	/** Link a suborganization to a super organization.
	 * 
	 * @param superIdentifier the identifier of the super organization.
	 * @param subIdentifier the identifier of the suborganization.
	 * @return {@code true} if the link is created; {@code false} if the link cannot be created.
	 */
	public boolean linkSubOrganization(long superIdentifier, long subIdentifier) {
		if (superIdentifier != subIdentifier) {
			final var superOrg = this.organizationRepository.findById(Long.valueOf(superIdentifier));
			if (superOrg.isPresent()) {
				final var subOrg = this.organizationRepository.findById(Long.valueOf(subIdentifier));
				if (subOrg.isPresent()) {
					final var superOrganization = superOrg.get();
					final var subOrganization = subOrg.get();
					if (subOrganization.getSuperOrganizations().add(superOrganization)
							&& superOrganization.getSubOrganizations().add(subOrganization)) {
						this.organizationRepository.save(subOrganization);
						this.organizationRepository.save(superOrganization);
						return true;
					}
				}
			}
		}
		return false;
	}

	/** Unlink a suborganization from a super organization.
	 * 
	 * @param superIdentifier the identifier of the super organization.
	 * @param subIdentifier the identifier of the suborganization.
	 * @return {@code true} if the link is deleted; {@code false} if the link cannot be deleted.
	 */
	public boolean unlinkSubOrganization(long superIdentifier, long subIdentifier) {
		final var superOrg = this.organizationRepository.findById(Long.valueOf(superIdentifier));
		if (superOrg.isPresent()) {
			final var subOrg = this.organizationRepository.findById(Long.valueOf(subIdentifier));
			if (subOrg.isPresent()) {
				final var superOrganization = superOrg.get();
				final var subOrganization = subOrg.get();
				if (superOrganization.getSubOrganizations().remove(subOrganization)) {
					subOrganization.getSuperOrganizations().remove(superOrganization);
					this.organizationRepository.save(superOrganization);
					this.organizationRepository.save(subOrganization);
					return true;
				}
			}
		}
		return false;
	}

	@Override
	public EntityEditingContext<ResearchOrganization> startEditing(ResearchOrganization organization) {
		assert organization != null;
		// Force initialization of the internal properties that are needed for editing
		if (organization.getId() != 0l) {
			inSession(session -> {
				session.load(organization, Long.valueOf(organization.getId()));
				Hibernate.initialize(organization.getAddresses());
				Hibernate.initialize(organization.getSuperOrganizations());
				Hibernate.initialize(organization.getSubOrganizations());
			});
		}
		return new EditingContext(organization);
	}

	@Override
	public EntityDeletingContext<ResearchOrganization> startDeletion(Set<ResearchOrganization> organizations) {
		assert organizations != null && !organizations.isEmpty();
		// Force loading of the linked entities
		inSession(session -> {
			for (final var organization : organizations) {
				if (organization.getId() != 0l) {
					session.load(organization, Long.valueOf(organization.getId()));
					Hibernate.initialize(organization.getAddresses());
					Hibernate.initialize(organization.getSuperOrganizations());
					Hibernate.initialize(organization.getSubOrganizations());
					Hibernate.initialize(organization.getDirectOrganizationMemberships());
					Hibernate.initialize(organization.getSuperOrganizationMemberships());
					Hibernate.initialize(organization.getTeachingActivities());
					Hibernate.initialize(organization.getFundedAssociatedStructures());
					Hibernate.initialize(organization.getMainOrganizationAssociatedStructuresHolders());
					Hibernate.initialize(organization.getSuperOrganizationAssociatedStructuresHolders());
					Hibernate.initialize(organization.getCoordinatedProjects());
					Hibernate.initialize(organization.getLearOrganizationProjects());
					Hibernate.initialize(organization.getLocalOrganizationProjects());
					Hibernate.initialize(organization.getOtherPartnerProjects());
					Hibernate.initialize(organization.getSuperOrganizationProjects());
				}
			}
		});
		return new DeletingContext(organizations);
	}

	/** Context for editing a {@link ResearchOrganization}.
	 * This context is usually defined when the entity is associated to
	 * external resources in the server file system.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class EditingContext extends AbstractEntityWithServerFilesEditingContext<ResearchOrganization> {

		private static final long serialVersionUID = 23311671802716077L;

		private UploadedFileTracker<ResearchOrganization> pathToLogo;

		private Set<ResearchOrganization> originalSuperOrganizations;

		/** Constructor.
		 *
		 * @param organization the edited organization.
		 */
		EditingContext(ResearchOrganization organization) {
			super(organization);
			this.pathToLogo = newUploadedFileTracker(organization,
					ResearchOrganization::getPathToLogo,
					(id, savedPath) -> {
						if (!Strings.isNullOrEmpty(savedPath)) {
							final var ext = FileSystem.extension(savedPath);
							if (!Strings.isNullOrEmpty(ext)) {
								ResearchOrganizationService.this.fileManager.deleteOrganizationLogo(id.longValue(), ext);
							}
						}
					},
					null);
			this.originalSuperOrganizations = new HashSet<>(organization.getSuperOrganizations());
		}

		@Override
		protected ResearchOrganization writeInJPA(ResearchOrganization entity, boolean initialSaving) {
			if (initialSaving) {
				// Compute the set of organizations that are removed or added in the list of super organizations
				final var addedOrganizations = new HashSet<ResearchOrganization>();
				final var removedOrganizations = new HashSet<>(this.originalSuperOrganizations);
				for (final var superOrganization : this.entity.getSuperOrganizations()) {
					if (!removedOrganizations.remove(superOrganization)) {
						addedOrganizations.add(superOrganization);
					}
				}
	
				// Update the links between the organizations
				final var orgas = new ArrayList<ResearchOrganization>();
				orgas.add(this.entity);
				if (!addedOrganizations.isEmpty() || !removedOrganizations.isEmpty()) {
					inSession(session -> {
						for (final var addedOrganization : addedOrganizations) {
							// The following line is here for ensuring that proxys are initialized
							final var addedOrganization0 = session.getReference(ResearchOrganization.class, Long.valueOf(addedOrganization.getId()));
							Hibernate.initialize(addedOrganization0);
							addedOrganization0.getSubOrganizations().add(this.entity);
							orgas.add(addedOrganization0);
						}
						for (final var removedOrganization : removedOrganizations) {
							// The following line is here for ensuring that proxys are initialized
							final var removedOrganization0 = session.getReference(ResearchOrganization.class, Long.valueOf(removedOrganization.getId()));
							Hibernate.initialize(removedOrganization0);
							removedOrganization0.getSubOrganizations().remove(this.entity);
							orgas.add(removedOrganization0);
						}
					});
				}
	
				final var changedOrganizations = ResearchOrganizationService.this.organizationRepository.saveAll(orgas);
				return changedOrganizations.stream().filter(it -> it.equals(this.entity)).findAny().orElse(entity);
			}
			return ResearchOrganizationService.this.organizationRepository.save(this.entity);
		}

		@Override
		protected void deleteOrRenameAssociatedFiles(long oldId) throws IOException {
			this.pathToLogo.deleteOrRenameFile(oldId, this.entity);
		}

		@Override
		protected boolean prepareAssociatedFileUpload() throws IOException {
			return !this.pathToLogo.deleteFile(this.entity);
		}

		@Override
		protected void postProcessAssociatedFiles() {
			this.pathToLogo.resetPathMemory(this.entity);
			this.originalSuperOrganizations = new HashSet<>(this.entity.getSuperOrganizations());
		}

		@Override
		public EntityDeletingContext<ResearchOrganization> createDeletionContext() {
			return ResearchOrganizationService.this.startDeletion(Collections.singleton(this.entity));
		}

	}

	/** Context for deleting a {@link ResearchOrganization}.
	 * 
	 * @author $Author: sgalland$
	 * @version $Name$ $Revision$ $Date$
	 * @mavengroupid $GroupId$
	 * @mavenartifactid $ArtifactId$
	 * @since 4.0
	 */
	protected class DeletingContext extends AbstractEntityDeletingContext<ResearchOrganization> {

		private static final long serialVersionUID = -5859881441705651933L;

		/** Constructor.
		 *
		 * @param organizations the organizations to delete.
		 */
		protected DeletingContext(Set<ResearchOrganization> organizations) {
			super(organizations);
		}

		@Override
		protected DeletionStatus computeDeletionStatus() {
			for(final var entity : getEntities()) {
				if (!entity.getTeachingActivities().isEmpty()) {
					return OrganizationDeletionStatus.TEACHING_ACTIVITY;
				}
				if (!entity.getFundedAssociatedStructures().isEmpty()) {
					return OrganizationDeletionStatus.FUNDED_ASSOCIATED_STRUCTURE;
				}
				if (!entity.getMainOrganizationAssociatedStructuresHolders().isEmpty()) {
					return OrganizationDeletionStatus.MAIN_ORGANIZATION_ASSOCIATED_STRUCTURE_HOLDER;
				}
				if (!entity.getSuperOrganizationAssociatedStructuresHolders().isEmpty()) {
					return OrganizationDeletionStatus.SUPER_ORGANIZATION_ASSOCIATED_STRUCTURE_HOLDER;
				}
				if (!entity.getDirectOrganizationMemberships().isEmpty()) {
					return OrganizationDeletionStatus.DIRECT_ORGANIZATION_MEMBERSHIP;
				}
				if (!entity.getSuperOrganizationMemberships().isEmpty()) {
					return OrganizationDeletionStatus.SUPER_ORGANIZATION_MEMBERSHIP;
				}
				if (!entity.getCoordinatedProjects().isEmpty()) {
					return OrganizationDeletionStatus.PROJECT_COORDINATOR;
				}
				if (!entity.getLearOrganizationProjects().isEmpty()) {
					return OrganizationDeletionStatus.PROJECT_LEAR_ORGANIZATION;
				}
				if (!entity.getLocalOrganizationProjects().isEmpty()) {
					return OrganizationDeletionStatus.PROJECT_LOCAL_ORGANIZATION;
				}
				if (!entity.getOtherPartnerProjects().isEmpty()) {
					return OrganizationDeletionStatus.PROJECT_OTHER_PARTNER_ORGANIZATION;
				}
				if (!entity.getSuperOrganizationProjects().isEmpty()) {
					return OrganizationDeletionStatus.PROJECT_SUPER_ORGANIZATION;
				}
			}
			return DeletionStatus.OK;
		}

		@Override
		protected void deleteEntities(Collection<Long> identifiers) throws Exception {
			// Unlink super and suborganizations
			final var updatedEntities = new ArrayList<ResearchOrganization>();
			for (final var orga : getEntities()) {
				for (final var superOrganization : orga.getSuperOrganizations()) {
					updatedEntities.add(superOrganization);
					superOrganization.getSubOrganizations().remove(orga);
				}
				orga.getSuperOrganizations().clear();
				for (final var subOrganization : orga.getSubOrganizations()) {
					updatedEntities.add(subOrganization);
					subOrganization.getSuperOrganizations().remove(orga);
				}
				orga.getSubOrganizations().clear();
				updatedEntities.add(orga);
			}
			ResearchOrganizationService.this.organizationRepository.saveAllAndFlush(updatedEntities);

			// Do the deletion
			ResearchOrganizationService.this.organizationRepository.deleteAllById(getDeletableEntityIdentifiers());

			for (final var id : identifiers) {
				ResearchOrganizationService.this.fileManager.deleteOrganizationLogo(id.longValue());
			}
		}

	}

}
