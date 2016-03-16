//    Chromis POS  - The New Face of Open Source POS
//    Copyright (c) (c) 2015-2016
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

package uk.chromis.pos.inventory;

import uk.chromis.basic.BasicException;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.model.*;
import uk.chromis.data.user.EditorRecord;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.panels.RecipeFilter;
import uk.chromis.pos.panels.JPanelTable2;
import uk.chromis.pos.ticket.ProductInfoExt;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class RecipePanel extends JPanelTable2 {

    private RecipeEditor editor;
    private RecipeFilter filter;

    @Override
    protected void init() {  
        
        filter = new RecipeFilter();
        filter.init(app);
        filter.addActionListener(new ReloadActionListener());
        
        row = new Row(
                new Field("ID", Datas.STRING, Formats.STRING),
                new Field("PRODUCT", Datas.STRING, Formats.STRING),
                new Field("PRODUCT_KIT", Datas.STRING, Formats.STRING),
                new Field("QUANTITY", Datas.DOUBLE, Formats.DOUBLE),
                new Field(AppLocal.getIntString("label.prodref"), Datas.STRING, Formats.STRING, true, true, true),
                new Field(AppLocal.getIntString("label.prodbarcode"), Datas.STRING, Formats.STRING, false, true, true),
                new Field(AppLocal.getIntString("label.prodname"), Datas.STRING, Formats.STRING, true, true, true)
        );        
        Table table = new Table(
                "PRODUCTS_KIT",
                new PrimaryKey("ID"),
                new Column("PRODUCT"),
                new Column("PRODUCT_KIT"),
                new Column("QUANTITY"));
         
        lpr = row.getListProvider(app.getSession(), 
                "SELECT KIT.ID, KIT.PRODUCT, KIT.PRODUCT_KIT, KIT.QUANTITY, P.REFERENCE, P.CODE, P.NAME " +
                "FROM PRODUCTS_KIT KIT, PRODUCTS P " +
                "WHERE KIT.PRODUCT_KIT = P.ID AND KIT.PRODUCT = ?", filter);
        spr = row.getSaveProvider(app.getSession(), table);              
        
        editor = new RecipeEditor(app, dirty);
    }

    @Override
    public void activate() throws BasicException {
        filter.activate();
        
        //super.activate();
        startNavigation();
        reload(filter);
    }

    @Override
    public Component getFilter(){
        return filter.getComponent();
    }
    
    @Override
    public EditorRecord getEditor() {
        return editor;
    }  
    
    @Override
    public String getTitle() {
        return AppLocal.getIntString("Menu.Recipe");
    } 
    
    private void reload(RecipeFilter filter) throws BasicException {
        ProductInfoExt prod = filter.getProductInfoExt();
        editor.setInsertProduct(prod); // must be set before load
        bd.setEditable(prod != null);
        bd.actionLoad();
    }
            
    private class ReloadActionListener implements ActionListener {
        @Override
        public void actionPerformed(ActionEvent e) {
            try {
                reload((RecipeFilter) e.getSource());
            } catch (BasicException w) {
            }
        }
    }
}
