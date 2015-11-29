//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) 2015 
//    http://www.chromis.co.uk 3.81
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
//    CSV Import Panel added by JDL - February 2013
//    Additonal library required - javacsv

package uk.chromis.pos.imports;

import com.csvreader.CsvReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import org.apache.commons.lang.StringUtils;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.DataResultSet;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.PreparedSentence;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWriteBasicExt;
import uk.chromis.data.loader.Session;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppConfig;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppProperties;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.forms.DataLogicSystem;
import uk.chromis.pos.forms.JPanelView;
import uk.chromis.pos.inventory.TaxCategoryInfo;
import uk.chromis.pos.sales.TaxesLogic;
import uk.chromis.pos.ticket.ProductInfoExt;
import uk.chromis.pos.util.BarcodeValidator;

/**
 * Graphical User Interface and code for importing data from a CSV file allowing
 * adding or updating many products quickly and easily.
 *
 * @author John L - Version 1.0
 * @author Walter Wojcik - Version 2.0+
 * @version 2.0 - Added functionality to remember the last folder opened and
 * importing categories from CVS.
 * @version 2.1 complete re-write of the core code, to make use of the core
 * classes available within Unicenta
 * @version 2.2(TJMChan) Fixed Indexing issue and added additional input fields
 */
public class JPanelCSVImport extends JPanel implements JPanelView {

    private AppView m_App;
    private AppProperties m_props;

    private ArrayList<String> Headers = new ArrayList<>();
    private Session s;
    private Connection con;
    private String csvFileName;
    private Double dOriginalRate;
    private String dCategory;
    private String csvMessage = "";
    private CsvReader products;
    private double oldSellPrice = 0;
    private double oldBuyPrice = 0;
    private int currentRecord;
    private int rowCount = 0;
    private String last_folder;
    private File config_file;
    private static String category_disable_text = "[ USE DEFAULT CATEGORY ]";
    private static String reject_bad_categories_text = "[ REJECT ITEMS WITH BAD CATEGORIES ]";
    private DataLogicSales m_dlSales;
    private DataLogicSystem m_dlSystem;

    protected SaveProvider spr;
    private String productReference;
    private String productBarcode;
    private String productName;
    private String Category;
    private Double productBuyPrice;
    private Double productSellPrice;
    private Double stockSecurity;
    private Double stockMaximum;
    private String stockLocation;
    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private SentenceList taxcatsent;
    private ComboBoxValModel taxcatmodel;
    private SentenceList taxsent;
    private TaxesLogic taxeslogic;
    private DocumentListener documentListener;
    private HashMap cat_list = new HashMap();
    private ArrayList badCategories = new ArrayList();
    private ProductInfoExt prodInfo;
    private String recordType = null;
    private int newRecords = 0;
    private int invalidRecords = 0;
    private int priceUpdates = 0;
    private int missingData = 0;
    private int noChanges = 0;
    private int badPrice = 0;
    private double dTaxRate;

    // Addtional Items
    private String buttonText;
    private String remotePrint;
    private String service;
    private String varPrice;
    private String warranty;
    private String textTip;
    private String prop;
    private String aux;
    private String shortName;
    private String isPack;
    private Double packSize;
    private String packOf;

    /**
     * Constructs a new JPanelCSVImport object
     *
     * @param oApp AppView
     */
    public JPanelCSVImport(AppView app) {

        m_App = app;
        m_props = m_App.getProperties();

        initComponents();

        /*
         * Create new datalogocsales & DataLogicSystem instances to allow access to sql routines.
         */
        m_dlSales = (DataLogicSales) m_App.getBean("uk.chromis.pos.forms.DataLogicSales");

        m_dlSystem = (DataLogicSystem) m_App.getBean("uk.chromis.pos.forms.DataLogicSystem");

        spr = new SaveProvider(
                m_dlSales.getProductCatUpdate(),
                m_dlSales.getProductCatInsert(),
                m_dlSales.getProductCatDelete());

        // Save Last file for later use.
        last_folder = m_props.getProperty("CSV.last_folder");
        config_file = m_props.getConfigFile();

        documentListener = new DocumentListener() {
            @Override
            public void changedUpdate(DocumentEvent documentEvent) {
                jHeaderRead.setEnabled(true);
            }

            @Override
            public void insertUpdate(DocumentEvent documentEvent) {
                if (!"".equals(jFileName.getText().trim())) {
                    jHeaderRead.setEnabled(true);
                }
            }

            @Override
            public void removeUpdate(DocumentEvent documentEvent) {
                if (jFileName.getText().trim().equals("")) {
                    jHeaderRead.setEnabled(false);
                }
            }
        };
        jFileName.getDocument().addDocumentListener(documentListener);

    }

    /**
     * Reads the headers from the CSV file and initializes subsequent form
     * fields. This function first reads the headers from the CSVFileName file,
     * then puts them into the header combo boxes and enables the other form
     * inputs.
     *
     * @todo Simplify this method by stripping the file reading and writing
     * functionality out into it's own class. Also make the enabling fields
     * section into it's own function and return the 'Headers' to the calling
     * function to be added there.
     *
     * @param CSVFileName Name of the file (including the path) to open and read
     * CSV data from
     * @throws IOException If there is an issue reading the CSV file
     */
    private void GetheadersFromFile(String CSVFileName) throws IOException {

        File f = new File(CSVFileName);
        if (f.exists()) {
            //products = new CsvReader(CSVFileName);
            products = new CsvReader(new InputStreamReader(new FileInputStream(CSVFileName), "UTF-8"));
            products.setDelimiter(((String) jComboSeparator.getSelectedItem()).charAt(0));
            products.readHeaders();
            // We need a minimum of 5 columns to map all required fields                            
            if (products.getHeaderCount() < 5) {
                JOptionPane.showMessageDialog(null,
                        "Insufficient headers found in file",
                        "Invalid Header Count.",
                        JOptionPane.WARNING_MESSAGE);
                products.close();
                return;
            }
            rowCount = 0;
            int i = 0;
            Headers.clear();
            Headers.add("");
            jComboName.addItem("");
            jComboReference.addItem("");
            jComboBarcode.addItem("");
            jComboBuy.addItem("");
            jComboSell.addItem("");
            jComboCategory.addItem("");
            jComboMaximum.addItem("");
            jComboSecurity.addItem("");

            //additional Items Information
            jComboBoxButtonText.addItem("");
            jComboBoxRemotePrint.addItem("");
            jComboBoxService.addItem("");
            jComboBoxVarPrice.addItem("");
            jComboBoxWarranty.addItem("");
            jComboBoxTextTip.addItem("");
            jComboBoxProp.addItem("");
            jComboBoxAux.addItem("");
            jComboBoxShortName.addItem("");
            jComboBoxIspack.addItem("");
            jComboBoxPackSize.addItem("");
            jComboBoxPackOf.addItem("");

            /**
             * @todo Return header list for processing elsewhere
             */
            while (i < products.getHeaderCount()) {
                jComboName.addItem(products.getHeader(i));
                jComboReference.addItem(products.getHeader(i));
                jComboBarcode.addItem(products.getHeader(i));
                jComboBuy.addItem(products.getHeader(i));
                jComboSell.addItem(products.getHeader(i));
                jComboCategory.addItem(products.getHeader(i));
                jComboSecurity.addItem(products.getHeader(i));
                jComboMaximum.addItem(products.getHeader(i));
                Headers.add(products.getHeader(i));

                //additional Items Information
                jComboBoxButtonText.addItem(products.getHeader(i));
                jComboBoxRemotePrint.addItem(products.getHeader(i));
                jComboBoxService.addItem(products.getHeader(i));
                jComboBoxVarPrice.addItem(products.getHeader(i));
                jComboBoxWarranty.addItem(products.getHeader(i));
                jComboBoxTextTip.addItem(products.getHeader(i));
                jComboBoxProp.addItem(products.getHeader(i));
                jComboBoxAux.addItem(products.getHeader(i));
                jComboBoxShortName.addItem(products.getHeader(i));
                jComboBoxIspack.addItem(products.getHeader(i));
                jComboBoxPackSize.addItem(products.getHeader(i));
                jComboBoxPackOf.addItem(products.getHeader(i));

                ++i;
            }

            //enable all the chsck boxes ready for use
            enableCheckBoxes();

            //Count the records found
            while (products.readRecord()) {
                ++rowCount;
            }

            jTextRecords.setText(Long.toString(rowCount));
            // close the file we will open again when required                        
            products.close();

        } else {
            JOptionPane.showMessageDialog(null, "Unable to locate "
                    + CSVFileName,
                    "File not found",
                    JOptionPane.WARNING_MESSAGE);
        }
    }

