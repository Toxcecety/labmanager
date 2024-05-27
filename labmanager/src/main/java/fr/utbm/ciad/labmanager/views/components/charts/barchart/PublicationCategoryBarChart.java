package fr.utbm.ciad.labmanager.views.components.charts.barchart;

import com.storedobject.chart.*;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.charts.AbstractPublicationCategoryChart;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.storedobject.chart.Color.TRANSPARENT;

public class PublicationCategoryBarChart extends AbstractPublicationCategoryChart {

    private PublicationService publicationService;
    private Data xValues;
    private List<BarChart> barChartList;
    private CategoryData categoryData;
    private CoordinateSystem rectangularCoordinate;
    private XAxis xAxis;
    private YAxis yAxis;
    private LineChart lineChart;
    private List<Integer> totalPublication;
    private List<PublicationType> publicationTypes;
    private Legend legend;

    public PublicationCategoryBarChart(@Autowired PublicationService publicationService) {
        super(publicationService);


        this.publicationService = publicationService;
        barChartList = new ArrayList<>();

        // xValues
        xValues = new Data();
        categoryData = new CategoryData();


        xValues.setName("Years");

        // yValues
        publicationTypes = getPublicationTypeList();
        barChartList = new ArrayList<>();

        totalPublication = new ArrayList<>();


        // Rectangular coordinates
        rectangularCoordinate = new RectangularCoordinate();
        yAxis = new YAxis(DataType.NUMBER);
        xAxis = new XAxis(categoryData);
        xAxis.setName("Years");

        rectangularCoordinate.addAxis(xAxis, yAxis);
        rectangularCoordinate.getPosition(true).setTop(Size.percentage(15));

        legend = new Legend();
        legend.getPosition(true).setLeft(Size.percentage(1));
        legend.getPosition(true).setTop(Size.percentage(6));


        disableDefaultLegend();
        add(legend, rectangularCoordinate);
        setSVGRendering();
        setDefaultBackground(TRANSPARENT);
    }



    public void addData(String chosenCategory) {
        Data data = new Data();
        Integer countTypePublicationV2;
        List<PublicationType> temporaryPublicationTypeList = publicationTypes.stream().filter(publicationType -> Objects.equals(publicationType.getCategory(true).toString(), chosenCategory)).toList();
        Integer totalYearCount = 0;
        for(int x = 0; x < getYears().size(); x++){
            for (PublicationType publicationType : temporaryPublicationTypeList ) {
                countTypePublicationV2 = this.publicationService.getCountPublicationByTypeByYearV2(publicationType, getYears().get(x));
                totalYearCount += countTypePublicationV2;
                totalPublication.set(x, countTypePublicationV2 + totalPublication.get(x));

            }
            data.add(totalYearCount);
            totalYearCount = 0;
        }


        BarChart barChart = new BarChart(categoryData, data);
        barChart.setName(chosenCategory);
        barChart.setStackName("BC");

        //System.out.println(totalPublication);

        barChartList.add(barChart);

    }

    public void removeData(String chosenCategory) {
        BarChart barChart = findBarChart(chosenCategory);
        Integer countTypePublicationV2;
        List<PublicationType> temporaryPublicationTypeList = publicationTypes.stream().filter(publicationType -> Objects.equals(publicationType.getCategory(true).toString(), chosenCategory)).toList();
        for(int x = 0; x < getYears().size(); x++){
            for (PublicationType publicationType : temporaryPublicationTypeList ) {
                countTypePublicationV2 = this.publicationService.getCountPublicationByTypeByYearV2(publicationType, getYears().get(x));
                totalPublication.set(x, countTypePublicationV2 - totalPublication.get(x));
            }
        }
        barChartList.remove(barChart);
    }

    @Override
    public SOChart createChart() {

        for (BarChart b : barChartList) {
            b.plotOn(rectangularCoordinate);
        }

        Data data = new Data();
        data.addAll(totalPublication);

        lineChart = new LineChart(categoryData,data);
        lineChart.setName("Total of publications");
        lineChart.plotOn(rectangularCoordinate);

        return this;
    }

    @Override
    public void setYear(Integer start) {
        getYears().clear();
        categoryData = new CategoryData();
        xValues = new Data();

        getYears().add(start);

        xValues.add(start);
        categoryData.add(start.toString());

        barChartList.clear();

        for(int i = 0; i < getYears().size(); i++){
            totalPublication.add(0);
        }

    }

    @Override
    public void setPeriod(Integer start, Integer end) {
        getYears().clear();
        categoryData = new CategoryData();
        xValues = new Data();

        Integer imp = start;
        while(imp <= end){
            getYears().add(imp);
            imp++;
        }

        for (Integer year : getYears()) {
            xValues.add(year);
            categoryData.add(year.toString());
        }

        barChartList.clear();

        for(int i = 0; i < getYears().size(); i++){
            totalPublication.add(0);
        }

    }


    protected BarChart findBarChart(String item){
        for (BarChart barChart : barChartList) {
            if (item.equals(barChart.getName())) {
                return barChart;
            }
        }
        return null;
    }


}
