/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.loanaccount.api;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.fineract.infrastructure.core.boot.JDBCDriverConfig;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenantConnection;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;

import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

public class BuildOptions
{
	  @Autowired private JDBCDriverConfig driverConfig ;
	  String res;
	  
	  
	  public String checkReceipt(String receipt,String tenant) {
	      //  System.out.println(config.getSecurityCredential());  
	      
	      JSONArray jsonArray=new JSONArray();
	        JSONObject jsonObject=new JSONObject();
	      
	 //       String tenantUrl = driverConfig.constructProtocol(tenantConnection.getSchemaServer(), tenantConnection.getSchemaServerPort(), tenantConnection.getSchemaName()) ;
	      
	        System.out.println("tenant"+tenant);
	//        System.out.println("connection"+tenantUrl);
	        
	        try {
				jsonObject.put("receiptNumber",receipt.replaceAll("\\W", "") );
				jsonObject.put("tenant", tenant);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	      



	        jsonArray.put(jsonObject);

	        String requestJson=jsonArray.toString().replaceAll("[\\[\\]]","");

	        OkHttpClient client = new OkHttpClient();
	        
	        String url="http://localhost/stkpush/api/receipt.php";
	        MediaType mediaType = MediaType.parse("application/json");
	        RequestBody body = RequestBody.create(mediaType, requestJson);
	        Request request = new Request.Builder()
	                .url(url)
	                .post(body)
	                .addHeader("content-type", "application/json")
	                .addHeader("tenant", tenant.toString())
	                .addHeader("cache-control", "no-cache")
	                .build();


	        Response response;
			try {
				response = client.newCall(request).execute();
				res = response.body().string();
		        //  JSONObject jsonmn = new JSONObject(res);  
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				e.getMessage();
			}
	                
	        
	        System.out.println(res);
	        return res;
	    }
	  
  public String checkReceiptOld(String receipt)
    throws MalformedURLException, IOException
  {
    String postUrl = "http://localhost/stkpush/api/receipt.php";
    final FineractPlatformTenant tenant = ThreadLocalContextUtil.getTenant();
    final FineractPlatformTenantConnection tenantConnection = tenant.getConnection();
    String tenantUrl = driverConfig.constructProtocol(tenantConnection.getSchemaServer(), tenantConnection.getSchemaServerPort(), tenantConnection.getSchemaName()) ;
  
    System.out.println("tenant"+tenant);
    System.out.println("connection"+tenantUrl);
    URL url = new URL(postUrl);
    HttpURLConnection conn = (HttpURLConnection)url.openConnection();

    conn.setDoOutput(true);
    conn.setRequestMethod("POST");
    conn.setRequestProperty("Content-Type", "application/json");
    
    String input = "{\"receiptNumber\":\"" + receipt.replaceAll("\\W", "") + "\"}";
    System.out.println("Receipt Input");
    System.out.println(input);
    
    OutputStream os = conn.getOutputStream();
    os.write(input.getBytes());
    os.flush();
    
    BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
    
    String output = br.readLine();
    
    return output;
  }
}

/* Location:
 * Qualified Name:     org.apache.fineract.portfolio.loanaccount.api.BuildOptions
 * Java Class Version: 8 (52.0)
 * JD-Core Version:    0.7.1
 */