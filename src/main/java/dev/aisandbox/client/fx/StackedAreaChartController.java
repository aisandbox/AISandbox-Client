package dev.aisandbox.client.fx;

import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.StackedAreaChart;
import javafx.scene.chart.XYChart;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StackedAreaChartController {

    private final StackedAreaChart chart;
    private final NumberAxis xAxis;
    private static final Logger LOG = LoggerFactory.getLogger(StackedAreaChartController.class.getName());
    // map of series
    private final Map<String, XYChart.Series> seriesMap = new HashMap<>();
    private int step = 0;
    private static final int HISTORY = 50;

    public StackedAreaChartController(StackedAreaChart chart) {
        this.chart = chart;
        chart.setAnimated(false);
        xAxis = (NumberAxis) chart.getXAxis();
        xAxis.setForceZeroInRange(false);
        xAxis.setTickUnit(1.0);
        xAxis.setAutoRanging(false);
    }

    public void add(Map<String, Double> timings) {
        step++;
        // remove old entries
        int oldStep = step - HISTORY;
        LOG.debug( "Moving chart bounds to {} - {}", oldStep, step);
        // check existing series
        for (XYChart.Series series : seriesMap.values()) {
            if (!series.getData().isEmpty()) {
                XYChart.Data data = (XYChart.Data) series.getData().get(0);
                if (data.getXValue().equals(oldStep)) {
                    series.getData().remove(data);
                }
            }
        }
        // cycle through the series
        timings.forEach((sname, svalue) -> {
            // do we already have this series created
            XYChart.Series series;
            if (seriesMap.containsKey(sname)) {
                series = seriesMap.get(sname);
            } else {
                series = new XYChart.Series();
                series.setName(sname);
                chart.getData().add(series);
                seriesMap.put(sname, series);
            }
            series.getData().add(new XYChart.Data(step, svalue));

        });
        xAxis.setLowerBound(oldStep + 1.0);
        xAxis.setUpperBound(step);
    }

    public void reset() {
        chart.getData().clear();
        seriesMap.clear();
        step = 0;
        xAxis.setLowerBound(0);
        xAxis.setUpperBound(HISTORY);
    }
}
