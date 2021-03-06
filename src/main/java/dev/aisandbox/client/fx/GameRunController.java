package dev.aisandbox.client.fx;

import dev.aisandbox.client.ApplicationModel;
import dev.aisandbox.client.SimulationRunThread;
import dev.aisandbox.client.agent.AgentConnectionException;
import dev.aisandbox.client.agent.AgentException;
import dev.aisandbox.client.agent.AgentParserException;
import dev.aisandbox.client.output.FormatTools;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ResourceBundle;
import java.util.concurrent.atomic.AtomicBoolean;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javax.imageio.ImageIO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Controller class used when running the simulations.
 *
 * @author gde
 * @version $Id: $Id
 */
@Component
@Slf4j
public class GameRunController {

  private final AtomicBoolean imageReady = new AtomicBoolean(true);
  private final ApplicationModel model;
  private final FXTools fxtools;
  @FXML private ResourceBundle resources;
  @FXML private ImageView profileChart;
  @FXML private Label runTimeField;
  @FXML private Label averageStepField;
  @FXML private Label stepCountField;
  @FXML private Button backButton;
  @FXML private Button startButton;
  @FXML private Pane imageAnchor;
  @FXML private Button stepButton;
  @FXML private Button pauseButton;

  @Autowired
  public GameRunController(ApplicationModel model, FXTools fxtools) {
    this.model = model;
    this.fxtools = fxtools;
  }

  private ImageView imageView;

  private BooleanProperty running = new SimpleBooleanProperty(false);
  SimulationRunThread thread = null;

  @FXML
  void backButtonAction(ActionEvent event) {
    log.info("Resetting scenario");
    model.resetRuntime();
    log.info("Selecting options screen");
    fxtools.moveToScreen(event, "/dev/aisandbox/client/fx/GameOptions.fxml");
  }

  @FXML
  void pauseButtonAction(ActionEvent event) {
    try {
      thread.setStopped(true);
    } catch (NullPointerException e) {
      log.debug("Trying to stop a simulation that has already finished", e);
    }
  }

  @FXML
  void stepButtonAction(ActionEvent event) {
    thread = new SimulationRunThread(model, 1);
    running.set(true);
    thread.start();
  }

  @FXML
  void startButtonAction(ActionEvent event) {
    thread = new SimulationRunThread(model);
    running.set(true);
    thread.start();
  }

  /** Called when the running thread has stopped running. */
  public void resetStartButton() {
    running.set(false);
    thread = null;
  }

  @FXML
  void initialize() {
    assert profileChart != null
        : "fx:id=\"profileChart\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert runTimeField != null
        : "fx:id=\"runTimeField\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert averageStepField != null
        : "fx:id=\"averageStepField\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert stepCountField != null
        : "fx:id=\"stepCountField\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert backButton != null
        : "fx:id=\"backButton\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert stepButton != null
        : "fx:id=\"stepButton\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert pauseButton != null
        : "fx:id=\"pauseButton\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert startButton != null
        : "fx:id=\"startButton\" was not injected: check your FXML file 'GameRun.fxml'.";
    assert imageAnchor != null
        : "fx:id=\"imageAnchor\" was not injected: check your FXML file 'GameRun.fxml'.";

    // setup autoscaling of imageview
    imageView = new ImageView();
    try {
      imageView.setImage(
          SwingFXUtils.toFXImage(
              ImageIO.read(
                  GameRunController.class.getResourceAsStream(
                      "/dev/aisandbox/client/testcard.png")),
              null));
    } catch (IOException e) {
      log.error("Error loading testcard", e);
    }
    imageAnchor.getChildren().add(imageView);
    // setup automatic resize
    imageAnchor
        .widthProperty()
        .addListener(
            (observableValue, number, t1) ->
                repositionImage(imageView, t1.doubleValue(), imageAnchor.getHeight()));
    imageAnchor
        .heightProperty()
        .addListener(
            (observableValue, number, t1) ->
                repositionImage(imageView, imageAnchor.getWidth(), t1.doubleValue()));

    // setup run buttons
    pauseButton.disableProperty().bind(running.not());
    startButton.disableProperty().bind(running);
    stepButton.disableProperty().bind(running);
    backButton.disableProperty().bind(running);
    // initialise simulation
    model.initialiseRuntime(this);
  }

