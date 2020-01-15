package ca.mcgill.ecse211.finalProject;

import lejos.hardware.sensor.EV3UltrasonicSensor;
import static ca.mcgill.ecse211.finalProject.Resources.*;

/**
 * Samples the left and right US sensors and invokes the selected controller on each cycle.
 * 
 * Control of the wall follower is applied periodically by the UltrasonicPoller thread. The while
 * loop at the bottom executes in a loop. Assuming that the us.fetchSample, and cont.processUSData
 * methods operate in about 20ms, and that the thread sleeps for 50 ms at the end of each loop, then
 * one cycle through the loop is approximately 70 ms. This corresponds to a sampling rate of 1/70ms
 * or about 14 Hz.
 */
public class UltrasonicPoller2 implements Runnable {
  private float[] usDataLeft;
  private float[] usDataRight;
  private int distanceLeft;
  private int distanceRight;
  private boolean avoiding = false;
  private EV3UltrasonicSensor sensorLeft;
  private EV3UltrasonicSensor sensorRight;
  private boolean avoidanceActivated = false;
  private boolean activated = true;
  
  /**
   * Constructs an UltrasonicPoller2 object.
   */
  public UltrasonicPoller2(EV3UltrasonicSensor usSensorLeft, EV3UltrasonicSensor usSensorRight) {
    usDataLeft = new float[usSensorLeft.sampleSize()];
    this.sensorLeft = usSensorLeft;
    usDataRight = new float[usSensorRight.sampleSize()];
    this.sensorRight = usSensorRight;
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert US result to an integer
   * [0,255] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (true) {
      // Do nothing if not activated
      if(activated) {
        sensorLeft.fetchSample(usDataLeft, 0); // acquire data
        distanceLeft = (int) (usDataLeft[0] * 100.0);// extract from buffer, convert to cm, cast to int
        usController.filterLeft(distanceLeft);
        sensorRight.fetchSample(usDataRight, 0); // acquire data
        distanceRight = (int) (usDataRight[0] * 100.0);// extract from buffer, convert to cm, cast to int
        usController.filterRight(distanceRight);
        // Look for obstacles if avoidance is activated
        if(avoidanceActivated){
          if(distanceRight < DISTANCE_THRESHOLD || distanceLeft < DISTANCE_THRESHOLD) {
            avoiding = true;
            leftMotor.stop(true);
            rightMotor.stop(false);
            // Flag signaling whether robot is travelling on x axis or not
            boolean xAxis = false;
            double theta = odometer.getTheta();
            // Get current position on the axis on which robot is travelling and update xAxis flag
            if ((theta < 315 && theta > 225) || (theta < 135 && theta > 45)) { // travelling on x
              xAxis = true;
            } else { // travelling on y
            }
            Navigation.avoidObstacle(xAxis);
            // Signal that robot is no longer avoiding
            avoiding = false;
          }
          }
        }
      try {
        Thread.sleep(50); //50
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }

  /**
   * Get the current distance measured by the Left Ultrasonic Sensor
   * Accessible from outside of class
   * 
   * @return distanceLeft, the current Left USsensor distance
   */
  public int getDistanceLeft() {
    return this.distanceLeft;
  }
  
  /**
   * Get the current distance measured by the Right Ultrasonic Sensor
   * Accessible from outside of class
   * 
   * @return distanceRight, the current Right USsensor distance
   */
  public int getDistanceRight() {
    return this.distanceRight;
  }
  
  /**
   * Signal whether robot is currently avoiding an obstacle or not
   * 
   * @return avoiding, the obstacle avoidance flag
   */
  public boolean getAvoiding() {
    return this.avoiding;
  }

  /**
   * Update the signal whether robot is currently avoiding an obstacle or not
   * 
   * @param newAvoiding, the new avoiding flag
   */
  public void setAvoiding(boolean newAvoiding) {
    this.avoiding = newAvoiding;
  }
  
  /**
   * Update the signal whether robot is looking for obstacles or not
   * 
   * @param isActivated, the new avoidanceActivated value
   */
  public void setAvoidanceActivated(boolean isActivated) {
    this.avoidanceActivated = isActivated;
  }
  
  /**
   * Update the signal whether the Ultrasonic Poller is activated or not
   * 
   * @param isActivated, the new activated value
   */
  public void setActivated(boolean isActivated) {
    this.activated = isActivated;
  }
}
