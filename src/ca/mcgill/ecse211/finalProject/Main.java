package ca.mcgill.ecse211.finalProject;

import static ca.mcgill.ecse211.finalProject.Resources.*;
import lejos.hardware.Sound;

/**
 * The main driver class for the localization lab.
 */
public class Main {
  // The value of the initial light sensor threshold reading
  static int lightSensorThreshold;

  /**
   * The main entry point. Navigates through the demo floor, localizing, passing through
   * the tunnel, finding launch points, navigating to an accessible launch point while
   * avoiding obstacles, launching the ping pong balls into the bin and returning to its
   * starting point.
   * 
   * @param args
   */
  public static void main(String[] args) {
    
    // Receive and update game parameters through wi-fi class
    updateTeamParameters();
    
    // Get an initial reading of the light intensity threshold in the demo room
    lightSensorThreshold = (int)((leftLightPoller.getRedValue() + rightLightPoller.getRedValue())/2 * RED_LIGHT_THRESHOLD);
    
    // Start the odometer and ultrasonic poller threads
    new Thread(odometer).start();
    new Thread(doubleUsPoller).start();
    
    // Start falling edge with 2 US sensors
    UltrasonicLocalizer.fallingEdge2Sensors();
    
    // Compute the turn angle needed to face 0 degrees
    double turnAngle = 360 - 90 * ourCorner - odometer.getTheta();
    
    // Turn to face 0 degrees
    Navigation.turnBy(turnAngle);
    
    // Deactivate the ultrasonic poller (no longer needed)
    doubleUsPoller.setActivated(false);
    
    // Localize to grid, find and navigate to (1,1)
    LightLocalization.findOrigin2Sensors();
    
    System.out.println("post-LL theta: " + odometer.getTheta());
    
    // Issue 3 beeps
    for (int i = 0; i < 3; i++) {
      Sound.beep();
      Main.sleepFor(100);
    }
    
    // Find and navigate through the tunnel
    Navigation.findTunnelEntry();
    
    // Activate the ultrasonic poller (needed for obstacle avoidance)
    doubleUsPoller.setActivated(true);
    
    // Find and navigate to a suitable launch point
    Launcher.findLaunchPoint();
    
    // Issue 3 beeps
    for (int i = 0; i < 3; i++) {
      Sound.beep();
      Main.sleepFor(100);
    }
    
    // Launch the ping pong balls
    Launcher.launchBalls();
    
    Navigation.findStartingPoint();
    
    // Issue 5 beeps
    for (int i = 0; i < 5; i++) {
      Sound.beep();
      Main.sleepFor(100);
    }
    
    System.exit(0);
  }
    
  /**
   * Sleeps current thread for the specified duration.
   * 
   * @param duration sleep duration in milliseconds
   */
  public static void sleepFor(long duration) {
    try {
      Thread.sleep(duration);
    } catch (InterruptedException e) {
      // There is nothing to be done here
    }
  }
  
  /**
   * Get the value of the current light sensor threshold
   * 
   * @return lightSensorThreshold, the light sensor threshold
   */
  public static int getLightSensorThreshold() {
    return lightSensorThreshold;
  }
  
}