package fr.utbm.ciad.labmanager.views.components.charts;

import com.storedobject.chart.*;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static com.storedobject.chart.Color.TRANSPARENT;

public abstract class AbstractPublicationCategoryChart extends AbstractSOChartChart implements PublicationCategoryChart {

    protected PublicationService publicationService;

    protected Toolbox toolbox;

    private List<PublicationType> publicationTypeList;

    private List<Integer> years;


    public AbstractPublicationCategoryChart(@Autowired PublicationService publicationService) {

        this.publicationService = publicationService;
        publicationTypeList = this.publicationService.getAllType();
        years = new ArrayList<>();

        toolbox = new Toolbox();
        Toolbox.Download toolboxDownload = new Toolbox.Download();
        toolboxDownload.setResolution(15);
        toolbox.addButton(toolboxDownload, new Toolbox.Zoom());
        toolbox.getPosition(true).setLeft(Size.percentage(80));


        disableDefaultLegend();
        setSVGRendering();
        setDefaultBackground(TRANSPARENT);
        add(toolbox);

    }

    public List<PublicationType> getPublicationTypeList(){
        return publicationTypeList;
    }

    public List<Integer> getYears(){
        return years;
    }

}

