//    Chromis POS  - The New Face of Open Source POS
//    Copyright (C) 2008-2014 
//    http://www.chromis.co.uk - additional amends by Walter Wojick for Blue Pay
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

import uk.chromis.pos.forms.AppProperties;

/**
 *
 *   
 */
public class PaymentGatewayFac {
    
    /** Creates a new instance of PaymentGatewayFac */
    private PaymentGatewayFac() {
    }
    
    /**
     *
     * @param props
     * @return
     */
    public static PaymentGateway getPaymentGateway(AppProperties props) {
        
        String sReader = props.getProperty("payment.gateway");
// JG 16 May 12 use switch
        switch (sReader) {
            case "external":
                return new PaymentGatewayExt();
            case "PayPoint / SecPay":
                return new PaymentGatewayPayPoint(props);
            case "AuthorizeNet":
                return new PaymentGatewayAuthorizeNet(props);
            case "BluePay AUTH.NET EMU":
                return new PaymentGatewayBluePayAUTHNETEMU(props);
            case "BluePay 2.0 POST":
                return new PaymentGatewayBluePay20POST(props);
            case "La Caixa (Spain)":
                return new PaymentGatewayCaixa(props);
            case "Planetauthorize":
                return new PaymentGatewayPlanetauthorize(props);
            case "First Data / LinkPoint / YourPay":
                return new PaymentGatewayLinkPoint(props);
            case "PaymentsGateway.net":
                return new PaymentGatewayPGNET(props);
            default:
                return null;
        }
    }      
}
