package fr.utbm.ciad.labmanager.views.components.charts.factory;

import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.PublicationCategoryNightingaleRoseChart;

public class PublicationCategoryNightingaleRoseChartFactory implements PublicationCategoryChartFactory<PublicationCategoryNightingaleRoseChart>{
    @Override
    public PublicationCategoryNightingaleRoseChart create(PublicationService publicationService) {
        return new PublicationCategoryNightingaleRoseChart(publicationService);
    }
}
