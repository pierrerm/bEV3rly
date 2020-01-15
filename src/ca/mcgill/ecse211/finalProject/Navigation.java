package ca.mcgill.ecse211.finalProject;

import static ca.mcgill.ecse211.finalProject.Resources.*;

/**
 * This class is used to drive the robot on the demo floor.
 */
public class Navigation {
  
  // the flag to signal if robot is in movement
  private static boolean navigating;
  // force direction for TravelToCorrection, 0 is none, 1 is Y, 2 is X
  private static int forceDirection = 0;
  
  // the current odometer data
  private static double xPosition;
  private static double yPosition;
  private static double currentTheta;
  
  // set the wheel motors' acceleration statically.
  static {
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
  }
  
  /**
   * Update the current odometer data.
   */
  private static void updateCoordinates() {
    xPosition = odometer.getX();
    yPosition = odometer.getY();
    currentTheta = odometer.getTheta();
  }
  
  /**
   * Navigate to the given X and Y coordinates
   * 
   * @param x, the double to store the desired X coordinate
   * @param y, the double to store the desired Y coordinate
   */
  public static void travelTo(double x, double y) {
    
    // Stop the robot
    leftMotor.stop();
    rightMotor.stop();
    // Update the acceleration of the wheel motors
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
    
    // Update the current odometer data
    updateCoordinates();
    
    // Draw the coordinates of the next waypoint to the LCD display
    LCD.drawString("X: " + x + ", Y: " + y, 0, 3);
    
    // Get the difference in positioning to the next waypoint
    double xDiff = x - xPosition;
    double yDiff = y - yPosition;
     
    // Compute distance between current position and next waypoint
    double distanceToNext = Math.hypot(xDiff, yDiff);
    
    // Compute the angle to the next waypoint
    double thetaToNextPoint = Math.toDegrees(Math.atan2(xDiff, yDiff));
    
    // Compute the difference in heading to the next waypoint
    double turnTheta = thetaToNextPoint - currentTheta;
    
    // Change current heading to heading of next waypoint
    turnBy(turnTheta);   
    
    // Speed up for straight line drives 
    leftMotor.setSpeed((int) ((double)FORWARD_SPEED * LEFT_MOTOR_CORRECTION));
    rightMotor.setSpeed(FORWARD_SPEED);
    
    // Roll forward to next way point
    Main.sleepFor(30);
    leftMotor.rotate(convertDistance(distanceToNext * LEFT_MOTOR_CORRECTION), true);
    rightMotor.rotate(convertDistance(distanceToNext), false);
    Main.sleepFor(30); //Delay.msDelay(10);
    
    leftMotor.stop(true);
    rightMotor.stop(false);
  }
  
  /**
   * Navigate to the given X and Y coordinates by only navigating along the x and y axes.
   * 
   * @param x, the double to store the desired X coordinate
   * @param y, the double to store the desired Y coordinate
   */
  public static void travelToCorrection(double x, double y, int forceDirection) {
    if ((forceDirection == 0 && (ourCorner == 0 || ourCorner == 2)) || forceDirection == 1) {
      travelAxisY(y);
      if(doubleUsPoller.getAvoiding()) return;
      travelAxisX(x);
    } else {
      travelAxisX(x);
      if(doubleUsPoller.getAvoiding()) return;
      travelAxisY(y);
    }
  }
  
