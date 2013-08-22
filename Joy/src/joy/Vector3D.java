/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package joy;

import javax.vecmath.Vector3d;

/**
 *
 * @author Chantal
 */
public class Vector3D extends Vector3d {

    public Vector3D(double x, double y, double z) {
        super(x, y, z);
    }

    public void rotateX(double angle) {
        rotateX(Math.cos(angle),  Math.sin(angle));
    }

    public void rotateY(double angle) {
        rotateY( Math.cos(angle),  Math.sin(angle));
    }

    public void rotateZ(double angle) {
        rotateZ( Math.cos(angle),  Math.sin(angle));
    }

    public void rotateX(double cosAngle, double sinAngle) {
        double newY = y * cosAngle - z * sinAngle;
        double newZ = y * sinAngle + z * cosAngle;
        y = newY;
        z = newZ;
    }

    public void rotateY(double cosAngle, double sinAngle) {
        double newX = z * sinAngle + x * cosAngle;
        double newZ = z * cosAngle - x * sinAngle;
        x = newX;
        z = newZ;
    }

    public void rotateZ(double cosAngle, double sinAngle) {
        double newX = x * cosAngle - y * sinAngle;
        double newY = x * sinAngle + y * cosAngle;
        x = newX;
        y = newY;
    }
}
