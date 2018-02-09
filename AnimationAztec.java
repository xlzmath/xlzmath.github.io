//Animation of the Aztec growth
//Based on interlacing variables in TASEP with step initial conditions, discrete time and parallel update

import java.awt.*;         // Defines basic classes for GUI programming.
import java.awt.event.*;   // Defines classes for working with events.
import javax.swing.*;      // Defines the Swing GUI classes.
import javax.swing.event.*;

public class AnimationAztec
extends JApplet implements ActionListener,Runnable,ChangeListener,ItemListener
{

//		Variables for the animator
Thread animator;
double delay;

//		 Variables for the specific program
boolean freeze; // Changed with StartStopButton
int[][] x=new int[1000][1000]; // x=particles; y=holes
int[][] y=new int[1000][1000]; // x=particles; y=holes
int[][] Oldx=new int[1000][1000]; // x=particles; y=holes
int[][] Oldy=new int[1000][1000];
int Tmax;
int t; // Time
int Speed;
int Diameter;
int Vis; // Vis Mode: 1=Full, 2=Only particles, 3=Only Aztec
boolean Particles,Dimers; //Decide if particles and/or dimers are drawn
boolean Border; // Decide if to draw black the borders or the Aztec or not
double p1;
double[] P=new double[1000];

int[] Xpoly=new int[4];
int[] Ypoly=new int[4];
//		Variables for the Buttons
Display1 TASEPDisplay;  // The display for the TASEP particles
JButton StartStopButton,DirTempoButton,MultilinesButton,NewPointsButton,SetParameters,Reset;
JTextField PointsText,DiameterText,RightProbaText; 
JLabel PointsLabel,DiameterLabel,RightProbaLabel; 
JSlider SpeedSlider;
JLabel SpeedLabel;
JComboBox BorderMenu,VisMenu;
JPanel AnimationRegion,ControlRegion;

//		Animation
public void start() {
 animator = new Thread(this);
 animator.setPriority(5);
 animator.start();
 }

public void run() {
//Remember the starting time
 long tm = System.currentTimeMillis();
 while (Thread.currentThread() == animator) {
//Display the next frame of animation.
	ComputeAnimation();
	repaint();
 try {
    tm += delay;
    Thread.sleep(Math.max(0, tm - System.currentTimeMillis()));
 } catch (InterruptedException e) {break;}
}
}
		    
public void stop() {
animator = null;
}
		   
//		 The init 
public void init() {
Tmax=30;
p1=0.5;

Speed = 10;
delay = (Speed > 0) ? (5000 / Speed) : 1;

Diameter=8;
   
InitFrames();
Inizializza();
}  // end init()

//		 Inizializza il testo
public void Inizializza() {
// Set the jump Rates
for (int n=1;n<=Tmax;n++) {P[n]=p1;}
// Set the initial positions of particles
for(int n=1;n<=Tmax;n++){
	for(int k=1;k<=n;k++){
	  x[k][n]=k;
	  Oldx[k][n]=x[k][n];
	}
}
// Set the initial positions of holes
for(int n=1;n<=Tmax;n++){
	for(int k=1;k<=Tmax+1-n;k++){
	  y[k][n]=n+k;
	  Oldy[k][n]=y[k][n];
	}
}
t=0;
} // End inizializza il resto   

//	Compute the next configuration for the animation
public void ComputeAnimation() {

if (t==Tmax) {freeze=true;  StartStopButton.setText("Finished");}

if (freeze==false) {
  t++;
// Save old positions (at time t only particles up to level t can move)
  for(int n=1;n<=t;n++){
  for(int k=1;k<=t;k++){Oldx[k][n]=x[k][n];}
  for(int k=1;k<=t-n+1;k++){Oldy[k][n]=y[k][n];}
  }

for (int n=t;n>=1;n--) {
  for (int k=1;k<=n;k++) {
// Random jump for k=n
  if (k==n) {if (Math.random()<=P[n]) {y[x[k][n]-k+1][n]--; x[k][n]++; }}
// except for k=n, check if particle can move
  if (k<n) {
    if ((x[k][n]<x[k][n-1])) {
	  if (Math.random()<=P[n]) {y[x[k][n]-k+1][n]--; x[k][n]++;}
    }
    }
  // Push particles if needed
  if ((k>=2)&&(n>=2)) {
	if (x[k][n]==x[k-1][n-1]) {y[x[k][n]-k+1][n]--; x[k][n]++;}
  }
//
  }
  }
}
} // End ComputeAnimation

//		 Restart the simulation when some parameters like number of points or configuration or geometry changed 

public void ridisegna() {
 TASEPDisplay.repaint();
}
		  
public void riparti() {
 freeze=true;
 StartStopButton.setText("Start");
 t=0;
 ridisegna();
}

public int[] CxSouth(int k, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= 2*Diameter*x[k][n];
    Posizioni[1]= 2*Diameter*x[k][n]+2*Diameter;
    Posizioni[2]= 2*Diameter*x[k][n]+Diameter;
    Posizioni[3]= 2*Diameter*x[k][n]-Diameter;
    
    return Posizioni;
  }

public int[] CySouth(int k, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= Diameter+20+2*Diameter*(Tmax-n)+2*Diameter;
    Posizioni[1]= Diameter+20+2*Diameter*(Tmax-n);
    Posizioni[2]= Diameter+20+2*Diameter*(Tmax-n)-Diameter;
    Posizioni[3]= Diameter+20+2*Diameter*(Tmax-n)+Diameter;
    
    return Posizioni;
  }

public int[] CxNorth(int k, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= 2*Diameter*y[k][n]-Diameter;
    Posizioni[1]= 2*Diameter*y[k][n]+Diameter;
    Posizioni[2]= 2*Diameter*y[k][n];
    Posizioni[3]= 2*Diameter*y[k][n]-2*Diameter;
    
    return Posizioni;
  }

public int[] CyNorth(int l, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= Diameter+20+2*Diameter*(Tmax-n)+3*Diameter;
    Posizioni[1]= Diameter+20+2*Diameter*(Tmax-n)+Diameter;
    Posizioni[2]= Diameter+20+2*Diameter*(Tmax-n);
    Posizioni[3]= Diameter+20+2*Diameter*(Tmax-n)+2*Diameter;
    
    return Posizioni;
  }

public int[] CxWest(int k, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= 2*Diameter*x[k][n];
    Posizioni[1]= 2*Diameter*x[k][n]+Diameter;
    Posizioni[2]= 2*Diameter*x[k][n]-Diameter;
    Posizioni[3]= 2*Diameter*x[k][n]-2*Diameter;
    
    return Posizioni;
  }

public int[] CyWest(int k, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= Diameter+20+2*Diameter*(Tmax-n)+2*Diameter;
    Posizioni[1]= Diameter+20+2*Diameter*(Tmax-n)+Diameter;
    Posizioni[2]= Diameter+20+2*Diameter*(Tmax-n)-Diameter;
    Posizioni[3]= Diameter+20+2*Diameter*(Tmax-n);
    
    return Posizioni;
  }

public int[] CxEast(int k, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= 2*Diameter*y[k][n]+Diameter;
    Posizioni[1]= 2*Diameter*y[k][n]+2*Diameter;
    Posizioni[2]= 2*Diameter*y[k][n];
    Posizioni[3]= 2*Diameter*y[k][n]-Diameter;
    
    return Posizioni;
  }

public int[] CyEast(int l, int n) {
    int Posizioni[]= new int[4]; 
                    
    Posizioni[0]= Diameter+20+2*Diameter*(Tmax-n)+3*Diameter;
    Posizioni[1]= Diameter+20+2*Diameter*(Tmax-n)+2*Diameter;
    Posizioni[2]= Diameter+20+2*Diameter*(Tmax-n);
    Posizioni[3]= Diameter+20+2*Diameter*(Tmax-n)+Diameter;
    
    return Posizioni;
  }

class Display1 extends JPanel {
Display1() {
  setBackground(Color.lightGray);
}
public void paintComponent(Graphics g){
 int width=getSize().width;
 int height=getSize().height;
 super.paintComponent(g);
 int k,n;
 
 // Draw the TASEP particles position in the (k,x_k+k) plot
 g.setColor(Color.black);
 g.drawString("Time = "+t,Diameter,15);
//PAINT THE POSITIONS OF THE PARTICLES
 g.setColor(Color.black);
 g.drawLine(0,20-1,width,20-1);
 g.drawLine(0,20+2*Diameter*(Tmax+1),width,20+2*Diameter*(Tmax+1));
 for(n=1;n<=Tmax;n++){
if (Dimers==true) {
// Aztec coloring (West + South)
   for(k=1;k<=n;k++){
	if (n<=t) {
	  if (x[k][n]==Oldx[k][n]) { 	 // Determine if it is south coloring
	  g.setColor(new Color(0, 0, 255));
	  g.fillPolygon(CxSouth(k,n),CySouth(k,n),4);
if (Border) {g.setColor(Color.black);
	  g.drawPolygon(CxSouth(k,n),CySouth(k,n),4);}
	  } else {                       // Otherwise it is west 
	  g.setColor(new Color(255 , 0, 0));
	  g.fillPolygon(CxWest(k,n),CyWest(k,n),4);
if (Border) {g.setColor(Color.black);
	  g.drawPolygon(CxWest(k,n),CyWest(k,n),4);}
      }
	}
   }
// End Aztec coloring (West + South)
// Aztec coloring (East + North)
   for (k=1;k<=t-n+1;k++) { //k=Tmax+1-t
	  if (n<=t) { 
	  if (((n>=2)&&(y[k][n]==Oldy[k][n-1]+1))||((n==1)&&(y[k][1]>x[1][1]))) { // Determine if it is north
        g.setColor(new Color(135,206,250));
	    g.fillPolygon(CxNorth(k,n),CyNorth(k,n),4);
if (Border) {g.setColor(Color.black);
	    g.drawPolygon(CxNorth(k,n),CyNorth(k,n),4);}
	  } else {  // Otherwise it is East
	        g.setColor(new Color(250,135,206));
		    g.fillPolygon(CxEast(k,n),CyEast(k,n),4);
if (Border) {g.setColor(Color.black);
		    g.drawPolygon(CxEast(k,n),CyEast(k,n),4);}		  
	  }
     }
   }
// End Aztec coloring (East + North)
}
if (Particles==true) {
// Particle positions
   for(k=1;k<=n;k++){
	g.setColor(Color.black);
   	g.fillOval(CxSouth(k,n)[0]-Diameter/2,CySouth(k,n)[1]+Diameter/2,Diameter,Diameter);
   }
// End Particle positions
}
}
} // end nested class Display1
}

// Construct the frames
public void InitFrames() {
getContentPane().setLayout(null);
getContentPane().setBackground(Color.black); 
// Set the animation region
AnimationRegion = new JPanel();
AnimationRegion.setLayout(new GridLayout(1,0));      
AnimationRegion.setBackground(Color.lightGray);
TASEPDisplay = new Display1();
TASEPDisplay.setBorder( BorderFactory.createLineBorder(Color.gray,1));
AnimationRegion.add(TASEPDisplay);
AnimationRegion.setBounds(200,0,getSize().width-200,getSize().height);
getContentPane().add(AnimationRegion);
		   
// Set the control region
ControlRegion = new JPanel();
ControlRegion.setLayout(null);
ControlRegion.setBounds(0,0,200,getSize().height);
getContentPane().add(ControlRegion);  
  
int NButton,HButton; // To organize the button positions, starts from 0
// Set the Start/Stop button, part of control region
StartStopButton = new JButton("Start");
NButton=0; HButton=1;
freeze=true;
StartStopButton.addActionListener(this);
StartStopButton.setBounds(5,5+NButton*40,190,35*HButton);
ControlRegion.add(StartStopButton);

VisMenu = new JComboBox();
NButton=1; HButton=1;
VisMenu.setBackground(Color.white);
VisMenu.addItem("Show Particles + Dimers");
VisMenu.addItem("Show Particles only");
VisMenu.addItem("Show Dimers only");
VisMenu.addItemListener(this);
VisMenu.setBounds(5,5+NButton*40,190,35*HButton);
ControlRegion.add(VisMenu);
Vis=1;
Particles=true;
Dimers=true;
// Starts with Dimers and Particles

BorderMenu = new JComboBox();
NButton=2; HButton=1;
BorderMenu.setBackground(Color.white);
BorderMenu.addItem("With dimer borders");
BorderMenu.addItem("No dimer borders");
BorderMenu.addItemListener(this);
BorderMenu.setBounds(5,5+NButton*40,190,35*HButton);
ControlRegion.add(BorderMenu);
Border=true; // Starts with Dimers

JPanel PointsPanel = new JPanel();
PointsPanel.setLayout(new GridLayout(4,2));
NButton=3; HButton=3;
PointsPanel.setBounds(5,5+NButton*40,190,35*HButton+15);
ControlRegion.add(PointsPanel);
// Set the Labels and the TextFields
PointsLabel = new JLabel("Nb Particles");
PointsPanel.add(PointsLabel);
PointsText = new JTextField(""+Tmax);
PointsPanel.add(PointsText);
		   
DiameterLabel = new JLabel("Particles Diameter");
PointsPanel.add(DiameterLabel);
DiameterText = new JTextField(""+Diameter);
PointsPanel.add(DiameterText);
		   
RightProbaLabel = new JLabel("Right jump proba");
PointsPanel.add(RightProbaLabel);
RightProbaText = new JTextField(""+p1);
PointsPanel.add(RightProbaText);

// Set the SpeedPanel, composed of a Slider and a Label
JPanel SpeedPanel = new JPanel();
SpeedPanel.setLayout(new BorderLayout());
NButton=6; HButton=2;
SpeedPanel.setBounds(5,5+NButton*40,190,35*HButton);
ControlRegion.add(SpeedPanel);
// Set the Slider  
SpeedSlider = new JSlider(0,100,Speed);
SpeedSlider.addChangeListener(this);
SpeedSlider.setMajorTickSpacing(50);
SpeedSlider.setMinorTickSpacing(5);
SpeedSlider.setPaintTicks(true);
SpeedSlider.setPaintLabels(true);
SpeedPanel.add(SpeedSlider, BorderLayout.CENTER);
// Set the Label
SpeedLabel = new JLabel("Speed = "+SpeedSlider.getValue());
SpeedPanel.add(SpeedLabel, BorderLayout.WEST);

//  Inizialize set parameters

NButton=8; HButton=1;
SetParameters = new JButton("Set the parameters");
SetParameters.addActionListener(this);
SetParameters.setBounds(5,5+NButton*40,190,35*HButton);
ControlRegion.add(SetParameters);  
		   
NButton=9; HButton=1;
Reset = new JButton("Reset");
Reset.addActionListener(this);
Reset.setBounds(5,5+NButton*40,190,35*HButton);
ControlRegion.add(Reset);  

} // End InitFrames

//		 Answer to button pressed
public void actionPerformed(ActionEvent evt) {
  String command = evt.getActionCommand();
		      
   if (command.equals("Start"))
   { 
     freeze=false;
     StartStopButton.setText("Stop");
   }
   else if (command.equals("Stop"))
   { 
     freeze=true;
     StartStopButton.setText("Start");
   }
   else if (command.equals("Set the parameters"))
   {
     ridisegna();
   }
   else if (command.equals("Reset"))
   {
     freeze=true;
     StartStopButton.setText("Start");
     Inizializza();
   }
   try {
     String xStr = PointsText.getText();
     if ((int)(Double.parseDouble(xStr))!=Tmax)
       {Tmax=(int)(Double.parseDouble(xStr));         
       Inizializza();
       } 
  }
  catch (NumberFormatException e) {
     PointsText.setText(""+Tmax);
  }
  try {
     String xStr = DiameterText.getText();
     Diameter = (int)(Double.parseDouble(xStr));
  }
  catch (NumberFormatException e) {
     DiameterText.setText(""+Diameter);
  }
  try {
     String xStr = RightProbaText.getText();
     p1 = (Double.parseDouble(xStr));
  }
  catch (NumberFormatException e) {
  	RightProbaText.setText(""+p1);
  }
}  // end actionPerformed
		   
//		 Answer to Slider modified
public void stateChanged(ChangeEvent evt) {
   if (evt.getSource() == SpeedSlider) {
   if (SpeedSlider.getValue()<1) {SpeedSlider.setValue(1);}
   Speed=SpeedSlider.getValue();
   SpeedLabel.setText("Speed = "+Speed);}
   delay=5000/Speed;
} // End stateChanged


public void itemStateChanged(ItemEvent evt) {
 String SlowName = (String)BorderMenu.getSelectedItem();
 String VisName = (String)VisMenu.getSelectedItem();
 
 if (SlowName=="No dimer borders")
 { 
   if (Border==true) {Border=false; repaint();}
 }
 else if (SlowName=="With dimer borders")
 {
   if (Border==false) {Border=true; repaint();}
 }    
 if (VisName=="Show Particles + Dimers")
 { 
   Particles=true; Dimers=true; repaint();
 } else if (VisName=="Show Particles only")
 {
   Particles=true; Dimers=false; repaint();	 
 } else if (VisName=="Show Dimers only")
 {
   Particles=false; Dimers=true; repaint();
 }
  
}  // End itemStateChanged

}