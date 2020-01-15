package ca.mcgill.ecse211.finalProject;

import static ca.mcgill.ecse211.finalProject.Resources.*;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * The odometer class keeps track of the robot's (x, y, theta) position.
 * 
 * @author Rodrigo Silva
 * @author Dirk Dubois
 * @author Derek Yu
 * @author Karim El-Baba
 * @author Michael Smith
 * @author Younes Boubekeur
 */
public class Odometer implements Runnable {
  
  /**
   * The x-axis position in cm.
   */
  private volatile double x;
  
  /**
   * The y-axis position in cm.
   */
  private volatile double y; // y-axis position
  
  /**
   * The orientation in degrees.
   */
  private volatile double theta; // Head angle

  // Thread control tools
  /**
   * Fair lock for concurrent writing
   */
  private static Lock lock = new ReentrantLock(true);
  
  /**
   * Indicates if a thread is trying to reset any position parameters
   */
  private volatile boolean isResetting = false;

  /**
   * Lets other threads know that a reset operation is over.
   */
  private Condition doneResetting = lock.newCondition();
  
  /**
   * The correction to turn angles
   */
  private static double turningCorrection = TURNING_CORRECTION;
  
  /**
   * Constructs an Odometer object.
   */
  public Odometer() {
    this.x = TILE_SIZE;
    this.y = TILE_SIZE;
    this.theta = 0.0;
  }

  /**
   * This method is where the logic for the odometer will run.
   */
  public void run() {
    long updateStart, updateEnd;
    
    // variables to store current tachometer readings for each motor
    int nowLeftMotorTachoCount = 0;
    int nowRightMotorTachoCount = 0;
    // variables to store previous tachometer readings for each motor
    int lastLeftMotorTachoCount = 0;
    int lastRightMotorTachoCount = 0;
    // variables to store wheel displacement in centimeters for each motor
    double distanceLeftMotor = 0;
    double distanceRightMotor = 0;
    // variables to store vehicle displacement and orientation
    double deltaDistance = 0;
    double deltaCorrection = 0; 
    // variable to store X and Y components of displacement
    double xCorrection = 0;
    double yCorrection = 0;

    while (true) {
      updateStart = System.currentTimeMillis();
      
      // Get current tachometer counts for each wheel
      nowLeftMotorTachoCount = leftMotor.getTachoCount();
      nowRightMotorTachoCount = rightMotor.getTachoCount();

      // Get wheel displacements in centimeters (with correction for left motor)
      distanceLeftMotor = Math.PI * WHEEL_RAD * (((double)(nowLeftMotorTachoCount 
          - lastLeftMotorTachoCount)) / (LEFT_MOTOR_CORRECTION)) / 180;
      distanceRightMotor = Math.PI * WHEEL_RAD * (((double)(nowRightMotorTachoCount 
          - lastRightMotorTachoCount))) / 180;

      // Save new tacho counts to old tacho counts
      lastLeftMotorTachoCount = nowLeftMotorTachoCount;
      lastRightMotorTachoCount = nowRightMotorTachoCount;

      // Compute vehicle displacement and orientation
      deltaDistance = 0.5 * (distanceLeftMotor + distanceRightMotor);
      deltaCorrection = (distanceLeftMotor - distanceRightMotor) / TRACK;
      deltaCorrection = (deltaCorrection * 180) / Math.PI;
      
      // Compute X and Y displacements
      xCorrection = deltaDistance * Math.sin((this.getXYT()[2] * Math.PI) / 180);
      yCorrection = deltaDistance * Math.cos((this.getXYT()[2] * Math.PI) / 180);

      // Update odometer position components      
      this.update(xCorrection, yCorrection, deltaCorrection);

      // this ensures that the odometer only runs once every period
      updateEnd = System.currentTimeMillis();
      if (updateEnd - updateStart < ODOMETER_PERIOD) {
        try {
          Thread.sleep(ODOMETER_PERIOD - (updateEnd - updateStart));
        } catch (InterruptedException e) {
          // there is nothing to be done
        }
      }
    }
  }
  
