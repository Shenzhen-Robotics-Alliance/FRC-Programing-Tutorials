package frc.robot.Modules;

import frc.robot.Drivers.IMUs.SimpleGyro;
import frc.robot.Drivers.Motors.Motor;
import frc.robot.Utils.MathUtils.AngleUtils;
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
                if (desired2DMotion.getMagnitude() < 0.05)
                    driveMotors(0, 0);
                chassisRotationPIDController.setDesiredPosition(desired2DMotion.getHeading());
                driveMotors(desired2DMotion.getMagnitude(), chassisRotationPIDController.getMotorPower(gyro.getYawVelocity(), gyro.getYaw()));
            }
        }
    }

    /**
     * @param desiredMotion the desired motion of the robot, in reference to the field
     * @param currentRobotFacing the current rotation of the robot
     * @return the nearest rotation to the current rotation at which the robot will be lined-up with the desired motion
     * */
    private static double getNearestDesiredRotation(Vector2D desiredMotion, double currentRobotFacing) {
        /*
        * TODO write this part
        *  hint: to align the robot to the direction of the desired motion, the robot either has to face to desiredMotion.getHeading(), or it can face to desiredMotion.getHeading() + Math.PI
        * */

        // TODO find the rotation difference from current facing to desired motion direction, or (desired motion direction + 180 degrees)
        final double rotationDifferenceToDesiredMotionDirection = AngleUtils.getActualDifference(0, 0),
                rotationDifferenceToReversedDesiredMotionDirection = AngleUtils.getActualDifference(0, 0);

        if (Math.abs(rotationDifferenceToDesiredMotionDirection) - Math.abs(rotationDifferenceToReversedDesiredMotionDirection) > Math.toRadians(10))
            /* if the reversed direction of desiredMotion is 10 degrees SMALLER than the raw direction of desiredMotion */
            return 0; // TODO here we return the reversed direction of desiredMotion
        return 0; // TODO here we return the raw direction of desiredMotion
    }

    /**
     * @param desiredMotion
     * @param currentRobotFacing
     * @return get the amount of linear motion that the tank drive needs to drive (in its forward direction)
     * */
    private static double getLinearMotionProjectionOnCurrentFacing(Vector2D desiredMotion, double currentRobotFacing) {
        final double differenceToDesiredMotion = AngleUtils.getActualDifference(currentRobotFacing, desiredMotion.getHeading());
        if (Math.abs(differenceToDesiredMotion) > Math.toRadians(45))
            return 0;
        return Math.cos(AngleUtils.getActualDifference(currentRobotFacing, desiredMotion.getHeading()));
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
