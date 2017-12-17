package a20170509.uwaterloo.ca.lab4_final;

import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.TimerTask;

public class AccelerometerEventListener implements SensorEventListener {
    // Class variables
    private final float FILTER_CONSTANT = 8.0f;

    private TextView instanceOutput;
    private GameLoopTask myGameLoopTask;

    private myFSM[] myFSMs = new myFSM[2]; //x, y z
    private int myFSMCounter;
    private final int FSM_COUNTER_DEFAULT = 20;

    private float[][] historyReading = new float[100][3];

    // recording history
    private void insertHistoryReading(float[] values){
        for(int i = 1; i < 100; i++){
            historyReading[i - 1][0] = historyReading[i][0];
            historyReading[i - 1][1] = historyReading[i][1];
            historyReading[i - 1][2] = historyReading[i][2];
        }

        historyReading[99][0] += (values[0] - historyReading[99][0]) / FILTER_CONSTANT;
        historyReading[99][1] += (values[1] - historyReading[99][1]) / FILTER_CONSTANT;
        historyReading[99][2] += (values[2] - historyReading[99][2]) / FILTER_CONSTANT;
    }


    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    private void determineGesture(){

        myFSM.mySig[] sigs = new myFSM.mySig[2];

        for(int i = 0; i < 2; i++) {
            sigs[i] = myFSMs[i].getSignature();
            myFSMs[i].resetFSM();
        }

        // Using signature to determine left, right, up or down
        if(sigs[0] == myFSM.mySig.SIG_A && sigs[1] == myFSM.mySig.SIG_X){
            instanceOutput.setText("RIGHT");
            myGameLoopTask.setDirection(GameLoopTask.direction.RIGHT);
        }
        else if(sigs[0] == myFSM.mySig.SIG_B && sigs[1] == myFSM.mySig.SIG_X){
            instanceOutput.setText("LEFT");
            myGameLoopTask.setDirection(GameLoopTask.direction.LEFT);
        }
        else if(sigs[0] == myFSM.mySig.SIG_X && sigs[1] == myFSM.mySig.SIG_A){
            instanceOutput.setText("UP");
            myGameLoopTask.setDirection(GameLoopTask.direction.UP);

        }
        else if(sigs[0] == myFSM.mySig.SIG_X && sigs[1] == myFSM.mySig.SIG_B){
            instanceOutput.setText("DOWN");
            myGameLoopTask.setDirection(GameLoopTask.direction.DOWN);

        }
        else{
            instanceOutput.setText("N/A");
            myGameLoopTask.setDirection(GameLoopTask.direction.NO_MOVEMENT);
        }

    }


    public AccelerometerEventListener(TextView outputView,GameLoopTask gameLoopTask) {
        instanceOutput = outputView;
        //graph = graphOutput;

        for(int i = 0; i < 3; i++)
            for(int j = 0; j < 100; j++)
                historyReading[j][i] = 0.0f;

        // start the finite state machines
        myFSMs[0] = new myFSM();
        myFSMs[1] = new myFSM();

        myFSMCounter = FSM_COUNTER_DEFAULT;
        myGameLoopTask = gameLoopTask;
    }


    public float[][] getHistoryReading(){
        return historyReading;
    }

    public void onAccuracyChanged(Sensor s, int i) { }

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void onSensorChanged(SensorEvent se) {

        if (se.sensor.getType() == Sensor.TYPE_LINEAR_ACCELERATION) {

            insertHistoryReading(se.values);

            if(myFSMCounter > 0) {

                boolean reductionFlag = false;

                for (int i = 0; i < 2; i++) {
                    myFSMs[i].supplyInput(historyReading[99][i]);
                    if(myFSMs[i].getState() != myFSM.FSMState.WAIT)
                        reductionFlag = true;
                }

                if(reductionFlag)
                    myFSMCounter--;
            }
            else if(myFSMCounter <= 0){
                determineGesture();
                myFSMCounter = FSM_COUNTER_DEFAULT;
            }

            if(myFSMs[0].isReady() && myFSMs[1].isReady())
                instanceOutput.setTextColor(Color.GREEN);
            else
                instanceOutput.setTextColor(Color.BLACK);

            // instanceOutput.setText("The Accelerometer Reading is: \n"
            //         + String.format("(%.2f, %.2f, %.2f)", historyReading[99][0],historyReading[99][1], historyReading[99][2]) + "\n");


            //graph.addPoint(historyReading[99]);

        }
    }

}



//Need to create a main game loop here with a Timer

