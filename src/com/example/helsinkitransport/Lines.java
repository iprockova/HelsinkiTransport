package com.example.helsinkitransport;

import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.ArrayList;
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
import android.content.Context;
import android.database.Cursor;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.Calendar;
import java.util.concurrent.ExecutionException;

import android.content.ContentValues;
import android.net.Uri;
import android.widget.Toast;

public class Lines extends ListActivity{
	
	private static String calanderURL = "";  
    private static String calanderEventURL = "";  
    private static String calanderRemiderURL = "";
    
    static{
    	calanderURL = "content://com.android.calendar/calendars";  
        calanderEventURL = "content://com.android.calendar/events";  
        calanderRemiderURL = "content://com.android.calendar/reminders"; 
    }
	
	private LinearLayout linearLayout;
	private LayoutInflater mInflater;
	private Vector<RowData> data;
	RowData rd;
	
	static  List<String> startingTime; 

	static  List<String> street;
	
	static  List<String> vehicles;
	
	static  List<String> durationTime;
	
	boolean flag = true;
	
	String departureTime,arrivalTime,distanceLenght, startTime = "";
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lines);
        
        flag = true;
        
        final Button button = (Button) findViewById(R.id.button_id);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                System.out.println("Button Clicked");
                if(startingTime.size() > 0) {
                	String startTimeHour = startTime.substring(0, 2);
                	String startTimeMinutes = startTime.substring(3, 5);
                	String arrivalTimeHour = arrivalTime.substring(0, 2);
                	String arrivalTimeMinutes = arrivalTime.substring(3, 5);
                	
                	String title = "Alarm for bus";
             		String description = "Alarm";
             		int startHour = Integer.parseInt(startTimeHour);
             		int endHour = Integer.parseInt(arrivalTimeHour);;
             		int MinutesBeforeAlarm = 3;
             		int startMinute = Integer.parseInt(startTimeMinutes);
             		int endMinute =  Integer.parseInt(arrivalTimeMinutes);
             		
             		setCalendarEvent(title, description, startHour, startMinute, endHour, endMinute,MinutesBeforeAlarm);
                }
            }
        });
        
        startingTime = new ArrayList<String>();
        street = new ArrayList<String>();
        vehicles = new ArrayList<String>();
        durationTime = new ArrayList<String>();
        
        
        Bundle b = getIntent().getExtras();
        int position = b.getInt("position");
        XMLfromString(UserDataContainer.xml, position);
        
        mInflater = (LayoutInflater) getSystemService(
        		Activity.LAYOUT_INFLATER_SERVICE);
        		data = new Vector<RowData>();
        		String[] startingTimeString  = (String [])startingTime.toArray(new String[startingTime.size()]);
        		String[] durationTimeString  = (String [])durationTime.toArray(new String[durationTime.size()]);
        		String[] streetString  = (String [])street.toArray(new String[street.size()]);
        		String[] vehiclesString  = (String [])vehicles.toArray(new String[vehicles.size()]);
        		
        		for(int i=0;i<startingTimeString.length;i++){
        			try {
        			 	rd = new RowData(i, startingTimeString[i],durationTimeString[i], streetString[i], vehiclesString[i], i);
        			    }
        			catch (ParseException e) {
        			    e.printStackTrace();
        			   }
        			   data.add(rd);
        		}
        	   CustomAdapter adapter = new CustomAdapter(this, R.layout.list_lines, R.id.title, data);
               setListAdapter(adapter);
        	   getListView().setTextFilterEnabled(true);
	}
	
	private void XMLfromString(String xml, int position){
        Document doc = null;
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
       
        try {
            DocumentBuilder db = dbf.newDocumentBuilder();

            InputSource is = new InputSource();
            is.setCharacterStream(new StringReader(xml));
            doc = db.parse(is); 
            doc.getDocumentElement().normalize();
            
            NodeList listOfR = doc.getElementsByTagName("ROUTE");
           // imgid = new Integer[listOfRoutes.getLength()];
            for(int s=0; s < listOfR.getLength(); s++){
            	Node routeNode = listOfR.item(s);
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
		                   
		                    String departure2 =  dep_hour + ":" + dep_min;
		                    System.out.println(departure2);
		                    departureTime = departure2;
		                    
		                    String arrival2 = arr_hour + ":" + arr_min;
		                    System.out.println( arrival2);
		                    arrivalTime= arrival2;
	                    
		                    String distanceStr = unitsElement.getAttribute("dist").toString();
		                    float distanceFloat = Float.parseFloat(distanceStr);
		                    int distanceInt = ((int)distanceFloat) / 1000;
		                    
		                    String time = unitsElement.getAttribute("time").toString();
		                    String totalTime_hour = time.substring(0, 2);
	                    
		                    String distance2 = "Total time: " + totalTime_hour;
		                    System.out.println(distance2);
		                    distanceLenght = distance2;
	                    }else {
	                    		System.out.println("walking");
	                    	    String dep_hour = departure[0].substring(0, 2);
			                    String dep_min = departure[0].substring(2, 4);
			                    
			                    String arr_hour = departure[1].substring(0, 2);
			                    String arr_min = departure[1].substring(2, 4);
			                    String departure2  = dep_hour + ":" + dep_min;
			                    String arrival2 = arr_hour + ":" + arr_min;
			                    departureTime = departure2;
			                    arrivalTime= arrival2;
			                    
			                    String distanceStr = unitsElement.getAttribute("dist").toString();
			                    float distanceFloat = Float.parseFloat(distanceStr);
			                    int distanceInt = ((int)distanceFloat) / 1000;
			                    
			                    String time = unitsElement.getAttribute("time").toString();
			                    String totalTime_hour = time.substring(0, 2);
			                    
			                    String distance2 = "Total time: " + totalTime_hour;
			                    distanceLenght = distance2;
	                    }
                }
            }
            
            
            
            
            
            
            
            NodeList listOfNodes = doc.getElementsByTagName("MTRXML");
            
            for(int s=0; s < listOfNodes.getLength(); s++){
            	Node routeNode = listOfNodes.item(s);
            	if(routeNode.getNodeType() == Node.ELEMENT_NODE){
            		Element rootElement = (Element)routeNode;
            		
            		NodeList listOfRoutes = rootElement.getElementsByTagName("ROUTE");
            		Element oneRoute = (Element)listOfRoutes.item(position);
            		
            		NodeList childList = oneRoute.getChildNodes();
            		
            		for(int j=0; j< childList.getLength(); j++){
            			Node child = (Node)childList.item(j);
            			
            			if(child.getNodeType() == Node.ELEMENT_NODE){
    	            		String name = child.getNodeName();
    	            		Element childElement = (Element)child;
    	            		if(name.equalsIgnoreCase("LENGTH")) {                           //-------------------LENGTH main tima and dist
    	            			String time = childElement.getAttribute("time");
    	            			float timeFloat = Float.parseFloat(time);
    	            			int timeInt = (int)timeFloat;
    	            			String dist = childElement.getAttribute("dist");
    	            			float distanceFloat = Float.parseFloat(dist);
			                    int distanceInt = ((int)distanceFloat) / 1000;
    	            			System.out.println("Route time and lenght: " + timeInt + "min, " + distanceInt + "km");
    	            		}else if(name.equalsIgnoreCase("WALK")) {						//-------------------WALK time and dist and starting time
    	            			NodeList listOfElements = childElement.getElementsByTagName("LENGTH");
    	            			Element lenghtNode = (Element)listOfElements.item(0);
    	            			String walkTotalTime = lenghtNode.getAttribute("time");
    	            			float walkTotalTimeFloat = Float.parseFloat(walkTotalTime);
    	            			int walkTotalTimeInt = (int) walkTotalTimeFloat;
    	            			String walkTotalTimeStr = Integer.toString(walkTotalTimeInt);
    	            			String walktDistance = lenghtNode.getAttribute("dist");
    	            			float distanceFloat = Float.parseFloat(walktDistance);
			                    int distanceInt = ((int)distanceFloat) / 1000;
    	            			System.out.println("Walk time and lenght: " + walkTotalTimeStr + "min, " + distanceInt + "km");
    	            			
    	            			NodeList listOfArrivals = childElement.getElementsByTagName("ARRIVAL");
    	            			Element arrivalElement = (Element)listOfArrivals.item(0);
    	            			
    	            			String walkStartingTime = arrivalElement.getAttribute("time");
    	            			String dep_hour = walkStartingTime.substring(0, 2);
				                String dep_min = walkStartingTime.substring(2, 4);
				                walkStartingTime = dep_hour +":" + dep_min; 
    	            			System.out.println("Walk starting time: " + walkStartingTime);
    	            			
    	            			
    	            			
    	            			String streetName="";
    	            			NodeList listOfPoints = childElement.getChildNodes();
    	            			for(int i=0; i < listOfPoints.getLength(); i++) {
    	            				Node childNode = (Node)listOfPoints.item(i);
    	            				if(childNode.getNodeType() == Node.ELEMENT_NODE){
    	            					Element childEl = (Element)childNode;
    	            					String x = childEl.getAttribute("x");
    	            					String y = childEl.getAttribute("y");
    	            					if((x==null && y==null) || (x.equalsIgnoreCase("") && y.equalsIgnoreCase("")))
    	            						continue;
    	            					else {
    	            						streetName = getPointName(x,y);
    	            						System.out.println("Walk Name :" + streetName);   					//-------------------WALK name
    	            						break;
    	            					}
    	            				}
    	            			}
    	            			startingTime.add("Start Time: " + walkStartingTime);
    	            			if(flag){
    	            				startTime = walkStartingTime;
    	            				flag = false;
    	            			}
    	            			street.add(streetName);
    	            			vehicles.add("Walk");
    	            			durationTime.add("Time: "+ walkTotalTimeStr + "min");
    	            			
    	            		}else if(name.equalsIgnoreCase("LINE")){									//-------------------LINE time and dist and starting time
    	            			NodeList listOfElements = childElement.getElementsByTagName("LENGTH");
    	            			Element lenghtNode = (Element)listOfElements.item(0);
    	            			String lineTotalTime = lenghtNode.getAttribute("time");
    	            			float lineTotatTimeFloat = Float.parseFloat(lineTotalTime);
    	            			int lineTotalTimeInt = (int) lineTotatTimeFloat;
    	            			String lineTotalTimeStr = Integer.toString(lineTotalTimeInt);
    	            			String lineDistance = lenghtNode.getAttribute("dist");
    	            			float distanceFloat = Float.parseFloat(lineDistance);
			                    int distanceInt = ((int)distanceFloat) / 1000;
    	            			System.out.println("Bus time and lenght: " + lineTotalTimeStr + "min, " + distanceInt + "km");
    	            			
    	            			NodeList listOfDepartures = childElement.getElementsByTagName("DEPARTURE");
    	            			Element departureElement = (Element)listOfDepartures.item(0);
    	            			
    	            			String busStartingTime = departureElement.getAttribute("time");
    	            			String dep_hour = busStartingTime.substring(0, 2);
				                String dep_min = busStartingTime.substring(2, 4);
				                busStartingTime = dep_hour +":" + dep_min; 
    	            			System.out.println("Bus starting time: " + busStartingTime);
    	            			
    	            			NodeList listOfStops = childElement.getElementsByTagName("STOP");
    	            			Element stopNode = (Element)listOfStops.item(0);
    	            			NodeList nameList = stopNode.getElementsByTagName("NAME");
    	            			Element nameStopElement = (Element)nameList.item(0);
    	            			String nameStop = nameStopElement.getAttribute("val");    					 //-------------------LINE name  ????????VERIFY             
    	            			System.out.println("Stop name: " + nameStop);
    	            			
    	            			String all_vehicles="";
    	            			String vehicleCode =  childElement.getAttribute("code");
			                    String number = ""; 
			                    String vehicleType =  childElement.getAttribute("type");
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
			                    all_vehicles += vehicle + " " + number + "  ";                           //-------------------LINE vehicle 
			                    
			                    startingTime.add("Starting time: " + busStartingTime);
			                    street.add(nameStop);
			                    vehicles.add(all_vehicles);
			                    durationTime.add("Time: " + lineTotalTimeStr + "min");
			                }
                		}
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
	
	private String getPointName(String x, String y) {
		String result="";
		try {
			 result = new getPointsTask().execute(x,y).get();
			 System.out.println("print"+result);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return result;
		
	}
	private class getPointsTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
        	String url = "http://api.reittiopas.fi/public-ytv/fi/api/?";
        	String routeXML = getXML(url + "x=" + urls[0] + "&y=" + urls[1] + "&user=" + UserDataContainer.username + "&pass=" + UserDataContainer.password);
    		String pointName = XMLfromString(routeXML);
    		return pointName;
	        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
        	
       }
    }
	 private String XMLfromString(String xml){
	        Document doc = null;
	        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	        String fullname = "";
	        try {
	            DocumentBuilder db = dbf.newDocumentBuilder();

	            InputSource is = new InputSource();
	            is.setCharacterStream(new StringReader(xml));
	            doc = db.parse(is); 
	            doc.getDocumentElement().normalize();
	            
	            NodeList listOfLocations = doc.getElementsByTagName("LOC");
	            Node locationNode = listOfLocations.item(0);
	            Element locationElement = (Element)locationNode;
	            String name = locationElement.getAttribute("name1");
	            String number = locationElement.getAttribute("number");
	            fullname = name;
	            if(number !=null){
	            	 fullname = name + " "+ number;
	            }
	            return fullname;
	        } catch (ParserConfigurationException e) {
	            System.out.println("XML parse error: " + e.getMessage());
	            return fullname;
	        } catch (SAXException e) {
	            System.out.println("Wrong XML file structure: " + e.getMessage());
	            return fullname;
	        } catch (IOException e) {
	            System.out.println("I/O exeption: " + e.getMessage());
	            return fullname;
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
		       ImageView i11=null;
		       ImageView i22=null;
		       RowData rowData= getItem(position);
		       if(null == convertView){
		            convertView = mInflater.inflate(R.layout.list_lines, null);
		            TextView fromTo = (TextView)convertView.findViewById(R.id.fromTo);
		            fromTo.setText(UserDataContainer.from + "(" + departureTime + ")" + "-" + UserDataContainer.to + "(" + arrivalTime + ")");
		            TextView time = (TextView)convertView.findViewById(R.id.time);
		            time.setText(distanceLenght);
		           
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
	            private ImageView i11=null; 
	            private ImageView i22=null; 

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
	
	/*
     * API 10
     * Take 5 input as parameters, title, description, startHour, endHour, MinutesBeforeAlarm
     * title and description, are strings, e.g. 
     * 
     * String title = "test title";
     * String description = "test description";
     * 
     * startHour, startMinute, endHour, endMinute and MinutesBeforeAlarm are integer, e.g.
     * int startHour = 11;
     * int endHour = 21;
     * int startMinute = 4;
     * int endMinute = 14;
     * int MinutesBeforeAlarm = 20;
     * 
     * the method can be called:
     * setCalendarEvent(title, description, startHour, startMinute, endHour, endMinute,MinutesBeforeAlarm);
     */
	
	private void setCalendarEvent(String title, String description,
			int startHour, int startMinute, int endHour, int endMinute, int MinutesBeforeAlarm) {
		String calendarID = "";  
        Cursor userCursor = getContentResolver().query(Uri.parse(calanderURL), null,   
                null, null, null);  
        if(userCursor.getCount() > 0){  
            userCursor.moveToFirst(); 
            //the Index column are analysed by debuging
            calendarID = userCursor.getString(userCursor.getColumnIndex("_id"));  
        }  
        
        //create a calendar event
        ContentValues createEvent = new ContentValues();  
		
		createEvent.put("title", title);  
		createEvent.put("description", description);  
        createEvent.put("calendar_id",calendarID);  
          
        Calendar mCalendar = Calendar.getInstance();  
        mCalendar.set(Calendar.HOUR_OF_DAY,startHour);
        mCalendar.set(Calendar.MINUTE,startMinute);
        
        long start = mCalendar.getTime().getTime();  
        mCalendar.set(Calendar.HOUR_OF_DAY,endHour);
		mCalendar.set(Calendar.MINUTE,endMinute);
        long end = mCalendar.getTime().getTime(); 
          
        createEvent.put("dtstart", start);  
        createEvent.put("dtend", end);  
        createEvent.put("hasAlarm",1);  
          
        Uri newEvent = getContentResolver().insert(Uri.parse(calanderEventURL), createEvent);  
        
        //id is used for refering the alarm
        long id = Long.parseLong( newEvent.getLastPathSegment() ); 
        
        //creat an alarm for the event
        ContentValues creatAlarm = new ContentValues();  
        creatAlarm.put( "event_id", id );
        
		//reminder
        creatAlarm.put( "minutes", MinutesBeforeAlarm );  
        getContentResolver().insert(Uri.parse(calanderRemiderURL), creatAlarm);  
        Toast.makeText(Lines.this, "created", Toast.LENGTH_LONG).show();
	}

	 
}
