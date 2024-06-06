package frc.robot.Modules;

import frc.robot.Drivers.Motors.Motor;
import frc.robot.Utils.RobotModuleOperatorMarker;

public class ChassisModule extends RobotModuleBase {
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
        /* called once when robot started */

        /* gain ownership to left and right motors */
        left.gainOwnerShip(this);
        right.gainOwnerShip(this);

        /* prevent random movements */
        left.setPower(0 ,this);
        right.setPower(0, this);
    }

    @Override
    protected void periodic(double dt) {
        /* called 50 times every second */

        /* set power to left and right motor */
        left.setPower(forward - turn * 0.5, this);
        right.setPower(forward + turn * 0.5, this);
    }

    @Override
    public void onReset() {

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
    public void setTurn(double turn, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;

        this.turn = turn;
    }
}