  // IT IS NOT NECESSARY TO MODIFY ANYTHING BELOW THIS LINE
  
  /**
   * Returns the Odometer data.
   * <p>
   * Writes the current position and orientation of the robot onto the odoData array.
   * {@code odoData[0] = * x, odoData[1] = y; odoData[2] = theta;}
   * 
   * @param position the array to store the odometer data
   * @return the odometer data.
   */
  public double[] getXYT() {
    double[] position = new double[3];
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }

      position[0] = x;
      position[1] = y;
      position[2] = theta;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return position;
  }
  
  /**
   * Returns the Odometer Theta data.
   * <p>
   * Writes the current Theta of the robot.
   * 
   * @param xPosition the double to store the theta heading angle
   * @return the theta heading angle.
   */
  public double getTheta() {
    double theta = 0;
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }

      theta = this.theta;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return theta;
  }
  
  /**
   * Returns the Odometer X data.
   * <p>
   * Writes the current X position of the robot.
   * 
   * @param xPosition the double to store the odometer x position
   * @return the odometer x position.
   */
  public double getX() {
    double xPosition = 0;
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }

      xPosition = x;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return xPosition;
  }
  
  /**
   * Returns the Odometer Y data.
   * <p>
   * Writes the current Y position of the robot.
   * 
   * @param yPosition the double to store the odometer Y position
   * @return the odometer Y position.
   */
  public double getY() {
    double yPosition = 0;
    lock.lock();
    try {
      while (isResetting) { // If a reset operation is being executed, wait until it is over.
        doneResetting.await(); // Using await() is lighter on the CPU than simple busy wait.
      }

      yPosition = y;
    } catch (InterruptedException e) {
      e.printStackTrace();
    } finally {
      lock.unlock();
    }

    return yPosition;
  }

  /**
   * Adds dx, dy and dtheta to the current values of x, y and theta, respectively. Useful for
   * odometry.
   * 
   * @param dx
   * @param dy
   * @param dtheta
   */
  public void update(double dx, double dy, double dtheta) {
    lock.lock();
    isResetting = true;
    try {
      x += dx;
      y += dy;
      theta = (theta + (360 + dtheta) % 360) % 360; // keeps the updates within 360 degrees
      isResetting = false;
      doneResetting.signalAll(); // Let the other threads know we are done resetting
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overrides the values of x, y and theta. Use for odometry correction.
   * 
   * @param x the value of x
   * @param y the value of y
   * @param theta the value of theta in degrees
   */
  public void setXYT(double x, double y, double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      this.y = y;
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites x. Use for odometry correction.
   * 
   * @param x the value of x
   */
  public void setX(double x) {
    lock.lock();
    isResetting = true;
    try {
      this.x = x;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites y. Use for odometry correction.
   * 
   * @param y the value of y
   */
  public void setY(double y) {
    lock.lock();
    isResetting = true;
    try {
      this.y = y;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }

  /**
   * Overwrites theta. Use for odometry correction.
   * 
   * @param theta the value of theta
   */
  public void setTheta(double theta) {
    lock.lock();
    isResetting = true;
    try {
      this.theta = theta;
      isResetting = false;
      doneResetting.signalAll();
    } finally {
      lock.unlock();
    }
  }
  
  /**
   * Overwrites turning correction. Use for odometry correction.
   * 
   * @param correction, the value of the new correction
   */
  public static void setTurningCorrection(double correction) {
    Odometer.turningCorrection = correction;
  }
  
  /**
   * Returns the current turning correction.
   * <p>
   * Writes the current turning correction of the odometer.
   * 
   * @param turningCorrection the double to store the turning correction
   * @return the turning correction.
   */
  public static double getTurningCorrection() {
    return Odometer.turningCorrection;
  }
  
}