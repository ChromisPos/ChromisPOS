/*
**    Chromis POS  - Open Source Point of Sale
**
**    This file is part of Chromis POS Version Chromis V1.5.4
**
**    Copyright (c) 2015-2023 Chromis & previous Openbravo POS related works   
**
**    https://www.chromis.co.uk
**   
**    Chromis POS is free software: you can redistribute it and/or modify
**    it under the terms of the GNU General Public License as published by
**    the Free Software Foundation, either version 3 of the License, or
**    (at your option) any later version.
**
**    Chromis POS is distributed in the hope that it will be useful,
**    but WITHOUT ANY WARRANTY; without even the implied warranty of
**    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
**    GNU General Public License for more details.
**
**    You should have received a copy of the GNU General Public License
**    along with Chromis POS.  If not, see <http://www.gnu.org/licenses/>
**
 */
package uk.chromis.globals;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Currency;
import java.util.Locale;
import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SerializerReadBoolean;
import uk.chromis.data.loader.SerializerReadDouble;
import uk.chromis.data.loader.SerializerReadImage;
import uk.chromis.data.loader.SerializerReadInteger;
import uk.chromis.data.loader.SerializerReadString;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.data.loader.SerializerWriteString;
import uk.chromis.data.loader.Session;
import uk.chromis.data.loader.StaticSentence;
import uk.chromis.data.loader.SessionFactory;

/**
 *
 * @author John
 */
public class SystemProperty {

    private static Object m_result;
    private static Image m_image;
    private static Session session;
    private static Boolean tabFound = false;

    public static final Boolean AMOUNTATTOP;
    public static final Boolean AUTOLOGOFFAFTERKITCHEN;
    public static final Boolean AUTOLOGOFFAFTERPRINT;
    public static final Boolean AUTOLOGOFFAFTERSALE;
    public static final Boolean AUTOLOGOFFINACTIVITYTIMER;
    public static final Boolean AUTOLOGOFFTOTABLES;
    public static final Boolean AUTOREFRESH;
    public static final Boolean AUTOSAVE;
    public static final Boolean CATBYNUMBERORDER;
    public static final Boolean CHANGETAXOPTIONS;
    public static final Boolean CLOSEDCASHBTN;
    public static final Boolean COLOURTICKETLINES;
    public static final Boolean CONSOLIDATED;
    public static final Boolean CREATEORDER;
    public static final Boolean CUSTOMERDISPLAY;
    public static final Boolean CUSTOMSOUNDS;
    public static final Boolean ENABLEAUTOLOGOFF;
    public static final Boolean ENABLECHANGELIMIT;
    public static final Boolean GIFTCARDSENABLED;
    public static final Boolean HIDEDEFAULTPRODUCT;
    public static final Boolean HISTORICPRODUCTICON;
    public static final Boolean HISTORICCATEGORYICON;
    public static final Boolean LAYAWAYPOPUP;
    public static final Boolean LONGNAMES;
    public static final Boolean LOYALTYENABLED;
    public static final Boolean NEWLAYOUT;
    public static final Boolean NEWSCREEN;
    public static final Boolean PAYMENTICONS;
    public static final Boolean PRICEWITH00;
    public static final Boolean PRODUCTUPDATE;
    public static final Boolean RECEIPTPRINTOFF;
    public static final Boolean RESERVATIONS;
    public static final Boolean SCANWITHDASHES;
    public static final Boolean SCONOFF;
    public static final Boolean SCRESTAURANT;
    public static final Boolean SHOWALLOFFERS;
    public static final Boolean SHOWCUSTOMERDETAILS;
    public static final Boolean SHOWGUI;
    public static final Boolean SHOWWAITERDETAILS;
    public static final Boolean TAXINCLUDED;
    public static final Boolean TESTMODE;
    public static final Boolean TEXTOPAQUE;
    public static final Boolean TEXTOVERLAY;
    public static final Boolean TRANSPARENTBUTTONS;
    public static final Boolean UNIUEINSTANCE;
    public static final Boolean USEFOBS;
    public static final Boolean USEPICKUPFORLAYAWAY;
    public static final Boolean ACCOUNTLOYALTY;
    public static final Boolean PICKUPENABLED;
    public static final Boolean LOYALTYONACCOUNTENABLED;
    public static final Boolean TABLELOCKING;
    public static final Boolean DEFAULTPRODUCT;
    public static final Boolean EXCLUDESC;
    public static final Boolean SHAREDTICKETBYUSER;
    public static final Boolean ALLOWSENTITEMREFUND;
    public static final Boolean REGISTERCUSTOMER;
    public static final Boolean PRODUCTSCRIPT;
    public static final Boolean NEWLOGIN;
    public static final Boolean SCREENDRAG;
    public static final Boolean HANDLINGFEES;
    public static final Boolean TAXEXEMPTION;
    public static final Boolean LOTTOPAYOUT;

