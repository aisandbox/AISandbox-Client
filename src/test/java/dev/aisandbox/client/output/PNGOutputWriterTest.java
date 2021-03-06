package dev.aisandbox.client.output;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import java.io.IOException;
import java.util.Locale;
import lombok.extern.slf4j.Slf4j;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

@Slf4j
public class PNGOutputWriterTest {

  @Rule public TemporaryFolder folder = new TemporaryFolder();

  @Test
  public void openTest() throws IOException {
    // this should open a directory within the temp folder
    log.info("Opening dir in " + folder.getRoot().getAbsolutePath());
    PNGOutputWriter png = new PNGOutputWriter();
    png.open(folder.getRoot());
    // there should be a new directory
    assertEquals("No new directory created", 0, folder.getRoot().listFiles().length);
  }

  @Test
  public void WriteTest() throws IOException {
    // open a new session then write three frames
    log.info("Write test in {}", folder.getRoot());
    PNGOutputWriter png = new PNGOutputWriter();
    png.open(folder.getRoot());
    png.addFrame(OutputTools.getBlackScreen());
    png.addFrame(OutputTools.getWhiteScreen());
    png.addFrame(OutputTools.getColouredScreen(Color.RED));
    png.close();
    assertEquals("Three files in directory", 3, folder.getRoot().listFiles().length);
  }

  @Test(expected = IOException.class)
  public void writeToClosedTest() throws IOException {
    // try to write to a session before it's open
    PNGOutputWriter png = new PNGOutputWriter();
    png.addFrame(OutputTools.getWhiteScreen());
  }

  @Test(expected = IOException.class)
  public void doubleOpenTest() throws IOException {
    // open a new session then try and open it without closing
    PNGOutputWriter png = new PNGOutputWriter();
    png.open(folder.getRoot());
    png.addFrame(OutputTools.getBlackScreen());
    png.open(folder.getRoot()); // THIS SHOULD THROW AN EXCEPTION
    png.close();
  }

  @Test(expected = Test.None.class /* no exception expected */)
  public void openCloseTest() throws IOException {
    // open a new session then try and open it without closing
    PNGOutputWriter png = new PNGOutputWriter();
    png.open(folder.getRoot());
    png.addFrame(OutputTools.getBlackScreen());
    png.close();
    png.open(folder.getRoot()); // No exception
    png.addFrame(OutputTools.getBlackScreen());
    png.close();
  }

  @Test
  public void nameTest() {
    PNGOutputWriter png = new PNGOutputWriter();
    assertNotNull("Null name", png.getName(Locale.UK));
    assertTrue("Short name", png.getName(Locale.UK).length() > 2);
  }
}
