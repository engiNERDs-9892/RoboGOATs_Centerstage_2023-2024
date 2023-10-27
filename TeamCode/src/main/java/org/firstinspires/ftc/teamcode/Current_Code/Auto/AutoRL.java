

package org.firstinspires.ftc.teamcode.Current_Code.Auto;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;
import org.openftc.easyopencv.OpenCvWebcam;

@Autonomous(name="AutoRL", group="Robot")
public class AutoRL extends LinearOpMode {

    /* Declare OpMode members. */
    private DcMotor motorFL = null;
    private DcMotor motorFR = null;
    private DcMotor motorBL = null;
    private DcMotor motorBR = null;
    private Servo servoRadial;
    private Servo servoLadial;
    private Servo servoWR;
    private Servo servoWL;

    private DcMotor motorLS = null;
    OpenCvWebcam webcam;
    Red.SkystoneDeterminationPipeline pipeline;
    Red.SkystoneDeterminationPipeline.SkystonePosition snapshotAnalysis = Red.SkystoneDeterminationPipeline.SkystonePosition.LEFT;
    int in = 45;

    // These variable are declared here (as class members) so they can be updated in various methods,
    // but still be displayed by sendTelemetry()


    // Calculate the COUNTS_PER_INCH for your specific drive train.
    // Go to your motor vendor website to determine your motor's COUNTS_PER_MOTOR_REV
    // For external drive gearing, set DRIVE_GEAR_REDUCTION as needed.
    // For example, use a value of 2.0 for a 12-tooth spur gear driving a 24-tooth spur gear.
    // This is gearing DOWN for less speed and more torque.
    // For gearing UP, use a gear ratio less than 1.0. Note this will affect the direction of wheel rotation.
    static final double COUNTS_PER_MOTOR_REV = 1120;   // eg: GoBILDA 312 RPM Yellow Jacket
    static final double DRIVE_GEAR_REDUCTION = 1.0;     // No External Gearing.
    static final double WHEEL_DIAMETER_INCHES = 4.0;     // For figuring circumference
    static final double COUNTS_PER_INCH = (COUNTS_PER_MOTOR_REV * DRIVE_GEAR_REDUCTION) / (WHEEL_DIAMETER_INCHES * 3.1415);


    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        webcam = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        pipeline = new Red.SkystoneDeterminationPipeline();
        webcam.setPipeline(pipeline);

        webcam.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                // This is in what viewing window the camera is seeing through and it doesn't matter
                // what orientation it is | UPRIGHT, SIDEWAYS_LEFT, SIDEWAYS_RIGHT, etc.

