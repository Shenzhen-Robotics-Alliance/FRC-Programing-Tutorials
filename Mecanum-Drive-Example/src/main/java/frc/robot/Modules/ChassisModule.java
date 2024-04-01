package frc.robot.Modules;

import frc.robot.Drivers.Motors.Motor;

public class ChassisModule extends RobotModuleBase {
    final Motor frontLeft, frontRight, backLeft, backRight;
    protected ChassisModule(Motor frontLeft, Motor frontRight, Motor backLeft, Motor backRight) {
        super("chassis");
        this.frontLeft = frontLeft;
        this.frontRight = frontRight;
        this.backLeft = backLeft;
        this.backRight = backRight;
    }

    @Override
    public void init() {

    }

    @Override
    protected void periodic(double dt) {

    }

    @Override
    public void onReset() {

    }
}