    /**
     * Enables all the selection options on the for to allow the user to
     * interact with the routine.
     *
     */
    private void enableCheckBoxes() {
        jHeaderRead.setEnabled(false);
        jImport.setEnabled(false);
        jComboReference.setEnabled(true);
        jComboName.setEnabled(true);
        jComboBarcode.setEnabled(true);
        jComboBuy.setEnabled(true);
        jComboSell.setEnabled(true);
        jComboCategory.setEnabled(true);
        jComboSecurity.setEnabled(true);
        jComboMaximum.setEnabled(true);
        jComboDefaultCategory.setEnabled(true);
        jComboTax.setEnabled(true);
        jCheckInCatalogue.setEnabled(true);
        jCheckSellIncTax.setEnabled(true);
        jCheckAddStockLevels.setEnabled(true);

        //additional Items Information
        jComboBoxButtonText.setEnabled(true);
        jComboBoxRemotePrint.setEnabled(true);
        jComboBoxService.setEnabled(true);
        jComboBoxVarPrice.setEnabled(true);
        jComboBoxWarranty.setEnabled(true);
        jComboBoxTextTip.setEnabled(true);
        jComboBoxProp.setEnabled(true);
        jComboBoxAux.setEnabled(true);
        jComboBoxShortName.setEnabled(true);
        jComboBoxIspack.setEnabled(true);
        jComboBoxPackSize.setEnabled(true);
        jComboBoxPackOf.setEnabled(true);
    }

    /**
     * Imports the CVS File using specifications from the form.
     *
     * @param CSVFileName Name of the file (including path) to import.
     * @throws IOException If there are file reading issues.
     */
    private void ImportCsvFile(String CSVFileName) throws IOException {

        File f = new File(CSVFileName);
        if (f.exists()) {

            // Read file
            //products = new CsvReader(CSVFileName);
            products = new CsvReader(new InputStreamReader(new FileInputStream(CSVFileName), "UTF-8"));
            products.setDelimiter(((String) jComboSeparator.getSelectedItem()).charAt(0));
            products.readHeaders();

            try {
                stockLocation = (String) ((Object[]) jParamsLocation.createValue())[1];
            } catch (BasicException ex) {
                jCheckAddStockLevels.setSelected(false);
            }

            currentRecord = 0;
            while (products.readRecord()) {
                productReference = products.get((String) jComboReference.getSelectedItem());
                productName = products.get((String) jComboName.getSelectedItem());
                productBarcode = products.get((String) jComboBarcode.getSelectedItem());
                String BuyPrice = products.get((String) jComboBuy.getSelectedItem());
                String SellPrice = products.get((String) jComboSell.getSelectedItem());
                Category = products.get((String) jComboCategory.getSelectedItem());
                String StockSecurity = products.get((String) jComboSecurity.getSelectedItem());
                String StockMaximum = products.get((String) jComboMaximum.getSelectedItem());
                //additional items
                buttonText = products.get((String) jComboBoxButtonText.getSelectedItem());
                remotePrint = products.get((String) jComboBoxRemotePrint.getSelectedItem());
                service = products.get((String) jComboBoxService.getSelectedItem());
                varPrice = products.get((String) jComboBoxVarPrice.getSelectedItem());
                warranty = products.get((String) jComboBoxWarranty.getSelectedItem());
                textTip = products.get((String) jComboBoxTextTip.getSelectedItem());
                prop = products.get((String) jComboBoxProp.getSelectedItem());
                aux = products.get((String) jComboBoxAux.getSelectedItem());
                shortName = products.get((String) jComboBoxShortName.getSelectedItem());
                isPack = products.get((String) jComboBoxIspack.getSelectedItem());
                String boxPackSize = products.get((String) jComboBoxPackSize.getSelectedItem());
                packOf = products.get((String) jComboBoxPackOf.getSelectedItem());

                currentRecord++;

                // Strip Currency Symbols
                BuyPrice = StringUtils.replaceChars(BuyPrice, "$", ""); // Remove Dolar, Euro and Pound sign Sign
                SellPrice = StringUtils.replaceChars(SellPrice, "$", ""); // Remove Dolar, Euro and Pound Sign
                BuyPrice = StringUtils.replaceChars(BuyPrice, "£", ""); // Remove Dolar, Euro and Pound sign Sign
                SellPrice = StringUtils.replaceChars(SellPrice, "£", ""); // Remove Dolar, Euro and Pound Sign
                BuyPrice = StringUtils.replaceChars(BuyPrice, "€", ""); // Remove Dolar, Euro and Pound sign Sign
                SellPrice = StringUtils.replaceChars(SellPrice, "€", ""); // Remove Dolar, Euro and Pound Sign

                dCategory = getCategory();

                // set the csvMessage to a default value
                if ("Bad Category".equals(dCategory)) {
                    csvMessage = "Bad category details";
                } else {
                    csvMessage = "Missing data or Invalid number";
                }

                // Validate and convert the prices or change them to null
                if (validateNumber(BuyPrice)) {
                    productBuyPrice = Double.parseDouble(BuyPrice);
                } else {
                    productBuyPrice = null;
                }

                if (validateNumber(SellPrice)) {
                    productSellPrice = getSellPrice(SellPrice);
                } else {
                    productSellPrice = null;
                }

                if (validateNumber(StockSecurity)) {
                    stockSecurity = Double.parseDouble(StockSecurity);
                } else {
                    stockSecurity = null;
                }

                if (validateNumber(StockMaximum)) {
                    stockMaximum = Double.parseDouble(StockMaximum);
                } else {
                    stockMaximum = null;
                }

                /**
                 * Check to make sure our entries aren't bad or blank or the
                 * category is not bad
                 *
                 */
                if ("".equals(productReference)
                        && "".equals(productName)
                        && "".equals(productBarcode)
                        && "".equals(BuyPrice)
                        && "".equals(SellPrice)) {
                    // Ignore blank lines in the import file
                } else if ("".equals(productReference)
                        | "".equals(productName)
                        | "".equals(productBarcode)
                        | "".equals(BuyPrice)
                        | "".equals(SellPrice)
                        | productBuyPrice == null
                        | productSellPrice == null
                        | "Bad Category".equals(dCategory)) {
                    if (productBuyPrice == null | productSellPrice == null) {
                        badPrice++;
                    } else {
                        missingData++;
                    }
                    createCSVEntry(csvMessage, null, null);
                } else {
// We know that the data passes the basic checks, so get more details about the product
                    recordType = getRecord();
                    switch (recordType) {
                        case "new":
                            createProduct("new");
                            newRecords++;
                            createCSVEntry("New product", null, null);
                            break;
                        case "name error":
                        case "barcode error":
                        case "reference error":
                        case "Duplicate Reference found.":
                        case "Duplicate Barcode found.":
                        case "Duplicate Description found.":
                        case "Exception":
                            invalidRecords++;
                            createCSVEntry(recordType, null, null);
                            break;
                        default:
                            updateRecord(recordType);
                            break;
                    }
                }
            }
            products.close();
        } else {
            JOptionPane.showMessageDialog(null, "Unable to locate " + CSVFileName, "File not found", JOptionPane.WARNING_MESSAGE);
        }

        // update the record fields on the form
        jTextNew.setText(Integer.toString(newRecords));
        jTextUpdate.setText(Integer.toString(priceUpdates));
        jTextInvalid.setText(Integer.toString(invalidRecords));
        jTextMissing.setText(Integer.toString(missingData));
        jTextNoChange.setText(Integer.toString(noChanges));
        jTextBadPrice.setText(Integer.toString(badPrice));
        if (badCategories.size() == 1 && badCategories.get(0) == "") {
            jTextBadCats.setText("0");
        } else {
            jTextBadCats.setText(Integer.toString(badCategories.size()));
        }
    }

    /**
     * Tests <code>testString</code> for validity as a number
     *
     * @param testString the string to be checked
     * @return <code>True<code> if a real number <code>False<code> if not
     */
    private Boolean validateNumber(String testString) {
        try {
            Double res = Double.parseDouble(testString);
            return (true);
        } catch (NumberFormatException e) {
            return (false);
        }
    }

