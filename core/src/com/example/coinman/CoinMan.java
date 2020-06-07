package com.example.coinman;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Sound;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.scenes.scene2d.Event;
import com.badlogic.gdx.scenes.scene2d.EventListener;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.Drawable;
import com.badlogic.gdx.scenes.scene2d.utils.TextureRegionDrawable;
import com.badlogic.gdx.utils.viewport.ScreenViewport;

import java.util.ArrayList;
import java.util.Random;

enum GameConstants{
	JUMPHEIGHT(20f), SPEED_COIN(7f),GRAVITY(0.5f), SPEED_BOMB(9f);
	private float floatValue;
	GameConstants(float v) {
		floatValue=v;
	}
	public float getValue(){
		return floatValue;
	}
}

public class CoinMan extends ApplicationAdapter {
	private SpriteBatch batch;
	private Texture background;
	//man
	private Texture[] man;
	private int manState=0;
	private Rectangle manRectangle;
	private int pause=0;
	private float velocity=0;
	private float manY=0;
	private Texture dizzyMan;

	private Random random;
	private int screenHeight=0;
	private int screenWidth=0;
	//bomb
	private ArrayList<Float> bombX=new ArrayList<>();
	private ArrayList<Float> bombY=new ArrayList<>();
	private ArrayList<Rectangle> bombRectangles=new ArrayList<>();
	private int bombCount=0;
	private Texture bomb;
	private Sound bombSound;
	//coin
	private ArrayList<Float> coinX=new ArrayList<>();
	private ArrayList<Float> coinY=new ArrayList<>();
	private ArrayList<Rectangle> coinRectangles=new ArrayList<>();
	private Texture coin;
	private int coinCount=0;
	//score
	private int score=0;
	private BitmapFont scoreFont;
	private Sound scoreSound;
	//GameState
	private int gameState=0;
	//Game Over
	private boolean gameOverFlag=false;
	private ImageButton playButton;
	private ImageButton.ImageButtonStyle playButtonStyle;
	private Texture playButtonImage;
	//Start Game Message
	private GlyphLayout startGameMessage;
	private BitmapFont startGameMessageFont;

	@Override
	public void create () {
		batch = new SpriteBatch();
		random=new Random();
		background=new Texture("bg.png");
		coin= new Texture("coin.png");
		bomb= new Texture("bomb.png");
		man=new Texture[4];
		man[0]=new Texture("frame-1.png");
		man[1]=new Texture("frame-2.png");
		man[2]=new Texture("frame-3.png");
		man[3]=new Texture("frame-4.png");
		dizzyMan=new Texture("dizzy-1.png");
		scoreFont=new BitmapFont();
		scoreFont.setColor(Color.WHITE);
		scoreFont.getData().setScale(8);
		screenHeight= (int) (Gdx.graphics.getHeight()-scoreFont.getCapHeight()-20);
		screenWidth=Gdx.graphics.getWidth();
		manY=screenHeight/2f- man[0].getHeight()/2f;

		startGameMessageFont =new BitmapFont();
		startGameMessageFont.setColor(Color.WHITE);
		startGameMessageFont.getData().setScale(5);
		startGameMessage=new GlyphLayout();
		startGameMessage.setText(startGameMessageFont,"Tap the screen to start");

		playButtonImage=new Texture("playButton.png");
		Drawable drawable=new TextureRegionDrawable(new TextureRegion(playButtonImage));
		playButton=new ImageButton(drawable);

		scoreSound=Gdx.audio.newSound(Gdx.files.internal("coinCollect.mp3"));
		bombSound=Gdx.audio.newSound(Gdx.files.internal("bombSound.mp3"));
	}

	public void makeCoin(){
		if(coinX.size()>0 && coinX.get(0)<0){
			coinX.remove(0);
			coinY.remove(0);
		}
		coinY.add(random.nextFloat() * (screenHeight-coin.getHeight()));
		coinX.add((float) screenWidth);
//		Gdx.app.log("coinsize",Integer.toString(coinX.size()));
	}

	public void makeBomb(){
		if(bombX.size()>0 && bombX.get(0)<0){
			bombX.remove(0);
			bombY.remove(0);
		}
		bombY.add(random.nextFloat() * (screenHeight-bomb.getHeight()));
		bombX.add((float) screenWidth);
//		Gdx.app.log("bombsize",Integer.toString(bombX.size()));
	}

