package util;

import Events.Child;
import Events.User;
import android.content.Context;
import android.content.Entity;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

/**
 * Created by adrianlim on 15-06-13.
 */
public class ServerConnector {

    private HttpClient http;
    private String rootUrl;

    public ServerConnector(){
        http = new DefaultHttpClient();
        rootUrl = "http://ds-medical-care.meteor.com/api/";
    }

    public void sendTrackabletoServer(String childid, String notifyat, String promptinterval, String problemid, String isproblem, String severity) throws Exception{
        String url = rootUrl + "trackables";
        HttpPost httpost = new HttpPost(url);
        httpost.setEntity(new StringEntity("notifyAt="+notifyat+"&promptInterval="+promptinterval+"&childId="+childid+"&problemId="+problemid+"&isProblemForChild"+isproblem));
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        HttpResponse response = http.execute(httpost);
    }

    public void sendProblemtoServer(String code, String name) throws Exception{
        String url = rootUrl + "problems";
        HttpPost httpost = new HttpPost(url);
        httpost.setEntity(new StringEntity("code="+code+"&name="+name));
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        HttpResponse response = http.execute(httpost);
    }

    public void sendEventtoServer(String time, String trackable,String happiness, String note) throws Exception{
        String url = rootUrl + "problems";
        HttpPost httpost = new HttpPost(url);
        httpost.setEntity(new StringEntity("timeStamp="+time+"&trackableId="+trackable+"&happinessLevel="+happiness+"&note="+note));
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/x-www-form-urlencoded; charset=UTF-8");
        HttpResponse response = http.execute(httpost);
    }


    public void authenticateUser(User user, String password) throws IOException, JSONException{
       // Just for debugging, feel free to get rid of this if this is stable
       //LinkedList<Child> test = getChildrenOfParent("bgzWZckbQFc8nEYYZ");
       //test.size();
       // http://ds-medical-care.meteor.com/api/superparents/bgzWZckbQFc8nEYYZ
        /*
        if (user.getPassword().equals(password)){
            Conf.getmInstance().currentUserId = user.get_id();
            Conf.getmInstance().currentUserName = user.getUsername();
            return true;
        }
        else {
            return false;
        }
        */
    }

    public void sendParenttoServer(String username,String password, String email) throws Exception{
        String json = "\"username\": \""+username+"\", \""+password+"\": \"Password1\", \"email\": \""+email+"\",";

        HttpPost hpost = new HttpPost(rootUrl+"parents");
        hpost.setEntity(new StringEntity(json));

        HttpResponse response = http.execute(hpost);
        //Just for debugging, sorry!
        //      getChildrenOfParent("bgzWZckbQFc8nEYYZ");

    }

    public LinkedList<Child> getChildrenOfParent(String parentId) throws IOException{
        JsonElement supertree = getSuperObject(parentId);
        JsonObject obj = supertree.getAsJsonObject();
        JsonElement data = obj.get("data");
        JsonObject dataobj = data.getAsJsonObject();
        JsonArray childarray = dataobj.getAsJsonArray("children");
        LinkedList<Child> childlist= new LinkedList<Child>();
        for (int i = 0; i < childarray.size(); i++) {
            JsonElement childje = childarray.get(i);
            JsonObject childit = childje.getAsJsonObject();

            String firstname = childit.get("firstName").toString();
            String lastName = childit.get("lastName").toString();
            String dob = childit.get("dob").toString();
            String gender = childit.get("gender").toString();
            String parentid = childit.get("parentId").toString();
            String bedtime = childit.get("bedTime").toString();
            String _id = childit.get("_id").toString();

            Child child = new Child(firstname,lastName,dob,gender,parentid,bedtime,_id);
            childlist.add(child);
        }
        return childlist;
    }

