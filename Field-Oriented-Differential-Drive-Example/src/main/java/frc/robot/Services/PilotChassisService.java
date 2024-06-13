package frc.robot.Services;

import edu.wpi.first.wpilibj.Joystick;
import frc.robot.Modules.ChassisModule;
import frc.robot.Utils.MathUtils.Vector2D;

public class PilotChassisService extends RobotServiceBase {
    private final ChassisModule chassis;
    private final Joystick joystick = new Joystick(0);
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
        /* if there is minor movement on joystick */
        if (Math.sqrt(joystick.getX() * joystick.getX() + joystick.getY() * joystick.getY()) < 0.1) {
            /* ignore and stop chassis */
            chassis.setForward(0, this);
            chassis.setRotaryPower(0, this);
            return;
        }
        chassis.setForward(joystick.getY() * -1, this);
        chassis.setRotaryPower(joystick.getX() * -0.6, this);

        // TODO: here, add a button on the joystick such that
        //  whenever the button is pressed, the chassis drives in field-centric mode, using the input from the joystick
        //  Hints:
        joystick.getRawButton(3); // returns true if button 3 on the joystick is pressed, you can find the joystick button port on the driver station
        new Vector2D(new double[] {joystick.getX(), -joystick.getY()}); // creates a Vector corresponding to the pilot's translational input
    }

    @Override
    public void onDestroy() {

    }
}
