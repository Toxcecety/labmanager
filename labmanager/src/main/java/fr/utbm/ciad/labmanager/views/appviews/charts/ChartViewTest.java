package fr.utbm.ciad.labmanager.views.appviews.charts;
import com.storedobject.chart.*;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import fr.utbm.ciad.labmanager.views.appviews.MainLayout;
import fr.utbm.ciad.labmanager.views.components.charts.barchart.BarChartPublication;
import fr.utbm.ciad.labmanager.views.components.charts.linechart.LineChartPublication;
import jakarta.annotation.security.PermitAll;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.util.*;

@Route(value = "t", layout = MainLayout.class)
@PermitAll
public class ChartViewTest extends HorizontalLayout {
    public ChartViewTest() {

        // Creating a chart display area
        SOChart soChart = new SOChart();
        soChart.setSize("800px", "500px");

        // Generating some random values for a LineChart
        Random random = new Random();
        DateData xValues = new DateData();
        Data yValues1 = new Data(), yValues2 = new Data();
        for (int x = 0; x < 12; x++) {
            xValues.add(LocalDate.of(2021, x + 1, 1));
            yValues1.add(random.nextDouble());
            yValues2.add(random.nextDouble());
        }
        xValues.setName("Months of 2021");
        yValues1.setName("Random Values");

        // Bar charts is initialized with the generated XY values
        BarChart barChart1 = new BarChart(xValues, yValues1);
        barChart1.setName("Bar #1");
        barChart1.setStackName(
                "BC"); // Just a name - should be same for all the charts on the same stack
        BarChart barChart2 = new BarChart(xValues, yValues2);
        barChart2.setName("Bar #2");
        barChart2.setStackName(
                "BC"); // Just a name - should be same for all the charts on the same stack

        BarChart barChart3 = new BarChart(xValues, yValues2);
        barChart3.setName("Bar #2");
        barChart3.setStackName(
                "BC");
        BarChart barChart4 = new BarChart(xValues, yValues2);
        barChart4.setName("Bar #3");
        barChart4.setStackName(
                "BC");
        BarChart barChart5 = new BarChart(xValues, yValues2);
        barChart5.setName("Bar #4");
        barChart5.setStackName(
                "BC");
        BarChart barChart6 = new BarChart(xValues, yValues2);
        barChart6.setName("Bar #5");
        barChart6.setStackName(
                "BC");
        BarChart barChart8 = new BarChart(xValues, yValues2);
        barChart8.setName("Bar #8");
        barChart8.setStackName(
                "BC");

        BarChart barChart7 = new BarChart(xValues, yValues2);
        barChart7.setName("Bar #7");
        barChart7.setStackName(
                "BC");

        BarChart barChart9 = new BarChart(xValues, yValues2);
        barChart9.setName("Bar #9");
        barChart9.setStackName(
                "BC");

        BarChart barChart10 = new BarChart(xValues, yValues2);
        barChart10.setName("Bar #10");
        barChart10.setStackName(
                "BC");

        BarChart barChart11 = new BarChart(xValues, yValues2);
        barChart11.setName("Bar #11");
        barChart11.setStackName(
                "BC");

        BarChart barChart12 = new BarChart(xValues, yValues2);
        barChart12.setName("Bar #12");
        barChart12.setStackName(
                "BC");

        BarChart barChart13 = new BarChart(xValues, yValues2);
        barChart13.setName("Bar #13");
        barChart13.setStackName(
                "BC");
        BarChart barChart14 = new BarChart(xValues, yValues2);
        barChart14.setName("Bar #14");
        barChart14.setStackName(
                "BC");
        BarChart barChart15 = new BarChart(xValues, yValues2);
        barChart15.setName("Bar #15");
        barChart15.setStackName(
                "BC");
        BarChart barChart16 = new BarChart(xValues, yValues2);
        barChart16.setName("Bar #16");
        barChart16.setStackName(
                "BC");
        BarChart barChart17 = new BarChart(xValues, yValues2);
        barChart17.setName("Bar #17");
        barChart17.setStackName(
                "BC");
        BarChart barChart18 = new BarChart(xValues, yValues2);
        barChart18.setName("Bar #18");
        barChart18.setStackName(
                "BC");
        BarChart barChart19 = new BarChart(xValues, yValues2);
        barChart19.setName("Bar #19");
        barChart19.setStackName(
                "BC");
        BarChart barChart20 = new BarChart(xValues, yValues2);
        barChart20.setName("Bar #20");
        barChart20.setStackName(
                "BC");
        BarChart barChart21 = new BarChart(xValues, yValues2);
        barChart21.setName("Bar #21");
        barChart21.setStackName(
                "BC");
        // Add a line chart too for demo purpose
        LineChart lineChart = new LineChart(xValues, yValues1);
        lineChart.setName("Line #1");

        // Define axes
        YAxis yAxis = new YAxis(yValues1); // Just need the value type as parameter
        XAxis xAxis = new XAxis(xValues);
        xAxis.setMinAsMinData(); // We want to start the X axis from minimum of our data
        xAxis.getLabel(true).setFormatter("{MMM}"); // Format the date
        xAxis.setName("2021");

        // Coordinate system
        RectangularCoordinate rc = new RectangularCoordinate(xAxis, yAxis);
        barChart1.plotOn(rc); // Plot on the rectangular coordinate.
        barChart2.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart3.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart4.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart5.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart6.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart7.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart8.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart9.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart10.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart11.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart12.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart13.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart14.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart15.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart16.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart17.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart18.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart19.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart20.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);
        barChart21.plotOn(rc); // Also you could do rc.add(barChart1, barChart2, lineChart);

        lineChart.plotOn(rc);

        // Title for the chart
        Title title = new Title("Stacked Bars & a Line Chart");
        title.setSubtext("To demo stacking feature");
        title.getPosition(true).setLeft(Size.percentage(10)); // Leave 10% space on the left side

        // We want to customize the legend's position
        soChart.disableDefaultLegend();
        Legend legend = new Legend();
        legend.getPosition(true).setRight(Size.percentage(10)); // Leave 10% space on the right

        // Add to the chart display area with a simple title and our custom legend
        // (Since rc is added, no need to add the charts already plotted on it)
        soChart.add(rc, title, legend);

        // Set the component for the view
        add(soChart);
    }
}
