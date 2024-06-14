package frc.robot.Services;

import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Modules.ChassisModule;

public class PilotChassisService extends RobotServiceBase {
    private final ChassisModule chassis;
    private final XboxController xboxController = new XboxController(1);
    public PilotChassisService(ChassisModule chassis) {
        super("pilot chassis service");
        this.chassis = chassis;
    }

    @Override
    public void init() {
        reset();
    }

    @Override
    public void reset() {
        /* gain ownership to chassis */
        chassis.gainOwnerShip(this);

        /* set chassis to be still */
        chassis.setForward(0, this); chassis.setTurn(0, this);
    }

    @Override
    public void periodic() {
        /* if there is minor movement on joystick */
        if (Math.sqrt(xboxController.getLeftX() * xboxController.getLeftX() + xboxController.getRightY() * xboxController.getRightY()) < 0.05) {
            /* ignore and stop chassis */
            chassis.setForward(0, this);
            chassis.setTurn(0, this);
            return;
        }

        /* otherwise, pass driver commands to chassis */
        chassis.setForward(xboxController.getRightY() * -0.5, this);
        chassis.setTurn(xboxController.getLeftX()* -0.3, this);
    }

    @Override
    public void onDestroy() {

    }
}
