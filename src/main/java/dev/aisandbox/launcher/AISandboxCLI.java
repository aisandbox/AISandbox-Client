package dev.aisandbox.launcher;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * AISandboxCLI class.
 *
 * @author gde
 * @version $Id: $Id
 */
@SpringBootApplication(scanBasePackages = "dev.aisandbox.client")
public class AISandboxCLI implements CommandLineRunner {
  private static final Logger LOG = LoggerFactory.getLogger(AISandboxCLI.class);

  /** {@inheritDoc} */
  @Override
  public void run(String... args) throws Exception {
    LOG.info("Launching in spring context - CLI");
  }
}
