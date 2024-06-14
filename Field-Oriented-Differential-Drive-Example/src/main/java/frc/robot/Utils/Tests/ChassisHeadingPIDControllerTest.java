package frc.robot.Utils.Tests;

import frc.robot.Drivers.IMUs.SimpleGyro;
import frc.robot.Modules.ChassisModule;

public class ChassisHeadingPIDControllerTest implements SimpleRobotTest {
    private final ChassisModule chassis;
    private final SimpleGyro gyro;

    public ChassisHeadingPIDControllerTest(ChassisModule chassis, SimpleGyro gyro) {
        this.chassis = chassis;
        this.gyro = gyro;
    }

    @Override
    public void testStart() {

    }

    @Override
    public void testPeriodic() {

    }
}