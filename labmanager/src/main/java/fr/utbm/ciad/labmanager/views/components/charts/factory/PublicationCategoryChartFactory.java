package fr.utbm.ciad.labmanager.views.components.charts.factory;

import fr.utbm.ciad.labmanager.data.publication.PublicationCategory;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

/** Interface that represents a factory for publication category charts.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public interface PublicationCategoryChartFactory<T> {

    /** Create an object of type T.
     *
     * @param publicationService the service for accessing the scientific publications.
     * @return T.
     */
    T create(@Autowired PublicationService publicationService);
}
