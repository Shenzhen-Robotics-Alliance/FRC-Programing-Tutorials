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
        // called once when robot started
        left.gainOwnerShip(this);
        right.gainOwnerShip(this);

        left.setPower(0 ,this);
        right.setPower(0, this);
    }

    @Override
    protected void periodic(double dt) {
        // relatively called

        left.setPower(forward - turn * 0.5, this);
        right.setPower(forward + turn * 0.5, this);

    }

    @Override
    public void onReset() {

    }

    public void setForward(double forward, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;

        this.forward = forward;
    }

    public void setTurn(double turn, RobotModuleOperatorMarker operator) {
        if (!isOwner(operator))
            return;

        this.turn = turn;
    }
}
