package dev.aisandbox.client.scenarios.twisty;

import dev.aisandbox.client.agent.Agent;
import dev.aisandbox.client.agent.AgentException;
import dev.aisandbox.client.fx.GameRunController;
import dev.aisandbox.client.output.FrameOutput;
import dev.aisandbox.client.scenarios.maze.MazeRunner;
import dev.aisandbox.client.scenarios.twisty.api.TwistyRequest;
import dev.aisandbox.client.scenarios.twisty.api.TwistyResponse;
import dev.aisandbox.client.scenarios.twisty.puzzles.Cube3x3x3;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TwistyThread extends Thread {

  private final Agent agent;
  private final FrameOutput output;
  private final GameRunController controller;
  private final Random random;
  private final PuzzleType puzzleType;
  private final Long maxStepCount;
  private BufferedImage logo;
  private static final int SCRAMBLE_MOVES = 200;

  @Getter private boolean running = false;

  public TwistyThread(
      Agent agent,
      FrameOutput output,
      GameRunController controller,
      Random random,
      PuzzleType puzzleType,
      Long maxStepCount) {
    this.agent = agent;
    this.output = output;
    this.controller = controller;
    this.random = random;
    this.puzzleType = puzzleType;
    this.maxStepCount = maxStepCount;
    try {
      logo =
          ImageIO.read(MazeRunner.class.getResourceAsStream("/dev/aisandbox/client/fx/logo1.png"));
    } catch (IOException e) {
      log.error("Error loading logo", e);
    }
  }

  private void scramblePuzzle(TwistyPuzzle twistyPuzzle) throws NotExistentMoveException {
    for (int i = 0; i < SCRAMBLE_MOVES; i++) {
      String randomMove =
          twistyPuzzle.getMoveList().get(random.nextInt(twistyPuzzle.getMoveList().size()));
      twistyPuzzle.applyMove(randomMove);
    }
  }

  @Override
  public void run() {
    running = true;
    try {
      // setup agent
      agent.setupAgent();
      // setup puzzle
      TwistyPuzzle twistyPuzzle = new Cube3x3x3();
      // scramble the puzzle
      scramblePuzzle(twistyPuzzle);
      // save the cell colours - just in case we want to reset the puzzle
      String savedPuzzle = twistyPuzzle.getState();
      // create an (empty) list of actions
      List<String> actions = new ArrayList<>();
      // draw current state
      BufferedImage image = renderPuzzle(twistyPuzzle);
      controller.updateBoardImage(image);
      output.addFrame(image);
      while (running) {
        if (actions.isEmpty()) {
          // get next set of actions
          TwistyRequest request = new TwistyRequest();
          request.setPuzzleType(puzzleType.toString());
          request.setMoves(twistyPuzzle.getMoveList());
          request.setState(twistyPuzzle.getState());
          log.info("Requesting new actions from state {}", twistyPuzzle.getState());
          // TODO - include history
          TwistyResponse response = agent.postRequest(request, TwistyResponse.class);
          actions.addAll(Arrays.asList(response.getMove().trim().split(" ")));
          log.info("Action list now '{}'", actions);
        }
        // perform actions
        if (!actions.isEmpty()) {
          String action = actions.remove(0);
          // TODO count move scores
          log.info("Applying move '{}'", action);
          twistyPuzzle.applyMove(action);
          log.info("State now {}", twistyPuzzle.getState());
          // draw current state
          image = renderPuzzle(twistyPuzzle);
          controller.updateBoardImage(image);
          output.addFrame(image);
        }
        if (twistyPuzzle.isSolved()) {
          // TODO register goal
          // scramble
          scramblePuzzle(twistyPuzzle);
          savedPuzzle = twistyPuzzle.getState();
          // draw new state
          image = renderPuzzle(twistyPuzzle);
          controller.updateBoardImage(image);
          output.addFrame(image);
        }
      }
    } catch (AgentException ae) {
      controller.showAgentError(agent.getTarget(), ae);
    } catch (NotExistentMoveException e) {
      log.error("Tried to use a non existing move", e);
    } catch (IOException e) {
      log.error("IO Error running simulation", e);
    }
    running = false;
    controller.resetStartButton();
  }

  protected void stopSimulation() {
    running = false;
    try {
      this.join();
    } catch (InterruptedException e) {
      log.warn("Interrupted!", e);
      // Restore interrupted state...
      Thread.currentThread().interrupt();
    }
  }

  private BufferedImage renderPuzzle(TwistyPuzzle puzzle) {
    BufferedImage image = puzzle.getStateImage();
    Graphics2D g = image.createGraphics();
    // add logo
    g.drawImage(logo, 100, 50, null);
    // TODO draw move history and graphs
    return image;
  }
}