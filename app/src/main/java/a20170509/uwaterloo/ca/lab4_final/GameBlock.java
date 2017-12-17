package a20170509.uwaterloo.ca.lab4_final;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.Log;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import java.util.Random;

// Abstract class for blocks
abstract class GameBlockTemplate extends ImageView{
    public GameBlockTemplate(Context context) {
        super(context);
    }
    public abstract void setDestination(GameLoopTask.direction newdir);
    public abstract void move();

    public TextView tv;


}
public class GameBlock extends GameBlockTemplate {

    // Class variables
    private final float acc = 4.0f;
    private final float IMAGE_SCALE = 0.5f;
    private int myCoordX;
    private int myCoordY;
    private int targetX;
    private int targetY;
    public int count;
    private int OFFSET_X = 150;
    private int OFFSET_Y = 120;
    private int Velocity;

    private GameLoopTask.direction myDir =GameLoopTask.direction.NO_MOVEMENT;
    public RelativeLayout gameBlockLayout;
    private GameLoopTask myBlock;

    public boolean canMove;
    private int [] BlockNum = new int [4];
    public boolean ifDouble = false;

    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public GameBlock(Context gbContext, RelativeLayout RL, int coord_X, int coord_Y,GameLoopTask task ){
        super(gbContext);
        this.setImageResource(R.drawable.gameblock);
        this.gameBlockLayout = RL;
        this.myBlock = task;

        myCoordX = coord_X;
        myCoordY = coord_Y;
        targetX = myCoordX;
        targetY = myCoordY;
        Velocity = 0;

        myDir = GameLoopTask.direction.NO_MOVEMENT;
        canMove = true;
        // set first block and scale graphics
        setX(myCoordX);
        setY(myCoordY);
        setScaleX(IMAGE_SCALE);
        setScaleY(IMAGE_SCALE);

        // Generate random  number for block that is a power of 2
        Random myRandom = new Random();
        count = (myRandom.nextInt(2)+1)*2;
        tv = new TextView(gbContext);

        // Add to textview
        gameBlockLayout.addView(tv);
        BlockNum = new int[4];
        ifDouble = false;

        // Edit textview display
        tv.setX(myCoordX+OFFSET_X);
        tv.setY(myCoordY+OFFSET_Y);
        tv.setText(String.format("%d",count));
        tv.setTextSize(40f);
        tv.setTextColor(Color.BLACK);

    }

    // Function return the current coordinates
    public int[] current(){
        int[] curr = new int[2];
        curr[0] = myCoordX;
        curr[1] = myCoordY;
        return curr;
    }
    // Function to return the current target coordinates
    public  int[] target(){
        int[] targetcoord = new int[2];
        targetcoord[0] = targetX;
        targetcoord[1] = targetY;
        return targetcoord;
    }
    // Function to get rid of the zeroes in the array of block numbers and return updated array
    public int[] remove_zero (){
        int tempX = 0;
        for (int a : BlockNum) {
            if (a != 0)
                tempX++;
        }
        int[] tempY = new int[tempX];
        int j = 0;
        for (int a = 0; a < BlockNum.length; a++) {
            if (BlockNum[a] != 0) {
                tempY[a - j] = BlockNum[a];
            } else j++;
        }
        return tempY;
    }

    // getter funtion for counter
    public  int get_number(){
        return  count;
    }


    // Funtion to remove a Game Block - Memory Management
    public  GameBlock remove(){

        gameBlockLayout.removeView(tv);
        myBlock = null;
        myDir = null;
        return this;
    }

    // Merging Algorithm - checks multiple cases of where the numbers are
    public int merge_same_num(int occ) {
        int[] tempY = remove_zero();
        if (tempY.length==4) {
            if(tempY[0]==tempY[1]){
                occ--;
                if(tempY[2]==tempY[3]) {
                    occ--;
                    ifDouble=true;
                }
            } else if(tempY[1]==tempY[2]){
                occ--;
            } else if(tempY[2]==tempY[3]){
                occ--;
                ifDouble=true;
            }
        }
        if (tempY.length==3) {
            if(tempY[0]==tempY[1]){
                occ--;
            } else if(tempY[1]==tempY[2]){
                occ--;
                ifDouble=true;
            }
        }
        if (tempY.length==2) {
            if(tempY[0]==tempY[1]){
                occ--;
                ifDouble=true;
            }
        }
        return occ;
    }

