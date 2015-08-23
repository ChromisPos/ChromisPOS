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
import java.util.*;
import java.awt.event.*;
  
public abstract class CustomButton extends JPanel
implements MouseListener{
  
String title = null;
private Vector listeners = null;
boolean hit = false;
  
public CustomButton (String title){
super();
this.title = title;
listeners = new Vector();
addMouseListener(this);
}
  
@Override
public Dimension getPreferredSize(){return new Dimension(120,80);}
  
@Override
public abstract void paintComponent(Graphics g);
  
 
@Override
public void mousePressed(MouseEvent e){
hit=true;
repaint();
}
  
@Override
public void mouseReleased(MouseEvent e){
hit=false;
repaint();
}
  
@Override
public void mouseClicked(MouseEvent e){
fireEvent(new ActionEvent(this,0,title));
}
  
@Override
public void mouseEntered(MouseEvent e){}
@Override
public void mouseExited(MouseEvent e){}
  
public void addActionListener(ActionListener listener){
listeners.addElement(listener);
}
  
public void removeActionListener(ActionListener listener){
listeners.removeElement(listener);
}
  
private void fireEvent(ActionEvent event){
for (int i = 0;i<listeners.size() ;i++ ){
ActionListener listener = (ActionListener)listeners.elementAt(i);
listener.actionPerformed(event);
}
}
  
}//

