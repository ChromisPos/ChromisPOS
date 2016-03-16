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
//
package uk.chromis.pos.ticket;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.gui.ListQBFModelNumber;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;
import uk.chromis.pos.reports.ReportEditorCreator;

/**
 *
 * @author John
 */
public class ProductFilterReport extends javax.swing.JPanel implements ReportEditorCreator {

    private SentenceList m_sentcat;
    private ComboBoxValModel m_CategoryModel;
    private ComboBoxValModel m_ProductNameModel;
    private final JPanel panel;
    private final JLabel jLabel1;
    private final JLabel jLabel2;
    private final JLabel jLabel3;
    private final JLabel jLabel4;
    private final JLabel jLabel5;
    private final JTextField m_jBarcode = new JTextField();
    private final JComboBox m_jCategory = new JComboBox();
    private final JComboBox m_jCboName = new JComboBox();
    ;
    private final JComboBox m_jCboPriceBuy = new JComboBox();
    private final JComboBox m_jCboPriceSell = new JComboBox();
    private final JTextField m_jName = new JTextField();
    private final JTextField m_jPriceBuy = new JTextField();
    private final JTextField m_jPriceSell = new JTextField();

    /**
     * Creates new form JQBFProduct
     */
    public ProductFilterReport() {

        Font font = new Font("Arial", 0, 12);
        setFont(font);
        panel = new JPanel(new MigLayout());

        GroupLayout layout = new GroupLayout(this);
        setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addGroup(layout.createSequentialGroup()
                        .addComponent(panel, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                        .addContainerGap(319, Short.MAX_VALUE)
                ));

        layout.setVerticalGroup(
                layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                .addComponent(panel, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jLabel1 = new JLabel(AppLocal.getIntString("label.prodcategory"));
        jLabel1.setFont(font);

        jLabel2 = new JLabel(AppLocal.getIntString("label.prodname"));
        jLabel2.setFont(font);

        jLabel3 = new JLabel(AppLocal.getIntString("label.prodpricesellexcludingtax"));
        jLabel3.setFont(font);

        jLabel4 = new JLabel(AppLocal.getIntString("label.prodpricebuy"));
        jLabel4.setFont(font);

        jLabel5 = new JLabel(AppLocal.getIntString("label.prodbarcode"));
        jLabel5.setFont(font);

        m_jBarcode.setFont(font);
        m_jBarcode.setPreferredSize(new Dimension(200, 25));
        m_jCategory.setFont(font);
        m_jCboName.setFont(font);
        m_jName.setFont(font);
        m_jName.setPreferredSize(new Dimension(200, 25));

        m_jPriceBuy.setFont(font);
        m_jPriceBuy.setPreferredSize(new Dimension(60, 25));

        m_jCboPriceBuy.setFont(font);
        m_jCboPriceSell.setFont(font);

        m_jPriceSell.setFont(font);
        m_jPriceSell.setPreferredSize(new Dimension(60, 25));

        jLabel2.setFont(font);
        jLabel2.setText(AppLocal.getIntString("label.prodname"));

        panel.add(jLabel1, "w 80");
        panel.add(m_jCategory, "w 220, gapright 5");
        panel.add(jLabel4, "w 90");

        panel.add(m_jCboPriceBuy, "w 120");
        panel.add(m_jPriceBuy, "w 50, gapright 10");

        panel.add(jLabel5, "w 80");
        panel.add(m_jBarcode, "w 220, wrap");

        panel.add(jLabel2, "w 80");
        panel.add(m_jCboName, "w 220");

        panel.add(jLabel3, "w 90");
        panel.add(m_jCboPriceSell, "w 120");
        panel.add(m_jPriceSell, "w 50, gapright 10, wrap");

        panel.add(new JLabel(), "w 80");
        panel.add(m_jName, "w 220, gapright 5");

    }

    /**
     *
     * @param app
     */
    @Override
    public void init(AppView app) {

        DataLogicSales dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");

        // El modelo de categorias
        m_sentcat = dlSales.getCategoriesList();
        m_CategoryModel = new ComboBoxValModel();

        // m_ProductNameModel = new ComboBoxValModel();
        //  m_ProductNameModel.add(null);
        //  m_ProductNameModel.add(ListQBFModelNumber.getMandatoryString());
        m_jCboName.setModel(ListQBFModelNumber.getNonMandatoryProduct());
        m_jCboPriceBuy.setModel(ListQBFModelNumber.getNonMandatoryPrice());
        m_jCboPriceSell.setModel(ListQBFModelNumber.getNonMandatoryPrice());

    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {

        List catlist = m_sentcat.list();
        catlist.add(0, null);
        m_CategoryModel = new ComboBoxValModel(catlist);
        m_jCategory.setModel(m_CategoryModel);
    }

    /**
     *
     * @return
     */
    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(
                new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.DOUBLE, Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.STRING});
    }

    /**
     *
     * @return
     */
    @Override
    public Component getComponent() {
        return this;
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {

        if (m_jBarcode.getText() == null || m_jBarcode.getText().equals("")) {
            // Filtro por formulario
            return new Object[]{
                m_jCboName.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_jCboName.getSelectedItem(), m_jName.getText(),
                m_jCboPriceBuy.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_jCboPriceBuy.getSelectedItem(), Formats.CURRENCY.parseValue(m_jPriceBuy.getText()),
                m_jCboPriceSell.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : m_jCboPriceSell.getSelectedItem(), Formats.CURRENCY.parseValue(m_jPriceSell.getText()),
                m_CategoryModel.getSelectedKey() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_CategoryModel.getSelectedKey(),
                QBFCompareEnum.COMP_NONE, null
            };
        } else {
            // Filtro por codigo de barras.
            return new Object[]{
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_NONE, null,
                QBFCompareEnum.COMP_RE, "%" + m_jBarcode.getText() + "%"
            };
        }
    }
}
