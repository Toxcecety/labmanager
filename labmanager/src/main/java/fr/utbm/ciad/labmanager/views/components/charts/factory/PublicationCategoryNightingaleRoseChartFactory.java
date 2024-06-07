package fr.utbm.ciad.labmanager.views.components.charts.factory;

import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryNightingaleRoseChart;

/** Implementation of a factory for a publication category nightingale rose chart.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class PublicationCategoryNightingaleRoseChartFactory implements PublicationCategoryChartFactory<PublicationCategoryNightingaleRoseChart>{

    /** Create an object of type PublicationCategoryNightingaleRoseChart.
     *
     * @param publicationService the service for accessing the scientific publications.
     * @return PublicationCategoryNightingaleRoseChart.
     */
    @Override
    public PublicationCategoryNightingaleRoseChart create(PublicationService publicationService) {
        return new PublicationCategoryNightingaleRoseChart(publicationService);
    }
}