    public static final int AUTOLOGOFFPERIOD;
    public static final int CHANGELIMIT;
    public static final int DRAWEROPENEDDAYS;
    public static final int EARNXPOINTS;
    public static final int LINESIZE;
    public static final int LOYALTYEXPIRYPERIOD;
    public static final int PICKUPSIZE;
    public static final int PRODUCTBUTTONHEIGHT;
    public static final int PRODUCTBUTTONWIDTH;
    public static final int RECEIPTSIZE;
    public static final int REMOVEDLINEDAYS;
    public static final int SCRRECEIPTCOLS;
    public static final int VOUCHERPOINTS;
    public static final int SWINGXCALIBRATION;
    public static final int SWINGYCALIBRATION;
    public static final int RECEIPTAUTOCLOSE;
    public static final int SWINGFONTSIZE;

    public static final Double OVERSCANAMOUNT;
    public static final Double REDEEMVALUE;
    public static final Double SCRATE;
    public static final Double HANDLINGFEE;

    public static final ImageIcon STARTLOGO;
    public static final ImageIcon COMPANYLOGO;
    public static final String BUTTONTEXTCOLOUR;
    public static final String COMMERCEID;
    public static final String COMMERCEPASSWORD;
    public static final String CURRENCY;
    public static final String CUSTOMERCOLOUR;
    public static final String DATE;
    public static final String DATETIME;
    public static final String DOUBLE;
    public static final String GATEWAY;
    public static final String GIFTCARDSTART;
    public static final String ICONCOLOUR;
    public static final String INTEGER;
    public static final String LAF;
    public static final String LOYALTYSTART;
    public static final String MAGREADER;
    public static final String PERCENT;
    public static final String SALESLAYOUT;
    public static final String SCREENMODE;
    public static final String SENTBACKGROUNDCOLOUR;
    public static final String SENTFONTCOLOUR;
    public static final String STARTTEXT;
    public static final String TABLECOLOUR;
    public static final String TIME;
    public static final String USERCOUNTRY;
    public static final String USERLANGUAGE;
    public static final String USERVARIANT;
    public static final String WAITERCOLOUR;
    public static final String WAITINGBACKGROUNDCOLOUR;
    public static final String WAITINGFONTCOLOUR;
    public static final String SENTSELECTEDCOLOUR;
    public static final String WAITINGSELECTEDCOLOUR;
    public static final String SELECTEDLANGUAGE;
    public static final String POPREFIX;
    public static final String SWINGFONT;

    public static final String MAGNETICLEADING;
    public static final String MAGNETICTRAILING;
    public static final String LOYALTYTYPE;

    public static final String CURRENCYSYMBOL;

