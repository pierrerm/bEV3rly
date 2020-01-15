package ca.mcgill.ecse211.finalProject;

import ca.mcgill.ecse211.wificlient.WifiConnection;
import lejos.hardware.ev3.LocalEV3;
import lejos.hardware.lcd.TextLCD;
import lejos.hardware.motor.EV3LargeRegulatedMotor;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import java.math.BigDecimal;
import java.util.Map;

/**
 * Integrate this carefully with your existing Resources class (See below for where to add your
 * code from your current Resources file). The order in which things are declared matters!
 * 
 * When you're done, replace this javadoc comment with the one you have from your Resources class.
 * 
 * @author Younes Boubekeur
 */
public class Resources {
  
  // Set these as appropriate for your team and current situation
  /**
   * The default server IP used by the profs and TA's.
   */
  public static final String DEFAULT_SERVER_IP = "192.168.2.3";
  
  /**
   * The IP address of the server that transmits data to the robot. Set this to the default for the
   * beta demo and competition.
   */
  public static final String SERVER_IP = "192.168.2.18";
  
  /**
   * Your team number.
   */
  public static final int TEAM_NUMBER = 3;
  
  /** 
   * Enables printing of debug info from the WiFi class. 
   */
  public static final boolean ENABLE_DEBUG_WIFI_PRINT = true;
  
  /**
   * Enable this to attempt to receive Wi-Fi parameters at the start of the program.
   */
  public static final boolean RECEIVE_WIFI_PARAMS = true;
  
  // DECLARE YOUR CURRENT RESOURCES HERE
  // eg, motors, sensors, constants, etc
  //////////////////////////////////////
  
//TEAM SPECIFIC PARAMETERS ------------------------------------------------------
  
 /**
  * Our team number
  */
 public static final int OUR_TEAM = 3;
 
 /**
  * Our wall intersection orientation
  */
 public static double ourWallIntersectionOrientation = 135;
 
 /**
  * Our starting corner
  */
 public static int ourCorner = 1;
 
 /**
  * The coordinates of the region representing our team's zone
  */
 public static Region ourZone;
 
 /**
  * The coordinates of the region representing our team's tunnel footprint
  */
 public static Region ourTunnel;
 
 /**
  * The coordinates of the point representing our team's target bin
  */
 public static Point ourBin;
  
//BOARD PARAMETERS ---------------------------------------------------------------
  
 /**
  * The tile size in centimeters.
  */
 public static final double TILE_SIZE = 30.48;
 
 /**
  * The number of tiles on the Y axis.
  */
 public static final int Y_TILES = 9;
 
 /**
  * The number of tiles on the X axis.
  */
 public static final double X_TILES = 15;
 
 /**
  * The heading angle of the intersection of the two walls
  */
 public static final double WALL_INTERSECTION_HEADING = 223;
 
  //ROBOT CORRECTIONS --------------------------------------------------------------
 
 /**
  * The left motor correction factor to straighten trajectory.
  */
 public static final double TURNING_CORRECTION = 1.04; //0.960 1.11
 
  /**
   * The left motor correction factor to straighten trajectory.
   */
  public static double LEFT_MOTOR_CORRECTION = 1.0034; //1.0034
  
  /**
   * The obstacle avoidance unit rolling distance.
   */
  public static final int AVOIDANCE_DISTANCE = 25;
  
  /**
   * The obstacle avoidance turn angle.
   */
  public static final int AVOIDANCE_TURN = 90;
  
  /**
   * The degree error.
   */
  public static final double DEG_ERR = 2.0;
  
  /**
   * The cm error.
   */
  public static final double CM_ERR = 1.0;
 
 // ROBOT PARAMETERS ---------------------------------------------------------------

 /**
  * The number of ping pong balls to be launched by the launcher.
  */
 public static final int NUM_BALLS = 5;

 /**
  * The launch distance of the ping pong ball launcher.
  */
 public static final double launchDistance = 4.6;
 
 /**
  * The launch points progress counter, counts the number of attempted launchpoints.
  */
 public static int launchPointsCovered = 0;
 
