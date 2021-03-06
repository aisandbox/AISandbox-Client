package dev.aisandbox.client.scenarios.zebra;

import dev.aisandbox.client.agent.Agent;
import dev.aisandbox.client.fx.GameRunController;
import dev.aisandbox.client.output.FrameOutput;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ZebraThread class.
 *
 * @author gde
 * @version $Id: $Id
 */
public class ZebraThread extends Thread {
  private static final Logger LOG = LoggerFactory.getLogger(ZebraThread.class);
  private final Random rand;
  private final Agent agent;
  private final FrameOutput output;
  private final GameRunController controller;
  private final ZebraPuzzleDifficultyEnum size;
  private final boolean multipleGuesses;
  private final long maxSteps;

  /**
   * Constructor for ZebraThread.
   *
   * @param agent a {@link dev.aisandbox.client.agent.Agent} object.
   * @param output a {@link dev.aisandbox.client.output.FrameOutput} object.
   * @param controller a {@link dev.aisandbox.client.fx.GameRunController} object.
   * @param randomSalt a long.
   * @param multipleGuesses a boolean.
   * @param size a {@link dev.aisandbox.client.scenarios.zebra.ZebraPuzzleDifficultyEnum} object.
   * @param maxSteps a long.
   */
  public ZebraThread(
      Agent agent,
      FrameOutput output,
      GameRunController controller,
      long randomSalt,
      boolean multipleGuesses,
      ZebraPuzzleDifficultyEnum size,
      long maxSteps) {
    this.agent = agent;
    this.output = output;
    this.controller = controller;
    this.multipleGuesses = multipleGuesses;
    this.size = size;
    this.maxSteps = maxSteps;
    if (randomSalt != 0) {
      rand = new Random(randomSalt);
    } else {
      rand = new Random();
    }
  }

  @Getter @Setter private boolean running = false;

  /** Run the zebra scenario. */
  @Override
  public void run() {
    running = true;
    agent.setupAgent();
    ZebraPuzzle puzzle = new ZebraPuzzle(rand, 11, 10);
    puzzle.generateSolution();
    long steps = 0;
    while (running) {
      // ask the agent for an attempt
      // score the attempt (create history)
      boolean solved = false;
      if (solved || !multipleGuesses) {
        // create a new puzzle
      }
      // output current state
      steps++;
      if (steps == maxSteps) {
        running = false;
      }
    }

    running = false;
  }

  /** stopSimulation. */
  protected void stopSimulation() {
    running = false;
    try {
      this.join();
    } catch (InterruptedException e) {
      LOG.warn("Interrupted!", e);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
    }
  }
}
