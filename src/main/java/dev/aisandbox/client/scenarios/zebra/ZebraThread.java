package dev.aisandbox.client.scenarios.zebra;

import dev.aisandbox.client.Agent;
import dev.aisandbox.client.fx.GameRunController;
import dev.aisandbox.client.output.FrameOutput;
import java.util.Random;
import lombok.Getter;
import lombok.Setter;

public class ZebraThread extends Thread {
  private final Random rand;
  private final Agent agent;
  private final FrameOutput output;
  private final GameRunController controller;
  private final ZebraPuzzleSizeEnum size;
  private final boolean multipleGuesses;
  private final long maxSteps;

  public ZebraThread(
      Agent agent,
      FrameOutput output,
      GameRunController controller,
      long randomSalt,
      boolean multipleGuesses,
      ZebraPuzzleSizeEnum size,
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
}