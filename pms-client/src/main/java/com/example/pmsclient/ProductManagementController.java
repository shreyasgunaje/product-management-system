package com.example.pmsclient;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TextField;
import javafx.scene.control.TableView;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.List;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

public class ProductManagementController implements Initializable {

    @FXML
    private TableColumn<List<Object>, Object> descriptionColumn;
    @FXML
    private TableColumn<List<Object>, Object> pIdColumn;
    @FXML
    private TableColumn<List<Object>, Object> pNameColumn;
    @FXML
    private TableColumn<List<Object>, Object> priceColumn;
    @FXML
    private TableView<List<Object>> productsTableView;
    @FXML
    private TableColumn<List<Object>, Object> actionsColumn;
    @FXML
    private TableColumn<List<Object>, Object> supplierColumn;
    @FXML
    private TextField add_productDescription;
    @FXML
    private TextField add_productName;
    @FXML
    private TextField add_price;
    @FXML
    private ComboBox<String> add_supplier;
    @FXML
    private Button searchProductBtn;
    @FXML
    private TextField searchProductText;
    @FXML
    private Button addProductBtn;

    private ClientApp clientApp;

    public ProductManagementController(){
        clientApp = new ClientApp();
    }


    @FXML
    protected void handleProductAddButtonAction(){
        String pname=add_productName.getText();
        String description=add_productDescription.getText();
        String price1= add_price.getText();
        String supplier=add_supplier.getSelectionModel().getSelectedItem();

        // Check if any of the fields are empty
        if (!pname.isEmpty() && !description.isEmpty() && !price1.isEmpty() && supplier != null && !supplier.isEmpty()) {
            try {
                // Convert the price text to an integer
                int price = Integer.parseInt(price1);

                // Send product information to the server
                boolean isProductAdded = clientApp.addProductsToServer(pname, description, price, supplier);

                // Handle the product addition result as needed
//                if (isProductAdded) {
//                    System.out.println("Product added successfully");
//                    // Add code to update the UI or perform additional actions after adding the product.
//                } else {
//                    System.out.println("Failed to add the product");
//                    // Handle the case where adding the product was not successful.
//                }
                showInfoWindow(isProductAdded, "Product added successfully", "Failed to add the product");

                if (isProductAdded) {

                    reloadProductsTableView();
                    // Clear the fields
                    add_productName.clear();
                    add_productDescription.clear();
                    add_price.clear();
                    add_supplier.getSelectionModel().clearSelection();  // Clear the selection in the ComboBox
                }


            } catch (NumberFormatException e) {
//                System.out.println("Invalid price format");
                showInfoWindow(false, "Invalid price format", "Please enter a valid integer for the price");
                // Handle the case where the price is not a valid integer.
            }
        } else {
//            System.out.println("Please fill in all fields");
            showInfoWindow(false, "Incomplete Fields", "Please fill in all fields");
            // Handle the case where one or more fields are empty.
        }
    }

    private void showInfoWindow(boolean success, String successMessage, String errorMessage) {
        Alert alert = new Alert(success ? AlertType.INFORMATION : AlertType.ERROR);
        alert.setTitle(success ? "Success" : "Error");
        alert.setHeaderText(null);

        if (success) {
            alert.setContentText(successMessage);
        } else {
            alert.setContentText(errorMessage);
        }

        alert.showAndWait();
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {

        // get the suppliers from suppliers table and populate the combobox
        List<String> supplierNames = clientApp.receiveSupplierNames();
        add_supplier.getItems().addAll(supplierNames);

        populateProductsTableView();
    }

    private void populateProductsTableView() {

        pIdColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(0)));
        pNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(1)));
        descriptionColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(2)));
        priceColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(3)));
        supplierColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().get(4)));

//        actionsColumn.setCellFactory(createCellFactory());

        // Get your data (List<List<Object>>) and convert it to ObservableList<List<Object>>
        List<List<Object>> products = clientApp.getProductsFromServer();
        ObservableList<List<Object>> productsData = FXCollections.observableArrayList(products);

        // Set the data to the TableView
        productsTableView.getItems().addAll(productsData);
    }

    // You can call this method from elsewhere in your application
     public void reloadProductsTableView() {
        List<List<Object>> updatedProducts = clientApp.getProductsFromServer();
        ObservableList<List<Object>> updatedProductsData = FXCollections.observableArrayList(updatedProducts);
        productsTableView.setItems(updatedProductsData);
    }
}
