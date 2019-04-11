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
public class Map {

    private int sizeX;
    private int sizeY;
    private int[][] map;

    public Map() {
        this.map = new int[][]{
            {10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14, 14, 14, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, 04, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, 02, 12, 14, 14, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, 10, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, 12, 14, 04, -1, -1, 14, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, 00, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, 11, 11, 11, -1, -1, -1, 12, 14, 02, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, 02, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, 00, 10, 12, -1, -1, -1, -1, -1, -1, 12, 14, 14, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, 00, 10, 12, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, -1, -1, -1, -1, 10, 12, -1, -1, -1, -1, -1, -1, 12, 14, -1, -1, -1, -1, 14},
            {10, 10, 10, 10, 10, 10, 12, 12, 12, 12, 12, 12, 12, 12, 14, 14, 14, 14, 14, 14}};

        this.sizeX = map.length;
        this.sizeY = map[0].length;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public int[][] getMap() {
        return map;
    }
}
