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
	
	//定义菜单栏
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
		
		//菜单栏
		jmb = new JMenuBar();
		jm = new JMenu("Game(G)");
		//设置快捷方式
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
		//闪烁效果
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
			
			//播放开始战斗的声音文件
			PlayAudio playAudio = new PlayAudio("D:\\eclipse\\workspace\\TankGame1\\start.wav");
			playAudio.start();
			
			//如果在游戏过程中点击New则不保存本局数据重新开始
			if(myPanel != null){
				Thread.currentThread().interrupt();
				this.remove(myPanel);
			}
			
			/**
			 * 在new出新的myPanel之前将敌人坦克数设置为初始值在游戏过程中点击new才会画出全部坦克
			 */
			Recorder.setEnNum(Recorder.getInitenemytanks());
			
			myPanel = new MyPanel();

			Thread thread1 = new Thread(myPanel);
			thread1.start();
			//加入新的之前删掉旧的 
			this.remove(myStartPanel);
			this.add(myPanel);	
			//显示刷新
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
				//保存数据并退出
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
	
	//控制什么时候画什么时候不画，出现闪烁效果
	int times = 0;
			
	@Override
	public void paint(Graphics g) {
		
		super.paint(g);
		g.fillRect(0, 0, 800, 600);
		
		if(times%2 == 0)
		{
			g.setColor(Color.red);
			Font myFont = new Font("华文新魏", Font.BOLD, 30);
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
	
	//定义一个我的坦克
	Hero hero = null;
	
	//定义一个敌人的坦克组
	Vector<EnemyTank> ets = new Vector<EnemyTank>();
	//敌人坦克数量
	int etNumber = Recorder.getEnNum();
	
	//定义一个炸弹集合，因为同一瞬间有可能不止一颗炸弹爆炸
	Vector<Bomb> bombs = new Vector<Bomb>();
	//利用爆炸的图片反过来显示作为新生坦克的效果
	Vector<Bomb> birth = new Vector<Bomb>();
	
	//定义三张图片,三张图片的切换组成一颗炸弹
	Image image1 = null;
	Image image2 = null;
	Image image3 = null;
	
	//定义一块大砖
	BigBrick bigBrick = null;
	Vector<BigBrick> bigBricks = new Vector<BigBrick>();
	
	public MyPanel(){
		
		//恢复记录
		Recorder.getBombTanks();
		
		//初始化我的坦克组
		hero = new Hero(400,570);
	
		//初始化第一关地图
		firstMap();
		
		//初始化敌人坦克
		for(int i=0;i<etNumber;i++){
			EnemyTank et = new EnemyTank((i+1)*70,0);
			//敌人坦克颜色
			et.setType(1);
			et.setDirect(1);
			
			//创建出敌人坦克时让它得到其它坦克
			et.giveOtherEnemyTank(ets);
			et.giveMyPanelBigBrick(bigBricks);
			
			//启动敌人坦克的线程
			Thread thread = new Thread(et);
			thread.start();
			
			//给敌人坦克添加一颗子弹
			Bullet bullet = new Bullet(et.getX()+10, et.getY()+30, 1);
			//将子弹加入Vector
			et.bullets.add(bullet);
			//启动子弹线程
			Thread thread2 = new Thread(bullet);
			thread2.start();
			
			ets.add(et);
		}
		
		//初始化三张图片
		//如果这样初始化图片那么第一个爆炸效果不明显
//		image1=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_1.gif"));
//		image2=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_2.gif"));
//		image3=Toolkit.getDefaultToolkit().getImage(Panel.class.getResource("/bomb_3.gif"));
	
		try {
			//这样得到的图片效果明显
			image1=ImageIO.read(new File("D:\\eclipse\\workspace\\TankGame1\\src\\bomb_1.gif"));
			image2=ImageIO.read(new File("D:\\eclipse\\workspace\\TankGame1\\src\\bomb_2.gif"));
			image3=ImageIO.read(new File("D:\\eclipse\\workspace\\TankGame1\\src\\bomb_3.gif"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 第一关地图
	 */
	public void firstMap(){
		
		//初始化大砖块		
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
	
	//画坦克
	@Override
	public void paint(Graphics g) {
		super.paint(g);
		g.fillRect(0, 0, 800, 600);
		
		//画出提示的坦克以及它们的生命数
		showTankLife(g);
		
		if(hero.isLive){
			//画出自己的坦克
			this.drawTank(hero.getX(), hero.getY(), g, hero.direct, hero.Type); 
		}else{
			if(Recorder.getMyLife() != 0){
				hero = new Hero(400, 570);
				//创建一颗炸弹并且放入Vector，反过来做出我的坦克出生时的效果
				Bomb bomb = new Bomb(hero.getX(), hero.getY());
				birth.add(bomb);
			}
		}
		
		//画砖块
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
	
		//画出敌人坦克
		//敌人坦克不断变化所以用ets.size()来表示
		//画出敌人坦克的时候画敌人的子弹，子弹属于敌人坦克
		for(int i=0;i<ets.size();i++){
			EnemyTank et = ets.get(i);
			if(et.isLive){
				this.drawTank(et.getX(), et.getY(), g, et.getDirect(), et.getType());
				
				for(int j=0;j<et.bullets.size();j++){
					
					Bullet b = et.bullets.get(j);
					
					if(b.isLive){
						g.draw3DRect(b.getX(), b.getY(), 1, 1, false);
					}else{
						//如果子弹消失了就将它从Vector中移除
						et.bullets.remove(b);
					}
				}
			}
		}
		
		//画出爆炸时炸弹
		for(int i=0;i<bombs.size();i++){
			//取出子弹
			Bomb bomb = bombs.get(i);
			
			if(bomb.life>6){
				g.drawImage(image1, bomb.getX(), bomb.getY(), 30, 30,this);
			}else if(bomb.life>3){
				g.drawImage(image2, bomb.getX(), bomb.getY(), 30, 30,this);
			}else {
				g.drawImage(image3, bomb.getX(), bomb.getY(), 30, 30,this);
			}
			
			//将炸弹生命值减小
			bomb.lifeDown();
			
			if(bomb.life == 0){
				bombs.remove(bomb);
			}
		}
		
		//利用爆炸的图片反过来显示作为新生坦克的效果
		for(int i=0;i<birth.size();i++){
			//取出子弹
			Bomb bomb = birth.get(i);
			
			if(bomb.life>6){
				g.drawImage(image3, bomb.getX(), bomb.getY(), 30, 30,this);
			}else if(bomb.life>3){
				g.drawImage(image2, bomb.getX(), bomb.getY(), 30, 30,this);
			}else {
				g.drawImage(image1, bomb.getX(), bomb.getY(), 30, 30,this);
			}
			
			//将炸弹生命值减小
			bomb.lifeDown();
			
			if(bomb.life == 0){
				birth.remove(bomb);
			}
			
		}
		
		//画我的子弹
		for(int i=0;i<hero.bullets.size();i++){
			
			Bullet b = hero.bullets.get(i);
			
			if(b!=null && b.isLive==true){
				g.draw3DRect(b.getX(), b.getY(), 1, 1, false);
			}
			//如果子弹消失了就将它从Vector中移除
			if(b.isLive==false){
				hero.bullets.remove(b);
			}
		}
		
	}
	
	//画出提示的坦克以及它们的生命数
	public void showTankLife(Graphics g){
		
		//画出用于提示的坦克
		this.drawTank(100, 620, g, 0, 1);
		g.setColor(Color.black);
		g.setFont(new Font("华文新魏", Font.ITALIC, 20));
		g.drawString("  X  " + Recorder.getEnNum(), 125, 640);
		this.drawTank(300, 620, g, 0, 0);
		g.setColor(Color.black);
		g.drawString("  X  " + Recorder.getMyLife(), 325, 640);
		
		//画出玩家成绩
		g.setColor(Color.red);
		g.setFont(new Font("华文新魏", Font.ITALIC, 20));
		g.drawString("kill :", 820, 30);
		g.drawString(Recorder.getKillEnTankNum()+"", 870, 30);
		
		g.setColor(Color.red);
		g.setFont(new Font("华文新魏", Font.ITALIC, 20));
		g.drawString("scores :", 820, 60);
		g.drawString(Recorder.getKillEnTankNum()+"", 900, 60);
		
	}
	
	//画大砖块
	public void drawBigBrick(int x,int y,Graphics g,BigBrick bigBrick){
		
		g.setColor(Color.orange);
		Brick brick = null;
		for(int i=0;i<bigBrick.bricks.size();i++){
			brick = bigBrick.bricks.get(i);
			
			/**
			 * 通过编号判断打中的是哪块砖
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
	
	//画坦克的方法
	public void drawTank(int x,int y,Graphics g,int direct,int type){
		
		//判断是什么类型的坦克
		switch(type){
		case 0:
			g.setColor(Color.red);
			break;
		case 1:
			g.setColor(Color.yellow);
			break;
		}
		
		//判断方向
		switch(	direct){
		//向上
		case 0:
			//左面的矩形
			g.fill3DRect(x, y, 5, 30,false);
			//画右面的矩形
			g.fill3DRect(x+15, y, 5, 30,false);
			//画出中间的矩形
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//画出中间的圆形
			g.fillOval(x+4, y+9, 10, 10);
			//画出中间竖线
			g.drawLine(x+10, y+10, x+10, y);
			break;
		//向下
		case 1:
			//左面的矩形
			g.fill3DRect(x, y, 5, 30,false);
			//画右面的矩形
			g.fill3DRect(x+15, y, 5, 30,false);
			//画出中间的矩形
			g.fill3DRect(x+5, y+5, 10, 20,false);
			//画出中间的圆形
			g.fillOval(x+4, y+9, 10, 10);
			//画出中间竖线
			g.drawLine(x+10, y+10, x+10, y+30);
			break;
		//向左
		case 2:
			//上面的矩形
			g.fill3DRect(x, y, 30, 5,false);
			//下边的矩形
			g.fill3DRect(x, y+15, 30, 5,false);
			//画出中间的矩形
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//画出中间的圆形
			g.fillOval(x+9, y+4, 10, 10);
			//画出中间横线
			g.drawLine(x+10, y+10, x, y+10);
			break;
		//向右
		case 3:
			//上面的矩形
			g.fill3DRect(x, y, 30, 5,false);
			//下边的矩形
			g.fill3DRect(x, y+15, 30, 5,false);
			//画出中间的矩形
			g.fill3DRect(x+5, y+5, 20, 10,false);
			//画出中间的圆形
			g.fillOval(x+9, y+4, 10, 10);
			//画出中间横线
			g.drawLine(x+10, y+10, x+30, y+10);
			break;
		}
	
	}
	
//	//我的坦克是否碰到敌人的坦克
//	public boolean touchEnemyTank(){
//		
//		for(int i=0;i<ets.size();i++){
//			//得到敌人的坦克
//			EnemyTank et = ets.get(i);
//			switch(et.direct){
//			//敌人坦克往上或往下
//			case 0:
//			case 1:
//				//我的坦克的四个方向
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
//			//敌人坦克往左或往右
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
	
	//敌人坦克间是否相互触碰
	//这个方法在EnemtTank类中比较好，这个属于敌人坦克的能力
//		public static boolean touchTank(){
//			
//			for(int i=0;i<ets.size()-1;i++){
//				EnemyTank this = ets.get(i);
//				for(int j=i+1;j<ets.size();j++){
//					EnemyTank et2 = ets.get(j);
//					
//					switch(et2.direct){
//					//敌人坦克往上或往下
//					case 0:
//					case 1:
//						//我的坦克的四个方向
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
//					//敌人坦克往左或往右
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
	
	//判断子弹是否击中砖块
	public void hitBrick(){
		
		Bullet bullet = null;
		BigBrick bigBrick = null;
		Brick brick = null;
		EnemyTank et = null;
		
		//判断我的子弹是否击中砖块
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
		
		//判断敌人的子弹是否击中砖块
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

	//判断是否击中敌人坦克
	public void hitEnemyTank(Bullet b,EnemyTank et){
		
		//判断敌人坦克的方向
		switch(et.direct){
		case 0:
		case 1:
			if(b.getX()>et.getX() && b.getX()<et.getX()+20 
			&& b.getY()<et.getY()+30 && b.getY()>et.getY()){
				//此时坦克死亡，子弹也死亡
				b.isLive=false;
				et.isLive=false;
				
				//减掉一个敌人
				Recorder.oneEnemyTankBomb();
				
				//创建一颗炸弹并且放入Vector
				Bomb bomb = new Bomb(et.getX(), et.getY());
				bombs.add(bomb);
			}
			break;
		case 2:
		case 3:
			if(b.getY()>et.getY() && b.getY()<et.getY()+30 
			&& b.getX()<et.getX()+20 && b.getX()>et.getX()){
				//此时坦克死亡，子弹也死亡
				b.isLive=false;
				et.isLive=false;
				
				//减掉一个敌人
				Recorder.oneEnemyTankBomb();
				
				//创建一颗炸弹并且放入Vector
				Bomb bomb = new Bomb(et.getX(), et.getY());
				bombs.add(bomb);
			}
			break;
		default:
			break;
		}
	}
	
	//判断我的坦克是否被敌人坦克击中
	public void hitMyTank(Bullet b,Hero hero){
		
		int heroLife = Recorder.getMyLife();
			
		//判断敌人坦克的方向
		switch(hero.direct){
		case 0:
		case 1:
			if(b.getX()>hero.getX() && b.getX()<hero.getX()+20 
			&& b.getY()<hero.getY()+30 && b.getY()>hero.getY()){
				//此时坦克死亡，子弹也死亡
				b.isLive=false;
				hero.isLive=false;
				heroLife--;
				Recorder.setMyLife(heroLife);
					
				//创建一颗炸弹并且放入Vector
				Bomb bomb = new Bomb(hero.getX(), hero.getY());
				bombs.add(bomb);
			}
			break;
		case 2:
		case 3:
			if(b.getY()>hero.getY() && b.getY()<hero.getY()+30 
			&& b.getX()<hero.getX()+20 && b.getX()>hero.getX()){
				//此时坦克死亡，子弹也死亡
				b.isLive=false;
				hero.isLive=false;
				heroLife--;
				Recorder.setMyLife(heroLife);
					
				//创建一颗炸弹并且放入Vector
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
				//设置我的坦克的方向
				this.hero.setDirect(0);
			}
			
			if(hero.getY()>0 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.moveUp();
				
			}
			break;
		case KeyEvent.VK_DOWN:
			if(hero.isChangeDirect){
				//设置我的坦克的方向
				this.hero.setDirect(1);
			}
			if(hero.getY()<575 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.moveDown();
				
			}
			break;
		case KeyEvent.VK_LEFT:
			if(hero.isChangeDirect){
				//设置我的坦克的方向
				this.hero.setDirect(2);
			}
			if(hero.getX()>0 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.moveLeft();
				
			}
			break;
		case KeyEvent.VK_RIGHT:
			if(hero.isChangeDirect){
				//设置我的坦克的方向
				this.hero.setDirect(3);
			}
			if(hero.getX()<775 && !hero.touchEnemyTank(ets) && hero.isCanMove==true && !hero.isTouchBigBrick(bigBricks)){
				this.hero.movRight();
				
			}
			break;
			
		}
		
		if(e.getKeyCode()==KeyEvent.VK_SPACE && hero.isCanFire==true){
			//子弹不能错过5颗
			if(this.hero.bullets.size()<5){
				this.hero.fire();
			}
		} 
		
		//必须重新绘制
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
		// 每隔100毫秒去重绘
		
		while(true){
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			//需要两层for循环对每个子弹和坦克进行匹配,击中敌人坦克，并且调用击中砖块的方法
			for(int i=0;i<hero.bullets.size();i++){
				//取出子弹
				Bullet bullet = hero.bullets.get(i);
				//判断子弹是否有效
				if(bullet.isLive){
					//取出坦克
					for(int j=0;j<ets.size();j++){
						EnemyTank et = ets.get(j);
						if(et.isLive){
							hitEnemyTank(bullet, et);
						}
					}
				}
			}
			
			//我的坦克被击中
			for(int j=0;j<ets.size();j++){
				
				EnemyTank et = ets.get(j);
				for(int i=0;i<et.bullets.size();i++){
					
					Bullet bullet = et.bullets.get(i);
					if(bullet.isLive && hero.isLive){
						hitMyTank(bullet, hero);
					}
				}
			}
			
			//子弹击中砖块
			hitBrick();
			
			this.repaint();
		}
	}
}


