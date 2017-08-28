package com.tankgame;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.Vector;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;

@SuppressWarnings("serial")
public class TankGame1 extends JFrame implements ActionListener{
	
	MyPanel myPanel = null;
	MyStartPanel myStartPanel = null;
	
	//����˵���
	JMenuBar jmb = null;
	JMenu jm = null;
	JMenuItem item1 = null;
	JMenuItem item2 = null;
	JMenuItem item3 = null;
	JMenuItem item4 = null;
	JMenuItem item5 = null;
	JMenuItem item6 = null;
	
	public static void main(String[] args) {
		new TankGame1();
	}
	
	public TankGame1(){
		
		//�˵���
		jmb = new JMenuBar();
		jm = new JMenu("Game(G)");
		//���ÿ�ݷ�ʽ
		jm.setMnemonic('G');
		item1 = new JMenuItem("New(N)");
		item1.setMnemonic('N');
		item2 = new JMenuItem("Save&Quit");
		item2.setMnemonic('Q');
		item3 = new JMenuItem("Save(S)");
		item3.setMnemonic('S');
		item4 = new JMenuItem("Reinstate(R)");
		item4.setMnemonic('R');
		item5 = new JMenuItem("Suspend(T)");
		item5.setMnemonic('T');
		item6 = new JMenuItem("Continue(C)");
		item6.setMnemonic('C');
		jmb.add(jm);
		jm.add(item1);
		jm.add(item5);
		jm.add(item6);
		jm.add(item3);
		jm.add(item4);
		jm.add(item2);
		item1.addActionListener(this);
		item1.setActionCommand("new");
		item2.addActionListener(this);
		item2.setActionCommand("Save&Quit");
		item3.addActionListener(this);
		item3.setActionCommand("Save");
		item4.addActionListener(this);
		item4.setActionCommand("Reinstate");
		item5.addActionListener(this);
		item5.setActionCommand("Suspend");
		item6.addActionListener(this);
		item6.setActionCommand("Continue");
		
		myStartPanel = new MyStartPanel();
		//��˸Ч��
		Thread thread = new Thread(myStartPanel);
		thread.start();
		
		this.setJMenuBar(jmb);
		this.add(myStartPanel);
		this.setSize(950, 760);
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		this.setVisible(true);
		
	}

