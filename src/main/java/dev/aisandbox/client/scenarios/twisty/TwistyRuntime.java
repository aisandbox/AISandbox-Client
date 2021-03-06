package dev.aisandbox.client.scenarios.twisty;

import dev.aisandbox.client.agent.Agent;
import dev.aisandbox.client.agent.AgentException;
import dev.aisandbox.client.agent.AgentParserException;
import dev.aisandbox.client.agent.AgentResetException;
import dev.aisandbox.client.output.charts.BaseAWTGraph;
import dev.aisandbox.client.output.charts.FrequencyMassDistributionGraph;
import dev.aisandbox.client.profiler.ProfileStep;
import dev.aisandbox.client.scenarios.RuntimeResponse;
import dev.aisandbox.client.scenarios.ScenarioRuntime;
import dev.aisandbox.client.scenarios.SimulationException;
import dev.aisandbox.client.scenarios.twisty.api.TwistyRequest;
import dev.aisandbox.client.scenarios.twisty.api.TwistyRequestHistory;
import dev.aisandbox.client.scenarios.twisty.api.TwistyResponse;
import dev.aisandbox.client.scenarios.twisty.tpmodel.Move;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import javax.imageio.ImageIO;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class TwistyRuntime implements ScenarioRuntime {

  // agents
  private Agent agent = null;
  // puzzle elements
  @Setter Random random = new Random();
  @Setter TwistyPuzzle puzzle;
  @Setter boolean startSolved;
  private static final int SCRAMBLE_MOVES = 200;
  String savedState;
  List<String> actions = new ArrayList<>();
  int moves;
  TwistyRequestHistory history = null;
  // UI elements
  private BufferedImage logo;
  private List<String> moveHistory = new ArrayList<>();
  private static final int HISTORY_WIDTH = 9;
  private static final int HISTORY_HEIGHT = 5;
  private static final int MOVE_HISTORY_MAX = HISTORY_WIDTH * HISTORY_HEIGHT;
  // the graph showing how quickly we solve problems
  private FrequencyMassDistributionGraph frequencyGraph = new FrequencyMassDistributionGraph();
  // this graph doesnt change very often, so we cache it.
  private BufferedImage frequencyGraphImage = null;
  // is this the first frame - if so add the starting image
  private boolean firstFrame = true;

  public TwistyRuntime() {
    try {
      // load logo
      logo =
          ImageIO.read(
              TwistyRuntime.class.getResourceAsStream("/dev/aisandbox/client/fx/logo1.png"));
    } catch (IOException e) {
      log.error("Error loading logo", e);
      logo = new BufferedImage(0, 0, BufferedImage.TYPE_INT_RGB);
    }
    // setup graph
    frequencyGraph.setTitle("# Moves to solve");
    frequencyGraph.setXaxisHeader("# Moves");
    frequencyGraph.setYaxisHeader("Frequency");
    frequencyGraph.setGraphWidth(HISTORY_WIDTH * Move.MOVE_ICON_WIDTH);
    frequencyGraph.setGraphHeight(350);
  }

  @Override
  public void setAgents(List<Agent> agents) {
    agent = agents.get(0);
    if (agents.size() > 1) {
      log.error("Passed multiple agents when only one will be used.");
    }
  }

  @Override
  public void initialise() {
    if (!startSolved) {
      scramblePuzzle();
    }
    savedState = puzzle.getState();
    moves = 0;
  }

  @Override
  public RuntimeResponse advance() throws AgentException, SimulationException {
    ProfileStep profileStep = new ProfileStep();
    List<BufferedImage> frames = new ArrayList<>();
    if (firstFrame) {
      frames.add(renderPuzzle());
      profileStep.addStep("Graphics");
      firstFrame = false;
    }
    if (actions.isEmpty()) {
      try {
        // get next set of actions
        TwistyRequest request = new TwistyRequest();
        request.setPuzzleType(puzzle.getPuzzleName());
        request.setMoves(puzzle.getMoveList());
        request.setState(puzzle.getState());
        request.setHistory(history);
        log.info("Requesting new actions from state {}", puzzle.getState());
        TwistyResponse response = agent.postRequest(request, TwistyResponse.class);
        actions.addAll(Arrays.asList(response.getMove().trim().split(" ")));
        log.info("Action list now '{}'", actions);
        profileStep.addStep("Network");
      } catch (AgentResetException r) {
        log.info("Received reset puzzle from user");
        profileStep.addStep("Network");
        // clear history
        moves = 0;
        moveHistory.clear();
        // reset puzzle
        puzzle.resetPuzzle(savedState);
        // reset moves
        profileStep.addStep("Puzzle Setup");
      }
    }
    // perform actions
    if (!actions.isEmpty()) {
      String action = actions.remove(0);
      try {
        history = new TwistyRequestHistory();
        history.setStartState(puzzle.getState());
        history.setMoves(action);
        log.info("Applying move '{}'", action);
        moves += puzzle.applyMove(action);
        moveHistory.add(action);
        while (moveHistory.size() > MOVE_HISTORY_MAX) {
          moveHistory.remove(0);
        }
        history.setEndState(puzzle.getState());
        history.setSuccess(puzzle.isSolved());
        log.info("State now {}", puzzle.getState());
        profileStep.addStep("Simulation");
      } catch (NotExistentMoveException e) {
        log.warn("Client used non existent move '{}'", action);
        throw new AgentParserException(agent.getTarget(), "Move doesn't exist '" + action + "'");
      }
    }
    // check if the last move solved the puzzle - and we're looking for a solve
    if (puzzle.isSolved() && !startSolved) {
      // register solve
      frequencyGraph.addValue(moves);
      frequencyGraphImage = frequencyGraph.getImage();
      // draw the solved image
      frames.add(renderPuzzle());
      profileStep.addStep("Graphics");
      // reset the puzzle
      log.info("Puzzle solved, resetting");
      // clear history
      moves = 0;
      moveHistory.clear();
      // scramble
      scramblePuzzle();
      // this is the new saved puzzle
      savedState = puzzle.getState();
      // draw new state
      frames.add(renderPuzzle());
      profileStep.addStep("Puzzle Setup");
    } else {
      // draw the puzzle as normal
      frames.add(renderPuzzle());
      profileStep.addStep("Graphics");
    }
    return new RuntimeResponse(profileStep, frames);
  }

  @Override
  public void writeStatistics(File statisticsOutputFile) {
    try {
      PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(statisticsOutputFile)));
      out.print("mean,");
      out.println(frequencyGraph.getMean());
      out.print("std,");
      out.println(frequencyGraph.getStandardDeviation());
      out.println("Values");
      Iterator<Entry<Comparable<?>, Long>> iterator =
          frequencyGraph.getFrequencyTable().entrySetIterator();
      while (iterator.hasNext()) {
        Entry<Comparable<?>, Long> e = iterator.next();
        out.print(e.getKey());
        out.print(",");
        out.println(e.getValue());
      }
      out.close();
    } catch (IOException e) {
      log.error("Error writing stats to file " + statisticsOutputFile.getAbsolutePath(), e);
    }
  }

  private void scramblePuzzle() {
    for (int i = 0; i < SCRAMBLE_MOVES; i++) {
      try {
        String randomMove = puzzle.getMoveList().get(random.nextInt(puzzle.getMoveList().size()));
        puzzle.applyMove(randomMove);
      } catch (NotExistentMoveException e) {
        log.error("Non existent move when trying a move defined in the class", e);
      }
    }
  }

  private BufferedImage renderPuzzle() {
    BufferedImage image = puzzle.getStateImage();
    Graphics2D g = image.createGraphics();
    g.setFont(new Font("OpenSans", Font.PLAIN, 22));
    g.setRenderingHint(
        RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
    g.setRenderingHint(
        RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_LCD_HRGB);
    // add logo
    g.drawImage(logo, 100, 50, null);
    // draw history
    for (int i = 0; i < moveHistory.size(); i++) {
      BufferedImage moveImage = puzzle.getMoveImage(moveHistory.get(i));
      if (moveImage != null) {
        g.drawImage(
            moveImage,
            (i % HISTORY_WIDTH) * Move.MOVE_ICON_WIDTH + 1350,
            (i / HISTORY_WIDTH) * Move.MOVE_ICON_HEIGHT + 550,
            null);
      }
    }
    g.setColor(Color.BLACK);
    if (frequencyGraphImage != null) {
      g.drawImage(frequencyGraphImage, 1350, 100, null);
      g.drawString(
          "Mean : " + BaseAWTGraph.toSignificantDigitString(frequencyGraph.getMean(), 5),
          1400,
          480);
      g.drawString(
          "\u03C3\u00B2 : "
              + BaseAWTGraph.toSignificantDigitString(frequencyGraph.getStandardDeviation(), 5),
          1400,
          480 + 24);
    }
    return image;
  }
}