    static {

        session = SessionFactory.getSession();

        ICONCOLOUR = getString("ICONCOLOUR");
        LAF = getString("LAF");
        SCREENMODE = getString("SCREENMODE");
        USERCOUNTRY = getString("USERCOUNTRY");
        USERLANGUAGE = getString("USERLANGUAGE");
        USERVARIANT = getString("USERVARIANT");

        AMOUNTATTOP = getBoolean("AMOUNTATTOP");
        AUTOLOGOFFAFTERKITCHEN = getBoolean("AUTOLOGOFFAFTERKITCHEN");
        AUTOLOGOFFAFTERPRINT = getBoolean("AUTOLOGOFFAFTERPRINT");
        AUTOLOGOFFAFTERSALE = getBoolean("AUTOLOGOFFAFTERSALE");
        AUTOLOGOFFINACTIVITYTIMER = getBoolean("AUTOLOGOFFINACTIVITYTIMER");
        AUTOLOGOFFTOTABLES = getBoolean("AUTOLOGOFFTOTABLES");
        AUTOREFRESH = getBoolean("AUTOREFRESH");
        AUTOSAVE = getBoolean("AUTOSAVE");
        CATBYNUMBERORDER = getBoolean("CATBYNUMBERORDER");
        CHANGETAXOPTIONS = getBoolean("CHANGETAXOPTIONS");
        CLOSEDCASHBTN = getBoolean("CLOSEDCASHBTN");
        COLOURTICKETLINES = getBoolean("COLOURTICKETLINES");
        CONSOLIDATED = getBoolean("CONSOLIDATED");
        CREATEORDER = getBoolean("CREATEORDER");
        CUSTOMERDISPLAY = getBoolean("CUSTOMERDISPLAY");
        CUSTOMSOUNDS = getBoolean("CUSTOMSOUNDS");
        ENABLEAUTOLOGOFF = getBoolean("ENABLEAUTOLOGOFF");
        ENABLECHANGELIMIT = getBoolean("ENABLECHANGELIMIT");
        GIFTCARDSENABLED = getBoolean("GIFTCARDSENABLED");
        HIDEDEFAULTPRODUCT = getBoolean("HIDEDEFAULTPRODUCT");
        HISTORICPRODUCTICON = getBoolean("HISTORICPRODUCTICON");
        HISTORICCATEGORYICON = getBoolean("HISTORICCATEGORYICON");
        LAYAWAYPOPUP = getBoolean("LAYAWAYPOPUP");
        LONGNAMES = getBoolean("LONGNAMES");
        LOYALTYENABLED = getBoolean("LOYALTYENABLED");
        NEWLAYOUT = getBoolean("NEWLAYOUT");
        NEWSCREEN = getBoolean("NEWSCREEN");
        PAYMENTICONS = getBoolean("PAYMENTICONS");
        PRICEWITH00 = getBoolean("PRICEWITH00");
        PRODUCTUPDATE = getBoolean("PRODUCTUPDATE");
        RECEIPTPRINTOFF = getBoolean("RECEIPTPRINTOFF");
        RESERVATIONS = getBoolean("RESERVATIONS");
        SCANWITHDASHES = getBoolean("SCANWITHDASHES");
        SCONOFF = getBoolean("SCONOFF");
        SCRESTAURANT = getBoolean("SCRESTAURANT");
        SHOWALLOFFERS = getBoolean("SHOWALLOFFERS");
        SHOWCUSTOMERDETAILS = getBoolean("SHOWCUSTOMERDETAILS");
        SHOWGUI = getBoolean("SHOWGUI");
        SHOWWAITERDETAILS = getBoolean("SHOWWAITERDETAILS");
        TAXINCLUDED = getBoolean("TAXINCLUDED");
        TESTMODE = getBoolean("TESTMODE");
        TEXTOPAQUE = getBoolean("TEXTOPAQUE");
        TEXTOVERLAY = getBoolean("TEXTOVERLAY");
        TRANSPARENTBUTTONS = getBoolean("TRANSPARENTBUTTONS");
        UNIUEINSTANCE = getBoolean("UNIUEINSTANCE");
        USEFOBS = getBoolean("USEFOBS");
        USEPICKUPFORLAYAWAY = getBoolean("USEPICKUPFORLAYAWAY");
        ACCOUNTLOYALTY = getBoolean("ACCOUNTLOYALTY");
        PICKUPENABLED = getBoolean("PICKUPENABLED");
        LOYALTYONACCOUNTENABLED = getBoolean("LOYALTYONACCOUNTENABLED");
        TABLELOCKING = getBoolean("TABLELOCKING");
        DEFAULTPRODUCT = getBoolean("DEFAULTPRODUCT");
        EXCLUDESC = getBoolean("EXCLUDESC");
        SHAREDTICKETBYUSER = getBoolean("SHAREDTICKETBYUSER");
        ALLOWSENTITEMREFUND = getBoolean("ALLOWSENTITEMREFUND");
        REGISTERCUSTOMER = getBoolean("REGISTERCUSTOMER");
        PRODUCTSCRIPT = getBoolean("PRODUCTSCRIPT");
        NEWLOGIN = getBoolean("NEWLOGIN");
        SCREENDRAG = getBoolean("SCREENDRAG");
        HANDLINGFEES = getBoolean("HANDLINGFEES");
        TAXEXEMPTION = getBoolean("TAXEXEMPTION");
        LOTTOPAYOUT = getBoolean("LOTTOPAYOUT");

        AUTOLOGOFFPERIOD = getInt("AUTOLOGOFFPERIOD");
        CHANGELIMIT = getInt("CHANGELIMIT");
        DRAWEROPENEDDAYS = getInt("DRAWEROPENEDDAYS");
        LINESIZE = getInt("LINESIZE");
        LOYALTYEXPIRYPERIOD = getInt("LOYALTYEXPIRYPERIOD");
        PICKUPSIZE = getInt("PICKUPSIZE");
        PRODUCTBUTTONHEIGHT = getInt("PRODUCTBUTTONHEIGHT");
        PRODUCTBUTTONWIDTH = getInt("PRODUCTBUTTONWIDTH");
        RECEIPTSIZE = getInt("RECEIPTSIZE");
        REMOVEDLINEDAYS = getInt("REMOVEDLINEDAYS");
        SCRATE = getDouble("SCRATE");
        SCRRECEIPTCOLS = getInt("SCRRECEIPTCOLS");
        SWINGXCALIBRATION = getInt("SWINGXCALIBRATION");
        SWINGYCALIBRATION = getInt("SWINGYCALIBRATION");
        RECEIPTAUTOCLOSE = getInt("RECEIPTAUTOCLOSE");

        BUTTONTEXTCOLOUR = getString("BUTTONTEXTCOLOUR");
        COMMERCEID = getString("COMMERCEID");
        COMMERCEPASSWORD = getString("COMMERCEPASSWORD");
        CURRENCY = getString("CURRENCY");
        CUSTOMERCOLOUR = getString("CUSTOMERCOLOUR");
        DATE = getString("DATE");
        DATETIME = getString("DATETIME");
        DOUBLE = getString("DOUBLE");
        GATEWAY = getString("GATEWAY");
        GIFTCARDSTART = getString("GIFTCARDSTART");
        INTEGER = getString("INTEGER");
        LOYALTYSTART = getString("LOYALTYSTART");
        MAGREADER = getString("MAGREADER");
        PERCENT = getString("PERCENT");
        SALESLAYOUT = getString("SALESLAYOUT");
        SENTBACKGROUNDCOLOUR = getString("SENTBACKGROUNDCOLOUR");
        SENTFONTCOLOUR = getString("SENTFONTCOLOUR");
        STARTLOGO = getImageIcon("STARTLOGO");
        COMPANYLOGO = getImageIcon("COMPANYLOGO");
        STARTTEXT = getString("STARTTEXT");
        TABLECOLOUR = getString("TABLECOLOUR");
        TIME = getString("TIME");
        WAITERCOLOUR = getString("WAITERCOLOUR");
        WAITINGBACKGROUNDCOLOUR = getString("WAITINGBACKGROUNDCOLOUR");
        WAITINGFONTCOLOUR = getString("WAITINGFONTCOLOUR");
        SENTSELECTEDCOLOUR = getString("SENTSELECTEDCOLOUR");
        WAITINGSELECTEDCOLOUR = getString("WAITINGSELECTEDCOLOUR");
        SELECTEDLANGUAGE = getString("SELECTEDLANGUAGE");
        POPREFIX = getString("POPREFIX");
        SWINGFONT = getString("SWINGFONT");

        EARNXPOINTS = getInt("EARNXPOINTS");
        LOYALTYTYPE = getString("LOYALTYTYPE");
        MAGNETICLEADING = getString("MAGNETICLEADING");
        MAGNETICTRAILING = getString("MAGNETICTRAILING");
        OVERSCANAMOUNT = getDouble("OVERSCANAMOUNT");
        HANDLINGFEE = getDouble("HANDLINGFEE");
        REDEEMVALUE = getDouble("REDEEMVALUE");
        VOUCHERPOINTS = getInt("VOUCHERPOINTS");
        SWINGFONTSIZE = getInt("SWINGFONTSIZE");

        Currency cur = Currency.getInstance(new Locale(USERLANGUAGE, USERCOUNTRY));
        CURRENCYSYMBOL = cur.getSymbol();

    }