    /*
     * Get the category to be used for the new product
     * returns category id as string
     */
    private String getCategory() {
        // get the category to be used for the product
        if (jComboCategory.getSelectedItem() != category_disable_text) {
            // get the category ID of the catergory passed
            String cat = (String) cat_list.get(Category);
            // only if we have a valid category 
            if (cat != null) {
                return (cat);
            }
        }
        if (!badCategories.contains(Category)) {
            badCategories.add(Category.trim()); // Save a list of the bad categories so we can tell the user later
        }
        return ((jComboDefaultCategory.getSelectedItem() == reject_bad_categories_text) ? "Bad Category" : (String) cat_list.get(m_CategoryModel.getSelectedText()));
    }

    /**
     * Adjusts the sell price for included taxes if needed and converted to
     * <code>double</code>
     *
     * @param pSellPrice sell price to be converted
     * @return sell price after adjustment for included taxes and converted to
     * <code>double</double>
     */
    private Double getSellPrice(String pSellPrice) {
        // Check if the selling price icludes taxes 
        dTaxRate = taxeslogic.getTaxRate((TaxCategoryInfo) taxcatmodel.getSelectedItem());
        if (jCheckSellIncTax.isSelected()) {
            return ((Double.parseDouble(pSellPrice)) / (1 + dTaxRate));
        } else {
            return (Double.parseDouble(pSellPrice));
        }
    }

