package frc.robot.Services;

import edu.wpi.first.wpilibj.GenericHID;
import frc.robot.Modules.ChassisModule;
import frc.robot.Utils.MathUtils.Rotation2D;
import frc.robot.Utils.MathUtils.Vector2D;
import frc.robot.Utils.PilotController;
import frc.robot.Utils.RobotConfigReader;

public class PilotChassisService extends RobotServiceBase {
    private final ChassisModule chassis;
    private final RobotConfigReader robotConfig;
    private final PilotController pilotController;
    public PilotChassisService(ChassisModule chassis, RobotConfigReader robotConfig) {
        super("pilot chassis");

        this.chassis = chassis;
        this.robotConfig = robotConfig;
        pilotController = new PilotController(robotConfig, "control-RM_POCKET");
    }

    @Override
    public void init() {
        // your code goes here
    }

    @Override
    public void reset() {
        // your code goes here
    }

    @Override
    public void periodic() {
        // your code goes here

        // example: get controller axis
        pilotController.getTranslationalStickValue(); // -> Vector2D
        pilotController.getTranslationalStickValue().getX(); // -> double
        pilotController.getTranslationalStickValue().getY(); // -> double
    }

    @Override
    public void onDestroy() {
        // your code goes here
    }
}