    private static ImageIcon getImageIcon(String constant) {
        try {
            m_image = (Image) new PreparedSentence(session,
                    "select userimage from systemblobs where constant = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadImage.INSTANCE).find(constant);
        } catch (BasicException e) {
            if (tabFound) {
                System.out.println("Error in Constant read ImageIcon!! ");
            }
        }
        return (m_image == null ? null : new ImageIcon(m_image));
    }

    private static String getString(String constant) {
        try {
            m_result = new PreparedSentence(session,
                    "select uservalue from systemproperties where constant = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(constant);
        } catch (BasicException e) {
            if (tabFound) {
                System.out.println("Error in Constant read string!! ");
            }
        }
        return (m_result == null ? "" : (String) m_result);
    }

    private static char getChar(String constant) {
        try {
            m_result = new PreparedSentence(session,
                    "select uservalue from systemproperties where constant = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadString.INSTANCE).find(constant);
        } catch (BasicException e) {
            if (tabFound) {
                System.out.println("Error in Constant read string!! ");
            }
        }
        return (m_result == null ? Character.MIN_VALUE : ((String) m_result).charAt(0));
    }

    public static Boolean getBoolean(String constant) {
        try {
            m_result = new PreparedSentence(session,
                    "select uservalue from systemproperties where constant = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadBoolean.INSTANCE).find(constant);
        } catch (BasicException e) {
            if (tabFound) {
                System.out.println("Error in Constant read Boolean!! ");
            }
        }
        return (m_result == null ? false : (Boolean) m_result);
    }

    private static int getInt(String constant) {
        try {
            m_result = new PreparedSentence(session,
                    "select uservalue from systemproperties where constant = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadInteger.INSTANCE).find(constant);
        } catch (BasicException e) {
            if (tabFound) {
                System.out.println("Error in Constant read integer !! ");
            }
        }
        return (m_result == null ? 0 : (int) m_result);
    }

    private static Double getDouble(String constant) {
        try {
            m_result = new PreparedSentence(session,
                    "select uservalue from systemproperties where constant = ? ",
                    SerializerWriteString.INSTANCE,
                    SerializerReadDouble.INSTANCE).find(constant);
        } catch (BasicException e) {
            if (tabFound) {
                System.out.println("Error in Constant read double !! ");
            }
        }
        return (m_result == null ? 0.00 : (double) m_result);
    }

    public static void updateSystemBlob(String constant, File selectedFile) {
        try {
            BufferedImage img = ImageIO.read(selectedFile);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(img, "png", baos);
            byte[] imageInByte = baos.toByteArray();
            int i = new PreparedSentence(session, "update systemblobs set userimage = ? where constant = ? ",
                    new SerializerWriteBasic(new Datas[]{
                Datas.BYTES,
                Datas.STRING
            })).exec(imageInByte, constant);
        } catch (IOException | BasicException ex) {
            System.out.println("Error updating systemblob - " + constant);
        }
    }

    public static void resetSystemBlob(String constant) {
        try {
            int i = new PreparedSentence(session, "update systemblobs set userimage = defaultimage where constant = ? ",
                    SerializerWriteString.INSTANCE,
                    null).exec(constant);
        } catch (BasicException ex) {
            System.out.println("Error updating systemblob - " + constant);
        }
    }

    public static void updatePropertyValueByConstant(String constant, char value) {
        try {
            new StaticSentence(session, "update systemproperties set uservalue = ? where constant = ? ", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(value, constant);
        } catch (BasicException ex) {
            System.out.println("Update error !!");
        }
    }

    public static void updatePropertyValueByConstant(String constant, String value) {
        try {
            new StaticSentence(session, "update systemproperties set uservalue = ? where constant = ? ", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(value, constant);
        } catch (BasicException ex) {
            System.out.println("Update error !!");
        }
    }

    public static void updatePropertyValueByConstant(String constant, Integer value) {
        try {
            new StaticSentence(session, "update systemproperties set uservalue = ? where constant = ? ", new SerializerWriteBasic(new Datas[]{
                Datas.STRING,
                Datas.STRING
            })).exec(Integer.toString(value), constant);
        } catch (BasicException ex) {
            System.out.println("Update error !!");
        }
    }

    public static void updatePropertyValueByConstant(String constant, Boolean value) {
        try {
            new StaticSentence(session, "update systemproperties set uservalue = ? where constant = ? ", new SerializerWriteBasic(new Datas[]{
                Datas.BOOLEAN,
                Datas.STRING
            })).exec(value, constant);
        } catch (BasicException ex) {
            System.out.println("Update error !!");
        }
    }

    public static void updatePropertySetToNull(String constant) {
        try {
            new StaticSentence(session, "update systemproperties set uservalue = null where constant = ? ",
                    SerializerWriteString.INSTANCE
            ).exec(constant);
        } catch (BasicException ex) {
            System.out.println("Update error !!");
        }
    }

}
