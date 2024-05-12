package fr.utbm.ciad.labmanager.views.components.charts.barchart;

import com.storedobject.chart.*;
import fr.utbm.ciad.labmanager.data.publication.PublicationType;
import fr.utbm.ciad.labmanager.services.publication.PublicationService;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BarCharPublicationTest extends BarChart{

    private PublicationService publicationService;

    private BarChart barChart1;
    private BarChart barChart2;
    private BarChart barChart3;
    private BarChart barChart4;
    private BarChart barChart5;
    private BarChart barChart6;
    private BarChart barChart7;
    private BarChart barChart8;
    private BarChart barChart9;
    private BarChart barChart10;
    private BarChart barChart11;
    private BarChart barChart12;
    private BarChart barChart13;
    private BarChart barChart14;
    private BarChart barChart15;
    private BarChart barChart16;
    private BarChart barChart17;
    private BarChart barChart18;
    private BarChart barChart19;
    private BarChart barChart20;
    private BarChart barChart21;
    private BarChart barChart22;
    private BarChart barChart23;
    private BarChart barChart24;
    private BarChart barChart25;

    private Data yValues1;
    private Data yValues2;
    private Data yValues3;
    private Data yValues4;
    private Data yValues5;
    private Data yValues6;
    private Data yValues7;
    private Data yValues8;
    private Data yValues9;
    private Data yValues10;
    private Data yValues11;
    private Data yValues12;
    private Data yValues13;
    private Data yValues14;
    private Data yValues15;
    private Data yValues16;
    private Data yValues17;
    private Data yValues18;
    private Data yValues19;
    private Data yValues20;
    private Data yValues21;
    private Data yValues22;
    private Data yValues23;
    private Data yValues24;
    private Data yValues25;

    private Data xValues;

    public BarCharPublicationTest(@Autowired PublicationService publicationService){

        this.publicationService = publicationService;


        List<Integer> years = this.publicationService.getAllYears();
        List<String> nameType = this.publicationService.getAllType();
        List<Map<String, Long>> countTypePublication;

        //1
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(0)));
        yValues1 = new Data();
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues1.add(entry.getValue());
            }
        }
        //2
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(1)));
        yValues2 = new Data();
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues2.add(entry.getValue());
            }
        }
        //3
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(2)));
        yValues3 = new Data();
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues3.add(entry.getValue());
            }
        }
        //4
        yValues4 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(3)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues4.add(entry.getValue());
            }
        }
        //5
        yValues5 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(4)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues5.add(entry.getValue());
            }
        }
        //6
        yValues6 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(5)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues1.add(entry.getValue());
            }
        }
        //7
        yValues7 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(6)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues6.add(entry.getValue());
            }
        }
        //8
        yValues8 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(7)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues8.add(entry.getValue());
            }
        }
        //9
        yValues9 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(8)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues9.add(entry.getValue());
            }
        }
        //10
        yValues10 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(9)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues10.add(entry.getValue());
            }
        }
        //11
        yValues11 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(10)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues11.add(entry.getValue());
            }
        }
        //12
        yValues12 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(11)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues12.add(entry.getValue());
            }
        }
        //13
        yValues13 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(12)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues13.add(entry.getValue());
            }
        }
        //14
        yValues14 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(13)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues14.add(entry.getValue());
            }
        }
        //15
        yValues15 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(14)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues15.add(entry.getValue());
            }
        }
        //16
        yValues16 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(15)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues16.add(entry.getValue());
            }
        }
        //17
        yValues17 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(16)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues16.add(entry.getValue());
            }
        }
        //18
        yValues18 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(17)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues18.add(entry.getValue());
            }
        }
        //19
        yValues19 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(18)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues19.add(entry.getValue());
            }
        }
        //20
        yValues20 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(19)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues20.add(entry.getValue());
            }
        }
        //21
        yValues21 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(20)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues21.add(entry.getValue());
            }
        }
        //22
        yValues22 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(21)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues22.add(entry.getValue());
            }
        }
        //23
        yValues23 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(22)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues23.add(entry.getValue());
            }
        }
        //24
        yValues24 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(23)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues24.add(entry.getValue());
            }
        }
        //25
        yValues25 = new Data();
        countTypePublication = this.publicationService.getCountPublicationByTypeByYear(PublicationType.valueOf(nameType.get(24)));
        for(Map<String,Long> map : countTypePublication ){
            for(Map.Entry<String, Long> entry : map.entrySet()){
                yValues25.add(entry.getValue());
            }
        }

        xValues = new Data();
        for (Integer year : years) {
            xValues.add(year);
        }
        xValues.setName("Years");

        barChart1 = new BarChart(xValues, yValues1);
        //barChart1.setName(nameType.get(0));
        barChart1.setStackName(
                "BC");

        barChart2 = new BarChart(xValues, yValues2);
        //barChart2.setName(nameType.get(1));
        barChart2.setStackName(
                "BC");

        barChart3 = new BarChart(xValues, yValues3);
        //barChart3.setName(nameType.get(2));
        barChart3.setStackName(
                "BC");

        barChart4 = new BarChart(xValues, yValues4);
        //barChart4.setName(nameType.get(3));
        barChart4.setStackName(
                "BC");

        barChart5 = new BarChart(xValues, yValues5);
        //barChart5.setName(nameType.get(4));
        barChart5.setStackName(
                "BC");

        barChart6 = new BarChart(xValues, yValues6);
        //barChart6.setName(nameType.get(5));
        barChart6.setStackName(
                "BC");

        barChart7 = new BarChart(xValues, yValues7);
        //barChart7.setName(nameType.get(6));
        barChart7.setStackName(
                "BC");

        barChart8 = new BarChart(xValues, yValues8);
        //barChart8.setName(nameType.get(7));
        barChart8.setStackName(
                "BC");

        barChart9 = new BarChart(xValues, yValues9);
        //barChart9.setName(nameType.get(8));
        barChart9.setStackName(
                "BC");

        barChart10 = new BarChart(xValues, yValues10);
        //barChart10.setName(nameType.get(9));
        barChart10.setStackName(
                "BC");

        barChart11 = new BarChart(xValues, yValues11);
        //barChart11.setName(nameType.get(10));
        barChart11.setStackName(
                "BC");

        barChart12 = new BarChart(xValues, yValues12);
        //barChart12.setName(nameType.get(11));
        barChart12.setStackName(
                "BC");

        barChart13 = new BarChart(xValues, yValues13);
        //barChart13.setName(nameType.get(12));
        barChart13.setStackName(
                "BC");

        barChart14 = new BarChart(xValues, yValues14);
        //barChart14.setName(nameType.get(13));
        barChart14.setStackName(
                "BC");

        barChart15 = new BarChart(xValues, yValues15);
        //barChart15.setName(nameType.get(14));
        barChart15.setStackName(
                "BC");

        barChart16 = new BarChart(xValues, yValues16);
        //barChart16.setName(nameType.get(15));
        barChart16.setStackName(
                "BC");

        barChart17 = new BarChart(xValues, yValues17);
        //barChart17.setName(nameType.get(16));
        barChart17.setStackName(
                "BC");

        barChart18 = new BarChart(xValues, yValues18);
        //barChart18.setName(nameType.get(17));
        barChart18.setStackName(
                "BC");

        barChart19 = new BarChart(xValues, yValues19);
        //barChart19.setName(nameType.get(18));
        barChart19.setStackName(
                "BC");

        barChart20 = new BarChart(xValues, yValues20);
        //barChart20.setName(nameType.get(11));
        barChart20.setStackName(
                "BC");

        barChart21 = new BarChart(xValues, yValues21);
        //barChart21.setName(nameType.get(20));
        barChart21.setStackName(
                "BC");

        barChart22 = new BarChart(xValues, yValues22);
        //barChart22.setName(nameType.get(21));
        barChart22.setStackName(
                "BC");

        barChart23 = new BarChart(xValues, yValues23);
        //barChart23.setName(nameType.get(22));
        barChart23.setStackName(
                "BC");

        barChart24 = new BarChart(xValues, yValues24);
        //barChart24.setName(nameType.get(23));
        barChart24.setStackName(
                "BC");

        barChart25 = new BarChart(xValues, yValues25);
        //barChart25.setName(nameType.get(24));
        barChart25.setStackName(
                "BC");

    }

    public void sendRectangularCoordinate(RectangularCoordinate rc){
        rc.add(barChart1,barChart2,barChart3,barChart4,barChart5,barChart6,barChart7,barChart8,barChart9,barChart10,barChart11,
                barChart12,barChart13,barChart14,barChart15,barChart16,barChart17,barChart18,barChart19,barChart20,barChart21,barChart22,
                barChart23,barChart24,barChart25);
    }
    public Data getXValues(){
        return xValues;
    }
}
