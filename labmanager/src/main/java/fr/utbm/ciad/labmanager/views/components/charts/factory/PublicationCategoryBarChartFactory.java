package fr.utbm.ciad.labmanager.views.components.charts.factory;

import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryBarChart;

/** Implementation of a factory for a publication category bar chart.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class PublicationCategoryBarChartFactory implements PublicationCategoryChartFactory<PublicationCategoryBarChart> {

    /** Create an object of type PublicationCategoryBarChart.
     *
     * @param publicationService the service for accessing the scientific publications.
     * @return PublicationCategoryBarChart.
     */
    @Override
    public PublicationCategoryBarChart create(PublicationService publicationService) {
        return new PublicationCategoryBarChart(publicationService);
    }
}
