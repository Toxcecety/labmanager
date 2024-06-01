package fr.utbm.ciad.labmanager.views.components.charts.factory;

import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.publicationcategory.PublicationCategoryPieChart;
import org.springframework.beans.factory.annotation.Autowired;

/** Implementation of a factory for a publication category pie chart.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public class PublicationCategoryPieChartFactory implements PublicationCategoryChartFactory<PublicationCategoryPieChart>{

    /** Create an object of type PublicationCategoryPieChart.
     *
     * @param publicationService the service for accessing the scientific publications.
     * @return PublicationCategoryPieChart.
     */
    @Override
    public PublicationCategoryPieChart create(@Autowired PublicationService publicationService) {
        return new PublicationCategoryPieChart(publicationService);
    }
}
