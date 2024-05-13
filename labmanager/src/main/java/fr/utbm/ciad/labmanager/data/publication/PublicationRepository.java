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

package fr.utbm.ciad.labmanager.data.publication;

import java.util.*;

import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.repository.query.Param;

/** JPA repository for a publication.
 * 
 * @author $Author: sgalland$
 * @author $Author: tmartine$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 */
public interface PublicationRepository extends JpaRepository<Publication, Long>, JpaSpecificationExecutor<Publication> {

	/** Replies the list of publications for the person with the given identifier.
	 *
	 * @param personId the identifier of the person.
	 * @return the list of publications.
	 */
	List<Publication> findAllByAuthorshipsPersonId(long personId);

	/** Replies the list of publications for the person with the given webpage identifier.
	 *
	 * @param webpageId the identifier of the webpage of the person.
	 * @return the list of publications.
	 */
	List<Publication> findAllByAuthorshipsPersonWebPageId(String webpageId);

	/** Replies the list of publications for the persons with the given identifiers.
	 *
	 * @param personIds the list of identifiers of the authors.
	 * @return the list of publications.
	 */
	Set<Publication> findAllByAuthorshipsPersonIdIn(Set<Long> personIds);
	
	/** Replies the list of publictions for the given identifiers.
	 *
	 * @param identifiers the identifiers to search for.
	 * @return the publications.
	 * @since 2.5
	 */
	Set<Publication> findAllByIdIn(Collection<Long> identifiers);

	/** Replies the list of publications with the given title.
	 *
	 * @param title the title to search for, with case insensitive test.
	 * @return the set of publications with the given title.
	 */
	List<Publication> findAllByTitleIgnoreCase(String title);

	/** Replies the list of publications for the given year.
	 *
	 * @param year the year of publication.
	 * @return the set of publications for the given year.
	 * @since 3.6
	 */
	List<Publication> findAllByPublicationYear(Integer publicationYear);

	/** Replies the list of year.
	 *
	 * @return the set of years.
	 * @since 3.6
	 */
	@Query("SELECT COUNT(p) AS publicationCount FROM Publication p GROUP BY p.publicationYear")
	List<Long> countPublicationsByYear();

	// Méthode pour obtenir les années des publications
	@Query("SELECT DISTINCT p.publicationYear FROM Publication p")
	List<Integer> findDistinctPublicationYears();

	@Query("SELECT p.type AS publicationType, COUNT(p) AS publicationCount FROM Publication p WHERE p.publicationYear = :year GROUP BY p.type ORDER BY p.type ASC")
	List<Map<String, Long>> countPublicationsByTypeForYear(@Param("year") int year);

	@Query("SELECT COUNT(p) AS publicationCount FROM Publication p WHERE p.type = :type GROUP BY p.publicationYear ORDER BY p.publicationYear ASC")
	List<Map<String, Long>> countPublicationsByYearForTypeOrdered(@Param("type") PublicationType type);

	@Query("SELECT DISTINCT p.type FROM Publication p")
	List<String> findAllDistinctPublicationTypes();

	@Query("SELECT COUNT(p) AS publicationCount FROM Publication p WHERE p.type = :type AND p.publicationYear = :year")
	Integer countPublicationsForTypeAndYearV2(@Param("type") PublicationType type, @Param("year") Integer year);

}
