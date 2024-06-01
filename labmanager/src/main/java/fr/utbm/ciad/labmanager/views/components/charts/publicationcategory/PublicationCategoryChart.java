package fr.utbm.ciad.labmanager.views.components.charts.publicationcategory;

import com.storedobject.chart.SOChart;
import fr.utbm.ciad.labmanager.views.components.charts.Chart;

/** Interface that represents a publication category chart.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public interface PublicationCategoryChart extends Chart {

    /** Add a data in the chart, for example, the name of a chosen publication category
     *
     * @param item the name of the chosen item.
     */
    public void addData(String item);

    /** Remove a data in the chart, for example, the name of a chosen publication category
     *
     * @param item the name of the chosen item.
     */
    public void removeData(String item);

    /** Replies the created chart (from SOChart library). The creation of the chart must be implemented in this method.
     *
     * @return The created chart.
     */
    public SOChart createChart();

    /** Method called at the creation of the chart. It precises that a unique year is provided by the user.
     *
     * @param start The year of study.
     */
    public void setYear(Integer start);

    /** Method called at the creation of the chart. It precises that a period is provided by the user.
     *
     * @param start The beginning of the period.
     * @param end The end of the period.
     */
    public void setPeriod(Integer start, Integer end);

}
