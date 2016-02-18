//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 
//    http://www.chromis.co.uk liquibase
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
package uk.chromis.pos.forms;

import uk.chromis.pos.promotion.PromotionInfo;
import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.*;
import uk.chromis.data.model.Field;
import uk.chromis.data.model.Row;
import uk.chromis.format.Formats;
import uk.chromis.pos.customers.CustomerInfoExt;
import uk.chromis.pos.customers.CustomerTransaction;
import uk.chromis.pos.inventory.*;
import uk.chromis.pos.mant.FloorsInfo;
import uk.chromis.pos.payment.PaymentInfo;
import uk.chromis.pos.payment.PaymentInfoTicket;
import uk.chromis.pos.ticket.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

/**
 *
 * @author adrianromero
 */
public class DataLogicSales extends BeanFactoryDataSingle {

    protected Session s;
    protected Datas[] auxiliarDatas;
    protected Datas[] stockdiaryDatas;
    protected SentenceExec m_sellvoucher;
    protected SentenceExec m_insertcat;
    protected Datas[] paymenttabledatas;
    private SentenceFind m_productname;
    protected Datas[] stockdatas;
    protected Row productsRow;
    private String pName;
    private Double getTotal;
    private Double getTendered;
    private String getRetMsg;
    public static final String DEBT = "debt";
    public static final String DEBT_PAID = "debtpaid";
    protected static final String PREPAY = "prepay";
    private static final Logger logger = Logger.getLogger("uk.chromis.pos.forms.DataLogicSales");
    private String getCardName;

    private SentenceExec m_updateRefund;

    // Use this INDEX_xxx instead of numbers to access arrays of product information
    public static int FIELD_COUNT = 0;
    public static int INDEX_ID = FIELD_COUNT++;
    public static int INDEX_REFERENCE = FIELD_COUNT++;
    public static int INDEX_CODE = FIELD_COUNT++;
    public static int INDEX_CODETYPE = FIELD_COUNT++;
    public static int INDEX_NAME = FIELD_COUNT++;
    public static int INDEX_ISCOM = FIELD_COUNT++;
    public static int INDEX_ISSCALE = FIELD_COUNT++;
    public static int INDEX_PRICEBUY = FIELD_COUNT++;
    public static int INDEX_PRICESELL = FIELD_COUNT++;
    public static int INDEX_CATEGORY = FIELD_COUNT++;
    public static int INDEX_TAXCAT = FIELD_COUNT++;
    public static int INDEX_ATTRIBUTESET_ID = FIELD_COUNT++;
    public static int INDEX_IMAGE = FIELD_COUNT++;
    public static int INDEX_ATTRIBUTES = FIELD_COUNT++;
    public static int INDEX_STOCKCOST = FIELD_COUNT++;
    public static int INDEX_STOCKVOLUME = FIELD_COUNT++;
    public static int INDEX_ISCATALOG = FIELD_COUNT++;
    public static int INDEX_CATORDER = FIELD_COUNT++;
    public static int INDEX_ISKITCHEN = FIELD_COUNT++;
    public static int INDEX_ISSERVICE = FIELD_COUNT++;
    public static int INDEX_DISPLAY = FIELD_COUNT++;
    public static int INDEX_ISVPRICE = FIELD_COUNT++;
    public static int INDEX_ISVERPATRIB = FIELD_COUNT++;
    public static int INDEX_TEXTTIP = FIELD_COUNT++;
    public static int INDEX_WARRANTY = FIELD_COUNT++;
    public static int INDEX_STOCKUNITS = FIELD_COUNT++;
    public static int INDEX_ALIAS = FIELD_COUNT++;
    public static int INDEX_ALWAYSAVAILABLE = FIELD_COUNT++;
    public static int INDEX_DISCOUNTED = FIELD_COUNT++;
    public static int INDEX_CANDISCOUNT = FIELD_COUNT++;
    public static int INDEX_ISPACK = FIELD_COUNT++;
    public static int INDEX_PACKQUANTITY = FIELD_COUNT++;
    public static int INDEX_PACKPRODUCT = FIELD_COUNT++;
    public static int INDEX_PROMOTIONID = FIELD_COUNT++;

