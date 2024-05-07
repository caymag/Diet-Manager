/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.mycompany.dietmanager;

import java.awt.*;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 *
 * @author maguire_937243
 */
public class MainWindow extends javax.swing.JFrame {

    private static final String API_KEY = "FlrlOGWPW8odRWz0DnMb3yrXRRNWMmwWoBURK3IM";
    double totalCalories;
    double totalProtein;
    double totalCarbs;
    double totalFats;
    /**
     * Creates new form MainWindow
     */
    public MainWindow() {
        initComponents();
        
        
    }
    
  public String searchFoodItems(String url) {
      
        StringBuilder responseBuilder = new StringBuilder();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        DefaultTableModel model = (DefaultTableModel) jTable_search.getModel();

        try {
            HttpGet request = new HttpGet(url);
            HttpResponse response = httpClient.execute(request);

            HttpEntity entity = response.getEntity();
            String responseBody = EntityUtils.toString(entity);
            //System.out.println(responseBody);
            JSONObject jsonResponse = new JSONObject(responseBody);
            JSONArray foodsArray = jsonResponse.getJSONArray("foods");

            for (int i = 0; i != foodsArray.length(); i++) {
                JSONObject foodObject = foodsArray.getJSONObject(i);
                String foodName = foodObject.getString("description");
                //System.out.println(foodName);
                //double nutrientServingSize = foodObject.getDouble("servingSize");
                String brandName;
                try {
                    brandName = foodObject.getString("brandName");
                } catch (JSONException e) {
                    brandName = "UNKNOWN";
                }
                
                String brandOwner;
                try { 
                    brandOwner = foodObject.getString("BrandOwner");
                } catch (JSONException e) {
                    brandOwner = "UNKNOWN";
                }
                
                   
                JSONArray nutrientsArray = foodObject.getJSONArray("foodNutrients");
                //System.out.println(nutrientsArray);
                double protein = 0;
                double carbs = 0;
                double fats = 0;
                double calories = 0;

                for (int j = 0; j != nutrientsArray.length(); j++) {
                    JSONObject nutrientObject = nutrientsArray.getJSONObject(j);
                    String nutrientName = nutrientObject.getString("nutrientName");
                    double nutrientAmount = nutrientObject.getDouble("value");
                    String nutrientUnit = nutrientObject.getString("unitName");
                    


                    switch (nutrientName)
                    {
                        case "Protein":
                            protein += nutrientAmount;// / 100) * nutrientServingSize;
                            break;
                        case "Carbohydrate, by difference":
                            carbs += nutrientAmount;// / 100) * nutrientServingSize;
                            break;
                        case "Total lipid (fat)":
                            fats += nutrientAmount;// / 100) * nutrientServingSize;
                            break;
                        case "Energy":
                            calories += nutrientAmount;// / 100) * nutrientServingSize;
                            break;
                        default:
                            break;
                    }
                }
                
                
                    model.addRow(new Object[]{});
                    model.setValueAt(foodName, i , 0);
                    model.setValueAt(brandName, i, 1 );
                    model.setValueAt(calories, i, 2);
                    model.setValueAt(protein, i, 3);
                    model.setValueAt(carbs, i, 4);
                    model.setValueAt(fats, i, 5);

            }
        } catch (Exception e) {
            e.printStackTrace();
            responseBuilder.append("Error fetching data from USDA API.");
            
            
        } finally {
            try {
                httpClient.close();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        return responseBuilder.toString();
    }
  
    public void FoodSelectionDialog() {
        String[] options = {"Breakfast", "Lunch", "Dinner", "Snack"};
        int choice = showOptionDialog("Select Meal", "Where would you like to add the selected food?", options);
        if (choice != JOptionPane.CLOSED_OPTION) {
            String selectedMeal = options[choice];
            addFood(selectedMeal);
        } else {
            System.out.println("User canceled the selection.");
        }
    }

    public static int showOptionDialog(String title, String message, String[] options) {
        return JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
    }

public void addFood(String selectedMeal) {
    DefaultTableModel model = (DefaultTableModel) jTable_search.getModel();
    DefaultTableModel targetModel;

   
    switch (selectedMeal) {
        case "Breakfast":
            targetModel = (DefaultTableModel) jTable_breakfast.getModel();
            break;
        case "Lunch":
            targetModel = (DefaultTableModel) jTable_lunch.getModel();
            break;
        case "Dinner":
            targetModel = (DefaultTableModel) jTable_dinner.getModel();
            break;
        case "Snack":
            targetModel = (DefaultTableModel) jTable_snack.getModel();
            break;
        default:
            
            return;
    }

    int selectedRow = jTable_search.getSelectedRow();
    if (selectedRow != -1) {
        String selectedFoodName = (String) model.getValueAt(selectedRow, 0);
        String selectedBrandName = (String) model.getValueAt(selectedRow, 1);
        double selectedCalories = (double) model.getValueAt(selectedRow, 2);
        totalCalories += selectedCalories;
        jLabel_totalCalories.setText(totalCalories + " calories for today");
        double selectedProtein = (double) model.getValueAt(selectedRow, 3);
        totalProtein += selectedProtein;
        jProgressBar_proteinToGo.setValue((int) totalProtein);
        jTextField_proteinToGo.setText(totalProtein + " protein for today");
        double selectedCarbs = (double) model.getValueAt(selectedRow, 4);
        totalCarbs += selectedCarbs;
        jProgressBar_carbsToGo.setValue((int) totalCarbs);
        jTextField_carbsToGo.setText(totalCarbs + " carbs for today");
        double selectedFats = (double) model.getValueAt(selectedRow, 5);
        totalFats += selectedFats;
        jProgressBar_fatsToGo.setValue((int) totalFats);
        jTextField_fatsToGo.setText(totalFats + " fats for today");

     
        targetModel.addRow(new Object[]{
                selectedFoodName,
                selectedBrandName,
                selectedCalories,
                selectedProtein,
                selectedCarbs,
                selectedFats
        });
    }
}
    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jTabbedPane_GUI = new javax.swing.JTabbedPane();
        jPanel_dashboard = new javax.swing.JPanel();
        jProgressBar_carbsToGo = new javax.swing.JProgressBar();
        jTextField_proteinToGo = new javax.swing.JTextField();
        jProgressBar_proteinToGo = new javax.swing.JProgressBar();
        jTextField_carbsToGo = new javax.swing.JTextField();
        jTextField_fatsToGo = new javax.swing.JTextField();
        jProgressBar_fatsToGo = new javax.swing.JProgressBar();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTable1 = new javax.swing.JTable();
        jLabel_totalCalories = new javax.swing.JLabel();
        jScrollPane_search = new javax.swing.JScrollPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane3 = new javax.swing.JScrollPane();
        jTable_search = new javax.swing.JTable();
        jButton_search = new javax.swing.JButton();
        jTextField_search = new javax.swing.JTextField();
        jButton_selectedFood = new javax.swing.JButton();
        jScrollPane_log = new javax.swing.JScrollPane();
        jPanel2 = new javax.swing.JPanel();
        jPanel_breakfastLog = new javax.swing.JPanel();
        jScrollPane2 = new javax.swing.JScrollPane();
        jTable_breakfast = new javax.swing.JTable();
        jLabel1 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        jPanel_lunchLog = new javax.swing.JPanel();
        jLabel_lunchCal = new javax.swing.JLabel();
        jScrollPane7 = new javax.swing.JScrollPane();
        jTable_lunch = new javax.swing.JTable();
        jPanel_snackLog = new javax.swing.JPanel();
        jLabel_snackCal = new javax.swing.JLabel();
        jScrollPane8 = new javax.swing.JScrollPane();
        jTable_snack = new javax.swing.JTable();
        jPanel_dinnerLog = new javax.swing.JPanel();
        jLabel_dinnerCal = new javax.swing.JLabel();
        jScrollPane9 = new javax.swing.JScrollPane();
        jTable_dinner = new javax.swing.JTable();
        jPanel_goals = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel6 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        jPanel6 = new javax.swing.JPanel();
        jLabel_journey = new javax.swing.JLabel();
        jTextField_levelUp = new javax.swing.JTextField();
        jScrollPane_settings = new javax.swing.JScrollPane();
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jTextField_Name = new javax.swing.JTextField();
        jButton_saveSettings = new javax.swing.JButton();
        jTextField_age = new javax.swing.JTextField();
        jTextField_currentWeight = new javax.swing.JTextField();
        jTextField_height = new javax.swing.JTextField();
        jTextField_gender = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        jLabel8 = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jTextField_activityLevel = new javax.swing.JTextField();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jTabbedPane_GUI.setBackground(new java.awt.Color(255, 250, 250));
        jTabbedPane_GUI.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N

        jPanel_dashboard.setBackground(new java.awt.Color(255, 250, 250));

        jProgressBar_carbsToGo.setBackground(new java.awt.Color(174, 198, 207));

        jTextField_proteinToGo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextField_proteinToGo.setText("XX proteins to go");
        jTextField_proteinToGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_proteinToGoActionPerformed(evt);
            }
        });

        jProgressBar_proteinToGo.setBackground(new java.awt.Color(174, 198, 207));

        jTextField_carbsToGo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextField_carbsToGo.setText("XX carbs to go");
        jTextField_carbsToGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_carbsToGoActionPerformed(evt);
            }
        });

        jTextField_fatsToGo.setFont(new java.awt.Font("Verdana", 0, 12)); // NOI18N
        jTextField_fatsToGo.setText("XX fats to go");
        jTextField_fatsToGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_fatsToGoActionPerformed(evt);
            }
        });

        jProgressBar_fatsToGo.setBackground(new java.awt.Color(174, 198, 207));

        jTable1.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null},
                {null, null, null, null}
            },
            new String [] {
                "Title 1", "Title 2", "Title 3", "Title 4"
            }
        ));
        jScrollPane1.setViewportView(jTable1);

        jLabel_totalCalories.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel_totalCalories.setText("0 calories today");

        javax.swing.GroupLayout jPanel_dashboardLayout = new javax.swing.GroupLayout(jPanel_dashboard);
        jPanel_dashboard.setLayout(jPanel_dashboardLayout);
        jPanel_dashboardLayout.setHorizontalGroup(
            jPanel_dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_dashboardLayout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addGroup(jPanel_dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_dashboardLayout.createSequentialGroup()
                        .addComponent(jLabel_totalCalories)
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addGroup(jPanel_dashboardLayout.createSequentialGroup()
                        .addGroup(jPanel_dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jProgressBar_proteinToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_proteinToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jProgressBar_fatsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_fatsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jTextField_carbsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 200, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jProgressBar_carbsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 254, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 231, Short.MAX_VALUE)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 316, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(211, 211, 211))))
        );
        jPanel_dashboardLayout.setVerticalGroup(
            jPanel_dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_dashboardLayout.createSequentialGroup()
                .addGroup(jPanel_dashboardLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_dashboardLayout.createSequentialGroup()
                        .addGap(70, 70, 70)
                        .addComponent(jLabel_totalCalories)
                        .addGap(18, 18, 18)
                        .addComponent(jTextField_proteinToGo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jProgressBar_proteinToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jTextField_carbsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jProgressBar_carbsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(44, 44, 44)
                        .addComponent(jTextField_fatsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jProgressBar_fatsToGo, javax.swing.GroupLayout.PREFERRED_SIZE, 29, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_dashboardLayout.createSequentialGroup()
                        .addGap(109, 109, 109)
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 271, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1049, Short.MAX_VALUE))
        );

        jTabbedPane_GUI.addTab("Dashboard", jPanel_dashboard);

        jScrollPane_search.setHorizontalScrollBar(null);

        jPanel1.setBackground(new java.awt.Color(255, 250, 250));

        jTable_search.setFont(new java.awt.Font("Zapf Dingbats", 1, 13)); // NOI18N
        jTable_search.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Food", "Brand", "Calories", "Protein", "Carbs", "Fats"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jTable_search.setAutoResizeMode(javax.swing.JTable.AUTO_RESIZE_ALL_COLUMNS);
        jTable_search.setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));
        jTable_search.setSelectionBackground(new java.awt.Color(174, 198, 207));
        jScrollPane3.setViewportView(jTable_search);
        if (jTable_search.getColumnModel().getColumnCount() > 0) {
            jTable_search.getColumnModel().getColumn(5).setResizable(false);
        }

        jButton_search.setText("Search");
        jButton_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_searchActionPerformed(evt);
            }
        });

        jTextField_search.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_searchActionPerformed(evt);
            }
        });

        jButton_selectedFood.setText("Add Selected Food");
        jButton_selectedFood.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_selectedFoodActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(316, 316, 316)
                        .addComponent(jTextField_search, javax.swing.GroupLayout.PREFERRED_SIZE, 205, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_search)
                        .addGap(18, 18, 18)
                        .addComponent(jButton_selectedFood, javax.swing.GroupLayout.PREFERRED_SIZE, 122, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGap(116, 116, 116)
                        .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 786, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1163, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(48, 48, 48)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField_search, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jButton_search)
                    .addComponent(jButton_selectedFood))
                .addGap(18, 18, 18)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 508, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(862, Short.MAX_VALUE))
        );

        jScrollPane_search.setViewportView(jPanel1);

        jTabbedPane_GUI.addTab("Search", jScrollPane_search);

        jPanel2.setBackground(new java.awt.Color(255, 250, 250));

        jPanel_breakfastLog.setBackground(new java.awt.Color(174, 198, 207));

        jTable_breakfast.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Food", "Brand", "Calories", "Protein", "Carbs", "Fats"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane2.setViewportView(jTable_breakfast);

        jLabel1.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel1.setText("Breakfast");

        jButton2.setText("Remove");

        javax.swing.GroupLayout jPanel_breakfastLogLayout = new javax.swing.GroupLayout(jPanel_breakfastLog);
        jPanel_breakfastLog.setLayout(jPanel_breakfastLogLayout);
        jPanel_breakfastLogLayout.setHorizontalGroup(
            jPanel_breakfastLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_breakfastLogLayout.createSequentialGroup()
                .addGap(15, 15, 15)
                .addGroup(jPanel_breakfastLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel_breakfastLogLayout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addGap(121, 121, 121)
                        .addComponent(jButton2))
                    .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(15, Short.MAX_VALUE))
        );
        jPanel_breakfastLogLayout.setVerticalGroup(
            jPanel_breakfastLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_breakfastLogLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addGroup(jPanel_breakfastLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jButton2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel_lunchLog.setBackground(new java.awt.Color(174, 198, 207));

        jLabel_lunchCal.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel_lunchCal.setText("Lunch ");

        jTable_lunch.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Food", "Brand", "Calories", "Protein", "Carbs", "Fats"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane7.setViewportView(jTable_lunch);

        javax.swing.GroupLayout jPanel_lunchLogLayout = new javax.swing.GroupLayout(jPanel_lunchLog);
        jPanel_lunchLog.setLayout(jPanel_lunchLogLayout);
        jPanel_lunchLogLayout.setHorizontalGroup(
            jPanel_lunchLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_lunchLogLayout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel_lunchLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_lunchLogLayout.createSequentialGroup()
                        .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(15, 15, 15))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_lunchLogLayout.createSequentialGroup()
                        .addComponent(jLabel_lunchCal)
                        .addGap(212, 212, 212))))
        );
        jPanel_lunchLogLayout.setVerticalGroup(
            jPanel_lunchLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_lunchLogLayout.createSequentialGroup()
                .addGap(9, 9, 9)
                .addComponent(jLabel_lunchCal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(29, Short.MAX_VALUE))
        );

        jPanel_snackLog.setBackground(new java.awt.Color(174, 198, 207));

        jLabel_snackCal.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel_snackCal.setText("Snack ");

        jTable_snack.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Food", "Brand", "Calories", "Protein", "Carbs", "Fats"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane8.setViewportView(jTable_snack);

        javax.swing.GroupLayout jPanel_snackLogLayout = new javax.swing.GroupLayout(jPanel_snackLog);
        jPanel_snackLog.setLayout(jPanel_snackLogLayout);
        jPanel_snackLogLayout.setHorizontalGroup(
            jPanel_snackLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_snackLogLayout.createSequentialGroup()
                .addGap(208, 208, 208)
                .addComponent(jLabel_snackCal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel_snackLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_snackLogLayout.createSequentialGroup()
                    .addGap(0, 15, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 15, Short.MAX_VALUE)))
        );
        jPanel_snackLogLayout.setVerticalGroup(
            jPanel_snackLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_snackLogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_snackCal)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel_snackLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel_snackLogLayout.createSequentialGroup()
                    .addGap(0, 38, Short.MAX_VALUE)
                    .addComponent(jScrollPane8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGap(0, 39, Short.MAX_VALUE)))
        );

        jPanel_dinnerLog.setBackground(new java.awt.Color(174, 198, 207));

        jLabel_dinnerCal.setFont(new java.awt.Font("Helvetica Neue", 0, 18)); // NOI18N
        jLabel_dinnerCal.setText("Dinner");

        jTable_dinner.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {
                "Food", "Brand", "Calories", "Protein", "Carbs", "Fats"
            }
        ) {
            Class[] types = new Class [] {
                java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class, java.lang.String.class
            };

            public Class getColumnClass(int columnIndex) {
                return types [columnIndex];
            }
        });
        jScrollPane9.setViewportView(jTable_dinner);

        javax.swing.GroupLayout jPanel_dinnerLogLayout = new javax.swing.GroupLayout(jPanel_dinnerLog);
        jPanel_dinnerLog.setLayout(jPanel_dinnerLogLayout);
        jPanel_dinnerLogLayout.setHorizontalGroup(
            jPanel_dinnerLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_dinnerLogLayout.createSequentialGroup()
                .addGap(16, 16, 16)
                .addGroup(jPanel_dinnerLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel_dinnerLogLayout.createSequentialGroup()
                        .addComponent(jLabel_dinnerCal)
                        .addGap(194, 194, 194)))
                .addContainerGap(14, Short.MAX_VALUE))
        );
        jPanel_dinnerLogLayout.setVerticalGroup(
            jPanel_dinnerLogLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_dinnerLogLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_dinnerCal)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane9, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(36, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel_snackLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_breakfastLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(26, 26, 26)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel_lunchLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_dinnerLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(1058, 1058, 1058))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_lunchLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_breakfastLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jPanel_snackLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel_dinnerLog, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap(419, Short.MAX_VALUE))
        );

        jScrollPane_log.setViewportView(jPanel2);

        jTabbedPane_GUI.addTab("Log", jScrollPane_log);

        jPanel_goals.setBackground(new java.awt.Color(255, 250, 250));

        jPanel5.setBackground(new java.awt.Color(174, 198, 207));

        jLabel6.setFont(new java.awt.Font("Segoe UI", 0, 24)); // NOI18N
        jLabel6.setText("Weight");

        jLabel10.setText("Maintain weight:");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(206, 206, 206)
                        .addComponent(jLabel6))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(24, 24, 24)
                        .addComponent(jLabel10, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(277, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(18, 18, 18)
                .addComponent(jLabel6)
                .addGap(18, 18, 18)
                .addComponent(jLabel10)
                .addContainerGap(254, Short.MAX_VALUE))
        );

        jPanel6.setBackground(new java.awt.Color(174, 198, 207));

        jLabel_journey.setText("Journey");

        jTextField_levelUp.setText("2 lbs until you level up!");
        jTextField_levelUp.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jTextField_levelUpActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField_levelUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel_journey))
                .addContainerGap(245, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel_journey)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_levelUp, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(132, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel_goalsLayout = new javax.swing.GroupLayout(jPanel_goals);
        jPanel_goals.setLayout(jPanel_goalsLayout);
        jPanel_goalsLayout.setHorizontalGroup(
            jPanel_goalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_goalsLayout.createSequentialGroup()
                .addGap(46, 46, 46)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(60, 60, 60)
                .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel_goalsLayout.setVerticalGroup(
            jPanel_goalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel_goalsLayout.createSequentialGroup()
                .addGroup(jPanel_goalsLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel_goalsLayout.createSequentialGroup()
                        .addGap(83, 83, 83)
                        .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel_goalsLayout.createSequentialGroup()
                        .addGap(152, 152, 152)
                        .addComponent(jPanel6, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1041, Short.MAX_VALUE))
        );

        jTabbedPane_GUI.addTab("Goals", jPanel_goals);

        jPanel3.setBackground(new java.awt.Color(255, 250, 250));

        jLabel2.setFont(new java.awt.Font("Helvetica Neue", 0, 24)); // NOI18N
        jLabel2.setForeground(new java.awt.Color(174, 198, 207));
        jLabel2.setText("Settings");

        jTextField_Name.setText("...");

        jButton_saveSettings.setText("Save");
        jButton_saveSettings.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton_saveSettingsActionPerformed(evt);
            }
        });

        jTextField_age.setText("...");

        jTextField_currentWeight.setText("...");

        jTextField_height.setText("...");

        jTextField_gender.setText("...");

        jLabel3.setText("Name");

        jLabel4.setText("Age");

        jLabel5.setText("Current weight");

        jLabel7.setText("Height");

        jLabel8.setText("Gender");

        jLabel9.setText("Activity level");

        jTextField_activityLevel.setText("...");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(jButton_saveSettings)
                .addGap(40, 40, 40))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                .addComponent(jLabel7)
                                .addComponent(jLabel5)
                                .addComponent(jTextField_age, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addComponent(jTextField_currentWeight)
                                .addComponent(jTextField_height)))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel9)
                            .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(jTextField_activityLevel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 168, Short.MAX_VALUE)
                                .addComponent(jLabel4, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel3, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField_Name, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jTextField_gender, javax.swing.GroupLayout.Alignment.LEADING)))
                        .addGap(267, 637, Short.MAX_VALUE))))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel3)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_Name, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_age, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_currentWeight, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel7)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTextField_height, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel8)
                .addGap(11, 11, 11)
                .addComponent(jTextField_gender, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel9)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTextField_activityLevel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 123, Short.MAX_VALUE)
                .addComponent(jButton_saveSettings)
                .addGap(24, 24, 24))
        );

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(460, 460, 460)
                        .addComponent(jLabel2))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addGap(88, 88, 88)
                        .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap(1201, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGap(34, 34, 34)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(845, Short.MAX_VALUE))
        );

        jScrollPane_settings.setViewportView(jPanel3);

        jTabbedPane_GUI.addTab("Settings", jScrollPane_settings);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane_GUI, javax.swing.GroupLayout.PREFERRED_SIZE, 1007, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane_GUI, javax.swing.GroupLayout.PREFERRED_SIZE, 1139, javax.swing.GroupLayout.PREFERRED_SIZE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jButton_selectedFoodActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_selectedFoodActionPerformed
        FoodSelectionDialog();
    }//GEN-LAST:event_jButton_selectedFoodActionPerformed

    private void jTextField_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_searchActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_searchActionPerformed

    private void jButton_searchActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_searchActionPerformed
        String foodItem = jTextField_search.getText();

        if (!foodItem.isEmpty())
        {
            try {
                String encodedFoodItem = URLEncoder.encode(foodItem, "UTF-8");

                // Sample API request URL to get a list of foods
                String url = "https://api.nal.usda.gov/fdc/v1/foods/search?query=" + encodedFoodItem + "&api_key=" + API_KEY;
                String apiResponse = searchFoodItems(url);
                System.out.println(apiResponse);
            } catch (UnsupportedEncodingException ex) {
                ex.printStackTrace();
                //resultTextArea.setText("Error encoding food item.");
            }
        }
    }//GEN-LAST:event_jButton_searchActionPerformed

    private void jTextField_levelUpActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_levelUpActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_levelUpActionPerformed

    private void jTextField_fatsToGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_fatsToGoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_fatsToGoActionPerformed

    private void jTextField_carbsToGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_carbsToGoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_carbsToGoActionPerformed

    private void jTextField_proteinToGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jTextField_proteinToGoActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jTextField_proteinToGoActionPerformed

    private void jButton_saveSettingsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton_saveSettingsActionPerformed
        double age =  Double.parseDouble(jTextField_age.getText());
        double height = Double.parseDouble(jTextField_height.getText());
        double weight = Double.parseDouble(jTextField_currentWeight.getText());
        Calculator calc = new Calculator( (int) age, jTextField_gender.getText(), (int) height, (int) weight, jTextField_activityLevel.getText());
        calc.getGoals();
    }//GEN-LAST:event_jButton_saveSettingsActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton_saveSettings;
    private javax.swing.JButton jButton_search;
    private javax.swing.JButton jButton_selectedFood;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JLabel jLabel_dinnerCal;
    private javax.swing.JLabel jLabel_journey;
    private javax.swing.JLabel jLabel_lunchCal;
    private javax.swing.JLabel jLabel_snackCal;
    private javax.swing.JLabel jLabel_totalCalories;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel_breakfastLog;
    private javax.swing.JPanel jPanel_dashboard;
    private javax.swing.JPanel jPanel_dinnerLog;
    private javax.swing.JPanel jPanel_goals;
    private javax.swing.JPanel jPanel_lunchLog;
    private javax.swing.JPanel jPanel_snackLog;
    private javax.swing.JProgressBar jProgressBar_carbsToGo;
    private javax.swing.JProgressBar jProgressBar_fatsToGo;
    private javax.swing.JProgressBar jProgressBar_proteinToGo;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane7;
    private javax.swing.JScrollPane jScrollPane8;
    private javax.swing.JScrollPane jScrollPane9;
    private javax.swing.JScrollPane jScrollPane_log;
    private javax.swing.JScrollPane jScrollPane_search;
    private javax.swing.JScrollPane jScrollPane_settings;
    private javax.swing.JTabbedPane jTabbedPane_GUI;
    private javax.swing.JTable jTable1;
    private javax.swing.JTable jTable_breakfast;
    private javax.swing.JTable jTable_dinner;
    private javax.swing.JTable jTable_lunch;
    private javax.swing.JTable jTable_search;
    private javax.swing.JTable jTable_snack;
    private javax.swing.JTextField jTextField_Name;
    private javax.swing.JTextField jTextField_activityLevel;
    private javax.swing.JTextField jTextField_age;
    private javax.swing.JTextField jTextField_carbsToGo;
    private javax.swing.JTextField jTextField_currentWeight;
    private javax.swing.JTextField jTextField_fatsToGo;
    private javax.swing.JTextField jTextField_gender;
    private javax.swing.JTextField jTextField_height;
    private javax.swing.JTextField jTextField_levelUp;
    private javax.swing.JTextField jTextField_proteinToGo;
    private javax.swing.JTextField jTextField_search;
    // End of variables declaration//GEN-END:variables
}
