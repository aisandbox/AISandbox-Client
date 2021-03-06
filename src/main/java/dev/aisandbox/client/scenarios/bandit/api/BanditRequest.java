package dev.aisandbox.client.scenarios.bandit.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.aisandbox.client.scenarios.ServerRequest;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/** BanditRequest */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@XmlRootElement(name = "BanditRequest")
public class BanditRequest implements ServerRequest {
  private BanditRequestHistory history = null;
  private String sessionID;
  private int banditCount;
  private int pullCount;
  private int pull;
}
