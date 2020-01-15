package ca.mcgill.ecse211.lab5;

import static ca.mcgill.ecse211.lab5.Resources.*;

/**
 * This class samples the light sensor and periodically updates the red light value.
 *
 */
public class LightsensorPoller implements Runnable {
  private float[] csData;
  private int redValue;
  
  /**
   * Constructs an LightsensorPoller object.
   */
  public LightsensorPoller() {
    // set sample size
    csData = new float[colorSensor.sampleSize()];
  }

  /*
   * Sensors now return floats using a uniform protocol. Need to convert CS result to an integer
   * [0,999] (non-Javadoc)
   * 
   * @see java.lang.Thread#run()
   */
  public void run() {
    while (true) {
      // acquire data
      colorSensor.getRedMode().fetchSample(csData, 0);
      // extract from buffer, convert to [0,999] number, cast to int
      redValue = (int) (csData[0] * 1000.0);
      try {
        Thread.sleep(30);
      } catch (Exception e) {
      } // Poor man's timed sampling
    }
  }

  /**
   * Get the current red value measured by the Light Sensor
   * Accessible from outside of class
   * 
   * @return redValue, the current CSsensor red light intensity value
   */
  public int getRedValue() {
    return this.redValue;
  }
}
