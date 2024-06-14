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

    private final EasyPIDController controller = new EasyPIDController(
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
                System.out.println("motion: " + motion);
                if (motion.getMagnitude() < 0.05) {
                    driveMotors(0, 0);
                    return;
                }
                controller.setDesiredPosition(motion.getHeading() - Math.toRadians(90));

                final double turnPower = controller.getMotorPower(gyro.getYawVelocity(), gyro.getYaw()),
                        forwardPower = motion.getMagnitude();
                driveMotors(forwardPower, turnPower);
            }
        }
    }

    private void driveMotors(double forward, double turn) {
        left.setPower(forward - turn * 0.5, this);
        right.setPower(forward + turn * 0.5, this);
    }

    @Override
    public void onReset() {
        /* called once when robot started */

        /* prevent random movements */
        left.setPower(0 ,this);
        right.setPower(0, this);

        this.driveMode = DriveMode.FIELD_CENTRIC;
        this.motion = new Vector2D();

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

    private Vector2D motion;
    public void setMotion(Vector2D motion, RobotModuleOperatorMarker operator){
        if (!isOwner(operator))
            return;

        this.driveMode = DriveMode.FIELD_CENTRIC;
        this.motion = motion;
    }
}
