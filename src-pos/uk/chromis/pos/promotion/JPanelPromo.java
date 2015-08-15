package uk.chromis.pos.promotion;
import javax.swing.ListCellRenderer;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ListCellRendererBasic;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.data.loader.TableDefinition;
import uk.chromis.format.Formats;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.Vectorer;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.data.user.SaveProvider;
import uk.chromis.data.user.ListProvider;
import uk.chromis.data.user.ListProviderCreator;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.panels.*;

/**
 * @author Aurelien Escartin
 */
public class JPanelPromo extends JPanelTable {
    
    private TableDefinition tpromo;
    private PromoEditor jeditor;
    
    /** Creates a new instance of JPanelPlaces */
    public JPanelPromo() {
    }
    
    /**
     *
     */
    protected void init() {
        DataLogicSales dlSales = null;
        dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");

        tpromo = new TableDefinition(app.getSession(),
            "PROMO_HEADER"
            , new String[] {"ID", "NAME", "STARTDATE", "ENDDATE", "STARTHOUR","ENDHOUR",
                "ARTICLE","ARTICLECATEGORY","TYPE","AMOUNT","QTYMIN","QTYMAX","QTYSTEP",
                "AMOUNTSTEP","BONUSARTICLE","BONUSARTICLEDESC"}
            , new String[] {"ID", "NAME", "STARTDATE", "ENDDATE", "STARTHOUR","ENDHOUR",
                "ARTICLE","ARTICLECATEGORY","TYPE","AMOUNT","QTYMIN","QTYMAX","QTYSTEP",
                "AMOUNTSTEP","BONUSARTICLE","BONUSARTICLEDESC"}    
            , new Datas[] {Datas.STRING, Datas.STRING, Datas.INT, Datas.INT, Datas.INT,Datas.INT,Datas.STRING,Datas.STRING,Datas.INT
            ,Datas.DOUBLE,Datas.INT,Datas.INT,Datas.INT,Datas.INT,Datas.STRING,Datas.STRING}
            , new Formats[] {Formats.STRING, Formats.STRING, Formats.INT, Formats.INT, Formats.INT,Formats.INT,Formats.STRING,Formats.STRING,Formats.INT
            ,Formats.DOUBLE,Formats.INT,Formats.INT,Formats.INT,Formats.INT,Formats.STRING,Formats.STRING}
            , new int[] {0}
        ); 
        jeditor = new PromoEditor(app, dlSales, dirty); 
        AppLocal.LIST_BY_RIGHTS="";        
    }
        
    /**
     *
     * @return
     */
    public ListProvider getListProvider() {
        return new ListProviderCreator(tpromo);
    }
    
    /**
     *
     * @return
     */
    public SaveProvider getSaveProvider() {
        return new SaveProvider(tpromo);      
    }
    
    /**
     *
     * @return
     */
    @Override
    public Vectorer getVectorer() {
        return tpromo.getVectorerBasic(new int[]{1});
    }
    
    /**
     *
     * @return
     */
    @Override
    public ListCellRenderer getListCellRenderer() {
        return new ListCellRendererBasic(tpromo.getRenderStringBasic(new int[]{1}));
    }
    
    /**
     *
     * @return
     */
    public EditorRecord getEditor() {
        return jeditor;
    }

    /**
     *
     * @return
     */
    public String getTitle() {
        return AppLocal.getIntString("Menu.Promo");
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        jeditor.activate(); // primero activo el editor 
        super.activate();   // segundo activo el padre        
    }     
}