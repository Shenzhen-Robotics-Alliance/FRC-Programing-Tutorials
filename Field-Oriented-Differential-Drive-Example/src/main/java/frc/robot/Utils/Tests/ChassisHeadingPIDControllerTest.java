package frc.robot.Utils.Tests;

import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Drivers.IMUs.SimpleGyro;
import frc.robot.Modules.ChassisModule;
import frc.robot.Utils.MechanismControllers.EasyPIDController;

public class ChassisHeadingPIDControllerTest implements SimpleRobotTest {
    private final ChassisModule chassis;
    private final SimpleGyro gyro;
    private final XboxController xboxController = new XboxController(1);
    private final EasyPIDController controller;

    public ChassisHeadingPIDControllerTest(ChassisModule chassis, SimpleGyro gyro) {
        this.chassis = chassis;
        this.gyro = gyro;
        controller = new EasyPIDController(
                new EasyPIDController.EasyPIDProfile(
                        0.5, // limit the chassis motor power to 70%
                        Math.toRadians(45), // the chassis starts slowing down when the error is smaller than 90 degrees
                        0.05, // to move the chassis, it takes at least 5% the motor power
                        Math.toRadians(2), // if the error is within 2 degrees, we ignore it
                        Math.toRadians(5), // if the error is within 5 degrees, we think the chassis is in position
                        0.25, // the chassis needs 0.3 seconds to slow down
                        true // when the robot turns 360 degrees, it goes back to 0 degrees.  we call this "in cycle"
                ), 0);
    }

    @Override
    public void testStart() {
        chassis.init();
        chassis.reset();
        chassis.enable();
        gyro.reset();
    }

    @Override
    public void testPeriodic() {
        controller.setDesiredPosition(0);
        if (xboxController.getAButton())
            chassis.setRotaryPower(
                    /* feed the gyro's data to the PID controller */
                    controller.getMotorPower(gyro.getYawVelocity(), gyro.getYaw()), null);
            /* otherwise, we use the controller's righter stick to control the rotation manually */
        else
            chassis.setRotaryPower(-xboxController.getRightX(), null);
        chassis.periodic();

        SmartDashboard.putNumber("Gyro Yaw", gyro.getYaw());
        SmartDashboard.putNumber("Gyro Yaw Vel", gyro.getYawVelocity());
        SmartDashboard.putNumber("Correction Power", controller.getMotorPower(gyro.getYawVelocity(), gyro.getYaw()));
    }
}