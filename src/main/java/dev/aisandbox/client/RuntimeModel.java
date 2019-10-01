package dev.aisandbox.client;

import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import lombok.Getter;
import lombok.Setter;
import dev.aisandbox.client.scenarios.Scenario;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.logging.Logger;

/**
 * The main (POJO) class used to hold the application state.
 * <p>This uses the Lombok library to auto generate much of its functionality
 */
@Component
public class RuntimeModel {

    @Getter
    Scenario scenario;

    @Getter
    @Setter
    Locale locale = Locale.UK;

    @Getter
    IntegerProperty minAgents = new SimpleIntegerProperty(1);

    @Getter
    IntegerProperty maxAgents = new SimpleIntegerProperty(1);

    @Getter
    ObservableList<Agent> agentList = FXCollections.observableList(new ArrayList<>());

    @Getter
    BooleanProperty valid = new SimpleBooleanProperty(false);

    @Getter
    @Setter
    private OutputFormat outputFormat = OutputFormat.NONE;

    @Getter
    @Setter
    private File outputDirectory = new File("./");

    private static final Logger LOG = Logger.getLogger(RuntimeModel.class.getName());

    public enum OUTPUT_TYPE {NONE, FILE, VIDEO}

    /**
     * Setup the model with usefull default values.
     */
    public RuntimeModel() {
        // TODO - load default values

        // bind validation rules
        valid.bind(Bindings.and(Bindings.size(agentList).greaterThanOrEqualTo(minAgents), Bindings.size(agentList).lessThanOrEqualTo(maxAgents)));
    }

    /**
     * Setter for the field <code>scenario</code>.
     * <p>This updates the number of required agents based on the requested scenario
     *
     * @param s a {@link dev.aisandbox.client.scenarios.Scenario} object.
     */
    public void setScenario(Scenario s) {
        this.scenario = s;
        LOG.info("changing scenario to " + s.getName());
        minAgents.setValue(s.getMinAgentCount());
        maxAgents.setValue(s.getMaxAgentCount());
    }


}
