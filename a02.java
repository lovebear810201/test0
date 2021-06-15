package net.incognitas.a02;

import javax.swing.*;
import java.awt.*;
import java.awt.image.*;
import java.awt.event.*;
import java.applet.*;
import java.io.*;
import javax.imageio.ImageIO;

public class a02 extends JFrame
{
	private AudioClip audio_laugh, audio_kim_laugh;
	private JDialog settingFrame;
	private boolean isHelpShow = false, isFLAG = false, isFLAGUsed = false;
	private int length = 196;
	private long startT, endT;
	private int cursor = 0, error = -1;
	private boolean[] q;
	private boolean isZeroRelease = true, isOneRelease = true, isResetRelease = true, isHelpRelease = true, isFLAGReleased = true;
	private static final int keyZero = KeyEvent.VK_DOWN, keyOne = KeyEvent.VK_RIGHT, keyReset = KeyEvent.VK_R, keySetting = KeyEvent.VK_ESCAPE, keyHelp = KeyEvent.VK_H, keySS = KeyEvent.VK_F2, keyFLAG = KeyEvent.VK_F;
	public a02()
	{
		super("無聊遊戲");
		try
		{
			audio_laugh = Applet.newAudioClip(getClass().getResource("/assets/audio/laugh"));
			audio_kim_laugh = Applet.newAudioClip(getClass().getResource("/assets/audio/kim_laugh"));
		}
		catch(Exception e)
		{
			e.printStackTrace();
			JOptionPane.showMessageDialog(null,"錯誤:資源消失","ERROR",JOptionPane.ERROR_MESSAGE);
			System.exit(1);
		}
		makeQ();
		settingFrame = new JDialog(this,"設定",true);
		settingFrame.setResizable(false);
		settingFrame.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Container setC = settingFrame.getContentPane();
		setC.setLayout(new GridBagLayout());
		GridBagConstraints gbc = new GridBagConstraints();
		JTextField inputText = new JTextField();
		JButton ok = new JButton("確定"), cancel = new JButton("取消");
		cancel.addActionListener((e) ->
		{
			settingFrame.dispose();
		});
		inputText.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
				{
					settingFrame.dispose();
				}
			}
			public void keyTyped(KeyEvent e)
			{
				char c = e.getKeyChar();
				if (c < '0'||c > '9') e.consume();
			}
		});
		ActionListener confirm = (e) ->
		{
			try
			{
				int tmp = Integer.parseInt(inputText.getText());
				if(tmp != length)
				{
					length = tmp;
					makeQ();
					cursor = 0;
					error = -1;
					repaint();
				}
				settingFrame.dispose();
			}
			catch(NumberFormatException ex)
			{
				ex.printStackTrace();
				JOptionPane.showMessageDialog(null,"錯誤:這不是數字或是數字太長","ERROR",JOptionPane.ERROR_MESSAGE);
			}
		};
		inputText.addActionListener(confirm);
		ok.addActionListener(confirm);
		
		gbc.gridx = gbc.gridy = 0;
		gbc.insets = new Insets(5,10,5,0);
		setC.add(new JLabel("長度:"),gbc);
		gbc.fill = GridBagConstraints.HORIZONTAL;
		gbc.gridx = 1;
		gbc.weightx = 1.0;
		gbc.insets = new Insets(5,0,5,10);
		setC.add(inputText,gbc);
		gbc.fill = GridBagConstraints.NONE;
		gbc.weightx = 0.0;
		gbc.gridx = 0;
		gbc.gridy = 1;
		gbc.insets = new Insets(10,10,10,0);
		gbc.anchor = GridBagConstraints.EAST;
		setC.add(ok,gbc);
		gbc.gridx = 1;
		gbc.insets = new Insets(10,0,10,10);
		gbc.anchor = GridBagConstraints.WEST;
		setC.add(cancel,gbc);
		settingFrame.setContentPane(setC);
		settingFrame.pack();
		
		this.setSize(600,400);
		this.setMinimumSize(new Dimension(600,400));
		this.setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		Dimension scrSize = Toolkit.getDefaultToolkit().getScreenSize();
		this.setLocation((scrSize.width - getWidth()) / 2, (scrSize.height - getHeight()) / 2);
		this.addKeyListener(new KeyAdapter()
		{
			public void keyPressed(KeyEvent e)
			{
				int code = e.getKeyCode();
				if(code == keyZero && isZeroRelease)
				{
					isZeroRelease = false;
					pressKey(false);
				}
				else if(code == keyOne && isOneRelease)
				{
					isOneRelease = false;
					pressKey(true);
				}
				else if(code == keyReset && isResetRelease)
				{
					isResetRelease = false;
					if(isFLAGUsed)
					{
						isFLAG = false;
						isFLAGUsed = false;
					}
					makeQ();
					cursor = 0;
					error = -1;
					repaint();
				}
				else if(code == keySetting)
				{
					Point mainFrameLocation = a02.this.getLocation();
					settingFrame.setLocation(mainFrameLocation.x + (a02.this.getWidth() - settingFrame.getWidth()) / 2, mainFrameLocation.y + (a02.this.getHeight() - settingFrame.getHeight()) / 2);
					inputText.setText(Integer.toString(length));
					settingFrame.setVisible(true);
				}
				else if(code == keyHelp && isHelpRelease)
				{
					isHelpRelease = false;
					if(isHelpShow) isHelpShow = false;
					else isHelpShow = true;
					repaint();
				}
				else if(code == keySS)
				{
					BufferedImage img = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
					Graphics g = img.getGraphics();
					paintContent(g);
					paintUI(g);
					try
					{
						File SSDir = new File("SS");
						if(!SSDir.exists() || SSDir.isFile()) SSDir.mkdir();
						File SSFile = new File("SS",System.currentTimeMillis() + ".png");
						ImageIO.write(img,"png",SSFile);
						JOptionPane.showMessageDialog(a02.this,"已截圖存檔至\n" + SSFile.getAbsolutePath(),"(標題空空的好像很難看)",JOptionPane.INFORMATION_MESSAGE);
					}
					catch(Exception ex)
					{
						ex.printStackTrace();
						JOptionPane.showMessageDialog(a02.this,"錯誤:截圖出錯惹","ERROR",JOptionPane.ERROR_MESSAGE);
					}
				}
				else if(code == keyFLAG && isFLAGReleased)
				{
					isFLAGReleased = false;
					isFLAG = true;
					repaint();
				}
			}
			public void keyReleased(KeyEvent e)
			{
				int code = e.getKeyCode();
				if(code == keyZero && !isZeroRelease)
				{
					isZeroRelease = true;
				}
				else if(code == keyOne && !isOneRelease)
				{
					isOneRelease = true;
				}
				else if(code == keyReset && !isResetRelease)
				{
					isResetRelease = true;
				}
				else if(code == keyHelp && !isHelpRelease)
				{
					isHelpRelease = true;
				}
				else if(code == keyFLAG && !isFLAGReleased)
				{
					isFLAGReleased = true;
				}
			}
		});
	}
	public void paint(Graphics g1)
	{
		BufferedImage img = new BufferedImage(getWidth(),getHeight(),BufferedImage.TYPE_INT_RGB);
		Graphics g = img.getGraphics();
		Insets ins = getInsets();
		final int width = getWidth() - (ins.left + ins.right), height = getHeight() - (ins.top + ins.bottom);
		g.setColor(Color.black);
		g.fillRect(ins.left,ins.top,width,height);
			
		paintContent(g);
		paintUI(g);
		g1.drawImage(img,0,0,null);
	}
	private void paintUI(Graphics g)
	{
		Font message_font = new Font("細明體",Font.PLAIN,16);
		FontMetrics message_fm = g.getFontMetrics(message_font);
		Insets ins = getInsets();
		final int width = getWidth() - (ins.left + ins.right), height = getHeight() - (ins.top + ins.bottom);
		int lineheight = message_fm.getHeight();
		
		g.setFont(message_font);
		Color textBgColor = Color.black;
		Color[] leftColors = {Color.white, Color.green,Color.green,null,null,null,Color.green,null,null,null};
		if(cursor == length) leftColors[2] = Color.green;
		else if(error != -1) leftColors[2] = Color.red;
		/*
			8:幫助
			7:0
			6:1
			5:立FLAG
			4:截圖
			3:設定
			2:重設
			1:FLAG
			0:FLAG
			-1:sign
		*/
		String[] leftMessages =
			{
				"MADE BY HSSLCreative"
				, isFLAG?isFLAGUsed?"既然你誠心誠意的求了, 予你便是!ouo":"(隨便說那種立FLAG的台詞可是會...)":null
				, isFLAG?isFLAGUsed?"(隨便說那種立FLAG的台詞可是會...)":"這樣下去只要不要按錯無事結束就好了呢":null
				, isHelpShow?"重設:" + KeyEvent.getKeyText(keyReset):null
				, isHelpShow?"設定:" + KeyEvent.getKeyText(keySetting):null
				, isHelpShow?"截圖:" + KeyEvent.getKeyText(keySS):null
				, isHelpShow?"立FLAG:" + KeyEvent.getKeyText(keyFLAG):null
				, isHelpShow?"1:" + KeyEvent.getKeyText(keyOne):null
				, isHelpShow?"0:" + KeyEvent.getKeyText(keyZero):null
				, isHelpShow?null:"顯示幫助:" + KeyEvent.getKeyText(keyHelp)
			};
		for(int k = 0,l = 0; k < leftMessages.length; k++)
		{
			if(leftMessages[k] == null) continue;
			g.setColor(textBgColor);
			g.fillRect(ins.left,height + ins.top - lineheight - l * lineheight,message_fm.stringWidth(leftMessages[k]),lineheight);
			if(leftColors[k] != null) g.setColor(leftColors[k]);
			else g.setColor(Color.yellow);
			g.drawString(leftMessages[k],ins.left,height + ins.top - message_fm.getDescent() - l * lineheight);
			l++;
		}
		
		Color[] rightColors = {error != -1?Color.red:cursor == length?Color.green:null,null,null,null,null};
		String[] rightMessages = {error != -1?"失敗":cursor == length?"成功":null,error != -1 || cursor == length?((cursor > 0?(endT - startT) / (float)cursor:0) + "毫秒/每位數"):null,error != -1 || cursor == length?(((endT - startT) > 0?cursor / (float)(endT - startT) * 1000:"無法計算 ") + "位數/每秒"):null,error != -1 || cursor == length?endT - startT + "ms":null,cursor + "/" + length};
		for(int m = 0,n = 0; m < rightMessages.length; m++)
		{
			if(rightMessages[m] == null) continue;
			g.setColor(textBgColor);
			g.fillRect(width + ins.left - message_fm.stringWidth(rightMessages[m]),height + ins.top - lineheight - n * lineheight,message_fm.stringWidth(rightMessages[m]),lineheight);
			if(rightColors[m] != null) g.setColor(rightColors[m]);
			else g.setColor(Color.yellow);
			g.drawString(rightMessages[m],width + ins.left - message_fm.stringWidth(rightMessages[m]),height + ins.top - message_fm.getDescent() - n * lineheight);
			n++;
		}
	}
	private void paintContent(Graphics g)
	{
		Insets ins = getInsets();
		Font font = new Font("Courier New",Font.PLAIN,16);
		FontMetrics fm = g.getFontMetrics(font);
		
		final int width = getWidth() - (ins.left + ins.right), height = getHeight() - (ins.top + ins.bottom);
		
		g.setFont(font);
		
		int row = (width - (width % fm.stringWidth("0"))) / fm.stringWidth("0");
		int col;
		if(row > length)
		{
			row = length;
			col = 1;
		}
		else
		{
			col = (length - (length % row)) / row + (length % row > 0?1:0);
		}
		int lineheight = fm.getHeight();
		int charWidth = fm.stringWidth("0");
		int t = 0;
		for(int i = 0; i < col; i++)
		{
			for(int j = 0; j < row; j++)
			{
				if(t < cursor)
				{
					//green
					g.setColor(Color.green);
					g.drawString((q[t] == true?"1":"0"),ins.left + j * charWidth,ins.top + fm.getAscent() + i * lineheight);
				}
				else if(t == error)
				{
					//red
					g.setColor(Color.red);
					g.drawString((q[t] == true?"1":"0"),ins.left + j * charWidth,ins.top + fm.getAscent() + i * lineheight);
				}
				else if(t >= cursor)
				{
					//white
					g.setColor(Color.white);
					g.drawString((q[t] == true?"1":"0"),ins.left + j * charWidth,ins.top + fm.getAscent() + i * lineheight);
				}
				if(t < length - 1) t++;
				else break;
			}
		}
	}
	private void pressKey(boolean now)
	{
		if(cursor == 0) startT = System.currentTimeMillis();
		if(error == -1 && cursor < length)
		{
			if(isFLAG)
			{
				q[cursor] = !now;
				isFLAGUsed = true;
			}
			if(q[cursor] == now)
			{
				cursor++;
			}
			else
			{
				error = cursor;
				if(!isFLAG) audio_laugh.play();
				else audio_kim_laugh.play();
			}
			endT = cursor == 0?startT:System.currentTimeMillis();
			repaint();
		}
	}
	private void makeQ()
	{
		q = new boolean[length];
		for(int i = 0; i < length; i++)
		{
			q[i] = Math.random() > 0.5?true:false;
		}
	}
	public static void main(String[] args)
	{
		a02 win = new a02();
		win.setVisible(true);
	}
}