    /**
     * Creates a new instance of SentenceContainerGeneric
     */
    public DataLogicSales() {
        stockdiaryDatas = new Datas[]{
            Datas.STRING,
            Datas.TIMESTAMP,
            Datas.INT,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.STRING};
        paymenttabledatas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.TIMESTAMP,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.STRING};
        stockdatas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.DOUBLE,
            Datas.DOUBLE,
            Datas.DOUBLE};
        auxiliarDatas = new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING,
            Datas.STRING};

        productsRow = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING, false, true, true),
                new Field(AppLocal.getIntString("label.prodbarcodetype"), Datas.STRING, Formats.STRING, false, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true),
                new Field("ISCOM", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("ISSCALE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field(AppLocal.getIntString("label.prodpricebuy"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
                new Field(AppLocal.getIntString("label.prodpricesell"), Datas.DOUBLE, Formats.CURRENCY, false, true, true),
                new Field(AppLocal.getIntString("label.prodcategory"), Datas.STRING, Formats.STRING, false, false, true),
                new Field(AppLocal.getIntString("label.taxcategory"), Datas.STRING, Formats.STRING, false, false, true),
                new Field(AppLocal.getIntString("label.attributeset"), Datas.STRING, Formats.STRING, false, false, true),
                new Field("IMAGE", Datas.IMAGE, Formats.NULL),
                new Field("ATTRIBUTES", Datas.BYTES, Formats.NULL),
                new Field("STOCKCOST", Datas.DOUBLE, Formats.CURRENCY),
                new Field("STOCKVOLUME", Datas.DOUBLE, Formats.DOUBLE),
                new Field("ISCATALOG", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("CATORDER", Datas.INT, Formats.INT),
                new Field("ISKITCHEN", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("ISSERVICE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field(AppLocal.getIntString("label.display"), Datas.STRING, Formats.STRING, false, true, true),
                new Field("ISVPRICE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("ISVERPATRIB", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("TEXTTIP", Datas.STRING, Formats.STRING),
                new Field("WARRANTY", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field(AppLocal.getIntString("label.stockunits"), Datas.DOUBLE, Formats.DOUBLE),
                new Field("ALIAS", Datas.STRING, Formats.STRING), //26
                new Field("ALWAYSAVAILABLE", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("DISCOUNTED", Datas.STRING, Formats.STRING),
                new Field("CANDISCOUNT", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("ISPACK", Datas.BOOLEAN, Formats.BOOLEAN),
                new Field("PACKQUANTITY", Datas.DOUBLE, Formats.DOUBLE),
                new Field("PACKPRODUCT", Datas.STRING, Formats.STRING),
                new Field("PROMOTIONID", Datas.STRING, Formats.STRING)
        );

        // If this fails there is a coding error - have you added a column
        // to the PRODUCTS table and not added an INDEX_xxx for it?
        assert (FIELD_COUNT == productsRow.getFields().length);
    }

    private String getSelectFieldList() {
        String sel = "P.ID, "
                + "P.REFERENCE, "
                + "P.CODE, "
                + "P.CODETYPE, "
                + "P.NAME, "
                + "P.ISCOM, "
                + "P.ISSCALE, "
                + "P.PRICEBUY, "
                + "P.PRICESELL, "
                + "P.CATEGORY, "
                + "P.TAXCAT, "
                + "P.ATTRIBUTESET_ID, "
                + "P.IMAGE, "
                + "P.ATTRIBUTES, "
                + "P.STOCKCOST, "
                + "P.STOCKVOLUME, "
                + "P.ISCATALOG, "
                + "P.CATORDER, "
                + "P.ISKITCHEN, "
                + "P.ISSERVICE, "
                + "P.DISPLAY, "
                + "P.ISVPRICE, "
                + "P.ISVERPATRIB, "
                + "P.TEXTTIP, "
                + "P.WARRANTY, "
                + "P.STOCKUNITS, "
                + "P.ALIAS, "
                + "P.ALWAYSAVAILABLE, "
                + "P.DISCOUNTED, "
                + "P.CANDISCOUNT, "
                + "P.ISPACK, P.PACKQUANTITY, P.PACKPRODUCT, "
                + "P.PROMOTIONID ";
        return sel;
    }

    /**
     *
     * @param s
     */
    @Override
    public void init(Session s) {
        this.s = s;

        m_updateRefund = new StaticSentence(s, "UPDATE TICKETLINES SET REFUNDQTY = ? WHERE TICKET = ? AND LINE = ?  ", new SerializerWriteBasic(new Datas[]{
            Datas.DOUBLE,
            Datas.STRING,
            Datas.INT
        }));

        m_sellvoucher = new StaticSentence(s, "INSERT INTO VOUCHERS ( VOUCHER, SOLDTICKETID) "
                + "VALUES (?, ?)", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING}));

        m_productname = new StaticSentence(s, "SELECT NAME FROM PRODUCTS WHERE ID = ? ",
                SerializerWriteString.INSTANCE, SerializerReadString.INSTANCE);

        m_insertcat = new StaticSentence(s, "INSERT INTO CATEGORIES ( ID, NAME, CATSHOWNAME ) "
                + "VALUES (?, ?, ?)", new SerializerWriteBasic(new Datas[]{
            Datas.STRING,
            Datas.STRING,
            Datas.BOOLEAN}));
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public final Object getCategoryColour(String id) throws BasicException {

        return new PreparedSentence(s,
                "SELECT COLOUR FROM CATEGORIES WHERE ID = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).find(id);
    }

    /**
     *
     * @return
     */
    public final Row getProductsRow() {
        return productsRow;
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public final ProductInfoExt getProductInfo(String id) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM STOCKCURRENT C LEFT JOIN PRODUCTS P ON (C.PRODUCT = P.ID) "
                + "WHERE ID = ?", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).find(id);
    }

    /**
     *
     * @param sCode
     * @return
     * @throws BasicException
     */
    public final ProductInfoExt getProductInfoByCode(String sCode) throws BasicException {
        
        if (sCode.startsWith("977")) {
            // This is an ISSN barcode (news and magazines)
            // the first 3 digits correspond to the 977 prefix assigned to serial publications,
            // the next 7 digits correspond to the ISSN of the publication
            // Anything after that is publisher dependant - we strip everything after 
            // the 10th character
            sCode = sCode.substring(0, 10);
        }

        return (ProductInfoExt) new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM STOCKCURRENT C RIGHT JOIN PRODUCTS P ON (C.PRODUCT = P.ID) "
                + " WHERE CODE = ? ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).find(sCode);
    }

    /**
     *
     * @param sReference
     * @return
     * @throws BasicException
     */
    public final ProductInfoExt getProductInfoByReference(String sReference) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM STOCKCURRENT C RIGHT JOIN PRODUCTS P ON (C.PRODUCT = P.ID) "
                + "WHERE REFERENCE = ?", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).find(sReference);
    }

    public final ProductInfoExt getProductInfoNoSC(String id) throws BasicException {
        return (ProductInfoExt) new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P WHERE P.ID = ? "
                + "ORDER BY P.ID, P.REFERENCE, P.NAME ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).find(id);
    }

    /**
     *
     * @return @throws BasicException
     */
    public final List<CategoryInfo> getRootCategories() throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES "
                + "WHERE PARENTID IS NULL AND CATSHOWNAME = " + s.DB.TRUE() + " "
                + "ORDER BY NAME", null, CategoryInfo.getSerializerRead()).list();
    }

    /**
     *
     * @return @throws BasicException
     */
    public final List<CategoryInfo> getRootCategoriesByCatOrder() throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES "
                + "WHERE PARENTID IS NULL AND CATSHOWNAME = " + s.DB.TRUE() + " AND CATORDER IS NOT NULL "
                + "ORDER BY CATORDER", null, CategoryInfo.getSerializerRead()).list();
    }

    public final List<CategoryInfo> getRootCategoriesByName() throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES "
                + "WHERE PARENTID IS NULL AND CATSHOWNAME = " + s.DB.TRUE() + " AND CATORDER IS NULL "
                + "ORDER BY NAME", null, CategoryInfo.getSerializerRead()).list();
    }

    /**
     *
     * @param category
     * @return
     * @throws BasicException
     */
    public final List<CategoryInfo> getSubcategories(String category) throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES WHERE PARENTID = ? ORDER BY NAME", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).list(category);
    }

    public final List<CategoryInfo> getSubcategoriesByCatOrder(String category) throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES WHERE PARENTID = ? AND CATORDER IS NOT NULL ORDER BY CATORDER", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).list(category);
    }

    public final List<CategoryInfo> getSubcategoriesByName(String category) throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES WHERE PARENTID = ? AND CATORDER IS NULL ORDER BY NAME", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).list(category);
    }

    /**
     *
     * @param category
     * @return
     * @throws BasicException
     */
    public List<ProductInfoExt> getProductCatalog(String category) throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P "
                + "WHERE (P.ISCATALOG = " + s.DB.TRUE() + " AND P.CATEGORY = ?) OR (P.ALWAYSAVAILABLE = " + s.DB.TRUE() + ") "
                + "ORDER BY P.CATORDER, P.NAME ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).list(category);
    }

    /**
     *
     * @param category
     * @return
     * @throws BasicException
     */
    public List<ProductInfoExt> getAllProductCatalogByCatOrder() throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P "
                + "WHERE P.ISCATALOG = " + s.DB.TRUE() + " "
                + "ORDER BY P.CATORDER, P.NAME ", null, ProductInfoExt.getSerializerRead()).list();
    }

    public List<ProductInfoExt> getAllNonProductCatalog() throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P "
                + "WHERE P.ISCATALOG = " + s.DB.FALSE() + " "
                + "ORDER BY P.CATEGORY, P.NAME ", null, ProductInfoExt.getSerializerRead()).list();
    }

    /**
     *
     * @param category
     * @return
     * @throws BasicException
     */
    public List<ProductInfoExt> getAllProductCatalog() throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P "
                + "WHERE P.ISCATALOG = " + s.DB.TRUE() + " "
                + "ORDER BY P.CATEGORY, P.NAME ", null, ProductInfoExt.getSerializerRead()).list();
    }

    /**
     *
     * @param category
     * @return
     * @throws BasicException
     */
    public List<ProductInfoExt> getProductCatalogAlways() throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM CATEGORIES C INNER JOIN PRODUCTS P ON (P.CATEGORY = C.ID) "
                + "WHERE P.ALWAYSAVAILABLE = " + s.DB.TRUE() + " "
                + "ORDER BY  C.NAME, P.NAME",
                null,
                ProductInfoExt.getSerializerRead()).list();

    }

    public List<ProductInfoExt> getProductNonCatalog(String category) throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P "
                + "WHERE P.ISCATALOG = " + s.DB.FALSE() + " "
                + "AND P.CATEGORY = ? "
                + "ORDER BY P.NAME ", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).list(category);
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public List<ProductInfoExt> getProductComments(String id) throws BasicException {
        return new PreparedSentence(s, "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P, PRODUCTS_COM M "
                + "WHERE P.ISCATALOG = " + s.DB.TRUE() + " "
                + "AND P.ID = M.PRODUCT2 AND M.PRODUCT = ? "
                + "AND P.ISCOM = " + s.DB.TRUE() + " "
                + "ORDER BY P.CATORDER, P.NAME", SerializerWriteString.INSTANCE, ProductInfoExt.getSerializerRead()).list(id);
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public final CategoryInfo getCategoryInfo(String id) throws BasicException {
        return (CategoryInfo) new PreparedSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES "
                + "WHERE ID = ? "
                + "ORDER BY NAME", SerializerWriteString.INSTANCE, CategoryInfo.getSerializerRead()).find(id);
    }

    public final SentenceList getProductList() {
        return new StaticSentence(s, new QBFBuilder(
                "SELECT "
                + getSelectFieldList()
                + "FROM STOCKCURRENT C RIGHT OUTER JOIN PRODUCTS P ON (C.PRODUCT = P.ID) "
                + "WHERE ?(QBF_FILTER) "
                + "ORDER BY P.REFERENCE, P.NAME",
                new String[]{"P.NAME", "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY", "P.CODE", "C.UNITS"}), new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.DOUBLE,}), ProductInfoExt.getSerializerRead());
    }

    public SentenceList getProductListNormal() {
        return new StaticSentence(s, new QBFBuilder(
                "SELECT "
                + getSelectFieldList()
                + "FROM STOCKCURRENT C RIGHT OUTER JOIN PRODUCTS P ON (C.PRODUCT = P.ID) "
                + "WHERE P.ISCOM = " + s.DB.FALSE() + " AND ?(QBF_FILTER) "
                + "ORDER BY P.REFERENCE, P.NAME",
                new String[]{"P.NAME", "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY", "P.CODE", "C.UNITS"}),
                new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.DOUBLE
        }), ProductInfoExt.getSerializerRead());
    }

    public SentenceList getProductListAuxiliar() {
        return new StaticSentence(s, new QBFBuilder(
                "SELECT "
                + getSelectFieldList()
                + "FROM STOCKCURRENT C RIGHT OUTER JOIN PRODUCTS P ON (C.PRODUCT = P.ID) "
                + "WHERE P.ISCOM = " + s.DB.TRUE() + " AND ?(QBF_FILTER) "
                + "ORDER BY P.REFERENCE",
                new String[]{"P.NAME", "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY", "P.CODE"}),
                new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING}),
                ProductInfoExt.getSerializerRead());
    }

    //Tickets and Receipt list
    /**
     *
     * @return
     */
    public SentenceList getTicketsList() {
        return new StaticSentence(s, new QBFBuilder(
                "SELECT "
                + "T.TICKETID, "
                + "T.TICKETTYPE, "
                + "R.DATENEW, "
                + "P.NAME, "
                + "C.NAME, "
                + "SUM(PM.TOTAL) "
                + "FROM RECEIPTS "
                + "R JOIN TICKETS T ON R.ID = T.ID LEFT OUTER JOIN PAYMENTS PM "
                + "ON R.ID = PM.RECEIPT LEFT OUTER JOIN CUSTOMERS C "
                + "ON C.ID = T.CUSTOMER LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                + "WHERE ?(QBF_FILTER) "
                + "GROUP BY "
                + "T.ID, "
                + "T.TICKETID, "
                + "T.TICKETTYPE, "
                + "R.DATENEW, "
                + "P.NAME, "
                + "C.NAME "
                + "ORDER BY R.DATENEW DESC, T.TICKETID",
                new String[]{"T.TICKETID", "T.TICKETTYPE", "PM.TOTAL", "R.DATENEW", "R.DATENEW", "P.NAME", "C.NAME"}), new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.INT,
            Datas.OBJECT, Datas.INT,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.TIMESTAMP,
            Datas.OBJECT, Datas.TIMESTAMP,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING}), new SerializerReadClass(FindTicketsInfo.class));
    }

    //User list
    /**
     *
     * @return
     */
    public final SentenceList getUserList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME "
                + "FROM PEOPLE "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(
                        dr.getString(1),
                        dr.getString(2));
            }
        });
    }

    // Listados para combo
    /**
     *
     * @return
     */
    public final SentenceList getTaxList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "CATEGORY, "
                + "CUSTCATEGORY, "
                + "PARENTID, "
                + "RATE, "
                + "RATECASCADE, "
                + "RATEORDER "
                + "FROM TAXES "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxInfo(
                        dr.getString(1),
                        dr.getString(2),
                        dr.getString(3),
                        dr.getString(4),
                        dr.getString(5),
                        dr.getDouble(6),
                        dr.getBoolean(7),
                        dr.getInt(8));
            }
        });
    }

    /**
     *
     * @return
     */
    public final SentenceList getCategoriesList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "IMAGE, "
                + "TEXTTIP, "
                + "CATSHOWNAME, "
                + "COLOUR, "
                + "CATORDER "
                + "FROM CATEGORIES "
                + "ORDER BY NAME", null, CategoryInfo.getSerializerRead());
    }

    /**
     *
     * @return
     */
    public final SentenceList getPromotionsList() {
        return new StaticSentence(s, "SELECT "
                + "ID, NAME, CRITERIA, SCRIPT, ISENABLED, ALLPRODUCTS "
                + "FROM PROMOTIONS "
                + "ORDER BY NAME", null,
                new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new PromotionInfo(dr.getString(1), dr.getString(2),
                        Formats.BYTEA.formatValue(dr.getBytes(3)),
                        Formats.BYTEA.formatValue(dr.getBytes(4)),
                        dr.getBoolean(5),
                        dr.getBoolean(6));
            }
        });
    }

    /**
     *
     * @return
     */
    public final SentenceList getTaxCustCategoriesList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME "
                + "FROM TAXCUSTCATEGORIES "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCustCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    /**
     *
     * @return @throws BasicException
     */
    @SuppressWarnings("unchecked")
    public final List<CustomerTransaction> getCustomersTransactionList(String name) throws BasicException {
        return (List<CustomerTransaction>) new PreparedSentence(s,
                "SELECT TICKETS.TICKETID, PRODUCTS.NAME AS PNAME, "
                + "SUM(TICKETLINES.UNITS) AS UNITS, "
                + "SUM(TICKETLINES.UNITS * TICKETLINES.PRICE) AS AMOUNT, "
                + "SUM(TICKETLINES.UNITS * TICKETLINES.PRICE * (1.0 + TAXES.RATE)) AS TOTAL, "
                + "RECEIPTS.DATENEW, CUSTOMERS.NAME AS CNAME "
                + "FROM RECEIPTS, CUSTOMERS, TICKETS, TICKETLINES "
                + "LEFT OUTER JOIN PRODUCTS ON TICKETLINES.PRODUCT = PRODUCTS.ID "
                + "LEFT OUTER JOIN TAXES ON TICKETLINES.TAXID = TAXES.ID  "
                + "WHERE CUSTOMERS.ID = TICKETS.CUSTOMER AND TICKETLINES.PRODUCT = PRODUCTS.ID AND RECEIPTS.ID = TICKETS.ID AND TICKETS.ID = TICKETLINES.TICKET "
                + "AND CUSTOMERS.NAME = ?"
                + "GROUP BY CUSTOMERS.NAME, RECEIPTS.DATENEW, TICKETS.TICKETID, PRODUCTS.NAME, TICKETS.TICKETTYPE "
                + "ORDER BY RECEIPTS.DATENEW DESC, PRODUCTS.NAME",
                SerializerWriteString.INSTANCE,
                CustomerTransaction.getSerializerRead()).list(name);
    }

    /**
     *
     * @return
     */
    public final SentenceList getTaxCategoriesList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME "
                + "FROM TAXCATEGORIES "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new TaxCategoryInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    /**
     *
     * @return
     */
    public final SentenceList getAttributeSetList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME "
                + "FROM ATTRIBUTESET "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new AttributeSetInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    /**
     *
     * @return
     */
    public final SentenceList getLocationsList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME, "
                + "ADDRESS FROM LOCATIONS "
                + "ORDER BY NAME", null, new SerializerReadClass(LocationInfo.class));
    }

    /**
     *
     * @return
     */
    public final SentenceList getFloorsList() {
        return new StaticSentence(s, "SELECT ID, NAME FROM FLOORS ORDER BY NAME", null, new SerializerReadClass(FloorsInfo.class));
    }

    /**
     *
     * @param card
     * @return
     * @throws BasicException
     */
    public CustomerInfoExt findCustomerExt(String card) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s, "SELECT "
                + "ID, "
                + "TAXID, "
                + "SEARCHKEY, "
                + "NAME, "
                + "CARD, "
                + "TAXCATEGORY, "
                + "NOTES, "
                + "MAXDEBT, "
                + "VISIBLE, "
                + "CURDATE, "
                + "CURDEBT, "
                + "FIRSTNAME, "
                + "LASTNAME, "
                + "EMAIL, "
                + "PHONE, "
                + "PHONE2, "
                + "FAX, "
                + "ADDRESS, "
                + "ADDRESS2, "
                + "POSTAL, "
                + "CITY, "
                + "REGION, "
                + "COUNTRY, "
                + "IMAGE "
                + "FROM CUSTOMERS "
                + "WHERE CARD = ? AND VISIBLE = " + s.DB.TRUE() + " "
                + "ORDER BY NAME", SerializerWriteString.INSTANCE, new CustomerExtRead()).find(card);
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public CustomerInfoExt loadCustomerExt(String id) throws BasicException {
        return (CustomerInfoExt) new PreparedSentence(s, "SELECT "
                + "ID, "
                + "TAXID, "
                + "SEARCHKEY, "
                + "NAME, "
                + "CARD, "
                + "TAXCATEGORY, "
                + "NOTES, "
                + "MAXDEBT, "
                + "VISIBLE, "
                + "CURDATE, "
                + "CURDEBT, "
                + "FIRSTNAME, "
                + "LASTNAME, "
                + "EMAIL, "
                + "PHONE, "
                + "PHONE2, "
                + "FAX, "
                + "ADDRESS, "
                + "ADDRESS2, "
                + "POSTAL, "
                + "CITY, "
                + "REGION, "
                + "COUNTRY, "
                + "IMAGE "
                + "FROM CUSTOMERS WHERE ID = ?", SerializerWriteString.INSTANCE, new CustomerExtRead()).find(id);
    }

    /**
     *
     * @param id
     * @return
     * @throws BasicException
     */
    public final boolean isCashActive(String id) throws BasicException {

        return new PreparedSentence(s,
                "SELECT MONEY FROM CLOSEDCASH WHERE DATEEND IS NULL AND MONEY = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).find(id)
                != null;
    }

    /**
     *
     * @param tickettype
     * @param ticketid
     * @return
     * @throws BasicException
     */
    public final TicketInfo loadTicket(final int tickettype, final int ticketid) throws BasicException {
        TicketInfo ticket = (TicketInfo) new PreparedSentence(s, "SELECT "
                + "T.ID, "
                + "T.TICKETTYPE, "
                + "T.TICKETID, "
                + "R.DATENEW, "
                + "R.MONEY, "
                + "R.ATTRIBUTES, "
                + "P.ID, "
                + "P.NAME, "
                + "T.CUSTOMER "
                + "FROM RECEIPTS R "
                + "JOIN TICKETS T ON R.ID = T.ID "
                + "LEFT OUTER JOIN PEOPLE P ON T.PERSON = P.ID "
                + "WHERE T.TICKETTYPE = ? AND T.TICKETID = ? "
                + "ORDER BY R.DATENEW DESC", SerializerWriteParams.INSTANCE, new SerializerReadClass(TicketInfo.class))
                .find(new DataParams() {
                    @Override
                    public void writeValues() throws BasicException {
                        setInt(1, tickettype);
                        setInt(2, ticketid);
                    }
                });
        if (ticket != null) {

            String customerid = ticket.getCustomerId();
            ticket.setCustomer(customerid == null
                    ? null
                    : loadCustomerExt(customerid));

            ticket.setLines(new PreparedSentence(s, "SELECT L.TICKET, L.LINE, L.PRODUCT, L.ATTRIBUTESETINSTANCE_ID, L.UNITS, L.PRICE, T.ID, T.NAME, T.CATEGORY, T.CUSTCATEGORY, T.PARENTID, T.RATE, T.RATECASCADE, T.RATEORDER, L.ATTRIBUTES, L.REFUNDQTY  "
                    + "FROM TICKETLINES L, TAXES T WHERE L.TAXID = T.ID AND L.TICKET = ? ORDER BY L.LINE", SerializerWriteString.INSTANCE, new SerializerReadClass(TicketLineInfo.class)).list(ticket.getId()));
            ticket.setPayments(new PreparedSentence(s //                    , "SELECT PAYMENT, TOTAL, TRANSID TENDERED FROM PAYMENTS WHERE RECEIPT = ?" 
                    , "SELECT PAYMENT, TOTAL, TRANSID, TENDERED, CARDNAME FROM PAYMENTS WHERE RECEIPT = ?", SerializerWriteString.INSTANCE, new SerializerReadClass(PaymentInfoTicket.class)).list(ticket.getId()));
        }
        return ticket;
    }

    /**
     *
     * @param ticket
     * @param location
     * @throws BasicException
     */
    public final void saveTicket(final TicketInfo ticket, final String location) throws BasicException {

        Transaction t;
        t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {

                // Set Receipt Id
                switch (ticket.getTicketType()) {
                    case NORMAL:
                        ticket.setTicketId(getNextTicketIndex());
                        break;
                    case REFUND:
                        ticket.setTicketId(getNextTicketRefundIndex());
                        break;
                    case PAYMENT:
                        ticket.setTicketId(getNextTicketPaymentIndex());
                        break;
                    case NOSALE:
                        ticket.setTicketId(getNextTicketPaymentIndex());
                        break;
                    case INVOICE:
                        ticket.setTicketId(getNextTicketInvoiceIndex());
                        break;

                    default:
                        throw new BasicException();
                }

                new PreparedSentence(s, "INSERT INTO RECEIPTS (ID, MONEY, DATENEW, ATTRIBUTES, PERSON) VALUES (?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE
                ).exec(new DataParams() {
                    @Override
                    public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                        setString(2, ticket.getActiveCash());
                        setTimestamp(3, ticket.getDate());
                        try {
                            ByteArrayOutputStream o = new ByteArrayOutputStream();
                            ticket.getProperties().storeToXML(o, AppLocal.APP_NAME, "UTF-8");
                            setBytes(4, o.toByteArray());
                        } catch (IOException e) {
                            setBytes(4, null);
                        }
                        setString(5, ticket.getProperty("person"));
                    }
                }
                );

                // new ticket
                new PreparedSentence(s, "INSERT INTO TICKETS (ID, TICKETTYPE, TICKETID, PERSON, CUSTOMER) VALUES (?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE
                ).exec(new DataParams() {
                    @Override
                    public void writeValues() throws BasicException {
                        setString(1, ticket.getId());
                        setInt(2, ticket.getTicketType().getId());
                        setInt(3, ticket.getTicketId());
                        setString(4, ticket.getUser().getId());
                        setString(5, ticket.getCustomerId());
                    }
                }
                );

                SentenceExec ticketlineinsert = new PreparedSentence(s, "INSERT INTO TICKETLINES (TICKET, LINE, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, TAXID, ATTRIBUTES, REFUNDQTY) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteBuilder.INSTANCE);

                for (TicketLineInfo l : ticket.getLines()) {

                    ticketlineinsert.exec(l);

                    if (l.getProductID() != null && l.isProductService() != true) {
                        // update the stock
                        getStockDiaryInsert().exec(new Object[]{
                            UUID.randomUUID().toString(),
                            ticket.getDate(),
                            l.getMultiply() < 0.0
                            ? MovementReason.IN_REFUND.getKey()
                            : MovementReason.OUT_SALE.getKey(),
                            location,
                            l.getProductID(),
                            l.getProductAttSetInstId(), -l.getMultiply(), l.getPrice(),
                            ticket.getUser().getName()
                        });
                    }

                }
                final Payments payments = new Payments();
                SentenceExec paymentinsert = new PreparedSentence(s, "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL, TRANSID, RETURNMSG, TENDERED, CARDNAME) VALUES (?, ?, ?, ?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE);

                for (final PaymentInfo p : ticket.getPayments()) {
                    payments.addPayment(p.getName(), p.getTotal(), p.getPaid(), ticket.getReturnMessage());
                }

                //for (final PaymentInfo p : ticket.getPayments()) {
                while (payments.getSize() >= 1) {
                    paymentinsert.exec(new DataParams() {
                        @Override
                        public void writeValues() throws BasicException {
                            pName = payments.getFirstElement();
                            getTotal = payments.getPaidAmount(pName);
                            getTendered = payments.getTendered(pName);
                            getRetMsg = payments.getRtnMessage(pName);
                            payments.removeFirst(pName);

                            setString(1, UUID.randomUUID().toString());
                            setString(2, ticket.getId());
                            setString(3, pName);
                            setDouble(4, getTotal);
                            setString(5, ticket.getTransactionID());
                            setBytes(6, (byte[]) Formats.BYTEA.parseValue(getRetMsg));
                            setDouble(7, getTendered);
                            setString(8, getCardName);
                            payments.removeFirst(pName);
                        }
                    });

                    if ("debt".equals(pName) || "debtpaid".equals(pName)) {
                        // udate customer fields...
                        ticket.getCustomer().updateCurDebt(getTotal, ticket.getDate());
                        // save customer fields...
                        getDebtUpdate().exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setDouble(1, ticket.getCustomer().getCurdebt());
                                setTimestamp(2, ticket.getCustomer().getCurdate());
                                setString(3, ticket.getCustomer().getId());
                            }
                        });
                    }
                }

                SentenceExec taxlinesinsert = new PreparedSentence(s, "INSERT INTO TAXLINES (ID, RECEIPT, TAXID, BASE, AMOUNT)  VALUES (?, ?, ?, ?, ?)", SerializerWriteParams.INSTANCE);
                if (ticket.getTaxes() != null) {
                    for (final TicketTaxInfo tickettax : ticket.getTaxes()) {
                        taxlinesinsert.exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setString(1, UUID.randomUUID().toString());
                                setString(2, ticket.getId());
                                setString(3, tickettax.getTaxInfo().getId());
                                setDouble(4, tickettax.getSubTotal());
                                setDouble(5, tickettax.getTax());
                            }
                        });
                    }
                }

                return null;
            }
        };
        t.execute();
    }

    /**
     *
     * @param ticket
     * @param location
     * @throws BasicException
     */
    public final void deleteTicket(final TicketInfo ticket, final String location) throws BasicException {

        Transaction t = new Transaction(s) {
            @Override
            public Object transact() throws BasicException {

                // update the inventory
                Date d = new Date();
                for (int i = 0; i < ticket.getLinesCount(); i++) {
                    if (ticket.getLine(i).getProductID() != null) {
                        // Hay que actualizar el stock si el hay producto
                        getStockDiaryInsert().exec(new Object[]{
                            UUID.randomUUID().toString(),
                            d,
                            ticket.getLine(i).getMultiply() >= 0.0
                            ? MovementReason.IN_REFUND.getKey()
                            : MovementReason.OUT_SALE.getKey(),
                            location,
                            ticket.getLine(i).getProductID(),
                            ticket.getLine(i).getProductAttSetInstId(), ticket.getLine(i).getMultiply(), ticket.getLine(i).getPrice(),
                            ticket.getUser().getName()
                        });
                    }
                }

                // update customer debts
                for (PaymentInfo p : ticket.getPayments()) {
                    if ("debt".equals(p.getName()) || "debtpaid".equals(p.getName())) {

                        // udate customer fields...
                        ticket.getCustomer().updateCurDebt(-p.getTotal(), ticket.getDate());

                        // save customer fields...
                        getDebtUpdate().exec(new DataParams() {
                            @Override
                            public void writeValues() throws BasicException {
                                setDouble(1, ticket.getCustomer().getCurdebt());
                                setTimestamp(2, ticket.getCustomer().getCurdate());
                                setString(3, ticket.getCustomer().getId());
                            }
                        });
                    }
                }

                // and delete the receipt
                new StaticSentence(s, "DELETE FROM TAXLINES WHERE RECEIPT = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM PAYMENTS WHERE RECEIPT = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM TICKETLINES WHERE TICKET = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM TICKETS WHERE ID = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                new StaticSentence(s, "DELETE FROM RECEIPTS WHERE ID = ?", SerializerWriteString.INSTANCE).exec(ticket.getId());
                return null;
            }
        };
        t.execute();
    }

    /**
     *
     * @return @throws BasicException
     */
    public final Integer getNextPickupIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "PICKUP_NUMBER").find();
    }

    /**
     *
     * @return @throws BasicException
     */
    public final Integer getNextTicketIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM").find();
    }

    public final Integer getNextTicketInvoiceIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_INVOICE").find();
    }

    /**
     *
     * @return @throws BasicException
     */
    public final Integer getNextTicketRefundIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_REFUND").find();
    }

    /**
     *
     * @return @throws BasicException
     */
    public final Integer getNextTicketPaymentIndex() throws BasicException {
        return (Integer) s.DB.getSequenceSentence(s, "TICKETSNUM_PAYMENT").find();
    }

    /**
     *
     * @return
     */
    public final SentenceList getProductCatQBF() {
        return new StaticSentence(s, new QBFBuilder(
                "SELECT "
                + getSelectFieldList()
                + "FROM PRODUCTS P "
                + "WHERE ?(QBF_FILTER) "
                + "ORDER BY P.REFERENCE",
                new String[]{
                    "P.NAME", "P.PRICEBUY", "P.PRICESELL", "P.CATEGORY", "P.CODE"}, false), new SerializerWriteBasic(new Datas[]{
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.DOUBLE,
            Datas.OBJECT, Datas.STRING,
            Datas.OBJECT, Datas.STRING}), productsRow.getSerializerRead());
    }

    /**
     *
     * @return
     */
    public final SentenceExec getProductCatInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                int i = new PreparedSentence(s, "INSERT INTO PRODUCTS (ID, "
                        + "REFERENCE, CODE, CODETYPE, NAME, ISCOM, "
                        + "ISSCALE, PRICEBUY, PRICESELL, CATEGORY, TAXCAT, "
                        + "ATTRIBUTESET_ID, IMAGE, STOCKCOST, STOCKVOLUME, ISCATALOG, CATORDER, "
                        + "ATTRIBUTES, ISKITCHEN, ISSERVICE, DISPLAY, ISVPRICE, "
                        + "ISVERPATRIB, TEXTTIP, WARRANTY, STOCKUNITS, ALIAS, ALWAYSAVAILABLE, DISCOUNTED, CANDISCOUNT, "
                        + "ISPACK, PACKQUANTITY, PACKPRODUCT, PROMOTIONID  ) "
                        + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)",
                        new SerializerWriteBasicExt(productsRow.getDatas(),
                                new int[]{INDEX_ID,
                                    INDEX_REFERENCE, INDEX_CODE, INDEX_CODETYPE,
                                    INDEX_NAME, INDEX_ISCOM, INDEX_ISSCALE,
                                    INDEX_PRICEBUY, INDEX_PRICESELL, INDEX_CATEGORY,
                                    INDEX_TAXCAT, INDEX_ATTRIBUTESET_ID, INDEX_IMAGE,
                                    INDEX_STOCKCOST, INDEX_STOCKVOLUME,
                                    INDEX_ISCATALOG, INDEX_CATORDER, INDEX_ATTRIBUTES,
                                    INDEX_ISKITCHEN, INDEX_ISSERVICE, INDEX_DISPLAY,
                                    INDEX_ISVPRICE, INDEX_ISVERPATRIB, INDEX_TEXTTIP,
                                    INDEX_WARRANTY, INDEX_STOCKUNITS, INDEX_ALIAS,
                                    INDEX_ALWAYSAVAILABLE, INDEX_DISCOUNTED, INDEX_CANDISCOUNT,
                                    INDEX_ISPACK, INDEX_PACKQUANTITY, INDEX_PACKPRODUCT,
                                    INDEX_PROMOTIONID})).exec(params);

                new PreparedSentence(s, "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, UNITS) VALUES ('0', ?, 0.0)",
                        new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{INDEX_ID})).exec(params);

                return i;
            }
        };
    }

    public final SentenceList getPackProductList() {
        return new StaticSentence(s, "SELECT "
                + "ID, "
                + "NAME "
                + "FROM PRODUCTS "
                + "ORDER BY NAME", null, new SerializerRead() {
            @Override
            public Object readValues(DataRead dr) throws BasicException {
                return new PackProductInfo(dr.getString(1), dr.getString(2));
            }
        });
    }

    /**
     *
     * @return
     */
    public final SentenceExec getProductCatUpdate() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                Object[] values = (Object[]) params;
                int i = new PreparedSentence(s, "UPDATE PRODUCTS SET REFERENCE = ?, "
                        + "CODE = ?, CODETYPE = ?, NAME = ?, ISCOM = ?, "
                        + "ISSCALE = ?, PRICEBUY = ?, "
                        + "PRICESELL = ?, CATEGORY = ?, "
                        + "TAXCAT = ?, ATTRIBUTESET_ID = ?, "
                        + "IMAGE = ?, STOCKCOST = ?, "
                        + "STOCKVOLUME = ?, ATTRIBUTES = ?, "
                        + "ISKITCHEN = ?, ISSERVICE = ?, "
                        + "DISPLAY = ?, ISVPRICE = ?, "
                        + "ISVERPATRIB = ?, TEXTTIP = ?, "
                        + "WARRANTY = ?, STOCKUNITS = ?, ALIAS = ?, ALWAYSAVAILABLE = ?, "
                        + "DISCOUNTED = ?, CANDISCOUNT = ?, "
                        + "ISPACK = ?, PACKQUANTITY = ?, PACKPRODUCT = ?, "
                        + "PROMOTIONID = ?, ISCATALOG = ?, CATORDER = ? "
                        + "WHERE ID = ?", new SerializerWriteBasicExt(productsRow.getDatas(),
                                new int[]{
                                    INDEX_REFERENCE, INDEX_CODE, INDEX_CODETYPE,
                                    INDEX_NAME, INDEX_ISCOM, INDEX_ISSCALE,
                                    INDEX_PRICEBUY, INDEX_PRICESELL, INDEX_CATEGORY,
                                    INDEX_TAXCAT, INDEX_ATTRIBUTESET_ID, INDEX_IMAGE,
                                    INDEX_STOCKCOST, INDEX_STOCKVOLUME, INDEX_ATTRIBUTES,
                                    INDEX_ISKITCHEN, INDEX_ISSERVICE, INDEX_DISPLAY,
                                    INDEX_ISVPRICE, INDEX_ISVERPATRIB, INDEX_TEXTTIP,
                                    INDEX_WARRANTY, INDEX_STOCKUNITS, INDEX_ALIAS,
                                    INDEX_ALWAYSAVAILABLE, INDEX_DISCOUNTED, INDEX_CANDISCOUNT,
                                    INDEX_ISPACK, INDEX_PACKQUANTITY, INDEX_PACKPRODUCT,
                                    INDEX_PROMOTIONID, INDEX_ISCATALOG, INDEX_CATORDER,
                                    INDEX_ID})).exec(params);

                return i;
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getProductCatDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "DELETE FROM STOCKCURRENT WHERE PRODUCT = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{INDEX_ID})).exec(params);
                return new PreparedSentence(s, "DELETE FROM PRODUCTS WHERE ID = ?", new SerializerWriteBasicExt(productsRow.getDatas(), new int[]{INDEX_ID})).exec(params);

            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getDebtUpdate() {

        return new PreparedSentence(s, "UPDATE CUSTOMERS SET CURDEBT = ?, CURDATE = ? WHERE ID = ?", SerializerWriteParams.INSTANCE);
    }

    /**
     *
     * @return
     */
    public final SentenceExec getStockDiaryInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                int updateresult = ((Object[]) params)[5] == null // si ATTRIBUTESETINSTANCE_ID is null
                        ? new PreparedSentence(s, "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4})).exec(params)
                        : new PreparedSentence(s, "UPDATE STOCKCURRENT SET UNITS = (UNITS + ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(s, "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, ?)", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{3, 4, 5, 6})).exec(params);
                }
                return new PreparedSentence(s, "INSERT INTO STOCKDIARY (ID, DATENEW, REASON, LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS, PRICE, AppUser) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{0, 1, 2, 3, 4, 5, 6, 7, 8})).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getStockDiaryDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                int updateresult = ((Object[]) params)[5] == null // if ATTRIBUTESETINSTANCE_ID is null
                        ? new PreparedSentence(s, "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4})).exec(params)
                        : new PreparedSentence(s, "UPDATE STOCKCURRENT SET UNITS = (UNITS - ?) WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{6, 3, 4, 5})).exec(params);

                if (updateresult == 0) {
                    new PreparedSentence(s, "INSERT INTO STOCKCURRENT (LOCATION, PRODUCT, ATTRIBUTESETINSTANCE_ID, UNITS) VALUES (?, ?, ?, -(?))", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{3, 4, 5, 6})).exec(params);
                }
                return new PreparedSentence(s, "DELETE FROM STOCKDIARY WHERE ID = ?", new SerializerWriteBasicExt(stockdiaryDatas, new int[]{0})).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getPaymentMovementInsert() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "INSERT INTO RECEIPTS (ID, MONEY, DATENEW) VALUES (?, ?, ?)", new SerializerWriteBasicExt(paymenttabledatas, new int[]{0, 1, 2})).exec(params);
                return new PreparedSentence(s, "INSERT INTO PAYMENTS (ID, RECEIPT, PAYMENT, TOTAL, NOTES) VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasicExt(paymenttabledatas, new int[]{3, 0, 4, 5, 6})).exec(params);
            }
        };
    }

    /**
     *
     * @return
     */
    public final SentenceExec getPaymentMovementDelete() {
        return new SentenceExecTransaction(s) {
            @Override
            public int execInTransaction(Object params) throws BasicException {
                new PreparedSentence(s, "DELETE FROM PAYMENTS WHERE ID = ?", new SerializerWriteBasicExt(paymenttabledatas, new int[]{3})).exec(params);
                return new PreparedSentence(s, "DELETE FROM RECEIPTS WHERE ID = ?", new SerializerWriteBasicExt(paymenttabledatas, new int[]{0})).exec(params);
            }
        };
    }

    public final Double getCustomerDebt(String id) throws BasicException {
       return (Double) new PreparedSentence(s, "SELECT CURDEBT FROM CUSTOMERS WHERE ID = ? ", 
              SerializerWriteString.INSTANCE, SerializerReadDouble.INSTANCE).find(id);

    }
   
    /**
     *
     * @param warehouse
     * @param id
     * @param attsetinstid
     * @return
     * @throws BasicException
     */
    public final double findProductStock(String warehouse, String id, String attsetinstid) throws BasicException {

        PreparedSentence p = attsetinstid == null
                ? new PreparedSentence(s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID IS NULL", new SerializerWriteBasic(Datas.STRING, Datas.STRING), SerializerReadDouble.INSTANCE)
                : new PreparedSentence(s, "SELECT UNITS FROM STOCKCURRENT WHERE LOCATION = ? AND PRODUCT = ? AND ATTRIBUTESETINSTANCE_ID = ?", new SerializerWriteBasic(Datas.STRING, Datas.STRING, Datas.STRING), SerializerReadDouble.INSTANCE);

        Double d = (Double) p.find(warehouse, id, attsetinstid);
        return d == null ? 0.0 : d;
    }

    /**
     *
     * @return
     */
    public final SentenceExec getCatalogCategoryAdd() {
        return new StaticSentence(s, "UPDATE PRODUCTS SET ISCATALOG = " + s.DB.TRUE() + " WHERE CATEGORY = ?", SerializerWriteString.INSTANCE);
    }

    /**
     *
     * @return
     */
    public final SentenceExec getCatalogCategoryDel() {
        return new StaticSentence(s, "UPDATE PRODUCTS SET ISCATALOG = " + s.DB.FALSE() + " WHERE CATEGORY = ?", SerializerWriteString.INSTANCE);
    }

    /**
     *
     * @return
     */
    public final TableDefinition getTableCategories() {
        return new TableDefinition(s,
                "CATEGORIES", new String[]{"ID", "NAME", "PARENTID", "IMAGE",
                    "TEXTTIP", "CATSHOWNAME", "COLOUR", "CATORDER"},
                new String[]{"ID", AppLocal.getIntString("Label.Name"), "",
                    AppLocal.getIntString("label.image")}, new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.IMAGE, Datas.STRING, Datas.BOOLEAN, Datas.STRING, Datas.INT}, new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING, Formats.NULL, Formats.STRING, Formats.BOOLEAN, Formats.STRING, Formats.INT}, new int[]{0}
        );
    }

    /**
     *
     * @return
     */
    public final TableDefinition getTableTaxes() {
        return new TableDefinition(s,
                "TAXES", new String[]{"ID", "NAME", "CATEGORY", "CUSTCATEGORY", "PARENTID", "RATE", "RATECASCADE", "RATEORDER"},
                new String[]{"ID", AppLocal.getIntString("Label.Name"), AppLocal.getIntString("label.taxcategory"),
                    AppLocal.getIntString("label.custtaxcategory"), AppLocal.getIntString("label.taxparent"), AppLocal.getIntString("label.dutyrate"),
                    AppLocal.getIntString("label.cascade"), AppLocal.getIntString("label.order")},
                new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.BOOLEAN, Datas.INT},
                new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.STRING, Formats.PERCENT, Formats.BOOLEAN, Formats.INT},
                new int[]{0},
                "NAME"
        );
    }

    /**
     *
     * @return
     */
    public final TableDefinition getTableTaxCustCategories() {
        return new TableDefinition(s,
                "TAXCUSTCATEGORIES", new String[]{"ID", "NAME"},
                new String[]{"ID", AppLocal.getIntString("Label.Name")},
                new Datas[]{Datas.STRING, Datas.STRING},
                new Formats[]{Formats.STRING, Formats.STRING},
                new int[]{0},
                "NAME"
        );
    }

    /**
     *
     * @return
     */
    public final TableDefinition getTableTaxCategories() {
        return new TableDefinition(s,
                "TAXCATEGORIES", new String[]{"ID", "NAME"},
                new String[]{"ID", AppLocal.getIntString("Label.Name")},
                new Datas[]{Datas.STRING, Datas.STRING},
                new Formats[]{Formats.STRING, Formats.STRING},
                new int[]{0},
                "NAME"
        );
    }

    /**
     *
     * @return
     */
    public final TableDefinition getTableLocations() {
        return new TableDefinition(s,
                "LOCATIONS", new String[]{"ID", "NAME", "ADDRESS"},
                new String[]{"ID", AppLocal.getIntString("label.locationname"), AppLocal.getIntString("label.locationaddress")},
                new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING},
                new Formats[]{Formats.STRING, Formats.STRING, Formats.STRING},
                new int[]{0},
                "NAME"
        );
    }

    public final void updateRefundQty(Double qty, String ticket, Integer line) throws BasicException {
        m_updateRefund.exec(qty, ticket, line);
    }

    public final boolean getVoucher(String id) throws BasicException {
        return new PreparedSentence(s,
                "SELECT SOLDTICKETID FROM VOUCHERS WHERE VOUCHER = ?",
                SerializerWriteString.INSTANCE,
                SerializerReadString.INSTANCE).find(id)
                != null;
    }

    public final String getProductNameByCode(String sCode) throws BasicException {
        return (String) m_productname.find(sCode);
    }

    public final void sellVoucher(Object[] voucher) throws BasicException {
        m_sellvoucher.exec(voucher);
    }

    public final void insertCategory(Object[] voucher) throws BasicException {
        m_insertcat.exec(voucher);
    }

    /**
     *
     */
    protected static class CustomerExtRead implements SerializerRead {

        /**
         *
         * @param dr
         * @return
         * @throws BasicException
         */
        @Override
        public Object readValues(DataRead dr) throws BasicException {
            CustomerInfoExt c = new CustomerInfoExt(dr.getString(1));
            c.setTaxid(dr.getString(2));
            c.setSearchkey(dr.getString(3));
            c.setName(dr.getString(4));
            c.setCard(dr.getString(5));
            c.setTaxCustomerID(dr.getString(6));
            c.setNotes(dr.getString(7));
            c.setMaxdebt(dr.getDouble(8));
            c.setVisible(dr.getBoolean(9));
            c.setCurdate(dr.getTimestamp(10));
            c.setCurdebt(dr.getDouble(11));
            c.setFirstname(dr.getString(12));
            c.setLastname(dr.getString(13));
            c.setEmail(dr.getString(14));
            c.setPhone(dr.getString(15));
            c.setPhone2(dr.getString(16));
            c.setFax(dr.getString(17));
            c.setAddress(dr.getString(18));
            c.setAddress2(dr.getString(19));
            c.setPostal(dr.getString(20));
            c.setCity(dr.getString(21));
            c.setRegion(dr.getString(22));
            c.setCountry(dr.getString(23));
            c.setImage(dr.getString(24));

            return c;
        }
    }
}