 /**
  * The threshold for rising edge detection in centimeters.
  */
 public static final double LIGHT_SENSOR_OFFSET = 3.2; //20.0
 
 /**
  * The number of balls the robot carries
  */
 public static final int BALL_COUNT = 5;
 
 /**
  * The wheel radius in centimeters.
  */
 public static final double WHEEL_RAD = 2.06;
 
 /**
  * The robot wheelbase in centimeters.
  */
 public static final double TRACK = 15.2; //15.2
 
 /**
  * The speed at which the robot moves forward in degrees per second.
  */
 public static final int FORWARD_SPEED = 200;
 
 /**
  * The speed at which the left wheel spins forward in degrees per second.
  */
 public static final int FORWARD_SPEED_L = (int) (FORWARD_SPEED * LEFT_MOTOR_CORRECTION);
 
 /**
  * The speed at which the right wheel spins forward in degrees per second.
  */
 public static final int FORWARD_SPEED_R = FORWARD_SPEED;
 
 /**
  * The speed at which the robot rotates in degrees per second.
  */
 public static final int ROTATE_SPEED = 130;
 
 /**
  * The speed at which the left wheel rotates in degrees per second.
  */
 public static final int ROTATE_SPEED_L = (int) (ROTATE_SPEED * LEFT_MOTOR_CORRECTION);
 
 /**
  * The speed at which the right wheel rotates in degrees per second.
  */
 public static final int ROTATE_SPEED_R = ROTATE_SPEED;
 
 /**
  * The motor acceleration in degrees per second squared.
  */
 public static final int ACCELERATION = 1000;
 
 /**
  * Timeout period in milliseconds.
  */
 public static final int TIMEOUT_PERIOD = 3000;
 
 /**
  * The odometer update period in ms.
  */
 public static final long ODOMETER_PERIOD = 25;
 
 // THRESHOLDS ---------------------------------------------------------------------
 
 /**
  * The distance threshold for obstacle detection.
  */
 public static final int DISTANCE_THRESHOLD = 9;
 
 /**
  * The red light threshold for line detection
  */
 public static final double RED_LIGHT_THRESHOLD = 0.85;
 
 /**
  * The threshold for rising edge and falling edge detection in centimeters.
  */
 public static final int DETECTION_THRESHOLD = 40;
 
 /**
  * The noise margin threshold for rising and falling edge detection in centimeters.
  */
 public static final int NOISE_THRESHOLD = 5;
 
 /**
  * The distance threshold for obstacle detection.
  */
 public static final int OBSTACLE_THRESHOLD = 9;
 
 /**
  * Offset from the wall (cm).
  */
 public static final int BAND_CENTER = 20;
 
 /**
  * Width of dead band (cm).
  */
 public static final int BAND_WIDTH = 3;
 
 /**
  * Threshold for filtering out ultrasonic consecutive sensor readings in wall following.
  */
 public static final int FILTER_OUT = 20;
   
 // MOTOR INSTANCES ----------------------------------------------------------------
 
