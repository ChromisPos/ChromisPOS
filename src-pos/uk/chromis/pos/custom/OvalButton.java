//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 
//    http://www.chromis.co.uk
//
//    This file is part of Chromis POS
//
//     Chromis POS is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    Chromis POS is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>.


package uk.chromis.pos.custom;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
  
class OvalButton extends CustomButton
{
  
OvalButton(String title){
super(title);
}
  
public void paintComponent(Graphics g){
g.setColor(getParent().getBackground());
g.fillRect(0,0,getWidth(),getHeight());
Graphics2D g2D = (Graphics2D)g;
if (hit==true){
g2D.setColor(Color.green);
}else{
g2D.setColor(Color.yellow);
};
g2D.fillOval(0,0,getWidth()-2,getHeight()-2);
g2D.setColor(Color.black);
g2D.drawOval(0,0,getWidth()-2,getHeight()-2);
g2D.drawString(title,10,getHeight()/2);
}
  
public static void main(String[] args){//TEST
JFrame jFrame = new JFrame();
jFrame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
OvalButton button = new OvalButton("Special button");
button.addActionListener(new ActionListener(){
public void actionPerformed(ActionEvent e){
System.out.println("I am clicked!");
}
});
Container cont = jFrame.getContentPane();
cont.setLayout(new FlowLayout());
cont.add(new JLabel("TEST ME:"));
cont.add(button);
jFrame.pack();
jFrame.setVisible(true);
}
  
}//