    // Funtion to move the block with linear acceleration
    @RequiresApi(api = Build.VERSION_CODES.HONEYCOMB)
    public void move(){

        // offsets for nicer display
        if(count>10){
            OFFSET_X=120;
            if(count>100){
                OFFSET_X=100;
                tv.setTextSize(30);
            }
            if(count>1000){
                OFFSET_Y = 120;
                OFFSET_X=95;
                tv.setTextSize(20);
            }
        }
        tv.setText(String.format("%d", count));
        bringToFront();
        tv.bringToFront();

        // Switch case to move in each direction
        switch(myDir){
            case UP:
                if(myCoordY>targetY){
                    if((myCoordY-Velocity)<=targetY){
                        myCoordY = targetY;
                        Velocity = 0;
                        tv.setText(String.format("%d", count));
                    }
                    else{
                        myCoordY -= Velocity;
                        Velocity += acc;
                    }
                }
                break;
            case DOWN:
                if(myCoordY<targetY){
                    if((myCoordY+Velocity)>=targetY){
                        myCoordY = targetY;
                        Velocity = 0;
                        tv.setText(String.format("%d", count));
                    }
                    else{
                        myCoordY += Velocity;
                        Velocity += acc;
                    }
                }
                break;
            case LEFT:
                if(myCoordX>targetX){
                    if((myCoordX-Velocity)<=targetX){
                        myCoordX = targetX;
                        Velocity = 0;
                        tv.setText(String.format("%d", count));
                    }
                    else{
                        myCoordX -= Velocity;
                        Velocity += acc;
                    }
                }
                break;
            case RIGHT:
                if(myCoordX<targetX){
                    if((myCoordX+Velocity)>=targetX){
                        myCoordX = targetX;
                        Velocity = 0;
                        tv.setText(String.format("%d", count));
                    }
                    else{
                        myCoordX += Velocity;
                        Velocity += acc;
                    }
                }
                break;
            default:
                break;
        }
        setX(myCoordX);
        setY(myCoordY);
        tv.setX(myCoordX+OFFSET_X);
        tv.setY(myCoordY+OFFSET_Y);
    }

