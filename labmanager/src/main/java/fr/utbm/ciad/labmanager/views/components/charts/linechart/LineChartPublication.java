package fr.utbm.ciad.labmanager.views.components.charts.linechart;

import com.storedobject.chart.*;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

public class LineChartPublication extends LineChart{

    private PublicationService publicationService;

    public LineChartPublication(@Autowired PublicationService publicationService){

        this.publicationService = publicationService;

        Data xValues = new Data();
        Data yValues = new Data();

        List<Integer> years = this.publicationService.getAllYears();
        List<Long> countPublication = this.publicationService.getCountPublicationsByYear();

        for(int i=0; i < years.size(); i++){

            xValues.add(Integer.valueOf(years.get(i)));
            yValues.add(countPublication.get(i));
        }

        xValues.setName("Years");
        yValues.setName("Number of publications");

        this.setXData(xValues);
        this.setYData(yValues);


    }
}
