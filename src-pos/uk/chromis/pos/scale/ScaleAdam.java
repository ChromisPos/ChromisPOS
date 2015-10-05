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

package uk.chromis.pos.scale;

import gnu.io.*;
import java.awt.Component;
import java.awt.Dimension;
import java.io.*;
import java.util.TooManyListenersException;
import uk.chromis.pos.forms.AppLocal;
import java.util.logging.Logger;

import java.awt.Font; 
import java.util.logging.Level;
import javax.swing.plaf.FontUIResource; 
import javax.swing.JOptionPane;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.UIManager;

/**
 *
 *   
 */
public class ScaleAdam implements Scale, SerialPortEventListener {
    
    private static final Logger logger = Logger.getLogger("uk.chromis.pos.scale.ScaleAdam");

    private CommPortIdentifier m_PortId;
    private SerialPort m_CommPort;  
    
    private String m_sPortScale;
    private OutputStream m_out;
    private InputStream m_in;

    private static final int SCALE_READY = 0;
    private static final int SCALE_READING = 1;
    private static final int SCALE_USERPRESSEDOK = 2;
    private static final int SCALE_USERPRESSEDCANCEL = 3;
    private static int SCALE_ERROR = -1;
    
    private String m_WeightBuffer;
    private int m_iStatusScale;
    private Component mParent;
    private Font m_OriginalFont;
    private JDialog m_Dialog;
    
    /** Creates a new instance of ScaleComm
     * @param sPortPrinter */
    public ScaleAdam(String sPortPrinter, Component parent ) {
        m_sPortScale = sPortPrinter;
        m_out = null;
        m_in = null;
        
        m_iStatusScale = SCALE_READY; 
        m_WeightBuffer = "";
        
         mParent = parent;
    }
    
    private void showDialog() {

        // Get details of the original font before we change it otherwise all dialogboxes will use new settings
        JOptionPane pane = new JOptionPane();
        Font originalFont=pane.getFont();

        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font("ARIAL",Font.PLAIN,20)));
        String message =  AppLocal.getIntString("scale.weighitem");
        JLabel FontText = new JLabel(message);
        FontText.setFont (new Font ( "Arial", Font.BOLD, 36) );

        JOptionPane newpane = new JOptionPane( );
        newpane.setMessage(FontText);
        newpane.setPreferredSize( new Dimension(450,150));
        m_Dialog = newpane.createDialog("Use Scales");
        m_Dialog.setVisible( true );

        // Return to default settings
        UIManager.put("OptionPane.buttonFont", new FontUIResource(new Font(originalFont.getName(),originalFont.getStyle(),originalFont.getSize())));

    }
    
    private void backgroundReadInput( final int nTimeout ) {
        Thread readThread = new Thread() {
            public void run() {
                readInput(nTimeout);
            }
        };
        readThread.start();
    } 
        
    private void readInput( int nTimeout ) {

        while( --nTimeout > 0 && m_iStatusScale ==  SCALE_READING ) {
            try {
                synchronized (this) {
                    wait(1000);
                }
            } catch (InterruptedException ex) {
               m_iStatusScale = SCALE_READY;
            }
        }
            
    } 
        
    /**
     *
     * @return
     */
    @Override
    public Double readWeight() {
        
        synchronized(this) {
            m_iStatusScale =  SCALE_READING;
            m_WeightBuffer = "";
            
            try {
                if (m_out == null) {
                    m_PortId = CommPortIdentifier.getPortIdentifier(m_sPortScale);                  
                    m_CommPort = (SerialPort) m_PortId.open("PORTID", 2000);      
                    m_CommPort.addEventListener(this);
                    m_CommPort.notifyOnDataAvailable(true);


                    m_out = m_CommPort.getOutputStream();  
                    m_in = m_CommPort.getInputStream();

                    m_CommPort.setSerialPortParams(4800, 
                            SerialPort.DATABITS_8, 
                            SerialPort.STOPBITS_1, 
                            SerialPort.PARITY_NONE);
                    m_CommPort.setFlowControlMode( SerialPort.FLOWCONTROL_NONE );
                }
            } catch (NoSuchPortException | PortInUseException | TooManyListenersException|  UnsupportedCommOperationException | IOException e ) {
                logger.log(Level.SEVERE, "Port exception", e );
                m_iStatusScale = SCALE_ERROR;
            } 
            
            backgroundReadInput( 20 );
        }
        
        showDialog();
                        
        synchronized(this) {
            try {
                if (m_out != null)
                    m_out.close();
                if (m_in != null)
                    m_in.close();
                if (m_CommPort != null) {
                    m_CommPort.removeEventListener();
                    m_CommPort.close();
                }
            } catch ( IOException e ) {
            }
            m_out = null;
            m_in = null;
            m_CommPort = null;
            m_PortId = null;
            
            if (m_iStatusScale == SCALE_READY) {
                
                logger.log(Level.INFO, "Scale ready", m_WeightBuffer );
                
                double dWeight = Double.parseDouble( m_WeightBuffer.toString() );
                
                return dWeight;
            } else {
                m_iStatusScale = SCALE_READY;

                logger.log(Level.WARNING, "Scale no data", m_WeightBuffer );
                
                // Timed out looking for weight or error
                return 0.0;
            }
        }
    }

    private void write(byte[] data) {
        synchronized (this) {
            try {  
                    m_out.write(data);
            } catch ( IOException e) {
                assert( false );
            }        
        }
    }
    
    /**
     *
     * @param e
     */
    @Override
    public void serialEvent(SerialPortEvent e) {

	// Determine type of event.
	switch (e.getEventType()) {
            case SerialPortEvent.BI:
            case SerialPortEvent.OE:
            case SerialPortEvent.FE:
            case SerialPortEvent.PE:
            case SerialPortEvent.CD:
            case SerialPortEvent.CTS:
            case SerialPortEvent.DSR:
            case SerialPortEvent.RI:
            case SerialPortEvent.OUTPUT_BUFFER_EMPTY:
                break;
                
            case SerialPortEvent.DATA_AVAILABLE:
                synchronized (this) {

                    try {
                        while (m_in.available() > 0) {
                            int b = m_in.read();

                        logger.log(Level.INFO, "Scale data event", b );
                        
                            if (b == 0x000D) { // CR ASCII
                                // End of Line
                                synchronized (this) {
                                    m_iStatusScale = SCALE_READY;
                                    logger.log(Level.INFO, "Scale read complete" );
                                    notifyAll();
                                    if( m_Dialog != null )
                                        m_Dialog.setVisible(false);
                                }
                            } else {

                                if( b == 0x2e || (b >= 0x30  && b <= 0x39 ) ) {  // Ascii for period or 0-9 

                                        m_WeightBuffer = m_WeightBuffer + Character.toString ((char) b);
                                     }
                                }
                            }
                        } catch (IOException eIO) {
                            logger.log(Level.SEVERE, "Scale io error", eIO );
                            m_iStatusScale = SCALE_ERROR;
                            if( m_Dialog != null )
                                m_Dialog.setVisible(false);
                        }
                }
                break;
        }
    }
}