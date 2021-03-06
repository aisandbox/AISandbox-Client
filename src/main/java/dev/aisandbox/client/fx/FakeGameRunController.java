package dev.aisandbox.client.fx;

import dev.aisandbox.client.ApplicationModel;
import dev.aisandbox.client.agent.AgentException;
import java.awt.image.BufferedImage;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * FakeGameRunController - used as an alternative to GameRunController when running in headless
 * mode.
 *
 * <p>Overrides all methods that would interact with the UI.
 */
public class FakeGameRunController extends GameRunController {

  @Autowired
  public FakeGameRunController(ApplicationModel model, FXTools fxtools) {
    super(model, fxtools);
  }

  @Override
  public void updateProfileInformation(
      BufferedImage image, long runTime, long averageStepTime, long stepCount) {
    // ignore - do nothing
  }

  /**
   * Overrides GameRunController method when in headless mode.
   *
   * @param image The pre-drawn {@link java.awt.image.BufferedImage} to display.
   */
  @Override
  public void updateBoardImage(BufferedImage image) {
    // IGNORE - Do Nothing
  }

  /** Overrides GameRunController method when in headless mode. */
  @Override
  public void resetStartButton() {
    // IGNORE - Do Nothing
  }

  /**
   * Overrides GameRunController method when in headless mode.
   *
   * @param e a {@link AgentException} object.
   */
  @Override
  public void showAgentError(AgentException e) {
    // IGNORE - Do Nothing
  }

  @Override
  public void showSimulationError(Exception e) {
    // IGNORE - Do Nothing
  }
}