  /**
   * Navigate to the given X coordinates by only navigating along the x axis.
   * 
   * @param x, the double to store the desired X coordinate
   */
  public static void travelAxisX(double x) {    
    // Signal that robot is not moving
    navigating = false;
    
    // Stop the robot
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Update the acceleration of the wheel motors
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
    leftMotor.setSpeed(FORWARD_SPEED_L);
    rightMotor.setSpeed(FORWARD_SPEED_R);
    
    int xLines = Math.abs((int)(Math.round((x - odometer.getX()) / TILE_SIZE)));
    boolean direction = true;
    
    if (x > odometer.getX() && xLines > 0) {
      turnBy(90 - odometer.getTheta());
    } else {
      turnBy(270 - odometer.getTheta());
      direction = false;
    }
    
    // Signal that robot is moving
    navigating = true;
    
    // Navigate the number of tiles to destination along the x axis
    for (int i = 0; i < xLines; i++) {
      leftMotor.setSpeed(FORWARD_SPEED_L);
      rightMotor.setSpeed(FORWARD_SPEED_R);
      double initialX = odometer.getX();
      Main.sleepFor(30);
//      leftMotor.rotate(convertDistance(0.8 * TILE_SIZE * LEFT_MOTOR_CORRECTION), true);
//      rightMotor.rotate(convertDistance(0.8 * TILE_SIZE), false);
      leftMotor.forward();
      rightMotor.forward();
      Main.sleepFor(30);
      if (direction) {
        while (odometer.getX() < initialX + 0.8 * TILE_SIZE) {
          if(doubleUsPoller.getAvoiding()) return;
        }
      } else {
        while (odometer.getX() > initialX - 0.8 * TILE_SIZE) {
          if(doubleUsPoller.getAvoiding()) return;
        }
      }
      leftMotor.stop(true);
      rightMotor.stop(false);
      findLine();
      double theta = odometer.getTheta();
      if (theta < 315 && theta > 225) {
        odometer.setX(Math.round(odometer.getX()/TILE_SIZE) * TILE_SIZE);
        odometer.setTheta(270);
      } else {
        odometer.setX(Math.round(odometer.getX()/TILE_SIZE) * TILE_SIZE);
        odometer.setTheta(90);
      }
    }
    // Stop the robot
    leftMotor.stop(true);
    rightMotor.stop(false);
    // Signal that robot is not moving
    navigating = false;
  }
  
  /**
   * Navigate to the given Y coordinates by only navigating along the y axis.
   * 
   * @param y, the double to store the desired Y coordinate
   */
  public static void travelAxisY(double y) {    
    //Signal that robot is not moving
    navigating = false;
    
    // Stop the robot
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Update the acceleration of the wheel motors
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
    leftMotor.setSpeed(FORWARD_SPEED_L);
    rightMotor.setSpeed(FORWARD_SPEED_R);
    
    int yLines = Math.abs((int)(Math.round((y - odometer.getY()) / TILE_SIZE)));
    boolean direction = true;
    
    if (y > odometer.getY() && yLines > 0) {
      turnBy(360 - odometer.getTheta());
    } else {
      turnBy(180 - odometer.getTheta());
      direction = false;
    }
    
    // Signal that robot is moving
    navigating = true;
    
    // Navigate the number of tiles to destination along the x axis
    for (int i = 0; i < yLines; i++) {
      leftMotor.setSpeed(FORWARD_SPEED_L);
      rightMotor.setSpeed(FORWARD_SPEED_R);
      double initialY = odometer.getY();
      Main.sleepFor(30);
      //leftMotor.rotate(convertDistance(0.8 * TILE_SIZE * LEFT_MOTOR_CORRECTION), true);
      //rightMotor.rotate(convertDistance(0.8 * TILE_SIZE), false);
      leftMotor.forward();
      rightMotor.forward();
      Main.sleepFor(30);
      if (direction) {
        while (odometer.getY() < initialY + 0.8 * TILE_SIZE) {
          if(doubleUsPoller.getAvoiding()) return;
        }
      } else {
        while (odometer.getY() > initialY - 0.8 * TILE_SIZE) {
          if(doubleUsPoller.getAvoiding()) return;
        }
      }
      leftMotor.stop(true);
      rightMotor.stop(false);
      findLine();
      double theta = odometer.getTheta();
      if (theta < 225 && theta > 135) {
        odometer.setY(Math.round(odometer.getY()/TILE_SIZE) * TILE_SIZE);
        odometer.setTheta(180);
      } else {
        odometer.setY(Math.round(odometer.getY()/TILE_SIZE) * TILE_SIZE);
        odometer.setTheta(0);
      }
    }
    
    // Stop the robot
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Signal that robot is not moving
    navigating = false;
  }
  
