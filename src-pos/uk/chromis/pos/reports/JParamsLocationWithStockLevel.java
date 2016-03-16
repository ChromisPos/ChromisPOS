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
package uk.chromis.pos.reports;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.GroupLayout;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import net.miginfocom.swing.MigLayout;
import uk.chromis.basic.BasicException;
import uk.chromis.data.gui.ComboBoxValModel;
import uk.chromis.data.loader.Datas;
import uk.chromis.data.loader.QBFCompareEnum;
import uk.chromis.data.loader.SentenceList;
import uk.chromis.data.loader.SerializerWrite;
import uk.chromis.data.loader.SerializerWriteBasic;
import uk.chromis.format.Formats;
import uk.chromis.pos.forms.AppLocal;
import uk.chromis.pos.forms.AppView;
import uk.chromis.pos.forms.DataLogicSales;

public class JParamsLocationWithStockLevel extends javax.swing.JPanel implements ReportEditorCreator {

    private final JPanel panel;
    private final JLabel jLabel1;
    private final JLabel jLabel2;

    private final JComboBox m_jLocation = new JComboBox();
    private final JComboBox m_jstock = new JComboBox();

    private final JTextField jStockLevel = new JTextField();

    private SentenceList m_sentlocations;
    private ComboBoxValModel m_LocationsModel;
    private ComboBoxValModel m_StockLevelModel;

    /**
     * Creates new form JParamsLocation
     */
    public JParamsLocationWithStockLevel() {

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

        jLabel1 = new JLabel(AppLocal.getIntString("label.warehouse"));
        jLabel1.setFont(font);

        jLabel2 = new JLabel(AppLocal.getIntString("label.stocklevel"));
        jLabel2.setFont(font);

        m_jstock.setFont(font);
        m_jLocation.setFont(font);

        jStockLevel.setFont(font);
        jStockLevel.setPreferredSize(new Dimension(60, 25));

        panel.add(jLabel1, "w 80");
        panel.add(m_jLocation, "w 220, gapright 5");
        panel.add(jLabel2, "w 90");

        panel.add(m_jstock, "w 120");
        panel.add(jStockLevel, "w 50, gapright 10");

    }

    /**
     *
     * @param app
     */
    @Override
    public void init(AppView app) {

        DataLogicSales dlSales = (DataLogicSales) app.getBean("uk.chromis.pos.forms.DataLogicSales");

        // El modelo de locales
        m_sentlocations = dlSales.getLocationsList();
        m_LocationsModel = new ComboBoxValModel();
    }

    /**
     *
     * @throws BasicException
     */
    @Override
    public void activate() throws BasicException {
        List a = m_sentlocations.list();
        a.add(0, null);
        m_LocationsModel = new ComboBoxValModel(a);
        m_LocationsModel.setSelectedFirst();
        m_jLocation.setModel(m_LocationsModel);

        m_StockLevelModel = new ComboBoxValModel();
        m_StockLevelModel.add(null);
        m_StockLevelModel.add(QBFCompareEnum.COMP_EQUALS);
        m_StockLevelModel.add(QBFCompareEnum.COMP_GREATER);
        m_StockLevelModel.add(QBFCompareEnum.COMP_LESS);
        m_jstock.setModel(m_StockLevelModel);
    }

    /**
     *
     * @return
     */
    @Override
    public SerializerWrite getSerializerWrite() {
        return new SerializerWriteBasic(new Datas[]{Datas.OBJECT, Datas.STRING, Datas.OBJECT, Datas.INT});
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
     * @param a
     */
    protected void addFirst(List a) {
        // do nothing
    }

    /**
     *
     * @param l
     */
    public void addActionListener(ActionListener l) {
        m_jLocation.addActionListener(l);
    }

    /**
     *
     * @param l
     */
    public void removeActionListener(ActionListener l) {
        m_jLocation.removeActionListener(l);
    }

    /**
     *
     * @return @throws BasicException
     */
    @Override
    public Object createValue() throws BasicException {

        if (m_StockLevelModel.getSelectedItem() != null) {
            return new Object[]{
                m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_LocationsModel.getSelectedKey(),
                m_StockLevelModel.getSelectedItem(), Formats.INT.parseValue(jStockLevel.getText())
            };
        } else {
            return new Object[]{
                m_LocationsModel.getSelectedKey() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_LocationsModel.getSelectedKey(),
                m_StockLevelModel.getSelectedItem() == null ? QBFCompareEnum.COMP_NONE : QBFCompareEnum.COMP_EQUALS, m_StockLevelModel.getSelectedKey()
            };
        }

    }
}
