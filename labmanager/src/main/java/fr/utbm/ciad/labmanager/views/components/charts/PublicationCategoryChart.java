package fr.utbm.ciad.labmanager.views.components.charts;

import com.storedobject.chart.SOChart;

import java.util.List;

public interface PublicationCategoryChart extends Chart {

    public void addData(String item);

    public void removeData(String item);

    public SOChart createChart();

    public void setYear(Integer start);

    public void setPeriod(Integer start, Integer end);

}
