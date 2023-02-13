public class TestBean {
    public String Remark;
    public String OrderNo;
    public String ShopOrderId;
    public String purchaseOrderNo;

    public TestBean() {
    }

    public TestBean(String remark, String orderNo, String shopOrderId, String purchaseOrderNo) {
        Remark = remark;
        OrderNo = orderNo;
        ShopOrderId = shopOrderId;
        this.purchaseOrderNo = purchaseOrderNo;
    }

    public String getRemark() {
        return Remark;
    }

    public void setRemark(String remark) {
        Remark = remark;
    }

    public String getOrderNo() {
        return OrderNo;
    }

    public void setOrderNo(String orderNo) {
        OrderNo = orderNo;
    }

    public String getShopOrderId() {
        return ShopOrderId;
    }

    public void setShopOrderId(String shopOrderId) {
        ShopOrderId = shopOrderId;
    }

    public String getPurchaseOrderNo() {
        return purchaseOrderNo;
    }

    public void setPurchaseOrderNo(String purchaseOrderNo) {
        this.purchaseOrderNo = purchaseOrderNo;
    }
}
