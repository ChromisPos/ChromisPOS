//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 uniCenta
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

package uk.chromis.pos.util;

/**
 *
 *   
 */
public class OSValidator {
   
    private String OS = System.getProperty("os.name").toLowerCase();

    /**
     *
     */
    public OSValidator() {
            
        }
        
    /**
     *
     * @return
     */
    public String getOS(){
      if (isWindows()) {
                    return("w");
		} else if (isMac()) {
                    return("m");
		} else if (isUnix()) {
                    return("l");
		} else if (isSolaris()) {
                    return("s");
		} else {
                    return("x");
		}
    }

    /**
     *
     * @return
     */
    public boolean isWindows() {
		return (OS.indexOf("win") >= 0);
	}

    /**
     *
     * @return
     */
    public  boolean isMac() {
		return (OS.indexOf("mac") >= 0); 
	}

    /**
     *
     * @return
     */
    public  boolean isUnix() {
		return (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0 || OS.indexOf("aix") > 0 );
	}

    /**
     *
     * @return
     */
    public  boolean isSolaris() {
		return (OS.indexOf("sunos") >= 0); 
	}

        
        
        
        
}
