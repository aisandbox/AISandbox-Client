package dev.aisandbox.client.scenarios.zebra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import dev.aisandbox.client.scenarios.zebra.vo.Characteristic;
import dev.aisandbox.client.scenarios.zebra.vo.Template;
import dev.aisandbox.launcher.AISandboxCLI;
import java.util.Random;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = {AISandboxCLI.class})
@SpringBootTest
public class GeneratorTest {

  private static final Logger LOG = LoggerFactory.getLogger(GeneratorTest.class.getName());

  Random rand = new Random();

  /** Create an 11x10 grid - the largest possible */
  @Test
  public void generateTemplateTest() {
    Template t = CharacteristicGenerator.createTemplate(rand, 11, 10);
    assertNotNull("Template=null", t);
    assertEquals("Template characteristic count", 11, t.getCharacteristics().size());
    for (Characteristic c : t.getCharacteristics()) {
      assertEquals("Characteristic length for " + c.getName(), 10, c.getInstances().size());
    }
  }

  /** Test grid trimming */
  @Test
  public void generateSmallTemplateTest() {
    Template t = CharacteristicGenerator.createTemplate(rand, 8, 4);
    assertNotNull("Template=null", t);
    assertEquals("Template characteristic count", 8, t.getCharacteristics().size());
    for (Characteristic c : t.getCharacteristics()) {
      assertEquals("Characteristic length for " + c.getName(), 4, c.getInstances().size());
    }
  }
}
