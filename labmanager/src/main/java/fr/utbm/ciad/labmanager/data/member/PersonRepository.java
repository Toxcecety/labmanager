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

package fr.utbm.ciad.labmanager.data.member;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

/** JPA Repository for the persons.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface PersonRepository extends JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {

	/** Replies a person who has the given first and last names.
	 * 
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @return the person.
	 */
	Optional<Person> findDistinctByFirstNameAndLastName(String firstName, String lastName);

	/** Replies a person who has the given identifier for her/his webpage.
	 * 
	 * @param identifier the expected identifier of the webpage of the person.
	 * @return the person.
	 */
	Optional<Person> findDistinctByWebPageId(String identifier);

	/** Replies the persons who have the given first and last names.
	 * 
	 * @param firstName the first name of the person.
	 * @param lastName the last name of the person.
	 * @return the persons.
	 */
	Set<Person> findByFirstNameAndLastName(String firstName, String lastName);

	/** Replies the persons in the organization with the given name and who have the given status.
	 *
	 * @param organizationName the name of the organization.
	 * @param status the member status
	 * @return the persons.
	 */
	Set<Person> findDistinctByMembershipsResearchOrganizationNameAndMembershipsMemberStatus(
			String organizationName, MemberStatus status);

	/** Replies the persons in the organization with the given acronym and who have the given status.
	 *
	 * @param organizationAcronym the acronym of the organization.
	 * @param status the member status
	 * @return the persons.
	 */
	Set<Person> findDistinctByMembershipsResearchOrganizationAcronymAndMembershipsMemberStatus(
			String organizationAcronym, MemberStatus status);

	/** Replies the persons in the organization with the given identifier.
	 *
	 * @param id the identifier of the organization.
	 * @return the persons.
	 */
	Set<Person> findDistinctByMembershipsResearchOrganizationId(long id);

	/** Replies the persons who authored the publication with the given identifier.
	 *
	 * @param id the identifier of the publication.
	 * @return the persons.
	 */
	List<Person> findByAuthorshipsPublicationIdOrderByAuthorshipsAuthorRank(long id);


	long count();

	@Query("SELECT p FROM Person p LEFT JOIN FETCH p.authorships m WHERE p.id = :id")
	Optional<Person> findByIdJoinAuthorships(long id);

	@NotNull
	@Modifying
	@Query("SELECT p FROM Person p")
	List<Person> findAll();

	@Query("SELECT p FROM Person p JOIN p.memberships m WHERE LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :organization, '%'))")
	Page<Person> findAll(String organization, Pageable pageable);

	@Query("SELECT p FROM Person p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
	Page<Person> findByName(String name, Pageable pageable);
	@Query("SELECT p FROM Person p JOIN p.memberships m WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%')) AND LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :organization, '%'))")
	Page<Person> findByName(String name, String organization, Pageable pageable);

	@Query("SELECT count(p) FROM Person p WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))")
	long countFindByName(String name);

	@Query("SELECT count(p) FROM Person p JOIN p.memberships m WHERE LOWER(p.firstName) LIKE LOWER(CONCAT('%', :name, '%')) OR LOWER(p.lastName) LIKE LOWER(CONCAT('%', :name, '%'))  AND LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :organization, '%'))")
	long countFindByName(String name, String organization);

	@Query("SELECT p FROM Person p WHERE LOWER(p.orcid) LIKE LOWER(CONCAT('%', :orcid, '%'))")
	Page<Person> findByOrcid(String orcid, Pageable pageable);

	@Query("SELECT p FROM Person p JOIN p.memberships m WHERE LOWER(p.orcid) LIKE LOWER(CONCAT('%', :orcid, '%')) AND LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :organization, '%'))")
	Page<Person> findByOrcid(String orcid, String organization, Pageable pageable);

	@Query("SELECT count(p) FROM Person p WHERE LOWER(p.orcid) ILIKE LOWER(CONCAT('%', :orcid, '%'))")
	long countFindByOrcid(String orcid);

	@Query("SELECT count(p) FROM Person p JOIN p.memberships m WHERE LOWER(p.orcid) ILIKE LOWER(CONCAT('%', :orcid, '%')) AND LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :organization, '%'))")
	long countFindByOrcid(String orcid, String organization);

	@Query("SELECT p FROM Person p JOIN p.memberships m WHERE LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :orgName, '%'))")
	Page<Person> findByOrganization(String orgName, Pageable pageable);

	@Query("SELECT p FROM Person p JOIN p.memberships m WHERE LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :orgName, '%')) AND LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :organization, '%'))")
	Page<Person> findByOrganization(String orgName, String organization, Pageable pageable);

	@Query("SELECT count(p) FROM Person p JOIN p.memberships m WHERE LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :orgName, '%'))")
	long countFindByOrganization(String orgName);

	@Query("SELECT count(p) FROM Person p JOIN p.memberships m WHERE LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :orgName, '%')) AND LOWER(m.researchOrganization.acronym) LIKE LOWER(CONCAT('%', :organization, '%'))")
	long countFindByOrganization(String orgName, String organization);
}