	@Override
	public void actionPerformed(ActionEvent arg0) {
		
		switch(arg0.getActionCommand()){
		case "new":
			
			//���ſ�ʼս���������ļ�
			PlayAudio playAudio = new PlayAudio("D:\\eclipse\\workspace\\TankGame1\\start.wav");
			playAudio.start();
			
			//�������Ϸ�����е��New�򲻱��汾���������¿�ʼ
			if(myPanel != null){
				Thread.currentThread().interrupt();
				this.remove(myPanel);
			}
			
			/**
			 * ��new���µ�myPanel֮ǰ������̹��������Ϊ��ʼֵ����Ϸ�����е��new�Żử��ȫ��̹��
			 */
			Recorder.setEnNum(Recorder.getInitenemytanks());
			
			myPanel = new MyPanel();

			Thread thread1 = new Thread(myPanel);
			thread1.start();
			//�����µ�֮ǰɾ���ɵ� 
			this.remove(myStartPanel);
			this.add(myPanel);	
			//��ʾˢ��
			this.setVisible(true);
			this.addKeyListener(myPanel);
			break;
			
		case "Suspend":
			Recorder.suspend(myPanel.hero,myPanel.ets);
			break;
			
		case "Continue":
			Recorder.continueGame(myPanel.hero, myPanel.ets);
			break;
			
		case "Save":
			Recorder.saveBombTanks();
			Recorder.saveData(myPanel.hero,myPanel.ets);
			break;
			
		case "Reinstate":
			Recorder.getData(myPanel.hero,myPanel.ets);
			break;
			
		case "Save&Quit":
			try {
				//�������ݲ��˳�
				Recorder.saveBombTanks();
				Recorder.saveData(myPanel.hero,myPanel.ets);
				System.exit(0);
			} catch (Exception e) {
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
		
		
		
	}

}

@SuppressWarnings("serial")
class MyStartPanel extends JPanel implements Runnable{
	
	//����ʲôʱ��ʲôʱ�򲻻���������˸Ч��
	int times = 0;
			
	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		g.fillRect(0, 0, 800, 600);
		
		if(times%2 == 0)
		{
			g.setColor(Color.red);
			Font myFont = new Font("������κ", Font.BOLD, 30);
			g.setFont(myFont);
			g.drawString("Level : 1", 350, 300);
		}		
		
	}

	@Override
	public void run() {
		
		while(true){
			try {
				Thread.sleep(500);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			times++;
			this.repaint();
		}
		
	}
}

@SuppressWarnings("serial")
class MyPanel extends JPanel implements KeyListener,Runnable{
	
	//����һ���ҵ�̹��
	Hero hero = null;
	
	//����һ�����˵�̹����
	Vector<EnemyTank> ets = new Vector<EnemyTank>();
	//����̹������
	int etNumber = Recorder.getEnNum();
	
	//����һ��ը�����ϣ���Ϊͬһ˲���п��ܲ�ֹһ��ը����ը
	Vector<Bomb> bombs = new Vector<Bomb>();
	//���ñ�ը��ͼƬ��������ʾ��Ϊ����̹�˵�Ч��
	Vector<Bomb> birth = new Vector<Bomb>();
	
	//��������ͼƬ,����ͼƬ���л����һ��ը��
	Image image1 = null;
	Image image2 = null;
	Image image3 = null;
	
	//����һ���ש
	BigBrick bigBrick = null;
	Vector<BigBrick> bigBricks = new Vector<BigBrick>();
	
	public MyPanel(){
		
		//�ָ���¼
		Recorder.getBombTanks();
		
		//��ʼ���ҵ�̹����
		hero = new Hero(400,570);
	
		//��ʼ����һ�ص�ͼ
		firstMap();
		
		//��ʼ������̹��
		for(int i=0;i<etNumber;i++){
			EnemyTank et = new EnemyTank((i+1)*70,0);
			//����̹����ɫ
			et.setType(1);
			et.setDirect(1);
			
			//����������̹��ʱ�����õ�����̹��
			et.giveOtherEnemyTank(ets);
			et.giveMyPanelBigBrick(bigBricks);
			
			//��������̹�˵��߳�
			Thread thread = new Thread(et);
			thread.start();
			
			//������̹�����һ���ӵ�
			Bullet bullet = new Bullet(et.getX()+10, et.getY()+30, 1);
			//���ӵ�����Vector
			et.bullets.add(bullet);
			//�����ӵ��߳�
			Thread thread2 = new Thread(bullet);
			thread2.start();
			
			ets.add(et);
		}
		
		//��ʼ������ͼƬ
		//���������ʼ��ͼƬ��ô��һ����ըЧ��������
//		image1=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
//		image2=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
//		image3=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));
	
		try {
			//�����õ���ͼƬЧ������
			image1=ImageIO.read(new File("D:\\eclipse\\workspace\\TankGame1\\src\\bomb_1.gif"));
			image2=ImageIO.read(new File("D:\\eclipse\\workspace\\TankGame1\\src\\bomb_2.gif"));
			image3=ImageIO.read(new File("D:\\eclipse\\workspace\\TankGame1\\src\\bomb_3.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * ��һ�ص�ͼ
	 */
	public void firstMap(){
		
		//��ʼ����ש��		
		bigBrick = new BigBrick(400, 100);
		bigBricks.add(bigBrick);
		for(int i=0;i<4;i++){
			bigBrick = new BigBrick(400, 280+i*30);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(370+i*60, 130);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(340+i*120, 160);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(310+i*180, 190);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(280+i*240, 220);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(390+i*30, 190);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<4;i++){
			bigBrick = new BigBrick(360+i*30, 250);
			bigBricks.add(bigBrick);
		}
		bigBrick = new BigBrick(370, 370);
		bigBricks.add(bigBrick);
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(330+i*150, 310);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(300+i*210, 340);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<5;i++){
			bigBrick = new BigBrick(100, 400+i*30);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(130, 400+i*60);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<3;i++){
			bigBrick = new BigBrick(160, 400+i*30);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<5;i++){
			bigBrick = new BigBrick(600, 400+i*30);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<2;i++){
			bigBrick = new BigBrick(630, 400+i*60);
			bigBricks.add(bigBrick);
		}
		for(int i=0;i<3;i++){
			bigBrick = new BigBrick(660, 400+i*30);
			bigBricks.add(bigBrick);
		}
		
	}
	
	//��̹��
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 800, 600);
		
		//������ʾ��̹���Լ����ǵ�������
		showTankLife(g);
		
		if(hero.isLive){
			//�����Լ���̹��
			this.drawTank(hero.getX(), hero.getY(), g, hero.direct, hero.Type); 
		}else{
			if(Recorder.getMyLife() != 0){
				hero = new Hero(400, 570);
				//����һ��ը�����ҷ���Vector�������������ҵ�̹�˳���ʱ��Ч��
				Bomb bomb = new Bomb(hero.getX(), hero.getY());
				birth.add(bomb);
			}
		}
		
		//��ש��
		for(int i=0;i<bigBricks.size();i++){
			
			BigBrick bigBrick = bigBricks.get(i);
			Brick brick = null;
			
			for(int j=0;j<bigBrick.bricks.size();j++){
				brick = bigBrick.bricks.get(j);
				
				if(brick.isLive == false){
					bigBrick.bricks.remove(brick);
				}
			}
			
			this.drawBigBrick(bigBrick.getX(),bigBrick.getY(),g,bigBrick);
			
		}
	
		//��������̹��
		//����̹�˲��ϱ仯������ets.size()����ʾ
		//��������̹�˵�ʱ�򻭵��˵��ӵ����ӵ����ڵ���̹��
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			if(et.isLive){
				this.drawTank(et.getX(), et.getY(), g, et.getDirect(), et.getType());
				
				for(int j=0;j<et.bullets.size();j++){
					
					Bullet b = et.bullets.get(j);
					
					if(b.isLive){
						g.draw3DRect(b.getX(), b.getY(), 1, 1, false);
					}else{
						//����ӵ���ʧ�˾ͽ�����Vector���Ƴ�
						et.bullets.remove(b);
					}
				}
			}
		}
		
		//������ըʱը��
		for(int i=0;i<bombs.size();i++){
			//ȡ���ӵ�
			Bomb bomb = bombs.get(i);
			
			if(bomb.life>6){
				g.drawImage(image1, bomb.getX(), bomb.getY(), 30, 30,this);
			}else if(bomb.life>3){
				g.drawImage(image2, bomb.getX(), bomb.getY(), 30, 30,this);
			}else {
				g.drawImage(image3, bomb.getX(), bomb.getY(), 30, 30,this);
			}
			
			//��ը������ֵ��С
			bomb.lifeDown();
			
			if(bomb.life == 0){
				bombs.remove(bomb);
			}
		}
		
		//���ñ�ը��ͼƬ��������ʾ��Ϊ����̹�˵�Ч��
		for(int i=0;i<birth.size();i++){
			//ȡ���ӵ�
			Bomb bomb = birth.get(i);
			
			if(bomb.life>6){
				g.drawImage(image3, bomb.getX(), bomb.getY(), 30, 30,this);
			}else if(bomb.life>3){
				g.drawImage(image2, bomb.getX(), bomb.getY(), 30, 30,this);
			}else {
				g.drawImage(image1, bomb.getX(), bomb.getY(), 30, 30,this);
			}
			
			//��ը������ֵ��С
			bomb.lifeDown();
			
			if(bomb.life == 0){
				birth.remove(bomb);
			}
			
		}
		
		//���ҵ��ӵ�
		for(int i=0;i<hero.bullets.size();i++){
			
			Bullet b = hero.bullets.get(i);
			
			if(b!=null && b.isLive==true){
				g.draw3DRect(b.getX(), b.getY(), 1, 1, false);
			}
			//����ӵ���ʧ�˾ͽ�����Vector���Ƴ�
			if(b.isLive==false){
				hero.bullets.remove(b);
			}
		}
		
	}
	
	//������ʾ��̹���Լ����ǵ�������
	public void showTankLife(Graphics g){
		
		//����������ʾ��̹��
		this.drawTank(100, 620, g, 0, 1);
		g.setColor(Color.black);
		g.setFont(new Font("������κ", Font.ITALIC, 20));
		g.drawString("  X  " + Recorder.getEnNum(), 125, 640);
		this.drawTank(300, 620, g, 0, 0);
		g.setColor(Color.black);
		g.drawString("  X  " + Recorder.getMyLife(), 325, 640);
		
		//������ҳɼ�
		g.setColor(Color.red);
		g.setFont(new Font("������κ", Font.ITALIC, 20));
		g.drawString("kill :", 820, 30);
		g.drawString(Recorder.getKillEnTankNum()+"", 870, 30);
		
		g.setColor(Color.red);
		g.setFont(new Font("������κ", Font.ITALIC, 20));
		g.drawString("scores :", 820, 60);
		g.drawString(Recorder.getKillEnTankNum()+"", 900, 60);
		
	}
	
	//����ש��
	public void drawBigBrick(int x,int y,Graphics g,BigBrick bigBrick){
		
		g.setColor(Color.orange);
		Brick brick = null;
		for(int i=0;i<bigBrick.bricks.size();i++){
			brick = bigBrick.bricks.get(i);
			
			/**
			 * ͨ������жϴ��е����Ŀ�ש
			 */
			if(brick.num==1 && brick.isLive){
				g.fill3DRect(x, y, 15, 10, false);
			}
			if(brick.num==2 && brick.isLive){
				g.fill3DRect(x, y+10, 15, 10, false);
			}
			if(brick.num==3 && brick.isLive){
				g.fill3DRect(x, y+20, 15, 10, false);
			}
			if(brick.num==4 && brick.isLive){
				g.fill3DRect(x+15, y, 15, 10, false);
			}
			if(brick.num==5 && brick.isLive){
				g.fill3DRect(x+15, y+10, 15, 10, false);
			}
			if(brick.num==6 && brick.isLive){
				g.fill3DRect(x+15, y+20, 15, 10, false);
			}
		}
		
	}
	
	//��̹�˵ķ���
	public void drawTank(int x,int y,Graphics g,int direct,int type){
		
		//�ж���ʲô���͵�̹��
		switch(type){
		case 0:
			g.setColor(Color.red);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		}
		
		//�жϷ���
		switch(	direct){
		//����
		case 0:
			//����ľ���
			g.fill3DRect(x, y, 5, 30,false);
			//������ľ���
			g.fill3DRect(x+15, y, 5, 30,false);
			//�����м�ľ���
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//�����м��Բ��
			g.fillOval(x+4, y+9, 10, 10);
			//�����м�����
			g.drawLine(x+10, y+10, x+10, y);
			break;
		//����
		case 1:
			//����ľ���
			g.fill3DRect(x, y, 5, 30,false);
			//������ľ���
			g.fill3DRect(x+15, y, 5, 30,false);
			//�����м�ľ���
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//�����м��Բ��
			g.fillOval(x+4, y+9, 10, 10);
			//�����м�����
			g.drawLine(x+10, y+10, x+10, y+30);
			break;
		//����
		case 2:
			//����ľ���
			g.fill3DRect(x, y, 30, 5,false);
			//�±ߵľ���
			g.fill3DRect(x, y+15, 30, 5,false);
			//�����м�ľ���
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//�����м��Բ��
			g.fillOval(x+9, y+4, 10, 10);
			//�����м����
			g.drawLine(x+10, y+10, x, y+10);
			break;
		//����
		case 3:
			//����ľ���
			g.fill3DRect(x, y, 30, 5,false);
			//�±ߵľ���
			g.fill3DRect(x, y+15, 30, 5,false);
			//�����м�ľ���
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//�����м��Բ��
			g.fillOval(x+9, y+4, 10, 10);
			//�����м����
			g.drawLine(x+10, y+10, x+30, y+10);
			break;
		}
	
	}
	
//	//�ҵ�̹���Ƿ��������˵�̹��
//	public boolean touchEnemyTank(){
//		
//		for(int i=0;i<ets.size();i++){
//			//�õ����˵�̹��
//			EnemyTank et = ets.get(i);
//			switch(et.direct){
//			//����̹�����ϻ�����
//			case 0:
//			case 1:
//				//�ҵ�̹�˵��ĸ�����
//				if(hero.direct==0){
//					if((hero.x>=et.x&&hero.x<=(et.x+20)&&hero.y==(et.y+30)
//					  || (hero.x+20)>=et.x&&(hero.x+20)<=(et.x+20)&&hero.y==(et.y+30))){
//						return true;
//					}
//				}else if(hero.direct==1){
//					if((hero.x+20)>=et.x&&((hero.x+20)<=(et.x+20))&&(hero.y+30)==et.y || 
//							hero.x>=et.x&&hero.x<=(et.x+20)&&(hero.y+30)==et.y){
//						return true;
//					}
//				}else if(hero.direct==2){
//					if(hero.y>=et.y&&hero.y<=(et.y+30)&&hero.x==(et.x+20) || (hero.y+20)>et.y&&(hero.y+20)<=(et.y+30)&&hero.x==(et.x+20)){
//						return true;
//					}
//				}else if(hero.direct==3){
//					if(hero.y>=et.y&&hero.y<=(et.y+30)&&(hero.x+30)==et.x || (hero.y+20)>=et.y&&(hero.y+20)<=(et.y+30)&&(hero.x+30)==et.x){
//						return true;
//					}
//				}
//				break;
//			//����̹�����������
//			case 2:
//			case 3:
//				if(hero.direct==0){
//					if((hero.x>=et.x&&hero.x<=(et.x+30)&&hero.y==(et.y+20)
//					  || (hero.x+20)>=et.x&&(hero.x+20)<=(et.x+30)&&hero.y==(et.y+20))){
//						return true;
//					}
//				}else if(hero.direct==1){
//					if((hero.x+20)>=et.x&&((hero.x+20)<=(et.x+30))&&(hero.y+30)==et.y || 
//							hero.x>=et.x&&hero.x<=(et.x+30)&&(hero.y+30)==et.y){
//						return true;
//					}
//				}else if(hero.direct==2){
//					if(hero.y>=et.y&&hero.y<=(et.y+20)&&hero.x==(et.x+30) || (hero.y+20)>et.y&&(hero.y+20)<=(et.y+20)&&hero.x==(et.x+30)){
//						return true;
//					}
//				}else if(hero.direct==3){
//					if(hero.y>=et.y&&hero.y<=(et.y+20)&&(hero.x+30)==et.x || (hero.y+20)>=et.y&&(hero.y+20)<=(et.y+20)&&(hero.x+30)==et.x){
//						return true;
//					}
//				}
//				break;
//			}
//		}
//		return false;
//	}
	
	//����̹�˼��Ƿ��໥����
	//���������EnemtTank���бȽϺã�������ڵ���̹�˵�����
//		public static boolean touchTank(){
//			
//			for(int i=0;i<ets.size()-1;i++){
//				EnemyTank this = ets.get(i);
//				for(int j=i+1;j<ets.size();j++){
//					EnemyTank et2 = ets.get(j);
//					
//					switch(et2.direct){
//					//����̹�����ϻ�����
//					case 0:
//					case 1:
//						//�ҵ�̹�˵��ĸ�����
//						if(this.direct==0){
//							if((this.x>=et2.x&&this.x<=(et2.x+20)&&this.y==(et2.y+30)
//							  || (this.x+20)>=et2.x&&(this.x+20)<=(et2.x+20)&&this.y==(et2.y+30))){
//								return true;
//							}
//						}else if(this.direct==1){
//							if((this.x+20)>=et2.x&&((this.x+20)<=(et2.x+20))&&(this.y+30)==et2.y || 
//									this.x>=et2.x&&this.x<=(et2.x+20)&&(this.y+30)==et2.y){
//								return true;  
//							}
//						}else if(this.direct==2){
//							if(this.y>=et2.y&&this.y<=(et2.y+30)&&this.x==(et2.x+20) || (this.y+20)>et2.y&&(this.y+20)<=(et2.y+30)&&this.x==(et2.x+20)){
//								return true;
//							}
//						}else if(this.direct==3){
//							if(this.y>=et2.y&&this.y<=(et2.y+30)&&(this.x+30)==et2.x || (this.y+20)>=et2.y&&(this.y+20)<=(et2.y+30)&&(this.x+30)==et2.x){
//								return true;
//							}
//						}
//						break;
//					//����̹�����������
//					case 2:
//					case 3:
//						if(this.direct==0){
//							if((this.x>=et2.x&&this.x<=(et2.x+30)&&this.y==(et2.y+20)
//							  || (this.x+20)>=et2.x&&(this.x+20)<=(et2.x+30)&&this.y==(et2.y+20))){
//								return true;
//							}
//						}else if(this.direct==1){
//							if((this.x+20)>=et2.x&&((this.x+20)<=(et2.x+30))&&(this.y+30)==et2.y || 
//									this.x>=et2.x&&this.x<=(et2.x+30)&&(this.y+30)==et2.y){
//								return true;
//							}
//						}else if(this.direct==2){
//							if(this.y>=et2.y&&this.y<=(et2.y+20)&&this.x==(et2.x+30) || (this.y+20)>et2.y&&(this.y+20)<=(et2.y+20)&&this.x==(et2.x+30)){
//								return true;
//							}
//						}else if(this.direct==3){
//							if(this.y>=et2.y&&this.y<=(et2.y+20)&&(this.x+30)==et2.x || (this.y+20)>=et2.y&&(this.y+20)<=(et2.y+20)&&(this.x+30)==et2.x){
//								return true;
//							}
//						}
//						break;
//						}
//					}
//				}
//			return false;
//		}
	
	//�ж��ӵ��Ƿ����ש��
	public void hitBrick(){
		
		Bullet bullet = null;
		BigBrick bigBrick = null;
		Brick brick = null;
		EnemyTank et = null;
		
		//�ж��ҵ��ӵ��Ƿ����ש��
		for(int i=0;i<hero.bullets.size();i++){
			bullet = hero.bullets.get(i);
			for(int j=0;j<bigBricks.size();j++){
				bigBrick = bigBricks.get(j);
				for(int z=0;z<bigBrick.bricks.size();z++){
					brick = bigBrick.bricks.get(z);
					if(bullet.x>=brick.x && bullet.x<=(brick.x+15) && bullet.y>=brick.y && bullet.y<=(brick.y+10)){
						bullet.isLive = false;
						brick.isLive = false;
					}
				}
			}
		}
		
		//�жϵ��˵��ӵ��Ƿ����ש��
		for(int m=0;m<this.ets.size();m++){
		    et = ets.get(m);
			for(int i=0;i<et.bullets.size();i++){
				bullet = et.bullets.get(i);
				for(int j=0;j<bigBricks.size();j++){
					bigBrick = bigBricks.get(j);
					for(int z=0;z<bigBrick.bricks.size();z++){
						brick = bigBrick.bricks.get(z);
						if(bullet.x>=brick.x && bullet.x<=(brick.x+15) && bullet.y>=brick.y && bullet.y<=(brick.y+10)){
							bullet.isLive = false;
							brick.isLive = false;
						}
					}
				}
			}
		}
		
	}

	//�ж��Ƿ���е���̹��
	public void hitEnemyTank(Bullet b,EnemyTank et){
		
		//�жϵ���̹�˵ķ���
		switch(et.direct){
		case 0:
		case 1:
			if(b.getX()>et.getX() && b.getX()<et.getX()+20 
			&& b.getY()<et.getY()+30 && b.getY()>et.getY()){
				//��ʱ̹���������ӵ�Ҳ����
				b.isLive=false;
				et.isLive=false;
				
				//����һ������
				Recorder.oneEnemyTankBomb();
				
				//����һ��ը�����ҷ���Vector
				Bomb bomb = new Bomb(et.getX(), et.getY());
				bombs.add(bomb);
			}
			break;
		case 2:
		case 3:
			if(b.getY()>et.getY() && b.getY()<et.getY()+30 
			&& b.getX()<et.getX()+20 && b.getX()>et.getX()){
				//��ʱ̹���������ӵ�Ҳ����
				b.isLive=false;
				et.isLive=false;
				
				//����һ������
				Recorder.oneEnemyTankBomb();
				
				//����һ��ը�����ҷ���Vector
				Bomb bomb = new Bomb(et.getX(), et.getY());
				bombs.add(bomb);
			}
			break;
		default:
			break;
		}
	}
	
	//�ж��ҵ�̹���Ƿ񱻵���̹�˻���
	public void hitMyTank(Bullet b,Hero hero){
		
		int heroLife = Recorder.getMyLife();
			
		//�жϵ���̹�˵ķ���
		switch(hero.direct){
		case 0:
		case 1:
			if(b.getX()>hero.getX() && b.getX()<hero.getX()+20 
			&& b.getY()<hero.getY()+30 && b.getY()>hero.getY()){
				//��ʱ̹���������ӵ�Ҳ����
				b.isLive=false;
				hero.isLive=false;
				heroLife--;
				Recorder.setMyLife(heroLife);
					
				//����һ��ը�����ҷ���Vector
				Bomb bomb = new Bomb(hero.getX(), hero.getY());
				bombs.add(bomb);
			}
			break;
		case 2:
		case 3:
			if(b.getY()>hero.getY() && b.getY()<hero.getY()+30 
			&& b.getX()<hero.getX()+20 && b.getX()>hero.getX()){
				//��ʱ̹���������ӵ�Ҳ����
				b.isLive=false;
				hero.isLive=false;
				heroLife--;
				Recorder.setMyLife(heroLife);
					
				//����һ��ը�����ҷ���Vector
				Bomb bomb = new Bomb(hero.getX(), hero.getY());
				bombs.add(bomb);
			}
			break;
		default:
			break;
		}
	}
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch(e.getKeyCode()){
		case KeyEvent.VK_UP:
			
			if(hero.isChangeDirect){
				//�����ҵ�̹�˵ķ���
				this.hero.setDirect(0);
			}
			
			if(hero.getY()>0 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.moveUp();
				
			}
			break;
		case KeyEvent.VK_DOWN:
			if(hero.isChangeDirect){
				//�����ҵ�̹�˵ķ���
				this.hero.setDirect(1);
			}
			if(hero.getY()<575 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.moveDown();
				
			}
			break;
		case KeyEvent.VK_LEFT:
			if(hero.isChangeDirect){
				//�����ҵ�̹�˵ķ���
				this.hero.setDirect(2);
			}
			if(hero.getX()>0 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.moveLeft();
				
			}
			break;
		case KeyEvent.VK_RIGHT:
			if(hero.isChangeDirect){
				//�����ҵ�̹�˵ķ���
				this.hero.setDirect(3);
			}
			if(hero.getX()<775 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.movRight();
				
			}
			break;
			
		}
		
		if(e.getKeyCode()==KeyEvent.VK_SPACE && hero.isCanFire==true){
			//�ӵ����ܴ��5��
			if(this.hero.bullets.size()<5){
				this.hero.fire();
			}
		} 
		
		//�������»���
		repaint();
		
	}

	@Override
	public void keyReleased(KeyEvent e) {
		
	}

	@Override
	public void keyTyped(KeyEvent e) {
		
	}

	@Override
	public void run() {
		// ÿ��100����ȥ�ػ�
		
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//��Ҫ����forѭ����ÿ���ӵ���̹�˽���ƥ��,���е���̹�ˣ����ҵ��û���ש��ķ���
			for(int i=0;i<hero.bullets.size();i++){
				//ȡ���ӵ�
				Bullet bullet = hero.bullets.get(i);
				//�ж��ӵ��Ƿ���Ч
				if(bullet.isLive){
					//ȡ��̹��
					for(int j=0;j<ets.size();j++){
						EnemyTank et = ets.get(j);
						if(et.isLive){
							hitEnemyTank(bullet, et);
						}
					}
				}
			}
			
			//�ҵ�̹�˱�����
			for(int j=0;j<ets.size();j++){
				
				EnemyTank et = ets.get(j);
				for(int i=0;i<et.bullets.size();i++){
					
					Bullet bullet = et.bullets.get(i);
					if(bullet.isLive && hero.isLive){
						hitMyTank(bullet, hero);
					}
				}
			}
			
			//�ӵ�����ש��
			hitBrick();
			
			this.repaint();
		}
	}
}


