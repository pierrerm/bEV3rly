package ca.mcgill.ecse211.finalProject;

import static ca.mcgill.ecse211.finalProject.Resources.*;

/**
 * This class is used to localize the robot using the light sensor
 * and make it navigate to the origin (1,1)
 */
public class LightLocalization {
  
  /**
   * Corrects the X, Y and theta of the robot with 2 sensors then navigates to (1,1)
   */
  public static void findOrigin2Sensors() {
    
    // Set acceleration and rotation speed of wheel motors (with correction for
    // left motor inefficiency)
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
    leftMotor.setSpeed(ROTATE_SPEED_L);
    rightMotor.setSpeed(ROTATE_SPEED_R);
    
    Navigation.findLine();
    
    switch (ourCorner) {
      case 0:
        odometer.setY(TILE_SIZE);
        odometer.setTheta(0);
        break;
      case 1:
        odometer.setX((X_TILES - 1) * TILE_SIZE);
        odometer.setTheta(270);
        break;
      case 2:
        odometer.setY((Y_TILES - 1) * TILE_SIZE);
        odometer.setTheta(180);
        break;
      case 3:
        odometer.setX(TILE_SIZE);
        odometer.setTheta(90);
        break;
    }
    
    // Make right angle turn to the right
    Navigation.turnBy(90);
    
    Navigation.findLine();
    
    switch (ourCorner) {
      case 0:
        odometer.setX(TILE_SIZE);
        odometer.setTheta(90);
        break;
      case 1:
        odometer.setY(TILE_SIZE);
        odometer.setTheta(0);
        break;
      case 2:
        odometer.setX((X_TILES - 1) * TILE_SIZE);
        odometer.setTheta(270);
        break;
      case 3:
        odometer.setY((Y_TILES - 1) * TILE_SIZE);
        odometer.setTheta(180);
        break;
    }
  }
  
  /**
   * Converts input distance to the total rotation of each wheel needed
   * to cover that distance.
   * 
   * @param distance
   * @return the wheel rotations necessary to cover the distance
   */
  public static int convertDistance(double distance) {
    return (int) ((180.0 * distance) / (Math.PI * WHEEL_RAD));
  }

  /**
   * Converts input angle to the total rotation of each wheel needed
   * to rotate the robot by that angle.
   * 
   * @param angle
   * @return the wheel rotations necessary to rotate the robot by the angle
   */
  public static int convertAngle(double angle) {
    return convertDistance(Math.PI * TRACK * angle / 360.0);
  }
}
