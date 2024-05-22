package fr.utbm.ciad.labmanager.views.appviews.charts;

import com.storedobject.chart.*;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.charts.barchart.BarChartPublication;
import fr.utbm.ciad.labmanager.views.components.publications.MultiPublicationTypeField;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.*;

import static com.storedobject.chart.Color.TRANSPARENT;

@Route(value = "charts", layout = MainLayout.class)
@PermitAll
public class ChartsView extends VerticalLayout {

    private PublicationService publicationService;

    private SOChart soChart;

    private List<BarChart> barChartList;

    private Button buttonValidate;

    private Button buttonDelete;

    private BarChartPublication barChartPublication;

    private MultiSelectComboBox<String> multiSelectComboBox;

    public ChartsView(@Autowired PublicationService publicationService) {

        this.publicationService = publicationService;





        barChartPublication = new BarChartPublication(this.publicationService);

        CategoryData categoryData = barChartPublication.getCategoryData();
        List<Integer> years = barChartPublication.getYears();
        List<String> nameType = this.publicationService.getAllType();



        List<Long> countPublication = this.publicationService.getCountPublicationsByYear();

        /*
        LineChart lineChart = new LineChart(categoryData, yValuesLine);
        lineChart.setName("Total of publications");


        lineChart.getAreaStyle(true).setColors(TRANSPARENT);
        lineChart.plotOn(barChartPublication.getRectangularCoordinate());
        */

        multiSelectComboBox = new MultiSelectComboBox<>("Select Publication Types");
        multiSelectComboBox.setItems(nameType);
        multiSelectComboBox.setSizeFull();
        multiSelectComboBox.addValueChangeListener(e -> {
            Set<String> oldValue = e.getOldValue();
            Set<String> newValue = e.getValue();

            Set<String> addedItems = new HashSet<>(newValue);
            addedItems.removeAll(oldValue);

            Set<String> removedItems = new HashSet<>(oldValue);
            removedItems.removeAll(newValue);

            if (!addedItems.isEmpty()) {
                for (String item : addedItems) {
                    barChartPublication.addData(item);
                    buttonValidate.setEnabled(true);
                }
            }

            if (!removedItems.isEmpty()) {
                for (String item : removedItems) {
                    barChartPublication.removeData(item);
                }

                if(multiSelectComboBox.getSelectedItems().isEmpty()){
                    buttonValidate.setEnabled(false);
                }
            }


        });

        add(multiSelectComboBox);

        buttonValidate = new Button("Create Chart");
        buttonValidate.setEnabled(false);
        buttonValidate.addClickListener(e -> {
            barChartPublication.plot();
            add(buttonDelete);
            remove(buttonValidate);
            add(barChartPublication.getSoChart());

        });

        buttonDelete = new Button("Delete Chart");
        buttonDelete.addClickListener(e -> {
            remove(barChartPublication.getSoChart());
            barChartPublication.emptyChart();
            multiSelectComboBox.clear();
            barChartPublication = new BarChartPublication(this.publicationService);
            remove(buttonDelete);
            add(buttonValidate);
        });
        buttonDelete.addThemeVariants(ButtonVariant.LUMO_ERROR);


        add(buttonValidate);



    }

}