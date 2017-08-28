package com.tankgame;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Vector;

import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.SourceDataLine;


class PlayAudio extends Thread{
	
	//得到声音文件
	private String fileString;
	
	public PlayAudio(String file){
		fileString = file;
	}
	
	@Override
	public void run() {

		File soundFile = new File(fileString);
		/**
		 * 音频输入流AudioInputStream是具有指定音频格式和长度的输入流。长度用示例帧表示，不用字节表示
		 */
		AudioInputStream audioInputStream = null;
		try {
			
			/**
			 * AudioSystem 类包括许多操作 AudioInputStream 对象的方法
			 * 从外部音频文件、流或 URL 获得音频输入流 
			 */
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		//AudioFormat 是在声音流中指定特定数据安排的类
		//getFormat()是获得此音频输入流中声音数据的音频格式，返回一个AudioFormat.
		AudioFormat format = audioInputStream.getFormat();
		
		//源数据行（SourceDataLine）是可以写入数据的数据行。从应用程序的角度来看，源数据行可能充当音频数据的目标。
		SourceDataLine auLine = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
		
		try {
			//可以通过使用适当 DataLine.Info 对象调用 Mixer 的 getLine 方法从混频器获得源数据行。
			auLine = (SourceDataLine) AudioSystem.getLine(info);
			//打开具有指定格式的行，这样可使行获得所有所需的系统资源并变得可操作。
			auLine.open(format);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}
		//允许某一数据行执行数据 I/O
		auLine.start();
		
		int nBytesRead = 0;
		byte[] abData = new byte[1024];
		
		try {
			
			while(nBytesRead != -1){
				//从音频流读取指定的最大数量的数据字节，并将其放入给定的字节数组中。
				nBytesRead = audioInputStream.read(abData,0,abData.length);
				if(nBytesRead >= 0){
					//通过此源数据行将音频数据写入混频器。
					auLine.write(abData, 0, nBytesRead);
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}finally{
			auLine.drain();
			auLine.close();
		}
		
	}
}

//定义记录类，同时也可以保存信息
class Recorder{
	
	//初始的敌人坦克数
	private static final int initEnemyTanks = 10;
	
	//大砖块的个数
	private static int bigBrickNum = 1;

	//敌人坦克的个数
	private static int enNum = initEnemyTanks;
	//我的坦克的剩余生命数
	private static int myLife = 3;
	//我的坦克击爆的敌人坦克数
	private static int killEnTankNum = 0;
	
	
	private static FileWriter fw = null;
	private static BufferedWriter bw = null;
	private static FileReader fr = null;
	private static BufferedReader br = null;
	
	public static int getBigBrickNum() {
		return bigBrickNum;
	}

	public static void setBigBrickNum(int bigBrickNum) {
		Recorder.bigBrickNum = bigBrickNum;
	}
	
	public static int getInitenemytanks() {
		return initEnemyTanks;
	}
	
	public static int getKillEnTankNum() {
		return killEnTankNum;
	}
	public static void setKillEnTankNum(int killEnTankNum) {
		Recorder.killEnTankNum = killEnTankNum;
	}
	public static int getEnNum() {
		return enNum;
	}
	public static void setEnNum(int enNum) {
		Recorder.enNum = enNum;
	}
	public static int getMyLife() {
		return myLife;
	}
	public static void setMyLife(int myLife) {
		Recorder.myLife = myLife;
	}	
	
	public static void oneEnemyTankBomb(){
		enNum--;
		killEnTankNum++;
	}
	
	//保存玩家击毁的坦克数量
	public static void saveBombTanks(){
		
		try {
			fw = new FileWriter("D:\\eclipse\\workspace\\TankGame1\\data.txt");
			bw = new BufferedWriter(fw);
			bw.write(killEnTankNum + "\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//得到玩家打爆的坦克数
	public static void getBombTanks(){
				
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\data.txt");
			br = new BufferedReader(fr);
			String result = br.readLine();
			killEnTankNum = Integer.parseInt(result);
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	//保存数据
	public static void saveData(Hero hero,Vector<EnemyTank> ets){
				
		//存储我的坦克的位置
		try {
			fw = new FileWriter("D:\\eclipse\\workspace\\TankGame1\\heroLocation.txt");
			bw = new BufferedWriter(fw);
			bw.write(hero.getX()+"|"+hero.getY()+"|"+hero.getDirect()+"\r\n");
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//存储敌人坦克的位置
		try {
			fw = new FileWriter("D:\\eclipse\\workspace\\TankGame1\\enemyTankLocation.txt");
			bw = new BufferedWriter(fw);
			for(int i=0;i<ets.size();i++){
				bw.write(ets.get(i).getX()+"|"+ets.get(i).getY()+"|"+ets.get(i).getDirect()+"|"+ets.get(i).getType()+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//存储我的坦克子弹的位置和方向
		try {
			fw = new FileWriter("D:\\eclipse\\workspace\\TankGame1\\heroBulletLocation.txt");
			bw = new BufferedWriter(fw);
			for(int i=0;i<hero.bullets.size();i++){
				bw.write(hero.bullets.get(i).x+"|"+hero.bullets.get(i).y+"|"+hero.bullets.get(i).direct+"|"+hero.bullets.get(i).speed+"\r\n");
			}
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//存储敌人坦克子弹的位置和方向
		try {
			fw = new FileWriter("D:\\eclipse\\workspace\\TankGame1\\enemyTankBulletLocation.txt");
			bw = new BufferedWriter(fw);
			for(int j=0;j<ets.size();j++){
				for(int i=0;i<ets.get(j).bullets.size();i++){
					bw.write(ets.get(j).bullets.get(i).x+"|"+ets.get(j).bullets.get(i).y+"|"+ets.get(j).bullets.get(i).direct+"|"+ets.get(j).bullets.get(i).speed+"\r\n");
				}
			}
			
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			try {
				bw.close();
				fw.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
	}
	
	//获得数据
	public static void getData(Hero hero,Vector<EnemyTank> ets){
				
		//获得我的坦克的位置数据并设置
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\heroLocation.txt");
			br = new BufferedReader(fr);
			String heroLocation = br.readLine();
			String[] result = heroLocation.split("\\|");
			
			/**
			 * 如果坦克已经被打死了，需要让它复活
			 */
			if(hero.isLive == false){
				hero.isLive = true;
			}
			
			hero.setX(Integer.parseInt(result[0]));
			hero.setY(Integer.parseInt(result[1]));
			hero.setDirect(Integer.parseInt(result[2]));
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//获得敌人坦克的位置和方向
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\enemyTankLocation.txt");
			br = new BufferedReader(fr);
			String enemyTankLocation = null;
			
			//先把原先的向量清空，否则出现了鬼影哈哈
			ets.clear();
			while((enemyTankLocation=br.readLine()) != null){
				EnemyTank et = new EnemyTank(0, 0);
				String[] result = enemyTankLocation.split("\\|");
				et.setX(Integer.parseInt(result[0]));
				et.setY(Integer.parseInt(result[1]));
				et.setDirect(Integer.parseInt(result[2]));
				et.setType(Integer.parseInt(result[3]));
				
				//开启敌人坦克的线程，不然不会动
				Thread thread = new Thread(et);
				thread.start();
				
				ets.add(et);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		
		//获得我的坦克子弹的数据
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\heroBulletLocation.txt");
			br = new BufferedReader(fr);
			String heroBulletLocationString = "";
			String[] result = null;
			
			for(int i=0;i<hero.bullets.size();i++){
				hero.bullets.get(i).isLive = false;
			}
			
			//读取存储的子弹数据
			while((heroBulletLocationString=br.readLine()) != null){
				Bullet bullet = new Bullet(0, 0, 0);
				result = heroBulletLocationString.split("\\|");
				bullet.setX(Integer.parseInt(result[0]));
				bullet.setY(Integer.parseInt(result[1]));
				bullet.setDirect(Integer.parseInt(result[2]));
				bullet.setSpeed(Integer.parseInt(result[3]));
				hero.bullets.add(bullet);
				
				//记得要开启子弹的线程，不然子弹不会动
				Thread thread = new Thread(bullet);
				thread.start();
			}
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		//获得敌人坦克子弹的数据
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\enemyTankBulletLocation.txt");
			br = new BufferedReader(fr);
			String enemyTankBulletLocationString = "";
			String[] result = null;
			for(int i=0;i<ets.size();i++){
				EnemyTank et = ets.get(i);
				while((enemyTankBulletLocationString=br.readLine()) != null){
					Bullet bullet = new Bullet(0, 0, 0);
					result = enemyTankBulletLocationString.split("\\|");
					bullet.setX(Integer.parseInt(result[0]));
					bullet.setY(Integer.parseInt(result[1]));
					bullet.setDirect(Integer.parseInt(result[2]));
					bullet.setSpeed(Integer.parseInt(result[3]));
					et.bullets.add(bullet);
							
					//记得开启子弹的线程，不然子弹不会动
					Thread thread = new Thread(bullet);
					thread.start();
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}finally{
			try {
				br.close();
				fr.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	//暂停
	public static void suspend(Hero hero,Vector<EnemyTank> ets){
		
		hero.isCanFire = false;
		hero.isCanMove = false;
		hero.isChangeDirect = false;
		//设置我的坦克的子弹速度为0
		for(int i=0;i<hero.bullets.size();i++){
			hero.bullets.get(i).setSpeed(0);
		}
		
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			//以免其方向改变
			et.isChangeETankDirect = false;
			et.isCanAddBullet = false;
			et.setSpeed(0);
			for(int j=0;j<et.bullets.size();j++){
				et.bullets.get(j).setSpeed(0);
			}
		}
	}
	
	//继续游戏
	public static void continueGame(Hero hero,Vector<EnemyTank> ets){
		
		hero.isCanFire = true;
		hero.isCanMove = true;
		hero.isChangeDirect = true;
		//设置我的坦克的子弹速度为原来的速度
		for(int i=0;i<hero.bullets.size();i++){
			hero.bullets.get(i).setSpeed(3);
		}
		
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			//以免其方向改变
			et.isChangeETankDirect = true;
			et.isCanAddBullet = true;
			et.setSpeed(2);
			for(int j=0;j<et.bullets.size();j++){
				et.bullets.get(j).setSpeed(3);
			}
		}	
	}
	
}

//小块砖(6个小块砖组成大砖块)
class Brick {
	
	//坐标以及是否活着
	int x=0;
	int y=0;
	boolean isLive = true;
	
	//编号（方便查看是哪块砖被打 了）
	int num = 1;
	
	public int getNum() {
		return num;
	}

	public void setNum(int num) {
		this.num = num;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public Brick(int x,int y,int num){
		this.x=x;
		this.y=y;
		this.num=num;
	}
}

class BigBrick{
	
	int x;
	int y;
	
	//定义一块砖
	Brick brick = null;
	//定义一个向量装砖块
	Vector<Brick> bricks = new Vector<Brick>();
	
	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public BigBrick(int x,int y){
		
		this.x = x;
		this.y = y;
	
		/**
		 * 每new一块大砖就要new六块小砖
		 * 左边三块编号为123，右边的为456
		 */
		for(int i=0;i<3;i++){
			brick = new Brick(this.x, this.y+i*10,i+1);
			bricks.add(brick);
		}
		for(int i=0;i<3;i++){
			brick = new Brick(this.x+15, this.y+i*10,i+4);
			bricks.add(brick);
		}
	}
	
}

//定义一个炸弹类
class Bomb{
	
	int x = 0;
	int y = 0;
	
	//是否活着
	boolean isLive = true;
	
	//赋予炸弹一个生命值，方便三张图片的转换
	int life = 9;

	public int getX() {
		return x;
	}
	public void setX(int x) {
		this.x = x;
	}
	public int getY() {
		return y;
	}
	public void setY(int y) {
		this.y = y;
	}
	
	public Bomb(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	//生命值减小
	public void lifeDown() {
		life--;
	}
	
}

//子弹
class Bullet implements Runnable{
	
	int x = 0;
	int y = 0;
	int direct = 0;
	int speed = 3;
	//子弹是否活着
	boolean isLive = true;
	
	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}

	
	public Bullet(int x,int y,int direct){
		this.x = x;
		this.y = y;
		this.direct = direct;
	}

	@Override
	public void run() {
		
		while (true){
			
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			switch(direct){
			//向上
			case 0:
				y-=speed;
				break;
			//向下
			case 1:
				y+=speed;
				break;
			//向左
			case 2:
				x-=speed;
				break;
			//向右
			case 3:
				x+=speed;
				break;
			} 
			
			//判断子弹是否碰触到边缘
			if(x<0||x>800||y<0||y>600){
				this.isLive = false;
				break;
			}
			
		}
	}
}

//坦克类
class Tank{
	
	//坦克的x,y坐标
	int x = 0;
	int y = 0;
	
	//坦克的方向
	int direct = 0;
	
	//抽象出坦克的速度
	int speed = 2;
	
	//坦克的类型
	int Type = 0;
	
	//坦克是否死亡
	boolean isLive = true;
	
	public int getType() {
		return Type;
	}

	public void setType(int type) {
		Type = type;
	}

	public int getSpeed() {
		return speed;
	}

	public void setSpeed(int speed) {
		this.speed = speed;
	}

	public int getDirect() {
		return direct;
	}

	public void setDirect(int direct) {
		this.direct = direct;
	}

	public int getX() {
		return x;
	}

	public void setX(int x) {
		this.x = x;
	}

	public int getY() {
		return y;
	}

	public void setY(int y) {
		this.y = y;
	}
	
	public Tank(int x,int y){
		this.x = x;
		this.y = y;
	}
	
	//判断是否碰到砖块
		public boolean isTouchBigBrick(Vector<BigBrick> bigBricks){
			
			BigBrick bigBrick = null;
			Brick brick = null;
			
			for(int i=0;i<bigBricks.size();i++){
				bigBrick = bigBricks.get(i);
				for(int j=0;j<bigBrick.bricks.size();j++){
					brick = bigBrick.bricks.get(j);
					//坦克的四个方向
					if(this.direct==0){
						if((this.x>=brick.x&&this.x<=(brick.x+15)&&this.y==(brick.y+10)
						  || (this.x+20)>=brick.x&&(this.x+20)<=(brick.x+15)&&this.y==(brick.y+10))){
							return true;
						}
					}else if(this.direct==1){
						if((this.x+20)>=brick.x&&((this.x+20)<=(brick.x+15))&&(this.y+30)==brick.y || 
								this.x>=brick.x&&this.x<=(brick.x+15)&&(this.y+30)==brick.y){
							return true;
						}
					}else if(this.direct==2){
						if(this.y>=brick.y&&this.y<=(brick.y+10)&&this.x==(brick.x+15) || (this.y+20)>=brick.y&&(this.y+20)<=(brick.y+10)&&this.x==(brick.x+15)){
							return true;
						}
					}else if(this.direct==3){
						if(this.y>=brick.y&&this.y<=(brick.y+10)&&(this.x+30)==brick.x || (this.y+20)>=brick.y&&(this.y+20)<=(brick.y+10)&&(this.x+30)==brick.x){
							return true;
						}
					}
				}
			}
			return false;
			
		}

}

//我的坦克
class Hero extends Tank{
	
	//是否可以移动
	public boolean isCanMove = true;
	//是否可以开火
	public boolean isCanFire = true;
	//是否可以改变方向
	public boolean isChangeDirect = true;
	
	//子弹
	Bullet bullet = null;
	Vector<Bullet> bullets = new Vector<>();
	
	public Hero(int x,int y){
		super(x, y);
	}
	
	//我的坦克是否碰到敌人的坦克
		public boolean touchEnemyTank(Vector<EnemyTank> ets){
			
			for(int i=0;i<ets.size();i++){
				//得到敌人的坦克
				EnemyTank et = ets.get(i);
				switch(et.direct){
				//敌人坦克往上或往下
				case 0:
				case 1:
					//我的坦克的四个方向
					if(this.direct==0){
						if((this.x>=et.x&&this.x<=(et.x+20)&&this.y==(et.y+30)
						  || (this.x+20)>=et.x&&(this.x+20)<=(et.x+20)&&this.y==(et.y+30))){
							return true;
						}
					}else if(this.direct==1){
						if((this.x+20)>=et.x&&((this.x+20)<=(et.x+20))&&(this.y+30)==et.y || 
								this.x>=et.x&&this.x<=(et.x+20)&&(this.y+30)==et.y){
							return true;
						}
					}else if(this.direct==2){
						if(this.y>=et.y&&this.y<=(et.y+30)&&this.x==(et.x+20) || (this.y+20)>et.y&&(this.y+20)<=(et.y+30)&&this.x==(et.x+20)){
							return true;
						}
					}else if(this.direct==3){
						if(this.y>=et.y&&this.y<=(et.y+30)&&(this.x+30)==et.x || (this.y+20)>=et.y&&(this.y+20)<=(et.y+30)&&(this.x+30)==et.x){
							return true;
						}
					}
					break;
				//敌人坦克往左或往右
				case 2:
				case 3:
					if(this.direct==0){
						if((this.x>=et.x&&this.x<=(et.x+30)&&this.y==(et.y+20)
						  || (this.x+20)>=et.x&&(this.x+20)<=(et.x+30)&&this.y==(et.y+20))){
							return true;
						}
					}else if(this.direct==1){
						if((this.x+20)>=et.x&&((this.x+20)<=(et.x+30))&&(this.y+30)==et.y || 
								this.x>=et.x&&this.x<=(et.x+30)&&(this.y+30)==et.y){
							return true;
						}
					}else if(this.direct==2){
						if(this.y>=et.y&&this.y<=(et.y+20)&&this.x==(et.x+30) || (this.y+20)>et.y&&(this.y+20)<=(et.y+20)&&this.x==(et.x+30)){
							return true;
						}
					}else if(this.direct==3){
						if(this.y>=et.y&&this.y<=(et.y+20)&&(this.x+30)==et.x || (this.y+20)>=et.y&&(this.y+20)<=(et.y+20)&&(this.x+30)==et.x){
							return true;
						}
					}
					break;
				}
			}
			return false;
		}
	
	//坦克向上移动
	public void moveUp(){
		y -= speed;
	}
			
	//坦克向下移动
	public void moveDown(){
		y += speed;
	}
				
	//坦克向左移动
	public void moveLeft(){
		x -= speed;
	}
				
	//坦克向右移动
	public void movRight(){
		x += speed;
	}
	
	//开火
	public void fire() {
					
		switch(this.direct){
		case 0:
			bullet = new Bullet(x+10, y,0);
			bullets.add(bullet);
			break;
		case 1:
			bullet = new Bullet(x+10, y+30,1);
			bullets.add(bullet);
			break;
		case 2:
			bullet = new Bullet(x, y+10,2);
			bullets.add(bullet);
			break;
		case 3:
			bullet = new Bullet(x+30, y+10,3);
			bullets.add(bullet);
			break;
		}
		//开启子弹线程
		Thread thread = new Thread(bullet);
		thread.start();
			
	}
	
}

//敌人的坦克
class EnemyTank extends Tank implements Runnable{
	
	//用来控制是否改变敌人坦克的方向
	public boolean isChangeETankDirect = true;
	//是否可以添加子弹，如果暂停了就不添加子弹了
	public boolean isCanAddBullet = true;
	
	Vector<Bullet> bullets = new Vector<Bullet>();
	
	//将MyPanel中的BigBrick想办法传过来
	Vector<BigBrick> bigBricks = null;
	
	//想办法得到MyPanel中其它敌人的坦克
	Vector<EnemyTank> ets = new Vector<EnemyTank>();
	
	public EnemyTank(int x,int y){
		super(x,y);
	}
	
	public void giveOtherEnemyTank(Vector<EnemyTank> ve){
		this.ets = ve;
	}
	
	public void giveMyPanelBigBrick(Vector<BigBrick> vb){
		this.bigBricks = vb;
	}
	
	//是否碰到其他坦克
	public boolean touchTank(){
		
			for(int j=0;j<ets.size();j++){
				EnemyTank et2 = ets.get(j);
				
				switch(et2.direct){
				//敌人坦克往上或往下
				case 0:
				case 1:
					//这个坦克的四个方向
					if(this.direct==0){
						if((this.x>=et2.x&&this.x<=(et2.x+20)&&this.y==(et2.y+30)
						  || (this.x+20)>=et2.x&&(this.x+20)<=(et2.x+20)&&this.y==(et2.y+30))){
							return true;
						}
					}else if(this.direct==1){
						if((this.x+20)>=et2.x&&((this.x+20)<=(et2.x+20))&&(this.y+30)==et2.y || 
								this.x>=et2.x&&this.x<=(et2.x+20)&&(this.y+30)==et2.y){
							return true;  
						}
					}else if(this.direct==2){
						if(this.y>=et2.y&&this.y<=(et2.y+30)&&this.x==(et2.x+20) || (this.y+20)>et2.y&&(this.y+20)<=(et2.y+30)&&this.x==(et2.x+20)){
							return true;
						}
					}else if(this.direct==3){
						if(this.y>=et2.y&&this.y<=(et2.y+30)&&(this.x+30)==et2.x || (this.y+20)>=et2.y&&(this.y+20)<=(et2.y+30)&&(this.x+30)==et2.x){
							return true;
						}
					}
					break;
				//敌人坦克往左或往右
				case 2:
				case 3:
					if(this.direct==0){
						if((this.x>=et2.x&&this.x<=(et2.x+30)&&this.y==(et2.y+20)
						  || (this.x+20)>=et2.x&&(this.x+20)<=(et2.x+30)&&this.y==(et2.y+20))){
							return true;
						}
					}else if(this.direct==1){
						if((this.x+20)>=et2.x&&((this.x+20)<=(et2.x+30))&&(this.y+30)==et2.y || 
								this.x>=et2.x&&this.x<=(et2.x+30)&&(this.y+30)==et2.y){
							return true;
						}
					}else if(this.direct==2){
						if(this.y>=et2.y&&this.y<=(et2.y+20)&&this.x==(et2.x+30) || (this.y+20)>et2.y&&(this.y+20)<=(et2.y+20)&&this.x==(et2.x+30)){
							return true;
						}
					}else if(this.direct==3){
						if(this.y>=et2.y&&this.y<=(et2.y+20)&&(this.x+30)==et2.x || (this.y+20)>=et2.y&&(this.y+20)<=(et2.y+20)&&(this.x+30)==et2.x){
							return true;
						}
					}
					break;
					}
				}
			
		return false;
	}
	
	@Override
	public void run() {
		while(true){
			switch(direct){
			case 0:
				//说明此时坦克正向上移动，让其再走两步否则效果感觉不好
				for(int i=0;i<50;i++){
					if(y>0 && !touchTank() && !isTouchBigBrick(bigBricks)){
						y-=speed;
					}
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 1:
				for(int i=0;i<50;i++){
					if(y<575 && !touchTank() && !isTouchBigBrick(bigBricks)){
						y+=speed;
					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 2:
				for(int i=0;i<50;i++){
					if(x>0 && !touchTank() && !isTouchBigBrick(bigBricks)){
						x-=speed;
					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			case 3:
				for(int i=0;i<50;i++){
					if(x<760 && !touchTank() && !isTouchBigBrick(bigBricks)){
						x+=speed;
					}
					
					try {
						Thread.sleep(50);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				break;
			}
			
			/**
			 * 按下暂停时isChangeETankDirect会变成false，此时坦克方向不变
			 */
			if(this.isChangeETankDirect){
				//随机产生一个方向
				this.direct=(int)(Math.random()*4);
			}
			
			//判断坦克是否死亡
			if(this.isLive == false){
				//此时结束线程
				break;
			}
			
			//判断是否需要为敌人坦克添加新的子弹
			if(isLive && bullets.size()<5 && this.isCanAddBullet==true){
				
				Bullet bullet = null;
				switch(direct){
				case 0:
					//给敌人坦克添加一颗子弹
					bullet = new Bullet(x+10, y, direct);
					//将子弹加入Vector
					bullets.add(bullet);
					break;
				case 1:
					bullet = new Bullet(x+10, y+30, direct);
					bullets.add(bullet);
					break;
				case 2:
					bullet = new Bullet(x, y+10, direct);
					bullets.add(bullet);
					break;
				case 3:
					bullet = new Bullet(x+30, y+10, direct);
					bullets.add(bullet);
					break;
				}
				
			
			//启动子弹线程
			Thread thread = new Thread(bullet);
			thread.start();
				
			}
			
		}
	}
}




