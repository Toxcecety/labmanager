package fr.utbm.ciad.labmanager.views.components.charts.factory;

import fr.utbm.ciad.labmanager.data.publication.PublicationCategory;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.barchart.PublicationCategoryBarChart;

public class PublicationCategoryBarChartFactory implements PublicationCategoryChartFactory<PublicationCategoryBarChart> {
    @Override
    public PublicationCategoryBarChart create(PublicationService publicationService) {
        return new PublicationCategoryBarChart(publicationService);
    }
}
