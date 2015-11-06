package com.example.yunita.tradiogc;

public class WebServer {
    private static final String RESOURCE_URL = "http://cmput301.softwareprocess.es:8080/cmput301f15t09/user/";
    private static final String SEARCH_URL = "http://cmput301.softwareprocess.es:8080/cmput301f15t09/user/_search";
    private static final String INVENTORY_URL = "http://cmput301.softwareprocess.es:8080/cmput301f15t09/inventory/";

    public WebServer() {

    }

    public String getResourceUrl() {
        return RESOURCE_URL;
    }

    public String getInventoryUrl() {
        return INVENTORY_URL;
    }

    public String getSearchUrl() {
        return SEARCH_URL;
    }

}
