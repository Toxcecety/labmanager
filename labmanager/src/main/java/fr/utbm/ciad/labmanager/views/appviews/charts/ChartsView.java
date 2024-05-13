package fr.utbm.ciad.labmanager.views.appviews.charts;

import com.storedobject.chart.*;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.charts.barchart.BarChartPublication;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route(value = "charts", layout = MainLayout.class)
@PermitAll
public class ChartsView extends HorizontalLayout {

    private PublicationService publicationService;


    public ChartsView(@Autowired PublicationService publicationService) {

        this.publicationService = publicationService;

        SOChart soChart = new SOChart();
        soChart.setSize("1300px", "550px");


        BarChartPublication barChartPublication = new BarChartPublication(this.publicationService);
        List<BarChart> barChartList = barChartPublication.getBarChartList();

        CategoryData categoryData = barChartPublication.getCategoryData();
        Data xValues = barChartPublication.getXValues();
        List<Integer> years = barChartPublication.getYears();

        List<Long> countPublication = this.publicationService.getCountPublicationsByYear();
        Data yValuesLine = new Data();
        for(int i=0; i < years.size(); i++){

            yValuesLine.add(countPublication.get(i));
        }
        LineChart lineChart = new LineChart(categoryData, yValuesLine);
        lineChart.setName("Total of publications");

        YAxis yAxis = new YAxis(DataType.NUMBER);
        XAxis xAxis = new XAxis(categoryData);

        xAxis.setName("Years");

        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        rc.getPosition(true).setTop(Size.percentage(30));
        for(BarChart br: barChartList){
            br.plotOn(rc);
        }

        lineChart.plotOn(rc);

        Title title = new Title("Number of publications per years");
        title.getPosition(true).setLeft(Size.percentage(10));


        soChart.disableDefaultLegend();
        Legend legend = new Legend();
        legend.getPosition(true).setLeft(Size.percentage(1));
        legend.getPosition(true).setTop(Size.percentage(6));
        soChart.add(legend, title,rc );
        add(soChart);
    }

}