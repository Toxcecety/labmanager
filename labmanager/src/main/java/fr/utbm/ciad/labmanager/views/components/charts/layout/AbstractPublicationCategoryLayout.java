package fr.utbm.ciad.labmanager.views.components.charts.layout;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import fr.utbm.ciad.labmanager.data.publication.Publication;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.addons.customfield.YearRange;
import fr.utbm.ciad.labmanager.views.components.charts.Chart;
import fr.utbm.ciad.labmanager.views.components.charts.PublicationCategoryChart;
import fr.utbm.ciad.labmanager.views.components.charts.PublicationCategoryPieChart;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryChartFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public abstract class AbstractPublicationCategoryLayout<T extends PublicationCategoryChart> extends AbstractChartLayout{

    private PublicationService publicationService;

    private PublicationCategoryChartFactory<T> factory;

    private MultiSelectComboBox multiSelectComboBox;

    private Button validateButton;

    private T chart;

    private HorizontalLayout horizontalLayout1;

    private HorizontalLayout horizontalLayout2;

    private YearRange yearRange;

    public AbstractPublicationCategoryLayout(@Autowired PublicationService publicationService, PublicationCategoryChartFactory<T> factory) {
        super();
        this.factory = factory;
        this.publicationService = publicationService;
        this.factory = getPublicationCategoryChartFactory();

        chart = this.factory.create(this.publicationService);

        horizontalLayout1 = new HorizontalLayout();
        horizontalLayout1.setWidthFull();

        horizontalLayout2 = new HorizontalLayout();
        horizontalLayout2.setWidthFull();

        yearRange = new YearRange(this.publicationService);


        multiSelectComboBox = new MultiSelectComboBox<>();
        multiSelectComboBox.setItems(this.publicationService.getAllCategories());
        multiSelectComboBox.setWidth(1000, Unit.PIXELS);
        multiSelectComboBox.addValueChangeListener(e -> {
            Set<String> oldValue = (Set<String>) e.getOldValue();
            Set<String> newValue = (Set<String>) e.getValue();

            Set<String> addedItems = new HashSet<>(newValue);
            addedItems.removeAll(oldValue);

            Set<String> removedItems = new HashSet<>(oldValue);
            removedItems.removeAll(newValue);

            if (!addedItems.isEmpty()) {
                validateButton.setEnabled(true);
            }

            if (!removedItems.isEmpty()) {
                if(multiSelectComboBox.getSelectedItems().isEmpty()){
                    validateButton.setEnabled(false);
                }
            }


        });


        validateButton = new Button("Create Chart");
        validateButton.setEnabled(false);
        validateButton.addClickListener(e -> {
            if(Objects.equals(validateButton.getText(), "Create Chart")){
                if(!yearRange.getButtonState()){
                    chart.setYear(yearRange.getChosenStartValue());
                }else{
                    chart.setPeriod(yearRange.getChosenStartValue(), yearRange.getChosenEndValue());
                }

                Set<String> items = multiSelectComboBox.getSelectedItems();
                for(String item : items){
                    this.chart.addData(item);
                }

                horizontalLayout1.add(this.chart.createChart());
                validateButton.setText("Refresh Chart");

            }else if(Objects.equals(validateButton.getText(), "Refresh Chart")){
                horizontalLayout1.remove(this.chart.createChart());
                this.chart = factory.create(this.publicationService);

                if(!yearRange.getButtonState()){
                    chart.setYear(yearRange.getChosenStartValue());
                }else{
                    chart.setPeriod(yearRange.getChosenStartValue(), yearRange.getChosenEndValue());
                }

                Set<String> items = multiSelectComboBox.getSelectedItems();
                for(String item : items){
                    this.chart.addData(item);
                }
                horizontalLayout1.add(this.chart.createChart());
            }

        });

        horizontalLayout2.add(multiSelectComboBox);
        horizontalLayout2.add(validateButton);

        add(horizontalLayout1);
        add(yearRange);
        add(horizontalLayout2);

    }

    public PublicationCategoryChartFactory getPublicationCategoryChartFactory(){
        return factory;
    }
}
