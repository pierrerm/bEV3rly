package ca.mcgill.ecse211.finalProject;

import lejos.hardware.sensor.EV3ColorSensor;

/**
 * This class samples the light sensor and periodically updates the red light value.
 *
 */
public class LightPoller {
  private float[] csData;
  private EV3ColorSensor sensor;
  
  /**
   * Constructs an LightsensorPoller object.
   */
  public LightPoller(EV3ColorSensor lSensor) {
    // set sample size
    csData = new float[lSensor.sampleSize()];
    this.sensor = lSensor;
  }

  /**
   * Get the current red value measured by the Light Sensor
   * Accessible from outside of class
   * 
   * @return the current CSsensor red light intensity value
   */
  public int getRedValue() {
    // acquire data
    sensor.getRedMode().fetchSample(csData, 0);
    // extract from buffer, convert to [0,999] number, cast to int
    return (int) (csData[0] * 1000.0);
    //return this.redValue;
  }
}
