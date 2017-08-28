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
	
	//�õ������ļ�
	private String fileString;
	
	public PlayAudio(String file){
		fileString = file;
	}
	
	@Override
	public void run() {

		File soundFile = new File(fileString);
		/**
		 * ��Ƶ������AudioInputStream�Ǿ���ָ����Ƶ��ʽ�ͳ��ȵ���������������ʾ��֡��ʾ�������ֽڱ�ʾ
		 */
		AudioInputStream audioInputStream = null;
		try {
			
			/**
			 * AudioSystem ����������� AudioInputStream ����ķ���
			 * ���ⲿ��Ƶ�ļ������� URL �����Ƶ������ 
			 */
			audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
		
		//AudioFormat ������������ָ���ض����ݰ��ŵ���
		//getFormat()�ǻ�ô���Ƶ���������������ݵ���Ƶ��ʽ������һ��AudioFormat.
		AudioFormat format = audioInputStream.getFormat();
		
		//Դ�����У�SourceDataLine���ǿ���д�����ݵ������С���Ӧ�ó���ĽǶ�������Դ�����п��ܳ䵱��Ƶ���ݵ�Ŀ�ꡣ
		SourceDataLine auLine = null;
		DataLine.Info info = new DataLine.Info(SourceDataLine.class,format);
		
		try {
			//����ͨ��ʹ���ʵ� DataLine.Info ������� Mixer �� getLine �����ӻ�Ƶ�����Դ�����С�
			auLine = (SourceDataLine) AudioSystem.getLine(info);
			//�򿪾���ָ����ʽ���У�������ʹ�л�����������ϵͳ��Դ����ÿɲ�����
			auLine.open(format);
		} catch (LineUnavailableException e) {
			e.printStackTrace();
			return;
		}
		//����ĳһ������ִ������ I/O
		auLine.start();
		
		int nBytesRead = 0;
		byte[] abData = new byte[1024];
		
		try {
			
			while(nBytesRead != -1){
				//����Ƶ����ȡָ������������������ֽڣ����������������ֽ������С�
				nBytesRead = audioInputStream.read(abData,0,abData.length);
				if(nBytesRead >= 0){
					//ͨ����Դ�����н���Ƶ����д���Ƶ����
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

//�����¼�࣬ͬʱҲ���Ա�����Ϣ
class Recorder{
	
	//��ʼ�ĵ���̹����
	private static final int initEnemyTanks = 10;
	
	//��ש��ĸ���
	private static int bigBrickNum = 1;

	//����̹�˵ĸ���
	private static int enNum = initEnemyTanks;
	//�ҵ�̹�˵�ʣ��������
	private static int myLife = 3;
	//�ҵ�̹�˻����ĵ���̹����
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
	
	//������һ��ٵ�̹������
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
	
	//�õ���Ҵ򱬵�̹����
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
	
	//��������
	public static void saveData(Hero hero,Vector<EnemyTank> ets){
				
		//�洢�ҵ�̹�˵�λ��
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
		
		//�洢����̹�˵�λ��
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
		
		//�洢�ҵ�̹���ӵ���λ�úͷ���
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
		
		//�洢����̹���ӵ���λ�úͷ���
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
	
	//�������
	public static void getData(Hero hero,Vector<EnemyTank> ets){
				
		//����ҵ�̹�˵�λ�����ݲ�����
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\heroLocation.txt");
			br = new BufferedReader(fr);
			String heroLocation = br.readLine();
			String[] result = heroLocation.split("\\|");
			
			/**
			 * ���̹���Ѿ��������ˣ���Ҫ��������
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
		
		//��õ���̹�˵�λ�úͷ���
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\enemyTankLocation.txt");
			br = new BufferedReader(fr);
			String enemyTankLocation = null;
			
			//�Ȱ�ԭ�ȵ�������գ���������˹�Ӱ����
			ets.clear();
			while((enemyTankLocation=br.readLine()) != null){
				EnemyTank et = new EnemyTank(0, 0);
				String[] result = enemyTankLocation.split("\\|");
				et.setX(Integer.parseInt(result[0]));
				et.setY(Integer.parseInt(result[1]));
				et.setDirect(Integer.parseInt(result[2]));
				et.setType(Integer.parseInt(result[3]));
				
				//��������̹�˵��̣߳���Ȼ���ᶯ
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
		
		
		//����ҵ�̹���ӵ�������
		try {
			fr = new FileReader("D:\\eclipse\\workspace\\TankGame1\\heroBulletLocation.txt");
			br = new BufferedReader(fr);
			String heroBulletLocationString = "";
			String[] result = null;
			
			for(int i=0;i<hero.bullets.size();i++){
				hero.bullets.get(i).isLive = false;
			}
			
			//��ȡ�洢���ӵ�����
			while((heroBulletLocationString=br.readLine()) != null){
				Bullet bullet = new Bullet(0, 0, 0);
				result = heroBulletLocationString.split("\\|");
				bullet.setX(Integer.parseInt(result[0]));
				bullet.setY(Integer.parseInt(result[1]));
				bullet.setDirect(Integer.parseInt(result[2]));
				bullet.setSpeed(Integer.parseInt(result[3]));
				hero.bullets.add(bullet);
				
				//�ǵ�Ҫ�����ӵ����̣߳���Ȼ�ӵ����ᶯ
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
		
		//��õ���̹���ӵ�������
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
							
					//�ǵÿ����ӵ����̣߳���Ȼ�ӵ����ᶯ
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
	
	//��ͣ
	public static void suspend(Hero hero,Vector<EnemyTank> ets){
		
		hero.isCanFire = false;
		hero.isCanMove = false;
		hero.isChangeDirect = false;
		//�����ҵ�̹�˵��ӵ��ٶ�Ϊ0
		for(int i=0;i<hero.bullets.size();i++){
			hero.bullets.get(i).setSpeed(0);
		}
		
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			//�����䷽��ı�
			et.isChangeETankDirect = false;
			et.isCanAddBullet = false;
			et.setSpeed(0);
			for(int j=0;j<et.bullets.size();j++){
				et.bullets.get(j).setSpeed(0);
			}
		}
	}
	
	//������Ϸ
	public static void continueGame(Hero hero,Vector<EnemyTank> ets){
		
		hero.isCanFire = true;
		hero.isCanMove = true;
		hero.isChangeDirect = true;
		//�����ҵ�̹�˵��ӵ��ٶ�Ϊԭ�����ٶ�
		for(int i=0;i<hero.bullets.size();i++){
			hero.bullets.get(i).setSpeed(3);
		}
		
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			//�����䷽��ı�
			et.isChangeETankDirect = true;
			et.isCanAddBullet = true;
			et.setSpeed(2);
			for(int j=0;j<et.bullets.size();j++){
				et.bullets.get(j).setSpeed(3);
			}
		}	
	}
	
}

//С��ש(6��С��ש��ɴ�ש��)
class Brick {
	
	//�����Լ��Ƿ����
	int x=0;
	int y=0;
	boolean isLive = true;
	
	//��ţ�����鿴���Ŀ�ש���� �ˣ�
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
	
	//����һ��ש
	Brick brick = null;
	//����һ������װש��
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
		 * ÿnewһ���ש��Ҫnew����Сש
		 * ���������Ϊ123���ұߵ�Ϊ456
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

//����һ��ը����
class Bomb{
	
	int x = 0;
	int y = 0;
	
	//�Ƿ����
	boolean isLive = true;
	
	//����ը��һ������ֵ����������ͼƬ��ת��
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
	
	//����ֵ��С
	public void lifeDown() {
		life--;
	}
	
}

//�ӵ�
class Bullet implements Runnable{
	
	int x = 0;
	int y = 0;
	int direct = 0;
	int speed = 3;
	//�ӵ��Ƿ����
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
			//����
			case 0:
				y-=speed;
				break;
			//����
			case 1:
				y+=speed;
				break;
			//����
			case 2:
				x-=speed;
				break;
			//����
			case 3:
				x+=speed;
				break;
			} 
			
			//�ж��ӵ��Ƿ���������Ե
			if(x<0||x>800||y<0||y>600){
				this.isLive = false;
				break;
			}
			
		}
	}
}

//̹����
class Tank{
	
	//̹�˵�x,y����
	int x = 0;
	int y = 0;
	
	//̹�˵ķ���
	int direct = 0;
	
	//�����̹�˵��ٶ�
	int speed = 2;
	
	//̹�˵�����
	int Type = 0;
	
	//̹���Ƿ�����
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
	
	//�ж��Ƿ�����ש��
		public boolean isTouchBigBrick(Vector<BigBrick> bigBricks){
			
			BigBrick bigBrick = null;
			Brick brick = null;
			
			for(int i=0;i<bigBricks.size();i++){
				bigBrick = bigBricks.get(i);
				for(int j=0;j<bigBrick.bricks.size();j++){
					brick = bigBrick.bricks.get(j);
					//̹�˵��ĸ�����
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

//�ҵ�̹��
class Hero extends Tank{
	
	//�Ƿ�����ƶ�
	public boolean isCanMove = true;
	//�Ƿ���Կ���
	public boolean isCanFire = true;
	//�Ƿ���Ըı䷽��
	public boolean isChangeDirect = true;
	
	//�ӵ�
	Bullet bullet = null;
	Vector<Bullet> bullets = new Vector<>();
	
	public Hero(int x,int y){
		super(x, y);
	}
	
	//�ҵ�̹���Ƿ��������˵�̹��
		public boolean touchEnemyTank(Vector<EnemyTank> ets){
			
			for(int i=0;i<ets.size();i++){
				//�õ����˵�̹��
				EnemyTank et = ets.get(i);
				switch(et.direct){
				//����̹�����ϻ�����
				case 0:
				case 1:
					//�ҵ�̹�˵��ĸ�����
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
				//����̹�����������
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
	
	//̹�������ƶ�
	public void moveUp(){
		y -= speed;
	}
			
	//̹�������ƶ�
	public void moveDown(){
		y += speed;
	}
				
	//̹�������ƶ�
	public void moveLeft(){
		x -= speed;
	}
				
	//̹�������ƶ�
	public void movRight(){
		x += speed;
	}
	
	//����
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
		//�����ӵ��߳�
		Thread thread = new Thread(bullet);
		thread.start();
			
	}
	
}

//���˵�̹��
class EnemyTank extends Tank implements Runnable{
	
	//���������Ƿ�ı����̹�˵ķ���
	public boolean isChangeETankDirect = true;
	//�Ƿ��������ӵ��������ͣ�˾Ͳ�����ӵ���
	public boolean isCanAddBullet = true;
	
	Vector<Bullet> bullets = new Vector<Bullet>();
	
	//��MyPanel�е�BigBrick��취������
	Vector<BigBrick> bigBricks = null;
	
	//��취�õ�MyPanel���������˵�̹��
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
	
	//�Ƿ���������̹��
	public boolean touchTank(){
		
			for(int j=0;j<ets.size();j++){
				EnemyTank et2 = ets.get(j);
				
				switch(et2.direct){
				//����̹�����ϻ�����
				case 0:
				case 1:
					//���̹�˵��ĸ�����
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
				//����̹�����������
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
				//˵����ʱ̹���������ƶ�������������������Ч���о�����
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
			 * ������ͣʱisChangeETankDirect����false����ʱ̹�˷��򲻱�
			 */
			if(this.isChangeETankDirect){
				//�������һ������
				this.direct=(int)(Math.random()*4);
			}
			
			//�ж�̹���Ƿ�����
			if(this.isLive == false){
				//��ʱ�����߳�
				break;
			}
			
			//�ж��Ƿ���ҪΪ����̹������µ��ӵ�
			if(isLive && bullets.size()<5 && this.isCanAddBullet==true){
				
				Bullet bullet = null;
				switch(direct){
				case 0:
					//������̹�����һ���ӵ�
					bullet = new Bullet(x+10, y, direct);
					//���ӵ�����Vector
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
				
			
			//�����ӵ��߳�
			Thread thread = new Thread(bullet);
			thread.start();
				
			}
			
		}
	}
}




