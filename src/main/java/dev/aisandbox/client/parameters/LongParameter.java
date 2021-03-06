package dev.aisandbox.client.parameters;

import dev.aisandbox.client.scenarios.ScenarioParameter;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleLongProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.util.converter.NumberStringConverter;
import lombok.Getter;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class LongParameter implements ScenarioParameter {

  private final String parameterKey;
  private LongProperty value = new SimpleLongProperty();
  @Getter private final String name;
  @Getter private final String tooltip;

  public LongParameter(String key, long startingValue, String name, String tooltip) {
    parameterKey = key;
    value.set(startingValue);
    this.name = name;
    this.tooltip = tooltip;
  }

  @Override
  public String getParameterKey() {
    return parameterKey;
  }

  @Override
  public Node getParameterControl() {
    // define controls
    HBox pane = new HBox();
    pane.setSpacing(5.0);
    pane.setAlignment(Pos.CENTER_LEFT);
    // text label
    Label label = new Label(name);
    label.setMaxWidth(Double.MAX_VALUE);
    // field
    TextField field = new TextField();
    pane.getChildren().add(label);
    pane.getChildren().add(field);
    pane.setHgrow(label, Priority.ALWAYS);
    // bind value
    field.textProperty().bindBidirectional(value, new NumberStringConverter());
    // return parameter
    return pane;
  }

  @Override
  public void setParsableValue(String val) throws ParameterParseException {
    try {
      value.set(Long.parseLong(val));
    } catch (Exception e) {
      throw new ParameterParseException(
          "Can't parse '" + val + "' to a long integer for " + parameterKey);
    }
  }

  public long getValue() {
    return value.get();
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("parameterKey", parameterKey)
        .append("value", value)
        .toString();
  }
}
