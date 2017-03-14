/*
 * John L changes
 */
package uk.chromis.pos.forms;

import java.util.HashMap;

/**
 *
 * @author John L July 2013
 */
public class Payments {
    private Double amount;
    private Double tendered;
    private HashMap paymentPaid;
    private HashMap paymentTendered;
    private HashMap changeGiven;
    private HashMap debtDue;
    private HashMap rtnMessage;
    private String name;

    /**
     *
     */
    public Payments() {
    paymentPaid =  new HashMap();
    paymentTendered =  new HashMap();
    rtnMessage = new HashMap();
    changeGiven = new HashMap();
    debtDue = new HashMap();
    }

    /**
     *
     * @param pName
     * @param pAmountPaid
     * @param pTendered
     * @param pChangeGiven
     * @param pDebtDue
     * @param rtnMsg
     */
    public void addPayment (String pName, Double pAmountPaid, Double pTendered, Double pChangeGiven, Double pDebtDue, String rtnMsg){
        if (paymentPaid.containsKey(pName)){
            paymentPaid.put(pName,Double.parseDouble(paymentPaid.get(pName).toString()) + pAmountPaid);
            paymentTendered.put(pName,Double.parseDouble(paymentTendered.get(pName).toString()) + pTendered); 
            changeGiven.put(pName,Double.parseDouble(changeGiven.get(pName).toString()) + pChangeGiven); 
            debtDue.put(pName,Double.parseDouble(changeGiven.get(pName).toString()) + pDebtDue); 
            rtnMessage.put(pName, rtnMsg);
        }else {    
            paymentPaid.put(pName, pAmountPaid);
            paymentTendered.put(pName,pTendered);
            changeGiven.put(pName,pChangeGiven);
            debtDue.put(pName,pDebtDue);
            rtnMessage.put(pName, rtnMsg);
        }        
}

    /**
     *
     * @param pName
     * @return
     */
    public Double getTendered (String pName){
    return(Double.parseDouble(paymentTendered.get(pName).toString()));
}


    /**
     *
     * @param pName
     * @return
     */
    public Double getChange (String pName){
    return(Double.parseDouble(changeGiven.get(pName).toString()));
}
    
    /**
     *
     * @param pName
     * @return
     */
    public Double getDebtDue (String pName){
    return(Double.parseDouble(debtDue.get(pName).toString()));
}

    /**
     *
     * @param pName
     * @return
     */
    public Double getPaidAmount (String pName){
    return(Double.parseDouble(paymentPaid.get(pName).toString()));
}

    /**
     *
     * @return
     */
    public Integer getSize(){
    return (paymentPaid.size());
}

    /**
     *
     * @param pName
     * @return
     */
    public String getRtnMessage(String pName){
    return (rtnMessage.get(pName).toString());
}

    /**
     *
     * @return
     */
    public String getFirstElement(){
    String rtnKey= paymentPaid.keySet().iterator().next().toString();
    return(rtnKey);
}

    /**
     *
     * @param pName
     */
    public void removeFirst (String pName){
   paymentPaid.remove(pName);
   paymentTendered.remove(pName);
   changeGiven.remove(pName);
   debtDue.remove(pName);
   rtnMessage.remove(pName);
}

}