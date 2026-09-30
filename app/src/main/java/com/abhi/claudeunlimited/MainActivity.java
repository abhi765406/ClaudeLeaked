package com.abhi.claudeunlimited;

import android.app.*;
import android.content.*;
import android.graphics.Color;
import android.net.Uri;
import android.os.*;
import android.view.*;
import android.widget.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.net.ssl.HttpsURLConnection;
import org.json.*;

public class MainActivity extends Activity {
    static final String PREF="profiles";
    LinearLayout list; TextView activeName, activeBase; ArrayList<Profile> profiles=new ArrayList<>(); int active=-1;
    int dp(float v){return (int)(v*getResources().getDisplayMetrics().density+.5f);}
    TextView tv(String s,int sp){ TextView t=new TextView(this); t.setText(s); t.setTextColor(Color.rgb(242,244,248)); t.setTextSize(sp); return t; }
    @Override public void onCreate(Bundle b){super.onCreate(b); setContentView(R.layout.activity_main); list=findViewById(R.id.profileList); activeName=findViewById(R.id.activeName); activeBase=findViewById(R.id.activeBase); load(); render();
        findViewById(R.id.addButton).setOnClickListener(v->addDialog());
        findViewById(R.id.testButton).setOnClickListener(v->testActive());
        findViewById(R.id.claudeButton).setOnClickListener(v->{try{startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://claude.ai")));}catch(Exception ignored){}});
    }
    static class Profile {String id,name,base,key; Profile(String i,String n,String b,String k){id=i;name=n;base=b;key=k;}}
    void load(){android.content.SharedPreferences p=getSharedPreferences(PREF,0); int n=p.getInt("n",0); for(int i=0;i<n;i++){String id=p.getString("id"+i,""); profiles.add(new Profile(id,p.getString("name"+i,"Profile "+(i+1)),p.getString("base"+i,"https://api.anthropic.com"),p.getString("key"+i,"")));} active=p.getInt("active",profiles.isEmpty()?-1:0);}
    void save(){SharedPreferences.Editor e=getSharedPreferences(PREF,0).edit();e.putInt("n",profiles.size()).putInt("active",active);for(int i=0;i<profiles.size();i++){Profile x=profiles.get(i);e.putString("id"+i,x.id).putString("name"+i,x.name).putString("base"+i,x.base).putString("key"+i,x.key);}e.apply();}
    void render(){list.removeAllViews();for(int i=0;i<profiles.size();i++){final int ix=i; Profile p=profiles.get(i); LinearLayout row=new LinearLayout(this);row.setOrientation(LinearLayout.VERTICAL);row.setPadding(dp(14),dp(12),dp(14),dp(12));row.setBackgroundResource(R.drawable.bg_card);LinearLayout.LayoutParams rp=new LinearLayout.LayoutParams(-1,dp(86));rp.setMargins(0,dp(6),0,0);row.setLayoutParams(rp);
        TextView a=tv(p.name+(i==active?"   • ACTIVE":""),17);a.setTypeface(null,1);TextView u=tv(p.base,12);u.setTextColor(Color.rgb(154,164,178));row.addView(a);row.addView(u);row.setOnClickListener(v->{active=ix;save();render();});row.setOnLongClickListener(v->{deleteDialog(ix);return true;});list.addView(row);}
        if(active>=0&&active<profiles.size()){activeName.setText(profiles.get(active).name);activeBase.setText(profiles.get(active).base);}else{activeName.setText("No profile selected");activeBase.setText("Add an Anthropic-compatible endpoint");}}
    EditText input(String hint){EditText e=new EditText(this);e.setHint(hint);e.setTextColor(Color.WHITE);e.setHintTextColor(Color.rgb(120,130,145));e.setBackgroundResource(R.drawable.bg_input);e.setSingleLine(true);LinearLayout.LayoutParams p=new LinearLayout.LayoutParams(-1,dp(50));p.setMargins(0,dp(8),0,0);e.setLayoutParams(p);return e;}
    void addDialog(){LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(20),0,dp(20),0);EditText name=input("Profile name");EditText base=input("Base URL (e.g. https://api.anthropic.com)");base.setText("https://api.anthropic.com");EditText key=input("API key (optional)");key.setInputType(0x81);box.addView(name);box.addView(base);box.addView(key);new AlertDialog.Builder(this).setTitle("Add API profile").setView(box).setNegativeButton("Cancel",null).setPositiveButton("Save",(d,w)->{String nm=name.getText().toString().trim();if(nm.isEmpty())nm="Profile "+(profiles.size()+1);String bu=base.getText().toString().trim();if(bu.endsWith("/"))bu=bu.substring(0,bu.length()-1);profiles.add(new Profile(UUID.randomUUID().toString(),nm,bu,key.getText().toString().trim()));active=profiles.size()-1;save();render();}).show();}
    void deleteDialog(int ix){new AlertDialog.Builder(this).setTitle("Delete profile?").setMessage(profiles.get(ix).name).setNegativeButton("Cancel",null).setPositiveButton("Delete",(d,w)->{profiles.remove(ix);if(active==ix)active=profiles.isEmpty()?-1:Math.min(ix,profiles.size()-1);else if(active>ix)active--;save();render();}).show();}
    void testActive(){if(active<0){Toast.makeText(this,"Add/select a profile first",Toast.LENGTH_SHORT).show();return;}final Profile p=profiles.get(active);Toast.makeText(this,"Testing endpoint…",Toast.LENGTH_SHORT).show();new AsyncTask<Void,Void,String>(){protected String doInBackground(Void...v){try{URL u=new URL(p.base);HttpURLConnection c=(HttpURLConnection)u.openConnection();c.setRequestMethod("GET");c.setConnectTimeout(6000);c.setReadTimeout(6000);if(p.key.length()>0)c.setRequestProperty("x-api-key",p.key);int code=c.getResponseCode();return "HTTP "+code;}catch(Exception e){return "Error: "+e.getClass().getSimpleName();}}protected void onPostExecute(String s){new AlertDialog.Builder(MainActivity.this).setTitle("Endpoint test").setMessage(s).setPositiveButton("OK",null).show();}}.execute();}
}
