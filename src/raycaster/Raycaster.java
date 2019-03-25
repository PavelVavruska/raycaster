/*
 * The MIT License
 *
 * Copyright 2019 Pavel Vavruska.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package raycaster;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import raycaster.models.Map;
import raycaster.models.Player;

/**
 * Raycaster engine inspired by 90's PC games.
 *
 * @author Pavel Vavruska
 */
public class Raycaster extends JPanel {

    static JFrame frame = new JFrame("Raycaster");
    Player player = new Player(3, 3, 45, 90);
    static int screenWidth = 640;
    static int screenWidthExtension = 400;
    static int screenHeight = 480;
    int pixelSize = 20;
    Map map = new Map();
    BufferedImage img = null;

    public Raycaster() {

        try {
            img = ImageIO.read(new File("static/textures.png"));
        } catch (IOException e) {
            System.out.println("ERROR READ FILE");
        }

        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(final KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    switch (e.getKeyChar()) {
                        case 'd':
                            if (player.getAngle() + 10D >= 360D) {
                                player.setAngle(0D);
                            } else {
                                player.setAngle(player.getAngle() + 10D);
                            }
                            break;
                        case 'a':
                            if (player.getAngle() - 10D < 0D) {
                                player.setAngle(350D);
                            } else {
                                player.setAngle(player.getAngle() - 10D);
                            }
                            break;
                        case 'w':
                            player.setX(player.getX() + Math.cos(Math.toRadians(player.getAngle())) / 10);
                            player.setY(player.getY() + Math.sin(Math.toRadians(player.getAngle())) / 10);
                            break;
                        case 's':
                            player.setX(player.getX() - Math.cos(Math.toRadians(player.getAngle())) / 10);
                            player.setY(player.getY() - Math.sin(Math.toRadians(player.getAngle())) / 10);
                            break;
                        case 'h':
                            player.setFov(player.getFov() + 1);
                            break;
                        case 'n':
                            player.setFov(player.getFov() - 1);
                            break;
                        case 'q':
                            player.setX(player.getX() + Math.sin(Math.toRadians(player.getAngle())) / 10);
                            player.setY(player.getY() + Math.cos(Math.toRadians(player.getAngle())) / 10);
                            break;
                        case 'e':
                            player.setX(player.getX() - Math.sin(Math.toRadians(player.getAngle())) / 10);
                            player.setY(player.getY() - Math.cos(Math.toRadians(player.getAngle())) / 10);
                            break;
                        default:
                            break;
                    }
                    frame.repaint();
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    private void paintColoredDotInMenu(Graphics g, double x, double y, Color color) {
        g.setColor(color);
        g.drawLine(screenWidth + (int) (x * pixelSize), (int) (y * pixelSize),
                screenWidth + (int) (x * pixelSize), (int) (y * pixelSize));
    }

    private void paintColoredVerticalLine(Graphics g, double x, double y1, double y2, Color color) {
        g.setColor(color);
        g.drawLine((int) (x), (int) (y1),
                (int) (x), (int) (y2));
    }

    @Override
    public void paint(Graphics g) {
        g.setColor(new Color(50, 50, 50)); // ceiling
        g.fillRect(screenWidth, 0, screenWidth + screenWidthExtension, screenHeight);
        g.setColor(new Color(56, 56, 56)); // ceiling
        g.fillRect(0, 0, screenWidth, screenHeight / 2);
        g.setColor(new Color(112, 112, 112)); // floor
        g.fillRect(0, screenHeight / 2, screenWidth, screenHeight);

        int fovLinesLength = 12;
        g.setColor(Color.green);

        double playerAngle = player.getAngle();
        double playerAngleStart = playerAngle - player.getFov() / 2;

        int xx = 0;
        int yy = 0;

        g.setColor(Color.black);
        for (int[] y : map.getMap()) {
            for (int x : y) {
                if (x > 0) {
                    switch (x) {
                        case 1:
                            g.setColor(Color.black);
                            break;
                        case 2:
                            g.setColor(Color.cyan);
                            break;
                        case 3:
                            g.setColor(Color.lightGray);
                            break;
                        case 4:
                            g.setColor(Color.orange);
                            break;
                        default:
                            break;
                    }
                    g.drawRect(screenWidth + xx * pixelSize + 2, yy * pixelSize + 2, pixelSize - 4, pixelSize - 4);
                }
                xx++;
            }
            xx = 0;
            yy++;
        }

        for (int xcor = 0; xcor < screenWidth; xcor = xcor + 1) {

            double playerAngleActual = playerAngleStart + player.getFov() / screenWidth * xcor;
            // degrees fixed to range 0-359
            if (playerAngleActual < 0) {
                playerAngleActual += 360;
            }

            if (playerAngleActual >= 360) {
                playerAngleActual -= 360;
            }
            double rayAngle = playerAngleActual;

            double rayY = player.getY(); // start position of ray on Y axis
            double rayX = player.getX(); // start position of ray on X axis
            TreeMap<Double, Double> zBuffer = new TreeMap(Collections.reverseOrder()); // Double = how far, Integer = what object type

            double lengthDeltaX;
            double lengthDeltaY;
            double toTileEdgeAngle;

            double lastRayX = 0D;
            double lastRayY = 0D;
            int rayTimeToDie = 50;

            while (rayX > 0 && rayY > 0 && rayX < map.getSizeX() && rayY < map.getSizeY() && rayTimeToDie > 0) {
                rayTimeToDie--;

                if (lastRayX == rayX && lastRayY == rayY) {
                    break;
                }
                int checkX = (int) rayX;

                if (rayAngle > 90 && rayAngle < 270) {
                    checkX = (int) Math.ceil(rayX - 1);
                }
                int checkY = (int) rayY;

                if (rayAngle > 180 && rayAngle < 360) {
                    checkY = (int) Math.ceil(rayY - 1);
                }

                if (checkX >= 0 && checkY >= 0 && checkX < map.getSizeX() && checkY < map.getSizeY()) {
                    int objectOnTheMap = map.getMap()[(int) checkY][(int) checkX];
                    double offset = (rayX - checkX) / 2 + (rayY - checkY) / 2;

                    Double objectInfo = objectOnTheMap + offset;

                    if (objectOnTheMap != 0) // read the map and save it to zbuffer
                    {
                        double x12 = player.getX() - rayX;
                        double y12 = player.getY() - rayY;
                        double distanceOfTwoPoints = Math.sqrt(Math.pow(x12, 2) + Math.pow(y12, 2));
                        zBuffer.put(distanceOfTwoPoints, objectInfo);
                        break;
                    }
                }

                /*
                4 QUADRANTS:
                1   0- 90
                2  90-180
                3 180-270
                4 270-360
                 */
                if (rayAngle >= 0 && rayAngle <= 90) {
                    lengthDeltaX = 1 + (int) rayX - rayX;
                    lengthDeltaY = 1 + (int) rayY - rayY;

                    toTileEdgeAngle = Math.toDegrees(Math.atan(lengthDeltaY / lengthDeltaX));

                    if (toTileEdgeAngle >= rayAngle) {
                        rayX = rayX + lengthDeltaX;
                        rayY += Math.tan(Math.toRadians(rayAngle)) * lengthDeltaX;
                    } else {
                        rayX += lengthDeltaY / Math.tan(Math.toRadians(rayAngle));
                        rayY = rayY + lengthDeltaY;
                    }
                    paintColoredDotInMenu(g, rayX, rayY, Color.green);

                } else if (rayAngle > 90 && rayAngle < 180) {
                    lengthDeltaX = 1 - (int) Math.ceil(rayX) + rayX;
                    lengthDeltaY = 1 + (int) rayY - rayY;
                    toTileEdgeAngle = Math.toDegrees(Math.atan(lengthDeltaX / lengthDeltaY));

                    if (toTileEdgeAngle + 90 <= rayAngle) {
                        rayX = rayX - lengthDeltaX;
                        rayY += lengthDeltaX / Math.tan(Math.toRadians(rayAngle - 90));
                    } else {
                        rayX -= Math.tan(Math.toRadians(rayAngle - 90)) * lengthDeltaY;
                        rayY = rayY + lengthDeltaY;
                    }
                    paintColoredDotInMenu(g, rayX, rayY, Color.green);

                } else if (rayAngle >= 180 && rayAngle < 270) {
                    lengthDeltaX = 1 - (int) Math.ceil(rayX) + rayX;
                    lengthDeltaY = 1 - (int) Math.ceil(rayY) + rayY;
                    toTileEdgeAngle = Math.toDegrees(Math.atan(lengthDeltaY / lengthDeltaX));

                    if (toTileEdgeAngle + 180 > rayAngle) {
                        rayX = rayX - lengthDeltaX;
                        rayY -= Math.tan(Math.toRadians(rayAngle - 180)) * lengthDeltaX;
                    } else {
                        rayX -= lengthDeltaY / Math.tan(Math.toRadians(rayAngle - 180));
                        rayY = rayY - lengthDeltaY;
                    }
                    paintColoredDotInMenu(g, rayX, rayY, Color.green);

                } else if (rayAngle >= 270 && rayAngle < 360) {
                    lengthDeltaX = 1 + (int) rayX - rayX;
                    lengthDeltaY = 1 - (int) Math.ceil(rayY) + rayY;
                    toTileEdgeAngle = Math.toDegrees(Math.atan(lengthDeltaX / lengthDeltaY));

                    if (toTileEdgeAngle + 270 > rayAngle) {
                        rayX += Math.tan(Math.toRadians(rayAngle - 270)) * lengthDeltaY;
                        rayY = rayY - lengthDeltaY;
                    } else {
                        rayX = rayX + lengthDeltaX;
                        rayY -= lengthDeltaX / Math.tan(Math.toRadians(rayAngle - 270));
                    }
                    paintColoredDotInMenu(g, rayX, rayY, Color.green);
                }
                lastRayX = rayY;
                lastRayY = rayX;
            }

            if (!zBuffer.isEmpty()) {

                for (java.util.Map.Entry<Double, Double> entry : zBuffer.entrySet()) {

                    // Actual line by line rendering of the visible object
                    int start = (int) (screenHeight / 2 - screenHeight / (entry.getKey() * 2));
                    int end = (int) (screenHeight / 2 + screenHeight / (entry.getKey() * 2));
                    double middle = 2 * screenHeight / (entry.getKey() * 2);
                    if (middle > 1000) {
                        // Performance fix for walking through the wall
                        middle = 1000;
                    }

                    double oneArtificialPixelSize = middle / 64;
                    if (oneArtificialPixelSize < 0.001) {
                        oneArtificialPixelSize = 0.001;
                    }

                    for (int verticalPixel = 1; verticalPixel <= middle; verticalPixel++) {
                        int colorPixel = (int) ((verticalPixel) / oneArtificialPixelSize);
                        if (colorPixel == 0) {
                            colorPixel = 1;
                        }
                        if (colorPixel >= 63) {
                            colorPixel = 63;
                        }
                        int xCorTexture = (int) (entry.getValue() * 64 - 64);
                        if (xCorTexture > 256) {
                            xCorTexture = 256;
                        }
                        if (xCorTexture <= 1) {
                            xCorTexture = 1;
                        }

                        Color imgColor = new Color(img.getRGB(xCorTexture, colorPixel));
                        int red = (int) (imgColor.getRed() - entry.getKey() * 5);
                        int green = (int) (imgColor.getGreen() - entry.getKey() * 5);
                        int blue = (int) (imgColor.getBlue() - entry.getKey() * 5);

                        Color resultColor = new Color((red >= 0) ? red : 0, (green >= 0) ? green : 0, (blue >= 0) ? blue : 0);

                        paintColoredVerticalLine(g,
                                xcor,
                                start + middle / 64 * colorPixel,
                                start + middle / 64 * colorPixel + oneArtificialPixelSize,
                                resultColor);
                    }

                }
            }
        }

        // player
        g.setColor(Color.white);
        g.drawRect(
                screenWidth
                + (int) player.getX()
                * pixelSize,
                (int) player.getY()
                * pixelSize,
                pixelSize,
                pixelSize);
        g.setColor(Color.GREEN);
        g.drawLine(
                screenWidth
                + (int) (player.getX()
                * pixelSize),
                (int) (player.getY()
                * pixelSize),
                screenWidth + (int) (player.getX()
                * pixelSize + Math.cos(Math.toRadians(player.getAngle())) * pixelSize * fovLinesLength),
                (int) (player.getY()
                * pixelSize + Math.sin(Math.toRadians(player.getAngle())) * pixelSize * fovLinesLength));
        //player fov
        g.setColor(Color.GREEN);
        // -1/2 fov
        g.drawLine(
                screenWidth
                + (int) (player.getX()
                * pixelSize),
                (int) (player.getY()
                * pixelSize),
                screenWidth + (int) (player.getX()
                * pixelSize + Math.cos(Math.toRadians(player.getAngle() - player.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                * pixelSize + Math.sin(Math.toRadians(player.getAngle() - player.getFov() / 2)) * pixelSize * fovLinesLength));
        // +1/2 fov
        g.drawLine(
                screenWidth
                + (int) (player.getX()
                * pixelSize),
                (int) (player.getY()
                * pixelSize),
                screenWidth + (int) (player.getX()
                * pixelSize + Math.cos(Math.toRadians(player.getAngle() + player.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                * pixelSize + Math.sin(Math.toRadians(player.getAngle() + player.getFov() / 2)) * pixelSize * fovLinesLength));
        // connect fov lines
        g.drawLine(
                screenWidth
                + (int) (player.getX()
                * pixelSize + Math.cos(Math.toRadians(player.getAngle() - player.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                * pixelSize + Math.sin(Math.toRadians(player.getAngle() - player.getFov() / 2)) * pixelSize * fovLinesLength),
                screenWidth + (int) (player.getX()
                * pixelSize + Math.cos(Math.toRadians(player.getAngle() + player.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                * pixelSize + Math.sin(Math.toRadians(player.getAngle() + player.getFov() / 2)) * pixelSize * fovLinesLength));
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {

        frame.getContentPane().add(new Raycaster());
        frame.setSize(screenWidth + screenWidthExtension, screenHeight);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setResizable(false);
        frame.setFocusable(true);
    }
}
