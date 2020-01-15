package ca.mcgill.ecse211.finalProject;

import static ca.mcgill.ecse211.finalProject.Resources.*;

/**
 * This class is used to localize the robot on the demo floor using the ultrasonic sensor.
 * It updates theta with an estimate ( 5 degrees precision ) of the robots heading.
 */
public class UltrasonicLocalizer {
  
  // Detection variables
  private static int d = DETECTION_THRESHOLD;
  private static int k = NOISE_THRESHOLD;
  // Heading variables
  private static double theta1 = 0;
  private static double theta2 = 0;
  private static double tempTheta1 = 0;
  private static double tempTheta2 = 0;
  private static double dTheta = 0;

  /**
   * Performs falling edge localization with 2 sensors: takes the average angle of detection
   * of the headings when the falling edge threshold is reached (wall detected)
   */
  public static void fallingEdge2Sensors() {
    // Intialize heading
    odometer.setTheta(0);
    // Set acceleration and rotation speed of wheel motors (with correction for
    // left motor inefficiency)
    leftMotor.setAcceleration(ACCELERATION);
    rightMotor.setAcceleration(ACCELERATION);
    leftMotor.setSpeed(ROTATE_SPEED_L);
    rightMotor.setSpeed(ROTATE_SPEED_R);
    
    Main.sleepFor(30);
    leftMotor.backward();
    rightMotor.forward();
    Main.sleepFor(30);
    
    // already facing wall
    while ((usController.readUSDistanceLeft() < d + k) || (usController.readUSDistanceRight() < d + k));

    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Turn right
    Main.sleepFor(30);
    leftMotor.forward();
    rightMotor.backward();
    Main.sleepFor(30);

    // Do nothing while no wall is detected
    while ((usController.readUSDistanceRight() > d + k));
    // Store heading when entering noise margin
    tempTheta1 = odometer.getTheta();
    // Do nothing while still in noise margin
    while ((usController.readUSDistanceRight() > d - k));

    // Stop turning when exiting noise margin
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Get average heading for noise margin
    theta1 = (odometer.getTheta() + tempTheta1)/2;

    // Turn left
    Main.sleepFor(30);
    leftMotor.backward();
    rightMotor.forward();
    Main.sleepFor(30);
    
    // Turn left until no longer facing wall
    while ((usController.readUSDistanceLeft() < d + k) || (usController.readUSDistanceRight() < d + k)) ;

    // Turn left
    Main.sleepFor(30);
    leftMotor.backward();
    rightMotor.forward();
    Main.sleepFor(30);

    // Do nothing while no wall is detected
    while ((usController.readUSDistanceLeft() > d + k));
    // Store heading when entering noise margin
    tempTheta2 = odometer.getTheta();
    // Do nothing while still in noise margin
    while ((usController.readUSDistanceLeft() > d - k));

    // Stop turning when exiting noise margin
    leftMotor.stop(true);
    rightMotor.stop(false);
    
    // Get average heading for noise margin
    theta2 = (odometer.getTheta() + tempTheta2)/2;
    //System.out.println("Theta2: " + theta2);

    // Get span between the two falling edges
    dTheta = theta2 - theta1;

    // Ensure we have minimal angle
    while (dTheta > 360 || dTheta < 0) {
      if (dTheta > 360) {
        dTheta -= 360;
      } else {
        dTheta += 360;
      }
    }
    
    // Correct Theta component of odometer using half of span between the two falling
    // edges heading angle of the intersection of the two walls)
    odometer.setTheta(ourWallIntersectionOrientation + dTheta/2);
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