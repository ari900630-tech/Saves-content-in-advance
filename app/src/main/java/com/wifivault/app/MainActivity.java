package com.wifivault.app;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.*;
import java.io.File;
import java.util.Locale;

public class MainActivity extends Activity {
    LinearLayout root; EditText urlInput; TextView status,storage; WebView web;
    boolean libraryShowing=false;
    int dp(float n){return (int)(n*getResources().getDisplayMetrics().density+.5f);}
    TextView label(String s,int size){TextView t=new TextView(this);t.setText(s);t.setTextSize(size);t.setTextColor(0xff17233d);t.setGravity(Gravity.CENTER_VERTICAL);return t;}
    android.graphics.drawable.GradientDrawable round(int c,int r){android.graphics.drawable.GradientDrawable d=new android.graphics.drawable.GradientDrawable();d.setColor(c);d.setCornerRadius(dp(r));d.setStroke(dp(1),0xffe4e9f3);return d;}
    Button button(String s){Button b=new Button(this);b.setText(s);b.setTextColor(-1);b.setTextSize(14);b.setAllCaps(false);b.setBackground(round(0xff3867f5,12));return b;}
    @Override public void onCreate(Bundle b){super.onCreate(b);getWindow().setStatusBarColor(0xfff5f7fc);getWindow().setNavigationBarColor(0xfff5f7fc);getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);buildUi();refreshStatus();}
    void buildUi(){
        root=new LinearLayout(this);root.setOrientation(1);root.setPadding(dp(16),dp(12),dp(16),dp(8));root.setBackgroundColor(0xfff5f7fc);setContentView(root);
        LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);TextView logo=label("◈",30);logo.setTextColor(0xff3867f5);top.addView(logo,new LinearLayout.LayoutParams(dp(42),dp(48)));
        LinearLayout brand=new LinearLayout(this);brand.setOrientation(1);TextView title=label("WiFi Vault",23);title.setTypeface(null,1);TextView sub=label("המאגר האישי שלך למצב לא מקוון",12);sub.setTextColor(0xff69758c);brand.addView(title);brand.addView(sub);top.addView(brand);root.addView(top);
        LinearLayout card=new LinearLayout(this);card.setOrientation(1);card.setPadding(dp(14),dp(12),dp(14),dp(12));card.setBackground(round(-1,18));LinearLayout.LayoutParams cp=new LinearLayout.LayoutParams(-1,-2);cp.topMargin=dp(14);root.addView(card,cp);
        status=label("בודק חיבור…",15);status.setTypeface(null,1);card.addView(status);storage=label("",13);storage.setTextColor(0xff64718a);card.addView(storage);TextView note=label("שמור קבצים ודפים מראש כדי להשתמש בהם גם ללא אינטרנט.",13);note.setTextColor(0xff64718a);card.addView(note);
        LinearLayout actions=new LinearLayout(this);Button browse=button("דפדפן"),library=button("הקבצים שלי");browse.setOnClickListener(v->showBrowser());library.setOnClickListener(v->showLibrary());actions.addView(browse,new LinearLayout.LayoutParams(0,dp(46),1));actions.addView(library,new LinearLayout.LayoutParams(0,dp(46),1));LinearLayout.LayoutParams ap=new LinearLayout.LayoutParams(-1,-2);ap.topMargin=dp(10);root.addView(actions,ap);
        urlInput=new EditText(this);urlInput.setSingleLine(true);urlInput.setHint("כתובת אתר או קישור לקובץ");urlInput.setTextSize(14);urlInput.setInputType(android.text.InputType.TYPE_CLASS_TEXT|android.text.InputType.TYPE_TEXT_VARIATION_URI);urlInput.setPadding(dp(12),0,dp(12),0);urlInput.setBackground(round(-1,12));LinearLayout.LayoutParams ip=new LinearLayout.LayoutParams(-1,dp(48));ip.topMargin=dp(12);root.addView(urlInput,ip);
        Button save=button("הורד ושמור לטלפון");save.setOnClickListener(v->downloadInput());LinearLayout.LayoutParams sp=new LinearLayout.LayoutParams(-1,dp(48));sp.topMargin=dp(8);root.addView(save,sp);
        web=new WebView(this);web.getSettings().setJavaScriptEnabled(true);web.getSettings().setDomStorageEnabled(true);web.getSettings().setLoadsImagesAutomatically(true);web.setWebViewClient(new WebViewClient());web.setWebChromeClient(new WebChromeClient());web.setDownloadListener((u,ua,d,m,l)->enqueueDownload(u));LinearLayout.LayoutParams wp=new LinearLayout.LayoutParams(-1,0,1);wp.topMargin=dp(12);root.addView(web,wp);showBrowser();
    }
    boolean wifiConnected(){try{ConnectivityManager cm=(ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);Network n=cm.getActiveNetwork();NetworkCapabilities c=cm.getNetworkCapabilities(n);return c!=null&&c.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)&&c.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET);}catch(Exception e){return false;}}
    void refreshStatus(){boolean w=wifiConnected();status.setText(w?"● מחובר ל־Wi-Fi":"○ אין חיבור Wi-Fi");status.setTextColor(w?0xff16835c:0xffbd5b32);File d=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);storage.setText("נשמרו: "+human(dirSize(d))+" · פנוי במכשיר: "+human(d==null?0:d.getUsableSpace()));}
    long dirSize(File f){if(f==null||!f.exists())return 0;if(f.isFile())return f.length();long s=0;File[] a=f.listFiles();if(a!=null)for(File x:a)s+=dirSize(x);return s;}
    String human(long n){if(n<1024)return n+" B";if(n<1048576)return String.format(Locale.US,"%.1f KB",n/1024.0);if(n<1073741824L)return String.format(Locale.US,"%.1f MB",n/1048576.0);return String.format(Locale.US,"%.2f GB",n/1073741824.0);}
    void showBrowser(){if(!wifiConnected())Toast.makeText(this,"גלישה חדשה דורשת אינטרנט; קבצים שמורים זמינים ללא רשת.",Toast.LENGTH_LONG).show();if(libraryShowing||web.getUrl()==null){web.loadUrl("https://www.google.com");}libraryShowing=false;refreshStatus();}
    void showLibrary(){libraryShowing=true;File d=getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS);StringBuilder h=new StringBuilder("<html dir='rtl'><meta name='viewport' content='width=device-width, initial-scale=1'><body style='font-family:sans-serif;background:#f5f7fc;padding:18px;color:#17233d'><h2>הקבצים השמורים</h2>");appendFiles(h,d);h.append("<p style='color:#68758b'>דפים דינמיים עשויים לדרוש אינטרנט.</p></body></html>");web.loadDataWithBaseURL(null,h.toString(),"text/html","UTF-8",null);refreshStatus();}
    void appendFiles(StringBuilder h,File f){if(f==null||!f.exists())return;File[] a=f.listFiles();if(a==null)return;for(File x:a)if(x.isDirectory())appendFiles(h,x);else h.append("<div style='background:white;border-radius:10px;padding:12px;margin:8px 0'>📄 ").append(escape(x.getName())).append(" · ").append(human(x.length())).append("</div>");}
    String escape(String s){return s.replace("&","&amp;").replace("<","&lt;").replace(">","&gt;").replace("'","&#39;");}
    void downloadInput(){String u=urlInput.getText().toString().trim();if(u.isEmpty()){Toast.makeText(this,"הזן קישור להורדה",Toast.LENGTH_SHORT).show();return;}if(!u.matches("(?i)^https?://.*"))u="https://"+u;if(!wifiConnected()){Toast.makeText(this,"התחבר ל־Wi-Fi לפני הורדה",Toast.LENGTH_LONG).show();return;}((InputMethodManager)getSystemService(INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(urlInput.getWindowToken(),0);if(u.matches("(?i).*\\.(pdf|zip|jpg|jpeg|png|mp3|mp4|txt|docx?)(\\?.*)?$"))enqueueDownload(u);else{web.loadUrl(u);Toast.makeText(this,"הדף נפתח. לא כל אתר ניתן לשמירה מלאה במצב לא מקוון.",Toast.LENGTH_LONG).show();}}
    String downloadName(String u){try{String p=Uri.parse(u).getLastPathSegment();if(p==null||p.trim().isEmpty())p="download-"+System.currentTimeMillis();p=Uri.decode(p).replaceAll("[\\\\/:*?<>|]","_").replace("\\\"","_");return p.length()>120?p.substring(p.length()-120):p;}catch(Exception e){return "download-"+System.currentTimeMillis();}}
    void enqueueDownload(String u){if(!wifiConnected()){Toast.makeText(this,"התחבר ל־Wi-Fi כדי להוריד",Toast.LENGTH_LONG).show();return;}try{DownloadManager.Request r=new DownloadManager.Request(Uri.parse(u));r.setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI);r.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);r.setTitle("WiFi Vault");r.setDescription("שמירת תוכן לשימוש לא מקוון");r.setDestinationInExternalFilesDir(this,Environment.DIRECTORY_DOWNLOADS,downloadName(u));((DownloadManager)getSystemService(DOWNLOAD_SERVICE)).enqueue(r);Toast.makeText(this,"ההורדה נוספה לתור",Toast.LENGTH_SHORT).show();}catch(Exception e){Toast.makeText(this,"לא ניתן להוריד את הקישור הזה",Toast.LENGTH_LONG).show();}}
    @Override public void onBackPressed(){if(web!=null&&web.canGoBack())web.goBack();else super.onBackPressed();}
}
