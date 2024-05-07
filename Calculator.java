/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.dietmanager;

import com.sun.net.httpserver.Request;
import java.io.IOException;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;
import org.apache.http.HttpEntity;
import org.apache.http.HttpRequest;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.net.http.HttpRequest.BodyPublishers;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author caydenmaguire
 */
public class Calculator {
    private static final String API_KEY = "c33d90cc1dmsh5aac23d84aba74dp11e691jsn24718098b8a1";
    private static final String BASE_URL = "https://fitness-calculator.p.rapidapi.com/";
    public int age = 18;
    public String gender = "male";
    public int height = 190;
    public int weight = 85;
    public String activityLevel = "level_4";

    public Calculator( int age, String gender, int height, int weight, String activityLevel)
    {
        this.age = age;
        this.gender = gender;
        this.height = height;
        this.weight = weight;
        this.activityLevel = activityLevel;
    }
    
    public void getGoals() {
            
        StringBuilder responseBuilder = new StringBuilder();
        CloseableHttpClient httpClient = HttpClients.createDefault();
        try {
            String url = BASE_URL + "dailycalorie?age=" + age + "&gender=" + gender + "&height=" + height + "&weight=" + weight + "&activitylevel=" + activityLevel;
            HttpGet request = new HttpGet(url);
            request.setHeader("X-RapidAPI-Key", API_KEY);
            request.setHeader("X-RapidAPI-Host", "fitness-calculator.p.rapidapi.com");

            CloseableHttpResponse response = httpClient.execute(request);

            try {
                HttpEntity entity = response.getEntity();
                String responseBody = EntityUtils.toString(entity);
                
                JSONObject jsonResponse = new JSONObject(responseBody);
            
                JSONObject data = jsonResponse.getJSONObject("data");
                double BMR = data.getDouble("BMR");
                JSONObject goals = data.getJSONObject("goals");
                double maintainWeight = goals.getDouble("maintain weight");
                double mildWeightLoss = goals.getDouble("Mild weight loss");
                double weightLoss = goals.getDouble("Weight loss");
                double extremeWeightLoss = goals.getDouble("Extreme weight loss");
                double mildWeightGain = goals.getDouble("Mild weight gain");
                double weightGain = goals.getDouble("Weight gain");
                double extremeWeightGain = goals.getDouble("Extreme weight gain");
                System.out.println(maintainWeight);
                System.out.println(weightLoss);
               
            
                responseBuilder.append(responseBody);
            } finally {
                response.close();
            }

            // Print the formatted response
            System.out.println(responseBuilder.toString());
            
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                httpClient.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
       
      
    }
    
}
        

