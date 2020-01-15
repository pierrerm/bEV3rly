package ca.mcgill.ecse211.finalProject;

import static ca.mcgill.ecse211.finalProject.Resources.*;

/**
 * This class is used to compute the location of suitable launch position on the demo floor, guide
 * the robot to the nearest available points and launch the ping pong balls at the appropriate angle.
 */
public class Launcher {
  // The array containing the available launch positions
  public static double launchPoints[][] = new double[15][5];
  
  /**
   * Computes and travels to the different launch positions until a suitable one is reached
   * and all ping pong balls have been launched.
   */
  public static void findLaunchPoint() {
    // Get the portion of the trigonometric circle that we should search
    double piArea[] = findPiArea();
    // Get the suitable launch positions in the above trigonometric circle area
    launchPoints = getLaunchPoints(piArea);
    // Start searching for obstacles
    doubleUsPoller.setAvoidanceActivated(true);
    // Travel to all launch points until an accessible one (no obstacle preventing access) is reached
    while ((launchPointsCovered < launchPoints.length) && launchPoints[launchPointsCovered][0] != 0) {
      // Flag to track if obstacle has been encountered
      boolean obstacleEncountered = false;
      // Travel to the next launch point
      // If the launch point is at an intersection, go straight to it, otherwise go to nearest
      // intersection, then go to point.
      if(launchPoints[launchPointsCovered][3] == 0.0) {
        // Travel to the launch point coordinates
        Navigation.travelToCorrection(launchPoints[launchPointsCovered][0] * TILE_SIZE, 
            launchPoints[launchPointsCovered][1] * TILE_SIZE, Navigation.getForceDirection());
        if(!doubleUsPoller.getAvoiding()) {
          // Turn to launch in direction of the bin
          Navigation.turnBy(launchPoints[launchPointsCovered][2] - odometer.getTheta());
        }
      } else {
        // Travel to the intersection closest to the launch position
        Navigation.travelToCorrection(launchPoints[launchPointsCovered][2] * TILE_SIZE, 
            launchPoints[launchPointsCovered][3] * TILE_SIZE, Navigation.getForceDirection());
        if(!doubleUsPoller.getAvoiding()) {
          // Travel to the launch point coordinates
          Navigation.travelTo(launchPoints[launchPointsCovered][0] * TILE_SIZE, 
              launchPoints[launchPointsCovered][1] * TILE_SIZE);
          // Turn to launch in direction of the bin
          Navigation.turnBy(launchPoints[launchPointsCovered][4] - odometer.getTheta());
        }
      }
      // Signal that obstacle has been encountered
      obstacleEncountered = doubleUsPoller.getAvoiding();
      // Wait while robot navigates around obstacle
      while (doubleUsPoller.getAvoiding()) {
        System.out.println("hi");
      }
      // Exit loop if no obstacle has been encountered
      if (!obstacleEncountered) {
        doubleUsPoller.setAvoidanceActivated(false);
        break;
      }
      // Increment launch points counter
      launchPointsCovered++;
    }
  }
  
  /**
   * Computes the area of the trigonometric circle that should be searched to find
   * suitable launch positions depending on bin and island coordinates
   * 
   * @return the array containing the min angle in first position and max angle in second position
   */
  public static double[] findPiArea() {
    // Build flag string representing the position of the bin relative to the island
    String binPos = ((ourBin.x < island.ll.x) ? "0" : ((ourBin.x < island.ur.x) ? "1" : "2")) +
        ((ourBin.y < island.ll.y) ? "0" : ((ourBin.y < island.ur.y) ? "1" : "2"));
    // Initialize the angle array, min is angle[0] and max is angle[1]
    double angle[] = {0,0};
    // Allocate the appropriate min and max angles based on the binPos flag String
    switch (binPos.charAt(0)) {
      case '0':
        if (binPos.charAt(1) == '0') {
          angle[0] = 0;
          angle[1] = Math.PI/2;
        } else if (binPos.charAt(1) == '1') {
          angle[0] = 0;
          angle[1] = Math.PI;
        } else {
          angle[0] = Math.PI/2;
          angle[1] = Math.PI;
        }
        break;
      case '1':
        if (binPos.charAt(1) == '0') {
          angle[0] = -Math.PI/2;
          angle[1] = Math.PI/2;
        } else if (binPos.charAt(1) == '1') {
          angle[0] = 0;
          angle[1] = 2 * Math.PI;
        } else {
          angle[0] = Math.PI/2;
          angle[1] = 3 * Math.PI/2;
        }
        break;
      case '2':
        if (binPos.charAt(1) == '0') {
          angle[0] = -Math.PI/2;
          angle[1] = 0;
        } else if (binPos.charAt(1) == '1') {
          angle[0] = Math.PI;
          angle[1] = 2 * Math.PI;
        } else {
          angle[0] = Math.PI;
          angle[1] = 3*Math.PI/2;
        }
        break;
    }
    // return the angle array with appropriate min and max angles
    return angle;
  }
  
