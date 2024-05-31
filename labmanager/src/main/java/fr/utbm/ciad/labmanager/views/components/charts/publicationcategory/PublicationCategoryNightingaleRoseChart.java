package fr.utbm.ciad.labmanager.views.components.charts.publicationcategory;

import com.storedobject.chart.*;
import com.storedobject.chart.Chart;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

/** Implementation of a publication category nightingale rose chart.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.0
 */
public class PublicationCategoryNightingaleRoseChart extends AbstractPublicationCategoryChart {

    private CategoryData categoryData;

    private NightingaleRoseChart nightingaleRoseChart;

    private Map<String, Integer> publicationCategories;

    private List<Integer> years;

    private Integer totalPublication;

    /** Constructor.
     *
     * @param publicationService the service for accessing the scientific publications.
     */
    public PublicationCategoryNightingaleRoseChart(@Autowired PublicationService publicationService){
        super(publicationService);
        publicationCategories = new HashMap<>();
        years = new ArrayList<>();
        totalPublication = 0;
    }

    /** Add a data in the chart, for example, the name of a chosen publication category
     *
     * @param item the name of the chosen item.
     */
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
        totalPublication += total;
        publicationCategories.put(chosenCategory,total);

    }

    /** Remove a data in the chart, for example, the name of a chosen publication category
     *
     * @param item the name of the chosen item.
     */
    public void removeData(String chosenCategory) {
        totalPublication -= publicationCategories.get(chosenCategory);
        publicationCategories.remove(chosenCategory);
    }

    /** Replies the created chart (from SOChart library). The creation of the chart must be implemented in this method.
     *
     * @return The created chart.
     */
    public SOChart createChart(){
        categoryData = new CategoryData();
        Data data = new Data();
        data.addAll(publicationCategories.values());

        float percentage = 0;
        String toString;
        for(var s : publicationCategories.entrySet()){
            percentage = ((float) s.getValue() /totalPublication)*100;
            toString = s.getKey() + " - " + String.format("%.2f",percentage) + "% ";
            categoryData.add(toString);
        }

        nightingaleRoseChart = new NightingaleRoseChart(categoryData,data);
        Chart.Label label = nightingaleRoseChart.getLabel(true);
        label.setInside(false);
        label.setFormatter("{0} - {1}");
        nightingaleRoseChart.setLabel(label);

        add(nightingaleRoseChart);
        return this;
    }

    /** Method called at the creation of the chart. It precises that a unique year is provided by the user.
     *
     * @param start The year of study.
     */
    @Override
    public void setYear(Integer start) {
        years = new ArrayList<>();
        categoryData = new CategoryData();

        years.add(start);
        categoryData.add(start.toString());

        publicationCategories.clear();


    }

    /** Method called at the creation of the chart. It precises that a period is provided by the user.
     *
     * @param start The beginning of the period.
     * @param end The end of the period.
     */
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
