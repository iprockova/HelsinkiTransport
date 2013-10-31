package com.example.helsinkitransport;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.Calendar;
import java.util.List;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


import android.app.Activity;
import android.app.ListActivity;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ParseException;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import android.app.AlertDialog;
import java.lang.Object;


public class Routes extends ListActivity{
	private LinearLayout linearLayout;
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	
	private String from_x, from_y, to_x, to_y;
	RowData rd;
	
	static String[] title = null;

	static String[] detail = null;
	
	static String[] distance = null;
	
	static String[] vehicles = null;

	private String url = "http://api.reittiopas.fi/public-ytv/fi/api/?";
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        setContentView(R.layout.routes);
	        
	        Bundle b = getIntent().getExtras();
	        String from = b.getString("from");
	        String to = b.getString("to");
	        
	        if(from.equalsIgnoreCase("") || from.equalsIgnoreCase(" ") || to.equalsIgnoreCase("") || to.equalsIgnoreCase(" ")){
	        	Intent intent = new Intent().setClass(this, Input.class);
	        	startActivity(intent);
	        }else {
	        	//getDataThread.start();
	        	new getDataTask().execute();
	            
		       }
	    }
	 private class getDataTask extends AsyncTask<Void, Void, Void> {
	        @Override
	        protected Void doInBackground(Void... urls) {
	        	Bundle b = getIntent().getExtras();
		        String from = b.getString("from");
		        String to = b.getString("to");
		        
	        	from = from.replace(" ", "");
	        	to = to.replace(" ", "");
	        	String fromXML = getXML(url + "key=" + from + "&user=" + UserDataContainer.username + "&pass=" + UserDataContainer.password);
	        	XMLfromString(fromXML, "from");
	            
	        	String toXML = getXML(url + "key=" + to + "&user=" + UserDataContainer.username + "&pass=" + UserDataContainer.password);
	        	XMLfromString(toXML, "to");
	        	
	        	if(from_x != null && from_y != null && to_x != null && to_y != null) {
	        		String routeXML = getXML(url + "a=" + from_x + "," + from_y + "&b=" + to_x + "," + to_y + "&user=" + UserDataContainer.username + "&pass=" + UserDataContainer.password);
	        		//System.out.println(routeXML);
	        		XMLfromString2(routeXML);
	        		
	        		System.out.print("");
	        	}
				return null;
	        	
		        }
	        // onPostExecute displays the results of the AsyncTask.
	        @Override
	        protected void onPostExecute(Void result) {
	        	 mInflater = (LayoutInflater) getSystemService(
			        		Activity.LAYOUT_INFLATER_SERVICE);
			        		data = new Vector<RowData>();
			        		
			        		for(int i=0;i<title.length;i++){
			        			try {
			        			 	rd = new RowData(i, title[i],detail[i], distance[i], vehicles[i], i);
			        			    }
			        			catch (ParseException e) {
			        			    e.printStackTrace();
			        			   }
			        			   data.add(rd);
			        		}
			        	   CustomAdapter adapter = new CustomAdapter(getApplicationContext(), R.layout.list, R.id.title, data);
			               setListAdapter(adapter);
			        	   getListView().setTextFilterEnabled(true);
	       }
	    }
	 
	 private String getXML(String url){
	     String line = null;
	     try 
	     {
	         DefaultHttpClient httpClient = new DefaultHttpClient();
	         HttpGet httpPost = new HttpGet(url);

	         HttpResponse httpResponse = httpClient.execute(httpPost);
	         HttpEntity httpEntity = httpResponse.getEntity();
	         line = EntityUtils.toString(httpEntity);

	     } catch (UnsupportedEncodingException e) {
	         e.printStackTrace();
	     } catch (MalformedURLException e) {
	         e.printStackTrace();
	     } catch (IOException e) {
	         e.printStackTrace();
	     }
	     return line;
	  }
	 
	 private void XMLfromString(String xml, String direction){
	        Document doc = null;
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	       
	        try {
	            DocumentBuilder db = dbf.newDocumentBuilder();

	            InputSource is = new InputSource();
	            is.setCharacterStream(new StringReader(xml));
	            doc = db.parse(is); 
	            doc.getDocumentElement().normalize();
	            
	            NodeList listOfBooks = doc.getElementsByTagName("GEOCODE");
	            for(int s=0; s < listOfBooks.getLength(); s++){
	            	Node bookNode = listOfBooks.item(s);
	                if(bookNode.getNodeType() == Node.ELEMENT_NODE){
	                	    Element bookElement = (Element)bookNode;
	                    	
	                	    NodeList unitsList = bookElement.getElementsByTagName("LOC"); 
		                    Element unitsElement = (Element)unitsList.item(0);
		                    
		                    String address = unitsElement.getAttribute("name1").toString();
		                    if(direction.equalsIgnoreCase("from")){
			                    from_x = unitsElement.getAttribute("x").toString();
			                    from_y = unitsElement.getAttribute("y").toString();
			                    System.out.println("Name: " + address + ", " + from_x + ", " + from_y);
		                    }else if(direction.equalsIgnoreCase("to")){
		                    	to_x = unitsElement.getAttribute("x").toString();
			                    to_y = unitsElement.getAttribute("y").toString();
			                    System.out.println("Name: " + address + ", " + to_x + ", " + to_y);
		                    }
		                   
	                    
	             }
	            }

	        } catch (ParserConfigurationException e) {
	            System.out.println("XML parse error: " + e.getMessage());
	            return;
	        } catch (SAXException e) {
	            System.out.println("Wrong XML file structure: " + e.getMessage());
	            return;
	        } catch (IOException e) {
	            System.out.println("I/O exeption: " + e.getMessage());
	            return;
	        }
	        

	    }
	 
	 private void XMLfromString2(String xml){
		    UserDataContainer.xml = xml;
	        Document doc = null;
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	       
	        try {
	            DocumentBuilder db = dbf.newDocumentBuilder();

	            InputSource is = new InputSource();
	            is.setCharacterStream(new StringReader(xml));
	            doc = db.parse(is); 
	            doc.getDocumentElement().normalize();
	            
	            NodeList listOfRoutes = doc.getElementsByTagName("ROUTE");
	            title = new String[listOfRoutes.getLength()];
	            detail = new String[listOfRoutes.getLength()];
	            distance = new String[listOfRoutes.getLength()];
	            vehicles = new String[listOfRoutes.getLength()];
	           // imgid = new Integer[listOfRoutes.getLength()];
	            for(int s=0; s < listOfRoutes.getLength(); s++){
	            	Node routeNode = listOfRoutes.item(s);
	                if(routeNode.getNodeType() == Node.ELEMENT_NODE){
	                	    Element bookElement = (Element)routeNode;
	                    	
	                	    NodeList unitsList = bookElement.getElementsByTagName("LENGTH"); 
		                    Element unitsElement = (Element)unitsList.item(0);
		                    
		                    
		                   
		                    String departure [] = new String[2];
		                    NodeList pointList = bookElement.getElementsByTagName("POINT"); 
		                    int i=0;
		                    for(int a=0; a<pointList.getLength() ; a++){
		                    	Element authorsElement = (Element)pointList.item(a);

		                    	NodeList pointDeparture_departure = authorsElement.getElementsByTagName("DEPARTURE");
		                    	Element pointDeparture_departure_node = (Element) pointDeparture_departure.item(0);
		                    	if(a == 0 || a == (pointList.getLength() -1)){
		                    		departure [i]= pointDeparture_departure_node.getAttribute("time");
		                    		i++;
		                    	}
		                    	
		                    }
		                    
		                    NodeList lineList = bookElement.getElementsByTagName("LINE"); 
		                    Element lineElement = (Element)lineList.item(0);
		                    if(lineElement != null){
		                    	NodeList stopList = lineElement.getElementsByTagName("STOP");
		                    	Element stopNode = (Element) stopList.item(0);
		                    	NodeList arrivalList = stopNode.getElementsByTagName("ARRIVAL");
		                    	Element arrivalElement = (Element)arrivalList.item(0);
		                    
			                    String dep_hour = departure[0].substring(0, 2);
			                    String dep_min = departure[0].substring(2, 4);
			                    
			                    String arr_hour = departure[1].substring(0, 2);
			                    String arr_min = departure[1].substring(2, 4);
		                   
		                    
			                    String arrivalStop =  arrivalElement.getAttribute("time");
			                    String stop_hour = arrivalStop.substring(0, 2);
			                    String stop_min = arrivalStop.substring(2, 4);
			                    title[s] = "Departure time: " + dep_hour + ":" + dep_min + "(" + stop_hour + ":" + stop_min + ")";
			                    
			                    
			                    detail[s] = "Arrival time: " + arr_hour + ":" + arr_min;
		                    
			                    String distanceStr = unitsElement.getAttribute("dist").toString();
			                    float distanceFloat = Float.parseFloat(distanceStr);
			                    int distanceInt = ((int)distanceFloat) / 1000;
			                    
			                    String time = unitsElement.getAttribute("time").toString();
			                    String totalTime_hour = time.substring(0, 2);
		                    
			                    distance[s] = "Distance: " + distanceInt + "km" + ", Total time: " + totalTime_hour;
		                    }else {
		                    		System.out.println("walking");
		                    	    String dep_hour = departure[0].substring(0, 2);
				                    String dep_min = departure[0].substring(2, 4);
				                    
				                    String arr_hour = departure[1].substring(0, 2);
				                    String arr_min = departure[1].substring(2, 4);
				                    title[s] = "Departure time: " + dep_hour + ":" + dep_min;
				                    detail[s] = "Arrival time: " + arr_hour + ":" + arr_min;
				                    
				                    String distanceStr = unitsElement.getAttribute("dist").toString();
				                    float distanceFloat = Float.parseFloat(distanceStr);
				                    int distanceInt = ((int)distanceFloat) / 1000;
				                    
				                    String time = unitsElement.getAttribute("time").toString();
				                    String totalTime_hour = time.substring(0, 2);
				                    
				                    distance[s] = "Distance: " + distanceInt + "km" + ", Total time: " + totalTime_hour;
				                    
				                    vehicles[s] = "Walk";
		                    }
		                    String all_vehicles="";
		                    for(int j=0 ; j < lineList.getLength(); j++) {
		                    	Element lineElement2 = (Element)lineList.item(j);
		                    	if(lineElement2!=null) {
				                    	String vehicleCode =  lineElement2.getAttribute("code");
					                    String number = ""; 
					                    String vehicleType =  lineElement2.getAttribute("type");
					                    int type = Integer.parseInt(vehicleType);
					                    String vehicle = "";
					                    switch (type) {
					                    case 2:  vehicle ="Tram";
					                    		 number = vehicleCode.substring(1,6);
					                    		 if(number.substring(0,1).equalsIgnoreCase("0"))
					                    			 number = number.substring(1);
					                    		 if(number.substring(0,1).equalsIgnoreCase("0"))
					                    			 number = number.substring(1);
					                             break;
					                    case 6:  vehicle = "Metro";
					                             number = "";
					                             break;
					                    case 12: vehicle = "Train";
					                    		 number = vehicleCode.substring(4,5);
					                             break;
					                    case 13: vehicle = "Train";
					                             number = vehicleCode.substring(3,4);
					                    		 break;
					                    case 7:  vehicle = "Ferry";
					                             number = "";
			                            		 break;
					                    default: vehicle = "Bus";
					                    		 number = vehicleCode.substring(1,5);
					                    		 if(number.substring(0,1).equalsIgnoreCase("0"))
					                    			 number = number.substring(1);
					                    		
					                             break;
					                    }
					                    all_vehicles += vehicle + " " + number + "  ";
					                    
		                    		}
			                    
		                    }
		                    vehicles[s] = all_vehicles;
	             }
	            }

	        } catch (ParserConfigurationException e) {
	            System.out.println("XML parse error: " + e.getMessage());
	            return;
	        } catch (SAXException e) {
	            System.out.println("Wrong XML file structure: " + e.getMessage());
	            return;
	        } catch (IOException e) {
	            System.out.println("I/O exeption: " + e.getMessage());
	            return;
	        }
	        

	    }
	    public void onListItemClick(ListView parent, View v, int position,long id) {        	
			        Intent intent = new Intent(this, Lines.class);
			        Bundle b = new Bundle();
	            	b.putInt("position", position );
	            	intent.putExtras(b);
			        this.startActivity(intent);
			     
		}
		
		private class RowData {
		       protected int mId;
		       protected int imageNextId;
		       protected String mTitle;
		       protected String mDetail;
		       protected String mDistance;
		       protected String mVehicle;
		       
		       RowData(int id, String title,String detail, String distance, String vehicle, int nextId){
			       mId=id;
			       imageNextId=nextId;
			       mTitle = title;
			       mDetail=detail;
			       mDistance=distance;
			       mVehicle = vehicle;
		       }
		       
		       @Override
		       public String toString() {
		               return mId+" "+mTitle+" "+mDetail+" "+mDistance+""+mVehicle+""+imageNextId;
		       }
		}
		
		
		private class CustomAdapter extends ArrayAdapter<RowData> {
			  public CustomAdapter(Context context, int resource, int textViewResourceId, List<RowData> objects) {               
				  super(context, resource, textViewResourceId, objects);
			  }
		  
		      @Override
		       public View getView(int position, View convertView, ViewGroup parent) {   
			       ViewHolder holder = null;
			       TextView title = null;
			       TextView detail = null;
			       TextView distance = null;
			       TextView vehicle = null;
			       RowData rowData= getItem(position);
			       if(null == convertView){
			            convertView = mInflater.inflate(R.layout.list, null);
			            holder = new ViewHolder(convertView);
			            convertView.setTag(holder);
			       }
		           holder = (ViewHolder) convertView.getTag();
		           title = holder.gettitle();
		           title.setText(rowData.mTitle);
		           
		           detail = holder.getdetail();
		           detail.setText(rowData.mDetail);  
		           
		           distance = holder.getdistance();
		           distance.setText(rowData.mDistance);  
		           
		           vehicle = holder.getvehicle();
		           vehicle.setText(rowData.mVehicle);  

		           return convertView;
		      	}
		      
		        private class ViewHolder {
		            private View mRow;
		            private TextView title = null;
		            private TextView detail = null;
		            private TextView distance = null;
		            private TextView vehicle = null;

		            public ViewHolder(View row) {
		            mRow = row;
		            }
		            
			        public TextView gettitle() {
			             if(null == title){
			                 title = (TextView) mRow.findViewById(R.id.title);
			                }
			            return title;
			         }     
			         public TextView getdetail() {
			             if(null == detail){
			                  detail = (TextView) mRow.findViewById(R.id.detail);
			                    }
			           return detail;
			         }
			         public TextView getdistance() {
			             if(null == distance){
			                  distance = (TextView) mRow.findViewById(R.id.distance);
			                    }
			           return distance;
			         }
			         public TextView getvehicle() {
			             if(null == vehicle){
			                  vehicle = (TextView) mRow.findViewById(R.id.vehicle);
			                    }
			           return vehicle;
			         }
		        }
		   }

}