	@Override
	public void render () {
		int pauseLimit = 6;
		batch.begin();
		batch.draw(background,0,0,screenWidth,Gdx.graphics.getHeight());

		if(gameState==1){
			//Game is live
			//Jump Up
			if(Gdx.input.justTouched()){
				velocity=-GameConstants.JUMPHEIGHT.getValue();
			}

			//Draw Bomb
			if(bombCount<180)
				bombCount++;
			else{
				bombCount=0;
				makeBomb();
			}
			bombRectangles.clear();
			for(int i=0;i<bombX.size();i++){
//			System.out.println("success");
				batch.draw(bomb,bombX.get(i),bombY.get(i));
				bombX.set(i,bombX.get(i)-GameConstants.SPEED_BOMB.getValue());
				bombRectangles.add(new Rectangle(bombX.get(i),bombY.get(i),bomb.getWidth(),bomb.getHeight()));
			}

			//Draw Coins
			if(coinCount<80)
				coinCount++;
			else{
				coinCount=0;
				makeCoin();
			}
			coinRectangles.clear();
			for(int i=0;i<coinX.size();i++){
//			System.out.println("success");
				batch.draw(coin,coinX.get(i),coinY.get(i));
				coinX.set(i,coinX.get(i)-GameConstants.SPEED_COIN.getValue());
				coinRectangles.add(new Rectangle(coinX.get(i),coinY.get(i),coin.getWidth(),coin.getHeight()));
			}

			//calculate velocity
			velocity+=GameConstants.GRAVITY.getValue();
			manY-=velocity;

			//Draw Man
			if(pause < pauseLimit)
				pause++;
			else {
				pause=0;
				if (manState >= 3)
					manState = 0;
				else
					manState++;
			}
			if(manY<=0){
				manY=0;
			}
			else if(manY >= screenHeight-man[manState].getHeight()){
				manY= screenHeight-man[manState].getHeight();
				velocity=0;
			}
		}
		else if(gameState==0){
			//Waiting to start the game
			if(Gdx.input.justTouched()){
				gameState=1;
			}

		}
		else if(gameState==2){
			//Game Over. Touch to Start again
			if(!gameOverFlag){
				Gdx.app.log("success",Boolean.toString(gameOverFlag));
				BitmapFont gameOverMessageFont=new BitmapFont();
				gameOverMessageFont.setColor(Color.FIREBRICK);
				gameOverMessageFont.getData().setScale(8);
				GlyphLayout gameOverMessage=new GlyphLayout(gameOverMessageFont,"Game Over");
				float gameOverHeight=screenHeight/2f+gameOverMessage.height+100;
				gameOverMessageFont.draw(batch,gameOverMessage,(screenWidth-gameOverMessage.width)/2f,gameOverHeight);
//				Sprite skin=new Sprite(playButtonImage);


			}
			if(Gdx.input.justTouched() && !gameOverFlag){
				gameState=1;
				//Reset score
				score=0;

				//Reset man
				manState=0;
				pause=0;
				manY=screenHeight/2f- man[0].getHeight()/2f;
				velocity=0;

				//Reset coin
				coinX.clear();
				coinY.clear();
				coinCount=0;
				coinRectangles.clear();

				//Reset bomb
				bombX.clear();
				bombY.clear();
				bombCount=0;
				bombRectangles.clear();
			}

		}

		if(gameState==2){
			//Drop dizzy man to ground
			velocity+=GameConstants.GRAVITY.getValue();
			manY-=velocity;
			if(manY<=0){
				manY=0;
				velocity=0;
				gameOverFlag=false;
			}
			Gdx.app.log("gameOver",Boolean.toString(gameOverFlag));
			batch.draw(dizzyMan, screenWidth / 2f - man[manState].getWidth() / 2f, manY);
		}
		else {
			batch.draw(man[manState], screenWidth / 2f - man[manState].getWidth() / 2f, manY);

			//Draw Message
			if(gameState==0){
				startGameMessageFont.draw(batch, startGameMessage,(screenWidth- startGameMessage.width)/2f,manY+man[manState].getHeight()+100);
			}

			manRectangle=new Rectangle(screenWidth/2f - man[manState].getWidth()/2f,manY,man[manState].getWidth(),man[manState].getHeight());

			//Check coin Collision
			for(int i=0;i<coinRectangles.size();i++){
				if(Intersector.overlaps(manRectangle,coinRectangles.get(i))){
					score+=5;
					Gdx.app.log("score = ",Integer.toString(score));
					coinRectangles.remove(i);
					coinX.remove(i);
					coinY.remove(i);
					scoreSound.play();
					break;
				}
			}

			//Check Bomb Collision
			for(int i=0;i<bombRectangles.size();i++){
				if(Intersector.overlaps(manRectangle,bombRectangles.get(i))){
					gameState=2;
					gameOverFlag=true;
					pause=0;
					bombSound.play();
				}
			}
		}

		//Draw Score
		scoreFont.draw(batch,Integer.toString(score),30,screenHeight+scoreFont.getCapHeight());
		batch.end();
	}

	@Override
	public void dispose () {
		batch.dispose();
		scoreSound.dispose();
		bombSound.dispose();
	}
}
