package frc.robot.Drivers.Motors;

import com.ctre.phoenix.motorcontrol.TalonSRXControlMode;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;
import frc.robot.Drivers.RobotDriverBase;
import frc.robot.Modules.RobotModuleBase;

public class TalonSRXMotor extends RobotDriverBase implements Motor {
    private final TalonSRX talonSRX;
    private double power = 0;
    private final boolean reversed;

    public TalonSRXMotor(TalonSRX talonSRX) {
        this(talonSRX, false);
    }
    public TalonSRXMotor(TalonSRX talonSRX, boolean reversed) {
        this.talonSRX = talonSRX;
        this.reversed = reversed;
    }

    @Override
    public int getPortID() {
        return talonSRX.getDeviceID();
    }

    @Override
    public void setPower(double power, RobotModuleBase operatorModule) {
        if (!isOwner(operatorModule))
            return;

        talonSRX.set(TalonSRXControlMode.PercentOutput, (this.power = power) * (reversed ? -1:1));
    }

    @Override
    public double getCurrentPower() {
        return power;
    }

    @Override
    public void onDestroy() {

    }

    @Override
    public void setMotorZeroPowerBehavior(ZeroPowerBehavior behavior, RobotModuleBase operatorModule) {

    }

    @Override
    public void disableMotor(RobotModuleBase operatorModule) {
        setPower(0, operatorModule);
    }

    @Override
    public void lockMotor(RobotModuleBase operatorModule) {
        disableMotor(operatorModule);
    }
}