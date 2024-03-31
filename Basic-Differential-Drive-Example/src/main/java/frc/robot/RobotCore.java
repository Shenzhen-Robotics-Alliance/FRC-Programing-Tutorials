package frc.robot;

import java.util.ArrayList;
import java.util.List;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Modules.RobotModuleBase;
import frc.robot.Services.RobotServiceBase;

/**
 *
 * the core of the robot, including all the modules that powers the module
 * note that services are not included in this field
 * */
public class RobotCore {
        private final List<RobotModuleBase> modules;
        private List<RobotServiceBase> services;
        /**
         * creates a robot core
         * creates the instances of all the modules, but do not call init functions yet
         * */
        public RobotCore() {
                modules = new ArrayList<>();
                services = new ArrayList<>();
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