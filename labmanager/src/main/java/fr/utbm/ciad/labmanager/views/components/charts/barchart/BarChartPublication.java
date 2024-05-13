package fr.utbm.ciad.labmanager.views.components.charts.barchart;

import com.storedobject.chart.*;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BarChartPublication extends BarChart{

    private PublicationService publicationService;

    private Data xValues;

    private List<BarChart> barChartList;

    private CategoryData categoryData;

    private List<Integer> years;


    public BarChartPublication(@Autowired PublicationService publicationService){

        this.publicationService = publicationService;

        xValues = new Data();
        ArrayList<Data> yValues = new ArrayList<>();

        years = this.publicationService.getAllYears();
        List<String> nameType = this.publicationService.getAllType();
        List<Map<String, Long>> countTypePublication;
        Integer countTypePublicationV2;

        categoryData = new CategoryData();
        xValues = new Data();

        for (Integer year : years) {
            xValues.add(Integer.valueOf(year));
            categoryData.add(year.toString());
        }

        /*
        for(int i=0; i < nameType.size(); i++){
            countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(i)));
            yValues.add(new Data());
            for(Map<String,Long> map : countTypePublication ){
                for(Map.Entry<String, Long> entry : map.entrySet()){
                    yValues.get(i).add(entry.getValue());
                }
            }
        }
        */
        barChartList = new ArrayList<>(nameType.size());

        for(int i=0; i < nameType.size(); i++){
            yValues.add(new Data());
            for(int x=0; x < years.size(); x++){
                countTypePublicationV2 = this.publicationService.getCountPublicationByTypeByYearV2(PublicationType.valueOf(nameType.get(i)),years.get(x));
                yValues.get(i).add(countTypePublicationV2);
            }

            BarChart barChart = new BarChart(categoryData,yValues.get(i));
            barChart.setName(nameType.get(i));
            barChart.setStackName("BC");

            barChartList.add(barChart);
        }


        xValues.setName("Years");

        this.setXData(xValues);
    }

    public Data getXValues(){
        return xValues;
    }

    public Data getYValues(){
        Data data = new Data();
        for(Number value : xValues){
            data.add(300);
        }
        return data;
    }

    public List<BarChart> getBarChartList(){
        return barChartList;
    }

    public int getTypeSize(){
        List<String> nameType = this.publicationService.getAllType();
        return nameType.size();
    }

    public List<Integer> getYears(){
        return years;
    }

    public CategoryData getCategoryData(){
        return categoryData;
    }

}
