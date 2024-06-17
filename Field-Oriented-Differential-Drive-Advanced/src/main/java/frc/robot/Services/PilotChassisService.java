package frc.robot.Services;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Modules.ChassisModule;
import frc.robot.Utils.MathUtils.Vector2D;

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
        chassis.setForward(0, this); chassis.setRotaryPower(0, this);
    }

    @Override
    public void periodic() {
        // creates a Vector corresponding to the pilot's translational input
        if (xboxController.getAButton())
            chassis.setMotion(new Vector2D(new double[] {
                    xboxController.getRightX(),
                    -xboxController.getRightY()
            }).multiplyBy(0.5), this);
        else {
            chassis.setForward(xboxController.getRightY() * -0.8, this);
            chassis.setRotaryPower(xboxController.getLeftX() * -0.5, this);
        }

        if (xboxController.getYButton())
            chassis.onReset();
    }

    @Override
    public void onDestroy() {

    }
}
