package fr.utbm.ciad.labmanager.views.appviews.charts;

import com.storedobject.chart.*;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.TabSheet;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.addons.customfield.YearRange;
import fr.utbm.ciad.labmanager.views.components.charts.PublicationCategoryNightingaleRoseChart;
import fr.utbm.ciad.labmanager.views.components.charts.PublicationCategoryPieChart;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryBarChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryNightingaleRoseChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryPieChartFactory;
import fr.utbm.ciad.labmanager.views.components.charts.layout.AbstractPublicationCategoryLayout;
import fr.utbm.ciad.labmanager.views.components.charts.barchart.PublicationCategoryBarChart;
import fr.utbm.ciad.labmanager.views.components.charts.layout.PublicationCategoryLayout;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

@Route(value = "charts", layout = MainLayout.class)
@PermitAll
public class ChartsView extends VerticalLayout {

    private List<AbstractPublicationCategoryLayout> abstractPublicationCategoryLayouts;

    private TabSheet tabSheet;

    private PublicationCategoryChartFactory<PublicationCategoryBarChart> barChartFactory;

    private PublicationCategoryChartFactory<PublicationCategoryPieChart> pieChartFactory;

    private PublicationCategoryChartFactory<PublicationCategoryNightingaleRoseChart> nightingaleChartFactory;

    public ChartsView(@Autowired PublicationService publicationService) {

        abstractPublicationCategoryLayouts = new ArrayList<>();
        barChartFactory = new PublicationCategoryBarChartFactory();
        pieChartFactory = new PublicationCategoryPieChartFactory();
        nightingaleChartFactory = new PublicationCategoryNightingaleRoseChartFactory();

        PublicationCategoryLayout<PublicationCategoryBarChart> barChart = new PublicationCategoryLayout<>(publicationService, barChartFactory);
        PublicationCategoryLayout<PublicationCategoryPieChart> pieChart = new PublicationCategoryLayout<>(publicationService, pieChartFactory);
        PublicationCategoryLayout<PublicationCategoryNightingaleRoseChart> nightingaleChart = new PublicationCategoryLayout<>(publicationService, nightingaleChartFactory);

        tabSheet = new TabSheet();

        tabSheet.add("BarChart",
                new Div(barChart));
        tabSheet.add("PieChart",
                new Div(pieChart));
        tabSheet.add("NightingaleRoseChart",
                new Div(nightingaleChart));
        add(tabSheet);

    }

}