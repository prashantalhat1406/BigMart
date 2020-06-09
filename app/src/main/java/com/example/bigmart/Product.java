package com.example.bigmart;

public class Product {
    String ID;
    String Name;
    Integer Qty;
    Integer QtyNos;
    Double MRP;
    Integer Discount;
    String Info;
    String Category;
    String SubCategory;
    String Type;
    Integer HSN;
    Integer GST;
    Integer MinStock;
    Integer MaxStock;
    String Name2;
    String DisplayName;

    public Product() {
    }

    public String getName2() {         return Name2;     }

    public void setName2(String name2) {         Name2 = name2;     }

    public String getDisplayName() {         return DisplayName;     }

    public void setDisplayName(String displayName) {         DisplayName = displayName;     }

    public Integer getHSN() {        return HSN;     }

    public void setHSN(Integer HSN) {        this.HSN = HSN;    }

    public Integer getGST() {        return GST;     }

    public void setGST(Integer GST) {         this.GST = GST;     }

    public Integer getMinStock() {         return MinStock;     }

    public void setMinStock(Integer minStock) {         MinStock = minStock;     }

    public Integer getMaxStock() {         return MaxStock;     }

    public void setMaxStock(Integer maxStock) {         MaxStock = maxStock;     }

    public String getSubCategory() {
        return SubCategory;
    }

    public String getType() {
        return Type;
    }

    public void setType(String type) {
        this.Type = type;
    }

    public Integer getQtyNos() {
        return QtyNos;
    }

    public void setQtyNos(Integer qtyNos) {
        QtyNos = qtyNos;
    }

    public void setSubCategory(String subCategory) {
        SubCategory = subCategory;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public Integer getQty() {
        return Qty;
    }

    public void setQty(Integer qty) {
        Qty = qty;
    }

    public Double getMRP() {
        return MRP;
    }

    public void setMRP(Double MRP) {
        this.MRP = MRP;
    }

    public Integer getDiscount() {
        return Discount;
    }

    public void setDiscount(Integer discount) {
        Discount = discount;
    }

    public String getInfo() {
        return Info;
    }

    public void setInfo(String info) {
        Info = info;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
