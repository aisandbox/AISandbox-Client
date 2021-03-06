package dev.aisandbox.client.scenarios.zebra.api;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.aisandbox.client.scenarios.ServerResponse;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

/**
 * ZebraResponse class.
 *
 * @author gde
 * @version $Id: $Id
 */
@Data
@JsonIgnoreProperties(ignoreUnknown = false)
@XmlRootElement(name = "ZebraResponse")
public class ZebraResponse implements ServerResponse {
  private Solution solution = new Solution();
}