    /**
     * Updated the record in the database with the new prices and category if
     * needed.
     *
     * @param pID Unique product id of the record to be updated It then creates
     * an updated record for the product, subject to the prices be different
     *
     */
    private void updateRecord(String pID) {
        prodInfo = new ProductInfoExt();
        try {
            prodInfo = m_dlSales.getProductInfo(pID);
            dOriginalRate = taxeslogic.getTaxRate(prodInfo.getTaxCategoryID());
            dCategory = ((String) cat_list.get(prodInfo.getCategoryID()) == null) ? prodInfo.getCategoryID() : (String) cat_list.get(prodInfo.getCategoryID());
            oldBuyPrice = prodInfo.getPriceBuy();
            oldSellPrice = prodInfo.getPriceSell();
            productSellPrice *= (1 + dOriginalRate);
            if ((oldBuyPrice != productBuyPrice) || (oldSellPrice != productSellPrice)) {
                createCSVEntry("Updated Price Details", oldBuyPrice, oldSellPrice * (1 + dOriginalRate));
                createProduct("update");
                priceUpdates++;
            } else {
                noChanges++;
            }
        } catch (BasicException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Gets the title of the current panel
     *
     * @return The name of the panel
     */
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.CSVImport");
    }

    /**
     * Returns this object
     *
     * @return
     */
    @Override
    public JComponent getComponent() {
        return this;
    }

    /**
     * Loads Location, Tax and category data into their combo boxes.
     *
     * @throws uk.chromis.basic.BasicException
     */
    @Override
    public void activate() throws BasicException {
        // Get tax details and logic
        taxsent = m_dlSales.getTaxList();  //get details taxes table
        taxeslogic = new TaxesLogic(taxsent.list());
        taxcatsent = m_dlSales.getTaxCategoriesList();
        taxcatmodel = new ComboBoxValModel(taxcatsent.list());
        jComboTax.setModel(taxcatmodel);

        // Get categories list
        m_sentcat = m_dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
        m_CategoryModel.add(reject_bad_categories_text);
        jComboDefaultCategory.setModel(m_CategoryModel);

        // Build the cat_list for later use
        cat_list = new HashMap<>();
        for (Object category : m_sentcat.list()) {
            m_CategoryModel.setSelectedItem(category);
            cat_list.put(category.toString(), m_CategoryModel.getSelectedKey().toString());
        }

        // reset the selected to the first in the list
        m_CategoryModel.setSelectedItem(null);
        taxcatmodel.setSelectedFirst();

        // Set the column delimiter
        jComboSeparator.removeAllItems();
        jComboSeparator.addItem(",");
        jComboSeparator.addItem(";");
        jComboSeparator.addItem("~");
        jComboSeparator.addItem("^");
        jComboSeparator.addItem("|");

        jParamsLocation.init(m_App);
        jParamsLocation.activate();
    }

    /**
     * Resets all the form fields, update 7.4.14 JDL To fix display error if
     * user does not exit before running next import
     */
    public void resetFields() {
        // Clear the form
        jComboReference.removeAllItems();
        jComboReference.setEnabled(false);
        jComboName.removeAllItems();
        jComboName.setEnabled(false);
        jComboBarcode.removeAllItems();
        jComboBarcode.setEnabled(false);
        jComboBuy.removeAllItems();
        jComboBuy.setEnabled(false);
        jComboSell.removeAllItems();
        jComboSell.setEnabled(false);
        jComboCategory.removeAllItems();
        jComboCategory.setEnabled(false);
        jComboDefaultCategory.setEnabled(false);
        jComboSecurity.removeAllItems();
        jComboSecurity.setEnabled(false);
        jComboMaximum.removeAllItems();
        jComboMaximum.setEnabled(false);
        jComboTax.setEnabled(false);

        //additional Information
        jComboBoxButtonText.setEnabled(false);
        jComboBoxButtonText.removeAllItems();
        jComboBoxRemotePrint.setEnabled(false);
        jComboBoxRemotePrint.removeAllItems();
        jComboBoxService.setEnabled(false);
        jComboBoxService.removeAllItems();
        jComboBoxVarPrice.setEnabled(false);
        jComboBoxVarPrice.removeAllItems();
        jComboBoxWarranty.setEnabled(false);
        jComboBoxWarranty.removeAllItems();
        jComboBoxTextTip.setEnabled(false);
        jComboBoxTextTip.removeAllItems();
        jComboBoxProp.setEnabled(false);
        jComboBoxProp.removeAllItems();
        jComboBoxAux.setEnabled(false);
        jComboBoxAux.removeAllItems();
        jComboBoxShortName.setEnabled(false);
        jComboBoxShortName.removeAllItems();
        jComboBoxIspack.setEnabled(false);
        jComboBoxIspack.removeAllItems();
        jComboBoxPackSize.setEnabled(false);
        jComboBoxPackSize.removeAllItems();
        jComboBoxPackOf.setEnabled(false);
        jComboBoxPackOf.removeAllItems();

        jImport.setEnabled(false);
        jHeaderRead.setEnabled(false);
        jCheckInCatalogue.setSelected(true);
        jCheckInCatalogue.setEnabled(false);
        jCheckSellIncTax.setSelected(false);
        jCheckSellIncTax.setEnabled(false);
        jCheckAddStockLevels.setSelected(false);
        jCheckAddStockLevels.setEnabled(false);
        jFileName.setText(null);
        csvFileName = "";
        jTextNew.setText("");
        jTextUpdate.setText("");
        jTextInvalid.setText("");
        jTextMissing.setText("");
        jTextNoChange.setText("");
        jTextRecords.setText("");
        jTextBadPrice.setText("");
        jTextBadCats.setText("");
        Headers.clear();
        newRecords = 0;
        invalidRecords = 0;
        priceUpdates = 0;
        missingData = 0;
        noChanges = 0;
        badPrice = 0;
    }

    /**
     * Checks the field mappings to ensure all compulsory fields have been
     * completed to allow import to proceed
     */
    public void checkFieldMapping() {
        boolean bStockOK = !jCheckAddStockLevels.isSelected();

        if (!bStockOK) {
            if (jComboSecurity.getSelectedItem() != "" && jComboMaximum.getSelectedItem() != "") {
                bStockOK = true;
            }
        }

        if (bStockOK && jComboReference.getSelectedItem() != "" && jComboName.getSelectedItem() != "" && jComboBarcode.getSelectedItem() != ""
                && jComboBuy.getSelectedItem() != "" && jComboSell.getSelectedItem() != "" && jComboCategory.getSelectedItem() != ""
                && m_CategoryModel.getSelectedText() != null) {
            jImport.setEnabled(true);
        } else {
            jImport.setEnabled(false);
        }
        System.out.println("here = " + bStockOK );
    }
    /**
     * Deactivates and resets all form fields.
     *
     * @return
     */
    @Override
    public boolean deactivate() {
        resetFields();
        return (true);
    }

    public void createLocationStock(String ProductID, String LocationID, Double security, Double maximum) throws BasicException {

        // This should only be called on new products - we dont support updates to stock levels
        Object[] values = new Object[5];
        values[0] = UUID.randomUUID().toString();                               // ID string
        values[1] = LocationID;                                           // Reference string
        values[2] = ProductID;                                             // Barcode String        
        values[3] = (double) security;                                                // Name string        
        values[4] = (double) maximum;                                     // IScomment flag (Attribute modifier)

        PreparedSentence sentence = new PreparedSentence(m_App.getSession(), "INSERT INTO STOCKLEVEL (ID, LOCATION, PRODUCT, STOCKSECURITY, STOCKMAXIMUM) VALUES (?, ?, ?, ?, ?)", new SerializerWriteBasicExt((new Datas[]{Datas.STRING, Datas.STRING, Datas.STRING, Datas.DOUBLE, Datas.DOUBLE}),
                new int[]{0, 1, 2, 3, 4}));

        sentence.exec(values);

    }

    public void updateLocationStock(String ProductID, String LocationID, Double security, Double maximum) throws BasicException {

        // This should only be called on new products - we dont support updates to stock levels
        Object[] values = new Object[4];
        values[0] = (double) security;                                                // Name string        
        values[1] = (double) maximum;                                     // IScomment flag (Attribute modifier)
        values[2] = ProductID;                                             // Barcode String        
        values[3] = LocationID;                                           // Reference string

        PreparedSentence sentence = new PreparedSentence(m_App.getSession(), "UPDATE STOCKLEVEL SET STOCKSECURITY = ?, STOCKMAXIMUM = ? WHERE PRODUCT = ? AND LOCATION = ?", new SerializerWriteBasicExt((new Datas[]{Datas.DOUBLE, Datas.DOUBLE, Datas.STRING, Datas.STRING}),
                new int[]{0, 1, 2, 3}));

        sentence.exec(values);

    }

    /**
     *
     * @return
     */
    public Boolean isExistingLocationStock(String ProductID, String LocationID) throws BasicException {
        Boolean bExists = false;

        // This should only be called on new products - we dont support updates to stock levels
        Object[] values = new Object[2];
        values[0] = ProductID;                                             // Barcode String        
        values[1] = LocationID;                                           // Reference string

        PreparedSentence sentence = new PreparedSentence(m_App.getSession(), "SELECT * FROM STOCKLEVEL WHERE PRODUCT = ? AND LOCATION = ?", new SerializerWriteBasicExt((new Datas[]{Datas.STRING, Datas.STRING}),
                new int[]{0, 1}));

        DataResultSet results = sentence.openExec(values);

        if (results.next()) {
            bExists = true;
        }

        return bExists;
    }

    public void UpdateOrCreateLocationStock(String ProductID, String LocationID, Double security, Double maximum) throws BasicException {

        if (isExistingLocationStock(ProductID, LocationID)) {
            updateLocationStock(ProductID, LocationID, security, maximum);
        } else {
            createLocationStock(ProductID, LocationID, security, maximum);
        }
    }

    /**
     *
     * @param pType
     */
    public void createProduct(String pType) {
// create a new product and save it using DalaLogicSales
        Object propBlob;
        try {
            if (prop.isEmpty()) {
                propBlob = null;
            } else {
                propBlob = Formats.BYTEA.parseValue(prop);
            }
        } catch (BasicException e) {
            propBlob = null;
        }

        Object[] myprod = new Object[33];
        myprod[0] = UUID.randomUUID().toString();                               // ID string
        myprod[1] = productReference;                                           // Reference string
        myprod[2] = productBarcode;                                             // Barcode String
        myprod[3] = BarcodeValidator.BarcodeValidate(productBarcode);           // Barcode Type
        myprod[4] = productName;                                                // Name string        
        myprod[5] = ((aux != null) && !aux.isEmpty() && (aux.equals("1") || aux.equalsIgnoreCase("yes")));  // IScondiment flag (Auxiliary modifier)
        myprod[6] = false;                                     // ISscale flag
        myprod[7] = productBuyPrice;                                            // Buy price double
        myprod[8] = productSellPrice;                                           // Sell price double
        myprod[9] = dCategory;                                                  // Category string
        myprod[10] = taxcatmodel.getSelectedKey();                               // Tax string
        myprod[11] = null;                                                      // Attributeset string
        myprod[12] = null;                                                      // Image
        myprod[13] = (double) 0;                                                // Stock cost double
        myprod[14] = (double) 0;                                                // Stock volume double
        myprod[15] = jCheckInCatalogue.isSelected();                            // In catalog flag
        myprod[16] = null;                                                      // catalog order        
        myprod[17] = ((propBlob == null) ? null : propBlob);        // Attributes (Product Properites)
        myprod[18] = ((remotePrint != null) && !remotePrint.isEmpty() && (remotePrint.equals("1") || remotePrint.equalsIgnoreCase("yes"))); // IsKitchen flag
        myprod[19] = ((service != null) && !service.isEmpty() && (service.equals("1") || service.equalsIgnoreCase("yes"))); // isService flag
        myprod[20] = ((buttonText != null) && !buttonText.isEmpty()) ? buttonText : "<HTML>" + productName; // Button Text     
        myprod[21] = ((varPrice != null) && !varPrice.isEmpty() && (varPrice.equals("1") || varPrice.equalsIgnoreCase("yes"))); // isVariable price flag
        myprod[22] = false;                                                     // Compulsory Att flag
        myprod[23] = ((textTip.isEmpty()) ? productName : textTip);                                               // Text tip string
        myprod[24] = ((warranty != null) && !warranty.isEmpty() && (warranty.equals("1") || warranty.equalsIgnoreCase("yes"))); // Warranty flag
        myprod[25] = 0.0;
        myprod[26] = shortName;                                                 // Alias
        myprod[27] = false;                                                     // AlwaysAvailable flag
        myprod[28] = "no";
        myprod[29] = false;
        myprod[30] = ((isPack != null) && !isPack.isEmpty() && (isPack.equals("1") || isPack.equalsIgnoreCase("yes"))); // Is a pack
        myprod[31] = packSize;                                                 // PackQuantity
        myprod[32] = ((packOf==null)||(packOf.isEmpty()))?null:packOf;          // Pack Product

        try {
            if ("new".equals(pType)) {
                spr.insertData(myprod);

                if (jCheckAddStockLevels.isSelected()) {
                    createLocationStock(myprod[0].toString(), stockLocation, stockSecurity, stockMaximum);
                }
            } else {
                myprod[0] = prodInfo.getID();
                spr.updateData(myprod);

                if (jCheckAddStockLevels.isSelected()) {
                    UpdateOrCreateLocationStock(myprod[0].toString(), stockLocation, stockSecurity, stockMaximum);
                }
            }

        } catch (BasicException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @param csvError
     * @param PreviousBuy
     * @param previousSell
     */
    public void createCSVEntry(String csvError, Double PreviousBuy, Double previousSell) {
// create a new csv entry and save it using DataLogicSystem
        Object[] myprod = new Object[11];
        myprod[0] = UUID.randomUUID().toString();                               // ID string
        myprod[1] = Integer.toString(currentRecord);                            // Record number
        myprod[2] = csvError;                                                   // Error description
        myprod[3] = productReference;                                           // Reference string
        myprod[4] = productBarcode;                                             // Barcode String        
        myprod[5] = productName;                                                // Name string        
        myprod[6] = productBuyPrice;                                            // Buy price
        myprod[7] = productSellPrice;                                           // Sell price
        myprod[8] = PreviousBuy;                                                // Previous Buy price double
        myprod[9] = previousSell;                                               // Previous Sell price double
        myprod[10] = Category;

        try {
            m_dlSystem.execAddCSVEntry(myprod);
        } catch (BasicException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     *
     * @return
     */
    public String getRecord() {
        // Get record type using using DataLogicSystem
        Object[] myprod = new Object[3];
        myprod[0] = productReference;
        myprod[1] = productBarcode;
        myprod[2] = productName;
        try {
            return (m_dlSystem.getProductRecordType(myprod));
        } catch (BasicException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "Exception";
    }

    /**
     *
     * Returns true if any entry in the combo boxes is in use
     *
     * @param entry
     * @return
     */
    public boolean isEntryInUse(String entry) {

        return ((entry == jComboCategory.getSelectedItem())
                | (entry == jComboReference.getSelectedItem())
                | (entry == jComboName.getSelectedItem())
                | (entry == jComboBuy.getSelectedItem())
                | (entry == jComboSell.getSelectedItem())
                | (entry == jComboMaximum.getSelectedItem())
                | (entry == jComboSecurity.getSelectedItem())
                | (entry == jComboBarcode.getSelectedItem())
                | (entry == jComboBoxButtonText.getSelectedItem())
                | (entry == jComboBoxRemotePrint.getSelectedItem())
                | (entry == jComboBoxService.getSelectedItem())
                | (entry == jComboBoxVarPrice.getSelectedItem())
                | (entry == jComboBoxWarranty.getSelectedItem())
                | (entry == jComboBoxTextTip.getSelectedItem())
                | (entry == jComboBoxProp.getSelectedItem())
                | (entry == jComboBoxAux.getSelectedItem())
                | (entry == jComboBoxShortName.getSelectedItem())
                | (entry == jComboBoxIspack.getSelectedItem())
                | (entry == jComboBoxPackSize.getSelectedItem())
                | (entry == jComboBoxPackOf.getSelectedItem()));

    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jHeader = new javax.swing.JPanel();
        jComboSeparator = new javax.swing.JComboBox();
        jLabel17 = new javax.swing.JLabel();
        jLabel18 = new javax.swing.JLabel();
        jFileChooserPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jFileName = new javax.swing.JTextField();
        jbtnDbDriverLib = new javax.swing.JButton();
        jHeaderRead = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jComboReference = new javax.swing.JComboBox();
        jComboBarcode = new javax.swing.JComboBox();
        jComboName = new javax.swing.JComboBox();
        jComboBuy = new javax.swing.JComboBox();
        jComboSell = new javax.swing.JComboBox();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jLabel20 = new javax.swing.JLabel();
        jLabel22 = new javax.swing.JLabel();
        jComboSecurity = new javax.swing.JComboBox();
        jComboBoxShortName = new javax.swing.JComboBox();
        jLabelShortName = new javax.swing.JLabel();
        jComboMaximum = new javax.swing.JComboBox();
        jLabel23 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jLabel12 = new javax.swing.JLabel();
        jCheckInCatalogue = new javax.swing.JCheckBox();
        jCheckSellIncTax = new javax.swing.JCheckBox();
        jCheckAddStockLevels = new javax.swing.JCheckBox();
        jLabel24 = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jComboCategory = new javax.swing.JComboBox();
        jComboDefaultCategory = new javax.swing.JComboBox();
        jComboTax = new javax.swing.JComboBox();
        jCustom = new javax.swing.JPanel();
        jCustom1 = new javax.swing.JPanel();
        jLabelButtonText = new javax.swing.JLabel();
        jComboBoxButtonText = new javax.swing.JComboBox();
        jLabelWarranty = new javax.swing.JLabel();
        jComboBoxWarranty = new javax.swing.JComboBox();
        jLabelProp = new javax.swing.JLabel();
        jComboBoxProp = new javax.swing.JComboBox();
        jLabelVarPrice = new javax.swing.JLabel();
        jComboBoxVarPrice = new javax.swing.JComboBox();
        jComboBoxIspack = new javax.swing.JComboBox();
        jLabelProp1 = new javax.swing.JLabel();
        jComboBoxPackOf = new javax.swing.JComboBox();
        jLabelProp2 = new javax.swing.JLabel();
        jCustom3 = new javax.swing.JPanel();
        jCustom2 = new javax.swing.JPanel();
        jLabelService = new javax.swing.JLabel();
        jComboBoxService = new javax.swing.JComboBox();
        jLabelAux = new javax.swing.JLabel();
        jComboBoxAux = new javax.swing.JComboBox();
        jLabelRemotePrint = new javax.swing.JLabel();
        jComboBoxRemotePrint = new javax.swing.JComboBox();
        jLabelTextTip = new javax.swing.JLabel();
        jComboBoxTextTip = new javax.swing.JComboBox();
        jComboBoxPackSize = new javax.swing.JComboBox();
        jLabelTextTip1 = new javax.swing.JLabel();
        jPanel2 = new javax.swing.JPanel();
        jLabel9 = new javax.swing.JLabel();
        jLabel14 = new javax.swing.JLabel();
        jLabel16 = new javax.swing.JLabel();
        jTextUpdates = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        jTextRecords = new javax.swing.JTextField();
        jTextNew = new javax.swing.JTextField();
        jTextInvalid = new javax.swing.JTextField();
        jTextUpdate = new javax.swing.JTextField();
        jTextMissing = new javax.swing.JTextField();
        jTextBadPrice = new javax.swing.JTextField();
        jTextNoChange = new javax.swing.JTextField();
        jLabel19 = new javax.swing.JLabel();
        jTextBadCats = new javax.swing.JTextField();
        jFooter = new javax.swing.JPanel();
        jParamsLocation = new uk.chromis.pos.reports.JParamsLocation();
        jImport = new javax.swing.JButton();

        setOpaque(false);
        setPreferredSize(new java.awt.Dimension(630, 430));

        jComboSeparator.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jComboSeparator.setPreferredSize(new java.awt.Dimension(50, 30));

        jLabel17.setFont(new java.awt.Font("Tahoma", 0, 10)); // NOI18N

        jLabel18.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("pos_messages"); // NOI18N
        jLabel18.setText(bundle.getString("label.csvdelimit")); // NOI18N
        jLabel18.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel1.setText(bundle.getString("label.csvfile")); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(100, 30));

        jFileName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jFileName.setPreferredSize(new java.awt.Dimension(275, 30));
        jFileName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jFileNameActionPerformed(evt);
            }
        });

        jbtnDbDriverLib.setIcon(new javax.swing.ImageIcon(getClass().getResource("/uk/chromis/images/fileopen.png"))); // NOI18N
        jbtnDbDriverLib.setMaximumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setMinimumSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.setPreferredSize(new java.awt.Dimension(64, 32));
        jbtnDbDriverLib.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jbtnDbDriverLibActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jFileChooserPanelLayout = new javax.swing.GroupLayout(jFileChooserPanel);
        jFileChooserPanel.setLayout(jFileChooserPanelLayout);
        jFileChooserPanelLayout.setHorizontalGroup(
            jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileChooserPanelLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(116, 116, 116))
        );
        jFileChooserPanelLayout.setVerticalGroup(
            jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFileChooserPanelLayout.createSequentialGroup()
                .addGroup(jFileChooserPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jFileName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jbtnDbDriverLib, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jHeaderRead.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jHeaderRead.setText(bundle.getString("label.csvread")); // NOI18N
        jHeaderRead.setEnabled(false);
        jHeaderRead.setPreferredSize(new java.awt.Dimension(120, 30));
        jHeaderRead.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jHeaderReadActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jHeaderLayout = new javax.swing.GroupLayout(jHeader);
        jHeader.setLayout(jHeaderLayout);
        jHeaderLayout.setHorizontalGroup(
            jHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jHeaderLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jLabel17)
                .addContainerGap())
            .addGroup(jHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jHeaderLayout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jHeaderLayout.createSequentialGroup()
                            .addGap(4, 4, 4)
                            .addComponent(jFileChooserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(jHeaderLayout.createSequentialGroup()
                            .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                            .addComponent(jComboSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGap(106, 106, 106)
                            .addComponent(jHeaderRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addContainerGap(340, Short.MAX_VALUE)))
        );
        jHeaderLayout.setVerticalGroup(
            jHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jHeaderLayout.createSequentialGroup()
                .addComponent(jLabel17, javax.swing.GroupLayout.PREFERRED_SIZE, 0, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 113, Short.MAX_VALUE))
            .addGroup(jHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jHeaderLayout.createSequentialGroup()
                    .addGap(23, 23, 23)
                    .addComponent(jFileChooserPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addGroup(jHeaderLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jComboSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jHeaderRead, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        jPanel4.setLayout(new javax.swing.BoxLayout(jPanel4, javax.swing.BoxLayout.LINE_AXIS));

        jComboReference.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboReference.setMaximumRowCount(12);
        jComboReference.setEnabled(false);
        jComboReference.setMinimumSize(new java.awt.Dimension(32, 25));
        jComboReference.setPreferredSize(new java.awt.Dimension(100, 30));
        jComboReference.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });

        jComboBarcode.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBarcode.setMaximumRowCount(12);
        jComboBarcode.setEnabled(false);
        jComboBarcode.setMinimumSize(new java.awt.Dimension(32, 25));
        jComboBarcode.setPreferredSize(new java.awt.Dimension(275, 30));
        jComboBarcode.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboItemStateChanged(evt);
            }
        });
        jComboBarcode.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });

        jComboName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboName.setMaximumRowCount(12);
        jComboName.setEnabled(false);
        jComboName.setMinimumSize(new java.awt.Dimension(32, 25));
        jComboName.setPreferredSize(new java.awt.Dimension(275, 30));
        jComboName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboItemStateChanged(evt);
            }
        });
        jComboName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });

        jComboBuy.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBuy.setMaximumRowCount(12);
        jComboBuy.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "" }));
        jComboBuy.setSelectedIndex(-1);
        jComboBuy.setEnabled(false);
        jComboBuy.setMinimumSize(new java.awt.Dimension(32, 25));
        jComboBuy.setPreferredSize(new java.awt.Dimension(275, 30));
        jComboBuy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboItemStateChanged(evt);
            }
        });
        jComboBuy.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });

        jComboSell.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboSell.setMaximumRowCount(12);
        jComboSell.setEnabled(false);
        jComboSell.setMinimumSize(new java.awt.Dimension(32, 25));
        jComboSell.setPreferredSize(new java.awt.Dimension(275, 30));
        jComboSell.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboItemStateChanged(evt);
            }
        });
        jComboSell.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });

        jLabel3.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel3.setText(bundle.getString("label.prodref")); // NOI18N
        jLabel3.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel4.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel4.setText(bundle.getString("label.prodbarcode")); // NOI18N
        jLabel4.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel5.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel5.setText(bundle.getString("label.prodname")); // NOI18N
        jLabel5.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel10.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel10.setText(bundle.getString("label.prodpricebuy")); // NOI18N
        jLabel10.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel20.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel20.setText(bundle.getString("label.prodpricesell")); // NOI18N
        jLabel20.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel22.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel22.setText("Security");

        jComboSecurity.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboSecurity.setMaximumRowCount(12);
        jComboSecurity.setEnabled(false);

        jComboBoxShortName.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxShortName.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxShortName.setSelectedIndex(-1);
        jComboBoxShortName.setEnabled(false);
        jComboBoxShortName.setPreferredSize(new java.awt.Dimension(100, 30));
        jComboBoxShortName.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });

        jLabelShortName.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelShortName.setText(bundle.getString("Label.Alias")); // NOI18N

        jComboMaximum.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboMaximum.setMaximumRowCount(12);
        jComboMaximum.setEnabled(false);

        jLabel23.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel23.setText(bundle.getString("label.maximum")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel22)
                            .addComponent(jLabel23))
                        .addGap(53, 53, 53)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jComboMaximum, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jComboSecurity, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabelShortName))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboName, javax.swing.GroupLayout.Alignment.LEADING, 0, 1, Short.MAX_VALUE)
                            .addComponent(jComboSell, 0, 1, Short.MAX_VALUE)
                            .addComponent(jComboReference, 0, 191, Short.MAX_VALUE)
                            .addComponent(jComboBuy, javax.swing.GroupLayout.Alignment.LEADING, 0, 1, Short.MAX_VALUE)
                            .addComponent(jComboBarcode, javax.swing.GroupLayout.Alignment.LEADING, 0, 1, Short.MAX_VALUE)
                            .addComponent(jComboBoxShortName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(2, 2, 2)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboReference, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBarcode, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBoxShortName, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabelShortName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboSell, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel20, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 62, Short.MAX_VALUE)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboSecurity, javax.swing.GroupLayout.PREFERRED_SIZE, 32, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboMaximum, javax.swing.GroupLayout.PREFERRED_SIZE, 33, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23, javax.swing.GroupLayout.PREFERRED_SIZE, 26, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(10, 10, 10))
        );

        jPanel4.add(jPanel1);

        jLabel8.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel8.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel8.setText(bundle.getString("label.prodincatalog")); // NOI18N

        jLabel12.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel12.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        jLabel12.setText(bundle.getString("label.csvsellingintax")); // NOI18N

        jCheckInCatalogue.setEnabled(false);

        jCheckSellIncTax.setEnabled(false);

        jCheckAddStockLevels.setEnabled(false);
        jCheckAddStockLevels.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboItemStateChanged(evt);
            }
        });

        jLabel24.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel24.setText("Include Stock Levels");

        jLabel11.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel11.setText(bundle.getString("label.prodcategory")); // NOI18N
        jLabel11.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel6.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel6.setText(bundle.getString("label.proddefaultcategory")); // NOI18N
        jLabel6.setPreferredSize(new java.awt.Dimension(100, 30));

        jLabel7.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel7.setText(bundle.getString("label.prodtaxcode")); // NOI18N
        jLabel7.setPreferredSize(new java.awt.Dimension(100, 30));

        jComboCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboCategory.setMaximumRowCount(12);
        jComboCategory.setEnabled(false);
        jComboCategory.setMinimumSize(new java.awt.Dimension(32, 25));
        jComboCategory.setName(""); // NOI18N
        jComboCategory.setPreferredSize(new java.awt.Dimension(275, 30));
        jComboCategory.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                jComboCategoryItemStateChanged(evt);
            }
        });
        jComboCategory.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });

        jComboDefaultCategory.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboDefaultCategory.setMaximumRowCount(12);
        jComboDefaultCategory.setEnabled(false);
        jComboDefaultCategory.setMinimumSize(new java.awt.Dimension(32, 25));
        jComboDefaultCategory.setPreferredSize(new java.awt.Dimension(275, 30));
        jComboDefaultCategory.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jComboDefaultCategoryActionPerformed(evt);
            }
        });

        jComboTax.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboTax.setMaximumRowCount(12);
        jComboTax.setEnabled(false);
        jComboTax.setPreferredSize(new java.awt.Dimension(275, 30));

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(0, 95, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel3Layout.createSequentialGroup()
                                .addGap(1, 1, 1)
                                .addComponent(jCheckInCatalogue, javax.swing.GroupLayout.PREFERRED_SIZE, 20, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(jCheckSellIncTax, javax.swing.GroupLayout.Alignment.TRAILING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(1, 1, 1)
                        .addComponent(jCheckAddStockLevels)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 163, javax.swing.GroupLayout.PREFERRED_SIZE))))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jComboDefaultCategory, javax.swing.GroupLayout.Alignment.TRAILING, 0, 193, Short.MAX_VALUE)
                    .addComponent(jComboTax, javax.swing.GroupLayout.Alignment.TRAILING, 0, 1, Short.MAX_VALUE)
                    .addComponent(jComboCategory, 0, 1, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jComboCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboDefaultCategory, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboTax, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 141, Short.MAX_VALUE)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jCheckAddStockLevels)
                    .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jCheckInCatalogue)
                        .addGap(10, 10, 10)
                        .addComponent(jCheckSellIncTax))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel12, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addGap(15, 15, 15))
        );

        jPanel4.add(jPanel3);

        jTabbedPane1.addTab(bundle.getString("label.general"), jPanel4); // NOI18N

        jCustom.setLayout(new javax.swing.BoxLayout(jCustom, javax.swing.BoxLayout.LINE_AXIS));

        jCustom1.setMaximumSize(new java.awt.Dimension(320, 348));
        jCustom1.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelButtonText.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelButtonText.setText("Button Text");
        jCustom1.add(jLabelButtonText, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 73, -1, -1));

        jComboBoxButtonText.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxButtonText.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxButtonText.setSelectedIndex(-1);
        jComboBoxButtonText.setEnabled(false);
        jComboBoxButtonText.setLightWeightPopupEnabled(false);
        jComboBoxButtonText.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxButtonText.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom1.add(jComboBoxButtonText, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 69, 200, -1));

        jLabelWarranty.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelWarranty.setText("Warranty");
        jCustom1.add(jLabelWarranty, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, 61, -1));

        jComboBoxWarranty.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxWarranty.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxWarranty.setSelectedIndex(-1);
        jComboBoxWarranty.setEnabled(false);
        jComboBoxWarranty.setMaximumSize(new java.awt.Dimension(200, 23));
        jComboBoxWarranty.setPreferredSize(new java.awt.Dimension(200, 23));
        jComboBoxWarranty.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom1.add(jComboBoxWarranty, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 11, -1, -1));

        jLabelProp.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelProp.setText(bundle.getString("label.prodproperties")); // NOI18N
        jCustom1.add(jLabelProp, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 102, 90, -1));

        jComboBoxProp.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxProp.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxProp.setSelectedIndex(-1);
        jComboBoxProp.setEnabled(false);
        jComboBoxProp.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxProp.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom1.add(jComboBoxProp, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 98, 200, -1));

        jLabelVarPrice.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelVarPrice.setText("Var Price");
        jCustom1.add(jLabelVarPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 44, -1, -1));

        jComboBoxVarPrice.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxVarPrice.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxVarPrice.setSelectedIndex(-1);
        jComboBoxVarPrice.setEnabled(false);
        jComboBoxVarPrice.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxVarPrice.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom1.add(jComboBoxVarPrice, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 200, -1));

        jComboBoxIspack.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxIspack.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxIspack.setSelectedIndex(-1);
        jComboBoxIspack.setEnabled(false);
        jComboBoxIspack.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxIspack.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom1.add(jComboBoxIspack, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 127, 200, -1));

        jLabelProp1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelProp1.setText(bundle.getString("label.ispack")); // NOI18N
        jCustom1.add(jLabelProp1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 127, 90, 23));

        jComboBoxPackOf.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxPackOf.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxPackOf.setSelectedIndex(-1);
        jComboBoxPackOf.setEnabled(false);
        jComboBoxPackOf.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxPackOf.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom1.add(jComboBoxPackOf, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 156, 200, -1));

        jLabelProp2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelProp2.setText(bundle.getString("label.productinpack")); // NOI18N
        jCustom1.add(jLabelProp2, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 160, 90, -1));

        jCustom.add(jCustom1);

        javax.swing.GroupLayout jCustom3Layout = new javax.swing.GroupLayout(jCustom3);
        jCustom3.setLayout(jCustom3Layout);
        jCustom3Layout.setHorizontalGroup(
            jCustom3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 40, Short.MAX_VALUE)
        );
        jCustom3Layout.setVerticalGroup(
            jCustom3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 355, Short.MAX_VALUE)
        );

        jCustom.add(jCustom3);

        jCustom2.setMaximumSize(new java.awt.Dimension(320, 348));
        jCustom2.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        jLabelService.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelService.setText("Service Item");
        jCustom2.add(jLabelService, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 15, -1, -1));

        jComboBoxService.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxService.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxService.setSelectedIndex(-1);
        jComboBoxService.setEnabled(false);
        jComboBoxService.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxService.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom2.add(jComboBoxService, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 11, 200, -1));

        jLabelAux.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelAux.setText("Aux Product");
        jCustom2.add(jLabelAux, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 44, -1, -1));

        jComboBoxAux.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxAux.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxAux.setSelectedIndex(-1);
        jComboBoxAux.setEnabled(false);
        jComboBoxAux.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxAux.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom2.add(jComboBoxAux, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 40, 200, -1));

        jLabelRemotePrint.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelRemotePrint.setText("Remote Print");
        jCustom2.add(jLabelRemotePrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 73, -1, -1));

        jComboBoxRemotePrint.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxRemotePrint.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxRemotePrint.setSelectedIndex(-1);
        jComboBoxRemotePrint.setEnabled(false);
        jComboBoxRemotePrint.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxRemotePrint.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom2.add(jComboBoxRemotePrint, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 69, 200, -1));

        jLabelTextTip.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelTextTip.setText(bundle.getString("label.texttip")); // NOI18N
        jCustom2.add(jLabelTextTip, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 102, -1, -1));

        jComboBoxTextTip.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxTextTip.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxTextTip.setSelectedIndex(-1);
        jComboBoxTextTip.setEnabled(false);
        jComboBoxTextTip.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxTextTip.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom2.add(jComboBoxTextTip, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 98, 200, -1));

        jComboBoxPackSize.setFont(new java.awt.Font("Arial", 0, 14)); // NOI18N
        jComboBoxPackSize.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jComboBoxPackSize.setSelectedIndex(-1);
        jComboBoxPackSize.setEnabled(false);
        jComboBoxPackSize.setMaximumSize(new java.awt.Dimension(66, 23));
        jComboBoxPackSize.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jComboBoxFocusGained(evt);
            }
        });
        jCustom2.add(jComboBoxPackSize, new org.netbeans.lib.awtextra.AbsoluteConstraints(110, 127, 200, -1));

        jLabelTextTip1.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabelTextTip1.setText(bundle.getString("label.packsof")); // NOI18N
        jCustom2.add(jLabelTextTip1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 127, 96, 23));

        jCustom.add(jCustom2);

        jTabbedPane1.addTab(bundle.getString("label.attributes"), jCustom); // NOI18N

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(new javax.swing.border.LineBorder(new java.awt.Color(153, 153, 153), 1, true), bundle.getString("title.CSVImport"), javax.swing.border.TitledBorder.DEFAULT_JUSTIFICATION, javax.swing.border.TitledBorder.DEFAULT_POSITION, new java.awt.Font("Arial", 1, 12), new java.awt.Color(102, 102, 102))); // NOI18N

        jLabel9.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel9.setText(bundle.getString("label.csvrecordsfound")); // NOI18N
        jLabel9.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel14.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel14.setText(bundle.getString("label.csvnewproducts")); // NOI18N
        jLabel14.setMaximumSize(new java.awt.Dimension(77, 14));
        jLabel14.setMinimumSize(new java.awt.Dimension(77, 14));
        jLabel14.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel16.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel16.setText(bundle.getString("label.cvsinvalid")); // NOI18N
        jLabel16.setPreferredSize(new java.awt.Dimension(100, 25));

        jTextUpdates.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jTextUpdates.setText(bundle.getString("label.csvpriceupdated")); // NOI18N
        jTextUpdates.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel2.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel2.setText(bundle.getString("label.csvmissing")); // NOI18N
        jLabel2.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel15.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel15.setText(bundle.getString("label.csvbad")); // NOI18N
        jLabel15.setPreferredSize(new java.awt.Dimension(100, 25));

        jLabel13.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel13.setText(bundle.getString("label.cvsnotchanged")); // NOI18N
        jLabel13.setPreferredSize(new java.awt.Dimension(100, 25));

        jTextRecords.setBackground(new java.awt.Color(224, 223, 227));
        jTextRecords.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextRecords.setForeground(new java.awt.Color(102, 102, 102));
        jTextRecords.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextRecords.setBorder(null);
        jTextRecords.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextRecords.setEnabled(false);
        jTextRecords.setPreferredSize(new java.awt.Dimension(70, 25));

        jTextNew.setBackground(new java.awt.Color(224, 223, 227));
        jTextNew.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextNew.setForeground(new java.awt.Color(102, 102, 102));
        jTextNew.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextNew.setBorder(null);
        jTextNew.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextNew.setEnabled(false);
        jTextNew.setPreferredSize(new java.awt.Dimension(70, 25));

        jTextInvalid.setBackground(new java.awt.Color(224, 223, 227));
        jTextInvalid.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextInvalid.setForeground(new java.awt.Color(102, 102, 102));
        jTextInvalid.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextInvalid.setBorder(null);
        jTextInvalid.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextInvalid.setEnabled(false);
        jTextInvalid.setPreferredSize(new java.awt.Dimension(70, 25));

        jTextUpdate.setBackground(new java.awt.Color(224, 223, 227));
        jTextUpdate.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextUpdate.setForeground(new java.awt.Color(102, 102, 102));
        jTextUpdate.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextUpdate.setBorder(null);
        jTextUpdate.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextUpdate.setEnabled(false);
        jTextUpdate.setPreferredSize(new java.awt.Dimension(70, 25));

        jTextMissing.setBackground(new java.awt.Color(224, 223, 227));
        jTextMissing.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextMissing.setForeground(new java.awt.Color(102, 102, 102));
        jTextMissing.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextMissing.setBorder(null);
        jTextMissing.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextMissing.setEnabled(false);
        jTextMissing.setPreferredSize(new java.awt.Dimension(70, 25));

        jTextBadPrice.setBackground(new java.awt.Color(224, 223, 227));
        jTextBadPrice.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextBadPrice.setForeground(new java.awt.Color(102, 102, 102));
        jTextBadPrice.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextBadPrice.setBorder(null);
        jTextBadPrice.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextBadPrice.setEnabled(false);
        jTextBadPrice.setPreferredSize(new java.awt.Dimension(70, 25));

        jTextNoChange.setBackground(new java.awt.Color(224, 223, 227));
        jTextNoChange.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextNoChange.setForeground(new java.awt.Color(102, 102, 102));
        jTextNoChange.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextNoChange.setBorder(null);
        jTextNoChange.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextNoChange.setEnabled(false);
        jTextNoChange.setPreferredSize(new java.awt.Dimension(70, 25));

        jLabel19.setFont(new java.awt.Font("Arial", 0, 12)); // NOI18N
        jLabel19.setText(bundle.getString("label.cvsbadcats")); // NOI18N
        jLabel19.setPreferredSize(new java.awt.Dimension(100, 25));

        jTextBadCats.setBackground(new java.awt.Color(224, 223, 227));
        jTextBadCats.setFont(new java.awt.Font("Arial", 1, 11)); // NOI18N
        jTextBadCats.setForeground(new java.awt.Color(102, 102, 102));
        jTextBadCats.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        jTextBadCats.setBorder(null);
        jTextBadCats.setDisabledTextColor(new java.awt.Color(0, 0, 0));
        jTextBadCats.setEnabled(false);
        jTextBadCats.setPreferredSize(new java.awt.Dimension(70, 25));

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jTextRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel19, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jTextUpdates, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel15, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jTextBadCats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextInvalid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextMissing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextBadPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextNoChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(26, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(0, 0, 0)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextRecords, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel14, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextNew, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel16, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextInvalid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextUpdates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextUpdate, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextMissing, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextBadPrice, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextNoChange, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel19, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextBadCats, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jImport.setFont(new java.awt.Font("Arial", 0, 11)); // NOI18N
        jImport.setText(bundle.getString("label.csvimpostbtn")); // NOI18N
        jImport.setEnabled(false);
        jImport.setPreferredSize(new java.awt.Dimension(120, 30));
        jImport.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jImportActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jFooterLayout = new javax.swing.GroupLayout(jFooter);
        jFooter.setLayout(jFooterLayout);
        jFooterLayout.setHorizontalGroup(
            jFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFooterLayout.createSequentialGroup()
                .addGap(20, 20, 20)
                .addComponent(jParamsLocation, javax.swing.GroupLayout.PREFERRED_SIZE, 361, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 232, Short.MAX_VALUE)
                .addComponent(jImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
        jFooterLayout.setVerticalGroup(
            jFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jFooterLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jFooterLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jParamsLocation, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(jFooterLayout.createSequentialGroup()
                        .addGap(14, 14, 14)
                        .addComponent(jImport, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jHeader, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 666, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(99, Short.MAX_VALUE))))
            .addGroup(layout.createSequentialGroup()
                .addComponent(jFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jHeader, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTabbedPane1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 90, Short.MAX_VALUE)
                .addComponent(jFooter, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void jHeaderReadActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jHeaderReadActionPerformed
        try {
            GetheadersFromFile(jFileName.getText());
        } catch (IOException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_jHeaderReadActionPerformed

    private void jImportActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jImportActionPerformed
// prevent any more key presses
        jImport.setEnabled(false);

        try {
            ImportCsvFile(jFileName.getText());
        } catch (IOException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }

    }//GEN-LAST:event_jImportActionPerformed

    private void jFileNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jFileNameActionPerformed
        jImport.setEnabled(false);
        jHeaderRead.setEnabled(true);
    }//GEN-LAST:event_jFileNameActionPerformed

    private void jbtnDbDriverLibActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jbtnDbDriverLibActionPerformed
        resetFields();

        // If CSV.last_file is null then use c:\ otherwise use saved dir
        JFileChooser chooser = new JFileChooser(last_folder == null ? "C:\\" : last_folder);
        FileNameExtensionFilter filter = new FileNameExtensionFilter("csv files", "csv");
        chooser.setFileFilter(filter);
        chooser.showOpenDialog(null);
        File csvFile = chooser.getSelectedFile();
// check if a file was selected        
        if (csvFile == null) {
            return;
        }

        File current_folder = chooser.getCurrentDirectory();
        // If we have a file lets save the directory for later use if it's different from the old
        if (last_folder == null || !last_folder.equals(current_folder.getAbsolutePath())) {
            AppConfig CSVConfig = new AppConfig(config_file);
            CSVConfig.load();
            CSVConfig.setProperty("CSV.last_folder", current_folder.getAbsolutePath());
            last_folder = current_folder.getAbsolutePath();
            try {
                CSVConfig.save();
            } catch (IOException ex) {
                Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        String csv = csvFile.getName();
        if (!(csv.trim().equals(""))) {
            csvFileName = csvFile.getAbsolutePath();
            jFileName.setText(csvFileName);
        }
    }//GEN-LAST:event_jbtnDbDriverLibActionPerformed

    private void jComboDefaultCategoryActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jComboDefaultCategoryActionPerformed
        checkFieldMapping();
    }//GEN-LAST:event_jComboDefaultCategoryActionPerformed

    private void jComboCategoryItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboCategoryItemStateChanged
        // if we have not selected [ USE DEFAULT CATEGORY ] allow the [ REJECT ITEMS WITH BAD CATEGORIES ] to be used in default category combo box
        try {
            if (jComboCategory.getSelectedItem() == "[ USE DEFAULT CATEGORY ]") {
                m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
                jComboDefaultCategory.setModel(m_CategoryModel);
            } else {
                m_CategoryModel = new ComboBoxValModel(m_sentcat.list());
                m_CategoryModel.add(reject_bad_categories_text);
                jComboDefaultCategory.setModel(m_CategoryModel);
            }
        } catch (BasicException ex) {
            Logger.getLogger(JPanelCSVImport.class.getName()).log(Level.SEVERE, null, ex);
        }
        checkFieldMapping();

    }//GEN-LAST:event_jComboCategoryItemStateChanged

    private void jComboBoxFocusGained(java.awt.event.FocusEvent evt) {//GEN-FIRST:event_jComboBoxFocusGained
        JComboBox myJComboBox = ((javax.swing.JComboBox) (evt.getComponent()));
        myJComboBox.removeAllItems();
        int i = 1;
        myJComboBox.addItem("");
        while (i < Headers.size()) {
            if (!isEntryInUse(Headers.get(i))) {
                myJComboBox.addItem(Headers.get(i));
            }
            ++i;
        }
    }//GEN-LAST:event_jComboBoxFocusGained

    private void jComboItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_jComboItemStateChanged
        checkFieldMapping();
    }//GEN-LAST:event_jComboItemStateChanged

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JCheckBox jCheckAddStockLevels;
    private javax.swing.JCheckBox jCheckInCatalogue;
    private javax.swing.JCheckBox jCheckSellIncTax;
    private javax.swing.JComboBox jComboBarcode;
    private javax.swing.JComboBox jComboBoxAux;
    private javax.swing.JComboBox jComboBoxButtonText;
    private javax.swing.JComboBox jComboBoxIspack;
    private javax.swing.JComboBox jComboBoxPackOf;
    private javax.swing.JComboBox jComboBoxPackSize;
    private javax.swing.JComboBox jComboBoxProp;
    private javax.swing.JComboBox jComboBoxRemotePrint;
    private javax.swing.JComboBox jComboBoxService;
    private javax.swing.JComboBox jComboBoxShortName;
    private javax.swing.JComboBox jComboBoxTextTip;
    private javax.swing.JComboBox jComboBoxVarPrice;
    private javax.swing.JComboBox jComboBoxWarranty;
    private javax.swing.JComboBox jComboBuy;
    private javax.swing.JComboBox jComboCategory;
    private javax.swing.JComboBox jComboDefaultCategory;
    private javax.swing.JComboBox jComboMaximum;
    private javax.swing.JComboBox jComboName;
    private javax.swing.JComboBox jComboReference;
    private javax.swing.JComboBox jComboSecurity;
    private javax.swing.JComboBox jComboSell;
    private javax.swing.JComboBox jComboSeparator;
    private javax.swing.JComboBox jComboTax;
    private javax.swing.JPanel jCustom;
    private javax.swing.JPanel jCustom1;
    private javax.swing.JPanel jCustom2;
    private javax.swing.JPanel jCustom3;
    private javax.swing.JPanel jFileChooserPanel;
    private javax.swing.JTextField jFileName;
    private javax.swing.JPanel jFooter;
    private javax.swing.JPanel jHeader;
    private javax.swing.JButton jHeaderRead;
    private javax.swing.JButton jImport;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabelAux;
    private javax.swing.JLabel jLabelButtonText;
    private javax.swing.JLabel jLabelProp;
    private javax.swing.JLabel jLabelProp1;
    private javax.swing.JLabel jLabelProp2;
    private javax.swing.JLabel jLabelRemotePrint;
    private javax.swing.JLabel jLabelService;
    private javax.swing.JLabel jLabelShortName;
    private javax.swing.JLabel jLabelTextTip;
    private javax.swing.JLabel jLabelTextTip1;
    private javax.swing.JLabel jLabelVarPrice;
    private javax.swing.JLabel jLabelWarranty;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private uk.chromis.pos.reports.JParamsLocation jParamsLocation;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTextField jTextBadCats;
    private javax.swing.JTextField jTextBadPrice;
    private javax.swing.JTextField jTextInvalid;
    private javax.swing.JTextField jTextMissing;
    private javax.swing.JTextField jTextNew;
    private javax.swing.JTextField jTextNoChange;
    private javax.swing.JTextField jTextRecords;
    private javax.swing.JTextField jTextUpdate;
    private javax.swing.JLabel jTextUpdates;
    private javax.swing.JButton jbtnDbDriverLib;
    // End of variables declaration//GEN-END:variables
}