 /**
  * The left launcher motor.
  */
 public static final EV3LargeRegulatedMotor launchMotorLeft =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("B"));
 
 /**
  * The right launcher motor.
  */
 public static final EV3LargeRegulatedMotor launchMotorRight =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("C"));
 
 /**
  * The left motor.
  */
 public static final EV3LargeRegulatedMotor leftMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("A"));

 /**
  * The right motor.
  */
 public static final EV3LargeRegulatedMotor rightMotor =
     new EV3LargeRegulatedMotor(LocalEV3.get().getPort("D"));
 
 // SENSOR INSTANCES ---------------------------------------------------------------
 
 /**
  * The right light sensor.
  */
 public static final EV3ColorSensor RIGHT_LIGHT_SENSOR = 
     new EV3ColorSensor(LocalEV3.get().getPort("S3"));
 
 /**
  * The left light sensor.
  */
 public static final EV3ColorSensor LEFT_LIGHT_SENSOR = 
     new EV3ColorSensor(LocalEV3.get().getPort("S1"));

 /**
  * The left ultrasonic sensor.
  */
 public static final EV3UltrasonicSensor LEFT_US_SENSOR = 
     new EV3UltrasonicSensor(LocalEV3.get().getPort("S4"));
 
 /**
  * The right ultrasonic sensor.
  */
 public static final EV3UltrasonicSensor RIGHT_US_SENSOR = 
     new EV3UltrasonicSensor(LocalEV3.get().getPort("S2"));
 
 /**
  * The LCD screen.
  */
 public static final TextLCD LCD = LocalEV3.get().getTextLCD();
 
 // THREAD INSTANCES ---------------------------------------------------------------
 
 /**
  * The center ultrasonic poller.
  */
 public static UltrasonicPoller2 doubleUsPoller = new UltrasonicPoller2(LEFT_US_SENSOR, RIGHT_US_SENSOR);
 
 /**
  * The light sensor poller.
  */
 public static LightPoller rightLightPoller = new LightPoller(RIGHT_LIGHT_SENSOR);
 
 /**
  * The light sensor poller.
  */
 public static LightPoller leftLightPoller = new LightPoller(LEFT_LIGHT_SENSOR);
 
 /**
  * The odometer.
  */
 public static Odometer odometer = new Odometer();
 
 /**
  * The Ultrasonic Controller.
  */
 public static UltrasonicController usController = new UltrasonicController();
  
 /**
  * Container for the Wi-Fi parameters.
  */
 public static Map<String, Object> wifiParameters;
 
 // This static initializer MUST be declared before any Wi-Fi parameters.
 static {
   receiveWifiParameters();
 }
 
 /**
  * Red team number.
  */
 public static int redTeam = get("RedTeam");

 /**
  * Red team's starting corner.
  */
 public static int redCorner = get("RedCorner");

 /**
  * Green team number.
  */
 public static int greenTeam = get("GreenTeam");

 /**
  * Green team's starting corner.
  */
 public static int greenCorner = get("GreenCorner");

 /**
  * The Red Zone.
  */
 public static Region red = new Region("Red_LL_x", "Red_LL_y", "Red_UR_x", "Red_UR_y");

 /**
  * The Green Zone.
  */
 public static Region green = new Region("Green_LL_x", "Green_LL_y", "Green_UR_x", "Green_UR_y");

 /**
  * The Island.
  */
 public static Region island =
     new Region("Island_LL_x", "Island_LL_y", "Island_UR_x", "Island_UR_y");

 /**
  * The red tunnel footprint.
  */
 public static Region tnr = new Region("TNR_LL_x", "TNR_LL_y", "TNR_UR_x", "TNR_UR_y");
 
 //public static double targetTheta = Math.max(get("TNR_LL_x"), get("TNR_UR_x")); // only for beta

 /**
  * The green tunnel footprint.
  */
 public static Region tng = new Region("TNG_LL_x", "TNG_LL_y", "TNG_UR_x", "TNG_UR_y");

 /**
  * The location of the red target bin.
  */
 public static Point redBin = new Point(get("Red_BIN_x"), get("Red_BIN_y"));

 /**
  * The location of the green target bin.
  */
 public static Point greenBin = new Point(get("Green_BIN_x"), get("Green_BIN_y"));
 
 /**
  * Receives Wi-Fi parameters from the server program.
  */
 public static void receiveWifiParameters() {
   // Only initialize the parameters if needed
   if (!RECEIVE_WIFI_PARAMS || wifiParameters != null) {
     return;
   }
   System.out.println("Waiting to receive Wi-Fi parameters.");

   // Connect to server and get the data, catching any errors that might occur
   try (WifiConnection conn =
       new WifiConnection(SERVER_IP, TEAM_NUMBER, ENABLE_DEBUG_WIFI_PRINT)) {
     /*
      * getData() will connect to the server and wait until the user/TA presses the "Start" button
      * in the GUI on their laptop with the data filled in. Once it's waiting, you can kill it by
      * pressing the upper left hand corner button (back/escape) on the EV3. getData() will throw
      * exceptions if it can't connect to the server (e.g. wrong IP address, server not running on
      * laptop, not connected to WiFi router, etc.). It will also throw an exception if it connects
      * but receives corrupted data or a message from the server saying something went wrong. For
      * example, if TEAM_NUMBER is set to 1 above but the server expects teams 17 and 5, this robot
      * will receive a message saying an invalid team number was specified and getData() will throw
      * an exception letting you know.
      */
     wifiParameters = conn.getData();
   } catch (Exception e) {
     System.err.println("Error: " + e.getMessage());
   }
 }
 
 /**
  * Returns the Wi-Fi parameter int value associated with the given key.
  * 
  * @param key the Wi-Fi parameter key
  * @return the Wi-Fi parameter int value associated with the given key
  */
 public static int get(String key) {
   if (wifiParameters != null) {
     return ((BigDecimal) wifiParameters.get(key)).intValue();
   } else {
     return 0;
   }
 }
  
 /**
  * Updates the parameters specific to our team based on our team number and
  * received Wi-Fi parameters.
  */
 public static void updateTeamParameters() {
   // our team is the green team
   if (greenTeam == OUR_TEAM) {
     ourCorner = greenCorner;
     ourZone = green;
     ourTunnel = tng;
     ourBin = greenBin;
     ourWallIntersectionOrientation = WALL_INTERSECTION_HEADING - 90 * greenCorner;
   // our team is the red team
   } else if (redTeam == OUR_TEAM) {
     ourCorner = redCorner;
     ourZone = red;
     ourTunnel = tnr;
     ourBin = redBin;
     ourWallIntersectionOrientation = WALL_INTERSECTION_HEADING - 90 * redCorner;
   }
 }
  
  /**
   * Represents a region on the competition map grid, delimited by its lower-left and upper-right
   * corners (inclusive).
   * 
   * @author Younes Boubekeur
   */
  public static class Region {
    /** The lower left corner of the region. */
    public Point ll;
    
    /** The upper right corner of the region. */
    public Point ur;
    
    /**
     * Constructs a Region.
     * 
     * @param lowerLeft the lower left corner of the region
     * @param upperRight the upper right corner of the region
     */
    public Region(Point lowerLeft, Point upperRight) {
      validateCoordinates(lowerLeft, upperRight);
      ll = lowerLeft;
      ur = upperRight;
    }
    
    /**
     * Helper constructor to make a Region directly from parameter names.
     * 
     * @param llX
     *     the Wi-Fi parameter key representing the lower left corner of the region x coordinate
     * @param llY
     *     the Wi-Fi parameter key representing the lower left corner of the region y coordinate
     * @param urX 
     *     the Wi-Fi parameter key representing the upper right corner of the region x coordinate
     * @param urY
     *     the Wi-Fi parameter key representing the upper right corner of the region y coordinate
     */
    public Region(String llX, String llY, String urX, String urY) {
      this(new Point(get(llX), get(llY)), new Point(get(urX), get(urY)));
    }
    
    /**
     * Validates coordinates.
     * 
     * @param lowerLeft the lower left corner of the region
     * @param upperRight the upper right corner of the region
     */
    private void validateCoordinates(Point lowerLeft, Point upperRight) {
      if (lowerLeft.x > upperRight.x || lowerLeft.y > upperRight.y) {
        throw new IllegalArgumentException(
            "Upper right cannot be below or to the left of lower left!");
      }
    }
    
    public String toString() {
      return "[" + ll + ", " + ur + "]";
    }
  }
  
  /**
   * Represents a coordinate point on the competition map grid.
   * 
   * @author Younes Boubekeur
   */
  public static class Point {
    /** The x coordinate. */
    public double x;
    
    /** The y coordinate. */
    public double y;
    
    /**
     * Constructs a Point.
     * 
     * @param x the x coordinate
     * @param y the y coordinate
     */
    public Point(double x, double y) {
      this.x = x;
      this.y = y;
    }
    
    public String toString() {
      return "(" + x + ", " + y + ")";
    }
    
  }
  
}