  /**
   * Navigate to the the tunnel entry, traverse the tunnel then localize
   */
  public static void findTunnelEntry(){
    switch(ourCorner) {
      case 0:
        if (island.ll.y >= ourZone.ur.y) {
          Navigation.travelToCorrection(ourTunnel.ll.x * TILE_SIZE,
              (ourTunnel.ll.y - 1) * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ll.x * TILE_SIZE + TILE_SIZE/2,
              ourTunnel.ll.y * TILE_SIZE - TILE_SIZE/2);
          Navigation.turnBy(360 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        } else {
          Navigation.travelToCorrection((ourTunnel.ll.x - 1) * TILE_SIZE,
              ourTunnel.ll.y * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ll.x * TILE_SIZE - TILE_SIZE/2,
              ourTunnel.ll.y * TILE_SIZE + TILE_SIZE/2);
          Navigation.turnBy(90 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        }
        break;
      case 1:
        if (island.ll.y >= ourZone.ur.y) {
          Navigation.travelToCorrection((ourTunnel.ll.x + 1) * TILE_SIZE,
              (ourTunnel.ll.y - 1) * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ll.x * TILE_SIZE + TILE_SIZE/2, 
              ourTunnel.ll.y * TILE_SIZE - TILE_SIZE/2);
          Navigation.turnBy(360 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        } else {
          Navigation.travelToCorrection((ourTunnel.ur.x + 1) * TILE_SIZE,
              (ourTunnel.ur.y - 1) * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE + TILE_SIZE/2,
              ourTunnel.ur.y * TILE_SIZE - TILE_SIZE/2);
          Navigation.turnBy(270 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        }
        break;
      case 2:
        if (island.ur.y <= ourZone.ll.y) {
          Navigation.travelToCorrection(ourTunnel.ur.x * TILE_SIZE,
              (ourTunnel.ur.y + 1) * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE - TILE_SIZE/2,
              ourTunnel.ur.y * TILE_SIZE + TILE_SIZE/2);
          Navigation.turnBy(180 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        } else {
          Navigation.travelToCorrection((ourTunnel.ur.x + 1) * TILE_SIZE,
              ourTunnel.ur.y * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE + TILE_SIZE/2,
              ourTunnel.ur.y * TILE_SIZE - TILE_SIZE/2);
          Navigation.turnBy(270 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        }
        break;
      case 3:
        if (island.ur.y <= ourZone.ll.y) {
          Navigation.travelToCorrection((ourTunnel.ur.x - 1) * TILE_SIZE, 
              (ourTunnel.ur.y + 1) * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE - TILE_SIZE/2, 
              ourTunnel.ur.y * TILE_SIZE + TILE_SIZE/2);
          Navigation.turnBy(180 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        } else {
          Navigation.travelToCorrection((ourTunnel.ll.x - 1) * TILE_SIZE, 
              (ourTunnel.ll.y + 1) * TILE_SIZE, 0);
          Navigation.travelTo(ourTunnel.ll.x * TILE_SIZE - TILE_SIZE/2, 
              ourTunnel.ll.y * TILE_SIZE + TILE_SIZE/2);
          Navigation.turnBy(90 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        }
        break;
    }
  }
  
  /**
   * Ensure that robot is centered before entering tunnel
   */
  public static void preTunnelCheck() {
    Navigation.turnBy(-90);
    findLine();
    Main.sleepFor(30);
    leftMotor.rotate(-convertDistance(TILE_SIZE/2 * LEFT_MOTOR_CORRECTION), true);
    rightMotor.rotate(-convertDistance(TILE_SIZE/2), false);
    Main.sleepFor(30);
    leftMotor.stop(true);
    rightMotor.stop(false);
    Navigation.turnBy(90);
  }
  
  /**
   * Traverse the tunnel for the specified tunnel length
   * 
   * @param distance the distance to be travelled to successfully traverse the tunnel
   */
  public static void travelThroughTunnel (double distance) {
    //Navigation.travelTo(x, y);
    leftMotor.setSpeed(FORWARD_SPEED_L);
    rightMotor.setSpeed(FORWARD_SPEED_R);
    Main.sleepFor(30);
    leftMotor.rotate(convertDistance((int)(distance * LEFT_MOTOR_CORRECTION)), true);
    rightMotor.rotate(convertDistance(distance), false);
    Main.sleepFor(30);
    findLine();
    boolean xAxis = false;
    double theta = odometer.getTheta();
    if (theta < 315 && theta > 225) {
      odometer.setX(Math.round(odometer.getX()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(270);
      xAxis = true;
    } else if (theta < 135 && theta > 45) {
      odometer.setX(Math.round(odometer.getX()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(90);
      xAxis = true;
    } else if (theta < 225 && theta > 135) {// travelling on y axis
      odometer.setY(Math.round(odometer.getY()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(180);
    } else  {// travelling on y axis
      odometer.setY(Math.round(odometer.getY()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(0);
    }
    int direction = 0;
    if (xAxis) {
      if (odometer.getY() - island.ur.y > odometer.getY() - island.ll.y) {
        Navigation.turnBy(360 - odometer.getTheta());
      } else {
        Navigation.turnBy(180 - odometer.getTheta());
        direction = 180;
      }
    } else {
      if (odometer.getX() - island.ll.x > odometer.getX() - island.ur.x) {
        Navigation.turnBy(270 - odometer.getTheta());
        direction = 270;
      } else {
        Navigation.turnBy(90 - odometer.getTheta());
        direction = 90;
      }
    }
    findLine();
    if (direction == 270) {
      odometer.setX(Math.round(odometer.getX()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(270);
      xAxis = true;
    } else if (direction == 90){
      odometer.setX(Math.round(odometer.getX()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(90);
      xAxis = true;
    } else if (direction == 180) {// travelling on y axis
      odometer.setY(Math.round(odometer.getY()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(180);
    } else  { // travelling on y axis
      odometer.setY(Math.round(odometer.getY()/TILE_SIZE) * TILE_SIZE);
      odometer.setTheta(0);
    }
  }

  /**
   * Change the current heading according to specified heading change
   * 
   * @param theta, the angle in degrees that the robot should change its heading from
   */
  public static void turnBy(double theta) {

    leftMotor.stop(true);
    rightMotor.stop(false);
    leftMotor.setSpeed(ROTATE_SPEED_L);
    rightMotor.setSpeed(ROTATE_SPEED_R);
    
    // Make sure robot makes minimal turn
    if (theta <= -180 && theta >= -359) {
      theta += 360;
    } else if (theta >= 180 && theta <= 359) {
      theta -= 360;
    } else if (theta >= 360 || theta <= -360) {
      theta = theta % 360;
    }
    
   // Get corrected theta with turning correction
    double correctedTheta = theta * TURNING_CORRECTION;
    
    // Slow down for turn
    leftMotor.setSpeed((int) ((double)ROTATE_SPEED * LEFT_MOTOR_CORRECTION));
    rightMotor.setSpeed(ROTATE_SPEED);

    // Signal that the robot is moving
    navigating = true;
    
    // Update the turning correction
    Odometer.setTurningCorrection(TURNING_CORRECTION);
    
    // Rotate to new angle
    Main.sleepFor(30);
    leftMotor.rotate(convertAngle(correctedTheta * LEFT_MOTOR_CORRECTION), true);
    rightMotor.rotate(-convertAngle(correctedTheta), false);
    Main.sleepFor(30);
    
    Odometer.setTurningCorrection(1);
    
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Signal that the robot is not moving
    navigating = false;
  }
  
  /**
   * Signal whether robot is currently mvoing or not
   * 
   * @return navigating, the boolean signaling if the robot moves
   */
  public static boolean isNavigating() {
    return navigating;
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
  
  /**
   * Avoid the obstacle ahead
   * 
   * @param detectLeft the flag signaling if the obstacle is detected by the left sensor
   * @param detectRight the flag signaling if the obstacle is detected by the right sensor
   */
  public static void avoidObstacle(boolean xAxis) {
    // Get line detection threshold
    int threshold = Main.getLightSensorThreshold();
    if(xAxis) forceDirection = 1;
    else forceDirection = 2;
    // Stop wheels simultaneously
    leftMotor.stop(true);
    rightMotor.stop(false);
    leftMotor.setSpeed(ROTATE_SPEED_L);
    rightMotor.setSpeed(ROTATE_SPEED_R);
    Main.sleepFor(30);
    leftMotor.backward();
    rightMotor.backward();
    Main.sleepFor(30);
    while (leftLightPoller.getRedValue() > threshold && rightLightPoller.getRedValue() > threshold) {
    }
    leftMotor.stop(true);
    rightMotor.stop(true);
    if (leftLightPoller.getRedValue() > threshold) { 
      while (leftLightPoller.getRedValue() > threshold) {
        rightMotor.setSpeed((int)(0.33 * ROTATE_SPEED_R));
        Main.sleepFor(30);
        rightMotor.forward();
        leftMotor.backward();
        Main.sleepFor(30);
      }
    } else if (rightLightPoller.getRedValue() > threshold) {
      while (rightLightPoller.getRedValue() > threshold) {
        leftMotor.setSpeed((int)(0.33 * ROTATE_SPEED_L));
        Main.sleepFor(30);
        leftMotor.forward();
        rightMotor.backward();
        Main.sleepFor(30);
      }
    }
    leftMotor.setSpeed(ROTATE_SPEED_L);
    rightMotor.setSpeed(ROTATE_SPEED_R);
    Main.sleepFor(30);
    leftMotor.rotate(-convertDistance(LIGHT_SENSOR_OFFSET * LEFT_MOTOR_CORRECTION), true);
    rightMotor.rotate(-convertDistance(LIGHT_SENSOR_OFFSET), false);
    Main.sleepFor(30);
    leftMotor.stop(true);
    rightMotor.stop(false);
  }
  
  /**
   * Find the closest line ahead and align wheels on it
   */
  public static void findLine() {
    // Get line detection threshold
    int threshold = Main.getLightSensorThreshold();
    
    leftMotor.setSpeed(ROTATE_SPEED_L);
    rightMotor.setSpeed(ROTATE_SPEED_R);
    // added
    Main.sleepFor(30);
    leftMotor.forward();
    rightMotor.forward();
    Main.sleepFor(30);
    while (leftLightPoller.getRedValue() > threshold && rightLightPoller.getRedValue() > threshold) {
      if(doubleUsPoller.getAvoiding()) return;
    }
    leftMotor.stop(true);
    rightMotor.stop(true);
    if (leftLightPoller.getRedValue() > threshold) { 
      while (leftLightPoller.getRedValue() > threshold) {
        rightMotor.setSpeed((int)(0.33 * ROTATE_SPEED_R));
        Main.sleepFor(30);
        rightMotor.backward();
        leftMotor.forward();
        Main.sleepFor(30);
      }
    } else if (rightLightPoller.getRedValue() > threshold) {
      while (rightLightPoller.getRedValue() > threshold) {
        leftMotor.setSpeed((int)(0.33 * ROTATE_SPEED_L));
        Main.sleepFor(30);
        leftMotor.backward();
        rightMotor.forward();
        Main.sleepFor(30);
      }
    }
    leftMotor.setSpeed(ROTATE_SPEED_L);
    rightMotor.setSpeed(ROTATE_SPEED_R);
    Main.sleepFor(30);
    leftMotor.rotate(convertDistance(LIGHT_SENSOR_OFFSET * LEFT_MOTOR_CORRECTION), true);
    rightMotor.rotate(convertDistance(LIGHT_SENSOR_OFFSET), false);
    Main.sleepFor(30);
    leftMotor.stop(true);
    rightMotor.stop(false);
  }
  
  /**
   * Get the current force direction value
   * 
   * @return forceDirection the flag signaling if axis x or y should be prioritized
   */
  public static int getForceDirection() {
    return forceDirection;
  }
  
  public static void findStartingPoint() {
    if(Launcher.launchPoints[launchPointsCovered][3] == 0.0) {
      Navigation.turnBy(-(Launcher.launchPoints[launchPointsCovered][2] - odometer.getTheta()));
    } else {
      Navigation.turnBy(-(Launcher.launchPoints[launchPointsCovered][4] - odometer.getTheta()));
       Navigation.travelTo(Launcher.launchPoints[launchPointsCovered][2] * TILE_SIZE, 
            Launcher.launchPoints[launchPointsCovered][3] * TILE_SIZE);
      }
    int counterDirection;
    if (forceDirection == 1) counterDirection = 2;
    else if (forceDirection == 2) counterDirection = 1;
    else {
      if (ourCorner == 2 || ourCorner == 0) counterDirection = 2;
      else counterDirection = 1;
    }
    switch(ourCorner) {
      case 0:
        if (island.ll.y >= ourZone.ur.y) {
          Navigation.travelToCorrection(ourTunnel.ur.x * TILE_SIZE,
              (ourTunnel.ur.y + 1) * TILE_SIZE, counterDirection);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE - TILE_SIZE/2,
              ourTunnel.ur.y * TILE_SIZE + TILE_SIZE/2);
          Navigation.turnBy(180 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        } else {
          Navigation.travelToCorrection((ourTunnel.ur.x + 1) * TILE_SIZE,
              ourTunnel.ur.y * TILE_SIZE, counterDirection);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE + TILE_SIZE/2,
              ourTunnel.ur.y * TILE_SIZE - TILE_SIZE/2);
          Navigation.turnBy(270 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        }
        Navigation.travelToCorrection(TILE_SIZE, TILE_SIZE, 0);
        break;
      case 1:
        if (island.ll.y >= ourZone.ur.y) {
          Navigation.travelToCorrection((ourTunnel.ur.x - 1) * TILE_SIZE, 
              (ourTunnel.ur.y + 1) * TILE_SIZE, counterDirection);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE - TILE_SIZE/2, 
              ourTunnel.ur.y * TILE_SIZE + TILE_SIZE/2);
          Navigation.turnBy(180 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        } else {
          Navigation.travelToCorrection((ourTunnel.ll.x - 1) * TILE_SIZE, 
              (ourTunnel.ll.y + 1) * TILE_SIZE, counterDirection);
          Navigation.travelTo(ourTunnel.ll.x * TILE_SIZE - TILE_SIZE/2, 
              ourTunnel.ll.y * TILE_SIZE + TILE_SIZE/2);
          Navigation.turnBy(90 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        }
        Navigation.travelToCorrection(14 * TILE_SIZE, TILE_SIZE, 0);
        break;
      case 2:
        if (island.ur.y <= ourZone.ll.y) {
          if (island.ur.y <= ourZone.ll.y) {
            Navigation.travelToCorrection(ourTunnel.ur.x * TILE_SIZE,
                (ourTunnel.ur.y + 1) * TILE_SIZE, counterDirection);
            Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE - TILE_SIZE/2,
                ourTunnel.ur.y * TILE_SIZE + TILE_SIZE/2);
            Navigation.turnBy(180 - odometer.getTheta());
            Navigation.preTunnelCheck();
            double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
            Navigation.travelThroughTunnel(tunnelDistance);
          } else {
            Navigation.travelToCorrection((ourTunnel.ur.x + 1) * TILE_SIZE,
                ourTunnel.ur.y * TILE_SIZE, counterDirection);
            Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE + TILE_SIZE/2,
                ourTunnel.ur.y * TILE_SIZE - TILE_SIZE/2);
            Navigation.turnBy(270 - odometer.getTheta());
            Navigation.preTunnelCheck();
            double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
            Navigation.travelThroughTunnel(tunnelDistance);
          }
        }
        Navigation.travelToCorrection(14 * TILE_SIZE, 8 * TILE_SIZE, 0);
        break;
      case 3:
        if (island.ur.y <= ourZone.ll.y) {
          Navigation.travelToCorrection((ourTunnel.ll.x + 1) * TILE_SIZE,
              (ourTunnel.ll.y - 1) * TILE_SIZE, counterDirection);
          Navigation.travelTo(ourTunnel.ll.x * TILE_SIZE + TILE_SIZE/2, 
              ourTunnel.ll.y * TILE_SIZE - TILE_SIZE/2);
          Navigation.turnBy(360 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.y - ourTunnel.ll.y) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        } else {
          Navigation.travelToCorrection((ourTunnel.ur.x + 1) * TILE_SIZE,
              (ourTunnel.ur.y - 1) * TILE_SIZE, counterDirection);
          Navigation.travelTo(ourTunnel.ur.x * TILE_SIZE + TILE_SIZE/2,
              ourTunnel.ur.y * TILE_SIZE - TILE_SIZE/2);
          Navigation.turnBy(270 - odometer.getTheta());
          Navigation.preTunnelCheck();
          double tunnelDistance = (1 + ourTunnel.ur.x - ourTunnel.ll.x) * TILE_SIZE;
          Navigation.travelThroughTunnel(tunnelDistance);
        }
        Navigation.travelToCorrection(TILE_SIZE, 8 * TILE_SIZE, 0);
        break;
    }
  }
}