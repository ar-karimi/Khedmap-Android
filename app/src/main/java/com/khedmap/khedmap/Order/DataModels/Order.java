package com.khedmap.khedmap.Order.DataModels;


public class Order {

    private int id;
    private String identification;
    private String subcategory;
    private String create;
    private String serviceTime;
    private String address;
    private String numberOfSuggestions;
    private String status;
    private int statusId;



    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }


    public String getIdentification() {
        return identification;
    }

    public void setIdentification(String identification) {
        this.identification = identification;
    }

    public String getSubcategory() {
        return subcategory;
    }

    public void setSubcategory(String subcategory) {
        this.subcategory = subcategory;
    }

    public String getCreate() {
        return create;
    }

    public void setCreate(String create) {
        this.create = create;
    }

    public String getServiceTime() {
        return serviceTime;
    }

    public void setServiceTime(String serviceTime) {
        this.serviceTime = serviceTime;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNumberOfSuggestions() {
        return numberOfSuggestions;
    }

    public void setNumberOfSuggestions(String numberOfSuggestions) {
        this.numberOfSuggestions = numberOfSuggestions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getStatusId() {
        return statusId;
    }

    public void setStatusId(int statusId) {
        this.statusId = statusId;
    }
}
