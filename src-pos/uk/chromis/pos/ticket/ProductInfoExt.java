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
package uk.chromis.pos.ticket;

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.DataRead;
import uk.chromis.data.loader.ImageUtils;
import uk.chromis.data.loader.SerializerRead;
import uk.chromis.format.Formats;
import java.awt.image.BufferedImage;
import java.util.Properties;

/**
 *
 * @author adrianromero
 *
 */
public class ProductInfoExt {

    private static final long serialVersionUID = 7587696873036L;

    protected String m_ID;
    protected String m_sRef;
    protected String m_sCode;
    protected String m_sCodetype;
    protected String m_sName;
    protected boolean m_bCom;
    protected boolean m_bScale;
    protected double m_dPriceBuy;
    protected double m_dPriceSell;
    protected String categoryid;
    protected String taxcategoryid;
    protected String attributesetid;
    protected BufferedImage m_Image;
    protected double m_stockCost;
    protected double m_stockVolume;
    protected boolean m_bKitchen;
    private boolean m_bService;
    protected Properties attributes;
    protected String m_sDisplay;
    protected boolean m_bVprice;
    protected boolean m_bVerpatrib;
    protected String m_sTextTip;
    protected boolean m_bWarranty;
    public double m_dStockUnits;
    protected String m_sAlias;
    protected boolean m_bAlwaysAvailable;
    protected boolean m_canDiscount;
    protected String m_discounted;

    public ProductInfoExt() {
        m_ID = null;                    //1
        m_sRef = "0000";                //2
        m_sCode = "0000";               //3
        m_sCodetype = null;             //4
        m_sName = null;                 //5
        m_bCom = false;                 //6
        m_bScale = false;               //7
        categoryid = null;              //8
        taxcategoryid = null;           //9
        attributesetid = null;          //10
        m_dPriceBuy = 0.0;              //11
        m_dPriceSell = 0.0;             //12
        m_stockCost = 0.0;              //13
        m_stockVolume = 0.0;            //14
        m_Image = null;                 //15
        m_bKitchen = false;             //16
        m_bService = false;             //17
        m_sDisplay = null;              //18
        attributes = new Properties();  //19
        m_bVprice = false;              //10
        m_bVerpatrib = false;           //21
        m_sTextTip = null;              //22
        m_bWarranty = false;            //23
        m_dStockUnits = 0.0;            //24
        m_sAlias=null;                  //25
        m_bAlwaysAvailable = false;     //26
        m_canDiscount = true;           //27
        m_discounted = "no";            //28
    }

    /**
     *
     * @return
     */
    public final String getID() {
        return m_ID;
    }

    public final void setID(String id) {
        m_ID = id;
    }

    public final String getReference() {
        return m_sRef;
    }

    public final void setReference(String sRef) {
        m_sRef = sRef;
    }

    public final String getCode() {
        return m_sCode;
    }

    public final void setCode(String sCode) {
        m_sCode = sCode;
    }

    public final String getCodetype() {
        return m_sCodetype;
    }

    public final void setCodetype(String sCodetype) {
        m_sCodetype = sCodetype;
    }

    public final String getName() {
        return m_sName;
    }

    public final void setName(String sName) {
        m_sName = sName;
    }

    public final String getDisplay() {
        return m_sDisplay;
    }

    public final void setDisplay(String sDisplay) {
        m_sDisplay = sDisplay;
    }

    public final boolean isCom() {
        return m_bCom;
    }

    public final void setCom(boolean bValue) {
        m_bCom = bValue;
    }

    public final boolean isScale() {
        return m_bScale;
    }

    public final void setScale(boolean bValue) {
        m_bScale = bValue;
    }

    public final boolean isKitchen() {
        return m_bKitchen;
    }

    public final void setKitchen(boolean bValue) {
        m_bKitchen = bValue;
    }

    public final boolean isService() {
        return m_bService;
    }

    public final void setService(boolean bValue) {
        m_bService = bValue;
    }

    public final boolean isVprice() {
        return m_bVprice;
    }

    public final boolean isVerpatrib() {
        return m_bVerpatrib;
    }

    public final String getTextTip() {
        return m_sTextTip;
    }

    public final boolean getWarranty() {
        return m_bWarranty;
    }

    public final void setWarranty(boolean bValue) {
        m_bWarranty = bValue;
    }

    public final String getCategoryID() {
        return categoryid;
    }