    public LinkedList<Child> getChildrenOfParent(String parentId, Context context) throws IOException{
        JsonElement supertree = getSuperObject(parentId);
        JsonObject obj = supertree.getAsJsonObject();
        JsonElement data = obj.get("data");
        JsonObject dataobj = data.getAsJsonObject();
        JsonArray childarray = dataobj.getAsJsonArray("children");
        LinkedList<Child> childlist= new LinkedList<Child>();
        for (int i = 0; i < childarray.size(); i++) {
            JsonElement childje = childarray.get(i);
            JsonObject childit = childje.getAsJsonObject();

            String firstname = childit.get("firstName").toString().replace("\"","");
            String lastName = childit.get("lastName").toString().replace("\"", "");
            String dob = childit.get("dob").toString().replace("\"", "");
            String gender = childit.get("gender").toString().replace("\"", "");
            String parentid = childit.get("parentId").toString().replace("\"", "");
            String bedtime = childit.get("bedTime").toString().replace("\"", "");
            String _id = childit.get("_id").toString().replace("\"","");

            Child child = new Child(firstname,lastName,dob,gender,parentid,bedtime,_id,context);
            childlist.add(child);
        }
        return childlist;
    }

    public void pushNote(String childId, String date, String note){
        //stuff....
    }

    public void sendChildData(String childId, String date, String mood){
        //stuff
    }

    private void sendJson(String uri, HashMap map) throws Exception{
        /*
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        needs to be added to whichever activity is running it.
         */

        String path = rootUrl+uri;
        HttpPost httpost = new HttpPost(path);
        JSONObject holder = getJsonObjectFromMap(map);
        StringEntity se = new StringEntity(holder.toString());
        httpost.setEntity(se);
        httpost.setEntity(se);
        //sets a request header so the page receving the request
        //will know what to do with it
        httpost.setHeader("Accept", "application/json");
        httpost.setHeader("Content-type", "application/json");

        //Handles what is returned from the page
        ResponseHandler responseHandler = new BasicResponseHandler();
        http.execute(httpost, responseHandler);
    }

    private static JSONObject getJsonObjectFromMap(Map params) throws JSONException {

        //all the passed parameters from the post request
        //iterator used to loop through all the parameters
        //passed in the post request
        Iterator iter = params.entrySet().iterator();

        //Stores JSON
        JSONObject holder = new JSONObject();

        //using the earlier example your first entry would get email
        //and the inner while would get the value which would be 'foo@bar.com'
        //{ fan: { email : 'foo@bar.com' } }

        //While there is another entry
        while (iter.hasNext())
        {
            //gets an entry in the params
            Map.Entry pairs = (Map.Entry)iter.next();

            //creates a key for Map
            String key = (String)pairs.getKey();

            //Create a new map
            Map m = (Map)pairs.getValue();

            //object for storing Json
            JSONObject data = new JSONObject();

            //gets the value
            Iterator iter2 = m.entrySet().iterator();
            while (iter2.hasNext())
            {
                Map.Entry pairs2 = (Map.Entry)iter2.next();
                data.put((String)pairs2.getKey(), pairs2.getValue());
            }

            //puts email and 'foo@bar.com'  together in map
            holder.put(key, data);
        }
        return holder;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException{
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }

    public User getUser(String username) throws IOException {
        URL url = new URL(rootUrl + "parents/username=" + username);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        Boolean childFlag = false;
        for (int counter=0;counter<11;counter++) {
            line = rd.readLine();
            sb.append(line);
        }
        rd.close();
        conn.disconnect();


        JsonParser parser = new JsonParser();
        JsonElement je = parser.parse(sb.toString());

        JsonObject jo = je.getAsJsonObject();
        JsonArray data = jo.getAsJsonArray("data");


        JsonObject user = data.get(0).getAsJsonObject();

        String newusername = user.get("username").toString().replace("\"","");
        String password = user.get("password").toString().replace("\"", "");
        String email = user.get("email").toString().replace("\"", "");
        String _id = user.get("_id").toString().replace("\"","");

        User u = new User(newusername, password, email, _id);
        return u;
    }


    public JsonElement getSuperObject(String parentId) throws IOException {
        URL url = new URL(rootUrl+"superparents/"+parentId);
        HttpURLConnection conn =
                (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() != 200) {
            throw new IOException(conn.getResponseMessage());
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader(
                new InputStreamReader(conn.getInputStream()));
        StringBuilder sb = new StringBuilder();
        String line;
        Boolean childFlag = false;
        while ((line = rd.readLine()) != null) {
            sb.append(line);
        }
        rd.close();
        conn.disconnect();

        //Gson gson = new GsonBuilder().setPrettyPrinting().create();
        JsonParser parser = new JsonParser();
        JsonElement je = parser.parse(sb.toString());
        //String prettyJsonString = gson.toJson(je);
        //System.out.print(prettyJsonString);
        return je;
    }
}
