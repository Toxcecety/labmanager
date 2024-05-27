package fr.utbm.ciad.labmanager.views.components.charts.layout;

import com.vaadin.flow.component.Unit;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.combobox.MultiSelectComboBox;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.components.addons.customfield.YearRange;
import fr.utbm.ciad.labmanager.views.components.charts.PublicationCategoryChart;
import fr.utbm.ciad.labmanager.views.components.charts.factory.PublicationCategoryChartFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class PublicationCategoryLayout<T extends PublicationCategoryChart> extends AbstractPublicationCategoryLayout{

    public PublicationCategoryLayout(@Autowired PublicationService publicationService, PublicationCategoryChartFactory<T> factory) {
        super(publicationService,factory);

    }

}
