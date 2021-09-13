package com.paloit.ecom.entity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@Entity
@Table(name = "orderDetails")
public class OrderDetail {

  @Id
  @Column(name = "order_id")
  @NotNull
  private long orderId;

  @Column(name = "nric_id")
  @NotNull
  private String nricId;

  @Column(name = "region")
  @NotNull
  private String region;

  @Column(name = "country")
  @NotNull
  private String country;

  @Column(name = "item_type")
  @NotNull
  private String itemType;

  @Column(name = "sales_channel")
  @NotNull
  private String salesChannel;

  @Column(name = "order_priority")
  @NotNull
  private char orderPriority;

  @Column(name = "order_date")
  @NotNull
  private LocalDate orderDate;

  @Column(name = "ship_date")
  @NotNull
  private LocalDate shipDate;

  @Column(name = "unit_sold")
  @NotNull
  private int unitSold;

  @Column(name = "unit_price")
  @NotNull
  private double unitPrice;

  @Column(name = "unit_cost")
  @NotNull
  private double unitCost;

  @Column(name = "total_revenue")
  @NotNull
  private float totalRevenue;

  @Column(name = "total_cost")
  @NotNull
  private float totalCost;

  @Column(name = "total_profit")
  @NotNull
  private float totalProfit;

  public OrderDetail(long orderId, String nricId, String region, String country, String itemType, String salesChannel, char orderPriority, LocalDate orderDate, LocalDate shipDate, int unitSold, double unitPrice, double unitCost, float totalRevenue, float totalCost, float totalProfit) {
    this.orderId = orderId;
    this.nricId = nricId;
    this.region = region;
    this.country = country;
    this.itemType = itemType;
    this.salesChannel = salesChannel;
    this.orderPriority = orderPriority;
    this.orderDate = orderDate;
    this.shipDate = shipDate;
    this.unitSold = unitSold;
    this.unitPrice = unitPrice;
    this.unitCost = unitCost;
    this.totalRevenue = totalRevenue;
    this.totalCost = totalCost;
    this.totalProfit = totalProfit;
  }

  public OrderDetail() {
  }

  public long getOrderId() {
    return orderId;
  }

  public void setOrderId(long orderId) {
    this.orderId = orderId;
  }

  public String getNricId() {
    return nricId;
  }

  public void setNricId(String nricId) {
    this.nricId = nricId;
  }

  public String getRegion() {
    return region;
  }

  public void setRegion(String region) {
    this.region = region;
  }

  public String getCountry() {
    return country;
  }

  public void setCountry(String country) {
    this.country = country;
  }

  public String getItemType() {
    return itemType;
  }

  public void setItemType(String itemType) {
    this.itemType = itemType;
  }

  public String getSalesChannel() {
    return salesChannel;
  }

  public void setSalesChannel(String salesChannel) {
    this.salesChannel = salesChannel;
  }

  public char getOrderPriority() {
    return orderPriority;
  }

  public void setOrderPriority(char orderPriority) {
    this.orderPriority = orderPriority;
  }

  public LocalDate getOrderDate() {
    return orderDate;
  }

  public void setOrderDate(LocalDate orderDate) {
    this.orderDate = orderDate;
  }

  public LocalDate getShipDate() {
    return shipDate;
  }

  public void setShipDate(LocalDate shipDate) {
    this.shipDate = shipDate;
  }

  public int getUnitSold() {
    return unitSold;
  }

  public void setUnitSold(int unitSold) {
    this.unitSold = unitSold;
  }

  public double getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(double unitPrice) {
    this.unitPrice = unitPrice;
  }

  public double getUnitCost() {
    return unitCost;
  }

  public void setUnitCost(double unitCost) {
    this.unitCost = unitCost;
  }

  public float getTotalRevenue() {
    return totalRevenue;
  }

  public void setTotalRevenue(float totalRevenue) {
    this.totalRevenue = totalRevenue;
  }

  public float getTotalCost() {
    return totalCost;
  }

  public void setTotalCost(float totalCost) {
    this.totalCost = totalCost;
  }

  public float getTotalProfit() {
    return totalProfit;
  }

  public void setTotalProfit(float totalProfit) {
    this.totalProfit = totalProfit;
  }

  @Override
  public String toString() {
    return "OrderDetails [" +
            "orderId=" + orderId +
            ", nricId='" + nricId  +
            ", region='" + region  +
            ", country='" + country  +
            ", itemType='" + itemType  +
            ", salesChannel='" + salesChannel +
            ", orderPriority=" + orderPriority +
            ", orderDate=" + orderDate +
            ", shipDate=" + shipDate +
            ", unitSold=" + unitSold +
            ", unitPrice=" + unitPrice +
            ", unitCost=" + unitCost +
            ", totalRevenue=" + totalRevenue +
            ", totalCost=" + totalCost +
            ", totalProfit=" + totalProfit +
            "]";
  }
}
