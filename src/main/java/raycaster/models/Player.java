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
package raycaster.models;

/**
 *
 * @author Pavel Vavruska
 */
public class Player {

    private double x;
    private double y;
    private double angle;
    private double velocityX = 0D;
    private double velocityY = 0D;
    private double velocityAngle = 0D;

    private void checkMapCollision(Map map) {
        // forward - backward

        //collison
        int checkX = (int) (this.getX()+this.getVelocityX());
        int checkY = (int) (this.getY()+this.getVelocityY());

        if (this.getAngle() > 90 && this.getAngle() < 270) {
            checkX = (int) Math.ceil(this.getX()+this.getVelocityX() - 1);
        }

        if (this.getAngle() > 180 && this.getAngle() < 360) {
            checkY = (int) Math.ceil(this.getY()+this.getVelocityY() - 1);
        }

        if (checkX >= 1 && checkY >= 1 && checkX < map.getSizeX() -1  && checkY < map.getSizeY()-1) {
            int checkBothX = checkX + (checkX - (int) this.getX());
            int checkBothY = checkY + (checkY - (int) this.getY());
            boolean collisionOnCorX = (map.getMap()[(int) this.getY()][checkBothX] >= 10
            ) || (map.getMap()[(int) this.getY() - 1][checkBothX] >= 10
            ) || (map.getMap()[(int) this.getY() + 1][checkBothX] >= 10);
            boolean collisionOnCorY = (map.getMap()[checkBothY][(int) this.getX()] >= 10
            ) || (map.getMap()[checkBothY][(int) this.getX() - 1] >= 10
            ) || (map.getMap()[checkBothY][(int) this.getX() + 1] >= 10);

            this.setX(this.getX()+this.getVelocityX());
            this.setY(this.getY()+this.getVelocityY());

            if (collisionOnCorX) {
                this.setX(this.getX()-this.getVelocityX());
                this.setVelocityX(0);
                if (!collisionOnCorY) {
                    this.setVelocityY(this.getVelocityY()*0.9);
                }
            } else {
                this.setVelocityX(this.getVelocityX()*0.9);
            }
            if (collisionOnCorY) {
                this.setY(this.getY()-this.getVelocityY());
                this.setVelocityY(0);
                if (!collisionOnCorX) {
                    this.setVelocityX(this.getVelocityX()*0.9);
                }
            } else {
                this.setVelocityY(this.getVelocityY()*0.9);
            }
        }
    }

    private void processViewAngle() {
        // turning left or right
        this.setAngle(this.getAngle()+this.getVelocityAngle());
        this.setVelocityAngle(this.getVelocityAngle()*0.9);

        // keeping turning angle in range of 0-360
        if (this.getAngle() >= 360D) {
            this.setAngle(this.getAngle() - 360D);
        }
        if (this.getAngle() < 0D) {
            this.setAngle(this.getAngle() + 360D);
        }
    }

    public Player(double x, double y, double angle) {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    public void tick(Map map) {
        checkMapCollision(map);
        processViewAngle();
    }

    public double getVelocityX() {
        return velocityX;
    }

    public void setVelocityX(double velocityX) {
        this.velocityX = velocityX;
    }

    public double getVelocityY() {
        return velocityY;
    }

    public void setVelocityY(double velocityY) {
        this.velocityY = velocityY;
    }

    public double getVelocityAngle() {
        return velocityAngle;
    }

    public void setVelocityAngle(double velocityAngle) {
        this.velocityAngle = velocityAngle;
    }

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }

    public double getAngle() {
        return angle;
    }

    public void setAngle(double angle) {
        this.angle = angle;
    }
}
