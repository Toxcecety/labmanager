package fr.utbm.ciad.labmanager.views.components.charts.layout;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.views.components.charts.Chart;

/** Abstract implementation of the layout for displaying charts.
 *
 * @author $Author: sgalland$
 * @author $Author: erenon$
 * @version $Name$ $Revision$ $Date$
 * @mavengroupid $GroupId$
 * @mavenartifactid $ArtifactId$
 * @since 4.1
 */
public abstract class AbstractChartLayout extends VerticalLayout implements ChartLayout {

    /** Constructor.
     */
    public AbstractChartLayout(){
        setSizeFull();
        setHeight(1000, Unit.PIXELS);
    }

}
