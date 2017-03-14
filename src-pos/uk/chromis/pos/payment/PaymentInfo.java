//    Chromis POS  - The New Face of Open Source POS
//    Copyright (C) (c) 2015-2016
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

package uk.chromis.pos.payment;

import uk.chromis.format.Formats;

/**
 *
 *   
 */
public abstract class PaymentInfo {
    
    /**
     *
     * @return
     */
    public abstract String getName();

    /**
     *
     * @return
     */
    public abstract double getTotal();

    /**
     *
     * @return
     */
    public abstract PaymentInfo copyPayment();

    /**
     *
     * @return
     */
    public abstract String getTransactionID();

    /**
     *
     * @return
     */
    public abstract double getPaid();

    /**
     *
     * @return
     */
    public abstract double getChange();

    /**
     *
     * @return
     */
    public abstract double getDebtDue();
    
    /**
     *
     */
    public abstract void setDebtDue( double debt );
    
    /**
     *
     * @return
     */
    public abstract double getTendered();

    /**
     *
     * @return
     */
    public abstract String getCardName();
    
    /**
     *
     * @return
     */
    public String printTotal() {
        return Formats.CURRENCY.formatValue(getTotal());
    }
}
