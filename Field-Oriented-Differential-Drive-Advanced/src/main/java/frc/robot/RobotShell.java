package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import frc.robot.Services.*;
import frc.robot.Utils.Tests.ChassisHeadingPIDControllerTest;
import frc.robot.Utils.Tests.SimpleRobotTest;

import java.util.ArrayList;
import java.util.List;

public class RobotShell extends TimedRobot {
    private static final int updateFreq = 100;
    public static final boolean isFormalCompetition = false;

    private final XboxController copilotGamePad = new XboxController(1);

    PilotChassisService pilotChassisService;

    private RobotCore robotCore;

    public RobotShell() {
        super(1.0/updateFreq);
    }


    /** called once when the robot powers on */
    @Override
    public void robotInit() {
        // System.out.println("<-- Robot Shell | robot init -->");
        robotCore = new RobotCore();
    }

    /** called once when the driver station first connects to the robot */
    @Override
    public void driverStationConnected() {
        // System.out.println("<-- Robot Shell | driver station connected -->");
        robotCore.initializeRobot();

        pilotChassisService = new PilotChassisService(robotCore.chassisModule);
    }

    /** called repeatedly after the robot powers on, no matter enabled or not */
    @Override
    public void robotPeriodic() {
        // System.out.println("<-- Robot Shell | robot periodic -->");
    }

    /** called once when auto is selected and enable button is hit */
    @Override
    public void autonomousInit() {
        // System.out.println("<-- Robot Shell | autonomous init -->");
    }

    @Override
    public void autonomousPeriodic() {
        // System.out.println("<-- Robot Shell | auto periodic -->");
        robotCore.updateRobot();
    }

    @Override
    public void teleopInit() {
        // System.out.println("<-- Robot Shell | teleop init -->");
        startManualStage();
    }

    @Override
    public void teleopPeriodic() {
        // System.out.println("<-- Robot Shell | teleop periodic -->");
        robotCore.updateRobot();
    }

    @Override
    public void disabledInit() {
        // System.out.println("<-- Robot Shell | disable init -->");
        stopStage();
    }

    @Override
    public void disabledPeriodic() {
        // System.out.println("<-- Robot Shell | disabled periodic -->");
        robotCore.updateModules();
    }

    private SimpleRobotTest robotTest = null;
    @Override
    public void testInit() {
        if (robotTest == null)
            robotTest = new ChassisHeadingPIDControllerTest(robotCore.chassisModule, robotCore.gyro);
        robotTest.testStart();
    }

    @Override
    public void testPeriodic() {
        // System.out.println("<-- Robot Shell | robot init -->");
        robotTest.testPeriodic();
    }

    private void startManualStage() {
        final List<RobotServiceBase> services = new ArrayList<>();

        // here, add robot services
        services.add(pilotChassisService);

        robotCore.startStage(services);
    }

    private void stopStage() {
        robotCore.stopStage();
    }
}