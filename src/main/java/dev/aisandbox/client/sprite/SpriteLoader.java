package dev.aisandbox.client.sprite;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * SpriteLoader class.
 *
 * @author gde
 * @version $Id: $Id
 */
@Component
public class SpriteLoader {

  private static final Logger LOG = LoggerFactory.getLogger(SpriteLoader.class.getName());

  private final boolean licencedAvailable;

  /** Constructor for SpriteLoader. */
  public SpriteLoader() {
    LOG.debug("Initialising sprite loader");
    licencedAvailable = false;
  }

  /**
   * loadSprites.
   *
   * @param path a {@link java.lang.String} object.
   * @param width a int.
   * @param height a int.
   * @return a {@link java.util.List} object.
   */
  public List<BufferedImage> loadSprites(String path, int width, int height) {
    try {
      return loadSpritesFromResources(path, width, height);
    } catch (IOException e) {
      LOG.error("Error loading sprites for {}", path, e);
      return new ArrayList<>();
    }
  }

  /**
   * loadSpritesFromResources.
   *
   * @param path a {@link java.lang.String} object.
   * @param width a int.
   * @param height a int.
   * @return a {@link java.util.List} object.
   * @throws java.io.IOException if any.
   */
  public static List<BufferedImage> loadSpritesFromResources(String path, int width, int height)
      throws IOException {
    BufferedImage sheet = ImageIO.read(SpriteLoader.class.getResourceAsStream(path));
    List<BufferedImage> images = new ArrayList<>();
    int x = 0;
    int y = 0;
    while (y < sheet.getHeight()) {
      while (x < sheet.getWidth()) {
        images.add(sheet.getSubimage(x, y, width, height));
        x += width;
      }
      x = 0;
      y += height;
    }
    return images;
  }
}
