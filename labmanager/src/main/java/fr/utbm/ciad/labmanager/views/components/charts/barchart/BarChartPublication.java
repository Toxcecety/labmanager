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

    List<BarChart> barChartList;


    public BarChartPublication(@Autowired PublicationService publicationService){

        this.publicationService = publicationService;

        xValues = new Data();
        ArrayList<Data> yValues = new ArrayList<>();

        List<Integer> years = this.publicationService.getAllYears();
        List<String> nameType = this.publicationService.getAllType();
        List<Map<String, Long>> countTypePublication;

        for(int i=0; i < nameType.size(); i++){
            countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(i)));
            yValues.add(new Data());
            for(Map<String,Long> map : countTypePublication ){
                for(Map.Entry<String, Long> entry : map.entrySet()){
                    yValues.get(i).add(entry.getValue());
                }
            }
        }

        for (Integer year : years) {
            xValues.add(Integer.valueOf(year));
        }

        barChartList = new ArrayList<>(nameType.size());

        for(int i = 0; i < nameType.size(); i++){
            BarChart barChart = new BarChart();
            barChartList.add(barChart);
        }

        int i = 0;
        for(BarChart br : barChartList){
            br.setXData(xValues);
            br.setYData(yValues.get(i));
            br.setName(nameType.get(i));
            i++;
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
}