  /**
   * Computes the coordinates and launch angle of the possible launch points in the specified
   * portion of the trigonometric circle, within the island boundaries.
   * 
   * @param piArea, the array containing the min and max angles of the trig circle portion
   * @return the array containing the launch points, along with launch parameters
   */
  public static double[][] getLaunchPoints(double piArea[]) {
    // The array containing the launch points and launch parameters
    double[][] launchPoints = new double[15][5];
    int counter = 0;
    // Navigate through the specified portion of the trig circle, with PI/16 increments
    for (double angle = piArea[0]; angle <= piArea[1]; angle += Math.PI/16) {
      // Get x and y coordinates of projected launch point for current angle
      double xPos = ourBin.x + launchDistance * Math.sin(angle);
      double yPos = ourBin.y + launchDistance * Math.cos(angle);
      // Check that the coordinates are inside of the island
      if (island.ll.x + 0.8 < xPos && island.ur.x - 0.8 > xPos && 
          island.ll.y + 0.8 < yPos && island.ur.y - 0.8 > yPos) {
        // If the coordinates aren't at an intersection, find the closest one
        if ((xPos - Math.round(xPos) != 0) || (yPos - Math.round(yPos) != 0))
          // Get returned launch parameters with actual coordinates, closest 
          // intersection coordinates and launch angle
          launchPoints[counter] = getLaunchPositionParameters(xPos, yPos, angle);
        else {
          // Get launch parameters with actual coordinates and launch angle
          launchPoints[counter][0] = xPos;
          launchPoints[counter][1] = yPos;
          launchPoints[counter][2] = Math.toDegrees(angle);          
        }
        // increment counter
        counter++;
      }
    }
    // return the accessible launch points and their parameters
    return launchPoints;
  }
  
  /**
   * Finds the closest intersection (adjacent to the given launch position) to the robot, and
   * the corresponding launch parameters.
   * 
   * @param posX, the x coordinate of the launch position
   * @param posY, the y coordinate of the launch position
   * @param angle, the corresponding launch angle
   * @return the array containing the launch point coordinates, the closest intersection 
   * coordinates and corresponding launch angle
   */
  public static double[] getLaunchPositionParameters(double posX, double posY, double angle) {
    // The launch parameters array
    double parameters[] = new double[5];
    // The array holding all intersections adjacent to the launch point
    double intersections[][] = new double[4][2];
    // Populate the intersections array with all adjacent intersections
    intersections[0][0] = Math.floor(posX);
    intersections[0][1] = Math.floor(posY);
    intersections[1][0] = Math.ceil(posX);
    intersections[1][1] = Math.floor(posY);
    intersections[2][0] = Math.floor(posX);
    intersections[2][1] = Math.ceil(posY);
    intersections[3][0] = Math.ceil(posX);
    intersections[3][1] = Math.ceil(posY);
    int i = 0;
    // The index of the closest intersection to the robot
    int minIndex = 0;
    // The distance to the robot of the the closest intersection to the robot
    double minDist = Math.hypot(intersections[0][0] * TILE_SIZE - odometer.getX(), 
        intersections[0][1] * TILE_SIZE - odometer.getY());
    // Cycle through all intersections and find the closest
    for (i = 1; i < intersections.length; i++) {
      double xDiff = intersections[i][0] * TILE_SIZE - odometer.getX();
      double yDiff = intersections[i][1] * TILE_SIZE - odometer.getY();
      double distanceToIntersection = Math.hypot(xDiff, yDiff);
      if (distanceToIntersection < minDist) {
        minIndex = i;
        minDist = distanceToIntersection;
      }
    }
    // Populate and return the parameters array with the appropriate launch parameters
    parameters[0] = posX;
    parameters[1] = posY;
    parameters[2] = intersections[minIndex][0];
    parameters[3] = intersections[minIndex][1];
    parameters[4] = Math.toDegrees(angle);
    return parameters;
  }
  
  /**
   * Launches the amount of ping pong balls specified in the NUM_BALLS field in resources.
   */
  public static void launchBalls() {
    launchMotorLeft.setAcceleration(200);
    launchMotorRight.setAcceleration(200);
    launchMotorLeft.setSpeed(50);
    launchMotorRight.setSpeed(50);
    launchMotorLeft.rotate(-NUM_BALLS*360, true);
    launchMotorRight.rotate(-NUM_BALLS*360, false);
  }
  
  /**
   * Gets the x or y coordinate of the specified launch point, depending on the robot heading
   * 
   * @param index, the index of the launch point to be accessed
   * @param xAxis, the boolean flag signaling whether or not the robot is on the xAxis
   * @return the corresponding launch point coordinate
   */
  public static double getLaunchParameter(int index, boolean xAxis) {
    if (xAxis) return launchPoints[index][0];
    else return launchPoints[index][1];
  }
}