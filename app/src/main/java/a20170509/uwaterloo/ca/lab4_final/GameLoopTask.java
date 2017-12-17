package a20170509.uwaterloo.ca.lab4_final;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.LinkedList;
import java.util.Random;
import java.util.TimerTask;

public class GameLoopTask extends TimerTask{

    // Class variables
    private Activity activity;
    private Context context;
    private RelativeLayout layout;
    private GameBlock newBlock;

    public enum  direction{UP,DOWN,LEFT,RIGHT,NO_MOVEMENT} // FSM States
    public LinkedList<GameBlock> gameblockList; // list of blocks

    // randomly generated numbers
    private int random_x;
    private int random_y;
    private Random myRandomGen = new Random();

    // boundaries
    public static final int LEFT_BOUNDARY = -75;
    public static final int UP_BOUNDARY = -75;
    public static final int SLOT_ISOLATION = 250;
    public static final int RIGHT_BOUNDARY = LEFT_BOUNDARY + 3 * SLOT_ISOLATION;
    public static final int DOWN_BOUNDARY = UP_BOUNDARY + 3 * SLOT_ISOLATION;

    // block board checks
    public int [][] num= new int[4][4];
    private boolean ifEmpty = true;

    private boolean Vic = false;
    private boolean [][] fullBlock= new boolean[4][4];
    private int [] replace = new int [100];

    // 'to be deleted' block list
    private LinkedList<Integer> tempList = new LinkedList<>();

    // Constructor
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameLoopTask(Activity activity, RelativeLayout layout, Context context) {
        // 2D array for board
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 4; j++) {
                fullBlock[i][j]=false;
                num[i][j]=0;
            }
        }
        this.activity = activity;
        this.layout = layout;
        this.context = context;
        gameblockList = new LinkedList<GameBlock>();
        createBlock();
        for(int i=0;i<100;i++){
            replace[i]=0;
        }
    }

    // Function to convert slot number to coordinates
    public static int convertnum(int number){
        number = LEFT_BOUNDARY+ number*SLOT_ISOLATION;
        return number;
    }
    // Function to convert coordinates to slot number
    public static int convertlocation(int location){
        location=(location-LEFT_BOUNDARY)/SLOT_ISOLATION;
        return location;
    }

    // Funtion to look ahead and watch out for collisions
    public boolean isOccupied(int curr_X,int curr_Y) {
        // iterate through linked list for a block with the specified position
        for(GameBlock each_block : gameblockList) {
            if(each_block.current()[0] == curr_X && each_block.current()[1] == curr_Y) {
                Log.d("Game Loop occupied: ", "Found!");
                return true;
            }
        }
        return false;
    }
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)

    // Funtion to create random blocks with each move
    private void createBlock(){
        boolean check = true;

        // random number generation
        random_x = myRandomGen.nextInt(4)*SLOT_ISOLATION + LEFT_BOUNDARY;
        random_y = myRandomGen.nextInt(4)*SLOT_ISOLATION + UP_BOUNDARY;

        // find if there is a position available and add block if there is
        for(int a = 0;a <4; a++){
            for(int b = 0; b < 4; b++){
                if(!fullBlock[a][b]) {
                    check = false;
                }
            }
        }
        if(!check) {//make sure the new block is in the empty block
            while (fullBlock[convertlocation(random_x)][convertlocation(random_y)]) {
                random_x = convertnum(myRandomGen.nextInt(4));
                random_y =  convertnum(myRandomGen.nextInt(4));
            }
            newBlock = new GameBlock(context, layout, random_x, random_y, this);
            layout.addView(newBlock);
            gameblockList.add(newBlock);
        }

    }

    @Override
    public void run(){
        this.activity.runOnUiThread(
                new Runnable() {
                    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
                    public void run() {
                        // boolean to check if movement is done
                        boolean move_done = true;
                        tempList.clear();

                        // Check for movement
                        for(GameBlock bg : gameblockList){
                            if(bg.current()[0]!=bg.target()[0]||bg.current()[1]!=bg.target()[1]){
                                move_done = false;
                            }
                        }
                        // Move the blocks in the linked list to their respective targets
                        for(GameBlock bg : gameblockList){
                            bg.move();
                            if(bg.current()[0]==bg.target()[0] && bg.current()[1]==bg.target()[1]){
                                for(GameBlock gb1:gameblockList){
                                    if(gb1.target()[0]==bg.target()[0]&&
                                            gb1.target()[1]==bg.target()[1]&& bg != gb1){
                                        if(bg.ifDouble){
                                            if(replace[gameblockList.indexOf(bg)]<1){
                                                // Add to temporary list of blocks to be removed
                                                tempList.add(gameblockList.indexOf(bg));
                                            }
                                        }
                                        if(replace[gameblockList.indexOf(bg)]<1 && move_done){

                                            // update value of block
                                            bg.count *= 2;

                                            // Check for end of game conditions
                                            if(bg.count>=256){
                                                Vic = true;
                                            }
                                            num[convertlocation(bg.target()[0])][convertlocation(bg.target()[1])] =bg.count;
                                            replace[gameblockList.indexOf(bg)]++;
                                        }
                                    }
                                }
                            }
                        }
                        if(move_done){
                            if(ifEmpty){
                                createBlock();
                                num[convertlocation(gameblockList.getLast().current()[0])][convertlocation(gameblockList.getLast().current()[1])] =gameblockList.getLast().get_number();
                                ifEmpty = false;
                            }
                            int delete = 0;
                            // Remove the marked blocks after merging is over
                            for(int x:tempList){
                                layout.removeView(gameblockList.get(x-delete).remove());
                                gameblockList.remove(x-delete);
                                delete++;
                            }
                        }
                        if(Vic||Defeat()){
                            // Implement End of Game Conditions
                            TextView END = new TextView(context);
                            layout.addView(END);
                            END.bringToFront();
                            END.setTextColor(Color.CYAN);
                            END.setTextSize(40);
                            END.setX(0);
                            END.setY(0);
                            if(Vic){

                                END.setText("VICTORY!!!!");
                            }
                            if(Defeat()){

                                END.setText("GAME OVER!!!!");
                            }

                        }
                    }
                }
        );
    }

    // Function to set the direction of the blocks
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void setDirection(direction newDirection){
        tempList.clear();
        ifEmpty = false;
        for(int i=0;i<100;i++){
            replace[i]=0;
        }

        // iterate through the list of game blocks
        for(GameBlock block : gameblockList) {
            block.setDestination(newDirection);
        }

        // reset 2D array
        for(int a = 0; a <4; a++){
            for(int b = 0; b < 4; b++){
                fullBlock[a][b] = false;
                num[a][b] = 0;
            }
        }
        // Update board with game blocks as they move
        for(GameBlock block2 : gameblockList) {
            if(block2.current()[0]!=block2.target()[0]||block2.current()[1]!=block2.target()[1]){
                ifEmpty = true;
            }
            for (int a = 0; a < 4; a++) {
                for (int b = 0; b < 4; b++) {
                    if (block2.target()[0] ==  convertnum(a) && block2.target()[1] == convertnum(b)) {
                        fullBlock[a][b] = true;
                        num[a][b] = block2.get_number();
                    }
                }
            }
        }
    }

    //Function to check if the game is over
    public boolean Defeat(){
        boolean victory = true;
        for(int b = 0; b <4; b++){
            for(int a = 0; a < 3; a++){
                if(num[a+1][b]==0||num[a][b]==0||num[a][b] ==num[a+1][b]||num[b][a]==num[b][a+1])
                {
                    victory = false;
                }
            }
        }
        return victory;
    }
}