package ca.mcgill.ecse211.finalProject;

import static ca.mcgill.ecse211.finalProject.Resources.*;

/**
 * Controller that controls the robot's movements based on ultrasonic data.
 */
public class UltrasonicController {

  int distance;
  int distanceLeft;
  int distanceRight;
  
  int filterControl;
  int filterControlLeft;
  int filterControlRight;
  
  /**
   * Returns the distance between the US sensor and an obstacle in cm.
   * 
   * @return the distance between the US sensor and an obstacle in cm
   */
  public int readUSDistance() {
    return this.distance;
  }
  
  /**
   * Returns the distance between the Left US sensor and an obstacle in cm.
   * 
   * @return the distance between the Left US sensor and an obstacle in cm
   */
  public int readUSDistanceLeft() {
    return this.distanceLeft;
  }
  
  /**
   * Returns the distance between the Right US sensor and an obstacle in cm.
   * 
   * @return the distance between the Right US sensor and an obstacle in cm
   */
  public int readUSDistanceRight() {
    return this.distanceRight;
  }
  
  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * @param distance distance in cm
   */
  void filter(int distance) {
    if (distance >= 255 && filterControl < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the filter value
      filterControl++;
    } else if (distance >= 255) {
      // Repeated large values, so there is nothing there: leave the distance alone
      this.distance = distance;
    } else {
      // distance went below 255: reset filter and leave distance alone.
      filterControl = 0;
      this.distance = distance;
    }
  }
  
  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * @param distance distance in cm
   */
  void filterLeft(int distance) {
    if (Math.abs(distance - this.distanceLeft) > 20 && filterControlLeft < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the filter value
      filterControlLeft++;
    } else if (Math.abs(distance - this.distanceLeft) > 20 ) {
      // Repeated large values, so there is nothing there: leave the distance alone
      this.distanceLeft = distance;
    } else {
      // distance went below 255: reset filter and leave distance alone.
      filterControlLeft = 0;
      this.distanceLeft = distance;
    }
  }
  
  /**
   * Rudimentary filter - toss out invalid samples corresponding to null signal.
   * @param distance distance in cm
   */
  void filterRight(int distance) {
    if (Math.abs(distance - this.distanceRight) > 20  && filterControlRight < FILTER_OUT) {
      // bad value, do not set the distance var, however do increment the filter value
      filterControlRight++;
    } else if (Math.abs(distance - this.distanceRight) > 20) {
      // Repeated large values, so there is nothing there: leave the distance alone
      this.distanceRight = distance;
    } else {
      // distance went below 255: reset filter and leave distance alone.
      filterControlRight = 0;
      this.distanceRight = distance;
    }
  }
  
}
