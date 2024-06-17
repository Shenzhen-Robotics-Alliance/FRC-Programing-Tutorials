package frc.robot.Utils.MechanismControllers;

import frc.robot.Utils.MathUtils.AngleUtils;
import frc.robot.Utils.MathUtils.LookUpTable;

/**
 * This is an easy tool to control mechanisms with the most basic PID algorithm.
 * assumption: the motor is configured to spin in the positive-encoder direction
 * This class has no essential different from PIDController class by WPILib, except that this class automatically calculates the kP and kD according to some parameters
 * */
public class EasyPIDController implements MechanismController {
    /** the profile of the mechanism being controlled */
    private final EasyPIDProfile profile;

    private double desiredPosition;
    private boolean mechanismInPosition;
    /**
     * initializes an easy pid controller with a given profile
     * */
    public EasyPIDController(EasyPIDProfile profile, double startingPosition) {
        this.profile = profile;
        desiredPosition = startingPosition;
        mechanismInPosition = false;
    }

    /**
     * @param mechanismVelocity the current velocity of the mechanism
     * @param mechanismPosition the current position of the mechanism
     * @return the correction power
     * */
    @Override
    public double getMotorPower(double mechanismVelocity, double mechanismPosition) {
        final double
                mechanismPositionWithFeedForward = mechanismPosition + mechanismVelocity * profile.mechanismDecelerationTime,
                error = profile.isMechanismInCycle ?
                        AngleUtils.getActualDifference(mechanismPositionWithFeedForward, desiredPosition)
                        : desiredPosition - mechanismPositionWithFeedForward;
        this.mechanismInPosition = Math.abs(error) < profile.errorAsMechanismInPlace;
        if (Math.abs(error) < profile.errorTolerance)
            return 0;
        final double power = LookUpTable.linearInterpretationWithBounding(profile.errorTolerance, profile.minimumPower, profile.errorStartDecelerate, profile.maximumPower, Math.abs(error));
        return Math.copySign(power, error);
    }

    public void setDesiredPosition(double desiredPosition) {
        this.desiredPosition = desiredPosition;
    }

    public boolean isMechanismInPosition() {
        return mechanismInPosition;
    }

    public static final class EasyPIDProfile {
        private final double maximumPower, errorStartDecelerate, minimumPower, errorTolerance, errorAsMechanismInPlace, mechanismDecelerationTime;
        private final boolean isMechanismInCycle;
        /**
         * creates the profile for a mechanism
         * @param maximumPower the maximum amount of output power allowed
         * @param errorStartDecelerate the amount of error (distance between current and desired position), in encoder units, that the mechanism will start decelerating from its maximum speed
         * @param minimumPower  the minimum amount of output power needed to move the mechanism
         * @param errorTolerance the amount of error to ignore, that is, if the error is within this range, the motor will stay still
         * @param errorAsMechanismInPlace the amount of error that the mechanism takes as the mechanism is in place
         * @param mechanismDecelerationTime the amount of time, in seconds, that the mechanism needs to decelerate from maximum speed to zero
         *                                  when moving, the mechanism always think itself this much time ahead of its current position
         * @param isMechanismInCycle whether the mechanism is a cycle
         *                           if the mechanism is a cycle, when it passes the position 2*PI (360deg), it will go to its origin (0)
         *                           examples of controllers that are in cycles are: swerve wheel steering controller, chassis rotation controller, etc.
         * */
        public EasyPIDProfile(double maximumPower, double errorStartDecelerate, double minimumPower, double errorTolerance, double errorAsMechanismInPlace, double mechanismDecelerationTime, boolean isMechanismInCycle) {
            this.maximumPower = maximumPower;
            this.errorStartDecelerate = errorStartDecelerate;
            this.minimumPower = minimumPower;
            this.errorTolerance = errorTolerance;
            this.errorAsMechanismInPlace = errorAsMechanismInPlace;
            this.mechanismDecelerationTime = mechanismDecelerationTime;
            this.isMechanismInCycle = isMechanismInCycle;
        }
    }
}
