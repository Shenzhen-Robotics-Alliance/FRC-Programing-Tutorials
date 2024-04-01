package frc.robot.Services;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Modules.ChassisModule;

public class PilotChassisService extends RobotServiceBase {
    private final ChassisModule chassis;
    private final Joystick joystick = new Joystick(0);
    public PilotChassisService(ChassisModule chassis) {
        super("pilot chassis service");
        this.chassis = chassis;
    }

    @Override
    public void init() {
        chassis.gainOwnerShip(this);

        chassis.setForward(0, this); chassis.setTurn(0, this);
    }

    @Override
    public void reset() {

    }

    @Override
    public void periodic() {
        if (Math.sqrt(joystick.getX() * joystick.getX() + joystick.getY() * joystick.getY()) < 0.1) {
            chassis.setForward(0, this);
            chassis.setTurn(0, this);
            return;
        }
        chassis.setForward(joystick.getY() * -1, this);
        chassis.setTurn(joystick.getX() * -0.6, this);
    }

    @Override
    public void onDestroy() {

    }
}
