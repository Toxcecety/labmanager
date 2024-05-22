package fr.utbm.ciad.labmanager.views.components.charts.barchart;

import com.storedobject.chart.*;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.storedobject.chart.Color.TRANSPARENT;

public class BarChartPublication {

    private PublicationService publicationService;
    private Data xValues;
    private List<BarChart> barChartList;
    private CategoryData categoryData;
    private List<Integer> years;
    private MultiSelectComboBox<String> multiSelectComboBox;
    private CoordinateSystem rectangularCoordinate;
    private SOChart soChart;
    private XAxis xAxis;
    private YAxis yAxis;
    private DataChannel dataChannel;
    private Legend legend;
    private Button button;
    private LineChart lineChart;
    private List<Integer> totalPublication;


    public BarChartPublication(@Autowired PublicationService publicationService) {
        soChart = new SOChart();
        soChart.setSize("1300px", "550px");
        legend = new Legend();


        this.publicationService = publicationService;

        // xValues
        xValues = new Data();
        categoryData = new CategoryData();
        years = this.publicationService.getAllYears();

        for (Integer year : years) {
            xValues.add(year);
            categoryData.add(year.toString());
        }
        xValues.setName("Years");

        // yValues
        List<String> nameType = this.publicationService.getAllType();
        barChartList = new ArrayList<>(nameType.size());

        totalPublication = new ArrayList<>();
        for(int i = 0; i < years.size(); i++){
            totalPublication.add(0);
        }
        System.out.println(totalPublication);


        // Rectangular coordinates
        rectangularCoordinate = new RectangularCoordinate();
        yAxis = new YAxis(DataType.NUMBER);
        xAxis = new XAxis(categoryData);
        xAxis.setName("Years");

        rectangularCoordinate.addAxis(xAxis, yAxis);
        rectangularCoordinate.getPosition(true).setTop(Size.percentage(15));

        /*
        for(String type : nameType) {
            addData(type);
        }
        */


        

        // MultiSelectComboBox for user input





        // Chart title, toolbox, and legend
        Title title = new Title("Number of publications per years");
        title.getPosition(true).setLeft(Size.percentage(10));

        Toolbox toolbox = new Toolbox();
        Toolbox.Download toolboxDownload = new Toolbox.Download();
        toolboxDownload.setResolution(15);
        toolbox.addButton(toolboxDownload, new Toolbox.Zoom());
        toolbox.getPosition(true).setLeft(Size.percentage(80));

        DataZoom dataZoom = new DataZoom(rectangularCoordinate, xAxis);

        soChart.disableDefaultLegend();
        legend.getPosition(true).setLeft(Size.percentage(1));
        legend.getPosition(true).setTop(Size.percentage(6));
        soChart.add(legend, title, rectangularCoordinate, toolbox, dataZoom);
        soChart.setSVGRendering();
        soChart.setDefaultBackground(TRANSPARENT);
    }

    public Data getXValues() {
        return xValues;
    }

    public Data getYValues() {
        Data data = new Data();
        for (Number value : xValues) {
            data.add(300);
        }
        return data;
    }

    public List<BarChart> getBarChartList() {
        return barChartList;
    }

    public int getTypeSize() {
        List<String> nameType = this.publicationService.getAllType();
        return nameType.size();
    }

    public List<Integer> getYears() {
        return years;
    }

    public CategoryData getCategoryData() {
        return categoryData;
    }

    public MultiSelectComboBox<String> getMultiSelectComboBox() {
        return multiSelectComboBox;
    }

    public CoordinateSystem getRectangularCoordinate() {
        return rectangularCoordinate;
    }

    public SOChart getSoChart() {
        return soChart;
    }

    public void addData(String chosenNameType) {
        Data data = new Data();
        Integer countTypePublicationV2;
        for (int x = 0; x < years.size(); x++) {
            countTypePublicationV2 = this.publicationService.getCountPublicationByTypeByYearV2(PublicationType.valueOf(chosenNameType), years.get(x));
            totalPublication.set(x, countTypePublicationV2 + totalPublication.get(x));
            data.add(countTypePublicationV2);
        }

        BarChart barChart = new BarChart(categoryData, data);
        barChart.setName(chosenNameType);
        barChart.setStackName("BC");

        System.out.println(totalPublication);

        barChartList.add(barChart);

    }

    public void removeData(String chosenNameType) {
        BarChart barChart = findBarChart(chosenNameType);
        Integer countTypePublicationV2;
        for (int x = 0; x < years.size(); x++) {
            countTypePublicationV2 = this.publicationService.getCountPublicationByTypeByYearV2(PublicationType.valueOf(chosenNameType), years.get(x));
            totalPublication.set(x,totalPublication.get(x) - countTypePublicationV2);
        }
        barChartList.remove(barChart);
    }

    public void plot() {
        for (BarChart b : barChartList) {
            b.plotOn(rectangularCoordinate);
        }

        Data data = new Data();
        data.addAll(totalPublication);

        lineChart = new LineChart(categoryData,data);
        lineChart.setName("Total of publications");
        lineChart.plotOn(rectangularCoordinate);
    }

    public void emptyChart(){
        barChartList.clear();
    }

    public DataChannel getDataChannel(){
        return dataChannel;
    }

    protected BarChart findBarChart(String item){
        for (BarChart barChart : barChartList) {
            if (item.equals(barChart.getName())) {
                System.out.println(item);
                return barChart;
            }
        }
        return null;
    }

    public Button getButton() {
        return button;
    }
}
