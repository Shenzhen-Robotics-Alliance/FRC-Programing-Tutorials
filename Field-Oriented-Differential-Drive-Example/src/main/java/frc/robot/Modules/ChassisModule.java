package frc.robot.Modules;

import frc.robot.Drivers.IMUs.SimpleGyro;
import frc.robot.Drivers.Motors.Motor;
import frc.robot.Utils.MathUtils.Vector2D;
import frc.robot.Utils.MechanismControllers.EasyPIDController;
import frc.robot.Utils.RobotModuleOperatorMarker;
public class ChassisModule extends RobotModuleBase {

    public enum DriveMode {
        BASIC,
        FIELD_CENTRIC
    }
    private DriveMode driveMode;
    final Motor left, right;
    private final SimpleGyro gyro;
    // positive is forward, positive is counter-clockwise
    private double commandedForward, commandedTurn;
    public ChassisModule(Motor left, Motor right, SimpleGyro gyro) {
        super("chassis");
        this.left = left;
        this.right = right;
        this.gyro = gyro;
    }

    @Override
    public void init() {
        /* gain ownership to left and right motors */
        left.gainOwnerShip(this);
        right.gainOwnerShip(this);
        onReset();
    }

    private final EasyPIDController chassisRotationPIDController = new EasyPIDController(
                new EasyPIDController.EasyPIDProfile(
            0.5, // limit the chassis motor power to 70%
                Math.toRadians(45), // the chassis starts slowing down when the error is smaller than 90 degrees
                        0.05, // to move the chassis, it takes at least 5% the motor power
                                Math.toRadians(2), // if the error is within 2 degrees, we ignore it
                                Math.toRadians(5), // if the error is within 5 degrees, we think the chassis is in position
                                0.25, // the chassis needs 0.3 seconds to slow down
                                true // when the robot turns 360 degrees, it goes back to 0 degrees.  we call this "in cycle"
                                ), 0);

    @Override
    protected void periodic(double dt) {
        /* called 50 times every second */
        switch (driveMode) {
            case BASIC -> { // set power to left and right motor
                if (Math.abs(commandedForward) < 0.05 && Math.abs(commandedTurn) < 0.05)
                    driveMotors(0,0);
                else
                    driveMotors(commandedForward, commandedTurn);
            }
            case FIELD_CENTRIC -> {
                System.out.println("motion: " + desired2DMotion);

                /*
                * TODO complete the field-centric driving mode with the following steps:
                *   1. apply a 5% dead-band, if the magnitude of motion is smaller than 0.05, let the chassis stay still
                *   2. otherwise, obtain the direction of the current desired motion
                *   3. send the direction of the desired motion to PID controller
                *   4. get the correction power from PID controller, use it as the "turn power"
                *   5. use the magnitude of motion as the "forward power"
                *   HINTS:
                *  */
                desired2DMotion.getMagnitude(); // get the magnitude of the desired motion, in double (0~1)
                desired2DMotion.getHeading(); // get the heading(direction) of the current motion
                chassisRotationPIDController.setDesiredPosition(0); // set the desired rotation to 0
                chassisRotationPIDController.getMotorPower(gyro.getYawVelocity(), gyro.getYaw()); // using the current data from the gyro, get the calculated PID correction power from the controller
                driveMotors(
                        // set the forward power to 0.2 (-1 ~ 1)
                        0.2,
                        // the turn power (-1 ~ 1) to -0.4
                        -0.4
                ); 
            }
        }
    }

    /**
     * basic diff-drive algorithm that drives the motors to a given motion
     * @param forward the amount of power that moves the robot to its forward direction
     * @param rotate the amount of power that rotates the robot
     * */
    private void driveMotors(double forward, double rotate) {
        left.setPower(forward - rotate * 0.5, this);
        right.setPower(forward + rotate * 0.5, this);
    }

    /**
     * called once when robot started
     * */
    @Override
    public void onReset() {
        /* prevent random movements */
        left.setPower(0 ,this);
        right.setPower(0, this);

        this.driveMode = DriveMode.FIELD_CENTRIC;
        this.desired2DMotion = new Vector2D();

        this.commandedForward = this.commandedTurn = 0;

        gyro.reset();
    }

    /**
     * set the forward motion of chassis
     * @param forward the amount of chassis power used to move forward
     * @param operator the module/service that is sending this command
     * */
    public void setForward(double forward, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;

        this.commandedForward = forward;
    }

    /**
     * set the rotating motion of chassis
     * @param turn the amount of chassis power used to rotate the chassis, positive is counter-clockwise
     * @param operator the module/service that is sending this command
     * */
    public void setRotaryPower(double turn, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;

        this.driveMode = DriveMode.BASIC;
        this.commandedTurn = turn;
    }

    private Vector2D desired2DMotion;
    /**
     * sets the current driving mode to field centric
     * and set the current desired motion of the robot
     * @param motion the desired motion of the robot
     * @param operator the module/service that is sending this command
     * */
    public void setMotion(Vector2D motion, RobotModuleOperatorMarker operator){
        if (!isOwner(operator))
            return;

        this.driveMode = DriveMode.FIELD_CENTRIC;
        this.desired2DMotion = motion;
    }
}
