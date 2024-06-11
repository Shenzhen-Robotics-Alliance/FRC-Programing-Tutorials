package frc.robot.Modules;

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
    // positive is forward, positive is counter-clockwise
    private double forward, turn;
    public ChassisModule(Motor left, Motor right) {
        super("chassis");
        this.left = left;
        this.right = right;
    }

    @Override
    public void init() {
    }

    private final EasyPIDController controller = new EasyPIDController(
            new EasyPIDController.EasyPIDProfile(
                    0.7, // limit the chassis motor power to 70%
                    Math.toRadians(60), // the chassis starts slowing down when the error is smaller than 90 degrees
                    0.05, // to move the chassis, it takes at least 5% the motor power
                    Math.toRadians(2), // if the error is within 2 degrees, we ignore it
                    Math.toRadians(5), // if the error is wihtin 5 degrees, we think the chassis is in position
                    0.3, // the chassis needs 0.3 seconds to slow down
                    true // when the robot turns 360 degrees, it goes back to 0 degrees.  we call this "in cycle"
            ), 0 // the chassis starts at zero degrees
    );

    @Override
    protected void periodic(double dt) {
        /* called 50 times every second */

        switch (driveMode) {
            case BASIC -> {
                /* set power to left and right motor */
                left.setPower(forward - turn * 0.5, this);
                right.setPower(forward + turn * 0.5, this);
            }
            case FIELD_CENTRIC -> {
                if (motion.getMagnitude() < 0.1) {
                    left.setPower(0, this);
                    right.setPower(0, this);
                    return;
                }
                motion.getMagnitude();
                motion.getHeading();
            }
        }
    }

    @Override
    public void onReset() {
        /* called once when robot started */

        /* gain ownership to left and right motors */
        left.gainOwnerShip(this);
        right.gainOwnerShip(this);

        /* prevent random movements */
        left.setPower(0 ,this);
        right.setPower(0, this);

        this.driveMode = DriveMode.FIELD_CENTRIC;
        this.motion = new Vector2D();
    }

    /**
     * set the forward motion of chassis
     * @param forward the amount of chassis power used to move forward
     * @param operator the module/service that is sending this command
     * */
    public void setForward(double forward, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;

        this.forward = forward;
    }

    /**
     * set the rotating motion of chassis
     * @param turn the amount of chassis power used to rotate the chassis, positive is counter-clockwise
     * @param operator the module/service that is sending this command
     * */
    public void setRotateryPower(double turn, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;

        this.turn = turn;
    }

    private Vector2D motion;
    public void setMotion(Vector2D motion, RobotModuleOperatorMarker operator){
        if (!isOwner(operator))
            return;

        this.motion = motion;
    }
}

