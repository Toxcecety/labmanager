package fr.utbm.ciad.labmanager.views.components.charts;

import com.storedobject.chart.*;
import com.storedobject.chart.Chart;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.storedobject.chart.Color.TRANSPARENT;

public class PublicationCategoryPieChart extends AbstractPublicationCategoryChart{

    private PublicationService publicationService;
    private CategoryData categoryData;
    private Map<String,Integer> publicationCategories;
    private PieChart pieChart;
    private List<Integer> years;

    public PublicationCategoryPieChart(@Autowired PublicationService publicationService){
        super(publicationService);

        this.publicationService = publicationService;
        publicationCategories = new HashMap<>();
        years = new ArrayList<>();
        categoryData = new CategoryData();


    }

    public void addData(String chosenCategory) {
        Integer countTypePublicationV2;
        List<PublicationType> temporaryPublicationTypeList = getPublicationTypeList().stream().filter(publicationType -> Objects.equals(publicationType.getCategory(true).toString(), chosenCategory)).toList();
        Integer total = 0;
        for(int x = 0; x < years.size(); x++){
            for (PublicationType publicationType : temporaryPublicationTypeList ) {
                countTypePublicationV2 = this.publicationService.getCountPublicationByTypeByYearV2(publicationType, years.get(x));
                total += countTypePublicationV2;

            }
        }
        publicationCategories.put(chosenCategory,total);

    }

    public void removeData(String chosenCategory) {
        publicationCategories.remove(chosenCategory);
    }

    public SOChart createChart(){
        categoryData = new CategoryData();
        Data data = new Data();
        data.addAll(publicationCategories.values());


        for(String s : publicationCategories.keySet()){
            categoryData.add(s);
        }

        pieChart = new PieChart(categoryData,data);

        Chart.Label label = pieChart.getLabel(true);
        label.setInside(false);
        label.setFormatter("{0} - {1}");
        pieChart.setLabel(label);



        add(pieChart);
        return this;
    }

    @Override
    public void setYear(Integer start) {
        years = new ArrayList<>();
        categoryData = new CategoryData();

        years.add(start);
        categoryData.add(start.toString());

        publicationCategories.clear();


    }

    @Override
    public void setPeriod(Integer start, Integer end) {
        years = new ArrayList<>();
        categoryData = new CategoryData();

        Integer imp = start;
        while(imp <= end){
            years.add(imp);
            imp++;
        }

        for (Integer year : years) {
            categoryData.add(year.toString());
        }

        publicationCategories.clear();

    }

}
