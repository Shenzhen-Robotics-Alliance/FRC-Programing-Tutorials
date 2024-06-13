package frc.robot.Utils.Tests;

import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Drivers.IMUs.SimpleGyro;
import frc.robot.Modules.ChassisModule;
import frc.robot.Utils.MechanismControllers.EasyPIDController;

public class ChassisHeadingPIDControllerTest implements SimpleRobotTest {
    private final ChassisModule chassis;
    private final SimpleGyro gyro;
    private final XboxController xboxController = new XboxController(1);
    EasyPIDController controller;

    public ChassisHeadingPIDControllerTest(ChassisModule chassis, SimpleGyro gyro) {
        this.chassis = chassis;
        this.gyro = gyro;
    }

    @Override
    public void testStart() {
        chassis.init();
        chassis.reset();
        chassis.enable();
    }

    @Override
    public void testPeriodic() {
        /* we will try make the chassis stay at its starting rotation */
        controller.setDesiredPosition(0);
        /* if button A is pressed, send the controller's output to chassis as rotating power */
        if (xboxController.getAButton())
            chassis.setTurn(controller.getMotorPower(gyro.getYawVelocity(), gyro.getYaw()), null);
            /* otherwise, we use the controller's righter stick to control the rotation manually */
        else
            chassis.setTurn(-xboxController.getRightX(), null);
        chassis.periodic();
    }
}