    // Function to set the destination of each block with the merging - unique for each direction thus the switch statement
    // Ex. If a block is able to merge with the adjacent block, it's destination also changes
    public void setDestination(GameLoopTask.direction setDir) {
        Log.d("IN set Destination", setDir.toString());
        myDir = setDir;
        int test;
        int occupiedNumber = 0;
        ifDouble = false;
        switch(setDir){
            case NO_MOVEMENT:
                break;
            case LEFT:
                test = GameLoopTask.LEFT_BOUNDARY;
                BlockNum = new int [(GameLoopTask.convertlocation(myCoordX)) + 1];
                BlockNum[GameLoopTask.convertlocation(myCoordX)] = count;
                while(test != myCoordX){
                    Log.d("block num", String.valueOf(BlockNum.length));
                    Log.d("block num1", String.valueOf(myBlock.num.length));
                    BlockNum[GameLoopTask.convertlocation(test)] =
                            myBlock.num[GameLoopTask.convertlocation(test)]
                                    [GameLoopTask.convertlocation(myCoordY)];
                    if(myBlock.isOccupied(test, myCoordY)){
                        occupiedNumber++;
                    }
                    if(test<GameLoopTask.RIGHT_BOUNDARY){
                        test += GameLoopTask.SLOT_ISOLATION;
                    }
                }
                occupiedNumber = merge_same_num(occupiedNumber);
                targetX = GameLoopTask.LEFT_BOUNDARY + occupiedNumber * GameLoopTask.SLOT_ISOLATION;
                targetY = myCoordY;
                Log.d("Game Block Report: ", String.format("Target X Coord: %d", targetX));
                break;

            case RIGHT:
                test = GameLoopTask.RIGHT_BOUNDARY;
                BlockNum = new int[4-(GameLoopTask.convertlocation(myCoordX))+1 ];
                BlockNum [4-(GameLoopTask.convertlocation(myCoordX))] = count;
                while(test != myCoordX){
                    Log.d("block num", String.valueOf(BlockNum.length));
                    Log.d("block num1", String.valueOf(myBlock.num.length));
                    BlockNum[4-(GameLoopTask.convertlocation(test))] =
                            myBlock.num[GameLoopTask.convertlocation(test)]
                                    [GameLoopTask.convertlocation(myCoordY)];

                    Log.d("Game Block Test Point", String.format("%d", test));
                    if(myBlock.isOccupied(test, myCoordY)){
                        occupiedNumber++;
                    }
                    if (test>GameLoopTask.LEFT_BOUNDARY) {
                        test -= GameLoopTask.SLOT_ISOLATION;
                    }
                }
                occupiedNumber = merge_same_num(occupiedNumber);

                targetX = GameLoopTask.RIGHT_BOUNDARY - occupiedNumber * GameLoopTask.SLOT_ISOLATION;
                targetY = myCoordY;
                Log.d("Game Block Report: ", String.format("Target X Coord: %d", targetX));
                break;
            case UP:
                test = GameLoopTask.UP_BOUNDARY;
                BlockNum = new int [(GameLoopTask.convertlocation(myCoordY))+1];
                BlockNum[GameLoopTask.convertlocation(myCoordY)] = count;
                while(test != myCoordY){
                    Log.d("block num", String.valueOf(BlockNum.length));
                    Log.d("block num1", String.valueOf(myBlock.num.length));
                    BlockNum[GameLoopTask.convertlocation(test)] =
                            myBlock.num[GameLoopTask.convertlocation(myCoordX)]
                                    [GameLoopTask.convertlocation(test)];

                    Log.d("Game Block Test Point", String.format("%d", test));
                    if(myBlock.isOccupied(myCoordX, test)){
                        occupiedNumber++;
                    }
                    if(test<GameLoopTask.DOWN_BOUNDARY){
                        test += GameLoopTask.SLOT_ISOLATION;
                    }
                }
                occupiedNumber = merge_same_num(occupiedNumber);
                targetY = GameLoopTask.UP_BOUNDARY + occupiedNumber * GameLoopTask.SLOT_ISOLATION;
                targetX = myCoordX;
                Log.d("Game Block Report: ", String.format("Target Y Coord: %d", targetY));
                break;
            case DOWN:
                test = GameLoopTask.DOWN_BOUNDARY;
                BlockNum = new int[4-(GameLoopTask.convertlocation(myCoordY)) +1];
                BlockNum [4-(GameLoopTask.convertlocation(myCoordY))] = count;
                while(test != myCoordY){
                    Log.d("block num", String.valueOf(BlockNum.length));
                    Log.d("block num1", String.valueOf(myBlock.num.length));
                    BlockNum[4-(GameLoopTask.convertlocation(test))] =
                            myBlock.num[GameLoopTask.convertlocation(myCoordX)]
                                    [GameLoopTask.convertlocation(test)];
                    Log.d("Game Block Test Point", String.format("%d", test));
                    if(myBlock.isOccupied(myCoordX, test)){
                        occupiedNumber++;
                    }
                    if (test>GameLoopTask.UP_BOUNDARY) {
                        test -= GameLoopTask.SLOT_ISOLATION;
                    }
                }
                occupiedNumber = merge_same_num(occupiedNumber);
                targetY = GameLoopTask.DOWN_BOUNDARY - occupiedNumber * GameLoopTask.SLOT_ISOLATION;
                targetX = myCoordX;
                Log.d("Game Block Report: ", String.format("Target Y Coord: %d", targetY));
                break;
            default:
                break;
        }
    }
}
