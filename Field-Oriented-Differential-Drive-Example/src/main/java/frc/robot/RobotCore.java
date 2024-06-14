package frc.robot;

import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Drivers.IMUs.NavX2IMU;
import frc.robot.Drivers.IMUs.SimpleGyro;
import frc.robot.Drivers.Motors.Motor;
import frc.robot.Drivers.Motors.MotorsSet;
import frc.robot.Drivers.Motors.VictorSPXMotor;
import frc.robot.Modules.ChassisModule;
import frc.robot.Modules.RobotModuleBase;
import frc.robot.Services.RobotServiceBase;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * the core of the robot, including all the modules that powers the module
 * note that services are not included in this field
 * */
public class RobotCore {
        private final List<RobotModuleBase> modules;
        private List<RobotServiceBase> services;

        public final ChassisModule chassisModule;
        public final SimpleGyro gyro = new SimpleGyro(0, true, new NavX2IMU());
        /**
         * creates a robot core
         * creates the instances of all the modules, but do not call init functions yet
         * */
        public RobotCore() {
                modules = new ArrayList<>();
                services = new ArrayList<>();

                final Motor
                        left = new MotorsSet(new Motor[] {
                                new VictorSPXMotor(new VictorSPX(1), false),
                                new VictorSPXMotor(new VictorSPX(4),false)}),
                        right = new MotorsSet(new Motor[] {
                                new VictorSPXMotor(new VictorSPX(2),true),
                                new VictorSPXMotor(new VictorSPX(3),true)
                        });
                chassisModule = new ChassisModule(left, right, gyro); modules.add(chassisModule);
        }

        /**
         * initializes the robot
         * note that this will take a little bit of time as it involves creating threads
         * it should be called once each competition, when the driver station connects to the robot
         * */
        public void initializeRobot() {
                System.out.println("<-- Robot | initializing robot... -->");
                /* initialize the modules and services */
                for (RobotModuleBase module:modules)
                        module.init();

                System.out.println("<-- Robot | robot initialized -->");
        }

        /**
         * resets the robot the current stage
         * this is called once at the start of each stage (auto or teleop)
         * @param services the robot services that will be used this stage
         * */
        public void startStage(List<RobotServiceBase> services) {
                this.services = services;
                System.out.println("<-- Robot Core | starting current stage... -->");
                /* initialize the services */
                for (RobotServiceBase service:services)
                        service.init();
                /* reset the services */
                for (RobotServiceBase service: services)
                        service.reset();
                /* resume the modules that was paused */
                for (RobotModuleBase module: modules)
                        module.enable();

                System.out.println("<-- Robot Core | current stage started -->");
        }

        /**
         * end the current stage
         * */
        public void stopStage() {
                System.out.println("<-- Robot | pausing robot... -->");

                for (RobotModuleBase module: modules)
                        module.disable();
                this.services = new ArrayList<>();

                System.out.println("<-- Robot | robot paused... -->");
        }

        /**
         * called when the robot is enabled
         * */
        private long t = System.currentTimeMillis();
        public void updateRobot() {
                for (RobotServiceBase service: services)
                        service.periodic();
                updateModules();

                /* monitor the program's performance */
                SmartDashboard.putNumber("robot main thread delay", System.currentTimeMillis()-t);
                t = System.currentTimeMillis();
        }

        public void updateModules() {
                for (RobotModuleBase module:modules)
                        module.periodic();
        }
}