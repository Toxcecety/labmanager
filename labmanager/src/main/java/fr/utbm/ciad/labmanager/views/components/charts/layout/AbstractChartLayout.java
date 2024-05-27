package fr.utbm.ciad.labmanager.views.components.charts.layout;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import fr.utbm.ciad.labmanager.views.components.charts.Chart;

public abstract class AbstractChartLayout extends VerticalLayout implements ChartLayout {


    public AbstractChartLayout(){
        setSizeFull();
        setHeight(1000, Unit.PIXELS);
    }

}
