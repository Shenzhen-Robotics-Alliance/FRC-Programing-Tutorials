package frc.robot.Utils.MechanismControllers;

public interface MechanismController {
    /** gets the calculate correction power that should be applied to the mechanism */
    double getMotorPower(double mechanismVelocity, double mechanismPosition);
}