  private void repositionImage(ImageView image, double paneWidth, double paneHeight) {
    // get image width and height
    double imageWidth = image.getImage().getWidth();
    double imageHeight = image.getImage().getHeight();
    log.debug("Scaling image {}x{} to pane {}x{}", imageWidth, imageHeight, paneWidth, paneHeight);
    // work out the best scale
    double scaleX = paneWidth / imageWidth;
    double scaleY = paneHeight / imageHeight;
    double scale = Math.min(scaleX, scaleY);
    image.setFitWidth(scale * imageWidth);
    image.setFitHeight(scale * imageHeight);
    image.setX((paneWidth - scale * imageWidth) / 2.0);
    image.setY((paneHeight - scale * imageHeight) / 2.0);
  }

  /**
   * Method to update the on-screen view of the simulation.
   *
   * <p>This can be called from any thread, but the screen will only update if the FX thread is not
   * busy.
   *
   * @param image The pre-drawn {@link java.awt.image.BufferedImage} to display.
   */
  public void updateBoardImage(BufferedImage image) {
    if (imageReady.getAndSet(false)) {
      Platform.runLater(
          () -> {
            imageView.setImage(SwingFXUtils.toFXImage(image, null));
            imageReady.set(true);
          });
    }
  }

  /**
   * Update the Profile graph.
   *
   * @param image The image to display.
   * @param runTime The amount of time spent running the simulation, in milliseconds.
   * @param averageStepTime The average step time, in milliseconds.
   * @param stepCount The number of steps taken.
   */
  public void updateProfileInformation(
      BufferedImage image, long runTime, long averageStepTime, long stepCount) {
    Platform.runLater(
        () -> {
          profileChart.setImage(SwingFXUtils.toFXImage(image, null));
          stepCountField.setText("Steps: " + stepCount);
          averageStepField.setText("Average Step : " + FormatTools.formatTime(averageStepTime));
          runTimeField.setText("Run Time : " + FormatTools.formatTime(runTime));
        });
  }

  /**
   * Inform the user that there has been an exception and that the simulation has been stopped.
   *
   * @param e a {@link AgentException} object.
   */
  public void showAgentError(AgentException e) {
    // special case - is this an agent connection exception
    if (e instanceof AgentConnectionException) {
      showAgentError(e.getTarget(), "Error connecting to agent", e.getMessage());
    } else if (e instanceof AgentParserException) {
      AgentParserException ape = (AgentParserException) e;
      StringBuilder sb = new StringBuilder();
      sb.append("Connection to the server produced HTTP code ");
      sb.append(ape.getResponseCode());
      sb.append("\nResponse:\n");
      sb.append(ape.getResponse());
      showAgentError(e.getTarget(), "Error parsing server response", sb.toString());
    } else {
      // show generic exception
      showAgentError(e.getTarget(), "Agent Exception", e.getMessage());
    }
  }

  /**
   * Show Agent Error dialog.
   *
   * @param agentURL a {@link java.lang.String} object.
   * @param description a {@link java.lang.String} object.
   * @param details a {@link java.lang.String} object.
   */
  private void showAgentError(String agentURL, String description, String details) {
    Platform.runLater(
        () -> {
          // show the exception
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Agent Error");
          alert.setHeaderText("There was an error talking to an agent");
          alert.setContentText(agentURL);

          TextArea textArea = new TextArea(details);
          textArea.setEditable(false);
          textArea.setWrapText(true);
          textArea.setMaxWidth(Double.MAX_VALUE);
          textArea.setMaxHeight(Double.MAX_VALUE);

          GridPane.setVgrow(textArea, Priority.ALWAYS);
          GridPane.setHgrow(textArea, Priority.ALWAYS);

          Label label = new Label(description);

          GridPane expContent = new GridPane();
          expContent.setMaxWidth(Double.MAX_VALUE);
          expContent.add(label, 0, 0);
          expContent.add(textArea, 0, 1);

          // Set expandable Exception into the dialog pane.
          alert.getDialogPane().setExpandableContent(expContent);

          alert.showAndWait();
        });
  }

  /**
   * Show a simulation error to the user.
   *
   * @param e The exception to display.
   */
  public void showSimulationError(Exception e) {
    Platform.runLater(
        () -> {
          // show the exception
          Alert alert = new Alert(Alert.AlertType.ERROR);
          alert.setTitle("Simulation Error");
          alert.setHeaderText("There was an error running the simulation");
          alert.setContentText(e.getMessage());
          alert.showAndWait();
        });
  }
}
