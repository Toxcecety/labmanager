package fr.utbm.ciad.labmanager.views.components.charts.factory;

import fr.utbm.ciad.labmanager.data.publication.PublicationCategory;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.PublicationCategoryPieChart;
import org.springframework.beans.factory.annotation.Autowired;

public class PublicationCategoryPieChartFactory implements PublicationCategoryChartFactory<PublicationCategoryPieChart>{

    @Override
    public PublicationCategoryPieChart create(@Autowired PublicationService publicationService) {
        return new PublicationCategoryPieChart(publicationService);
    }
}
