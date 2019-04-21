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
import java.awt.Graphics2D;
import java.awt.KeyEventDispatcher;
import java.awt.KeyboardFocusManager;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.TreeMap;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import raycaster.models.Config;
import raycaster.models.Map;
import raycaster.models.Player;

/**
 * Raycaster engine inspired by 90's PC games.
 *
 * @author Pavel Vavruska
 */
public class Raycaster extends JPanel {

    private static JFrame frame = new JFrame("Raycaster");

    private static int screenWidth = 640;
    private static int screenWidthExtension = 400;
    private static int screenHeight = 480;
    private static int pixelSize = 20;
    private static Long startTime = 0L;
    private static Long endTime = 0L;
    private static LinkedList<Integer> frameTimes = new LinkedList<>();
    private static  BufferedImage imgObjects = null;
    private static  BufferedImage imgBackground = null;

    private Player player = new Player(3, 3, 45);
    private Config config = new Config(90, true, true);

    private Map map = new Map();

    private static int cores = Runtime.getRuntime().availableProcessors();

    private static Thread[] threads = new Thread[cores];
    private static BufferedImage[] bufferedImageCore = new BufferedImage[cores];

    private static Graphics2D[] g2dCore = new Graphics2D[cores];

    public Raycaster() {
        System.out.println("Number of cores:" + cores);
        try {
            imgObjects = ImageIO.read(new File("static/textures.png"));
        } catch (IOException e) {
            System.out.println("Error while loading wall-object texture file.");
        }

        try {
            imgBackground = ImageIO.read(new File("static/background.png"));
        } catch (IOException e) {
            System.out.println("Error while loading background texture file.");
        }


        KeyEventDispatcher keyEventDispatcher = new KeyEventDispatcher() {
            @Override
            public boolean dispatchKeyEvent(final KeyEvent e) {
                if (e.getID() == KeyEvent.KEY_TYPED) {
                    switch (e.getKeyChar()) {
                        case 'd':
                            player.setVelocityAngle(player.getVelocityAngle()+1D);
                            break;
                        case 'a':
                            player.setVelocityAngle(player.getVelocityAngle()-1D);
                            break;
                        case 'w':
                            player.setVelocityX(player.getVelocityX() + Math.cos(Math.toRadians(player.getAngle())) / 100);
                            player.setVelocityY(player.getVelocityY() + Math.sin(Math.toRadians(player.getAngle())) / 100);
                            break;
                        case 's':
                            player.setVelocityX(player.getVelocityX() - Math.cos(Math.toRadians(player.getAngle())) / 100);
                            player.setVelocityY(player.getVelocityY() - Math.sin(Math.toRadians(player.getAngle())) / 100);
                            break;
                        case 'h':
                            config.setFov(config.getFov() + 1);
                            break;
                        case 'n':
                            config.setFov(config.getFov() - 1);
                            break;
                        case 'p':
                            config.setPerspectiveCorrectionOn(!config.isPerspectiveCorrectionOn());
                            break;
                        case 'm':
                            config.setMetricOn(!config.isMetricOn());
                            break;
                        case 'q':
                            player.setAngle(player.getAngle() - 90D);
                            player.setVelocityX(player.getVelocityX() + Math.cos(Math.toRadians(player.getAngle())) / 100);
                            player.setVelocityY(player.getVelocityY() + Math.sin(Math.toRadians(player.getAngle())) / 100);
                            player.setAngle(player.getAngle() + 90D);
                            break;
                        case 'e':
                            player.setAngle(player.getAngle() + 90D);
                            player.setVelocityX(player.getVelocityX() + Math.cos(Math.toRadians(player.getAngle())) / 100);
                            player.setVelocityY(player.getVelocityY() + Math.sin(Math.toRadians(player.getAngle())) / 100);
                            player.setAngle(player.getAngle() - 90D);
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        };
        KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(keyEventDispatcher);
    }

    private void paintColoredDotInMenu(Graphics2D g, double x, double y, Color color) {
        g.setColor(color);
        g.drawLine(screenWidth + (int) (x * pixelSize), (int) (y * pixelSize),
                screenWidth + (int) (x * pixelSize), (int) (y * pixelSize));
    }

    private void paintColoredVerticalLine(Graphics2D g, double x, double y1, double y2, Color color) {
        g.setColor(color);
        g.drawLine((int) (x), (int) (y1),
                (int) (x), (int) (y2));
    }

    @Override
    protected void paintComponent(Graphics g)
    {
        BufferedImage bufferedImage = new BufferedImage(screenWidth+screenWidthExtension, screenHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = bufferedImage.createGraphics();
        //paint using g2d ...
        endTime = startTime;
        startTime = System.nanoTime();

        g2d.setColor(new Color(50, 50, 50)); // minimap
        g2d.fillRect(screenWidth, 0, screenWidth + screenWidthExtension, screenHeight);
        /*g2d.setColor(new Color(56, 56, 56)); // ceiling
        g2d.fillRect(0, 0, screenWidth, screenHeight / 2);
        g2d.setColor(new Color(112, 112, 112)); // floor
        g2d.fillRect(0, screenHeight / 2, screenWidth, screenHeight);*/
        //g2d.drawImage(imgBackground,0,0);
        g2d.drawImage(imgBackground, 0, 0, 640, 480, null);
        //g2d.dispose();

        double playerAngle = player.getAngle();
        double playerAngleStart = playerAngle - config.getFov() / 2;

        int xx = 0;
        int yy = 0;

        g2d.setColor(Color.black);
        for (int[] y : map.getMap()) {
            for (int x : y) {
                if (x >= 0) {
                    switch (x) {
                        case 0:
                        case 1:
                        case 2:
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                        case 7:
                        case 8:
                            g2d.setColor(Color.orange);
                            break;
                        case 10:
                            g2d.setColor(Color.black);
                            break;
                        case 12:
                            g2d.setColor(Color.cyan);
                            break;
                        case 14:
                            g2d.setColor(Color.lightGray);
                            break;
                        default:
                            break;
                    }
                    g2d.drawRect(screenWidth + xx * pixelSize + 2, yy * pixelSize + 2, pixelSize - 4, pixelSize - 4);
                }
                xx++;
            }
            xx = 0;
            yy++;
        }

        for (int threadCurrentNumber = 0; threadCurrentNumber < cores; threadCurrentNumber++) {
            final int threadCurrentNumberFinal = threadCurrentNumber;
            int threadStartCor = screenWidth/cores*threadCurrentNumberFinal;
            int threadEndCor = screenWidth/cores;
            bufferedImageCore[threadCurrentNumberFinal] = new BufferedImage(threadEndCor, screenHeight, BufferedImage.TYPE_INT_ARGB);
            g2dCore[threadCurrentNumberFinal] = bufferedImageCore[threadCurrentNumberFinal].createGraphics();

            threads[threadCurrentNumberFinal] = new Thread(new Runnable() {
                @Override
                public void run() {
                    for (int screenCoordinateX = threadStartCor; screenCoordinateX < threadStartCor+threadEndCor; screenCoordinateX++) {
                        double rayAngle = playerAngleStart + config.getFov() / screenWidth * screenCoordinateX;
                        // degrees fixed to range 0-359
                        if (rayAngle < 0) {
                            rayAngle += 360;
                        }

                        if (rayAngle >= 360) {
                            rayAngle -= 360;
                        }

                        double rayPositionY = player.getY(); // start position of ray on Y axis
                        double rayPositionX = player.getX(); // start position of ray on X axis
                        TreeMap<Double, Double> zBufferWall = new TreeMap(Collections.reverseOrder()); // Double = how far, Integer = what object type
                        TreeMap<Double, Double> zBufferObject = new TreeMap(Collections.reverseOrder()); // Double = how far, Integer = what object type

                        double rayLengthDeltaX;
                        double rayLengthDeltaY;
                        double rayAngleToTileEdge = 0D;
                        double rayPerspectiveCorrectionAngle = 0D;

                        double rayPositionPreviousX = 0D;
                        double rayPositionPreviousY = 0D;
                        byte rayTimeToDie = 50;

                        while (rayPositionX > 0 && rayPositionY > 0 && rayPositionX < map.getSizeX() && rayPositionY < map.getSizeY() && rayTimeToDie > 0) {
                            rayTimeToDie--;

                            if (rayPositionPreviousX == rayPositionX && rayPositionPreviousY == rayPositionY) {
                                break;
                            }
                            int rayPositionForMapCollisionX = (int) rayPositionX;

                            if (rayAngle > 90 && rayAngle < 270) {
                                rayPositionForMapCollisionX = (int) Math.ceil(rayPositionX - 1);
                            }
                            int rayPositionForMapCollisionY = (int) rayPositionY;

                            if (rayAngle > 180 && rayAngle < 360) {
                                rayPositionForMapCollisionY = (int) Math.ceil(rayPositionY - 1);
                            }

                            if (rayPositionForMapCollisionX >= 0 && rayPositionForMapCollisionY >= 0 && rayPositionForMapCollisionX < map.getSizeX() && rayPositionForMapCollisionY < map.getSizeY()) {
                                int objectOnTheMapTypeId = map.getMap()[(int) rayPositionForMapCollisionY][(int) rayPositionForMapCollisionX];
                                double rayPositionOffsetFromTheObjectEdge = ((rayPositionX - rayPositionForMapCollisionX) + (rayPositionY - rayPositionForMapCollisionY));

                                Double objectOnTheMapTypeIdWithOffset = objectOnTheMapTypeId + rayPositionOffsetFromTheObjectEdge;

                                if (objectOnTheMapTypeId != -1) // read the map and save it to zbuffer
                                {
                                    double rayDistanceFromPlayerX = player.getX() - rayPositionX;
                                    double rayDistanceFromPlayerY = player.getY() - rayPositionY;
                                    double rayDistanceFromPlayer = Math.sqrt(Math.pow(rayDistanceFromPlayerX, 2) + Math.pow(rayDistanceFromPlayerY, 2));
                                    if (objectOnTheMapTypeId >= 10) { // solid walls
                                        if (config.isPerspectiveCorrectionOn()) {
                                            rayPerspectiveCorrectionAngle = Math.abs(rayAngle) - Math.abs(playerAngle);
                                            double rayDistanceFromPlayerWithPerspectiveCorrection = Math.cos(Math.toRadians(rayPerspectiveCorrectionAngle)) * rayDistanceFromPlayer;
                                            zBufferWall.put(rayDistanceFromPlayerWithPerspectiveCorrection, objectOnTheMapTypeIdWithOffset - 10);
                                        } else {
                                            zBufferWall.put(rayDistanceFromPlayer, objectOnTheMapTypeIdWithOffset - 10);
                                        }
                                        break;
                                    } else { // transparent walls
                                        if (config.isPerspectiveCorrectionOn()) {
                                            rayPerspectiveCorrectionAngle = Math.abs(rayAngle) - Math.abs(playerAngle);
                                            double rayDistanceFromPlayerWithPerspectiveCorrection = Math.cos(Math.toRadians(rayPerspectiveCorrectionAngle)) * rayDistanceFromPlayer;
                                            zBufferObject.put(rayDistanceFromPlayerWithPerspectiveCorrection, objectOnTheMapTypeIdWithOffset);
                                        } else {
                                            zBufferObject.put(rayDistanceFromPlayer, objectOnTheMapTypeIdWithOffset);
                                        }
                                    }
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
                                rayLengthDeltaX = 1 + (int) rayPositionX - rayPositionX;
                                rayLengthDeltaY = 1 + (int) rayPositionY - rayPositionY;

                                rayAngleToTileEdge = Math.toDegrees(Math.atan(rayLengthDeltaY / rayLengthDeltaX));

                                if (rayAngleToTileEdge >= rayAngle) {
                                    rayPositionX = rayPositionX + rayLengthDeltaX;
                                    rayPositionY += Math.tan(Math.toRadians(rayAngle)) * rayLengthDeltaX;
                                } else {
                                    rayPositionX += rayLengthDeltaY / Math.tan(Math.toRadians(rayAngle));
                                    rayPositionY = rayPositionY + rayLengthDeltaY;
                                }

                            } else if (rayAngle > 90 && rayAngle < 180) {
                                rayLengthDeltaX = 1 - (int) Math.ceil(rayPositionX) + rayPositionX;
                                rayLengthDeltaY = 1 + (int) rayPositionY - rayPositionY;
                                rayAngleToTileEdge = 90 + Math.toDegrees(Math.atan(rayLengthDeltaX / rayLengthDeltaY));

                                if (rayAngleToTileEdge <= rayAngle) {
                                    rayPositionX = rayPositionX - rayLengthDeltaX;
                                    rayPositionY += rayLengthDeltaX / Math.tan(Math.toRadians(rayAngle - 90));
                                } else {
                                    rayPositionX -= Math.tan(Math.toRadians(rayAngle - 90)) * rayLengthDeltaY;
                                    rayPositionY = rayPositionY + rayLengthDeltaY;
                                }

                            } else if (rayAngle >= 180 && rayAngle < 270) {
                                rayLengthDeltaX = 1 - (int) Math.ceil(rayPositionX) + rayPositionX;
                                rayLengthDeltaY = 1 - (int) Math.ceil(rayPositionY) + rayPositionY;
                                rayAngleToTileEdge = 180 + Math.toDegrees(Math.atan(rayLengthDeltaY / rayLengthDeltaX));

                                if (rayAngleToTileEdge > rayAngle) {
                                    rayPositionX = rayPositionX - rayLengthDeltaX;
                                    rayPositionY -= Math.tan(Math.toRadians(rayAngle - 180)) * rayLengthDeltaX;
                                } else {
                                    rayPositionX -= rayLengthDeltaY / Math.tan(Math.toRadians(rayAngle - 180));
                                    rayPositionY = rayPositionY - rayLengthDeltaY;
                                }

                            } else if (rayAngle >= 270 && rayAngle < 360) {
                                rayLengthDeltaX = 1 + (int) rayPositionX - rayPositionX;
                                rayLengthDeltaY = 1 - (int) Math.ceil(rayPositionY) + rayPositionY;
                                rayAngleToTileEdge = 270 + Math.toDegrees(Math.atan(rayLengthDeltaX / rayLengthDeltaY));

                                if (rayAngleToTileEdge > rayAngle) {
                                    rayPositionX += Math.tan(Math.toRadians(rayAngle - 270)) * rayLengthDeltaY;
                                    rayPositionY = rayPositionY - rayLengthDeltaY;
                                } else {
                                    rayPositionX = rayPositionX + rayLengthDeltaX;
                                    rayPositionY -= rayLengthDeltaX / Math.tan(Math.toRadians(rayAngle - 270));
                                }
                            }
                            paintColoredDotInMenu(g2d, rayPositionX, rayPositionY, Color.green);
                            rayPositionPreviousX = rayPositionY;
                            rayPositionPreviousY = rayPositionX;
                        }
                        drawFromZBufferWall(zBufferWall, screenCoordinateX, threadCurrentNumberFinal, threadStartCor);
                        drawFromZBufferObject(zBufferObject, screenCoordinateX, threadCurrentNumberFinal, threadStartCor);
                    }
                }
            });
        }
        for (int threadCurrentNumber = 0; threadCurrentNumber < cores; threadCurrentNumber++) {
            threads[threadCurrentNumber].start();
        }

        for (int threadCurrentNumber = 0; threadCurrentNumber < cores; threadCurrentNumber++) {
            try {
                threads[threadCurrentNumber].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            int threadStartCoordinateX = screenWidth/cores*threadCurrentNumber;
            g2dCore[threadCurrentNumber].dispose();
            g2d.drawImage(bufferedImageCore[threadCurrentNumber],null, threadStartCoordinateX, 0);
        }

        drawPlayerOnMap(g2d, 2);

        if (config.isMetricOn()) {
            drawPlayerMetricOnMap(g2d, 12);
            drawMetricOverlay(g2d);
        }

        if (endTime != 0L) {
            int millisElapsed = java.lang.Math.toIntExact((startTime - endTime)/1000/1000);
            frameTimes.add(millisElapsed);
            if (frameTimes.size() > screenWidthExtension) {
                frameTimes.removeFirst();
            }

            player.tick(map);
            frame.repaint();
        }

        Graphics2D g2dComponent = (Graphics2D) g;
        g2dComponent.drawImage(bufferedImage, null, 0, 0);
    }

    private void drawFromZBufferWall(TreeMap<Double, Double> zBufferWall, int xcor, int finalNumThreads, int threadStartCor) {
        for (java.util.Map.Entry<Double, Double> entry : zBufferWall.entrySet()) {

            // Actual line by line rendering of the visible object
            int start = (int) (screenHeight / 2 - screenHeight / (entry.getKey() * 2));
            int end = (int) (screenHeight / 2 + screenHeight / (entry.getKey() * 2));
            double middle = 2 * screenHeight / (entry.getKey() * 2);

            double oneArtificialPixelSize = middle / 64;

            for (int verticalPixel = 1; verticalPixel <= middle; verticalPixel++) { // y full range
                int colorPixel = (int) (verticalPixel / oneArtificialPixelSize);

                if (colorPixel > 63) {
                    colorPixel = 63;
                }

                int xCorTexture = (int) (entry.getValue() * 64);

                if (xCorTexture <= 1) {
                    xCorTexture = 1;
                }

                Color imgColor = new Color(imgObjects.getRGB(xCorTexture, 64 +colorPixel));
                int red = (int) (imgColor.getRed() - entry.getKey() * 5);
                int green = (int) (imgColor.getGreen() - entry.getKey() * 5);
                int blue = (int) (imgColor.getBlue() - entry.getKey() * 5);

                Color resultColor = new Color((red >= 0) ? red : 0, (green >= 0) ? green : 0, (blue >= 0) ? blue : 0);
                // Performance fix - skipping colorPixels outside of the POV
                if (start + middle / 64 * colorPixel >= -64 && start + middle / 64 * colorPixel <= 450) {
                    paintColoredVerticalLine(g2dCore[finalNumThreads],
                            xcor-threadStartCor,
                            start + middle / 64 * colorPixel,
                            start + middle / 64 * colorPixel + oneArtificialPixelSize,
                            resultColor);
                }
            }
        }
    }

    private void drawFromZBufferObject(TreeMap<Double, Double> zBufferObject, int xcor, int finalNumThreads, int threadStartCor) {
        for (java.util.Map.Entry<Double, Double> entry : zBufferObject.entrySet()) {

            // Actual line by line rendering of the visible object
            int start = (int) (screenHeight / 2 - screenHeight / (entry.getKey() * 2));
            double middle = 2 * screenHeight / (entry.getKey() * 2);

            double oneArtificialPixelSize = middle / 64;

            if (oneArtificialPixelSize > 150) {
                // fix FPS drop when near objects
                break;
            }

            for (int verticalPixel = 1; verticalPixel <= middle; verticalPixel++) { // y full range
                int colorPixel = (int) (verticalPixel / oneArtificialPixelSize);

                if (colorPixel > 63) {
                    colorPixel = 63;
                }

                int xCorTexture = (int) (entry.getValue() * 64);

                if (xCorTexture <= 1) {
                    xCorTexture = 1;
                }

                Color imgColor = new Color(imgObjects.getRGB(xCorTexture, colorPixel));
                if (imgColor.getGreen() >= 1) {
                    int red = (int) (imgColor.getRed() - entry.getKey() * 5);
                    int green = (int) (imgColor.getGreen() - entry.getKey() * 5);
                    int blue = (int) (imgColor.getBlue() - entry.getKey() * 5);

                    Color resultColor = new Color((red >= 0) ? red : 0, (green >= 0) ? green : 0, (blue >= 0) ? blue : 0);

                    // Performance fix - skipping colorPixels outside of the POV
                    if (start + middle / 64 * colorPixel >= -64 && start + middle / 64 * colorPixel <= 450) {
                        paintColoredVerticalLine(g2dCore[finalNumThreads],
                                xcor - threadStartCor,
                                start + middle / 64 * colorPixel,
                                start + middle / 64 * colorPixel + oneArtificialPixelSize,
                                resultColor);
                    }
                }
            }
        }
    }

    private void drawPlayerOnMap(Graphics2D g2d, int playerSize) {
        // player
        g2d.setColor(Color.white);
        g2d.drawRect(
                screenWidth
                        + (int) player.getX()
                        * pixelSize,
                (int) player.getY()
                        * pixelSize,
                pixelSize,
                pixelSize);

        g2d.drawLine(
                screenWidth
                        + (int) (player.getX()
                        * pixelSize),
                (int) (player.getY()
                        * pixelSize),
                screenWidth + (int) (player.getX()
                        * pixelSize + Math.cos(Math.toRadians(player.getAngle())) * pixelSize * playerSize),
                (int) (player.getY()
                        * pixelSize + Math.sin(Math.toRadians(player.getAngle())) * pixelSize * playerSize));
    }
    private void drawPlayerMetricOnMap(Graphics2D g2d, int fovLinesLength) {
        //player fov
        g2d.setColor(Color.GREEN);
        // -1/2 fov
        g2d.drawLine(
                screenWidth
                        + (int) (player.getX()
                        * pixelSize),
                (int) (player.getY()
                        * pixelSize),
                screenWidth + (int) (player.getX()
                        * pixelSize + Math.cos(Math.toRadians(player.getAngle() - config.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                        * pixelSize + Math.sin(Math.toRadians(player.getAngle() - config.getFov() / 2)) * pixelSize * fovLinesLength));
        // +1/2 fov
        g2d.drawLine(
                screenWidth
                        + (int) (player.getX()
                        * pixelSize),
                (int) (player.getY()
                        * pixelSize),
                screenWidth + (int) (player.getX()
                        * pixelSize + Math.cos(Math.toRadians(player.getAngle() + config.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                        * pixelSize + Math.sin(Math.toRadians(player.getAngle() + config.getFov() / 2)) * pixelSize * fovLinesLength));
        // connect fov lines
        g2d.drawLine(
                screenWidth
                        + (int) (player.getX()
                        * pixelSize + Math.cos(Math.toRadians(player.getAngle() - config.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                        * pixelSize + Math.sin(Math.toRadians(player.getAngle() - config.getFov() / 2)) * pixelSize * fovLinesLength),
                screenWidth + (int) (player.getX()
                        * pixelSize + Math.cos(Math.toRadians(player.getAngle() + config.getFov() / 2)) * pixelSize * fovLinesLength),
                (int) (player.getY()
                        * pixelSize + Math.sin(Math.toRadians(player.getAngle() + config.getFov() / 2)) * pixelSize * fovLinesLength));
    }

    private void drawMetricOverlay(Graphics2D g2d) {
        // draw frametime
        g2d.drawString(String.valueOf(
                frameTimes.getLast()) + " ms", // frametime in ms
                screenWidth + screenWidthExtension/2,
                pixelSize*22);

        // draw frames per second
        if (frameTimes.getLast() != 0) {
            g2d.drawString(String.valueOf(
                    1000/(frameTimes.getLast())) + " FPS", // frametime in ms
                    1,
                    10
            );
        }

        // draw player info
        g2d.drawString(String.format( "%.2f ° angle",
                player.getAngle()), // frametime in ms
                20,
                pixelSize*20);
        g2d.drawString(String.format("X: %.2f Y: %.2f",
                player.getX(), player.getY()), // frametime in ms
                20,
                pixelSize*21);

        // draw frametime graph
        g2d.setColor(Color.white);
        for (int frame=1;frame<frameTimes.size();frame++) {
            g2d.drawLine(screenWidth+frame-1, pixelSize*20+ Math.toIntExact(frameTimes.get(frame-1)/5), screenWidth+frame, pixelSize*20+ Math.toIntExact(frameTimes.get(frame)/5));
        }
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
        frameTimes.add(100);
        JPanel jpanel = new JPanel();
        frame.add(jpanel);
    }
}