                webcam.startStreaming(1280, 720, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {
            }
        });

        while (!isStarted() && !isStopRequested()) {
            telemetry.addData("Realtime analysis", pipeline.getAnalysis());
            telemetry.update();

            // Don't burn CPU cycles busy-looping in this sample
            sleep(50);
        }

        snapshotAnalysis = pipeline.getAnalysis();


        telemetry.addData("Snapshot post-START analysis", snapshotAnalysis);
        telemetry.update();

        // Initialize the drive system variables.
        motorFR = hardwareMap.get(DcMotor.class, "motorFR");
        motorFL = hardwareMap.get(DcMotor.class, "motorFL");
        motorBR = hardwareMap.get(DcMotor.class, "motorBR");
        motorBL = hardwareMap.get(DcMotor.class, "motorBL");
        motorLS = hardwareMap.get(DcMotor.class, "motorLS");
        servoRadial = hardwareMap.servo.get("servoRadial");
        servoLadial = hardwareMap.servo.get("servoLadial");
        servoWR = hardwareMap.servo.get("servoWR");
        servoWL = hardwareMap.servo.get("servoWL");


        // To drive forward, most robots need the motor on one side to be reversed, because the axles point in opposite directions.
        // When run, this OpMode should start both motors driving forward. So adjust these two lines based on your first test drive.
        // Note: The settings here assume direct drive on left and right wheels.  Gear Reduction or 90 Deg drives may require direction flips



        // Ensure the robot is stationary.  Reset the encoders and set the motors to BRAKE mode
        motorFL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBL.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBR.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorLS.setMode(DcMotor.RunMode.RUN_USING_ENCODER);


        telemetry.addData("Status", "\uD83C\uDD97");
        telemetry.update();

        waitForStart();

        switch (snapshotAnalysis) {
            case LEFT: // Level 3
            {
                left(.25, 30);
                back(.25, 8);
                //drop the thing
                left(.25, 2);
                forward(.25, 80);
                left(.25, 6);

                break;


            }


            case RIGHT: // Level 1
            {
                left(.25, 30);
                forward(.25, 16);
                //drop the thing
                left(.25, 2);
                forward(.25, 64);
                right(.25, 6);

                break;
            }

            case CENTER: // Level 2
            {
                left(.25, 30);
                forward(.25, 8);
                //drop the thing
                left(.25, 2);
                forward(.25, 72);

                break;
            }
        }

    }






    public void forward(double speed, int distance) {
        int moveCounts = (int) (distance * COUNTS_PER_INCH);

        motorBL.setDirection(DcMotorSimple.Direction.FORWARD);
        motorFR.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBR.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFL.setDirection(DcMotorSimple.Direction.FORWARD);

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFL.setTargetPosition(distance * in);
        motorFR.setTargetPosition(distance * in);
        motorBL.setTargetPosition(distance * in);
        motorBR.setTargetPosition(distance * in);

        motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorBL.setPower(speed);
        motorFL.setPower(speed);
        motorBR.setPower(speed);
        motorFR.setPower(speed);



        while (opModeIsActive() && motorFL.isBusy()) {
        }

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }
    public void back(double speed, int distance) {
        int moveCounts = (int) (distance * COUNTS_PER_INCH);

        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFR.setDirection(DcMotorSimple.Direction.FORWARD);
        motorBR.setDirection(DcMotorSimple.Direction.FORWARD);
        motorFL.setDirection(DcMotorSimple.Direction.REVERSE);

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFL.setTargetPosition(distance * in);
        motorFR.setTargetPosition(distance * in);
        motorBL.setTargetPosition(distance * in);
        motorBR.setTargetPosition(distance * in);

        motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorBL.setPower(speed);
        motorFL.setPower(speed);
        motorBR.setPower(speed);
        motorFR.setPower(speed);



        while (opModeIsActive() && motorFL.isBusy()) {
        }

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }
    public void right(double speed, int distance) {
        int moveCounts = (int) (distance * COUNTS_PER_INCH);

        motorBL.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFR.setDirection(DcMotorSimple.Direction.FORWARD);
        motorBR.setDirection(DcMotorSimple.Direction.REVERSE);
        motorFL.setDirection(DcMotorSimple.Direction.FORWARD);

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFL.setTargetPosition(distance * in);
        motorFR.setTargetPosition(distance * in);
        motorBL.setTargetPosition(distance * in);
        motorBR.setTargetPosition(distance * in);

        motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorBL.setPower(speed);
        motorFL.setPower(speed);
        motorBR.setPower(speed);
        motorFR.setPower(speed);



        while (opModeIsActive() && motorFL.isBusy()) {
        }

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }
    public void left(double speed, int distance) {
        int moveCounts = (int) (distance * COUNTS_PER_INCH);

        motorBL.setDirection(DcMotorSimple.Direction.FORWARD);
        motorFR.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBR.setDirection(DcMotorSimple.Direction.FORWARD);
        motorFL.setDirection(DcMotorSimple.Direction.REVERSE);

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFL.setTargetPosition(distance * in);
        motorFR.setTargetPosition(distance * in);
        motorBL.setTargetPosition(distance * in);
        motorBR.setTargetPosition(distance * in);

        motorFL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFR.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBL.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBR.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorBL.setPower(speed);
        motorFL.setPower(speed);
        motorBR.setPower(speed);
        motorFR.setPower(speed);



        while (opModeIsActive() && motorFL.isBusy()) {
        }

        motorFL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBL.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBR.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    public void lsUp(double speed, int distance){
        int moveCounts = (int) (distance * COUNTS_PER_INCH);

        motorLS.setDirection(DcMotorSimple.Direction.REVERSE);


        motorLS.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        motorLS.setTargetPosition(distance * in);


        motorLS.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        motorLS.setPower(-speed);

        sleep(500);

        servoRadial.setPosition(0);
        servoLadial.setPosition(180);

        sleep(500);

        servoWR.setPosition(0);
        servoWL.setPosition(180);

        while (opModeIsActive() && motorFL.isBusy()) {
        }

        motorLS.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

    }

    public void lsDown(double speed, int distance){
        int moveCounts = (int) (distance * COUNTS_PER_INCH);

        motorLS.setDirection(DcMotorSimple.Direction.FORWARD);


        motorLS.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


        motorLS.setTargetPosition(distance * in);


        motorLS.setMode(DcMotor.RunMode.RUN_TO_POSITION);


        motorLS.setPower(-speed);

        sleep(500);

        servoRadial.setPosition(180);
        servoLadial.setPosition(0);

        sleep(500);

        servoWR.setPosition(180);
        servoWL.setPosition(0);


        while (opModeIsActive() && motorFL.isBusy()) {
        }

        motorLS.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);


    }

}