    public final void setCategoryID(String sCategoryID) {
        categoryid = sCategoryID;
    }

    public final String getTaxCategoryID() {
        return taxcategoryid;
    }

    public final void setTaxCategoryID(String value) {
        taxcategoryid = value;
    }

    public final String getAttributeSetID() {
        return attributesetid;
    }

    public final void setAttributeSetID(String value) {
        attributesetid = value;
    }

    public final double getPriceBuy() {
        return m_dPriceBuy;
    }

    public final void setPriceBuy(double dPrice) {
        m_dPriceBuy = dPrice;
    }

    public final double getPriceSell() {
        return m_dPriceSell;
    }

    public final void setPriceSell(double dPrice) {
        m_dPriceSell = dPrice;
    }

    public final Double getStockUnits() {
        return m_dStockUnits;

    }

    public final void setStockUnits(double dStockUnits) {
        m_dStockUnits = dStockUnits;
    }

    public final double getStockVolume() {
        return m_stockVolume;
    }

    public final void setStockVolume(double dStockVolume) {
        m_stockVolume = dStockVolume;
    }

    public final double getStockCost() {
        return m_stockCost;
    }

    public final void setStockCost(double dPrice) {
        m_stockCost = dPrice;
    }

    public final void setTextTip(String value) {
        m_sTextTip = value;
    }

    public final double getPriceSellTax(TaxInfo tax) {
        return m_dPriceSell * (1.0 + tax.getRate());
    }

    public String printPriceSell() {
        return Formats.CURRENCY.formatValue(new Double(getPriceSell()));
    }

    public String printPriceSellTax(TaxInfo tax) {
        return Formats.CURRENCY.formatValue(new Double(getPriceSellTax(tax)));
    }

    public BufferedImage getImage() {
        return m_Image;
    }

    public void setImage(BufferedImage img) {
        m_Image = img;
    }

    public String getProperty(String key) {
        return attributes.getProperty(key);
    }

    public String getProperty(String key, String defaultvalue) {
        return attributes.getProperty(key, defaultvalue);
    }

    public void setProperty(String key, String value) {
        attributes.setProperty(key, value);
    }

    public Properties getProperties() {
        return attributes;
    }

     public final String getAlias() {
        return m_sAlias;
    }

     public final void setAlias(String alias) {
        m_sAlias = alias;
    }     
    
        public final boolean getAlwaysAvailable() {
        return m_bAlwaysAvailable;
    }

    public final void setAlwaysAvailable(boolean bValue) {
        m_bAlwaysAvailable = bValue;
    }
 
    public final boolean getCanDiscount() {
        return m_canDiscount;
    }  
    
     public final String getDiscounted() {
        return m_discounted;
    }   
    
     public void setDiscounted(String discount) {
        m_discounted = discount;
    }
    
    public static SerializerRead getSerializerRead() {
        return new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                ProductInfoExt product = new ProductInfoExt();
                product.m_ID = dr.getString(1);
                product.m_sRef = dr.getString(2);
                product.m_sCode = dr.getString(3);
                product.m_sCodetype = dr.getString(4);
                product.m_sName = dr.getString(5);
                product.m_bCom = dr.getBoolean(6);
                product.m_bScale = dr.getBoolean(7);
                product.m_dPriceBuy = dr.getDouble(8);
                product.m_dPriceSell = dr.getDouble(9);
                product.taxcategoryid = dr.getString(10);
                product.categoryid = dr.getString(11);
                product.attributesetid = dr.getString(12);
                product.m_Image = ImageUtils.readImage(dr.getBytes(13));
                product.attributes = ImageUtils.readProperties(dr.getBytes(14));
                product.m_bKitchen = dr.getBoolean(15);
                product.m_bService = dr.getBoolean(16);
                product.m_sDisplay = dr.getString(17);
                product.m_bVprice = dr.getBoolean(18);
                product.m_bVerpatrib = dr.getBoolean(19);
                product.m_sTextTip = dr.getString(20);
                product.m_bWarranty = dr.getBoolean(21);
                product.m_dStockUnits = dr.getDouble(22);
                product.m_sAlias = dr.getString(23);
                product.m_bAlwaysAvailable = dr.getBoolean(24);
                product.m_discounted = dr.getString(25);
                product.m_canDiscount = dr.getBoolean(26);

                return product;
            }
        };
    }

    @Override
    public final String toString() {
        return m_sRef + " - " + m_sName;
    }
}
