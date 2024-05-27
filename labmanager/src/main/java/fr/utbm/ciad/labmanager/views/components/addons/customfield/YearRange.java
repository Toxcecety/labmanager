package fr.utbm.ciad.labmanager.views.components.addons.customfield;

import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.customfield.CustomField;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.select.Select;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;

import java.awt.*;
import java.util.List;

public class YearRange extends CustomField<Integer> {

    private PublicationService publicationService;

    private List<Integer> years;

    private Select<Integer> start;

    private Select<Integer> end;

    private Integer chosenStartValue;

    private Integer chosenEndValue;

    private Button button;

    private Text text;

    private Boolean buttonState;

    private HorizontalLayout horizontalLayout;

    public YearRange(@Autowired PublicationService publicationService){
        this.publicationService = publicationService;
        years = this.publicationService.getAllYears();
        
        horizontalLayout = new HorizontalLayout();
        horizontalLayout.setSizeFull();
        horizontalLayout.setAlignItems(FlexComponent.Alignment.END);

        start = new Select<>();
        start.setLabel("Start");
        start.setItems(years);
        start.setValue(years.get(0));
        chosenStartValue = years.get(0);
        start.addValueChangeListener(e ->{
            Integer selectedValue = e.getValue();
            chosenStartValue = selectedValue;
            int startValue = years.indexOf(selectedValue);
            end.setItems(years.subList(startValue,years.size()));
            end.setValue(years.get(years.size()-1));
        });

        end = new Select<>();
        end.setLabel("End");
        end.setItems(years.subList(1, years.size()));
        end.setValue(years.get(years.size()-1));
        chosenEndValue = years.get(years.size()-1);
        end.addValueChangeListener(e ->{
            Integer selectedValue = e.getValue();
            chosenEndValue = selectedValue;

        });

        button = new Button("Set period");
        button.addSingleClickListener(e -> switchPeriodState());
        button.setMinWidth("150px");
        buttonState = false;
        horizontalLayout.add(button);

        text = new Text(" - ");
        horizontalLayout.add(start);
        add(horizontalLayout);

    }

    @Override
    protected Integer generateModelValue() {
        return 0;
    }

    @Override
    protected void setPresentationValue(Integer integer) {
    }

    public Integer getChosenStartValue() {
        return chosenStartValue;
    }

    public Integer getChosenEndValue() {
        return chosenEndValue;
    }

    protected void switchPeriodState(){
        if(buttonState){
            button.setText("Set period");
            horizontalLayout.remove(text,end);
            buttonState = false;
        }else{
            button.setText("Set year");
            horizontalLayout.add(text,end);
            buttonState = true;

        }
    }

    public Select<Integer> getStart() {
        return start;
    }

    public Select<Integer> getEnd() {
        return end;
    }

    public Boolean getButtonState() {
        return buttonState;
    }
}
