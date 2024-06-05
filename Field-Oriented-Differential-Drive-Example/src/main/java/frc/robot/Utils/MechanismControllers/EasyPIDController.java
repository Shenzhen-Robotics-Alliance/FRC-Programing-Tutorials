package frc.robot.Utils.MechanismControllers;

public class EasyPIDController implements MechanismController {
    private final EasyPIDProfile easyPIDProfile;
    public EasyPIDController(EasyPIDProfile easyPIDProfile) {
        this.easyPIDProfile = easyPIDProfile;
    }

    @Override
    public double getMotorPower(double mechanismVelocity, double mechanismPosition) {
        return 0;
    }

    public static final class EasyPIDProfile {
        private final double maximumPower, errorStartDecelerate, minimumPower, errorTolerance, feedForwardRate;
        private final boolean isMechanismInCycle;
        public EasyPIDProfile(double maximumPower, double errorStartDecelerate, double minimumPower, double errorTolerance, double feedForwardRate, boolean isMechanismInCycle) {
            this.maximumPower = maximumPower;
            this.errorStartDecelerate = errorStartDecelerate;
            this.minimumPower = minimumPower;
            this.errorTolerance = errorTolerance;
            this.feedForwardRate = feedForwardRate;
            this.isMechanismInCycle = isMechanismInCycle;
        }
    }
}
