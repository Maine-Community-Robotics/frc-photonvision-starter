package frc.robot;

/** Robot-specific settings. Replace the example wiring before enabling hardware. */
public final class Constants {
  private Constants() {}

  public static final class Drive {
    // Set true only after checking PWM channels, motor types, and inversion on your robot.
    public static final boolean HARDWARE_CONFIGURED = false;
    public static final int LEFT_FRONT_PWM = 0;
    public static final int LEFT_REAR_PWM = 1;
    public static final int RIGHT_FRONT_PWM = 2;
    public static final int RIGHT_REAR_PWM = 3;
    public static final boolean LEFT_INVERTED = false;
    public static final boolean RIGHT_INVERTED = true;
    public static final double MAX_OUTPUT = 0.60;
    private Drive() {}
  }

  public static final class Operator {
    public static final int DRIVER_USB_PORT = 0;
    public static final double DEADBAND = 0.08;
    public static final double PRECISION_SCALE = 0.35;
    private Operator() {}
  }

  public static final class Vision {
    public static final String CAMERA_NAME = "mcr-front";
    public static final int APRILTAG_PIPELINE_INDEX = 0;
    public static final double MAX_FRAME_AGE_SECONDS = 0.25;
    public static final double MAX_FUTURE_SECONDS = 0.02;
    public static final double MAX_POSE_AMBIGUITY = 0.20;
    public static final double TURN_KP_PER_DEGREE = 0.015;
    public static final double YAW_TOLERANCE_DEGREES = 2.0;
    public static final double MAX_TURN = 0.30;
    private Vision() {}
  }
}
