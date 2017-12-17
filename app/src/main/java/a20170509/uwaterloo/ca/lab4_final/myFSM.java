package a20170509.uwaterloo.ca.lab4_final;

public class myFSM {

    //FSM Constants
    enum FSMState{WAIT, A_RISE, B_FALL, DETERMINED};
    //FSM Initial State
    private FSMState myState;

    //Signature Constants
    enum mySig{SIG_A, SIG_B, SIG_X};
    //Default Signature
    private mySig Signature;

    //Signature A Constants
    private final float[] THRESHOLD_A = {0.6f, 2.0f};
    //Signature B Constants
    private final float[] THRESHOLD_B = {-0.6f, -2.0f};

    private float previousInput;

    private void mainFSM(float currentInput){

        float deltaInput = currentInput - previousInput;  // change in acceleration

        // Switch statement to determine which state, developed in lab 2
        switch(myState){

            case WAIT:

                if(deltaInput >= THRESHOLD_A[0]) {
                    myState = FSMState.A_RISE;
                }
                else if(deltaInput <= THRESHOLD_B[0]){
                    myState = FSMState.B_FALL;
                }

                break;

            case A_RISE:

                if(deltaInput <= 0){
                    if(currentInput >= THRESHOLD_A[1]){
                        Signature = mySig.SIG_A;
                        myState = FSMState.DETERMINED;
                    }
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case B_FALL:

                if(deltaInput >= 0){
                    if(currentInput <= THRESHOLD_B[1]){
                        Signature = mySig.SIG_B;
                        myState = FSMState.DETERMINED;
                    }
                    else {
                        Signature = mySig.SIG_X;
                        myState = FSMState.DETERMINED;
                    }
                }

                break;

            case DETERMINED:

                break;

            default:

                resetFSM();
                break;
        }

    }

    // FSM Constructor
    public myFSM(){
        myState = FSMState.WAIT;
        Signature = mySig.SIG_X;
        previousInput = 0.0f;
    }

    // Default case function; set back to wait state
    public void resetFSM(){
        myState = FSMState.WAIT;
        Signature = mySig.SIG_X;
        previousInput = 0.0f;
    }

    // Function to feed the FSM
    public void supplyInput(float input){
        //Log.d("Debug FSM", "Input: " + input);

        mainFSM(input);

        previousInput = input;
    }

    // Getter function to get the Signature
    public mySig getSignature(){
        if(myState == FSMState.DETERMINED){
            return Signature;
        }
        else{
            return mySig.SIG_X;
        }
    }

    // Function to check state of the FSM
    public boolean isReady(){
        if(myState == FSMState.WAIT)
            return true;
        else
            return false;
    }

    // Getter function to get the state
    public FSMState getState(){
        return myState;
    }

}

